package br.com.escalator.qa;

import br.com.escalator.service.GeradorDeAlphaTex;
import br.com.escalator.service.GeradorDeTablatura;
import br.com.escalator.service.ModoMaior;
import br.com.escalator.service.ModoMenorHarmonica;
import br.com.escalator.web.EscalatorWebService;
import br.com.escalator.web.ResultadoEstudo;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Auditoria dos 7 shapes da menor harmonica em Am. As casas foram lidas do PDF
 * image-only {@code SHAPES_menorharm.pdf} por OCR/morfologia e conferidas nota a
 * nota com a rotacao dos graus de A B C D E F G#. Nenhuma regra validada de
 * escala, armadura, compasso ou renderizacao e alterada.
 */
class MenorHarmonicaAuditoriaTest {

    /** Altura das cordas soltas na ordem AlphaTab (1 = E aguda ... 6 = E grave). */
    private static final int[] CORDAS_SOLTAS = {4, 11, 7, 2, 9, 4};
    private static final Set<Integer> GRAUS_MENOR_HARMONICA = Set.of(9, 11, 0, 2, 4, 5, 8);
    private static final Pattern NOTA_ALPHATEX = Pattern.compile("(\\d+)\\.(\\d+)");

    /** Modo -> tonica de referencia em Am e casas (E grave -> E aguda). */
    private static final Map<ModoMenorHarmonica, Caso> CASOS = casos();

    private record Caso(String tonica, int[][] casas) {
    }

    private static Map<ModoMenorHarmonica, Caso> casos() {
        Map<ModoMenorHarmonica, Caso> casos = new LinkedHashMap<>();
        casos.put(ModoMenorHarmonica.EOLIO_7, new Caso("A", new int[][]{
                {5, 7, 8}, {5, 7, 8}, {6, 7, 9},
                {5, 7, 9}, {6, 9, 10}, {7, 8, 10}}));
        casos.put(ModoMenorHarmonica.LOCRIO_6, new Caso("B", new int[][]{
                {7, 8, 10}, {7, 8, 11}, {7, 9, 10},
                {7, 9, 10}, {9, 10, 12}, {8, 10, 12}}));
        casos.put(ModoMenorHarmonica.JONIO_5SUST, new Caso("C", new int[][]{
                {8, 10, 12}, {8, 11, 12}, {9, 10, 12},
                {9, 10, 13}, {10, 12, 13}, {10, 12, 13}}));
        casos.put(ModoMenorHarmonica.DORICO_4SUST, new Caso("D", new int[][]{
                {10, 12, 13}, {11, 12, 14}, {10, 12, 14},
                {10, 13, 14}, {12, 13, 15}, {12, 13, 16}}));
        casos.put(ModoMenorHarmonica.FRIGIO_DOMINANTE, new Caso("E", new int[][]{
                {12, 13, 16}, {12, 14, 15}, {12, 14, 15},
                {13, 14, 16}, {13, 15, 17}, {13, 16, 17}}));
        casos.put(ModoMenorHarmonica.LIDIO_9SUST, new Caso("F", new int[][]{
                {13, 16, 17}, {14, 15, 17}, {14, 15, 18},
                {14, 16, 17}, {15, 17, 18}, {16, 17, 19}}));
        casos.put(ModoMenorHarmonica.DIMINUTO, new Caso("G#", new int[][]{
                {16, 17, 19}, {15, 17, 19}, {15, 18, 19},
                {16, 17, 19}, {17, 18, 21}, {17, 19, 20}}));
        return casos;
    }

    @Test
    void tabelaUnicaDosSeteShapesCorrespondeAoCore() {
        Map<ModoMenorHarmonica, Integer> graus = Map.of(
                ModoMenorHarmonica.EOLIO_7, 0,
                ModoMenorHarmonica.LOCRIO_6, 2,
                ModoMenorHarmonica.JONIO_5SUST, 3,
                ModoMenorHarmonica.DORICO_4SUST, 5,
                ModoMenorHarmonica.FRIGIO_DOMINANTE, 7,
                ModoMenorHarmonica.LIDIO_9SUST, 8,
                ModoMenorHarmonica.DIMINUTO, 11);
        assertEquals(7, ModoMenorHarmonica.values().length);
        for (ModoMenorHarmonica modo : ModoMenorHarmonica.values()) {
            assertEquals(graus.get(modo), modo.grau(), modo.name());
            assertTrue(ModoMenorHarmonica.porPadrao(modo.padraoOpcao()).isPresent(), modo.name());
            assertTrue(ModoMenorHarmonica.porModoTablatura(modo.modoTablatura()).isPresent(), modo.name());
            // Os novos valores nao podem colidir com os modos gregos (9 a 15).
            assertTrue(ModoMaior.porPadrao(modo.padraoOpcao()).isEmpty(), modo.name());
        }
    }

