package br.com.listacompras.util;

import br.com.listacompras.model.Repositorio;
import br.com.listacompras.model.ValidacaoException;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;

/**
 * Regras para encerrar o sistema (RF018). Usada pelo botão "Sair do sistema"
 * do menu e pelo "X" da janela, para que os dois façam exatamente a mesma coisa.
 */
public final class Encerramento {

    private Encerramento() {
    }

    /**
     * Pergunta se o usuário quer sair (RN070). Se houver algo digitado e ainda
     * não confirmado na tela aberta, a pergunta avisa disso (RN072). Quando o
     * usuário confirma, grava o arquivo antes de fechar (RN071).
     *
     * @param cena a tela que está aberta agora
     * @return true se pode encerrar; false se o usuário desistiu
     */
    public static boolean confirmarSaida(Scene cena) {
        String pergunta = temCadastroEmAndamento(cena)
                ? "Há um cadastro em andamento que ainda não foi confirmado. Deseja sair mesmo assim?"
                : "Deseja realmente sair do sistema?";

        if (!Mensagem.confirmar(pergunta)) {
            return false;
        }

        try {
            Repositorio.getInstancia().gravarTudo();
        } catch (ValidacaoException erro) {
            // Se não deu para gravar, avisamos e deixamos o usuário decidir
            return Mensagem.confirmar(erro.getMessage() + ". Deseja sair mesmo assim?");
        }
        return true;
    }

    /**
     * Procura algum campo de texto preenchido na tela. O campo de quantidade
     * começa com "1", então ele só conta se o usuário trocou esse valor.
     */
    private static boolean temCadastroEmAndamento(Scene cena) {
        if (cena == null) {
            return false;
        }
        for (Node componente : cena.getRoot().lookupAll(".text-field")) {
            if (componente instanceof TextField campo && campo.getText() != null && !campo.getText().isBlank()) {
                boolean quantidadePadrao = "campoQuantidade".equals(campo.getId())
                        && campo.getText().trim().equals("1");
                if (!quantidadePadrao) {
                    return true;
                }
            }
        }
        return false;
    }
}
