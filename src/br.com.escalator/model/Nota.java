package br.com.escalator.model;

import java.util.Map;

// src/main/java/br/com/escalator/model/Nota.java
public enum Nota {
    C(0), C_SHARP(1), D(2), D_SHARP(3), E(4), F(5), F_SHARP(6),
    G(7), G_SHARP(8), A(9), A_SHARP(10), B(11);

    private final int valor;

    Nota(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }

    // Método utilitário para obter a próxima nota
    public static Nota getNotaPorValor(int valor) {
        int valorMod = valor % 12;
        for (Nota nota : Nota.values()) {
            if (nota.valor == valorMod) {
                return nota;
            }
        }
        throw new IllegalArgumentException("Valor de nota inválido: " + valor);
    }

    //Sobrescrevendo toString() para melhor saída**
    @Override
    public String toString() {
        // Isso fará com que a saída do console mostre C#, D#, F#, etc., em vez de C_SHARP
        return this.name().replace("_SHARP", "#");
    }

    // Mapeamento de Bemol (String Exibição) para Sustenido (String Enum Interna)
    private static final Map<String, String> BEMOL_TO_SHARP = Map.of(
            "Bb", "A_SHARP",
            "Eb", "D_SHARP",
            "Ab", "G_SHARP",
            "Db", "C_SHARP",
            "Gb", "F_SHARP",
            "Cb", "B" // Cb é enarmônico de B natural
    );

    // Mapeamento de Sustenido (String Enum Interna) para Bemol (String Exibição)
    public static final Map<String, String> SHARP_TO_BEMOL = Map.of(
            "A#", "Bb",
            "D#", "Eb",
            "G#", "Ab",
            "C#", "Db",
            "F#", "Gb"
    );

    // As duas sequências a serem exibidas no menu para entrada do usuário
    private static final String CICLO_SUSTENIDOS_EXIBICAO = "C, G, D, A, E, B, F#, C#";
    private static final String CICLO_BEMOIS_EXIBICAO = "F, Bb, Eb, Ab, Db, Gb, Cb";
}