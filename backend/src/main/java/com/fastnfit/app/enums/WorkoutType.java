package com.fastnfit.app.enums;

public enum WorkoutType {
    HIGH_ENERGY("high-energy"),
    LOW_IMPACT("low-impact"),
    OTHERS("others"),
    PRENATAL("prenatal"),
    POSTNATAL("postnatal"),
    Yoga("yoga"),
    HIIT("HIIT"),
    STRENGTH("Strength");

    private final String value;

    WorkoutType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static WorkoutType fromString(String value){
        for (WorkoutType type:WorkoutType.values()){
            if (type.getValue().equalsIgnoreCase(value)){
                return type;
            }
        }
        throw new IllegalArgumentException("Unexpected value:"+value);
    }
}