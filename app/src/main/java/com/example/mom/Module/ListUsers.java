package com.example.mom.Module;

public class ListUsers {
    private Boolean editable;
    private String ID;

    public ListUsers() {
    }

    public ListUsers(Boolean editable, String ID) {
        this.editable = editable;
        this.ID = ID;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
