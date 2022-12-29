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

import java.sql.SQLException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import xyz.breadoon.rest.auth.AuthHandlerIF;
import xyz.breadoon.rest.auth.impl.DefaultAuthHandler;
import xyz.breadoon.rest.config.restconfig.RestConfigHolder;
import xyz.breadoon.rest.config.restconfig.RestRequest;
import xyz.breadoon.rest.config.runconfig.RunConfig;
import xyz.breadoon.rest.processing.builtin.BuiltInHandlerAbstract;
import xyz.breadoon.rest.processing.builtin.BuiltInHandlerIF;
import xyz.breadoon.rest.processing.builtin.BuiltInRuntimeException;
import xyz.breadoon.rest.processing.helper.BuiltInClassNotFoundException;
import xyz.breadoon.rest.processing.helper.BuiltInHelper;
import xyz.breadoon.rest.processing.obj.ResponseResult;
import xyz.breadoon.rest.processing.obj.RuntimeInfo;
import xyz.breadoon.rest.util.JsonPathUtil;
import xyz.breadoon.rest.util.LogUtil;

import io.vertx.core.Vertx;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;

public class RestRequestProcessor {
	
	
	private static final String SUB_FUNCTION_STRING = "subFunc";
	
	private static final String BODY_TYPE_URLENCODE = "URLENCODE";
	private static final String BODY_TYPE_JSON = "JSON";
	private static final String BODY_TYPE_FORM = "FORM";
	
	
	private static final String ACTION_TYPE_BUILT_IN = "built-in";
	private static final String ACTION_TYPE_SQL = "sql";
	private static final String ACTION_TYPE_BLOCK = "block";
	
	private static final String NEXT_PIPE_DECIDER = "NextPipeDecider";
	
	private static final String[] SYSTEM_BUILTINS = {"RollbackExit", "SendResponse", "SaveAsUserObject", "SetVariable", NEXT_PIPE_DECIDER };
	
	private static ObjectMapper objectMapper = new ObjectMapper();
			
	public void process(Vertx vertx, RoutingContext rc, String version, String requestKey) {
		
		vertx.executeBlocking(promise -> {
			
			ParameterAnalyzer parameterAnalyzer = new ParameterAnalyzer();
			RuntimeInfo runtimeInfo = parameterAnalyzer.initRuntimeInfo(rc);
			
			LogUtil.write(String.format("version : %s, requestKey : %s --> START", version, requestKey));
			processInternal(rc, version, requestKey, runtimeInfo);
			LogUtil.write(String.format("version : %s, requestKey : %s --> END", version, requestKey));
			promise.complete(runtimeInfo);

			
		}, res-> {
			sendResponse(rc, (RuntimeInfo)res.result());
		}
		);
			
		
	}
	
