package br.com.escalator.web;

import java.util.List;

public record ResultadoEstudo(
        String titulo,
        String notasFormatadas,
        String nomePadrao,
        List<String> linhasSaida,
        String tablaturaFormatada,
        String tablaturaAlphaTex,
        String intervalos,
        String referencia
) {
    public ResultadoEstudo(String titulo, String notasFormatadas, String nomePadrao,
                           List<String> linhasSaida, String tablaturaFormatada,
                           String tablaturaAlphaTex, String intervalos) {
        this(titulo, notasFormatadas, nomePadrao, linhasSaida, tablaturaFormatada,
                tablaturaAlphaTex, intervalos, "");
    }
}
