# Cronograma de entrega — Lista de Compras (JavaFX)

**Prazo:** segunda-feira, 09/11/2026, 23:59
**Revisado em:** 02/11/2026 · **Andamento atualizado em:** 03/11/2026 (faltam 6 dias)

O sistema atende a **dois trabalhos ao mesmo tempo**:

| Disciplina | Documento | O que cobra do sistema |
|---|---|---|
| Paradigma de Programação | `Instruções_Tabalho_Bimestral.pdf` | JavaFX + Scene Builder, 3 telas (Cadastro, Consulta, Movimentação), passagem de dados entre telas, POO, herança, polimorfismo, documentação e apresentação |
| Processo de Software e Engenharia de Requisitos | `Trabalho_Bimestral_DFD_e_BDD_v1.pdf` | RF001–RF018, NF001–NF006, **RN001–RN090**, DFD com depósitos D1–D4 e histórias **H01–H18** (cenários BDD) |

**Grupo:** Aguinaldo Alves, Gustavo Pereira, Gustavo Miguel e João Pedro.

> Atenção: o BancoGafanhotos (Aguinaldo e Gustavo Pereira) vence no **mesmo
> dia**. Por isso a divisão abaixo deixa menos carga para o Gustavo Pereira.

---

## 1. Decisão tomada: como as "3 telas" do PDF de Java se encaixam na análise

O documento de análise fixa o menu em **5 opções** (RN002) e diz que a
consulta fica **na própria tela de cadastro** (RN010). Por isso **não vamos
criar tela nova**. O encaixe fica assim:

| Tela exigida no PDF de Java | Tela do sistema | Requisitos da análise |
|---|---|---|
| **Cadastro** | Cadastro de Lista, de Estabelecimento e de Produto | RF002, RF006, RF010 |
| **Consulta** | A relação (tabela/cartões) dentro dessas mesmas telas | RF003, RF007, RF011 (RN010: "na própria tela de cadastro") |
| **Movimentação** | Criar Lista de Compras, que também consulta os itens | RF014–RF017, mais RF015 |

**Fluxo de Java (Cadastro → Consulta → Movimentação → Consulta de novo):**
1. Cadastrar a lista "Compras da semana". A tabela da tela mostra a lista com **0 itens, R$ 0,00**.
2. Em Criar Lista, incluir itens.
3. Voltar ao Cadastro de Lista: a mesma linha mostra **3 itens, R$ 92,00**.
4. Tentar excluir a lista: aparece "Não é possível excluir: a lista possui itens vinculados" (RN019).

Isso prova que as telas trabalham com **os mesmos objetos**. Na apresentação
de Java, explicar que a Consulta fica embutida no Cadastro por exigência do
documento de requisitos (RN010).

---

## 2a. Atualização de 09/11: Fases 2, 3 e 4 implementadas (sem commit)

| Item | Situação |
|---|---|
| Cadastro de Lista, Estabelecimento e Produto: incluir, consultar, editar (botão vira "Salvar" + "Cancelar"), excluir com confirmação, bloqueio em uso, contadores | ✅ |
| Exclusão polimórfica única: `controller/ExclusaoCadastro.excluir(Cadastro, ...)` | ✅ |
| Criar Lista: combos com objetos, "Sem estabelecimento" (RN051), soma de quantidade (RN055), filtro (RN058), totais, editar e excluir item | ✅ |
| Passagem de dados: botão **Montar** na linha da lista → `Navegador.abrir(...)` devolve o controller → `setListaSelecionada(lista)` | ✅ |
| `model/Persistencia` com Gson: `dados/lista_compras.json`, grava a cada operação, cria vazio, avisa se corrompido (guarda cópia `.corrompido`) | ✅ |
| Sair com confirmação (botão do menu e "X" da janela) | ✅ |
| Foto do produto pelo `FileChooser` (NF006) | ✅ |
| Testes: `TesteModel` 52/52 e roteiro automático das telas 52/52 | ✅ |
| Fase 5: mensagens conferidas com o PDF do BDD; ajustes de RN012 (relação vazia), contadores "N listas cadastradas", aviso da H12 c2, RN071/RN072 ao sair e foto por link (RN088). `TesteTelasBDD` segue H01–H18: **91/91 OK** | ✅ |
| Fase 6: `docs/Documentacao_Java.pdf` (fonte em `Documentacao_Java.html`), com o roteiro da apresentação no apêndice | ✅ |
| **Falta:** ensaio da apresentação, commit e entrega (empacotar sem `target/` e sem `dados/`) | ⏳ |

