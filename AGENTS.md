# AGENTS.md - Escalator Project

## 1. Visão Geral e Stack
- **Projeto:** Escalator (Gerador e visualizador de escalas musicais, intervalos, licks, harmonias e tablaturas).
- **Stack Backend:** Java, Spring Boot, Maven.
- **Stack Frontend:** Thymeleaf, HTML5, CSS3, JavaScript, AlphaTab (renderização de tablatura e partitura).
- **Regra de Ouro:** Nunca sobrescrever ou quebrar as regras já validadas de cálculo de escalas, intervalos, fórmulas harmônicas, compassos, rítmica e renderização do AlphaTab. Toda nova feature deve estender o núcleo existente.

## 2. Regras Musicais Estritas (Zero Alucinação)
- **Fidelidade ao Braço da Guitarra:** Afinação padrão (E2 A2 D3 G3 B3 E4) como base, respeitando estritamente o mapeamento casa/corda e enarmonia correta conforme o campo harmônico/escala.
- **Base de Conhecimento (Tabs do Autor):** Licks e padrões de digitação devem ser indexados e recuperados (abordagem RAG) a partir das tablaturas reais fornecidas pelo autor no repositório, preservando mecânicas reais de guitarra (alternate picking, economy, sweep, legato, saltos de corda, bends, vibratos e subdivisões rítmicas).
- **Rítmica e Compassos:** Todo lick gerado ou recuperado deve fechar matematicamente dentro da fórmula de compasso definida (ex: 4/4, 3/4, 6/8, 5/4) e manter sintaxe válida para o AlphaTab (`alphaTex`).

## 3. Papéis do Time (Subagents)
1. **@dev-backend-rag:** Responsável pela arquitetura Java/Spring Boot, parser/indexador das tablaturas do usuário (RAG local/estruturado por escala, modo, técnica, compasso e região do braço) e endpoints de geração/combinação de licks sem alterar a lógica core validada.
2. **@qa-musical:** Responsável por testes unitários (JUnit 5) e auditoria musical: valida se cada nota gerada pertence à escala/modo solicitado, se a digitação no braço é fisicamente tocável, se a soma rítmica fecha o compasso e se o código `alphaTex` não possui erros de sintaxe.
3. **@code-reviewer:** Revisa diffs antes da conclusão. Foca em Clean Code, SOLID, performance, imutabilidade (Records/DTOs), isolamento de responsabilidades e garantia de zero regressão nas classes core de escalas e AlphaTab.
4. **@frontend-rock:** Responsável por evoluir o HTML/CSS/JS com identidade visual Guitar/Rock/Metal (dark theme refinado, estética de amp/pedalboard/fretboard, tipografia marcante, micro-interações e responsividade) sem quebrar os IDs, classes e hooks usados pelo JavaScript e pelo AlphaTab.

## 4. Fluxo de Trabalho Obrigatório
- Antes de codar, apresentar um plano curto listando os arquivos que serão criados ou modificados.
- Trabalhar em incrementos pequenos e testáveis.
- Nenhum código é considerado pronto sem passar pela validação do `@qa-musical` e revisão do `@code-reviewer`.