	private void processInternal(RoutingContext rc, String version, String requestKey, RuntimeInfo runtimeInfo) {
		
		
		RestRequest request = RestConfigHolder.getRestApis().get(version).get(requestKey);
		
		// deep copy
		try {
			request = objectMapper.readValue(objectMapper.writeValueAsString(request), RestRequest.class);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			LogUtil.write(e);
		}

		
		String apiName = null;
		
		// 일단 parent의 body type을 쫒아간다.
		String subFuncName = null;
		if ( BODY_TYPE_URLENCODE.equalsIgnoreCase(request.getBodyType()) && runtimeInfo.getQueryParams().get(SUB_FUNCTION_STRING) != null ) {
			subFuncName = runtimeInfo.getQueryParams().get(SUB_FUNCTION_STRING);
		} else if ( BODY_TYPE_JSON.equalsIgnoreCase(request.getBodyType())) { 
			if( runtimeInfo.getJsonContext() != null ) {
				try {
					subFuncName = runtimeInfo.getJsonContext().read("$." + SUB_FUNCTION_STRING);
				} catch(Exception e) {
					// do nothing.
				}
				
				if ( subFuncName != null && !"".equals(subFuncName.trim()) )
					subFuncName = subFuncName.trim();
			}
		}
		
		// subFunc인 경우 body type은 같아야 한다.
		if( subFuncName != null && !"".equals(subFuncName.trim()) ) {
			apiName = requestKey + "-" + subFuncName;
			request = RestConfigHolder.getRestApis().get(version).get(apiName);
			
			// deep copy
			try {
				request = objectMapper.readValue(objectMapper.writeValueAsString(request), RestRequest.class);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				LogUtil.write(e);
			}
		} else
			apiName = requestKey;
		
		
		if ( request == null ) {
			runtimeInfo.setResponseResult(new ResponseResult("error", "api id '" + apiName + "' is not found.", null));
			return;
		}
		
		
		if( request.isNeedAuth() ) {
			boolean isValid = false;
			 try {
				 isValid = getAuthHander().checkAuth(rc.request(), rc.response());
			} catch (Exception e) {
				isValid = false;
				LogUtil.write(e);
			}
			 
			if ( !isValid ) {
				runtimeInfo.setResponseResult(new ResponseResult("auth", "Not authorized access path: " + rc.request().absoluteURI(), null));
				return;
			}
		}
		
		boolean isJson = BODY_TYPE_URLENCODE.equalsIgnoreCase(request.getBodyType()) || BODY_TYPE_FORM.equalsIgnoreCase(request.getBodyType()) ? false : true;
		boolean isFormData = BODY_TYPE_FORM.equalsIgnoreCase(request.getBodyType()) ? true : false;
		
		runtimeInfo.setIsJson(isJson);
		
		// buit-in type인 경우 
		if ( ACTION_TYPE_BUILT_IN.equalsIgnoreCase(request.getActionType()) ) 
			processBuiltInJob(rc, request, runtimeInfo, apiName, true);
		else if ( ACTION_TYPE_SQL.equalsIgnoreCase(request.getActionType()) )  // sql type인 경우 
			processSqlJob(rc, request, runtimeInfo, apiName, true);
		else if ( ACTION_TYPE_BLOCK.equalsIgnoreCase(request.getActionType()) ) { // block type인 경우 
			// trial version에서는 이 기능을 제공하지 않는다.			
			runtimeInfo.setResponseResult(new ResponseResult(apiName, "This feature is not supported in this version", null));
		}
	}
	
	
	private void processBuiltInJob(RoutingContext rc, RestRequest request, RuntimeInfo runtimeInfo, String apiName, boolean doReturn) {
		String builtInName = request.getBuiltIn().getName();
		ParameterAnalyzer pa = new ParameterAnalyzer();
		List<Object> args = null;
		try {
			args = pa.getParamValues(request.getBuiltIn().getParams(), runtimeInfo);
		} catch (InputParamException e) {
			LogUtil.write(e);
			runtimeInfo.setResponseResult(new ResponseResult(apiName, e.getMessage(), null));
			return;
		}
		
		Object rtn = null; 
				
		try {
			boolean isFormData = BODY_TYPE_FORM.equalsIgnoreCase(request.getBodyType()) ? true : false;
			
			rtn = processBuiltInFunction(rc, builtInName, isFormData, args);
			
			if (doReturn) {
				runtimeInfo.setResponseResult(new ResponseResult("", "", rtn));
			} else {
				String saveAs = request.getBuiltIn().getSaveAs();
				if( saveAs != null && "".equalsIgnoreCase(saveAs.trim()))
					runtimeInfo.getUserSavedObjs().put(saveAs, JsonPathUtil.getDocumentContext(rtn));
				else
					runtimeInfo.getBuiltInResults().put(builtInName, JsonPathUtil.getDocumentContext(rtn));
			}
			
		} catch (BuiltInClassNotFoundException | BuiltInRuntimeException | JsonProcessingException e) {
			runtimeInfo.setResponseResult(new ResponseResult(apiName, e.getMessage(), null));
		}
		
	}
	
	
	
