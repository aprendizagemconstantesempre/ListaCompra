package br.com.listacompras.controller;

import br.com.listacompras.data.Repositorio;
import br.com.listacompras.model.Estabelecimento;
import br.com.listacompras.model.ItemLista;
import br.com.listacompras.model.ListaCompra;
import br.com.listacompras.model.Produto;
import br.com.listacompras.util.BotoesLinha;
import br.com.listacompras.util.Formatador;
import br.com.listacompras.util.Mensagem;
import br.com.listacompras.util.Navegador;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Popup;
import javafx.util.StringConverter;

import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Controller da tela onde montamos a lista (requisitos RF014 a RF017).
 * É a tela de MOVIMENTAÇÃO do trabalho: cria, altera e exclui itens,
 * e a tabela com os totalizadores é a consulta dos itens (RF015).
 */
public class CriarListaController implements Initializable {

    @FXML private ComboBox<ListaCompra> comboLista;
    @FXML private ComboBox<Estabelecimento> comboEstabelecimento;
    @FXML private TextField campoPesquisaProduto;
    @FXML private TextField campoQuantidade;
    @FXML private TextField campoValorUnitario;
    @FXML private Button btnAdicionarItem;
    @FXML private Button btnCancelarItem;

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

    /**
     * Opção "Sem estabelecimento" da lista suspensa.
     */
    private static final Estabelecimento SEM_ESTABELECIMENTO =
            new Estabelecimento("Sem estabelecimento", null);

    private final Repositorio repositorio = new Repositorio();

    // Produtos encontrados pela pesquisa e a janelinha que os mostra abaixo do campo
    private FilteredList<Produto> produtosFiltrados;
    private final ListView<Produto> listaResultados = new ListView<>();
    private final Popup popupResultados = new Popup();

    // Produto escolhido na pesquisa (nulo enquanto nenhum foi clicado)
    private Produto produtoSelecionado;

    // Evita que, ao escrever o nome do produto escolhido no campo, a pesquisa recomece
    private boolean preenchendoCampo;

    // Item que está sendo editado (nulo quando estamos incluindo um novo)
    private ItemLista itemEmEdicao;

    @Override
    public void initialize(URL url, ResourceBundle recursos) {
        comboLista.setConverter(new StringConverter<>() {
            @Override public String toString(ListaCompra l) { return l == null ? "" : l.getNome(); }
            @Override public ListaCompra fromString(String string) { return null; }
        });
        comboLista.setItems(FXCollections.observableArrayList(repositorio.listarListasDeCompra()));

        comboEstabelecimento.setConverter(new StringConverter<>() {
            @Override public String toString(Estabelecimento e) { return e == null ? "" : e.getNome(); }
            @Override public Estabelecimento fromString(String string) { return null; }
        });
        List<Estabelecimento> estabelecimentos = new ArrayList<>();
        estabelecimentos.add(SEM_ESTABELECIMENTO);
        estabelecimentos.addAll(repositorio.listarEstabelecimentos());
        comboEstabelecimento.setItems(FXCollections.observableArrayList(estabelecimentos));
        comboEstabelecimento.setValue(SEM_ESTABELECIMENTO);

        produtosFiltrados = new FilteredList<>(FXCollections.observableArrayList(repositorio.listarProdutos()));
        configurarPesquisaProduto();

        // Trocar a lista ou o estabelecimento muda os itens mostrados (RN058)
        comboLista.valueProperty().addListener((propriedade, antes, depois) -> atualizarTabela());
        comboEstabelecimento.valueProperty().addListener((propriedade, antes, depois) -> atualizarTabela());

        colunaProduto.setCellValueFactory(linha ->
                new SimpleStringProperty(obterDescricaoProduto(linha.getValue())));
        colunaUnidade.setCellValueFactory(linha ->
                new SimpleStringProperty(linha.getValue().getUnidade().getDescricao()));
        colunaQuantidade.setCellValueFactory(linha ->
                new SimpleStringProperty(Formatador.quantidade(linha.getValue().getQuantidade())));
        colunaValorUnitario.setCellValueFactory(linha -> {
            Double valor = linha.getValue().getValorUnitario();
            return new SimpleStringProperty(valor == null ? "—" : Formatador.moeda(valor));
        });
        colunaSubtotal.setCellValueFactory(linha ->
                new SimpleStringProperty(Formatador.moeda(linha.getValue().getSubtotal())));

        BotoesLinha.configurarColuna(colunaAcoes, this::editarItem, this::excluirItem);
        tabelaItens.setPlaceholder(new Label("Selecione uma lista para ver os itens, ou inclua o primeiro item"));

        atualizarTabela();
    }

