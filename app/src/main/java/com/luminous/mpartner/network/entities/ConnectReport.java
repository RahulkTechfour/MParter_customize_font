package com.luminous.mpartner.network.entities;
//Add new report by Anusha 31/05/2018 TASK#3783
public class ConnectReport {

    public String Particulars, Hups, Battery, Hkva, Panel, Stabilizer;


    public ConnectReport(String particulars, String hups, String battery, String hkva, String panel, String stabilizer) {
        Particulars = particulars;
        Hups = hups;
        Battery = battery;
        Hkva = hkva;
        Panel = panel;
        Stabilizer = stabilizer;
    }
}
