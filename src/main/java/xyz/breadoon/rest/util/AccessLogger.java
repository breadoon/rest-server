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
package xyz.breadoon.rest.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpVersion;
import io.vertx.core.net.SocketAddress;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.LoggerFormat;
import xyz.breadoon.rest.config.runconfig.RunConfig;
import xyz.breadoon.rest.handlers.RequestLogHandler;

public class AccessLogger implements RequestLogHandler {
	private final boolean immediate;
	private final LoggerFormat format;
	private final DateFormat dateTimeFormat;
	
	private static final String definedLogFileName = "access_";
	private static final String definedLogPostFix  = ".log";
	
	private static boolean isLogFileOpened = false;
	
	private static final String logDir = RunConfig.getLogging().getFilePath();
	
	///////////////////////////////////////////////////////////////////////////////
	//	USER DEFINE END
	//////////////////////////////////////////////////////////////////////////////	/
	
	
	private static String curDate = null;	

	
	
	private static String systemLogFilePath = null;

	private static PrintWriter logFile;		
	private static final SimpleDateFormat dateFormatter 		= new SimpleDateFormat("yyyyMMdd", Locale.KOREA);


	static{
		curDate = dateFormatter.format(new Date(System.currentTimeMillis()));
		
		
		boolean createOrNot = true;
		
		//System.out.println(filePath);
		File fLogDir = new File( logDir );
		System.out.println( "## AccessLogger[static] :: start :: " + fLogDir.getAbsolutePath()  );
		
		if (!fLogDir.exists())		
			createOrNot = createDir( logDir + File.separator + definedLogFileName + curDate + definedLogPostFix);
		fLogDir = null;
		
		if ( !createOrNot ) 
			System.out.println("[Critical] Logfile Directory Creation Error");
		
		
		FileOutputStream fos = null;
		try {
			systemLogFilePath = getFilePath( logDir + File.separator + definedLogFileName + curDate + definedLogPostFix); 
			fos = new FileOutputStream(systemLogFilePath, true);
			isLogFileOpened = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}//try
		
		
		logFile =  new PrintWriter(new BufferedWriter(new OutputStreamWriter(fos)),true);
	}//static
	
	
	
	private static void open() throws IOException {
		curDate = dateFormatter.format(new Date(System.currentTimeMillis()));
		
		boolean createOrNot = true;
		
		File fLogDir = new File( logDir );
		
		if (!fLogDir.exists())		
			createOrNot = createDir( logDir + File.separator + definedLogFileName + curDate + definedLogPostFix);
		
		if ( !createOrNot ) 
			throw new IOException("[Critical] Logfile Directory Creation Error : " + logDir);
		
		
		FileOutputStream fos = null;
		
		systemLogFilePath = getFilePath( logDir + File.separator + definedLogFileName + curDate + definedLogPostFix); 
		fos = new FileOutputStream(systemLogFilePath, true);
		
		logFile =  new PrintWriter(new BufferedWriter(new OutputStreamWriter(fos)),true);
		write( "## AccessLogger[open] :: start :: " + fLogDir.getAbsolutePath()  );
		
		isLogFileOpened = true;
	}//static
	
	
	private static boolean createDir(String filePath) {

		File file;
		String filePathTemp = getFilePath(filePath);
		

		file = new File(filePathTemp.substring(0, filePathTemp.lastIndexOf(File.separator)));

		
		return file.mkdirs();
			
	}//method
	
	private static String getFilePath(String pathName) {

		try {
			return new File(pathName).getPath();
		}
		catch(NullPointerException e) {
			return null;
		}
	}//method

	
	/**---------------------------------
		오픈된 로그 화일을 닫는다.
	----------------------------------*/
	private synchronized static void close() throws IOException {
	
		// 기존에 로그 파일이 열려 있었다면 강제로 종료한다.
		if (isLogFileOpened == true) {
			isLogFileOpened = false;
			logFile.close();			
		}
	}
	
	
	private synchronized static void write(String message) {
		if (!isLogFileOpened) {
			try {
				open();
			}
			catch (IOException e) {
				return;
			}
		}
		try {
			// 로그를 기록하기전에 날짜가 변경되었는지 확인하고 새로운 파일 생성 여부를 결정한다.			
			checkDate();
								
			logFile.println( message );				
			logFile.flush();			
		}
		catch (Exception e) {
		}
	
	}//method
	
	
	private static void checkDate() {
	    try {            
	        if(!curDate.equals(dateFormatter.format(new Date(System.currentTimeMillis())))) {
	            logFile.println("Log file continued with new date...");
	            close();
	            open();
	        }
	    }
	    catch(Exception exception) { 
	    }
	}
	
	

