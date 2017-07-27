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
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import main.java.fishtank.environment.Environment;
import main.java.fishtank.main.GsonDeserializer;
import main.java.fishtank.main.GsonDeserializerDevices;
import main.java.fishtank.main.GsonSerializer;
import main.java.fishtank.main.GsonSerializerDevices;

public class WriteToJSONFile {
	
	private static final Logger LOGGER = Logger.getLogger(WriteToJSONFile.class.getName());
	private Gson gson;
	
	public String dataFilePath;
	
	public WriteToJSONFile() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Environment.class, new GsonSerializer());
		gsonBuilder.registerTypeAdapter(Environment.class, new GsonDeserializer());
		gsonBuilder.registerTypeAdapter(FishTankDevice.class, new GsonSerializerDevices());
		gsonBuilder.registerTypeAdapter(DevicesCentral.class, new GsonDeserializerDevices());
		gsonBuilder.setPrettyPrinting();
		this.gson= gsonBuilder.create();
		this.dataFilePath = new File("src/main/resources/jsonData.json").getAbsolutePath();
	}
	
	public WriteToJSONFile(final String filePath) {
		this.setDataFilePath(filePath);
	}

	public void writeToFile (final FishTankDevice obj) {
		final String gsonRepr = this.gson.toJson(obj);
		LOGGER.log(Level.INFO, "FishTankDevice object converted to JSON representation.", gsonRepr);
		this.writeData(gsonRepr);
	}
	
	public void writeToFile (final DevicesCentral obj) {
		final String gsonRepr = this.gson.toJson(obj);
		LOGGER.log(Level.INFO, "DevicesCentral object converted to JSON representation.", gsonRepr);
		this.writeData(gsonRepr);
	}
	
	public void writeToFile (final Environment obj) {
		String gsonRepr = this.gson.toJson(obj);
		LOGGER.log(Level.INFO, "Environment object converted to JSON representation.", gsonRepr);
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

	public Environment getEnvironmentData() {
		return this.gson.fromJson(this.getStringRepr()	, Environment.class);
	}
	
	public DevicesCentral getDevicesData() {
		return this.gson.fromJson(this.getStringRepr(), DevicesCentral.class);
	}
	
	private String getStringRepr() {
		String jsonStringRepr = "";
		try (Scanner scanner = new Scanner(new File(this.dataFilePath))) {
			while (scanner.hasNextLine()){
				jsonStringRepr += scanner.nextLine();
				LOGGER.fine(jsonStringRepr);
			} 
			scanner.close();
		} catch (FileNotFoundException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
		return jsonStringRepr;
	}
	
	public void setDataFilePath(final String filePath) {
		this.dataFilePath = filePath;
		LOGGER.log(Level.INFO, "Data file path set to: " + this.dataFilePath, this.dataFilePath);
	}

}
