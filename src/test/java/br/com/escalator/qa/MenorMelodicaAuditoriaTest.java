package br.com.escalator.qa;

import br.com.escalator.model.Escala;
import br.com.escalator.model.Nota;
import br.com.escalator.service.GeradorDeAlphaTex;
import br.com.escalator.service.GeradorDeTablatura;
import br.com.escalator.service.ModoMaior;
import br.com.escalator.service.ModoMenorHarmonica;
import br.com.escalator.service.ModoMenorMelodica;
import br.com.escalator.service.PentatonicaM6;
import br.com.escalator.service.PentatonicaM7;
import br.com.escalator.web.EscalatorWebService;
import br.com.escalator.web.ResultadoEstudo;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Auditoria dos 7 shapes da menor melodica em Am. As casas foram lidas do PDF
 * {@code menormelodica_shapes.pdf} pela mesma calibracao posicional usada nas
 * pentatonicas e conferidas nota a nota com A B C D E F# G#. Nenhuma regra
 * validada de escala, armadura, compasso ou renderizacao e alterada.
 */
class MenorMelodicaAuditoriaTest {

    /** Altura das cordas soltas na ordem AlphaTab (1 = E aguda ... 6 = E grave). */
    private static final int[] CORDAS_SOLTAS = {4, 11, 7, 2, 9, 4};
    private static final Set<Integer> GRAUS_MENOR_MELODICA = Set.of(9, 11, 0, 2, 4, 6, 8);
    private static final Pattern NOTA_ALPHATEX = Pattern.compile("(\\d+)\\.(\\d+)");

    /** Modo -> tonica de referencia em Am e casas (E grave -> E aguda). */
    private static final Map<ModoMenorMelodica, Caso> CASOS = casos();

    private record Caso(String tonica, int[][] casas) {
    }

    private static Map<ModoMenorMelodica, Caso> casos() {
        Map<ModoMenorMelodica, Caso> casos = new LinkedHashMap<>();
        casos.put(ModoMenorMelodica.DORICO_7PLUS, new Caso("A", new int[][]{
                {5, 7, 8}, {5, 7, 9}, {6, 7, 9},
                {5, 7, 9}, {7, 9, 10}, {7, 8, 10}}));
        casos.put(ModoMenorMelodica.FRIGIO_6, new Caso("B", new int[][]{
                {7, 8, 10}, {7, 9, 11}, {7, 9, 10},
                {7, 9, 11}, {9, 10, 12}, {8, 10, 12}}));
        casos.put(ModoMenorMelodica.LIDIO_5SUST, new Caso("C", new int[][]{
                {8, 10, 12}, {9, 11, 12}, {9, 10, 12},
                {9, 11, 13}, {10, 12, 13}, {10, 12, 14}}));
        casos.put(ModoMenorMelodica.MIXO_4SUST, new Caso("D", new int[][]{
                {10, 12, 14}, {11, 12, 14}, {10, 12, 14},
                {11, 13, 14}, {12, 13, 15}, {12, 14, 16}}));
        casos.put(ModoMenorMelodica.MIXO_6B, new Caso("E", new int[][]{
                {12, 14, 16}, {12, 14, 15}, {12, 14, 16},
                {13, 14, 16}, {13, 15, 17}, {14, 16, 17}}));
        casos.put(ModoMenorMelodica.LOCRIO_9, new Caso("F#", new int[][]{
                {14, 16, 17}, {14, 15, 17}, {14, 16, 18},
                {14, 16, 17}, {15, 17, 19}, {16, 17, 19}}));
        casos.put(ModoMenorMelodica.ALT_7, new Caso("G#", new int[][]{
                {16, 17, 19}, {15, 17, 19}, {16, 18, 19},
                {16, 17, 19}, {17, 19, 21}, {17, 19, 20}}));
        return casos;
    }

