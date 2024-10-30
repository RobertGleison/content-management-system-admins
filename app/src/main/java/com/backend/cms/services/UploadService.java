package com.backend.cms.services;

import android.content.ContentResolver;
import android.net.Uri;

import com.backend.cms.entities.Media;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadService {
    private final RetrofitInterface api;
    private final ContentResolver contentResolver;

    public UploadService(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.11:8080/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.api = retrofit.create(RetrofitInterface.class);
    }

    public Call<ResponseBody> uploadMedia(Media media, File videoFile, File thumbnailFile, Uri videoUri, Uri imageUri) {
        MultipartBody.Part videoPart = prepareFilePart("videoFile", videoFile, videoUri);
        MultipartBody.Part thumbnailPart = prepareFilePart("thumbnail", thumbnailFile, imageUri);

        RequestBody titleBody = createPartFromString(media.getTitle());
        RequestBody descriptionBody = createPartFromString(media.getDescription());
        RequestBody genreBody = createPartFromString(media.getGenre());
        RequestBody publisherBody = createPartFromString(media.getPublisher());
        RequestBody durationBody = createPartFromString(String.valueOf(media.getDuration()));

        return api.uploadVideo(
                videoPart,
                thumbnailPart,
                titleBody,
                descriptionBody,
                genreBody,
                publisherBody,
                durationBody
        );
    }

    private RequestBody createPartFromString(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }

    private MultipartBody.Part prepareFilePart(String partName, File file, Uri uri) {
        RequestBody requestBody = RequestBody.create(
                MediaType.parse(contentResolver.getType(uri)),
                file
        );
        return MultipartBody.Part.createFormData(partName, file.getName(), requestBody);
    }
}