## 2. Onde estamos (03/11)

| Item | Situação |
|---|---|
| `pom.xml` com JavaFX 21 e `Main` → `App` | ✅ Pronto (`fb6cd69`) |
| 5 telas FXML com CSS, ligadas aos controllers | ✅ Visual pronto |
| Model: `Cadastro` e filhas, `ItemLista`, `UnidadeMedida`, `ValidacaoException`, `Repositorio` | ✅ Pronto (`3f62c1a`), `TesteModel` 52/52 OK |
| `util/Formatador` (R$ e quantidade) e `util/Mensagem` (janela de aviso) | ✅ Pronto (`3f62c1a`) |
| Navegação Menu ↔ telas e Sair | ✅ Pronta; os 9 botões sem função mostram "Função em desenvolvimento..." (`3f62c1a`) |
| `Navegador.abrir(...)` | Troca de tela, mas **ainda não passa dados** (Fase 2) |
| Controllers ligados ao `Repositorio`, confirmações e gravação em JSON | **A fazer** (Fases 2 a 4) |

### Pontos em que as telas atuais não batem com a análise

| Regra | O que muda |
|---|---|
| RN080: "editar e excluir na mesma linha do registro" | ✅ Feito: coluna "AÇÕES" nas tabelas e botões em cada cartão de produto (`util/BotoesLinha`). Os cliques ainda mostram "Função em desenvolvimento..." (Fases 2 e 3) |
| RN018/033/047/067/070: exclusões e saída pedem confirmação | Criar uma janela de confirmação (`Alert` CONFIRMATION) reutilizável |
| RN040/RN090: imagem padrão do produto | ✅ Feito: `src/main/resources/img/produto_padrao.png`, usada na prévia e nos cartões sem foto válida |
| Coluna "Detalhes" (`getDetalhe()`) | ✅ Feito: coluna "DETALHES" em Lista, "DIAS DE PROMOCAO" em Estabelecimento e unidade no cartão de Produto |
| RN011/028/042: contador de registros | Já há `rotuloContador` nas telas; falta preencher |

---

## 3. Modelo de classes (ligado ao DFD)

Os **depósitos do DFD viram listas do `Repositorio`**. É a ponte entre as duas
disciplinas e vale citar nos dois documentos.

| Depósito do DFD | Classe | Lista no `Repositorio` |
|---|---|---|
| D1 Listas de compras | `ListaCompra` | `listas` |
| D2 Estabelecimentos | `Estabelecimento` | `estabelecimentos` |
| D3 Produtos | `Produto` | `produtos` |
| D4 Itens da lista | `ItemLista` | `itens` |

**Pacote `br.com.listacompras.model`:**

- **`Cadastro`** (classe **abstrata**, pai). Tem `id` e `nome` (`private`, com
  getters/setters) e o método herdado `temMesmoNome(String)`, que compara
  ignorando maiúsculas, minúsculas e espaços (RN007, RN022, RN037: **uma** regra
  escrita **uma** vez e herdada pelas três classes). Também declara dois métodos
  abstratos: `getDetalhe()` e `getMensagemEmUso()`.
  - `ListaCompra extends Cadastro`. `getDetalhe()` → "3 itens — R$ 92,00".
  - `Estabelecimento extends Cadastro`. Tem os dias de promoção (`DayOfWeek`,
    sem repetir; RN023). `getDetalhe()` → "Ter, Sex", na ordem Dom→Sáb (RN026),
    ou "Sem dias de promoção" (RN027).
  - `Produto extends Cadastro`. O `nome` herdado é a descrição; tem `unidade`
    (enum `UnidadeMedida`: UNIDADE, QUILOGRAMA, LITRO, PACOTE, CAIXA; RN038) e
    `caminhoFoto` (opcional). `getDetalhe()` → "Pacote".
- **`ItemLista`**: `idLista`, `idEstabelecimento` (opcional; RN051), `idProduto`,
  `unidade` (cópia da unidade no momento da inclusão; RN056), `quantidade`
  (decimal > 0; RN053), `valorUnitario` (opcional, ≥ 0; RN054) e `getSubtotal()`
  (zero se não houver valor; RN060).
- **`Repositorio`** (um só objeto para o sistema inteiro): as 4 listas,
  `itensDaLista(lista, estabelecimento)` (RN058), `estaEmUso(Cadastro)`
  (RN019/034/048) e `incluirItem(...)`, que soma a quantidade se o produto já
  estiver na lista (RN055).
