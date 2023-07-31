package com.techvg.inventory.management.domain;

import com.techvg.inventory.management.service.dto.SecurityUserDTO;

public class SalesByUser implements Comparable<SalesByUser> {

    public SecurityUserDTO securityUser;
    public long totalQuoate;
    public long convertedClients;

    @Override
    public int compareTo(SalesByUser o) {
        long compareQuantity = ((SalesByUser) o).totalQuoate;

        // ascending order
        return (int) (this.totalQuoate - compareQuantity);
    }
}
