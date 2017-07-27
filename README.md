# kura-gateway-simulator-FishTank

To run the simulation with default initial values, run FishTank.java as a java application, which is located in the src/main/java/fishtank/main directory. This will run the Environment and the Devices parts of the simulation and print devices' output to the console. Output from the Environment can be seen in envData.txt. Output from each device is written to their own respective .json file (for example, output from an AirThermometer object with id=1 will be printed to a file named air-thermometer-1-data.json). Application logs are all in Log.log. All files produces by the application are located in the src/main/resources directory. 

First time running the application will create a configuration.json and a devices.json files, which will contain the default values of environment and devices objects. You can change these values and create new devices objects by altering these files. To create a new device, use the following structure:
	,
    {
      "id": "",
      "name": "",
      "manufacturer": "",
      "model": "",
      "type": "*.class"
    }
    
*Devices may only be of type AirThemometer.class, Clock.class, CO2Meter.class, OxygenMeter.class, PHMeter.class, or WaterThermometer.class. 

The following run of the application will use these configurations to make the environment and device objects and run them. 

For Second Evaluation:

Goals set:

- Make devices that read data from Environment and implement FishTankDevice interface
- Make sure scheduling of devices and environment works
- Make a way for the user to provide initial inputs
- Make a main function which starts the application

Goals met:

- Made devices that implement FishTankDevice interface
- Changed scheduling with threads to scheduling with ScheduledExecutorService (different intervals for environment and devices possible)
- Made a configuration file for Environment initial inputs and a devices file for configuring devices
- Made a main function that starts the application
- Made a logger