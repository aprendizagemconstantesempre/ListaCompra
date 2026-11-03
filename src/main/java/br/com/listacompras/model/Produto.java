package br.com.listacompras.model;

/**
 * Produto que o usuário costuma comprar, com descrição, unidade de medida
 * e foto opcional (RF010 a RF013). Corresponde ao depósito D3 do DFD.
 *
 * O "nome" herdado de Cadastro guarda a descrição do produto. Criamos
 * getDescricao() e setDescricao() só para o código ficar com o nome que
 * o usuário vê na tela.
 */
public class Produto extends Cadastro {

    private UnidadeMedida unidade;

    // Caminho do arquivo JPEG/PNG ou endereço da imagem. Nulo quando não há foto (RN039).
    private String caminhoFoto;

    public Produto(int id, String descricao, UnidadeMedida unidade, String caminhoFoto) {
        super(id, descricao);
        this.unidade = unidade;
        this.caminhoFoto = caminhoFoto;
    }

    public String getDescricao() {
        return getNome();
    }

    public void setDescricao(String descricao) {
        setNome(descricao);
    }

    public UnidadeMedida getUnidade() {
        return unidade;
    }

    public void setUnidade(UnidadeMedida unidade) {
        this.unidade = unidade;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }

    /** Sem foto, a tela mostra a imagem padrão (RN040). */
    public boolean temFoto() {
        return caminhoFoto != null && !caminhoFoto.isBlank();
    }

    /** Exemplo: "Pacote". */
    @Override
    public String getDetalhe() {
        return unidade.getDescricao();
    }

    @Override
    public String getMensagemEmUso() {
        return "Não é possível excluir: o produto está incluído em uma lista";
    }

    @Override
    public boolean estaVinculadoA(ItemLista item) {
        return item.getIdProduto() == getId();
    }
}
