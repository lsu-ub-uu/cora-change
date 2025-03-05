#import binaries to fedora without copy, prof of concept
some urls:
* https://wiki.lyrasis.org/display/FF/Documentation
* https://wiki.lyrasis.org/display/FEDORA6x/Technical+Documentation
* https://wiki.lyrasis.org/display/FEDORA6x/RESTful+HTTP+API+-+Side+Loading
* https://wiki.lyrasis.org/display/FEDORA6x/Fedora+OCFL+Object+Structure
* https://wiki.lyrasis.org/display/FEDORA6x/Fedora+Header+Files
* https://ocfl.io/
* https://github.com/OCFL/ocfl-java?tab=readme-ov-file
* https://github.com/OCFL/ocfl-java/blob/main/docs/USAGE.md
* http://localhost:38087/fcrepo/
* http://localhost:38087/fcrepo/rest/fcr:search?offset=0&max_results=10


##start clean fedora systemone
start by removing all data from sharedArchive/systemone
start systemone fedora

run the system and add a binary through it, to get correct metadata to compare with.

##create basic ocfl info using ocfl-java, and move file into repository
run the test:
OCFLwrappereTest.java/testPutOneBinary
to add our binary to fedora


##create needed fedora files using python script
go into fedoradocker using exec as root

go to folder:
/tmp/sharedArchive

add the script generate_fcrepo9.py

run script:
(you can add --dry-run to the end)
python3 generate_fcrepo9.py systemOne/1a0/c80/f4b/1a0c80f4b87f8b9bce0fb9672fb2b2912b5e5613c115626d7c1f391dc157fcdb/

**script still needs some more fixes**
* wrong checksum in inventory.json.sha512
* inventory.json inside folder v1 should be the same as the one on the same level as v1 (this inventory.json, is updated by the script) 

##side load the added binary in fedora
try to sideload the added binary from host computer

https://wiki.lyrasis.org/display/FEDORA6x/RESTful+HTTP+API+-+Side+Loading

user RESTer plugin in firefox

POST http://localhost:38087/fcrepo/rest/systemOne:binary:binary:001-master/fcr:reindex 

fedora from the host: http://localhost:38087/fcrepo/

**look for additional errors and fix them**