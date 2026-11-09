package br.com.listacompras.model;

/**
 * Classe pai de tudo o que o usuário cadastra: lista de compras,
 * estabelecimento e produto.
 *
 * HERANÇA: ListaCompra, Estabelecimento e Produto estendem esta classe e
 * herdam o id, o nome e o método temMesmoNome(). Assim a regra de "nome não
 * pode se repetir" (RN007, RN022 e RN037) fica escrita uma única vez.
 *
 * POLIMORFISMO: os três métodos abstratos do final são obrigatórios em cada
 * classe filha, e cada uma responde do seu jeito. Quem recebe um Cadastro
 * chama o método sem precisar saber se é uma lista, um estabelecimento ou um
 * produto.
 *
 * A classe é abstrata porque não existe um "cadastro genérico": todo objeto
 * criado é sempre uma lista, um estabelecimento ou um produto.
 */
public abstract class Cadastro {

    // ENCAPSULAMENTO: os atributos são privados e só são acessados pelos métodos abaixo
    private int id;
    private String nome;

    /**
     * Construtor usado pelas classes filhas através do super(id, nome).
     * Ele é protected porque só as filhas precisam chamá-lo.
     */
    protected Cadastro(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    /** Construtor vazio usado pelo Gson ao ler o arquivo JSON (a Persistencia preenche os campos). */
    protected Cadastro() {
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Compara o nome deste cadastro com outro nome, ignorando maiúsculas,
     * minúsculas e os espaços do início e do fim.
     * Exemplo: "Compras da semana" e "  compras DA semana " são o mesmo nome.
     */
    public boolean temMesmoNome(String outroNome) {
        if (nome == null || outroNome == null) {
            return false;
        }
        return nome.trim().equalsIgnoreCase(outroNome.trim());
    }

    /**
     * Texto curto mostrado na coluna "Detalhes" das telas.
     * Lista: "3 produtos — R$ 92,00". Estabelecimento: "Ter, Sex". Produto: "Pacote".
     */
    public abstract String getDetalhe();

    /**
     * Mensagem exibida quando o usuário tenta excluir um cadastro que já
     * está sendo usado em algum item de lista (RN019, RN034 e RN048).
     */
    public abstract String getMensagemEmUso();

    /**
     * Responde se este cadastro faz parte do item informado.
     * Cada classe filha sabe qual campo do item aponta para ela.
     */
    public abstract boolean estaVinculadoA(ItemLista item);

    /**
     * O ComboBox do JavaFX usa o toString() para escrever cada opção na tela,
     * por isso devolvemos o nome.
     */
    @Override
    public String toString() {
        return nome;
    }
}
