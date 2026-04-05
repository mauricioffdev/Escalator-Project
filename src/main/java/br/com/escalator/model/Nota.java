package br.com.escalator.model;

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

    public static Nota getNotaPorValor(int valor) {
        int valorMod = Math.floorMod(valor, 12);
        for (Nota nota : Nota.values()) {
            if (nota.valor == valorMod) {
                return nota;
            }
        }
        throw new IllegalArgumentException("Valor de nota invalido: " + valor);
    }

    @Override
    public String toString() {
        return this.name().replace("_SHARP", "#");
    }
}
