## Resumo de Pacotes e Classes - Escalator

### Pacotes

- `br.com.escalator`: pacote raiz com os pontos de entrada da aplicacao (`App` no modo console e `EscalatorWebApplication` no modo web/Spring Boot).
- `br.com.escalator.model`: representa o dominio musical (notas e escalas) e concentra regras base de calculo.
- `br.com.escalator.service`: contem a logica de geracao de conteudo de estudo (sequencias, triades, tablaturas, AlphaTex e licks).
- `br.com.escalator.web`: camada web MVC (controller + service + record de saida) para receber parametros da tela e devolver o resultado ao template.

### Classes

- `src/main/java/App.java`: aplicacao CLI legada; mostra menus, le entrada do usuario, decide modo (maior/menor relativa), gera notas e padroes e imprime no terminal.
- `src/main/java/br/com/escalator/EscalatorWebApplication.java`: bootstrap do Spring Boot (inicia o servidor web).

#### Modelo

- `src/main/java/br/com/escalator/model/Nota.java`: `enum` com as 12 notas cromaticas; faz conversao por valor semitonal (`0..11`) via `getNotaPorValor`, normalizacao modular (`Math.floorMod`) e `toString` que troca `_SHARP` por `#`.
- `src/main/java/br/com/escalator/model/Escala.java`: define uma escala por intervalos e calcula as notas a partir da tonica. Constantes disponiveis: `MAIOR`, `PENTATONICA_M7`, `PENTATONICA_BLUES` e `MENOR_HARMONICA` (T 2 3b 4 5 6b 7+).

#### Servicos de geracao

- `src/main/java/br/com/escalator/service/GeradorDePadroes.java`: gera padroes textuais de estudo: sequencia de N notas (`gerarSequencia`, `gerarSequenciaPorNomes`) e triades diatonicas por grau (`gerarTriades`, `gerarTriadesPorNomes`).
- `src/main/java/br/com/escalator/service/GeradorDeTablatura.java`: monta a tablatura ASCII transpondo shapes mestres para a tonalidade alvo. Contem identificadores de modo para maior, menor natural relativa, menor harmonica (3npc), pentatonica m7 e blues. Shapes disponiveis:
  - Shape 1 (3 notas por corda) para maior/menor/pentatonicas;
  - Shapes 2 e 3 para pentatonica m7 e blues (com blue note);
  - Shapes 4 e 5 e Extra apenas para pentatonica m7 (posicao aberta de 3 oitavas no Extra);
  - Menor harmonica com a 7maior escrita corretamente.
  Expondo o record `NotaTablatura(corda, traste)` (corda 1 = E aguda, 6 = E grave), usado pelo gerador de AlphaTex.
- `src/main/java/br/com/escalator/service/GeradorDeAlphaTex.java`: converte a lista de notas (corda/traste) de `GeradorDeTablatura` para AlphaTex, consumido pelo player AlphaTab. Detalhes atuais:
  - nota no formato `<traste>.<corda>` (ex.: `9.4`); ordem melodica de subida (corda 6 a 1) e retorno (corda 1 a 6);
  - `\instrument 29` define o timbre GM de guitarra com overdrive leve como padrao (o default do AlphaTex seria guitarra acustica);
  - duracoes e formulas por escala:Study 3/4 com tercinas de colcheia (3 notas por corda), pentatonica m7 em semicolcheias (12 notas por compasso 3/4), pentatonica blues em 4/4 (14 notas + pausa no shape 1, 15 notas + pausa nos shapes 2 e 3) e shape Extra em 2/4 com duas tercinas de colcheia;
  - `\ts` (formula de compasso), `\ks` (armadura de clave), `\tuning`, `\title` e `\subtitle` como metadados; sem marcacao de BPM e sem dinamicas.
