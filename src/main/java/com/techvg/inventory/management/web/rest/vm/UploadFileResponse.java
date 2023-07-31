package com.techvg.inventory.management.web.rest.vm;

public class UploadFileResponse {

    private String fileName;
    private Long productId;
    private Long purchaseQuotationId;

    //	  private  String dateOfCreation;

    public Long getPurchaseQuotationId() {
        return purchaseQuotationId;
    }

    public void setPurchaseQuotationId(Long purchaseQuotationId) {
        this.purchaseQuotationId = purchaseQuotationId;
    }

    public UploadFileResponse(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
