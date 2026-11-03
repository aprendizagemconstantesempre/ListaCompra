package org.example;

import br.com.listacompras.App;
/**
 * Ponto de entrada do projeto.
 *
 * Esta classe NAO herda de Application de proposito: quando a classe principal
 * herda de Application e o JavaFX esta no classpath (e nao no module-path),
 * o Java recusa iniciar com o erro "JavaFX runtime components are missing".
 * Chamando o App a partir de uma classe comum, esse problema nao acontece.
 */
public class Main {

    public static void main(String[] args) {
        // Repassamos a execucao para o App, que carrega o MenuPrincipal.fxml
        App.main(args);
    }
}
