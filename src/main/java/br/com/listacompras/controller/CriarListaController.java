package br.com.listacompras.controller;

import br.com.listacompras.util.Navegador;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller da tela onde montamos a lista (requisitos RF014 a RF017).
 */
public class CriarListaController implements Initializable {

    @FXML private ComboBox<String> comboLista;
    @FXML private ComboBox<String> comboEstabelecimento;
    @FXML private ComboBox<String> comboProduto;
    @FXML private TextField campoQuantidade;
    @FXML private TextField campoValorUnitario;
    @FXML private Button btnAdicionarItem;

    @FXML private Label rotuloItens;
    @FXML private Label rotuloContador;
    @FXML private TableView<Object> tabelaItens;
    @FXML private TableColumn<Object, String> colunaProduto;
    @FXML private TableColumn<Object, String> colunaUnidade;
    @FXML private TableColumn<Object, String> colunaQuantidade;
    @FXML private TableColumn<Object, String> colunaValorUnitario;
    @FXML private TableColumn<Object, String> colunaSubtotal;

    @FXML private Label rotuloTotalItens;
    @FXML private Label rotuloValorTotal;
    @FXML private Button btnVoltar;
    @FXML private Button btnEditarItem;
    @FXML private Button btnExcluirItem;

    @Override
    public void initialize(URL url, ResourceBundle recursos) {
        // Proximo passo: preencher as tres listas suspensas com as listas,
        // os estabelecimentos e os produtos que ja foram cadastrados.
    }

    @FXML
    private void adicionarItem(ActionEvent evento) {
        // Proximo passo (RF014): montar o item com produto, quantidade e valor,
        // adicionar na tabela e recalcular os totalizadores do rodape.
    }

    @FXML
    private void editarItem(ActionEvent evento) {
        // Proximo passo (RF016): alterar quantidade ou valor do item selecionado.
    }

    @FXML
    private void excluirItem(ActionEvent evento) {
        // Proximo passo (RF017): remover o item selecionado e atualizar os totais.
    }

    @FXML
    private void voltarAoMenu(ActionEvent evento) {
        Navegador.abrir(btnVoltar, "/view/MenuPrincipal.fxml");
    }
}
