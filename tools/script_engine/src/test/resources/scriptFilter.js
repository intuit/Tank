
try{
    load("nashorn:mozilla_compat.js");
}catch(e){}


importPackage(com.intuit.tank.script.models);

var TankScript = ioBean.getInput("script");

steps = TankScript.getSteps().toArray();
ioBean.println("Number of Steps: " + TankScript.getSteps().size());
