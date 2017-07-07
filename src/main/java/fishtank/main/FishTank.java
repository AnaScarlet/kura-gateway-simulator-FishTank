package main.java.fishtank.main;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
		env.start();
		//TODO User input for how long to run the simulation?
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
	}

}
