package br.com.listacompras;

import br.com.listacompras.model.Estabelecimento;
import br.com.listacompras.model.ItemLista;
import br.com.listacompras.model.ListaCompra;
import br.com.listacompras.model.Produto;
import br.com.listacompras.data.Repositorio;
import br.com.listacompras.model.Enums.UnidadeMedida;
import br.com.listacompras.util.Encerramento;
import br.com.listacompras.util.Formatador;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/**
 * Teste da Fase 5: percorre as telas seguindo os cenários BDD de H01 a H18,
 * com os mesmos dados e as mesmas mensagens do documento de análise.
 *
 * O teste clica nos botões de verdade (inclusive Editar, Excluir e Montar
 * dentro das linhas) e responde sozinho às janelas de mensagem: um relógio
 * verifica a cada 50 ms se há uma janela aberta, guarda o texto dela e
 * clica no botão combinado ("Sim", "Não" ou OK).
 *
 * Os dados são gravados em uma pasta temporária, então o arquivo
 * dados/lista_compras.json do projeto não é alterado.
 *
 * Como rodar no IntelliJ: botão direito neste arquivo > Run 'TesteTelasBDD.main()'.
 */
@SuppressWarnings("unchecked")
public class TesteTelasBDD {

    private static final List<String> mensagens = new ArrayList<>();
    private static final Deque<String> respostas = new ArrayDeque<>();
    private static int passou = 0;
    private static int falhou = 0;
    private static Stage palco;

    public static void main(String[] args) throws Exception {
        Path pasta = Files.createTempDirectory("lista_compras_teste");
        Path arquivo = pasta.resolve("lista_compras.json");

        Platform.startup(() -> {
            Timeline respondedor = new Timeline(new KeyFrame(Duration.millis(50), evento -> responderJanelas()));
            respondedor.setCycleCount(Animation.INDEFINITE);
            respondedor.play();

            Platform.runLater(() -> {
                try {
                    rodar(arquivo);
                } catch (Throwable erro) {
                    erro.printStackTrace();
                    falhou++;
                }
                System.out.println("\nRESULTADO: " + passou + " passaram, " + falhou + " falharam");
                Platform.exit();
            });
        });
    }

