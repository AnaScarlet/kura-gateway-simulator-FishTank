package main.java.fishtank.main;

import java.io.File;
import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;

import main.java.fishtank.devices.WriteToJSONFile;
import main.java.fishtank.environment.Environment;
import resources.*;

public class FishTank {
	
	private static final Logger LOGGER = Logger.getLogger(Environment.class.getName());

	public static void main(String[] args) {
		try {
			MyLogger.setup();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		WriteToJSONFile writer = new WriteToJSONFile(); // write default values to config file
		String path = new File("src/main/java/fishtank/main/configuration.json").getAbsolutePath();
		LOGGER.log(Level.INFO, path);
		writer.setDataFilePath(path);
		writer.writeToFile(new Environment());
	}

}
