package com.luminous.mpartner.network.entities;

public class TermsAndConditions {

    String TermsCondition;
    String schemeinfo;
    String Image;

    public TermsAndConditions(String termsCondition, String schemeinfo, String image) {
        TermsCondition = termsCondition;
        this.schemeinfo = schemeinfo;
        Image = image;
    }

    public String getTermsCondition() {
        return TermsCondition;
    }

    public String getSchemeinfo() {
        return schemeinfo;
    }

    public String getImage() {
        return Image;
    }
}
