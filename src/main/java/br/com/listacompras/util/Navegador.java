package br.com.listacompras.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Classe auxiliar para trocar de tela.
 * Criamos ela para nao repetir o mesmo codigo de navegacao em cada controller.
 */
public class Navegador {

    /**
     * Troca o conteudo da janela atual pela tela informada.
     *
     * @param componenteDaTela qualquer componente da tela que esta aberta agora
     * @param caminhoFxml      caminho do FXML de destino, por exemplo "/view/CadastroLista.fxml"
     */
    public static void abrir(Node componenteDaTela, String caminhoFxml) {
        try {
            // Carregamos o FXML de destino
            Parent novaTela = FXMLLoader.load(Navegador.class.getResource(caminhoFxml));

            // Descobrimos qual janela esta aberta a partir do componente que recebemos
            Stage palco = (Stage) componenteDaTela.getScene().getWindow();

            // Trocamos a cena da janela pela tela nova
            palco.setScene(new Scene(novaTela));
        } catch (Exception erro) {
            // Por enquanto apenas mostramos o erro no console para facilitar a depuracao
            erro.printStackTrace();
        }
    }
}
