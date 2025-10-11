package br.com.escalator.service;

import br.com.escalator.model.Nota;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class GeradorDeTablatura {

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

    private static final ShapeMestre SHAPE_3NPC_MAIOR_TRANSPOABLE;
    private static final ShapeMestre SHAPE_3NPC_MENOR_TRANSPOABLE;
    private static final Map<String, Integer> NOTAS_NA_CORDA_E;

    static {
        NOTAS_NA_CORDA_E = new HashMap<>();
        NOTAS_NA_CORDA_E.put("E", 0); NOTAS_NA_CORDA_E.put("F", 1);
        NOTAS_NA_CORDA_E.put("F_SHARP", 2); NOTAS_NA_CORDA_E.put("G", 3);
        NOTAS_NA_CORDA_E.put("G_SHARP", 4); NOTAS_NA_CORDA_E.put("A", 5);
        NOTAS_NA_CORDA_E.put("A_SHARP", 6); NOTAS_NA_CORDA_E.put("B", 7);
        NOTAS_NA_CORDA_E.put("C", 8); NOTAS_NA_CORDA_E.put("C_SHARP", 9);
        NOTAS_NA_CORDA_E.put("D", 10); NOTAS_NA_CORDA_E.put("D_SHARP", 11);

        // --- CORREÇÃO: Posições redefinidas para o layout 3+3 ---

        Map<String, List<NotaTab>> notas3NpcMaior = new HashMap<>();
        notas3NpcMaior.put("E_high", Arrays.asList(new NotaTab(10, 50), new NotaTab(12, 53), new NotaTab(13, 56)));
        notas3NpcMaior.put("B",      Arrays.asList(new NotaTab(10, 40), new NotaTab(12, 43), new NotaTab(13, 46)));
        notas3NpcMaior.put("G",      Arrays.asList(new NotaTab(9, 31), new NotaTab(10, 34), new NotaTab(12, 37)));
        notas3NpcMaior.put("D",      Arrays.asList(new NotaTab(9, 20), new NotaTab(10, 23), new NotaTab(12, 26)));
        notas3NpcMaior.put("A",      Arrays.asList(new NotaTab(8, 11), new NotaTab(10, 14), new NotaTab(12, 17)));
        notas3NpcMaior.put("E_low",  Arrays.asList(new NotaTab(8, 2), new NotaTab(10, 5), new NotaTab(12, 8)));
        SHAPE_3NPC_MAIOR_TRANSPOABLE = new ShapeMestre(notas3NpcMaior, 58);

        Map<String, List<NotaTab>> notas3NpcMenor = new HashMap<>();
        notas3NpcMenor.put("E_high", Arrays.asList(new NotaTab(7, 50), new NotaTab(8, 53), new NotaTab(10, 56)));
        notas3NpcMenor.put("B",      Arrays.asList(new NotaTab(6, 40), new NotaTab(8, 43), new NotaTab(10, 46)));
        notas3NpcMenor.put("G",      Arrays.asList(new NotaTab(5, 31), new NotaTab(7, 34), new NotaTab(9, 37)));
        notas3NpcMenor.put("D",      Arrays.asList(new NotaTab(5, 20), new NotaTab(7, 23), new NotaTab(9, 26)));
        notas3NpcMenor.put("A",      Arrays.asList(new NotaTab(5, 11), new NotaTab(7, 14), new NotaTab(8, 17)));
        notas3NpcMenor.put("E_low",  Arrays.asList(new NotaTab(5, 2), new NotaTab(7, 5), new NotaTab(8, 8)));
        SHAPE_3NPC_MENOR_TRANSPOABLE = new ShapeMestre(notas3NpcMenor, 58);
    }

    public static String[] gerar(String tonicaAlvo, String modoNome) {
        ShapeMestre shapeBase;
        int casaTonicaMestre;

        String tonicaNormalizada = tonicaAlvo.replace("#", "_SHARP");

        if (modoNome.contains("Menor")) {
            shapeBase = SHAPE_3NPC_MENOR_TRANSPOABLE;
            casaTonicaMestre = 5;
        } else {
            shapeBase = SHAPE_3NPC_MAIOR_TRANSPOABLE;
            casaTonicaMestre = 8;
        }

        if (!NOTAS_NA_CORDA_E.containsKey(tonicaNormalizada)) {
            return new String[]{"Tonalidade inválida para transposição: " + tonicaAlvo};
        }

        int casaTonicaAlvo = NOTAS_NA_CORDA_E.get(tonicaNormalizada);
        int shift = casaTonicaAlvo - casaTonicaMestre;
        return montarTablatura(shapeBase, shift);
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
                    for(int j=0; j<fretStr.length(); j++){
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