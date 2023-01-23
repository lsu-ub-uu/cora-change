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

import se.uu.ub.cora.change.spies.CoraClientSpy;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataRecord;

public class RecordTypeUpdaterTest {
	private CoraClientSpy coraClient;
	private RecordTypeUpdater updater;

	@BeforeMethod
	public void beforeMethod() {
		coraClient = new CoraClientSpy();
		updater = new RecordTypeUpdater(coraClient);
	}

	@Test
	public void testListRecordTypes() throws Exception {
		updater.addValidationTypeToAllRecordTypes();

		coraClient.MCR.assertParameters("readListAsDataRecords", 0, "recordType");
	}

	@Test
	public void testOneRecordType() throws Exception {
		ClientDataRecord cdr = new ClientDataRecordSpy();
		ClientDataGroup cdg = new ClientDataGroupSpy();
	}

}
