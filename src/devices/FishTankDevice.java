package devices;

public interface FishTankDevice extends Runnable {
	
	public void run();
	
	public boolean isRunning();
	
	public String getID();
	
	public String getName();
	
	public String getManufacturer();
	
	public String getModel();
	
	public String getErrorLogFile();
	
}
