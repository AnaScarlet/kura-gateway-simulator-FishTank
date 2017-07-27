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
package main.java.fishtank.main;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;

public class MyLogger {

	private static FileHandler handler;
	static private SimpleFormatter formatter;
	
	static public void setup() throws IOException {
		Logger logger = Logger.getLogger("");
		 
		logger.setLevel(Level.ALL);
		File file = new File("src/main/resources/Log.log");
		if (!file.exists()) {
			file.mkdirs();
		}
		handler = new FileHandler(file.getAbsolutePath());
		formatter = new SimpleFormatter();
		handler.setFormatter(formatter);
		logger.addHandler(handler);
	
	}

}
