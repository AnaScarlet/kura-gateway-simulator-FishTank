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
	private float dissolvedCO2 = (float) 0.0;
	private float dissolvedOxygen = (float) 0.0;
	private float pH = (float) 7.0;
	private float pHchangeInDay = 0;
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
	private PH pHobj;

	public Environment(int hour, int airTemperature, int waterTemperature, int timeSpeed, float dissolvedCO2,
			float dissolvedOxygen, float pH, int plantNum, int fishNum, int decomposersNum) {
		this(true);
		this.clock = new Clock(this, hour);
		
		this.hour = hour;
		this.airTemperature = airTemperature;
		this.waterTemperature = waterTemperature;
		this.timeSpeed = timeSpeed;
		this.dissolvedCO2 = dissolvedCO2;
		this.dissolvedOxygen = dissolvedOxygen;
		this.pH = pH;
		this.plantNum = plantNum;
		this.fishNum = fishNum;
		this.decomposersNum = decomposersNum;
	}
	
	public Environment(boolean calledOn) {
		if (!calledOn)
			this.clock = new Clock(this);
		this.airTemp = new AirTemperature(this);
		this.waterTemp = new WaterTemperature(this);
		this.fish = new Fish(this);
		this.decomposers = new Decomposers(this);
		this.plants = new Plants(this);
		this.pHobj = new PH(this);
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
	
	/**
	 * Reference: http://www.fondriest.com/environmental-measurements/parameters/water-quality/
	 * @author Owner
	 *
	 */
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
							env.dissolvedOxygen > Fish.SMALL_FISH_MAX_DO
							|| env.pHchangeInDay > 1.5 && env.smallFishNum > 0) {
						env.smallFishNum -= Fish.DEATH_RATE;
						env.deadOrganismMass += Fish.SMALL_FISH_MASS * Fish.DEATH_RATE;
					} 
					if (env.smallFishNum > 0 && env.pHchangeInDay < 1.5) {
						env.dissolvedOxygen -= Fish.SMALL_FISH_RESPIRATION_RATE * env.smallFishNum;
						env.dissolvedCO2 += Fish.SMALL_FISH_RESPIRATION_RATE * env.smallFishNum;
					} 
					if (env.dissolvedOxygen < Fish.MEDIUM_FISH_MIN_DO || 
							env.dissolvedOxygen > Fish.MEDIUM_FISH_MAX_DO
							|| env.pHchangeInDay > 1.5 && env.mediumFishNum > 0) {
						env.mediumFishNum -= Fish.DEATH_RATE;
						env.deadOrganismMass += Fish.MEDIUM_FISH_MASS * Fish.DEATH_RATE;
					} 
					if (env.mediumFishNum > 0 && env.pHchangeInDay < 1.5) {
						env.dissolvedOxygen -= Fish.MEDIUM_FISH_RESPIRATION_RATE * env.mediumFishNum;
						env.dissolvedCO2 += Fish.MEDIUM_FISH_RESPIRATION_RATE * env.mediumFishNum;
					} 
					if (env.dissolvedOxygen < Fish.LARGE_FISH_MIN_DO || 
							env.dissolvedOxygen > Fish.LARGE_FISH_MAX_DO
							|| env.pHchangeInDay > 1.5 && env.largeFishNum > 0) {
						env.largeFishNum -= Fish.DEATH_RATE;
						env.deadOrganismMass += Fish.LARGE_FISH_MASS * Fish.DEATH_RATE;
					} 
					if (env.largeFishNum > 0 && env.pHchangeInDay < 1.5) {
						env.dissolvedOxygen -= Fish.LARGE_FISH_RESPIRATION_RATE * env.largeFishNum;
						env.dissolvedCO2 += Fish.LARGE_FISH_RESPIRATION_RATE * env.largeFishNum;
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
					} 
					if (env.deadOrganismMass < 0 && env.decomposersNum > 0) {
						env.deadOrganismMass -= env.decomposersNum; // each decomposer reduces 
																// dead mass by one unit per hour
						env.dissolvedOxygen -= Decomposers.RESPIRATION_RATE * env.decomposersNum;
						env.dissolvedCO2 += Decomposers.RESPIRATION_RATE * env.decomposersNum;
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
					if (env.dissolvedCO2 < Plants.CO2_FRACTION_REQ
							|| env.pHchangeInDay > 2 && env.plantNum > 0) {
						env.plantNum -= Plants.DEATH_RATE;
						env.deadOrganismMass += Plants.MASS * Plants.DEATH_RATE;
					} 
					if (env.plantNum > 0 && env.pHchangeInDay < 2) {
						env.dissolvedOxygen += Plants.PHOTOSYNTHESIS_RATE * env.plantNum;
					}
				}
			}
		}
		
	}
	
	private class PH implements Runnable {
		private Environment env;
		private int phChange = 0;
		private int numTimesRan = 0;
		
		public PH(Environment env) {
			this.env = env;
			env.createThread(this, "pH Thread");
		}
		
		public void run() {
			synchronized(env) {
				while (true) {
					int reactionRate = 0;
					try {
						reactionRate = this.calcCO2ReactionRate();
					} catch (UnlikelyPHException e) {
						e.printStackTrace();
					}
					if (this.calcTotalPhotosynthesisRate() < this.calcTotalRespirationRate()) {
						env.pH += reactionRate;
						if (numTimesRan <= 23) {
							phChange += reactionRate;
							numTimesRan++;
						} else {
							env.pHchangeInDay = phChange;
							numTimesRan = 0;
						}
					} 
				}
			}
		}
		
		private float calcTotalRespirationRate() {
			return (Decomposers.RESPIRATION_RATE * (float) env.decomposersNum) + 
					(Fish.SMALL_FISH_RESPIRATION_RATE * (float) env.smallFishNum) + 
					(Fish.MEDIUM_FISH_RESPIRATION_RATE * (float) env.mediumFishNum) +
					(Fish.LARGE_FISH_RESPIRATION_RATE * (float) env.largeFishNum);
		}
		
		private float calcTotalPhotosynthesisRate() {
			return Plants.PHOTOSYNTHESIS_RATE * env.plantNum;
		}
		
		/**
		 * Reference: http://ion.chem.usu.edu/~sbialkow/Classes/3650/Carbonate/Carbonic%20Acid.html
		 * @return reactionRate
		 * @throws UnlikelyPHException
		 */
		private int calcCO2ReactionRate() throws UnlikelyPHException {
			int reactionRate = 0;
			if (env.pH < 6.4 && env.pH > -1) {
			} else if (env.pH > 6.4 && env.pH < 10.4) {
				reactionRate = 1;
			} else if (env.pH > 10.4 && env.pH < 15) {
				reactionRate = 2;
			} else {
				throw new UnlikelyPHException();
			}
			return reactionRate;
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
	public float getdissolvedCO2() {
		return dissolvedCO2;
	}
	public void setdissolvedCO2(float dissolvedCO2) {
		this.dissolvedCO2 = dissolvedCO2;
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
