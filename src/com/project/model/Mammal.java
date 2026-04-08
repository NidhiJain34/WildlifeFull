package com.project.model;

public class Mammal extends Animal {
    private String furType;

    public Mammal() {}

    public String getFurType()       { return furType; }
    public void setFurType(String v) { this.furType = v; }

    @Override
    public String getCategory() {
        return "Mammal";
    }

    @Override
    public String getSpecialCharacteristic() {
        return "Fur Type: " + (furType != null && !furType.isEmpty() ? furType : "Unknown");
    }
}
