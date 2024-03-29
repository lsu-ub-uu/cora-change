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
package se.uu.ub.cora.change.utils;

import java.util.Map;

public interface RecordTypeUtil {
	/**
	 * getMapOfImplementingToParent return a Map with all existing recordType ids as key and the
	 * abstract parent id for the recordType as value. If there are more than one level of parents
	 * is the top one set as value. If the recordType is an implementing type the recordTypes own id
	 * set as value.
	 * 
	 * @return A {@link Map} with all recordType ids as key and parent as value
	 */
	Map<String, String> getMapOfImplementingToParent();

}