    private static void rodar(Path arquivo) throws Exception {
        Repositorio repo = new Repositorio();
        repo.inicializar();
        verificar("RN076: arquivo criado vazio na primeira abertura", true, Files.exists(arquivo));

        palco = new Stage();
        Parent menu = FXMLLoader.load(TesteTelasBDD.class.getResource("/view/MenuPrincipal.fxml"));
        palco.setScene(new Scene(menu));
        palco.show();

        // ================================================================ H01
        titulo("H01 — Exibir menu principal");
        List<String> ordem = new ArrayList<>();
        for (Node filho : ((VBox) palco.getScene().getRoot()).getChildren()) {
            if (filho instanceof Button botao) {
                ordem.add(botao.getId());
            }
        }
        verificar("c1: cinco opções na ordem do documento",
                List.of("btnCadastrarLista", "btnCadastrarEstabelecimento", "btnCadastrarProduto",
                        "btnCriarLista", "btnSair"), ordem);
        clicar("btnCadastrarProduto");
        verificar("c2: 'Cadastrar produto' abre a tela de produtos", true, existe("painelProdutos"));
        clicar("btnVoltar");
        clicar("btnCadastrarEstabelecimento");
        clicar("btnVoltar");
        verificar("c4: ao fechar a tela volta ao menu", true, existe("btnSair"));
        System.out.println("   c3: não se aplica (o menu só tem botões; não há como digitar opção inválida)");

        // ================================================================ H02 / H03
        clicar("btnCadastrarLista");
        titulo("H03 — Consultar listas (relação vazia)");
        TableView<ListaCompra> tabelaListas = q("tabelaListas");
        verificar("c2: relação vazia", 0, tabelaListas.getItems().size());
        verificar("c2: contador zerado", "0 listas cadastradas", texto("rotuloContador"));
        verificar("RN012: aviso de relação vazia", "Ainda não há listas cadastradas",
                ((Label) tabelaListas.getPlaceholder()).getText());

        titulo("H02 — Cadastrar lista de compras");
        digitar("campoNomeLista", "Compras da semana");
        clicar("btnAdicionar");
        verificar("c1: lista aparece na relação", "Compras da semana", tabelaListas.getItems().get(0).getNome());
        verificar("c1: campo de digitação limpo", "", ((TextField) q("campoNomeLista")).getText());
        digitar("campoNomeLista", "");
        clicar("btnAdicionar");
        verificar("c2: nome vazio", "Informe o nome da lista", ultimaMensagem());
        digitar("campoNomeLista", "compras da semana");
        clicar("btnAdicionar");
        verificar("c3: nome repetido", "Já existe uma lista com esse nome", ultimaMensagem());
        verificar("c3: lista não é gravada", 1, repo.listarListasDeCompra().size());

        titulo("H03 — Consultar listas");
        digitar("campoNomeLista", "Lista do mes");
        clicar("btnAdicionar");
        digitar("campoNomeLista", "Lista de teste");
        clicar("btnAdicionar");
        verificar("c1: três listas na relação", 3, tabelaListas.getItems().size());
        verificar("c1: contador", "3 listas cadastradas", texto("rotuloContador"));
        digitar("campoNomeLista", "Churrasco de sábado");
        clicar("btnAdicionar");
        verificar("c3: relação passa a 4", "4 listas cadastradas", texto("rotuloContador"));

        titulo("H04 — Editar lista de compras");
        clicarNaLinha(tabelaListas, lista("Lista do mes"), "Editar");
        digitar("campoNomeLista", "Lista do mês");
        clicar("btnAdicionar");
        verificar("c1: nome alterado na relação", true, temNaTabela(tabelaListas, "Lista do mês"));
        clicarNaLinha(tabelaListas, lista("Lista do mês"), "Editar");
        digitar("campoNomeLista", "Compras da semana");
        clicar("btnAdicionar");
        verificar("c2: nome de outra lista", "Já existe uma lista com esse nome", ultimaMensagem());
        clicar("btnCancelar");
        verificar("c2: nome não é alterado", true, temNaTabela(tabelaListas, "Lista do mês"));
        clicarNaLinha(tabelaListas, lista("Compras da semana"), "Editar");
        clicar("btnAdicionar");
        verificar("c3: mesmo nome não acusa duplicidade", "Lista alterada com sucesso", ultimaMensagem());

        titulo("H05 — Excluir lista de compras");
        respostas.add("Sim");
        clicarNaLinha(tabelaListas, lista("Lista de teste"), "Excluir");
        verificar("c1: lista sai da relação", false, temNaTabela(tabelaListas, "Lista de teste"));
        respostas.add("Não");
        clicarNaLinha(tabelaListas, lista("Compras da semana"), "Excluir");
        verificar("c2: cancelar mantém a lista", true, temNaTabela(tabelaListas, "Compras da semana"));
        clicar("btnVoltar");

        // ================================================================ H06 a H09
        clicar("btnCadastrarEstabelecimento");
        TableView<Estabelecimento> tabelaEst = q("tabelaEstabelecimentos");

        titulo("H06 — Cadastrar estabelecimento");
        digitar("campoNomeEstabelecimento", "Mercado Central");
        marcar("btnTerca", true);
        marcar("btnSexta", true);
        clicar("btnAdicionar");
        verificar("c1: aparece com terça e sexta", "Ter, Sex", estabelecimento("Mercado Central").getDetalhe());
        digitar("campoNomeEstabelecimento", "Padaria Bela Vista");
        clicar("btnAdicionar");
        verificar("c2: sem dias de promoção", "Sem dias de promoção",
                estabelecimento("Padaria Bela Vista").getDetalhe());
        clicar("btnAdicionar");
        verificar("c3: nome vazio", "Informe o nome do estabelecimento", ultimaMensagem());
        digitar("campoNomeEstabelecimento", "MERCADO CENTRAL");
        clicar("btnAdicionar");
        verificar("c4: nome repetido", "Já existe um estabelecimento com esse nome", ultimaMensagem());
        digitar("campoNomeEstabelecimento", "");

        titulo("H07 — Consultar estabelecimentos");
        digitar("campoNomeEstabelecimento", "Farmácia Popular");
        marcar("btnSexta", true);
        marcar("btnSegunda", true);
        marcar("btnQuarta", true);
        clicar("btnAdicionar");
        verificar("c2: dias na ordem da semana", "Seg, Qua, Sex", estabelecimento("Farmácia Popular").getDetalhe());
        verificar("RN028: contador", "3 estabelecimentos cadastrados", texto("rotuloContador"));

        titulo("H08 — Editar estabelecimento");
        clicarNaLinha(tabelaEst, estabelecimento("Mercado Central"), "Editar");
        marcar("btnSexta", false);
        marcar("btnQuarta", true);
        clicar("btnAdicionar");
        verificar("c1: passa a terça e quarta", "Ter, Qua", estabelecimento("Mercado Central").getDetalhe());
        clicarNaLinha(tabelaEst, estabelecimento("Farmácia Popular"), "Editar");
        marcar("btnSegunda", false);
        marcar("btnQuarta", false);
        marcar("btnSexta", false);
        clicar("btnAdicionar");
        verificar("c2: retirar todos os dias", "Sem dias de promoção",
                estabelecimento("Farmácia Popular").getDetalhe());
        clicarNaLinha(tabelaEst, estabelecimento("Farmácia Popular"), "Editar");
        digitar("campoNomeEstabelecimento", "Mercado Central");
        clicar("btnAdicionar");
        verificar("c3: nome de outro estabelecimento", "Já existe um estabelecimento com esse nome", ultimaMensagem());
        clicar("btnCancelar");

        titulo("H09 — Excluir estabelecimento");
        digitar("campoNomeEstabelecimento", "Loja de teste");
        clicar("btnAdicionar");
        respostas.add("Sim");
        clicarNaLinha(tabelaEst, estabelecimento("Loja de teste"), "Excluir");
        verificar("c1: estabelecimento sai da relação", false, temNaTabela(tabelaEst, "Loja de teste"));
        clicar("btnVoltar");

        // ================================================================ H10 a H13
        clicar("btnCadastrarProduto");
        titulo("H10 — Cadastrar produto");
        cadastrarProduto("Arroz branco 5kg", UnidadeMedida.PACOTE);
        verificar("c1: aparece com a unidade Pacote", "Pacote", textoDoCartao("Arroz branco 5kg", 1));
        cadastrarProduto("Sabonete", UnidadeMedida.UNIDADE);
        verificar("c2: sem foto usa a imagem padrão", true, fotoDoCartao("Sabonete").contains("produto_padrao"));
        cadastrarProduto("", UnidadeMedida.UNIDADE);
        verificar("c3: descrição vazia", "Informe a descrição do produto", ultimaMensagem());
        cadastrarProduto("Leite integral", UnidadeMedida.LITRO);
        cadastrarProduto("leite integral", UnidadeMedida.LITRO);
        verificar("c4: descrição repetida", "Já existe um produto com essa descrição", ultimaMensagem());
        digitar("campoDescricao", "");

        titulo("H11 — Consultar produtos");
        cadastrarProduto("Feijão carioca", UnidadeMedida.UNIDADE);
        verificar("c1: quatro produtos", "4 produtos cadastrados", texto("rotuloContador"));
        verificar("c1: quatro cartões", 4, ((FlowPane) q("painelProdutos")).getChildren().size());

        titulo("H12 — Editar produto");
        clicarNoCartao("Feijão carioca", "Editar");
        ((ComboBox<UnidadeMedida>) q("comboUnidade")).setValue(UnidadeMedida.QUILOGRAMA);
        clicar("btnAdicionar");
        verificar("c1: passa a Quilograma", "Quilograma", textoDoCartao("Feijão carioca", 1));
        clicarNoCartao("Feijão carioca", "Editar");
        digitar("campoDescricao", "Arroz branco 5kg");
        clicar("btnAdicionar");
        verificar("c3: descrição de outro produto", "Já existe um produto com essa descrição", ultimaMensagem());
        clicar("btnCancelar");
        verificar("c3: descrição não é alterada", true, produto("Feijão carioca") != null);

        titulo("H13 — Excluir produto");
        cadastrarProduto("Produto de teste", UnidadeMedida.CAIXA);
        verificar("antes: 5 produtos", "5 produtos cadastrados", texto("rotuloContador"));
        respostas.add("Sim");
        clicarNoCartao("Produto de teste", "Excluir");
        verificar("c1 / H11 c3: produto sai da relação", "4 produtos cadastrados", texto("rotuloContador"));
        clicar("btnVoltar");

        // ================================================================ H14
        titulo("H14 — Criar lista de compras");
        clicar("btnCriarLista");
        selecionar("comboProduto", produto("Arroz branco 5kg"));
        digitar("campoQuantidade", "2");
        clicar("btnAdicionarItem");
        verificar("c3: sem lista selecionada", "Selecione a lista de compras", ultimaMensagem());
        clicar("btnVoltar");

        // Botão "Montar" na linha da lista: passagem de dados entre telas
        clicar("btnCadastrarLista");
        clicarNaLinha(q("tabelaListas"), lista("Compras da semana"), "Montar");
        verificar("Montar: abre o Criar Lista com a lista já selecionada", "Compras da semana",
                ((ComboBox<ListaCompra>) q("comboLista")).getValue().getNome());

        ComboBox<Estabelecimento> comboEst = q("comboEstabelecimento");
        TableView<ItemLista> tabelaItens = q("tabelaItens");
        comboEst.setValue(estabelecimento("Mercado Central"));
        incluirItem("Arroz branco 5kg", "2", "24,90");
        verificar("c1: item aparece na relação", "Arroz branco 5kg", descricaoDoItem(tabelaItens.getItems().get(0)));

        comboEst.setValue(comboEst.getItems().get(0)); // "Sem estabelecimento"
        incluirItem("Sabonete", "4", "");
        ItemLista sabonetaSemLocal = tabelaItens.getItems().get(0);
        verificar("c2: item gravado sem estabelecimento", null, sabonetaSemLocal.getIdEstabelecimento());

        comboEst.setValue(estabelecimento("Mercado Central"));
        incluirItem("Leite integral", "0", "5,50");
        verificar("c4: quantidade zero", "A quantidade deve ser maior que zero", ultimaMensagem());
        incluirItem("Leite integral", "6", "5,50");
        incluirItem("Leite integral", "2", "");
        verificar("c5: não cria item repetido", 2, tabelaItens.getItems().size());
        verificar("c5: quantidade passa a 8", 8.0, item(tabelaItens, "Leite integral").getQuantidade());

        // Volta o leite para 6, que é o dado usado nos cenários seguintes
        editarItem(tabelaItens, "Leite integral", "6", "5,50");

        // ================================================================ H15
        titulo("H15 — Consultar itens da lista");
        incluirItem("Sabonete", "4", "2,30");
        verificar("c1: três itens", 3, tabelaItens.getItems().size());
        verificar("c2: subtotal do arroz", "R$ 49,80", celula(tabelaItens, "Arroz branco 5kg", 4));
        verificar("c3: total de itens", "12", texto("rotuloTotalItens"));
        verificar("c3: valor total", "R$ 92,00", texto("rotuloValorTotal"));
        verificar("RN061: contador da tabela", "3 itens", texto("rotuloContador"));

        ComboBox<ListaCompra> comboLista = q("comboLista");
        comboLista.setValue(lista("Lista do mês"));
        verificar("c4: lista sem itens fica vazia", 0, tabelaItens.getItems().size());
        verificar("c4: total de itens zerado", "0", texto("rotuloTotalItens"));
        verificar("c4: valor total zerado", "R$ 0,00", texto("rotuloValorTotal"));
        comboLista.setValue(lista("Compras da semana"));

        // ================================================================ H16
        titulo("H16 — Editar item");
        editarItem(tabelaItens, "Leite integral", "8", "5,50");
        verificar("c1: subtotal do leite", "R$ 44,00", celula(tabelaItens, "Leite integral", 4));
        verificar("c1: valor total", "R$ 103,00", texto("rotuloValorTotal"));
        editarItem(tabelaItens, "Leite integral", "6", "5,50");

        editarItem(tabelaItens, "Arroz branco 5kg", "0", "24,90");
        verificar("c3: quantidade zero na edição", "A quantidade deve ser maior que zero", ultimaMensagem());
        clicar("btnCancelarItem");
        verificar("c3: quantidade continua 2", 2.0, item(tabelaItens, "Arroz branco 5kg").getQuantidade());

        comboEst.setValue(comboEst.getItems().get(0));
        editarItem(tabelaItens, "Sabonete", "4", "2,30");
        verificar("c2: valor informado depois", "R$ 9,20", celula(tabelaItens, "Sabonete", 4));
        verificar("c2: total recalculado", "R$ 9,20", texto("rotuloValorTotal"));
        comboEst.setValue(estabelecimento("Mercado Central"));

        // ================================================================ H17
        titulo("H17 — Excluir item");
        verificar("antes: valor total", "R$ 92,00", texto("rotuloValorTotal"));
        respostas.add("Não");
        clicarNaLinha(tabelaItens, item(tabelaItens, "Leite integral"), "Excluir");
        verificar("c2: cancelar mantém o item", 3, tabelaItens.getItems().size());
        verificar("c2: totais não mudam", "R$ 92,00", texto("rotuloValorTotal"));
        respostas.add("Sim");
        clicarNaLinha(tabelaItens, item(tabelaItens, "Sabonete"), "Excluir");
        verificar("c1: item sai da relação", 2, tabelaItens.getItems().size());
        verificar("c1: valor total", "R$ 82,80", texto("rotuloValorTotal"));
        verificar("c3: produto continua cadastrado", true, produto("Sabonete") != null);
        clicar("btnVoltar");

        // ================================================================ H12 c2, bloqueios e H04 c4
        titulo("H12 c2 / H05 c3 / H09 c2 / H13 c2 / H04 c4 — cadastros em uso");
        clicar("btnCadastrarProduto");
        clicarNoCartao("Leite integral", "Editar");
        ((ComboBox<UnidadeMedida>) q("comboUnidade")).setValue(UnidadeMedida.CAIXA);
        clicar("btnAdicionar");
        verificar("H12 c2: aviso de produto em listas montadas", true,
                ultimaMensagem().contains("o produto já está em listas montadas"));
        verificar("H12 c2: quantidade do item mantida", 6.0,
                itemDoProduto(repo, "Leite integral").getQuantidade());
        verificar("RN056: item guarda a unidade antiga", UnidadeMedida.LITRO,
                itemDoProduto(repo, "Leite integral").getUnidade());
        respostas.add("Sim");
        clicarNoCartao("Arroz branco 5kg", "Excluir");
        verificar("H13 c2", "Não é possível excluir: o produto está incluído em uma lista", ultimaMensagem());
        clicar("btnVoltar");

        clicar("btnCadastrarEstabelecimento");
        respostas.add("Sim");
        clicarNaLinha(q("tabelaEstabelecimentos"), estabelecimento("Mercado Central"), "Excluir");
        verificar("H09 c2", "Não é possível excluir: o estabelecimento possui itens vinculados", ultimaMensagem());
        clicar("btnVoltar");

        clicar("btnCadastrarLista");
        tabelaListas = q("tabelaListas");
        respostas.add("Sim");
        clicarNaLinha(tabelaListas, lista("Compras da semana"), "Excluir");
        verificar("H05 c3", "Não é possível excluir: a lista possui itens vinculados", ultimaMensagem());
        verificar("Consulta: a mesma lista mostra os itens da Movimentação", "3 produtos — R$ 92,00",
                detalheDaLista(lista("Compras da semana")));
        clicarNaLinha(tabelaListas, lista("Compras da semana"), "Editar");
        digitar("campoNomeLista", "Compras do mercado");
        clicar("btnAdicionar");
        verificar("H04 c4: itens continuam na lista renomeada", 3,
                repo.listarItensPorLista(lista("Compras do mercado").getId()).size());

        // ================================================================ H18
        titulo("H18 — Encerrar o sistema");
        digitar("campoNomeLista", "Lista ainda não confirmada");
        respostas.add("Não");
        boolean saiu = Encerramento.confirmarSaida(palco.getScene());
        verificar("RN072: avisa do cadastro em andamento", true,
                ultimaMensagem().startsWith("Há um cadastro em andamento"));
        verificar("RN072: Não continua no sistema", false, saiu);
        clicar("btnCancelar");
        clicar("btnVoltar");

        respostas.add("Não");
        clicar("btnSair");
        verificar("c2: pergunta ao sair", "Deseja realmente sair do sistema?", ultimaMensagem());
        verificar("c2: cancelar mantém o menu aberto", true, palco.isShowing() && existe("btnSair"));

        Files.delete(arquivo); // para provar que a saída grava o arquivo
        respostas.add("Sim");
        verificar("c1: confirmar permite encerrar", true, Encerramento.confirmarSaida(palco.getScene()));
        verificar("c1 / RN071: dados gravados no arquivo ao sair", true, Files.exists(arquivo));

        int listas = repo.listarListasDeCompra().size();
        int estabelecimentos = repo.listarEstabelecimentos().size();
        int produtos = repo.listarProdutos().size();
        repo.inicializar(); // simula abrir o sistema de novo
        verificar("c3: três listas continuam", 3, listas);
        verificar("c3: listas recarregadas do JSON", listas, repo.listarListasDeCompra().size());
        verificar("c3: estabelecimentos recarregados", estabelecimentos, repo.listarEstabelecimentos().size());
        verificar("c3: quatro produtos recarregados", 4, repo.listarProdutos().size());
        verificar("c3: produtos", produtos, repo.listarProdutos().size());
        verificar("c3: totais iguais após recarregar", "3 produtos — R$ 92,00",
                detalheDaLista(lista("Compras do mercado")));

        titulo("RN077 — Arquivo corrompido");
        Files.writeString(arquivo, "{ isto não é JSON");
        try {
            repo.inicializar();
            verificar("avisa em vez de encerrar", "aviso", "nenhum aviso");
        } catch (RuntimeException erro) {
            verificar("avisa em vez de encerrar", true, erro.getMessage().contains("corrompido"));
            verificar("guarda uma cópia do arquivo", true,
                    Files.exists(arquivo.resolveSibling("lista_compras.json.corrompido")));
        }
    }

