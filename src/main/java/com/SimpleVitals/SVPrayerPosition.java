package com.SimpleVitals;

public enum SVPrayerPosition {
    LEFT("Left"),
    RIGHT("Right"),
    TOP("Top"),
    BOTTOM("Bottom"),
    OFF("Off");

    private final String name;

    SVPrayerPosition(String name){
        this.name = name;
    }

    @Override
    public String toString(){
        return name;
    }
}
