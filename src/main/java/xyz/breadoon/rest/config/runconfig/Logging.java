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

public class Logging {
	private String filePath;
    private String filePrefix;
    private int logLevel = 1;
    private int accessLogLevel = 0;

    public String getFilePath() { return filePath; }
    public void setFilePath(String value) { this.filePath = value; }

    public String getFilePrefix() { return filePrefix; }
    public void setFilePrefix(String value) { this.filePrefix = value; }

    public int getLogLevel() { return logLevel; }
    public void setLogLevel(int value) { this.logLevel = value; }
    
	public int getAccessLogLevel() { return accessLogLevel; }
	public void setAccessLogLevel(int value) { this.accessLogLevel = value; }
    
    
}
