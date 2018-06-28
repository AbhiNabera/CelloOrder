package com.example.wonglab.jmorder.Database;


import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "order")
public class Order {

    @DatabaseField(columnName = "customer_name")
    public String customer_name;

    @DatabaseField(id = true, columnName = "timestamp")
    public String timestamp;

    @DatabaseField(columnName = "status")
    public boolean status;
    
    @ForeignCollectionField(eager = true)
    public ForeignCollection<Item> items;

    public Order(){}

    public Order(String timestamp, String customer_name, ForeignCollection<Item> items, boolean status){

        this.status = status;
        this.timestamp = timestamp;
        this.customer_name = customer_name;
        this.items = items;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean getStatus() {
        return status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public ForeignCollection<Item> getItems() {
        return items;
    }

    public void setItems(ForeignCollection<Item> items) {
        this.items = items;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }
}
