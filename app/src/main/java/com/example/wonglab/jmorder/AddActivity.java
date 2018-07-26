package com.example.wonglab.jmorder;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wonglab.jmorder.API.ApiInterface;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddActivity extends AppCompatActivity {

    Button add_customer, add_item;
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add);
        getSupportActionBar().hide();
        Button add_customer = (Button) findViewById(R.id.add_customer);
        final Button add_item = (Button) findViewById(R.id.add_item);

        pd = new ProgressDialog(AddActivity.this);

        add_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(AddActivity.this);
                // Include dialog.xml file
                dialog.setContentView(R.layout.customer_dialog);
                // Set dialog title
                dialog.setTitle("Add Customer");
                // set values for custom dialog components - text, image and button
                final EditText add_cust_edit = (EditText) dialog.findViewById(R.id.add_cust_edit);
                Button add_cust_btn = (Button) dialog.findViewById(R.id.add_cust_btn);
                dialog.show();

                add_cust_edit.setFocusableInTouchMode(true);
                add_cust_edit.requestFocus();

                add_cust_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("button click","button");
                        if(add_cust_edit.getText().toString().trim().length()!=0) {
                            addCustomer(add_cust_edit.getText().toString());
                            dialog.cancel();
                        }
                        else
                        {
                            Toast.makeText(AddActivity.this, "Enter customer name!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(AddActivity.this);
                // Include dialog.xml file
                dialog.setContentView(R.layout.item_dialog);
                // Set dialog title
                dialog.setTitle("Add Item");
                // set values for custom dialog components - text, image and button
                final EditText add_item_edit = (EditText) dialog.findViewById(R.id.add_item_edit);
                Button add_item_btn = (Button) dialog.findViewById(R.id.add_item_btn);
                dialog.show();

                add_item_edit.setFocusableInTouchMode(true);
                add_item_edit.requestFocus();

                add_item_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("button click","button");
                        if(add_item_edit.getText().toString().trim().length()!=0) {
                            addItem(add_item_edit.getText().toString());
                            dialog.cancel();
                        }
                        else{
                            Toast.makeText(AddActivity.this, "Enter item name!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    public void addItem(String name){

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://us-central1-jmorder-53c71.cloudfunctions.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        pd.setMessage("Adding Item! Please wait.");
        pd.show();
        pd.setCancelable(false);

        apiInterface.add_item(name.toUpperCase()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful()){
                    Log.d("Response",""+response.body());
                    Toast.makeText(AddActivity.this,"Added successfully", Toast.LENGTH_SHORT).show();
                    pd.cancel();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(AddActivity.this,"Network error!", Toast.LENGTH_SHORT).show();
                pd.cancel();
            }
        });
    }

    public void addCustomer(String name){

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://us-central1-jmorder-53c71.cloudfunctions.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        pd.setMessage("Adding customer! Please wait.");
        pd.show();
        pd.setCancelable(false);
        apiInterface.add_customer(name.toUpperCase()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful()){
                    Log.d("Response",""+response.body());
                    Toast.makeText(AddActivity.this,"Added successfully", Toast.LENGTH_SHORT).show();
                    pd.cancel();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(AddActivity.this,"Network error!", Toast.LENGTH_SHORT).show();
                pd.cancel();
            }
        });
    }
}
