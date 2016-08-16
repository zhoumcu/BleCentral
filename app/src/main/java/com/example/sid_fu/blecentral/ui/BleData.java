package com.example.sid_fu.blecentral.ui;

/**
 * Created by Administrator on 2016/4/14.
 */
public class BleData {
    public int temp = 0;
    public float press = 0;
    public double sensorFailure = 0;
    public double sensorLow = 0;
    public double leakage = 0;
    public double leakageQucik = 0;
    private int status;
    private float voltage;

    public double getSensorLow() {
        return sensorLow;
    }

    public void setSensorLow(double sensorLow) {
        this.sensorLow = sensorLow;
    }

    public double getLeakageQucik() {
        return leakageQucik;
    }

    public void setLeakageQucik(double leakageQucik) {
        this.leakageQucik = leakageQucik;
    }

    public float getPress() {
        return press;
    }

    public void setPress(float press) {
        this.press = press;
    }

    public double getLeakage() {
        return leakage;
    }

    public void setLeakage(double leakage) {
        this.leakage = leakage;
    }

    public double getSensorFailure() {
        return sensorFailure;
    }

    public void setSensorFailure(double sensorFailure) {
        this.sensorFailure = sensorFailure;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setVoltage(float voltage) {
        this.voltage = voltage;
    }

    public float getVoltage() {
        return voltage;
    }
}
