package com.example.mayank.iot.Network;

import com.example.mayank.iot.Model.DHT11Model;
import com.example.mayank.iot.Model.TorsionModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Mayank on 3/16/2017.
 */
public interface ApiInterface {
    @GET("{id}/feeds.json")
    Call<TorsionModel> getTorsionDetails(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("{id}/feeds.json")
    Call<DHT11Model> getDhtDetails(@Path("id") int id, @Query("api_key") String apiKey);


}
