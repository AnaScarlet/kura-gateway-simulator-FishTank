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

public class Environment extends Thread {
	
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
		
		System.out.println("Preparations complete.");
		
		new Clock(this, this.hour);
		new AirTemperature(this);
		new WaterTemperature(this);
		new Fish(this);
		new Decomposers(this);
		new Plants(this);
		new PH(this);
		
		System.out.println("Threads started.");		
	}
	
	private static class Clock implements Runnable {
		
		public static final int MILLISEC = 60000;
		protected int interval;
		private Environment env;
		private Calendar cal = Calendar.getInstance();
		private static boolean clockDone = false;
		
		public Clock(final Environment env, final int hour) {
			this.env = env;
			setHour(hour);
			setINTERVAL();
			
			env.createThread(this, "Clock Thread");
		}
		
		public void run() {
			while(run) {
				clockDone = false;
				System.out.println("In Clock Thread");
				try {
					Thread.sleep(this.interval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				cal.roll(Calendar.HOUR_OF_DAY, true);
				setHour();
				synchronized(env) {
					writer.writeToFile("Hour:" + String.valueOf(env.getHour()) + ",");
					System.out.println("Clock data written to file.");
					clockDone = true;
					env.notifyAll();
				}
			}
		}
		
		public synchronized void setHour() {
			env.hour = cal.get(Calendar.HOUR_OF_DAY);
		}
		
		public  synchronized void setHour(final int hour) {
			cal.set(Calendar.HOUR_OF_DAY, hour);
			this.setHour();
		}
		
		public synchronized void setINTERVAL() {
			this.interval = MILLISEC / env.timeSpeed;
		}

		public static boolean clockDone(){
			return clockDone;
		}
		
	}
	
	private class AirTemperature implements Runnable {
		
		private Environment env;
		private int rate = 1; // in Celsius per hour
		
		public AirTemperature(Environment env) {
			this.env = env;
			env.createThread(this, "Air Temperature Thread");
		}
		
		public void run() {
			while(!Clock.clockDone()) {
				synchronized(env) {
					try {
						env.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("In Air Temperature Thread");
					if (this.env.hour < 6 && this.env.hour > 10) {
						this.increase(this.rate);
					} else if (this.env.hour > 6 && this.env.hour < 10) {
						this.increase(- this.rate);
					}
					writer.writeToFile("Air Temperature:" + String.valueOf(env.getAirTemperature()) + ",");
					System.out.println("Air Temperature data written to file.");
				}
			}
		}
		
		public synchronized void increase (int rate) {
			env.airTemperature += rate;
		}

	}
	
	private class WaterTemperature implements Runnable {
		
		private Environment env;
		private int rate = 1; // in Celsius per hour
		
		public WaterTemperature(Environment env) {
			this.env = env;	
			env.createThread(this, "Water Temperature Thread");
		}
		
		public void run() {
			while(!Clock.clockDone()) {
				synchronized(env) {
					try {
						env.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("In Water Temperature Thread");
					if (env.airTemperature > env.waterTemperature) {
						this.increase(this.rate);
					} else if (env.airTemperature < env.waterTemperature) {
						this.increase(- this.rate);
					}
					writer.writeToFile("Water Temperature:" + String.valueOf(env.getWaterTemperature()) + ",");
					System.out.println("Water Temperature data written to file.");
				}
			}
		}
		
		private synchronized void increase (int rate) {
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
		public static final float SMALL_FISH_MAX_DO = 6; 
		public static final float MEDIUM_FISH_MAX_DO = 10;
		public static final float LARGE_FISH_MAX_DO = 15;
		
		public static final int DEATH_RATE = 1; // fish per hour
		
		public static final int SMALL_FISH_MASS = 100; // mass is relative to decomposer mass
		public static final int MEDIUM_FISH_MASS = 500;
		public static final int LARGE_FISH_MASS = 1000;
		
		public static final float SMALL_FISH_RESPIRATION_RATE = (float) 0.05;
		public static final float MEDIUM_FISH_RESPIRATION_RATE = (float) 0.2;
		public static final float LARGE_FISH_RESPIRATION_RATE = (float) 0.5;
		
		
		public Fish(Environment env) {
			this.env = env;
			env.createThread(this, "Fish Thread");
		}
		
		public void run() {
			while(!Clock.clockDone()) {
				synchronized(env) {
					try {
						env.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("In Fish Thread");
					if ((env.dissolvedOxygen < Fish.SMALL_FISH_MIN_DO || 
							env.dissolvedOxygen > Fish.SMALL_FISH_MAX_DO
							|| env.pHchangeInDay > 1.5) && env.smallFishNum > 0) {
						env.smallFishNum -= Fish.DEATH_RATE;
						env.deadOrganismMass += Fish.SMALL_FISH_MASS * Fish.DEATH_RATE;
					} 
					if (env.dissolvedOxygen == 0 && env.smallFishNum > 0) {
						env.smallFishNum = 0;
						env.deadOrganismMass += Fish.SMALL_FISH_MASS * env.smallFishNum;
					}
					if (env.smallFishNum > 0 && env.pHchangeInDay < 1.5) {
						env.dissolvedCO2 += Fish.SMALL_FISH_RESPIRATION_RATE * env.smallFishNum;
						env.dissolvedOxygen -= Fish.SMALL_FISH_RESPIRATION_RATE * env.smallFishNum;
					} 
					if ((env.dissolvedOxygen < Fish.MEDIUM_FISH_MIN_DO || 
							env.dissolvedOxygen > Fish.MEDIUM_FISH_MAX_DO
							|| env.pHchangeInDay > 1.5) && env.mediumFishNum > 0) {
						env.mediumFishNum -= Fish.DEATH_RATE;
						env.deadOrganismMass += Fish.MEDIUM_FISH_MASS * Fish.DEATH_RATE;
					} 
					if (env.dissolvedOxygen == 0 && env.mediumFishNum > 0) {
						env.mediumFishNum = 0;
						env.deadOrganismMass += Fish.MEDIUM_FISH_MASS * env.mediumFishNum;
					}
					if (env.mediumFishNum > 0 && env.pHchangeInDay < 1.5) {
						env.dissolvedCO2 += Fish.MEDIUM_FISH_RESPIRATION_RATE * env.mediumFishNum;
						env.dissolvedOxygen -= Fish.MEDIUM_FISH_RESPIRATION_RATE * env.mediumFishNum;
					} 
					if ((env.dissolvedOxygen < Fish.LARGE_FISH_MIN_DO || 
							env.dissolvedOxygen > Fish.LARGE_FISH_MAX_DO
							|| env.pHchangeInDay > 1.5) && env.largeFishNum > 0) {
						env.largeFishNum -= Fish.DEATH_RATE;
						env.deadOrganismMass += Fish.LARGE_FISH_MASS * Fish.DEATH_RATE;
					} 
					if (env.dissolvedOxygen == 0 && env.largeFishNum > 0) {
						env.largeFishNum = 0;
						env.deadOrganismMass += Fish.LARGE_FISH_MASS * env.largeFishNum;
					}
					if (env.largeFishNum > 0 && env.pHchangeInDay < 1.5) {
						env.dissolvedCO2 += Fish.LARGE_FISH_RESPIRATION_RATE * env.largeFishNum;
						env.dissolvedOxygen -= Fish.LARGE_FISH_RESPIRATION_RATE * env.largeFishNum;
					} 
					writer.writeToFile("Small Fish:" + String.valueOf(env.getSmallFishNum()) + "," 
					+ "Medium Fish:" + env.getMediumFishNum() + ","
					+ "Large Fish:" + env.getLargeFishNum() + ",");
					System.out.println("Fish data written to file.");
				}
			}
		}
		
	}
	
	private class Decomposers implements Runnable {
		
		private Environment env;
		public static final float DO = 1;
		public static final int DEATH_RATE = 5; // decomposers per hour
		public static final int MASS = 1;
		public static final float RESPIRATION_RATE = (float) 0.0025; // of a plant per hour
		
		public Decomposers(Environment env) {
			this.env = env;
			env.createThread(this, "Decomposers Thread");
		}
		
		public void run() {
			while(!Clock.clockDone()) {
				synchronized(env) {
					try {
						env.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("In Decomposers Thread");
					if (env.dissolvedOxygen < Decomposers.DO && env.decomposersNum > 0) {
						env.decomposersNum -= Decomposers.DEATH_RATE;
						env.deadOrganismMass += Decomposers.MASS * Decomposers.DEATH_RATE;
					} if (env.dissolvedOxygen == 0 && env.decomposersNum > 0) {
						env.decomposersNum = 0;
						env.deadOrganismMass += Decomposers.MASS * env.decomposersNum;
					}if (env.deadOrganismMass < 0 && env.decomposersNum > 0) {
						env.deadOrganismMass -= env.decomposersNum; // each decomposer reduces 
																// dead mass by one unit per hour
						env.dissolvedCO2 += Decomposers.RESPIRATION_RATE * env.decomposersNum;
						env.dissolvedOxygen -= Decomposers.RESPIRATION_RATE * env.decomposersNum;					
					}
					writer.writeToFile("Decomposers:" + String.valueOf(env.getDecomposersNum()) + ",");
					System.out.println("Decomposers data written to file.");
				}
			}
		}
	}
	
	private class Plants implements Runnable {
		
		private Environment env;
		public static final int DEATH_RATE = 1; // plants per hour
		public static final int MASS = 150;
		public static final float PHOTOSYNTHESIS_RATE = (float) 0.1; // O2 produced per hour per plant
		public static final float CO2_FRACTION_REQ = (float) 0.2;
		
		public Plants(Environment env) {
			this.env = env;
			env.createThread(this, "Plants Thread");
		}
		
		public void run() {
			while(!Clock.clockDone()) {
				synchronized(env) {
					try {
						env.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("In Plants Thread");
					if ((env.dissolvedCO2 < Plants.CO2_FRACTION_REQ
							|| env.pHchangeInDay > 2) && env.plantNum > 0) {
						env.plantNum -= Plants.DEATH_RATE;
						env.deadOrganismMass += Plants.MASS * Plants.DEATH_RATE;
					} 
					if (env.dissolvedCO2 == 0 && env.plantNum > 0) {
						env.plantNum = 0;
						env.deadOrganismMass += Plants.MASS * env.plantNum;
					}
					if (env.plantNum > 0 && env.pHchangeInDay < 2) {
						env.dissolvedOxygen += Plants.PHOTOSYNTHESIS_RATE * env.plantNum;
					}
					writer.writeToFile(String.valueOf("Plants:" + env.getPlantNum()) + "," 
							+ "Oxygen:" + env.getDissolvedOxygen() + "," 
							+ "CO2:" + env.getdissolvedCO2() + ",");
					System.out.println("Plant data written to file.");
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
			while(!Clock.clockDone()) {
				synchronized(env) {
					try {
						env.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("In PH Thread");
					float reactionRate = 0;
					try {
						reactionRate = this.calcCO2ReactionRate();
					} catch (UnlikelyPHException e) {
						e.printStackTrace();
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
					writer.writeToFile("PH:" + String.valueOf(env.getpH()) + ",");
					System.out.println("PH data written to file.");
				}
			}
		}
		
		private synchronized float calcTotalRespirationRate() {
			return (Decomposers.RESPIRATION_RATE * (float) env.decomposersNum) + 
					(Fish.SMALL_FISH_RESPIRATION_RATE * (float) env.smallFishNum) + 
					(Fish.MEDIUM_FISH_RESPIRATION_RATE * (float) env.mediumFishNum) +
					(Fish.LARGE_FISH_RESPIRATION_RATE * (float) env.largeFishNum);
		}
		
		private synchronized float calcTotalPhotosynthesisRate() {
			return Plants.PHOTOSYNTHESIS_RATE * env.plantNum;
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

	}
	
	private void createThread(Runnable obj, String threadName) {
		Thread t = new Thread(obj, threadName);
		t.start();
		System.out.println(t + " started");
	}

	public void stopThreads() throws InterruptedException {
		Environment.run = false;
		Thread.sleep(5000);
		writer.done();
		this.join();
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
	public synchronized float getdissolvedCO2() {
		return dissolvedCO2;
	}
	public void setdissolvedCO2(float dissolvedCO2) {
		this.dissolvedCO2 = dissolvedCO2;
	}
	public synchronized float getDissolvedOxygen() {
		return dissolvedOxygen;
	}
	public void setDissolvedOxygen(float dissolvedOxygen) {
		this.dissolvedOxygen = dissolvedOxygen;
	}
	public synchronized float getpH() {
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
