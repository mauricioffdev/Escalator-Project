package br.com.escalator.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Licks fixos exibidos no seletor de Padrão, agrupados pela escala de origem.
 *
 * <p>Os licks de pentatônica m7 vêm de {@code tabs-kb/pdf/Licks_Penta_Am_Rock.pdf}.
 * Os frets e efeitos foram lidos das posições X/Y da tablatura impressa (corda 1
 * no topo). O lick de Frígio Dominante vem do Guitar Pro original e pertence à
 * menor harmônica, não à pentatônica.</p>
 *
 * <p>Convenção (ver {@code tabs-kb/json/convencoes-licks.json}): o título na tab
 * é sempre {@code Lick N}; a escala já é identificada pelo grupo do dropdown.</p>
 */
public final class GeradorDeLicks {

    /** Identificadores legados (parâmetro antigo {@code lick}). */
    public static final String LICK_FRIGIO_DOMINANTE = "frigio-dominante";
    public static final String LICK_PENTA_BLUESY = "penta-m7-bluesy";
    public static final String LICK_PENTA_ZW = "penta-m7-zw";
    public static final String LICK_PENTA_03 = "penta-m7-03";

    /** Valores do seletor Padrão (depois dos 41 padrões já existentes). */
    public static final String PADRAO_FRIGIO_DOMINANTE = "42";
    public static final String PADRAO_PENTA_BLUESY = "43";
    public static final String PADRAO_PENTA_ZW = "44";
    public static final String PADRAO_PENTA_03 = "45";

    /** Modos de estudo que possuem licks (valor do seletor {@code modo}). */
    public static final String MODO_PENTA_M7 = "3";
    public static final String MODO_MENOR_HARMONICA = "5";

    private static final String TITULO_LICK_1 = "Lick 1";
    private static final String TITULO_LICK_2 = "Lick 2";
    private static final String TITULO_LICK_3 = "Lick 3";
    private static final String NOTAS_FRIGIO_DOMINANTE = "[E, F, G#, A, B, C, D]";
    private static final String TUNING = "\\tuning (E4 B3 G3 D3 A2 E2) { hide }\n";
    /** GM 29: guitarra com overdrive leve, padrão usado por todos os licks. */
    private static final String INSTRUMENTO = "\\instrument 29\n";
    private static final String COMPASSO_4_4 = "4 4";

    // ---------------------------------------------------------------------
    // Menor harmônica - lick antigo do Guitar Pro (E Frígio Dominante)
    // ---------------------------------------------------------------------

    /** Primeiro compasso: 16 semicolcheias. */
    private static final List<NotaLick> COMPASSO_1 = List.of(
            new NotaLick(2, 13), new NotaLick(1, 17), new NotaLick(1, 16), new NotaLick(1, 17),
            new NotaLick(1, 13), new NotaLick(1, 17), new NotaLick(1, 16), new NotaLick(1, 17),
            new NotaLick(1, 16), new NotaLick(1, 17), new NotaLick(1, 13), new NotaLick(1, 17),
            new NotaLick(1, 12), new NotaLick(1, 17), new NotaLick(2, 15), new NotaLick(1, 17)
    );

    /** Segundo compasso: 4 semicolcheias, 12 sextinas e nota final com vibrato. */
    private static final List<NotaLick> COMPASSO_2 = List.of(
            new NotaLick(2, 13), new NotaLick(1, 17), new NotaLick(2, 12), new NotaLick(1, 17),
            new NotaLick(1, 16), new NotaLick(1, 17), new NotaLick(1, 16), new NotaLick(1, 13),
            new NotaLick(2, 17), new NotaLick(2, 15), new NotaLick(2, 13), new NotaLick(3, 16),
            new NotaLick(3, 14), new NotaLick(3, 13), new NotaLick(4, 14), new NotaLick(4, 12),
            new NotaLick(4, 14, true)
    );

