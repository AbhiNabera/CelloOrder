package com.example.wonglab.jmorder.API;

import java.util.List;
import java.util.Observable;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("customer")
    rx.Observable<List<Element>> getCustomerList(@Query("param") String param);

    @GET("item?")
    rx.Observable<List<Element>> getItemList(@Query("param") String param);

}
