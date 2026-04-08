package com.project.model;

public class Reptile extends Animal {
    
    public Reptile() {}

    @Override
    public String getCategory() {
        return "Reptile";
    }

    @Override
    public String getSpecialCharacteristic() {
        return "Cold-blooded";
    }
}
