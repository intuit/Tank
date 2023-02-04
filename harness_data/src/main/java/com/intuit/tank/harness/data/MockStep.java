package com.intuit.tank.harness.data;

public class MockStep extends TestStep implements FailableStep {
    private String name = "";

    private String script = "";

    private String scriptGroupName = "";

    private transient String onFail = "";

    public String getName() {
        return name;
    }

    public void setOnFail(String onFail) { this.onFail = onFail; }

    public String getOnFail() {
        return onFail;
    }

    public void setScript(String script) { this.script = script;}

    public String getScript() {
        return script;
    }

    public String getScriptGroupName() {
        return scriptGroupName;
    }
    @Override
    public String getInfo() {
        return "Mock Step: " + name;
    }

}
