package br.com.listacompras;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

/**
 * Classe que inicia a aplicacao.
 * Toda aplicacao JavaFX precisa de uma classe que herde de Application.
 */
public class App extends Application {

    @Override
    public void start(Stage palco) throws Exception {
        // Carregamos o arquivo FXML da tela principal, que o Scene Builder gerou
        Parent raiz = FXMLLoader.load(getClass().getResource("/view/MenuPrincipal.fxml"));

        // Colocamos a tela dentro de uma Scene e a Scene dentro do palco (a janela)
        Scene cena = new Scene(raiz);
        palco.setTitle("Lista de Compras");
        palco.setScene(cena);
        palco.show();
    }

    public static void main(String[] args) {
        // Este metodo entrega o controle para o JavaFX, que chama o start acima
        launch(args);
    }
}
