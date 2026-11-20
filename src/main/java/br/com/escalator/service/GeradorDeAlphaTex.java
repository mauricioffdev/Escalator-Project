package br.com.escalator.service;

import br.com.escalator.model.Armadura;
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
 *  <li>A ordem melódica vem de {@link GeradorDeTablatura#gerarNotas}, que devolve a
 *       <b>subida</b> do grave (corda 6) ao agudo (corda 1) seguida do <b>retorno</b>
 *       pelo caminho inverso (corda 1 de volta à corda 6). O retorno repete a última
 *       nota da subida e termina na primeira nota da escala, que é repetida quando o
 *       player faz loop.</li>
 *  <li>O estudo fica em <b>3/4</b> com <b>6 tercinas de colcheia</b> por volta
 *       (1 por corda, 3 por compasso): compassos 1 e 2 = subida (cordas 6 a 1);
 *       compassos 3 e 4 = retorno (cordas 1 a 6). {@code :8 { tu 3 }} define a
 *       tercina; {@code |} separa os compassos.</li>
 *  <li>Além do shape "3 notas por corda" (padrão), a classe renderiza as
 *       pentatônicas m7 (shapes 1 a 5 e extra) e blues (shapes 1 a 5 e extra). Os
 *       shapes m7 1 a 5 usam 2 notas por corda em <b>semicolcheias</b>
 *       ({@code .16}, 12 por compasso 3/4); o extra usa 3 notas por corda,
 *       duas tercinas de colcheia por compasso 2/4 ({@code :8 { tu 3 }}).
 *       A blues usa semicolcheias no compasso 4/4: 14 notas + {@code r.8} nos
 *       shapes 1, 4 e 5; 15 notas + {@code r.16} nos shapes 2 e 3 em 4/4. A
 *       posicao aberta usa a mesma formatacao do extra m7: duas tercinas de
 *       colcheia por compasso 2/4 ({@code :8 { tu 3 }}), fechando 6 compassos.
 *       Nenhuma dinâmica é adicionada.</li>
 *  <li>{@code \instrument 29} seleciona o preset GM de guitarra com overdrive
 *       leve para a reprodução (o padrão do AlphaTex é guitarra acústica).</li>
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
    /** Compasso 4/4 em semicolcheias: as 14 notas do shape 1 da pentatônica blues. */
    private static final int NOTAS_POR_COMPASSO_BLUES = 14;
    /** Shapes 2 e 3 blues tem 15 notas (blue note nas cordas E grave, G e E aguda). */
    private static final int NOTAS_POR_COMPASSO_BLUES_15 = 15;
    /** GM 29 (0-based): guitarra com overdrive leve no lugar da acústica padrão. */
    private static final String INSTRUMENTO = "\\instrument 29\n";
    /** Duas tercinas de colcheia (6 notas) completam o compasso 2/4 dos shapes extra. */
    private static final int NOTAS_POR_COMPASSO_2_TERCINAS = 6;
    /** Fórmulas de compasso suportadas pelo {@code \ts}. */
    private static final String COMPASSO_2_4 = "2 4";
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

    /** Mantem o comportamento padrao (ciclo das quintas) para chamadas diretas. */
    public static String gerar(String titulo, String tonicaAlvo, String modoNome) {
        return gerar(titulo, tonicaAlvo, modoNome, false);
    }

    /**
     * @param usarBemol ciclo das quartas (bemois) ou das quintas (sustenidos),
     *                  conforme o nome da tonica escolhida pelo usuario
     */
    public static String gerar(String titulo, String tonicaAlvo, String modoNome, boolean usarBemol) {
        List<GeradorDeTablatura.NotaTablatura> notas = GeradorDeTablatura.gerarNotas(tonicaAlvo, modoNome);
        if (notas.isEmpty()) {
            return "";
        }

        ConfiguracaoEstudo config = configuracao(modoNome);

        StringBuilder tex = new StringBuilder();
        tex.append("\\title \"").append(escapar(titulo)).append("\"\n");
        tex.append(TUNING);
        tex.append(INSTRUMENTO);
        tex.append("\\ts ").append(config.compasso()).append('\n');
        tex.append("\\ks ").append(nomeArmaduraPara(tonicaAlvo, modoNome, usarBemol)).append('\n');

        int notasNoCompasso = 0;
        for (int i = 0; i < notas.size(); i++) {
            if (notasNoCompasso == 0) {
                tex.append(config.prefixoCompasso());
            }
            GeradorDeTablatura.NotaTablatura nota = notas.get(i);
            tex.append(nota.traste()).append('.').append(nota.corda()).append(config.sufixoDuracao());
            notasNoCompasso++;

            // A pausa final fecha cada compasso completo (não apenas o último),
            // preservando a pausa do compasso de ida além do de volta (blues).
            if (i == notas.size() - 1) {
                tex.append(config.pausaFinal()).append(" |");
            } else if (notasNoCompasso == config.notasPorCompasso()) {
                tex.append(config.pausaFinal()).append(" |\n");
                notasNoCompasso = 0;
            } else {
                tex.append(' ');
            }
        }
        return tex.toString();
    }

    /**
     * Define as regras de renderização de acordo com o modo solicitado. Na m7,
     * os shapes 1 a 5 usam semicolcheias (12 por compasso 3/4) e o extra usa
     * duas tercinas de colcheia por compasso 2/4. A blues usa semicolcheias em
     * 4/4: nos shapes 1, 4 e 5 há 14 notas e pausa de colcheia; os shapes 2 e 3
     * têm 15 notas e pausa de semicolcheia. A posicao aberta segue o extra m7:
     * 36 notas (ida e volta) em tercina, 6 compassos de 2/4 sem pausa.
     */
    private static ConfiguracaoEstudo configuracao(String modoNome) {
        return switch (modoNome) {
            case GeradorDeTablatura.MODO_PENTATONICA_M7,
                 GeradorDeTablatura.MODO_PENTATONICA_M7_SHAPE2,
                 GeradorDeTablatura.MODO_PENTATONICA_M7_SHAPE3,
                 GeradorDeTablatura.MODO_PENTATONICA_M7_SHAPE4,
                 GeradorDeTablatura.MODO_PENTATONICA_M7_SHAPE5 ->
                    new ConfiguracaoEstudo(null, "", DURACAO_SEMICOLCHEIA, NOTAS_POR_COMPASSO_M7, COMPASSO_3_4, "");
            case GeradorDeTablatura.MODO_PENTATONICA_M7_EXTRA,
                 GeradorDeTablatura.MODO_PENTATONICA_BLUES_EXTRA ->
                    new ConfiguracaoEstudo(null, DURACAO_TERCINA, "", NOTAS_POR_COMPASSO_2_TERCINAS, COMPASSO_2_4, "");
            case GeradorDeTablatura.MODO_PENTATONICA_BLUES,
                  GeradorDeTablatura.MODO_PENTATONICA_BLUES_SHAPE4,
                  GeradorDeTablatura.MODO_PENTATONICA_BLUES_SHAPE5 ->
                    new ConfiguracaoEstudo(null, "", DURACAO_SEMICOLCHEIA, NOTAS_POR_COMPASSO_BLUES, COMPASSO_4_4, " r.8");
            case GeradorDeTablatura.MODO_PENTATONICA_BLUES_SHAPE2,
                 GeradorDeTablatura.MODO_PENTATONICA_BLUES_SHAPE3 ->
                    new ConfiguracaoEstudo(null, "", DURACAO_SEMICOLCHEIA, NOTAS_POR_COMPASSO_BLUES_15, COMPASSO_4_4, " r.16");
            default ->
                    new ConfiguracaoEstudo("Shapes-Tab", DURACAO_TERCINA, "", NOTAS_POR_COMPASSO, COMPASSO_3_4, "");
        };
    }

    /**
     * Escreve a armadura no ciclo harmônico escolhido pelo usuário: quintas com
     * sustenidos (F# e C#, nunca Gb ou Db) e quartas com bemois (Gb e Cb, nunca
     * F# ou B).
     *
     * <p>As pentatônicas e as menores usam a relativa maior; os modos da escala
     * maior usam o parente (ex.: D Dorico usa {@code \ks C}). Nas menores, o
     * nome da armadura mantém o sufixo {@code minor}, mas soletrado no ciclo
     * correto (ex.: {@code \ks Abminor}, não {@code A#minor}).</p>
     */
    private static String nomeArmaduraPara(String tonica, String modoNome, boolean usarBemol) {
        Nota nota = Nota.valueOf(tonica.replace("#", "_SHARP"));

        if (isPentatonica(modoNome)) {
            return Armadura.nomeParente(Nota.getNotaPorValor(nota.getValor() + 3), usarBemol);
        }

        var modo = ModoMaior.porModoTablatura(modoNome);
        if (modo.isPresent() && modo.get().grau() > 0) {
            Nota parente = Nota.getNotaPorValor(nota.getValor() - modo.get().grau());
            return modo.get().relativaMenor()
                    ? Armadura.nomeDaNota(nota, parente, usarBemol) + "minor"
                    : Armadura.nomeParente(parente, usarBemol);
        }

        if (modoNome.startsWith("Menor")) {
            Nota parente = Nota.getNotaPorValor(nota.getValor() + 3);
            return Armadura.nomeDaNota(nota, parente, usarBemol) + "minor";
        }

        return Armadura.nomeDaNota(nota, nota, usarBemol);
    }

    private static boolean isPentatonica(String modoNome) {
        return GeradorDeTablatura.MODO_PENTATONICA_M7.equals(modoNome)
                || GeradorDeTablatura.MODO_PENTATONICA_M7_SHAPE2.equals(modoNome)
                || GeradorDeTablatura.MODO_PENTATONICA_M7_SHAPE3.equals(modoNome)
                || GeradorDeTablatura.MODO_PENTATONICA_M7_SHAPE4.equals(modoNome)
                || GeradorDeTablatura.MODO_PENTATONICA_M7_SHAPE5.equals(modoNome)
                || GeradorDeTablatura.MODO_PENTATONICA_M7_EXTRA.equals(modoNome)
                || GeradorDeTablatura.MODO_PENTATONICA_BLUES.equals(modoNome)
                || GeradorDeTablatura.MODO_PENTATONICA_BLUES_SHAPE2.equals(modoNome)
                || GeradorDeTablatura.MODO_PENTATONICA_BLUES_SHAPE3.equals(modoNome)
                || GeradorDeTablatura.MODO_PENTATONICA_BLUES_SHAPE4.equals(modoNome)
                || GeradorDeTablatura.MODO_PENTATONICA_BLUES_SHAPE5.equals(modoNome)
                || GeradorDeTablatura.MODO_PENTATONICA_BLUES_EXTRA.equals(modoNome);
    }

    private static String escapar(String texto) {
        return texto.replace("\\", "\\\\").replace("\"", "'");
    }
}