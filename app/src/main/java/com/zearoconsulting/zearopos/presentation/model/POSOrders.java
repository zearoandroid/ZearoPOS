package com.zearoconsulting.zearopos.presentation.model;

import java.io.Serializable;

/**
 * Created by saravanan on 24-05-2016.
 */
public class POSOrders implements Serializable {

    private String customerName;
    private long posId;
    private long bpId;
    private int isCashCustomer;
    private String isPosted;
    private String isPrinted;
    private String isDiscounted;
    private int totalDiscType;
    private int totalDiscValue;
    private String isKOT;
    private String orderType;
    private long clientId;
    private long orgId;
    private double orderTotalAmt;
    private int orderTotalQty;

    public double getOrderTotalAmt() {
        return orderTotalAmt;
    }

    public void setOrderTotalAmt(double orderTotalAmt) {
        this.orderTotalAmt = orderTotalAmt;
    }

    public int getOrderTotalQty() {
        return orderTotalQty;
    }

    public void setOrderTotalQty(int orderTotalQty) {
        this.orderTotalQty = orderTotalQty;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getIsKOT() {
        return isKOT;
    }

    public void setIsKOT(String isKOT) {
        this.isKOT = isKOT;
    }

    public long getBpId() {
        return bpId;
    }

    public void setBpId(long bpId) {
        this.bpId = bpId;
    }

    public String getIsPrinted() {
        return isPrinted;
    }

    public void setIsPrinted(String isPrinted) {
        this.isPrinted = isPrinted;
    }

    public int getIsCashCustomer() {
        return isCashCustomer;
    }

    public void setIsCashCustomer(int isCashCustomer) {
        this.isCashCustomer = isCashCustomer;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public long getPosId() {
        return posId;
    }

    public void setPosId(long posId) {
        this.posId = posId;
    }

    public String getIsPosted() {
        return isPosted;
    }

    public void setIsPosted(String isPosted) {
        this.isPosted = isPosted;
    }

    public int getTotalDiscType() {
        return totalDiscType;
    }

    public void setTotalDiscType(int totalDiscType) {
        this.totalDiscType = totalDiscType;
    }

    public int getTotalDiscValue() {
        return totalDiscValue;
    }

    public void setTotalDiscValue(int totalDiscValue) {
        this.totalDiscValue = totalDiscValue;
    }

    public String getIsDiscounted() {
        return isDiscounted;
    }

    public void setIsDiscounted(String isDiscounted) {
        this.isDiscounted = isDiscounted;
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
