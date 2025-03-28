package com.fastnfit.app.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum WorkoutType {
    LOW_IMPACT("low-impact"),
    OTHERS("others"),
    PRENATAL("prenatal"),
    POSTNATAL("postnatal"),
    Yoga("yoga"),
    HIIT("HIIT"),
    STRENGTH("strength"),
    BODY_WEIGHT("body-weight");

    private final String value;

    WorkoutType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static WorkoutType fromString(String value){
        for (WorkoutType type:WorkoutType.values()){
            if (type.getValue().equalsIgnoreCase(value)){
                return type;
            }
        }
        throw new IllegalArgumentException("Unexpected value:"+value);
    }
}