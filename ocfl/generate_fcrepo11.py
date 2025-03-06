import os
import json
import hashlib
import logging
from datetime import datetime
import argparse
import shutil

def sha512_checksum(file_path):
    sha512 = hashlib.sha512()
    with open(file_path, 'rb') as f:
        while chunk := f.read(8192):
            sha512.update(chunk)
    return sha512.hexdigest()

def setup_logging():
    logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

def find_ocfl_objects(base_path):
    ocfl_objects = []
    for root, dirs, files in os.walk(base_path):
        if 'v1' in dirs and 'inventory.json' in files:
            ocfl_objects.append(root)
    return ocfl_objects

def copy_inventory_files(ocfl_object_path, dry_run):
    inventory_path = os.path.join(ocfl_object_path, 'inventory.json')
    inventory_sha_path = os.path.join(ocfl_object_path, 'inventory.json.sha512')
    v1_path = os.path.join(ocfl_object_path, 'v1')
    
    if dry_run:
        logging.info(f'[Dry Run] Would copy inventory files to {v1_path}')
    else:
        shutil.copy2(inventory_path, os.path.join(v1_path, 'inventory.json'))
        shutil.copy2(inventory_sha_path, os.path.join(v1_path, 'inventory.json.sha512'))
        logging.info(f'Copied inventory files to {v1_path}')

def create_fcrepo_catalog(ocfl_object_path, dry_run):
    v1_content_path = os.path.join(ocfl_object_path, 'v1', 'content')
    fcrepo_path = os.path.join(v1_content_path, '.fcrepo')
    
    if dry_run:
        logging.info(f'[Dry Run] Would create directory: {fcrepo_path}')
    else:
        os.makedirs(fcrepo_path, exist_ok=True)
    
    filenames = [f for f in os.listdir(v1_content_path) if os.path.isfile(os.path.join(v1_content_path, f))]
    if not filenames:
        logging.warning(f'No files found in {v1_content_path}, skipping.')
        return None
    
    filename = filenames[0]  # Assuming only one file per OCFL object
    fcr_root_path = os.path.join(fcrepo_path, 'fcr-root.json')
    fcr_desc_path = os.path.join(fcrepo_path, 'fcr-root~fcr-desc.json')
    fcr_desc_nt_path = os.path.join(v1_content_path, f"{filename}~fcr-desc.nt")
    
    created_date = datetime.utcnow().isoformat() + 'Z'
    state_token = hashlib.md5(created_date.encode()).hexdigest().upper()
    
    file_path = os.path.join(v1_content_path, filename)
    file_hash = sha512_checksum(file_path)
    file_size = os.path.getsize(file_path)
    
    fcr_root_metadata = {
        "id": f"info:fedora/{filename}",
        "parent": "info:fedora",
        "stateToken": state_token,
        "interactionModel": "http://www.w3.org/ns/ldp#NonRDFSource",
        "mimeType": "application/octet-stream",
        "filename": filename,
        "contentSize": file_size,
        "digests": [f"urn:sha-512:{file_hash}"],
        "createdDate": created_date,
        "lastModifiedDate": created_date,
        "mementoCreatedDate": created_date,
        "archivalGroup": False,
        "objectRoot": True,
        "deleted": False,
        "contentPath": filename,
        "headersVersion": "1.0"
    }
    
    fcr_desc_metadata = {
        "id": f"info:fedora/{filename}/fcr:metadata",
        "parent": f"info:fedora/{filename}",
        "stateToken": hashlib.md5(f"desc-{created_date}".encode()).hexdigest().upper(),
        "interactionModel": "http://fedora.info/definitions/v4/repository#NonRdfSourceDescription",
        "contentSize": 0,
        "digests": ["urn:sha-512:cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e"],
        "createdDate": created_date,
        "lastModifiedDate": created_date,
        "mementoCreatedDate": created_date,
        "archivalGroup": False,
        "objectRoot": False,
        "deleted": False,
        "contentPath": f"{filename}~fcr-desc.nt",
        "headersVersion": "1.0"
    }
    
    if dry_run:
        logging.info(f'[Dry Run] Would write {fcr_root_path}, {fcr_desc_path}, and {fcr_desc_nt_path}')
    else:
        with open(fcr_root_path, 'w') as f:
            json.dump(fcr_root_metadata, f, indent=4)
        with open(fcr_desc_path, 'w') as f:
            json.dump(fcr_desc_metadata, f, indent=4)
        open(fcr_desc_nt_path, 'w').close()  # Create empty file
    
    return [fcr_root_metadata, fcr_desc_metadata]

def update_inventory(ocfl_object_path, metadata_list, dry_run):
    if not metadata_list:
        return

    inventory_path = os.path.join(ocfl_object_path, 'inventory.json')
    inventory_sha_path = os.path.join(ocfl_object_path, 'inventory.json.sha512')
    
    if not os.path.exists(inventory_path):
        logging.warning(f'Inventory file not found: {inventory_path}')
        return

    with open(inventory_path, 'r') as f:
        inventory = json.load(f)

    for metadata in metadata_list:
        for digest in metadata['digests']:
            if 'urn:sha-512:' in digest:
                digest = digest.replace('urn:sha-512:', '')
            inventory['manifest'][digest] = [f'v1/content/{metadata["contentPath"]}']
    
    fcrepo_files = [
        'v1/content/.fcrepo/fcr-root.json',
        'v1/content/.fcrepo/fcr-root~fcr-desc.json'
    ]
    for file_path in fcrepo_files:
        file_hash = sha512_checksum(os.path.join(ocfl_object_path, file_path))
        inventory['manifest'][file_hash] = [file_path]
    
#    inventory['versions']['v1']['state'] = inventory['manifest']
    inventory['versions']['v1']['state'] = remove_prefix(inventory['manifest'])

    if dry_run:
        logging.info(f'[Dry Run] Would update {inventory_path}')
    else:
        with open(inventory_path, 'w') as f:
            json.dump(inventory, f, indent=4)
        logging.info(f'Updated {inventory_path}')
        
        with open(inventory_sha_path, 'w') as f:
            sha512_hash = sha512_checksum(inventory_path)
            f.write(f'{sha512_hash}  inventory.json\n')
        logging.info(f'Updated {inventory_sha_path}')

def remove_prefix(obj, prefix="v1/content/"):
    if isinstance(obj, dict):
        return {k: remove_prefix(v, prefix) for k, v in obj.items()}
    elif isinstance(obj, list):
        return [remove_prefix(item, prefix) for item in obj]
    elif isinstance(obj, str):
        return obj.replace(prefix, "")
    return obj

def main(base_path, dry_run):
    setup_logging()
    ocfl_objects = find_ocfl_objects(base_path)
    for obj in ocfl_objects:
        logging.info(f'Processing OCFL object: {obj}')
        metadata_list = create_fcrepo_catalog(obj, dry_run)
        update_inventory(obj, metadata_list, dry_run)
        copy_inventory_files(obj, dry_run)

def parse_args():
    parser = argparse.ArgumentParser(description='Generate Fedora .fcrepo catalog and update OCFL inventory.')
    parser.add_argument('base_path', help='Path to the OCFL root directory')
    parser.add_argument('--dry-run', action='store_true', help='Simulate changes without modifying files')
    return parser.parse_args()

if __name__ == "__main__":
    args = parse_args()
    main(args.base_path, args.dry_run)
