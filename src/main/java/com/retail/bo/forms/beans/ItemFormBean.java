package com.retail.bo.forms.beans;

import com.retail.bo.model.Item;
import java.util.ArrayList;
import java.util.List;

public class ItemFormBean {
    private String ean;
    private String itemId;
    
    private List<Item> items = new ArrayList<>();
    private List<Item> mappedItems = new ArrayList<>();
    private List<Item> unmappedItems = new ArrayList<>();
    private List<Item> products = new ArrayList<>();

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public List<Item> getMappedItems() {
        return mappedItems;
    }

    public void setMappedItems(List<Item> mappedItems) {
        this.mappedItems = mappedItems;
    }

    public List<Item> getUnmappedItems() {
        return unmappedItems;
    }

    public void setUnmappedItems(List<Item> unmappedItems) {
        this.unmappedItems = unmappedItems;
    }

    public List<Item> getProducts() {
        return products;
    }

    public void setProducts(List<Item> products) {
        this.products = products;
    }
}
