package com.example.mom.Modules;

public class User {
    private String username, email, phone, amount, unit, uniqueID, GroupID;

    public User() {
    }

    public User(String username, String email, String phone, String amount, String unit, String uniqueID, String groupID) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.amount = amount;
        this.unit = unit;
        this.uniqueID = uniqueID;
        GroupID = groupID;
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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
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

    public String getGroupID() {
        return GroupID;
    }

    public void setGroupID(String groupID) {
        GroupID = groupID;
    }
}
