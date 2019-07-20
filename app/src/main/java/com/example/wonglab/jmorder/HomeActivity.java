package com.example.wonglab.jmorder;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.wonglab.jmorder.API.ApiInterface;
import com.example.wonglab.jmorder.API.Element;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.wonglab.jmorder.NewOrderActivity.databaseHelper;

@RuntimePermissions
public class HomeActivity extends AppCompatActivity {

    private Dao<Order, String> orderDao;
    private Dao<Item, String> itemDao;
    private int STORAGE_PERMISSION_CODE = 1;
    Button newOrder, sendOrders;
    ImageButton add, deleteOrders;
    ProgressDialog progressDialog;
    public String customerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching data...");
        progressDialog.setCancelable(false);

        newOrder = (Button) findViewById(R.id.new_order);
        sendOrders = (Button) findViewById(R.id.send_orders);
        add = (ImageButton) findViewById(R.id.add);
        deleteOrders = (ImageButton) findViewById(R.id.delete_orders);

        newOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initialize();
            }
        });

        sendOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeActivityPermissionsDispatcher.readWriteExternalStorageWithPermissionCheck(HomeActivity.this);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newAddIntent = new Intent(HomeActivity.this, AddActivityPassword.class);
                startActivity(newAddIntent);
                finish();
            }
        });

        deleteOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Are you sure?")
                        .setMessage("All the orders either sent or unsent would be deleted! Do you want to proceed?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                clear();
                                Toast.makeText(HomeActivity.this, "Deleted Order Database!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create()
                        .show();
            }
        });
    }

    public void initialize(){
        progressDialog.show();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://us-central1-jmorder-53c71.cloudfunctions.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        apiInterface.getAllCustomerList("").enqueue(new Callback<List<Element>>() {
            @Override
            public void onResponse(Call<List<Element>> call, Response<List<Element>> response) {
                if(response.isSuccessful()){
                    Log.d("Home Activity", response.body().toString());
                    NewOrderActivity.allCustomer = response.body();
                    isCompleted1 = true;
                    activityTransition();
                }
            }

            @Override
            public void onFailure(Call<List<Element>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(HomeActivity.this, "Network failure! Check internet connection.", Toast.LENGTH_SHORT).show();
            }
        });

        apiInterface.getAllItemList("").enqueue(new Callback<List<Element>>() {
            @Override
            public void onResponse(Call<List<Element>> call, Response<List<Element>> response) {
                if(response.isSuccessful()){
                    Log.d("Home Activity", response.body().toString());
                    NewOrderActivity.allItem = response.body();
                    isCompleted2 = true;
                    activityTransition();
                }
            }

            @Override
            public void onFailure(Call<List<Element>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(HomeActivity.this, "Network failure! Check internet connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public boolean isCompleted1 = false, isCompleted2 = false;
    public void activityTransition(){
        if(isCompleted1 && isCompleted2){
            progressDialog.dismiss();
            Intent newOrderIntent = new Intent(HomeActivity.this, NewOrderActivity.class);
            startActivity(newOrderIntent);
        }
    }



    @Override
    public void onResume(){
        super.onResume();
        isCompleted1 = false;
        isCompleted2 = false;
    }

    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void readWriteExternalStorage(){
        Toast.makeText(HomeActivity.this, "Done!", Toast.LENGTH_SHORT).show();
        getAllstackOrder();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        HomeActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnShowRationale({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showRationalForStorage(final PermissionRequest request){
        new AlertDialog.Builder(this)
                .setTitle("Permission Needed")
                .setMessage("This app requires to access external storage")
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        request.proceed();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        request.cancel();
                    }
                })
                .create()
                .show();
    }

    @OnPermissionDenied({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onExternalStorageDenied(){
        Toast.makeText(HomeActivity.this, "Access Denied", Toast.LENGTH_SHORT).show();
    }

    public void getAllstackOrder(){

        try {
            List<Order> orderList = getDatabaseHelper().getOrderDao().queryForAll();
            Log.d("database size:", ""+ orderList.size());
            for (Order order : orderList){
                customerName = order.getCustomer_name().trim().length() == 0 ? "" : order.getCustomer_name();
            }
            writeToCSV(orderList, customerName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static final String CSV_SEPARATOR = ",";
    private void writeToCSV(List<Order> orderList, String customerName)
    {
        Log.d("File Name", Environment.getExternalStorageDirectory().getAbsolutePath());
        try
        {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + customerName + ".csv"), "UTF-8"));
            for (Order order : orderList)
            {
                StringBuffer custName = new StringBuffer();
                custName.append(CSV_SEPARATOR);
                custName.append(order.getCustomer_name().trim().length() ==0 ? "" : order.getCustomer_name());
                bw.write(custName.toString());
                bw.newLine();

                int i = 1;
                ForeignCollection<Item> items = order.getItems();
                for (Item item : items) {
                    StringBuffer oneLine = new StringBuffer();
                    oneLine.append(i);
                    oneLine.append(CSV_SEPARATOR);
                    i = i + 1;
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
            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + customerName + ".csv");
            Uri attachment = FileProvider.getUriForFile(HomeActivity.this, BuildConfig.APPLICATION_ID + ".provider",f);
            String[] addresses = {"orders.jm1995@gmail.com"};
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
