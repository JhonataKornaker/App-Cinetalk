package com.example.app_cinetalk.network;

import android.content.Context;

import com.example.app_cinetalk.config.AuthInterceptor;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "https://api-cinetalk.onrender.com/api/";
    private static Retrofit retrofitWithAuth;

    private static final long CACHE_SIZE = 10 * 1024 * 1024; // 10 MB

    public static Retrofit getRetrofitInstance(Context context) {
        if (retrofitWithAuth == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Configura o cache
            File cacheDir = new File(context.getCacheDir(), "http_cache");
            Cache cache = new Cache(cacheDir, CACHE_SIZE);

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(new AuthInterceptor(context))
                    .cache(cache)
                    .build();

            retrofitWithAuth = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitWithAuth;
    }

    public static void resetRetrofit() {
        retrofitWithAuth = null;
    }
}
