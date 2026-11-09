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
     * Troca o conteudo da janela atual pela tela informada e devolve o
     * controller da tela nova.
     *
     * PASSAGEM DE DADOS: com o controller em maos, a tela de origem pode
     * entregar um objeto para a tela de destino. Exemplo, no Cadastro de Lista:
     * <pre>
     *   CriarListaController destino = Navegador.abrir(botao, "/view/CriarLista.fxml");
     *   destino.setListaSelecionada(lista);
     * </pre>
     * Quem nao precisa passar nada simplesmente ignora o retorno.
     *
     * @param componenteDaTela qualquer componente da tela que esta aberta agora
     * @param caminhoFxml      caminho do FXML de destino, por exemplo "/view/CadastroLista.fxml"
     * @param <T>              tipo do controller da tela de destino
     */
    public static <T> T abrir(Node componenteDaTela, String caminhoFxml) {
        try {
            // Usamos um objeto FXMLLoader (e nao o FXMLLoader.load direto) para
            // conseguir pegar o controller depois de carregar a tela
            FXMLLoader carregador = new FXMLLoader(Navegador.class.getResource(caminhoFxml));
            Parent novaTela = carregador.load();

            // Descobrimos qual janela esta aberta a partir do componente que recebemos
            Stage palco = (Stage) componenteDaTela.getScene().getWindow();

            // Trocamos a cena da janela pela tela nova
            palco.setScene(new Scene(novaTela));
            return carregador.getController();
        } catch (Exception erro) {
            erro.printStackTrace();
            Mensagem.avisar("Não foi possível abrir a tela " + caminhoFxml);
            return null;
        }
    }
}
