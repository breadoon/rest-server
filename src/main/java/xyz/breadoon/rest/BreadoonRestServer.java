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
package xyz.breadoon.rest;

import java.io.IOException;
import java.util.HashMap;

import xyz.breadoon.rest.config.builtInconfig.BuiltIn;
import xyz.breadoon.rest.config.builtInconfig.BuiltInConfig;
import xyz.breadoon.rest.config.builtInconfig.BuiltInConfigLoader;
import xyz.breadoon.rest.config.restconfig.RestConfigHolder;
import xyz.breadoon.rest.config.restconfig.RestConfigLoader;
import xyz.breadoon.rest.config.restconfig.RestRequest;
import xyz.breadoon.rest.config.runconfig.Cors;
import xyz.breadoon.rest.config.runconfig.RunConfig;
import xyz.breadoon.rest.config.runconfig.RunConfigLoader;
import xyz.breadoon.rest.handlers.RequestLogHandler;
import xyz.breadoon.rest.jdbc.ConnectionInitialize;
import xyz.breadoon.rest.mapping.RestApiMapper;
import xyz.breadoon.rest.processing.helper.BuiltInHelper;
import xyz.breadoon.rest.util.LogUtil;
import xyz.breadoon.rest.util.StringUtil;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.StaticHandler;



public class BreadoonRestServer {
	
	
	public static void main(String[] args) {
		BreadoonRestServer server = new BreadoonRestServer();
		server.init();
	}
	
	public void init() {
		
		//org.apache.ibatis.logging.LogFactory.useJdkLogging();

		// 운영 설정 정보를 얻는다.
		try {
			RunConfigLoader.load();
			
			LogUtil.write("[STEP #1] Load runconfig.");
			
		} catch (IOException e) {
			LogUtil.write(e);
			System.exit(1);
		}
		
		HashMap<String, HashMap<String, RestRequest>> restapiList = RestConfigLoader.load("config/restapi");
		
		// 다른 곳에서 읽어 들일 수 있도록 전역 클래스에 저장해 놓는다.
		RestConfigHolder.setRestApis(restapiList);
		
		LogUtil.write("[STEP #2] Load rest api information.");
		
		if ( restapiList.size() == 0 ) 
			LogUtil.write("Warning : Runnable rest api set does not exist.");
			
			
		try {
			ConnectionInitialize.init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogUtil.write("Warning : Can not initialize DB pool.");
		}
		
		LogUtil.write("[STEP #3] DB pool initialized");
		
		
		Vertx vertx = Vertx.vertx();
    	HttpServer httpServer = vertx.createHttpServer();
    	
    	
    	final Router router = Router.router(vertx);
    	
    	// body handler를 설정해 줘야 json body를 사용할 수 있다.
    	// 가장 먼저 선언이 되어야 한다.???
    	
    	if ( RunConfig.getFileUpload() != null && RunConfig.getFileUpload().getTempPath() != null && !"".equals(RunConfig.getFileUpload().getTempPath().trim()) )
    		router.route().handler(BodyHandler.create().setUploadsDirectory(RunConfig.getFileUpload().getTempPath().trim()));
    	else 
    		router.route().handler(BodyHandler.create());
    	
    	router.route().handler(StaticHandler.create().setWebRoot("webapps"));
    	
    	// CORS handling
    	router.route().handler(this::handlerCheckCorsHeaders);
    	router.route().method(HttpMethod.OPTIONS).handler(this::handlerOptionsMethod);
    	
    	// Request Log hanlder
    	LoggerFormat lf = LoggerFormat.DEFAULT;
    	if( RunConfig.getLogging().getAccessLogLevel() == 1 )
    		lf = LoggerFormat.SHORT;
    	else if( RunConfig.getLogging().getAccessLogLevel() == 2 )
    		lf = LoggerFormat.TINY;
    	
    	router.route().handler(RequestLogHandler.create(lf));
    	
    	
    	// 설정된 시간(초)에 한 번씩 변경 사항이 있는지 확인하여 반영한다.
    	if( RunConfig.getIsDev() ) {
	    	vertx.setPeriodic(RunConfig.getAutoReloadDuration(), x -> {
					final HashMap<String, HashMap<String, RestRequest>> apiList = RestConfigLoader.load("config/restapi");
					RestConfigHolder.getRestApis().putAll(apiList);
					if ( apiList.size() > 0 ) {
				    	// 최조 실행 정보를 매핑한다.
						RestApiMapper.reMapping(apiList);
					}
				
	    	});
    	}
    	
    	
    	// 최조 실행 정보를 매핑한다.
		RestApiMapper.mapping(vertx, router, restapiList, false);
		LogUtil.write("[STEP #4] Rest api mapping completed");
		
		// 최조 실행 정보를 매핑한다.
		try {
			BuiltInConfig builtInConfig = BuiltInConfigLoader.load();
			
			if ( builtInConfig != null && builtInConfig.getBuiltIns() != null ) {
				if ( builtInConfig.getBuiltIns().length > 0 ) {
					HashMap<String, String> builtInMap = new HashMap<String, String>();
					
					for( BuiltIn bi : builtInConfig.getBuiltIns() ) {
						builtInMap.put(bi.getName(), bi.getClassName());
					}
					
					// 사용자 정의 built-in을 시스템에 등록한다.
					BuiltInHelper.getInstance().addBuiltIns(builtInMap);
				}
			}
			
			
		} catch (IOException e) {
			LogUtil.write(e);
		}
		
		LogUtil.write("[STEP #5] Built-in function mapping completed");
		
		httpServer.requestHandler(router).listen(RunConfig.getPort() , res -> {
			if (res.succeeded()) {
				LogUtil.write("[STEP #6] Rest server ready on port[" + RunConfig.getPort() + "]");
			} else {
				LogUtil.write(String.format("Can't start at %d, maybe another process occupied this port.",RunConfig.getPort()));
				System.exit(1);
			}
		});
			
		
	
	}
	
