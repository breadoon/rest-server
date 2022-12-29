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
package xyz.breadoon.rest.config.runconfig;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

public class EnvironmentVariable {
	
	private static Configuration jsonConfig = Configuration.defaultConfiguration()
						            .jsonProvider(new JacksonJsonProvider())
						            .mappingProvider(new JacksonMappingProvider());
	private static ObjectMapper mapper = new ObjectMapper();
	
    private DocumentContext dev;
    private DocumentContext op;

    public DocumentContext getDev() { return dev; }
    public void setDev(Object value) { 
    	try {
			this.dev = JsonPath.using(jsonConfig).parse(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(value));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			this.dev = null;
		}; 
    }

    public DocumentContext getOp() { return op; }
    public void setOp(Object value) { 
    	try {
			this.op = JsonPath.using(jsonConfig).parse(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(value));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			this.op = null;
		}; 
    }
}
