/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
