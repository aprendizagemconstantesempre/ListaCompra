package br.com.listacompras.controller;

import br.com.listacompras.util.Navegador;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller da tela de cadastro de listas de compras.
 * Deixamos os metodos preparados para que possamos implementar a regra
 * de negocio dos requisitos RF002 a RF005.
 */
public class CadastroListaController implements Initializable {

    @FXML private TextField campoNomeLista;
    @FXML private Button btnAdicionar;
    @FXML private Label rotuloContador;
    @FXML private TableView<String> tabelaListas;
    @FXML private TableColumn<String, String> colunaNomeLista;
    @FXML private Button btnVoltar;
    @FXML private Button btnEditar;
    @FXML private Button btnExcluir;

    /**
     * O JavaFX chama este metodo logo depois de montar a tela.
     * E aqui que preparamos a tabela e carregamos os dados salvos.
     */
    @Override
    public void initialize(URL url, ResourceBundle recursos) {
        // Proximo passo: dizer a coluna de onde ela tira o texto de cada linha
        // e carregar as listas que ja estao gravadas no arquivo (NF001).
    }

    @FXML
    private void adicionarLista(ActionEvent evento) {
        // Proximo passo (RF002): validar o campo obrigatorio (NF005),
        // criar a lista e atualizar a tabela e o contador.
    }

    @FXML
    private void editarLista(ActionEvent evento) {
        // Proximo passo (RF004): pegar a linha selecionada na tabela e alterar o nome.
    }

    @FXML
    private void excluirLista(ActionEvent evento) {
        // Proximo passo (RF005): remover da tabela a lista que estiver selecionada.
    }

    @FXML
    private void voltarAoMenu(ActionEvent evento) {
        // Voltamos para a tela principal do sistema
        Navegador.abrir(btnVoltar, "/view/MenuPrincipal.fxml");
    }
}
