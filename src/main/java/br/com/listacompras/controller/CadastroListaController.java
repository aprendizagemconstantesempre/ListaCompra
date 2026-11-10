package br.com.listacompras.controller;

import br.com.listacompras.data.Repositorio;
import br.com.listacompras.model.ItemLista;
import br.com.listacompras.model.ListaCompra;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class CadastroListaController implements Initializable {

    @FXML private TextField campoNomeLista;
    @FXML private Button btnAdicionar;
    @FXML private Button btnCancelar;
    @FXML private Label rotuloContador;
    @FXML private TableView<ListaCompra> tabelaListas;
    @FXML private TableColumn<ListaCompra, String> colunaNomeLista;
    @FXML private TableColumn<ListaCompra, String> colunaDetalhes;
    @FXML private TableColumn<ListaCompra, Void> colunaAcoes;
    @FXML private Button btnVoltar;

    private final Repositorio repositorio = new Repositorio();
    private ListaCompra listaEmEdicao;

    @Override
    public void initialize(URL url, ResourceBundle recursos) {
        colunaNomeLista.setCellValueFactory(linha -> new SimpleStringProperty(linha.getValue().getNome()));
        colunaDetalhes.setCellValueFactory(linha -> new SimpleStringProperty(formatarDetalhe(linha.getValue())));
        BotoesLinha.configurarColuna(colunaAcoes, this::montarLista, this::editarLista, this::excluirLista);

        campoNomeLista.setOnAction(evento -> adicionarLista(null));
        tabelaListas.setPlaceholder(new Label("Ainda não há listas cadastradas"));
        atualizarTabela();
    }

    private String formatarDetalhe(ListaCompra lista) {
        if (lista == null) return "";
        List<ItemLista> itens = repositorio.listarItensPorLista(lista.getId());
        String produtos = (itens.size() == 1) ? "1 produto" : itens.size() + " produtos";
        double total = itens.stream().mapToDouble(ItemLista::getSubtotal).sum();
        return produtos + " — " + Formatador.moeda(total);
    }

    @FXML
    private void adicionarLista(ActionEvent evento) {
        String nome = campoNomeLista.getText();
        if (nome == null || nome.isBlank()) {
            Mensagem.avisar("Informe o nome da lista");
            campoNomeLista.requestFocus();
            return;
        }
        nome = nome.trim();

        boolean eraNovo = (listaEmEdicao == null);
        if (eraNovo) {
            repositorio.salvarListaDeCompra(new ListaCompra(nome));
        } else {
            listaEmEdicao.setNome(nome);
            repositorio.salvarListaDeCompra(listaEmEdicao);
        }
        limparFormulario();
        atualizarTabela();
        Mensagem.informar(eraNovo ? "Lista cadastrada com sucesso" : "Lista alterada com sucesso");
    }

    private void editarLista(ListaCompra lista) {
        listaEmEdicao = lista;
        campoNomeLista.setText(lista.getNome());
        btnAdicionar.setText("Salvar");
        mostrarCancelar(true);
        campoNomeLista.requestFocus();
    }

    private void excluirLista(ListaCompra lista) {
        if (ExclusaoCadastro.excluir(lista, "a lista")) {
            if (lista == listaEmEdicao) {
                limparFormulario();
            }
            atualizarTabela();
        }
    }

    private void montarLista(ListaCompra lista) {
        CriarListaController criarLista = Navegador.abrir(btnVoltar, "/view/CriarLista.fxml");
        if (criarLista != null) {
            criarLista.setListaSelecionada(lista);
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
        listaEmEdicao = null;
        campoNomeLista.clear();
        btnAdicionar.setText("+ Adicionar");
        mostrarCancelar(false);
    }

    private void mostrarCancelar(boolean mostrar) {
        btnCancelar.setVisible(mostrar);
        btnCancelar.setManaged(mostrar);
    }

    private void atualizarTabela() {
        tabelaListas.setItems(FXCollections.observableArrayList(repositorio.listarListasDeCompra()));
        tabelaListas.refresh();
        rotuloContador.setText(Formatador.contador(repositorio.listarListasDeCompra().size(),
                "lista cadastrada", "listas cadastradas"));
    }
}
