package br.com.escalator.service;

import br.com.escalator.model.Nota;

import java.util.ArrayList;
import java.util.List;

public class GeradorDePadroes {
    public List<String> gerarSequencia(List<Nota> notasDaEscala, int notasPorSequencia) {
        List<String> sequencias = new ArrayList<>();
        if (notasDaEscala == null || notasDaEscala.isEmpty()) {
            return sequencias;
        }

        for (int i = 0; i < notasDaEscala.size(); i++) {
            StringBuilder sb = new StringBuilder("[");
            for (int j = 0; j < notasPorSequencia; j++) {
                sb.append(notasDaEscala.get((i + j) % notasDaEscala.size()));
                if (j < notasPorSequencia - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");
            sequencias.add(sb.toString());
        }
        return sequencias;
    }

    public List<String> gerarTriades(List<Nota> notasDaEscala, String modoNome) {
        List<String> triades = new ArrayList<>();
        if (notasDaEscala == null || notasDaEscala.size() != 7) {
            return triades;
        }

        String[] qualidades;
        if (modoNome.contains("Menor")) {
            qualidades = new String[]{"menor", "diminuta", "Maior", "menor", "menor", "Maior", "Maior"};
        } else {
            qualidades = new String[]{"Maior", "menor", "menor", "Maior", "Maior", "menor", "diminuta"};
        }

        for (int i = 0; i < notasDaEscala.size(); i++) {
            Nota tonica = notasDaEscala.get(i);
            Nota terca = notasDaEscala.get((i + 2) % notasDaEscala.size());
            Nota quinta = notasDaEscala.get((i + 4) % notasDaEscala.size());

            triades.add(String.format("%s %s: %s - %s - %s", tonica, qualidades[i], tonica, terca, quinta));
        }

        return triades;
    }
}
