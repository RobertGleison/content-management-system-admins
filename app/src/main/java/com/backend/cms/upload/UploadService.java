package com.backend.cms.upload;

import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;

import com.backend.cms.retrofitAPI.RetrofitClient;
import com.backend.cms.retrofitAPI.RetrofitInterface;
import com.backend.cms.entities.MediaUploadRequest;

import java.io.File;
import java.io.IOException;
import okio.Buffer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class UploadService {
    private static final String TAG = "UploadService";
    private final RetrofitInterface api;
    private final ContentResolver contentResolver;

    public UploadService(ContentResolver contentResolver, RetrofitClient retrofitClient) {
        this.contentResolver = contentResolver;
        this.api = retrofitClient.getApi();
    }

    public Call<ResponseBody> uploadMedia(MediaUploadRequest mediaUploadRequest,
                                          File videoFile,
                                          File thumbnailFile,
                                          Uri videoUri,
                                          Uri imageUri) {
        Log.d(TAG, "=== Starting Upload Request ===");
        logRequestParameters(mediaUploadRequest, videoFile, thumbnailFile);

        try {
            // Prepare file parts first
            MultipartBody.Part videoPart = prepareFilePart("videoFile", videoFile, videoUri);
            MultipartBody.Part thumbnailPart = prepareFilePart("thumbnail", thumbnailFile, imageUri);

            // Prepare text parts with proper null checks
            RequestBody titleBody = createPartFromString(mediaUploadRequest.getTitle());
            RequestBody descriptionBody = createPartFromString(mediaUploadRequest.getDescription());
            RequestBody genreBody = createPartFromString(mediaUploadRequest.getGenre());
            RequestBody yearBody = createPartFromString(
                    mediaUploadRequest.getYear() != null ?
                            String.valueOf(mediaUploadRequest.getYear()) : ""
            );
            RequestBody publisherBody = createPartFromString(mediaUploadRequest.getPublisher());
            RequestBody durationBody = createPartFromString(
                    mediaUploadRequest.getDuration() != null ?
                            String.valueOf(mediaUploadRequest.getDuration()) : ""
            );

            // Log the complete request
            logCompleteRequest(videoPart, thumbnailPart, titleBody, descriptionBody,
                    genreBody, yearBody, publisherBody, durationBody);

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
        } catch (Exception e) {
            Log.e(TAG, "Error preparing upload request", e);
            throw e;
        }
    }

    private RequestBody createPartFromString(String value) {
        String safeValue = value != null ? value : "";
        return RequestBody.create(
                MediaType.parse("text/plain"),
                safeValue
        );
    }

    private MultipartBody.Part prepareFilePart(String partName, File file, Uri uri) {
        if (file == null || !file.exists()) {
            Log.e(TAG, "File is null or doesn't exist: " + partName);
            throw new IllegalArgumentException("Invalid file for: " + partName);
        }

        String mimeType = contentResolver.getType(uri);
        if (mimeType == null) {
            mimeType = partName.equals("videoFile") ? "video/mp4" : "image/jpeg";
            Log.w(TAG, "Using default mime type for " + partName + ": " + mimeType);
        }

        RequestBody requestFile = RequestBody.create(
                MediaType.parse(mimeType),
                file
        );

        MultipartBody.Part part = MultipartBody.Part.createFormData(
                partName,
                file.getName(),
                requestFile
        );

        logFilePart(partName, file, mimeType, part);
        return part;
    }

    private void logRequestParameters(MediaUploadRequest request, File videoFile, File thumbnailFile) {
        Log.d(TAG, "Request Parameters:");
        Log.d(TAG, "Title: " + request.getTitle());
        Log.d(TAG, "Description: " + request.getDescription());
        Log.d(TAG, "Genre: " + request.getGenre());
        Log.d(TAG, "Year: " + request.getYear());
        Log.d(TAG, "Publisher: " + request.getPublisher());
        Log.d(TAG, "Duration: " + request.getDuration());

        if (videoFile != null) {
            Log.d(TAG, String.format("Video: %s (%.2f MB)",
                    videoFile.getName(),
                    videoFile.length() / (1024.0 * 1024.0)));
        }

        if (thumbnailFile != null) {
            Log.d(TAG, String.format("Thumbnail: %s (%.2f MB)",
                    thumbnailFile.getName(),
                    thumbnailFile.length() / (1024.0 * 1024.0)));
        }
    }

    private void logFilePart(String partName, File file, String mimeType, MultipartBody.Part part) {
        Log.d(TAG, String.format("File Part: %s", partName));
        Log.d(TAG, String.format("- File name: %s", file.getName()));
        Log.d(TAG, String.format("- File size: %.2f MB", file.length() / (1024.0 * 1024.0)));
        Log.d(TAG, String.format("- MIME type: %s", mimeType));
        Log.d(TAG, String.format("- Headers: %s", part.headers()));
    }

    private void logCompleteRequest(MultipartBody.Part videoPart, MultipartBody.Part thumbnailPart,
                                    RequestBody... textParts) {
        Log.d(TAG, "=== Complete Multipart Request ===");

        Log.d(TAG, "Files:");
        if (videoPart != null) Log.d(TAG, "- Video: " + videoPart.headers());
        if (thumbnailPart != null) Log.d(TAG, "- Thumbnail: " + thumbnailPart.headers());

        Log.d(TAG, "Text Parts:");
        String[] names = {"title", "description", "genre", "year", "publisher", "duration"};
        for (int i = 0; i < textParts.length; i++) {
            try {
                Buffer buffer = new Buffer();
                textParts[i].writeTo(buffer);
                Log.d(TAG, String.format("- %s: %s", names[i], buffer.readUtf8()));
            } catch (IOException e) {
                Log.e(TAG, "Error reading text part: " + names[i], e);
            }
        }
    }
}