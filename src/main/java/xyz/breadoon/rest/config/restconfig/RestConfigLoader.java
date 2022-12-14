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

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import xyz.breadoon.rest.util.FileUtil;
import xyz.breadoon.rest.util.LogUtil;

public class RestConfigLoader {

	private static HashMap<String, Long> dirStats = new HashMap<String, Long>();
	private static FilenameFilter yamlFilefilter = new FilenameFilter(){
		public boolean accept(File dir, String name) {
			String lowercaseName = name.toLowerCase();
            if (lowercaseName.endsWith(".yml")) {
               return true;
            } else {
               return false;
            }
		}
    };
	
    /**
     * version, <request_id, request_obj> map return
     * @param yamlRootDir
     * @return
     */
	public static HashMap<String, HashMap<String, RestRequest>> load(String yamlRootDir) {
		
		ObjectMapper om = new ObjectMapper(new YAMLFactory()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
	    File yamlSrcDir = new File(yamlRootDir);
	    File[] versionDirList = yamlSrcDir.listFiles();
	    
	    TypeReference<HashMap<String, RestRequest>> typeRef = new TypeReference<HashMap<String, RestRequest>>(){};
	    HashMap<String, HashMap<String, RestRequest>> requestTotal = new HashMap<String, HashMap<String, RestRequest>>();
	    HashMap<String, RestRequest> requestList = null;
	    
	    
	    Path yamlPath = null; 
	    Path rootPath = Paths.get(new File(yamlRootDir).getAbsolutePath());
	    String relativePathStr = null, version = null;
	    File[] yamlFileList = null;
	    long dirLastModified = 0L;
	    
	    // rest api root ????????? version ??????????????? ???????????????.
	    // ?????? multi-context ??????
	    for ( File versionDir : versionDirList ) {
	    	
	    	// ????????? skip??????.
	    	if ( versionDir.isDirectory() ) {
	    	
	    		version = versionDir.getName();
	    		dirLastModified = FileUtil.findLastModifiedInDir(versionDir);
	    		
	    		if ( dirStats.get(version) == null || dirStats.get(version) < dirLastModified )
	    		{
	    			yamlFileList = versionDir.listFiles(yamlFilefilter);
	    			
			    	for ( File eachYaml : yamlFileList ) {
			    	
			    		// yaml ????????? ????????? ???????????? ????????? ????????? ????????????.
				    	if ( eachYaml.length() > 0 ) {
					    	try {
								requestList = om.readValue(eachYaml, typeRef);
								
								// swagger?????? ???????????? ?????? ???????????? tag??? ?????? ?????????.
								for (RestRequest req : requestList.values()) {
						    		req.setTag(eachYaml.getName());
						    	}
							} catch (JsonParseException e) {
								// TODO Auto-generated catch block
								LogUtil.write(e);
							} catch (JsonMappingException e) {
								// TODO Auto-generated catch block
								LogUtil.write(e);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								LogUtil.write(e);
							}
					    	
					    	// ?????? ?????? ????????? 
					    	if( requestTotal.get(version) != null && requestTotal.get(version).size() > 0 ) {
					    		requestTotal.get(version).putAll(requestList);
					    	} else {
					    		requestTotal.put(version, requestList);
					    	}
					    	
				    	}
			    	} // yaml file list for
			    	
			    	dirStats.put(version, dirLastModified);
	    		}
	    	} // version dir for
	    }
	    
	    return requestTotal;
		
	}
	
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		
		HashMap<String, HashMap<String, RestRequest>> restapiList = RestConfigLoader.load("config/restapi");
		
	}
}
