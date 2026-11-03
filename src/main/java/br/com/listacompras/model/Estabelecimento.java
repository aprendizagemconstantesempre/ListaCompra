package br.com.listacompras.model;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Lugar onde o usuário faz compras, com os dias da semana em que costuma
 * haver promoção (RF006 a RF009). Corresponde ao depósito D2 do DFD.
 *
 * Os dias usam o DayOfWeek do próprio Java (MONDAY, TUESDAY...), assim não
 * precisamos inventar uma forma de guardar os dias.
 */
public class Estabelecimento extends Cadastro {

    // Abreviações na ordem do DayOfWeek do Java, que começa na segunda-feira
    private static final String[] NOMES_DIAS = {"Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom"};

    private List<DayOfWeek> diasPromocao = new ArrayList<>();

    public Estabelecimento(int id, String nome, List<DayOfWeek> diasPromocao) {
        super(id, nome);
        this.diasPromocao = organizarDias(diasPromocao);
    }

    /**
     * Devolvemos uma cópia da lista. Assim, quem chamar este método não
     * consegue alterar os dias sem passar pelo setDiasPromocao (encapsulamento).
     */
    public List<DayOfWeek> getDiasPromocao() {
        return new ArrayList<>(diasPromocao);
    }

    /** Uma lista vazia ou nula significa "sem promoção" (RN024 e RN031). */
    public void setDiasPromocao(List<DayOfWeek> dias) {
        this.diasPromocao = organizarDias(dias);
    }

    /**
     * Devolve os dias sem repetir nenhum (RN023) e já em ordem, de domingo
     * a sábado (RN026). É private para que o construtor e o setter usem
     * exatamente a mesma regra.
     */
    private List<DayOfWeek> organizarDias(List<DayOfWeek> dias) {
        List<DayOfWeek> semRepetir = new ArrayList<>();
        if (dias != null) {
            for (DayOfWeek dia : dias) {
                if (dia != null && !semRepetir.contains(dia)) {
                    semRepetir.add(dia);
                }
            }
        }

        // No Java o domingo vale 7. O resto da divisão por 7 transforma o
        // domingo em 0, então ele passa a ser o primeiro da semana.
        semRepetir.sort(Comparator.comparingInt(dia -> dia.getValue() % 7));
        return semRepetir;
    }

    /** Exemplo: "Ter, Sex" ou "Sem dias de promoção" (RN025 a RN027). */
    @Override
    public String getDetalhe() {
        if (diasPromocao.isEmpty()) {
            return "Sem dias de promoção";
        }

        List<String> nomes = new ArrayList<>();
        for (DayOfWeek dia : diasPromocao) {
            nomes.add(NOMES_DIAS[dia.getValue() - 1]);
        }
        return String.join(", ", nomes);
    }

    @Override
    public String getMensagemEmUso() {
        return "Não é possível excluir: o estabelecimento possui itens vinculados";
    }

    /**
     * Usamos Objects.equals porque o item pode não ter estabelecimento
     * (o id fica nulo), e comparar nulo com == daria erro.
     */
    @Override
    public boolean estaVinculadoA(ItemLista item) {
        return Objects.equals(item.getIdEstabelecimento(), getId());
    }
}