    /**
     * PASSAGEM DE DADOS: o Cadastro de Lista chama este método logo depois de
     * abrir esta tela (botão "Montar") e entrega o objeto ListaCompra escolhido.
     */
    public void setListaSelecionada(ListaCompra lista) {
        if (lista != null) {
            for (ListaCompra l : comboLista.getItems()) {
                if (l.getId().equals(lista.getId())) {
                    comboLista.setValue(l);
                    break;
                }
            }
        }
    }

    /**
     * Pesquisa de produto: ao digitar, os produtos encontrados aparecem logo
     * abaixo do campo (sem diferenciar maiúsculas e acentos; todas as palavras
     * precisam aparecer). Clicar em um produto o seleciona. Pelo teclado, as
     * setas percorrem os resultados, Enter seleciona e Esc fecha a lista.
     * Clicar no campo vazio mostra todos os produtos.
     */
    private void configurarPesquisaProduto() {
        listaResultados.setItems(produtosFiltrados);
        listaResultados.setPlaceholder(new Label("Nenhum produto encontrado"));
        listaResultados.setFocusTraversable(false);
        listaResultados.setPrefHeight(200);
        listaResultados.setCellFactory(lista -> new ListCell<>() {
            @Override
            protected void updateItem(Produto produto, boolean vazio) {
                super.updateItem(produto, vazio);
                setText(vazio || produto == null ? null : produto.getDescricao());
            }
        });
        listaResultados.setOnMouseClicked(evento -> {
            Produto clicado = listaResultados.getSelectionModel().getSelectedItem();
            if (clicado != null) {
                selecionarProduto(clicado);
            }
        });
        popupResultados.getContent().add(listaResultados);
        popupResultados.setAutoHide(true);

        campoPesquisaProduto.textProperty().addListener((propriedade, antes, depois) -> {
            if (preenchendoCampo) return;
            produtoSelecionado = null;
            filtrarProdutos(depois);
            if (normalizar(depois).isEmpty()) {
                popupResultados.hide();
            } else {
                mostrarResultados();
            }
        });

        campoPesquisaProduto.setOnMouseClicked(evento -> {
            if (!popupResultados.isShowing()) {
                filtrarProdutos(produtoSelecionado == null ? campoPesquisaProduto.getText() : "");
                mostrarResultados();
            }
        });

        campoPesquisaProduto.focusedProperty().addListener((propriedade, antes, focado) -> {
            if (!focado) popupResultados.hide();
        });

        campoPesquisaProduto.setOnKeyPressed(evento -> {
            int atual = listaResultados.getSelectionModel().getSelectedIndex();
            switch (evento.getCode()) {
                case DOWN -> {
                    if (!popupResultados.isShowing()) mostrarResultados();
                    else if (atual < produtosFiltrados.size() - 1) destacarResultado(atual + 1);
                    evento.consume();
                }
                case UP -> {
                    if (atual > 0) destacarResultado(atual - 1);
                    evento.consume();
                }
                case ENTER -> {
                    Produto escolhido = produtoParaEnter();
                    if (escolhido != null) selecionarProduto(escolhido);
                    evento.consume();
                }
                case ESCAPE -> popupResultados.hide();
                default -> { }
            }
        });
    }

    private void filtrarProdutos(String texto) {
        String[] palavras = normalizar(texto).split("\\s+");
        produtosFiltrados.setPredicate(produto -> {
            String descricao = normalizar(produto.getDescricao());
            for (String palavra : palavras) {
                if (!descricao.contains(palavra)) return false;
            }
            return true;
        });
    }

    /** Abre a lista de resultados logo abaixo do campo, com a mesma largura dele. */
    private void mostrarResultados() {
        if (campoPesquisaProduto.getScene() == null || campoPesquisaProduto.getScene().getWindow() == null) return;
        destacarResultado(produtosFiltrados.isEmpty() ? -1 : 0);
        listaResultados.setPrefWidth(campoPesquisaProduto.getWidth());
        Bounds limites = campoPesquisaProduto.localToScreen(campoPesquisaProduto.getBoundsInLocal());
        if (limites == null) return;
        if (popupResultados.isShowing()) {
            popupResultados.setX(limites.getMinX());
            popupResultados.setY(limites.getMaxY());
        } else {
            popupResultados.show(campoPesquisaProduto, limites.getMinX(), limites.getMaxY());
        }
    }

