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
import java.util.logging.Level;
import java.util.logging.Logger;

import main.java.fishtank.devices.DevicesCentral;
import main.java.fishtank.devices.WriteToJSONFile;
import main.java.fishtank.environment.Environment;

public class FishTank {
	
	private static final Logger LOGGER = Logger.getLogger(FishTank.class.getName());
	public static Environment env;

	public static void main(String[] args) {
		try {
			MyLogger.setup();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		WriteToJSONFile writer = new WriteToJSONFile(); // write default values to config file
		File configFile = new File("src/main/java/fishtank/main/configuration.json");
		writer.setDataFilePath(configFile.getAbsolutePath());
		
		if (!configFile.exists() && !configFile.isDirectory()) {
			LOGGER.info("First time creating the configuration file.");
			writer.writeToFile(new Environment());
		}
		
		FishTank.env = writer.getEnvironmentData();
		LOGGER.info("Environment object created: " + FishTank.env.toString());
		
		File devicesFile = new File("src/main/java/fishtank/main/devices.json");
		writer.setDataFilePath(devicesFile.getAbsolutePath());
		
		if (!devicesFile.exists() && !devicesFile.isDirectory()) {
			LOGGER.info("First time creating the devices file.");
			DevicesCentral devicesCentral = new DevicesCentral(FishTank.env, FishTank.env.getInterval());
			devicesCentral.createDevice(DevicesCentral.AIR_THERMOMETER, "1", "Air Thermometer", "Eclipse", "X");
			devicesCentral.createDevice(DevicesCentral.CLOCK, "2", "Clock", "Eclipse", "X");
			devicesCentral.createDevice(DevicesCentral.CO2_METER, "3", "CO2 Pro", "Google", "Pro1");
			devicesCentral.createDevice(DevicesCentral.OXYGEN_METER, "4", "Oxygen Pro", "Google", "Pro2");
			devicesCentral.createDevice(DevicesCentral.PH_METER, "5", "PH Measuring Pro", "Google", "ProX");
			devicesCentral.createDevice(DevicesCentral.WATER_THEMOMETER, "6", "Water Thermometer", "Eclipse", "XX");
			LOGGER.info(devicesCentral.toString());
			writer.writeToFile(devicesCentral);
		}
		
		DevicesCentral devicesCentral = writer.getDevicesData();	
		LOGGER.info("Devices created and started: " + devicesCentral.toString());
		
		MyScheduledExecutor executor = new MyScheduledExecutor(FishTank.env, devicesCentral);
		executor.schedule();		
		
		//TODO User input for how long to run the simulation?
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
		executor.shutdownExecutor();	
		devicesCentral.stopDevices();

	}

}
