package br.com.listacompras.model;

/**
 * Um produto colocado em uma lista de compras, com quantidade e valor
 * (RF014 a RF017). Corresponde ao depósito D4 do DFD, que é a associação
 * entre lista, estabelecimento e produto.
 *
 * O item guarda os ids da lista, do estabelecimento e do produto, e não os
 * objetos inteiros. Assim, quando o usuário edita um produto, todos os itens
 * passam a mostrar o produto atualizado, e o arquivo JSON (Fase 4) não fica
 * com cópias repetidas.
 *
 * Esta classe não herda de Cadastro porque o item não tem nome próprio e
 * não é cadastrado sozinho: ele só existe dentro de uma lista.
 */
public class ItemLista {

    private int id;
    private int idLista;

    // Integer (e não int) porque pode ficar nulo: o estabelecimento é opcional (RN051)
    private Integer idEstabelecimento;

    private int idProduto;

    // Unidade que o produto tinha no momento da inclusão (RN056)
    private UnidadeMedida unidade;

    private double quantidade;

    // Double (e não double) porque pode ficar nulo: o valor é opcional (RN054)
    private Double valorUnitario;

    public ItemLista(int id, int idLista, Integer idEstabelecimento, int idProduto,
                     UnidadeMedida unidade, double quantidade, Double valorUnitario) {
        this.id = id;
        this.idLista = idLista;
        this.idEstabelecimento = idEstabelecimento;
        this.idProduto = idProduto;
        this.unidade = unidade;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
    }

    public int getId() {
        return id;
    }

    public int getIdLista() {
        return idLista;
    }

    public Integer getIdEstabelecimento() {
        return idEstabelecimento;
    }

    public int getIdProduto() {
        return idProduto;
    }

    public UnidadeMedida getUnidade() {
        return unidade;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public Double getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(Double valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    /** Usado quando o mesmo produto é incluído de novo na lista (RN055). */
    public void somarQuantidade(double quantidadeExtra) {
        this.quantidade = this.quantidade + quantidadeExtra;
    }

    /** Quantidade vezes valor unitário. Sem valor informado, o subtotal é zero (RN060). */
    public double getSubtotal() {
        if (valorUnitario == null) {
            return 0;
        }
        return quantidade * valorUnitario;
    }

    /** Descrição do produto, buscada no repositório pelo id. A tabela da tela usa este método. */
    public String getDescricaoProduto() {
        Produto produto = Repositorio.getInstancia().buscarProduto(idProduto);
        return (produto == null) ? "" : produto.getDescricao();
    }
}
