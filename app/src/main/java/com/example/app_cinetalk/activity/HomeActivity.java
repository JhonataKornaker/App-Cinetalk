package com.example.app_cinetalk.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.app_cinetalk.R;
import com.example.app_cinetalk.model.Filme;
import com.example.app_cinetalk.model.FilmeAdapter;
import com.example.app_cinetalk.network.ApiClient;
import com.example.app_cinetalk.network.ApiService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.view.View;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    private ApiService apiService;
    private String token;
    private RecyclerView recyclerView;
    private FilmeAdapter adapter;
    private ImageButton btnLogout;
    private FloatingActionButton fab;
    private EditText edtNome, edtCategoria, edtNota, edtComentario;
    private Button btnConfirm, btnRecarregar;
    private ProgressBar progressBarHome;
    private SwipeRefreshLayout swipeRefresh;
    private View errorLayout;
    private TextView mensagemErro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        errorLayout = findViewById(R.id.errorLayout);
        mensagemErro = findViewById(R.id.textView2);
        btnRecarregar = findViewById(R.id.btnRecarregar);

        swipeRefresh = findViewById(R.id.swipeRefreshLayout);
        swipeRefresh.setOnRefreshListener(() -> {
            listarFilmes();
            swipeRefresh.setRefreshing(false);
        });

        progressBarHome = findViewById(R.id.progressBarHome);

        recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FilmeAdapter(new FilmeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Filme filme) {
                showExibirDialog(filme);
            }

            @Override
            public void onItemLongClick(Filme filme) {
                showSnackbarToDelete(filme);
            }
        });
        recyclerView.setAdapter(adapter);

        btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {
            logout();
        });

        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        token = prefs.getString("TOKEN", null);

        if (token != null) {
            // Inicializar o ApiService
            apiService = ApiClient.getRetrofitInstance(getApplicationContext()).create(ApiService.class);
            listarFilmes();
        } else {
            // Direcionar para a LoginActivity
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            Toast.makeText(this, "Error Token", Toast.LENGTH_SHORT).show();
            startActivity(intent);
            finish();
        }


        fab = findViewById(R.id.btnAdicionarFilme);

        fab.setOnClickListener(v -> {
            showCadastroDialog();
        });
    }

    private void listarFilmes() {
        progressBarHome.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        Call<List<Filme>> call = apiService.listarFilmesUser();
        call.enqueue(new Callback<List<Filme>>() {
            @Override
            public void onResponse(Call<List<Filme>> call, Response<List<Filme>> response) {
                progressBarHome.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                if (response.isSuccessful()) {
                    List<Filme> filmes = response.body();
                    if (filmes != null && !filmes.isEmpty()) {
                        adapter.submitList(filmes);
                    } else {
                        adapter.submitList(Collections.emptyList());
                        mostrarMsgVazia();
                    }
                } else {
                    exibirErro("Erro na requisição: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Filme>> call, Throwable t) {
                progressBarHome.setVisibility(View.GONE);
                exibirErro("Erro na requisição: " + t.getMessage());
            }
        });
    }

    private void exibirErro(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
        recyclerView.setVisibility(View.GONE); // Esconde RecyclerView
        errorLayout.setVisibility(View.VISIBLE);
        mensagemErro.setText(mensagem);
        mensagemErro.setVisibility(View.VISIBLE);
        btnRecarregar.setVisibility(View.VISIBLE);
        btnRecarregar.setOnClickListener(v -> {
            listarFilmes();
            errorLayout.setVisibility(View.GONE);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        listarFilmes();
    }

    private void logout() {
        // Remover o token de autenticação
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("TOKEN");  // Remover o token específico
        editor.apply();

        if (adapter != null) {
            adapter.submitList(null); // Limpa os dados do RecyclerView
        }

        // Redirecionar para a LoginActivity
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();  // Finaliza a HomeActivity
    }

    private void mostrarMsgVazia() {
        recyclerView.setVisibility(View.GONE); // Esconde o RecyclerView
        TextView mensagemVazia = findViewById(R.id.mensagem_vazia);
        mensagemVazia.setVisibility(View.VISIBLE);
    }

    private void showCadastroDialog() {

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_adicionar);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ProgressBar progressBar;

        edtNome = dialog.findViewById(R.id.editTextAdicionarNome);
        edtCategoria = dialog.findViewById(R.id.editTextAdicionarCategoria);
        edtNota = dialog.findViewById(R.id.editTextAdicionarNota);
        edtComentario = dialog.findViewById(R.id.editTextAdicionarComentario);
        btnConfirm = dialog.findViewById(R.id.buttonConfirm);
        progressBar = dialog.findViewById(R.id.progressBar);

        btnConfirm.setOnClickListener(v -> {
            String nome = edtNome.getText().toString();
            String categoria = edtCategoria.getText().toString();
            String nota = edtNota.getText().toString();
            String comentario = edtComentario.getText().toString();

            if (TextUtils.isEmpty(nome)) {
                edtNome.setError("Nome é obrigatório");
                return;
            }
            if (TextUtils.isEmpty(categoria)) {
                edtCategoria.setError("Categoria é obrigatória");
                return;
            }
            if (TextUtils.isEmpty(nota)) {
                edtNota.setError("Nota é obrigatória");
                return;
            }
            if (TextUtils.isEmpty(comentario)) {
                edtComentario.setError("Comentário é obrigatório");
            }

            Log.d("Nota", "Valor inserido: " + nota);

            salvarFilme(nome, categoria, nota, comentario, dialog);
        });

        dialog.show();
    }

    private void salvarFilme(String nome, String categoria, String nota, String comentario, Dialog dialog) {

        progressBarHome.setVisibility(View.VISIBLE);
        btnConfirm.setEnabled(false);

        double avaliacao = Double.parseDouble(nota);

        Filme filmeCadatro = new Filme(nome, categoria, avaliacao, comentario);

        Call<Filme> call = apiService.cadastrarFilme(filmeCadatro);

        call.enqueue(new Callback<Filme>() {
            @Override
            public void onResponse(Call<Filme> call, Response<Filme> response) {
                progressBarHome.setVisibility(View.GONE);  // Esconde o carregamento se falhou
                btnConfirm.setEnabled(true);

                Filme filmeAdicionado = response.body();

                // Crie uma nova lista mutável com os itens atuais
                List<Filme> filmesAtualizados = new ArrayList<>(adapter.getCurrentList());

                // Adicione o novo filme à nova lista
                filmesAtualizados.add(filmeAdicionado);

                // Atualize a lista no adaptador com a nova lista
                adapter.submitList(filmesAtualizados);

                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<Filme> call, Throwable t) {
                progressBarHome.setVisibility(View.GONE);  // Esconde o carregamento se falhou
                btnConfirm.setEnabled(true);
                System.out.println("Falha na requisição: " + t.getMessage());
            }
        });
    }

    private void showExibirDialog(Filme filme) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_exibir_filme);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView txtNome = dialog.findViewById(R.id.textViewNomeFilme);
        TextView txtCategoria = dialog.findViewById(R.id.textViewCategoria);
        TextView txtNota = dialog.findViewById(R.id.textViewNota);
        TextView txtComentario = dialog.findViewById(R.id.textViewComentario);

        txtNome.setText(filme.getNome());
        txtCategoria.setText(filme.getGenero());
        txtNota.setText(String.valueOf(filme.getAvaliacao()));
        txtComentario.setText(filme.getComentario());

        dialog.show();
    }

    private void deletarFilme(String idFilme) {
        Call<Void> call = apiService.deletarFilme(idFilme);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("DeletarFilme", "Filme deletado com sucesso!");
                    Toast.makeText(getApplicationContext(), "Filme deletado com sucesso!", Toast.LENGTH_SHORT).show();

                    List<Filme> filmes = new ArrayList<>(adapter.getCurrentList());
                    Iterator<Filme> iterator = filmes.iterator();
                    while (iterator.hasNext()) {
                        Filme filme = iterator.next();
                        if (filme.getId().equals(idFilme)) {
                            Log.d("DeletarFilme", "Removendo filme: " + filme.getNome());
                            iterator.remove();
                            break;
                        }
                    }
                    adapter.submitList(filmes); // Atualiza a lista no RecyclerView
                    Log.d("DeletarFilme", "Lista atualizada no RecyclerView");

                } else {
                    Log.d("DeletarFilme", "Erro ao deletar o filme: " + response.message());
                    Log.d("DeletarFilme", "Código de erro: " + response.code());
                    Log.d("DeletarFilme", "Corpo da resposta: " + response.errorBody());
                    Toast.makeText(getApplicationContext(), "Erro ao deletar o filme!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("DeletarFilme", "Erro de comunicação: " + t.getMessage());
                Toast.makeText(getApplicationContext(), "Erro de comunicação: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSnackbarToDelete(Filme filme) {
        View rootView = findViewById(android.R.id.content);

        Snackbar snackbar = Snackbar.make(
                rootView,
                "Deseja deletar o filme " + filme.getNome() + "?",
                Snackbar.LENGTH_LONG
        );

        snackbar.setAction("SIM", v -> {
            deletarFilme(filme.getId());
            Toast.makeText(this, filme.getNome() + " deletado!", Toast.LENGTH_SHORT).show();
        });

        snackbar.setActionTextColor(getResources().getColor(android.R.color.holo_red_light));

        // Mostra o Snackbar
        snackbar.show();
    }
}