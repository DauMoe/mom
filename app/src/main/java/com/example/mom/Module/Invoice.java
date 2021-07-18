package com.example.mom.Module;

import java.io.Serializable;

public class Invoice implements Serializable {
    private String company, from, billID, unit;
    private Long time, total, amount;

    public Invoice() {
    }

    public Invoice(String company, String from, String billID, String unit, Long time, Long total, Long amount) {
        this.company = company;
        this.from = from;
        this.billID = billID;
        this.unit = unit;
        this.time = time;
        this.total = total;
        this.amount = amount;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getBillID() {
        return billID;
    }

    public void setBillID(String billID) {
        this.billID = billID;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
