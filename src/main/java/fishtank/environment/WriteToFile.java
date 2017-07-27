/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package main.java.fishtank.environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WriteToFile {
	
	private static final Logger LOGGER = Logger.getLogger(WriteToFile.class.getName());
	
	private File file;
	private FileWriter writer;
	
	public WriteToFile(String fileName) {
		this.file = new File("src/main/resources/" + fileName);
		try {
			this.writer = new FileWriter(file);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
	}

	public void writeToFile(String input) {
		try {
			writer.write(input);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
	}
	
	public void done() {
		try {
			LOGGER.info("Environment writer closing down.");
			writer.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
	}
	
	public String getAbsoluteFilePath(){
		return file.getAbsolutePath();
	}
}
