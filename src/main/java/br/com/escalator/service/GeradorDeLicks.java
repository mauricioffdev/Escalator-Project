package br.com.escalator.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Licks fixos exibidos no componente de Licks da interface.
 *
 * <p>O lick de Frígio Dominante usa a escala de E Frígio Dominante
 * (E, F, G#, A, B, C, D) em 4/4. As notas foram transcritas do arquivo
 * original do Guitar Pro (faixa "E.Guitar I (Left)", compassos 1 e 2):
 * o primeiro compasso tem 16 semicolcheias, e o segundo começa com 4
 * semicolcheias, passa por 12 notas em sextinas (6:4) e termina com
 * 14-12-14 na corda 4, com vibrato na última nota.</p>
 */
public final class GeradorDeLicks {

    /** Identificador do lick de E Frígio Dominante no dropdown. */
    public static final String LICK_FRIGIO_DOMINANTE = "frigio-dominante";

    private static final String TITULO_FRIGIO_DOMINANTE =
            "Lick em Frígio Dominante (E Frígio Dominante)";
    private static final String NOTAS_FRIGIO_DOMINANTE = "[E, F, G#, A, B, C, D]";
    private static final String NOME_PADRAO_FRIGIO_DOMINANTE = "Lick - Frígio Dominante";
    private static final String TUNING = "\\tuning (E4 B3 G3 D3 A2 E2) { hide }\n";
    private static final String COMPASSO_4_4 = "4 4";

    /** Primeiro compasso: 16 semicolcheias, sem pausa. */
    private static final List<NotaLick> COMPASSO_1 = List.of(
            new NotaLick(2, 13), new NotaLick(1, 17), new NotaLick(1, 16), new NotaLick(1, 17),
            new NotaLick(1, 13), new NotaLick(1, 17), new NotaLick(1, 16), new NotaLick(1, 17),
            new NotaLick(1, 16), new NotaLick(1, 17), new NotaLick(1, 13), new NotaLick(1, 17),
            new NotaLick(1, 12), new NotaLick(1, 17), new NotaLick(2, 15), new NotaLick(1, 17)
    );

    /**
     * Segundo compasso: 4 semicolcheias, 12 notas em sextinas (6:4) e a
     * última nota como semínima com vibrato. O `:4` do AlphaTex encerra
     * a quiáltera antes dessa nota final.
     */
    private static final List<NotaLick> COMPASSO_2 = List.of(
            // Semicolcheias de abertura
            new NotaLick(2, 13), new NotaLick(1, 17), new NotaLick(2, 12), new NotaLick(1, 17),

            // Sextinas (6:4), quatro grupos de 6 notas
            new NotaLick(1, 16), new NotaLick(1, 17), new NotaLick(1, 16), new NotaLick(1, 13),
            new NotaLick(2, 17), new NotaLick(2, 15), new NotaLick(2, 13), new NotaLick(3, 16),
            new NotaLick(3, 14), new NotaLick(3, 13), new NotaLick(4, 14), new NotaLick(4, 12),

            // Nota final com vibrato
            new NotaLick(4, 14, true)
    );

    private static final int FIM_SEMICOLCHEIAS_COMPASSO_2 = 4;
    private static final int FIM_SEXTINAS_COMPASSO_2 = 16;

    private static final Lick FRIGIO_DOMINANTE = new Lick(
            LICK_FRIGIO_DOMINANTE,
            TITULO_FRIGIO_DOMINANTE,
            NOTAS_FRIGIO_DOMINANTE,
            NOME_PADRAO_FRIGIO_DOMINANTE,
            construirTablatura(),
            construirAlphaTex()
    );

    private static final Map<String, Lick> LICKS = Map.of(
            LICK_FRIGIO_DOMINANTE, FRIGIO_DOMINANTE
    );

    private GeradorDeLicks() {
    }

    /** Busca um lick pelo identificador usado no dropdown. */
    public static Lick buscar(String id) {
        return LICKS.get(id);
    }

    /** Lista os licks disponíveis para montar o dropdown. */
    public static List<Lick> listar() {
        return List.copyOf(LICKS.values());
    }

    /**
     * Lick pronto para exibição: metadados, tablatura ASCII e AlphaTex.
     */
    public record Lick(String id, String titulo, String notasFormatadas, String nomePadrao,
                       String tablatura, String alphaTex) {
    }

    private record NotaLick(int corda, int traste, boolean vibrato) {
        NotaLick(int corda, int traste) {
            this(corda, traste, false);
        }
    }

    private static String construirAlphaTex() {
        StringBuilder tex = new StringBuilder();
        tex.append("\\title \"").append(TITULO_FRIGIO_DOMINANTE).append("\"\n");
        tex.append(TUNING);
        tex.append("\\ts ").append(COMPASSO_4_4).append('\n');
        tex.append("\\ks C\n");

        tex.append(":16 ");
        adicionarNotas(tex, COMPASSO_1);
        tex.append("|\n");

        tex.append(":16 ");
        adicionarNotas(tex, COMPASSO_2.subList(0, FIM_SEMICOLCHEIAS_COMPASSO_2));

        // 6:4: seis notas no tempo de quatro semicolcheias. As 12 notas
        // seguintes totalizam dois tempos; o `:4` limpa a quiáltera.
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

    private static String construirTablatura() {
        String[] nomesCordas = {"e", "B", "G", "D", "A", "E"};
        StringBuilder[] linhas = new StringBuilder[nomesCordas.length];
        for (int i = 0; i < nomesCordas.length; i++) {
            linhas[i] = new StringBuilder(nomesCordas[i]).append(" |");
        }

        adicionarCompasso(linhas, COMPASSO_1, COMPASSO_1.size());
        adicionarSeparador(linhas);
        adicionarCompasso(linhas, COMPASSO_2, COMPASSO_2.size());
        adicionarSeparador(linhas);

        return String.join(System.lineSeparator(),
                Arrays.stream(linhas).map(StringBuilder::toString).toList());
    }

    /**
     * Escreve as notas no grid da tablatura. Cada nota/repetição ocupa três
     * colunas; as cordas sem nota naquela coluna recebem traços.
     */
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

    private static void adicionarSeparador(StringBuilder[] linhas) {
        for (StringBuilder linha : linhas) {
            linha.append("|");
        }
    }
}
