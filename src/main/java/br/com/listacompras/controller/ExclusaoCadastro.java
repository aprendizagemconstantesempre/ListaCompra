package br.com.listacompras.controller;

import br.com.listacompras.model.Cadastro;
import br.com.listacompras.model.Repositorio;
import br.com.listacompras.model.ValidacaoException;
import br.com.listacompras.util.Mensagem;

/**
 * Exclusão usada pelas três telas de cadastro: lista, estabelecimento e produto.
 *
 * POLIMORFISMO: o método recebe um Cadastro, sem saber se é uma ListaCompra,
 * um Estabelecimento ou um Produto. Se o cadastro estiver em uso, o
 * Repositorio lança o erro com cadastro.getMensagemEmUso(), e cada classe
 * devolve a própria frase (RN019, RN034 e RN048). Assim as três telas usam
 * o mesmo código.
 */
final class ExclusaoCadastro {

    private ExclusaoCadastro() {
    }

    /**
     * Pede confirmação (RN018, RN033 e RN047) e exclui o cadastro.
     *
     * @param tipo como o cadastro é chamado na pergunta, por exemplo "a lista"
     * @return true se o cadastro foi excluído; false se o usuário desistiu ou se ele está em uso
     */
    static boolean excluir(Cadastro cadastro, String tipo) {
        if (cadastro == null) {
            return false;
        }

        if (!Mensagem.confirmar("Deseja realmente excluir " + tipo + " \"" + cadastro.getNome() + "\"?")) {
            return false;
        }

        try {
            Repositorio.getInstancia().excluir(cadastro);
            return true;
        } catch (ValidacaoException erro) {
            Mensagem.avisar(erro.getMessage());
            return false;
        }
    }
}
