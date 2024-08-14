package se.uu.ub.cora.change;

import java.text.MessageFormat;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import se.uu.ub.cora.clientdata.ClientData;
import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataChild;
import se.uu.ub.cora.clientdata.ClientDataChildFilter;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataList;
import se.uu.ub.cora.clientdata.ClientDataProvider;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.ClientDataRecordGroup;
import se.uu.ub.cora.clientdata.ClientDataRecordLink;
import se.uu.ub.cora.javaclient.cora.DataClient;
import se.uu.ub.cora.javaclient.cora.DataClientFactoryImp;

public class ChangeTextsToDivaText {
	public static final String ANSI_BOLD = "\033[0;1m";
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	private static final String RECORD_INFO = "recordInfo";
	private static final String VALIDATION_TYPE = "validationType";
	private static final int DEFAULT_THREAD_COUNT = 20;
	private static final int DEFAULT_QUEUE_LENGTH = 20;
	private DataClientFactoryImp dataClientFactory;
	private DataClient dataClient;
	private String apptokenUrl;
	private String baseUrl;
	private int totalDivaTexts = 0;
	private int divaTextChangeNeeded = 0;
	private int totalOtherTexts = 0;
	private String totalNumberOfTypeInStorage;
	private ExecutorService executorService;

	public ChangeTextsToDivaText(String apptokenUrl, String baseUrl) {
		this.apptokenUrl = apptokenUrl;
		this.baseUrl = baseUrl;
		dataClientFactory = DataClientFactoryImp.usingAppTokenVerifierUrlAndBaseUrl(apptokenUrl,
				baseUrl);
		dataClient = dataClientFactory.factorUsingUserIdAndAppToken(
				"systemoneAdmin@system.cora.uu.se", "5d3f3ed4-4931-4924-9faa-8eaf5ac6457e");

	}

	public void changeValidationTypeForDivaTexts() {
		// executorService = Executors.newFixedThreadPool(30);
		executorService = new ThreadPoolExecutor(DEFAULT_THREAD_COUNT, DEFAULT_THREAD_COUNT, 2,
				TimeUnit.MINUTES, new ArrayBlockingQueue<>(DEFAULT_QUEUE_LENGTH),
				new ThreadPoolExecutor.CallerRunsPolicy());

		ClientDataList listOfTexts = dataClient.readList("text");
		totalNumberOfTypeInStorage = listOfTexts.getTotalNumberOfTypeInStorage();
		for (ClientData clientData : listOfTexts.getDataList()) {
			handleOneText(clientData);
		}
		systemOutPrintlnBoldGreen("Total texts: " + totalNumberOfTypeInStorage);
		systemOutPrintlnBoldGreen("Total diva texts: " + totalDivaTexts);
		systemOutPrintlnBoldGreen("Diva texts change: " + divaTextChangeNeeded);
		systemOutPrintlnBoldYellow("Other texts: " + totalOtherTexts);
		executorService.shutdown();
	}

	private void handleOneText(ClientData clientData) {
		ClientDataRecord textRecord = (ClientDataRecord) clientData;

		ClientDataRecordGroup textRecordGroup = textRecord.getDataRecordGroup();
		String id = textRecordGroup.getId();
		if ("diva".equals(textRecordGroup.getDataDivider())) {
			totalDivaTexts++;
			handleDivaText(textRecordGroup, id);
		} else {
			totalOtherTexts++;
			System.out.println("id: " + id);
		}
		System.out.println(MessageFormat.format("Texts handled: {0} / {1}",
				totalDivaTexts + totalOtherTexts, totalNumberOfTypeInStorage));
	}

