package com.techvg.inventory.management.domain.enumeration;

/**
 * The OrderType enumeration.
 */
public enum OrderType {
    PURCHASE_ORDER("PURCHASE_ORDER"),
    PRODUCT_QUATATION("PRODUCT_QUATATION"),
    TAX_INVOICE("TAX_INVOICE"),
    PO_REQUISITION("PO_REQUISITION");

    private final String value;

    OrderType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