    private static final int FIM_SEMICOLCHEIAS_COMPASSO_2 = 4;
    private static final int FIM_SEXTINAS_COMPASSO_2 = 16;

    // ---------------------------------------------------------------------
    // Pentatônica m7 em Am - frets e tempos lidos do PDF (corda 1 = topo)
    // ---------------------------------------------------------------------

    /**
     * 1) Bluesy: G(7) com bend full -> B(5) -> e(5), quatro grupos marcados com
     * "3" no PDF (colcheias de tercina, 12 notas fechando o compasso). O segundo
     * compasso é uma única nota B(8) presa por ligadura, com o bend full no
     * final (texto "full" no PDF), sem notas extras nem pausas.
     */
    private static final List<Compasso> COMPASSOS_BLUESY = List.of(
            new Compasso(1, ":8", "3", List.of(
                    t(bend(3, 7)), t(n(2, 5)), t(n(1, 5)),
                    t(bend(3, 7)), t(n(2, 5)), t(n(1, 5)),
                    t(bend(3, 7)), t(n(2, 5)), t(n(1, 5)),
                    t(bend(3, 7)), t(n(2, 5)), t(n(1, 5)))),
            new Compasso(2, ":1", null, List.of(g(16, bend(2, 8))))
    );

    /**
     * 2) Estilo ZW: dois compassos de tercinas (12 colcheias de tercina cada)
     * descendo pelas cordas, sem pausas. O terceiro compasso é a nota Lá na
     * quarta corda (D7) mantida por um compasso inteiro.
     */
    private static final List<Compasso> COMPASSOS_ZW = List.of(
            new Compasso(1, ":8", "3", List.of(
                    t(n(1, 8)), t(n(1, 5)), t(n(2, 8)), t(n(2, 5)),
                    t(n(3, 7)), t(n(3, 5)), t(n(2, 8)), t(n(2, 5)),
                    t(n(3, 7)), t(n(3, 5)), t(n(4, 7)), t(n(4, 5)))),
            new Compasso(2, ":8", "3", List.of(
                    t(n(3, 7)), t(n(3, 5)), t(n(4, 7)), t(n(4, 5)),
                    t(n(5, 7)), t(n(5, 5)), t(n(4, 7)), t(n(4, 5)),
                    t(n(5, 7)), t(n(5, 5)), t(n(6, 8)), t(n(6, 5)))),
            new Compasso(3, ":1", null, List.of(g(16, n(4, 7))))
    );

    /**
     * Lick 03: a primeira nota G(7) vale duas semicolcheias e recebe o bend
     * full. Há pull-off B(8)->B(5) e a cadeia G(8)->G(7)->G(5) na terceira
     * corda. O acorde B(5)+G(5) vale duas semicolcheias; não há pausas. O
     * último compasso é D(7) mantido. Nenhuma marcação de BPM é emitida.
     */
    private static final List<Compasso> COMPASSOS_LICK_03 = List.of(
            new Compasso(1, ":16", null, List.of(
                    g(2, bend(3, 7)), g(1, n(2, 5)), g(1, n(1, 5)),
                    g(1, pull(2, 8)), g(1, n(2, 5)), g(1, n(3, 7)),
                    g(1, n(2, 5)), g(1, pull(3, 8)), g(1, pull(3, 7)), g(1, n(3, 5)),
                    g(1, n(4, 7)), g(1, n(3, 7)), g(1, n(4, 7)),
                    g(2, n(2, 5), n(3, 5)))),
            new Compasso(2, ":1", null, List.of(g(16, n(4, 7))))
    );

    private static final Lick FRIGIO_DOMINANTE = new Lick(
            LICK_FRIGIO_DOMINANTE, PADRAO_FRIGIO_DOMINANTE, MODO_MENOR_HARMONICA,
            TITULO_LICK_1, NOTAS_FRIGIO_DOMINANTE,
            "Lick 1 - Menor Harmônica (Frígio Dominante)",
            construirTablatura(), construirAlphaTex(), ""
    );

