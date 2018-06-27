package com.example.wonglab.jmorder;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;

public class CustomerAutoCompleteTextChangedListener implements TextWatcher{

    public static final String TAG = "CustomerAutoCompleteTextChangedListener.java";
    Context context;

    public CustomerAutoCompleteTextChangedListener(Context context){
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
        newOrderActivity.item = newOrderActivity.getItemsFromDb(userInput.toString());

        // update the adapater
        newOrderActivity.myAdapter.notifyDataSetChanged();
        newOrderActivity.myAdapter = new ArrayAdapter<String>(newOrderActivity, android.R.layout.simple_dropdown_item_1line, newOrderActivity.item);
        newOrderActivity.myAutoComplete.setAdapter(newOrderActivity.myAdapter);

    }
}
