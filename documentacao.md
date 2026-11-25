### Documentação Técnica do Projeto Escalator

Este documento detalha as principais tecnologias, conceitos de programação e estruturas de dados utilizados no desenvolvimento do **Escalator**, um sistema para treino de escalas e harmonia musical em Java.

#### 1. Conceitos de Orientação a Objetos (POO)

O projeto foi construído com base em princípios de POO para garantir modularidade e organização.

* **Classes**: O código é organizado em classes com responsabilidades bem definidas:
    * `App`: A classe principal, responsável pela interação com o usuário, menus e orquestração das operações.
    * `Escala`: Uma classe de "modelo" que define as propriedades de uma escala (nome e intervalos) e a lógica para calcular as notas.
    * `GeradorDePadroes`: Uma classe de "serviço" que encapsula a lógica de negócio para gerar padrões de treino (sequências e tríades).
    * `Nota` (Enum): Uma classe especial que representa um conjunto fixo de constantes (as 12 notas musicais), garantindo a integridade dos dados.

* **Encapsulamento**: As classes ocultam seus dados internos e expõem apenas a funcionalidade necessária. Por exemplo, a classe `Escala` tem um `array` de `intervalos` privado, e o acesso a ele é feito por meio do método público `calcularNotas()`. Isso impede que o estado interno do objeto seja modificado acidentalmente.

* **Métodos e Atributos Estáticos**: Foram usados para funcionalidades que não precisam de uma instância de objeto:
    * **Atributos Estáticos (`static`)**: Variáveis como `CICLO_SUSTENIDOS_EXIBICAO` e `TONICAS_MAIORES_DE_BEMOL` são estáticas porque são constantes e compartilhadas por todas as partes do programa, sem a necessidade de criar um objeto `App`.
    * **Métodos Estáticos (`static`)**: Métodos como `coletarTonica()` e `isTonicaDeBemol()` são estáticos porque realizam uma tarefa que não depende do estado de um objeto específico, podendo ser chamados diretamente da classe `App`.

#### 2. Estruturas de Dados e Coleções

O projeto faz uso de coleções de dados para armazenar e manipular informações de forma eficiente.

* **Arrays (`[]`)**: Usados para armazenar sequências de valores de um tipo específico. Por exemplo, o `int[] intervalos` da classe `Escala` define os intervalos de semitons de uma escala, e os `String[]` são usados para armazenar os nomes dos tons nos ciclos.

* **Listas (`List`)**: A `java.util.List` foi amplamente utilizada para coleções dinâmicas de objetos, como a lista de `Nota`s da escala calculada.
    * `ArrayList`: A implementação de lista mais comum, usada para armazenar as notas de uma escala (`List<Nota>`) e as sequências de padrões (`List<String>`), pois permite adicionar e acessar elementos facilmente.
    * `stream`: O uso de `stream()` para mapear e coletar dados (`.map().collect(Collectors.toList())`) é uma forma moderna e funcional de processar coleções em Java.

* **Mapas (`Map`)**: A `java.util.Map` foi fundamental para armazenar pares de chave-valor e mapear notações musicais.
    * `Map.of()`: Usado para criar mapas imutáveis de forma concisa, como `BEMOL_TO_SHARP`, garantindo que os dados não sejam alterados após a inicialização.
    * `getOrDefault()`: Um método seguro para buscar valores, retornando um valor padrão se a chave não for encontrada, evitando erros de `NullPointerException`.

#### 3. Sintaxe e Recursos da Linguagem

* **Enums**: A classe `Nota` foi implementada como um `enum`, a estrutura ideal para um conjunto de constantes fixo. O `enum` facilita o uso e a validação das notas, além de ser facilmente convertível para um valor numérico modular (0-11) para cálculos.
* **Controle de Fluxo**:
    * **Laços de Repetição (`do-while`, `for`)**: O `do-while` principal mantém o programa rodando até que o usuário decida sair, enquanto os laços `for` são usados para iterar sobre as notas de uma escala e gerar os padrões.
    * **Estruturas Condicionais (`if`, `else if`, `switch`)**: Usadas para direcionar a lógica do programa, como na escolha do modo (Maior ou Menor) e na seleção da opção de padrão de treino.
* **Manipulação de Strings**: Métodos como `toUpperCase()`, `replace()`, `contains()` e `String.format()` foram usados para processar e formatar a entrada do usuário e a saída do console, tornando a interação mais intuitiva e as saídas mais legíveis.
* **Tratamento de Exceções (`try-catch`)**: O bloco `try-catch` é usado para lidar com o erro de `IllegalArgumentException` quando o usuário digita uma nota inválida, garantindo que o programa não pare de forma abrupta.