    private void destacarResultado(int indice) {
        if (indice < 0) {
            listaResultados.getSelectionModel().clearSelection();
        } else {
            listaResultados.getSelectionModel().select(indice);
            listaResultados.scrollTo(indice);
        }
    }

    /** Enter: o produto com o nome exato digitado; senão o destacado na lista. */
    private Produto produtoParaEnter() {
        if (produtoSelecionado != null) return produtoSelecionado;
        String digitado = normalizar(campoPesquisaProduto.getText());
        for (Produto p : produtosFiltrados) {
            if (normalizar(p.getDescricao()).equals(digitado)) return p;
        }
        Produto destacado = listaResultados.getSelectionModel().getSelectedItem();
        if (destacado != null) return destacado;
        return produtosFiltrados.isEmpty() ? null : produtosFiltrados.get(0);
    }

    /** Escolhe o produto: escreve o nome no campo, fecha a lista e vai para a quantidade. */
    private void selecionarProduto(Produto produto) {
        produtoSelecionado = produto;
        preencherCampoPesquisa(produto.getDescricao());
        popupResultados.hide();
        campoQuantidade.requestFocus();
        campoQuantidade.selectAll();
    }

    private void preencherCampoPesquisa(String texto) {
        preenchendoCampo = true;
        campoPesquisaProduto.setText(texto);
        preenchendoCampo = false;
        filtrarProdutos("");
    }

    /** Minúsculas, sem acentos e sem espaços nas pontas, para comparar textos. */
    private static String normalizar(String texto) {
        if (texto == null) return "";
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim();
    }

    private String obterDescricaoProduto(ItemLista item) {
        if (item == null || item.getIdProduto() == null) return "";
        Produto produto = repositorio.buscarProdutoPorId(item.getIdProduto());
        return (produto == null) ? "" : produto.getDescricao();
    }

    /** Inclui um item (RF014) ou salva a alteração do item em edição (RF016). */
    @FXML
    public void adicionarItem(ActionEvent evento) {
        try {
            double quantidade = lerQuantidade();
            Double valorUnitario = lerValorUnitario();

            ListaCompra lista = comboLista.getValue();
            if (lista == null) {
                throw new IllegalArgumentException("Selecione a lista de compras");
            }
            Produto produto = produtoSelecionado;
            if (produto == null) {
                throw new IllegalArgumentException("Selecione o produto");
            }

            Estabelecimento est = estabelecimentoEscolhido();
            String idEst = (est == null || est == SEM_ESTABELECIMENTO) ? null : est.getId();

            if (itemEmEdicao == null) {
                ItemLista existente = repositorio.buscarItem(lista.getId(), idEst, produto.getId());
                if (existente != null) {
                    existente.somarQuantidade(quantidade);
                    if (valorUnitario != null) {
                        existente.setValorUnitario(valorUnitario);
                    }
                    repositorio.salvarItem(existente);
                } else {
                    repositorio.salvarItem(new ItemLista(lista.getId(), idEst, produto.getId(),
                            produto.getUnidade(), quantidade, valorUnitario));
                }
            } else {
                itemEmEdicao.setQuantidade(quantidade);
                itemEmEdicao.setValorUnitario(valorUnitario);
                repositorio.salvarItem(itemEmEdicao);
            }
            limparFormulario();
            atualizarTabela();
        } catch (IllegalArgumentException erro) {
            Mensagem.avisar(erro.getMessage());
        }
    }

    /** Coloca quantidade e valor do item nos campos e troca o botão para "Salvar item" (RF016). */
    private void editarItem(ItemLista item) {
        itemEmEdicao = item;
        produtoSelecionado = repositorio.buscarProdutoPorId(item.getIdProduto());
        preencherCampoPesquisa(produtoSelecionado == null ? "" : produtoSelecionado.getDescricao());
        campoQuantidade.setText(Formatador.quantidade(item.getQuantidade()).replace(".", ""));
        campoValorUnitario.setText(item.getValorUnitario() == null
                ? "" : String.format(Locale.ROOT, "%.2f", item.getValorUnitario()).replace('.', ','));

        comboLista.setDisable(true);
        comboEstabelecimento.setDisable(true);
        campoPesquisaProduto.setDisable(true);
        btnAdicionarItem.setText("Salvar item");
        mostrarCancelar(true);
        campoQuantidade.requestFocus();
    }

