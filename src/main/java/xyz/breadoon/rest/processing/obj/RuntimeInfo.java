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
package xyz.breadoon.rest.processing.obj;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import com.jayway.jsonpath.DocumentContext;

import io.vertx.ext.web.RoutingContext;

public class RuntimeInfo {
	
	private Connection dbConn = null;
	
	
	private Map<String, String> pathParams = null;
	private HashMap<String, String> queryParams = null;
	private DocumentContext jsonContext = null;
	
	private HashMap<String, DocumentContext> builtInResults = null;
	private DocumentContext sqlResults = null;
	private HashMap<String, DocumentContext> userSavedObjs = null;

	private boolean isMustCommit = false;
	
	private RoutingContext routingContext = null;
	
	private ResponseResult responseResult = null;
	
	private boolean isJson = false;
	
	public Connection getDbConn() {
		return dbConn;
	}

	public void setDbConn(Connection dbConn) {
		this.dbConn = dbConn;
	}

	public Map<String, String> getPathParams() {
		return pathParams;
	}

	public void setPathParams(Map<String, String> pathParams) {
		this.pathParams = pathParams;
	}

	public HashMap<String, String> getQueryParams() {
		return queryParams;
	}

	public void setQueryParams(HashMap<String, String> queryParams) {
		this.queryParams = queryParams;
	}

	public DocumentContext getJsonContext() {
		return jsonContext;
	}

	public void setJsonContext(DocumentContext jsonContext) {
		this.jsonContext = jsonContext;
	}

	public HashMap<String, DocumentContext> getBuiltInResults() {
		
		if ( builtInResults == null )
			builtInResults = new HashMap<String, DocumentContext>();
		
		return builtInResults;
	}

	public void setBuiltInResults(HashMap<String, DocumentContext> builtInResults) {
		this.builtInResults = builtInResults;
	}

	public DocumentContext getSqlResults() {
		return sqlResults;
	}

	public void setSqlResults(DocumentContext sqlResults) {
		this.sqlResults = sqlResults;
	}

	public HashMap<String, DocumentContext> getUserSavedObjs() {
		
		if ( userSavedObjs == null )
			userSavedObjs = new HashMap<String, DocumentContext>();
		
		return userSavedObjs;
	}

	public void setUserSavedObjs(HashMap<String, DocumentContext> userSavedObjs) {
		this.userSavedObjs = userSavedObjs;
	}


	public RoutingContext getRoutingContext() {
		return routingContext;
	}

	public void setRoutingContext(RoutingContext routingContext) {
		this.routingContext = routingContext;
	}

	public boolean isMustCommit() {
		return isMustCommit;
	}

	public void setMustCommit(boolean isMustCommit) {
		this.isMustCommit = isMustCommit;
	}

	public ResponseResult getResponseResult() {
		return responseResult;
	}

	public void setResponseResult(ResponseResult responseResult) {
		this.responseResult = responseResult;
	}

	public boolean getIsJson() {
		return isJson;
	}

	public void setIsJson(boolean isJson) {
		this.isJson = isJson;
	}

	
	
	
}
