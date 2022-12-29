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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

import io.vertx.core.MultiMap;
import io.vertx.ext.web.RoutingContext;
import xyz.breadoon.rest.config.restconfig.SqlJob;
import xyz.breadoon.rest.config.runconfig.RunConfig;
import xyz.breadoon.rest.processing.helper.SystemVariableHelper;
import xyz.breadoon.rest.processing.obj.RuntimeInfo;
import xyz.breadoon.rest.util.StringUtil;

public class ParameterAnalyzer {

	private static final Pattern parenPattern = Pattern.compile("(!![^)]+)");
	private static final Configuration jsonConfig = Configuration.defaultConfiguration()
									            .jsonProvider(new JacksonJsonProvider())
									            .mappingProvider(new JacksonMappingProvider());
	
	private static final String ARROW_STR = "->";
	private static final String CONTENT_TYPE_HEADER = "content-type";
	
	public RuntimeInfo initRuntimeInfo(RoutingContext rc) {
		
		RuntimeInfo runtimeInfo = new RuntimeInfo();
		runtimeInfo.setPathParams(rc.pathParams());
		
		MultiMap mm = null;
		
		
		if( rc.request().getHeader(CONTENT_TYPE_HEADER) != null && rc.request().getHeader(CONTENT_TYPE_HEADER).toLowerCase().startsWith("multipart/form-data"))
			mm = rc.request().formAttributes();
		else
			mm = rc.queryParams();
		
		HashMap<String, String> queryMap = new HashMap<String, String>();
		for( Entry<String,String> entry :  mm.entries()) 
			queryMap.put(entry.getKey(), entry.getValue());
		
		runtimeInfo.setQueryParams(queryMap);
		
		if( rc.request().getHeader(CONTENT_TYPE_HEADER) != null && "application/json".equalsIgnoreCase(rc.request().getHeader(CONTENT_TYPE_HEADER))) {
			DocumentContext documentContext = null;
			try {
				if ( rc.getBodyAsString() != null && rc.getBodyAsString().length() > 0 )
					documentContext = JsonPath.using(jsonConfig).parse(rc.getBodyAsString());
			} catch( InvalidJsonException e ) {
				// GET인 경우 JSON이 오지 않을 수도 있다.
			}
			runtimeInfo.setJsonContext(documentContext);
		}
		
		runtimeInfo.setRoutingContext(rc);
		
		return runtimeInfo;
	}
	
