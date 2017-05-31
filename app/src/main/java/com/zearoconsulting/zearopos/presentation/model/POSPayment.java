package com.zearoconsulting.zearopos.presentation.model;

import java.io.Serializable;

/**
 * Created by saravanan on 05-09-2016.
 */
public class POSPayment implements Serializable {

    private long posId;
    private double cash;
    private double amex;
    private double gift;
    private double master;
    private double visa;
    private double other;
    private double change;
    private long clientId;
    private long orgId;
    private int isComplement;

    public long getPosId() {
        return posId;
    }

    public void setPosId(long posId) {
        this.posId = posId;
    }

    public double getCash() {
        return cash;
    }

    public void setCash(double cash) {
        this.cash = cash;
    }

    public double getAmex() {
        return amex;
    }

    public void setAmex(double amex) {
        this.amex = amex;
    }

    public double getGift() {
        return gift;
    }

    public void setGift(double gift) {
        this.gift = gift;
    }

    public double getMaster() {
        return master;
    }

    public void setMaster(double master) {
        this.master = master;
    }

    public double getVisa() {
        return visa;
    }

    public void setVisa(double visa) {
        this.visa = visa;
    }

    public double getOther() {
        return other;
    }

    public void setOther(double other) {
        this.other = other;
    }

    public double getChange() {
        return change;
    }

    public int getIsComplement() {
        return isComplement;
    }

    public void setIsComplement(int isComplement) {
        this.isComplement = isComplement;
    }
    public void setChange(double change) {
        this.change = change;
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
