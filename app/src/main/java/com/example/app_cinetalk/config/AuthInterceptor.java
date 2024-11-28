package com.example.app_cinetalk.config;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = prefs.getString("TOKEN", null);

        Request originalRequest = chain.request();

        if (token == null || token.isEmpty()) {
            return chain.proceed(originalRequest);
        }

        // Adiciona o token no cabeçalho da requisição
        Request newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();

        return chain.proceed(newRequest);
    }
}
