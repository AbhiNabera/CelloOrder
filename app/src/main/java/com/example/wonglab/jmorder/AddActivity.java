package com.example.wonglab.jmorder;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class AddActivity extends AppCompatActivity {

    Button add_customer, add_item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Button add_customer = (Button) findViewById(R.id.add_customer);
        Button add_item = (Button) findViewById(R.id.add_item);


        add_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(AddActivity.this);
                // Include dialog.xml file
                dialog.setContentView(R.layout.customer_dialog);
                // Set dialog title
                dialog.setTitle("Add Customer");
                // set values for custom dialog components - text, image and button
                EditText add_cust_edit = (EditText) dialog.findViewById(R.id.add_cust_edit);
                Button add_cust_btn = (Button) dialog.findViewById(R.id.add_cust_btn);
                dialog.show();

                add_cust_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

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
                EditText add_item_edit = (EditText) dialog.findViewById(R.id.add_item_edit);
                Button add_item_btn = (Button) dialog.findViewById(R.id.add_item_btn);
                dialog.show();

                add_item_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
            }
        });
    }
}
