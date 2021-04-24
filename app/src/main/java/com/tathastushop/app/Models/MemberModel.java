package com.tathastushop.app.Models;

import java.io.Serializable;

public class MemberModel implements Serializable {
    private String memberid;
    private String username;
    private String refferal;
    private String mobile;
    private String regdate;

    public MemberModel() {
    }

    public MemberModel(String memberid, String username, String refferal, String mobile, String regdate) {
        this.memberid = memberid;
        this.username = username;
        this.refferal = refferal;
        this.mobile = mobile;
        this.regdate = regdate;
    }

    public String getMemberid() {
        return memberid;
    }

    public void setMemberid(String memberid) {
        this.memberid = memberid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRefferal() {
        return refferal;
    }

    public void setRefferal(String refferal) {
        this.refferal = refferal;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRegdate() {
        return regdate;
    }

    public void setRegdate(String regdate) {
        this.regdate = regdate;
    }
}
