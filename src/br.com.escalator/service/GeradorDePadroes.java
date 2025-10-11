package br.com.escalator.service;

import br.com.escalator.model.Nota;

import java.util.ArrayList;
import java.util.List;

public class GeradorDePadroes {

    /**
     * Gera sequências de notas a partir de uma escala.
     * @param notasDaEscala A lista de notas da escala (já rotacionada para a tônica correta).
     * @param notasPorSequencia O número de notas em cada grupo (ex: 3 para tercinas).
     * @return Uma lista de strings, onde cada string é uma sequência (ex: "[C, D, E]").
     */
    public List<String> gerarSequencia(List<Nota> notasDaEscala, int notasPorSequencia) {
        List<String> sequencias = new ArrayList<>();
        if (notasDaEscala == null || notasDaEscala.isEmpty()) {
            return sequencias;
        }

        for (int i = 0; i < notasDaEscala.size(); i++) {
            StringBuilder sb = new StringBuilder("[");
            for (int j = 0; j < notasPorSequencia; j++) {
                sb.append(notasDaEscala.get((i + j) % notasDaEscala.size()).toString());
                if (j < notasPorSequencia - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");
            sequencias.add(sb.toString());
        }
        return sequencias;
    }

    /**
     * Gera as tríades diatônicas de uma escala.
     * @param notasDaEscala A lista de notas da escala (já rotacionada para a tônica correta).
     * @param modoNome A string que identifica o modo ("Maior" ou "Menor Natural").
     * @return Uma lista de strings, onde cada string representa uma tríade formatada.
     */
    public List<String> gerarTriades(List<Nota> notasDaEscala, String modoNome) {
        List<String> triades = new ArrayList<>();
        if (notasDaEscala == null || notasDaEscala.size() != 7) {
            return triades;
        }

        // CORREÇÃO: Usando a variável 'modoNome' que é passada como parâmetro.
        String[] qualidades;
        if (modoNome.contains("Menor")) {
            qualidades = new String[]{"menor", "diminuta", "Maior", "menor", "menor", "Maior", "Maior"};
        } else { // Default para Maior
            qualidades = new String[]{"Maior", "menor", "menor", "Maior", "Maior", "menor", "diminuta"};
        }

        for (int i = 0; i < notasDaEscala.size(); i++) {
            Nota tonica = notasDaEscala.get(i);
            Nota terca = notasDaEscala.get((i + 2) % notasDaEscala.size());
            Nota quinta = notasDaEscala.get((i + 4) % notasDaEscala.size());

            String triadeFormatada = String.format("%s %s: %s - %s - %s",
                    tonica.toString(),
                    qualidades[i],
                    tonica.toString(),
                    terca.toString(),
                    quinta.toString()
            );
            triades.add(triadeFormatada);
        }
        return triades;
    }
}