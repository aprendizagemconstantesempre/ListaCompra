package br.com.listacompras.model;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Guarda todos os dados do sistema enquanto ele está aberto e aplica as
 * regras de negócio antes de alterar qualquer coisa.
 *
 * As quatro listas abaixo são os quatro depósitos do DFD (D1 a D4).
 *
 * PASSAGEM DE DADOS: existe um único Repositorio para o sistema inteiro
 * (getInstancia()). Todas as telas leem e alteram os mesmos objetos. Por
 * isso, um item incluído na tela "Criar lista" aparece na hora nos totais da
 * tela "Cadastrar lista".
 *
 * Quando uma regra não é cumprida, os métodos lançam ValidacaoException com
 * a mensagem para o usuário. O controller só mostra essa mensagem na tela.
 */
public class Repositorio {

    // Único objeto Repositorio do sistema, criado quando a classe é carregada
    private static final Repositorio INSTANCIA = new Repositorio();

    private final List<ListaCompra> listas = new ArrayList<>();               // D1
    private final List<Estabelecimento> estabelecimentos = new ArrayList<>(); // D2
    private final List<Produto> produtos = new ArrayList<>();                 // D3
    private final List<ItemLista> itens = new ArrayList<>();                  // D4

    // Construtor privado: ninguém de fora consegue criar um segundo Repositorio
    private Repositorio() {
    }

    public static Repositorio getInstancia() {
        return INSTANCIA;
    }

    // =====================================================================
    // Consultas (RF003, RF007, RF011). Devolvemos listas que não podem ser
    // alteradas por fora, para que toda mudança passe pelas regras abaixo.
    // =====================================================================

    public List<ListaCompra> getListas() {
        return Collections.unmodifiableList(listas);
    }

    public List<Estabelecimento> getEstabelecimentos() {
        return Collections.unmodifiableList(estabelecimentos);
    }

    public List<Produto> getProdutos() {
        return Collections.unmodifiableList(produtos);
    }

    public Produto buscarProduto(int id) {
        for (Produto produto : produtos) {
            if (produto.getId() == id) {
                return produto;
            }
        }
        return null;
    }

    // =====================================================================
    // Listas de compras (RF002, RF004)
    // =====================================================================

    public ListaCompra cadastrarLista(String nome) {
        String nomeLimpo = exigirTexto(nome, "Informe o nome da lista");                     // RN006
        recusarNomeRepetido(listas, nomeLimpo, null, "Já existe uma lista com esse nome");  // RN007

        ListaCompra lista = new ListaCompra(proximoId(listas), nomeLimpo);
        listas.add(lista);
        return lista;
    }

    public void alterarLista(ListaCompra lista, String novoNome) {
        String nomeLimpo = exigirTexto(novoNome, "Informe o nome da lista");                 // RN015
        // Passamos a própria lista como "ignorar": manter o mesmo nome não é repetição (RN016)
        recusarNomeRepetido(listas, nomeLimpo, lista, "Já existe uma lista com esse nome");
        lista.setNome(nomeLimpo); // os itens continuam ligados pelo id (RN017)
    }

    // =====================================================================
    // Estabelecimentos (RF006, RF008)
    // =====================================================================

    public Estabelecimento cadastrarEstabelecimento(String nome, List<DayOfWeek> diasPromocao) {
        String nomeLimpo = exigirTexto(nome, "Informe o nome do estabelecimento");          // RN021
        recusarNomeRepetido(estabelecimentos, nomeLimpo, null,
                "Já existe um estabelecimento com esse nome");                             // RN022

        Estabelecimento estabelecimento =
                new Estabelecimento(proximoId(estabelecimentos), nomeLimpo, diasPromocao);
        estabelecimentos.add(estabelecimento);
        return estabelecimento;
    }

    public void alterarEstabelecimento(Estabelecimento estabelecimento, String novoNome,
                                       List<DayOfWeek> diasPromocao) {
        String nomeLimpo = exigirTexto(novoNome, "Informe o nome do estabelecimento");      // RN030
        recusarNomeRepetido(estabelecimentos, nomeLimpo, estabelecimento,
                "Já existe um estabelecimento com esse nome");
        estabelecimento.setNome(nomeLimpo);
        estabelecimento.setDiasPromocao(diasPromocao); // pode ficar sem nenhum dia (RN031)
    }