    @Test
    void tabelaUnicaDosSeteShapesNaoColideComAsFamiliasValidadas() {
        Map<ModoMenorMelodica, Integer> graus = Map.of(
                ModoMenorMelodica.DORICO_7PLUS, 0,
                ModoMenorMelodica.FRIGIO_6, 2,
                ModoMenorMelodica.LIDIO_5SUST, 3,
                ModoMenorMelodica.MIXO_4SUST, 5,
                ModoMenorMelodica.MIXO_6B, 7,
                ModoMenorMelodica.LOCRIO_9, 9,
                ModoMenorMelodica.ALT_7, 11);
        assertEquals(7, ModoMenorMelodica.values().length);
        assertEquals(List.of("35", "36", "37", "38", "39", "40", "41"),
                java.util.Arrays.stream(ModoMenorMelodica.values())
                        .map(ModoMenorMelodica::padraoOpcao).toList());
        for (ModoMenorMelodica modo : ModoMenorMelodica.values()) {
            assertEquals(graus.get(modo), modo.grau(), modo.name());
            assertTrue(ModoMenorMelodica.porPadrao(modo.padraoOpcao()).isPresent(), modo.name());
            assertTrue(ModoMenorMelodica.porModoTablatura(modo.modoTablatura()).isPresent(), modo.name());
            assertTrue(ModoMaior.porPadrao(modo.padraoOpcao()).isEmpty(), modo.name());
            assertTrue(ModoMenorHarmonica.porPadrao(modo.padraoOpcao()).isEmpty(), modo.name());
            assertTrue(PentatonicaM7.porPadrao(modo.padraoOpcao()).isEmpty(), modo.name());
            assertTrue(PentatonicaM6.porPadrao(modo.padraoOpcao()).isEmpty(), modo.name());
        }
    }

    @Test
    void intervalosSaoOsDoPdfDaMenorMelodica() {
        assertEquals("T 2 3b 4 5 6 7+", ModoMenorMelodica.DORICO_7PLUS.intervalos());
        assertEquals("T 2b 3b 4 5 6 7", ModoMenorMelodica.FRIGIO_6.intervalos());
        assertEquals("T 2 3 4# 5# 6 7+", ModoMenorMelodica.LIDIO_5SUST.intervalos());
        assertEquals("T 2 3 4# 5 6 7", ModoMenorMelodica.MIXO_4SUST.intervalos());
        assertEquals("T 2 3 4 5 6b 7", ModoMenorMelodica.MIXO_6B.intervalos());
        assertEquals("T 2 3b 4 5b 6b 7", ModoMenorMelodica.LOCRIO_9.intervalos());
        assertEquals("T 2b 2# 3 5b 5# 7", ModoMenorMelodica.ALT_7.intervalos());
    }

    @Test
    void casasDosSeteShapesBatitamComOPdf() {
        for (Map.Entry<ModoMenorMelodica, Caso> entry : CASOS.entrySet()) {
            assertCasas(entry.getKey(), entry.getValue());
        }
    }

    @Test
    void setePosicoesEncadeiamAsTonicasAteGSharp() {
        List<Integer> casasTonica = new ArrayList<>();
        for (Map.Entry<ModoMenorMelodica, Caso> entry : CASOS.entrySet()) {
            List<GeradorDeTablatura.NotaTablatura> notas = GeradorDeTablatura.gerarNotas(
                    entry.getValue().tonica(), entry.getKey().modoTablatura());
            casasTonica.add(notas.get(0).traste());
        }
        assertEquals(List.of(5, 7, 8, 10, 12, 14, 16), casasTonica,
                "as tonicas devem formar o encadeamento A-B-C-D-E-F#-G#");
    }

    @Test
    void todasAsNotasPertencemAMenorMelodicaEEstaoDentroDoBraco() {
        for (Map.Entry<ModoMenorMelodica, Caso> entry : CASOS.entrySet()) {
            for (GeradorDeTablatura.NotaTablatura nota : GeradorDeTablatura.gerarNotas(
                    entry.getValue().tonica(), entry.getKey().modoTablatura())) {
                int altura = (CORDAS_SOLTAS[nota.corda() - 1] + nota.traste()) % 12;
                assertTrue(GRAUS_MENOR_MELODICA.contains(altura), entry.getKey()
                        + ": nota " + altura + " fora de A menor melodica");
                assertTrue(nota.traste() >= 0 && nota.traste() <= 24, entry.getKey()
                        + ": casa " + nota.traste() + " fora do braco");
            }
        }
    }

    @Test
    void transposicaoMantemAsNotasNaEscalaDeCadaTonica() {
        for (Nota tonica : Nota.values()) {
            for (ModoMenorMelodica modo : ModoMenorMelodica.values()) {
                // O gerador recebe a tonica do ROOT do shape. A parente fica
                // tantos semitons abaixo quantos o grau do modo no PDF.
                Nota parente = Nota.getNotaPorValor(tonica.getValor() - modo.grau());
                Set<Integer> permitidas = Escala.MENOR_MELODICA.calcularNotas(parente).stream()
                        .map(Nota::getValor)
                        .collect(java.util.stream.Collectors.toSet());
                for (GeradorDeTablatura.NotaTablatura nota : GeradorDeTablatura.gerarNotas(
                        tonica.toString(), modo.modoTablatura())) {
                    int altura = (CORDAS_SOLTAS[nota.corda() - 1] + nota.traste()) % 12;
                    assertTrue(permitidas.contains(altura), tonica + "/" + modo.name()
                            + ": nota " + altura + " fora da escala (casa " + nota.traste() + ")");
                }
            }
        }
    }