	/**
	 * 순서별 파라미터 값을 얻는다.
	 * @param inputParamStrList
	 * @param runtimeInfo
	 * @param isJson
	 * @return
	 * @throws InputParamException
	 */
	public List<Object> getParamValues(String[] inputParamStrList, RuntimeInfo runtimeInfo) throws InputParamException {
    	
		ArrayList<Object> rtnParams = new ArrayList<Object>();
		String typeNdefaultStr = null, typeStr = null, defaultStr = null, valueStr = null, valueTypeStr = null, valueNameStr = null;
		Object valueObj = null;
		String[] splits = null;
		
		if( inputParamStrList != null )
		for( String inputStr: inputParamStrList) {
	    	Matcher matcher = parenPattern.matcher(inputStr);
	        
	        if(!matcher.find()) 
	        	throw new InputParamException("Input type directive not found");
	        
	        typeNdefaultStr = matcher.group(1);
	        splits = typeNdefaultStr.trim().split("[\\s]+");  
	        
	        // default value 설정이 있는 경우 
	        if ( splits.length == 2) {
	        	typeStr = splits[0]; defaultStr = splits[1];
	        } else {
	        	typeStr = splits[0]; defaultStr = null;
	        	
	        }
	        
	        valueStr = inputStr.substring(typeNdefaultStr.length() + 2).trim();
	        
	        // 상수 문자열이면 더 이상 확인하지 않아도 된다.
	        if ( "!!constant-string".startsWith(typeStr.toLowerCase()) ) {
	        	rtnParams.add(valueStr);
	        	continue;
	        	
	        }
	        
	        if ( valueStr.length() >= 2 ) {
	        	valueTypeStr = valueStr.substring(0, 2);
	        	valueNameStr = valueStr.substring(2).trim();
	        	
	        	if ( valueTypeStr.startsWith("e.") ) {	// environment value, 이 경우 다른 입력을 찹조할 수 있음.
	        	
	        		try {
	        			if ( RunConfig.getIsDev() ) 
		        			valueObj = RunConfig.getEnvironmentVariable().getDev().read("$." + valueNameStr);
		        		else
		        			valueObj = RunConfig.getEnvironmentVariable().getOp().read("$." + valueNameStr);
		        	} catch(Exception e ) {
		        		throw new InputParamException(valueNameStr + " : " + e.getMessage());
		        	}
	        		
	        		
	        		if ( valueObj instanceof String && isRuntimeVariable((String)valueObj)) {
	        			valueObj = getEvaluateValue((String)valueObj, runtimeInfo);
	        		}
	        		
	        	} else if ( isRuntimeVariable(valueStr) ) {
	        		valueObj = getEvaluateValue(valueStr, runtimeInfo);
	        	} else {	// 직접 입력값인 경우는 그대로 사용한다.
	        		valueObj = valueStr;
	        	}
		        
	        } else {
	        	valueObj = valueStr;	// 직접 입력값인 경우는 그대로 사용한다.
	        }
	        
	        // value object가 null인 경우 default string을 이용하여 값을 채울 수 있도록 한다.
	        if ( valueObj == null &&  defaultStr != null ) {
	        	valueObj = defaultStr;
	        }
	        
	        if ( "!!string".startsWith(typeStr.toLowerCase()) ) {
	        	
	        	if ( valueObj == null || (valueObj instanceof String && "nil".equalsIgnoreCase((String)valueObj)) ) {
	        		rtnParams.add(null);
	        	} else {
	        		if (valueObj instanceof String ) 
	        			rtnParams.add(valueObj);
	        		else
	        			throw new InputParamException(valueStr + " must be string type");
	        		
	        		
	        	}
	        	
	        } else if ( "!!int".startsWith(typeStr.toLowerCase()) ) {
	        	
	        	
	        	if ( valueObj == null )
	        		rtnParams.add(0);
	        	else {
		        	if ( valueObj instanceof String && StringUtil.isNumeric((String)valueObj)) {
		        		try {
		        			// 자동 형변환을 진행한다.
		        			rtnParams.add((int)Double.parseDouble((String)valueObj));
		        		} catch ( NumberFormatException e) {
		        			throw new InputParamException(valueStr + " must be int type");
		        		}
		        			
		        	} else if ( valueObj instanceof Double ||
		        				valueObj instanceof Float ||
		        				valueObj instanceof Long ||
		        				valueObj instanceof Integer 
		        			) {
		        		rtnParams.add((int)valueObj);
		        	} else 
		        		throw new InputParamException(valueStr + " must be int type");
	        	}
	        	
	        	
	        } else if ( "!!long".startsWith(typeStr.toLowerCase()) ) {
	        	if ( valueObj == null )
	        		rtnParams.add((long)0);
	        	else {
	        	
		        	if ( valueObj instanceof Long && StringUtil.isNumeric((String)valueObj)) {
		        		try {
		        			// 자동 형변환을 진행한다.
		        			rtnParams.add((long)Double.parseDouble((String)valueObj));
		        		} catch ( NumberFormatException e) {
		        			throw new InputParamException(valueStr + " must be int type");
		        		}
		        			
		        	} else if ( valueObj instanceof Double ||
		        				valueObj instanceof Float ||
		        				valueObj instanceof Long || 
		        				valueObj instanceof Integer 
		        			) {
		        		rtnParams.add((long)valueObj);
		        	} else 
		        		throw new InputParamException(valueStr + " must be long type");
	        	}
	        	
	        } else if ( "!!float".startsWith(typeStr.toLowerCase()) ) {
	        	if ( valueObj == null )
	        		rtnParams.add((float)0.0);
	        	else {
	        	
		        	if ( valueObj instanceof Float && StringUtil.isNumeric((String)valueObj)) {
		        		try {
		        			// 자동 형변환을 진행한다.
		        			rtnParams.add((float)Double.parseDouble((String)valueObj));
		        		} catch ( NumberFormatException e) {
		        			throw new InputParamException(valueStr + " must be int type");
		        		}
		        			
		        	} else if ( valueObj instanceof Double ||
		        				valueObj instanceof Float ||
		        				valueObj instanceof Long || 
		        				valueObj instanceof Integer 
		        			) {
		        		rtnParams.add((float)valueObj);
		        	} else 
		        		throw new InputParamException(valueStr + " must be float type");
	        	}
	        	
	        } else if ( "!!double".startsWith(typeStr.toLowerCase()) ) {
	        	if ( valueObj == null )
	        		rtnParams.add((long)0.0);
	        	else {
	        	
		        	if ( valueObj instanceof Double && StringUtil.isNumeric((String)valueObj)) {
		        		try {
		        			// 자동 형변환을 진행한다.
		        			rtnParams.add((float)Double.parseDouble((String)valueObj));
		        		} catch ( NumberFormatException e) {
		        			throw new InputParamException(valueStr + " must be int type");
		        		}
		        			
		        	} else if ( valueObj instanceof Double ||
		        				valueObj instanceof Float ||
		        				valueObj instanceof Long || 
		        				valueObj instanceof Integer 
		        			) {
		        		rtnParams.add((double)valueObj);
		        	} else 
		        		throw new InputParamException(valueStr + " must be double type");
	        	}
	        	
	        } else if ( "!!interface".startsWith(typeStr.toLowerCase()) ) { // json string object로 반환된다. 받는쪽에서 type mapping을 해서 사용해야 한다.
	        	rtnParams.add(valueObj);
	        } 
		}
		
		return rtnParams;
        
    }
	
		
	
	
	/**
	 * SQL 치환을 위한 파라미터 값을 얻는다.
	 * @param replaceParamStrList
	 * @param runtimeInfo
	 * @param isJson
	 * @return
	 * @throws InputParamException
	 */
	public Object getStringReplaceParamValue(String replaceParamStr, RuntimeInfo runtimeInfo) throws InputParamException {
    	
		String valueTypeStr = null, valueNameStr = null;
		Object valueObj = null;
		
	    	
        if ( replaceParamStr.length() >= 2 ) {
        	valueTypeStr = replaceParamStr.substring(0, 2);
        	valueNameStr = replaceParamStr.substring(2).trim();
        	
        	if ( valueTypeStr.startsWith("e.") ) {	// environment value, 이 경우 다른 입력을 찹조할 수 있음.
        		
        		try {
	        		if ( RunConfig.getIsDev() ) 
	        			valueObj = RunConfig.getEnvironmentVariable().getDev().read("$." + valueNameStr);
	        		else
	        			valueObj = RunConfig.getEnvironmentVariable().getOp().read("$." + valueNameStr);
	        		
	        	} catch(Exception e ) {
	        		throw new InputParamException(valueNameStr + " : " + e.getMessage());
	        	}
        		
        		if ( valueObj instanceof String && isRuntimeVariable((String)valueObj)) {
        			valueObj = getEvaluateValue((String)valueObj, runtimeInfo);
        		}
        		
        	} else if ( isRuntimeVariable(replaceParamStr) ) {
        		valueObj = getEvaluateValue(replaceParamStr, runtimeInfo);
        	}
	        
        }
        
		
		return valueObj;
        
    }
	
