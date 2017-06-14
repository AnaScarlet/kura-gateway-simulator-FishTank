package main.java.fishtank.devices;

import main.java.fishtank.environment.*;

import java.io.*;
import java.util.ArrayList;

public class Clock implements FishTankDevice {

	private boolean isRunning;
	private final String id;
	private final String name;
	private final String manufacturer;
	private final String model;
	public static final String errorLogFile = "../resources/ClockErrorLog.txt";
	
	private Environment env;
	private ArrayList<Integer> hoursArray;
	
	
	public Clock(final String id, final String name, final String manufacturer, final String model, final Environment env){
		this.id = id;
		this.name = name;
		this.manufacturer = manufacturer;
		this.model = model;
		try {
			System.setErr(new PrintStream(Clock.errorLogFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		this.env = env;
		this.createThread(this, "Clock Device Thread");
	}
	
	public void run() {
		hoursArray = new ArrayList<Integer>();
		synchronized (env) {
			while (true) {
				try {
					env.wait();
				} catch (InterruptedException e) {
					System.err.println(e);
				}
				System.out.println("Update received. Uploading data...");
				hoursArray.add(Integer.valueOf(env.getHour()));
				this.isRunning = true;
			}
		}
	}

	public boolean writeToFile() {
		if (!this.isRunning) {
			(new WriteToFile()).writeToFile(this);
			return true;
		} return false;
	}
	
	private void createThread(Runnable obj, String threadName) {
		Thread t = new Thread(obj, threadName);
		t.start();
		System.out.println(t + " started");
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

	public String getErrorLogFile() {
		return this.errorLogFile;
	}

	@Override
	public String toString() {
		return "ID: " + this.getID() + " Name: " + this.getName() + " Manufacturer: " 
				+ this.getManufacturer() + " Model: " + this.getModel();
	}
}
