package com.example.mom.Module;

public class Bill {
    private String company, name, amount, billID;
    private Long time;

    public Bill(String company, String name, String amount, String billID, Long time) {
        this.company = company;
        this.name = name;
        this.amount = amount;
        this.billID = billID;
        this.time = time;
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

    public String getBillID() {
        return billID;
    }

    public void setBillID(String billID) {
        this.billID = billID;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
