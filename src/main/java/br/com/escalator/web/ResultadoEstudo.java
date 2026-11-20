package br.com.escalator.web;

import java.util.List;

public record ResultadoEstudo(
        String titulo,
        String notasFormatadas,
        String nomePadrao,
        List<String> linhasSaida,
        String tablaturaFormatada,
        String tablaturaAlphaTex,
        String intervalos
) {
    /** Mantem as chamadas antigas (licks e saidas sem sequencia de intervalos). */
    public ResultadoEstudo(String titulo, String notasFormatadas, String nomePadrao,
                           List<String> linhasSaida, String tablaturaFormatada,
                           String tablaturaAlphaTex) {
        this(titulo, notasFormatadas, nomePadrao, linhasSaida, tablaturaFormatada,
                tablaturaAlphaTex, "");
    }
}