    // ==================================================================== ajudantes

    private static void responderJanelas() {
        for (Window janela : new ArrayList<>(Window.getWindows())) {
            if (janela instanceof Stage stage && stage.isShowing() && stage.getScene() != null
                    && stage.getScene().getRoot().lookup(".dialog-pane") instanceof DialogPane painel) {
                mensagens.add(painel.getContentText());
                String resposta = respostas.poll();
                for (ButtonType tipo : painel.getButtonTypes()) {
                    if (resposta == null || tipo.getText().equals(resposta)) {
                        ((Button) painel.lookupButton(tipo)).fire();
                        break;
                    }
                }
            }
        }
    }

    private static void verificar(String descricao, Object esperado, Object obtido) {
        if (Objects.equals(esperado, obtido)) {
            passou++;
            System.out.println("   OK     " + descricao);
        } else {
            falhou++;
            System.out.println("   FALHOU " + descricao + " (esperado: " + esperado + ", obtido: " + obtido + ")");
        }
    }

    private static void titulo(String texto) {
        System.out.println("\n== " + texto);
    }

    private static String ultimaMensagem() {
        return mensagens.isEmpty() ? null : mensagens.get(mensagens.size() - 1);
    }

    private static <T extends Node> T q(String id) {
        return (T) palco.getScene().lookup("#" + id);
    }

