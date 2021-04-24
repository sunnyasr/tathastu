package com.tathastushop.app.PrefManager;

import android.content.Context;
import android.content.SharedPreferences;

public class UserManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "tashastu-user";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String KEY_TOKEN = "isToken";
    private static final String KEY_NAME = "isName";
    private static final String KEY_MOBILE = "isMobile";
    private static final String KEY_EMAIL = "isEmail";
    private static final String KEY_WALLET = "isWallet";
    private static final String KEY_PHOTO = "isPhoto";
    private static final String KEY_PASS = "isPass";
    private static final String KEY_MID = "isMid";
    private static final String KEY_ADDRESS = "isAddress";
    private static final String KEY_PINCODE = "isPinCode";
    private static final String KEY_DISTRICT = "isDistrict";
    private static final String KEY_STATE = "isState";
    private static final String KEY_ADHARNO = "isAdharno";
    private static final String KEY_ADHARPHOTO = "isAdharphoto";
    private static final String KEY_ACNO = "isAcno";
    private static final String KEY_ACNAME = "isAcname";
    private static final String KEY_IFSC = "isIfsc";
    private static final String KEY_BRANCH = "isBranch";
    private static final String KEY_BNAME = "isBname";
    private static final String KEY_CITY = "isCity";

    public UserManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.commit();
    }

    public String getToken() {
        return pref.getString(KEY_TOKEN, "");
    }

    public void setName(String name) {
        editor.putString(KEY_NAME, name);
        editor.commit();
    }

    public String getName() {
        return pref.getString(KEY_NAME, "");
    }

    //PinCode
    public void setPinCode(String name) {
        editor.putString(KEY_PINCODE, name);
        editor.commit();
    }

    public String getPinCode() {
        return pref.getString(KEY_PINCODE, "");
    }

    //District
    public void setDistrict(String name) {
        editor.putString(KEY_DISTRICT, name);
        editor.commit();
    }

    public String getDistrict() {
        return pref.getString(KEY_DISTRICT, "");
    }

    //State
    public void setState(String name) {
        editor.putString(KEY_STATE, name);
        editor.commit();
    }

    public String getState() {
        return pref.getString(KEY_STATE, "");
    }

    public void setMobile(String mobile) {
        editor.putString(KEY_MOBILE, mobile);
        editor.commit();
    }

    public String getMobile() {
        return pref.getString(KEY_MOBILE, "9876543210");
    }

    public void setEmail(String email) {
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL, "test@gmail.com");
    }

    public void setWallet(int wallet) {
        editor.putInt(KEY_WALLET, wallet);
        editor.commit();
    }

    public Integer getWallet() {
        return pref.getInt(KEY_WALLET, 0);
    }

    public void setPhoto(String photo) {
        editor.putString(KEY_PHOTO, photo);
        editor.commit();
    }

    public String getPhoto() {
        return pref.getString(KEY_PHOTO, "");
    }

    public void setPass(String name) {
        editor.putString(KEY_PASS, name);
        editor.commit();
    }

    public String getPass() {
        return pref.getString(KEY_PASS, "");
    }

    public void setMID(String name) {
        editor.putString(KEY_MID, name);
        editor.commit();
    }

    public String getMID() {
        return pref.getString(KEY_MID, "");
    }

    public void setAddress(String name) {
        editor.putString(KEY_ADDRESS, name);
        editor.commit();
    }

    public String getAddress() {
        return pref.getString(KEY_ADDRESS, "");
    }

    public void setAdharNo(String name) {
        editor.putString(KEY_ADHARNO, name);
        editor.commit();
    }

    public String getAdharNo() {
        return pref.getString(KEY_ADHARNO, "");
    }

    public void setAdharPhoto(String name) {
        editor.putString(KEY_ADHARPHOTO, name);
        editor.commit();
    }

    public String getAdharPhoto() {
        return pref.getString(KEY_ADHARPHOTO, "");
    }

    public void setAcno(String name) {
        editor.putString(KEY_ACNO, name);
        editor.commit();
    }

    public String getAcno() {
        return pref.getString(KEY_ACNO, "");
    }

    public void setIFSC(String name) {
        editor.putString(KEY_IFSC, name);
        editor.commit();
    }

    public String getIFSC() {
        return pref.getString(KEY_IFSC, "");
    }

    public String getBranch() {
        return pref.getString(KEY_BRANCH, "");
    }

    public void setBranch(String name) {
        editor.putString(KEY_BRANCH, name);
        editor.commit();
    }

    public String getBName() {
        return pref.getString(KEY_BNAME, "");
    }

    public void setBName(String name) {
        editor.putString(KEY_BNAME, name);
        editor.commit();
    }

    public String getCity() {
        return pref.getString(KEY_CITY, "");
    }

    public void setCity(String name) {
        editor.putString(KEY_CITY, name);
        editor.commit();
    }

    public String getAcname() {
        return pref.getString(KEY_ACNAME, "");
    }

    public void setAcname(String name) {
        editor.putString(KEY_ACNAME, name);
        editor.commit();
    }


}
