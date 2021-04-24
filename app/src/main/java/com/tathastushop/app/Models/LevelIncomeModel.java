package com.tathastushop.app.Models;

import java.io.Serializable;

public class LevelIncomeModel implements Serializable {

    private String incomeid;
    private String mid;
    private String fid;
    private String levelid;
    private String productid;
    private String orderid;
    private String amount;
    private String created_date;
    private String activated;
    private String enabled;
    private String percent;
    private String nett;


    public LevelIncomeModel() {
    }

    public LevelIncomeModel(String incomeid, String mid, String fid, String levelid, String productid, String orderid, String amount, String created_date, String activated, String enabled, String percent, String nett) {
        this.incomeid = incomeid;
        this.mid = mid;
        this.fid = fid;
        this.levelid = levelid;
        this.productid = productid;
        this.orderid = orderid;
        this.amount = amount;
        this.created_date = created_date;
        this.activated = activated;
        this.enabled = enabled;
        this.percent = percent;
        this.nett = nett;
    }


    public String getIncomeid() {
        return incomeid;
    }

    public void setIncomeid(String incomeid) {
        this.incomeid = incomeid;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getLevelid() {
        return levelid;
    }

    public void setLevelid(String levelid) {
        this.levelid = levelid;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
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

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public String getNett() {
        return nett;
    }

    public void setNett(String nett) {
        this.nett = nett;
    }
}
