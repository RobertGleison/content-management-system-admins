package com.example.testingnetflix.retrofitAPI;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class RetrofitClient {
    private static RetrofitClient instance = null;
    private static OkHttpClient client;
    private static Gson gson;
    private static Retrofit retrofit;
    private static RetrofitInterface api;
    private static String idToken; // Store the ID token

    // Add method to update the ID token
    public static void setIdToken(String token) {
        idToken = token;
    }

    private RetrofitClient() {
        // Existing LocalDateTime adapter code remains the same
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

        // Create authentication interceptor
        Interceptor authInterceptor = chain -> {
            Request originalRequest = chain.request();

            // Only add the token if it exists
            if (idToken != null && !idToken.isEmpty()) {
                Request newRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer " + idToken)
                        .build();
                return chain.proceed(newRequest);
            }

            return chain.proceed(originalRequest);
        };

        // Add the interceptor to OkHttpClient
        client = new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .connectTimeout(RetrofitNetworkConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(RetrofitNetworkConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(RetrofitNetworkConfig.READ_TIMEOUT, TimeUnit.SECONDS)
                .build();

        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, localDateTimeAdapter)
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitNetworkConfig.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        api = retrofit.create(RetrofitInterface.class);
    }

    // Rest of the existing methods remain the same
    public static synchronized RetrofitClient getInstance() {
        if (instance == null) instance = new RetrofitClient();
        return instance;
    }

    public RetrofitInterface getApi() {
        return api;
    }

    public void closeConnection() {
        if (client != null) {
            client.dispatcher().cancelAll();
            client.dispatcher().executorService().shutdown();
            client.connectionPool().evictAll();

            try {
                if (client.cache() != null) {
                    client.cache().close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Clear all static references
        instance = null;
        api = null;
        client = null;
        gson = null;
        retrofit = null;
        idToken = null; // Clear the token as well
    }
}