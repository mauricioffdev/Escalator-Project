# AGENTS.md - Escalator Project

## 1. Visão Geral e Stack
- **Projeto:** Escalator (Gerador e visualizador de escalas musicais, intervalos, licks, harmonias e tablaturas).
- **Stack Backend:** Java, Spring Boot, Maven.
- **Stack Frontend:** Thymeleaf, HTML5, CSS3, JavaScript, AlphaTab (renderização de tablatura e partitura).
- **Regra de Ouro:** Nunca sobrescrever ou quebrar as regras já validadas de cálculo de escalas, intervalos, fórmulas harmônicas, compassos, rítmica e renderização do AlphaTab. Toda nova feature deve estender o núcleo existente.

## 2. Regras Musicais Estritas (Zero Alucinação)
- **Título dos licks:** sempre `Lick N` (N sequencial dentro da escala) na tablatura e no `\title` do alphaTex. Nunca incluir escala/estilo no título — o dropdown já identifica a escala. Contrato salvo em `src/main/resources/tabs-kb/json/convencoes-licks.json`.
- **Organização dos licks:** cada modo de estudo mostra somente o seu grupo (`Pentatônica m7` e `Menor Harmônica` hoje). O lick de Frígio Dominante pertence à menor harmônica, nunca à pentatônica.
- **Fidelidade ao Braço da Guitarra:** Afinação padrão (E2 A2 D3 G3 B3 E4) como base, respeitando estritamente o mapeamento casa/corda e enarmonia correta conforme o campo harmônico/escala.
- **Base de Conhecimento (Tabs do Autor):** Licks e padrões de digitação devem ser indexados e recuperados (abordagem RAG) a partir das tablaturas reais fornecidas pelo autor no repositório, preservando mecânicas reais de guitarra (alternate picking, economy, sweep, legato, saltos de corda, bends, vibratos e subdivisões rítmicas).
- **Rítmica e Compassos:** Todo lick gerado ou recuperado deve fechar matematicamente dentro da fórmula de compasso definida (ex: 4/4, 3/4, 6/8, 5/4) e manter sintaxe válida para o AlphaTab (`alphaTex`).
- **Velocidade (regra permanente):** Nenhuma marcação de BPM/tempo nas tabs (alphaTex e ASCII) nem na referência textual. `{tempo N}`, `♩ = N` e equivalentes são proibidos; a velocidade é controlada exclusivamente pelo player.
- **Referências dos licks:** a referência textual só é preenchida quando há uma dica de estilo ou execução (ex.: `frase bluesy estilo Zakk`, `lick estilo Dime`). Nunca incluir proveniência (fonte, arquivo, faixa, compassos, página ou PDF); sem dica, a referência fica vazia e o rótulo some na tela. Contrato em `src/main/resources/tabs-kb/json/convencoes-licks.json`.

## 3. Papéis do Time (Subagents)
1. **@dev-backend-rag:** Responsável pela arquitetura Java/Spring Boot, parser/indexador das tablaturas do usuário (RAG local/estruturado por escala, modo, técnica, compasso e região do braço) e endpoints de geração/combinação de licks sem alterar a lógica core validada.
2. **@qa-musical:** Responsável por testes unitários (JUnit 5) e auditoria musical: valida se cada nota gerada pertence à escala/modo solicitado, se a digitação no braço é fisicamente tocável, se a soma rítmica fecha o compasso e se o código `alphaTex` não possui erros de sintaxe.
3. **@code-reviewer:** Revisa diffs antes da conclusão. Foca em Clean Code, SOLID, performance, imutabilidade (Records/DTOs), isolamento de responsabilidades e garantia de zero regressão nas classes core de escalas e AlphaTab.
4. **@frontend-rock:** Responsável por evoluir o HTML/CSS/JS com identidade visual Guitar/Rock/Metal (dark theme refinado, estética de amp/pedalboard/fretboard, tipografia marcante, micro-interações e responsividade) sem quebrar os IDs, classes e hooks usados pelo JavaScript e pelo AlphaTab.

## 4. Fluxo de Trabalho Obrigatório
- Antes de codar, apresentar um plano curto listando os arquivos que serão criados ou modificados.
- Trabalhar em incrementos pequenos e testáveis.
- Nenhum código é considerado pronto sem passar pela validação do `@qa-musical` e revisão do `@code-reviewer`.