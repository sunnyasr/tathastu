package com.tathastushop.app.Models;

import java.io.Serializable;

public class OrdersModel implements Serializable {

    private String orderid;
    private String mid;
    private String pid;
    private String status;
    private String discount;
    private String price;
    private String nett;
    private String qty;
    private String total;
    private String order_date;
    private String delivered_date;
    private String activated;
    private String enabled;
    private String pname;
    private String pcode;
    private String image;
    private String username;
    private String mobile;
    private String address;

    public OrdersModel() {
    }

    public OrdersModel(String orderid, String mid, String pid, String status, String discount, String price, String nett, String qty, String total, String order_date, String delivered_date, String activated, String enabled, String pname, String pcode, String image, String username, String mobile, String address) {
        this.orderid = orderid;
        this.mid = mid;
        this.pid = pid;
        this.status = status;
        this.discount = discount;
        this.price = price;
        this.nett = nett;
        this.qty = qty;
        this.total = total;
        this.order_date = order_date;
        this.delivered_date = delivered_date;
        this.activated = activated;
        this.enabled = enabled;
        this.pname = pname;
        this.pcode = pcode;
        this.image = image;
        this.username = username;
        this.mobile = mobile;
        this.address = address;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getNett() {
        return nett;
    }

    public void setNett(String nett) {
        this.nett = nett;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getDelivered_date() {
        return delivered_date;
    }

    public void setDelivered_date(String delivered_date) {
        this.delivered_date = delivered_date;
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

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPcode() {
        return pcode;
    }

    public void setPcode(String pcode) {
        this.pcode = pcode;
    }
}
