package br.com.listacompras.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Formata números no padrão brasileiro para mostrar na tela.
 * Criamos esta classe para não repetir a mesma formatação em vários lugares.
 */
public final class Formatador {

    private static final Locale BRASIL = Locale.of("pt", "BR");

    // Até 3 casas decimais e sem zeros sobrando: 2 -> "2", 1.25 -> "1,25"
    private static final DecimalFormat QUANTIDADE =
            new DecimalFormat("#,##0.###", DecimalFormatSymbols.getInstance(BRASIL));

    // Construtor privado: ninguém precisa criar um objeto Formatador, só usar os métodos
    private Formatador() {
    }

    /** 92.0 -> "R$ 92,00" e 1234.5 -> "R$ 1.234,50". */
    public static String moeda(double valor) {
        return String.format(BRASIL, "R$ %,.2f", valor);
    }

    /**
     * Texto do contador das telas (RN011, RN028, RN042), no singular ou no plural.
     * Exemplo: contador(3, "lista cadastrada", "listas cadastradas") -> "3 listas cadastradas".
     */
    public static String contador(int quantidade, String singular, String plural) {
        return quantidade + " " + (quantidade == 1 ? singular : plural);
    }

    /** 12.0 -> "12" e 1.25 -> "1,25". */
    public static String quantidade(double quantidade) {
        return QUANTIDADE.format(quantidade);
    }
}
