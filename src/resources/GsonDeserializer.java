package resources;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import main.java.fishtank.environment.Environment;

public class GsonDeserializer implements com.google.gson.JsonDeserializer<Environment> {
	
	private static final Logger LOGGER = Logger.getLogger(Environment.class.getName());

	@Override
	public Environment deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		LOGGER.info("Using Environement deserializer");
		
		final JsonObject jsonObject = json.getAsJsonObject();
		
		final int hour = jsonObject.get("hour").getAsInt();
		final int airTemperature = jsonObject.get("air temperature").getAsInt();
		final int waterTemperature = jsonObject.get("water temperature").getAsInt();
		final int timeSpeed = jsonObject.get("time speed").getAsInt();
		final float dissolvedCO2 = jsonObject.get("dissolved CO2").getAsFloat();
		final float dissolvedOxygen = jsonObject.get("dissolved oxygen").getAsFloat();
		final float pH = jsonObject.get("pH").getAsFloat();
		final int plantNum = jsonObject.get("plant number").getAsInt();
		final int decomposersNum = jsonObject.get("decomposers number").getAsInt();
		final int smallFishNum = jsonObject.get("small fish number").getAsInt();
		final int mediumFishNum = jsonObject.get("medium fish number").getAsInt();
		final int largeFishNum = jsonObject.get("large fish number").getAsInt();
		
		Environment env = new Environment();
		
		env.setHour(hour);
		env.setAirTemperature(airTemperature);
		env.setWaterTemperature(waterTemperature);
		env.setTimeSpeed(timeSpeed);
		env.setDissolvedCO2(dissolvedCO2);
		env.setDissolvedOxygen(dissolvedOxygen);
		env.setpH(pH);
		env.setPlantNum(plantNum);
		env.setDecomposersNum(decomposersNum);
		env.setSmallFishNum(smallFishNum);
		env.setMediumFishNum(mediumFishNum);
		env.setLargeFishNum(largeFishNum);
		
		return env;
	}

}
