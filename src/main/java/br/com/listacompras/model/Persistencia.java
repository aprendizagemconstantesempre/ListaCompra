package br.com.listacompras.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Grava e carrega todos os dados do sistema em um arquivo JSON (NF001),
 * usando a biblioteca Gson do Google.
 *
 * O arquivo guarda as quatro listas do Repositorio, que são os quatro
 * depósitos do DFD (D1 a D4). Exemplo do conteúdo:
 *
 * <pre>
 * {
 *   "listas": [ { "id": 1, "nome": "Compras da semana" } ],
 *   "estabelecimentos": [ { "diasPromocao": ["TUESDAY", "FRIDAY"], "id": 1, "nome": "Mercado Central" } ],
 *   "produtos": [ { "unidade": "PACOTE", "id": 1, "nome": "Arroz branco 5kg" } ],
 *   "itens": [ { "id": 1, "idLista": 1, "idEstabelecimento": 1, "idProduto": 1, ... } ]
 * }
 * </pre>
 *
 * Regras atendidas: grava a cada operação (RN074), carrega ao abrir (RN075),
 * cria o arquivo vazio na primeira vez (RN076) e avisa quando o arquivo está
 * corrompido (RN077).
 */
public class Persistencia {

    /** Caminho padrão: pasta "dados" ao lado de onde o programa é executado. */
    public static final Path ARQUIVO_PADRAO = Path.of("dados", "lista_compras.json");

    private final Path arquivo;

    // setPrettyPrinting deixa o JSON com uma informação por linha, fácil de ler
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Persistencia(Path arquivo) {
        this.arquivo = arquivo;
    }

    public Path getArquivo() {
        return arquivo;
    }

    /**
     * Formato do arquivo: um objeto com as quatro listas.
     * É o Gson quem preenche estes campos ao ler o JSON.
     */
    private static class Dados {
        List<ListaCompra> listas = new ArrayList<>();
        List<Estabelecimento> estabelecimentos = new ArrayList<>();
        List<Produto> produtos = new ArrayList<>();
        List<ItemLista> itens = new ArrayList<>();
    }

    /**
     * Lê o arquivo e coloca os dados no repositório (RN075).
     * Se o arquivo ainda não existe, cria um arquivo vazio (RN076).
     *
     * @throws ValidacaoException quando o arquivo existe mas não pode ser lido (RN077).
     *         Antes de lançar o erro, guardamos uma cópia do arquivo com a extensão
     *         ".corrompido", para que os dados não se percam na próxima gravação.
     */
    public void carregar(Repositorio repositorio) {
        if (!Files.exists(arquivo)) {
            repositorio.substituirDados(new ArrayList<>(), new ArrayList<>(),
                    new ArrayList<>(), new ArrayList<>());
            salvar(repositorio);
            return;
        }

        try {
            String json = Files.readString(arquivo, StandardCharsets.UTF_8);
            Dados dados = gson.fromJson(json, Dados.class);
            if (dados == null) {
                // Arquivo vazio: começamos sem nenhum cadastro
                dados = new Dados();
            }
            repositorio.substituirDados(semNulos(dados.listas), semNulos(dados.estabelecimentos),
                    semNulos(dados.produtos), semNulos(dados.itens));
        } catch (IOException | JsonParseException | IllegalStateException erro) {
            guardarCopiaDoArquivoCorrompido();
            repositorio.substituirDados(new ArrayList<>(), new ArrayList<>(),
                    new ArrayList<>(), new ArrayList<>());
            throw new ValidacaoException("O arquivo de dados está corrompido e não pôde ser carregado. "
                    + "O sistema será iniciado sem dados.");
        }
    }

    /** Grava o conteúdo atual do repositório no arquivo (RN074). */
    public void salvar(Repositorio repositorio) {
        Dados dados = new Dados();
        dados.listas = new ArrayList<>(repositorio.getListas());
        dados.estabelecimentos = new ArrayList<>(repositorio.getEstabelecimentos());
        dados.produtos = new ArrayList<>(repositorio.getProdutos());
        dados.itens = new ArrayList<>(repositorio.getItens());

        try {
            if (arquivo.getParent() != null) {
                Files.createDirectories(arquivo.getParent());
            }
            Files.writeString(arquivo, gson.toJson(dados), StandardCharsets.UTF_8);
        } catch (IOException erro) {
            throw new ValidacaoException("Não foi possível gravar os dados no arquivo " + arquivo);
        }
    }

    /** Uma lista que não veio no JSON chega nula; trocamos por uma lista vazia. */
    private <T> List<T> semNulos(List<T> lista) {
        List<T> resultado = new ArrayList<>();
        if (lista != null) {
            for (T elemento : lista) {
                if (elemento != null) {
                    resultado.add(elemento);
                }
            }
        }
        return resultado;
    }

    private void guardarCopiaDoArquivoCorrompido() {
        try {
            Path copia = arquivo.resolveSibling(arquivo.getFileName() + ".corrompido");
            Files.copy(arquivo, copia, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException erro) {
            // Se nem a cópia der certo, seguimos: o aviso ao usuário já será exibido
        }
    }
}
