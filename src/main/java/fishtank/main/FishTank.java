package main.java.fishtank.main;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.java.fishtank.devices.DevicesCentral;
import main.java.fishtank.devices.WriteToJSONFile;
import main.java.fishtank.environment.Environment;
import resources.*;

public class FishTank {
	
	private static final Logger LOGGER = Logger.getLogger(FishTank.class.getName());

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
			writer.writeToFile(new Environment());
			LOGGER.info("First time creating the configuration file.");
		}
		
		Environment env = writer.getData();
		LOGGER.info("Environment object created: " + env.toString());
		
		DevicesCentral devicesCentral = new DevicesCentral(env, 12000);
		
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e1) {
			LOGGER.log(Level.SEVERE, e1.toString(), e1);
		}
		
		devicesCentral.createDevice(DevicesCentral.AIR_THEMOMETER, "1", "Air Thermometer", "Eclipse", "X");
		devicesCentral.createDevice(DevicesCentral.CLOCK, "2", "Clock", "Eclipse", "X");
		devicesCentral.createDevice(DevicesCentral.CO2_METER, "3", "CO2 Pro", "Google", "Pro1");
		devicesCentral.createDevice(DevicesCentral.OXYGEN_METER, "4", "Oxygen Pro", "Google", "Pro2");
		devicesCentral.createDevice(DevicesCentral.PH_METER, "5", "PH Measuring Pro", "Google", "ProX");
		devicesCentral.createDevice(DevicesCentral.WATER_THEMOMETER, "6", "Water Thermometer", "Eclipse", "XX");
		
		LOGGER.info("Devices created and started.");
		
		MyScheduledExecutor executor = new MyScheduledExecutor(env, devicesCentral);
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
