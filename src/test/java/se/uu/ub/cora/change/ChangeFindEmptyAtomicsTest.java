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
	private static final String SYSTEMONE_LOCAL_APPTOKEN_URL = "http://localhost:8180/login/rest/";
	private static final String SYSTEMONE_LOCAL_BASE_URL = "http://localhost:8080/systemone/rest/";

	private static final String SYSTEMONE_PREVIEW_APPTOKEN_URL = "https://preview.systemone.cora.epc.ub.uu.se/login/rest/";
	private static final String SYSTEMONE_PREVIEW_BASE_URL = "https://preview.systemone.cora.epc.ub.uu.se/rest/";

	private static final String ALVIN_PREVIEW_APPTOKEN_URL = "https://preview.alvin.cora.epc.ub.uu.se/login/rest/";
	private static final String ALVIN_PREVIEW_BASE_URL = "https://preview.alvin.cora.epc.ub.uu.se/rest/";

	private static final String DIVA_PREVIEW_APPTOKEN_URL = "https://preview.diva.cora.epc.ub.uu.se/login/rest/";
	private static final String DIVA_PREVIEW_BASE_URL = "https://preview.diva.cora.epc.ub.uu.se/rest/";

	private static final String ALVIN_LOCAL_APPTOKEN_URL = "http://localhost:8181/login/rest/";
	private static final String ALVIN_LOCAL_BASE_URL = "http://localhost:8081/alvin/rest/";

	private static final String DIVA_LOCAL_APPTOKEN_URL = "http://localhost:8182/login/rest/";
	private static final String DIVA_LOCAL_BASE_URL = "http://localhost:8082/diva/rest/";

	private static final String SYSTEMONE_DEV_APPTOKEN_URL = "http://130.238.171.238:38180/login/rest/";
	private static final String SYSTEMONE_DEV_BASE_URL = "http://130.238.171.238:38080/systemone/rest/";

	private static final String ALVIN_DEV_APPTOKEN_URL = "http://130.238.171.238:38181/login/rest/";
	private static final String ALVIN_DEV_BASE_URL = "http://130.238.171.238:38081/alvin/rest/";

	private static final String DIVA_DEV_APPTOKEN_URL = "http://130.238.171.238:38182/login/rest/";
	private static final String DIVA_DEV_BASE_URL = "http://130.238.171.238:38082/diva/rest/";

	@Test(enabled = false)
	public void runFindEmptyForSystemOneTexts() {

		ChangeFindEmptyAtomics updater = new ChangeFindEmptyAtomics(SYSTEMONE_PREVIEW_APPTOKEN_URL,
				SYSTEMONE_PREVIEW_BASE_URL, "systemoneAdmin@system.cora.uu.se",
				"47e50f91-9d62-44ae-96f7-6102139c838e");

		updater.findEmptyAtomics(DRY_RUN);
	}

	@Test(enabled = false)
	public void runFindEmptyForAlvinOneTexts() {

		// ChangeFindEmptyAtomics updater = new ChangeFindEmptyAtomics(ALVIN_PREVIEW_APPTOKEN_URL,
		// ALVIN_PREVIEW_BASE_URL, "alvinAdmin@cora.epc.ub.uu.se",
		// "41c37615-c33d-4e01-9748-554d1b2cfec1");
		ChangeFindEmptyAtomics updater = new ChangeFindEmptyAtomics(ALVIN_DEV_APPTOKEN_URL,
				ALVIN_DEV_BASE_URL, "alvinAdmin@cora.epc.ub.uu.se",
				"6bd7cba2-5982-4158-9f13-412bf4b486c8");

		updater.findEmptyAtomics(DRY_RUN);
	}

	@Test(enabled = false)
	public void runChangeValidationTypeForSystemOneTexts() {

		ChangeFindEmptyAtomics updater = new ChangeFindEmptyAtomics(SYSTEMONE_DEV_APPTOKEN_URL,
				SYSTEMONE_DEV_BASE_URL, "systemoneAdmin@system.cora.uu.se",
				"47e50f91-9d62-44ae-96f7-6102139c838e");

		updater.findEmptyAtomics(DRY_RUN);
	}

	@Test(enabled = false)
	public void runChangeValidationTypeForAlvinTexts() {

		ChangeFindEmptyAtomics updater = new ChangeFindEmptyAtomics(ALVIN_DEV_APPTOKEN_URL,
				ALVIN_DEV_BASE_URL, "alvinAdmin@cora.epc.ub.uu.se",
				"b1752dd4-eeb4-4b7d-98e2-9d6167cce2e4");

		updater.findEmptyAtomics(DRY_RUN);
	}

	@Test(enabled = false)
	public void runFindEmptyForDivaOneTextsPreview() {

		ChangeFindEmptyAtomics updater = new ChangeFindEmptyAtomics(DIVA_PREVIEW_APPTOKEN_URL,
				DIVA_PREVIEW_BASE_URL, "divaAdmin@cora.epc.ub.uu.se",
				"b5ffafe1-15a5-492b-baa9-511cb2a47b33");

		updater.findEmptyAtomics();
	}

	@Test(enabled = false)
	public void runFindEmptyForDivaOneTextsDev() {

		ChangeFindEmptyAtomics updater = new ChangeFindEmptyAtomics(DIVA_DEV_APPTOKEN_URL,
				DIVA_DEV_BASE_URL, "divaAdmin@cora.epc.ub.uu.se",
				"cb1256d6-29a3-4162-abb0-cd73732963c6");

		updater.findEmptyAtomics(DRY_RUN);
	}

}
