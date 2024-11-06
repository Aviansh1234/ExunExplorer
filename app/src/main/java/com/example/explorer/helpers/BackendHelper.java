package com.example.explorer.helpers;

import android.content.Intent;
import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BackendHelper {
    private static String baseUrl = "https://exunbackend.onrender.com";
    static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();
    static Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
                .client(okHttpClient)
                .build();

    public static List<GeoPoint> getLatLongs() throws IOException {
        ServerInterface serverInterface = retrofit.create(ServerInterface.class);
        Call<List<Map<String,Object>>> call = serverInterface.getLocations();
        Response<List<Map<String,Object>>>res = call.execute();
        if(res.isSuccessful()){
            List<Map<String,Object>> arr = res.body();
            List<GeoPoint>ans = new ArrayList<>();
            for(Map<String,Object> mp : arr){
                ans.add(new GeoPoint((Double) mp.get("Latitude"),(Double)mp.get("Longitude")));
            }
            return ans;
        }
        return new ArrayList<GeoPoint>();
    }
    public static void imageUpload(Intent data){
        FirebaseStorage.getInstance().getReference().putFile(data.getData())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                System.out.println(uri.toString());
                            }
                        });
                    }
                });
    }
    public static String imageIdentification(Intent data){
        return "Unknown";
    }
    public static void toggleSOS(){

    }
    public static void sendLocation(GeoPoint loc) throws IOException {
        System.out.println("sending location");
        Map<String,Object>data = new HashMap<>();
        data.put("Longitude",loc.getLongitude());
        data.put("Latitude",loc.getLatitude());
        data.put("Time",System.currentTimeMillis());
        data.put("Id",FirebaseAuth.getInstance().getCurrentUser().getUid());
        ServerInterface serverInterface = retrofit.create(ServerInterface.class);
        Call<String> call = serverInterface.sendLocation((Double) data.get("Latitude"), (Double)data.get("Longitude"), (String)data.get("Id"));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                System.out.println("kar to diya");
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                System.out.println("nahi hua");
            }
        });
    }
}
