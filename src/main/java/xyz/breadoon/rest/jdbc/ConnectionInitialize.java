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
package xyz.breadoon.rest.jdbc;

import java.sql.*;

import org.apache.commons.pool.impl.GenericObjectPool;

import xyz.breadoon.rest.config.runconfig.DB;
import xyz.breadoon.rest.config.runconfig.RunConfig;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;



public class ConnectionInitialize {
 
 public static void init() throws Exception {
 
	String driverClassName = null;
	String url = null;
	String username = null;
	String password = null;
	String validationQuery = null;
	boolean defaultAutoCommit = true;
	boolean defaultReadOnly = false;
	int maxActive = 0;
	int maxIdle = 0;
	long maxWait = 0;

	DB db = RunConfig.getDB();

    
	driverClassName = db.getDriverClassName();
    url = db.getUrl();
    username = db.getUser();
    password = db.getPassword();
//    defaultAutoCommit = "true".equals(System.getProperty("push.db.defaultAutoCommit", null));
//    defaultReadOnly = "true".equals(System.getProperty("push.db.defaultReadOnly", null));
    maxActive = db.getMaxActiveConns();
    maxIdle = db.getMaxIdleConns();
    maxWait = db.getMaxWaitMillis();
     
    validationQuery = db.getValidationQuery();
     
        
        try {

           // jdbc driver ??? ?????? ????????? ???????????????.
           setupDriver(driverClassName, 

                           url, 

                           username, 

                           password, 

                           defaultAutoCommit, 

                           defaultReadOnly, 

                           maxActive, 

                           maxIdle, 

                           maxWait,
                           
                           validationQuery);


           System.out.println("Connection initialize success");
        } catch (Exception exception) {
           System.out.println("Connection initialize fail!");
           exception.printStackTrace();
      }
 } 
 
 private static void setupDriver(String driverClassName, 
       String url, 
       String username,
       String password,
       boolean defaultAutoCommit,
       boolean defaultReadOnly,
       int maxActive,
       int maxIdle,
       long maxWait,
       String validationQuery) throws Exception { 


       try {

            // jdbc class??? ???????????????.
            Class.forName(driverClassName);
        } catch (ClassNotFoundException classnotfoundexception) {
            System.out.println(driverClassName+" is not found");
            classnotfoundexception.printStackTrace();
            throw classnotfoundexception;
        }
        

        // ????????? ?????? ????????? commons-collections??? genericOjbectPool??? ???????????????.
        GenericObjectPool connectionPool = new GenericObjectPool(null);
        connectionPool.setMaxActive(maxActive);
        connectionPool.setMaxIdle(maxIdle);
        connectionPool.setMaxWait(maxWait);
        connectionPool.setTestOnBorrow(true);
        
        

        // ?????? ???????????? ??????????????? ???????????? DriverManagerConnectionFactory??? ???????????????.
        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(url, username, password);
        

        // ConnectionFactory??? ?????? ???????????? PoolableConnectionFactory??? ????????????
        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, connectionPool, null, validationQuery, defaultReadOnly, defaultAutoCommit);
        

        // ??????????????? PoolingDriver ????????? ????????????
        Class.forName("org.apache.commons.dbcp.PoolingDriver");

        PoolingDriver driver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");


        // ????????? ?????? ????????????. ???????????? "breadoon"????????? ???????????????       
        driver.registerPool("breadoon",connectionPool);        
    }
}
