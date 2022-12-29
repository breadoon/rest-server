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
package xyz.breadoon.rest.auth.impl;

import java.util.Base64;

import xyz.breadoon.rest.auth.AuthHandlerIF;
import xyz.breadoon.rest.config.runconfig.CookieConfig;
import xyz.breadoon.rest.config.runconfig.RunConfig;
import xyz.breadoon.rest.util.CryptoDESedeEx;
import xyz.breadoon.rest.util.LogUtil;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.Cookie;

public class DefaultAuthHandler implements AuthHandlerIF {

	private static final String encKey = "default_encrypt_key@breadoon.xyz";
	private static CookieConfig cookieConfig = RunConfig.getCookieConfig();
	private static final String OPT_IS_WEB_ACCESS = "optIsWebAccess";
	
	@Override
	public boolean checkAuth(HttpServerRequest req, HttpServerResponse resp) throws Exception {
		
		CryptoDESedeEx des = new CryptoDESedeEx();
		des.setKeyString(encKey);
		String decAuthStr = null;
		String[] userInfos = null;
		
		if ( RunConfig.getIsDev() ) {
			
			String bearerAuthStr = req.getHeader("Authorization");
			
			if ( bearerAuthStr != null && bearerAuthStr.startsWith("Bearer ") ) {
				
				byte[] sesionInnfo = Base64.getDecoder().decode(bearerAuthStr.substring(7));
				decAuthStr = new String(des.decrypt(sesionInnfo));
				
				userInfos = decAuthStr.split("\\|");
				
				if ( userInfos[0].length() == 32 )
					return true;
				else
					return false;
			}
		}
		
		Cookie cookie = req.getCookie(cookieConfig.getName());
		Cookie optCookie = null;
		boolean isWebAccess = true;
		
		
		
		try {
			
			if( cookie != null ) {
				decAuthStr = new String(des.decrypt(CryptoDESedeEx.HexStringToBinary(cookie.getValue())));
				userInfos = decAuthStr.split("\\|");
				// 32자리 uuid를 사용할 경우
				if ( userInfos[0].length() == 32 ) {
				
					optCookie = req.getCookie(OPT_IS_WEB_ACCESS);
					isWebAccess = (optCookie != null && optCookie.getValue() != null) ? true : false;
					
					if ( isWebAccess ) {
						optCookie.setMaxAge(cookieConfig.getOptWebMaxAge()); 
						resp.addCookie(cookie.setMaxAge(cookieConfig.getMaxAge()));
						resp.addCookie(optCookie);
					}
					
					return true;
				}
			} else {
				return false;
			}
			
		} catch (Exception e) {
			LogUtil.write(e);
		}
		
		// TODO Auto-generated method stub
		return false;
	}
	
	public String getSessionId(HttpServerRequest req) throws Exception {
		
		CryptoDESedeEx des = new CryptoDESedeEx();
		des.setKeyString(encKey);
		String decAuthStr = null;
		String[] userInfos = null;
		
		if ( RunConfig.getIsDev() ) {
			
			String bearerAuthStr = req.getHeader("Authorization");
			
			if ( bearerAuthStr != null && bearerAuthStr.startsWith("Bearer ") ) {
				
				byte[] sesionInnfo = Base64.getDecoder().decode(bearerAuthStr.substring(7));
				decAuthStr = new String(des.decrypt(sesionInnfo));
				
				userInfos = decAuthStr.split("\\|");
				
				if ( userInfos[0].length() == 32 )
					return userInfos[0];
				else
					return null;
			}
		}
		
		
		
		Cookie cookie = req.getCookie(cookieConfig.getName(), cookieConfig.getDomain(), "/");
		try {
			
			decAuthStr = new String(des.decrypt(CryptoDESedeEx.HexStringToBinary(cookie.getValue())));
			userInfos = decAuthStr.split("\\|");
			// 32자리 uuid를 사용할 경우
			if ( userInfos[0].length() == 32 ) {
				return userInfos[0];
			}
		} catch (Exception e) {
		}
		
		// TODO Auto-generated method stub
		return null;
	}


	

}
