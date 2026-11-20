package br.com.escalator.model;

import java.util.List;
import java.util.Locale;

/**
 * Regra de harmonia do projeto: as escalas do ciclo das quintas sao escritas
 * com sustenidos (C G D A E B F# C#) e as do ciclo das quartas com bemois
 * (C F Bb Eb Ab Db Gb Cb).
 *
 * <p>A escolha do ciclo vem do <b>nome digitado pelo usuario</b>, nunca apenas
 * da altura (pitch class): Db e C#, Gb e F#, Cb e B tem a mesma altitude, mas
 * pertencem a ciclos diferentes. A partir da tonica parente e do grau, todos
 * os nomes diatonicos sao derivados pela letra e pelo acidente necessario.</p>
 */
public final class Armadura {

    private static final List<String> LETRAS = List.of("C", "D", "E", "F", "G", "A", "B");
    /** Intervalos da escala maior; o indice tambem da a altitude natural de cada letra. */
    private static final List<Integer> INTERVALOS_MAIOR = List.of(0, 2, 4, 5, 7, 9, 11);
    /** Letra da escala para cada altitude quando o ciclo e de sustenidos. */
    private static final List<Integer> LETRA_SUSTENIDO = List.of(0, 0, 1, 1, 2, 3, 3, 4, 4, 5, 5, 6);
    /** Letra da escala para cada altitude quando o ciclo e de bemois. */
    private static final List<Integer> LETRA_BEMOL = List.of(0, 1, 1, 2, 3, 3, 4, 4, 5, 5, 6, 0);
    /** Nomes cromaticos convencionais para notas de fora do campo harmonico. */
    private static final List<String> CONVENCAO_SUSTENIDO =
            List.of("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B");
    private static final List<String> CONVENCAO_BEMOL =
            List.of("C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "Cb");

    private Armadura() {
    }

    /**
     * Determina o ciclo pelo nome escrito, nao pela altitude.
     *
     * <p>Para tonicas maiores: F pertence ao ciclo das quartas (sua escala tem
     * Bb) e qualquer nome terminado em "b" tambem; os demais (inclusive F# e
     * C#) pertencem ao ciclo das quintas. Para tonicas menores, a comparacao e
     * feita com a relativa maior (tonica + 3 semitons): Dm, Gm, Cm e Fm usam
     * bemois, enquanto Am, Em, Bm e as menores com "#" usam sustenidos.</p>
     *
     * <p>Um "B" unico e o B natural (ciclo das quintas); "Bb", "GB" ou "CB"
     * sao bemois. Isso tambem aceita a entrada em caixa alta usada em GETs.</p>
     */
    public static boolean usarBemol(String nomeTonica) {
        String nome = nomeTonica == null ? "" : nomeTonica.trim();
        boolean menor = nome.endsWith("m") || nome.endsWith("M");
        if (menor) {
            nome = nome.substring(0, nome.length() - 1);
        }
        if (nome.endsWith("#")) {
            return false;
        }
        if (nome.endsWith("b")) {
            return true;
        }
        if (nome.endsWith("B") && nome.length() > 1) {
            return true;
        }
        if (menor) {
            // Relativas maiores de C, D, F e G: Eb, F, Ab e Bb -> ciclo das quartas.
            return switch (nome.toLowerCase(Locale.ROOT)) {
                case "c", "d", "f", "g" -> true;
                default -> false;
            };
        }
        return "F".equalsIgnoreCase(nome);
    }

    /** Nome da tonica parente no ciclo escolhido (ex.: Gb ou F#, Cb ou B). */
    public static String nomeParente(Nota parente, boolean usarBemol) {
        return nomeDoGrau(parente, 0, usarBemol);
    }

    /**
     * Nome diatonico de um grau da escala maior parente.
     *
     * @param parente   tonica da escala maior de referencia
     * @param grau      indice 0 a 6 (0 = tonica, 5 = 6a grau, etc.)
     * @param usarBemol ciclo das quartas ({@code true}) ou das quintas
     */
    public static String nomeDoGrau(Nota parente, int grau, boolean usarBemol) {
        List<Integer> letras = usarBemol ? LETRA_BEMOL : LETRA_SUSTENIDO;
        int indiceLetra = (letras.get(parente.getValor()) + grau) % 7;
        int altura = Math.floorMod(parente.getValor() + INTERVALOS_MAIOR.get(grau), 12);
        int acidente = Math.floorMod(altura - INTERVALOS_MAIOR.get(indiceLetra) + 6, 12) - 6;

        StringBuilder nome = new StringBuilder(LETRAS.get(indiceLetra));
        if (acidente > 0) {
            nome.append("#".repeat(acidente));
        } else if (acidente < 0) {
            nome.append("b".repeat(-acidente));
        }
        return nome.toString();
    }

    /**
     * Soletra uma nota que pertence a escala maior parente, preservando a
     * enarmonia do campo harmonico. Para notas de fora (ex.: a blue note), usa
     * o nome convencional do ciclo.
     */
    public static String nomeDaNota(Nota nota, Nota parente, boolean usarBemol) {
        for (int grau = 0; grau < INTERVALOS_MAIOR.size(); grau++) {
            int altura = Math.floorMod(parente.getValor() + INTERVALOS_MAIOR.get(grau), 12);
            if (altura == nota.getValor()) {
                return nomeDoGrau(parente, grau, usarBemol);
            }
        }
        return (usarBemol ? CONVENCAO_BEMOL : CONVENCAO_SUSTENIDO).get(nota.getValor());
    }
}
