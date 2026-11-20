package br.com.escalator.qa;

import br.com.escalator.rag.PdfTabExtractor;
import br.com.escalator.service.GeradorDeAlphaTex;
import br.com.escalator.service.GeradorDeTablatura;
import br.com.escalator.service.ModoMaior;
import br.com.escalator.web.EscalatorWebService;
import br.com.escalator.web.ResultadoEstudo;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Auditoria dos 7 modos da escala maior. As ancoras (Jonio e Eolio) ja estavam
 * no core validado e nao podem mudar; os cinco demos sao rotacoes uniformes
 * dos graus dessas ancoras, derivadas em ShapeDerivador.modos().
 */
class ModosAuditoriaTest {

    /** Altura das cordas soltas na ordem AlphaTab (1 = E aguda ... 6 = E grave). */
    private static final int[] CORDAS_SOLTAS = {4, 11, 7, 2, 9, 4};
    private static final Set<Integer> GRAUS_C_MAIOR = Set.of(0, 2, 4, 5, 7, 9, 11);
    private static final Pattern NOTA_ALPHATEX = Pattern.compile("(\\d+)\\.(\\d+)");

    /** Modo -> tonica de referencia na familia de C maior -> casas (E grave -> E aguda). */
    private static final Map<String, CasoModo> CASOS = casos();

    private record CasoModo(String tonica, int[][] casas, String opcaoWeb, String armadura) {
    }

    private static Map<String, CasoModo> casos() {
        Map<String, CasoModo> casos = new LinkedHashMap<>();
        casos.put("Maior", new CasoModo("C", new int[][]{
                {8, 10, 12}, {8, 10, 12}, {9, 10, 12},
                {9, 10, 12}, {10, 12, 13}, {10, 12, 13}}, "1", "C"));
        casos.put(GeradorDeTablatura.MODO_DORICO, new CasoModo("D", new int[][]{
                {10, 12, 13}, {10, 12, 14}, {10, 12, 14},
                {10, 12, 14}, {12, 13, 15}, {12, 13, 15}}, "6", "C"));
        casos.put(GeradorDeTablatura.MODO_FRIGIO, new CasoModo("E", new int[][]{
                {12, 13, 15}, {12, 14, 15}, {12, 14, 15},
                {12, 14, 16}, {13, 15, 17}, {13, 15, 17}}, "7", "C"));
        casos.put(GeradorDeTablatura.MODO_LIDIO, new CasoModo("F", new int[][]{
                {13, 15, 17}, {14, 15, 17}, {14, 15, 17},
                {14, 16, 17}, {15, 17, 18}, {15, 17, 19}}, "8", "C"));
        // A regra validada de oitavacao do core coloca G na casa 3 (nao 15),
        // renderizando esta posicao uma oitava abaixo da derivacao bruta.
        casos.put(GeradorDeTablatura.MODO_MIXOLIDIO, new CasoModo("G", new int[][]{
                {3, 5, 7}, {3, 5, 7}, {3, 5, 7},
                {4, 5, 7}, {5, 6, 8}, {5, 7, 8}}, "9", "C"));
        casos.put("Menor Natural (Relativa)", new CasoModo("A", new int[][]{
                {5, 7, 8}, {5, 7, 8}, {5, 7, 9},
                {5, 7, 9}, {6, 8, 10}, {7, 8, 10}}, "2", "Aminor"));
        casos.put(GeradorDeTablatura.MODO_LOCRIO, new CasoModo("B", new int[][]{
                {7, 8, 10}, {7, 8, 10}, {7, 9, 10},
                {7, 9, 10}, {8, 10, 12}, {8, 10, 12}}, "10", "C"));
        return casos;
    }

    @Test
    void tabelaUnicaDosSeteModosCorrespondeAoCore() {
        Map<String, String> tablaturaPorPadrao = Map.of(
                "9", "Maior",
                "10", GeradorDeTablatura.MODO_DORICO,
                "11", GeradorDeTablatura.MODO_FRIGIO,
                "12", GeradorDeTablatura.MODO_LIDIO,
                "13", GeradorDeTablatura.MODO_MIXOLIDIO,
                "14", "Menor Natural (Relativa)",
                "15", GeradorDeTablatura.MODO_LOCRIO);
        Map<String, Integer> graus = Map.of(
                "9", 0, "10", 2, "11", 4, "12", 5, "13", 7, "14", 9, "15", 11);
        assertEquals(7, ModoMaior.values().length);
        for (ModoMaior modo : ModoMaior.values()) {
            assertEquals(tablaturaPorPadrao.get(modo.padraoOpcao()), modo.modoTablatura(), modo.name());
            assertEquals(graus.get(modo.padraoOpcao()), modo.grau(), modo.name());
            assertTrue(ModoMaior.porPadrao(modo.padraoOpcao()).isPresent(), modo.name());
            assertTrue(ModoMaior.porOpcao(modo.opcao()).isPresent(), modo.name());
        }
    }

