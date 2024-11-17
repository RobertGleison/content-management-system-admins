package com.backend.cms.retrofitAPI;

public class RetrofitNetworkConfig {
    // Connectio URL with backend server
    public static final String BASE_URL = "http://10.0.2.2:8080/";

    // Timeouts in seconds
    public static final int CONNECT_TIMEOUT = 60;
    public static final int WRITE_TIMEOUT = 60;
    public static final int READ_TIMEOUT = 60;
    public static final String MEDIA_TYPE_TEXT = "text/plain";

    // Singleton to not permit another instantiation
    private RetrofitNetworkConfig() {
    }
}