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
package main.java.fishtank.devices;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

import main.java.fishtank.environment.Environment;

public class WriteToJSONFile {
	
	private static final Logger LOGGER = Logger.getLogger(Environment.class.getName());
	
	public static final String dataFilePath = "C:/Users/Owner/git/kura-gateway-simulator-FishTank/src/resources/jsonData.json";

	public void writeToFile (FishTankDevice obj) {
		Gson gson = new Gson();
		String gsonRepr = gson.toJson(obj);
		LOGGER.log(Level.INFO, "Object converted to JSON representation.", gsonRepr);

		try {
			FileWriter writer = new FileWriter(WriteToJSONFile.dataFilePath);
			writer.write(gsonRepr);
			LOGGER.log(Level.INFO, "Object written to file.");
			writer.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
	}

}
