package com.zearoconsulting.zearopos.presentation.model;

import java.io.Serializable;

/**
 * Created by saravanan on 02-06-2016.
 */
public class BPartner implements Serializable {

    private long bpId;
    private String bpName;
    private String bpValue;
    private long bpPriceListId;
    private String bpEmail;
    private long bpNumber;
    private long clientId;
    private long orgId;
    private String isCredit;
    private double creditLimit;

    public double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(double creditLimit) {
        this.creditLimit = creditLimit;
    }

    public String getIsCredit() {
        return isCredit;
    }

    public void setIsCredit(String isCredit) {
        this.isCredit = isCredit;
    }

    public long getBpPriceListId() {
        return bpPriceListId;
    }

    public void setBpPriceListId(long bpPriceListId) {
        this.bpPriceListId = bpPriceListId;
    }

    public String getBpEmail() {
        return bpEmail;
    }

    public void setBpEmail(String bpEmail) {
        this.bpEmail = bpEmail;
    }

    public long getBpNumber() {
        return bpNumber;
    }

    public void setBpNumber(long bpNumber) {
        this.bpNumber = bpNumber;
    }

    public long getBpId() {
        return bpId;
    }

    public void setBpId(long bpId) {
        this.bpId = bpId;
    }

    public String getBpName() {
        return bpName;
    }

    public void setBpName(String bpName) {
        this.bpName = bpName;
    }

    public String getBpValue() {
        return bpValue;
    }

    public void setBpValue(String bpValue) {
        this.bpValue = bpValue;
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