    private static boolean existe(String id) {
        return palco.getScene().lookup("#" + id) != null;
    }

    private static void clicar(String id) {
        ((ButtonBase) q(id)).fire();
    }

    private static void digitar(String id, String texto) {
        ((TextField) q(id)).setText(texto);
    }

    private static void marcar(String id, boolean marcado) {
        ((ToggleButton) q(id)).setSelected(marcado);
    }

    private static <T> void selecionar(String id, T valor) {
        ((ComboBox<T>) q(id)).setValue(valor);
    }

    private static String texto(String id) {
        return ((Labeled) q(id)).getText();
    }

    /** Clica no botão (Editar, Excluir ou Montar) que fica na linha do registro. */
    private static <T> void clicarNaLinha(TableView<T> tabela, T registro, String textoDoBotao) {
        tabela.applyCss();
        tabela.layout();
        for (Node no : tabela.lookupAll(".table-row-cell")) {
            if (no instanceof TableRow<?> linha && java.util.Objects.equals(linha.getItem(), registro)) {
                for (Node botao : linha.lookupAll(".button")) {
                    if (botao instanceof Button b && textoDoBotao.equals(b.getText())) {
                        b.fire();
                        return;
                    }
                }
            }
        }
        throw new IllegalStateException("Botão " + textoDoBotao + " não encontrado na linha de " + registro);
    }

