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

public class SqlJob {

	
	
	private String type = null;
	private String query = null;
	private String saveAs = null;
	private String[] params = null;
	private boolean useMybatis = false;
	private boolean mustCommit = false;
	private boolean selectOne = false;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getSaveAs() {
		return saveAs;
	}
	public void setSaveAs(String saveAs) {
		this.saveAs = saveAs;
	}
	public String[] getParams() {
		return params;
	}
	public void setParams(String[] params) {
		this.params = params;
	}
	
	public boolean isUseMybatis() {
		return useMybatis;
	}
	
	public void setUseMybatis(boolean useMybatis) {
		this.useMybatis = useMybatis;
	}
	public boolean isMustCommit() {
		return mustCommit;
	}
	public void setMustCommit(boolean mustCommit) {
		this.mustCommit = mustCommit;
	}
	public boolean isSelectOne() {
		return selectOne;
	}
	public void setSelectOne(boolean selectOne) {
		this.selectOne = selectOne;
	}
	
	
	
}
