package com.zearoconsulting.zearopos.presentation.model;

import java.io.Serializable;

import java.io.Serializable;

/**
 * Created by saravanan on 27-05-2016.
 */
public class Product implements Serializable {

    private long categoryId;
    private long prodId;
    private String prodName;
    private String prodValue;
    private long uomId;
    private String uomValue;
    private double salePrice;
    private double costPrice;
    private String prodImage;
    private String prodArabicName;
    private int defaultQty;
    private String showDigitalMenu;
    private String prodVideoPath;
    private String calories;
    private String preparationTime;
    private String description;
    private long terminalId;
    private int qty;
    private double totalPrice;
    private long clientId;
    private long orgId;

    public long getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(long terminalId) {
        this.terminalId = terminalId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProdArabicName(String prodArabicName) { this.prodArabicName = prodArabicName; }

    public String getProdImage() {
        return prodImage;
    }

    public void setProdImage(String prodImage) {
        this.prodImage = prodImage;
    }

    public long getCategoryId() {return categoryId; }

    public void setCategoryId(long categoryId) { this.categoryId = categoryId; }

    public long getProdId() {
        return prodId;
    }

    public void setProdId(long prodId) {
        this.prodId = prodId;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getProdValue() {
        return prodValue;
    }

    public void setProdValue(String prodValue) {
        this.prodValue = prodValue;
    }

    public long getUomId() {
        return uomId;
    }

    public void setUomId(long uomId) {
        this.uomId = uomId;
    }

    public String getUomValue() {
        return uomValue;
    }

    public void setUomValue(String uomValue) {
        this.uomValue = uomValue;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
    }

    public String getProdArabicName() {
        return prodArabicName;
    }

    public int getDefaultQty() { return defaultQty; }

    public void setDefaultQty(int defaultQty) { this.defaultQty = defaultQty; }

    public String getShowDigitalMenu() {
        return showDigitalMenu;
    }

    public void setShowDigitalMenu(String showDigitalMenu) {
        this.showDigitalMenu = showDigitalMenu;
    }

    public String getProdVideoPath() {
        return prodVideoPath;
    }

    public void setProdVideoPath(String prodVideoPath) {
        this.prodVideoPath = prodVideoPath;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(String preparationTime) {
        this.preparationTime = preparationTime;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public long getOrgId() {
        return orgId;
    }

    public void setOrgId(long orgId) {
        this.orgId = orgId;
    }
}
