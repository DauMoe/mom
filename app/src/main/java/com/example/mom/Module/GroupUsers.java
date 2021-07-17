package com.example.mom.Module;

public class GroupUsers {
    private String[] list_user, editable, viewonly;
    private String name;

    public GroupUsers() {
    }

    public GroupUsers(String[] list_user, String[] editable, String[] viewonly, String name) {
        this.list_user = list_user;
        this.editable = editable;
        this.viewonly = viewonly;
        this.name = name;
    }

    public String[] getList_user() {
        return list_user;
    }

    public void setList_user(String[] list_user) {
        this.list_user = list_user;
    }

    public String[] getEditable() {
        return editable;
    }

    public void setEditable(String[] editable) {
        this.editable = editable;
    }

    public String[] getViewonly() {
        return viewonly;
    }

    public void setViewonly(String[] viewonly) {
        this.viewonly = viewonly;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
