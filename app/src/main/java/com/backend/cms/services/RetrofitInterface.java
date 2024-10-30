package com.backend.cms.services;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.Call;


public interface RetrofitInterface {
    @Multipart
    @POST("media/upload")
    Call<ResponseBody> uploadVideo(
            @Part MultipartBody.Part videoFile,
            @Part MultipartBody.Part thumbnail,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("genre") RequestBody genre,
            @Part("publisher") RequestBody publisher,
            @Part("duration") RequestBody duration
    );
}
