package br.com.escalator.rag;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Auditoria musical dos shapes derivados dos PDFs (referencia A menor blues). */
class ShapeDerivadorTest {

    private static final Set<Integer> ESCALA_BLUES_DE_A = Set.of(9, 0, 2, 3, 4, 7);
    private static final int BLUE_NOTE = 3;
    private static final int[] CORDAS_SOLTAS = {4, 11, 7, 2, 9, 4};

    private final ShapeDerivador derivador = new ShapeDerivador(new PdfTabIndex());

    @Test
    void derivaAsCasasDoShape4() {
        assertEquals(List.of(
                List.of(12, 15),
                List.of(13, 15, 16),
                List.of(12, 14),
                List.of(12, 13, 14),
                List.of(12, 15),
                List.of(12, 15)
        ), derivador.bluesShape4());
    }

    @Test
    void derivaAsCasasDoShape5() {
        assertEquals(List.of(
                List.of(15, 17),
                List.of(15, 16, 17),
                List.of(14, 17),
                List.of(14, 17),
                List.of(15, 17, 18),
                List.of(15, 17)
        ), derivador.bluesShape5());
    }

    @Test
    void derivaAPosicaoAbertaComABlueNoteNaAguda() {
        List<List<Integer>> aberta = derivador.bluesAberta();
        assertEquals(6, aberta.size());
        assertEquals(List.of(11, 12, 15), aberta.get(0),
                "A blue note (D#) entra na casa 11 e o topo da corda E aguda e a casa 15");
        assertEquals(18, aberta.stream().mapToInt(List::size).sum());
    }

    @Test
    void todasAsNotasPertencemAPentatonicaBluesDeA() {
        auditar("Shape 4", derivador.bluesShape4());
        auditar("Shape 5", derivador.bluesShape5());
        auditar("Aberta", derivador.bluesAberta());
    }

    private static void auditar(String nome, List<List<Integer>> casas) {
        boolean temBlueNote = false;
        for (int corda = 0; corda < casas.size(); corda++) {
            List<Integer> notasDaCorda = casas.get(corda);
            int anterior = -1;
            for (int casa : notasDaCorda) {
                assertTrue(casa >= 0 && casa <= 24,
                        nome + "/corda " + (corda + 1) + ": casa fora do braco " + casa);
                assertTrue(casa > anterior,
                        nome + "/corda " + (corda + 1) + ": casas nao crescentes");
                anterior = casa;
                int altura = (CORDAS_SOLTAS[corda] + casa) % 12;
                assertTrue(ESCALA_BLUES_DE_A.contains(altura),
                        nome + "/corda " + (corda + 1) + " casa " + casa
                                + ": nota " + altura + " fora da escala");
                if (altura == BLUE_NOTE) {
                    temBlueNote = true;
                }
            }
        }
        assertTrue(temBlueNote, nome + " nao contem a blue note (b5)");
    }
}
