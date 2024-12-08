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

/**
 * Singleton class managing Retrofit instance for API communication.
 * Handles:
 * - API client configuration
 * - Authentication token management
 * - JSON serialization/deserialization
 * - Network timeouts and interceptors
 * - Connection lifecycle
 *
 * This client uses OkHttp for HTTP operations and Gson for JSON processing.
 * All network operations are configured according to RetrofitNetworkConfig settings.
 */
public class RetrofitClient {
    private static RetrofitClient instance = null;
    private static OkHttpClient client;
    private static Gson gson;
    private static Retrofit retrofit;
    private static RetrofitInterface api;
    private static String idToken; // Firebase ID token for authentication


    /**
     * Updates the authentication token used for API requests.
     * This token is included in the Authorization header of all subsequent requests.
     *
     * @param token The Firebase ID token to use for authentication
     */
    public static void setIdToken(String token) {
        idToken = token;
    }


    /**
     * Private constructor to enforce singleton pattern.
     * Initializes:
     * - LocalDateTime type adapter for JSON serialization
     * - Authentication interceptor for token handling
     * - OkHttpClient with timeouts and interceptors
     * - Gson converter with custom type adapters
     * - Retrofit instance with base configuration
     */
    private RetrofitClient() {
        // LocalDateTime adapter for JSON serialization
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

        // Authentication interceptor for adding token to requests
        Interceptor authInterceptor = chain -> {
            Request originalRequest = chain.request();

            if (idToken != null && !idToken.isEmpty()) {
                Request newRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer " + idToken)
                        .build();
                return chain.proceed(newRequest);
            }

            return chain.proceed(originalRequest);
        };

        // Configure OkHttpClient with timeouts and interceptor
        client = new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .connectTimeout(RetrofitNetworkConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(RetrofitNetworkConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(RetrofitNetworkConfig.READ_TIMEOUT, TimeUnit.SECONDS)
                .build();

        // Configure Gson with custom type adapters
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, localDateTimeAdapter)
                .create();

        // Build Retrofit instance
        retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitNetworkConfig.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        api = retrofit.create(RetrofitInterface.class);
    }


    /**
     * Returns the singleton instance of RetrofitClient.
     * Creates a new instance if one doesn't exist.
     *
     * @return The singleton RetrofitClient instance
     */
    public static synchronized RetrofitClient getInstance() {
        if (instance == null) instance = new RetrofitClient();
        return instance;
    }


    /**
     * Provides access to the configured API interface.
     *
     * @return RetrofitInterface instance for making API calls
     */
    public RetrofitInterface getApi() {
        return api;
    }


    /**
     * Closes all active connections and cleans up resources.
     * This method:
     * - Cancels all pending requests
     * - Shuts down the executor service
     * - Evicts all connections from the pool
     * - Closes the cache
     * - Clears all static references
     *
     * Call this method when cleaning up or when you need to reset
     * the client's state completely.
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
        idToken = null;
    }
}