    private static <T> boolean temNaTabela(TableView<T> tabela, String nome) {
        for (T registro : tabela.getItems()) {
            if (registro.toString().equals(nome)) {
                return true;
            }
        }
        return false;
    }

    private static VBox cartao(String descricao) {
        for (Node no : ((FlowPane) q("painelProdutos")).getChildren()) {
            if (no instanceof VBox cartao && cartao.getChildren().get(1) instanceof Label rotulo
                    && rotulo.getText().equals(descricao)) {
                return cartao;
            }
        }
        throw new IllegalStateException("Cartão não encontrado: " + descricao);
    }

    private static void clicarNoCartao(String descricao, String textoDoBotao) {
        for (Node botao : cartao(descricao).lookupAll(".button")) {
            if (botao instanceof Button b && textoDoBotao.equals(b.getText())) {
                b.fire();
                return;
            }
        }
        throw new IllegalStateException("Botão " + textoDoBotao + " não encontrado no cartão " + descricao);
    }

    /** Texto do cartão: 1 = unidade (logo abaixo da descrição). */
    private static String textoDoCartao(String descricao, int posicaoDepoisDaDescricao) {
        return ((Label) cartao(descricao).getChildren().get(1 + posicaoDepoisDaDescricao)).getText();
    }

    private static String fotoDoCartao(String descricao) {
        ImageView foto = (ImageView) cartao(descricao).getChildren().get(0).lookup(".image-view");
        return foto.getImage().getUrl();
    }

