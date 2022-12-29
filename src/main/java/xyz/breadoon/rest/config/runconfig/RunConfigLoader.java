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

import java.io.*;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class RunConfigLoader {

	
	
	public static RunConfig load() throws JsonParseException, JsonMappingException, IOException {
		
		ObjectMapper om = new ObjectMapper(new YAMLFactory()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		TypeReference<RunConfig> typeRef = new TypeReference<RunConfig>(){};
		
		return om.readValue(new File("config/runConfig.yml"), typeRef);
		
	}
	
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		
		RunConfig cfg = RunConfigLoader.load();
		
		System.out.println(cfg.getEnvironmentVariable().getDev().jsonString());
		System.out.println(cfg.getAutoReloadDuration());
		System.out.println(cfg.getCors());
		
		
		
		
		String aa = cfg.getEnvironmentVariable().getOp().read("$.test.SUBSCRIBE_BILL_AMOUNT");
		System.out.println(aa);
	}
}
