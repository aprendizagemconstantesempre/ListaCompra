package br.com.listacompras.controller;

import br.com.listacompras.util.Navegador;
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
    @FXML private TableView<Object> tabelaEstabelecimentos;
    @FXML private TableColumn<Object, String> colunaNome;
    @FXML private TableColumn<Object, String> colunaDias;
    @FXML private Button btnVoltar;
    @FXML private Button btnEditar;
    @FXML private Button btnExcluir;

    @Override
    public void initialize(URL url, ResourceBundle recursos) {
        // Proximo passo: configurar as colunas e carregar os estabelecimentos ja salvos.
    }

    @FXML
    private void adicionarEstabelecimento(ActionEvent evento) {
        // Proximo passo (RF006): ler o nome, descobrir quais botoes de dia estao
        // selecionados e gravar o estabelecimento com essa lista de dias.
    }

    @FXML
    private void editarEstabelecimento(ActionEvent evento) {
        // Proximo passo (RF008): alterar nome e dias do estabelecimento selecionado.
    }

    @FXML
    private void excluirEstabelecimento(ActionEvent evento) {
        // Proximo passo (RF009): excluir o estabelecimento selecionado na tabela.
    }

    @FXML
    private void voltarAoMenu(ActionEvent evento) {
        Navegador.abrir(btnVoltar, "/view/MenuPrincipal.fxml");
    }
}
