package br.com.listacompras.model;

import br.com.listacompras.util.Formatador;

import java.time.DayOfWeek;
import java.util.List;

/**
 * Teste da Fase 1: confere as regras do Model sem abrir nenhuma tela.
 * Cada verificação cita a história (H) ou a regra (RN) do documento de análise.
 *
 * Como rodar no IntelliJ: clique com o botão direito neste arquivo > Run 'TesteModel.main()'.
 * No final aparece quantas verificações passaram.
 */
public class TesteModel {

    private static int passou = 0;
    private static int falhou = 0;

    public static void main(String[] args) {
        Repositorio repo = Repositorio.getInstancia();

        titulo("H02/H04 — Listas de compras");
        ListaCompra semana = repo.cadastrarLista("Compras da semana");
        ListaCompra mes = repo.cadastrarLista("Lista do mês");
        ListaCompra teste = repo.cadastrarLista("Lista de teste");
        ListaCompra listaH16 = repo.cadastrarLista("Lista H16");
        igual("ids gerados em sequência", 4, listaH16.getId());
        esperarErro("H02 c2: nome vazio", "Informe o nome da lista", () -> repo.cadastrarLista("   "));
        esperarErro("H02 c3: nome repetido ignorando maiúsculas", "Já existe uma lista com esse nome",
                () -> repo.cadastrarLista("compras da semana"));
        esperarErro("H04 c2: renomear para nome de outra lista", "Já existe uma lista com esse nome",
                () -> repo.alterarLista(mes, "Compras da semana"));
        repo.alterarLista(semana, "Compras da semana");
        verificar("H04 c3: confirmar mantendo o mesmo nome não acusa duplicidade", true);

        titulo("H06/H07 — Estabelecimentos");
        Estabelecimento mercado = repo.cadastrarEstabelecimento("Mercado Central",
                List.of(DayOfWeek.FRIDAY, DayOfWeek.TUESDAY));
        Estabelecimento padaria = repo.cadastrarEstabelecimento("Padaria Bela Vista", List.of());
        Estabelecimento farmacia = repo.cadastrarEstabelecimento("Farmácia Popular",
                List.of(DayOfWeek.FRIDAY, DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.MONDAY));
        Estabelecimento loja = repo.cadastrarEstabelecimento("Loja de teste",
                List.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY));
        igual("H06 c1: dias de promoção", "Ter, Sex", mercado.getDetalhe());
        igual("H07 c3: sem dias de promoção", "Sem dias de promoção", padaria.getDetalhe());
        igual("H07 c2 + RN023: ordem da semana e sem repetir", "Seg, Qua, Sex", farmacia.getDetalhe());
        igual("RN026: domingo vem antes de sábado", "Dom, Sáb", loja.getDetalhe());
        esperarErro("H06 c3: nome vazio", "Informe o nome do estabelecimento",
                () -> repo.cadastrarEstabelecimento("", List.of()));
        esperarErro("H06 c4: nome repetido", "Já existe um estabelecimento com esse nome",
                () -> repo.cadastrarEstabelecimento("MERCADO CENTRAL", List.of()));
        repo.alterarEstabelecimento(farmacia, "Farmácia Popular", List.of());
        igual("H08 c2: retirar todos os dias", "Sem dias de promoção", farmacia.getDetalhe());

        titulo("H10/H12 — Produtos");
        Produto arroz = repo.cadastrarProduto("Arroz branco 5kg", UnidadeMedida.PACOTE, null);
        Produto leite = repo.cadastrarProduto("Leite integral", UnidadeMedida.LITRO, null);
        Produto sabonete = repo.cadastrarProduto("Sabonete", UnidadeMedida.UNIDADE, null);
        Produto produtoTeste = repo.cadastrarProduto("Produto de teste", UnidadeMedida.CAIXA, "");
        igual("H10 c1: unidade de medida", "Pacote", arroz.getDetalhe());
        verificar("H10 c2: produto sem foto usa a imagem padrão", !sabonete.temFoto() && !produtoTeste.temFoto());
        esperarErro("H10 c3: descrição vazia", "Informe a descrição do produto",
                () -> repo.cadastrarProduto(null, UnidadeMedida.UNIDADE, null));
        esperarErro("H10 c4: descrição repetida", "Já existe um produto com essa descrição",
                () -> repo.cadastrarProduto("leite integral", UnidadeMedida.LITRO, null));
        esperarErro("RN038: unidade obrigatória", "Selecione a unidade de medida",
                () -> repo.cadastrarProduto("Feijão carioca", null, null));
        esperarErro("H12 c3: renomear para descrição de outro produto", "Já existe um produto com essa descrição",
                () -> repo.alterarProduto(sabonete, "Arroz branco 5kg", UnidadeMedida.UNIDADE, null));

