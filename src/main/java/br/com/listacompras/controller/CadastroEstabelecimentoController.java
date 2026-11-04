package br.com.listacompras.controller;

import br.com.listacompras.model.Estabelecimento;
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
import javafx.scene.control.ToggleButton;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller da tela de estabelecimentos (requisitos RF006 a RF009).
 */
public class CadastroEstabelecimentoController implements Initializable {

    @FXML private TextField campoNomeEstabelecimento;

    // Guardamos cada dia da semana em um botao que fica pressionado quando selecionado
    @FXML private ToggleButton btnDomingo;
    @FXML private ToggleButton btnSegunda;
    @FXML private ToggleButton btnTerca;
    @FXML private ToggleButton btnQuarta;
    @FXML private ToggleButton btnQuinta;
    @FXML private ToggleButton btnSexta;
    @FXML private ToggleButton btnSabado;

    @FXML private Button btnAdicionar;
    @FXML private Label rotuloContador;
    @FXML private TableView<Estabelecimento> tabelaEstabelecimentos;
    @FXML private TableColumn<Estabelecimento, String> colunaNome;
    @FXML private TableColumn<Estabelecimento, String> colunaDias;
    @FXML private TableColumn<Estabelecimento, Void> colunaAcoes;
    @FXML private Button btnVoltar;

    @Override
    public void initialize(URL url, ResourceBundle recursos) {
        colunaNome.setCellValueFactory(linha -> new SimpleStringProperty(linha.getValue().getNome()));

        // Polimorfismo: aqui getDetalhe() devolve os dias, como "Ter, Sex"
        colunaDias.setCellValueFactory(linha -> new SimpleStringProperty(linha.getValue().getDetalhe()));

        // Editar e excluir ficam na propria linha do registro (RN080)
        BotoesLinha.configurarColuna(colunaAcoes, this::editarEstabelecimento, this::excluirEstabelecimento);

        tabelaEstabelecimentos.setItems(
                FXCollections.observableArrayList(Repositorio.getInstancia().getEstabelecimentos()));
    }

    @FXML
    private void adicionarEstabelecimento(ActionEvent evento) {
        // Proximo passo (RF006): ler o nome, descobrir quais botoes de dia estao
        // selecionados e gravar o estabelecimento com essa lista de dias.
        Mensagem.emDesenvolvimento();
    }

    private void editarEstabelecimento(Estabelecimento estabelecimento) {
        // Proximo passo (RF008): alterar nome e dias do estabelecimento desta linha.
        Mensagem.emDesenvolvimento();
    }

    private void excluirEstabelecimento(Estabelecimento estabelecimento) {
        // Proximo passo (RF009): confirmar e excluir o estabelecimento desta linha.
        Mensagem.emDesenvolvimento();
    }

    @FXML
    private void voltarAoMenu(ActionEvent evento) {
        Navegador.abrir(btnVoltar, "/view/MenuPrincipal.fxml");
    }
}
