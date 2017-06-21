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

public class WriteToFile {
	
	private File file;
	private FileWriter writer;
	
	public WriteToFile(String fileName) {
		this.file = new File("src/resources/" + fileName);
		try {
			this.writer = new FileWriter(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeToFile(String input) {
		try {
			writer.write(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void done() {
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getAbsoluteFilePath(){
		return file.getAbsolutePath();
	}
}
