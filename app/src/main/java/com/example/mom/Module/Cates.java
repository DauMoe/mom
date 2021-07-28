package com.example.mom.Module;

public class Cates {
    private String name, uniqueID;

    public Cates() {
    }

    public Cates(String name, String uniqueID) {
        this.name = name;
        this.uniqueID = uniqueID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }
}
