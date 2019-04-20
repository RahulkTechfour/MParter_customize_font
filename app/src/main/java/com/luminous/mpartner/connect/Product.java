package com.luminous.mpartner.connect;

import java.io.Serializable;

public class Product implements Serializable {

    public String Id, Name;

    public Product(String id, String name) {
        Id = id;
        Name = name;
    }
}