	public AccessLogger(LoggerFormat format, boolean immediate) {
		this.format = format;
		this.immediate = immediate;
		
		this.dateTimeFormat = DateFormat.getDateTimeInstance();
	}


	private String getClientAddress(SocketAddress inetSocketAddress) {
		return inetSocketAddress == null ? null : inetSocketAddress.host();
	}

	private void log(RoutingContext context, long timestamp, String remoteClient, HttpVersion version, HttpMethod method, String uri) {
		HttpServerRequest request = context.request();
	    long contentLength = 0L;
	    String versionFormatted;
	   
	    if (this.immediate) {
	      versionFormatted = request.headers().get("content-length");
	      if (versionFormatted != null) {
	        try {
	          contentLength = Long.parseLong(versionFormatted.toString());
	        } catch (NumberFormatException var17) {
	          contentLength = 0L;
	        }
	      }
	    } else {
	      contentLength = request.response().bytesWritten();
	    }
	    

	    versionFormatted = "-";
	    switch (version) {
	      case HTTP_1_0:
	        versionFormatted = "HTTP/1.0";
	        break;
	      case HTTP_1_1:
	        versionFormatted = "HTTP/1.1";
	        break;
	      case HTTP_2:
	        versionFormatted = "HTTP/2.0";
	        break;
	      default:
	        versionFormatted = "-";
	        break;
	    }

	    MultiMap headers = request.headers();
	    int status = request.response().getStatusCode();
	    String message = null;
	    switch (this.format) {
	      case DEFAULT:
	        String referrer = headers.contains("referrer") ? headers.get("referrer") : headers.get("referer");
	        String userAgent = headers.get("user-agent");
	        String body = context.getBodyAsString().replaceAll("(\\r|\\n)", "");
	        referrer = referrer == null ? "-" : referrer;
	        userAgent = userAgent == null ? "-" : userAgent;
	        message = String.format("[%s] %s - - \"%s %s %s\" %d %d \"%s\" \"%s\" %dms %s",
	            this.dateTimeFormat.format(new Date(timestamp)),
	            remoteClient,
	            method,
	            uri,
	            versionFormatted,
	            status,
	            contentLength,
	            referrer,
	            userAgent,
	            System.currentTimeMillis() - timestamp,
	            body);
	        break;
	      case SHORT:
	        message = String.format("[%s] %s - - \"%s %s %s\" %d %d \"%s\" \"%s\" %dms",
	            this.dateTimeFormat.format(new Date(timestamp)),
	            remoteClient,
	            method,
	            uri,
	            versionFormatted,
	            status,
	            contentLength,
	            headers.contains("referrer") ? headers.get("referrer") : headers.get("referer"),
	            headers.get("user-agent"),
	            System.currentTimeMillis() - timestamp);
	        break;
	      case TINY:
	        message = String.format("%s - %s %s %d %d",
	            remoteClient,
	            method,
	            uri,
	            status,
	            contentLength);
	        break;
	      default:
	        message = String.format("%s - %s %s %d %d",
	            remoteClient,
	            method,
	            uri,
	            status,
	            contentLength);
	        break;
	    }
	    
	    write(message);

	  }


	  /**
	   * Core logic for the handler.
	   *
	   * @param context     {@link RoutingContext} routing context
	   */
	  public void handle(RoutingContext context) {
	    long timestamp = System.currentTimeMillis();
	    String remoteClient = this.getClientAddress(context.request().remoteAddress());
	    HttpMethod method = context.request().method();
	    String uri = context.request().uri(); 
	    HttpVersion version = context.request().version();
	    if (this.immediate) {
	      this.log(context, timestamp, remoteClient, version, method, uri);
	    } else {
	      context.addBodyEndHandler((handler) -> {
	        this.log(context, timestamp, remoteClient, version, method, uri);
	      });
	    }

	    context.next();
	  }
	

}
