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

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.java.fishtank.environment.Environment;

public class CO2Meter implements FishTankDevice {

	private static final Logger LOGGER = Logger.getLogger(CO2Meter.class.getName());
	
	private boolean isRunning;
	private final String id;
	private final String name;
	private final String manufacturer;
	private final String model;
	private final String type = "CO2Meter.class";
			
	private Environment env;
	private ArrayList<Float> airArray;
	
	
	public CO2Meter(final String id, final String name, final String manufacturer, final String model, 
			final Environment env){
		this.id = id;
		this.name = name;
		this.manufacturer = manufacturer;
		this.model = model;
		this.airArray = new ArrayList<Float>();
		
		this.env = env;
	}
	
	public void run() {
		this.isRunning = true;
		LOGGER.log(Level.FINE, "Update received. Uploading data...");
		final Float dataPoint = Float.valueOf(env.getDissolvedCO2());
		airArray.add(dataPoint);
	}

	public boolean writeToFile() {
		if (!this.isRunning) {
			WriteToJSONFile writer = new WriteToJSONFile();
			writer.setDataFilePath("src/resources/co2-meter-data.json");
			writer.writeToFile(this);
			return true;
		} return false;
	}
	
	public void setIsRunning(boolean value) {
		this.isRunning = value;
	}
	
	public boolean isRunning() {
		return this.isRunning;
	}

	public String getID() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getManufacturer() {
		return this.manufacturer;
	}

	public String getModel() {
		return this.model;
	}
	
	public String getType() {
		return this.type;
	}
	
	public String getDataArrayString() {
		String stringArray = "";
		for (Float element : this.airArray) {
			stringArray += element.toString() + ", ";
		}
		return stringArray;
	}

	@Override
	public String toString() {
		return "ID: " + this.getID() + " Name: " + this.getName() + " Manufacturer: " 
				+ this.getManufacturer() + " Model: " + this.getModel();
	}

}
