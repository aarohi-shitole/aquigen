package com.techvg.inventory.management.domain.enumeration;

/**
 * The NotificationType enumeration.
 */
public enum NotificationType {
    APPROVAL("Apporoval"),
    REQUEST("Request"),
    ALERT("Alert"),
    TRANSFER("Transfer"),
    QUOTATION_REMINDER("Reminder");

    private final String value;

    NotificationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
