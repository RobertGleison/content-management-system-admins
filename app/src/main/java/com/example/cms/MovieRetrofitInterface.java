package com.example.cms;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface MovieRetrofitInterface {
    @Multipart
    @POST("media/upload")
    Call<ResponseBody> uploadMovie(
        @Part("movieTitle") ResponseBody movieTitle,
        @Part("movieDescription") ResponseBody movieDescription,
        @Part("movieGenre") ResponseBody movieGenre,
        @Part("movieYear") ResponseBody movieYear,
        @Part("moviePublisher") ResponseBody moviePublisher,
        @Part("movieDuration") ResponseBody movieDuration
    );
}