    @Test
    void armaduraEdaParenteMaiorDoDoricoComSetimaElevada() {
        EscalatorWebService service = new EscalatorWebService();
        for (ModoMenorMelodica modo : ModoMenorMelodica.values()) {
            // Am e um Dorico com 7+ de G: F# ja esta na armadura e G# deve
            // aparecer como acidente escrito na pauta, nao como \ks Aminor.
            assertKsApparece(service.gerar("13", "Am", modo.padraoOpcao()), "G", modo);
            assertKsApparece(service.gerar("13", "Dm", modo.padraoOpcao()), "C", modo);
        }
        assertTrue(service.gerar("13", "Gm/Bb", "35").tablaturaAlphaTex().contains("\\ks F\n"));
        assertTrue(service.gerar("13", "A#m/C#", "35").tablaturaAlphaTex().contains("\\ks Ab\n"));

        String tex = service.gerar("13", "Am", "35").tablaturaAlphaTex();
        assertFalse(tex.contains("\\ks Aminor"), "armadura antiga nao pode voltar: " + tex);
        assertFalse(tex.contains("\\ks Dminor"), "armadura antiga nao pode voltar: " + tex);
    }

    @Test
    void sequenciaETriadesContinuamDisponiveisNaMenorMelodica() {
        EscalatorWebService service = new EscalatorWebService();

        ResultadoEstudo sequencia = service.gerar("13", "Am", "1");
        assertEquals("A Menor Melódica", sequencia.titulo());
        assertEquals("Sequencia de 3 Notas", sequencia.nomePadrao());
        assertEquals(List.of("[A, B, C]", "[B, C, D]", "[C, D, E]", "[D, E, F#]",
                        "[E, F#, G#]", "[F#, G#, A]", "[G#, A, B]"),
                sequencia.linhasSaida());
        assertEquals("T 2 3b 4 5 6 7+", sequencia.intervalos());
        assertTrue(sequencia.tablaturaFormatada().isEmpty());
        assertTrue(sequencia.tablaturaAlphaTex().isEmpty());

        ResultadoEstudo triades = service.gerar("13", "Am", "2");
        assertEquals("Sequencia de Triades", triades.nomePadrao());
        assertEquals(List.of("A menor: A - C - E", "B menor: B - D - F#",
                        "C aumentada: C - E - G#", "D menor: D - F# - A",
                        "E menor: E - G# - B", "F# diminuta: F# - A - C",
                        "G# diminuta: G# - B - D"),
                triades.linhasSaida());
        assertEquals("T 2 3b 4 5 6 7+", triades.intervalos());
    }

    @Test
    void soletracaoDiatonicaUsaAParenteMaiorEmTodasAsTonicas() {
        EscalatorWebService service = new EscalatorWebService();
        Map<String, List<String>> esperados = new LinkedHashMap<>();
        esperados.put("Am/C", List.of("A", "B", "C", "D", "E", "F#", "G#"));
        esperados.put("Em/G", List.of("E", "F#", "G", "A", "B", "C#", "D#"));
        esperados.put("Bm/D", List.of("B", "C#", "D", "E", "F#", "G#", "A#"));
        esperados.put("F#m/A", List.of("F#", "G#", "A", "B", "C#", "D#", "E#"));
        esperados.put("C#m/E", List.of("C#", "D#", "E", "F#", "G#", "A#", "B#"));
        esperados.put("G#m/B", List.of("G#", "A#", "B", "C#", "D#", "E#", "F##"));
        esperados.put("D#m/F#", List.of("D#", "E#", "F#", "G#", "A#", "B#", "C##"));
        esperados.put("A#m/C#", List.of("A#", "B#", "C#", "D#", "E#", "F##", "G##"));
        esperados.put("Dm/F", List.of("D", "E", "F", "G", "A", "B", "C#"));
        esperados.put("Gm/Bb", List.of("G", "A", "Bb", "C", "D", "E", "F#"));
        esperados.put("Cm/Eb", List.of("C", "D", "Eb", "F", "G", "A", "B"));
        esperados.put("Fm/Ab", List.of("F", "G", "Ab", "Bb", "C", "D", "E"));
        esperados.put("Bbm/Db", List.of("Bb", "C", "Db", "Eb", "F", "G", "A"));
        esperados.put("Ebm/Gb", List.of("Eb", "F", "Gb", "Ab", "Bb", "C", "D"));
        esperados.put("Abm/Cb", List.of("Ab", "Bb", "Cb", "Db", "Eb", "F", "G"));

        for (Map.Entry<String, List<String>> entry : esperados.entrySet()) {
            ResultadoEstudo resultado = service.gerar("13", entry.getKey(), "1");
            assertEquals("[" + String.join(", ", entry.getValue()) + "]",
                    resultado.notasFormatadas(), entry.getKey());
        }

        assertEquals("A# Menor Melódica", service.gerar("13", "A#m/C#", "1").titulo());
        assertEquals("G## Alt 7 (superlócrio)", service.gerar("13", "A#m/C#", "41").titulo());
        assertEquals("C## Alt 7 (superlócrio)", service.gerar("13", "D#m/F#", "41").titulo());
        assertEquals("F## Alt 7 (superlócrio)", service.gerar("13", "G#m/B", "41").titulo());
    }

