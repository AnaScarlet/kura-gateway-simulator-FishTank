/*******************************************************************************
 * Copyright (c) 2017 Anastasiya Lazarenko
 *******************************************************************************/
package main.java.fishtank.devices;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.java.fishtank.environment.Environment;

public class OxygenMeter implements FishTankDevice {
	
	private static final Logger LOGGER = Logger.getLogger(OxygenMeter.class.getName());
	
	private boolean isRunning;
	private final String id;
	private final String name;
	private final String manufacturer;
	private final String model;
	private final String type = "OxygenMeter.class";
	
	private Environment env;
	private ArrayList<Float> airArray;
	
	
	public OxygenMeter(final String id, final String name, final String manufacturer, final String model, 
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
		final Float dataPoint = Float.valueOf(env.getDissolvedOxygen());
		airArray.add(dataPoint);
	}

	public boolean writeToFile() {
		if (!this.isRunning) {
			WriteToJSONFile writer = new WriteToJSONFile();
			writer.setDataFilePath("src/main/resources/oxygen-meter-" + this.id + "-data.json");
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
