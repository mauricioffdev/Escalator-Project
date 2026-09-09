package br.com.escalator.service;

import java.util.List;

/**
 * Converte a lista de notas (corda/traste) calculada por {@link GeradorDeTablatura}
 * para a sintaxe AlphaTex, consumida pela biblioteca JavaScript AlphaTab
 * (https://www.alphatab.net/).
 *
 * <p>Resumo da conversão (a parte mais delicada):
 * <ul>
* <li>Cada nota é descrita como <b>&lt;traste&gt;.&lt;corda&gt;</b>, ex.: {@code 9.4} =
     *       traste 9 na corda 4. As cordas seguem a numeração AlphaTab/cifra oficial:
 *       <b>1 = E aguda</b> ... <b>6 = E grave</b> (oposto do desenho ASCII).</li>
 *  <li>A ordem melódica vem de {@link GeradorDeTablatura#gerarNotas}, que já devolve
 *       as notas do grave (corda 6) ao agudo (corda 1), ou seja, o shape
 *       "3 notas por corda" ascendente que o usuário vê no ASCII.</li>
 *  <li>O estudo fica em <b>3/4</b> com <b>6 tercinas de colcheia</b> (1 por corda,
 *       3 por compasso): compasso 1 = cordas 6, 5 e 4; compasso 2 = cordas 3, 2 e 1.
 *       {@code :8 { tu 3 }} define a tercina; {@code |} separa os compassos.</li>
 *  <li>{@code \ts} fixa a fórmula 3/4 e {@code \ks} a armadura de clave do tom
 *       (acidentes só na armadura, sem sustenidos/bemóis inline nas notas).</li>
 *  <li>{@code \tuning}, {@code \title} e {@code \subtitle} são metadados que
 *       deixam a partitura com cara de estudo profissional (sem indicação de BPM).</li>
 * </ul>
 */
public class GeradorDeAlphaTex {

    /** Colcheias em tercina: duração padrão de todas as notas do exercício. */
    private static final String DURACAO_TERCINA = ":8 { tu 3 } ";
    /** 9 colcheias em tercina (3 grupos de 3) completam um compasso 3/4. */
    private static final int NOTAS_POR_COMPASSO = 9;
    /** Numerador e denominador da fórmula de compasso (3/4). */
    private static final int COMPASSO_NUMERADOR = 3;
    private static final int COMPASSO_DENOMINADOR = 4;
    /** Tuning padrão da guitarra: lista da corda 1 (E4 aguda) à corda 6 (E2 grave). */
    private static final String TUNING = "\\tuning (E4 B3 G3 D3 A2 E2) { hide }\n";

    private GeradorDeAlphaTex() {
    }

    public static String gerar(String titulo, String tonicaAlvo, String modoNome) {
        List<GeradorDeTablatura.NotaTablatura> notas = GeradorDeTablatura.gerarNotas(tonicaAlvo, modoNome);
        if (notas.isEmpty()) {
            return "";
        }

        StringBuilder tex = new StringBuilder();
        tex.append("\\title \"").append(escapar(titulo)).append("\"\n");
        tex.append("\\subtitle \"3 notas por corda\"\n");
        tex.append(TUNING);
        tex.append("\\ts ").append(COMPASSO_NUMERADOR).append(' ').append(COMPASSO_DENOMINADOR).append('\n');
        tex.append("\\ks ").append(nomeArmadura(tonicaAlvo, modoNome.startsWith("Menor"))).append('\n');

        int notasNoCompasso = 0;
        for (int i = 0; i < notas.size(); i++) {
            if (notasNoCompasso == 0) {
                tex.append(DURACAO_TERCINA);
            }
            GeradorDeTablatura.NotaTablatura nota = notas.get(i);
            tex.append(nota.traste()).append('.').append(nota.corda());
            notasNoCompasso++;

            if (i < notas.size() - 1) {
                if (notasNoCompasso == NOTAS_POR_COMPASSO) {
                    tex.append(" |\n");
                    notasNoCompasso = 0;
                } else {
                    tex.append(' ');
                }
            }
        }
        tex.append(" |");
        return tex.toString();
    }

    /**
     * Converte a tônica (notação com sustenidos do enum {@code Nota}) no nome de
     * armadura aceito pelo {@code \ks} do AlphaTex. Tons enarmônicos sem nome
     * próprio no AlphaTex são "achatados" ({@code D#} &rarr; {@code Eb}, etc.).
     */
    private static String nomeArmadura(String tonica, boolean menor) {
        String tom = switch (tonica) {
            case "D#" -> "Eb";
            case "G#" -> "Ab";
            case "A#" -> "Bb";
            default -> tonica;
        };
        return menor ? tom + "minor" : tom;
    }

    private static String escapar(String texto) {
        return texto.replace("\\", "\\\\").replace("\"", "'");
    }
}