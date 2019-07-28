package ru.plasticworld.clientapp.data;

public class ParametersData {

    private String paramName;
    private String parameter;
    private String deviceName;

    public ParametersData(String paramName, String parameter, String deviceName) {
        this.paramName = paramName;
        this.parameter = parameter;
        this.deviceName = deviceName;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
