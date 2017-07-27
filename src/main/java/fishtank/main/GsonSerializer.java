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

import com.google.gson.*;

import main.java.fishtank.environment.Environment;

public class GsonSerializer implements JsonSerializer<Environment> {
	
	private static final Logger LOGGER = Logger.getLogger(Environment.class.getName());

	@Override
	public JsonElement serialize(Environment src, Type typeOfSrc, JsonSerializationContext context) {
		LOGGER.info("Using Environement serializer");
		
		final JsonObject jsonObj = new JsonObject();
		
		jsonObj.addProperty("hour", src.getHour());
		jsonObj.addProperty("air temperature", src.getAirTemperature());
		jsonObj.addProperty("water temperature", src.getWaterTemperature());
		jsonObj.addProperty("time speed", src.getTimeSpeed());
		jsonObj.addProperty("dissolved CO2", src.getDissolvedCO2());
		jsonObj.addProperty("dissolved oxygen", src.getDissolvedOxygen());
		jsonObj.addProperty("pH", src.getPH());
		jsonObj.addProperty("plant number", src.getPlantNum());
		jsonObj.addProperty("decomposers number", src.getDecomposersNum());
		jsonObj.addProperty("small fish number", src.getSmallFishNum());
		jsonObj.addProperty("medium fish number", src.getMediumFishNum());
		jsonObj.addProperty("large fish number", src.getLargeFishNum());
		
		return jsonObj;
	}

}
