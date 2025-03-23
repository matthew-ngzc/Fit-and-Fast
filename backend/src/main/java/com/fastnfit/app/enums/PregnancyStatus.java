package com.fastnfit.app.enums;

// Enums for type safety
public enum PregnancyStatus {
    NO("NO"),
    PREGNANT("YES, PREGNANT"),
    POSTPARTUM("YES POSTPARTUM");

    private final String value;

    PregnancyStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PregnancyStatus fromValue(String value) {
        for (PregnancyStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid pregnancy status: " + value);
    }
}
