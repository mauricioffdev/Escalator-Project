package br.com.escalator.model;

import java.util.ArrayList;
import java.util.List;

public class Escala {
    public static final Escala MAIOR = new Escala("Maior", new int[]{0, 2, 4, 5, 7, 9, 11});
    public static final Escala PENTATONICA_M7 = new Escala("Pentatonica m7", new int[]{0, 3, 5, 7, 10});
    public static final Escala PENTATONICA_BLUES = new Escala("Pentatonica Blues", new int[]{0, 3, 5, 6, 7, 10});
    /** Menor harmonica: T 2 3b 4 5 6b 7+ (ex.: A B C D E F G#). */
    public static final Escala MENOR_HARMONICA = new Escala("Menor Harmônica", new int[]{0, 2, 3, 5, 7, 8, 11});

    private final String nome;
    private final int[] intervalos;

    public Escala(String nome, int[] intervalos) {
        this.nome = nome;
        this.intervalos = intervalos;
    }

    public List<Nota> calcularNotas(Nota tonica) {
        List<Nota> notasDaEscala = new ArrayList<>();
        int valorTonica = tonica.getValor();

        for (int intervalo : intervalos) {
            notasDaEscala.add(Nota.getNotaPorValor(valorTonica + intervalo));
        }

        return notasDaEscala;
    }

    public String getNome() {
        return nome;
    }
}
