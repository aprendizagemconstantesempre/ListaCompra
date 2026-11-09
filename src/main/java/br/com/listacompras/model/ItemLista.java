package br.com.listacompras.model;

import br.com.listacompras.model.Enums.UnidadeMedida;

public class ItemLista extends Entidade {

    private String idLista;
    private String idEstabelecimento;
    private String idProduto;
    private UnidadeMedida unidade;
    private double quantidade;
    private Double valorUnitario;

    public ItemLista() {
    }

    public ItemLista(String idLista, String idEstabelecimento, String idProduto,
                     UnidadeMedida unidade, double quantidade, Double valorUnitario) {
        this.idLista = idLista;
        this.idEstabelecimento = idEstabelecimento;
        this.idProduto = idProduto;
        this.unidade = unidade;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
    }

    public String getIdLista() {
        return idLista;
    }

    public void setIdLista(String idLista) {
        this.idLista = idLista;
        touch();
    }

    public String getIdEstabelecimento() {
        return idEstabelecimento;
    }

    public void setIdEstabelecimento(String idEstabelecimento) {
        this.idEstabelecimento = idEstabelecimento;
        touch();
    }

    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
        touch();
    }

    public UnidadeMedida getUnidade() {
        return unidade;
    }

    public void setUnidade(UnidadeMedida unidade) {
        this.unidade = unidade;
        touch();
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
        touch();
    }

    public Double getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(Double valorUnitario) {
        this.valorUnitario = valorUnitario;
        touch();
    }

    public void somarQuantidade(double quantidadeMais) {
        this.quantidade += quantidadeMais;
        touch();
    }

    public double getSubtotal() {
        if (valorUnitario == null) {
            return 0;
        }
        return quantidade * valorUnitario;
    }
}
