package com.SimpleVitals;

public enum SVAlerts {
    OFF("Off"),
    ON("On"),;

    private final String name;

    SVAlerts(String name){
        this.name = name;
    }

    @Override
    public String toString(){
        return name;
    }
}