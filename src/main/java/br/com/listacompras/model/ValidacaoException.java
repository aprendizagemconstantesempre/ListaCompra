package br.com.listacompras.model;

/**
 * Erro lançado quando uma operação não pode ser realizada, por exemplo
 * um nome em branco, um nome repetido ou uma exclusão de cadastro em uso.
 *
 * A mensagem já é o texto que o usuário vai ler, igual ao das histórias de
 * usuário (BDD). O controller só precisa pegar a mensagem com getMessage()
 * e mostrar em uma janela de aviso.
 *
 * Também é um exemplo de herança: aproveitamos tudo o que a RuntimeException
 * do Java já faz e só damos um nome próprio para o nosso tipo de erro.
 */
public class ValidacaoException extends RuntimeException {

    // Número de versão que o Java pede para classes de erro; não precisamos mexer nele
    private static final long serialVersionUID = 1L;

    public ValidacaoException(String mensagem) {
        super(mensagem);
    }
}
