package com.retail.bo.model;

public class UnmapProduct {
    private String id;
    private String ean;
    private String productName;
    private String description;
    private String lotId;
    private String quantity;
    private String mrp;
    private String cp;
    private String sp;
    private String expiryDate;    
    
    
    public UnmapProduct(String ean, String productName, String description, String lotId, String quantity, String mrp, String cp, String sp, String expiryDate) {
        this.ean = ean;
        this.productName = productName;
        this.description = description;
        this.lotId = lotId;
        this.quantity = quantity;
        this.mrp = mrp;
        this.cp = cp;
        this.sp = sp;
        this.expiryDate = expiryDate;
    }
    
    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLotId() {
        return lotId;
    }

    public void setLotId(String lotId) {
        this.lotId = lotId;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public String getSp() {
        return sp;
    }

    public void setSp(String sp) {
        this.sp = sp;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }    
}
