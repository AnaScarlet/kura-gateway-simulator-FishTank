/*******************************************************************************
 * Copyright (c) 2017 Anastasiya Lazarenko
 *******************************************************************************/
package main.java.fishtank.environment;

import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Environment {
	
	private static final Logger LOGGER = Logger.getLogger(Environment.class.getName());
	
	private volatile int hour; // 24-hour format
	private volatile int airTemperature; // in Celsius by default
	private volatile int waterTemperature; // in Celsius by default
	private int timeSpeed; // ex: x2, x3, x10, where 0 is time stopped
	private float dissolvedCO2;
	private volatile float dissolvedOxygen;
	private float pH;
	private volatile float pHchangeInDay;
	private int plantNum;
	private int decomposersNum;
	private int smallFishNum;
	private int mediumFishNum;
	private int largeFishNum;
	private volatile int deadOrganismMass;
	private int timeInterval;

	private static WriteToFile writer;
	private boolean run;
	
	private Clock clock;
	private AirTemperature airTemp;
	private WaterTemperature waterTemp;
	private Fish fish;
	private Decomposers decomposers;
	private Plants plants;
	private PH pH_obj;
	private Gases gases;
		
	public static final float SMALL_FISH_MIN_DO = 2; // DO - dissolved oxygen in mg/L. 
	public static final float MEDIUM_FISH_MIN_DO = 6;
	public static final float LARGE_FISH_MIN_DO = 10;
	public static final float SMALL_FISH_MAX_DO = 6; 
	public static final float MEDIUM_FISH_MAX_DO = 10;
	public static final float LARGE_FISH_MAX_DO = 15;
	public static final float DECOMPOSERS_DO = 1;
	public static final float DISSOLVED_CO2 = (float) 0.2;
	
	public static final float SMALL_FISH_RESPIRATION_RATE = (float) 0.05;
	public static final float MEDIUM_FISH_RESPIRATION_RATE = (float) 0.2;
	public static final float LARGE_FISH_RESPIRATION_RATE = (float) 0.5;
	public static final float DECOMPOSERS_RESPIRATION_RATE = (float) 0.0025; // of a plant per hour
	public static final float PHOTOSYNTHESIS_RATE = (float) 0.1; // O2 produced per hour per plant
	
	public static final int MILLISEC = 60000;

	public Environment(int hour, int airTemperature, int waterTemperature, int timeSpeed, float dissolvedCO2,
			float dissolvedOxygen, float pH, int plantNum, int smallFishNum, int mediumFishNum, int largeFishNum,
			int decomposersNum) {
		this.hour = hour;
		this.airTemperature = airTemperature;
		this.waterTemperature = waterTemperature;
		this.timeSpeed = timeSpeed;
		this.dissolvedCO2 = dissolvedCO2;
		this.dissolvedOxygen = dissolvedOxygen;
		this.pH = pH;
		this.plantNum = plantNum;
		this.smallFishNum = smallFishNum;
		this.mediumFishNum = mediumFishNum;
		this.largeFishNum = largeFishNum;
		this.decomposersNum = decomposersNum;
		this.deadOrganismMass = 0;
		this.pHchangeInDay = 0;
		
		this.run = true;
		Environment.writer = new WriteToFile("envData.txt");
		
		LOGGER.log(Level.FINE, "Preparations complete.");
		
		this.clock = new Clock(this, this.hour);
		this.airTemp = new AirTemperature(this);
		this.waterTemp = new WaterTemperature(this);
		this.fish = new Fish(this);
		this.decomposers = new Decomposers(this);
		this.plants = new Plants(this);
		this.pH_obj = new PH(this);
		this.gases = new Gases(this);
	}
	
	/**
	 * Sets variables to default values (no fish or plants). 
	 */
	public Environment() {
		this(0, 25, 25, 1, (float) 5, (float) 5, (float) 7, 0, 0, 0, 0, 20);
	}
	
	public void callElements() {
		this.clock.run();
		this.airTemp.run();
		this.waterTemp.run();
		this.fish.run();
		this.decomposers.run();
		this.plants.run();
		this.pH_obj.run();
		this.gases.run();
		}
	
	private static class Clock {
		
		private Environment env;
		private Calendar cal = Calendar.getInstance();
		
		public Clock(final Environment env, final int hour) {
			this.env = env;
			setHour(hour);
			env.calculateInterval();
		}
		
		public void run() {
			LOGGER.log(Level.FINE, "In Clock Thread");
			synchronized(env) {
				cal.roll(Calendar.HOUR_OF_DAY, true);
				setHour();
				writer.writeToFile("\n\nHour:" + String.valueOf(env.getHour()) + ",");
				LOGGER.log(Level.FINE, "Clock data written to file.");
			LOGGER.log(Level.INFO, "Cycle " + (env.hour + 1) + " complete.");
			}
		}
		
		public synchronized void setHour() {
			this.env.hour = cal.get(Calendar.HOUR_OF_DAY);
		}
		
		public  synchronized void setHour(final int hour) {
			cal.set(Calendar.HOUR_OF_DAY, hour);
			this.setHour();
		}
		
	}
	
	private class AirTemperature {
		
		private Environment env;
		private int rate = 1; // in Celsius per hour
		
		public AirTemperature(Environment env) {
			this.env = env;
		}
		
		public void run() {
			LOGGER.log(Level.FINE, "In Air Temperature Thread");
			if (this.env.hour < 6 && this.env.hour > 10) {
				this.increase(this.rate);
			} else if (this.env.hour > 6 && this.env.hour < 10) {
				this.increase(- this.rate);
			}
			writer.writeToFile("\nAir Temperature:" + String.valueOf(env.getAirTemperature()) + ",");
			LOGGER.log(Level.FINE, "Air Temperature data written to file.");
		}
		
		public synchronized void increase (int rate) {
			this.env.airTemperature += rate;
		}
	}
	
	private class WaterTemperature {
		
		private Environment env;
		private int rate = 1; // in Celsius per hour
		
		public WaterTemperature(Environment env) {
			this.env = env;	
		}
		
		public void run() {
			LOGGER.log(Level.FINE, "In Water Temperature Thread");
			if (env.airTemperature > env.waterTemperature) {
				this.increase(this.rate);
			} else if (env.airTemperature < env.waterTemperature) {
				this.increase(- this.rate);
			}
			writer.writeToFile("\nWater Temperature:" + String.valueOf(env.getWaterTemperature()) + ",");
			LOGGER.log(Level.FINE, "Water Temperature data written to file.");
		}
		
		private synchronized void increase (int rate) {
			this.env.waterTemperature += rate;
		}
	}
	
	/**
	 * Reference: http://www.fondriest.com/environmental-measurements/parameters/water-quality/
	 * @author Owner
	 *
	 */
	private class Fish{
		
		private Environment env; 
		
		public static final int DEATH_RATE = 1; // fish per hour
		
		public static final int SMALL_FISH_MASS = 100; // mass is relative to decomposer mass
		public static final int MEDIUM_FISH_MASS = 500;
		public static final int LARGE_FISH_MASS = 1000;		
		
		public Fish(Environment env) {
			this.env = env;
		}
		
		public void run() {
				LOGGER.log(Level.FINE, "In Fish Thread");
				if (env.dissolvedOxygen == 0 && env.smallFishNum > 0) {
					env.smallFishNum = 0;
					env.deadOrganismMass += Fish.SMALL_FISH_MASS * env.smallFishNum;
				}
				else if ((env.dissolvedOxygen < Environment.SMALL_FISH_MIN_DO || 
						env.dissolvedOxygen > Environment.SMALL_FISH_MAX_DO
						|| env.pHchangeInDay > 1.5) && env.smallFishNum > 0) {
					env.smallFishNum -= Fish.DEATH_RATE;
					env.deadOrganismMass += Fish.SMALL_FISH_MASS * Fish.DEATH_RATE;
				} 
				if (env.dissolvedOxygen == 0 && env.mediumFishNum > 0) {
					env.mediumFishNum = 0;
					env.deadOrganismMass += Fish.MEDIUM_FISH_MASS * env.mediumFishNum;
				}
				else if ((env.dissolvedOxygen < Environment.MEDIUM_FISH_MIN_DO || 
						env.dissolvedOxygen > Environment.MEDIUM_FISH_MAX_DO
						|| env.pHchangeInDay > 1.5) && env.mediumFishNum > 0) {
					env.mediumFishNum -= Fish.DEATH_RATE;
					env.deadOrganismMass += Fish.MEDIUM_FISH_MASS * Fish.DEATH_RATE;
				} 
				if (env.dissolvedOxygen == 0 && env.largeFishNum > 0) {
					env.largeFishNum = 0;
					env.deadOrganismMass += Fish.LARGE_FISH_MASS * env.largeFishNum;
				}
				else if ((env.dissolvedOxygen < Environment.LARGE_FISH_MIN_DO || 
						env.dissolvedOxygen > Environment.LARGE_FISH_MAX_DO
						|| env.pHchangeInDay > 1.5) && env.largeFishNum > 0) {
					env.largeFishNum -= Fish.DEATH_RATE;
					env.deadOrganismMass += Fish.LARGE_FISH_MASS * Fish.DEATH_RATE;
				} 
				writer.writeToFile("\nSmall Fish:" + String.valueOf(env.getSmallFishNum()) + "," 
				+ "\nMedium Fish:" + env.getMediumFishNum() + ","
				+ "\nLarge Fish:" + env.getLargeFishNum() + ",");
				LOGGER.log(Level.FINE, "Fish data written to file.");
			}
	}
	
	private class Decomposers {
		
		private Environment env;
		public static final int DEATH_RATE = 5; // decomposers per hour
		public static final int MASS = 1;
		
		public Decomposers(Environment env) {
			this.env = env;
		}
		
		public void run() {
			LOGGER.log(Level.FINE, "In Decomposers Thread");
			if (env.dissolvedOxygen < Environment.DECOMPOSERS_RESPIRATION_RATE && env.decomposersNum > 0) {
				env.decomposersNum -= Decomposers.DEATH_RATE;
				env.deadOrganismMass += Decomposers.MASS * Decomposers.DEATH_RATE;
			} if (env.dissolvedOxygen == 0 && env.decomposersNum > 0) {
				env.decomposersNum = 0;
				env.deadOrganismMass += Decomposers.MASS * env.decomposersNum;
			} else if (env.deadOrganismMass < 0 && env.decomposersNum > 0) {
				env.deadOrganismMass -= env.decomposersNum; // each decomposer reduces 
														// dead mass by one unit per hour					
			}
			writer.writeToFile("\nDecomposers:" + String.valueOf(env.getDecomposersNum()) + ",");
			LOGGER.log(Level.FINE, "Decomposers data written to file.");
		}
	}
	
	private class Plants {
		
		private Environment env;
		public static final int DEATH_RATE = 1; // plants per hour
		public static final int MASS = 150;
		
		public Plants(Environment env) {
			this.env = env;
		}
		
		public void run() {
			LOGGER.log(Level.FINE, "In Plants Thread");
			
			if (env.dissolvedCO2 == 0 && env.plantNum > 0) {
				env.plantNum = 0;
				env.deadOrganismMass += Plants.MASS * env.plantNum;
			}
			else if ((env.dissolvedCO2 < Environment.DISSOLVED_CO2 || env.pHchangeInDay > 2) && env.plantNum > 0) {
				env.plantNum -= Plants.DEATH_RATE;
				env.deadOrganismMass += Plants.MASS * Plants.DEATH_RATE;
			} 
			writer.writeToFile(String.valueOf("\nPlants:" + env.getPlantNum()) + ",");
			
			LOGGER.log(Level.FINE, "Plant data written to file.");
		}
	}
	
	private class PH {
		private Environment env;
		private int phChange = 0;
		private int numTimesRan = 0;
		
		public PH(Environment env) {
			this.env = env;
		}
		
		public void run() {
			LOGGER.log(Level.FINE, "In PH Thread");
			float reactionRate = 0;
			try {
				reactionRate = this.calcCO2ReactionRate();
			} catch (UnlikelyPHException e) {
				LOGGER.log(Level.SEVERE, e.toString(), e);
			}
			env.pH -= reactionRate;
			if (this.calcTotalPhotosynthesisRate() < this.calcTotalRespirationRate()) {
				if (numTimesRan <= 23) {
					phChange += reactionRate;
					numTimesRan++;
				} else {
					env.pHchangeInDay = phChange;
					numTimesRan = 0;
				}
			} 
			writer.writeToFile("\nPH:" + String.valueOf(env.getPH()) + ",");
			LOGGER.log(Level.FINE, "PH data written to file.");
		}
		
		private synchronized float calcTotalRespirationRate() {
			return (Environment.DECOMPOSERS_RESPIRATION_RATE * (float) env.decomposersNum) + 
					(Environment.SMALL_FISH_RESPIRATION_RATE * (float) env.smallFishNum) + 
					(Environment.MEDIUM_FISH_RESPIRATION_RATE * (float) env.mediumFishNum) +
					(Environment.LARGE_FISH_RESPIRATION_RATE * (float) env.largeFishNum);
		}
		
		private synchronized float calcTotalPhotosynthesisRate() {
			return Environment.PHOTOSYNTHESIS_RATE * env.plantNum;
		}
		
		/**
		 * Reference: http://ion.chem.usu.edu/~sbialkow/Classes/3650/Carbonate/Carbonic%20Acid.html
		 * @return reactionRate
		 * @throws UnlikelyPHException
		 */
		private synchronized float calcCO2ReactionRate() throws UnlikelyPHException {
			float reactionRate = 0; // per hour
			if (env.pH < 6.4 && env.pH > 0) {
			} else if (env.pH > 6.4 && env.pH < 10.4) {
				reactionRate = (float) 0.01;
			} else if (env.pH > 10.4 && env.pH < 14) {
				reactionRate = (float) 0.02;
			} else {
				throw new UnlikelyPHException();
			} if (env.dissolvedCO2 >= 10)
				reactionRate += 0.5;
			return reactionRate;
		}

	}
	
	private class Gases {
		
		private Environment env;
		
		public Gases(Environment env) {
			this.env =  env;
		}
		
		public void run() {
			LOGGER.log(Level.FINE, "In Gases Thread");
			if (env.smallFishNum > 0) {
				env.dissolvedCO2 += Environment.SMALL_FISH_RESPIRATION_RATE * env.smallFishNum;
				env.dissolvedOxygen -= Environment.SMALL_FISH_RESPIRATION_RATE * env.smallFishNum;
			} 
			if (env.mediumFishNum > 0) {
				env.dissolvedCO2 += Environment.MEDIUM_FISH_RESPIRATION_RATE * env.mediumFishNum;
				env.dissolvedOxygen -= Environment.MEDIUM_FISH_RESPIRATION_RATE * env.mediumFishNum;
			} 
			if (env.largeFishNum > 0) {
				env.dissolvedCO2 += Environment.LARGE_FISH_RESPIRATION_RATE * env.largeFishNum;
				env.dissolvedOxygen -= Environment.LARGE_FISH_RESPIRATION_RATE * env.largeFishNum;
			} 
			if (env.plantNum > 0) {
				env.dissolvedOxygen += Environment.DISSOLVED_CO2 * env.plantNum;
			}
			if (env.decomposersNum > 0) {
				env.dissolvedCO2 += Environment.DECOMPOSERS_RESPIRATION_RATE * env.decomposersNum;
				env.dissolvedOxygen -= Environment.DECOMPOSERS_RESPIRATION_RATE * env.decomposersNum;					
			}
			writer.writeToFile(String.valueOf("\nOxygen:" + env.getDissolvedOxygen() + "," 
					+ "\nCO2:" + env.getDissolvedCO2() + ","));
			
			LOGGER.log(Level.FINE, "Gases data written to file.");
		}
	}
	
	public void closeWriter(){
		writer.done();
	}
	
	public void calculateInterval() {
		this.setInterval(MILLISEC / this.timeSpeed);
	}
	
	public void setInterval(int interval) {
		this.timeInterval = interval;
	}
	
	public int getInterval() {
		return this.timeInterval;
	}
	
	public boolean getRun() {
		return this.run;
	}
	
	public int getHour() {
		return hour;
	}
	
	public void setHour(int hour) {
		this.hour = hour;
	}
	
	public int getAirTemperature() {
		return airTemperature;
	}
	
	public void setAirTemperature(int airTemperature) {
		this.airTemperature = airTemperature;
	}
	
	public int getWaterTemperature() {
		return waterTemperature;
	}
	
	public void setWaterTemperature(int waterTemperature) {
		this.waterTemperature = waterTemperature;
	}
	
	public int getTimeSpeed() {
		return timeSpeed;
	}
	
	public void setTimeSpeed(int timeSpeed) {
		this.timeSpeed = timeSpeed;
	}
	public float getDissolvedCO2() {
		return dissolvedCO2;
	}
	
	public void setDissolvedCO2(float dissolvedCO2) {
		this.dissolvedCO2 = dissolvedCO2;
	}
	
	public float getDissolvedOxygen() {
		return dissolvedOxygen;
	}
	
	public void setDissolvedOxygen(float dissolvedOxygen) {
		this.dissolvedOxygen = dissolvedOxygen;
	}
	
	public float getPH() {
		return pH;
	}
	
	public void setpH(float pH) {
		this.pH = pH;
	}
	
	public synchronized int getPlantNum() {
		return plantNum;
	}
	
	public void setPlantNum(int plantNum) {
		this.plantNum = plantNum;
	}
	
	public int getDecomposersNum() {
		return decomposersNum;
	}
	
	public void setDecomposersNum(int decomposersNum) {
		this.decomposersNum = decomposersNum;
	}

	public int getSmallFishNum() {
		return smallFishNum;
	}

	public void setSmallFishNum(int smallFishNum) {
		this.smallFishNum = smallFishNum;
	}

	public int getMediumFishNum() {
		return mediumFishNum;
	}

	public void setMediumFishNum(int mediumFishNum) {
		this.mediumFishNum = mediumFishNum;
	}

	public synchronized int getLargeFishNum() {
		return largeFishNum;
	}

	public void setLargeFishNum(int largeFishNum) {
		this.largeFishNum = largeFishNum;
	}
	
	public WriteToFile getWriter() {
		return Environment.writer;
	}
	
	public String toString() {
		return "Air Temperature: " + this.getAirTemperature() + ", Decomposers:" + this.getDecomposersNum()
			+ ", Dossolved CO2:" + this.getDissolvedCO2() + ", Dissolved Oxygen:" + this.getDissolvedOxygen()
			+ ", Hour:" + this.getHour() + ", Large Fish:" + this.getLargeFishNum()  + ", Medium Fish:" + this.getMediumFishNum()
			+ ", Small Fish:" + this.getSmallFishNum() + ", PH:" + this.getPH() + ", Plants:" + this.getPlantNum() 
			+ ", Time Speed:" + this.getTimeSpeed() + ", Water Temperature:" + this.getWaterTemperature()
			+ ", Time Interval: " + this.timeInterval;
	}

}
