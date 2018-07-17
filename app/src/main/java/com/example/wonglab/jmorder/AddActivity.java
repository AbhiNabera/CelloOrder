package com.example.wonglab.jmorder;

import android.app.Dialog;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Button add_customer = (Button) findViewById(R.id.add_customer);
        final Button add_item = (Button) findViewById(R.id.add_item);


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

                add_cust_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("button click","button");
                        if(add_cust_edit.getText().toString().trim().length()!=0) {
                            addCustomer(add_cust_edit.getText().toString());
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

                add_item_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("button click","button");
                        if(add_item_edit.getText().toString().trim().length()!=0) {
                            addItem(add_item_edit.getText().toString());
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

        apiInterface.add_item(name.toUpperCase()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful()){
                    Log.d("Response",""+response.body());
                    Toast.makeText(AddActivity.this,"Added Successfully", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(AddActivity.this,"Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addCustomer(String name){

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://us-central1-jmorder-53c71.cloudfunctions.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        apiInterface.add_customer(name.toUpperCase()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful()){
                    Log.d("Response",""+response.body());
                    Toast.makeText(AddActivity.this,"Added Successfully", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(AddActivity.this,"Network error!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
