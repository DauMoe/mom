package com.example.mom.Module;

import java.io.Serializable;

public class User implements Serializable {
    private String username, email, phone, unit, uniqueID, pin, address, gender, dob;
    private long amount;

    public User() {
    }

    public User(String email, String phone, String unit, String uniqueID, String pin, long amount) {
        this.email = email;
        this.phone = phone;
        this.unit = unit;
        this.uniqueID = uniqueID;
        this.pin = pin;
        this.amount = amount;
    }

    public User(String username, String email, String phone, String unit, String uniqueID, String PIN, String address, String gender, String dob, long amount) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.unit = unit;
        this.uniqueID = uniqueID;
        this.pin = PIN;
        this.address = address;
        this.gender = gender;
        this.dob = dob;
        this.amount = amount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getPIN() {
        return pin;
    }

    public void setPIN(String PIN) {
        this.pin = PIN;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}
