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
package se.uu.ub.cora.change.spy;

import java.util.Collections;
import java.util.Map;

import se.uu.ub.cora.change.utils.RecordTypeUtil;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class RecordTypeUtilSpy implements RecordTypeUtil {

	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public RecordTypeUtilSpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("getMapOfImplementingToParent",
				() -> Collections.emptyMap());
	}

	@Override
	public Map<String, String> getMapOfImplementingToParent() {
		return (Map<String, String>) MCR.addCallAndReturnFromMRV();
	}

}
