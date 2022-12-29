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
package xyz.breadoon.rest.config.restconfig;

import java.util.HashMap;

public class NodeElement {

	public static final int NODE_STRING = 1;
	public static final int NODE_OBJECT = 2;
	public static final int NODE_INT = 3;
	public static final int NODE_FLOAT = 4;
	public static final int NODE_DOUBLE = 5;
	public static final int NODE_OBJECT_ARRAY = 6;
	public static final int NODE_LONG = 7; 
	
	private int type = 1;
	private String name = null;
	private boolean isRequired = false;
	private String defaultValue = null;
	
	private HashMap<String, NodeElement> children = new HashMap<String, NodeElement>();
	
	
	public NodeElement( ) {
	}
	
	public NodeElement( String name, int type, boolean isRequired ) {
		this.name = name;
		this.type = type;
		this.isRequired = isRequired;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isRequired() {
		return isRequired;
	}

	public void setRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public HashMap<String, NodeElement> getChildren() {
		return children;
	}

	public void setChildren(HashMap<String, NodeElement> children) {
		this.children = children;
	}
	
	
	
}
