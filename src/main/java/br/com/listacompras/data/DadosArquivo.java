package br.com.listacompras.data;

import br.com.listacompras.model.Estabelecimento;
import br.com.listacompras.model.ItemLista;
import br.com.listacompras.model.ListaCompra;
import br.com.listacompras.model.Produto;

import java.util.ArrayList;
import java.util.List;

/**
 * Estrutura de dados serializada no arquivo JSON.
 */
public class DadosArquivo {
    public List<ListaCompra> listas = new ArrayList<>();
    public List<Estabelecimento> estabelecimentos = new ArrayList<>();
    public List<Produto> produtos = new ArrayList<>();
    public List<ItemLista> itens = new ArrayList<>();
}
