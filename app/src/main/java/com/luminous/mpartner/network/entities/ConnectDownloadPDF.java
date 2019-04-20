package com.luminous.mpartner.network.entities;
//Add Download PDF by Anusha 28/06/2018 TASK#4009
public class ConnectDownloadPDF {

    public String SerialNo, ProductType, Dis_Sap_Code, Dis_Name, Dlr_Sap_Code, Dlr_Name, product_code, product_detail, Sale_Date;
    public String cust_Name, cust_number, cust_add, dis_state, dlr_state, Point, Status, Remark, Created_On;


    public ConnectDownloadPDF(String serialNo, String productType, String dis_Sap_Code, String dis_Name, String dlr_Sap_Code, String dlr_Name,
                              String product_code, String product_detail, String sale_Date, String cust_Name, String cust_number,
                              String cust_add, String dis_state, String dlr_state, String point, String status, String remark, String created_On) {
        this.SerialNo = serialNo;
        this.ProductType = productType;
        this.Dis_Sap_Code = dis_Sap_Code;
        this.Dis_Name = dis_Name;
        this.Dlr_Sap_Code = dlr_Sap_Code;
        this.Dlr_Name = dlr_Name;
        this.product_code = product_code;
        this.product_detail = product_detail;
        this.Sale_Date = sale_Date;
        this.cust_Name = cust_Name;
        this.cust_number = cust_number;
        this.cust_add = cust_add;
        this.dis_state = dis_state;
        this.dlr_state = dlr_state;
        this.Point = point;
        this.Status = status;
        this.Remark = remark;
        this.Created_On = created_On;
    }
}
