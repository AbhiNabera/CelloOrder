package com.example.wonglab.jmorder.Database;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "item")
public class Item {

    @DatabaseField(generatedId = true, columnName = "id")
    private int id;

    @DatabaseField( /*id = true*/ columnName = "item_name")
    private String item_name;

    @DatabaseField(columnName = "quantity")
    private String quantity;

    @DatabaseField(canBeNull = true, foreign = true, foreignAutoRefresh=true)
    private Order order;

    public Item(){

    }

    public Item(String item_name, String quantity, Order order){
        this.item_name = item_name;
        this.quantity = quantity;
        this.order = order;
    }

    public String getItem_name() {
        return item_name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

}
