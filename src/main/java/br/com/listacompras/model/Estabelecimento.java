package br.com.listacompras.model;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public class Estabelecimento extends Entidade {

    private static final String[] NOMES_DIAS = {"Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom"};

    private String nome;
    private List<DayOfWeek> diasPromocao = new ArrayList<>();

    public Estabelecimento() {
    }

    public Estabelecimento(String nome, List<DayOfWeek> diasPromocao) {
        this.nome = nome;
        this.diasPromocao = organizarDias(diasPromocao);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
        touch();
    }

    @Override
    public String toString() {
        return nome != null ? nome : "";
    }

    public List<DayOfWeek> getDiasPromocao() {
        return new ArrayList<>(diasPromocao);
    }

    public void setDiasPromocao(List<DayOfWeek> dias) {
        this.diasPromocao = organizarDias(dias);
        touch();
    }

    private List<DayOfWeek> organizarDias(List<DayOfWeek> dias) {
        if (dias == null || dias.isEmpty()) {
            return new ArrayList<>();
        }

        List<DayOfWeek> ordenados = new ArrayList<>();
        if (dias.contains(DayOfWeek.SUNDAY)) {
            ordenados.add(DayOfWeek.SUNDAY);
        }
        for (DayOfWeek dia : DayOfWeek.values()) {
            if (dia != DayOfWeek.SUNDAY && dias.contains(dia) && !ordenados.contains(dia)) {
                ordenados.add(dia);
            }
        }
        return ordenados;
    }

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
}
