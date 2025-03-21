package com.fastnfit.app.enums;

public enum WorkoutType {
    HIGH_ENERGY("high-energy"),
    LOW_IMPACT("low-impact");

    private final String value;

    WorkoutType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}