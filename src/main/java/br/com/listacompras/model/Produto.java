package br.com.listacompras.model;

import br.com.listacompras.model.Enums.UnidadeMedida;

public class Produto extends Entidade {

    private String descricao;
    private UnidadeMedida unidade;
    private String caminhoFoto;

    public Produto() {
    }

    public Produto(String descricao, UnidadeMedida unidade, String caminhoFoto) {
        this.descricao = descricao;
        this.unidade = unidade;
        this.caminhoFoto = caminhoFoto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
        touch();
    }

    @Override
    public String toString() {
        return descricao != null ? descricao : "";
    }

    public UnidadeMedida getUnidade() {
        return unidade;
    }

    public void setUnidade(UnidadeMedida unidade) {
        this.unidade = unidade;
        touch();
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
        touch();
    }

    public boolean temFoto() {
        return caminhoFoto != null && !caminhoFoto.isBlank();
    }

    public String getDetalhe() {
        return (unidade == null) ? "" : unidade.getDescricao();
    }
}
