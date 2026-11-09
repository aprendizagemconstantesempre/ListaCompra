package br.com.listacompras.model;

public class ListaCompra extends Entidade {

    private String nome;

    public ListaCompra() {
    }

    public ListaCompra(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
        touch();
    }

    @Override
    public String toString() {
        return nome != null ? nome : "";
    }
}
