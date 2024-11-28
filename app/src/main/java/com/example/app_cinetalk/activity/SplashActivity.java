package com.example.app_cinetalk.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_cinetalk.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Verificar se o token está salvo no SharedPreferences
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String token = prefs.getString("TOKEN", null);

        // Usar Handler para esperar alguns segundos antes de redirecionar
        new Handler().postDelayed(() -> {
            if (token != null) {
                // Token encontrado, redireciona para a HomeActivity com o token
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                intent.putExtra("TOKEN", token);  // Passa o token para a HomeActivity
                startActivity(intent);
            } else {
                // Token não encontrado, redireciona para o LoginActivity
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
            }
            finish();  // Finaliza a SplashActivity para que o usuário não possa voltar a ela
        }, 2000);  // 2 segundos de delay para mostrar a tela de splash

    }
}