package com.SimpleVitals;

public enum SVIdleHold {
    ACTIVE_PRAYER("Active prayers"),
    PRAYER_COUNTER("Prayer count"),
    ACTIVE_AND_COUNTER("Active & count"),
    WHOLE_HUD("Whole HUD"),
    NONE("None");

    private final String name;

    SVIdleHold(String name){
        this.name = name;
    }

    @Override
    public String toString(){
        return name;
    }
}
