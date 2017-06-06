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
package environment;

import java.util.Calendar;

public class Environment {
	
	private int hour = 0; // 24-hour format
	private int airTemperature = 0; // in Celsius by default
	private int waterTemperature = 0; // in Celsius by default
	private int timeSpeed = 1; // ex: x2, x3, x10, where 0 is time stopped
	private float CO2fraction = (float) 0.0;
	private float dissolvedOxygen = (float) 0.0;
	private float pH = (float) 7.0;
	private int plantNum = 0;
	private int fishNum= 0;
	private int decomposersNum = 0;
	private int smallFishNum = 0;
	private int mediumFishNum = 0;
	private int largeFishNum = 0;
	private int deadOrganismMass = 0;

	private Clock clock;
	private AirTemperature airTemp;
	private WaterTemperature waterTemp;
	private Fish fish;
	private Decomposers decomposers;
	private Plants plants;

	public Environment(int hour, int airTemperature, int waterTemperature, int timeSpeed, float cO2fraction,
			float dissolvedOxygen, float pH, int plantNum, int fishNum, int decomposersNum) {
		super();
		this.hour = hour;
		this.airTemperature = airTemperature;
		this.waterTemperature = waterTemperature;
		this.timeSpeed = timeSpeed;
		this.CO2fraction = cO2fraction;
		this.dissolvedOxygen = dissolvedOxygen;
		this.pH = pH;
		this.plantNum = plantNum;
		this.fishNum = fishNum;
		this.decomposersNum = decomposersNum;
	}
	
	public Environment() {
		this.clock = new Clock(this);
		this.airTemp = new AirTemperature(this);
		this.waterTemp = new WaterTemperature(this);
		this.fish = new Fish(this);
		this.decomposers = new Decomposers(this);
		this.plants = new Plants(this);
	}
	
	private class Clock implements Runnable {
		
		public static final int MILLISEC = 216000;
		protected int interval;
		private Environment env;
		private Calendar cal = Calendar.getInstance();
		
		public Clock(Environment env) {
			setHour();
			setINTERVAL();
			this.env = env;
			env.createThread(this, "Clock Thread");
		}
		
		public Clock(final Environment env, final int hour) {
			setHour(hour);
			setINTERVAL();
			this.env = env;
			env.createThread(this, "Clock Thread");
		}
		
		public void run() {
			synchronized(env) {
				while(true) {
					System.out.println("The hour is " + env.hour);
					try {
						Thread.sleep(this.interval);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					cal.roll(Calendar.HOUR_OF_DAY, true);
					setHour();
				}
			}
		}
		
		public void setHour() {
			env.hour = cal.get(Calendar.HOUR_OF_DAY);
		}
		
		public void setHour(final int hour) {
			cal.set(Calendar.HOUR_OF_DAY, hour);
			this.setHour();
		}
		
		public void setINTERVAL() {
			this.interval = MILLISEC / env.timeSpeed;
		}

	}
	
	private class AirTemperature implements Runnable {
		
		private Environment env;
		private int rate; // in Celsius per hour
		
		public AirTemperature(Environment env) {
			this.env = env;
			env.createThread(this, "Air Temperature Thread");
		}
		
		public void run() {
			synchronized(env) {
				while(true) {
					if (this.env.hour < 6 && this.env.hour > 10) {
						airTemp.increase(this.rate);
					} else if (this.env.hour > 6 && this.env.hour < 10) {
						airTemp.increase(- this.rate);
					}
				}
			}
		}
		
		public void increase (int rate) {
			env.airTemperature += rate;
		}

	}
	
	private class WaterTemperature implements Runnable {
		
		private Environment env;
		private int rate; // in Celsius per hour
		
		public WaterTemperature(Environment env) {
			this.env = env;	
			env.createThread(this, "Water Temperature Thread");
		}
		
		public void run() {
			synchronized(env) {
				while (true) {
					if (env.airTemperature > env.waterTemperature) {
						env.waterTemp.increase(this.rate);
					} else if (env.airTemperature < env.waterTemperature) {
						env.waterTemp.increase(- this.rate);
					}
				}
			}
		}
		
		public void increase (int rate) {
			env.waterTemperature += rate;
		}

	}
	
	private class Fish implements Runnable{
		
		private Environment env; 
		public static final float SMALL_FISH_MIN_DO = 2; // DO - dissolved oxygen in mg/L. 
		public static final float MEDIUM_FISH_MIN_DO = 6;
		public static final float LARGE_FISH_MIN_DO = 10;
		public static final float SMALL_FISH_MAX_DO = 5; 
		public static final float MEDIUM_FISH_MAX_DO = 9;
		public static final float LARGE_FISH_MAX_DO = 14;
		
		public static final int DEATH_RATE = 1; // fish per hour
		
		public static final int SMALL_FISH_MASS = 100; // mass is relative to decomposer mass
		public static final int MEDIUM_FISH_MASS = 500;
		public static final int LARGE_FISH_MASS = 1000;
		
		public static final float SMALL_FISH_RESPIRATION_RATE = (float) 0.5;
		public static final float MEDIUM_FISH_RESPIRATION_RATE = (float) 2;
		public static final float LARGE_FISH_RESPIRATION_RATE = (float) 5;
		
		
		public Fish(Environment env) {
			this.env = env;
			env.createThread(this, "Fish Thread");
		}
		
