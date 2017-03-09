package com.zearoconsulting.zearopos.presentation.model;

import java.io.Serializable;

/**
 * Created by saravanan on 24-05-2016.
 */
public class Organization implements Serializable {

    private long orgId;
    private long clientId;
    private String orgName;
    private String orgArabicName;
    private String orgImage;
    private String orgPhone;
    private String orgEmail;
    private String orgAddress;
    private String orgCity;
    private String orgCountry;
    private String orgWebUrl;
    private String isDefault;
    private String orgDescription;
    private String orgFooter;

    public String getOrgFooter() {
        return orgFooter;
    }

    public void setOrgFooter(String orgFooter) {
        this.orgFooter = orgFooter;
    }

    public String getOrgDescription() {
        return orgDescription;
    }

    public void setOrgDescription(String orgDescription) {
        this.orgDescription = orgDescription;
    }

    public String getOrgArabicName() {
        return orgArabicName;
    }

    public void setOrgArabicName(String orgArabicName) {
        this.orgArabicName = orgArabicName;
    }

    public String getOrgImage() {
        return orgImage;
    }

    public void setOrgImage(String orgImage) {
        this.orgImage = orgImage;
    }

    public String getOrgPhone() {
        return orgPhone;
    }

    public void setOrgPhone(String orgPhone) {
        this.orgPhone = orgPhone;
    }

    public String getOrgEmail() {
        return orgEmail;
    }

    public void setOrgEmail(String orgEmail) {
        this.orgEmail = orgEmail;
    }

    public String getOrgAddress() {
        return orgAddress;
    }

    public void setOrgAddress(String orgAddress) {
        this.orgAddress = orgAddress;
    }

    public String getOrgCity() {
        return orgCity;
    }

    public void setOrgCity(String orgCity) {
        this.orgCity = orgCity;
    }

    public String getOrgCountry() {
        return orgCountry;
    }

    public void setOrgCountry(String orgCountry) {
        this.orgCountry = orgCountry;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public long getOrgId() {
        return orgId;
    }

    public void setOrgId(long orgId) {
        this.orgId = orgId;
    }

    public String getOrgWebUrl() {
        return orgWebUrl;
    }

    public void setOrgWebUrl(String orgWebUrl) {
        this.orgWebUrl = orgWebUrl;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }
}
