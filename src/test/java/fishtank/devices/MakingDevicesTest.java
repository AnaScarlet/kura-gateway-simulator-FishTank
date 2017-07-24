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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import main.java.fishtank.devices.DevicesCentral;
import main.java.fishtank.environment.Environment;
import main.java.fishtank.main.MyScheduledExecutor;

public class MakingDevicesTest {

	private static final Logger LOGGER = Logger.getLogger(MakingDevicesTest.class.getName());

	@Test
	public void main() {
		Environment env = new Environment();
		env.setTimeSpeed(10);
		env.setSmallFishNum(4);

		DevicesCentral devicesCentral = new DevicesCentral(env, env.getInterval());
		
		devicesCentral.createDevice(DevicesCentral.AIR_THERMOMETER, "1", "Air Thermometer", "Eclipse", "X");
		devicesCentral.createDevice(DevicesCentral.CLOCK, "2", "Clock", "Eclipse", "X");
		devicesCentral.createDevice(DevicesCentral.CO2_METER, "3", "CO2 Pro", "Google", "Pro1");
		devicesCentral.createDevice(DevicesCentral.OXYGEN_METER, "4", "Oxygen Pro", "Google", "Pro2");
		devicesCentral.createDevice(DevicesCentral.PH_METER, "5", "PH Measuring Pro", "Google", "ProX");
		devicesCentral.createDevice(DevicesCentral.WATER_THEMOMETER, "6", "Water Thermometer", "Eclipse", "XX");
		
		LOGGER.info("Devices created and started.");
		
		MyScheduledExecutor executor = new MyScheduledExecutor(env, devicesCentral);
		executor.schedule();
		
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
		executor.shutdownExecutor();	
		devicesCentral.stopDevices();

	}
}
