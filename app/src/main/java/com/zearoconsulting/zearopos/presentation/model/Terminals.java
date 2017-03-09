package com.zearoconsulting.zearopos.presentation.model;

import java.io.Serializable;

/**
 * Created by saravanan on 30-10-2016.
 */

public class Terminals implements Serializable {

    private long terminalId;
    private String terminalName;
    private String terminalIP;
    private long clientId;
    private long orgId;

    public String getTerminalIP() {
        return terminalIP;
    }

    public void setTerminalIP(String terminalIP) {
        this.terminalIP = terminalIP;
    }

    public long getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(long terminalId) {
        this.terminalId = terminalId;
    }

    public String getTerminalName() {
        return terminalName;
    }

    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
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