	/**
	 * 각 파라미터의 값을 평가하여 반환한다.
	 * @param paramName
	 * @param runtimeInfo
	 * @param isJson
	 * @return
	 * @throws InputParamException
	 */
	private Object getEvaluateValue(String paramName, RuntimeInfo runtimeInfo) throws InputParamException {
		Object valueObj = null;
		
		String valueTypeStr = null, valueNameStr = null;
		DocumentContext ctx = null;
		
		if ( paramName.length() >= 2 ) {
        	valueTypeStr = paramName.substring(0, 2);
        	valueNameStr = paramName.substring(2).trim();
        	
        	if ( "p.".equalsIgnoreCase(valueTypeStr) ) {	// path param
	        	valueObj = runtimeInfo.getPathParams().get(valueNameStr);
	        } else if ( valueTypeStr.equalsIgnoreCase("r.") ) {	// request param
	        	
	        	if ( !runtimeInfo.getIsJson() )
	        		valueObj = runtimeInfo.getQueryParams().get(valueNameStr);
	        	else {
	        		if ( runtimeInfo.getJsonContext() != null )
		        		try {
		        			valueObj = runtimeInfo.getJsonContext().read("$.data." + valueNameStr, new TypeRef<Object>(){} );
			        	} catch(Exception e ) {
			        		throw new InputParamException(valueNameStr + " : " + e.getMessage());
			        	}
	        	}
	        } else if ( "s.".equalsIgnoreCase(valueTypeStr) ) {	// system variable
	        	if ( "UID".equalsIgnoreCase(valueNameStr) )
	        		valueObj = SystemVariableHelper.get32Uuid();
	        	else if ( "UserSessionId".equalsIgnoreCase(valueNameStr) )
	        		valueObj = SystemVariableHelper.getSessionUuid(runtimeInfo.getRoutingContext().request());
	        	else {	// 개발자가 system variable을 등록할 수 있도록 한다.
	        		throw new InputParamException("Unsupported system variable.");
	        	}
	        	
	        } else if ( "f.".equalsIgnoreCase(valueTypeStr) ) {	// sql runtime value 
	        												// 별도의 이름을 지정하는 경우는 UserSavedObject로 들어가게 된다. 
	        	try {
	        		valueObj = runtimeInfo.getSqlResults().read("$." + valueNameStr, new TypeRef<Object>(){} );
	        	} 
	        	catch(Exception e ) {
	        		throw new InputParamException( paramName + " : " + e.getMessage());
	        	}
	        } else if ( "u.".equalsIgnoreCase(valueTypeStr) ) {	// user saved Object value 
	        	
	        	int idx = valueNameStr.indexOf(".");
	        	
	        	if ( idx == -1 ) 
	        		throw new InputParamException("UserSavedObject name is not found.");
	        	
	        	String userObjName = valueNameStr.substring(0, idx);
	        	String userObjPath = valueNameStr.substring(idx).trim();
	        	
	        	ctx =  runtimeInfo.getUserSavedObjs().get(userObjName);
	        	
	        	if ( ctx == null )
	        		return null;
	        	
	        	try {
	        		valueObj = ctx.read("$" + userObjPath, new TypeRef<Object>(){} );
	        	} catch(Exception e ) {
	        		throw new InputParamException(paramName + " : " + e.getMessage());
	        	}
	        	
			} else if ( "b.".equalsIgnoreCase(valueTypeStr) ) {	// built-in function return 
	        	
	        	int idx = valueNameStr.indexOf(".");
	        	
	        	if ( idx == -1 ) 
	        		throw new InputParamException("BuiltIn function name is not found.");
	        	
	        	String builtInFuncName = valueNameStr.substring(0, idx);
	        	String builtInObjPath = valueNameStr.substring(idx).trim();
	        	
	        	ctx =  runtimeInfo.getBuiltInResults().get(builtInFuncName);
	        	
	        	if ( ctx == null )
	        		return null;
	        	
	        	try {
	        		valueObj = ctx.read("$" + builtInObjPath, new TypeRef<Object>(){} );
	        	} catch(Exception e ) {
	        		throw new InputParamException(paramName + " : " + e.getMessage());
	        	}
			} 

	        
        }
		
		return valueObj;
	}
	
	
	
	
	
	
	
	private boolean isRuntimeVariable(String paramName) {
		
		String valueTypeStr = null;
		
		if ( paramName.length() >= 2 ) {
        	valueTypeStr = paramName.substring(0, 2);
			if ( "p.".equalsIgnoreCase(valueTypeStr) 
				|| "r.".equalsIgnoreCase(valueTypeStr)
				|| "s.".equalsIgnoreCase(valueTypeStr)
				|| "f.".equalsIgnoreCase(valueTypeStr)
				|| "u.".equalsIgnoreCase(valueTypeStr)
				|| "b.".equalsIgnoreCase(valueTypeStr)
				) return true;
			else return false;
		}
		else
			return false;
	}
}
