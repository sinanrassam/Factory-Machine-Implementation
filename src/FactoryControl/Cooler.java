/*
 * A class that represents a factory machine cooler.
 */
package FactoryControl;

/**
 *
 * @author sinan
 */
public interface Cooler {
    public final int DANGER_ZONE = 50;
    
    public int getCoolingFactor();
    
    public boolean isConnectedToMachine();
}
