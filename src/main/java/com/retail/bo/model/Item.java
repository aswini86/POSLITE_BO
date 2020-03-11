package com.retail.bo.model;

public class Item {
    private String id;
    private String ean;
    private String productName;
    private String description;
    private String lotId;
    private String batchNumber;
    private double quantity;
    private double mrp;
    private double cp;
    private double sp;
    private String manufacturingDate;  
    private String expiryDate;    

    public Item() {
        
    }

    public Item(String ean) {
        this.ean = ean;
    }    

    public Item(String ean, String productName, String description, String lotId, String batchNumber, double quantity, double mrp, 
    		double sp, double cp, String manufacturingDate, String expiryDate) {
        this.ean = ean;
        this.productName = productName;
        this.description = description;
        this.lotId = lotId;
        this.batchNumber = batchNumber;
        this.quantity = quantity;
        this.mrp = mrp;
        this.cp = cp;
        this.sp = sp;
        this.manufacturingDate = manufacturingDate;
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
    
    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }
    
    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getMrp() {
        return mrp;
    }

    public void setMrp(double mrp) {
        this.mrp = mrp;
    }

    public double getCp() {
        return cp;
    }

    public void setCp(double cp) {
        this.cp = cp;
    }

    public double getSp() {
        return sp;
    }

    public void setSp(double sp) {
        this.sp = sp;
    }
    
    public String getManufacturingDate() {
        return manufacturingDate;
    }

    public void setManufacturingDate(String manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }
    
    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
}
