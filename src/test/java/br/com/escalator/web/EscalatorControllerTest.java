package br.com.escalator.web;

import br.com.escalator.service.ModoMenorHarmonica;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/** Auditoria da selecao de padrao recebida pelo controller (URLs e formulario). */
class EscalatorControllerTest {

    private final EscalatorController controller = new EscalatorController(new EscalatorWebService());

    @Test
    void modo5ComShape1LegadoUsaEolio7DoPdf() {
        ExtendedModelMap model = new ExtendedModelMap();

        controller.index("5", "Am", "3", "", true, model);

        assertEquals("16", model.getAttribute("padraoSelecionado"));
        assertNull(model.getAttribute("erro"));
        ResultadoEstudo resultado = (ResultadoEstudo) model.getAttribute("resultado");
        assertNotNull(resultado);
        assertEquals(ModoMenorHarmonica.EOLIO_7.rotulo(), resultado.nomePadrao());
    }

    @Test
    void modo5ComPadraoDeOutraFamiliaNaoGeraErro() {
        for (String padrao : List.of("4", "8", "9", "15")) {
            ExtendedModelMap model = new ExtendedModelMap();

            controller.index("5", "Am", padrao, "", true, model);

            assertEquals("16", model.getAttribute("padraoSelecionado"), padrao);
            assertNull(model.getAttribute("erro"), padrao);
            assertNotNull(model.getAttribute("resultado"), padrao);
        }
    }

    @Test
    void sequenciasComunsContinuamDisponiveisNaMenorHarmonica() {
        for (String padrao : List.of("1", "2")) {
            ExtendedModelMap model = new ExtendedModelMap();

            controller.index("5", "Am", padrao, "", true, model);

            assertEquals(padrao, model.getAttribute("padraoSelecionado"));
            assertNull(model.getAttribute("erro"), padrao);
            assertNotNull(model.getAttribute("resultado"), padrao);
        }
    }

    @Test
    void shapeDaMenorForaDoModo5MostraMensagemEspecifica() {
        ExtendedModelMap model = new ExtendedModelMap();

        controller.index("1", "Am/C", "17", "", true, model);

        assertEquals("Este shape esta disponivel apenas para a menor harmonica.",
                model.getAttribute("erro"));
        assertNull(model.getAttribute("resultado"));
    }

    @Test
    void gregosEPentatonicasNaoSaoAlterados() {
        ExtendedModelMap maior = new ExtendedModelMap();
        controller.index("1", "Am/C", "9", "", true, maior);
        assertNull(maior.getAttribute("erro"));
        assertEquals("9", maior.getAttribute("padraoSelecionado"));
        assertNotNull(maior.getAttribute("resultado"));

        ExtendedModelMap pentatonica = new ExtendedModelMap();
        controller.index("3", "Am/C", "4", "", true, pentatonica);
        assertNull(pentatonica.getAttribute("erro"));
        assertEquals("4", pentatonica.getAttribute("padraoSelecionado"));
        assertEquals("Shape 2",
                ((ResultadoEstudo) pentatonica.getAttribute("resultado")).nomePadrao());
    }
}
