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
package environment_ai;

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
	private int smallFishNum;
	private int mediumFishNum;
	private int largeFishNum;

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
		public static final float SMALL_FISH_DO = 4; // DO - dissolved oxygen in mg/L. 
		public static final float MEDIUM_FISH_DO = (float) 7.5;
		public static final float LARGE_FISH_DO = 12;
		public static final int DEATH_RATE = 1; // fish per hour
		
		
		public Fish(Environment env) {
			this.env = env;
			env.createThread(this, "Fish Thread");
		}
		
		public void run() {
			synchronized(env) {
				while (true) {
					if (env.dissolvedOxygen < Fish.SMALL_FISH_DO) {
						env.smallFishNum -= Fish.DEATH_RATE;
					} if (env.dissolvedOxygen < Fish.MEDIUM_FISH_DO) {
						env.mediumFishNum -= Fish.DEATH_RATE;
					} if (env.dissolvedOxygen < Fish.LARGE_FISH_DO) {
						env.largeFishNum -= Fish.DEATH_RATE;
					} 
				}
			}
		}
		
	}
	
	private class Decomposers implements Runnable {
		
		private Environment env;
		public static final float DO = 1;
		public static final int DEATH_RATE = 5; // decomposers per hour
		
		public Decomposers(Environment env) {
			this.env = env;
			env.createThread(this, "Decomposers Thread");
		}
		
		public void run() {
			synchronized(env) {
				while (true) {
					if (env.dissolvedOxygen < Decomposers.DO) {
						env.decomposersNum -= Decomposers.DEATH_RATE;
					}
				}
			}
		}
		
	}
	
	private class Plants implements Runnable {
		
		private Environment env;
		public static final int DEATH_RATE = 1; // plants per hour
		
		public Plants(Environment env) {
			this.env = env;
			env.createThread(this, "Plants Thread");
		}
		
		public void run() {
			
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