    @Test
    void triadesRespeitamASoletracaoDiatonicaForaDeAm() {
        EscalatorWebService service = new EscalatorWebService();
        assertEquals(List.of("D menor: D - F - A", "E menor: E - G - B",
                        "F aumentada: F - A - C#", "G menor: G - B - D",
                        "A menor: A - C# - E", "B diminuta: B - D - F",
                        "C# diminuta: C# - E - G"),
                service.gerar("13", "Dm", "2").linhasSaida());
        assertEquals(List.of("G# menor: G# - B - D#", "A# menor: A# - C# - E#",
                        "B aumentada: B - D# - F##", "C# menor: C# - E# - G#",
                        "D# menor: D# - F## - A#", "E# diminuta: E# - G# - B",
                        "F## diminuta: F## - A# - C#"),
                service.gerar("13", "G#m/B", "2").linhasSaida());
        assertEquals(List.of("A# menor: A# - C# - E#", "B# menor: B# - D# - F##",
                        "C# aumentada: C# - E# - G##", "D# menor: D# - F## - A#",
                        "E# menor: E# - G## - B#", "F## diminuta: F## - A# - C#",
                        "G## diminuta: G## - B# - D#"),
                service.gerar("13", "A#m/C#", "2").linhasSaida());
    }

    @Test
    void rotulosGrausEIntervalosChegamAoResultado() {
        EscalatorWebService service = new EscalatorWebService();
        assertEquals("A Menor Melódica", service.gerar("13", "Am", "35").titulo());
        assertEquals("B Frígio 6", service.gerar("13", "Am", "36").titulo());
        assertEquals("E Mixo 6b", service.gerar("13", "Am", "39").titulo());
        assertEquals("G# Alt 7 (superlócrio)", service.gerar("13", "Am", "41").titulo());
        List<String> base = List.of("A", "B", "C", "D", "E", "F#", "G#");
        for (ModoMenorMelodica modo : ModoMenorMelodica.values()) {
            ResultadoEstudo resultado = service.gerar("13", "Am", modo.padraoOpcao());
            List<String> esperados = new ArrayList<>(base);
            // A rotacao da lista segue o grau diatonico (0 a 6), nao a
            // distancia em semitons usada para transpor o shape no braco.
            java.util.Collections.rotate(esperados, -modo.ordinal());
            assertEquals("[" + String.join(", ", esperados) + "]",
                    resultado.notasFormatadas(), modo.name());
            assertEquals(modo.intervalos(), resultado.intervalos(), modo.name());
            assertEquals(modo.rotulo(), resultado.nomePadrao(), modo.name());
        }
    }

    @Test
    void shapeDaMenorMelodicaSoFuncionaDentroDoModo13() {
        EscalatorWebService service = new EscalatorWebService();
        assertThrows(IllegalArgumentException.class,
                () -> service.gerar("5", "Am", ModoMenorMelodica.FRIGIO_6.padraoOpcao()));
        assertThrows(IllegalArgumentException.class,
                () -> service.gerar("1", "Am/C", ModoMenorMelodica.ALT_7.padraoOpcao()));
        assertThrows(IllegalArgumentException.class,
                () -> service.gerar("11", "Am/C", ModoMenorMelodica.DORICO_7PLUS.padraoOpcao()));
        assertTrue(!service.gerar("13", "Am", "35").notasFormatadas().isEmpty());
    }

    @Test
    void ritmoFechaEmQuatroCompassosDeTresQuartos() {
        for (Map.Entry<ModoMenorMelodica, Caso> entry : CASOS.entrySet()) {
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
                                         ModoMenorMelodica modo) {
        assertTrue(resultado.tablaturaAlphaTex().contains("\\ks " + armadura + "\n"),
                modo + ": armadura " + armadura + " ausente em: " + resultado.tablaturaAlphaTex());
    }

    private static void assertCasas(ModoMenorMelodica modo, Caso caso) {
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
