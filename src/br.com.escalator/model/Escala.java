package br.com.escalator.model;

import java.util.ArrayList;
import java.util.List;


public class Escala {
    private final String nome;
    // Os intervalos são representados pela distância em semitons da tônica (ex: 0, 2, 4, 5, 7, 9, 11 para Maior)
    private final int[] intervalos;

    // Torna as escalas constantes estáticas para uso em App.java
    public static final Escala MAIOR = new Escala("Maior", new int[]{0, 2, 4, 5, 7, 9, 11});
    // Menor Natural (T-S-T-T-S-T-T): Intervalos 0, 2, 3, 5, 7, 8, 10
    public static final Escala MENOR_NATURAL = new Escala("Menor Natural", new int[]{0, 2, 3, 5, 7, 8, 10});


    public Escala(String nome, int[] intervalos) {
        this.nome = nome;
        this.intervalos = intervalos;
    }

    //Metodo principal: calcula as notas da escala a partir de uma tônica (Root)
    public List<Nota> calcularNotas(Nota tonica) {
        List<Nota> notasDaEscala = new ArrayList<>();
        int valorTonica = tonica.getValor();

        for (int intervalo : intervalos) {
            // Calcula o valor da nota usando a tônica + intervalo (em semitons)
            int valorNota = valorTonica + intervalo;
            // Usa o Enum.getNotaPorValor para converter o valor modular na Nota correta
            notasDaEscala.add(Nota.getNotaPorValor(valorNota));
        }
        return notasDaEscala;
    }

    // Getters
    public String getNome() { return nome; }
}