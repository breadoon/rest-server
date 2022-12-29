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
package xyz.breadoon.rest.mapping;

import java.util.HashMap;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import xyz.breadoon.rest.config.restconfig.RestConfigHolder;
import xyz.breadoon.rest.config.restconfig.RestRequest;
import xyz.breadoon.rest.processing.RestRequestProcessor;
import xyz.breadoon.rest.util.StringUtil;

public class RestApiMapper {

	private static Router savedRouter = null;
	private static Vertx savedVertx = null;
	
	
	public static void mapping(Vertx vertx, Router router, HashMap<String, HashMap<String, RestRequest>> restApis, boolean isReload) {
		
		savedRouter = router;
		savedVertx = vertx;
		
		String pathPattern = null;
		
		HashMap<String, RestRequest> versionRestApis = null;
		
		for( String version : restApis.keySet() ) {
		
			versionRestApis = restApis.get(version);
		
			if ( isReload ) {
				// 기존에 등록되어 있는 해당 버전의 모든 route 정보를 제거한다.
				
				router.getRoutes().stream()
		        .filter(route -> route.getPath() != null && route.getPath().startsWith("/"+version))
		        .forEach(route -> route.remove());
			}
			
			for( String key : versionRestApis.keySet() ) {
				
				// 이런 방식으로 final에 가까운 scope로 처리되어야만 lambda 함수에 값을 넣을 수 있다.
				final RestRequest api = versionRestApis.get(key);

				if ( !"INLINE".equalsIgnoreCase(api.getMethod()) && api.getPattern() != null )
				{
					pathPattern = StringUtil.curlyBraceVarToVertxVar(api.getPattern());				
					router.route("/"+version + pathPattern).method(HttpMethod.valueOf(api.getMethod().toUpperCase())).handler(rc -> {
				      	  new RestRequestProcessor().process(vertx, rc, version, key);
				      	});
				}
				
			}
		}
			
	}
	
	public static synchronized void reMapping(HashMap<String, HashMap<String, RestRequest>> restApis) {
		
		String pathPattern = null;
		
		HashMap<String, RestRequest> versionRestApis = null;
		
		for( String version : restApis.keySet() ) {
		
			versionRestApis = restApis.get(version);
		
			// 기존에 등록되어 있는 해당 버전의 모든 route 정보를 제거한다.
				
			savedRouter.getRoutes().stream()
	        .filter(route -> route.getPath() != null && route.getPath().startsWith("/"+version))
	        .forEach(route -> route.remove());
			
			Route route = null;
			for( String key : versionRestApis.keySet() ) {
				
				// 이런 방식으로 final에 가까운 scope로 처리되어야만 lambda 함수에 값을 넣을 수 있다.
				final RestRequest api = versionRestApis.get(key);
				if ( !"INLINE".equalsIgnoreCase(api.getMethod()) && api.getPattern() != null )
				{
					pathPattern = StringUtil.curlyBraceVarToVertxVar(api.getPattern());				
					route = savedRouter.route("/"+version + pathPattern).method(HttpMethod.valueOf(api.getMethod().toUpperCase()));
					
					route.handler(rc -> {
				      	  	new RestRequestProcessor().process(savedVertx, rc, version, key);
				      	});
				}
					
			}
			
		}
			
	}
	
	public static synchronized void addSingleApi(String version, String key, RestRequest api) {
		
		HashMap<String, RestRequest> apiMap = new HashMap<String, RestRequest>();
		apiMap.put(key, api);
		
		RestConfigHolder.addRestApi(version, apiMap);
		
		String pathPattern = null;
		
		pathPattern = StringUtil.curlyBraceVarToVertxVar(api.getPattern());
		
		// body handler를 추가해야 json body를 사용할 수 있다.
		Route route = savedRouter.route("/"+version + pathPattern).method(HttpMethod.valueOf(api.getMethod().toUpperCase()));
		
		route.handler(rc -> {
	      		new RestRequestProcessor().process(savedVertx, rc, version, key);
	      	});
			
	}
}