    @Test
    void intervalosSaoOsDoPdfDaMenorHarmonica() {
        assertEquals("T 2 3b 4 5 6b 7+", ModoMenorHarmonica.EOLIO_7.intervalos());
        assertEquals("T 2b 3b 4 5b 6 7", ModoMenorHarmonica.LOCRIO_6.intervalos());
        assertEquals("T 2 3 4 5# 6 7+", ModoMenorHarmonica.JONIO_5SUST.intervalos());
        assertEquals("T 2 3b 4# 5 6 7", ModoMenorHarmonica.DORICO_4SUST.intervalos());
        assertEquals("T 2b 3 4 5 6b 7", ModoMenorHarmonica.FRIGIO_DOMINANTE.intervalos());
        assertEquals("T 2# 3 4# 5 6 7+", ModoMenorHarmonica.LIDIO_9SUST.intervalos());
        assertEquals("T 2b 3b 4b 5b 6b 7b", ModoMenorHarmonica.DIMINUTO.intervalos());
    }

    @Test
    void casasDosSeteShapesBatitamComOPdf() {
        for (Map.Entry<ModoMenorHarmonica, Caso> entry : CASOS.entrySet()) {
            assertCasas(entry.getKey(), entry.getValue());
        }
    }

    @Test
    void eolio7DoPdfSubstituiOMesmoDesenhoDaAncora() {
        Caso caso = CASOS.get(ModoMenorHarmonica.EOLIO_7);
        assertCasas(ModoMenorHarmonica.EOLIO_7, caso);

        // Sem shape escolhido o servico continua gerando exatamente o desenho
        // antigo: so mudam o rotulo e a linha de intervalos.
        EscalatorWebService service = new EscalatorWebService();
        ResultadoEstudo padraoAntigo = service.gerar("5", "Am", "3");
        ResultadoEstudo eolio7 = service.gerar("5", "Am", ModoMenorHarmonica.EOLIO_7.padraoOpcao());
        assertEquals(padraoAntigo.titulo(), eolio7.titulo());
        assertEquals(padraoAntigo.notasFormatadas(), eolio7.notasFormatadas());
        assertEquals(padraoAntigo.tablaturaFormatada(), eolio7.tablaturaFormatada());
        assertEquals(padraoAntigo.tablaturaAlphaTex(), eolio7.tablaturaAlphaTex());
    }

    @Test
    void setePosicoesEncadeiamAsTonicasAteGSharp() {
        List<Integer> casasTonica = new ArrayList<>();
        for (Map.Entry<ModoMenorHarmonica, Caso> entry : CASOS.entrySet()) {
            List<GeradorDeTablatura.NotaTablatura> notas = GeradorDeTablatura.gerarNotas(
                    entry.getValue().tonica(), entry.getKey().modoTablatura());
            casasTonica.add(notas.get(0).traste());
        }
        assertEquals(List.of(5, 7, 8, 10, 12, 13, 16), casasTonica,
                "as tonicas devem formar o encadeamento A-B-C-D-E-F-G#");
    }

    @Test
    void todasAsNotasPertencemAMenorHarmonicaEEstaoDentroDoBraco() {
        for (Map.Entry<ModoMenorHarmonica, Caso> entry : CASOS.entrySet()) {
            for (GeradorDeTablatura.NotaTablatura nota : GeradorDeTablatura.gerarNotas(
                    entry.getValue().tonica(), entry.getKey().modoTablatura())) {
                int altura = (CORDAS_SOLTAS[nota.corda() - 1] + nota.traste()) % 12;
                assertTrue(GRAUS_MENOR_HARMONICA.contains(altura), entry.getKey()
                        + ": nota " + altura + " fora de A menor harmonica");
                assertTrue(nota.traste() >= 0 && nota.traste() <= 24, entry.getKey()
                        + ": casa " + nota.traste() + " fora do braco");
            }
        }
    }

    @Test
    void armaduraSempreDoParenteMenorNaoDoRootDoModo() {
        EscalatorWebService service = new EscalatorWebService();
        for (ModoMenorHarmonica modo : ModoMenorHarmonica.values()) {
            assertKsApparece(service.gerar("5", "Am", modo.padraoOpcao()), "Aminor", modo);
            assertKsApparece(service.gerar("5", "Dm", modo.padraoOpcao()), "Dminor", modo);
        }
        // Ciclo das quartas: Abm continua soletrado com bemol em qualquer shape.
        assertKsApparece(service.gerar("5", "Abm", ModoMenorHarmonica.LOCRIO_6.padraoOpcao()),
                "Abminor", ModoMenorHarmonica.LOCRIO_6);
    }

