package com.example.explorer.helpers;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ServerInterface {
    @GET("getHotelInfo")
    Call<List<String>> getHotelInfo(@Query("hotelId") String hotelId);
    @GET("getLocations")
    Call<List<Map<String,Double>>> getLocations();
    @GET("sendLocation")
    Call<String> sendLocation(@Query("location") Map<String,Object> mp);
    @GET("uploadImage")
    Call<String>uploadImage(@Query("image") Map<String,Object>data);
}
