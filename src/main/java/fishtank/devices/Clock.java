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

import main.java.fishtank.environment.*;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Clock implements FishTankDevice {

	private static final Logger LOGGER = Logger.getLogger(Environment.class.getName());
	
	private boolean isRunning;
	private final String id;
	private final String name;
	private final String manufacturer;
	private final String model;
	private Object monitor;
	
	private Environment env;
	private ArrayList<Integer> hoursArray;
	
	
	public Clock(final String id, final String name, final String manufacturer, final String model, 
			final Environment env, final Object monitor){
		this.id = id;
		this.name = name;
		this.manufacturer = manufacturer;
		this.model = model;
		this.hoursArray = new ArrayList<Integer>();
		this.monitor = monitor;
		
		this.env = env;
		this.createThread(this, "Clock Device Thread");
	}
	
	public void run() {
		this.isRunning = true;
		synchronized (this.monitor) {
			while (true) {
				try {
					this.monitor.wait();
				} catch (InterruptedException e) {
					LOGGER.log(Level.SEVERE, e.toString(), e);
				}
				LOGGER.log(Level.INFO, "Update received. Uploading data...");
				hoursArray.add(Integer.valueOf(env.getHour()));
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
