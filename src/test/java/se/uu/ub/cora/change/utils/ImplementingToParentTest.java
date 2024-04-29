package se.uu.ub.cora.change.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.Optional;

import org.testng.annotations.Test;

public class ImplementingToParentTest {
	private static final String SYSTEMONE_LOCAL_APPTOKEN_URL = "http://localhost:8180/login/rest/";
	private static final String SYSTEMONE_LOCAL_BASE_URL = "http://localhost:8080/systemone/rest/";

	private static final String ALVIN_LOCAL_APPTOKEN_URL = "http://localhost:8181/login/rest/";
	private static final String ALVIN_LOCAL_BASE_URL = "http://localhost:8081/alvin/rest/";

	private static final String DIVA_LOCAL_APPTOKEN_URL = "http://localhost:8182/login/rest/";
	private static final String DIVA_LOCAL_BASE_URL = "http://localhost:8082/diva/rest/";

	public void storeMap(Map<String, String> map, String filePath) {

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
			oos.writeObject(map);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public Optional<Map<String, String>> readMapFromFile(String filePath) {

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
			Map<String, String> readMap = (Map<String, String>) ois.readObject();
			return Optional.of(readMap);
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	@Test(enabled = false)
	public void storeImplementingToParentForSystemOne() throws Exception {
		writeToDisk(SYSTEMONE_LOCAL_APPTOKEN_URL, SYSTEMONE_LOCAL_BASE_URL, "SystemOne");
	}

	@Test(enabled = false)
	public void storeImplementingToParentForAlvin() throws Exception {
		writeToDisk(ALVIN_LOCAL_APPTOKEN_URL, ALVIN_LOCAL_BASE_URL, "Alvin");
	}

	@Test(enabled = false)
	public void storeImplementingToParentForDiVA() throws Exception {
		writeToDisk(DIVA_LOCAL_APPTOKEN_URL, DIVA_LOCAL_BASE_URL, "DiVA");
	}

	private void writeToDisk(String appTokenUrl, String baseUrl, String system) {
		// DataClientFactoryImp dataClientFactory = DataClientFactoryImp
		// .usingAppTokenVerifierUrlAndBaseUrl(appTokenUrl, baseUrl);
		// DataClient dataClient = dataClientFactory.factorUsingUserIdAndAppToken("141414",
		// "63e6bd34-02a1-4c82-8001-158c104cae0e");
		//
		// RecordTypeUtil recordTypeUtil = RecordTypeUtilImp.usingDataClient(dataClient);
		// Map<String, String> mapToStore = recordTypeUtil.getMapOfImplementingToParent();
		//
		String filePath = System.getProperty("user.home") + "/workspace/cora-change/impToParent"
				+ system + ".ser";
		// storeMap(mapToStore, filePath);

		Optional<Map<String, String>> readMapFromFile = readMapFromFile(filePath);
		System.out.println(readMapFromFile);
	}
}
