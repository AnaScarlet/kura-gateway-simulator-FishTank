/*******************************************************************************
 * Copyright (c) 2017 Anastasiya Lazarenko
 *******************************************************************************/
package main.java.fishtank.devices;

public interface FishTankDevice {
	
	public void run();
	
	public boolean isRunning();
	
	public void setIsRunning(boolean value);
	
	public String getID(); // must have no spaces
	
	public String getName();
	
	public String getManufacturer();
	
	public String getModel();
	
	public String getType();
	
	public String getDataArrayString();
	
	@Override
	public String toString();

	public boolean writeToFile();
}
