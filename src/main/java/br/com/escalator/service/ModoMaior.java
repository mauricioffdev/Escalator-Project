package br.com.escalator.service;

import java.util.Arrays;
import java.util.Optional;

/**
 * Fonte unica dos 7 modos da escala maior. Cada modo guarda a opcao legada do
 * seletor de modo, a opcao atual no seletor de padrao, o grau em relacao a
 * tonica parente (usado na armadura), o identificador do shape no core, o rotulo
 * exibido e a sequencia de intervalos do PDF de referencia.
 */
public enum ModoMaior {

    JONIO("1", "9", 0, "Maior", "Jônio (Escala Maior)",
            "T 2 3 4 5 6 7+", false),
    DORICO("6", "10", 2, GeradorDeTablatura.MODO_DORICO, "Dórico",
            "T 2 3b 4 5 6 7", false),
    FRIGIO("7", "11", 4, GeradorDeTablatura.MODO_FRIGIO, "Frígio",
            "T 2b 3b 4 5 6b 7", false),
    LIDIO("8", "12", 5, GeradorDeTablatura.MODO_LIDIO, "Lídio",
            "T 2 3 4# 5 6 7+", false),
    MIXOLIDIO("9", "13", 7, GeradorDeTablatura.MODO_MIXOLIDIO, "Mixolídio",
            "T 2 3 4 5 6 7", false),
    EOLIO("2", "14", 9, "Menor Natural (Relativa)", "Eólio (Escala menor relativa)",
            "T 2 3b 4 5 6b 7", true),
    LOCRIO("10", "15", 11, GeradorDeTablatura.MODO_LOCRIO, "Lócrio",
            "T 2b 3b 4 5b 6b 7", false);

    private final String opcao;
    private final String padraoOpcao;
    private final int grau;
    private final String modoTablatura;
    private final String rotulo;
    private final String intervalos;
    private final boolean relativaMenor;

    ModoMaior(String opcao, String padraoOpcao, int grau, String modoTablatura,
              String rotulo, String intervalos, boolean relativaMenor) {
        this.opcao = opcao;
        this.padraoOpcao = padraoOpcao;
        this.grau = grau;
        this.modoTablatura = modoTablatura;
        this.rotulo = rotulo;
        this.intervalos = intervalos;
        this.relativaMenor = relativaMenor;
    }

    /** Opcao legada do seletor de modo, preservada para URLs e chamadas diretas. */
    public String opcao() {
        return opcao;
    }

    /** Opcao atual no seletor de padrao (os modos gregos "viraram shapes"). */
    public String padraoOpcao() {
        return padraoOpcao;
    }

    /** Distancia em semitons entre a tonica do modo e a tonica parente. */
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

    /** Sequencia de intervalos do PDF Shapes_Modos.pdf. */
    public String intervalos() {
        return intervalos;
    }

    /** verdadeiro apenas para Eolio, cuja armadura mantem o sufixo minor. */
    public boolean relativaMenor() {
        return relativaMenor;
    }

    public static Optional<ModoMaior> porOpcao(String opcao) {
        return Arrays.stream(values()).filter(modo -> modo.opcao.equals(opcao)).findFirst();
    }

    public static Optional<ModoMaior> porPadrao(String padraoOpcao) {
        return Arrays.stream(values())
                .filter(modo -> modo.padraoOpcao.equals(padraoOpcao))
                .findFirst();
    }

    public static Optional<ModoMaior> porModoTablatura(String modoTablatura) {
        return Arrays.stream(values())
                .filter(modo -> modo.modoTablatura.equals(modoTablatura))
                .findFirst();
    }
}
