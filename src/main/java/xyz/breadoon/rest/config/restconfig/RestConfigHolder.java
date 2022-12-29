/*******************************************************************************
 * This file is part of the breadoon project.
 * Copyright (c) 2022-2022 breadoon@gmail.com
 * Authors: breadoon@gmail.com.
 * This program is offered under a commercial and under the AGPL license.
 * For commercial licensing, contact breadoon@gmail.com.  For AGPL licensing, see below.
 * AGPL licensing:
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package xyz.breadoon.rest.config.restconfig;

import java.util.HashMap;

public class RestConfigHolder {

	// version, <key, api_obj>
	private static HashMap<String, HashMap<String, RestRequest>> restApis = null;

	public static HashMap<String, HashMap<String, RestRequest>> getRestApis() {
		return restApis;
	}

	public static void setRestApis(HashMap<String, HashMap<String, RestRequest>> apis) {
		restApis = apis;
	}
	
	public static void addRestApi(String version, HashMap<String, RestRequest> api) {
		restApis.put(version, api);
	}
	
}
