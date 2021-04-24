package com.tathastushop.app.Models;

public class TransactionModel {
    private String transactionid;
    private String mid;
    private String amount;
    private String admincharge;
    private String tds;
    private String nett;
    private String status;
    private String reqdate;
    private String paydate;
    private String acno;
    private String acname;
    private String bankname;
    private String activated;
    private String enabled;
    private String username;
    private String ifsccode;

    public TransactionModel() {
    }

    public TransactionModel(String transactionid, String mid, String amount, String admincharge, String tds, String nett, String status, String reqdate, String paydate, String acno, String acname, String bankname, String activated, String enabled, String username, String ifsccode) {
        this.transactionid = transactionid;
        this.mid = mid;
        this.amount = amount;
        this.admincharge = admincharge;
        this.tds = tds;
        this.nett = nett;
        this.status = status;
        this.reqdate = reqdate;
        this.paydate = paydate;
        this.acno = acno;
        this.acname = acname;
        this.bankname = bankname;
        this.activated = activated;
        this.enabled = enabled;
        this.username = username;
        this.ifsccode = ifsccode;
    }

    public String getTransactionid() {
        return transactionid;
    }

    public void setTransactionid(String transactionid) {
        this.transactionid = transactionid;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAdmincharge() {
        return admincharge;
    }

    public void setAdmincharge(String admincharge) {
        this.admincharge = admincharge;
    }

    public String getTds() {
        return tds;
    }

    public void setTds(String tds) {
        this.tds = tds;
    }

    public String getNett() {
        return nett;
    }

    public void setNett(String nett) {
        this.nett = nett;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReqdate() {
        return reqdate;
    }

    public void setReqdate(String reqdate) {
        this.reqdate = reqdate;
    }

    public String getPaydate() {
        return paydate;
    }

    public void setPaydate(String paydate) {
        this.paydate = paydate;
    }

    public String getAcno() {
        return acno;
    }

    public void setAcno(String acno) {
        this.acno = acno;
    }

    public String getAcname() {
        return acname;
    }

    public void setAcname(String acname) {
        this.acname = acname;
    }

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    public String getActivated() {
        return activated;
    }

    public void setActivated(String activated) {
        this.activated = activated;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIfsccode() {
        return ifsccode;
    }

    public void setIfsccode(String ifsccode) {
        this.ifsccode = ifsccode;
    }
}