        titulo("H14/H15 — Montar a lista e totalizadores");
        ItemLista itemArroz = repo.incluirItem(semana, mercado, arroz, 2, 24.90);
        repo.incluirItem(semana, mercado, leite, 6, 5.50);
        repo.incluirItem(semana, mercado, sabonete, 4, 2.30);
        List<ItemLista> doMercado = repo.itensDaLista(semana, mercado);
        igual("H15 c1: três itens", 3, doMercado.size());
        igual("H15 c2: subtotal do arroz", "R$ 49,80", Formatador.moeda(itemArroz.getSubtotal()));
        igual("H15 c3: total de itens", "12", Formatador.quantidade(repo.somarQuantidades(doMercado)));
        igual("H15 c3: valor total", "R$ 92,00", Formatador.moeda(repo.somarValores(doMercado)));
        igual("Detalhe da lista (coluna da Consulta)", "3 produtos — R$ 92,00", semana.getDetalhe());
        igual("RN056: item guarda a unidade do produto", UnidadeMedida.PACOTE, itemArroz.getUnidade());
        igual("Descrição do produto no item", "Arroz branco 5kg", itemArroz.getDescricaoProduto());

        List<ItemLista> daPadaria = repo.itensDaLista(semana, padaria);
        verificar("RN058 + H15 c4: outro estabelecimento não mostra os itens e os totais ficam zerados",
                daPadaria.isEmpty() && repo.somarValores(daPadaria) == 0 && repo.somarQuantidades(daPadaria) == 0);

        ItemLista itemLeite = repo.incluirItem(semana, mercado, leite, 2, null);
        igual("H14 c5: incluir de novo não cria item repetido", 3, repo.itensDaLista(semana, mercado).size());
        igual("H14 c5: a quantidade passa a ser 8", "8", Formatador.quantidade(itemLeite.getQuantidade()));
        igual("H16 c1: subtotal do leite com 8 unidades", "R$ 44,00", Formatador.moeda(itemLeite.getSubtotal()));
        igual("H16 c1: novo valor total", "R$ 103,00", Formatador.moeda(repo.somarValores(doMercado)));

        esperarErro("H14 c3: sem lista selecionada", "Selecione a lista de compras",
                () -> repo.incluirItem(null, mercado, arroz, 1, null));
        esperarErro("H14 c4: quantidade zero", "A quantidade deve ser maior que zero",
                () -> repo.incluirItem(semana, mercado, arroz, 0, null));
        esperarErro("RN054: valor negativo", "O valor unitário não pode ser negativo",
                () -> repo.incluirItem(semana, mercado, arroz, 1, -1.0));

        ItemLista semEstabelecimento = repo.incluirItem(listaH16, null, sabonete, 4, null);
        verificar("H14 c2: item gravado sem estabelecimento", semEstabelecimento.getIdEstabelecimento() == null);
        verificar("RN054: decimal aceito e valor opcional", repo.incluirItem(listaH16, null, arroz, 1.25, null) != null);

        titulo("H16 — Editar item");
        igual("H16 c2: sem valor informado o subtotal é zero", "R$ 0,00",
                Formatador.moeda(semEstabelecimento.getSubtotal()));
        repo.alterarItem(semEstabelecimento, 4, 2.30);
        igual("H16 c2: depois de informar R$ 2,30", "R$ 9,20", Formatador.moeda(semEstabelecimento.getSubtotal()));
        esperarErro("H16 c3: quantidade zero na edição", "A quantidade deve ser maior que zero",
                () -> repo.alterarItem(itemArroz, 0, 24.90));
        igual("H16 c3: a quantidade continua 2", "2", Formatador.quantidade(itemArroz.getQuantidade()));

