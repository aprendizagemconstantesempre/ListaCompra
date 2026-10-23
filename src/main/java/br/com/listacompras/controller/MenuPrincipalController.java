package br.com.listacompras.controller;

import br.com.listacompras.util.Navegador;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Controller da tela principal (RF001).
 * Aqui apenas decidimos para qual tela o usuario vai quando clica em cada opcao.
 */
public class MenuPrincipalController {

    // Cada campo abaixo corresponde a um fx:id que colocamos no arquivo FXML
    @FXML private Button btnCadastrarLista;
    @FXML private Button btnCadastrarEstabelecimento;
    @FXML private Button btnCadastrarProduto;
    @FXML private Button btnCriarLista;
    @FXML private Button btnSair;

    @FXML
    private void abrirCadastroLista(ActionEvent evento) {
        // Pedimos ao navegador que troque a tela atual pela tela de cadastro de listas
        Navegador.abrir(btnCadastrarLista, "/view/CadastroLista.fxml");
    }

    @FXML
    private void abrirCadastroEstabelecimento(ActionEvent evento) {
        Navegador.abrir(btnCadastrarEstabelecimento, "/view/CadastroEstabelecimento.fxml");
    }

    @FXML
    private void abrirCadastroProduto(ActionEvent evento) {
        Navegador.abrir(btnCadastrarProduto, "/view/CadastroProduto.fxml");
    }

    @FXML
    private void abrirCriarLista(ActionEvent evento) {
        Navegador.abrir(btnCriarLista, "/view/CriarLista.fxml");
    }

    @FXML
    private void sairDoSistema(ActionEvent evento) {
        // Encerramos a aplicacao conforme o requisito RF018
        Platform.exit();
    }
}