    private static final Lick PENTA_BLUESY = new Lick(
            LICK_PENTA_BLUESY, PADRAO_PENTA_BLUESY, MODO_PENTA_M7,
            TITULO_LICK_1, "[A, C, D, E, G]",
            "Lick 1 - Pentatônica m7",
            construirTablaturaPenta(COMPASSOS_BLUESY),
            construirAlphaTexPenta(TITULO_LICK_1, COMPASSOS_BLUESY),
            "Frase bluesy, muito usada no rock clássico e heavy tradicional."
    );

    private static final Lick PENTA_ZW = new Lick(
            LICK_PENTA_ZW, PADRAO_PENTA_ZW, MODO_PENTA_M7,
            TITULO_LICK_2, "[A, C, D, E, G]",
            "Lick 2 - Pentatônica m7",
            construirTablaturaPenta(COMPASSOS_ZW),
            construirAlphaTexPenta(TITULO_LICK_2, COMPASSOS_ZW),
            "Frase estilo ZW (Zakk Wylde) no primeiro shape da penta de Am."
    );

    private static final Lick PENTA_03 = new Lick(
            LICK_PENTA_03, PADRAO_PENTA_03, MODO_PENTA_M7,
            TITULO_LICK_3, "[A, C, D, E, G]",
            "Lick 3 - Pentatônica m7",
            construirTablaturaPenta(COMPASSOS_LICK_03),
            construirAlphaTexPenta(TITULO_LICK_3, COMPASSOS_LICK_03),
            ""
    );

    private static final List<Lick> LICKS = List.of(
            FRIGIO_DOMINANTE, PENTA_BLUESY, PENTA_ZW, PENTA_03
    );

    private static final Map<String, Lick> POR_ID = Map.of(
            LICK_FRIGIO_DOMINANTE, FRIGIO_DOMINANTE,
            LICK_PENTA_BLUESY, PENTA_BLUESY,
            LICK_PENTA_ZW, PENTA_ZW,
            LICK_PENTA_03, PENTA_03
    );

    private static final Map<String, Lick> POR_PADRAO = Map.of(
            PADRAO_FRIGIO_DOMINANTE, FRIGIO_DOMINANTE,
            PADRAO_PENTA_BLUESY, PENTA_BLUESY,
            PADRAO_PENTA_ZW, PENTA_ZW,
            PADRAO_PENTA_03, PENTA_03
    );

    private GeradorDeLicks() {
    }

    /** Busca pelo identificador legado usado antes dos licks entrarem em Padrão. */
    public static Lick buscar(String id) {
        return id == null ? null : POR_ID.get(id);
    }

    /** Busca pelo valor do seletor Padrão. */
    public static Lick porPadrao(String padraoOpcao) {
        return padraoOpcao == null ? null : POR_PADRAO.get(padraoOpcao);
    }

    /** Lista os licks na ordem desejada no dropdown. */
    public static List<Lick> listar() {
        return LICKS;
    }

    /** Lista apenas os licks do modo de estudo informado. */
    public static List<Lick> listarPorModo(String modo) {
        return LICKS.stream().filter(lick -> lick.modo().equals(modo)).toList();
    }

    /** Nota exposta para auditoria/exportação (corda 1 = E4 no AlphaTab). */
    public record NotaDados(int corda, int traste, boolean bendFull, boolean vibrato,
                            boolean pullOff) {
    }

    /** Golpe exposto para auditoria/exportação; unidades em semicolcheias. */
    public record GolpeDados(double unidades, List<NotaDados> notas) {
    }

    /** Compasso exposto para auditoria/exportação; {@code tuplet} nulo quando não há. */
    public record CompassoDados(int numero, String duracao, String tuplet,
                                List<GolpeDados> golpes) {
    }

