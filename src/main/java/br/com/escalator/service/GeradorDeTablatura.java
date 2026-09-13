package br.com.escalator.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeradorDeTablatura {

    /** Identificador do shape/picking pentatônica m7 (2 notas por corda). */
    public static final String MODO_PENTATONICA_M7 = "pentatonica m7";
    /** Identificador da pentatônica blues (2-3 notas por corda). */
    public static final String MODO_PENTATONICA_BLUES = "pentatonica blues";

    private static final int LIMIAR_OITAVACAO_CORDA_GRAVE = 2;
    private static final int OITAVA = 12;

    private static class NotaTab {
        int fret;
        int posicao;

        NotaTab(int fret, int posicao) {
            this.fret = fret;
            this.posicao = posicao;
        }
    }

    private static class ShapeMestre {
        Map<String, List<NotaTab>> notasPorCorda;
        int comprimentoLinha;

        ShapeMestre(Map<String, List<NotaTab>> notas, int comprimento) {
            this.notasPorCorda = notas;
            this.comprimentoLinha = comprimento;
        }
    }

    /**
     * Uma nota pronta para consumo na ordem melódica, já com a numeração de
     * corda do AlphaTab (1 = E aguda, 6 = E grave) e o traste final (shape +
     * transposição).
     */
    public record NotaTablatura(int corda, int traste) {
    }

    private record Transposicao(ShapeMestre shape, int shift) {
    }

    private static final ShapeMestre SHAPE_3NPC_MAIOR_TRANSPOABLE;
    private static final ShapeMestre SHAPE_3NPC_MENOR_TRANSPOABLE;
    private static final ShapeMestre SHAPE_PENTATONICA_M7_TRANSPOABLE;
    private static final ShapeMestre SHAPE_PENTATONICA_BLUES_TRANSPOABLE;
    private static final Map<String, Integer> NOTAS_NA_CORDA_E;

    static {
        NOTAS_NA_CORDA_E = new HashMap<>();
        NOTAS_NA_CORDA_E.put("E", 0);
        NOTAS_NA_CORDA_E.put("F", 1);
        NOTAS_NA_CORDA_E.put("F_SHARP", 2);
        NOTAS_NA_CORDA_E.put("G", 3);
        NOTAS_NA_CORDA_E.put("G_SHARP", 4);
        NOTAS_NA_CORDA_E.put("A", 5);
        NOTAS_NA_CORDA_E.put("A_SHARP", 6);
        NOTAS_NA_CORDA_E.put("B", 7);
        NOTAS_NA_CORDA_E.put("C", 8);
        NOTAS_NA_CORDA_E.put("C_SHARP", 9);
        NOTAS_NA_CORDA_E.put("D", 10);
        NOTAS_NA_CORDA_E.put("D_SHARP", 11);

        Map<String, List<NotaTab>> notas3NpcMaior = new HashMap<>();
        notas3NpcMaior.put("E_high", Arrays.asList(new NotaTab(10, 50), new NotaTab(12, 53), new NotaTab(13, 56)));
        notas3NpcMaior.put("B", Arrays.asList(new NotaTab(10, 40), new NotaTab(12, 43), new NotaTab(13, 46)));
        notas3NpcMaior.put("G", Arrays.asList(new NotaTab(9, 31), new NotaTab(10, 34), new NotaTab(12, 37)));
        notas3NpcMaior.put("D", Arrays.asList(new NotaTab(9, 20), new NotaTab(10, 23), new NotaTab(12, 26)));
        notas3NpcMaior.put("A", Arrays.asList(new NotaTab(8, 11), new NotaTab(10, 14), new NotaTab(12, 17)));
        notas3NpcMaior.put("E_low", Arrays.asList(new NotaTab(8, 2), new NotaTab(10, 5), new NotaTab(12, 8)));
        SHAPE_3NPC_MAIOR_TRANSPOABLE = new ShapeMestre(notas3NpcMaior, 58);

        Map<String, List<NotaTab>> notas3NpcMenor = new HashMap<>();
        notas3NpcMenor.put("E_high", Arrays.asList(new NotaTab(7, 50), new NotaTab(8, 53), new NotaTab(10, 56)));
        notas3NpcMenor.put("B", Arrays.asList(new NotaTab(6, 40), new NotaTab(8, 43), new NotaTab(10, 46)));
        notas3NpcMenor.put("G", Arrays.asList(new NotaTab(5, 31), new NotaTab(7, 34), new NotaTab(9, 37)));
        notas3NpcMenor.put("D", Arrays.asList(new NotaTab(5, 20), new NotaTab(7, 23), new NotaTab(9, 26)));
        notas3NpcMenor.put("A", Arrays.asList(new NotaTab(5, 11), new NotaTab(7, 14), new NotaTab(8, 17)));
        notas3NpcMenor.put("E_low", Arrays.asList(new NotaTab(5, 2), new NotaTab(7, 5), new NotaTab(8, 8)));
        SHAPE_3NPC_MENOR_TRANSPOABLE = new ShapeMestre(notas3NpcMenor, 58);

        // Pentatonica m7 (menor pentatonica: 1, b3, 4, 5, b7) na caixa clasica do
        // braco com estritamente 2 notas por corda. Tonica na corda E grave (casa 5).
        Map<String, List<NotaTab>> notasPentatonicaM7 = new HashMap<>();
        notasPentatonicaM7.put("E_low", Arrays.asList(new NotaTab(5, 2), new NotaTab(8, 5)));
        notasPentatonicaM7.put("A", Arrays.asList(new NotaTab(5, 8), new NotaTab(7, 11)));
        notasPentatonicaM7.put("D", Arrays.asList(new NotaTab(5, 14), new NotaTab(7, 17)));
        notasPentatonicaM7.put("G", Arrays.asList(new NotaTab(5, 20), new NotaTab(7, 23)));
        notasPentatonicaM7.put("B", Arrays.asList(new NotaTab(5, 26), new NotaTab(8, 29)));
        notasPentatonicaM7.put("E_high", Arrays.asList(new NotaTab(5, 32), new NotaTab(8, 35)));
        SHAPE_PENTATONICA_M7_TRANSPOABLE = new ShapeMestre(notasPentatonicaM7, 38);

        // Pentatonica blues (pentatonica m7 + blue note b5) na mesma regiao da
        // caixa. Somente as cordas que contem a blue note (A e G) tem 3 notas;
        // as demais (E grave, D, B e E aguda) seguem com 2. Tonica na corda E grave (casa 5).
        Map<String, List<NotaTab>> notasPentatonicaBlues = new HashMap<>();
        notasPentatonicaBlues.put("E_low", Arrays.asList(new NotaTab(5, 2), new NotaTab(8, 5)));
        notasPentatonicaBlues.put("A", Arrays.asList(new NotaTab(5, 8), new NotaTab(6, 11), new NotaTab(7, 14)));
        notasPentatonicaBlues.put("D", Arrays.asList(new NotaTab(5, 17), new NotaTab(7, 20)));
        notasPentatonicaBlues.put("G", Arrays.asList(new NotaTab(5, 23), new NotaTab(7, 26), new NotaTab(8, 29)));
        notasPentatonicaBlues.put("B", Arrays.asList(new NotaTab(5, 32), new NotaTab(8, 35)));
        notasPentatonicaBlues.put("E_high", Arrays.asList(new NotaTab(5, 38), new NotaTab(8, 41)));
        SHAPE_PENTATONICA_BLUES_TRANSPOABLE = new ShapeMestre(notasPentatonicaBlues, 47);
    }

    public static String[] gerar(String tonicaAlvo, String modoNome) {
        Transposicao transposicao = resolverTransposicao(tonicaAlvo, modoNome);
        if (transposicao == null) {
            return new String[]{"Tonalidade invalida para transposicao: " + tonicaAlvo};
        }
        return montarTablatura(transposicao.shape, transposicao.shift);
    }

    /**
     * Retorna as notas do shape "3 notas por corda" na ordem melódica
     * ascendente (da corda 6/E grave para a corda 1/E aguda), para que
     * bibliotecas como o AlphaTab possam tocar/renderizar o estudo.
     */
    public static List<NotaTablatura> gerarNotas(String tonicaAlvo, String modoNome) {
        Transposicao transposicao = resolverTransposicao(tonicaAlvo, modoNome);
        if (transposicao == null) {
            return List.of();
        }

        // O shape do braco guarda as notas por corda usando os nomes ASCII
        // (E_high..E_low). AlphaTab numera as cordas de 1 (aguda) ate 6 (grave),
        // entao o indice 0 (E_high) vira corda 1 e o indice 5 (E_low) vira corda 6.
        String[] nomesCordas = {"E_high", "B", "G", "D", "A", "E_low"};
        List<NotaTablatura> notas = new ArrayList<>();
        for (int i = nomesCordas.length - 1; i >= 0; i--) {
            List<NotaTab> notasDaCorda = transposicao.shape.notasPorCorda.get(nomesCordas[i]);
            if (notasDaCorda == null) {
                continue;
            }
            for (NotaTab nota : notasDaCorda) {
                notas.add(new NotaTablatura(i + 1, nota.fret + transposicao.shift));
            }
        }
        return notas;
    }

    private static Transposicao resolverTransposicao(String tonicaAlvo, String modoNome) {
        String tonicaNormalizada = tonicaAlvo.replace("#", "_SHARP");

        if (!NOTAS_NA_CORDA_E.containsKey(tonicaNormalizada)) {
            return null;
        }

        int casaTonicaAlvo = NOTAS_NA_CORDA_E.get(tonicaNormalizada);
        if (casaTonicaAlvo < LIMIAR_OITAVACAO_CORDA_GRAVE) {
            casaTonicaAlvo += OITAVA;
        }

        ShapeMestre shapeBase;
        int casaTonicaMestre;
        if (MODO_PENTATONICA_M7.equals(modoNome)) {
            shapeBase = SHAPE_PENTATONICA_M7_TRANSPOABLE;
            casaTonicaMestre = 5;
        } else if (MODO_PENTATONICA_BLUES.equals(modoNome)) {
            shapeBase = SHAPE_PENTATONICA_BLUES_TRANSPOABLE;
            casaTonicaMestre = 5;
        } else if (modoNome.contains("Menor")) {
            shapeBase = SHAPE_3NPC_MENOR_TRANSPOABLE;
            casaTonicaMestre = 5;
        } else {
            shapeBase = SHAPE_3NPC_MAIOR_TRANSPOABLE;
            casaTonicaMestre = 8;
        }

        return new Transposicao(shapeBase, casaTonicaAlvo - casaTonicaMestre);
    }

    private static String[] montarTablatura(ShapeMestre shape, int shift) {
        String[] nomesCordas = {"E_high", "B", "G", "D", "A", "E_low"};
        String[] tablaturaGerada = new String[6];
        int meio = 29;

        for (int i = 0; i < nomesCordas.length; i++) {
            String nomeCorda = nomesCordas[i];
            List<NotaTab> notasDaCorda = shape.notasPorCorda.get(nomeCorda);
            char[] linha = new char[shape.comprimentoLinha];
            Arrays.fill(linha, '-');

            if (notasDaCorda != null) {
                for (NotaTab nota : notasDaCorda) {
                    int novoFret = nota.fret + shift;
                    String fretStr = String.valueOf(novoFret);
                    for (int j = 0; j < fretStr.length(); j++) {
                        if (nota.posicao + j < linha.length) {
                            linha[nota.posicao + j] = fretStr.charAt(j);
                        }
                    }
                }
            }

            String nomeCordaDisplay = nomeCorda.startsWith("E") ? "E" : nomeCorda;
            String primeiraMetade = new String(linha, 0, Math.min(meio, linha.length));
            String segundaMetade = (linha.length > meio) ? new String(linha, meio, linha.length - meio) : "";
            tablaturaGerada[i] = String.format("%s |%s|%s|", nomeCordaDisplay, primeiraMetade, segundaMetade);
        }

        return tablaturaGerada;
    }
}
