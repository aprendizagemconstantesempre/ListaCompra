package br.com.listacompras.util;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;

public final class Encerramento {

    private Encerramento() {
    }

    public static boolean confirmarSaida(Scene cena) {
        String pergunta = temCadastroEmAndamento(cena)
                ? "Há um cadastro em andamento que ainda não foi confirmado. Deseja sair mesmo assim?"
                : "Deseja realmente sair do sistema?";

        return Mensagem.confirmar(pergunta);
    }

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
