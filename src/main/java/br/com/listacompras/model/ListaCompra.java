package br.com.listacompras.model;

import br.com.listacompras.util.Formatador;

import java.util.List;

/**
 * Lista de compras criada pelo usuário, por exemplo "Compras da semana"
 * (RF002 a RF005). Corresponde ao depósito D1 do DFD.
 *
 * Herda de Cadastro o id, o nome e a comparação de nomes. Os itens da lista
 * ficam guardados no Repositorio (depósito D4), ligados a ela pelo id.
 */
public class ListaCompra extends Cadastro {

    public ListaCompra(int id, String nome) {
        // super(...) chama o construtor da classe pai, que guarda o id e o nome
        super(id, nome);
    }

    /** Construtor vazio usado pelo Gson ao ler o arquivo JSON. */
    private ListaCompra() {
    }

    /** Exemplo: "3 produtos — R$ 92,00". */
    @Override
    public String getDetalhe() {
        Repositorio repositorio = Repositorio.getInstancia();
        List<ItemLista> itens = repositorio.itensDaLista(this);

        String produtos = (itens.size() == 1) ? "1 produto" : itens.size() + " produtos";
        return produtos + " — " + Formatador.moeda(repositorio.somarValores(itens));
    }

    @Override
    public String getMensagemEmUso() {
        return "Não é possível excluir: a lista possui itens vinculados";
    }

    /** O item pertence a esta lista quando guarda o id dela. */
    @Override
    public boolean estaVinculadoA(ItemLista item) {
        return item.getIdLista() == getId();
    }
}