    /**
     * Estrutura musical de um lick, derivada das mesmas listas usadas para gerar
     * o AlphaTex. Serve de fonte única para auditoria e exportação JSON.
     */
    public static List<CompassoDados> compassosDe(String lickId) {
        if (LICK_FRIGIO_DOMINANTE.equals(lickId)) {
            List<GolpeDados> primeiro = COMPASSO_1.stream()
                    .map(nota -> golpeDe(nota, 1))
                    .toList();
            List<GolpeDados> segundo = new ArrayList<>();
            for (int i = 0; i < COMPASSO_2.size(); i++) {
                double unidades = i == COMPASSO_2.size() - 1 ? 4
                        : i >= FIM_SEMICOLCHEIAS_COMPASSO_2 && i < FIM_SEXTINAS_COMPASSO_2
                        ? 4.0 / 6.0 : 1;
                segundo.add(golpeDe(COMPASSO_2.get(i), unidades));
            }
            return List.of(new CompassoDados(1, ":16", null, primeiro),
                    new CompassoDados(2, ":4", "6:4", segundo));
        }

        List<Compasso> compassos = switch (lickId) {
            case LICK_PENTA_BLUESY -> COMPASSOS_BLUESY;
            case LICK_PENTA_ZW -> COMPASSOS_ZW;
            case LICK_PENTA_03 -> COMPASSOS_LICK_03;
            default -> throw new IllegalArgumentException("Lick desconhecido: " + lickId);
        };
        return compassos.stream()
                .map(c -> new CompassoDados(c.numero(), c.duracao(), c.tuplet(),
                        c.golpes().stream()
                                .map(g -> new GolpeDados(g.unidades(), g.notas().stream()
                                        .map(n -> new NotaDados(n.corda(), n.traste(),
                                                n.bendFull(), n.vibrato(), n.pullOff()))
                                        .toList()))
                                .toList()))
                .toList();
    }

    private static GolpeDados golpeDe(NotaLick nota, double unidades) {
        return new GolpeDados(unidades,
                List.of(new NotaDados(nota.corda(), nota.traste(), false, nota.vibrato(), false)));
    }

    /**
     * Lick pronto para exibição: escala, título simples, metadados, tablatura
     * ASCII, AlphaTex e o texto de referência preservado do exemplo original.
     */
    public record Lick(String id, String padraoOpcao, String modo, String titulo,
                       String notasFormatadas, String nomePadrao, String tablatura,
                       String alphaTex, String referencia) {
    }

    private record NotaLick(int corda, int traste, boolean vibrato) {
        NotaLick(int corda, int traste) {
            this(corda, traste, false);
        }
    }

    /** Nota em um golpe; corda 1 = E4 no AlphaTab. */
    private record NotaEvento(int corda, int traste, boolean bendFull, boolean vibrato,
                              boolean pullOff) {
    }

    /** Golpe (uma ou mais notas simultâneas, ou pausa) com duração em semicolcheias. */
    private record Golpe(List<NotaEvento> notas, double unidades) {
    }

    /** Compasso com duração base, quiáltera opcional e golpes. */
    private record Compasso(int numero, String duracao, String tuplet, List<Golpe> golpes) {
    }

    private static NotaEvento n(int corda, int traste) {
        return new NotaEvento(corda, traste, false, false, false);
    }

    /** Bend "full" do PDF: dois semitons = 4 quartos de tom no alphaTex. */
    private static NotaEvento bend(int corda, int traste) {
        return new NotaEvento(corda, traste, true, false, false);
    }

    /**
     * Pull-off: o {@code {h}} vai na nota de casa maior; o AlphaTab deduz a
     * direção da ligadura pela casa da nota seguinte na mesma corda.
     */
    private static NotaEvento pull(int corda, int traste) {
        return new NotaEvento(corda, traste, false, false, true);
    }

    private static Golpe g(double unidades, NotaEvento... notas) {
        return new Golpe(List.of(notas), unidades);
    }