    @Test
    void intervalosDosSeteModosVemDoPdfDosModosGregos() throws IOException {
        URL pdf = getClass().getResource("/tabs-kb/pdf/Shapes_Modos.pdf");
        assertNotNull(pdf, "PDF dos modos gregos ausente nos recursos");
        String texto = PdfTabExtractor.extrair(pdf.openStream());

        // O ponto casa com o acento do rotulo ("Jonio"/"Jonio" no PDF). A busca
        // e linha a linha para nao confundir "T 2 3 4 5 6 7" com "...7+".
        Map<String, String> rotulosNoPdf = Map.of(
                "9", "J.nio", "10", "D.rico", "11", "Fr.gio", "12", "L.dio",
                "13", "Mixol.dio", "14", "E.lio", "15", "L.crio");
        for (ModoMaior modo : ModoMaior.values()) {
            String rotulo = rotulosNoPdf.get(modo.padraoOpcao());
            assertNotNull(rotulo, modo.name());
            Matcher linha = Pattern.compile(
                    "^.*" + rotulo + ".*Intervalos:[ \\t]*([^\\r\\n]+?)[ \\t]*$",
                    Pattern.MULTILINE).matcher(texto);
            assertTrue(linha.find(), modo + ": linha de intervalos ausente no PDF");
            assertEquals(modo.intervalos(), linha.group(1).trim(), modo.name());
        }
    }

    @Test
    void novoPadraoGregoGeraOMesmoEstudoDoModoLegado() {
        EscalatorWebService service = new EscalatorWebService();
        for (ModoMaior modo : ModoMaior.values()) {
            ResultadoEstudo novo = service.gerar("1", "Am/C", modo.padraoOpcao());
            ResultadoEstudo legado = service.gerar(modo.opcao(), "Am/C", "3");
            assertEquals(legado.titulo(), novo.titulo(), modo.name());
            assertEquals(legado.notasFormatadas(), novo.notasFormatadas(), modo.name());
            assertEquals(legado.tablaturaFormatada(), novo.tablaturaFormatada(), modo.name());
            assertEquals(legado.tablaturaAlphaTex(), novo.tablaturaAlphaTex(), modo.name());
            assertEquals(modo.intervalos(), novo.intervalos(), modo.name());
            assertEquals(modo.rotulo(), novo.nomePadrao(), modo.name());
        }
    }

    @Test
    void shape1DaEscalaMaiorEhOMesmoJonioComOutroRotulo() {
        EscalatorWebService service = new EscalatorWebService();
        ResultadoEstudo shape1 = service.gerar("1", "Am/C", "3");
        ResultadoEstudo jonio = service.gerar("1", "Am/C", ModoMaior.JONIO.padraoOpcao());

        assertEquals(ModoMaior.JONIO.rotulo(), shape1.nomePadrao());
        assertEquals(jonio.titulo(), shape1.titulo());
        assertEquals(jonio.notasFormatadas(), shape1.notasFormatadas());
        assertEquals(jonio.tablaturaFormatada(), shape1.tablaturaFormatada());
        assertEquals(jonio.tablaturaAlphaTex(), shape1.tablaturaAlphaTex());
        assertEquals(ModoMaior.JONIO.intervalos(), shape1.intervalos());
    }

    @Test
    void ancorasValidadasNaoMudaram() {
        assertCasas("Maior");
        assertCasas("Menor Natural (Relativa)");
    }

    @Test
    void cincoModosDerivadosBatitosComAsCasasEsperadas() {
        for (String modo : List.of(GeradorDeTablatura.MODO_DORICO,
                GeradorDeTablatura.MODO_FRIGIO, GeradorDeTablatura.MODO_LIDIO,
                GeradorDeTablatura.MODO_MIXOLIDIO, GeradorDeTablatura.MODO_LOCRIO)) {
            assertCasas(modo);
        }
    }

    @Test
    void osSeteModosCobremOCobracoSemSobreporPosicoes() {
        List<Integer> casasTonica = new ArrayList<>();
        for (Map.Entry<String, CasoModo> entry : CASOS.entrySet()) {
            List<GeradorDeTablatura.NotaTablatura> notas =
                    GeradorDeTablatura.gerarNotas(entry.getValue().tonica(), entry.getKey());
            casasTonica.add(notas.get(0).traste());
        }
        assertEquals(List.of(8, 10, 12, 13, 3, 5, 7), casasTonica,
                "as tonicas devem formar o encadeamento C-D-E-F-G-A-B");
    }

