package com.techvg.inventory.management.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.techvg.inventory.management.domain.PurchaseQuotationDetails} entity.
 */
public class PurchaseQuotationDetailsDTO implements Serializable {

    private Long id;

    private Double qtyordered;

    private Integer gstTaxPercentage;

    private Double pricePerUnit;

    private Double totalPrice;

    private Double discount;

    private Double cgstNumber;

    private Double sgstNumber;

    private String lastModified;

    private String lastModifiedBy;

    private String freeField1;

    private String freeField2;

    private ProductDTO product;

    private Long purchaseQuotationId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getQtyordered() {
        return qtyordered;
    }

    public void setQtyordered(Double qtyordered) {
        this.qtyordered = qtyordered;
    }

    public Integer getGstTaxPercentage() {
        return gstTaxPercentage;
    }

    public void setGstTaxPercentage(Integer gstTaxPercentage) {
        this.gstTaxPercentage = gstTaxPercentage;
    }

    public Double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(Double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getFreeField1() {
        return freeField1;
    }

    public void setFreeField1(String freeField1) {
        this.freeField1 = freeField1;
    }

    public String getFreeField2() {
        return freeField2;
    }

    public void setFreeField2(String freeField2) {
        this.freeField2 = freeField2;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PurchaseQuotationDetailsDTO)) {
            return false;
        }

        return id != null && id.equals(((PurchaseQuotationDetailsDTO) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PurchaseQuotationDetailsDTO{" +
            "id=" + getId() +
            ", qtyordered=" + getQtyordered() +
            ", gstTaxPercentage=" + getGstTaxPercentage() +
            ", pricePerUnit=" + getPricePerUnit() +
            ", totalPrice=" + getTotalPrice() +
            ", discount=" + getDiscount() +
             ", cgstNumber=" + getCgstNumber() +
            ", sgstNumber=" + getSgstNumber() +
            ", lastModified='" + getLastModified() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", freeField1='" + getFreeField1() + "'" +
            ", freeField2='" + getFreeField2() + "'" +
            ", product=" + getProduct() +
            ", purchaseQuotationId=" + getPurchaseQuotationId() +
            "}";
    }

    public Long getPurchaseQuotationId() {
        return purchaseQuotationId;
    }

    public void setPurchaseQuotationId(Long purchaseQuotationId) {
        this.purchaseQuotationId = purchaseQuotationId;
    }

    public Double getCgstNumber() {
        return cgstNumber;
    }

    public void setCgstNumber(Double cgstNumber) {
        this.cgstNumber = cgstNumber;
    }

    public Double getSgstNumber() {
        return sgstNumber;
    }

    public void setSgstNumber(Double sgstNumber) {
        this.sgstNumber = sgstNumber;
    }
}
