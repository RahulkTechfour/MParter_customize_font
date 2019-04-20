package com.luminous.mpartner.events;

public class ProductCatalogClickEvent {

    private int id;

    public ProductCatalogClickEvent(int id) {
        this.id = id;
    }

    public int getProductCatalogId() {
        return id;
    }
}