- **`Persistencia`**: grava e carrega `dados/lista_compras.json` com a
  biblioteca **Gson** (uma dependência no `pom.xml`). Atende NF001 e RN074–RN077.

### Onde está cada conceito de POO (para os dois documentos e a apresentação)

| Conceito | Onde |
|---|---|
| Encapsulamento | Todos os atributos `private`, acesso só por getters/setters |
| Herança | `Cadastro` → `ListaCompra`, `Estabelecimento`, `Produto`. Os três herdam `id`, `nome` e `temMesmoNome()` |
| **Polimorfismo** | 1) Coluna "Detalhes": `cadastro.getDetalhe()` mostra texto diferente para cada tipo. 2) **Um único** método de exclusão, usado pelas 3 telas, recebe um `Cadastro`; se `repositorio.estaEmUso(c)`, mostra `c.getMensagemEmUso()`, e cada classe devolve a própria frase do BDD (H05, H09, H13) |
| Passagem de dados entre telas | (a) Todas as telas usam o mesmo `Repositorio`. (b) Botão **"Montar"** na linha da lista: o `Navegador` passa a devolver o controller, e a tela de Cadastro de Lista chama `criarListaController.setListaSelecionada(lista)`, abrindo o Criar Lista com a lista já escolhida. O Criar Lista continua no menu (RN078) |

> **Mudança em relação à versão anterior do cronograma:** saiu a ideia de
> `ProdutoUnidade`/`ProdutoPeso` (quantidade inteira ou decimal), porque a
> análise define que **qualquer** quantidade decimal > 0 é aceita. Saiu também
> a "tela de Consulta nova".

---

## 4. Divisão sugerida (cada um precisa saber explicar a própria parte)

| Integrante | Responsável por | Histórias BDD |
|---|---|---|
| **Aguinaldo** | Model, `Repositorio`, `Persistencia` (JSON), `Navegador` com passagem de dados, confirmação reutilizável | NF001 (RN074–RN077) |
| **Gustavo Pereira** (carga menor por causa do BancoGafanhotos) | Menu, Sair e Cadastro de Lista | H01, H02–H05, H18 |
| **Gustavo Miguel** | Cadastro de Estabelecimento e Cadastro de Produto (com imagem padrão) | H06–H09, H10–H13 |
| **João Pedro** | Criar Lista: incluir, consultar, editar e excluir itens, com totalizadores | H14–H17 |

O Aguinaldo entrega o Model **primeiro** (até terça), porque as telas dependem dele.

---

## 5. Cronograma por fase

| Fase | Data | O que fazer | Pronto quando… | Situação |
|---|---|---|---|---|
| **0 — Preparação** | Seg 02/11 | Commitar `pom.xml` e `Main.java`; confirmar que o app abre no IntelliJ; combinar a divisão com o grupo e mandar os dois PDFs para todos | Todos com o projeto rodando | ✅ Concluída (`fb6cd69`) |
| **1 — Base (Model)** | Ter 03/11 | **Aguinaldo:** `Cadastro`, `ListaCompra`, `Estabelecimento`, `Produto`, `UnidadeMedida`, `ItemLista` e `Repositorio` (ainda sem JSON). **Demais:** no Scene Builder, ajustar as telas: botões na linha (RN080), coluna "Detalhes", imagem padrão | Um `main` de teste cria objetos, imprime `getDetalhe()` e o `temMesmoNome("  compras DA semana ")` dá `true` | ✅ Model concluído (`3f62c1a`, teste 52/52). Ajustes nas telas concluídos em 03/11 (RN080, coluna "Detalhes" e imagem padrão), sem commit ainda |
| *Extra — navegação* | Ter 03/11 | Conferir a navegação e fazer os botões sem função mostrarem "Função em desenvolvimento..." | Todos os botões respondem ao clique | ✅ Concluída (`3f62c1a`, teste 20/20) |
| **2 — Telas de cadastro + consulta** | Qua 04/11 – Qui 05/11 | **G. Pereira:** Lista + Menu. **G. Miguel:** Estabelecimento + Produto. Tudo com validação de obrigatório (NF005; a mensagem diz qual campo), duplicidade, contador, editar e excluir com confirmação e bloqueio de exclusão em uso. **Aguinaldo:** `Mensagem.confirmar`/`aviso` e `Navegador` com passagem de dados (o método de exclusão polimórfico já está pronto no `Repositorio`) | Cenários H02–H13 passam | ⏳ Próxima |
| **3 — Movimentação (Criar Lista)** | Qui 05/11 – Sex 06/11 | **João Pedro:** combos com os cadastros (RN052), lista obrigatória e estabelecimento opcional, inclusão com soma de quantidade (RN055), tabela filtrada (RN058), subtotal, totalizadores, editar e excluir item. **Aguinaldo:** botão "Montar" passando a lista | H14–H17 passam, incluindo o cálculo de R$ 92,00 → R$ 103,00 → R$ 82,80 | ⏳ A fazer (as regras já estão no `Repositorio`) |
| **4 — Persistência JSON + Sair** | Sex 06/11 | **Aguinaldo:** Gson no `pom.xml`, gravar a cada operação (RN074), carregar ao abrir (RN075), criar arquivo vazio (RN076), avisar se estiver corrompido (RN077). **G. Pereira:** Sair com confirmação (RN070) | H18 passa: fechar, abrir e os dados continuam lá | ⏳ A fazer |
| **5 — Teste pelo BDD** | Sáb 07/11 | Rodar **todos** os cenários de H01 a H18 (~60) e marcar OK ou falha numa planilha; conferir que as mensagens são **exatamente** as do BDD; corrigir | Todos os cenários OK (ou as exceções anotadas) | ⏳ A fazer |
| **6 — Documentação + apresentação** | Dom 08/11 | Documento de Java (itens abaixo), roteiro de demonstração, ensaio; commit final | Documento pronto e cada um sabe a sua parte | ⏳ A fazer |
| **Reserva + entrega** | Seg 09/11 (até 23:59) | Folga; empacotar o projeto IntelliJ (sem `target/`, com `dados/` vazio) e entregar | Entregue | — |

