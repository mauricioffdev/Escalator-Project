package br.com.escalator.service;

import java.util.Arrays;
import java.util.Optional;

/**
 * Fonte unica dos 7 shapes da menor melodica em Am, conforme o PDF
 * {@code menormelodica_shapes.pdf}. Cada modo guarda a opcao do seletor de
 * padrao, a distancia em semitons ate a tonica base da familia (usada para
 * transpor o desenho e calcular a armadura), o identificador do shape no core,
 * o rotulo exibido e a sequencia de intervalos do PDF.
 *
 * <p>Os valores de {@code padrao} comecam em 35 para nao colidir com os shapes
 * comuns (1 a 8), os modos gregos (9 a 15), a menor harmonica (16 a 22) e as
 * pentatonicas M7/m6 (23 a 34). O modo e discriminado pelo seletor de escala
 * ({@code modo=13}).</p>
 */
public enum ModoMenorMelodica {

    DORICO_7PLUS("35", 0, GeradorDeTablatura.MODO_MENOR_MELODICA,
            "Dórico 7+ (Am7+)", "T 2 3b 4 5 6 7+"),
    FRIGIO_6("36", 2, GeradorDeTablatura.MODO_MENOR_MELODICA_FRIGIO_6,
            "Frígio 6 (Bm7)", "T 2b 3b 4 5 6 7"),
    LIDIO_5SUST("37", 3, GeradorDeTablatura.MODO_MENOR_MELODICA_LIDIO_5SUST,
            "Lídio 5# (C7+/5#)", "T 2 3 4# 5# 6 7+"),
    MIXO_4SUST("38", 5, GeradorDeTablatura.MODO_MENOR_MELODICA_MIXO_4SUST,
            "Mixo 4# (D7)", "T 2 3 4# 5 6 7"),
    MIXO_6B("39", 7, GeradorDeTablatura.MODO_MENOR_MELODICA_MIXO_6B,
            "Mixo 6b (E7)", "T 2 3 4 5 6b 7"),
    LOCRIO_9("40", 9, GeradorDeTablatura.MODO_MENOR_MELODICA_LOCRIO_9,
            "Lócrio 9 (F#m7/5b)", "T 2 3b 4 5b 6b 7"),
    ALT_7("41", 11, GeradorDeTablatura.MODO_MENOR_MELODICA_ALT_7,
            "Alt 7 (superlócrio) (G#m7/5b)", "T 2b 2# 3 5b 5# 7");

    /** Opcao do seletor de escala que habilita esta familia de shapes. */
    public static final String MODO_OPCAO = "13";
    /**
     * A menor melodica e um Dorico com 7+ da parente maior: Am tem armadura de
     * G (F# na armadura, G# escrito na pauta), Dm tem armadura de C, etc.
     */
    public static final int SALTO_ARMADURA_MAIOR = -2;

    private final String padraoOpcao;
    private final int grau;
    private final String modoTablatura;
    private final String rotulo;
    private final String intervalos;

    ModoMenorMelodica(String padraoOpcao, int grau, String modoTablatura,
                      String rotulo, String intervalos) {
        this.padraoOpcao = padraoOpcao;
        this.grau = grau;
        this.modoTablatura = modoTablatura;
        this.rotulo = rotulo;
        this.intervalos = intervalos;
    }

    /** Opcao do seletor de padrao (35 a 41). */
    public String padraoOpcao() {
        return padraoOpcao;
    }

    /** Distancia em semitons entre o root do shape e a tonica base da familia. */
    public int grau() {
        return grau;
    }

    /** Identificador do modo usado pelo gerador de tablatura e pelo AlphaTex. */
    public String modoTablatura() {
        return modoTablatura;
    }

    /** Rotulo exibido no dropdown de padroes. */
    public String rotulo() {
        return rotulo;
    }

    /** Sequencia de intervalos do PDF menormelodica_shapes.pdf. */
    public String intervalos() {
        return intervalos;
    }

    public static Optional<ModoMenorMelodica> porPadrao(String padraoOpcao) {
        return Arrays.stream(values())
                .filter(modo -> modo.padraoOpcao.equals(padraoOpcao))
                .findFirst();
    }

    public static Optional<ModoMenorMelodica> porModoTablatura(String modoTablatura) {
        return Arrays.stream(values())
                .filter(modo -> modo.modoTablatura.equals(modoTablatura))
                .findFirst();
    }
}
