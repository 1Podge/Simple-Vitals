package com.SimpleVitals;

public enum SVSize {
    SMALL("Small", 14),
    MEDIUM("Medium", 28),
    LARGE("Large", 56);

    private final String name;
    private final int size;

    SVSize(String name, int size){
        this.name = name;
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString(){
        return name;
    }
}