package br.com.listacompras.controller;

import br.com.listacompras.model.Produto;
import br.com.listacompras.model.Repositorio;
import br.com.listacompras.model.UnidadeMedida;
import br.com.listacompras.model.ValidacaoException;
import br.com.listacompras.util.BotoesLinha;
import br.com.listacompras.util.Formatador;
import br.com.listacompras.util.Mensagem;
import br.com.listacompras.util.Navegador;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller da tela de produtos (requisitos RF010 a RF013).
 * A consulta (RF011) são os cartões desta mesma tela (RN010).
 */
public class CadastroProdutoController implements Initializable {

    @FXML private ImageView previaFoto;
    @FXML private Button btnEscolherFoto;
    @FXML private TextField campoDescricao;
    @FXML private ComboBox<UnidadeMedida> comboUnidade;
    @FXML private Button btnAdicionar;
    @FXML private Button btnCancelar;
    @FXML private Label rotuloContador;

    // Dentro deste painel vamos criar um cartao para cada produto cadastrado
    @FXML private FlowPane painelProdutos;
    @FXML private Button btnVoltar;

    private final Repositorio repositorio = Repositorio.getInstancia();

    // Produto que esta sendo editado (nulo quando estamos cadastrando um novo)
    private Produto produtoEmEdicao;

    // Caminho da foto escolhida no formulario. Nulo = sem foto (RN039).
    private String caminhoFotoEscolhida;

    @Override
    public void initialize(URL url, ResourceBundle recursos) {
        // As opcoes vem do enum: o usuario so consegue escolher uma das cinco (RN038)
        comboUnidade.setItems(FXCollections.observableArrayList(UnidadeMedida.values()));
        comboUnidade.setValue(UnidadeMedida.UNIDADE);

        atualizarCartoes();
    }

