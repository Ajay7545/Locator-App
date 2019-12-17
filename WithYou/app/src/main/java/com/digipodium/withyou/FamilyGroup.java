package com.digipodium.withyou;

public class FamilyGroup {
    public String username;
    public String email;
    public double lat, lng;
    public String group;

    public FamilyGroup() {
    }

    public FamilyGroup(String username, String email, double lat, double lng, String group) {
        this.username = username;
        this.email = email;
        this.lat = lat;
        this.lng = lng;
        this.group = group;
    }
}
