package com.example.app_cinetalk.network;

import com.example.app_cinetalk.model.Filme;
import com.example.app_cinetalk.model.LoginRequest;
import com.example.app_cinetalk.model.LoginResponse;
import com.example.app_cinetalk.model.RegisterUser;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("register")
    Call<RegisterUser> register(@Body RegisterUser registerUser);

    @POST("movie")
    Call<Filme> cadastrarFilme(@Body Filme filmesCadastro);

    @GET("movie")
    Call<List<Filme>> listarFilmesUser();

    @DELETE("movie/{id}")
    Call<Void> deletarFilme(@Path("id") String id);

    @PATCH("filmes/{id}")
    Call<Filme> atualizarFilmeParcial(
            @Body Filme filme,
            @Path("id") String id,
            @Query("userId") String userId
    );

}
