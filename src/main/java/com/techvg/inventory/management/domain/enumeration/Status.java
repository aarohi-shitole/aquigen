package com.techvg.inventory.management.domain.enumeration;

/**
 * The Status enumeration.
 */
public enum Status {
    REQUESTED("REQUESTED"),
    APPROVED("APPROVED"),
    CANCELED("CANCELED"),
    RECEIVED("RECEIVED"),
    COMPLETED("COMPLETED");

    private final String value;

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
