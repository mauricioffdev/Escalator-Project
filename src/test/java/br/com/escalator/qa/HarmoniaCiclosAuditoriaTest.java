package br.com.escalator.qa;

import br.com.escalator.model.Armadura;
import br.com.escalator.model.Nota;
import br.com.escalator.service.GeradorDeAlphaTex;
import br.com.escalator.web.EscalatorWebService;
import br.com.escalator.web.ResultadoEstudo;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Auditoria da regra harmonica do projeto: o ciclo das quintas e escrito com
 * sustenidos (F# e C#, nunca Gb/Db) e o ciclo das quartas com bemois (Gb e Cb,
 * nunca F#/B). O ciclo e escolhido pelo NOME digitado, nao pela altitude.
 */
class HarmoniaCiclosAuditoriaTest {

    private static final List<String> QUINTAS = List.of(
            "Am/C", "Em/G", "Bm/D", "F#m/A", "C#m/E", "G#m/B", "D#m/F#", "A#m/C#");
    private static final List<String> QUARTAS = List.of(
            "Dm/F", "Gm/Bb", "Cm/Eb", "Fm/Ab", "Bbm/Db", "Ebm/Gb", "Abm/Cb");

    /** Par -> titulo esperado e armadura esperada no modo Lidio. */
    private static final Map<String, String[]> LIDIO = Map.ofEntries(
            Map.entry("Am/C", new String[]{"F Lidio", "C"}),
            Map.entry("Em/G", new String[]{"C Lidio", "G"}),
            Map.entry("Bm/D", new String[]{"G Lidio", "D"}),
            Map.entry("F#m/A", new String[]{"D Lidio", "A"}),
            Map.entry("C#m/E", new String[]{"A Lidio", "E"}),
            Map.entry("G#m/B", new String[]{"E Lidio", "B"}),
            Map.entry("D#m/F#", new String[]{"B Lidio", "F#"}),
            Map.entry("A#m/C#", new String[]{"F# Lidio", "C#"}),
            Map.entry("Dm/F", new String[]{"Bb Lidio", "F"}),
            Map.entry("Gm/Bb", new String[]{"Eb Lidio", "Bb"}),
            Map.entry("Cm/Eb", new String[]{"Ab Lidio", "Eb"}),
            Map.entry("Fm/Ab", new String[]{"Db Lidio", "Ab"}),
            Map.entry("Bbm/Db", new String[]{"Gb Lidio", "Db"}),
            Map.entry("Ebm/Gb", new String[]{"Cb Lidio", "Gb"}),
            Map.entry("Abm/Cb", new String[]{"Fb Lidio", "Cb"}));

    @Test
    void cicloEEscolhidoPeloNomeDigitadoNaoPelaAltitude() {
        for (String par : QUINTAS) {
            assertFalse(Armadura.usarBemol(par.split("/")[1]), par + ": deve usar sustenidos");
        }
        for (String par : QUARTAS) {
            assertTrue(Armadura.usarBemol(par.split("/")[1]), par + ": deve usar bemois");
        }
        // Menor harmonica vem sem o par relativo no dropdown.
        assertTrue(Armadura.usarBemol("Dm"), "Dm tem relativa F (ciclo das quartas)");
        assertTrue(Armadura.usarBemol("Abm"));
        assertFalse(Armadura.usarBemol("G#m"), "G#m tem relativa B (ciclo das quintas)");
        assertFalse(Armadura.usarBemol("A#m"));
    }

    @Test
    void parentesSaoSoletradosConformeOCiclo() {
        Map<Nota, String> sustenidos = Map.of(
                Nota.C, "C", Nota.G, "G", Nota.D, "D", Nota.A, "A",
                Nota.E, "E", Nota.B, "B", Nota.F_SHARP, "F#", Nota.C_SHARP, "C#");
        sustenidos.forEach((nota, nome) ->
                assertEquals(nome, Armadura.nomeParente(nota, false), nota.toString()));

        Map<Nota, String> bemois = Map.of(
                Nota.C, "C", Nota.F, "F", Nota.A_SHARP, "Bb", Nota.D_SHARP, "Eb",
                Nota.G_SHARP, "Ab", Nota.C_SHARP, "Db", Nota.F_SHARP, "Gb", Nota.B, "Cb");
        bemois.forEach((nota, nome) ->
                assertEquals(nome, Armadura.nomeParente(nota, true), nota.toString()));
    }

    @Test
    void nomesDiatonicosRespeitamACenarmoniaDoCampoHarmonico() {
        assertEquals(List.of("F#", "G#", "A#", "B", "C#", "D#", "E#"),
                graus(Nota.F_SHARP, false));
        assertEquals(List.of("C#", "D#", "E#", "F#", "G#", "A#", "B#"),
                graus(Nota.C_SHARP, false));
        assertEquals(List.of("Gb", "Ab", "Bb", "Cb", "Db", "Eb", "F"),
                graus(Nota.F_SHARP, true));
        assertEquals(List.of("Cb", "Db", "Eb", "Fb", "Gb", "Ab", "Bb"),
                graus(Nota.B, true));
    }

    @Test
    void tituloEArmaduraDosSeteModosSeguemOCicloEscolhido() {
        EscalatorWebService service = new EscalatorWebService();
        for (Map.Entry<String, String[]> caso : LIDIO.entrySet()) {
            ResultadoEstudo resultado = service.gerar("8", caso.getKey(), "3");
            assertEquals(caso.getValue()[0], resultado.titulo(), caso.getKey());
            assertTrue(resultado.tablaturaAlphaTex().contains("\\ks " + caso.getValue()[1] + "\n"),
                    caso.getKey() + ": armadura esperada " + caso.getValue()[1]);
        }
    }

    @Test
    void armaduraDasPentatonicasEaDaRelativaMaiorNoCicloCerto() {
        EscalatorWebService service = new EscalatorWebService();
        assertKsApparece(service.gerar("3", "D#m/F#", "3"), "F#");
        assertKsApparece(service.gerar("4", "D#m/F#", "3"), "F#");
        assertKsApparece(service.gerar("3", "Abm/Cb", "3"), "Cb");
        assertKsApparece(service.gerar("4", "Abm/Cb", "3"), "Cb");
        assertKsApparece(service.gerar("3", "Dm/F", "3"), "F");
    }

    @Test
    void armaduraMenorMantemOSufixoMasSoletraNoCicloCerto() {
        EscalatorWebService service = new EscalatorWebService();
        assertKsApparece(service.gerar("2", "Abm/Cb", "3"), "Abminor");
        assertKsApparece(service.gerar("2", "Dm/F", "3"), "Dminor");
        // Ancora validada: Am/C continua com Aminor.
        assertKsApparece(service.gerar("2", "Am/C", "3"), "Aminor");

        // Menor harmonica chega ao servico so com a tonica menor.
        assertKsApparece(service.gerar("5", "D#m", "3"), "D#minor");
        assertKsApparece(service.gerar("5", "Dm", "3"), "Dminor");
        assertEquals("D# Menor Harmônica", service.gerar("5", "D#m", "3").titulo());
    }

    @Test
    void sobrecargaPadraoDoAlphaTexContinuaEmSustenidos() {
        assertTrue(GeradorDeAlphaTex.gerar("t", "G#", "pentatonica m7", true)
                .contains("\\ks Cb\n"));
        assertTrue(GeradorDeAlphaTex.gerar("t", "G#", "pentatonica m7")
                .contains("\\ks B\n"));
    }

    @Test
    void titulosGrausETriadesSaoDiatonicosMesmoEmCbECSharp() {
        EscalatorWebService service = new EscalatorWebService();

        ResultadoEstudo cb = service.gerar("1", "Abm/Cb", "2");
        assertEquals("Cb Maior", cb.titulo());
        assertEquals("[Cb, Db, Eb, Fb, Gb, Ab, Bb]", cb.notasFormatadas());
        assertTrue(cb.linhasSaida().get(0).startsWith("Cb Maior: Cb - Eb - Gb"),
                cb.linhasSaida().toString());
        assertKsApparece(service.gerar("1", "Abm/Cb", "3"), "Cb");

        ResultadoEstudo sustenidos = service.gerar("1", "A#m/C#", "2");
        assertEquals("C# Maior", sustenidos.titulo());
        assertEquals("[C#, D#, E#, F#, G#, A#, B#]", sustenidos.notasFormatadas());
        assertTrue(sustenidos.linhasSaida().get(0).startsWith("C# Maior: C# - E# - G#"));
        assertKsApparece(service.gerar("1", "A#m/C#", "3"), "C#");
    }

    @Test
    void setimaMaiorDaMenorHarmonicaFicaNoCicloCerto() {
        EscalatorWebService service = new EscalatorWebService();
        assertEquals("[D, E, F, G, A, Bb, C#]", service.gerar("5", "Dm", "1").notasFormatadas());
        assertEquals("[Ab, Bb, Cb, Db, Eb, Fb, G]", service.gerar("5", "Abm", "1").notasFormatadas());
        assertEquals("[F#, G#, A, B, C#, D, E#]", service.gerar("5", "F#m", "1").notasFormatadas());
        assertEquals("[D#, E#, F#, G#, A#, B, D]", service.gerar("5", "D#m", "1").notasFormatadas());
    }

    @Test
    void cicloFuncionaComEntradaEmCaixaAltaSemConfundirBNatural() {
        EscalatorWebService service = new EscalatorWebService();
        assertKsApparece(service.gerar("1", "BBM/DB", "3"), "Db");
        assertKsApparece(service.gerar("1", "D#M/F#", "3"), "F#");
        assertFalse(Armadura.usarBemol("B"), "B natural pertence ao ciclo das quintas");
        assertTrue(Armadura.usarBemol("CB"), "Cb pertence ao ciclo das quartas");
    }

    private static void assertKsApparece(ResultadoEstudo resultado, String armadura) {
        assertTrue(resultado.tablaturaAlphaTex().contains("\\ks " + armadura + "\n"),
                "armadura " + armadura + " ausente em: " + resultado.tablaturaAlphaTex());
    }

    private static List<String> graus(Nota parente, boolean usarBemol) {
        return java.util.stream.IntStream.range(0, 7)
                .mapToObj(grau -> Armadura.nomeDoGrau(parente, grau, usarBemol))
                .toList();
    }
}
