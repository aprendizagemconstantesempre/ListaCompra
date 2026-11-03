package br.com.listacompras.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Janelas de mensagem para o usuário.
 * Criamos esta classe para que todas as telas mostrem as mensagens do mesmo jeito.
 */
public final class Mensagem {

    // Construtor privado: só usamos os métodos, sem criar objetos Mensagem
    private Mensagem() {
    }

    /** Mostra uma janela de informação e espera o usuário clicar em OK. */
    public static void informar(String texto) {
        Alert janela = new Alert(AlertType.INFORMATION);
        janela.setTitle("Lista de Compras");
        janela.setHeaderText(null);
        janela.setContentText(texto);
        janela.showAndWait();
    }

    /** Aviso provisório para os botões cuja função ainda vai ser programada. */
    public static void emDesenvolvimento() {
        informar("Função em desenvolvimento...");
    }
}
