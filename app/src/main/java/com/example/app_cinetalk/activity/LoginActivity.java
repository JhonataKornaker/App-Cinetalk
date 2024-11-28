package com.example.app_cinetalk.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.app_cinetalk.R;
import com.example.app_cinetalk.model.LoginRequest;
import com.example.app_cinetalk.model.LoginResponse;
import com.example.app_cinetalk.network.ApiClient;
import com.example.app_cinetalk.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextView txtCadastro, txtEmail, txtSenha;
    private ApiService apiService;
    private String token;
    private Button btnEntrar;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        apiService = ApiClient.getRetrofitInstance(getApplicationContext()).create(ApiService.class);

        txtCadastro = findViewById(R.id.txtCadastro);
        txtEmail = findViewById(R.id.editTextLoginEmail);
        txtSenha = findViewById(R.id.editTextLoginPassword);
        btnEntrar = findViewById(R.id.btnLoginEntrar);
        progressBar = findViewById(R.id.progressBar);

        txtCadastro.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
            startActivity(intent);
        });

        btnEntrar.setOnClickListener(v -> {
            String email = txtEmail.getText().toString();
            String senha = txtSenha.getText().toString();
            autenticarUsuario(email, senha);
        });

    }

    private void autenticarUsuario (String email, String senha) {
        LoginRequest loginRequest = new LoginRequest(email, senha);

        progressBar.setVisibility(View.VISIBLE);
        btnEntrar.setEnabled(false);

        apiService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                progressBar.setVisibility(View.GONE);  // Esconde o carregamento após resposta
                btnEntrar.setEnabled(true);

                if (response.isSuccessful()) {
                    token = response.body().getToken();
                    Log.d("LoginActivity", "Token User Logado: " + token);
                    abrirHomeActivityComToken(token);
                } else {
                    showError("Erro ao fazer login, tente novamente.");
                    Log.e("LoginActivity", "Erro na autenticação: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);  // Esconde o carregamento se falhou
                btnEntrar.setEnabled(true);  // Reabilita o botão
                showError("Erro na requisição, verifique sua conexão.");
            }
        });
    }

    private void abrirHomeActivityComToken(String token) {
        preferencesUser(token);

        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();  // Finaliza a MainActivity, para que o usuário não possa voltar a ela
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void preferencesUser(String token) {
        // Log para verificar o token recebido
        Log.d("PreferencesUser", "Token recebido para salvar: " + token);

        SharedPreferences prefsUser = getSharedPreferences("MeuApp", MODE_PRIVATE);
        String userId = prefsUser.getString("userId", null);

        // Salva o estado de login do usuário no SharedPreferences
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("TOKEN", token);  // Salve o token
        boolean isSaved = editor.commit(); // Usar commit() para garantir que o token foi salvo imediatamente

        // Verifica se o token foi salvo corretamente
        if (isSaved) {
            Log.d("PreferencesUser", "Token salvo com sucesso no SharedPreferences.");
        } else {
            Log.e("PreferencesUser", "Erro ao salvar o token no SharedPreferences.");
        }

        // Para confirmar, recupere o token logo após salvá-lo
        String savedToken = prefs.getString("TOKEN", null);
        Log.d("PreferencesUser", "Token recuperado após salvar: " + savedToken);

        ApiClient.resetRetrofit();
    }

}