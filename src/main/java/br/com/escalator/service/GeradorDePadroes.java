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

        return gerarSequenciaPorNomes(nomesDe(notasDaEscala), notasPorSequencia);
    }

    /**
     * Mesma sequencia de N notas, porem partindo dos nomes das notas ja
     * escritos na convencao da tonalidade (usado pela menor harmonica, cuja
     * 7maior nao existe no enum de notas).
     */
    public List<String> gerarSequenciaPorNomes(List<String> nomesDasNotas, int notasPorSequencia) {
        List<String> sequencias = new ArrayList<>();
        if (nomesDasNotas == null || nomesDasNotas.isEmpty()) {
            return sequencias;
        }

        for (int i = 0; i < nomesDasNotas.size(); i++) {
            StringBuilder sb = new StringBuilder("[");
            for (int j = 0; j < notasPorSequencia; j++) {
                sb.append(nomesDasNotas.get((i + j) % nomesDasNotas.size()));
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

        return gerarTriadesPorNomes(nomesDe(notasDaEscala), modoNome);
    }

    /**
     * Mesmas 7 trides diatonicas, porem montadas com os nomes das notas ja
     * escritos na convencao da tonalidade.
     */
    public List<String> gerarTriadesPorNomes(List<String> nomesDasNotas, String modoNome) {
        List<String> triades = new ArrayList<>();
        if (nomesDasNotas == null || nomesDasNotas.size() != 7) {
            return triades;
        }

        String[] qualidades;
        if (modoNome.contains("Harmônica")) {
            qualidades = new String[]{"menor", "diminuta", "aumentada", "menor", "Maior", "Maior", "diminuta"};
        } else if (modoNome.contains("Menor")) {
            qualidades = new String[]{"menor", "diminuta", "Maior", "menor", "menor", "Maior", "Maior"};
        } else {
            qualidades = new String[]{"Maior", "menor", "menor", "Maior", "Maior", "menor", "diminuta"};
        }

        for (int i = 0; i < nomesDasNotas.size(); i++) {
            String tonica = nomesDasNotas.get(i);
            String terca = nomesDasNotas.get((i + 2) % nomesDasNotas.size());
            String quinta = nomesDasNotas.get((i + 4) % nomesDasNotas.size());

            triades.add(String.format("%s %s: %s - %s - %s", tonica, qualidades[i], tonica, terca, quinta));
        }

        return triades;
    }

    private List<String> nomesDe(List<Nota> notas) {
        return notas.stream().map(Nota::toString).toList();
    }
}
