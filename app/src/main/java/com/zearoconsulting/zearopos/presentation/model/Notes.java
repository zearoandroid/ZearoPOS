package com.zearoconsulting.zearopos.presentation.model;

/**
 * Created by saravanan on 24-12-2016.
 */

public class Notes {

    private long notesId;
    private String notesName;
    private long clientId;
    private long orgId;
    private long prodcutId;

    public long getProdcutId() {
        return prodcutId;
    }

    public void setProdcutId(long prodcutId) {
        this.prodcutId = prodcutId;
    }

    public long getNotesId() {
        return notesId;
    }

    public void setNotesId(long notesId) {
        this.notesId = notesId;
    }

    public String getNotesName() {
        return notesName;
    }

    public void setNotesName(String notesName) {
        this.notesName = notesName;
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
