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
package xyz.breadoon.rest.processing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.breadoon.rest.processing.obj.RuntimeInfo;
import xyz.breadoon.rest.util.StringUtil;

public class StringAnalyzer {

	// ${aaa.aaa}와 같은 패턴을 찾아내기 위함.
	private static Pattern replaceParamPattern = Pattern.compile("\\$\\{[^}]+}");
	
	public static String makeFlattenString(String str, RuntimeInfo runtimeInfo) throws InputParamException {
		
    	Matcher matcher = replaceParamPattern.matcher(str);
        
    	ArrayList<String> replaceParamList = new ArrayList<String>();
    	HashMap<String, Object> replaceObjs = new HashMap<String, Object>();
    	
        while(matcher.find()) 
        	replaceParamList.add(matcher.group(0));
        
        if ( replaceParamList.size() > 0 ) {
        	
        	ParameterAnalyzer analyzer = new ParameterAnalyzer();
        	
        	Object obj = null;
        	for(String param : replaceParamList) {
        		obj = analyzer.getStringReplaceParamValue(param.substring(2,param.length()-1).trim(), runtimeInfo);
        		replaceObjs.put(param, obj);
        	}
        	
        	
        	// replace를 하도록 한다.
        	Iterator<String> itr = replaceObjs.keySet().iterator();
        	String key = null;
        	while( itr.hasNext() ) {
        		key = itr.next();
        		str = str.replace(key, StringUtil.getFormatedString(replaceObjs.get(key)));
        	}
        	
        } 
        
        return str;
    }
	
	
}
