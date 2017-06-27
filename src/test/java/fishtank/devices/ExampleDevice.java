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
package test.java.fishtank.devices;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.java.fishtank.devices.FishTankDevice;
import main.java.fishtank.devices.WriteToJSONFile;
import main.java.fishtank.environment.Environment;

public class ExampleDevice implements FishTankDevice{
	private static final Logger LOGGER = Logger.getLogger(Environment.class.getName());

	private boolean isRunning;
	private final String id = "ABC123";
	private final String name = "The Device";
	private final String manufacturer = "This is Us";
	private final String model = "X";
	private Object monitor = null;
	private ArrayList<Integer> data = new ArrayList<Integer>();
	
	public ExampleDevice(Object monitor) {
		this.createThread(this, "Example Device Thread");
		if (monitor != null)	
			this.monitor = monitor;
		else
			LOGGER.warning("Monitor object parameter was null.");
	}
 	
	public ExampleDevice() {
		this.createThread(this, "Example Device Thread");
		this.monitor = new Object();
	}
	
	public void run(){
		this.isRunning = true;
		LOGGER.info("In run method of ExampleDevice");
		for (int i = 0; i < 5; i++) {
			data.add(i);
			LOGGER.log(Level.FINE, "Data point: " + i , i);
		}
		this.isRunning = false;
		synchronized(monitor) {
			monitor.notifyAll();
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
	public Integer[] getData() {
		Integer[] a = {1};
		return (Integer[]) this.data.toArray(a);
	}
	@Override
	public String toString() {
		return "ID: " + this.getID() + " Name: " + this.getName() + " Manufacturer: " 
				+ this.getManufacturer() + " Model: " + this.getModel();
	}
}
