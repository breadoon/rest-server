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
package xyz.breadoon.rest.processing.helper;

import java.util.UUID;


import io.vertx.core.http.HttpServerRequest;
import xyz.breadoon.rest.auth.impl.DefaultAuthHandler;

public class SystemVariableHelper {

	public static String get32Uuid() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	public static String getSessionUuid(HttpServerRequest req) {
		
		try {
			return new DefaultAuthHandler().getSessionId(req);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
}
