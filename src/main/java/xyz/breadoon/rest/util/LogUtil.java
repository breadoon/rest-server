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

import java.io.*;
import java.util.*;

import xyz.breadoon.rest.config.runconfig.RunConfig;

import java.text.SimpleDateFormat;



public class LogUtil {

///////////////////////////////////////////////////////////////////////////////
//USER DEFINE START
///////////////////////////////////////////////////////////////////////////////

			
	private static final String definedLogFileName = RunConfig.getLogging().getFilePrefix();
	private static final String definedLogPostFix  = ".log";
	
	private static boolean isLogFileOpened = false;
	
	private static final String logDir = RunConfig.getLogging().getFilePath();

///////////////////////////////////////////////////////////////////////////////
//	USER DEFINE END
//////////////////////////////////////////////////////////////////////////////	/
	
	
	private static String curDate = null;	

	
	
	private static String systemLogFilePath = null;

	private static PrintWriter logFile;		
	private static final SimpleDateFormat currentTimeFormatter 	= new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA);
	private static final SimpleDateFormat dateFormatter 		= new SimpleDateFormat("yyyyMMdd", Locale.KOREA);


	static{
		curDate = dateFormatter.format(new Date(System.currentTimeMillis()));
		
		
		boolean createOrNot = true;
		
		//System.out.println(filePath);
		File fLogDir = new File( logDir );
		System.out.println( "## LogUtil[static] :: start :: " + fLogDir.getAbsolutePath()  );
		
		if (!fLogDir.exists())		
			createOrNot = createDir( logDir + File.separator + definedLogFileName + "_" + curDate + definedLogPostFix);
		fLogDir = null;
		
		if ( !createOrNot ) 
			System.out.println("[Critical] Logfile Directory Creation Error");
		
		
		FileOutputStream fos = null;
		try {
			systemLogFilePath = getFilePath( logDir + File.separator + definedLogFileName + "_" + curDate + definedLogPostFix); 
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
			createOrNot = createDir( logDir + File.separator + definedLogFileName + "_" +  curDate + definedLogPostFix);
		
		if ( !createOrNot ) 
			throw new IOException("[Critical] Logfile Directory Creation Error");
		
		
		FileOutputStream fos = null;
		
		systemLogFilePath = getFilePath( logDir + File.separator + definedLogFileName + "_" +  curDate + definedLogPostFix); 
		fos = new FileOutputStream(systemLogFilePath, true);
		
		logFile =  new PrintWriter(new BufferedWriter(new OutputStreamWriter(fos)),true);
		
		write( "## LogUtil[open] :: start :: " + fLogDir.getAbsolutePath()  );
		
		isLogFileOpened = true;
	}//static
	
	
	public static boolean createDir(String filePath) {

		File file;
		String filePathTemp = getFilePath(filePath);
		

		file = new File(filePathTemp.substring(0, filePathTemp.lastIndexOf(File.separator)));

		
		return file.mkdirs();
			
	}//method
	
	public static String getFilePath(String pathName) {

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
	public synchronized static void close() throws IOException {
	
		// 기존에 로그 파일이 열려 있었다면 강제로 종료한다.
		if (isLogFileOpened == true) {
			isLogFileOpened = false;
			logFile.close();			
		}
	}
	
	
	public synchronized static void write(String message) {
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
								
			message = "[" + currentTimeFormatter.format( new Date( System.currentTimeMillis() ) ) + "] " + message;
			logFile.println( message );				
			logFile.flush();			
			System.out.println( message );
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
	
	
	public static synchronized void write( Exception e ){
		
		if(!isLogFileOpened) {
	        try {
	            open();
	        }
	        catch(IOException ioe) {
	            return;
	        }
	    }
		checkDate();
		
	    String message = "[" + currentTimeFormatter.format(new Date(System.currentTimeMillis())) + "] Exception thrown: ";
	    logFile.println( message );
	    e.printStackTrace( logFile );
	    logFile.flush();
	    System.out.println( message );
	    e.printStackTrace();
	}//method
	
	public static synchronized void write( String message, Exception ex ){
		if(!isLogFileOpened) {
	        try {
	            open();
	        }
	        catch(IOException ioe) {
	            return;
	        }
	    }
		checkDate();
		write( message );
		write( ex );
	}//method
}//class