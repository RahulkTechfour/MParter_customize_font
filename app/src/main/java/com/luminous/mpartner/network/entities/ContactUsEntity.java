package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContactUsEntity {

    @SerializedName("contactus_title")
    @Expose
    String contactusTitle;
    @SerializedName("address")
    @Expose
    String address;
    @SerializedName("phoneno")
    @Expose
    String phoneno;
    @SerializedName("sales_support_phoneno")
    @Expose
    String salesSupportPhoneno;
    @SerializedName("email")
    @Expose
    String email;

    public String getContactusTitle() {
        return contactusTitle;
    }

    public void setContactusTitle(String contactusTitle) {
        this.contactusTitle = contactusTitle;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public String getSalesSupportPhoneno() {
        return salesSupportPhoneno;
    }

    public void setSalesSupportPhoneno(String salesSupportPhoneno) {
        this.salesSupportPhoneno = salesSupportPhoneno;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}