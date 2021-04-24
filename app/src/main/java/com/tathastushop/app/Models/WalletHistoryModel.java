package com.tathastushop.app.Models;


public class WalletHistoryModel {
    private int whid;
    private int mid;
    private int amount;
    private String incometype;
    private String deposit_withdraw;
    private String date;
    private String username;





    public WalletHistoryModel() {
    }

    public WalletHistoryModel(int whid, int mid, int amount, String incometype, String deposit_withdraw, String date, String username) {
        this.whid = whid;
        this.mid = mid;
        this.amount = amount;
        this.incometype = incometype;
        this.deposit_withdraw = deposit_withdraw;
        this.date = date;
        this.username = username;
    }

    public int getWhid() {
        return whid;
    }

    public void setWhid(int whid) {
        this.whid = whid;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getIncometype() {
        return incometype;
    }

    public void setIncometype(String incometype) {
        this.incometype = incometype;
    }

    public String getDeposit_withdraw() {
        return deposit_withdraw;
    }

    public void setDeposit_withdraw(String deposit_withdraw) {
        this.deposit_withdraw = deposit_withdraw;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}