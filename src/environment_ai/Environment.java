package environment_ai;

import java.util.Calendar;

public class Environment {
	
	private int hour = 0; // 24-hour format
	private int airTemperature = 0; // in Celsius by default
	private int waterTemperature = 0; // in Celsius by default
	private int timeSpeed = 1; // ex: x2, x3, x10, where 0 is time stopped
	private float CO2fraction = (float) 0.0;
	private float O2fraction = (float) 0.0;
	private float pH = (float) 7.0;
	private int plantNum = 0;
	private int fishNum= 0;
	private int reptileNum = 0;
	private Clock clock;
	private AirTemperature airTemp;

	public Environment(int hour, int airTemperature, int waterTemperature, int timeSpeed, float cO2fraction,
			float o2fraction, float pH, int plantNum, int fishNum, int reptileNum) {
		super();
		this.hour = hour;
		this.airTemperature = airTemperature;
		this.waterTemperature = waterTemperature;
		this.timeSpeed = timeSpeed;
		CO2fraction = cO2fraction;
		O2fraction = o2fraction;
		this.pH = pH;
		this.plantNum = plantNum;
		this.fishNum = fishNum;
		this.reptileNum = reptileNum;
	}
	
	public Environment() {
		this.clock = new Clock(this);
		this.airTemp = new AirTemperature(this);
	}
	
	private class Clock implements Runnable {
		
		public static final int MILLISEC = 216000;
		protected int interval;
		private Environment env;
		private Calendar cal = Calendar.getInstance();
		
		public Clock(Environment env) {
			this(1);
			this.env = env;
		}
		
		public Clock(final int timeSpeed) {
			setHour();
			env.setTimeSpeed(timeSpeed);
			setINTERVAL();
			createThread();
		}
		
		public Clock(final int timeSpeed, final int hour) {
			setHour(hour);
			env.setTimeSpeed(timeSpeed);
			setINTERVAL();
			createThread();
		}
		
		private void createThread() {
			Thread t = new Thread(this, "Clock Thread");
			t.start();
			System.out.println(t + " started");
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
			createThread();
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
		
		private void createThread() {
			Thread t = new Thread(this, "Air Temperature Thread");
			t.start();
			System.out.println(t + " started");
		}
	}
	
	private class WaterTemperature implements Runnable {
		
		private Environment env;
		
		public WaterTemperature(Environment env) {
			this.env = env;	
			createThread();
		}
		
		public void run() {
			synchronized(env) {
				while (true) {
					
				}
			}
		}
		
		private void createThread() {
			Thread t = new Thread(this, "Water Temperature Thread");
			t.start();
			System.out.println(t + " started");
		}
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
	public float getO2fraction() {
		return O2fraction;
	}
	public void setO2fraction(float o2fraction) {
		O2fraction = o2fraction;
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
	public int getReptileNum() {
		return reptileNum;
	}
	public void setReptileNum(int reptileNum) {
		this.reptileNum = reptileNum;
	}
	
}
