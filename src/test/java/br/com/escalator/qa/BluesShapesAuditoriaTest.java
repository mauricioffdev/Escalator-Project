package br.com.escalator.qa;

import br.com.escalator.model.Escala;
import br.com.escalator.model.Nota;
import br.com.escalator.rag.PdfTabIndex;
import br.com.escalator.rag.ShapeDerivador;
import br.com.escalator.service.GeradorDeAlphaTex;
import br.com.escalator.service.GeradorDeTablatura;
import br.com.escalator.web.EscalatorWebService;
import br.com.escalator.web.ResultadoEstudo;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Auditoria musical e de sintaxe dos novos shapes blues integrados ao core. */
class BluesShapesAuditoriaTest {

    private static final int[] CORDAS_SOLTAS = {4, 11, 7, 2, 9, 4};
    private static final Pattern NOTA_ALPHATEX = Pattern.compile("(\\d+)\\.(\\d+)\\.16");

    private static final List<String> MODOS = List.of(
            GeradorDeTablatura.MODO_PENTATONICA_BLUES_SHAPE4,
            GeradorDeTablatura.MODO_PENTATONICA_BLUES_SHAPE5,
            GeradorDeTablatura.MODO_PENTATONICA_BLUES_EXTRA
    );

    private final ShapeDerivador derivador = new ShapeDerivador(new PdfTabIndex());

    @Test
    void coreCorrespondeAsCasasDerivadasDosPdfs() {
        assertCasas(MODOS.get(0), derivador.bluesShape4());
        assertCasas(MODOS.get(1), derivador.bluesShape5());
        assertCasas(MODOS.get(2), derivador.bluesAberta());

        assertEquals(List.of(11, 12, 15),
                GeradorDeTablatura.gerarNotas("A", MODOS.get(2)).stream()
                        .filter(nota -> nota.corda() == 1)
                        .map(GeradorDeTablatura.NotaTablatura::traste)
                        .distinct()
                        .toList(),
                "a casa 10 da E aguda nao e tocada e o retorno começa na casa 15");
    }

    @Test
    void transposicaoMantemTodasAsNotasNaEscalaDeCadaTonica() {
        for (String modo : MODOS) {
            for (Nota tonica : Nota.values()) {
                Set<Integer> permitidas = Escala.PENTATONICA_BLUES.calcularNotas(tonica)
                        .stream().map(Nota::getValor).collect(Collectors.toSet());
                for (GeradorDeTablatura.NotaTablatura nota
                        : GeradorDeTablatura.gerarNotas(tonica.toString(), modo)) {
                    int altura = (CORDAS_SOLTAS[nota.corda() - 1] + nota.traste()) % 12;
                    assertTrue(permitidas.contains(altura), modo + "/" + tonica
                            + ": nota " + altura + " fora da escala (casa " + nota.traste() + ")");
                }
            }
        }
    }

    @Test
    void alphaTexFechaMatematicamenteOTempo() {
        auditarRitmo(MODOS.get(0), "4 4", 16);
        auditarRitmo(MODOS.get(1), "4 4", 16);
        auditarTercina(MODOS.get(2));
    }

    @Test
    void webServiceDisponibilizaOsTresShapesParaBlues() {
        EscalatorWebService service = new EscalatorWebService();
        for (String opcao : List.of("6", "7", "8")) {
            ResultadoEstudo resultado = service.gerar("4", "Am/C", opcao);
            assertTrue(resultado.tablaturaAlphaTex().contains("\\ts "),
                    opcao + " deve gerar AlphaTex");
            assertFalse(resultado.tablaturaFormatada().isBlank(),
                    opcao + " deve gerar tablatura ASCII");
        }
    }

    private static void assertCasas(String modo, List<List<Integer>> esperado) {
        List<GeradorDeTablatura.NotaTablatura> notas =
                GeradorDeTablatura.gerarNotas("A", modo);
        List<List<Integer>> atual = new ArrayList<>();
        for (int corda = 1; corda <= 6; corda++) {
            atual.add(new ArrayList<>());
        }
        for (GeradorDeTablatura.NotaTablatura nota
                : notas.subList(0, notas.size() / 2)) {
            atual.get(nota.corda() - 1).add(nota.traste());
        }
        assertEquals(esperado, atual, modo);
    }

    private static void auditarRitmo(String modo, String compasso, int unidadesPorCompasso) {
        String tex = GeradorDeAlphaTex.gerar("Teste", "A", modo);
        assertTrue(tex.contains("\\ts " + compasso + "\n"), modo + ": compasso errado");

        for (String trecho : tex.split("\\|")) {
            Matcher matcher = NOTA_ALPHATEX.matcher(trecho);
            long notas = matcher.results().count();
            if (notas == 0) {
                continue;
            }
            int pausas = trecho.contains("r.16") ? 1 : trecho.contains("r.8") ? 2 : 0;
            assertEquals(unidadesPorCompasso, notas + pausas,
                    modo + ": compasso nao fecha (" + trecho.trim() + ")");
        }
    }

    /** O extra blues usa a mesma tercina do extra m7: 6 notas por 2/4, sem pausa. */
    private static void auditarTercina(String modo) {
        String tex = GeradorDeAlphaTex.gerar("Teste", "A", modo);
        assertTrue(tex.contains("\\ts 2 4\n"), modo + ": compasso errado");
        assertFalse(tex.contains(" r."), modo + ": a tercina nao deve ter pausa");

        String prefixo = ":8 { tu 3 }";
        List<String> barras = tex.lines()
                .map(String::trim)
                .filter(linha -> linha.contains(prefixo))
                .toList();
        for (String barra : barras) {
            assertTrue(barra.startsWith(prefixo), modo + ": a tercina deve iniciar o compasso");
            assertEquals(1, barra.split(Pattern.quote(prefixo), -1).length - 1,
                    modo + ": um prefixo de tercina por compasso");
            String notasTexto = barra.substring(prefixo.length()).replace("|", "").trim();
            assertTrue(Pattern.matches("\\d+\\.\\d+( \\d+\\.\\d+){5}", notasTexto),
                    modo + ": duas tercinas de 6 notas, sem sufixo de duracao (" + barra + ")");
        }
        assertEquals(6, barras.size(), "as 36 notas (ida e volta) fecham 6 compassos de 2/4");
    }

    /** As unicas notas diferentes entre os extras devem ser a blue note da E aguda. */
    @Test
    void bluesExtraDiferDoM7ExtraApenasNaBlueNote() {
        String m7 = GeradorDeAlphaTex.gerar("Teste", "A", GeradorDeTablatura.MODO_PENTATONICA_M7_EXTRA);
        String blues = GeradorDeAlphaTex.gerar("Teste", "A", GeradorDeTablatura.MODO_PENTATONICA_BLUES_EXTRA);
        assertEquals(m7.replace("10.1", "11.1"), blues);
    }
}
