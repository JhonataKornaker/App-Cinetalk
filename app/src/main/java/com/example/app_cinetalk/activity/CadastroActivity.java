package com.example.app_cinetalk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.app_cinetalk.R;
import com.example.app_cinetalk.model.RegisterUser;
import com.example.app_cinetalk.network.ApiClient;
import com.example.app_cinetalk.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadastroActivity extends AppCompatActivity {

    private EditText edtNome, edtEmail, edtSenha, edtConfirmarSenha;
    private Button btnCadastrarConta;
    private ApiService apiService;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        apiService = ApiClient.getRetrofitInstance(getApplicationContext()).create(ApiService.class);

        edtNome = findViewById(R.id.editTextCadastroNome);
        edtEmail = findViewById(R.id.editTextCadastroEmail);
        edtSenha = findViewById(R.id.editTextCadastroSenha);
        edtConfirmarSenha = findViewById(R.id.editTextCadastroConfirmarSenha);
        btnCadastrarConta = findViewById(R.id.btnCadastrarConta);
        progressBar = findViewById(R.id.progressBarCadastro);

        btnCadastrarConta.setOnClickListener(v -> {
            String senha = edtSenha.getText().toString();
            String confirmarSenha = edtConfirmarSenha.getText().toString();
            String nome = edtNome.getText().toString();
            String email = edtEmail.getText().toString();

            if (TextUtils.isEmpty(nome)) {
                edtNome.setError("Nome é obrigatório");
                return;
            }

            if (TextUtils.isEmpty(email)) {
                edtEmail.setError("Email é obrigatório");
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edtEmail.setError("Formato de email inválido");
                return;
            }

            if (TextUtils.isEmpty(senha)) {
                edtSenha.setError("Senha é obrigatória");
                return;
            }

            if (senha.length() < 4) {
                edtSenha.setError("Senha deve ter no mínimo 4 caracteres");
                return;
            }

            if (senha.isEmpty() || confirmarSenha.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
            } else if (!senha.equals(confirmarSenha)) {
                Toast.makeText(this, "As senhas não correspondem", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(this, "Senha confirmada com sucesso", Toast.LENGTH_SHORT).show();
                cadastrarUsuario(nome, email, senha);
            }
        });
    }

    private void cadastrarUsuario(String nome, String email, String senha) {
        RegisterUser registerUser = new RegisterUser(nome, email, senha);

        progressBar.setVisibility(View.VISIBLE);
        btnCadastrarConta.setEnabled(false);

        apiService.register(registerUser).enqueue(new Callback<RegisterUser>() {
            @Override
            public void onResponse(Call<RegisterUser> call, Response<RegisterUser> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(CadastroActivity.this, "Cadastro Realizado com Sucesso!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(CadastroActivity.this, "Erro no Cadastro!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterUser> call, Throwable t) {
                Log.e("MainActivity", "Falha na autenticação: " + t.getMessage());
            }
        });
    }
}