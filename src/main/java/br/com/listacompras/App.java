package br.com.listacompras;

import br.com.listacompras.data.Repositorio;
import br.com.listacompras.util.Encerramento;
import br.com.listacompras.util.Mensagem;
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
        // Antes de mostrar qualquer tela, carregamos os dados do arquivo JSON (RN075).
        // Se o arquivo nao existir, ele e criado vazio (RN076).
        try {
            new Repositorio().inicializar();
        } catch (Exception erro) {
            Mensagem.avisar(erro.getMessage());
        }

        // Carregamos o arquivo FXML da tela principal, que o Scene Builder gerou
        Parent raiz = FXMLLoader.load(getClass().getResource("/view/MenuPrincipal.fxml"));

        // Colocamos a tela dentro de uma Scene e a Scene dentro do palco (a janela)
        Scene cena = new Scene(raiz);
        palco.setTitle("Lista de Compras");
        palco.setScene(cena);

        // Fechar pelo "X" da janela segue as mesmas regras do botao Sair (RN070 a RN072)
        palco.setOnCloseRequest(evento -> {
            if (!Encerramento.confirmarSaida(palco.getScene())) {
                evento.consume(); // consume() cancela o fechamento
            }
        });

        palco.show();
    }

    public static void main(String[] args) {
        // Este metodo entrega o controle para o JavaFX, que chama o start acima
        launch(args);
    }
}