        titulo("H17 — Excluir item");
        repo.incluirItem(mes, mercado, arroz, 2, 24.90);
        repo.incluirItem(mes, mercado, leite, 6, 5.50);
        ItemLista saboneteMes = repo.incluirItem(mes, mercado, sabonete, 4, 2.30);
        igual("H17 c1: valor total antes", "R$ 92,00", Formatador.moeda(repo.somarValores(repo.itensDaLista(mes, mercado))));
        repo.excluirItem(saboneteMes);
        igual("H17 c1: valor total depois", "R$ 82,80", Formatador.moeda(repo.somarValores(repo.itensDaLista(mes, mercado))));
        verificar("H17 c3: o produto continua cadastrado", repo.getProdutos().contains(sabonete));

        titulo("H12 c2 — Mudar a unidade de um produto em uso (RN046)");
        boolean aviso = repo.alterarProduto(leite, "Leite integral", UnidadeMedida.UNIDADE, null);
        verificar("o sistema avisa", aviso);
        igual("os itens mantêm a unidade da época", UnidadeMedida.LITRO, itemLeite.getUnidade());
        igual("e a mesma quantidade", "8", Formatador.quantidade(itemLeite.getQuantidade()));
        verificar("sem aviso quando o produto não está em uso",
                !repo.alterarProduto(produtoTeste, "Produto de teste", UnidadeMedida.UNIDADE, null));

        titulo("H05/H09/H13 — Exclusões (polimorfismo)");
        esperarErro("H05 c3: lista com itens", "Não é possível excluir: a lista possui itens vinculados",
                () -> repo.excluir(semana));
        esperarErro("H09 c2: estabelecimento com itens",
                "Não é possível excluir: o estabelecimento possui itens vinculados", () -> repo.excluir(mercado));
        esperarErro("H13 c2: produto em uma lista", "Não é possível excluir: o produto está incluído em uma lista",
                () -> repo.excluir(arroz));
        repo.excluir(teste);
        repo.excluir(loja);
        repo.excluir(produtoTeste);
        verificar("H05 c1 / H09 c1 / H13 c1: excluídos os cadastros sem uso",
                !repo.getListas().contains(teste) && !repo.getEstabelecimentos().contains(loja)
                        && !repo.getProdutos().contains(produtoTeste));
        verificar("os cadastros em uso continuam lá",
                repo.getListas().contains(semana) && repo.getEstabelecimentos().contains(mercado)
                        && repo.getProdutos().contains(arroz));

        titulo("Polimorfismo — o mesmo método, um resultado para cada tipo");
        List<Cadastro> cadastros = List.of(semana, mercado, arroz);
        for (Cadastro cadastro : cadastros) {
            System.out.println("   " + cadastro.getClass().getSimpleName() + " \"" + cadastro.getNome()
                    + "\" -> getDetalhe() = " + cadastro.getDetalhe());
        }

        titulo("Encapsulamento");
        esperarErro("a lista devolvida pelo repositório não aceita alteração por fora", null,
                () -> repo.getListas().clear());

        System.out.println();
        System.out.println("RESULTADO: " + passou + " passaram, " + falhou + " falharam");
    }

    // ----- pequenos ajudantes do teste -----

    private static void titulo(String texto) {
        System.out.println();
        System.out.println("== " + texto);
    }

    private static void verificar(String descricao, boolean condicao) {
        if (condicao) {
            passou++;
            System.out.println("   OK     " + descricao);
        } else {
            falhou++;
            System.out.println("   FALHOU " + descricao);
        }
    }

    private static void igual(String descricao, Object esperado, Object obtido) {
        boolean ok = esperado.equals(obtido);
        verificar(descricao + (ok ? "" : "  (esperado: " + esperado + " | obtido: " + obtido + ")"), ok);
    }

    /** Executa a ação e confere se ela foi recusada com a mensagem esperada (null = qualquer erro). */
    private static void esperarErro(String descricao, String mensagemEsperada, Runnable acao) {
        try {
            acao.run();
            verificar(descricao + "  (deveria ter dado erro)", false);
        } catch (RuntimeException erro) {
            boolean ok = mensagemEsperada == null || mensagemEsperada.equals(erro.getMessage());
            verificar(descricao + (ok ? "" : "  (mensagem: " + erro.getMessage() + ")"), ok);
        }
    }
}
