package br.com.listacompras.model;

import br.com.listacompras.data.Repositorio;
import br.com.listacompras.model.Enums.UnidadeMedida;
import br.com.listacompras.util.Formatador;

import java.time.DayOfWeek;
import java.util.List;

/**
 * Teste do Repositório atômico e entidades do Model com UUID e touch().
 */
public class TesteModel {

    private static int passou = 0;
    private static int falhou = 0;

    public static void main(String[] args) {
        try {
            java.nio.file.Files.deleteIfExists(java.nio.file.Path.of("dados", "lista_compras.json"));
        } catch (Exception e) {}

        Repositorio repo = new Repositorio();
        repo.inicializar();

        titulo("Listas de Compras - Operações Atômicas e Entidade Base");
        ListaCompra semana = new ListaCompra("Compras da semana");
        repo.salvarListaDeCompra(semana);
        ListaCompra mes = new ListaCompra("Lista do mês");
        repo.salvarListaDeCompra(mes);
        ListaCompra teste = new ListaCompra("Lista de teste");
        repo.salvarListaDeCompra(teste);
        ListaCompra listaH16 = new ListaCompra("Lista H16");
        repo.salvarListaDeCompra(listaH16);

        verificar("id gerado automaticamente como String UUID", listaH16.getId() != null && !listaH16.getId().isBlank());
        verificar("dataDeCriacao e dataDeAtualizacao preenchidas", listaH16.getDataDeCriacao() != null && listaH16.getDataDeAtualizacao() != null);

        // Testa hidratação a partir de uma nova instância do repositório
        ListaCompra hidratada = new Repositorio().buscarListaDeCompraPorId(listaH16.getId());
        verificar("hidratado do repositório com dataDeCriacao original", listaH16.getDataDeCriacao().equals(hidratada.getDataDeCriacao()));
        verificar("hidratado do repositório com dataDeAtualizacao original", listaH16.getDataDeAtualizacao().equals(hidratada.getDataDeAtualizacao()));

        listaH16.touch();
        verificar("touch() executado com sucesso", listaH16.getDataDeAtualizacao() != null);

        igual("busca por id", "Compras da semana", repo.buscarListaDeCompraPorId(semana.getId()).getNome());
        igual("listar listas de compra", 4, repo.listarListasDeCompra().size());

        titulo("Estabelecimentos - Operações Atômicas");
        Estabelecimento mercado = new Estabelecimento("Mercado Central", List.of(DayOfWeek.FRIDAY, DayOfWeek.TUESDAY));
        repo.salvarEstabelecimento(mercado);
        Estabelecimento padaria = new Estabelecimento("Padaria Bela Vista", List.of());
        repo.salvarEstabelecimento(padaria);
        Estabelecimento farmacia = new Estabelecimento("Farmácia Popular",
                List.of(DayOfWeek.FRIDAY, DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.MONDAY));
        repo.salvarEstabelecimento(farmacia);
        Estabelecimento loja = new Estabelecimento("Loja de teste", List.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY));
        repo.salvarEstabelecimento(loja);

        igual("H06 c1: dias de promoção", "Ter, Sex", mercado.getDetalhe());
        igual("H07 c3: sem dias de promoção", "Sem dias de promoção", padaria.getDetalhe());
        igual("H07 c2 + RN023: ordem da semana e sem repetir", "Seg, Qua, Sex", farmacia.getDetalhe());
        igual("RN026: domingo vem antes de sábado", "Dom, Sáb", loja.getDetalhe());
        igual("listar estabelecimentos", 4, repo.listarEstabelecimentos().size());

        titulo("Produtos - Operações Atômicas");
        Produto arroz = new Produto("Arroz branco 5kg", UnidadeMedida.PACOTE, null);
        repo.salvarProduto(arroz);
        Produto leite = new Produto("Leite integral", UnidadeMedida.LITRO, null);
        repo.salvarProduto(leite);
        Produto sabonete = new Produto("Sabonete", UnidadeMedida.UNIDADE, null);
        repo.salvarProduto(sabonete);
        Produto produtoTeste = new Produto("Produto de teste", UnidadeMedida.CAIXA, "");
        repo.salvarProduto(produtoTeste);

        igual("H10 c1: unidade de medida", "Pacote", arroz.getDetalhe());
        verificar("H10 c2: produto sem foto usa a imagem padrão", !sabonete.temFoto() && !produtoTeste.temFoto());
        igual("listar produtos", 4, repo.listarProdutos().size());

        titulo("Itens de Lista - Operações Atômicas");
        ItemLista itemArroz = new ItemLista(semana.getId(), mercado.getId(), arroz.getId(), arroz.getUnidade(), 2, 24.90);
        verificar("item de lista herda de Entidade e autogera id", itemArroz instanceof Entidade && itemArroz.getId() != null);
        repo.salvarItem(itemArroz);
        ItemLista itemLeite = new ItemLista(semana.getId(), mercado.getId(), leite.getId(), leite.getUnidade(), 6, 5.50);
        repo.salvarItem(itemLeite);
        ItemLista itemSabonete = new ItemLista(semana.getId(), mercado.getId(), sabonete.getId(), sabonete.getUnidade(), 4, 2.30);
        repo.salvarItem(itemSabonete);

        List<ItemLista> doMercado = repo.listarItensPorListaEEstabelecimento(semana.getId(), mercado.getId());
        igual("três itens cadastrados", 3, doMercado.size());
        igual("subtotal do arroz", "R$ 49,80", Formatador.moeda(itemArroz.getSubtotal()));

        double totalQtd = doMercado.stream().mapToDouble(ItemLista::getQuantidade).sum();
        double totalValor = doMercado.stream().mapToDouble(ItemLista::getSubtotal).sum();
        igual("total de itens calculado", "12", Formatador.quantidade(totalQtd));
        igual("valor total calculado", "R$ 92,00", Formatador.moeda(totalValor));

        List<ItemLista> itensSemana = repo.listarItensPorLista(semana.getId());
        String detalheSemana = (itensSemana.size() == 1 ? "1 produto" : itensSemana.size() + " produtos")
                + " — " + Formatador.moeda(itensSemana.stream().mapToDouble(ItemLista::getSubtotal).sum());
        igual("Detalhe da lista", "3 produtos — R$ 92,00", detalheSemana);
        igual("Descrição do produto no item", "Arroz branco 5kg", repo.buscarProdutoPorId(itemArroz.getIdProduto()).getDescricao());

        titulo("Edição e Exclusão");
        itemLeite.setQuantidade(8);
        repo.salvarItem(itemLeite);
        igual("subtotal do leite com 8 unidades", "R$ 44,00", Formatador.moeda(itemLeite.getSubtotal()));

        repo.deletarItem(itemSabonete.getId());
        igual("item excluído", 2, repo.listarItensPorListaEEstabelecimento(semana.getId(), mercado.getId()).size());

        repo.deletarListaDeCompra(teste.getId());
        repo.deletarEstabelecimento(loja.getId());
        repo.deletarProduto(produtoTeste.getId());
        verificar("cadastros deletados com sucesso",
                repo.buscarListaDeCompraPorId(teste.getId()) == null
                        && repo.buscarEstabelecimentoPorId(loja.getId()) == null
                        && repo.buscarProdutoPorId(produtoTeste.getId()) == null);

        System.out.println();
        System.out.println("RESULTADO: " + passou + " passaram, " + falhou + " falharam");
    }

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
}
