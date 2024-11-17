package com.backend.cms.retrofitAPI;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;


public class RetrofitClient {
    private static RetrofitClient instance = null;
    private static RetrofitInterface api;

    private RetrofitClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(RetrofitNetworkConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(RetrofitNetworkConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(RetrofitNetworkConfig.READ_TIMEOUT, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitNetworkConfig.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(RetrofitInterface.class);
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) instance = new RetrofitClient();
        return instance;
    }

    public RetrofitInterface getApi() {
        return api;
    }
}