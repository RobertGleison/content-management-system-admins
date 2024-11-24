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


/**
 * Singleton class that manages the Retrofit instance for making API calls.
 * Configures and provides access to a Retrofit client with custom settings for:
 * - LocalDateTime serialization/deserialization
 * - Network timeouts
 * - Base URL configuration
 * - JSON conversion
 */
public class RetrofitClient {
    private static RetrofitClient instance = null;
    private static OkHttpClient client;
    private static Gson gson;
    private static Retrofit retrofit;
    private static RetrofitInterface api;

    /**
     * Private constructor that initializes the Retrofit instance with custom configurations.
     * Sets up:
     * - Custom TypeAdapter for LocalDateTime handling
     * - OkHttpClient with timeout configurations
     * - Gson converter with custom type adapters
     * - Retrofit instance with base URL and configurations
     */
    private RetrofitClient() {
        // Create custom TypeAdapter for LocalDateTime
        TypeAdapter<LocalDateTime> localDateTimeAdapter = new TypeAdapter<LocalDateTime>() {
            /**
             * Writes LocalDateTime to JSON format.
             * @param out JSON writer
             * @param value LocalDateTime value to write
             * @throws IOException if writing fails
             */
            @Override
            public void write(JsonWriter out, LocalDateTime value) throws IOException {
                if (value == null) {
                    out.nullValue();
                } else {
                    out.value(value.toString());
                }
            }


            /**
             * Reads LocalDateTime from JSON format.
             * @param in JSON reader
             * @return parsed LocalDateTime or null if parsing fails
             * @throws IOException if reading fails
             */
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

        // Create OkHttpClient with timeout configurations
        client = new OkHttpClient.Builder()
                .connectTimeout(RetrofitNetworkConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(RetrofitNetworkConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(RetrofitNetworkConfig.READ_TIMEOUT, TimeUnit.SECONDS)
                .build();

        // Create Gson with custom TypeAdapter for LocalDateTime
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, localDateTimeAdapter)
                .create();

        // Create and configure Retrofit instance
        retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitNetworkConfig.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        api = retrofit.create(RetrofitInterface.class);
    }


    /**
     * Gets the singleton instance of RetrofitClient.
     * Creates a new instance if one doesn't exist.
     * @return The singleton instance of RetrofitClient
     */
    public static synchronized RetrofitClient getInstance() {
        if (instance == null) instance = new RetrofitClient();
        return instance;
    }


    /**
     * Gets the configured Retrofit interface for making API calls.
     * @return The RetrofitInterface instance containing API endpoint definitions
     */
    public RetrofitInterface getApi() {
        return api;
    }

    /**
     * Closes all active connections and cleans up resources.
     * This method should be called when the application is being shut down
     * or when you want to force close all network connections.
     */
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
    }
}