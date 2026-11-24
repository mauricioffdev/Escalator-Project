package br.com.escalator.service;

import java.util.Arrays;
import java.util.Optional;

/**
 * Fonte unica dos 6 desenhos da pentatonica M7 em A, conforme
 * {@code tabs-kb/pdf/pentaM7.pdf} (T 3 4 5 7: A C# D E G).
 *
 * <p>Os valores de {@code padrao} começam em 23 para nao colidir com os shapes
 * comuns (1 a 8), os modos gregos (9 a 15) e a menor harmonica (16 a 22). O
 * seletor de escala usa {@code modo=11}.</p>
 */
public enum PentatonicaM7 {

    SHAPE_1("23", GeradorDeTablatura.MODO_PENTATONICA_M7_A, "Shape 1"),
    SHAPE_2("24", GeradorDeTablatura.MODO_PENTATONICA_M7_A_SHAPE2, "Shape 2"),
    SHAPE_3("25", GeradorDeTablatura.MODO_PENTATONICA_M7_A_SHAPE3, "Shape 3"),
    SHAPE_4("26", GeradorDeTablatura.MODO_PENTATONICA_M7_A_SHAPE4, "Shape 4"),
    SHAPE_5("27", GeradorDeTablatura.MODO_PENTATONICA_M7_A_SHAPE5, "Shape 5"),
    EXTRA("28", GeradorDeTablatura.MODO_PENTATONICA_M7_A_EXTRA, "Shape em 3 oitavas");

    /** Opcao do seletor de modo de estudo. */
    public static final String MODO_OPCAO = "11";
    /** Distancia em semitons da tonica ate o parente (A -> D, quarta justa). */
    public static final int SALTO_PARENTE = 5;
    /** Intervalos impressos no PDF de referencia. */
    public static final String INTERVALOS = "T 3 4 5 7";

    private final String padraoOpcao;
    private final String modoTablatura;
    private final String rotulo;

    PentatonicaM7(String padraoOpcao, String modoTablatura, String rotulo) {
        this.padraoOpcao = padraoOpcao;
        this.modoTablatura = modoTablatura;
        this.rotulo = rotulo;
    }

    public String padraoOpcao() {
        return padraoOpcao;
    }

    public String modoTablatura() {
        return modoTablatura;
    }

    public String rotulo() {
        return rotulo;
    }

    public static Optional<PentatonicaM7> porPadrao(String padraoOpcao) {
        return Arrays.stream(values())
                .filter(shape -> shape.padraoOpcao.equals(padraoOpcao))
                .findFirst();
    }

    public static Optional<PentatonicaM7> porModoTablatura(String modoTablatura) {
        return Arrays.stream(values())
                .filter(shape -> shape.modoTablatura.equals(modoTablatura))
                .findFirst();
    }
}
