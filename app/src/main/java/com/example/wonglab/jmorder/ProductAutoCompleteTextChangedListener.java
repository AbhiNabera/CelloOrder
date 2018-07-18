package com.example.wonglab.jmorder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.wonglab.jmorder.API.ApiInterface;
import com.example.wonglab.jmorder.API.Element;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ProductAutoCompleteTextChangedListener implements TextWatcher {

    public static final String TAG = "ProductAutoCompleteTextChangedListener.java";
    Context context;

    public Subscription searchResultsSubscription;

    public NewOrderActivity newOrderActivity;

    public ProductAutoCompleteTextChangedListener(Context context){
        this.context = context;
        newOrderActivity = ((NewOrderActivity) context);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    @SuppressLint("LongLogTag")
    @Override
    public void onTextChanged(CharSequence userInput, int start, int before, int count) {

        // if you want to see in the logcat what the user types
        Log.e(TAG, "User input: " + userInput);

        // query the database based on the user input
        //newOrderActivity.item1 = newOrderActivity.getItemsFromDb1(userInput.toString());

        if(userInput.toString().length()!=0) {
            showSearchResult(userInput.toString());
        }

    }

    public Subscriber<List<Element>> searchResultsSubscriber() {
        return new Subscriber<List<Element>>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                Log.e("subscriber error :", e.getMessage());
            }

            @Override
            public void onNext(List<Element> searchList) {
                if(searchList != null){
                    Log.d("result",""+searchList.size());

                    String[] item_name = new String[searchList.size()];
                    int i = 0;
                    for(Element element: searchList){
                        item_name[i] = element.getName();
                        i++;
                    }

                    //update the adapter list
                    newOrderActivity.item1 = item_name;

                    // update the adapater
                    newOrderActivity.myAdapter1.notifyDataSetChanged();
                    newOrderActivity.myAdapter1 = new ArrayAdapter<String>(newOrderActivity, android.R.layout.simple_dropdown_item_1line, newOrderActivity.item1);
                    newOrderActivity.myAutoComplete1.setAdapter(newOrderActivity.myAdapter1);

                }

            }
        };
    }

    public void showSearchResult(String query) {

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://us-central1-jmorder-53c71.cloudfunctions.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        if (searchResultsSubscription != null && !searchResultsSubscription.isUnsubscribed()) {
            searchResultsSubscription.unsubscribe();
        }
        try {
            //String encodedQuery = URLEncoder.encode(query, "UTF-8");
            rx.Observable<List<Element>> observable = apiInterface.getItemList(query.trim().toUpperCase());
            searchResultsSubscription = observable
                    .debounce(500, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(searchResultsSubscriber());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
