package br.com.listacompras.controller;

import br.com.listacompras.model.ListaCompra;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller da tela de cadastro de listas de compras (RF002 a RF005).
 * A consulta (RF003) fica na tabela desta mesma tela (RN010).
 */
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

    private final Repositorio repositorio = Repositorio.getInstancia();

    // Lista que está sendo editada. Fica nula quando o formulário está cadastrando uma nova.
    private ListaCompra listaEmEdicao;

    /**
     * O JavaFX chama este metodo logo depois de montar a tela.
     * E aqui que preparamos a tabela e carregamos os dados salvos.
     */
    @Override
    public void initialize(URL url, ResourceBundle recursos) {
        colunaNomeLista.setCellValueFactory(linha -> new SimpleStringProperty(linha.getValue().getNome()));

        // Polimorfismo: cada tipo de cadastro responde getDetalhe() do seu jeito
        colunaDetalhes.setCellValueFactory(linha -> new SimpleStringProperty(linha.getValue().getDetalhe()));

        // Montar, editar e excluir ficam na propria linha do registro (RN080)
        BotoesLinha.configurarColuna(colunaAcoes, this::montarLista, this::editarLista, this::excluirLista);

        // Enter no campo tem o mesmo efeito de clicar no botao
        campoNomeLista.setOnAction(evento -> adicionarLista(null));

        // Relação vazia mostra um aviso no lugar das linhas (RN012)
        tabelaListas.setPlaceholder(new Label("Ainda não há listas cadastradas"));

        atualizarTabela();
    }

    /** Cadastra uma lista nova (RF002) ou salva a alteração da lista em edição (RF004). */
    @FXML
    private void adicionarLista(ActionEvent evento) {
        try {
            if (listaEmEdicao == null) {
                repositorio.cadastrarLista(campoNomeLista.getText());
                Mensagem.informar("Lista cadastrada com sucesso");
            } else {
                repositorio.alterarLista(listaEmEdicao, campoNomeLista.getText());
                Mensagem.informar("Lista alterada com sucesso");
            }
            limparFormulario();
            atualizarTabela();
        } catch (ValidacaoException erro) {
            // A mensagem já vem pronta do Repositorio, igual à do BDD
            Mensagem.avisar(erro.getMessage());
            campoNomeLista.requestFocus();
        }
    }

    /** Coloca o nome da lista no campo e troca o botão para "Salvar" (RF004). */
    private void editarLista(ListaCompra lista) {
        listaEmEdicao = lista;
        campoNomeLista.setText(lista.getNome());
        btnAdicionar.setText("Salvar");
        mostrarCancelar(true);
        campoNomeLista.requestFocus();
    }

    /** Confirma e exclui a lista, se ela não tiver itens (RF005, RN018, RN019). */
    private void excluirLista(ListaCompra lista) {
        if (ExclusaoCadastro.excluir(lista, "a lista")) {
            if (lista == listaEmEdicao) {
                limparFormulario();
            }
            atualizarTabela();
        }
    }

    /**
     * PASSAGEM DE DADOS: abre a tela Criar Lista e entrega a ela o objeto
     * ListaCompra desta linha, que já aparece selecionado na tela nova.
     */
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
        // Voltamos para a tela principal do sistema
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

    /** Recarrega a tabela e o contador de registros (RN011). */
    private void atualizarTabela() {
        tabelaListas.setItems(FXCollections.observableArrayList(repositorio.getListas()));
        tabelaListas.refresh();
        rotuloContador.setText(Formatador.contador(repositorio.getListas().size(),
                "lista cadastrada", "listas cadastradas"));
    }
}
