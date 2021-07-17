package com.example.mom.Module;

public class Events implements  Comparable<Events>{
    private String amount, billID, uniqueID, unit, from;
    private boolean earnings;
    private long time;
    private int groupID;

    public Events() {
    }

    public Events(String amount, String billID, String uniqueID, String unit, String from, boolean earnings, long time, int groupID) {
        this.amount = amount;
        this.billID = billID;
        this.uniqueID = uniqueID;
        this.unit = unit;
        this.from = from;
        this.earnings = earnings;
        this.time = time;
        this.groupID = groupID;
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

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public boolean isEarnings() {
        return earnings;
    }

    public void setEarnings(boolean earnings) {
        this.earnings = earnings;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    @Override
    public int compareTo(Events o) {
        if (getTime() == 0 || o.getTime() == 0) {
            return 0;
        }
        return Long.valueOf(getTime()).compareTo(Long.valueOf(o.getTime()));
    }
}
