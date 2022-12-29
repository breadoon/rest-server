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
package xyz.breadoon.rest.config.builtInconfig;

import java.io.*;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class BuiltInConfigLoader {

	
	public static BuiltInConfig load() throws JsonParseException, JsonMappingException, IOException {
		
		ObjectMapper om = new ObjectMapper(new YAMLFactory()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		TypeReference<BuiltInConfig> typeRef = new TypeReference<BuiltInConfig>(){};
		
		
		
		return om.readValue(new File("config/built-in.yml"), typeRef);
		
	}
	
	
	public static void main(String[] args) {
		
		BuiltInConfig builtInConfig = null;
		try {
			builtInConfig = BuiltInConfigLoader.load();
			
			if ( builtInConfig != null && builtInConfig.getBuiltIns() != null ) {
				if ( builtInConfig.getBuiltIns().length > 0 ) {
					HashMap<String, String> builtInMap = new HashMap<String, String>();
					
					for( BuiltIn bi : builtInConfig.getBuiltIns() ) {
						
						//System.out.println(bi.getClassName());
						builtInMap.put(bi.getName(), bi.getClassName());
					}
					
					
					System.out.println(builtInMap.get("login"));
					
					
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//System.out.println(cfg.getBuiltIns().length);
		
		
	}
}
