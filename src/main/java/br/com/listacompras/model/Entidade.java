package br.com.listacompras.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade base de domínio com identificador UUID autogerado e controle de timestamps.
 * Os campos de auditoria são controlados automaticamente e protegidos contra alteração externa.
 */
public abstract class Entidade {

    private String id = UUID.randomUUID().toString();
    private String dataDeCriacao = LocalDateTime.now().toString();
    private String dataDeAtualizacao = LocalDateTime.now().toString();

    protected Entidade() {
    }

    /**
     * Atualiza a data (timestamp) de modificação da entidade.
     */
    public void touch() {
        this.dataDeAtualizacao = LocalDateTime.now().toString();
    }

    public String getId() {
        return id;
    }

    public String getDataDeCriacao() {
        return dataDeCriacao;
    }

    public String getDataDeAtualizacao() {
        return dataDeAtualizacao;
    }
}
