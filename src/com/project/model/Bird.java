package com.project.model;

public class Bird extends Animal {
    private double wingSpan;

    public Bird() {}

    public double getWingSpan()      { return wingSpan; }
    public void setWingSpan(double v){ this.wingSpan = v; }

    @Override
    public String getCategory() {
        return "Bird";
    }

    @Override
    public String getSpecialCharacteristic() {
        return "Wing Span: " + wingSpan + "m";
    }
}
