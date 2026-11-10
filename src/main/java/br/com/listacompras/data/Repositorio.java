package br.com.listacompras.data;

import br.com.listacompras.model.Estabelecimento;
import br.com.listacompras.model.ItemLista;
import br.com.listacompras.model.ListaCompra;
import br.com.listacompras.model.Produto;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class Repositorio {

    private static final Path ARQUIVO = Path.of("dados", "lista_compras.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Repositorio() {
    }

    public void inicializar() {
        if (!Files.exists(ARQUIVO)) {
            gravarArquivo(new DadosArquivo());
        }
    }

    private synchronized DadosArquivo lerArquivo() {
        if (!Files.exists(ARQUIVO)) return new DadosArquivo();
        try {
            String json = Files.readString(ARQUIVO, StandardCharsets.UTF_8);
            DadosArquivo dados = gson.fromJson(json, DadosArquivo.class);
            return (dados != null) ? dados : new DadosArquivo();
        } catch (Exception e) {
            return new DadosArquivo();
        }
    }

    private synchronized void gravarArquivo(DadosArquivo dados) {
        try {
            if (ARQUIVO.getParent() != null) {
                Files.createDirectories(ARQUIVO.getParent());
            }
            Files.writeString(ARQUIVO, gson.toJson(dados), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    // =========================================================================
    // LISTAS DE COMPRA
    // =========================================================================

    public List<ListaCompra> listarListasDeCompra() {
        return lerArquivo().listas;
    }

    public ListaCompra buscarListaDeCompraPorId(String id) {
        return listarListasDeCompra().stream()
                .filter(l -> Objects.equals(l.getId(), id))
                .findFirst().orElse(null);
    }

    public void salvarListaDeCompra(ListaCompra lista) {
        lista.touch();
        DadosArquivo dados = lerArquivo();
        dados.listas.removeIf(l -> Objects.equals(l.getId(), lista.getId()));
        dados.listas.add(lista);
        gravarArquivo(dados);
    }

    public boolean deletarListaDeCompra(String id) {
        DadosArquivo dados = lerArquivo();
        boolean removido = dados.listas.removeIf(l -> Objects.equals(l.getId(), id));
        if (removido) gravarArquivo(dados);
        return removido;
    }

    // =========================================================================
    // ESTABELECIMENTOS
    // =========================================================================

    public List<Estabelecimento> listarEstabelecimentos() {
        return lerArquivo().estabelecimentos;
    }

    public Estabelecimento buscarEstabelecimentoPorId(String id) {
        if (id == null) return null;
        return listarEstabelecimentos().stream()
                .filter(e -> Objects.equals(e.getId(), id))
                .findFirst().orElse(null);
    }

    public void salvarEstabelecimento(Estabelecimento est) {
        est.touch();
        DadosArquivo dados = lerArquivo();
        dados.estabelecimentos.removeIf(e -> Objects.equals(e.getId(), est.getId()));
        dados.estabelecimentos.add(est);
        gravarArquivo(dados);
    }

    public boolean deletarEstabelecimento(String id) {
        DadosArquivo dados = lerArquivo();
        boolean removido = dados.estabelecimentos.removeIf(e -> Objects.equals(e.getId(), id));
        if (removido) gravarArquivo(dados);
        return removido;
    }

    // =========================================================================
    // PRODUTOS
    // =========================================================================

    public List<Produto> listarProdutos() {
        return lerArquivo().produtos;
    }

    public Produto buscarProdutoPorId(String id) {
        return listarProdutos().stream()
                .filter(p -> Objects.equals(p.getId(), id))
                .findFirst().orElse(null);
    }

    public void salvarProduto(Produto prod) {
        prod.touch();
        DadosArquivo dados = lerArquivo();
        dados.produtos.removeIf(p -> Objects.equals(p.getId(), prod.getId()));
        dados.produtos.add(prod);
        gravarArquivo(dados);
    }

    public boolean deletarProduto(String id) {
        DadosArquivo dados = lerArquivo();
        boolean removido = dados.produtos.removeIf(p -> Objects.equals(p.getId(), id));
        if (removido) gravarArquivo(dados);
        return removido;
    }

    // =========================================================================
    // ITENS
    // =========================================================================

    public List<ItemLista> listarItens() {
        return lerArquivo().itens;
    }

    public ItemLista buscarItemPorId(String id) {
        return listarItens().stream()
                .filter(i -> Objects.equals(i.getId(), id))
                .findFirst().orElse(null);
    }

    public List<ItemLista> listarItensPorLista(String idLista) {
        return listarItens().stream()
                .filter(i -> Objects.equals(i.getIdLista(), idLista))
                .toList();
    }

    public List<ItemLista> listarItensPorListaEEstabelecimento(String idLista, String idEstabelecimento) {
        return listarItens().stream()
                .filter(i -> Objects.equals(i.getIdLista(), idLista) && Objects.equals(i.getIdEstabelecimento(), idEstabelecimento))
                .toList();
    }

    public ItemLista buscarItem(String idLista, String idEstabelecimento, String idProduto) {
        return listarItens().stream()
                .filter(i -> Objects.equals(i.getIdLista(), idLista)
                        && Objects.equals(i.getIdEstabelecimento(), idEstabelecimento)
                        && Objects.equals(i.getIdProduto(), idProduto))
                .findFirst().orElse(null);
    }

    public void salvarItem(ItemLista item) {
        DadosArquivo dados = lerArquivo();
        dados.itens.removeIf(i -> Objects.equals(i.getId(), item.getId()));
        dados.itens.add(item);
        gravarArquivo(dados);
    }

    public boolean deletarItem(String id) {
        DadosArquivo dados = lerArquivo();
        boolean removido = dados.itens.removeIf(i -> Objects.equals(i.getId(), id));
        if (removido) gravarArquivo(dados);
        return removido;
    }
}
