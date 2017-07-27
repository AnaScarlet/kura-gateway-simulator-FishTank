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
package main.java.fishtank.main;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import main.java.fishtank.devices.DevicesCentral;
import main.java.fishtank.environment.Environment;
import main.java.fishtank.main.FishTank;

public class GsonDeserializerDevices implements JsonDeserializer<DevicesCentral> {

	private static final Logger LOGGER = Logger.getLogger(GsonDeserializerDevices.class.getName());

	@Override
	public DevicesCentral deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		LOGGER.info("Using FishTankDevice deserializer");
		
		final JsonObject jsonObject = json.getAsJsonObject();
		
		final int timeInterval = jsonObject.get("timeInterval").getAsInt();
		DevicesCentral devicesCentral = new DevicesCentral(FishTank.env, timeInterval);
		
		final JsonArray devicesList = jsonObject.get("devicesList").getAsJsonArray();
		for (JsonElement device : devicesList) {
			final String id = device.getAsJsonObject().get("id").getAsString();
			final String name = device.getAsJsonObject().get("name").getAsString();
			final String manufacturer = device.getAsJsonObject().get("manufacturer").getAsString();
			final String model = device.getAsJsonObject().get("model").getAsString();
			final String type = device.getAsJsonObject().get("type").getAsString();
			switch(type) {
			case "AirThermometer.class":
				devicesCentral.createDevice(DevicesCentral.AIR_THERMOMETER, id, name, manufacturer, model);
				break;
			case "Clock.class":
				devicesCentral.createDevice(DevicesCentral.CLOCK, id, name, manufacturer, model);
				break;
			case "CO2Meter.class":
				devicesCentral.createDevice(DevicesCentral.CO2_METER, id, name, manufacturer, model);
				break;
			case "OxygenMeter.class":
				devicesCentral.createDevice(DevicesCentral.OXYGEN_METER, id, name, manufacturer, model);
				break;
			case "PHMeter.class":
				devicesCentral.createDevice(DevicesCentral.PH_METER, id, name, manufacturer, model);
				break;
			case "WaterThermometer.class":
				devicesCentral.createDevice(DevicesCentral.WATER_THEMOMETER, id, name, manufacturer, model);
				break;
			} 
		}
		
		return devicesCentral;
	}

}
