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
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import se.uu.ub.cora.clientdata.ClientData;
import se.uu.ub.cora.clientdata.ClientDataAtomic;
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

public class ChangeRegexp {
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
	private int totalFound = 0;
	private int cleaned = 0;
	private int totalOther = 0;
	private String totalNumberOfTypeInStorage;
	private ExecutorService executorService;

	public ChangeRegexp(String apptokenUrl, String baseUrl, String user, String appToken) {
		JavaClientAppTokenCredentials appTokenCredentials = new JavaClientAppTokenCredentials(
				baseUrl, apptokenUrl, user, appToken);
		dataClient = JavaClientProvider
				.createDataClientUsingJavaClientAppTokenCredentials(appTokenCredentials);
	}

	public void removeEmptyTexts(String... options) {
		setUpExecutorService();
		boolean dryRun = isDryRun(options);
		changeRecords(dryRun);
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

	private void changeRecords(boolean dryRun) {
		ClientDataList listOfRecords = dataClient.readList("metadata");
		totalNumberOfTypeInStorage = listOfRecords.getTotalNumberOfTypeInStorage();
		for (ClientData clientData : listOfRecords.getDataList()) {
			handleOneRecord(clientData, dryRun);
		}
	}

	private void handleOneRecord(ClientData clientData, boolean dryRun) {
		ClientDataRecord oneRecord = (ClientDataRecord) clientData;
		ClientDataRecordGroup recordGroup = oneRecord.getDataRecordGroup();
		boolean recordToBeUpdated = false;

		// List<ClientDataChild> textParts = recordGroup.getAllChildrenWithNameInData("textPart");
		//
		// for (ClientDataChild textPartChild : textParts) {
		// if (hasEmptyText(textPartChild)) {
		// textToBeUpdated = true;
		// printMessage(oneRecord, textPartChild);
		// ClientDataChildFilter filter = createFilterForClientDataChild(textPartChild);
		// recordGroup.removeAllChildrenMatchingFilter(filter);
		// totalFound++;
		// }
		// }
		Optional<String> type = recordGroup.getAttributeValue("type");
		if (type.isPresent() && "textVariable".equals(type.get())) {

			String regExValue = recordGroup.getFirstAtomicValueWithNameInData("regEx");
			if (List.of(".*", ".+").contains(regExValue)) {
				totalFound++;
				recordToBeUpdated = true;
				String message = MessageFormat.format(
						"Problematic regEx found for type: {0} id: {1} with regEx: {2}",
						oneRecord.getType(), oneRecord.getId(), regExValue);
				systemOutPrintlnBoldRed(message);
				ClientDataAtomic atomic = recordGroup.getFirstDataAtomicWithNameInData("regEx");
				atomic.setValue("^\\S.*$");
			}
		} else {
			totalOther++;
		}

		if (recordToBeUpdated && !dryRun) {
			updateRecord(recordGroup);
		}

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

	private void updateRecord(ClientDataRecordGroup recordGroup) {
		// Runnable runnableTask = () -> {
		String id = recordGroup.getId();
		String type = recordGroup.getType();
		System.out.println("updating id: " + id);
		try {
			dataClient.update(type, id, recordGroup);
			systemOutPrintlnBoldGreen("updated: " + type + " " + id);
			cleaned++;
		} catch (Exception e) {
			systemOutPrintlnBoldRed(type + " " + id + " error: " + e);
		}
		// };
		// executorService.execute(runnableTask);
	}

	private void printReport() {
		systemOutPrintlnBoldGreen("Total records: " + totalNumberOfTypeInStorage);
		systemOutPrintlnBoldYellow("Total records found: " + totalFound);
		systemOutPrintlnBoldGreen("Total records changed: " + cleaned);
		systemOutPrintlnBoldYellow("Other records: " + totalOther);
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
