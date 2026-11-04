package br.com.listacompras.controller;

import br.com.listacompras.model.ListaCompra;
import br.com.listacompras.model.Repositorio;
import br.com.listacompras.util.BotoesLinha;
import br.com.listacompras.util.Mensagem;
import br.com.listacompras.util.Navegador;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
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
    @FXML private TableView<ListaCompra> tabelaListas;
    @FXML private TableColumn<ListaCompra, String> colunaNomeLista;
    @FXML private TableColumn<ListaCompra, String> colunaDetalhes;
    @FXML private TableColumn<ListaCompra, Void> colunaAcoes;
    @FXML private Button btnVoltar;

    /**
     * O JavaFX chama este metodo logo depois de montar a tela.
     * E aqui que preparamos a tabela e carregamos os dados salvos.
     */
    @Override
    public void initialize(URL url, ResourceBundle recursos) {
        colunaNomeLista.setCellValueFactory(linha -> new SimpleStringProperty(linha.getValue().getNome()));

        // Polimorfismo: cada tipo de cadastro responde getDetalhe() do seu jeito
        colunaDetalhes.setCellValueFactory(linha -> new SimpleStringProperty(linha.getValue().getDetalhe()));

        // Editar e excluir ficam na propria linha do registro (RN080)
        BotoesLinha.configurarColuna(colunaAcoes, this::editarLista, this::excluirLista);

        tabelaListas.setItems(FXCollections.observableArrayList(Repositorio.getInstancia().getListas()));
    }

    @FXML
    private void adicionarLista(ActionEvent evento) {
        // Proximo passo (RF002): validar o campo obrigatorio (NF005),
        // criar a lista e atualizar a tabela e o contador.
        Mensagem.emDesenvolvimento();
    }

    private void editarLista(ListaCompra lista) {
        // Proximo passo (RF004): alterar o nome da lista desta linha.
        Mensagem.emDesenvolvimento();
    }

    private void excluirLista(ListaCompra lista) {
        // Proximo passo (RF005): confirmar e remover a lista desta linha.
        Mensagem.emDesenvolvimento();
    }

    @FXML
    private void voltarAoMenu(ActionEvent evento) {
        // Voltamos para a tela principal do sistema
        Navegador.abrir(btnVoltar, "/view/MenuPrincipal.fxml");
    }
}
