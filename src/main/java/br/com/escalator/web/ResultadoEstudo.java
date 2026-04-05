package br.com.escalator.web;

import java.util.List;

public record ResultadoEstudo(
        String titulo,
        String notasFormatadas,
        String nomePadrao,
        List<String> linhasSaida,
        String tablaturaFormatada
) {
}
