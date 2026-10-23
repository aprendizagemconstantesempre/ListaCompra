package br.com.listacompras.controller;

import br.com.listacompras.util.Navegador;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller da tela de produtos (requisitos RF010 a RF013).
 */
public class CadastroProdutoController implements Initializable {

    @FXML private ImageView previaFoto;
    @FXML private Button btnEscolherFoto;
    @FXML private TextField campoDescricao;
    @FXML private ComboBox<String> comboUnidade;
    @FXML private Button btnAdicionar;
    @FXML private Label rotuloContador;

    // Dentro deste painel vamos criar um cartao para cada produto cadastrado
    @FXML private FlowPane painelProdutos;
    @FXML private Button btnVoltar;

    @Override
    public void initialize(URL url, ResourceBundle recursos) {
        // Proximo passo: limpar o cartao de exemplo que deixamos no FXML
        // e montar um cartao para cada produto ja gravado.
    }

    @FXML
    private void escolherFoto(ActionEvent evento) {
        // Proximo passo (NF006): abrir um FileChooser para o usuario escolher
        // a imagem do produto e mostrar a previa no ImageView.
    }

    @FXML
    private void adicionarProduto(ActionEvent evento) {
        // Proximo passo (RF010): validar a descricao, montar o produto com
        // foto e unidade de medida e criar o cartao correspondente.
    }

    @FXML
    private void voltarAoMenu(ActionEvent evento) {
        Navegador.abrir(btnVoltar, "/view/MenuPrincipal.fxml");
    }
}
