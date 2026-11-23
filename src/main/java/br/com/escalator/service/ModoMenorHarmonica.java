package br.com.escalator.service;

import java.util.Arrays;
import java.util.Optional;

/**
 * Fonte unica dos 7 shapes da menor harmonica em Am, conforme o PDF
 * {@code SHAPES_menorharm.pdf}. Cada modo guarda a opcao do seletor de padrao,
 * a distancia em semitons ate a tonica parente (usada na armadura), o
 * identificador do shape no core, o rotulo exibido e a sequencia de intervalos
 * do PDF.
 *
 * <p>Os valores de {@code padrao} comecam em 16 para nao colidir com os shapes
 * comuns (1 a 8) nem com os modos gregos (9 a 15). O modo e discriminado pelo
 * seletor de escala ({@code modo=5}).</p>
 */
public enum ModoMenorHarmonica {

    EOLIO_7("16", 0, GeradorDeTablatura.MODO_MENOR_HARMONICA,
            "Eólio 7+ (Am7+)", "T 2 3b 4 5 6b 7+"),
    LOCRIO_6("17", 2, "Lócrio 6",
            "Lócrio 6 (Bm7(5b))", "T 2b 3b 4 5b 6 7"),
    JONIO_5SUST("18", 3, "Jônio 5#",
            "Jônio 5# (C7+/5#)", "T 2 3 4 5# 6 7+"),
    DORICO_4SUST("19", 5, "Dórico 4#",
            "Dórico 4# (Dm7)", "T 2 3b 4# 5 6 7"),
    FRIGIO_DOMINANTE("20", 7, "Frígio Dominante",
            "Frígio Dominante/Frígio maior (E7)", "T 2b 3 4 5 6b 7"),
    LIDIO_9SUST("21", 8, "Lídio 9#",
            "Lídio 9# (F7+)", "T 2# 3 4# 5 6 7+"),
    DIMINUTO("22", 11, "Diminuto",
            "Diminuto (G#dim)", "T 2b 3b 4b 5b 6b 7b");

    private final String padraoOpcao;
    private final int grau;
    private final String modoTablatura;
    private final String rotulo;
    private final String intervalos;

    ModoMenorHarmonica(String padraoOpcao, int grau, String modoTablatura,
                       String rotulo, String intervalos) {
        this.padraoOpcao = padraoOpcao;
        this.grau = grau;
        this.modoTablatura = modoTablatura;
        this.rotulo = rotulo;
        this.intervalos = intervalos;
    }

    /** Opcao do seletor de padrao (16 a 22). */
    public String padraoOpcao() {
        return padraoOpcao;
    }

    /** Distancia em semitons entre a tonica do modo e a tonica parente menor. */
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

    /** Sequencia de intervalos do PDF SHAPES_menorharm.pdf. */
    public String intervalos() {
        return intervalos;
    }

    public static Optional<ModoMenorHarmonica> porPadrao(String padraoOpcao) {
        return Arrays.stream(values())
                .filter(modo -> modo.padraoOpcao.equals(padraoOpcao))
                .findFirst();
    }

    public static Optional<ModoMenorHarmonica> porModoTablatura(String modoTablatura) {
        return Arrays.stream(values())
                .filter(modo -> modo.modoTablatura.equals(modoTablatura))
                .findFirst();
    }
}
