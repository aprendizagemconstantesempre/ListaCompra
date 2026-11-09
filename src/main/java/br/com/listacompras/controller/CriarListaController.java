package br.com.listacompras.controller;

import br.com.listacompras.model.Estabelecimento;
import br.com.listacompras.model.ItemLista;
import br.com.listacompras.model.ListaCompra;
import br.com.listacompras.model.Produto;
import br.com.listacompras.model.Repositorio;
import br.com.listacompras.model.ValidacaoException;
import br.com.listacompras.util.BotoesLinha;
import br.com.listacompras.util.Formatador;
import br.com.listacompras.util.Mensagem;
import br.com.listacompras.util.Navegador;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Controller da tela onde montamos a lista (requisitos RF014 a RF017).
 * É a tela de MOVIMENTAÇÃO do trabalho de Java: cria, altera e exclui itens,
 * e a tabela com os totalizadores é a consulta dos itens (RF015).
 */
public class CriarListaController implements Initializable {

    @FXML private ComboBox<ListaCompra> comboLista;
    @FXML private ComboBox<Estabelecimento> comboEstabelecimento;
    @FXML private ComboBox<Produto> comboProduto;
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
     * Opção "Sem estabelecimento" da lista suspensa. O estabelecimento é
     * opcional (RN051): quando esta opção está escolhida, o item fica sem
     * estabelecimento. Ela só existe na tela e nunca vai para o Repositorio.
     */
    private static final Estabelecimento SEM_ESTABELECIMENTO =
            new Estabelecimento(0, "Sem estabelecimento", null);

    private final Repositorio repositorio = Repositorio.getInstancia();

    // Item que esta sendo editado (nulo quando estamos incluindo um novo)
    private ItemLista itemEmEdicao;

    @Override
    public void initialize(URL url, ResourceBundle recursos) {
        // As listas suspensas mostram os objetos que ja foram cadastrados (RN052)
        comboLista.setItems(FXCollections.observableArrayList(repositorio.getListas()));

        List<Estabelecimento> estabelecimentos = new ArrayList<>();
        estabelecimentos.add(SEM_ESTABELECIMENTO);
        estabelecimentos.addAll(repositorio.getEstabelecimentos());
        comboEstabelecimento.setItems(FXCollections.observableArrayList(estabelecimentos));
        comboEstabelecimento.setValue(SEM_ESTABELECIMENTO);

        comboProduto.setItems(FXCollections.observableArrayList(repositorio.getProdutos()));

        // Trocar a lista ou o estabelecimento muda os itens mostrados (RN058)
        comboLista.valueProperty().addListener((propriedade, antes, depois) -> atualizarTabela());
        comboEstabelecimento.valueProperty().addListener((propriedade, antes, depois) -> atualizarTabela());

        colunaProduto.setCellValueFactory(linha ->
                new SimpleStringProperty(linha.getValue().getDescricaoProduto()));
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

        // Editar e excluir ficam na propria linha do item (RN080)
        BotoesLinha.configurarColuna(colunaAcoes, this::editarItem, this::excluirItem);

        tabelaItens.setPlaceholder(new Label("Selecione uma lista para ver os itens, ou inclua o primeiro item"));

        atualizarTabela();
    }

    /**
     * PASSAGEM DE DADOS: o Cadastro de Lista chama este método logo depois de
     * abrir esta tela (botão "Montar") e entrega o objeto ListaCompra escolhido.
     * A tela já abre com a lista selecionada e mostrando os itens dela.
     */
    public void setListaSelecionada(ListaCompra lista) {
        comboLista.setValue(lista);
    }

    /** Inclui um item (RF014) ou salva a alteração do item em edição (RF016). */
    @FXML
    private void adicionarItem(ActionEvent evento) {
        try {
            double quantidade = lerQuantidade();
            Double valorUnitario = lerValorUnitario();

            if (itemEmEdicao == null) {
                // Se o produto já estiver na lista, o Repositorio soma a quantidade (RN055)
                repositorio.incluirItem(comboLista.getValue(), estabelecimentoEscolhido(),
                        comboProduto.getValue(), quantidade, valorUnitario);
            } else {
                repositorio.alterarItem(itemEmEdicao, quantidade, valorUnitario);
            }
            limparFormulario();
            atualizarTabela();
        } catch (ValidacaoException erro) {
            Mensagem.avisar(erro.getMessage());
        }
    }

