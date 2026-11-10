package br.com.listacompras.controller;

import br.com.listacompras.data.Repositorio;
import br.com.listacompras.model.Entidade;
import br.com.listacompras.model.Estabelecimento;
import br.com.listacompras.model.ListaCompra;
import br.com.listacompras.model.Produto;
import br.com.listacompras.util.Mensagem;

import java.util.Objects;

final class ExclusaoCadastro {

    private ExclusaoCadastro() {
    }

    static boolean excluir(Entidade entidade, String tipo) {
        if (entidade == null) {
            return false;
        }

        String nomeItem = "";
        if (entidade instanceof ListaCompra l) {
            nomeItem = l.getNome();
        } else if (entidade instanceof Estabelecimento e) {
            nomeItem = e.getNome();
        } else if (entidade instanceof Produto p) {
            nomeItem = p.getDescricao();
        }

        if (!Mensagem.confirmar("Deseja realmente excluir " + tipo + " \"" + nomeItem + "\"?")) {
            return false;
        }

        Repositorio repositorio = new Repositorio();
        boolean emUso = false;
        String mensagemEmUso = "";

        if (entidade instanceof ListaCompra l) {
            emUso = repositorio.listarItens().stream().anyMatch(i -> Objects.equals(i.getIdLista(), l.getId()));
            mensagemEmUso = "Não é possível excluir: a lista possui itens vinculados";
        } else if (entidade instanceof Estabelecimento e) {
            emUso = repositorio.listarItens().stream().anyMatch(i -> Objects.equals(i.getIdEstabelecimento(), e.getId()));
            mensagemEmUso = "Não é possível excluir: o estabelecimento possui itens vinculados";
        } else if (entidade instanceof Produto p) {
            emUso = repositorio.listarItens().stream().anyMatch(i -> Objects.equals(i.getIdProduto(), p.getId()));
            mensagemEmUso = "Não é possível excluir: o produto está incluído em uma lista";
        }

        if (emUso) {
            Mensagem.avisar(mensagemEmUso);
            return false;
        }

        if (entidade instanceof ListaCompra l) {
            repositorio.deletarListaDeCompra(l.getId());
        } else if (entidade instanceof Estabelecimento e) {
            repositorio.deletarEstabelecimento(e.getId());
        } else if (entidade instanceof Produto p) {
            repositorio.deletarProduto(p.getId());
        }
        return true;
    }
}
