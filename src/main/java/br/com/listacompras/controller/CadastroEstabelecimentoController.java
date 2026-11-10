package br.com.listacompras.controller;

import br.com.listacompras.data.Repositorio;
import br.com.listacompras.model.Estabelecimento;
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
import javafx.scene.control.ToggleButton;

import java.net.URL;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller da tela de estabelecimentos (requisitos RF006 a RF009).
 * A consulta (RF007) fica na tabela desta mesma tela (RN010).
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
    @FXML private Button btnCancelar;
    @FXML private Label rotuloContador;
    @FXML private TableView<Estabelecimento> tabelaEstabelecimentos;
    @FXML private TableColumn<Estabelecimento, String> colunaNome;
    @FXML private TableColumn<Estabelecimento, String> colunaDias;
    @FXML private TableColumn<Estabelecimento, Void> colunaAcoes;
    @FXML private Button btnVoltar;

    private final Repositorio repositorio = new Repositorio();

    // Liga cada botao da tela ao dia da semana do Java. A ordem e a da tela (Dom a Sab).
    private final Map<DayOfWeek, ToggleButton> botoesDosDias = new LinkedHashMap<>();

    // Estabelecimento que esta sendo editado (nulo quando estamos cadastrando um novo)
    private Estabelecimento estabelecimentoEmEdicao;

    @Override
    public void initialize(URL url, ResourceBundle recursos) {
        botoesDosDias.put(DayOfWeek.SUNDAY, btnDomingo);
        botoesDosDias.put(DayOfWeek.MONDAY, btnSegunda);
        botoesDosDias.put(DayOfWeek.TUESDAY, btnTerca);
        botoesDosDias.put(DayOfWeek.WEDNESDAY, btnQuarta);
        botoesDosDias.put(DayOfWeek.THURSDAY, btnQuinta);
        botoesDosDias.put(DayOfWeek.FRIDAY, btnSexta);
        botoesDosDias.put(DayOfWeek.SATURDAY, btnSabado);

        colunaNome.setCellValueFactory(linha -> new SimpleStringProperty(linha.getValue().getNome()));
        colunaDias.setCellValueFactory(linha -> new SimpleStringProperty(linha.getValue().getDetalhe()));

        BotoesLinha.configurarColuna(colunaAcoes, this::editarEstabelecimento, this::excluirEstabelecimento);
        tabelaEstabelecimentos.setPlaceholder(new Label("Ainda não há estabelecimentos cadastrados"));

        campoNomeEstabelecimento.setOnAction(evento -> adicionarEstabelecimento(null));
        atualizarTabela();
    }

    /** Cadastra (RF006) ou salva a alteração (RF008) do estabelecimento. */
    @FXML
    private void adicionarEstabelecimento(ActionEvent evento) {
        String nome = campoNomeEstabelecimento.getText();
        if (nome == null || nome.isBlank()) {
            Mensagem.avisar("Informe o nome do estabelecimento");
            campoNomeEstabelecimento.requestFocus();
            return;
        }
        nome = nome.trim();

        boolean eraNovo = (estabelecimentoEmEdicao == null);
        if (eraNovo) {
            repositorio.salvarEstabelecimento(new Estabelecimento(nome, diasSelecionados()));
        } else {
            estabelecimentoEmEdicao.setNome(nome);
            estabelecimentoEmEdicao.setDiasPromocao(diasSelecionados());
            repositorio.salvarEstabelecimento(estabelecimentoEmEdicao);
        }
        limparFormulario();
        atualizarTabela();
        Mensagem.informar(eraNovo ? "Estabelecimento cadastrado com sucesso" : "Estabelecimento alterado com sucesso");
    }

    /** Preenche o formulário com os dados da linha e troca o botão para "Salvar" (RF008). */
    private void editarEstabelecimento(Estabelecimento estabelecimento) {
        estabelecimentoEmEdicao = estabelecimento;
        campoNomeEstabelecimento.setText(estabelecimento.getNome());

        List<DayOfWeek> dias = estabelecimento.getDiasPromocao();
        for (Map.Entry<DayOfWeek, ToggleButton> par : botoesDosDias.entrySet()) {
            par.getValue().setSelected(dias.contains(par.getKey()));
        }

        btnAdicionar.setText("Salvar");
        mostrarCancelar(true);
        campoNomeEstabelecimento.requestFocus();
    }

    /** Confirma e exclui, se não houver itens ligados a ele (RF009, RN033, RN034). */
    private void excluirEstabelecimento(Estabelecimento estabelecimento) {
        if (ExclusaoCadastro.excluir(estabelecimento, "o estabelecimento")) {
            if (estabelecimento == estabelecimentoEmEdicao) {
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

    /** Devolve os dias cujos botões estão pressionados. Nenhum dia também é válido (RN024). */
    private List<DayOfWeek> diasSelecionados() {
        List<DayOfWeek> dias = new ArrayList<>();
        for (Map.Entry<DayOfWeek, ToggleButton> par : botoesDosDias.entrySet()) {
            if (par.getValue().isSelected()) {
                dias.add(par.getKey());
            }
        }
        return dias;
    }

    private void limparFormulario() {
        estabelecimentoEmEdicao = null;
        campoNomeEstabelecimento.clear();
        for (ToggleButton botao : botoesDosDias.values()) {
            botao.setSelected(false);
        }
        btnAdicionar.setText("+ Adicionar");
        mostrarCancelar(false);
    }

    private void mostrarCancelar(boolean mostrar) {
        btnCancelar.setVisible(mostrar);
        btnCancelar.setManaged(mostrar);
    }

    /** Recarrega a tabela e o contador de registros (RN028). */
    private void atualizarTabela() {
        tabelaEstabelecimentos.setItems(
                FXCollections.observableArrayList(repositorio.listarEstabelecimentos()));
        tabelaEstabelecimentos.refresh();
        rotuloContador.setText(Formatador.contador(repositorio.listarEstabelecimentos().size(),
                "estabelecimento cadastrado", "estabelecimentos cadastrados"));
    }
}
