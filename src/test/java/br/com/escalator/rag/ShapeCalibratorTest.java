package br.com.escalator.rag;

import br.com.escalator.service.GeradorDeTablatura;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShapeCalibratorTest {

    @Test
    void reconstruiOsShapesBluesJaValidados() {
        PdfTabIndex indice = new PdfTabIndex();
        List<TabReference> blocos = indice.porDocumento(PdfTabIndex.DOCUMENTO_BLUES);

        String[] modos = {
                GeradorDeTablatura.MODO_PENTATONICA_BLUES,
                GeradorDeTablatura.MODO_PENTATONICA_BLUES_SHAPE2,
                GeradorDeTablatura.MODO_PENTATONICA_BLUES_SHAPE3
        };

        for (int i = 0; i < modos.length; i++) {
            List<List<Integer>> esperado = casasDoCodigo(modos[i]);
            List<List<Integer>> calculado =
                    ShapeCalibrator.casasPorCorda(blocos.get(i), i + 1);
            assertEquals(esperado, calculado,
                    "Bloco " + (i + 1) + " (" + modos[i] + ")");
        }
    }

    /**
     * Primeiras casas de cada corda na subida gerada pelo codigo validado.
     * O indice 0 e a corda 1 (E aguda), alinhado com a ordem das linhas do PDF.
     */
    private static List<List<Integer>> casasDoCodigo(String modo) {
        List<GeradorDeTablatura.NotaTablatura> notas =
                GeradorDeTablatura.gerarNotas("A", modo);
        List<List<Integer>> porCorda = new ArrayList<>();
        for (int corda = 1; corda <= 6; corda++) {
            porCorda.add(new ArrayList<>());
        }
        for (GeradorDeTablatura.NotaTablatura nota
                : notas.subList(0, notas.size() / 2)) {
            porCorda.get(nota.corda() - 1).add(nota.traste());
        }
        return porCorda.stream().map(List::copyOf).toList();
    }
}
