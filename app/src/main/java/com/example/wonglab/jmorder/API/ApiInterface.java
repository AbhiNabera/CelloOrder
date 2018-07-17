package com.example.wonglab.jmorder.API;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Observable;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("customer")
    rx.Observable<List<Element>> getCustomerList(@Query("param") String param);

    @GET("item")
    rx.Observable<List<Element>> getItemList(@Query("param") String param);

    @FormUrlEncoded
    @POST("add_customer")
    Call<JsonObject> add_customer(@Field("name") String name);

    @FormUrlEncoded
    @POST("add_item")
    Call<JsonObject> add_item(@Field("name") String name);

}
