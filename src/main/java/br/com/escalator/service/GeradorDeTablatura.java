package br.com.escalator.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GeradorDeTablatura {

    /** Identificador do shape/picking pentatônica m7 (2 notas por corda). */
    public static final String MODO_PENTATONICA_M7 = "pentatonica m7";
    /** Identificador da pentatônica blues (2-3 notas por corda). */
    public static final String MODO_PENTATONICA_BLUES = "pentatonica blues";
    /** Identificador do segundo shape da pentatônica m7 (região das casas 7-10). */
    public static final String MODO_PENTATONICA_M7_SHAPE2 = "pentatonica m7 shape 2";
    /** Identificador do segundo shape da pentatônica blues (com a blue note). */
    public static final String MODO_PENTATONICA_BLUES_SHAPE2 = "pentatonica blues shape 2";
    /** Identificador do terceiro shape da pentatônica m7 (região das casas 9-13). */
    public static final String MODO_PENTATONICA_M7_SHAPE3 = "pentatonica m7 shape 3";
    /** Identificador do terceiro shape da pentatônica blues (com a blue note). */
    public static final String MODO_PENTATONICA_BLUES_SHAPE3 = "pentatonica blues shape 3";
    /** Quarto shape da pentatônica blues, derivado do PDF de referência. */
    public static final String MODO_PENTATONICA_BLUES_SHAPE4 = "pentatonica blues shape 4";
    /** Quinto shape da pentatônica blues, derivado do PDF de referência. */
    public static final String MODO_PENTATONICA_BLUES_SHAPE5 = "pentatonica blues shape 5";
    /** Posição aberta de 3 oitavas da pentatônica blues, derivada do PDF. */
    public static final String MODO_PENTATONICA_BLUES_EXTRA = "pentatonica blues extra";
    /** Identificador do quarto shape da pentatônica m7 (região das casas 12-15). */
    public static final String MODO_PENTATONICA_M7_SHAPE4 = "pentatonica m7 shape 4";
    /** Identificador do quinto shape da pentatônica m7 (região das casas 14-17). */
    public static final String MODO_PENTATONICA_M7_SHAPE5 = "pentatonica m7 shape 5";
    /** Shape extra da folha: posição aberta de 3 oitavas. */
    public static final String MODO_PENTATONICA_M7_EXTRA = "pentatonica m7 extra";
    /** Pentatonica M7 (T 3 4 5 7) do PDF pentaM7.pdf, referencia em A. */
    public static final String MODO_PENTATONICA_M7_A = "pentatonica M7 A";
    public static final String MODO_PENTATONICA_M7_A_SHAPE2 = "pentatonica M7 A shape 2";
    public static final String MODO_PENTATONICA_M7_A_SHAPE3 = "pentatonica M7 A shape 3";
    public static final String MODO_PENTATONICA_M7_A_SHAPE4 = "pentatonica M7 A shape 4";
    public static final String MODO_PENTATONICA_M7_A_SHAPE5 = "pentatonica M7 A shape 5";
    public static final String MODO_PENTATONICA_M7_A_EXTRA = "pentatonica M7 A extra";
    /** Pentatonica m6 (T 3b 4 5 6) do PDF pentam6.pdf, referencia em Am. */
    public static final String MODO_PENTATONICA_M6_A = "pentatonica m6 A";
    public static final String MODO_PENTATONICA_M6_A_SHAPE2 = "pentatonica m6 A shape 2";
    public static final String MODO_PENTATONICA_M6_A_SHAPE3 = "pentatonica m6 A shape 3";
    public static final String MODO_PENTATONICA_M6_A_SHAPE4 = "pentatonica m6 A shape 4";
    public static final String MODO_PENTATONICA_M6_A_SHAPE5 = "pentatonica m6 A shape 5";
    public static final String MODO_PENTATONICA_M6_A_EXTRA = "pentatonica m6 A extra";
    /** Eolio 7+ da menor harmonica: substitui o desenho anterior pelo shape do PDF. */
    public static final String MODO_MENOR_HARMONICA = "Menor Harmônica";
    /** Demais modos da menor harmonica, na ordem dos shapes do PDF. */
    public static final String MODO_MENOR_HARMONICA_LOCRIO_6 = "Lócrio 6";
    public static final String MODO_MENOR_HARMONICA_JONIO_5SUST = "Jônio 5#";
    public static final String MODO_MENOR_HARMONICA_DORICO_4SUST = "Dórico 4#";
    public static final String MODO_MENOR_HARMONICA_FRIGIO_DOMINANTE = "Frígio Dominante";
    public static final String MODO_MENOR_HARMONICA_LIDIO_9SUST = "Lídio 9#";
    public static final String MODO_MENOR_HARMONICA_DIMINUTO = "Diminuto";
    /** Identificadores da familia da menor harmonica (espelha ModoMenorHarmonica). */
    private static final Set<String> MODOS_MENOR_HARMONICA = Set.of(
            MODO_MENOR_HARMONICA, MODO_MENOR_HARMONICA_LOCRIO_6,
            MODO_MENOR_HARMONICA_JONIO_5SUST, MODO_MENOR_HARMONICA_DORICO_4SUST,
            MODO_MENOR_HARMONICA_FRIGIO_DOMINANTE, MODO_MENOR_HARMONICA_LIDIO_9SUST,
            MODO_MENOR_HARMONICA_DIMINUTO);
    /** Primeiro shape da menor melodica: substitui o desenho generico de A menor. */
    public static final String MODO_MENOR_MELODICA = "Menor Melódica";
    /** Demais modos da menor melodica, na ordem dos shapes do PDF. */
    public static final String MODO_MENOR_MELODICA_FRIGIO_6 = "Frígio 6";
    public static final String MODO_MENOR_MELODICA_LIDIO_5SUST = "Lídio 5#";
    public static final String MODO_MENOR_MELODICA_MIXO_4SUST = "Mixo 4#";
    public static final String MODO_MENOR_MELODICA_MIXO_6B = "Mixo 6b";
    public static final String MODO_MENOR_MELODICA_LOCRIO_9 = "Lócrio 9";
    public static final String MODO_MENOR_MELODICA_ALT_7 = "Alt 7 (superlócrio)";
    /** Identificadores da familia da menor melodica (espelha ModoMenorMelodica). */
    private static final Set<String> MODOS_MENOR_MELODICA = Set.of(
            MODO_MENOR_MELODICA, MODO_MENOR_MELODICA_FRIGIO_6,
            MODO_MENOR_MELODICA_LIDIO_5SUST, MODO_MENOR_MELODICA_MIXO_4SUST,
            MODO_MENOR_MELODICA_MIXO_6B, MODO_MENOR_MELODICA_LOCRIO_9,
            MODO_MENOR_MELODICA_ALT_7);
    /** Modos da escala maior: rotacoes da mesma escala, ancoradas nos shapes validados. */
    public static final String MODO_DORICO = "Dorico";
    public static final String MODO_FRIGIO = "Frigio";
    public static final String MODO_LIDIO = "Lidio";
    public static final String MODO_MIXOLIDIO = "Mixolidio";
    public static final String MODO_LOCRIO = "Locrio";

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
    private static final ShapeMestre SHAPE_3NPC_DORICO_TRANSPOABLE;
    private static final ShapeMestre SHAPE_3NPC_FRIGIO_TRANSPOABLE;
    private static final ShapeMestre SHAPE_3NPC_LIDIO_TRANSPOABLE;
    private static final ShapeMestre SHAPE_3NPC_MIXOLIDIO_TRANSPOABLE;
    private static final ShapeMestre SHAPE_3NPC_MENOR_TRANSPOABLE;
    private static final ShapeMestre SHAPE_3NPC_LOCRIO_TRANSPOABLE;
    private static final ShapeMestre SHAPE_3NPC_MENOR_HARMONICA_EOLIO_7_TRANSPOABLE;
    private static final ShapeMestre SHAPE_3NPC_MENOR_HARMONICA_LOCRIO_6_TRANSPOABLE;
    private static final ShapeMestre SHAPE_3NPC_MENOR_HARMONICA_JONIO_5SUST_TRANSPOABLE;
    private static final ShapeMestre SHAPE_3NPC_MENOR_HARMONICA_DORICO_4SUST_TRANSPOABLE;
    private static final ShapeMestre SHAPE_3NPC_MENOR_HARMONICA_FRIGIO_DOMINANTE_TRANSPOABLE;
    private static final ShapeMestre SHAPE_3NPC_MENOR_HARMONICA_LIDIO_9SUST_TRANSPOABLE;
    private static final ShapeMestre SHAPE_3NPC_MENOR_HARMONICA_DIMINUTO_TRANSPOABLE;
    private static final ShapeMestre SHAPE_3NPC_MENOR_MELODICA_DORICO_7_TRANSPOABLE;
    private static final ShapeMestre SHAPE_3NPC_MENOR_MELODICA_FRIGIO_6_TRANSPOABLE;
    private static final ShapeMestre SHAPE_3NPC_MENOR_MELODICA_LIDIO_5SUST_TRANSPOABLE;
    private static final ShapeMestre SHAPE_3NPC_MENOR_MELODICA_MIXO_4SUST_TRANSPOABLE;
    private static final ShapeMestre SHAPE_3NPC_MENOR_MELODICA_MIXO_6B_TRANSPOABLE;
    private static final ShapeMestre SHAPE_3NPC_MENOR_MELODICA_LOCRIO_9_TRANSPOABLE;
    private static final ShapeMestre SHAPE_3NPC_MENOR_MELODICA_ALT_7_TRANSPOABLE;
    private static final ShapeMestre SHAPE_PENTATONICA_M7_TRANSPOABLE;
    private static final ShapeMestre SHAPE_PENTATONICA_BLUES_TRANSPOABLE;
    private static final ShapeMestre SHAPE_PENTATONICA_M7_SHAPE2_TRANSPOABLE;
    private static final ShapeMestre SHAPE_PENTATONICA_BLUES_SHAPE2_TRANSPOABLE;
    private static final ShapeMestre SHAPE_PENTATONICA_M7_SHAPE3_TRANSPOABLE;
    private static final ShapeMestre SHAPE_PENTATONICA_BLUES_SHAPE3_TRANSPOABLE;
    private static final ShapeMestre SHAPE_PENTATONICA_BLUES_SHAPE4_TRANSPOABLE;
    private static final ShapeMestre SHAPE_PENTATONICA_BLUES_SHAPE5_TRANSPOABLE;
    private static final ShapeMestre SHAPE_PENTATONICA_BLUES_EXTRA_TRANSPOABLE;
    private static final ShapeMestre SHAPE_PENTATONICA_M7_SHAPE4_TRANSPOABLE;
    private static final ShapeMestre SHAPE_PENTATONICA_M7_SHAPE5_TRANSPOABLE;
    private static final ShapeMestre SHAPE_PENTATONICA_M7_EXTRA_TRANSPOABLE;
    private static final ShapeMestre SHAPE_PENTATONICA_M7_A_TRANSPOABLE;
    private static final ShapeMestre SHAPE_PENTATONICA_M7_A_SHAPE2_TRANSPOABLE;
    private static final ShapeMestre SHAPE_PENTATONICA_M7_A_SHAPE3_TRANSPOABLE;
    private static final ShapeMestre SHAPE_PENTATONICA_M7_A_SHAPE4_TRANSPOABLE;
    private static final ShapeMestre SHAPE_PENTATONICA_M7_A_SHAPE5_TRANSPOABLE;
    private static final ShapeMestre SHAPE_PENTATONICA_M7_A_EXTRA_TRANSPOABLE;
    private static final ShapeMestre SHAPE_PENTATONICA_M6_A_TRANSPOABLE;
    private static final ShapeMestre SHAPE_PENTATONICA_M6_A_SHAPE2_TRANSPOABLE;
    private static final ShapeMestre SHAPE_PENTATONICA_M6_A_SHAPE3_TRANSPOABLE;
    private static final ShapeMestre SHAPE_PENTATONICA_M6_A_SHAPE4_TRANSPOABLE;
    private static final ShapeMestre SHAPE_PENTATONICA_M6_A_SHAPE5_TRANSPOABLE;
    private static final ShapeMestre SHAPE_PENTATONICA_M6_A_EXTRA_TRANSPOABLE;
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

        // Os cinco modos restantes da escala maior sao rotacoes uniformes dos
        // graus dos shapes Jônio (casa 8) e Eólio (casa 5) ja validados. As
        // casas foram obtidas pela rotacao uniforme dos graus e conferidas nota
        // a nota em ModosAuditoriaTest. A ordem dos arrays e E grave -> E aguda.
        SHAPE_3NPC_DORICO_TRANSPOABLE = shape3Npc(
                new int[]{10, 12, 13}, new int[]{10, 12, 14}, new int[]{10, 12, 14},
                new int[]{10, 12, 14}, new int[]{12, 13, 15}, new int[]{12, 13, 15});
        SHAPE_3NPC_FRIGIO_TRANSPOABLE = shape3Npc(
                new int[]{12, 13, 15}, new int[]{12, 14, 15}, new int[]{12, 14, 15},
                new int[]{12, 14, 16}, new int[]{13, 15, 17}, new int[]{13, 15, 17});
        SHAPE_3NPC_LIDIO_TRANSPOABLE = shape3Npc(
                new int[]{13, 15, 17}, new int[]{14, 15, 17}, new int[]{14, 15, 17},
                new int[]{14, 16, 17}, new int[]{15, 17, 18}, new int[]{15, 17, 19});
        SHAPE_3NPC_MIXOLIDIO_TRANSPOABLE = shape3Npc(
                new int[]{15, 17, 19}, new int[]{15, 17, 19}, new int[]{15, 17, 19},
                new int[]{16, 17, 19}, new int[]{17, 18, 20}, new int[]{17, 19, 20});
        SHAPE_3NPC_LOCRIO_TRANSPOABLE = shape3Npc(
                new int[]{7, 8, 10}, new int[]{7, 8, 10}, new int[]{7, 9, 10},
                new int[]{7, 9, 10}, new int[]{8, 10, 12}, new int[]{8, 10, 12});

        Map<String, List<NotaTab>> notas3NpcMenor = new HashMap<>();
        notas3NpcMenor.put("E_high", Arrays.asList(new NotaTab(7, 50), new NotaTab(8, 53), new NotaTab(10, 56)));
        notas3NpcMenor.put("B", Arrays.asList(new NotaTab(6, 40), new NotaTab(8, 43), new NotaTab(10, 46)));
        notas3NpcMenor.put("G", Arrays.asList(new NotaTab(5, 31), new NotaTab(7, 34), new NotaTab(9, 37)));
        notas3NpcMenor.put("D", Arrays.asList(new NotaTab(5, 20), new NotaTab(7, 23), new NotaTab(9, 26)));
        notas3NpcMenor.put("A", Arrays.asList(new NotaTab(5, 11), new NotaTab(7, 14), new NotaTab(8, 17)));
        notas3NpcMenor.put("E_low", Arrays.asList(new NotaTab(5, 2), new NotaTab(7, 5), new NotaTab(8, 8)));
        SHAPE_3NPC_MENOR_TRANSPOABLE = new ShapeMestre(notas3NpcMenor, 58);

        // Menor harmonica: o desenho anterior e substituido pelo shape Eolio 7+
        // do PDF SHAPES_menorharm.pdf. As casas coincidem com a ancora validada
        // (tonica na corda E grave, casa 5, com G# no lugar da 7ma natural).
        Map<String, List<NotaTab>> notas3NpcMenorHarmonica = new HashMap<>();
        notas3NpcMenorHarmonica.put("E_high", Arrays.asList(new NotaTab(7, 50), new NotaTab(8, 53), new NotaTab(10, 56)));
        notas3NpcMenorHarmonica.put("B", Arrays.asList(new NotaTab(6, 40), new NotaTab(9, 43), new NotaTab(10, 46)));
        notas3NpcMenorHarmonica.put("G", Arrays.asList(new NotaTab(5, 31), new NotaTab(7, 34), new NotaTab(9, 37)));
        notas3NpcMenorHarmonica.put("D", Arrays.asList(new NotaTab(6, 20), new NotaTab(7, 23), new NotaTab(9, 26)));
        notas3NpcMenorHarmonica.put("A", Arrays.asList(new NotaTab(5, 11), new NotaTab(7, 14), new NotaTab(8, 17)));
        notas3NpcMenorHarmonica.put("E_low", Arrays.asList(new NotaTab(5, 2), new NotaTab(7, 5), new NotaTab(8, 8)));
        SHAPE_3NPC_MENOR_HARMONICA_EOLIO_7_TRANSPOABLE = new ShapeMestre(notas3NpcMenorHarmonica, 58);

        // Os outros 6 shapes do PDF sao as posicoes 3NPC seguintes da MESMA
        // menor harmonica de Am (A B C D E F G#). A ordem dos arrays e
        // E grave -> E aguda, igual aos shapes 3NPC ja validados.
        SHAPE_3NPC_MENOR_HARMONICA_LOCRIO_6_TRANSPOABLE = shape3Npc(
                new int[]{7, 8, 10}, new int[]{7, 8, 11}, new int[]{7, 9, 10},
                new int[]{7, 9, 10}, new int[]{9, 10, 12}, new int[]{8, 10, 12});
        SHAPE_3NPC_MENOR_HARMONICA_JONIO_5SUST_TRANSPOABLE = shape3Npc(
                new int[]{8, 10, 12}, new int[]{8, 11, 12}, new int[]{9, 10, 12},
                new int[]{9, 10, 13}, new int[]{10, 12, 13}, new int[]{10, 12, 13});
        SHAPE_3NPC_MENOR_HARMONICA_DORICO_4SUST_TRANSPOABLE = shape3Npc(
                new int[]{10, 12, 13}, new int[]{11, 12, 14}, new int[]{10, 12, 14},
                new int[]{10, 13, 14}, new int[]{12, 13, 15}, new int[]{12, 13, 16});
        SHAPE_3NPC_MENOR_HARMONICA_FRIGIO_DOMINANTE_TRANSPOABLE = shape3Npc(
                new int[]{12, 13, 16}, new int[]{12, 14, 15}, new int[]{12, 14, 15},
                new int[]{13, 14, 16}, new int[]{13, 15, 17}, new int[]{13, 16, 17});
        SHAPE_3NPC_MENOR_HARMONICA_LIDIO_9SUST_TRANSPOABLE = shape3Npc(
                new int[]{13, 16, 17}, new int[]{14, 15, 17}, new int[]{14, 15, 18},
                new int[]{14, 16, 17}, new int[]{15, 17, 18}, new int[]{16, 17, 19});
        SHAPE_3NPC_MENOR_HARMONICA_DIMINUTO_TRANSPOABLE = shape3Npc(
                new int[]{16, 17, 19}, new int[]{15, 17, 19}, new int[]{15, 18, 19},
                new int[]{16, 17, 19}, new int[]{17, 18, 21}, new int[]{17, 19, 20});

        // Menor melodica (jazz): o PDF menormelodica_shapes.pdf desenha as 7
        // posicoes 3NPC de A B C D E F# G#. A ordem dos arrays continua sendo
        // E grave -> E aguda, igual aos demais shapes 3NPC ja validados.
        Map<String, List<NotaTab>> notas3NpcMenorMelodica = new HashMap<>();
        notas3NpcMenorMelodica.put("E_high", Arrays.asList(new NotaTab(7, 50), new NotaTab(8, 53), new NotaTab(10, 56)));
        notas3NpcMenorMelodica.put("B", Arrays.asList(new NotaTab(7, 40), new NotaTab(9, 43), new NotaTab(10, 46)));
        notas3NpcMenorMelodica.put("G", Arrays.asList(new NotaTab(5, 31), new NotaTab(7, 34), new NotaTab(9, 37)));
        notas3NpcMenorMelodica.put("D", Arrays.asList(new NotaTab(6, 20), new NotaTab(7, 23), new NotaTab(9, 26)));
        notas3NpcMenorMelodica.put("A", Arrays.asList(new NotaTab(5, 11), new NotaTab(7, 14), new NotaTab(9, 17)));
        notas3NpcMenorMelodica.put("E_low", Arrays.asList(new NotaTab(5, 2), new NotaTab(7, 5), new NotaTab(8, 8)));
        SHAPE_3NPC_MENOR_MELODICA_DORICO_7_TRANSPOABLE = new ShapeMestre(notas3NpcMenorMelodica, 58);

        SHAPE_3NPC_MENOR_MELODICA_FRIGIO_6_TRANSPOABLE = shape3Npc(
                new int[]{7, 8, 10}, new int[]{7, 9, 11}, new int[]{7, 9, 10},
                new int[]{7, 9, 11}, new int[]{9, 10, 12}, new int[]{8, 10, 12});
        SHAPE_3NPC_MENOR_MELODICA_LIDIO_5SUST_TRANSPOABLE = shape3Npc(
                new int[]{8, 10, 12}, new int[]{9, 11, 12}, new int[]{9, 10, 12},
                new int[]{9, 11, 13}, new int[]{10, 12, 13}, new int[]{10, 12, 14});
        SHAPE_3NPC_MENOR_MELODICA_MIXO_4SUST_TRANSPOABLE = shape3Npc(
                new int[]{10, 12, 14}, new int[]{11, 12, 14}, new int[]{10, 12, 14},
                new int[]{11, 13, 14}, new int[]{12, 13, 15}, new int[]{12, 14, 16});
        SHAPE_3NPC_MENOR_MELODICA_MIXO_6B_TRANSPOABLE = shape3Npc(
                new int[]{12, 14, 16}, new int[]{12, 14, 15}, new int[]{12, 14, 16},
                new int[]{13, 14, 16}, new int[]{13, 15, 17}, new int[]{14, 16, 17});
        SHAPE_3NPC_MENOR_MELODICA_LOCRIO_9_TRANSPOABLE = shape3Npc(
                new int[]{14, 16, 17}, new int[]{14, 15, 17}, new int[]{14, 16, 18},
                new int[]{14, 16, 17}, new int[]{15, 17, 19}, new int[]{16, 17, 19});
        SHAPE_3NPC_MENOR_MELODICA_ALT_7_TRANSPOABLE = shape3Npc(
                new int[]{16, 17, 19}, new int[]{15, 17, 19}, new int[]{16, 18, 19},
                new int[]{16, 17, 19}, new int[]{17, 19, 21}, new int[]{17, 19, 20});

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

        // Shape 2 da pentatonica m7: segunda posicao da caixa (regiao das casas
        // 7-10 em A menor). Na referencia de A menor (tons C D E G A) fica nas
        // cordas 6 a 1 com as casas 8/10, 7/10, 7/10, 7/9, 8/10 e 8/10.
        // A tonica de referencia continua sendo A (corda E grave, casa 5), o que
        // permite reaproveitar a mesma transposicao do shape 1.
        Map<String, List<NotaTab>> notasPentatonicaM7Shape2 = new HashMap<>();
        notasPentatonicaM7Shape2.put("E_low", Arrays.asList(new NotaTab(8, 2), new NotaTab(10, 5)));
        notasPentatonicaM7Shape2.put("A", Arrays.asList(new NotaTab(7, 8), new NotaTab(10, 11)));
        notasPentatonicaM7Shape2.put("D", Arrays.asList(new NotaTab(7, 14), new NotaTab(10, 17)));
        notasPentatonicaM7Shape2.put("G", Arrays.asList(new NotaTab(7, 20), new NotaTab(9, 23)));
        notasPentatonicaM7Shape2.put("B", Arrays.asList(new NotaTab(8, 26), new NotaTab(10, 29)));
        notasPentatonicaM7Shape2.put("E_high", Arrays.asList(new NotaTab(8, 32), new NotaTab(10, 35)));
        SHAPE_PENTATONICA_M7_SHAPE2_TRANSPOABLE = new ShapeMestre(notasPentatonicaM7Shape2, 38);

        // Shape 2 da pentatonica blues: o mesmo desenho do shape 2 m7 acrescido
        // da blue note (b5). No shape 2 ela aparece em tres cordas: E grave
        // (casa 11), G (casa 8) e E aguda (casa 11). A blue note da corda A nao
        // entra porque a mesma nota ja esta na corda E grave (casa 11). Com isso
        // cada volta tem 15 notas e fecha o 4/4 com pausa de semicolcheia.
        Map<String, List<NotaTab>> notasPentatonicaBluesShape2 = new HashMap<>();
        notasPentatonicaBluesShape2.put("E_low", Arrays.asList(new NotaTab(8, 2), new NotaTab(10, 5), new NotaTab(11, 8)));
        notasPentatonicaBluesShape2.put("A", Arrays.asList(new NotaTab(7, 11), new NotaTab(10, 14)));
        notasPentatonicaBluesShape2.put("D", Arrays.asList(new NotaTab(7, 17), new NotaTab(10, 20)));
        notasPentatonicaBluesShape2.put("G", Arrays.asList(new NotaTab(7, 23), new NotaTab(8, 26), new NotaTab(9, 29)));
        notasPentatonicaBluesShape2.put("B", Arrays.asList(new NotaTab(8, 32), new NotaTab(10, 35)));
        notasPentatonicaBluesShape2.put("E_high", Arrays.asList(new NotaTab(8, 38), new NotaTab(10, 41), new NotaTab(11, 44)));
        SHAPE_PENTATONICA_BLUES_SHAPE2_TRANSPOABLE = new ShapeMestre(notasPentatonicaBluesShape2, 49);

        // Shape 3 da pentatonica m7: terceira posicao da caixa (regiao das casas
        // 9-13 em A menor). Na referencia de A menor (tons C D E G A) fica nas
        // cordas 6 a 1 com as casas 10/12, 10/12, 10/12, 9/12, 10/13 e 10/12.
        Map<String, List<NotaTab>> notasPentatonicaM7Shape3 = new HashMap<>();
        notasPentatonicaM7Shape3.put("E_low", Arrays.asList(new NotaTab(10, 2), new NotaTab(12, 5)));
        notasPentatonicaM7Shape3.put("A", Arrays.asList(new NotaTab(10, 8), new NotaTab(12, 11)));
        notasPentatonicaM7Shape3.put("D", Arrays.asList(new NotaTab(10, 14), new NotaTab(12, 17)));
        notasPentatonicaM7Shape3.put("G", Arrays.asList(new NotaTab(9, 20), new NotaTab(12, 23)));
        notasPentatonicaM7Shape3.put("B", Arrays.asList(new NotaTab(10, 26), new NotaTab(13, 29)));
        notasPentatonicaM7Shape3.put("E_high", Arrays.asList(new NotaTab(10, 32), new NotaTab(12, 35)));
        SHAPE_PENTATONICA_M7_SHAPE3_TRANSPOABLE = new ShapeMestre(notasPentatonicaM7Shape3, 38);

        // Shape 3 da pentatonica blues: o mesmo desenho do shape 3 m7 acrescido
        // da blue note (b5) nas cordas E grave (casa 11), D (casa 13) e E aguda
        // (casa 11). Com isso cada volta tem 15 notas e fecha o 4/4 com pausa de
        // semicolcheia, igual ao shape 2 blues.
        Map<String, List<NotaTab>> notasPentatonicaBluesShape3 = new HashMap<>();
        notasPentatonicaBluesShape3.put("E_low", Arrays.asList(new NotaTab(10, 2), new NotaTab(11, 5), new NotaTab(12, 8)));
        notasPentatonicaBluesShape3.put("A", Arrays.asList(new NotaTab(10, 11), new NotaTab(12, 14)));
        notasPentatonicaBluesShape3.put("D", Arrays.asList(new NotaTab(10, 17), new NotaTab(12, 20), new NotaTab(13, 23)));
        notasPentatonicaBluesShape3.put("G", Arrays.asList(new NotaTab(9, 26), new NotaTab(12, 29)));
        notasPentatonicaBluesShape3.put("B", Arrays.asList(new NotaTab(10, 32), new NotaTab(13, 35)));
        notasPentatonicaBluesShape3.put("E_high", Arrays.asList(new NotaTab(10, 38), new NotaTab(11, 41), new NotaTab(12, 44)));
        SHAPE_PENTATONICA_BLUES_SHAPE3_TRANSPOABLE = new ShapeMestre(notasPentatonicaBluesShape3, 49);

        // Os shapes 4, 5 e extra vem da folha de pentatonica m7. Os numeros
        // impressos no PDF sao dedos; as casas abaixo foram lidas pela posicao
        // horizontal de cada numero, mantendo a mesma referencia de A menor
        // (tonica na casa 5 da corda E grave) dos shapes 1, 2 e 3.
        Map<String, List<NotaTab>> notasPentatonicaM7Shape4 = new HashMap<>();
        notasPentatonicaM7Shape4.put("E_low", Arrays.asList(new NotaTab(12, 2), new NotaTab(15, 5)));
        notasPentatonicaM7Shape4.put("A", Arrays.asList(new NotaTab(12, 8), new NotaTab(15, 11)));
        notasPentatonicaM7Shape4.put("D", Arrays.asList(new NotaTab(12, 14), new NotaTab(14, 17)));
        notasPentatonicaM7Shape4.put("G", Arrays.asList(new NotaTab(12, 20), new NotaTab(14, 23)));
        notasPentatonicaM7Shape4.put("B", Arrays.asList(new NotaTab(13, 26), new NotaTab(15, 29)));
        notasPentatonicaM7Shape4.put("E_high", Arrays.asList(new NotaTab(12, 32), new NotaTab(15, 35)));
        SHAPE_PENTATONICA_M7_SHAPE4_TRANSPOABLE = new ShapeMestre(notasPentatonicaM7Shape4, 38);

        // Shape 5: mantem a ultima nota A da casa 17 na corda 1, conforme a
        // folha. A subida termina nela, como nos demais shapes, e o retorno
        // completo fecha o loop.
        Map<String, List<NotaTab>> notasPentatonicaM7Shape5 = new HashMap<>();
        notasPentatonicaM7Shape5.put("E_low", Arrays.asList(new NotaTab(15, 2), new NotaTab(17, 5)));
        notasPentatonicaM7Shape5.put("A", Arrays.asList(new NotaTab(15, 8), new NotaTab(17, 11)));
        notasPentatonicaM7Shape5.put("D", Arrays.asList(new NotaTab(14, 14), new NotaTab(17, 17)));
        notasPentatonicaM7Shape5.put("G", Arrays.asList(new NotaTab(14, 20), new NotaTab(17, 23)));
        notasPentatonicaM7Shape5.put("B", Arrays.asList(new NotaTab(15, 26), new NotaTab(17, 29)));
        notasPentatonicaM7Shape5.put("E_high", Arrays.asList(new NotaTab(15, 32), new NotaTab(17, 35)));
        SHAPE_PENTATONICA_M7_SHAPE5_TRANSPOABLE = new ShapeMestre(notasPentatonicaM7Shape5, 38);

        // Shape extra (penta menor aberta): 3 notas por corda, da casa 5 da E
        // grave a casa 15 da E aguda. Sao 18 notas na subida, exatamente 3
        // compassos de 2 tercinas, e o retorno repete o topo para fechar o loop.
        Map<String, List<NotaTab>> notasPentatonicaM7Extra = new HashMap<>();
        notasPentatonicaM7Extra.put("E_low", Arrays.asList(new NotaTab(5, 2), new NotaTab(8, 5), new NotaTab(10, 8)));
        notasPentatonicaM7Extra.put("A", Arrays.asList(new NotaTab(5, 11), new NotaTab(7, 14), new NotaTab(10, 17)));
        notasPentatonicaM7Extra.put("D", Arrays.asList(new NotaTab(7, 20), new NotaTab(10, 23), new NotaTab(12, 26)));
        notasPentatonicaM7Extra.put("G", Arrays.asList(new NotaTab(7, 31), new NotaTab(9, 34), new NotaTab(12, 37)));
        notasPentatonicaM7Extra.put("B", Arrays.asList(new NotaTab(10, 40), new NotaTab(13, 43), new NotaTab(15, 46)));
        notasPentatonicaM7Extra.put("E_high", Arrays.asList(new NotaTab(10, 50), new NotaTab(12, 53), new NotaTab(15, 56)));
        SHAPE_PENTATONICA_M7_EXTRA_TRANSPOABLE = new ShapeMestre(notasPentatonicaM7Extra, 58);

        // Shapes blues 4 e 5: mesmas bases dos shapes m7 4 e 5, com a blue note
        // nas cordas indicadas pelo PDF. As casas foram derivadas em
        // ShapeDerivador e auditadas em ShapeDerivadorTest (referencia A menor).
        Map<String, List<NotaTab>> notasPentatonicaBluesShape4 = new HashMap<>();
        notasPentatonicaBluesShape4.put("E_low", Arrays.asList(new NotaTab(12, 2), new NotaTab(15, 5)));
        notasPentatonicaBluesShape4.put("A", Arrays.asList(new NotaTab(12, 8), new NotaTab(15, 11)));
        notasPentatonicaBluesShape4.put("D", Arrays.asList(new NotaTab(12, 14), new NotaTab(13, 17), new NotaTab(14, 20)));
        notasPentatonicaBluesShape4.put("G", Arrays.asList(new NotaTab(12, 23), new NotaTab(14, 26)));
        notasPentatonicaBluesShape4.put("B", Arrays.asList(new NotaTab(13, 29), new NotaTab(15, 32), new NotaTab(16, 35)));
        notasPentatonicaBluesShape4.put("E_high", Arrays.asList(new NotaTab(12, 38), new NotaTab(15, 41)));
        SHAPE_PENTATONICA_BLUES_SHAPE4_TRANSPOABLE = new ShapeMestre(notasPentatonicaBluesShape4, 47);

        Map<String, List<NotaTab>> notasPentatonicaBluesShape5 = new HashMap<>();
        notasPentatonicaBluesShape5.put("E_low", Arrays.asList(new NotaTab(15, 2), new NotaTab(17, 5)));
        notasPentatonicaBluesShape5.put("A", Arrays.asList(new NotaTab(15, 8), new NotaTab(17, 11), new NotaTab(18, 14)));
        notasPentatonicaBluesShape5.put("D", Arrays.asList(new NotaTab(14, 17), new NotaTab(17, 20)));
        notasPentatonicaBluesShape5.put("G", Arrays.asList(new NotaTab(14, 23), new NotaTab(17, 26)));
        notasPentatonicaBluesShape5.put("B", Arrays.asList(new NotaTab(15, 29), new NotaTab(16, 32), new NotaTab(17, 35)));
        notasPentatonicaBluesShape5.put("E_high", Arrays.asList(new NotaTab(15, 38), new NotaTab(17, 41)));
        SHAPE_PENTATONICA_BLUES_SHAPE5_TRANSPOABLE = new ShapeMestre(notasPentatonicaBluesShape5, 47);

        // Posicao aberta de 3 oitavas: shape extra m7 validado + blue note na
        // corda E aguda (casa 11). O autor confirmou que o primeiro numero
        // impresso nessa corda (casa 10) nao e nota: a corda 1 vai de 11 a 15 e
        // o retorno começa na casa 15, como no shape extra m7.
        Map<String, List<NotaTab>> notasPentatonicaBluesExtra = new HashMap<>();
        notasPentatonicaBluesExtra.put("E_low", Arrays.asList(new NotaTab(5, 2), new NotaTab(8, 5), new NotaTab(10, 8)));
        notasPentatonicaBluesExtra.put("A", Arrays.asList(new NotaTab(5, 11), new NotaTab(7, 14), new NotaTab(10, 17)));
        notasPentatonicaBluesExtra.put("D", Arrays.asList(new NotaTab(7, 20), new NotaTab(10, 23), new NotaTab(12, 26)));
        notasPentatonicaBluesExtra.put("G", Arrays.asList(new NotaTab(7, 29), new NotaTab(9, 32), new NotaTab(12, 35)));
        notasPentatonicaBluesExtra.put("B", Arrays.asList(new NotaTab(10, 38), new NotaTab(13, 41), new NotaTab(15, 44)));
        notasPentatonicaBluesExtra.put("E_high", Arrays.asList(new NotaTab(11, 50),
                new NotaTab(12, 53), new NotaTab(15, 56)));
        SHAPE_PENTATONICA_BLUES_EXTRA_TRANSPOABLE = new ShapeMestre(notasPentatonicaBluesExtra, 60);

        // Pentatonica M7 e pentatonica m6 dos PDFs pentaM7.pdf e pentam6.pdf.
        // As casas foram lidas pela posicao horizontal dos dedos impressos e
        // conferidas nota a nota contra os intervalos de cada folha. A ancora
        // continua sendo A na casa 5 da corda E grave, igual as pentatonicas
        // m7/blues ja validadas.
        SHAPE_PENTATONICA_M7_A_TRANSPOABLE = shape2Pentatonica(
                new int[]{5, 9}, new int[]{5, 7}, new int[]{5, 7},
                new int[]{6, 7}, new int[]{5, 8}, new int[]{5, 9});
        SHAPE_PENTATONICA_M7_A_SHAPE2_TRANSPOABLE = shape2Pentatonica(
                new int[]{9, 10}, new int[]{7, 10}, new int[]{7, 11},
                new int[]{7, 9}, new int[]{8, 10}, new int[]{9, 10});
        SHAPE_PENTATONICA_M7_A_SHAPE3_TRANSPOABLE = shape2Pentatonica(
                new int[]{10, 12}, new int[]{10, 12}, new int[]{11, 12},
                new int[]{9, 12}, new int[]{10, 14}, new int[]{10, 12});
        SHAPE_PENTATONICA_M7_A_SHAPE4_TRANSPOABLE = shape2Pentatonica(
                new int[]{12, 15}, new int[]{12, 16}, new int[]{12, 14},
                new int[]{12, 14}, new int[]{14, 15}, new int[]{12, 15});
        SHAPE_PENTATONICA_M7_A_SHAPE5_TRANSPOABLE = shape2Pentatonica(
                new int[]{15, 17}, new int[]{16, 17}, new int[]{14, 17},
                new int[]{14, 18}, new int[]{15, 17}, new int[]{15, 17});

        // Extra M7 ("3 oitavas"): 12 notas na subida, duas por corda, com o Lá
        // da casa 17 da E aguda removido para fechar 3/4. A volta espelha o
        // desenho a partir do Mi da casa 12, sem pausas.
        Map<String, List<NotaTab>> notasPentatonicaM7AExtra = new HashMap<>();
        notasPentatonicaM7AExtra.put("E_low", Arrays.asList(new NotaTab(5, 2), new NotaTab(9, 5)));
        notasPentatonicaM7AExtra.put("A", Arrays.asList(new NotaTab(5, 11), new NotaTab(7, 14)));
        notasPentatonicaM7AExtra.put("D", Arrays.asList(new NotaTab(7, 20), new NotaTab(11, 23)));
        notasPentatonicaM7AExtra.put("G", Arrays.asList(new NotaTab(7, 31), new NotaTab(9, 34)));
        notasPentatonicaM7AExtra.put("B", Arrays.asList(new NotaTab(10, 40), new NotaTab(14, 43)));
        notasPentatonicaM7AExtra.put("E_high", Arrays.asList(new NotaTab(10, 50),
                new NotaTab(12, 53)));
        SHAPE_PENTATONICA_M7_A_EXTRA_TRANSPOABLE = new ShapeMestre(notasPentatonicaM7AExtra, 58);

        SHAPE_PENTATONICA_M6_A_TRANSPOABLE = shape2Pentatonica(
                new int[]{5, 8}, new int[]{5, 7}, new int[]{4, 7},
                new int[]{5, 7}, new int[]{5, 7}, new int[]{5, 8});
        SHAPE_PENTATONICA_M6_A_SHAPE2_TRANSPOABLE = shape2Pentatonica(
                new int[]{8, 10}, new int[]{7, 9}, new int[]{7, 10},
                new int[]{7, 9}, new int[]{7, 10}, new int[]{8, 10});
        SHAPE_PENTATONICA_M6_A_SHAPE3_TRANSPOABLE = shape2Pentatonica(
                new int[]{10, 12}, new int[]{9, 12}, new int[]{10, 12},
                new int[]{9, 11}, new int[]{10, 13}, new int[]{10, 12});
        SHAPE_PENTATONICA_M6_A_SHAPE4_TRANSPOABLE = shape2Pentatonica(
                new int[]{12, 14}, new int[]{12, 15}, new int[]{12, 14},
                new int[]{11, 14}, new int[]{13, 15}, new int[]{12, 14});
        SHAPE_PENTATONICA_M6_A_SHAPE5_TRANSPOABLE = shape2Pentatonica(
                new int[]{14, 17}, new int[]{15, 17}, new int[]{14, 16},
                new int[]{14, 17}, new int[]{15, 17}, new int[]{14, 17});

        // Extra m6 ("Penta m7blues + Penta m6"): desenho de 16 notas na subida
        // que mistura D# e G do blues com F# da pentatonica m6.
        Map<String, List<NotaTab>> notasPentatonicaM6AExtra = new HashMap<>();
        notasPentatonicaM6AExtra.put("E_low", Arrays.asList(new NotaTab(5, 2), new NotaTab(8, 5)));
        notasPentatonicaM6AExtra.put("A", Arrays.asList(new NotaTab(5, 11),
                new NotaTab(6, 14), new NotaTab(7, 17)));
        notasPentatonicaM6AExtra.put("D", Arrays.asList(new NotaTab(4, 20),
                new NotaTab(5, 23), new NotaTab(7, 26)));
        notasPentatonicaM6AExtra.put("G", Arrays.asList(new NotaTab(5, 31),
                new NotaTab(7, 34), new NotaTab(8, 37)));
        notasPentatonicaM6AExtra.put("B", Arrays.asList(new NotaTab(5, 40),
                new NotaTab(7, 43), new NotaTab(8, 46)));
        notasPentatonicaM6AExtra.put("E_high", Arrays.asList(new NotaTab(5, 50), new NotaTab(8, 53)));
        SHAPE_PENTATONICA_M6_A_EXTRA_TRANSPOABLE = new ShapeMestre(notasPentatonicaM6AExtra, 60);
    }

    /** Monta um shape de 2 notas por corda com o layout das pentatonicas m7/blues. */
    private static ShapeMestre shape2Pentatonica(int[] eLow, int[] a, int[] d,
                                                 int[] g, int[] b, int[] eHigh) {
        String[] cordas = {"E_low", "A", "D", "G", "B", "E_high"};
        int[] posicoes = {2, 8, 14, 20, 26, 32};
        int[][] frets = {eLow, a, d, g, b, eHigh};
        Map<String, List<NotaTab>> notas = new HashMap<>();
        for (int corda = 0; corda < cordas.length; corda++) {
            List<NotaTab> notasDaCorda = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                notasDaCorda.add(new NotaTab(frets[corda][i], posicoes[corda] + i * 3));
            }
            notas.put(cordas[corda], notasDaCorda);
        }
        return new ShapeMestre(notas, 38);
    }

    /**
     * Monta um shape de 3 notas por corda usando exatamente o mesmo layout de
     * colunas dos shapes 3npc validados (2/5/8, 11/14/17, 20/23/26, 31/34/37,
     * 40/43/46 e 50/53/56). Apenas as casas mudam entre os modos.
     */
    private static ShapeMestre shape3Npc(int[] eLow, int[] a, int[] d,
                                         int[] g, int[] b, int[] eHigh) {
        String[] cordas = {"E_low", "A", "D", "G", "B", "E_high"};
        int[] posicoes = {2, 11, 20, 31, 40, 50};
        int[][] frets = {eLow, a, d, g, b, eHigh};
        Map<String, List<NotaTab>> notas = new HashMap<>();
        for (int corda = 0; corda < cordas.length; corda++) {
            List<NotaTab> notasDaCorda = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                notasDaCorda.add(new NotaTab(frets[corda][i], posicoes[corda] + i * 3));
            }
            notas.put(cordas[corda], notasDaCorda);
        }
        return new ShapeMestre(notas, 58);
    }

    public static String[] gerar(String tonicaAlvo, String modoNome) {
        Transposicao transposicao = resolverTransposicao(tonicaAlvo, modoNome);
        if (transposicao == null) {
            return new String[]{"Tonalidade invalida para transposicao: " + tonicaAlvo};
        }
        return montarTablatura(transposicao.shape, transposicao.shift);
    }

    /**
     * Retorna as notas do shape na ordem melódica: primeiro a subida da escala
     * (da corda 6/E grave para a corda 1/E aguda) e, em seguida, o movimento de
     * volta pelo caminho inverso (da corda 1/E aguda de volta para a corda 6/E
     * grave). O retorno começa repetindo a última nota da subida (sua 1ª nota)
     * e termina na primeira nota da escala, que é repetida quando o player faz
     * loop, de modo que a volta emende direto na próxima subida.
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

        // Movimento de volta: percorre a subida ao contrario. Como a 1a nota do
        // retorno e a ultima nota da subida, a nota mais aguda fica repetida; e
        // como a ultima nota do retorno e a 1a nota da escala, o loop tambem
        // repete a nota mais grave ao recomecar.
        List<NotaTablatura> retorno = new ArrayList<>(notas.size());
        for (int i = notas.size() - 1; i >= 0; i--) {
            retorno.add(notas.get(i));
        }
        notas.addAll(retorno);
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
        } else if (MODO_PENTATONICA_M7_SHAPE2.equals(modoNome)) {
            shapeBase = SHAPE_PENTATONICA_M7_SHAPE2_TRANSPOABLE;
            casaTonicaMestre = 5;
        } else if (MODO_PENTATONICA_M7_SHAPE3.equals(modoNome)) {
            shapeBase = SHAPE_PENTATONICA_M7_SHAPE3_TRANSPOABLE;
            casaTonicaMestre = 5;
        } else if (MODO_PENTATONICA_M7_SHAPE4.equals(modoNome)) {
            shapeBase = SHAPE_PENTATONICA_M7_SHAPE4_TRANSPOABLE;
            casaTonicaMestre = 5;
        } else if (MODO_PENTATONICA_M7_SHAPE5.equals(modoNome)) {
            shapeBase = SHAPE_PENTATONICA_M7_SHAPE5_TRANSPOABLE;
            casaTonicaMestre = 5;
        } else if (MODO_PENTATONICA_M7_EXTRA.equals(modoNome)) {
            shapeBase = SHAPE_PENTATONICA_M7_EXTRA_TRANSPOABLE;
            casaTonicaMestre = 5;
        } else if (MODO_PENTATONICA_BLUES.equals(modoNome)) {
            shapeBase = SHAPE_PENTATONICA_BLUES_TRANSPOABLE;
            casaTonicaMestre = 5;
        } else if (MODO_PENTATONICA_BLUES_SHAPE2.equals(modoNome)) {
            shapeBase = SHAPE_PENTATONICA_BLUES_SHAPE2_TRANSPOABLE;
            casaTonicaMestre = 5;
        } else if (MODO_PENTATONICA_BLUES_SHAPE3.equals(modoNome)) {
            shapeBase = SHAPE_PENTATONICA_BLUES_SHAPE3_TRANSPOABLE;
            casaTonicaMestre = 5;
        } else if (MODO_PENTATONICA_BLUES_SHAPE4.equals(modoNome)) {
            shapeBase = SHAPE_PENTATONICA_BLUES_SHAPE4_TRANSPOABLE;
            casaTonicaMestre = 5;
        } else if (MODO_PENTATONICA_BLUES_SHAPE5.equals(modoNome)) {
            shapeBase = SHAPE_PENTATONICA_BLUES_SHAPE5_TRANSPOABLE;
            casaTonicaMestre = 5;
        } else if (MODO_PENTATONICA_BLUES_EXTRA.equals(modoNome)) {
            shapeBase = SHAPE_PENTATONICA_BLUES_EXTRA_TRANSPOABLE;
            casaTonicaMestre = 5;
        } else if (MODO_PENTATONICA_M7_A.equals(modoNome)) {
            shapeBase = SHAPE_PENTATONICA_M7_A_TRANSPOABLE;
            casaTonicaMestre = 5;
        } else if (MODO_PENTATONICA_M7_A_SHAPE2.equals(modoNome)) {
            shapeBase = SHAPE_PENTATONICA_M7_A_SHAPE2_TRANSPOABLE;
            casaTonicaMestre = 5;
        } else if (MODO_PENTATONICA_M7_A_SHAPE3.equals(modoNome)) {
            shapeBase = SHAPE_PENTATONICA_M7_A_SHAPE3_TRANSPOABLE;
            casaTonicaMestre = 5;
        } else if (MODO_PENTATONICA_M7_A_SHAPE4.equals(modoNome)) {
            shapeBase = SHAPE_PENTATONICA_M7_A_SHAPE4_TRANSPOABLE;
            casaTonicaMestre = 5;
        } else if (MODO_PENTATONICA_M7_A_SHAPE5.equals(modoNome)) {
            shapeBase = SHAPE_PENTATONICA_M7_A_SHAPE5_TRANSPOABLE;
            casaTonicaMestre = 5;
        } else if (MODO_PENTATONICA_M7_A_EXTRA.equals(modoNome)) {
            shapeBase = SHAPE_PENTATONICA_M7_A_EXTRA_TRANSPOABLE;
            casaTonicaMestre = 5;
        } else if (MODO_PENTATONICA_M6_A.equals(modoNome)) {
            shapeBase = SHAPE_PENTATONICA_M6_A_TRANSPOABLE;
            casaTonicaMestre = 5;
        } else if (MODO_PENTATONICA_M6_A_SHAPE2.equals(modoNome)) {
            shapeBase = SHAPE_PENTATONICA_M6_A_SHAPE2_TRANSPOABLE;
            casaTonicaMestre = 5;
        } else if (MODO_PENTATONICA_M6_A_SHAPE3.equals(modoNome)) {
            shapeBase = SHAPE_PENTATONICA_M6_A_SHAPE3_TRANSPOABLE;
            casaTonicaMestre = 5;
        } else if (MODO_PENTATONICA_M6_A_SHAPE4.equals(modoNome)) {
            shapeBase = SHAPE_PENTATONICA_M6_A_SHAPE4_TRANSPOABLE;
            casaTonicaMestre = 5;
        } else if (MODO_PENTATONICA_M6_A_SHAPE5.equals(modoNome)) {
            shapeBase = SHAPE_PENTATONICA_M6_A_SHAPE5_TRANSPOABLE;
            casaTonicaMestre = 5;
        } else if (MODO_PENTATONICA_M6_A_EXTRA.equals(modoNome)) {
            shapeBase = SHAPE_PENTATONICA_M6_A_EXTRA_TRANSPOABLE;
            casaTonicaMestre = 5;
        } else if (MODO_DORICO.equals(modoNome)) {
            shapeBase = SHAPE_3NPC_DORICO_TRANSPOABLE;
            casaTonicaMestre = 10;
        } else if (MODO_FRIGIO.equals(modoNome)) {
            shapeBase = SHAPE_3NPC_FRIGIO_TRANSPOABLE;
            casaTonicaMestre = 12;
        } else if (MODO_LIDIO.equals(modoNome)) {
            shapeBase = SHAPE_3NPC_LIDIO_TRANSPOABLE;
            casaTonicaMestre = 13;
        } else if (MODO_MIXOLIDIO.equals(modoNome)) {
            shapeBase = SHAPE_3NPC_MIXOLIDIO_TRANSPOABLE;
            casaTonicaMestre = 15;
        } else if (MODO_LOCRIO.equals(modoNome)) {
            shapeBase = SHAPE_3NPC_LOCRIO_TRANSPOABLE;
            casaTonicaMestre = 7;
        } else if (MODO_MENOR_HARMONICA.equals(modoNome)) {
            shapeBase = SHAPE_3NPC_MENOR_HARMONICA_EOLIO_7_TRANSPOABLE;
            casaTonicaMestre = 5;
        } else if (MODO_MENOR_HARMONICA_LOCRIO_6.equals(modoNome)) {
            shapeBase = SHAPE_3NPC_MENOR_HARMONICA_LOCRIO_6_TRANSPOABLE;
            casaTonicaMestre = 7;
        } else if (MODO_MENOR_HARMONICA_JONIO_5SUST.equals(modoNome)) {
            shapeBase = SHAPE_3NPC_MENOR_HARMONICA_JONIO_5SUST_TRANSPOABLE;
            casaTonicaMestre = 8;
        } else if (MODO_MENOR_HARMONICA_DORICO_4SUST.equals(modoNome)) {
            shapeBase = SHAPE_3NPC_MENOR_HARMONICA_DORICO_4SUST_TRANSPOABLE;
            casaTonicaMestre = 10;
        } else if (MODO_MENOR_HARMONICA_FRIGIO_DOMINANTE.equals(modoNome)) {
            shapeBase = SHAPE_3NPC_MENOR_HARMONICA_FRIGIO_DOMINANTE_TRANSPOABLE;
            casaTonicaMestre = 12;
        } else if (MODO_MENOR_HARMONICA_LIDIO_9SUST.equals(modoNome)) {
            shapeBase = SHAPE_3NPC_MENOR_HARMONICA_LIDIO_9SUST_TRANSPOABLE;
            casaTonicaMestre = 13;
        } else if (MODO_MENOR_HARMONICA_DIMINUTO.equals(modoNome)) {
            shapeBase = SHAPE_3NPC_MENOR_HARMONICA_DIMINUTO_TRANSPOABLE;
            casaTonicaMestre = 16;
        } else if (MODO_MENOR_MELODICA.equals(modoNome)) {
            shapeBase = SHAPE_3NPC_MENOR_MELODICA_DORICO_7_TRANSPOABLE;
            casaTonicaMestre = 5;
        } else if (MODO_MENOR_MELODICA_FRIGIO_6.equals(modoNome)) {
            shapeBase = SHAPE_3NPC_MENOR_MELODICA_FRIGIO_6_TRANSPOABLE;
            casaTonicaMestre = 7;
        } else if (MODO_MENOR_MELODICA_LIDIO_5SUST.equals(modoNome)) {
            shapeBase = SHAPE_3NPC_MENOR_MELODICA_LIDIO_5SUST_TRANSPOABLE;
            casaTonicaMestre = 8;
        } else if (MODO_MENOR_MELODICA_MIXO_4SUST.equals(modoNome)) {
            shapeBase = SHAPE_3NPC_MENOR_MELODICA_MIXO_4SUST_TRANSPOABLE;
            casaTonicaMestre = 10;
        } else if (MODO_MENOR_MELODICA_MIXO_6B.equals(modoNome)) {
            shapeBase = SHAPE_3NPC_MENOR_MELODICA_MIXO_6B_TRANSPOABLE;
            casaTonicaMestre = 12;
        } else if (MODO_MENOR_MELODICA_LOCRIO_9.equals(modoNome)) {
            shapeBase = SHAPE_3NPC_MENOR_MELODICA_LOCRIO_9_TRANSPOABLE;
            casaTonicaMestre = 14;
        } else if (MODO_MENOR_MELODICA_ALT_7.equals(modoNome)) {
            shapeBase = SHAPE_3NPC_MENOR_MELODICA_ALT_7_TRANSPOABLE;
            casaTonicaMestre = 16;
        } else if (modoNome.contains("Menor")) {
            shapeBase = SHAPE_3NPC_MENOR_TRANSPOABLE;
            casaTonicaMestre = 5;
        } else {
            shapeBase = SHAPE_3NPC_MAIOR_TRANSPOABLE;
            casaTonicaMestre = 8;
        }

        // No shape Diminuto (harmonica) e no Alt 7 (melodica) a tonica G#
        // existe na casa 4 e na 16. O PDF desenha a posicao na casa 16, entao
        // preservamos essa oitava apenas nessas familias (a regra dos gregos
        // permanece intocada).
        if ((MODOS_MENOR_HARMONICA.contains(modoNome) || MODOS_MENOR_MELODICA.contains(modoNome))
                && casaTonicaAlvo + OITAVA == casaTonicaMestre) {
            casaTonicaAlvo += OITAVA;
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
