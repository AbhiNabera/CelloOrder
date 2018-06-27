package com.example.wonglab.jmorder;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;

public class ProductAutoCompleteTextChangedListener implements TextWatcher {

    public static final String TAG = "ProductAutoCompleteTextChangedListener.java";
    Context context;

    public ProductAutoCompleteTextChangedListener(Context context){
        this.context = context;
    }

    @Override
    public void afterTextChanged(Editable s) {
        // TODO Auto-generated method stub

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTextChanged(CharSequence userInput, int start, int before, int count) {

        // if you want to see in the logcat what the user types
        Log.e(TAG, "User input: " + userInput);

        NewOrderActivity newOrderActivity = ((NewOrderActivity) context);

        // query the database based on the user input
        newOrderActivity.item1 = newOrderActivity.getItemsFromDb1(userInput.toString());

        // update the adapater
        newOrderActivity.myAdapter1.notifyDataSetChanged();
        newOrderActivity.myAdapter1 = new ArrayAdapter<String>(newOrderActivity, android.R.layout.simple_dropdown_item_1line, newOrderActivity.item1);
        newOrderActivity.myAutoComplete1.setAdapter(newOrderActivity.myAdapter1);

    }
}
