package br.com.listacompras.controller;

import br.com.listacompras.model.Produto;
import br.com.listacompras.model.Repositorio;
import br.com.listacompras.util.BotoesLinha;
import br.com.listacompras.util.Mensagem;
import br.com.listacompras.util.Navegador;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.File;
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
        // Tiramos o cartao de exemplo que existe no FXML so para o Scene Builder
        painelProdutos.getChildren().clear();

        for (Produto produto : Repositorio.getInstancia().getProdutos()) {
            painelProdutos.getChildren().add(criarCartao(produto));
        }
    }

    /** Monta o cartao de um produto, com os botoes dentro do proprio cartao (RN080). */
    private VBox criarCartao(Produto produto) {
        ImageView foto = new ImageView(carregarFoto(produto));
        foto.setFitWidth(110);
        foto.setFitHeight(78);
        foto.setPreserveRatio(true);

        StackPane areaFoto = new StackPane(foto);
        areaFoto.getStyleClass().add("area-foto");
        areaFoto.setPrefHeight(90);

        Label descricao = new Label(produto.getDescricao());
        descricao.getStyleClass().add("titulo-opcao");

        // Polimorfismo: aqui getDetalhe() devolve a unidade, como "Pacote"
        Label detalhe = new Label(produto.getDetalhe());
        detalhe.getStyleClass().add("descricao-opcao");

        VBox cartao = new VBox(6, areaFoto, descricao, detalhe,
                BotoesLinha.criar(() -> editarProduto(produto), () -> excluirProduto(produto)));
        cartao.getStyleClass().add("cartao");
        cartao.setPrefWidth(170);
        return cartao;
    }

    /** Devolve a foto do produto ou a imagem padrao, se nao houver foto valida (RN040). */
    private Image carregarFoto(Produto produto) {
        if (produto.temFoto()) {
            String caminho = produto.getCaminhoFoto();
            // O campo guarda um arquivo do computador ou um endereco da internet
            String url = caminho.contains("://") ? caminho : new File(caminho).toURI().toString();
            Image foto = new Image(url);
            if (!foto.isError()) {
                return foto;
            }
        }
        return new Image(getClass().getResource("/img/produto_padrao.png").toExternalForm());
    }

    @FXML
    private void escolherFoto(ActionEvent evento) {
        // Proximo passo (NF006): abrir um FileChooser para o usuario escolher
        // a imagem do produto e mostrar a previa no ImageView.
        Mensagem.emDesenvolvimento();
    }

    @FXML
    private void adicionarProduto(ActionEvent evento) {
        // Proximo passo (RF010): validar a descricao, montar o produto com
        // foto e unidade de medida e criar o cartao correspondente.
        Mensagem.emDesenvolvimento();
    }

    private void editarProduto(Produto produto) {
        // Proximo passo (RF012): alterar os dados do produto deste cartao.
        Mensagem.emDesenvolvimento();
    }

    private void excluirProduto(Produto produto) {
        // Proximo passo (RF013): confirmar e excluir o produto deste cartao.
        Mensagem.emDesenvolvimento();
    }

    @FXML
    private void voltarAoMenu(ActionEvent evento) {
        Navegador.abrir(btnVoltar, "/view/MenuPrincipal.fxml");
    }
}
