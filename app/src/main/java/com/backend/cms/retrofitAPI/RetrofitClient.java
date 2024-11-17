package com.backend.cms.retrofitAPI;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class RetrofitClient {
    private static RetrofitClient instance = null;
    private static RetrofitInterface api;

    private RetrofitClient() {

        // Create custom TypeAdapter for LocalDateTime
        TypeAdapter<LocalDateTime> localDateTimeAdapter = new TypeAdapter<LocalDateTime>() {
            @Override
            public void write(JsonWriter out, LocalDateTime value) throws IOException {
                if (value == null) {
                    out.nullValue();
                } else {
                    out.value(value.toString());
                }
            }

            @Override
            public LocalDateTime read(JsonReader in) throws IOException {
                if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                    in.nextNull();
                    return null;
                }
                String dateStr = in.nextString();
                try {
                    return LocalDateTime.parse(dateStr);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };

        // Create OkHttpClient
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(RetrofitNetworkConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(RetrofitNetworkConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(RetrofitNetworkConfig.READ_TIMEOUT, TimeUnit.SECONDS)
                .build();

        // Create Gson with custom TypeAdapter
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, localDateTimeAdapter)
                .create();

        // Create Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitNetworkConfig.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
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