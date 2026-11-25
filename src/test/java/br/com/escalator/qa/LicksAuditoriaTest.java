package br.com.escalator.qa;

import br.com.escalator.service.GeradorDeLicks;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Auditoria musical dos licks fixos: compasso, escala, efeitos e títulos. */
class LicksAuditoriaTest {

    private static final int[] CORDAS_ABERTAS = {4, 11, 7, 2, 9, 4}; // E B G D A E
    private static final Set<Integer> PENTA_AM = Set.of(9, 0, 2, 4, 7); // A C D E G

    @Test
    void todosOsCompassosFechamEmQuatroQuartos() {
        for (GeradorDeLicks.Lick lick : GeradorDeLicks.listar()) {
            List<GeradorDeLicks.CompassoDados> compassos = GeradorDeLicks.compassosDe(lick.id());
            for (GeradorDeLicks.CompassoDados compasso : compassos) {
                assertEquals(16.0, compasso.golpes().stream()
                        .mapToDouble(GeradorDeLicks.GolpeDados::unidades)
                        .sum(), 0.001, lick.id() + " - compasso " + compasso.numero());
            }
        }
    }

    @Test
    void titulosDaTabSaoSempreLickN() {
        for (GeradorDeLicks.Lick lick : GeradorDeLicks.listar()) {
            assertTrue(lick.titulo().matches("Lick [123]"), lick.id() + ": " + lick.titulo());
            assertTrue(lick.alphaTex().startsWith("\\title \"" + lick.titulo() + "\"\n"),
                    lick.id() + " - titulo do alphaTex");
        }
    }

    @Test
    void cadaLickFicaNoGrupoDaSuaEscala() {
        assertEquals(1, GeradorDeLicks.listarPorModo(GeradorDeLicks.MODO_MENOR_HARMONICA).size());
        assertEquals(3, GeradorDeLicks.listarPorModo(GeradorDeLicks.MODO_PENTA_M7).size());
        assertEquals(GeradorDeLicks.MODO_MENOR_HARMONICA,
                GeradorDeLicks.buscar(GeradorDeLicks.LICK_FRIGIO_DOMINANTE).modo());
        for (String id : List.of(GeradorDeLicks.LICK_PENTA_BLUESY,
                GeradorDeLicks.LICK_PENTA_ZW, GeradorDeLicks.LICK_PENTA_03)) {
            assertEquals(GeradorDeLicks.MODO_PENTA_M7, GeradorDeLicks.buscar(id).modo());
        }
    }

    @Test
    void notasDaPentatonicaRespeitamAmComPassagemDocumentadaNoLick03() {
        for (String id : List.of(GeradorDeLicks.LICK_PENTA_BLUESY,
                GeradorDeLicks.LICK_PENTA_ZW, GeradorDeLicks.LICK_PENTA_03)) {
            GeradorDeLicks.Lick lick = GeradorDeLicks.buscar(id);
            assertNotNull(lick, id);
            int notas = 0;
            for (GeradorDeLicks.CompassoDados compasso : GeradorDeLicks.compassosDe(id)) {
                for (GeradorDeLicks.GolpeDados golpe : compasso.golpes()) {
                    for (GeradorDeLicks.NotaDados nota : golpe.notas()) {
                        int classe = (CORDAS_ABERTAS[nota.corda() - 1] + nota.traste()) % 12;
                        // O PDF traz G(8) = D# como passagem cromatica pontual no
                        // Lick 03 (X=257,2 da pagina 2), entre G(7) e G(5).
                        boolean passagemDocumentada = GeradorDeLicks.LICK_PENTA_03.equals(id)
                                && nota.corda() == 3 && nota.traste() == 8;
                        assertTrue(PENTA_AM.contains(classe) || passagemDocumentada,
                                () -> id + " tem nota fora da pentatonica: corda "
                                        + nota.corda() + " casa " + nota.traste());
                        notas++;
                    }
                }
            }
            assertTrue(notas > 0, id);
        }
    }

