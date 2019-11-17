/*
 * A class that represents a factory machine.
 */
package FactoryControl;

import java.util.Random;

/**
 *
 * @author sinan
 */
public class Machine implements Runnable {
    
    private boolean isRunning;
    private int minTemp, maxTemp;
    private int currentTemp;
    public Cooler connectedCooler;
    
    public Machine(int minTemp, int maxTemp) {
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        currentTemp = 20;
        isRunning = false;
        connectedCooler = null;
    }
    
    public void startMachine() {
        if ((currentTemp <= minTemp) || (currentTemp >= maxTemp)) {
            System.out.println("Reseting Machine");
            currentTemp = 20;
        }
        isRunning = true;
    }
    
    public synchronized boolean isRunning() {
        return isRunning;
    }
    
    public synchronized void stopMachine() {
        isRunning = false;
    }
    
    public synchronized int getCurrentTemp() {
        return currentTemp;
    }
    
    public int getMinTemp() {
        return minTemp;
    }
    
    public int getMaxTemp() {
        return maxTemp;
    }
    
    public synchronized boolean connectCooler(Cooler cooler) {
        connectedCooler = cooler;
        return isCoolerConnected();
    }
    
    public synchronized boolean isCoolerConnected() {
        return connectedCooler != null;
    }
    
    public synchronized void disconnectCooler() {
        connectedCooler = null;
    }
    
    @Override
    public void run() {
        startMachine();
        Random rand = new Random();
        while (isRunning()) {
            if ((currentTemp >= maxTemp) || (currentTemp <= minTemp)) {
                stopMachine();
                System.out.println("Machine failed");
                throw new MachineTemperatureException((currentTemp <= minTemp)? "Machine too cold" : "Machine too hot");
            }
            if (!isCoolerConnected()) {
                currentTemp += rand.nextInt(6);
                System.out.println("A cooler has not connected to this machine yet");
            }
            else {
                currentTemp -= connectedCooler.getCoolingFactor();
                System.out.println("A cooler is connected to this machine");
            }
            
            try {Thread.sleep(200);}
            catch (InterruptedException e){}
        }
    }
    
    
    private class MachineTemperatureException extends RuntimeException {
        
        public MachineTemperatureException(String error) {
            super(error);
        }
    }
}
