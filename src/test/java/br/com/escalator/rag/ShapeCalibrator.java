package br.com.escalator.rag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Converte os numeros impressos no diagrama para casas do braço.
 *
 * <p>A regra e a mesma usada quando derivamos os shapes 4, 5 e extra: o numero
 * impresso indica a posicao dentro da caixa e a casa e
 * {@code base da corda + numero - 1}. As bases abaixo sao as primeiras casas
 * de cada corda nos shapes m7 ja validados em {@code GeradorDeTablatura}.</p>
 *
 * <p>Ordem das cordas: E aguda, B, G, D, A, E grave (a mesma de
 * {@code gerarNotas}).</p>
 */
public final class ShapeCalibrator {

    /** Bases por shape, indexadas na ordem E aguda -> E grave. */
    public static final Map<Integer, int[]> BASES_POR_SHAPE = Map.of(
            1, new int[]{5, 5, 5, 5, 5, 5},
            2, new int[]{8, 8, 7, 7, 7, 8},
            3, new int[]{10, 10, 9, 10, 10, 10},
            4, new int[]{12, 13, 12, 12, 12, 12},
            5, new int[]{15, 15, 14, 14, 15, 15}
    );

    /** Bases da posicao aberta de 3 oitavas (shape extra do m7). */
    public static final int[] BASE_ABERTA = {10, 10, 7, 7, 5, 5};

    private ShapeCalibrator() {
    }

    /**
     * Converte cada linha do diagrama em casas. O indice 0 da lista devolvida
     * e a corda E aguda e o indice 5 e a E grave, na mesma ordem em que os
     * numeros aparecem no PDF (evitando colisao de nomes entre as duas cordas E).
     */
    public static List<List<Integer>> casasPorCorda(TabReference referencia, int[] bases) {
        List<TabReference.CordaReferencia> cordas = referencia.cordas();
        if (cordas.size() != bases.length) {
            throw new IllegalArgumentException(
                    "Referencia com " + cordas.size() + " cordas para " + bases.length + " bases");
        }
        List<List<Integer>> casas = new ArrayList<>();
        for (int i = 0; i < cordas.size(); i++) {
            int base = bases[i];
            List<Integer> convertidas = new ArrayList<>();
            for (int numero : cordas.get(i).dedos()) {
                convertidas.add(base + numero - 1);
            }
            casas.add(List.copyOf(convertidas));
        }
        return List.copyOf(casas);
    }

    public static List<List<Integer>> casasPorCorda(TabReference referencia, int numeroShape) {
        int[] bases = BASES_POR_SHAPE.get(numeroShape);
        if (bases == null) {
            throw new IllegalArgumentException("Shape sem base conhecida: " + numeroShape);
        }
        return casasPorCorda(referencia, bases);
    }
}