#### 4. Licks fixos e base de conhecimento (`tabs-kb`)

A base local de tablaturas e contratos fica em `src/main/resources/tabs-kb/`:

* `pdf/Licks_Penta_Am_Rock.pdf`: fonte original dos três licks da pentatônica m7. PDFs adicionados recentemente ficam fora do Git (`.gitignore`); o que é versionado são os dados derivados.
* `json/licks-penta-am-m7-fonte.json`: dados posicionais extraídos do PDF (glifos, palavras e linhas da tablatura), usados como evidência nas auditorias musicais.
* `json/licks-penta-am-m7.json`: espelho legível do catálogo Java, gerado a partir dos DTOs públicos. Contém `id`, `padraoOpcao`, `modo`, `titulo`, `alphaTex`, `tablatura`, `referencia` e os compassos com `unidades`, `bendFull`, `vibrato` e `pullOff`.
* `json/convencoes-licks.json`: contrato aplicado a todo lick novo (título, grupo por escala, velocidade e referência).

`GeradorDeLicks` centraliza quatro licks prontos, selecionáveis pelo próprio dropdown **Padrão**:

| Valor | ID legado | Escala | Título | Conteúdo |
| --- | --- | --- | --- | --- |
| `42` | `frigio-dominante` | Menor harmônica (modo 5) | Lick 1 | E Frígio Dominante; 16 semicolcheias + 4 semicolcheias, 12 sextinas (6:4) e nota final com vibrato |
| `43` | `penta-m7-bluesy` | Pentatônica m7 (modo 3) | Lick 1 | quatro grupos de tercina `G7` (bend full) -> `B5` -> `e5`; final `B8` com bend full |
| `44` | `penta-m7-zw` | Pentatônica m7 (modo 3) | Lick 2 | dois compassos de tercinas e `D7` (Lá na 4ª corda) sustentado |
| `45` | `penta-m7-03` | Pentatônica m7 (modo 3) | Lick 3 | bend full na primeira nota `G7`, pull-offs `B8->B5` e `G8->G7->G5`, passagem cromática `G8` documentada, acorde `B5+G5` e final `D7` |

Os IDs legados continuam aceitos pelo parâmetro `lick` de URLs antigas; o valor de `padrao` tem prioridade. No template, cada escala mostra somente o seu grupo: os licks da pentatônica ficam abaixo dos shapes da pentatônica e o lick da menor harmônica fica abaixo dos shapes da menor harmônica.

#### 5. Sintaxe musical e regras permanentes dos licks

* Durações em semicolcheias: `16`, `4`, `2` e `1` unidades viram `:1`, `:4`, `:8` e `:16` no AlphaTex. Cada compasso é validado com soma exata de 16 unidades em 4/4, sem pausas implícitas.
* Efeitos: `{b (0 4)}` é bend full (dois semitons), `{h}` inicia a ligadura — o AlphaTab decide entre hammer-on e pull-off pela casa da nota seguinte na mesma corda — e `{v}` é vibrato. Tercinas usam `:8 { tu 3 }` e sextinas `:16 { tu 6 }`.
* Título na tablatura ASCII e no `\title` do AlphaTex é sempre `Lick N`, sequencial dentro da escala; escala e estilo são identificados pelo dropdown.
* Velocidade: nenhuma marcação de BPM/tempo nas tabs ou na referência (`{tempo N}` e `♩ = N` são proibidos). A velocidade é controlada somente pelo player (`#playerTempo`).
* Referência textual: só é preenchida quando há dica de estilo ou execução (ex.: `frase bluesy estilo Zakk`, `lick estilo Dime`). Nunca registra proveniência (fonte, arquivo, faixa, compassos, página ou PDF); sem dica, fica vazia e o rótulo some na tela.

#### 6. Validação automatizada

`LicksAuditoriaTest` confere compassos fechando 4/4, notas pertencentes à escala (com a exceção cromática documentada), efeitos, durações reais do AlphaTex e ausência de BPM/proveniência. `LicksJsonTest` garante que o JSON é espelho fiel do catálogo e do contrato. `EscalatorControllerTest` cobre seleção por `padrao`/`lick`, modo efetivo, rótulo da tônica e referências. O fluxo termina com `mvn -q test`, `git -c core.whitespace=cr-at-eol diff --check`, auditoria musical e revisão de código.