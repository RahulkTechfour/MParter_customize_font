package com.luminous.mpartner.network.entities;


import java.io.Serializable;

public class Product implements Serializable {

    public String Id, Name;


    public Product(String id, String name) {
        Id = id;
        Name = name;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