    // =====================================================================
    // Produtos (RF010, RF012)
    // =====================================================================

    public Produto cadastrarProduto(String descricao, UnidadeMedida unidade, String caminhoFoto) {
        String descricaoLimpa = exigirTexto(descricao, "Informe a descrição do produto");   // RN036
        recusarNomeRepetido(produtos, descricaoLimpa, null,
                "Já existe um produto com essa descrição");                                 // RN037
        exigirUnidade(unidade);                                                             // RN038

        Produto produto = new Produto(proximoId(produtos), descricaoLimpa, unidade, caminhoFoto);
        produtos.add(produto);
        return produto;
    }

    /**
     * Altera o produto e devolve true quando a unidade mudou em um produto que
     * já está em itens de lista. Nesse caso a tela mostra o aviso da RN046.
     * Os itens não mudam, porque cada um guardou a unidade da época (RN056).
     */
    public boolean alterarProduto(Produto produto, String novaDescricao, UnidadeMedida novaUnidade,
                                  String caminhoFoto) {
        String descricaoLimpa = exigirTexto(novaDescricao, "Informe a descrição do produto"); // RN045
        recusarNomeRepetido(produtos, descricaoLimpa, produto,
                "Já existe um produto com essa descrição");
        exigirUnidade(novaUnidade);

        boolean mudouUnidadeEmUso = produto.getUnidade() != novaUnidade && estaEmUso(produto);

        produto.setDescricao(descricaoLimpa);
        produto.setUnidade(novaUnidade);
        produto.setCaminhoFoto(caminhoFoto);
        return mudouUnidadeEmUso;
    }

    // =====================================================================
    // Exclusão de cadastros (RF005, RF009, RF013) — exemplo de POLIMORFISMO
    // =====================================================================

