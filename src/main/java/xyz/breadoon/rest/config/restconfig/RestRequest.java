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
/**
 * REST API에 대한 기본적인 설정 정보를 가지고 있는다. 
 */

package xyz.breadoon.rest.config.restconfig;

public class RestRequest {

	
	
	private String desc = null;
	private String pattern = null;
	private String method = null;
	private String bodyType = null;
	private String actionType = null;
	private boolean needAuth = false;
	private BuiltInJob builtIn = null;
	private SqlJob sql = null;
	private BlockComponent[] actionBlock = null;
	
	private String tag = null;
	
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getBodyType() {
		return bodyType;
	}
	public void setBodyType(String bodyType) {
		this.bodyType = bodyType;
	}
	

	public String getActionType() {
		return actionType;
	}
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	public boolean isNeedAuth() {
		return needAuth;
	}
	public void setNeedAuth(boolean needAuth) {
		this.needAuth = needAuth;
	}
	
	public BuiltInJob getBuiltIn() {
		return builtIn;
	}
	public void setBuiltIn(BuiltInJob builtIn) {
		this.builtIn = builtIn;
	}
	public SqlJob getSql() {
		return sql;
	}
	public void setSql(SqlJob sql) {
		this.sql = sql;
	}
	public BlockComponent[] getActionBlock() {
		return actionBlock;
	}
	public void setActionBlock(BlockComponent[] actionBlock) {
		this.actionBlock = actionBlock;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	
	
	
	
}
