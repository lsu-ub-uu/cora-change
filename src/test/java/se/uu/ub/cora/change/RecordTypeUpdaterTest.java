/*
 * Copyright 2023 Uppsala University Library
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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RecordTypeUpdaterTest {

	private RecordTypeUpdater updater;

	private static final String SYSTEMONE_UTV_APPTOKEN_URL = "http://localhost:8180/login/rest/";
	private static final String SYSTEMONE_UTV_BASE_URL = "http://localhost:8080/systemone/rest/";

	private static final String ALVIN_UTV_APPTOKEN_URL = "http://localhost:8181/login/rest/";
	private static final String ALVIN_UTV_BASE_URL = "http://localhost:8081/alvin/rest/";

	private static final String DIVA_UTV_APPTOKEN_URL = "http://localhost:8182/login/rest/";
	private static final String DIVA_UTV_BASE_URL = "http://localhost:8082/diva/rest/";

	private static final String SYSTEMONE_DEV_APPTOKEN_URL = "http://130.238.171.238:38180/login/rest/";
	private static final String SYSTEMONE_DEV_BASE_URL = "http://130.238.171.238:38080/systemone/rest/";

	private static final String ALVIN_DEV_APPTOKEN_URL = "http://130.238.171.238:38181/login/rest/";
	private static final String ALVIN_DEV_BASE_URL = "http://130.238.171.238:38081/alvin/rest/";

	private static final String DIVA_DEV_APPTOKEN_URL = "http://130.238.171.238:38182/login/rest/";
	private static final String DIVA_DEV_BASE_URL = "http://130.238.171.238:38082/diva/rest/";

	@BeforeMethod
	public void beforeMethod() {
	}

	@Test(enabled = false)
	public void testListRecordTypes_SystemOne_Utv() throws Exception {
		updater = new RecordTypeUpdater(SYSTEMONE_UTV_APPTOKEN_URL, SYSTEMONE_UTV_BASE_URL);
		updater.updateAllRecordInfosForAllGroupForAllRecordTypes();
	}

	@Test(enabled = false)
	public void testListRecordTypes_Alvin_Utv() throws Exception {
		updater = new RecordTypeUpdater(ALVIN_UTV_APPTOKEN_URL, ALVIN_UTV_BASE_URL);
		updater.updateAllRecordInfosForAllGroupForAllRecordTypes();
	}

	@Test(enabled = false)
	public void testListRecordTypes_Diva_Utv() throws Exception {
		updater = new RecordTypeUpdater(DIVA_UTV_APPTOKEN_URL, DIVA_UTV_BASE_URL);
		updater.updateAllRecordInfosForAllGroupForAllRecordTypes();
	}

	@Test(enabled = false)
	public void testListRecordTypes_SystemOne_Dev() throws Exception {
		updater = new RecordTypeUpdater(SYSTEMONE_DEV_APPTOKEN_URL, SYSTEMONE_DEV_BASE_URL);
		updater.updateAllRecordInfosForAllGroupForAllRecordTypes();
	}

	@Test(enabled = false)
	public void testListRecordTypes_Alvin_Dev() throws Exception {
		updater = new RecordTypeUpdater(ALVIN_DEV_APPTOKEN_URL, ALVIN_DEV_BASE_URL);
		updater.updateAllRecordInfosForAllGroupForAllRecordTypes();
	}

	@Test(enabled = false)
	public void testListRecordTypes_Diva_Dev() throws Exception {
		updater = new RecordTypeUpdater(DIVA_DEV_APPTOKEN_URL, DIVA_DEV_BASE_URL);
		updater.updateAllRecordInfosForAllGroupForAllRecordTypes();
	}
}