    @Test
    void bluesyTemQuatroGruposDeTercinaEBendFullFinalNaCasa8() {
        GeradorDeLicks.Lick bluesy = GeradorDeLicks.buscar(GeradorDeLicks.LICK_PENTA_BLUESY);
        List<GeradorDeLicks.CompassoDados> compassos = GeradorDeLicks.compassosDe(bluesy.id());
        assertEquals(2, compassos.size());

        GeradorDeLicks.CompassoDados primeiro = compassos.get(0);
        assertEquals("3", primeiro.tuplet());
        assertEquals(12, primeiro.golpes().size());
        for (GeradorDeLicks.GolpeDados golpe : primeiro.golpes()) {
            assertEquals(4.0 / 3.0, golpe.unidades(), 0.001);
            assertEquals(1, golpe.notas().size());
        }
        assertFalse(bluesy.alphaTex().contains(" r "), "Bluesy nao tem pausas");

        GeradorDeLicks.CompassoDados segundo = compassos.get(1);
        assertEquals(1, segundo.golpes().size());
        assertEquals(16.0, segundo.golpes().get(0).unidades(), 0.001);
        assertEquals(1, segundo.golpes().get(0).notas().size());
        GeradorDeLicks.NotaDados nota = segundo.golpes().get(0).notas().get(0);
        assertEquals(2, nota.corda());
        assertEquals(8, nota.traste());
        assertTrue(nota.bendFull());
        assertTrue(bluesy.alphaTex().contains(":8 { tu 3 }"));
        assertTrue(bluesy.alphaTex().contains(":1 8.2{b (0 4)}"));
        assertEquals(5, contar(bluesy.alphaTex(), "{b (0 4)}"));
        assertEquals("Frase bluesy, muito usada no rock clássico e heavy tradicional.",
                bluesy.referencia());
    }

    @Test
    void zwTemDoisCompassosDeTercinaSemPausasENotaLaNaQuartaCorda() {
        GeradorDeLicks.Lick zw = GeradorDeLicks.buscar(GeradorDeLicks.LICK_PENTA_ZW);
        List<GeradorDeLicks.CompassoDados> compassos = GeradorDeLicks.compassosDe(zw.id());
        assertEquals(3, compassos.size());
        assertEquals("3", compassos.get(0).tuplet());
        assertEquals("3", compassos.get(1).tuplet());
        assertNull(compassos.get(2).tuplet());
        assertEquals(12, compassos.get(0).golpes().size());
        assertEquals(12, compassos.get(1).golpes().size());
        assertEquals(1, compassos.get(2).golpes().size());
        GeradorDeLicks.NotaDados finalDa = compassos.get(2).golpes().get(0).notas().get(0);
        assertEquals(4, finalDa.corda());
        assertEquals(7, finalDa.traste()); // D7 = Lá
        assertEquals(16.0, compassos.get(2).golpes().get(0).unidades(), 0.001);
        assertFalse(zw.alphaTex().contains(" r "), "ZW nao tem pausas");
        assertTrue(zw.alphaTex().contains(":8 { tu 3 }"));
        assertEquals("Frase estilo ZW (Zakk Wylde) no primeiro shape da penta de Am.",
                zw.referencia());
    }

    @Test
    void lick03TemNotasEFeitosDoPdf() {
        GeradorDeLicks.Lick lick03 = GeradorDeLicks.buscar(GeradorDeLicks.LICK_PENTA_03);
        List<GeradorDeLicks.CompassoDados> compassos = GeradorDeLicks.compassosDe(lick03.id());
        assertEquals(2, compassos.size());
        List<GeradorDeLicks.GolpeDados> golpes = compassos.get(0).golpes();
        assertEquals(14, golpes.size());
        assertEquals(2.0, golpes.get(0).unidades(), 0.001);
        assertEquals(3, golpes.get(0).notas().get(0).corda());
        assertEquals(7, golpes.get(0).notas().get(0).traste());
        assertTrue(golpes.get(0).notas().get(0).bendFull(), "bend full na primeira nota G(7)");
        for (int i = 1; i <= 12; i++) {
            assertEquals(1.0, golpes.get(i).unidades(), 0.001, "golpe " + i);
        }
        assertEquals(2.0, golpes.get(13).unidades(), 0.001);
        assertEquals(2, golpes.get(13).notas().size());
        assertEquals(1, compassos.get(1).golpes().size());
        assertEquals(16.0, compassos.get(1).golpes().get(0).unidades(), 0.001);
        assertEquals(4, compassos.get(1).golpes().get(0).notas().get(0).corda());
        assertEquals(7, compassos.get(1).golpes().get(0).notas().get(0).traste());

        assertTrue(golpes.get(3).notas().get(0).pullOff(), "pull-off em B(8)");
        assertTrue(golpes.get(7).notas().get(0).pullOff(), "pull-off em G(8)");
        assertTrue(golpes.get(8).notas().get(0).pullOff(), "pull-off em G(7)");
        assertFalse(golpes.get(4).notas().get(0).pullOff());
        assertFalse(golpes.get(9).notas().get(0).pullOff());

        String texto = lick03.alphaTex();
        assertTrue(texto.contains("7.3{b (0 4)}"), "bend full na primeira nota G(7)");
        assertFalse(texto.contains("5.2{b (0 4)}"), "nao ha bend em B(5)");
        assertTrue(texto.contains("8.2{h} 5.2"), "pull-off B(8)->B(5)");
        assertTrue(texto.contains("8.3{h} 7.3{h} 5.3"), "pull-offs G(8)->G(7)->G(5)");
        assertTrue(texto.contains("8.3"), "passagem G(8) do PDF");
        assertFalse(texto.contains(" r "), "Lick 03 nao tem pausas");
        assertEquals("", lick03.referencia());
    }

