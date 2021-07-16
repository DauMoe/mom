package com.example.mom.Modules;

public class User {
    private int ID, GroupID;
    private String username, email, phone, amount, ava_uri;

    public User(int ID, int groupID, String username, String email, String phone, String amount, String ava_uri) {
        this.ID = ID;
        GroupID = groupID;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.amount = amount;
        this.ava_uri = ava_uri;
    }
}