    /** Monta o cartao de um produto, com os botoes dentro do proprio cartao (RN080). */
    private VBox criarCartao(Produto produto) {
        ImageView foto = new ImageView(carregarFoto(produto.getCaminhoFoto()));
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

    /** Devolve a foto informada ou a imagem padrao, se nao houver foto valida (RN040). */
    private Image carregarFoto(String caminho) {
        if (caminho != null && !caminho.isBlank()) {
            // O campo guarda um arquivo do computador ou um endereco da internet
            String url = caminho.contains("://") ? caminho : new File(caminho).toURI().toString();
            Image foto = new Image(url);
            if (!foto.isError()) {
                return foto;
            }
        }
        return new Image(getClass().getResource("/img/produto_padrao.png").toExternalForm());
    }

    /** Abre a janela do sistema para escolher uma imagem JPEG ou PNG (NF006). */
    @FXML
    private void escolherFoto(ActionEvent evento) {
        FileChooser seletor = new FileChooser();
        seletor.setTitle("Escolher foto do produto");
        seletor.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imagens (JPEG ou PNG)", "*.jpg", "*.jpeg", "*.png"));

        File arquivo = seletor.showOpenDialog(btnEscolherFoto.getScene().getWindow());
        if (arquivo != null) {
            caminhoFotoEscolhida = arquivo.getAbsolutePath();
            previaFoto.setImage(carregarFoto(caminhoFotoEscolhida));
        }
    }

    /**
     * Pede o endereço (URL) de uma imagem da internet, a outra forma de foto
     * aceita pela RN088. Se o endereço não abrir, a prévia mostra a imagem padrão (RN090).
     */
    @FXML
    private void informarLinkFoto(ActionEvent evento) {
        TextInputDialog janela = new TextInputDialog(
                caminhoFotoEscolhida != null && caminhoFotoEscolhida.contains("://") ? caminhoFotoEscolhida : "");
        janela.setTitle("Lista de Compras");
        janela.setHeaderText(null);
        janela.setContentText("Endereço da imagem:");

        Optional<String> resposta = janela.showAndWait();
        if (resposta.isPresent()) {
            String endereco = resposta.get().trim();
            caminhoFotoEscolhida = endereco.isEmpty() ? null : endereco;
            previaFoto.setImage(carregarFoto(caminhoFotoEscolhida));
        }
    }

    /** Cadastra (RF010) ou salva a alteração (RF012) do produto. */
    @FXML
    private void adicionarProduto(ActionEvent evento) {
        try {
            if (produtoEmEdicao == null) {
                repositorio.cadastrarProduto(campoDescricao.getText(), comboUnidade.getValue(),
                        caminhoFotoEscolhida);
                Mensagem.informar("Produto cadastrado com sucesso");
            } else {
                boolean mudouUnidadeEmUso = repositorio.alterarProduto(produtoEmEdicao,
                        campoDescricao.getText(), comboUnidade.getValue(), caminhoFotoEscolhida);
                if (mudouUnidadeEmUso) {
                    // RN046: os itens já incluídos guardaram a unidade antiga (RN056)
                    Mensagem.avisar("Atenção: o produto já está em listas montadas. "
                            + "As quantidades já lançadas nos itens permanecem as mesmas");
                } else {
                    Mensagem.informar("Produto alterado com sucesso");
                }
            }
            limparFormulario();
            atualizarCartoes();
        } catch (ValidacaoException erro) {
            Mensagem.avisar(erro.getMessage());
            campoDescricao.requestFocus();
        }
    }

    /** Preenche o formulário com os dados do cartão e troca o botão para "Salvar" (RF012). */
    private void editarProduto(Produto produto) {
        produtoEmEdicao = produto;
        campoDescricao.setText(produto.getDescricao());
        comboUnidade.setValue(produto.getUnidade());
        caminhoFotoEscolhida = produto.getCaminhoFoto();
        previaFoto.setImage(carregarFoto(caminhoFotoEscolhida));

        btnAdicionar.setText("Salvar");
        mostrarCancelar(true);
        campoDescricao.requestFocus();
    }

    /** Confirma e exclui, se o produto não estiver em nenhuma lista (RF013, RN047, RN048). */
    private void excluirProduto(Produto produto) {
        if (ExclusaoCadastro.excluir(produto, "o produto")) {
            if (produto == produtoEmEdicao) {
                limparFormulario();
            }
            atualizarCartoes();
        }
    }

    @FXML
    private void cancelarEdicao(ActionEvent evento) {
        limparFormulario();
    }

    @FXML
    private void voltarAoMenu(ActionEvent evento) {
        Navegador.abrir(btnVoltar, "/view/MenuPrincipal.fxml");
    }

    private void limparFormulario() {
        produtoEmEdicao = null;
        caminhoFotoEscolhida = null;
        campoDescricao.clear();
        comboUnidade.setValue(UnidadeMedida.UNIDADE);
        previaFoto.setImage(carregarFoto(null));
        btnAdicionar.setText("+ Adicionar");
        mostrarCancelar(false);
    }

    private void mostrarCancelar(boolean mostrar) {
        btnCancelar.setVisible(mostrar);
        btnCancelar.setManaged(mostrar);
    }

    /** Recria um cartão para cada produto e atualiza o contador (RN042). */
    private void atualizarCartoes() {
        // Tiramos tudo, inclusive o cartao de exemplo que existe no FXML so para o Scene Builder
        painelProdutos.getChildren().clear();
        for (Produto produto : repositorio.getProdutos()) {
            painelProdutos.getChildren().add(criarCartao(produto));
        }
        if (repositorio.getProdutos().isEmpty()) {
            // Relação vazia mostra um aviso no lugar dos cartões (mesma ideia da RN012)
            Label vazio = new Label("Ainda não há produtos cadastrados");
            vazio.getStyleClass().add("descricao-opcao");
            painelProdutos.getChildren().add(vazio);
        }
        rotuloContador.setText(Formatador.contador(repositorio.getProdutos().size(),
                "produto cadastrado", "produtos cadastrados"));
    }
}
