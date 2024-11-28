package com.example.app_cinetalk.model;

import java.util.Objects;

public class Filme {

    private String id;
    private String nome;
    private String genero;
    private double avaliacao;
    private String comentario;
    private Usuario usuario;

    public Filme(String nome, String genero, double avaliacao, String comentario) {
        this.nome = nome;
        this.genero = genero;
        this.avaliacao = avaliacao;
        this.comentario = comentario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public double getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(double avaliacao) {
        this.avaliacao = avaliacao;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Filme filme = (Filme) o;
        return Double.compare(avaliacao, filme.avaliacao) == 0 && Objects.equals(id, filme.id) && Objects.equals(nome, filme.nome) && Objects.equals(genero, filme.genero) && Objects.equals(comentario, filme.comentario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, genero, avaliacao, comentario);
    }
}
