package com.backend.cms.upload;

import android.content.ContentResolver;
import android.net.Uri;

import com.backend.cms.retrofitAPI.RetrofitClient;
import com.backend.cms.retrofitAPI.RetrofitInterface;
import com.backend.cms.entities.MediaUploadRequest;

import java.io.File;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class UploadService {
    private final RetrofitInterface api;
    private final ContentResolver contentResolver;


    public UploadService(ContentResolver contentResolver, RetrofitClient retrofitClient) {
        this.contentResolver = contentResolver;
        this.api = retrofitClient.getApi();
    }

    public Call<ResponseBody> uploadMedia(MediaUploadRequest mediaUploadRequest, File videoFile, File thumbnailFile, Uri videoUri, Uri imageUri) {
        MultipartBody.Part videoPart = prepareFilePart("videoFile", videoFile, videoUri);
        MultipartBody.Part thumbnailPart = prepareFilePart("thumbnail", thumbnailFile, imageUri);

        RequestBody titleBody = createPartFromString(mediaUploadRequest.getTitle());
        RequestBody descriptionBody = createPartFromString(mediaUploadRequest.getDescription());
        RequestBody genreBody = createPartFromString(mediaUploadRequest.getGenre());
        RequestBody yearBody = createPartFromString(String.valueOf(mediaUploadRequest.getYear()));
        RequestBody publisherBody = createPartFromString(mediaUploadRequest.getPublisher());
        RequestBody durationBody = createPartFromString(String.valueOf(mediaUploadRequest.getDuration()));

        return api.uploadVideo(
                videoPart,
                thumbnailPart,
                titleBody,
                descriptionBody,
                genreBody,
                yearBody,
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