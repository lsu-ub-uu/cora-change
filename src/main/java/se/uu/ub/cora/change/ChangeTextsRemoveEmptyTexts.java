/*
 * Copyright 2025 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.uu.ub.cora.change;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import se.uu.ub.cora.clientdata.ClientData;
import se.uu.ub.cora.clientdata.ClientDataAttribute;
import se.uu.ub.cora.clientdata.ClientDataChild;
import se.uu.ub.cora.clientdata.ClientDataChildFilter;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataList;
import se.uu.ub.cora.clientdata.ClientDataProvider;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.ClientDataRecordGroup;
import se.uu.ub.cora.javaclient.JavaClientAppTokenCredentials;
import se.uu.ub.cora.javaclient.JavaClientProvider;
import se.uu.ub.cora.javaclient.data.DataClient;

public class ChangeTextsRemoveEmptyTexts {
	private static final String TEXT_WRITTER_PATTERN = "Empty text found for id: {0} in name:{1}, attributes:{2}";
	private static final String ATTRIBUTE_PATTERN = "\"{0}\":\"{1}\"";
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
	private DataClient dataClient;
	private int totalTextsWithEmptyText = 0;
	private int textsCleaned = 0;
	private int totalOtherTexts = 0;
	private String totalNumberOfTypeInStorage;
	private ExecutorService executorService;

	public ChangeTextsRemoveEmptyTexts(String apptokenUrl, String baseUrl, String user,
			String appToken) {
		JavaClientAppTokenCredentials appTokenCredentials = new JavaClientAppTokenCredentials(
				baseUrl, apptokenUrl, user, appToken);
		dataClient = JavaClientProvider
				.createDataClientUsingJavaClientAppTokenCredentials(appTokenCredentials);
	}

	public void removeEmptyTexts(String... options) {
		setUpExecutorService();
		boolean dryRun = isDryRun(options);
		changeTexts(dryRun);
		printReport();
		shutDownExecutorService();
	}

	private boolean isDryRun(String... options) {
		List<String> optionList = Arrays.asList(options);
		return optionList.contains("dryRun");
	}

	private void setUpExecutorService() {
		executorService = new ThreadPoolExecutor(DEFAULT_THREAD_COUNT, DEFAULT_THREAD_COUNT, 2,
				TimeUnit.MINUTES, new ArrayBlockingQueue<>(DEFAULT_QUEUE_LENGTH),
				new ThreadPoolExecutor.CallerRunsPolicy());
	}

	private void changeTexts(boolean dryRun) {
		ClientDataList listOfTexts = dataClient.readList("text");
		totalNumberOfTypeInStorage = listOfTexts.getTotalNumberOfTypeInStorage();
		for (ClientData clientData : listOfTexts.getDataList()) {
			handleOneText(clientData, dryRun);
		}
	}

	private void handleOneText(ClientData clientData, boolean dryRun) {
		ClientDataRecord textRecord = (ClientDataRecord) clientData;
		ClientDataRecordGroup textRecordGroup = textRecord.getDataRecordGroup();

		List<ClientDataChild> textParts = textRecordGroup.getAllChildrenWithNameInData("textPart");

		boolean textToBeUpdated = false;
		for (ClientDataChild textPartChild : textParts) {
			if (hasEmptyText(textPartChild)) {
				textToBeUpdated = true;
				printMessage(textRecord, textPartChild);
				ClientDataChildFilter filter = createFilterForClientDataChild(textPartChild);
				textRecordGroup.removeAllChildrenMatchingFilter(filter);
				totalTextsWithEmptyText++;
			}
		}
		if (textToBeUpdated && !dryRun) {
			updateText(textRecordGroup);
		}
		totalOtherTexts++;

		// if (textHasEmptyText(textRecordGroup)) {
		// handleEmptyText(textRecordGroup, id);
		// } else {
		// System.out.println("id: " + id);
		// }
		// System.out.println(MessageFormat.format("Texts handled: {0} / {1}",
		// totalTextsWithEmptyText + totalOtherTexts, totalNumberOfTypeInStorage));
	}

	private ClientDataChildFilter createFilterForClientDataChild(ClientDataChild textPartChild) {
		ClientDataChildFilter filter = ClientDataProvider
				.createDataChildFilterUsingChildNameInData("textPart");
		for (ClientDataAttribute clientDataAttribute : textPartChild.getAttributes()) {
			String nameInData = clientDataAttribute.getNameInData();
			String value = clientDataAttribute.getValue();
			filter.addAttributeUsingNameInDataAndPossibleValues(nameInData, Set.of(value));
		}
		return filter;
	}

	private void printMessage(ClientDataRecord textRecord, ClientDataChild textPartChild) {
		String message = createMessage(textRecord, textPartChild);
		if ("sv".equals(textPartChild.getAttribute("lang").getValue())) {
			systemOutPrintlnBoldRed(message);
		} else {
			systemOutPrintlnBoldYellow(message);
		}
	}

	private String createMessage(ClientDataRecord textRecord, ClientDataChild textPartChild) {
		return MessageFormat.format(TEXT_WRITTER_PATTERN, textRecord.getId(),
				textPartChild.getNameInData(), attributesToString(textPartChild));
	}

	private boolean hasEmptyText(ClientDataChild textPartChild) {
		ClientDataGroup textPartGroup = (ClientDataGroup) textPartChild;
		String textValue = textPartGroup.getFirstAtomicValueWithNameInData("text");
		return textValue.isEmpty() || textValue.isBlank();
	}

	private String attributesToString(ClientDataChild textPartChild) {
		Collection<ClientDataAttribute> attributes = textPartChild.getAttributes();
		List<String> attributesStrings = new ArrayList<>();
		for (ClientDataAttribute attribute : attributes) {
			String attributeFormatted = MessageFormat.format(ATTRIBUTE_PATTERN,
					attribute.getNameInData(), attribute.getValue());
			attributesStrings.add(attributeFormatted);
		}
		return "{" + String.join(",", attributesStrings) + "}";
	}

	private void updateText(ClientDataRecordGroup textRecordGroup) {
		// Runnable runnableTask = () -> {
		String id = textRecordGroup.getId();
		System.out.println("updating id: " + id);
		try {
			dataClient.update("text", id, textRecordGroup);
			systemOutPrintlnBoldGreen("updated: " + id);
			textsCleaned++;
		} catch (Exception e) {
			systemOutPrintlnBoldRed(id + " error: " + e);
		}
		// };
		// executorService.execute(runnableTask);
	}

	// private boolean textHasEmptyText(ClientDataRecordGroup textRecordGroup) {
	// // TODO: here!!!!
	// return "diva".equals(textRecordGroup.getDataDivider());
	// }
	//
	// private void handleEmptyText(ClientDataRecordGroup textRecordGroup, String id) {
	// systemOutPrintlnBoldGreen("id: " + id);
	// ClientDataGroup recordInfo = textRecordGroup.getFirstGroupWithNameInData(RECORD_INFO);
	// ClientDataRecordLink validationType = recordInfo
	// .getFirstChildOfTypeAndName(ClientDataRecordLink.class, VALIDATION_TYPE);
	// systemOutPrintlnBoldGreen(validationType.getLinkedRecordId());
	// if (!"divaText".equals(validationType.getLinkedRecordId())) {
	// handleTextChange(textRecordGroup, id, recordInfo, validationType);
	// }
	// }
	//
	// private void handleTextChange(ClientDataRecordGroup textRecordGroup, String id,
	// ClientDataGroup recordInfo, ClientDataRecordLink validationType) {
	// divaTextChangeNeeded++;
	// ClientDataRecordLink newValidationType = ClientDataProvider
	// .createRecordLinkUsingNameInDataAndTypeAndId(validationType.getNameInData(),
	// validationType.getLinkedRecordType(), "divaText");
	// recordInfo.removeChildrenWithTypeAndName(ClientDataRecordLink.class, VALIDATION_TYPE);
	// recordInfo.addChild(newValidationType);
	// systemOutPrintlnBoldYellow("changed validationType... updating record with id: " + id);
	//
	// if (textRecordGroup.getAllChildrenWithNameInData("textPart").size() == 1) {
	// createAndAddNewEnglishTextPart(textRecordGroup, id);
	// }
	// ClientDataChildFilter filter = ClientDataProvider
	// .createDataChildFilterUsingChildNameInData("textPart");
	// filter.addAttributeUsingNameInDataAndPossibleValues("lang", Set.of("en"));
	// filter.addAttributeUsingNameInDataAndPossibleValues("type", Set.of("alternative"));
	// List<ClientDataChild> enParts = textRecordGroup.getAllChildrenMatchingFilter(filter);
	// for (ClientDataChild enPart : enParts) {
	// ClientDataGroup enGroup = (ClientDataGroup) enPart;
	// if (enGroup.getFirstAtomicValueWithNameInData("text").getBytes().length == 0) {
	// systemOutPrintlnBoldRed("EMPTY ENGLISH!!!!");
	// textRecordGroup.removeAllChildrenMatchingFilter(filter);
	// createAndAddNewEnglishTextPart(textRecordGroup, id);
	// }
	// }
	// if (!id.equals("returnText")
	// && !id.equals("doctoralThesisContentTypeCollectionVarDefText")) {
	// updateText(textRecordGroup, id);
	// }
	// systemOutPrintlnBoldGreen("...updated");
	// }

	// private void createAndAddNewEnglishTextPart(ClientDataRecordGroup textRecordGroup, String id)
	// {
	// String english = "CHANGE ME, my id is: " + id;
	// systemOutPrintlnBoldRed(english);
	// ClientDataGroup textPart = createNewEnglishTextPartWithString(english);
	// textRecordGroup.addChild(textPart);
	// }

	// private ClientDataGroup createNewEnglishTextPartWithString(String english) {
	// ClientDataGroup textPart = ClientDataProvider.createGroupUsingNameInData("textPart");
	// textPart.addAttributeByIdWithValue("type", "alternative");
	// textPart.addAttributeByIdWithValue("lang", "en");
	// ClientDataAtomic englishPart = ClientDataProvider
	// .createAtomicUsingNameInDataAndValue("text", english);
	// textPart.addChild(englishPart);
	// return textPart;
	// }

	private void printReport() {
		systemOutPrintlnBoldGreen("Total texts: " + totalNumberOfTypeInStorage);
		systemOutPrintlnBoldYellow("Total texts with empty value: " + totalTextsWithEmptyText);
		systemOutPrintlnBoldGreen("Total texts cleaned: " + textsCleaned);
		systemOutPrintlnBoldYellow("Other texts: " + totalOtherTexts);
	}

	private void shutDownExecutorService() {
		executorService.shutdown();
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