    private static void cadastrarProduto(String descricao, UnidadeMedida unidade) {
        digitar("campoDescricao", descricao);
        ((ComboBox<UnidadeMedida>) q("comboUnidade")).setValue(unidade);
        clicar("btnAdicionar");
    }

    private static void incluirItem(String produto, String quantidade, String valor) {
        selecionar("comboProduto", produto(produto));
        digitar("campoQuantidade", quantidade);
        digitar("campoValorUnitario", valor);
        clicar("btnAdicionarItem");
    }

    private static void editarItem(TableView<ItemLista> tabela, String produto, String quantidade, String valor) {
        clicarNaLinha(tabela, item(tabela, produto), "Editar");
        digitar("campoQuantidade", quantidade);
        digitar("campoValorUnitario", valor);
        clicar("btnAdicionarItem");
    }

    private static ItemLista item(TableView<ItemLista> tabela, String produto) {
        for (ItemLista item : tabela.getItems()) {
            if (descricaoDoItem(item).equals(produto)) {
                return item;
            }
        }
        throw new IllegalStateException("Item não encontrado: " + produto);
    }

    private static ItemLista itemDoProduto(Repositorio repo, String produto) {
        for (ItemLista item : repo.listarItens()) {
            if (descricaoDoItem(item).equals(produto)) {
                return item;
            }
        }
        return null;
    }

