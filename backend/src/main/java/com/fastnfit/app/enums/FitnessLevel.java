package com.fastnfit.app.enums;

public enum FitnessLevel {
    BEGINNER("BEGINNER"), INTERMEDIATE("INTERMEDIATE"), ADVANCED("ADVANCED");

    private final String value;

    FitnessLevel(String value){
        this.value=value;
    }

    public static FitnessLevel fromValue(String value) {
        for (FitnessLevel level : values()) {
            if (level.value.equals(value.toUpperCase())) {
                return level;
            }
        }
        throw new IllegalArgumentException("Invalid fitness level: " + value);
    }
}