    /** Colcheia de tercina: 12 notas ocupam 16 semicolcheias (4/3 cada uma). */
    private static Golpe t(NotaEvento... notas) {
        return new Golpe(List.of(notas), 4.0 / 3.0);
    }

    /** Cabeçalho AlphaTex compartilhado por todos os licks (afinação, timbre, 4/4). */
    private static StringBuilder cabecalhoAlphaTex(String titulo) {
        StringBuilder tex = new StringBuilder();
        tex.append("\\title \"").append(titulo).append("\"\n");
        tex.append(TUNING);
        tex.append(INSTRUMENTO);
        tex.append("\\ts ").append(COMPASSO_4_4).append('\n');
        tex.append("\\ks C\n");
        return tex;
    }

    private static String construirAlphaTex() {
        StringBuilder tex = cabecalhoAlphaTex(TITULO_LICK_1);

        tex.append(":16 ");
        adicionarNotas(tex, COMPASSO_1);
        tex.append("|\n");

        tex.append(":16 ");
        adicionarNotas(tex, COMPASSO_2.subList(0, FIM_SEMICOLCHEIAS_COMPASSO_2));

        tex.append(":16 { tu 6 } ");
        adicionarNotas(tex, COMPASSO_2.subList(FIM_SEMICOLCHEIAS_COMPASSO_2,
                FIM_SEXTINAS_COMPASSO_2));

        tex.append(":4 ");
        adicionarNota(tex, COMPASSO_2.get(FIM_SEXTINAS_COMPASSO_2));
        tex.append("|\n");
        return tex.toString();
    }

    private static void adicionarNotas(StringBuilder tex, List<NotaLick> notas) {
        for (NotaLick nota : notas) {
            adicionarNota(tex, nota);
        }
    }

    private static void adicionarNota(StringBuilder tex, NotaLick nota) {
        tex.append(nota.traste()).append('.').append(nota.corda());
        if (nota.vibrato()) {
            tex.append("{ v }");
        }
        tex.append(' ');
    }

    /**
     * Emite cada compasso usando as duracoes reais dos golpes: o compasso pode
     * trocar de ":8" para ":16" (Lick 3) ou usar uma unica quiAltera (:8 tu 3).
     */
    private static String construirAlphaTexPenta(String titulo, List<Compasso> compassos) {
        StringBuilder tex = cabecalhoAlphaTex(titulo);

        for (Compasso compasso : compassos) {
            Double duracaoAtual = null;
            if (compasso.tuplet() != null) {
                // Tercinas usam a duracao base do compasso para todos os golpes.
                tex.append(compasso.duracao()).append(' ');
                tex.append("{ tu ").append(compasso.tuplet()).append(" } ");
                duracaoAtual = compasso.golpes().get(0).unidades();
            }
            for (Golpe golpe : compasso.golpes()) {
                if (compasso.tuplet() == null && !mesmaDuracao(duracaoAtual, golpe.unidades())) {
                    tex.append(duracaoParaUnidades(golpe.unidades())).append(' ');
                    duracaoAtual = golpe.unidades();
                }
                if (golpe.notas().isEmpty()) {
                    tex.append("r ");
                } else if (golpe.notas().size() == 1) {
                    adicionarNotaEvento(tex, golpe.notas().get(0));
                    tex.append(' ');
                } else {
                    tex.append('(');
                    for (NotaEvento nota : golpe.notas()) {
                        adicionarNotaEvento(tex, nota);
                        tex.append(' ');
                    }
                    tex.append(") ");
                }
            }
            tex.append("|\n");
        }
        return tex.toString();
    }

    private static boolean mesmaDuracao(Double atual, double proxima) {
        return atual != null && Math.abs(atual - proxima) < 0.0001;
    }

