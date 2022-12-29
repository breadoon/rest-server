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

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import xyz.breadoon.rest.processing.builtin.BuiltInHandlerAbstract;
import xyz.breadoon.rest.processing.builtin.BuiltInHandlerIF;
import xyz.breadoon.rest.util.LogUtil;


public class BuiltInHelper {
	
	// 함수명, 클래스 full package 쌍 구조
	private static HashMap<String, String> BUILT_INS = new HashMap<String, String>();
	private static BuiltInHelper helper = null;
	
	
	public static BuiltInHelper getInstance() {
		if ( helper == null ) {
			helper = new BuiltInHelper();
			return helper;
		}
		else
			return helper;
	}
	
	private BuiltInHelper() {
		
		// system에서 만든 built-in을 등록한다.
	}
	
	public void addBuiltIns(Map<String,String> userDefinedBuiltIns) {
		
		Iterator<String> userKeys = userDefinedBuiltIns.keySet().iterator();
		
		String key = null;
		while( userKeys.hasNext()) {
			key = userKeys.next();
			
			if ( BUILT_INS.containsKey(key) ) {
				LogUtil.write("User defined built-in function [" + key + "] is aleady in system function");
			} else {
				BUILT_INS.put(key, userDefinedBuiltIns.get(key));
			}
		}
		
		
		
	}

	public BuiltInHandlerIF getBuiltInHandlerIF(String builtInName) throws BuiltInClassNotFoundException {
		
		Class builtInClass = null;
		String className = null;
		try {
			
			// 아래 코드에서 Map에서 직접 읽을 경우 오류가 발생하므로 따로 String으로 받아서 읽어야 한다.
			className = BUILT_INS.get(builtInName);
			builtInClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new BuiltInClassNotFoundException(builtInName + " not found");			
		}
		
		Constructor constructor = null ;
		try {
			constructor = builtInClass.getConstructor(null);
		} catch (Exception e) {
			throw new BuiltInClassNotFoundException(builtInName + " need default constructor");
		}
		
		
		Object obj = null;
		try {
			obj = constructor.newInstance();
		} catch (Exception e) {
			throw new BuiltInClassNotFoundException(builtInName + " can not create instance.");
		} 
		
		if ( obj instanceof BuiltInHandlerIF ) 
			return (BuiltInHandlerIF)obj;
		else
			throw new BuiltInClassNotFoundException(builtInName + " is not a BuiltInHandlerIF instance");
	
	}
	
	public BuiltInHandlerAbstract getBuiltInHandlerAbstract(String builtInName) throws BuiltInClassNotFoundException {
		
		Class builtInClass = null;
		String className = null;
		try {
			
			// 아래 코드에서 Map에서 직접 읽을 경우 오류가 발생하므로 따로 String으로 받아서 읽어야 한다.
			className = BUILT_INS.get(builtInName);
			builtInClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new BuiltInClassNotFoundException(builtInName + " not found");			
		}
		
		Constructor constructor = null ;
		try {
			constructor = builtInClass.getConstructor(null);
		} catch (Exception e) {
			throw new BuiltInClassNotFoundException(builtInName + " need default constructor");
		}
		
		
		Object obj = null;
		try {
			obj = constructor.newInstance();
		} catch (Exception e) {
			throw new BuiltInClassNotFoundException(builtInName + " can not create instance.");
		} 
		
		if ( obj instanceof BuiltInHandlerAbstract ) 
			return (BuiltInHandlerAbstract)obj;
		else
			throw new BuiltInClassNotFoundException(builtInName + " is not a BuiltInHandlerAbstract instance");
	
	}
}
