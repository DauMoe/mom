package com.example.mom.Module;

public class Bill {
    private int ID;
    private String company, name, amount;
    private Long time;

    public Bill(int ID, String company, String name, String amount, Long time) {
        this.ID = ID;
        this.company = company;
        this.name = name;
        this.amount = amount;
        this.time = time;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
