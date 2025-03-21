package com.fastnfit.app.enums;

public enum WorkoutGoal {
    GENERAL_FITNESS("General Fitness"),
    WEIGHT_LOSS("Weight Loss"),
    STRENGTH_BUILDING("Strength Building"),
    FLEXIBILITY("Flexibility"),
    STRESS_RELIEF("Stress Relief"),
    PRENATAL("Prenatal"),
    POST_PREGNANCY_RECOVERY("Post-Pregnancy Recovery");

    private final String value;

    WorkoutGoal(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