    /** Texto de uma célula da tabela de itens: 4 = coluna SUBTOTAL. */
    private static String descricaoDoItem(ItemLista item) {
        if (item == null || item.getIdProduto() == null) return "";
        Produto p = new Repositorio().buscarProdutoPorId(item.getIdProduto());
        return p != null ? p.getDescricao() : "";
    }

    private static String detalheDaLista(ListaCompra lista) {
        if (lista == null) return "";
        Repositorio repo = new Repositorio();
        List<ItemLista> itens = repo.listarItensPorLista(lista.getId());
        String produtos = (itens.size() == 1) ? "1 produto" : itens.size() + " produtos";
        double total = itens.stream().mapToDouble(ItemLista::getSubtotal).sum();
        return produtos + " — " + Formatador.moeda(total);
    }

    private static String celula(TableView<ItemLista> tabela, String produto, int coluna) {
        return (String) tabela.getColumns().get(coluna).getCellData(item(tabela, produto));
    }

    private static ListaCompra lista(String nome) {
        for (ListaCompra lista : new Repositorio().listarListasDeCompra()) {
            if (lista.getNome().equals(nome)) {
                return lista;
            }
        }
        return null;
    }

    private static Estabelecimento estabelecimento(String nome) {
        for (Estabelecimento e : new Repositorio().listarEstabelecimentos()) {
            if (e.getNome().equals(nome)) {
                return e;
            }
        }
        return null;
    }

    private static Produto produto(String descricao) {
        for (Produto p : new Repositorio().listarProdutos()) {
            if (p.getDescricao().equals(descricao)) {
                return p;
            }
        }
        return null;
    }
}
