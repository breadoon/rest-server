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
package xyz.breadoon.rest.processing.builtin.impl;

import java.util.HashMap;
import java.util.List;


import com.fasterxml.jackson.databind.ObjectMapper;
import xyz.breadoon.rest.config.restconfig.RestRequest;
import xyz.breadoon.rest.mapping.RestApiMapper;
import xyz.breadoon.rest.processing.builtin.BuiltInHandlerIF;
import xyz.breadoon.rest.processing.builtin.BuiltInRuntimeException;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

public class NewRestApiBuiltIn implements BuiltInHandlerIF {

	
	@Override
	public Object process(HttpServerRequest req, HttpServerResponse resp, List<Object> args)
			throws BuiltInRuntimeException {
		
		if( args.size() < 3 ) {
			throw new BuiltInRuntimeException("need [verion, key, apiObject]");
		}
		
		// apiobject 객체는 HashMap 구조로 온다.
		HashMap apiHashMap = (HashMap)args.get(2);
		ObjectMapper mapper = new ObjectMapper();
		RestRequest api = mapper.convertValue(apiHashMap, RestRequest.class);
		RestApiMapper.addSingleApi((String)args.get(0), (String)args.get(1), api);
		
		return "";
	}

}
