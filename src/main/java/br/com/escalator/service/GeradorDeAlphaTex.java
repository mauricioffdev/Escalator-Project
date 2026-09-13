package br.com.escalator.service;

import br.com.escalator.model.Nota;

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
 *  <li>Além do shape "3 notas por corda" (padrão), a classe renderiza duas
 *       pentatônicas: <b>"pentatonica m7"</b> com estritamente 2 notas por
 *       corda em <b>semicolcheias</b> ({@code .16}, 12 por compasso 3/4) e
 *       <b>"pentatonica blues"</b> em <b>semicolcheias</b> no compasso 4/4,
 *       com as 14 notas em 3 grupos completos de 4 e, no último tempo, 2
 *       semicolcheias seguidas de uma pausa de colcheia ({@code r.8}).
 *       Nenhuma delas recebe sinais de dinâmica.</li>
 *  <li>{@code \ts} fixa a fórmula de compasso de cada modo e {@code \ks} a armadura
 *       de clave do tom (acidentes só na armadura, sem sustenidos/bemóis inline
 *       nas notas).</li>
 *  <li>{@code \tuning}, {@code \title} e {@code \subtitle} são metadados que
 *       deixam a partitura com cara de estudo profissional (sem indicação de BPM).</li>
 * </ul>
 */
public class GeradorDeAlphaTex {

    /** Colcheias em tercina: duração padrão de todas as notas do exercício. */
    private static final String DURACAO_TERCINA = ":8 { tu 3 } ";
    /** 9 colcheias em tercina (3 grupos de 3) completam um compasso 3/4. */
    private static final int NOTAS_POR_COMPASSO = 9;
    /** Semicolcheia (16th): duração das notas das pentatônicas. */
    private static final String DURACAO_SEMICOLCHEIA = ".16";
    /** Compasso 3/4 em semicolcheias: 3 grupos de 4 notas (pentatônica m7). */
    private static final int NOTAS_POR_COMPASSO_M7 = 12;
    /** Compasso 4/4 em semicolcheias: as 14 notas da pentatônica blues em uma barra. */
    private static final int NOTAS_POR_COMPASSO_BLUES = 14;
    /** Fórmulas de compasso suportadas pelo {@code \ts}. */
    private static final String COMPASSO_3_4 = "3 4";
    private static final String COMPASSO_4_4 = "4 4";
    /** Tuning padrão da guitarra: lista da corda 1 (E4 aguda) à corda 6 (E2 grave). */
    private static final String TUNING = "\\tuning (E4 B3 G3 D3 A2 E2) { hide }\n";

    /**
     * Regras de renderização de cada escala: legenda do subtítulo, marcador
     *  de duração do início do compasso, sufixo de duração de cada nota,
     *  quantas notas cabem em um compasso, a fórmula de compasso e um possível
     *  encerramento (pausa) da barra.
     */
    private record ConfiguracaoEstudo(String legenda, String prefixoCompasso, String sufixoDuracao,
                                      int notasPorCompasso, String compasso, String pausaFinal) {
    }

    private GeradorDeAlphaTex() {
    }

    public static String gerar(String titulo, String tonicaAlvo, String modoNome) {
        List<GeradorDeTablatura.NotaTablatura> notas = GeradorDeTablatura.gerarNotas(tonicaAlvo, modoNome);
        if (notas.isEmpty()) {
            return "";
        }

        ConfiguracaoEstudo config = configuracao(modoNome);

        StringBuilder tex = new StringBuilder();
        tex.append("\\title \"").append(escapar(titulo)).append("\"\n");
        String subtitulo = subtituloPara(modoNome, config);
        if (subtitulo != null) {
            tex.append("\\subtitle \"").append(subtitulo).append("\"\n");
        }
        tex.append(TUNING);
        tex.append("\\ts ").append(config.compasso()).append('\n');
        tex.append("\\ks ").append(nomeArmaduraPara(tonicaAlvo, modoNome)).append('\n');

        int notasNoCompasso = 0;
        for (int i = 0; i < notas.size(); i++) {
            if (notasNoCompasso == 0) {
                tex.append(config.prefixoCompasso());
            }
            GeradorDeTablatura.NotaTablatura nota = notas.get(i);
            tex.append(nota.traste()).append('.').append(nota.corda()).append(config.sufixoDuracao());
            notasNoCompasso++;

            if (i < notas.size() - 1) {
                if (notasNoCompasso == config.notasPorCompasso()) {
                    tex.append(" |\n");
                    notasNoCompasso = 0;
                } else {
                    tex.append(' ');
                }
            }
        }
        tex.append(config.pausaFinal()).append(" |");
        return tex.toString();
    }

    /**
     * Define as regras de renderização de acordo com o modo solicitado. A
     * pentatônica m7 usa estritamente 2 notas por corda (semicolcheias, 12 por
     * compasso 3/4) e a pentatônica blues usa semicolcheias em 4/4 com as 14
     * notas em 3 grupos completos de 4 mais 2 semicolcheias e uma pausa de
     * colcheia no último tempo. Nenhuma dinâmica é adicionada em qualquer caso.
     */
    private static ConfiguracaoEstudo configuracao(String modoNome) {
        return switch (modoNome) {
            case GeradorDeTablatura.MODO_PENTATONICA_M7 ->
                    new ConfiguracaoEstudo(null, "", DURACAO_SEMICOLCHEIA, NOTAS_POR_COMPASSO_M7, COMPASSO_3_4, "");
            case GeradorDeTablatura.MODO_PENTATONICA_BLUES ->
                    new ConfiguracaoEstudo(null, "", DURACAO_SEMICOLCHEIA, NOTAS_POR_COMPASSO_BLUES, COMPASSO_4_4, " r.8");
            default ->
                    new ConfiguracaoEstudo("3 notas por corda", DURACAO_TERCINA, "", NOTAS_POR_COMPASSO, COMPASSO_3_4, "");
        };
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

    /**
     * As pentatônicas são sempre do relativo menor: a armadura correta é a da
     * relativa maior do tom escolhido (ex.: pentatônica de Am usa {@code \ks C},
     * a armadura de C). Os demais modos usam {@link #nomeArmadura}.
     */
    private static String nomeArmaduraPara(String tonica, String modoNome) {
        if (GeradorDeTablatura.MODO_PENTATONICA_M7.equals(modoNome)
                || GeradorDeTablatura.MODO_PENTATONICA_BLUES.equals(modoNome)) {
            return relativaMaior(tonica);
        }
        return nomeArmadura(tonica, modoNome.startsWith("Menor"));
    }

    private static String relativaMaior(String tonica) {
        Nota menor = Nota.valueOf(tonica.replace("#", "_SHARP"));
        Nota maior = Nota.getNotaPorValor(menor.getValor() + 3);
        return nomeArmadura(maior.toString(), false);
    }

    /**
     * Subtítulo apenas para os modos de shape padronizado (ex.: "3 notas por
     * corda"). As pentatônicas não exibem subtítulo, pois o tom já aparece no
     * título em forma de acorde menor (ex.: "Pentatonica de Am").
     */
    private static String subtituloPara(String modoNome, ConfiguracaoEstudo config) {
        if (GeradorDeTablatura.MODO_PENTATONICA_M7.equals(modoNome)
                || GeradorDeTablatura.MODO_PENTATONICA_BLUES.equals(modoNome)) {
            return null;
        }
        return config.legenda();
    }

    private static String escapar(String texto) {
        return texto.replace("\\", "\\\\").replace("\"", "'");
    }
}