- `src/main/java/br/com/escalator/service/GeradorDeLicks.java`: classe final com o catalogo de quatro licks fixos, todos com `\tuning`, `\instrument 29` e `\ts 4 4`:
  - `42` / `frigio-dominante` (modo 5, menor harmonica, `Lick 1`): E Frigio Dominante (E, F, G#, A, B, C, D); compasso 1 com 16 semicolcheias, compasso 2 com 4 semicolcheias + 12 sextinas (6:4) e nota final com vibrato;
  - `43` / `penta-m7-bluesy` (modo 3, `Lick 1`): quatro grupos de tercina `G7` (bend full) -> `B5` -> `e5` e final `B8` com bend full;
  - `44` / `penta-m7-zw` (modo 3, `Lick 2`): dois compassos de tercinas e `D7` (La na 4a corda) sustentado;
  - `45` / `penta-m7-03` (modo 3, `Lick 3`): bend full na primeira nota `G7`, pull-offs `B8->B5` e `G8->G7->G5`, passagem cromatica `G8` documentada, acorde `B5+G5` e final `D7`.
  Expoe `listar()`, `listarPorModo(modo)`, `buscar(id)` (URLs legadas), `porPadrao(valor)` e `compassosDe(id)`. Os records publicos `Lick`, `CompassoDados(numero, duracao, tuplet, golpes)`, `GolpeDados(unidades, notas)` e `NotaDados(corda, traste, bendFull, vibrato, pullOff)` sao a fonte unica para auditoria e exportacao JSON. Nenhuma marcação de BPM e titulos sempre `Lick N`.
- `src/main/resources/tabs-kb/json/`: espelhos versionados da base de tabs — `licks-penta-am-m7.json` (catalogo serializado), `licks-penta-am-m7-fonte.json` (glifos/palavras extraidos do PDF) e `convencoes-licks.json` (contrato de titulos, grupos, velocidade e referencias). O PDF de origem `tabs-kb/pdf/Licks_Penta_Am_Rock.pdf` fica fora do Git.

#### Web

- `src/main/java/br/com/escalator/web/EscalatorController.java`: endpoint `GET /` com parametros `modo`, `tonica`, `padrao`, `lick` e `gerar`. Resolve o lick por `padrao` (valor `42..45`) ou pelo parametro legado `lick`, ajusta `modoEfetivo` e `padraoEfetivo` e preserva o valor quando ele pertence a um grupo de licks. Monta as listas de tonicas (par menor/maior, com rotulo invertido conforme o modo), o rotulo da tonica (menor harmonica so mostra a menor), os grupos `licksPentaM7` e `licksMenorHarmonica` e mensagens de erro specificas para shapes fora das pentatonicas. Quando `gerar=true`, chama `gerarLick` se houver lick selecionado, senao `gerar`.
- `src/main/java/br/com/escalator/web/EscalatorWebService.java`: orquestra a geracao web. Faz parse do par relativo (ex.: `Am/C`, aceitando so a menor no modo 5), escolhe a escala conforme `modo`:
  - `1` = Maior, `2` = Menor Natural (Relativa), `3` = Pentatonica m7, `4` = Pentatonica Blues, `5` = Menor Harmonica;
  - decide notacao sustenido/bemol pela armadura da relativa maior;
  - `padrao`: `1` = sequencia de 3 notas, `2` = sequencia de triades, `3` = Shape 1, `4` = Shape 2, `5` = Shape 3, `6` = Shape 4, `7` = Shape 5, `8` = Shape Extra;
  - Shapes 4/5/Extra sao restritos a pentatonica m7; Shapes 2/3, as pentatonicas m7 ou blues;
  - para padroes com tablatura, chama `GeradorDeTablatura.gerar` e `GeradorDeAlphaTex.gerar` e devolve ambos no resultado;
  - `gerarLick(Lick)` devolve tablatura/AlphaTex prontos e preserva o texto de `referencia` do catalogo; a busca por valor legado foi removida (o controller resolve o lick).
- `src/main/java/br/com/escalator/web/ResultadoEstudo.java`: `record` imutavel de saida para a UI com 8 campos: `titulo`, `notasFormatadas`, `nomePadrao`, `linhasSaida`, `tablaturaFormatada`, `tablaturaAlphaTex`, `intervalos` e `referencia` (dica de estilo/execucao; vazia quando nao ha dica). Construtor de 7 argumentos completa `referencia` com string vazia.

#### Interface e recursos

- `src/main/resources/templates/index.html`: template Thymeleaf com formularios, braço de guitarra interativo e player AlphaTab. Pontos relevantes:
  - AlphaTab 1.8.4 carregado via ESM do CDN oficial, `useWorkers: false`, fonte no CDN e SoundFont local `/soundfonts/GeneralUser-GS.v2.0.3.sf3`;
  - controles: Play/Pause, Parar, Metronomo, Loop, progresso, `#playerTempo` (50% a 150%, default 75%) e `#playerTimbre` (`27` = Clean, `29` = Overdrive, selecionado por default);
  - na troca de timbre, atualiza `playbackInfo.program` e todas as automations de instrumento (`type === 2`) do score antes de `api.loadMidiForScore()`. Isso evita que a automation congelada do primeiro beat (criada no parse de `\instrument 29`) reaplique o overdrive sobre o clean no tick 0;
  - a troca de timbre altera apenas o MIDI/timbre: notas, ritmos e shapes permanecem os mesmos;
  - remove o rodape "rendered by alphaTab" apos o render e esconde dinamicas (`hideDynamics = true`);
  - `window.alphaTabTex` recebe o AlphaTex do `ResultadoEstudo` e `window.tabScale` ajusta a escala da tablatura para pentatonicas;
  - dropdown `Padrão` organizado por familia: shapes da pentatonica seguidos dos tres licks m7; shapes da menor harmonica seguidos do lick de Frigio Dominante; demais grupos de shapes depois. `configurarOpcoesPadrao()` filtra por ID do optgroup (nao por posicao) e trocar de modo reabilita a tonica;
  - ao selecionar um lick, `configurarSelecaoLick()` desabilita a tonica; o rotulo `Referência:` so aparece quando ha dica (texto nao vazio).
- `src/main/resources/static/css/style.css`: estilos da interface (painel, cartoes, braço, player e estados dos botoes).
- `src/main/resources/static/soundfonts/GeneralUser-GS.v2.0.3.sf3`: SoundFont local usado pelo player. O preset 29 e `Overdrive Guitar` (amostras `Overdrive Guitar-*`) e o preset 27 e `Clean Guitar`; o staccato percebido e limitacao do alphaTab 1.8.4 com loops/envelopes do SoundFont, nao presenca de piano.
- `src/main/resources/application.properties`: `spring.thymeleaf.cache=false` para refletir alteracoes do template sem reiniciar.

### Fluxo rapido (web)

1. Usuario acessa `GET /` com parametros (`modo`, `tonica`, `padrao`, `lick`, `gerar`).
2. `EscalatorController` monta dropdowns/rotulos e, se `gerar=true`, chama `EscalatorWebService` (lick ou escala).
3. `EscalatorWebService` interpreta modo/tonica/padrao, usa `Escala`, `Nota`, `GeradorDePadroes`, `GeradorDeTablatura`, `GeradorDeAlphaTex` e/ou `GeradorDeLicks` e aplica a notacao sustenido/bemol da armadura.
4. O resultado e empacotado em `ResultadoEstudo` (titulo, notas, nome do padrao, linhas, tablatura ASCII, AlphaTex, intervalos e referencia).
5. O template `index.html` renderiza a resposta; se houver AlphaTex, inicializa o player AlphaTab com o SoundFont local e os controles de tempo/timbre.
6. Ao trocar Clean/Overdrive, o JavaScript atualiza `playbackInfo.program` e as automations de instrumento do score e recarrega o MIDI sem alterar notes/ritmos/shapes.

### Testes de auditoria

- `src/test/java/br/com/escalator/qa/LicksAuditoriaTest.java`: valida compassos fechando 4/4, notas da pentatonica (com a passagem cromatica `G8` documentada), títulos `Lick N`, grupos por escala, tercinas, bends, pull-offs, duracoes reais do AlphaTex e ausencia de BPM/proveniencia nas referencias.
- `src/test/java/br/com/escalator/qa/LicksJsonTest.java`: garante que `licks-penta-am-m7.json` e espelho fiel do catalogo Java (inclusive `pullOff`), que os compassos fecham 4/4 e que os contratos de titulo/velocidade/referencia estao registrados.
- `src/test/java/br/com/escalator/web/EscalatorControllerTest.java`: cobre selecao dos licks por `padrao` (`42..45`) e por parametro legado `lick`, modo efetivo, rotulo da tonica, grupos do dropdown e referencias esperadas.
