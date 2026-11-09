package br.com.listacompras.model.Enums;

/**
 * Unidades de medida aceitas para um produto (RN038).
 *
 * Usamos um enum porque a lista de opções é fixa: o usuário só pode escolher
 * uma destas cinco, e o Java não deixa criar uma unidade diferente.
 */
public enum UnidadeMedida {

    UNIDADE("Unidade"),
    QUILOGRAMA("Quilograma"),
    LITRO("Litro"),
    PACOTE("Pacote"),
    CAIXA("Caixa");

    // Texto que aparece na tela, com letra maiúscula só no começo
    private final String descricao;

    UnidadeMedida(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    /** O ComboBox mostra "Pacote" em vez de "PACOTE". */
    @Override
    public String toString() {
        return descricao;
    }
}