    /** Coloca quantidade e valor do item nos campos e troca o botão para "Salvar item" (RF016). */
    private void editarItem(ItemLista item) {
        itemEmEdicao = item;
        comboProduto.setValue(repositorio.buscarProduto(item.getIdProduto()));
        // Tiramos o ponto de milhar ("1.234" -> "1234") para o número ser lido de volta igual
        campoQuantidade.setText(Formatador.quantidade(item.getQuantidade()).replace(".", ""));
        campoValorUnitario.setText(item.getValorUnitario() == null
                ? "" : String.format(Locale.ROOT, "%.2f", item.getValorUnitario()).replace('.', ','));

        // Durante a edição só a quantidade e o valor podem mudar (RN064, RN065)
        comboLista.setDisable(true);
        comboEstabelecimento.setDisable(true);
        comboProduto.setDisable(true);
        btnAdicionarItem.setText("Salvar item");
        mostrarCancelar(true);
        campoQuantidade.requestFocus();
    }

    /** Confirma e remove só o item; o produto continua cadastrado (RF017, RN067, RN069). */
    private void excluirItem(ItemLista item) {
        String pergunta = "Deseja realmente excluir o item \"" + item.getDescricaoProduto() + "\" da lista?";
        if (Mensagem.confirmar(pergunta)) {
            repositorio.excluirItem(item);
            if (item == itemEmEdicao) {
                limparFormulario();
            }
            atualizarTabela();
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

    /** Troca a opção "Sem estabelecimento" por null, que é o que o Repositorio entende. */
    private Estabelecimento estabelecimentoEscolhido() {
        Estabelecimento escolhido = comboEstabelecimento.getValue();
        return (escolhido == SEM_ESTABELECIMENTO) ? null : escolhido;
    }

    /** Quantidade é obrigatória e aceita decimais com vírgula ou ponto, como "1,5". */
    private double lerQuantidade() {
        Double quantidade = lerNumero(campoQuantidade.getText());
        if (quantidade == null) {
            throw new ValidacaoException("A quantidade deve ser maior que zero");
        }
        return quantidade; // o Repositorio recusa zero e negativos (RN053)
    }

    /** Valor unitário é opcional (RN054): campo vazio vira null. */
    private Double lerValorUnitario() {
        String texto = campoValorUnitario.getText();
        if (texto == null || texto.isBlank()) {
            return null;
        }
        Double valor = lerNumero(texto);
        if (valor == null) {
            throw new ValidacaoException("Informe um valor unitário válido");
        }
        return valor;
    }

    /**
     * Converte o texto digitado em número. Aceita "4,50", "4.50" e "1.234,50".
     * Devolve null quando o texto está vazio ou não é um número.
     */
    private Double lerNumero(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        String limpo = texto.trim().replace("R$", "").trim();
        if (limpo.contains(",")) {
            // Padrão brasileiro: o ponto separa milhar e a vírgula separa os centavos
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
        comboProduto.setValue(null);
        campoQuantidade.setText("1");
        campoValorUnitario.clear();
        comboLista.setDisable(false);
        comboEstabelecimento.setDisable(false);
        comboProduto.setDisable(false);
        btnAdicionarItem.setText("+ Adicionar item");
        mostrarCancelar(false);
    }

    private void mostrarCancelar(boolean mostrar) {
        btnCancelarItem.setVisible(mostrar);
        btnCancelarItem.setManaged(mostrar);
    }

    /**
     * Mostra só os itens da lista e do estabelecimento escolhidos (RN058) e
     * recalcula os totalizadores do rodapé (RN061 e RN062).
     */
    private void atualizarTabela() {
        ListaCompra lista = comboLista.getValue();
        List<ItemLista> itens = (lista == null)
                ? new ArrayList<>()
                : repositorio.itensDaLista(lista, estabelecimentoEscolhido());

        tabelaItens.setItems(FXCollections.observableArrayList(itens));
        tabelaItens.refresh();

        rotuloItens.setText(lista == null ? "Itens da lista" : "Itens da lista — " + lista.getNome());
        rotuloContador.setText(Formatador.contador(itens.size(), "item", "itens"));
        rotuloTotalItens.setText(Formatador.quantidade(repositorio.somarQuantidades(itens)));
        rotuloValorTotal.setText(Formatador.moeda(repositorio.somarValores(itens)));
    }
}
