package com.example.wonglab.jmorder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.wonglab.jmorder.Database.DatabaseHelper;
import com.example.wonglab.jmorder.Database.Item;
import com.example.wonglab.jmorder.Database.Order;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.table.TableUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.List;

import static com.example.wonglab.jmorder.NewOrderActivity.databaseHelper;

public class HomeActivity extends AppCompatActivity {

    private Dao<Order, String> orderDao;
    private Dao<Item, String> itemDao;
    Button newOrder, sendOrders, deleteOrders;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();

        newOrder = (Button) findViewById(R.id.new_order);
        sendOrders = (Button) findViewById(R.id.send_orders);
        deleteOrders = (Button) findViewById(R.id.delete_orders);

        newOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newOrderIntent = new Intent(HomeActivity.this, NewOrderActivity.class);
                startActivity(newOrderIntent);
            }
        });

        sendOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAllstackOrder();
            }
        });

        deleteOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear();
                Toast.makeText(HomeActivity.this, "Deleted Order Database!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getAllstackOrder(){

        try {
            List<Order> orderList = getDatabaseHelper().getOrderDao().queryForAll();
            Log.d("database size:", ""+ orderList.size());
            writeToCSV(orderList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static final String CSV_SEPARATOR = ",";
    private void writeToCSV(List<Order> orderList)
    {
        Log.d("File Name", Environment.getExternalStorageDirectory().getAbsolutePath());
        try
        {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Orders.csv"), "UTF-8"));
            for (Order order : orderList)
            {
                StringBuffer custName = new StringBuffer();
                custName.append(order.getCustomer_name().trim().length() ==0 ? "" : order.getCustomer_name());
                bw.write(custName.toString());
                bw.newLine();

                ForeignCollection<Item> items = order.getItems();
                for (Item item : items) {
                    StringBuffer oneLine = new StringBuffer();
                    oneLine.append(item.getItem_name().trim().length() ==0 ? "" : item.getItem_name());
                    oneLine.append(CSV_SEPARATOR);
                    oneLine.append(item.getQuantity().trim().length() == 0? "" : item.getQuantity());
                    oneLine.append(CSV_SEPARATOR);
                    bw.write(oneLine.toString());
                    bw.newLine();
                }
                bw.newLine();
                order.setStatus(true);

            }
            bw.flush();
            bw.close();
            Log.d("CSV", "Write to CSV successful");
            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Orders.csv");
            Uri attachment = Uri.fromFile(f);
            String[] addresses = {"jainbros123@gmail.com"};
            String subject = "JM Orders";
            composeEmail(addresses, subject, attachment);
        }
        catch (UnsupportedEncodingException e) {Log.d("Error", "Unsupported encoding");}
        catch (FileNotFoundException e){Log.d("Error", "File not found");}
        catch (IOException e){Log.d("Error", "IO exception");}
    }

    public void composeEmail(String[] addresses, String subject, Uri attachment) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/csv");
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_STREAM, attachment);
        startActivity(Intent.createChooser(intent, "Send Mail"));
    }

    public void clear(){

        openConnection();

        try {
            TableUtils.dropTable(itemDao.getConnectionSource(),Item.class,false);
            System.out.println("Item table dropped");
            TableUtils.createTable(itemDao.getConnectionSource(),Item.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            TableUtils.dropTable(orderDao.getConnectionSource(),Order.class,false);
            //orderDao.executeRaw("drop database orders;");
            System.out.println("Order database dropped");
            TableUtils.createTable(orderDao.getConnectionSource(),Order.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        closeConnection();
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

    private DatabaseHelper getDatabaseHelper(){
        if(databaseHelper == null){
            databaseHelper = databaseHelper.getHelper(HomeActivity.this);
        }
        return databaseHelper;
    }

}
