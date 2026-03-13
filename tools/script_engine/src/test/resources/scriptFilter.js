//jsCombinedFilter_20171010.js

try{
    load("nashorn:mozilla_compat.js");
}catch(e){}


importPackage(com.intuit.tank.script.models);

var TurboScaleScript = ioBean.getInput("script");

steps = TurboScaleScript.getSteps().toArray();
ioBean.println("Number of Steps: " + TurboScaleScript.getSteps().size());