	private void processSqlJob(RoutingContext rc, RestRequest request, RuntimeInfo runtimeInfo, String apiName, boolean doReturn) {
		String qry = request.getSql().getQuery();
		if ( qry == null || qry.trim().length() == 0 ) {
			runtimeInfo.setResponseResult(new ResponseResult(apiName, "Has no sql information.", null));
			return;
		}
		
		try {
			if ( !request.getSql().isUseMybatis() ) // mybatis가 아닌 경우만 flattening을 진행한다. 
				qry = StringAnalyzer.makeFlattenString(qry, runtimeInfo);
		} catch (InputParamException e) {
			runtimeInfo.setResponseResult(new ResponseResult(apiName, e.getMessage(), null));
			return;
		}
		
		ParameterAnalyzer pa = new ParameterAnalyzer();
		List<Object> args = null;
		try {

			// trial version에서는 mybatis 관련 분석 기능을 제공하지 않는다. 
			args = pa.getParamValues(request.getSql().getParams(), runtimeInfo);
			
			
		} catch (InputParamException e) {
			LogUtil.write(e);
			runtimeInfo.setResponseResult(new ResponseResult(apiName, e.getMessage(), null));
			return;
		}
		
		// READ Query 실행
		if ( "READ".equalsIgnoreCase(request.getSql().getType()) ) {
			
			try {
				Object rtn = null;
				
				if ( request.getSql().isSelectOne() ) 
					rtn = QueryExecutor.executeSelectOneReadQuery(runtimeInfo, qry, args);
				else
					rtn = QueryExecutor.executeReadQuery(runtimeInfo, qry, args);
				
				if (doReturn) {
					runtimeInfo.setResponseResult(new ResponseResult("", "", rtn));
				} else {
					String saveAs = request.getSql().getSaveAs();
					if( saveAs != null && "".equalsIgnoreCase(saveAs.trim()))
						runtimeInfo.getUserSavedObjs().put(saveAs, JsonPathUtil.getDocumentContext(rtn));
					else
						runtimeInfo.setSqlResults(JsonPathUtil.getDocumentContext(rtn));
				}
			} catch (JsonProcessingException | SQLException e) {
				LogUtil.write(e);
				runtimeInfo.setResponseResult(new ResponseResult(apiName, e.getMessage(), null));
			}
			
		} else if ( "WRITE".equalsIgnoreCase(request.getSql().getType()) ) { // WRITE Query 실행
			try {
				int rtn = QueryExecutor.executeWriteQuery(runtimeInfo, qry, args, false);
				runtimeInfo.setResponseResult(new ResponseResult("", "", rtn));
			} catch (SQLException e) {
				LogUtil.write(e);
				runtimeInfo.setResponseResult(new ResponseResult(apiName, e.getMessage(), null));
			}
		}
	}
	
	
	private Object processBuiltInFunction(RoutingContext rc, String builtInName, boolean isFormData, List<Object> args) throws BuiltInClassNotFoundException, BuiltInRuntimeException {
		Object obj = null;
		
		if ( isFormData ) {
			
			for ( FileUpload f : rc.fileUploads() ) {
				if( f.size() >= RunConfig.getFileUpload().getMaxSize() ) {
					throw new BuiltInRuntimeException(String.format("File '%s' exceed upload limit %d(bytes)", f.fileName(), RunConfig.getFileUpload().getMaxSize()));
				}
			}
			
			BuiltInHandlerAbstract hanlder = BuiltInHelper.getInstance().getBuiltInHandlerAbstract(builtInName);
			hanlder.setUploadFiles(rc.fileUploads());
			writeLog(builtInName, args);
			obj = hanlder.process(rc.request(), rc.response(), args);
		} else {
			BuiltInHandlerIF hanlder = BuiltInHelper.getInstance().getBuiltInHandlerIF(builtInName);
			writeLog(builtInName, args);
			obj = hanlder.process(rc.request(), rc.response(), args);
		}
		
		return obj;
		
	}
	
	
	private boolean isSystemBuiltIn(String funcName) {
		for( int i = 0; i < SYSTEM_BUILTINS.length; i++ ) {
			if ( SYSTEM_BUILTINS[i].equalsIgnoreCase(funcName) )
				return true;
		}
		
		return false;
	}
	
	
	
	
	
	
	/**
	 * 
	 * @return
	 */
	// 현재는 일단 default Handler를 통해서 처리하도록하고 향후 사용자 정의 handler가 있는지 확인 후 처리한다.
	private AuthHandlerIF getAuthHander() {
		return new DefaultAuthHandler();
		
	}
	
	private void sendResponse(RoutingContext rc, RuntimeInfo runtimeInfo) {
		
		ObjectMapper mapper = new ObjectMapper();
		String rtnJson = null;
		
		if ( runtimeInfo == null ) {
			rc.response()
		      .putHeader("content-type", "application/json")
		      .end("processing error.");
			return;
		}
		
		// 사용자가 직접 commit 하지 않은 경우는 시스템에서 판단한다. 
		// 판단 기준은 status가 공백열이 아닌 경우는 오류로 처리한다.
		if( !runtimeInfo.isMustCommit() ) {
			// 오류가 발생한 경우, status가 공백열이 아닌 경우는 오류이다.
			if ( runtimeInfo.getResponseResult().getStatus() != null && !"".equalsIgnoreCase(runtimeInfo.getResponseResult().getStatus()) ) {
				
				if( runtimeInfo.getDbConn() != null ) {
					try {
						runtimeInfo.getDbConn().rollback();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						try {
							runtimeInfo.getDbConn().close();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
						}
					}
				}
			} else {
				if( runtimeInfo.getDbConn() != null ) {
					try {
						runtimeInfo.getDbConn().commit();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						try {
							runtimeInfo.getDbConn().close();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
						}
					}
				}
			}
		} else {
			if( runtimeInfo.getDbConn() != null ) {
				try {
					runtimeInfo.getDbConn().commit();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						runtimeInfo.getDbConn().close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
					}
				}
			}
		}
			
		
		
		
		
		
		try {
			rtnJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(runtimeInfo.getResponseResult());
		} catch (JsonProcessingException e) {
			LogUtil.write(e);
		}
		
		rc.response()
	      .putHeader("content-type", "application/json")
	      .end(rtnJson);
	}
	
	
	private static void writeLog(String funcName, List<Object> params) {
		if( RunConfig.getLogging().getLogLevel() == 1) {
			LogUtil.write("Built-in Function : " + funcName);
			
			StringBuilder paramBuf = new StringBuilder("[");
			
			for( int i = 0; i < params.size(); i++ ) {
				paramBuf.append(params.get(i));
				
				if( i != params.size()-1)
					paramBuf.append(", ");
			}
			
			paramBuf.append("]");
			
			LogUtil.write(paramBuf.toString());
		}
	}
}
