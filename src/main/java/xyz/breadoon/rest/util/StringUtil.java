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
package xyz.breadoon.rest.util;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
	
	private static final Pattern curlyBracePattern = Pattern.compile("\\{[^\\}]*\\}");
	private static final String urlRegex = "^((http|https)://)?(www.)?([a-zA-Z0-9]+)\\.[a-z]+([a-zA-z0-9.?#]+)?";
	

	public static String curlyBraceVarToVertxVar(String data) {
		Matcher m = curlyBracePattern.matcher(data);
		HashMap<String, String> replaceMap = new HashMap<String, String>();
		String d;
		while(m.find()){
		    d = data.substring(m.start(), m.end());
		    replaceMap.put(d, ":" + data.substring(m.start()+1, m.end()-1));
		}
		
		
		for( String key : replaceMap.keySet() ) 
			data = data.replace(key, replaceMap.get(key));
		
		return data;
	}
	
	public static boolean isNumeric(String input) {
		try {
			Double.parseDouble(input);
			return true;
		}
		catch (NumberFormatException e) {
			return false;
		}
	}
	
	
	public static String getFormatedString(Object obj) {
		if ( obj instanceof String ) {
			return (String) obj;
    	} else if ( obj instanceof Double ) {
    		return String.format("%f", obj);
    	} else if ( obj instanceof Float ) {
    		return String.format("%f", obj);
    	} else if ( obj instanceof Long ) {
    		return String.format("%d", obj);
    	} else if ( obj instanceof Integer ) {
    		return String.format("%d", obj);
    	} else if ( obj instanceof Boolean ) {
    		return String.format("%b", obj);
    	} else {
    		return "";
    	}
    				
	}
	
	public static String getOriginStringWithoutPort(String origin) {
		Pattern parenPattern = Pattern.compile(urlRegex);

		Matcher matcher = parenPattern.matcher(origin);
			        
		if(matcher.find()) {
			return matcher.group(0);
		} else {
			return origin;
		}
	}


	
	public static void main(String[] args) {
		System.out.println(StringUtil.curlyBraceVarToVertxVar("/products/{product_id}/hashtags/{product_id2}"));
		System.out.println(StringUtil.curlyBraceVarToVertxVar("/products/:product_id/hashtags/:product_id2"));
	}
	
	
}
