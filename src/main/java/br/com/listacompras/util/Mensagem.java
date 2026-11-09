package br.com.listacompras.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Janelas de mensagem para o usuário.
 * Criamos esta classe para que todas as telas mostrem as mensagens do mesmo jeito.
 */
public final class Mensagem {

    private static final String TITULO = "Lista de Compras";

    // Construtor privado: só usamos os métodos, sem criar objetos Mensagem
    private Mensagem() {
    }

    /** Mostra uma janela de informação, por exemplo "Lista cadastrada com sucesso". */
    public static void informar(String texto) {
        mostrar(AlertType.INFORMATION, texto);
    }

    /**
     * Mostra uma janela de aviso, usada quando uma regra não é cumprida
     * (campo obrigatório, nome repetido, exclusão de cadastro em uso...).
     */
    public static void avisar(String texto) {
        mostrar(AlertType.WARNING, texto);
    }

    /**
     * Pergunta ao usuário e devolve true só se ele clicar em "Sim".
     * Usada antes de excluir e antes de sair (RN018, RN033, RN047, RN067 e RN070).
     */
    public static boolean confirmar(String pergunta) {
        ButtonType sim = new ButtonType("Sim", ButtonData.YES);
        ButtonType nao = new ButtonType("Não", ButtonData.NO);

        Alert janela = new Alert(AlertType.CONFIRMATION, pergunta, sim, nao);
        janela.setTitle(TITULO);
        janela.setHeaderText(null);

        Optional<ButtonType> resposta = janela.showAndWait();
        return resposta.isPresent() && resposta.get() == sim;
    }

    private static void mostrar(AlertType tipo, String texto) {
        Alert janela = new Alert(tipo);
        janela.setTitle(TITULO);
        janela.setHeaderText(null);
        janela.setContentText(texto);
        janela.showAndWait();
    }
}
