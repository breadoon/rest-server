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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import xyz.breadoon.rest.config.runconfig.RunConfig;
import xyz.breadoon.rest.jdbc.ConnectionResource;
import xyz.breadoon.rest.processing.obj.RuntimeInfo;
import xyz.breadoon.rest.util.JsonPathUtil;
import xyz.breadoon.rest.util.LogUtil;

public class QueryExecutor {

	private QueryExecutor() {
		
	}
	
	private static void checkDBConnection(RuntimeInfo runtimeInfo) throws SQLException {
		if( runtimeInfo.getDbConn() == null ) {
			
			try {
				Connection conn = (new ConnectionResource()).getConnection();
				conn.setAutoCommit(false);
				runtimeInfo.setDbConn(conn);
			} catch (Exception e) {
				throw new SQLException("Can't get DB Connection");
			}
		}
	}
	
	public static Map<String,Object> executeSelectOneReadQuery( RuntimeInfo runtimeInfo, String sql, List<Object> params ) throws SQLException {
		
		checkDBConnection(runtimeInfo);
		
		Connection conn = runtimeInfo.getDbConn();
		
		long start = System.currentTimeMillis();
		
		PreparedStatement pstmt = conn.prepareStatement(sql);
		
		for( int i = 0; i < params.size(); i++ ) {
			pstmt.setObject(i+1, params.get(i));
		}
		
		writeBeforeLog(sql, params);
		ResultSet rs = pstmt.executeQuery();
		
		HashMap<String,Object> resultHashMap = convertResultSetToSelectOne(rs);
		
		rs.close();
		pstmt.close();
		
		writeAfterLog(start);
		
		return resultHashMap;
		
	}
	
	public static List<HashMap<String,Object>> executeReadQuery( RuntimeInfo runtimeInfo, String sql, List<Object> params ) throws SQLException {
		
		checkDBConnection(runtimeInfo);
		
		Connection conn = runtimeInfo.getDbConn();
		
		long start = System.currentTimeMillis();
		
		PreparedStatement pstmt = conn.prepareStatement(sql);
		
		for( int i = 0; i < params.size(); i++ ) {
			pstmt.setObject(i+1, params.get(i));
		}
		
		writeBeforeLog(sql, params);
		ResultSet rs = pstmt.executeQuery();
		
		ArrayList<HashMap<String,Object>> resultHashMap = convertResultSetToArrayList(rs);
		
		rs.close();
		pstmt.close();
		
		writeAfterLog(start);
		
		return resultHashMap;
		
	}
	
	
	public static void executeNSaveReadQuery( RuntimeInfo runtimeInfo, String sql, List<Object> params ) throws SQLException, JsonProcessingException {
		
		checkDBConnection(runtimeInfo);
		
		Connection conn = runtimeInfo.getDbConn();
		
		long start = System.currentTimeMillis();
		
		PreparedStatement pstmt = conn.prepareStatement(sql);
		
		for( int i = 0; i < params.size(); i++ ) {
			pstmt.setObject(i+1, params.get(i));
		}
		
		writeBeforeLog(sql, params);
		ResultSet rs = pstmt.executeQuery();
		
		ArrayList<HashMap<String,Object>> resultHashMap = convertResultSetToArrayList(rs);
		
		rs.close();
		pstmt.close();
		
		writeAfterLog(start);
		
		runtimeInfo.setSqlResults(JsonPathUtil.getDocumentContext(resultHashMap));
		
	}
	
	
	
	
	public static int executeWriteQuery( RuntimeInfo runtimeInfo, String sql, List<Object> params, boolean mustCommit) throws SQLException {
		
		checkDBConnection(runtimeInfo);
		
		Connection conn = runtimeInfo.getDbConn();
		
		long start = System.currentTimeMillis();
		
		PreparedStatement pstmt = conn.prepareStatement(sql);
		
		for( int i = 0; i < params.size(); i++ ) {
			pstmt.setObject(i+1, params.get(i));
		}
		
		writeBeforeLog(sql, params);
		int updateCount = pstmt.executeUpdate();
		pstmt.close();
		
		if ( mustCommit ) conn.commit();
		
		writeAfterLog(start);
		
		return updateCount;
		
		
	}
	
	
	private static HashMap<String,Object> convertResultSetToSelectOne(ResultSet rs) throws SQLException {
	    ResultSetMetaData md = rs.getMetaData();
	    int columns = md.getColumnCount();
	    HashMap<String,Object> row = new HashMap<String,Object>();
	 
	    if(rs.next()) {
	        for(int i=1; i<=columns; ++i) {
	        	
	        	if ( md.getColumnLabel(i) == null || md.getColumnLabel(i).equalsIgnoreCase(""))
	        		row.put("__EMPTY_LABEL__", rs.getObject(i));
	        	else
	        		row.put(md.getColumnLabel(i), rs.getObject(i));
	        }
	        
	    }
	 
	    return row;
	}
	
	private static ArrayList<HashMap<String,Object>> convertResultSetToArrayList(ResultSet rs) throws SQLException {
	    ResultSetMetaData md = rs.getMetaData();
	    int columns = md.getColumnCount();
	    ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
	 
	    while(rs.next()) {
	        HashMap<String,Object> row = new HashMap<String, Object>(columns);
	        for(int i=1; i<=columns; ++i) {
	        	
	        	if ( md.getColumnLabel(i) == null || md.getColumnLabel(i).equalsIgnoreCase(""))
	        		row.put("__EMPTY_LABEL__", rs.getObject(i));
	        	else
	        		row.put(md.getColumnLabel(i), rs.getObject(i));
	        }
	        list.add(row);
	    }
	 
	    return list;
	}
	
	private static void writeBeforeLog(String sql, List<Object> params) {
		if( RunConfig.getLogging().getLogLevel() == 1) {
			LogUtil.write("[SQL] : " + sql);
			
			
			StringBuilder paramBuf = new StringBuilder("{");
			
			for( int i = 0; i < params.size(); i++ ) {
				paramBuf.append(params.get(i));
				
				if( i != params.size()-1)
					paramBuf.append(", ");
			}
			
			paramBuf.append("}");
			LogUtil.write("[PARAMETERS] : " + paramBuf.toString());
		}
	}
	
	private static void writeAfterLog(long start) {
		if( RunConfig.getLogging().getLogLevel() == 1) {
			LogUtil.write("[EXECUTION TIME] : " + (System.currentTimeMillis() - start) + " ms");
		}
	}


}
