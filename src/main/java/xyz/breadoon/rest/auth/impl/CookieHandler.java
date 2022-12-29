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


import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import xyz.breadoon.rest.config.runconfig.CookieConfig;
import xyz.breadoon.rest.config.runconfig.RunConfig;
import xyz.breadoon.rest.util.CryptoDESedeEx;
import xyz.breadoon.rest.util.LogUtil;
import io.vertx.core.http.Cookie;

public class CookieHandler {

	private static final String encKey = "default_encrypt_key@breadoon.xyz";	// 32자리 
	private static CookieConfig cookieConfig = RunConfig.getCookieConfig();
	
	public boolean addAuth(HttpServerRequest req, HttpServerResponse resp, String cookieStr ) throws Exception {
		
		
		
		CryptoDESedeEx des = new CryptoDESedeEx();
		try {
			des.setKeyString(encKey);
			cookieStr = CryptoDESedeEx.BinaryToHexString(des.encrypt(cookieStr));
		} catch (Exception e) {
			LogUtil.write(e);
		}
		
		
		
		Cookie cookie = Cookie.cookie(cookieConfig.getName(), cookieStr);
		cookie.setDomain(cookieConfig.getDomain()); 
		cookie.setMaxAge(cookieConfig.getMaxAge()); 
		cookie.setPath("/"); 
		
		
		resp.addCookie(cookie);
		

		// TODO Auto-generated method stub
		return true;
	}

}
