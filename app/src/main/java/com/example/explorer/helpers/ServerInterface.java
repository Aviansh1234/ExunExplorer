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
    Call<List<Map<String,Object>>> getLocations();
    @GET("sendLocation")
    Call<String> sendLocation(@Query("latitude") double lat,@Query("longitude") double lon, @Query("id") String id);
    @GET("uploadImage")
    Call<String>uploadImage();
}