    /** Confirma e remove só o item; o produto continua cadastrado (RF017, RN067, RN069). */
    private void excluirItem(ItemLista item) {
        String pergunta = "Deseja realmente excluir o item \"" + obterDescricaoProduto(item) + "\" da lista?";
        if (Mensagem.confirmar(pergunta)) {
            repositorio.deletarItem(item.getId());
            if (item == itemEmEdicao) {
                limparFormulario();
            }
            atualizarTabela();
        }
    }

    @FXML
    public void cancelarEdicao(ActionEvent evento) {
        limparFormulario();
    }

    @FXML
    public void voltarAoMenu(ActionEvent evento) {
        Navegador.abrir(btnVoltar, "/view/MenuPrincipal.fxml");
    }

    /** Troca a opção "Sem estabelecimento" por null. */
    private Estabelecimento estabelecimentoEscolhido() {
        Estabelecimento escolhido = comboEstabelecimento.getValue();
        return (escolhido == SEM_ESTABELECIMENTO) ? null : escolhido;
    }

    /** Quantidade é obrigatória e aceita decimais com vírgula ou ponto, como "1,5". */
    private double lerQuantidade() {
        Double quantidade = lerNumero(campoQuantidade.getText());
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero");
        }
        return quantidade;
    }

    /** Valor unitário é opcional (RN054): campo vazio vira null. */
    private Double lerValorUnitario() {
        String texto = campoValorUnitario.getText();
        if (texto == null || texto.isBlank()) {
            return null;
        }
        Double valor = lerNumero(texto);
        if (valor == null) {
            throw new IllegalArgumentException("Informe um valor unitário válido");
        }
        return valor;
    }

    private Double lerNumero(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        String limpo = texto.trim().replace("R$", "").trim();
        if (limpo.contains(",")) {
            limpo = limpo.replace(".", "").replace(",", ".");
        }
        try {
            double numero = Double.parseDouble(limpo);
            return Double.isFinite(numero) ? numero : null;
        } catch (NumberFormatException erro) {
            return null;
        }
    }

    private void limparFormulario() {
        itemEmEdicao = null;
        produtoSelecionado = null;
        preencherCampoPesquisa("");
        popupResultados.hide();
        campoQuantidade.setText("1");
        campoValorUnitario.clear();

        comboLista.setDisable(false);
        comboEstabelecimento.setDisable(false);
        campoPesquisaProduto.setDisable(false);
        btnAdicionarItem.setText("+ Adicionar item");
        mostrarCancelar(false);
    }

    private void mostrarCancelar(boolean mostrar) {
        btnCancelarItem.setVisible(mostrar);
        btnCancelarItem.setManaged(mostrar);
    }

    private void atualizarTabela() {
        ListaCompra lista = comboLista.getValue();
        Estabelecimento est = estabelecimentoEscolhido();
        List<ItemLista> itens = (lista == null)
                ? new ArrayList<>()
                : repositorio.listarItensPorListaEEstabelecimento(lista.getId(),
                    est == null ? null : est.getId());

        tabelaItens.setItems(FXCollections.observableArrayList(itens));
        tabelaItens.refresh();

        rotuloItens.setText(lista == null ? "Itens da lista" : "Itens da lista — " + lista.getNome());
        rotuloContador.setText(Formatador.contador(itens.size(), "item", "itens"));

        // Os totalizadores somam somente os itens exibidos na tabela (lista + estabelecimento)
        rotuloTotalItens.setText(Formatador.quantidade(somarQuantidades(itens)));
        rotuloValorTotal.setText(Formatador.moeda(somarValores(itens)));
    }

    private double somarQuantidades(List<ItemLista> itens) {
        return itens.stream().mapToDouble(ItemLista::getQuantidade).sum();
    }

    private double somarValores(List<ItemLista> itens) {
        return itens.stream().mapToDouble(ItemLista::getSubtotal).sum();
    }
}