    /**
     * Responde se algum item de lista usa este cadastro.
     * Funciona para lista, estabelecimento e produto, porque cada classe
     * responde estaVinculadoA() do seu próprio jeito.
     */
    public boolean estaEmUso(Cadastro cadastro) {
        for (ItemLista item : itens) {
            if (cadastro.estaVinculadoA(item)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Um único método de exclusão para os três tipos de cadastro.
     * Se o cadastro estiver em uso, cada tipo devolve a própria mensagem
     * (RN019, RN034 e RN048). A confirmação do usuário (RN018, RN033 e RN047)
     * é pedida pelo controller antes de chamar este método.
     */
    public void excluir(Cadastro cadastro) {
        if (estaEmUso(cadastro)) {
            throw new ValidacaoException(cadastro.getMensagemEmUso());
        }

        // O remove só encontra o objeto na lista do tipo dele; nas outras, não faz nada
        listas.remove(cadastro);
        estabelecimentos.remove(cadastro);
        produtos.remove(cadastro);
    }

    // =====================================================================
    // Itens da lista (RF014 a RF017)
    // =====================================================================

    /** Todos os itens de uma lista, de qualquer estabelecimento. */
    public List<ItemLista> itensDaLista(ListaCompra lista) {
        List<ItemLista> resultado = new ArrayList<>();
        for (ItemLista item : itens) {
            if (lista.estaVinculadoA(item)) {
                resultado.add(item);
            }
        }
        return resultado;
    }

    /**
     * Só os itens da lista E do estabelecimento escolhidos (RN058).
     * Com estabelecimento nulo, mostra os itens que ficaram sem estabelecimento.
     */
    public List<ItemLista> itensDaLista(ListaCompra lista, Estabelecimento estabelecimento) {
        Integer idEstabelecimento = (estabelecimento == null) ? null : estabelecimento.getId();

        List<ItemLista> resultado = new ArrayList<>();
        for (ItemLista item : itensDaLista(lista)) {
            if (Objects.equals(item.getIdEstabelecimento(), idEstabelecimento)) {
                resultado.add(item);
            }
        }
        return resultado;
    }

    /**
     * Inclui um produto na lista (RF014). Se o produto já estiver na mesma
     * lista e no mesmo estabelecimento, soma a quantidade ao item existente
     * em vez de criar um repetido (RN055).
     *
     * @param estabelecimento pode ser nulo (RN051)
     * @param valorUnitario   pode ser nulo (RN054)
     */
    public ItemLista incluirItem(ListaCompra lista, Estabelecimento estabelecimento, Produto produto,
                                 double quantidade, Double valorUnitario) {
        if (lista == null) {
            throw new ValidacaoException("Selecione a lista de compras");                   // RN050
        }
        if (produto == null) {
            throw new ValidacaoException("Selecione o produto");                            // RN053
        }
        validarQuantidadeEValor(quantidade, valorUnitario);

        for (ItemLista item : itensDaLista(lista, estabelecimento)) {
            if (produto.estaVinculadoA(item)) {
                item.somarQuantidade(quantidade);
                if (valorUnitario != null) {
                    item.setValorUnitario(valorUnitario);
                }
                return item;
            }
        }

        Integer idEstabelecimento = (estabelecimento == null) ? null : estabelecimento.getId();
        ItemLista novo = new ItemLista(proximoIdItem(), lista.getId(), idEstabelecimento,
                produto.getId(), produto.getUnidade(), quantidade, valorUnitario);
        itens.add(novo);
        return novo;
    }

    /** Altera quantidade e valor de um item, com as mesmas regras da inclusão (RN064, RN065). */
    public void alterarItem(ItemLista item, double novaQuantidade, Double novoValorUnitario) {
        validarQuantidadeEValor(novaQuantidade, novoValorUnitario);
        item.setQuantidade(novaQuantidade);
        item.setValorUnitario(novoValorUnitario);
    }

    /** Remove só o item. O produto continua cadastrado (RN069). */
    public void excluirItem(ItemLista item) {
        itens.remove(item);
    }

    /** Total de itens: soma das quantidades (RN061). */
    public double somarQuantidades(List<ItemLista> itensDaTela) {
        double total = 0;
        for (ItemLista item : itensDaTela) {
            total += item.getQuantidade();
        }
        return total;
    }

    /** Valor total: soma dos subtotais (RN062). */
    public double somarValores(List<ItemLista> itensDaTela) {
        double total = 0;
        for (ItemLista item : itensDaTela) {
            total += item.getSubtotal();
        }
        return total;
    }

    // =====================================================================
    // Regras usadas por vários métodos acima
    // =====================================================================

    /** Campo obrigatório (NF005): recusa texto vazio e devolve o texto sem espaços nas pontas. */
    private String exigirTexto(String texto, String mensagem) {
        if (texto == null || texto.isBlank()) {
            throw new ValidacaoException(mensagem);
        }
        return texto.trim();
    }

    /**
     * Recusa um nome que já existe na lista informada.
     * Aceita lista de qualquer filho de Cadastro e usa o temMesmoNome() herdado:
     * o mesmo código serve para listas, estabelecimentos e produtos.
     *
     * @param ignorar o próprio cadastro que está sendo editado (ou null no cadastro novo)
     */
    private void recusarNomeRepetido(List<? extends Cadastro> cadastros, String nome,
                                     Cadastro ignorar, String mensagem) {
        for (Cadastro cadastro : cadastros) {
            if (cadastro != ignorar && cadastro.temMesmoNome(nome)) {
                throw new ValidacaoException(mensagem);
            }
        }
    }

    private void exigirUnidade(UnidadeMedida unidade) {
        if (unidade == null) {
            throw new ValidacaoException("Selecione a unidade de medida");
        }
    }

    private void validarQuantidadeEValor(double quantidade, Double valorUnitario) {
        if (quantidade <= 0) {
            throw new ValidacaoException("A quantidade deve ser maior que zero");           // RN053
        }
        if (valorUnitario != null && valorUnitario < 0) {
            throw new ValidacaoException("O valor unitário não pode ser negativo");         // RN054
        }
    }

    /** Próximo id livre: o maior id existente mais 1. */
    private int proximoId(List<? extends Cadastro> cadastros) {
        int maior = 0;
        for (Cadastro cadastro : cadastros) {
            maior = Math.max(maior, cadastro.getId());
        }
        return maior + 1;
    }

    private int proximoIdItem() {
        int maior = 0;
        for (ItemLista item : itens) {
            maior = Math.max(maior, item.getId());
        }
        return maior + 1;
    }
}
