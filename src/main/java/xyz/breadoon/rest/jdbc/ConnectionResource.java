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

import java.sql.Connection;
import java.sql.DriverManager;


/**
 * @author  jsjung90
 */
public class ConnectionResource implements ConnectionContext {
   private Connection connection = null;
   private boolean transaction = false;



   public ConnectionResource() throws Exception {
      init(false); 
   }
    
    public ConnectionResource(boolean transaction) throws Exception {
       init(transaction);
    }
    
    public void init(boolean transaction) throws Exception {
       this.transaction = transaction;
       try {
    	   connection = DriverManager.getConnection("jdbc:apache:commons:dbcp:breadoon");
       } catch(Exception e) {
    	   e.printStackTrace();
       }
       
       if (connection == null) throw new Exception("fail to get connection");
    }
     
    /**
	 * @return
	 * @uml.property  name="connection"
	 */
    public Connection getConnection() {  
       return connection;
    }



    public void rollback() {
       if (transaction) {
          if (connection != null) try { connection.rollback(); } catch (Exception e) {}
      }
   }



   public void commit() {
      if (transaction) {
         if (connection != null) try { connection.commit(); } catch (Exception e) {}
      }
   }



   public void release() {
      if (connection != null) {
         if (transaction) {
            try { connection.setAutoCommit(true); } catch (Exception e) {}
         }
         try { connection.close(); } catch (Exception e) {}
      }
   }
}
