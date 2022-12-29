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
package xyz.breadoon.rest.processing.builtin.impl;

import java.util.List;


import xyz.breadoon.rest.processing.builtin.BuiltInHandlerAbstract;
import xyz.breadoon.rest.processing.builtin.BuiltInRuntimeException;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.FileUpload;

public class FileUploadTester extends BuiltInHandlerAbstract {

	
	@Override
	public Object process(HttpServerRequest req, HttpServerResponse resp, List<Object> args)
	throws BuiltInRuntimeException {
		
		try {
			System.out.println(String.format("call FileUploadTester... : %d", args.size()) );
			
			for( Object param : args ) {
				System.out.println(String.format("param : %d", param));
			}
			
			if ( uploadFiles != null ) {
				for (FileUpload f : uploadFiles) {
			        System.out.println("Filename: " + f.fileName());
			        System.out.println("Size: " + f.size());
			      }
			}
		} catch(Exception e) {
			throw new BuiltInRuntimeException(e.getMessage());
		}
			
		
		
		return "";
	}

}
