package br.com.escalator.service;

import br.com.escalator.model.Nota;
import java.util.ArrayList;
import java.util.List;

public class GeradorDePadroes {

    /**
     * Gera um padrão de sequência de N notas.
     * Ex: Para N=3: (1-2-3), (2-3-4), (3-4-5), etc.
     * A formatação final (# vs b) deve ser aplicada APÓS a chamada deste método, em App.java.
     */
    public List<String> gerarSequencia(List<Nota> notas, int n) {
        List<String> padrao = new ArrayList<>();
        int tamanhoEscala = notas.size();

        // Para evitar IndexOutOfBounds, permite a sequência "circular"
        for (int i = 0; i < tamanhoEscala; i++) {
            StringBuilder sequencia = new StringBuilder();

            for (int j = 0; j < n; j++) {
                // (i + j) % tamanhoEscala garante que o índice volte ao início (ciclo)
                Nota notaAtual = notas.get((i + j) % tamanhoEscala);

                // Nota: A saída é sempre o nome do Enum (ex: C, F#, A#)
                sequencia.append(notaAtual);
                if (j < n - 1) {
                    sequencia.append("-");
                }
            }
            padrao.add(sequencia.toString());
        }
        return padrao;
    }

    /*
     Gera as tríades (1-3-5) para cada grau da escala.
     Além das notas, informa a qualidade (Maior, Menor, Diminuta).
     A formatação final (# vs b) deve ser aplicada APÓS a chamada deste
     metodo em java.
     Aceita o nome da escala (ou um indicador de modo) para definir as qualidades.
     */
    public List<String> gerarTriades(List<Nota> notas, String modo) {
        List<String> triades = new ArrayList<>();
        int tamanhoEscala = notas.size(); // Deve ser 7 para a escala maior

        // Qualidades fixas
        String[] qualidades;

        if (modo.contains("Maior")) {
            // Maior: I, ii, iii, IV, V, vi, vii°
            qualidades = new String[]{"Maior (I)", "Menor (II)", "Menor (III)", "Maior (IV)", "Maior (V)", "Menor (VI)", "Diminuta (VII°)"};
        } else { // Assumindo Menor Natural
            // Menor Natural: i, ii°, III, iv, v, VI, VII
            qualidades = new String[]{"Menor (I)", "Diminuta (II°)", "Maior (III)", "Menor (IV)", "Menor (V)", "Maior (VI)", "Maior (VII)"};
        }

        // ... (o loop de cálculo das notas é o mesmo)
        for (int i = 0; i < tamanhoEscala; i++) {
            // ... (cálculo de terca e quinta)

            // Variaveis declaradas e resolvidas corretamente dentro do escopo do loop.
            Nota terca = notas.get((i + 2) % tamanhoEscala); // 3ª nota (índice + 2)
            Nota quinta = notas.get((i + 4) % tamanhoEscala); // 5ª nota (índice + 4)

            String triade = String.format("%s - %s - %s [%s]",
                    notas.get(i), terca, quinta, qualidades[i]);
            triades.add(triade);
        }
        return triades;
    }
}