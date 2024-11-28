package com.example.app_cinetalk.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_cinetalk.R;

import java.util.List;

public class FilmeAdapter extends ListAdapter<Filme, FilmeAdapter.FilmeViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Filme filme);
        void onItemLongClick(Filme filme);
    }

    private final OnItemClickListener listener;

    // **Construtor**: Não precisa mais de uma lista externa. O ListAdapter lida com a lista internamente.
    public FilmeAdapter(OnItemClickListener listener) {
        super(new DiffUtil.ItemCallback<Filme>() {
            @Override
            public boolean areItemsTheSame(@NonNull Filme oldItem, @NonNull Filme newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Filme oldItem, @NonNull Filme newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.listener = listener;
    }

    // Criação do ViewHolder (associa o layout ao ViewHolder)
    @NonNull
    @Override
    public FilmeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_adapter, parent, false);
        return new FilmeViewHolder(view);
    }

    // Associa os dados ao ViewHolder para a posição fornecida
    @Override
    public void onBindViewHolder(@NonNull FilmeViewHolder holder, int position) {
        // Obtém o item da lista usando o método getItem do ListAdapter
        Filme filme = getItem(position);
        holder.txtNome.setText(filme.getNome());
        holder.txtGenero.setText(filme.getGenero());
        holder.txtAvaliacao.setText(String.valueOf(filme.getAvaliacao()));
        holder.bind(filme, listener);
    }

    // Classe interna para o ViewHolder
    static class FilmeViewHolder extends RecyclerView.ViewHolder {

        TextView txtNome, txtGenero, txtAvaliacao;

        public FilmeViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inicializa as views do layout
            txtNome = itemView.findViewById(R.id.textTitulo);
            txtGenero = itemView.findViewById(R.id.textGenero);
            txtAvaliacao = itemView.findViewById(R.id.textAvaliacao);
        }

        public void bind(final Filme filme, final OnItemClickListener listener) {
            txtNome.setText(filme.getNome());
            txtGenero.setText(filme.getGenero());
            txtAvaliacao.setText(String.valueOf(filme.getAvaliacao()));

            itemView.setOnClickListener(v -> {
                listener.onItemClick(filme); // Chama o método onItemClick do listener
            });

            itemView.setOnLongClickListener(v -> {
                listener.onItemLongClick(filme); // Chama o método onItemLongClick do listener
                return true;
            });
        }
    }
}
