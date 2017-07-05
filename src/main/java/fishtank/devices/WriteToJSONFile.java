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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import main.java.fishtank.environment.Environment;
import resources.GsonDeserializer;
import resources.GsonSerializer;

public class WriteToJSONFile {
	
	private static final Logger LOGGER = Logger.getLogger(Environment.class.getName());
	private Gson gson;
	
	public String dataFilePath;
	
	public WriteToJSONFile() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Environment.class, new GsonSerializer());
		gsonBuilder.registerTypeAdapter(Environment.class, new GsonDeserializer());
		gsonBuilder.setPrettyPrinting();
		this.gson= gsonBuilder.create();
		this.dataFilePath = new File("src/resources/jsonData.json").getAbsolutePath();
	}
	
	public WriteToJSONFile(final String filePath) {
		this.setDataFilePath(filePath);
	}

	public void writeToFile (final FishTankDevice obj) {
		final String gsonRepr = this.gson.toJson(obj);
		LOGGER.log(Level.INFO, "Object converted to JSON representation.", gsonRepr);
		this.writeData(gsonRepr);
	}
	
	public void writeToFile (final Environment obj) {
		String gsonRepr = this.gson.toJson(obj);
		LOGGER.log(Level.INFO, "Object converted to JSON representation.", gsonRepr);
		this.writeData(gsonRepr);
	}
	
	private void writeData(final String gsonRepr) {
		try {
			final FileWriter writer = new FileWriter(this.dataFilePath);
			writer.write(gsonRepr);
			LOGGER.log(Level.INFO, "Object written to file.");
			writer.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
	}

	public void setDataFilePath(final String filePath) {
		this.dataFilePath = filePath;
		LOGGER.log(Level.INFO, "Data file path set to: " + this.dataFilePath, this.dataFilePath);
	}

}
