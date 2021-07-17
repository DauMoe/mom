package com.example.mom.Module;

import java.io.Serializable;

public class Invoice implements Serializable {
    private String company, from, amount, billID, unit;
    private Long time, total;

    public Invoice(String company, String from, String amount, String billID, String unit, Long time, Long total) {
        this.company = company;
        this.from = from;
        this.amount = amount;
        this.billID = billID;
        this.unit = unit;
        this.time = time;
        this.total = total;
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
}
