package br.com.listacompras.controller;

import br.com.listacompras.model.ItemLista;
import br.com.listacompras.util.BotoesLinha;
import br.com.listacompras.util.Mensagem;
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
    @FXML private TableView<ItemLista> tabelaItens;
    @FXML private TableColumn<ItemLista, String> colunaProduto;
    @FXML private TableColumn<ItemLista, String> colunaUnidade;
    @FXML private TableColumn<ItemLista, String> colunaQuantidade;
    @FXML private TableColumn<ItemLista, String> colunaValorUnitario;
    @FXML private TableColumn<ItemLista, String> colunaSubtotal;
    @FXML private TableColumn<ItemLista, Void> colunaAcoes;

    @FXML private Label rotuloTotalItens;
    @FXML private Label rotuloValorTotal;
    @FXML private Button btnVoltar;

    @Override
    public void initialize(URL url, ResourceBundle recursos) {
        // Proximo passo: preencher as tres listas suspensas com as listas,
        // os estabelecimentos e os produtos que ja foram cadastrados.

        // Editar e excluir ficam na propria linha do item (RN080)
        BotoesLinha.configurarColuna(colunaAcoes, this::editarItem, this::excluirItem);
    }

    @FXML
    private void adicionarItem(ActionEvent evento) {
        // Proximo passo (RF014): montar o item com produto, quantidade e valor,
        // adicionar na tabela e recalcular os totalizadores do rodape.
        Mensagem.emDesenvolvimento();
    }

    private void editarItem(ItemLista item) {
        // Proximo passo (RF016): alterar quantidade ou valor do item desta linha.
        Mensagem.emDesenvolvimento();
    }

    private void excluirItem(ItemLista item) {
        // Proximo passo (RF017): remover o item desta linha e atualizar os totais.
        Mensagem.emDesenvolvimento();
    }

    @FXML
    private void voltarAoMenu(ActionEvent evento) {
        Navegador.abrir(btnVoltar, "/view/MenuPrincipal.fxml");
    }
}
