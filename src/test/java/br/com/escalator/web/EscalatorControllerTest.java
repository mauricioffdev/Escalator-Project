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

    @Test
    void modos11E12SempreRecebemUmShapeDaPropriaFamilia() {
        ExtendedModelMap m7 = new ExtendedModelMap();
        controller.index("11", "Am/C", "1", "", true, m7);
        assertEquals("23", m7.getAttribute("padraoSelecionado"));
        assertNull(m7.getAttribute("erro"));
        assertNotNull(m7.getAttribute("resultado"));

        ExtendedModelMap m6 = new ExtendedModelMap();
        controller.index("12", "Am/C", "8", "", true, m6);
        assertEquals("29", m6.getAttribute("padraoSelecionado"));
        assertNull(m6.getAttribute("erro"));
        assertNotNull(m6.getAttribute("resultado"));
    }

    @Test
    void controllerDisponibilizaOsDoisGruposNovosNoTemplate() {
        ExtendedModelMap model = new ExtendedModelMap();
        controller.index("11", "Am/C", "23", "", false, model);

        assertNotNull(model.getAttribute("modosPentatonicaM7"));
        assertNotNull(model.getAttribute("modosPentatonicaM6"));
        assertEquals(6, ((Object[]) model.getAttribute("modosPentatonicaM7")).length);
        assertEquals(6, ((Object[]) model.getAttribute("modosPentatonicaM6")).length);
    }

    @Test
    void shapeNovoForaDoSeuModoMostraMensagemEspecifica() {
        ExtendedModelMap model = new ExtendedModelMap();
        controller.index("3", "Am/C", "24", "", true, model);

        assertEquals("Este shape esta disponivel apenas para a pentatonica M7.",
                model.getAttribute("erro"));
        assertNull(model.getAttribute("resultado"));
    }

    @Test
    void modo13PreservaSequenciasETriadesERedirecionaPadroesForaDaFamilia() {
        ExtendedModelMap sequencia = new ExtendedModelMap();
        controller.index("13", "Am", "1", "", true, sequencia);
        assertEquals("1", sequencia.getAttribute("padraoSelecionado"));
        assertNull(sequencia.getAttribute("erro"));
        assertEquals("Sequencia de 3 Notas",
                ((ResultadoEstudo) sequencia.getAttribute("resultado")).nomePadrao());

        ExtendedModelMap triades = new ExtendedModelMap();
        controller.index("13", "Am", "2", "", true, triades);
        assertEquals("2", triades.getAttribute("padraoSelecionado"));
        assertNull(triades.getAttribute("erro"));
        assertEquals("Sequencia de Triades",
                ((ResultadoEstudo) triades.getAttribute("resultado")).nomePadrao());

        ExtendedModelMap invalido = new ExtendedModelMap();
        controller.index("13", "Am", "23", "", true, invalido);
        assertEquals("35", invalido.getAttribute("padraoSelecionado"));
        assertNull(invalido.getAttribute("erro"));
        assertNotNull(invalido.getAttribute("resultado"));

        @SuppressWarnings("unchecked")
        List<EscalatorController.TonicaOpcao> tonicas =
                (List<EscalatorController.TonicaOpcao>) sequencia.getAttribute("tonicas");
        assertEquals("Am", tonicas.get(0).valor());
    }

    @Test
    void controllerDisponibilizaGrupoDaMenorMelodicaNoTemplate() {
        ExtendedModelMap model = new ExtendedModelMap();
        controller.index("13", "Am", "35", "", false, model);

        assertNotNull(model.getAttribute("modosMenorMelodica"));
        assertEquals(7, ((Object[]) model.getAttribute("modosMenorMelodica")).length);
        assertEquals("Tônica (menor)", model.getAttribute("rotuloTonica"));
    }

    @Test
    void shapeDaMenorMelodicaForaDoModoMostraMensagemEspecifica() {
        ExtendedModelMap model = new ExtendedModelMap();
        controller.index("1", "Am/C", "35", "", true, model);

        assertEquals("Este shape esta disponivel apenas para a menor melódica.",
                model.getAttribute("erro"));
        assertNull(model.getAttribute("resultado"));
    }

    @Test
    void licksSaoSelecionaveisPeloValorDoDropdownPadrao() {
        List<String> padroes = List.of("42", "43", "44", "45");
        List<String> modos = List.of("5", "3", "3", "3");
        List<String> referencias = List.of("",
                "Frase bluesy, muito usada no rock clássico e heavy tradicional.",
                "Frase estilo ZW (Zakk Wylde) no primeiro shape da penta de Am.", "");
        for (int i = 0; i < padroes.size(); i++) {
            String padrao = padroes.get(i);
            ExtendedModelMap model = new ExtendedModelMap();

            controller.index("1", "Am/C", padrao, "", true, model);

            assertEquals(padrao, model.getAttribute("padraoSelecionado"), padrao);
            assertEquals(modos.get(i), model.getAttribute("modoSelecionado"), padrao);
            assertEquals("5".equals(modos.get(i)) ? "Tônica (menor)" : "Tônica (menor/maior)",
                    model.getAttribute("rotuloTonica"), padrao);
            assertEquals(3, ((List<?>) model.getAttribute("licksPentaM7")).size());
            assertEquals(1, ((List<?>) model.getAttribute("licksMenorHarmonica")).size());
            assertNull(model.getAttribute("erro"), padrao);
            ResultadoEstudo resultado = (ResultadoEstudo) model.getAttribute("resultado");
            assertNotNull(resultado, padrao);
            assertEquals(referencias.get(i), resultado.referencia(), padrao);
        }
    }

    @Test
    void urlsLegadasComParametroLickContinuamFuncionando() {
        List<String> licks = List.of("frigio-dominante", "penta-m7-bluesy",
                "penta-m7-zw", "penta-m7-03");
        List<String> padroes = List.of("42", "43", "44", "45");
        List<String> titulos = List.of("Lick 1", "Lick 1", "Lick 2", "Lick 3");
        List<String> modos = List.of("5", "3", "3", "3");
        List<String> referencias = List.of("",
                "Frase bluesy, muito usada no rock clássico e heavy tradicional.",
                "Frase estilo ZW (Zakk Wylde) no primeiro shape da penta de Am.", "");
        for (int i = 0; i < licks.size(); i++) {
            String lick = licks.get(i);
            ExtendedModelMap model = new ExtendedModelMap();

            controller.index("1", "Am/C", "1", lick, true, model);

            assertNull(model.getAttribute("erro"), lick);
            assertEquals(padroes.get(i), model.getAttribute("padraoSelecionado"), lick);
            assertEquals(modos.get(i), model.getAttribute("modoSelecionado"), lick);
            ResultadoEstudo resultado = (ResultadoEstudo) model.getAttribute("resultado");
            assertNotNull(resultado, lick);
            assertEquals(titulos.get(i), resultado.titulo(), lick);
            assertEquals(referencias.get(i), resultado.referencia(), lick);
            assertEquals(lick, model.getAttribute("lickSelecionado"));
        }
    }

    @Test
    void parametroLickInvalidoMostraErroEmVezDeGerarEstudoComum() {
        ExtendedModelMap model = new ExtendedModelMap();

        controller.index("1", "Am/C", "1", "nao-existe", true, model);

        assertEquals("Lick invalido. Selecione uma opcao disponivel.", model.getAttribute("erro"));
        assertNull(model.getAttribute("resultado"));
    }
}
