package com.example.testingnetflix.upload;

import android.content.ContentResolver;
import android.net.Uri;

import com.example.testingnetflix.retrofitAPI.RetrofitClient;
import com.example.testingnetflix.retrofitAPI.RetrofitInterface;
import com.example.testingnetflix.entities.MediaUploadRequest;

import java.io.File;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

// Service class responsible for handling media upload operations to the server using Retrofit
public class UploadService {
    private final RetrofitInterface api;
    private final ContentResolver contentResolver;


    public UploadService(ContentResolver contentResolver, RetrofitClient retrofitClient) {
        this.contentResolver = contentResolver;
        this.api = retrofitClient.getApi();
    }


    /**
     * Uploads media content (video and thumbnail) along with metadata to the server.
     * Creates a multipart request containing both files and metadata fields.
     * @param mediaUploadRequest Object containing metadata for the upload (title, description, etc.)
     * @param videoFile The video file to be uploaded
     * @param thumbnailFile The thumbnail image file for the video
     * @param videoUri URI reference to the video file in the device
     * @param imageUri URI reference to the thumbnail image in the device
     * @return A Call object representing the upload request
     */
    public Call<ResponseBody> uploadMedia(MediaUploadRequest mediaUploadRequest,
                                          File videoFile,
                                          File thumbnailFile,
                                          Uri videoUri,
                                          Uri imageUri) {
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


    /**
     * Creates a RequestBody from a string value for use in multipart form data.
     * @param value The string value to convert into a RequestBody
     * @return RequestBody instance containing the string value with plain text media type
     */
    private RequestBody createPartFromString(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }


    /**
     * Prepares a file for multipart form data upload by creating a MultipartBody.Part.
     * Uses ContentResolver to determine the correct MIME type of the file.
     * @param partName The name of the form part (parameter name expected by the server)
     * @param file The file to be uploaded
     * @param uri URI reference to the file in the device
     * @return MultipartBody.Part instance ready for upload
     */
    private MultipartBody.Part prepareFilePart(String partName, File file, Uri uri) {
        RequestBody requestBody = RequestBody.create(
                MediaType.parse(contentResolver.getType(uri)),
                file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestBody);
    }
}