### Documento de entrega de Java (Fase 6)

1. Integrantes do grupo
2. Funcionamento do sistema (pode reaproveitar o "Resumo do escopo" da análise)
3. Estrutura das classes (diagrama simples), com a tabela D1–D4 → classes
4. Conceitos de POO (tabela da seção 3)
5. Passagem de dados entre telas (como os dados são criados, armazenados, enviados, recuperados, alterados e refletidos nas outras telas)
6. Herança
7. Polimorfismo (onde, quais classes, o que varia, qual o benefício)
8. Fluxo da aplicação, com a justificativa da Consulta embutida no Cadastro (RN010)

---

## 6. Roteiro da apresentação de Java (10 itens obrigatórios)

1. **Cadastro:** cadastrar "Mercado Central" (Ter, Sex), "Arroz branco 5kg" (Pacote), "Leite integral" e "Sabonete", e a lista "Compras da semana".
2. **Consulta:** mostrar as tabelas e os contadores, e a lista com "0 itens — R$ 0,00".
3. **Movimentação:** clicar em "Montar" na lista (passagem de dados) e incluir os 3 itens do H15.
4. **Validação:** quantidade 0 → "A quantidade deve ser maior que zero".
5. **Atualização:** os totais mostram 12 itens e R$ 92,00; editar o leite para 8 → R$ 103,00.
6. **Consulta de novo:** voltar ao Cadastro de Lista e mostrar "3 itens — R$ 103,00"; tentar excluir a lista e ver o bloqueio.
7. **Passagem de dados:** mostrar no código o `setListaSelecionada(...)` e o `Repositorio`.
8. **Objetos:** fechar e abrir o app; os dados voltam do JSON.
9. **Herança e polimorfismo:** `Cadastro`, `getDetalhe()` e `getMensagemEmUso()`.
10. **Scene Builder:** abrir um FXML ao vivo.

---

## 7. Se apertar: o que cortar (em ordem)

| Item | Prioridade na análise | Impacto |
|---|---|---|
| Aviso de "cadastro em andamento" ao sair (RN072) | Parte do RF018 | Pequeno |
| Foto escolhida pelo usuário (NF006) | **Desejável** | Manter só a imagem padrão (RN040) |
| Editar item (RF016) | **Desejável** | Mas ele está no roteiro; cortar só em último caso |
| Botões na linha (RN080) | NF002, Importante | Deixar no rodapé e anotar como desvio da análise |

**Não cortar:** a gravação em JSON (NF001 é **Essencial** na análise), a
validação de obrigatório e de duplicidade, as confirmações, o bloqueio de
exclusão em uso, os totalizadores, a herança, o polimorfismo e a passagem de dados.

**Não se aplica a interface gráfica:** a RN004 ("opção inválida no menu"), porque
o menu só tem botões. Vale citar na documentação.
