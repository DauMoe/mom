package com.example.mom.Module;

import java.util.ArrayList;
import java.util.List;

public class GroupUsers {
    private String name, host;
    private List<String> members;

    public GroupUsers() {
    }

    public GroupUsers(String name, String host, List<String> members) {
        this.name = name;
        this.host = host;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }
}
