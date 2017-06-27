package main.java.fishtank.devices;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.java.fishtank.environment.Environment;

public class PHMeter implements FishTankDevice {

	private static final Logger LOGGER = Logger.getLogger(Environment.class.getName());
	
	private boolean isRunning;
	private final String id;
	private final String name;
	private final String manufacturer;
	private final String model;
	
	private Environment env;
	private ArrayList<Integer> phArray;
	
	
	public PHMeter(final String id, final String name, final String manufacturer, final String model, final Environment env){
		this.id = id;
		this.name = name;
		this.manufacturer = manufacturer;
		this.model = model;
		this.phArray = new ArrayList<Integer>();
		
		this.env = env;
		this.createThread(this, "PH Meter Device Thread");
	}
	
	public void run() {
		this.isRunning = true;
		synchronized (env) {
			while (true) {
				try {
					env.wait();
				} catch (InterruptedException e) {
					LOGGER.log(Level.SEVERE, e.toString(), e);
				}
				LOGGER.log(Level.INFO, "Update received. Uploading data...");
				phArray.add(Integer.valueOf(env.getHour()));
			}
		}
	}

	public boolean writeToFile() {
		if (!this.isRunning) {
			(new WriteToJSONFile()).writeToFile(this);
			return true;
		} return false;
	}
	
	private void createThread(Runnable obj, String threadName) {
		Thread t = new Thread(obj, threadName);
		t.start();
		LOGGER.log(Level.INFO, t.getName() + " started", t);
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

	@Override
	public String toString() {
		return "ID: " + this.getID() + " Name: " + this.getName() + " Manufacturer: " 
				+ this.getManufacturer() + " Model: " + this.getModel();
	}
}
