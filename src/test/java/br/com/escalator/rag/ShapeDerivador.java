package br.com.escalator.rag;

import java.util.ArrayList;
import java.util.List;

/**
 * Deriva as casas dos shapes de pentatonica blues que ainda nao estavam no
 * core a partir dos PDFs de referencia.
 *
 * <p>A regra e a mesma ja validada nos shapes 1 a 3: cada numero impresso no
 * diagrama e uma posicao dentro da caixa e a casa e {@code base + numero - 1}.
 * As bases dos shapes 4 e 5 vem dos shapes m7 correspondentes, validados no
 * core. A posicao aberta de 3 oitavas reaproveita o shape extra m7 validado e
 * acrescenta apenas a blue note que o PDF indica na corda E aguda. O autor
 * confirmou que o primeiro numero impresso nessa corda (casa 10) nao e nota,
 * entao a corda 1 da posicao aberta vai de 11 a 15.</p>
 *
 * <p>Todas as listas estao na ordem E aguda, B, G, D, A, E grave.</p>
 */
public final class ShapeDerivador {

    /** Referencia de A menor blues: tonica A(9), b5 D#(3). */
    private static final int TONICA_REFERENCIA = 9;
    private static final int BLUE_NOTE = (TONICA_REFERENCIA + 6) % 12;
    /** Casa impressa na E aguda que o autor confirmou nao ser uma nota tocada. */
    private static final int CASA_AGUDA_NAO_TOCADA = 10;

    /** Alturas das cordas soltas na ordem E aguda, B, G, D, A, E grave. */
    private static final int[] CORDAS_SOLTAS = {4, 11, 7, 2, 9, 4};

    /**
     * Casas do shape extra m7 validado no core (E aguda -> E grave). Sao usadas
     * como base da posicao aberta de 3 oitavas; o PDF so acrescenta a blue note.
     * O 10 da aguda permanece na base para posicionar a blue note 11 e e
     * removido da nota final em {@link #bluesAberta()}.
     */
    private static final int[][] BASE_ABERTA_M7 = {
            {10, 12, 15},
            {10, 13, 15},
            {7, 9, 12},
            {7, 10, 12},
            {5, 7, 10},
            {5, 8, 10}
    };

    private final PdfTabIndex indice;

    public ShapeDerivador(PdfTabIndex indice) {
        this.indice = indice;
    }

    /** Quantidade de numeros impressos por corda, na ordem E aguda -> E grave. */
    private static final List<Integer> DIGITOS_SHAPE_4 = List.of(2, 3, 2, 3, 2, 2);
    private static final List<Integer> DIGITOS_SHAPE_5 = List.of(2, 3, 2, 2, 3, 2);
    private static final List<Integer> DIGITOS_ABERTA = List.of(4, 3, 3, 3, 3, 3);

    public List<List<Integer>> bluesShape4() {
        return ShapeCalibrator.casasPorCorda(bloco(3, DIGITOS_SHAPE_4), 4);
    }

    public List<List<Integer>> bluesShape5() {
        return ShapeCalibrator.casasPorCorda(bloco(4, DIGITOS_SHAPE_5), 5);
    }

    /**
     * Posicao aberta de 3 oitavas: assume o shape extra m7 validado e insere a
     * blue note nas cordas em que o PDF imprime um numero a mais que a base.
     */
    public List<List<Integer>> bluesAberta() {
        TabReference bloco = bloco(5, DIGITOS_ABERTA);
        List<List<Integer>> casas = new ArrayList<>();
        for (int corda = 0; corda < BASE_ABERTA_M7.length; corda++) {
            int numerosNoPdf = bloco.cordas().get(corda).dedos().size();
            List<Integer> base = List.of(BASE_ABERTA_M7[corda][0],
                    BASE_ABERTA_M7[corda][1], BASE_ABERTA_M7[corda][2]);
            if (numerosNoPdf == base.size()) {
                casas.add(base);
                continue;
            }
            if (numerosNoPdf != base.size() + 1) {
                throw new IllegalStateException("Quantidade de notas inesperada na corda "
                        + corda + " da posicao aberta: " + numerosNoPdf);
            }
            int casaBlue = proximaCasaDaBlueNote(corda, base);
            List<Integer> comBlue = new ArrayList<>(base);
            int posicao = 0;
            while (posicao < comBlue.size() && comBlue.get(posicao) < casaBlue) {
                posicao++;
            }
            comBlue.add(posicao, casaBlue);
            casas.add(List.copyOf(comBlue));
        }
        // Correcao do autor sobre a leitura do PDF: a casa 10 impressa na E
        // aguda nao e tocada. O topo da subida e a casa 15, como no m7 extra.
        long casasNaoTocadas = casas.get(0).stream()
                .filter(casa -> casa == CASA_AGUDA_NAO_TOCADA)
                .count();
        if (casasNaoTocadas != 1) {
            throw new IllegalStateException("A casa " + CASA_AGUDA_NAO_TOCADA
                    + " deveria aparecer uma unica vez na E aguda da posicao aberta");
        }
        casas.set(0, casas.get(0).stream()
                .filter(casa -> casa != CASA_AGUDA_NAO_TOCADA)
                .toList());
        return List.copyOf(casas);
    }

    /**
     * Localiza o bloco pelo titulo quando ele existe; para os blocos sem titulo
     * impresso usa a posicao, mas exige que a assinatura de digitos confira.
     * Assim uma reordenacao da folha falha alto, em vez de gerar casas erradas.
     */
    private TabReference bloco(int indiceBloco, List<Integer> digitosEsperados) {
        List<TabReference> blocos = indice.porDocumento(PdfTabIndex.DOCUMENTO_BLUES);
        if (indiceBloco >= blocos.size()) {
            throw new IllegalStateException("Bloco de blues nao indexado: " + indiceBloco);
        }
        TabReference bloco = blocos.get(indiceBloco);
        List<Integer> digitos = bloco.cordas().stream()
                .map(corda -> (long) corda.dedos().size())
                .map(Long::intValue)
                .toList();
        if (!digitos.equals(digitosEsperados)) {
            throw new IllegalStateException("Assinatura de digitos mudou em '" + bloco.titulo()
                    + "': esperado " + digitosEsperados + ", encontrado " + digitos);
        }
        return bloco;
    }

    /** Menor casa acima da primeira nota da corda cuja altura seja a b5. */
    private static int proximaCasaDaBlueNote(int corda, List<Integer> base) {
        int primeira = base.get(0);
        int casa = (BLUE_NOTE - CORDAS_SOLTAS[corda] + 12) % 12;
        while (casa < primeira) {
            casa += 12;
        }
        return casa;
    }
}
