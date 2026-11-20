package br.com.escalator.rag;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfTabIndexTest {

    @Test
    void indexaOsSeteModosDaEscalaMaior() {
        List<TabReference> modos = new PdfTabIndex().porDocumento(PdfTabIndex.DOCUMENTO_MODOS);
        assertEquals(7, modos.size());
        for (TabReference modo : modos) {
            assertEquals(6, modo.cordas().size(), modo.titulo());
            for (TabReference.CordaReferencia corda : modo.cordas()) {
                assertEquals(3, corda.dedos().size(), modo.titulo() + " / " + corda.corda());
            }
        }
    }

    @Test
    void indexaOsSeisBlocosDePentatonicaBlues() {
        List<TabReference> blues = new PdfTabIndex().porDocumento(PdfTabIndex.DOCUMENTO_BLUES);
        assertEquals(6, blues.size());
        for (TabReference bloco : blues) {
            assertEquals(6, bloco.cordas().size(), bloco.titulo());
        }
    }

    @Test
    void referenciaMenorConfereComShapeJaValidado() {
        TabReference referencia = new PdfTabIndex()
                .buscar("Penta Blues Menor - Referência.")
                .orElseThrow();

        List<List<Integer>> dedos = referencia.cordas().stream()
                .map(TabReference.CordaReferencia::dedos)
                .toList();

        // Mesma sequencia de dedos do shape 1 da pentatonica blues em Am,
        // ja implementado em GeradorDeTablatura (E_high -> E_low).
        assertEquals(List.of(
                List.of(1, 4),
                List.of(1, 4),
                List.of(1, 3, 4),
                List.of(1, 3),
                List.of(1, 2, 3),
                List.of(1, 4)
        ), dedos);
        assertTrue(referencia.titulo().contains("Menor"));
    }
}
