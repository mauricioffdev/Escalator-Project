package br.com.escalator.qa;

import br.com.escalator.service.GeradorDeLicks;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Garante que o JSON salvo em tabs-kb/json continua sendo espelho do catalogo de licks. */
class LicksJsonTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final int[] CORDAS_ABERTAS = {4, 11, 7, 2, 9, 4}; // E B G D A E
    private static final Set<Integer> PENTA_AM = Set.of(9, 0, 2, 4, 7); // A C D E G

    @Test
    void jsonEhEspelhoFielDoCatalogo() throws Exception {
        JsonNode raiz = ler("tabs-kb/json/licks-penta-am-m7.json");
        assertEquals(1, raiz.get("versao").asInt());
        JsonNode licks = raiz.get("licks");
        assertEquals(GeradorDeLicks.listar().size(), licks.size());

        List<GeradorDeLicks.Lick> catalogo = GeradorDeLicks.listar();
        for (int i = 0; i < catalogo.size(); i++) {
            GeradorDeLicks.Lick lick = catalogo.get(i);
            JsonNode no = licks.get(i);
            assertEquals(lick.id(), no.get("id").asText());
            assertEquals(lick.padraoOpcao(), no.get("padraoOpcao").asText());
            assertEquals(lick.modo(), no.get("modo").asText());
            assertEquals(lick.titulo(), no.get("titulo").asText());
            assertEquals(lick.notasFormatadas(), no.get("notasFormatadas").asText());
            assertEquals(lick.nomePadrao(), no.get("nomePadrao").asText());
            assertEquals(lick.referencia(), no.get("referencia").asText());
            assertEquals(lick.alphaTex(), no.get("alphaTex").asText());
            // System.lineSeparator() varia entre SO; normaliza para comparar o conteudo.
            assertEquals(lick.tablatura().replace("\r\n", "\n"),
                    no.get("tablatura").asText().replace("\r\n", "\n"));
        }
    }

    @Test
    void compassosDoJsonSaoEspelhoDasListasDoCatalogo() throws Exception {
        JsonNode licks = ler("tabs-kb/json/licks-penta-am-m7.json").get("licks");
        for (JsonNode lick : licks) {
            String id = lick.get("id").asText();
            List<GeradorDeLicks.CompassoDados> esperados = GeradorDeLicks.compassosDe(id);
            JsonNode compassos = lick.get("compassos");
            assertEquals(esperados.size(), compassos.size(), id + " - quantidade de compassos");

            for (int c = 0; c < esperados.size(); c++) {
                GeradorDeLicks.CompassoDados esperado = esperados.get(c);
                JsonNode compasso = compassos.get(c);
                assertEquals(esperado.numero(), compasso.get("numero").asInt());
                assertEquals(esperado.duracao(), compasso.get("duracao").asText());
                assertEquals(esperado.tuplet(), compasso.has("tuplet")
                        ? compasso.get("tuplet").asText() : null);
                assertEquals(esperado.golpes().size(), compasso.get("golpes").size());

                double soma = 0;
                for (int g = 0; g < esperado.golpes().size(); g++) {
                    GeradorDeLicks.GolpeDados golpeEsperado = esperado.golpes().get(g);
                    JsonNode golpe = compasso.get("golpes").get(g);
                    assertEquals(golpeEsperado.unidades(), golpe.get("unidades").asDouble(), 0.0001,
                            id + " - compasso " + (c + 1) + " golpe " + (g + 1));
                    soma += golpe.get("unidades").asDouble();
                    assertEquals(golpeEsperado.notas().size(), golpe.get("notas").size());

                    for (int n = 0; n < golpeEsperado.notas().size(); n++) {
                        GeradorDeLicks.NotaDados notaEsperada = golpeEsperado.notas().get(n);
                        JsonNode nota = golpe.get("notas").get(n);
                        assertEquals(notaEsperada.corda(), nota.get("corda").asInt());
                        assertEquals(notaEsperada.traste(), nota.get("traste").asInt());
                        assertEquals(notaEsperada.bendFull(), nota.get("bendFull").asBoolean());
                        assertEquals(notaEsperada.vibrato(), nota.get("vibrato").asBoolean());
                        assertEquals(notaEsperada.pullOff(), nota.get("pullOff").asBoolean());
                    }
                }
                assertEquals(16.0, soma, 0.001,
                        id + " - compasso " + (c + 1) + " nao fecha 4/4");
            }
        }
    }

    @Test
    void quiAlteraDoFrigioEstaMarcadaNoJson() throws Exception {
        JsonNode licks = ler("tabs-kb/json/licks-penta-am-m7.json").get("licks");
        JsonNode frigio = licks.get(0).get("compassos");
        assertNull(frigio.get(0).get("tuplet"));
        assertEquals("6:4", frigio.get(1).get("tuplet").asText());
        for (int i = 4; i <= 15; i++) {
            assertEquals(4.0 / 6.0, frigio.get(1).get("golpes").get(i)
                    .get("unidades").asDouble(), 0.0001);
        }
        assertEquals(4.0, frigio.get(1).get("golpes").get(16).get("unidades").asDouble(), 0.0001);
    }

    @Test
    void convencaoDeTitulosEstaRegistradaNoJson() throws Exception {
        JsonNode convencao = ler("tabs-kb/json/convencoes-licks.json");
        assertEquals("Lick N", convencao.get("tituloNaTab").asText());
        assertEquals("3", convencao.get("modoPentatonicaM7").asText());
        assertEquals("5", convencao.get("modoMenorHarmonica").asText());
        for (GeradorDeLicks.Lick lick : GeradorDeLicks.listar()) {
            assertTrue(lick.titulo().matches("Lick [123]"), lick.id());
        }
    }

    @Test
    void convencaoDeReferenciasEstaRegistradaNoJson() throws Exception {
        JsonNode convencao = ler("tabs-kb/json/convencoes-licks.json");
        String regra = convencao.get("referencia").asText();
        assertTrue(regra.contains("dica"));
        assertTrue(regra.contains("proveniência"));
        assertTrue(convencao.get("regras").toString().contains("proveniência"));

        JsonNode licks = ler("tabs-kb/json/licks-penta-am-m7.json").get("licks");
        for (JsonNode lick : licks) {
            String id = lick.get("id").asText();
            String referencia = lick.get("referencia").asText().toLowerCase();
            for (String termo : List.of("fonte", "arquivo", "guitar pro", "faixa", "compasso",
                    "pdf", "página")) {
                assertFalse(referencia.contains(termo), id + " - " + termo);
            }
        }
    }

    @Test
    void jsonNaoContemMarcacaoDeBpmEConvencaoRegistraRegra() throws Exception {
        JsonNode convencao = ler("tabs-kb/json/convencoes-licks.json");
        assertTrue(convencao.get("velocidade").asText().contains("player"));
        assertTrue(convencao.get("regras").toString().contains("BPM"));

        JsonNode licks = ler("tabs-kb/json/licks-penta-am-m7.json").get("licks");
        for (JsonNode lick : licks) {
            String id = lick.get("id").asText();
            for (String campo : List.of("alphaTex", "tablatura", "referencia")) {
                String texto = lick.get(campo).asText().toLowerCase();
                assertFalse(texto.contains("{tempo"), id + " - " + campo);
                assertFalse(texto.contains("bpm"), id + " - " + campo);
                assertFalse(texto.contains("♩"), id + " - " + campo);
            }
        }
    }

    @Test
    void notasDoJsonRespeitamAPentatonicaDeAm() throws Exception {
        JsonNode licks = ler("tabs-kb/json/licks-penta-am-m7.json").get("licks");
        for (JsonNode lick : licks) {
            String id = lick.get("id").asText();
            if (!id.startsWith("penta-m7")) {
                continue; // Frigio dominante usa outra escala
            }
            int notas = 0;
            for (JsonNode compasso : lick.get("compassos")) {
                for (JsonNode golpe : compasso.get("golpes")) {
                    for (JsonNode nota : golpe.get("notas")) {
                        int corda = nota.get("corda").asInt();
                        int traste = nota.get("traste").asInt();
                        int classe = (CORDAS_ABERTAS[corda - 1] + traste) % 12;
                        // G(8) = D# e uma passagem cromatica pontual do PDF no Lick 03.
                        boolean passagemDocumentada = "penta-m7-03".equals(id)
                                && corda == 3 && traste == 8;
                        assertTrue(PENTA_AM.contains(classe) || passagemDocumentada,
                                () -> id + " tem nota fora da pentatonica: corda " + corda
                                        + " casa " + traste);
                        notas++;
                    }
                }
            }
            assertTrue(notas > 0, id);
        }
    }

    @Test
    void arquivoDeFonteContemOsTextosUsadosNaAuditoria() throws Exception {
        String fonte = new String(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("tabs-kb/json/licks-penta-am-m7-fonte.json").readAllBytes(),
                java.nio.charset.StandardCharsets.UTF_8);
        for (String texto : List.of("full", "P.M.", "1/2", "Lick 03", "E-Gt")) {
            assertTrue(fonte.contains(texto), "Texto ausente no JSON de fonte: " + texto);
        }

        JsonNode raiz = ler("tabs-kb/json/licks-penta-am-m7-fonte.json");
        assertTrue(raiz.get("palavras").size() > 100);
        assertTrue(raiz.get("glyphs").size() > 100);
        assertEquals(6, raiz.get("linhasTablatura").get("pagina1Lick1").size());
        assertEquals(6, raiz.get("linhasTablatura").get("pagina2Lick3").size());
    }

    private static JsonNode ler(String recurso) throws Exception {
        try (InputStream entrada = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(recurso)) {
            assertNotNull(entrada, "Recurso nao encontrado: " + recurso);
            return MAPPER.readTree(entrada);
        }
    }
}