    /** Converte unidades de semicolcheia na duracao correspondente do alphaTex. */
    private static String duracaoParaUnidades(double unidades) {
        if (Math.abs(unidades - 16.0) < 0.0001) {
            return ":1";
        }
        if (Math.abs(unidades - 4.0) < 0.0001) {
            return ":4";
        }
        if (Math.abs(unidades - 2.0) < 0.0001) {
            return ":8";
        }
        if (Math.abs(unidades - 1.0) < 0.0001) {
            return ":16";
        }
        throw new IllegalArgumentException("Duracao nao suportada no alphaTex: " + unidades);
    }

    private static void adicionarNotaEvento(StringBuilder tex, NotaEvento nota) {
        // "full" do PDF = dois semitons = 4 quartos de tom no alphaTex.
        tex.append(nota.traste()).append('.').append(nota.corda());
        if (nota.bendFull()) {
            tex.append("{b (0 4)}");
        }
        if (nota.pullOff()) {
            tex.append("{h}");
        }
        if (nota.vibrato()) {
            tex.append("{v}");
        }
    }

    private static String construirTablatura() {
        StringBuilder[] linhas = novaGrade();

        adicionarCompasso(linhas, COMPASSO_1, COMPASSO_1.size());
        adicionarSeparador(linhas);
        adicionarCompasso(linhas, COMPASSO_2, COMPASSO_2.size());
        adicionarSeparador(linhas);

        return finalizarGrade(linhas);
    }

    private static void adicionarCompasso(StringBuilder[] linhas, List<NotaLick> notas, int colunas) {
        for (int coluna = 0; coluna < colunas; coluna++) {
            NotaLick nota = coluna < notas.size() ? notas.get(coluna) : null;
            for (int corda = 1; corda <= linhas.length; corda++) {
                StringBuilder linha = linhas[corda - 1];
                if (nota != null && nota.corda() == corda) {
                    String valor = String.valueOf(nota.traste());
                    if (nota.vibrato()) {
                        valor += "~";
                    }
                    linha.append(String.format("%-3s", valor).replace(' ', '-'));
                } else {
                    linha.append("---");
                }
            }
        }
    }

    private static String construirTablaturaPenta(List<Compasso> compassos) {
        StringBuilder[] linhas = novaGrade();
        for (Compasso compasso : compassos) {
            for (Golpe golpe : compasso.golpes()) {
                for (int corda = 1; corda <= linhas.length; corda++) {
                    int cordaAtual = corda;
                    NotaEvento nota = golpe.notas().stream()
                            .filter(candidato -> candidato.corda() == cordaAtual)
                            .findFirst()
                            .orElse(null);
                    if (nota == null) {
                        linhas[cordaAtual - 1].append("---");
                    } else {
                        String valor = Integer.toString(nota.traste());
                        if (nota.bendFull()) {
                            valor += "b";
                        }
                        if (nota.pullOff()) {
                            valor += "p";
                        }
                        linhas[cordaAtual - 1].append(String.format("%-3s", valor).replace(' ', '-'));
                    }
                }
            }
            adicionarSeparador(linhas);
        }
        return finalizarGrade(linhas);
    }

    /** Cria as seis linhas da tablatura ASCII (corda 1 = e, corda 6 = E). */
    private static StringBuilder[] novaGrade() {
        String[] nomesCordas = {"e", "B", "G", "D", "A", "E"};
        StringBuilder[] linhas = new StringBuilder[nomesCordas.length];
        for (int i = 0; i < nomesCordas.length; i++) {
            linhas[i] = new StringBuilder(nomesCordas[i]).append(" |");
        }
        return linhas;
    }

    private static String finalizarGrade(StringBuilder[] linhas) {
        return String.join(System.lineSeparator(),
                Arrays.stream(linhas).map(StringBuilder::toString).toList());
    }

    private static void adicionarSeparador(StringBuilder[] linhas) {
        for (StringBuilder linha : linhas) {
            linha.append('|');
        }
    }
}
