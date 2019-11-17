/*
 * A class that implements both the Cooler interface and the Runnable interface,
 * representing a cooler for monitoring a collection of machines.
*/
package FactoryControl;

/**
 *
 * @author sinan
 */
public class MonitoringCooler implements Cooler, Runnable {
    
    private int coolingFactor;
    private Machine[] machines;
    private Machine connectedMachine;
    private boolean isRunning;
    
    public MonitoringCooler(Machine[] machines, int coolingFactor) {
        this.machines = machines;
        this.coolingFactor = coolingFactor;
        isRunning = false;
        connectedMachine = null;
    }
    
    @Override
    public int getCoolingFactor() {
        return coolingFactor;
    }
    
    public void stopCooler() {
        isRunning = false;
    }
    
    public void startCooler() {
        isRunning = true;
    }
    
    public synchronized boolean isRunning() {
        return isRunning;
    }
    
    @Override
    public synchronized boolean isConnectedToMachine() {
        return (connectedMachine != null);
    }
    
    public synchronized boolean connectToMachine(Machine machine) {
        connectedMachine = machine;
        return isConnectedToMachine();
    }
    
    public synchronized void disconnectFromMachine() {
        connectedMachine = null;
    }
    
    public synchronized Machine getConnectedMachine() {
        return connectedMachine;
    }
    
    @Override
    public void run() {
        startCooler();
        while (isRunning()) {
            for (Machine m : machines) {
                if (m.isRunning()) {
                    if (!m.isCoolerConnected() && !isConnectedToMachine()) {
                        if (m.getCurrentTemp() >= m.getMaxTemp() - DANGER_ZONE) {
                            m.connectCooler(this);
                            connectedMachine = m;
                            System.out.println("Cooler is connected");
                        }
                    }
                    else if (getConnectedMachine() == m) {
                        if (m.getCurrentTemp() <= m.getMinTemp() + DANGER_ZONE) {
                            m.disconnectCooler();
                            connectedMachine = null;
                            System.out.println("Cooler has been disconnected");
                        }
                    }
                }
                if (!m.isRunning() && getConnectedMachine()== m) {
                    m.disconnectCooler();
                    connectedMachine = null;
                    System.out.println("Cooler has been disconnected");
                }
            }
            try {Thread.sleep(10);}
            catch (InterruptedException e){}
        }
    }
}
