package br.com.escalator.rag;

import java.util.List;

/**
 * Referencia musical extraida de um PDF, antes de qualquer conversao para
 * casas do braço. Os numeros sao dedos (1 a 4), conforme impressos na folha.
 */
public record TabReference(
        String documento,
        String titulo,
        List<CordaReferencia> cordas
) {
    public TabReference {
        cordas = List.copyOf(cordas);
    }

    public record CordaReferencia(String corda, List<Integer> dedos) {
        public CordaReferencia {
            dedos = List.copyOf(dedos);
        }
    }
}