		public void run() {
			synchronized(env) {
				while (true) {
					if (env.dissolvedOxygen < Fish.SMALL_FISH_MIN_DO || 
							env.dissolvedOxygen > Fish.SMALL_FISH_MAX_DO && env.smallFishNum > 0) {
						env.smallFishNum -= Fish.DEATH_RATE;
						env.deadOrganismMass += Fish.SMALL_FISH_MASS;
					} if (env.smallFishNum > 0) {
						env.dissolvedOxygen -= Fish.SMALL_FISH_RESPIRATION_RATE * env.smallFishNum;
						env.CO2fraction += Fish.SMALL_FISH_RESPIRATION_RATE * env.smallFishNum;
					} if (env.dissolvedOxygen < Fish.MEDIUM_FISH_MIN_DO || 
							env.dissolvedOxygen > Fish.MEDIUM_FISH_MAX_DO && env.mediumFishNum > 0) {
						env.mediumFishNum -= Fish.DEATH_RATE;
						env.deadOrganismMass += Fish.MEDIUM_FISH_MASS;
					} if (env.mediumFishNum > 0) {
						env.dissolvedOxygen -= Fish.MEDIUM_FISH_RESPIRATION_RATE * env.mediumFishNum;
						env.CO2fraction += Fish.MEDIUM_FISH_RESPIRATION_RATE * env.mediumFishNum;
					} if (env.dissolvedOxygen < Fish.LARGE_FISH_MIN_DO || 
							env.dissolvedOxygen > Fish.LARGE_FISH_MAX_DO && env.largeFishNum > 0) {
						env.largeFishNum -= Fish.DEATH_RATE;
						env.deadOrganismMass += Fish.LARGE_FISH_MASS;
					} if (env.largeFishNum > 0) {
						env.dissolvedOxygen -= Fish.LARGE_FISH_RESPIRATION_RATE * env.largeFishNum;
						env.CO2fraction += Fish.LARGE_FISH_RESPIRATION_RATE * env.largeFishNum;
					} 
				}
			}
		}
		
	}
	
	private class Decomposers implements Runnable {
		
		private Environment env;
		public static final float DO = 1;
		public static final int DEATH_RATE = 5; // decomposers per hour
		public static final int MASS = 1;
		public static final float RESPIRATION_RATE = (float) 0.025; // of a plant per hour
		
		public Decomposers(Environment env) {
			this.env = env;
			env.createThread(this, "Decomposers Thread");
		}
		
		public void run() {
			synchronized(env) {
				while (true) {
					if (env.dissolvedOxygen < Decomposers.DO && env.decomposersNum > 0) {
						env.decomposersNum -= Decomposers.DEATH_RATE;
						env.deadOrganismMass += Decomposers.MASS * Decomposers.DEATH_RATE;
					} if (env.deadOrganismMass < 0 && env.decomposersNum > 0) {
						env.deadOrganismMass -= env.decomposersNum; // each decomposer reduces 
																// dead mass by one unit per hour
						env.dissolvedOxygen -= Decomposers.RESPIRATION_RATE * env.decomposersNum;
						env.CO2fraction += Decomposers.RESPIRATION_RATE * env.decomposersNum;
					}
				}
			}
		}
		
	}
	
	private class Plants implements Runnable {
		
		private Environment env;
		public static final int DEATH_RATE = 1; // plants per hour
		public static final int MASS = 150;
		public static final int PHOTOSYNTHESIS_RATE = 2; // O2 produced per hour per plant
		public static final int CO2_FRACTION_REQ = 3;
		
		public Plants(Environment env) {
			this.env = env;
			env.createThread(this, "Plants Thread");
		}
		
		public void run() {
			synchronized(env) {
				while (true) {
					if (env.CO2fraction < Plants.CO2_FRACTION_REQ && env.plantNum > 0) {
						env.plantNum -= Plants.DEATH_RATE;
						env.deadOrganismMass += Plants.MASS * Plants.DEATH_RATE;
					} if (env.plantNum > 0) {
						env.dissolvedOxygen += Plants.PHOTOSYNTHESIS_RATE * env.plantNum;
					}
				}
			}
		}
		
	}
	
	private class Oxygen implements Runnable {
		private Environment env;
		
		public Oxygen(Environment env) {
			this.env = env;
			env.createThread(this, "Oxygen Thread");
		}
		
		public void run() {
			synchronized(env) {
				while (true) {
					// implement devices???
				}
			}
		}
	}
	
	private class PH implements Runnable {
		private Environment env;
		
		public PH(Environment env) {
			this.env = env;
			env.createThread(this, "pH Thread");
		}
		
		public void run() {
			synchronized(env) {
				while (true) {
					// how CO2 affects pH... 
				}
			}
		}
	}
	
	private void createThread(Runnable obj, String threadName) {
		Thread t = new Thread(obj, threadName);
		t.start();
		System.out.println(t + " started");
	}
	
	public int getHour() {
		return hour;
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
	public float getCO2fraction() {
		return CO2fraction;
	}
	public void setCO2fraction(float cO2fraction) {
		CO2fraction = cO2fraction;
	}
	public float getDissolvedOxygen() {
		return dissolvedOxygen;
	}
	public void setDissolvedOxygen(float dissolvedOxygen) {
		this.dissolvedOxygen = dissolvedOxygen;
	}
	public float getpH() {
		return pH;
	}
	public void setpH(float pH) {
		this.pH = pH;
	}
	public int getPlantNum() {
		return plantNum;
	}
	public void setPlantNum(int plantNum) {
		this.plantNum = plantNum;
	}
	public int getFishNum() {
		return fishNum;
	}
	public void setFishNum(int fishNum) {
		this.fishNum = fishNum;
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

	public int getLargeFishNum() {
		return largeFishNum;
	}

	public void setLargeFishNum(int largeFishNum) {
		this.largeFishNum = largeFishNum;
	}

}
