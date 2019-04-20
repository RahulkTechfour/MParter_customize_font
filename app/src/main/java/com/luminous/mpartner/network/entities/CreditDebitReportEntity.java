
package com.luminous.mpartner.network.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreditDebitReportEntity {

    @SerializedName("AccountingDocumentNumber")
    @Expose
    private String accountingDocumentNumber;
    @SerializedName("PostingDate")
    @Expose
    private String postingDate;
    @SerializedName("Documenttype")
    @Expose
    private String documenttype;
    @SerializedName("localcurrency")
    @Expose
    private String localcurrency;
    @SerializedName("Assignmentnumber")
    @Expose
    private String assignmentnumber;
    @SerializedName("ItemText")
    @Expose
    private String itemText;

    public String getAccountingDocumentNumber() {
        return accountingDocumentNumber;
    }

    public void setAccountingDocumentNumber(String accountingDocumentNumber) {
        this.accountingDocumentNumber = accountingDocumentNumber;
    }

    public String getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(String postingDate) {
        this.postingDate = postingDate;
    }

    public String getDocumenttype() {
        return documenttype;
    }

    public void setDocumenttype(String documenttype) {
        this.documenttype = documenttype;
    }

    public String getLocalcurrency() {
        return localcurrency;
    }

    public void setLocalcurrency(String localcurrency) {
        this.localcurrency = localcurrency;
    }

    public String getAssignmentnumber() {
        return assignmentnumber;
    }

    public void setAssignmentnumber(String assignmentnumber) {
        this.assignmentnumber = assignmentnumber;
    }

    public String getItemText() {
        return itemText;
    }

    public void setItemText(String itemText) {
        this.itemText = itemText;
    }

}
