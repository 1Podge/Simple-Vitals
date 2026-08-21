package com.SimpleVitals;

public enum SVPosition {
    LEFT("Left"),
    RIGHT("Right"),
    OFF("Off");

    private final String name;

    SVPosition(String name){
        this.name = name;
    }
}