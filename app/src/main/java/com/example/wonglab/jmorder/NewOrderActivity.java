package com.example.wonglab.jmorder;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.Selection;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wonglab.jmorder.Database.DatabaseHelper;
import com.example.wonglab.jmorder.Database.Item;
import com.example.wonglab.jmorder.Database.Order;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class NewOrderActivity extends AppCompatActivity implements OrderRecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    static DatabaseHelper databaseHelper;
    private Dao<Order, String> orderDao;
    private Dao<Item, String> itemDao;

    private Order order;

    private String timestamp;

    private RecyclerView orderListRecycler;
    private RecyclerView.Adapter orderListAdapter;
    private RecyclerView.LayoutManager layoutManager;

    CustomerAutoCompleteView myAutoComplete;
    ProductAutoCompleteView myAutoComplete1;

    // adapter for auto-complete
    ArrayAdapter<String> myAdapter;
    ArrayAdapter<String> myAdapter1;

    // for database operations
    CustomerDatabaseHandler databaseH;
    ProductDatabaseHandler databaseH1;

    // just to add some initial value
    String[] item = new String[] {"Please search..."};
    String[] item1 = new String[] {"Please search..."};

    EditText qty;
    Button add, save;

    List<String> itemInput = new ArrayList<>();
    List<String> qtyInput = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_new_order);
        getSupportActionBar().hide();

        timestamp = ""+System.currentTimeMillis();

        order = new Order();

        try {
            copyDataBase1();
            copyDataBase2();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try{

            // instantiate database handler
            databaseH = new CustomerDatabaseHandler(NewOrderActivity.this);
            databaseH1 = new ProductDatabaseHandler(NewOrderActivity.this);

            // put sample data to database
            //insertSampleData();

            // autocompletetextview is in activity_main.xml
            myAutoComplete = (CustomerAutoCompleteView) findViewById(R.id.myautocomplete);
            myAutoComplete1 = (ProductAutoCompleteView) findViewById(R.id.myautocomplete1);

            // add the listener so it will tries to suggest while the user types
            myAutoComplete.addTextChangedListener(new CustomerAutoCompleteTextChangedListener(this));
            myAutoComplete1.addTextChangedListener(new ProductAutoCompleteTextChangedListener(this));

            // set our adapter
            myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, item);
            myAutoComplete.setAdapter(myAdapter);

            myAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, item1);
            myAutoComplete1.setAdapter(myAdapter1);

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        orderListRecycler = (RecyclerView) findViewById(R.id.orderListRecycler);
        add = (Button) findViewById(R.id.add);
        save = (Button) findViewById(R.id.save);
        qty = (EditText) findViewById(R.id.qty);
        orderListRecycler.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        orderListRecycler.setLayoutManager(layoutManager);
        orderListAdapter = new OrderListRecyclerAdapter(itemInput, qtyInput);
        orderListRecycler.setAdapter(orderListAdapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new OrderRecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(orderListRecycler);

        //save to database

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String custName = myAutoComplete.getText().toString();
                String itemName = myAutoComplete1.getText().toString();
                String quantity = qty.getText().toString();
                if(custName.length() == 0){
                    Toast.makeText(NewOrderActivity.this, "Enter Customer Name!", Toast.LENGTH_SHORT).show(); }
                else if(itemName.length() == 0){
                    Toast.makeText(NewOrderActivity.this, "Select Item!", Toast.LENGTH_SHORT).show(); }
                else if(quantity.length() == 0){
                    Toast.makeText(NewOrderActivity.this, "Enter Quantity!", Toast.LENGTH_SHORT).show(); }
                else{
                    itemInput.add(itemName);
                    qtyInput.add(quantity);
                    orderListAdapter.notifyDataSetChanged();
                    myAutoComplete1.setText("");
                    qty.setText("");
                    myAutoComplete1.requestFocus();
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String custName = myAutoComplete.getText().toString();
                if(custName.length() == 0){
                    Toast.makeText(NewOrderActivity.this, "Enter Customer Name!", Toast.LENGTH_SHORT).show(); }
                else{
                    save(custName);
                    myAutoComplete.requestFocus();
                }
            }
        });

        AutoCompleteTextView custFrom = (AutoCompleteTextView) findViewById(R.id.myautocomplete);
        custFrom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> p, View v, int pos, long id) {
                AutoCompleteTextView itemTo = (AutoCompleteTextView) findViewById(R.id.myautocomplete1);
                itemTo.setFocusableInTouchMode(true);
                itemTo.requestFocus();
            }
        });

        AutoCompleteTextView itemFrom = (AutoCompleteTextView) findViewById(R.id.myautocomplete1);
        itemFrom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> p, View v, int pos, long id) {
                EditText qtyTo = (EditText) findViewById(R.id.qty);
                qtyTo.setFocusableInTouchMode(true);
                qtyTo.requestFocus();
            }
        });

        //getAllstackOrder();
    }

    // this function is used in CustomAutoCompleteTextChangedListener.java
    public String[] getItemsFromDb(String searchTerm){

        // add items on the array dynamically
        List<CustomerObject> products = databaseH.read(searchTerm);
        int rowCount = products.size();

        String[] item = new String[rowCount];
        int x = 0;

        for (CustomerObject record : products) {

            item[x] = record.objectName;
            x++;
        }

        return item;
    }

    public String[] getItemsFromDb1(String searchTerm){

        // add items on the array dynamically
        List<ProductObject> products = databaseH1.read(searchTerm);
        int rowCount = products.size();

        String[] item1 = new String[rowCount];
        int x = 0;

        for (ProductObject record : products) {

            item1[x] = record.objectName;
            x++;
        }

        return item1;
    }

    public void copyDataBase1() throws IOException {
        String package_name = getApplicationContext().getPackageName();
        String DB_PATH = "/data/data/"+package_name+"/databases/";
        String DB_NAME = "CustomerDatabase";
        try {
            InputStream myInput = getApplicationContext().getAssets().open(DB_NAME);

            File dbFile=new File(DB_PATH);
            dbFile.mkdirs();

            String outputFileName = DB_PATH + DB_NAME;
            OutputStream myOutput = new FileOutputStream(outputFileName);

            byte[] buffer = new byte[1024];
            int length;

            while((length = myInput.read(buffer))>0){
                myOutput.write(buffer, 0, length);
            }

            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getAllstackOrder(){

        try {
            List<Order> orderList = getDatabaseHelper().getOrderDao().queryForAll();
            Log.d("database size:", ""+ orderList.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    private void save(String customer_name){

        openConnection();

        Order order = new Order();
        order.setTimestamp(System.currentTimeMillis()+"");
        order.setCustomer_name(customer_name);
        order.setStatus(false);

        try {
            orderDao.createOrUpdate(order);
            Log.d("order stored","successfully");

            Toast.makeText(NewOrderActivity.this, "Order created and stored!", Toast.LENGTH_LONG).show();



        } catch (SQLException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < itemInput.size(); i++){
            addItem(itemInput.get(i),  qtyInput.get(i), customer_name, order);
        }

        closeConnection();

        myAutoComplete.setText("");
        itemInput.clear();
        qtyInput.clear();
        orderListAdapter.notifyDataSetChanged();

    }


    private void addItem(String item_name, String quantity, String customer_name, Order order){

        //openConnection();

        Item item = new Item();

        item.setItem_name(item_name);
        item.setQuantity(quantity);
        item.setOrder(order);

        try {
            itemDao.createOrUpdate(item);
            Log.d("item stored","successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //closeConnection();

    }

    private DatabaseHelper getDatabaseHelper(){
        if(databaseHelper == null){
            databaseHelper = databaseHelper.getHelper(NewOrderActivity.this);
        }
        return databaseHelper;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Runtime.getRuntime().gc();

        if(databaseHelper!=null){
            databaseHelper.close();
            databaseHelper = null;
        }

    }

    public void openConnection(){

        databaseHelper = getDatabaseHelper();
        try {
            itemDao = databaseHelper.getItemDao();
            orderDao = databaseHelper.getOrderDao();
        } catch (SQLException e) {
            Log.e("Error getting helper : ", e.getErrorCode() + " " + e.getMessage());
        }
    }

    public void closeConnection(){
        if(databaseHelper!=null){
            databaseHelper.close();
            databaseHelper = null;
        }
    }


    public void copyDataBase2() throws IOException {
        String package_name = getApplicationContext().getPackageName();
        String DB_PATH = "/data/data/"+package_name+"/databases/";
        String DB_NAME = "ProductDatabase";
        try {
            InputStream myInput = getApplicationContext().getAssets().open(DB_NAME);

            File dbFile = new File(DB_PATH);
            dbFile.mkdirs();

            String outputFileName = DB_PATH + DB_NAME;
            OutputStream myOutput = new FileOutputStream(outputFileName);

            byte[] buffer = new byte[1024];
            int length;

            while((length = myInput.read(buffer))>0){
                myOutput.write(buffer, 0, length);
            }

            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof OrderListRecyclerAdapter.ViewHolder) {
            // get the removed item name to display it in snack bar
            String name = itemInput.get(viewHolder.getAdapterPosition());

            // backup of removed item for undo purpose
            final String deletedItem = itemInput.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            itemInput.remove(itemInput.get(viewHolder.getAdapterPosition()));
            qtyInput.remove(qtyInput.get(viewHolder.getAdapterPosition()));
            orderListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        if(orderListAdapter.getItemCount() != 0)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("Exit");
            builder.setMessage("Any unsaved orders would be discarded. Are you sure you want to continue?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "yes", then he is allowed to exit from application
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user select "No", just cancel this dialog and continue with app
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }

        else {
            finish();
        }
    }

}




