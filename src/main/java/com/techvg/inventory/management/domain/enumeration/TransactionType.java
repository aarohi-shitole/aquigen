package com.techvg.inventory.management.domain.enumeration;

/**
 * The TransactionType enumeration.
 */
public enum TransactionType {
    OUTWARDS_CONSUMPTION("Outward_Consumption"),
    INWARD_STOCKS("Inward_Stocks"),
    TRANSFER_STOCKS_INWARD("Transfer_Stocks_Inward"),
    TRANSFER_STOCKS_OUTWARDS("Transfer_Stocks_Outward"),
    PRODUCT_CONSUMPTION(" PRODUCT_CONSUMPTION"),
    OTHER("Other");

    private final String value;

    TransactionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