    @Test
    void todasAsNotasPertencemAEscalaEEstaoDentroDoBraco() {
        for (Map.Entry<String, CasoModo> entry : CASOS.entrySet()) {
            for (GeradorDeTablatura.NotaTablatura nota
                    : GeradorDeTablatura.gerarNotas(entry.getValue().tonica(), entry.getKey())) {
                int altura = (CORDAS_SOLTAS[nota.corda() - 1] + nota.traste()) % 12;
                assertTrue(GRAUS_C_MAIOR.contains(altura), entry.getKey()
                        + ": nota " + altura + " fora de C maior");
                assertTrue(nota.traste() >= 0 && nota.traste() <= 24, entry.getKey()
                        + ": casa " + nota.traste() + " fora do braco");
            }
        }
    }

    @Test
    void armaduraSempreRefleteOParenteDaEscala() {
        for (Map.Entry<String, CasoModo> entry : CASOS.entrySet()) {
            String tex = GeradorDeAlphaTex.gerar("Teste", entry.getValue().tonica(), entry.getKey());
            assertTrue(tex.contains("\\ks " + entry.getValue().armadura() + "\n"), entry.getKey());
        }
    }

    @Test
    void ritmoFechaEmQuatroCompassosDeTresQuartos() {
        for (Map.Entry<String, CasoModo> entry : CASOS.entrySet()) {
            String tex = GeradorDeAlphaTex.gerar("Teste", entry.getValue().tonica(), entry.getKey());
            assertTrue(tex.contains("\\ts 3 4\n"), entry.getKey() + ": compasso errado");
            int compassos = 0;
            for (String trecho : tex.split("\\|")) {
                Matcher matcher = NOTA_ALPHATEX.matcher(trecho);
                long notas = matcher.results().count();
                if (notas == 0) {
                    continue;
                }
                assertEquals(9, notas, entry.getKey() + ": compasso nao fecha");
                compassos++;
            }
            assertEquals(4, compassos, entry.getKey() + ": quantidade de compassos");
        }
    }

    @Test
    void eolioNoNovoPadraoMantemAArmaduraMenorDoCicloCerto() {
        EscalatorWebService service = new EscalatorWebService();
        assertTrue(service.gerar("1", "Am/C", "14").tablaturaAlphaTex().contains("\\ks Aminor\n"));
        assertTrue(service.gerar("1", "Abm/Cb", "14").tablaturaAlphaTex().contains("\\ks Abminor\n"));
        assertTrue(service.gerar("1", "G#m/B", "14").tablaturaAlphaTex().contains("\\ks G#minor\n"));
    }

    @Test
    void pentatonicasEMenorHarmonicaNaoTemIntervalosDosGregos() {
        EscalatorWebService service = new EscalatorWebService();
        assertTrue(service.gerar("3", "Am/C", "3").intervalos().isEmpty());
        assertTrue(service.gerar("4", "Am/C", "3").intervalos().isEmpty());
        assertTrue(service.gerar("5", "Am", "3").intervalos().isEmpty());
    }

    @Test
    void webServiceGeraOsCincoModosComTablaturaEAlphaTex() {
        EscalatorWebService service = new EscalatorWebService();
        for (String opcao : List.of("6", "7", "8", "9", "10")) {
            ResultadoEstudo resultado = service.gerar(opcao, "Am/C", "3");
            assertFalse(resultado.tablaturaFormatada().isBlank(), opcao + ": tablatura vazia");
            assertTrue(resultado.tablaturaAlphaTex().contains("\\ks C\n"), opcao + ": armadura errada");
            assertTrue(resultado.tablaturaAlphaTex().contains("\\ts 3 4\n"), opcao + ": compasso errado");
        }
    }

    private static void assertCasas(String modo) {
        CasoModo caso = CASOS.get(modo);
        List<GeradorDeTablatura.NotaTablatura> notas =
                GeradorDeTablatura.gerarNotas(caso.tonica(), modo);
        List<List<Integer>> atual = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            atual.add(new ArrayList<>());
        }
        for (GeradorDeTablatura.NotaTablatura nota : notas.subList(0, notas.size() / 2)) {
            // AlphaTab: corda 1 = E aguda, corda 6 = E grave. Os arrays esperados
            // estao em ordem inversa (E grave -> E aguda), como os shapes do core.
            atual.get(6 - nota.corda()).add(nota.traste());
        }
        List<List<Integer>> esperado = new ArrayList<>();
        for (int[] corda : caso.casas()) {
            esperado.add(java.util.Arrays.stream(corda).boxed().toList());
        }
        assertEquals(esperado, atual, modo);
    }
}
