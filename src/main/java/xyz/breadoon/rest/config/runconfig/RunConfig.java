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
package xyz.breadoon.rest.config.runconfig;



public class RunConfig {

    private static int port;
    private static boolean isDev;
    private static long autoReloadDuration = 5000;
    private static String version;
    private static DB db;
    private static Logging logging;
    private static CookieConfig cookieConfig;
    private static EnvironmentVariable environmentVariable;
    private static Cors[] cors;
    private static FileUploadConfig fileUpload;

    // 아래의 setter는 static을 사용하게 되면 yaml의 값이 정상적으로 셋팅이 되지 않으니 주의할 것!!

    public static int getPort() { return port; }
    public void setPort(int value) { port = value; }

    public static boolean getIsDev() { return isDev; }
    public void setIsDev(boolean value) { isDev = value; }

    public static String getVersion() { return version; }
    public void setVersion(String value) { version = value; }    

    public static DB getDB() { return db; }
    public void setDB(DB value) { db = value; }

    public static Logging getLogging() { return logging; }
    public void setLogging(Logging value) { logging = value; }

    public static CookieConfig getCookieConfig() { return cookieConfig; }
    public void setCookieConfig(CookieConfig value) { cookieConfig = value; }


    public static EnvironmentVariable getEnvironmentVariable() { return environmentVariable; }
    public void setEnvironmentVariable(EnvironmentVariable value) { environmentVariable = value; }
	
    public static long getAutoReloadDuration() {
		return autoReloadDuration;
	}
	
	public void setAutoReloadDuration(long value) {
		autoReloadDuration = value;
	}
	
	public static Cors[] getCors() {
		return cors;
	}
	public void setCors(Cors[] value) {
		cors = value;
	}
	
	public static FileUploadConfig getFileUpload() {
		return fileUpload;
	}
	public void setFileUpload(FileUploadConfig value) {
		fileUpload = value;
	}
    
	
	
    
}









