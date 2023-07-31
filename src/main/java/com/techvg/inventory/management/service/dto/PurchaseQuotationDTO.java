package com.techvg.inventory.management.service.dto;

import com.techvg.inventory.management.domain.enumeration.OrderType;
import com.techvg.inventory.management.domain.enumeration.Status;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A DTO for the
 * {@link com.techvg.inventory.management.domain.PurchaseQuotation} entity.
 */
public class PurchaseQuotationDTO implements Serializable {

    private Long id;

    private String refrenceNumber;

    private Double totalPOAmount;

    private Double totalGSTAmount;

    private Instant expectedDeliveryDate;

    private Instant poDate;

    private OrderType orderType;

    private Status orderStatus;

    private Boolean isPayment;

    private Long invoiceRefrence;

    private String termsAndCondition;

    private String notes;

    private String lastModified;

    private String lastModifiedBy;

    private String freeField1;

    private String freeField2;

    private Double discount;

    private String currency;

    private String showStructure;

    private String kindAttention;

    private String freightCharges;

    private String poFileName;

    private SecurityUserDTO securityUser;

    private SecurityUserDTO approvalId;

    private ProjectDTO project;

    private ClientDetailsDTO clientDetails;

    private List<PurchaseQuotationDetailsDTO> purchaseQuotationDetails = new ArrayList<PurchaseQuotationDetailsDTO>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRefrenceNumber() {
        return refrenceNumber;
    }

    public void setRefrenceNumber(String refrenceNumber) {
        this.refrenceNumber = refrenceNumber;
    }

    public Double getTotalPOAmount() {
        return totalPOAmount;
    }

    public void setTotalPOAmount(Double totalPOAmount) {
        this.totalPOAmount = totalPOAmount;
    }

    public Double getTotalGSTAmount() {
        return totalGSTAmount;
    }

    public void setTotalGSTAmount(Double totalGSTAmount) {
        this.totalGSTAmount = totalGSTAmount;
    }

    public Instant getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(Instant expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public Instant getPoDate() {
        return poDate;
    }

    public void setPoDate(Instant poDate) {
        this.poDate = poDate;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public Status getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Status orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getTermsAndCondition() {
        return termsAndCondition;
    }

    public void setTermsAndCondition(String termsAndCondition) {
        this.termsAndCondition = termsAndCondition;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getShowStructure() {
        return showStructure;
    }

    public void setShowStructure(String showStructure) {
        this.showStructure = showStructure;
    }

    public String getKindAttention() {
        return kindAttention;
    }

    public void setKindAttention(String kindAttention) {
        this.kindAttention = kindAttention;
    }

    public String getFreightCharges() {
        return freightCharges;
    }

    public void setFreightCharges(String freightCharges) {
        this.freightCharges = freightCharges;
    }

    public SecurityUserDTO getSecurityUser() {
        return securityUser;
    }

    public void setSecurityUser(SecurityUserDTO securityUser) {
        this.securityUser = securityUser;
    }

    public ProjectDTO getProject() {
        return project;
    }

    public void setProject(ProjectDTO project) {
        this.project = project;
    }

    public ClientDetailsDTO getClientDetails() {
        return clientDetails;
    }

    public void setClientDetails(ClientDetailsDTO clientDetails) {
        this.clientDetails = clientDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PurchaseQuotationDTO)) {
            return false;
        }

        PurchaseQuotationDTO purchaseQuotationDTO = (PurchaseQuotationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, purchaseQuotationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
	@Override
	public String toString() {
		return "PurchaseQuotationDTO{" + "id=" + getId() + "invoiceRefrence=" + getInvoiceRefrence() +
				", refrenceNumber='" + getRefrenceNumber() + "'"
				+ ", totalPOAmount=" + getTotalPOAmount() + ", totalGSTAmount=" + getTotalGSTAmount()
				+ ", expectedDeliveryDate='" + getExpectedDeliveryDate() + "'" + ", poDate='" + getPoDate() + "'"
				+ ", orderType='" + getOrderType() + "'" + ", orderStatus='" + getOrderStatus() + "'"
				+  ", isPayment='" + getIsPayment() + "'" +
				", termsAndCondition='" + getTermsAndCondition() + "'" + ", notes='" + getNotes() + "'"
				+ ", lastModified='" + getLastModified() + "'" + ", lastModifiedBy='" + getLastModifiedBy() + "'"
				+ ", freeField1='" + getFreeField1() + "'" + ", freeField2='" + getFreeField2() + "'"
				+ ", securityUser=" + getSecurityUser() + ", approvalId=" + getApprovalId() +
				", project=" + getProject() + ", clientDetails="
				+ getClientDetails() + ", discount=" + getDiscount() + ", currency='" + getCurrency() + "'"
				+ ", showStructure='" + getShowStructure() + "'" + ", kindAttention='" + getKindAttention() + "'"
				+ ", poFileName='" + getPoFileName() + "'" 
				+ ", freightCharges='" + getFreightCharges() + "'" + "}";
	}

    public List<PurchaseQuotationDetailsDTO> getPurchaseQuotationDetails() {
        return purchaseQuotationDetails;
    }

    public void setPurchaseQuotationDetails(List<PurchaseQuotationDetailsDTO> purchaseQuotationDetails) {
        this.purchaseQuotationDetails = purchaseQuotationDetails;
    }

    public String getPoFileName() {
        return poFileName;
    }

    public void setPoFileName(String poFileName) {
        this.poFileName = poFileName;
    }

    public Boolean getIsPayment() {
        return isPayment;
    }

    public void setIsPayment(Boolean isPayment) {
        this.isPayment = isPayment;
    }

    public Long getInvoiceRefrence() {
        return invoiceRefrence;
    }

    public void setInvoiceRefrence(Long invoiceRefrence) {
        this.invoiceRefrence = invoiceRefrence;
    }

    public SecurityUserDTO getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(SecurityUserDTO approvalId) {
        this.approvalId = approvalId;
    }
}
