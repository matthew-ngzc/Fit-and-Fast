package com.fastnfit.app.enums;

public enum WorkoutGoal {
    GENERAL("general"),
    WEIGHT_LOSS("weight loss"),
    STRENGTH_BUILDING("strength"),
    FLEXIBILITY("flexibility"),
    STRESS_RELIEF("stress-relief"),
    PRENATAL("prenatal"),
    POST_PREGNANCY_RECOVERY("post-pregnancy");

    private final String value;

    WorkoutGoal(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static WorkoutGoal fromString(String value){
        for (WorkoutGoal goal:WorkoutGoal.values()){
            if (goal.getValue().equalsIgnoreCase(value)){
                return goal;
            }
        }
        throw new IllegalArgumentException("Unexpected value:"+value);
    }
}
