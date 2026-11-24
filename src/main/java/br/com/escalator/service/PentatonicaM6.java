package br.com.escalator.service;

import java.util.Arrays;
import java.util.Optional;

/**
 * Fonte unica dos 6 desenhos da pentatonica m6 em Am, conforme
 * {@code tabs-kb/pdf/pentam6.pdf} (T 3b 4 5 6: A C D E F#).
 *
 * <p>O desenho extra da folha se chama "Penta m7blues + Penta m6" e combina as
 * notas das duas escalas; por isso a auditoria musical desse shape usa a uniao
 * das duas. Os demais usam somente a pentatonica m6.</p>
 *
 * <p>Os valores de {@code padrao} começam em 29, depois da pentatonica M7. O
 * seletor de escala usa {@code modo=12}.</p>
 */
public enum PentatonicaM6 {

    SHAPE_1("29", GeradorDeTablatura.MODO_PENTATONICA_M6_A, "Shape 1"),
    SHAPE_2("30", GeradorDeTablatura.MODO_PENTATONICA_M6_A_SHAPE2, "Shape 2"),
    SHAPE_3("31", GeradorDeTablatura.MODO_PENTATONICA_M6_A_SHAPE3, "Shape 3"),
    SHAPE_4("32", GeradorDeTablatura.MODO_PENTATONICA_M6_A_SHAPE4, "Shape 4"),
    SHAPE_5("33", GeradorDeTablatura.MODO_PENTATONICA_M6_A_SHAPE5, "Shape 5"),
    EXTRA("34", GeradorDeTablatura.MODO_PENTATONICA_M6_A_EXTRA, "Penta blues + 6");

    /** Opcao do seletor de modo de estudo. */
    public static final String MODO_OPCAO = "12";
    /** Distancia em semitons da tonica ate o parente (A -> G, setima menor). */
    public static final int SALTO_PARENTE = 10;
    /** Intervalos impressos no PDF de referencia. */
    public static final String INTERVALOS = "T 3b 4 5 6";

    private final String padraoOpcao;
    private final String modoTablatura;
    private final String rotulo;

    PentatonicaM6(String padraoOpcao, String modoTablatura, String rotulo) {
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

    public static Optional<PentatonicaM6> porPadrao(String padraoOpcao) {
        return Arrays.stream(values())
                .filter(shape -> shape.padraoOpcao.equals(padraoOpcao))
                .findFirst();
    }

    public static Optional<PentatonicaM6> porModoTablatura(String modoTablatura) {
        return Arrays.stream(values())
                .filter(shape -> shape.modoTablatura.equals(modoTablatura))
                .findFirst();
    }
}
