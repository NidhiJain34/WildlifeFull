package com.project.model;

import java.sql.Date;

public abstract class Animal {
    private int    id;
    private String name;
    private Date   dob;
    private int    age;
    private int    speciesId;
    private String speciesName;

    // Abstract method showcasing polymorphism
    public abstract String getSpecialCharacteristic();
    
    // Derived class will return its category type
    public abstract String getCategory();

    public Animal() {}

    public int    getId()            { return id; }
    public String getName()          { return name; }
    public Date   getDob()           { return dob; }
    public int    getAge()           { return age; }
    public int    getSpeciesId()     { return speciesId; }
    public String getSpeciesName()   { return speciesName; }

    public void setId(int v)           { this.id = v; }
    public void setName(String v)      { this.name = v; }
    public void setDob(Date v)         { this.dob = v; }
    public void setAge(int v)          { this.age = v; }
    public void setSpeciesId(int v)    { this.speciesId = v; }
    public void setSpeciesName(String v){ this.speciesName = v; }
}
