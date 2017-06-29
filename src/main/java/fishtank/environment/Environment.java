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
package main.java.fishtank.environment;

import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Environment extends Thread {
	
	private static final Logger LOGGER = Logger.getLogger(Environment.class.getName());
	
	private volatile int hour = 0; // 24-hour format
	private volatile int airTemperature = 0; // in Celsius by default
	private volatile int waterTemperature = 0; // in Celsius by default
	private int timeSpeed = 1; // ex: x2, x3, x10, where 0 is time stopped
	private float dissolvedCO2 = (float) 0.0;
	private volatile float dissolvedOxygen = (float) 0.0;
	private float pH = (float) 7.0;
	private volatile float pHchangeInDay = 0;
	private int plantNum = 0;
	private int decomposersNum = 0;
	private int smallFishNum = 0;
	private int mediumFishNum = 0;
	private int largeFishNum = 0;
	private volatile int deadOrganismMass = 0;

	private static WriteToFile writer;
	private static boolean run;
	
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

	}
	
	public void run() {
		Environment.run = true;
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
		
		LOGGER.log(Level.FINE, "Threads started.");		
	}
	
	private static class Clock implements Runnable {
		
		public static final int MILLISEC = 60000;
		protected int interval;
		private Environment env;
		private Calendar cal = Calendar.getInstance();
		private boolean clockDone = false;
		private Object deviceMonitor;
		
		public Clock(final Environment env, final int hour) {
			this.env = env;
			deviceMonitor = new Object();
			setHour(hour);
			setINTERVAL();
			
			env.createThread(this, "Clock Thread");
		}
		
		public void run() {
			while(run) {
				clockDone = false;
				LOGGER.log(Level.FINE, "In Clock Thread");
				try {
					Thread.sleep(this.interval);
				} catch (InterruptedException e) {
					LOGGER.log(Level.SEVERE, e.toString(), e);
				}
				cal.roll(Calendar.HOUR_OF_DAY, true);
				setHour();
				synchronized(env) {
					writer.writeToFile("\n\nHour:" + String.valueOf(env.getHour()) + ",");
					LOGGER.log(Level.FINE, "Clock data written to file.");
					this.clockDone = true;
					this.env.notifyAll();
				} synchronized (this.deviceMonitor) {
					this.deviceMonitor.notifyAll();
				} 
				LOGGER.log(Level.INFO, "Cycle complete.");
			}
		}
		
		public synchronized void setHour() {
			this.env.hour = cal.get(Calendar.HOUR_OF_DAY);
		}
		
		public  synchronized void setHour(final int hour) {
			cal.set(Calendar.HOUR_OF_DAY, hour);
			this.setHour();
		}
		
		public synchronized void setINTERVAL() {
			this.interval = MILLISEC / env.timeSpeed;
		}

		public boolean clockDone(){
			return this.clockDone;
		}
		
		public Object getMonitor() {
			return this.deviceMonitor;
		}
		
	}
	
	private class AirTemperature implements Runnable {
		
		private Environment env;
		private Object deviceMonitor;
		private int rate = 1; // in Celsius per hour
		
		public AirTemperature(Environment env) {
			this.env = env;
			deviceMonitor = new Object();
			env.createThread(this, "Air Temperature Thread");
		}
		
		public void run() {
			while(!clock.clockDone()) {
				synchronized(env) {
					try {
						env.wait();
					} catch (InterruptedException e) {
						LOGGER.log(Level.SEVERE, e.toString(), e);
					}
					LOGGER.log(Level.FINE, "In Air Temperature Thread");
					if (this.env.hour < 6 && this.env.hour > 10) {
						this.increase(this.rate);
					} else if (this.env.hour > 6 && this.env.hour < 10) {
						this.increase(- this.rate);
					}
					writer.writeToFile("\nAir Temperature:" + String.valueOf(env.getAirTemperature()) + ",");
					LOGGER.log(Level.FINE, "Air Temperature data written to file.");
				} synchronized (this.deviceMonitor) {
					this.deviceMonitor.notifyAll();
				}
			}
		}
		
		public synchronized void increase (int rate) {
			this.env.airTemperature += rate;
		}
		
		public Object getMonitor() {
			return this.deviceMonitor;
		}

	}
	
	private class WaterTemperature implements Runnable {
		
		private Environment env;
		private Object deviceMonitor;
		private int rate = 1; // in Celsius per hour
		
		public WaterTemperature(Environment env) {
			this.env = env;	
			this.deviceMonitor = new Object();
			env.createThread(this, "Water Temperature Thread");
		}
		
		public void run() {
			while(!clock.clockDone()) {
				synchronized(env) {
					try {
						env.wait();
					} catch (InterruptedException e) {
						LOGGER.log(Level.SEVERE, e.toString(), e);
					}
					LOGGER.log(Level.FINE, "In Water Temperature Thread");
					if (env.airTemperature > env.waterTemperature) {
						this.increase(this.rate);
					} else if (env.airTemperature < env.waterTemperature) {
						this.increase(- this.rate);
					}
					writer.writeToFile("\nWater Temperature:" + String.valueOf(env.getWaterTemperature()) + ",");
					LOGGER.log(Level.FINE, "Water Temperature data written to file.");
				} synchronized (this.deviceMonitor) {
					this.deviceMonitor.notifyAll();
				}
			}
		}
		
		private synchronized void increase (int rate) {
			this.env.waterTemperature += rate;
		}
		
		public Object getMonitor() {
			return this.deviceMonitor;
		}

	}
	
	/**
	 * Reference: http://www.fondriest.com/environmental-measurements/parameters/water-quality/
	 * @author Owner
	 *
	 */
	private class Fish implements Runnable{
		
		private Environment env; 
		
		public static final int DEATH_RATE = 1; // fish per hour
		
		public static final int SMALL_FISH_MASS = 100; // mass is relative to decomposer mass
		public static final int MEDIUM_FISH_MASS = 500;
		public static final int LARGE_FISH_MASS = 1000;		
		
		public Fish(Environment env) {
			this.env = env;
			env.createThread(this, "Fish Thread");
		}
		
		public void run() {
			while(!clock.clockDone()) {
				synchronized(env) {
					try {
						env.wait();
					} catch (InterruptedException e) {
						LOGGER.log(Level.SEVERE, e.toString(), e);
					}
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
		}
		
	}
	
	private class Decomposers implements Runnable {
		
		private Environment env;
		public static final int DEATH_RATE = 5; // decomposers per hour
		public static final int MASS = 1;
		
		public Decomposers(Environment env) {
			this.env = env;
			env.createThread(this, "Decomposers Thread");
		}
		
		public void run() {
			while(!clock.clockDone()) {
				synchronized(env) {
					try {
						env.wait();
					} catch (InterruptedException e) {
						LOGGER.log(Level.SEVERE, e.toString(), e);
					}
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
		}
	}
	
	private class Plants implements Runnable {
		
		private Environment env;
		public static final int DEATH_RATE = 1; // plants per hour
		public static final int MASS = 150;
		
		public Plants(Environment env) {
			this.env = env;
			env.createThread(this, "Plants Thread");
		}
		
		public void run() {
			while(!clock.clockDone()) {
				synchronized(env) {
					try {
						env.wait();
					} catch (InterruptedException e) {
						LOGGER.log(Level.SEVERE, e.toString(), e);
					}
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
		}
		
	}
	
	private class PH implements Runnable {
		private Environment env;
		private Object deviceMonitor;
		private int phChange = 0;
		private int numTimesRan = 0;
		
		public PH(Environment env) {
			this.env = env;
			deviceMonitor = new Object();
			env.createThread(this, "pH Thread");
		}
		
		public void run() {
			while(!clock.clockDone()) {
				synchronized(env) {
					try {
						env.wait();
					} catch (InterruptedException e) {
						LOGGER.log(Level.SEVERE, e.toString(), e);
					}
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
				} synchronized (this.deviceMonitor) {
					this.deviceMonitor.notifyAll();
				}
			}
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
			float reactionRate = 0;
			if (env.pH < 6.4 && env.pH > 0) {
			} else if (env.pH > 6.4 && env.pH < 10.4) {
				reactionRate = 1;
			} else if (env.pH > 10.4 && env.pH < 14) {
				reactionRate = 2;
			} else {
				throw new UnlikelyPHException();
			} if (env.dissolvedCO2 >= 10)
				reactionRate += 0.5;
			return reactionRate;
		}
		
		public Object getMonitor() {
			return this.deviceMonitor;
		}

	}
	
	private class Gases implements Runnable {
		
		private Environment env;
		private Object deviceMonitor;
		
		public Gases(Environment env) {
			this.env =  env;
			deviceMonitor = new Object();
			env.createThread(this, "Gases Thread");
		}
		
		public void run() {
			while(!clock.clockDone()) {
				synchronized(env) {
					try {
						env.wait();
					} catch (InterruptedException e) {
						LOGGER.log(Level.SEVERE, e.toString(), e);
					}
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
				} synchronized (this.deviceMonitor) {
					this.deviceMonitor.notifyAll();
				}
			}			
		}
		
		public Object getMonitor() {
			return this.deviceMonitor;
		}
	}
	
	private void createThread(Runnable obj, String threadName) {
		Thread t = new Thread(obj, threadName);
		t.start();
		LOGGER.log(Level.INFO, t.getName() + " started", t);
	}

	public void stopThreads() throws InterruptedException {
		Environment.run = false;
		Thread.sleep(5000);
		writer.done();
		this.join();
	}
	
	public void makeClockDevice(final String id, final String name, final String manufacturer, final String model) {
		new main.java.fishtank.devices.Clock(id, name, manufacturer, model, this, clock.getMonitor());
	}
	
	public void makeAirThermometerDevice(final String id, final String name, final String manufacturer, final String model) {
		new main.java.fishtank.devices.AirThermometer(id, name, manufacturer, model, this, airTemp.getMonitor());
	}	
	
	public void makeWaterThermometerDevice(final String id, final String name, final String manufacturer, final String model) {
		new main.java.fishtank.devices.WaterThermometer(id, name, manufacturer, model, this, waterTemp.getMonitor());
	}
	
	public void makeOxygenMeterDevice(final String id, final String name, final String manufacturer, final String model) {
		new main.java.fishtank.devices.OxygenMeter(id, name, manufacturer, model, this, gases.getMonitor());
	}
	
	public void makeCO2MeterDevice(final String id, final String name, final String manufacturer, final String model) {
		new main.java.fishtank.devices.CO2Meter(id, name, manufacturer, model, this, gases.getMonitor());
	}
	
	public void makePHMeterDevice(final String id, final String name, final String manufacturer, final String model) {
		new main.java.fishtank.devices.PHMeter(id, name, manufacturer, model, this, pH_obj.getMonitor());
	}
	
	public boolean getRun() {
		return Environment.run;
	}
	
	public synchronized int getHour() {
		return hour;
	}
	
	public synchronized int getAirTemperature() {
		return airTemperature;
	}
	
	public void setAirTemperature(int airTemperature) {
		this.airTemperature = airTemperature;
	}
	
	public synchronized int getWaterTemperature() {
		return waterTemperature;
	}
	
	public void setWaterTemperature(int waterTemperature) {
		this.waterTemperature = waterTemperature;
	}
	
	public synchronized int getTimeSpeed() {
		return timeSpeed;
	}
	
	public void setTimeSpeed(int timeSpeed) {
		this.timeSpeed = timeSpeed;
	}
	public synchronized float getDissolvedCO2() {
		return dissolvedCO2;
	}
	
	public void setDissolvedCO2(float dissolvedCO2) {
		this.dissolvedCO2 = dissolvedCO2;
	}
	
	public synchronized float getDissolvedOxygen() {
		return dissolvedOxygen;
	}
	
	public void setDissolvedOxygen(float dissolvedOxygen) {
		this.dissolvedOxygen = dissolvedOxygen;
	}
	
	public synchronized float getPH() {
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
	
	public synchronized int getDecomposersNum() {
		return decomposersNum;
	}
	
	public void setDecomposersNum(int decomposersNum) {
		this.decomposersNum = decomposersNum;
	}

	public synchronized int getSmallFishNum() {
		return smallFishNum;
	}

	public void setSmallFishNum(int smallFishNum) {
		this.smallFishNum = smallFishNum;
	}

	public synchronized int getMediumFishNum() {
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

}