    @Test
    void rotulosGrausEIntervalosChegamAoResultado() {
        EscalatorWebService service = new EscalatorWebService();
        assertEquals("B Lócrio 6", service.gerar("5", "Am", "17").titulo());
        assertEquals("E Frígio Dominante", service.gerar("5", "Am", "20").titulo());
        assertEquals("G# Diminuto", service.gerar("5", "Am", "22").titulo());
        for (ModoMenorHarmonica modo : ModoMenorHarmonica.values()) {
            ResultadoEstudo resultado = service.gerar("5", "Am", modo.padraoOpcao());
            assertEquals(modo.intervalos(), resultado.intervalos(), modo.name());
            assertEquals(modo.rotulo(), resultado.nomePadrao(), modo.name());
        }
    }

    @Test
    void tituloDoDiminutoUsaAMesmaSoletracaoDaPrimeiraNotaDaEscala() {
        EscalatorWebService service = new EscalatorWebService();
        String padrao = ModoMenorHarmonica.DIMINUTO.padraoOpcao();
        Map<String, String> esperados = Map.of(
                "Cm", "B Diminuto",
                "C#m", "B# Diminuto",
                "Dm", "C# Diminuto",
                "F#m", "E# Diminuto",
                "Gm", "F# Diminuto");
        for (Map.Entry<String, String> entry : esperados.entrySet()) {
            ResultadoEstudo resultado = service.gerar("5", entry.getKey(), padrao);
            assertEquals(entry.getValue(), resultado.titulo(), entry.getKey());
            assertEquals(entry.getValue(), resultado.notasFormatadas().substring(1, resultado.notasFormatadas().indexOf(','))
                    + " Diminuto", entry.getKey());
        }
    }

    @Test
    void shapeDaMenorHarmonicaSofuncionaDentroDoModo5() {
        EscalatorWebService service = new EscalatorWebService();
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.gerar("1", "Am/C", ModoMenorHarmonica.LOCRIO_6.padraoOpcao()));
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.gerar("3", "Am/C", ModoMenorHarmonica.DIMINUTO.padraoOpcao()));
        // As opcoes legadas continuam valendo dentro do modo 5.
        assertTrue(!service.gerar("5", "Am", "3").notasFormatadas().isEmpty());
    }

    @Test
    void ritmoFechaEmQuatroCompassosDeTresQuartos() {
        for (Map.Entry<ModoMenorHarmonica, Caso> entry : CASOS.entrySet()) {
            String tex = GeradorDeAlphaTex.gerar("Teste", entry.getValue().tonica(),
                    entry.getKey().modoTablatura(), false);
            assertTrue(tex.contains("\\ts 3 4\n"), entry.getKey() + ": compasso errado");
            int compassos = 0;
            for (String trecho : tex.split("\\|")) {
                long notas = NOTA_ALPHATEX.matcher(trecho).results().count();
                if (notas == 0) {
                    continue;
                }
                assertEquals(9, notas, entry.getKey() + ": compasso nao fecha");
                compassos++;
            }
            assertEquals(4, compassos, entry.getKey() + ": quantidade de compassos");
        }
    }

    private static void assertKsApparece(ResultadoEstudo resultado, String armadura,
                                         ModoMenorHarmonica modo) {
        assertTrue(resultado.tablaturaAlphaTex().contains("\\ks " + armadura + "\n"),
                modo + ": armadura " + armadura + " ausente em: " + resultado.tablaturaAlphaTex());
    }

    private static void assertCasas(ModoMenorHarmonica modo, Caso caso) {
        List<GeradorDeTablatura.NotaTablatura> notas = GeradorDeTablatura.gerarNotas(
                caso.tonica(), modo.modoTablatura());
        List<List<Integer>> atual = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            atual.add(new ArrayList<>());
        }
        for (GeradorDeTablatura.NotaTablatura nota : notas.subList(0, notas.size() / 2)) {
            // AlphaTab: corda 1 = E aguda, corda 6 = E grave. Os arrays
            // esperados estao em ordem inversa, como os shapes do core.
            atual.get(6 - nota.corda()).add(nota.traste());
        }
        List<List<Integer>> esperado = new ArrayList<>();
        for (int[] corda : caso.casas()) {
            esperado.add(java.util.Arrays.stream(corda).boxed().toList());
        }
        assertEquals(esperado, atual, modo.name());
    }
}