	private void handlerCheckCorsHeaders(final RoutingContext ctx) {
	    
		final HttpServerResponse response = ctx.response();
	    final String origin = ctx.request().getHeader("Origin");
	    
	    if (origin != null) {
	    	if (isAllowedCors(origin)) {
	    		response.putHeader("Access-Control-Allow-Origin", origin);
	    		// Tell browser that response might change with origin          
	    		response.putHeader("Vary", "Origin");
	    	}
	    }
	    
	    ctx.next();
	    
	}
	
	private void handlerOptionsMethod(final RoutingContext ctx) {
		final HttpServerResponse response = ctx.response();
		final String origin = ctx.request().getHeader("Origin");
		
		if (origin != null) {
			if (isAllowedCors(origin)) {
				response.putHeader("Access-Control-Allow-Origin", origin);
				response.putHeader("Access-Control-Allow-Methods", "OPTIONS, GET, POST, PUT, PATCH, HEAD, DELETE");
				// FIXME: what header do we actually need
				response.putHeader("Access-Control-Allow-Headers", "Content-Type, Accept, Authorization");
				ctx.end("OPTIONS, GET, POST, PUT, PATCH, HEAD, DELETE");
				
			} else {
			    // Throw a 400 for people not welcome
			    response.setStatusCode(400).end("Your domain is not allowed");
			}
			
			
		} 
		
		 
	}
	
	/**
	  * @param origin - the Host where the request came from
	  * @return true if we serve it
	*/
	private boolean isAllowedCors(final String origin) {
		
		if( RunConfig.getCors() != null )
		for ( Cors each : RunConfig.getCors() ) {
			if ( each.getType() == 0 ) {	// exact matching
				if ( origin.equals(each.getDomain()))
					return true;
			} else if ( each.getType() == 1 ) {	// postfix matching
				if ( origin.endsWith(StringUtil.getOriginStringWithoutPort(each.getDomain())) )
					return true;
			}
		}
		return false;
	}
}