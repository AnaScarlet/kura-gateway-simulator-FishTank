package test.java.fishtank.devices;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import main.java.fishtank.environment.Environment;

public class MakingDevicesTest {

	private static final Logger LOGGER = Logger.getLogger(MakingDevicesTest.class.getName());

	@Test
	public void main() {
		Environment env = new Environment();
		env.setTimeSpeed(10);
		env.setSmallFishNum(4);
		env.start();
		
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e1) {
			LOGGER.log(Level.SEVERE, e1.toString(), e1);
		}
		
		env.makeAirThermometerDevice("1", "Air Thermometer", "Eclipse", "X");
		env.makeClockDevice("2", "Clock", "Eclipse", "X");
		env.makeCO2MeterDevice("3", "CO2 Pro", "Google", "Pro1");
		env.makeOxygenMeterDevice("4", "Oxygen Pro", "Google", "Pro2");
		env.makePHMeterDevice("5", "PH Measuring Pro", "Google", "ProX");
		env.makeWaterThermometerDevice("6", "Water Thermometer", "Eclipse", "XX");
		LOGGER.info("Devices created and started.");
		
		try {
			Thread.sleep(60000);
			env.stopThreads();
		} catch (InterruptedException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
		
		Environment.AirThermometerDevice.setIsRunning(false);
		Environment.ClockDevice.setIsRunning(false);
		Environment.CO2MeterDevice.setIsRunning(false);
		Environment.OxygenMeterDevice.setIsRunning(false);
		Environment.PHMeterDevice.setIsRunning(false);
		Environment.WaterThermometerDevice.setIsRunning(false);
		
		Environment.AirThermometerDevice.writeToFile();
		Environment.ClockDevice.writeToFile();
		Environment.CO2MeterDevice.writeToFile();
		Environment.OxygenMeterDevice.writeToFile();
		Environment.PHMeterDevice.writeToFile();
		Environment.WaterThermometerDevice.writeToFile();
		
		env = null;
		System.gc();
	}
}
