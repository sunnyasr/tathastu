package com.tathastushop.app.Models;

public class ProductModel {

    private String productid;
    private String pname;
    private int mrp;
    private String discount;
    private int shipping;
    private int price;
    private String pcode;
    private String image;
    private String description;
    private boolean m;
    private boolean l;
    private boolean xl;
    private boolean xxl;
    private boolean xxxl;
    private String created_date;
    private String activated;
    private String enabled;

    public ProductModel() {
    }

    public ProductModel(String productid, String pname, int mrp, String discount, int shipping, int price, String pcode, String image, String description, boolean m, boolean l, boolean xl, boolean xxl, boolean xxxl, String created_date, String activated, String enabled) {
        this.productid = productid;
        this.pname = pname;
        this.mrp = mrp;
        this.discount = discount;
        this.shipping = shipping;
        this.price = price;
        this.pcode = pcode;
        this.image = image;
        this.description = description;
        this.m = m;
        this.l = l;
        this.xl = xl;
        this.xxl = xxl;
        this.xxxl = xxxl;
        this.created_date = created_date;
        this.activated = activated;
        this.enabled = enabled;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public int getMrp() {
        return mrp;
    }

    public void setMrp(int mrp) {
        this.mrp = mrp;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public int getShipping() {
        return shipping;
    }

    public void setShipping(int shipping) {
        this.shipping = shipping;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getPcode() {
        return pcode;
    }

    public void setPcode(String pcode) {
        this.pcode = pcode;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isM() {
        return m;
    }

    public void setM(boolean m) {
        this.m = m;
    }

    public boolean isL() {
        return l;
    }

    public void setL(boolean l) {
        this.l = l;
    }

    public boolean isXl() {
        return xl;
    }

    public void setXl(boolean xl) {
        this.xl = xl;
    }

    public boolean isXxl() {
        return xxl;
    }

    public void setXxl(boolean xxl) {
        this.xxl = xxl;
    }

    public boolean isXxxl() {
        return xxxl;
    }

    public void setXxxl(boolean xxxl) {
        this.xxxl = xxxl;
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
}
