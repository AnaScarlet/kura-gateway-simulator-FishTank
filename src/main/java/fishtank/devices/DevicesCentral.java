package main.java.fishtank.devices;

import java.util.ArrayList;

import main.java.fishtank.environment.Environment;

public class DevicesCentral {

	private ArrayList<FishTankDevice> devicesList;
	private int timeInterval;
	private Environment env;
	
	public static final int AIR_THEMOMETER = 1;
	public static final int CLOCK = 2;
	public static final int CO2_METER = 3;
	public static final int OXYGEN_METER = 4;
	public static final int PH_METER = 5;
	public static final int WATER_THEMOMETER = 6;
	
	public DevicesCentral(Environment env, int timeInterval) {
		this.env = env;
		this.timeInterval = timeInterval;
		this.devicesList = new ArrayList<FishTankDevice>();
	}
	
	public void createDevice(final int TYPE_OF_DEVICE, final String id, 
			final String name, final String manufacturer, final String model) {
		switch(TYPE_OF_DEVICE) {
		case AIR_THEMOMETER: 
			this.devicesList.add(new main.java.fishtank.devices.AirThermometer(id, name, manufacturer, model, this.env));
			break;
		case CLOCK: 
			this.devicesList.add(new main.java.fishtank.devices.Clock(id, name, manufacturer, model, this.env));
			break;
		case CO2_METER:
			this.devicesList.add(new main.java.fishtank.devices.CO2Meter(id, name, manufacturer, model, this.env));
			break;
		case OXYGEN_METER:
			this.devicesList.add(new main.java.fishtank.devices.OxygenMeter(id, name, manufacturer, model, this.env));
			break;
		case PH_METER:
			this.devicesList.add(new main.java.fishtank.devices.PHMeter(id, name, manufacturer, model, this.env));
			break;
		case WATER_THEMOMETER:
			this.devicesList.add(new main.java.fishtank.devices.WaterThermometer(id, name, manufacturer, model, this.env));
			break;
		}
	}
	
	public void runDevices() {
		for (FishTankDevice device : this.devicesList) {
			device.run();
		}
	}
	
	public void stopDevices() {
		for (FishTankDevice device : this.devicesList) {
			device.setIsRunning(false);
			device.writeToFile();
		}
	}
	
	public FishTankDevice[] getDeviceList() {
		return this.devicesList.toArray(new FishTankDevice[this.devicesList.size()]);
	}
	
	public void setDevicesList(FishTankDevice[] array) {
		for (FishTankDevice device : array) {
			this.devicesList.add(device);
		}
	}
	
	public int getTimeInterval(){
		return this.timeInterval;
	}
	
}
