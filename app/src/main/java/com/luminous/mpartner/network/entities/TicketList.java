package com.luminous.mpartner.network.entities;

//Get Tickets by Anusha 27/06/2018 TASK#4009
public class TicketList {

    public String CreatedOn, SerialNo, Status, Tic_Id;


    public TicketList(String date, String serialno, String status, String tic_Id) {
        CreatedOn = date;
        SerialNo = serialno;
        Status = status;
        Tic_Id = tic_Id;  //by Anusha 06/07/2018 TASK#4009
    }
}