	private void handleDivaText(ClientDataRecordGroup textRecordGroup, String id) {
		systemOutPrintlnBoldGreen("id: " + id);
		ClientDataGroup recordInfo = textRecordGroup.getFirstGroupWithNameInData(RECORD_INFO);
		ClientDataRecordLink validationType = recordInfo
				.getFirstChildOfTypeAndName(ClientDataRecordLink.class, VALIDATION_TYPE);
		systemOutPrintlnBoldGreen(validationType.getLinkedRecordId());
		if (!"divaText".equals(validationType.getLinkedRecordId())) {
			handleTextChange(textRecordGroup, id, recordInfo, validationType);
		}
	}

	private void handleTextChange(ClientDataRecordGroup textRecordGroup, String id,
			ClientDataGroup recordInfo, ClientDataRecordLink validationType) {
		divaTextChangeNeeded++;
		ClientDataRecordLink newValidationType = ClientDataProvider
				.createRecordLinkUsingNameInDataAndTypeAndId(validationType.getNameInData(),
						validationType.getLinkedRecordType(), "divaText");
		recordInfo.removeChildrenWithTypeAndName(ClientDataRecordLink.class, VALIDATION_TYPE);
		recordInfo.addChild(newValidationType);
		systemOutPrintlnBoldYellow("changed validationType... updating record with id: " + id);

		if (textRecordGroup.getAllChildrenWithNameInData("textPart").size() == 1) {
			createAndAddNewEnglishTextPart(textRecordGroup, id);
		}
		ClientDataChildFilter filter = ClientDataProvider
				.createDataChildFilterUsingChildNameInData("textPart");
		filter.addAttributeUsingNameInDataAndPossibleValues("lang", Set.of("en"));
		filter.addAttributeUsingNameInDataAndPossibleValues("type", Set.of("alternative"));
		List<ClientDataChild> enParts = textRecordGroup.getAllChildrenMatchingFilter(filter);
		for (ClientDataChild enPart : enParts) {
			ClientDataGroup enGroup = (ClientDataGroup) enPart;
			if (enGroup.getFirstAtomicValueWithNameInData("text").getBytes().length == 0) {
				systemOutPrintlnBoldRed("EMPTY ENGLISH!!!!");
				textRecordGroup.removeAllChildrenMatchingFilter(filter);
				createAndAddNewEnglishTextPart(textRecordGroup, id);
			}
		}
		if (!id.equals("returnText")
				&& !id.equals("doctoralThesisContentTypeCollectionVarDefText")) {
			Runnable runnableTask = () -> {
				systemOutPrintlnBoldRed("START!!!!");
				systemOutPrintlnBoldYellow("storing id: " + id);
				dataClient.update("text", id, textRecordGroup);
				systemOutPrintlnBoldRed("DONE!!!!");
			};
			executorService.execute(runnableTask);
		}
		systemOutPrintlnBoldGreen("...updated");
	}

	private void createAndAddNewEnglishTextPart(ClientDataRecordGroup textRecordGroup, String id) {
		String english = "CHANGE ME, my id is: " + id;
		systemOutPrintlnBoldRed(english);
		ClientDataGroup textPart = createNewEnglishTextPartWithString(english);
		textRecordGroup.addChild(textPart);
	}

	private ClientDataGroup createNewEnglishTextPartWithString(String english) {
		ClientDataGroup textPart = ClientDataProvider.createGroupUsingNameInData("textPart");
		textPart.addAttributeByIdWithValue("type", "alternative");
		textPart.addAttributeByIdWithValue("lang", "en");
		ClientDataAtomic englishPart = ClientDataProvider
				.createAtomicUsingNameInDataAndValue("text", english);
		textPart.addChild(englishPart);
		return textPart;
	}

	private void systemOutPrintlnBoldGreen(String string) {
		System.out.println(ANSI_BOLD + ANSI_GREEN + string + ANSI_RESET);
	}

	private void systemOutPrintlnBoldRed(String string) {
		System.out.println(ANSI_BOLD + ANSI_RED + string + ANSI_RESET);
	}

	private void systemOutPrintlnBoldYellow(String string) {
		System.out.println(ANSI_BOLD + ANSI_YELLOW + string + ANSI_RESET);
	}

}
