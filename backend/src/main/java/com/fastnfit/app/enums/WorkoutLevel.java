package com.fastnfit.app.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum WorkoutLevel{
    Beginner,Intermediate,Advanced,All_Levels;
}