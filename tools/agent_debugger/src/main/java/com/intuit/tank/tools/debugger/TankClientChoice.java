package com.intuit.tank.tools.debugger;
/**
 * 
 */
public class TankClientChoice {

    private String name;
    private String clientClass;
    /**
     * @param name
     * @param clientClass
     */
    public TankClientChoice(String name, String clientClass) {
        super();
        this.name = name;
        this.clientClass = clientClass;
    }
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @return the clientClass
     */
    public String getClientClass() {
        return clientClass;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
