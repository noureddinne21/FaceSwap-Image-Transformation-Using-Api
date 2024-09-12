package com.nouroeddinne.apifaceswap;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OkHttpMapExample {

    private static final String API_URL = "https://faceswap-image-transformation-api.p.rapidapi.com/faceswapbase64";
    private static final String API_KEY = "cd947def00mshf2f8e21fff017ebp13720fjsn8e061a1c961d";

    public interface Callback {
        void onSuccess(String url);
        void onError(Exception e);
    }

    public void sendRequest(Map<String, String> parameters, Callback callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS) // Increase connection timeout
                .writeTimeout(60, TimeUnit.SECONDS)   // Increase write timeout
                .readTimeout(60, TimeUnit.SECONDS)    // Increase read timeout
                .build();

        MediaType mediaType = MediaType.parse("application/json");

        // Convert Map to JSON
        Gson gson = new Gson();
        String jsonBody = gson.toJson(parameters);
        RequestBody body = RequestBody.create(jsonBody, mediaType);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("x-rapidapi-key", API_KEY)
                .addHeader("x-rapidapi-host", "faceswap-image-transformation-api.p.rapidapi.com")
                .addHeader("Content-Type", "application/json")
                .build();

        new Thread(() -> {
            Response response = null;
            try {
                response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    JsonObject jsonObject = JsonParser.parseString(responseData).getAsJsonObject();
                    String resultImageUrl = jsonObject.get("ResultImageUrl").getAsString();
                    callback.onSuccess(resultImageUrl);
                } else {
                    callback.onError(new IOException("Request failed with code: " + response.code()));
                }
            } catch (IOException e) {
                callback.onError(e);
            } finally {
                if (response != null) {
                    response.close(); // Ensure the response body is closed
                }
            }
        }).start();
    }
}