    @Test
    void alphaTexDoLick03UsaAsDuracoesDoModelo() {
        GeradorDeLicks.Lick lick03 = GeradorDeLicks.buscar(GeradorDeLicks.LICK_PENTA_03);
        String alphaTex = lick03.alphaTex();
        String primeiroCompasso = alphaTex.split("\\|")[0];

        // 1 colcheia (2 unidades) + 12 semicolcheias + acorde em colcheia.
        assertTrue(primeiroCompasso.contains(":8 7.3{b (0 4)} :16 5.2"), primeiroCompasso);
        assertTrue(primeiroCompasso.contains("7.4 :8 (5.2 5.3 )"), primeiroCompasso);
        assertEquals(2, contar(primeiroCompasso, ":8 "));
        assertEquals(1, contar(primeiroCompasso, ":16 "));

        int inicioSemicolcheias = primeiroCompasso.indexOf(":16 ") + 4;
        int fimSemicolcheias = primeiroCompasso.indexOf(" :8 (");
        String notas = primeiroCompasso.substring(inicioSemicolcheias, fimSemicolcheias).trim();
        assertEquals(12, notas.split("\\s+").length, notas);
        assertFalse(alphaTex.contains(" r "), "Lick 03 nao tem pausas");
    }

    @Test
    void tabsNaoContemMarcacaoDeBpm() {
        for (GeradorDeLicks.Lick lick : GeradorDeLicks.listar()) {
            for (String texto : List.of(lick.alphaTex(), lick.tablatura(), lick.referencia())) {
                assertFalse(texto.contains("{tempo"), lick.id());
                assertFalse(texto.contains("♩"), lick.id());
                assertFalse(texto.toLowerCase().contains("bpm"), lick.id());
            }
        }
    }

    @Test
    void referenciasNaoExibemRotuloTextosDoExemplo() {
        for (GeradorDeLicks.Lick lick : GeradorDeLicks.listar()) {
            assertFalse(lick.referencia().contains("Textos do exemplo"), lick.id());
        }
        assertFalse(GeradorDeLicks.buscar(GeradorDeLicks.LICK_PENTA_03)
                .referencia().contains("P.M."));
        assertFalse(GeradorDeLicks.buscar(GeradorDeLicks.LICK_PENTA_03)
                .referencia().contains("E-Gt"));
    }

    @Test
    void referenciasSaoDicasSemProveniencia() {
        assertEquals("", GeradorDeLicks.buscar(GeradorDeLicks.LICK_FRIGIO_DOMINANTE).referencia());
        assertEquals("", GeradorDeLicks.buscar(GeradorDeLicks.LICK_PENTA_03).referencia());
        List<String> termosDeProveniencia = List.of("fonte", "arquivo", "Guitar Pro", "faixa",
                "compasso", "PDF", "página");
        for (GeradorDeLicks.Lick lick : GeradorDeLicks.listar()) {
            String referencia = lick.referencia().toLowerCase();
            for (String termo : termosDeProveniencia) {
                assertFalse(referencia.contains(termo.toLowerCase()),
                        lick.id() + " nao deve registrar proveniencia: " + termo);
            }
        }
    }

    @Test
    void valoresDoDropdownSaoUnicosENaoColidemComPadroesExistentes() {
        List<String> valores = GeradorDeLicks.listar().stream()
                .map(GeradorDeLicks.Lick::padraoOpcao)
                .toList();
        assertEquals(4, valores.stream().distinct().count());

        Set<String> existentes = new HashSet<>();
        Arrays.stream(br.com.escalator.service.ModoMaior.values())
                .map(br.com.escalator.service.ModoMaior::padraoOpcao).forEach(existentes::add);
        Arrays.stream(br.com.escalator.service.PentatonicaM7.values())
                .map(br.com.escalator.service.PentatonicaM7::padraoOpcao).forEach(existentes::add);
        Arrays.stream(br.com.escalator.service.PentatonicaM6.values())
                .map(br.com.escalator.service.PentatonicaM6::padraoOpcao).forEach(existentes::add);
        Arrays.stream(br.com.escalator.service.ModoMenorHarmonica.values())
                .map(br.com.escalator.service.ModoMenorHarmonica::padraoOpcao).forEach(existentes::add);
        Arrays.stream(br.com.escalator.service.ModoMenorMelodica.values())
                .map(br.com.escalator.service.ModoMenorMelodica::padraoOpcao).forEach(existentes::add);

        assertTrue(existentes.stream().noneMatch(valores::contains),
                "Valores de licks colidem com padroes existentes");
    }

    private static int contar(String texto, String trecho) {
        int total = 0;
        int indice = 0;
        while ((indice = texto.indexOf(trecho, indice)) != -1) {
            total++;
            indice += trecho.length();
        }
        return total;
    }
}
