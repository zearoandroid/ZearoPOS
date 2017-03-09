package com.zearoconsulting.zearopos.presentation.model;

import java.io.Serializable;

/**
 * Created by saravanan on 31-10-2016.
 */

public class KOTHeader implements Serializable {

    private long tablesId;
    private long kotNumber;
    private long invoiceNumber;
    private long terminalId;
    private double totalAmount;
    private String printed;
    private String posted;
    private String isSelected;
    private String isKOT;
    private String orderType;
    private String kotType;
    private int coversCount=0;
    private String orderBy;
    private long clientId;
    private long orgId;

    public int getCoversCount() {
        return coversCount;
    }

    public void setCoversCount(int coversCount) {
        this.coversCount = coversCount;
    }

    public String getKotType() {
        return kotType;
    }

    public void setKotType(String kotType) {
        this.kotType = kotType;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
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

    public long getTablesId() {
        return tablesId;
    }

    public void setTablesId(long tablesId) {
        this.tablesId = tablesId;
    }

    public long getKotNumber() {
        return kotNumber;
    }

    public void setKotNumber(long kotNumber) {
        this.kotNumber = kotNumber;
    }

    public long getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(long invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public long getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(long terminalId) {
        this.terminalId = terminalId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPrinted() {
        return printed;
    }

    public void setPrinted(String printed) {
        this.printed = printed;
    }

    public String getPosted() {
        return posted;
    }

    public void setPosted(String posted) {
        this.posted = posted;
    }

    public String getSelected() {
        return isSelected;
    }

    public void setSelected(String isSelected) {
        this.isSelected = isSelected;
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
