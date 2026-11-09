package br.com.listacompras.util;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;

import java.util.function.Consumer;

/**
 * Botões Editar e Excluir que ficam na mesma linha do registro (RN080).
 * Criamos esta classe para que as tabelas e os cartões de produto usem
 * exatamente os mesmos botões, sem repetir código em cada controller.
 */
public final class BotoesLinha {

    // Construtor privado: só usamos os métodos, sem criar objetos BotoesLinha
    private BotoesLinha() {
    }

    /** Cria o par de botões. Usado direto nos cartões de produto. */
    public static HBox criar(Runnable aoEditar, Runnable aoExcluir) {
        Button editar = new Button("Editar");
        editar.getStyleClass().addAll("botao-secundario", "botao-linha");
        editar.setOnAction(evento -> aoEditar.run());

        Button excluir = new Button("Excluir");
        excluir.getStyleClass().addAll("botao-excluir", "botao-linha");
        excluir.setOnAction(evento -> aoExcluir.run());

        HBox botoes = new HBox(6, editar, excluir);
        botoes.setAlignment(Pos.CENTER);
        return botoes;
    }

    /**
     * Coloca os botões em todas as linhas preenchidas de uma coluna da tabela.
     * Ao clicar, o método recebido é chamado com o registro daquela linha.
     */
    public static <T> void configurarColuna(TableColumn<T, Void> coluna,
                                            Consumer<T> aoEditar, Consumer<T> aoExcluir) {
        configurarColuna(coluna, null, aoEditar, aoExcluir);
    }

    /**
     * Igual ao método acima, com um botão "Montar" antes dos outros dois.
     * Usado na tabela de listas para abrir o Criar Lista com a lista da linha.
     *
     * @param aoMontar pode ser null; nesse caso a linha fica só com Editar e Excluir
     */
    public static <T> void configurarColuna(TableColumn<T, Void> coluna, Consumer<T> aoMontar,
                                            Consumer<T> aoEditar, Consumer<T> aoExcluir) {
        coluna.setCellFactory(colunaDaTabela -> new TableCell<>() {
            private final HBox botoes = criar(
                    () -> aoEditar.accept(getTableRow().getItem()),
                    () -> aoExcluir.accept(getTableRow().getItem()));

            {
                // Este bloco roda quando a célula é criada: acrescenta o botão Montar
                if (aoMontar != null) {
                    Button montar = new Button("Montar");
                    montar.getStyleClass().addAll("botao-principal", "botao-linha");
                    montar.setOnAction(evento -> aoMontar.accept(getTableRow().getItem()));
                    botoes.getChildren().add(0, montar);
                }
            }

            @Override
            protected void updateItem(Void item, boolean vazia) {
                super.updateItem(item, vazia);
                // Linhas vazias da tabela não mostram botões
                setGraphic(vazia ? null : botoes);
            }
        });
    }
}
