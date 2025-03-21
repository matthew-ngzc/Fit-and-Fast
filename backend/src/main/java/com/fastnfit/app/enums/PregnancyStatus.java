package com.fastnfit.app.enums;

// Enums for type safety
public enum PregnancyStatus {
    NO("no"),
    PREGNANT("yes, pregnant"),
    POSTPARTUM("yes postpartum");

    private final String value;

    PregnancyStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PregnancyStatus fromValue(String value) {
        for (PregnancyStatus status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid pregnancy status: " + value);
    }
}
