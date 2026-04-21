/*
 * Copyright 2025, 2026 Uppsala University Library
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

import org.testng.annotations.Test;

public class ChangeFindEmptyAtomicsTest {

	private static final String DRY_RUN = "dryRun";
	private static final String SYSTEMONE_LOCAL_LOGIN_URL = "http://localhost:8180/login/rest/";
	private static final String SYSTEMONE_LOCAL_BASE_URL = "http://localhost:8080/systemone/rest/";

	private static final String SYSTEMONE_PREVIEW_LOGIN_URL = "https://preview.systemone.cora.epc.ub.uu.se/login/rest/";
	private static final String SYSTEMONE_PREVIEW_BASE_URL = "https://preview.systemone.cora.epc.ub.uu.se/rest/";

	private static final String ALVIN_PREVIEW_LOGIN_URL = "https://preview.alvin.cora.epc.ub.uu.se/login/rest/";
	private static final String ALVIN_PREVIEW_BASE_URL = "https://preview.alvin.cora.epc.ub.uu.se/rest/";

	private static final String ALVIN_PRE_LOGIN_URL = "https://cora.alvin-portal.org/login/rest/";
	private static final String ALVIN_PRE_BASE_URL = "https://cora.alvin-portal.org/rest/";

	private static final String DIVA_PREVIEW_LOGIN_URL = "https://preview.diva.cora.epc.ub.uu.se/login/rest/";
	private static final String DIVA_PREVIEW_BASE_URL = "https://preview.diva.cora.epc.ub.uu.se/rest/";

	private static final String ALVIN_LOCAL_LOGIN_URL = "http://localhost:8181/login/rest/";
	private static final String ALVIN_LOCAL_BASE_URL = "http://localhost:8081/alvin/rest/";

	private static final String DIVA_LOCAL_LOGIN_URL = "http://localhost:8182/login/rest/";
	private static final String DIVA_LOCAL_BASE_URL = "http://localhost:8082/diva/rest/";

	private static final String SYSTEMONE_DEV_LOGIN_URL = "http://130.238.171.238:38180/login/rest/";
	private static final String SYSTEMONE_DEV_BASE_URL = "http://130.238.171.238:38080/systemone/rest/";

	private static final String ALVIN_DEV_LOGIN_URL = "http://130.238.171.238:38181/login/rest/";
	private static final String ALVIN_DEV_BASE_URL = "http://130.238.171.238:38081/alvin/rest/";

	private static final String DIVA_DEV_LOGIN_URL = "http://130.238.171.238:38182/login/rest/";
	private static final String DIVA_DEV_BASE_URL = "http://130.238.171.238:38082/diva/rest/";

	@Test(enabled = false)
	public void runFindEmptyForSystemOneTexts() {

		ChangeFindEmptyAtomics updater = new ChangeFindEmptyAtomics(SYSTEMONE_PREVIEW_LOGIN_URL,
				SYSTEMONE_PREVIEW_BASE_URL, "systemoneAdmin@system.cora.uu.se",
				"47e50f91-9d62-44ae-96f7-324234234");

		updater.findEmptyAtomics(DRY_RUN);
	}

	@Test(enabled = false)
	public void runFindEmptyForAlvinOneTexts_PRE() {
		ChangeFindEmptyAtomics updater = new ChangeFindEmptyAtomics(ALVIN_PRE_LOGIN_URL,
				ALVIN_PRE_BASE_URL, "alvinAdmin@cora.epc.ub.uu.se",
				"f2b2a0b8-3ae4-4223-acd6-423423423");

		updater.findEmptyAtomics(DRY_RUN);
	}

	@Test(enabled = false)
	public void runFindEmptyForAlvinOneTexts() {
		ChangeFindEmptyAtomics updater = new ChangeFindEmptyAtomics(ALVIN_DEV_LOGIN_URL,
				ALVIN_DEV_BASE_URL, "alvinAdmin@cora.epc.ub.uu.se",
				"6bd7cba2-5982-4158-9f13-4231423423");

		updater.findEmptyAtomics(DRY_RUN);
	}

	@Test(enabled = false)
	public void runChangeValidationTypeForSystemOneTexts() {

		ChangeFindEmptyAtomics updater = new ChangeFindEmptyAtomics(SYSTEMONE_DEV_LOGIN_URL,
				SYSTEMONE_DEV_BASE_URL, "systemoneAdmin@system.cora.uu.se",
				"47e50f91-9d62-44ae-96f7-432423423");

		updater.findEmptyAtomics(DRY_RUN);
	}

	@Test(enabled = false)
	public void runChangeValidationTypeForAlvinTexts() {

		ChangeFindEmptyAtomics updater = new ChangeFindEmptyAtomics(ALVIN_DEV_LOGIN_URL,
				ALVIN_DEV_BASE_URL, "alvinAdmin@cora.epc.ub.uu.se",
				"b1752dd4-eeb4-4b7d-98e2-432423423");

		updater.findEmptyAtomics(DRY_RUN);
	}

	@Test(enabled = false)
	public void runFindEmptyForDivaOneTextsPreview() {

		ChangeFindEmptyAtomics updater = new ChangeFindEmptyAtomics(DIVA_PREVIEW_LOGIN_URL,
				DIVA_PREVIEW_BASE_URL, "divaAdmin@cora.epc.ub.uu.se",
				"b5ffafe1-15a5-492b-baa9-43242421");

		updater.findEmptyAtomics();
	}

	@Test(enabled = false)
	public void runFindEmptyForDivaOneTextsDev() {

		ChangeFindEmptyAtomics updater = new ChangeFindEmptyAtomics(DIVA_DEV_LOGIN_URL,
				DIVA_DEV_BASE_URL, "divaAdmin@cora.epc.ub.uu.se",
				"cb1256d6-29a3-4162-abb0-4324231234");

		updater.findEmptyAtomics(DRY_RUN);
	}

}
