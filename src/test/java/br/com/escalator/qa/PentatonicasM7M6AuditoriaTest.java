package br.com.escalator.qa;

import br.com.escalator.model.Escala;
import br.com.escalator.model.Nota;
import br.com.escalator.service.GeradorDeAlphaTex;
import br.com.escalator.service.GeradorDeTablatura;
import br.com.escalator.service.PentatonicaM6;
import br.com.escalator.service.PentatonicaM7;
import br.com.escalator.web.EscalatorWebService;
import br.com.escalator.web.ResultadoEstudo;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Auditoria musical das pentatônicas M7 e m6 derivadas dos novos PDFs. */
class PentatonicasM7M6AuditoriaTest {

    private static final int[] CORDAS_SOLTAS = {4, 11, 7, 2, 9, 4};
    private static final Pattern NOTA_ALPHATEX = Pattern.compile("(\\d+)\\.(\\d+)\\.16");

    private static final List<String> TONICAS = List.of(
            "Am/C", "Em/G", "Bm/D", "F#m/A", "C#m/E", "G#m/B", "D#m/F#", "A#m/C#",
            "Dm/F", "Gm/Bb", "Cm/Eb", "Fm/Ab", "Bbm/Db", "Ebm/Gb", "Abm/Cb");
    /** Aceitas pelo {@code \ks} do AlphaTab: no maximo 7 sustenidos ou 7 bemois. */
    private static final Set<String> ARMADURAS_ALPHATEX = Set.of(
            "C", "G", "D", "A", "E", "B", "F#", "C#",
            "F", "Bb", "Eb", "Ab", "Db", "Gb", "Cb");

    private static final List<String> MODOS_M7 = List.of(
            GeradorDeTablatura.MODO_PENTATONICA_M7_A,
            GeradorDeTablatura.MODO_PENTATONICA_M7_A_SHAPE2,
            GeradorDeTablatura.MODO_PENTATONICA_M7_A_SHAPE3,
            GeradorDeTablatura.MODO_PENTATONICA_M7_A_SHAPE4,
            GeradorDeTablatura.MODO_PENTATONICA_M7_A_SHAPE5,
            GeradorDeTablatura.MODO_PENTATONICA_M7_A_EXTRA);

    private static final List<String> MODOS_M6 = List.of(
            GeradorDeTablatura.MODO_PENTATONICA_M6_A,
            GeradorDeTablatura.MODO_PENTATONICA_M6_A_SHAPE2,
            GeradorDeTablatura.MODO_PENTATONICA_M6_A_SHAPE3,
            GeradorDeTablatura.MODO_PENTATONICA_M6_A_SHAPE4,
            GeradorDeTablatura.MODO_PENTATONICA_M6_A_SHAPE5,
            GeradorDeTablatura.MODO_PENTATONICA_M6_A_EXTRA);

    @Test
    void padroesNaoColidemComAsFamiliasJaValidadas() {
        assertEquals(List.of("23", "24", "25", "26", "27", "28"),
                java.util.Arrays.stream(PentatonicaM7.values())
                        .map(PentatonicaM7::padraoOpcao).toList());
        assertEquals(List.of("29", "30", "31", "32", "33", "34"),
                java.util.Arrays.stream(PentatonicaM6.values())
                        .map(PentatonicaM6::padraoOpcao).toList());

        Set<String> vistos = new HashSet<>();
        for (PentatonicaM7 shape : PentatonicaM7.values()) {
            assertTrue(vistos.add(shape.padraoOpcao()), shape.padraoOpcao());
        }
        for (PentatonicaM6 shape : PentatonicaM6.values()) {
            assertTrue(vistos.add(shape.padraoOpcao()), shape.padraoOpcao());
        }
        assertEquals(12, vistos.size());
    }

    @Test
    void casasLidasDosPdfsBatComOCore() {
        // Ordem das listas: E aguda, B, G, D, A, E grave.
        assertCasas(MODOS_M7.get(0), List.of(
                List.of(5, 9), List.of(5, 8), List.of(6, 7),
                List.of(5, 7), List.of(5, 7), List.of(5, 9)));
        assertCasas(MODOS_M7.get(1), List.of(
                List.of(9, 10), List.of(8, 10), List.of(7, 9),
                List.of(7, 11), List.of(7, 10), List.of(9, 10)));
        assertCasas(MODOS_M7.get(2), List.of(
                List.of(10, 12), List.of(10, 14), List.of(9, 12),
                List.of(11, 12), List.of(10, 12), List.of(10, 12)));
        assertCasas(MODOS_M7.get(3), List.of(
                List.of(12, 15), List.of(14, 15), List.of(12, 14),
                List.of(12, 14), List.of(12, 16), List.of(12, 15)));
        assertCasas(MODOS_M7.get(4), List.of(
                List.of(15, 17), List.of(15, 17), List.of(14, 18),
                List.of(14, 17), List.of(16, 17), List.of(15, 17)));
        // A casa 17 da E aguda foi removida a pedido para fechar 3/4 com 12 notas.
        assertCasas(MODOS_M7.get(5), List.of(
                List.of(10, 12), List.of(10, 14), List.of(7, 9),
                List.of(7, 11), List.of(5, 7), List.of(5, 9)));

        assertCasas(MODOS_M6.get(0), List.of(
                List.of(5, 8), List.of(5, 7), List.of(5, 7),
                List.of(4, 7), List.of(5, 7), List.of(5, 8)));
        assertCasas(MODOS_M6.get(1), List.of(
                List.of(8, 10), List.of(7, 10), List.of(7, 9),
                List.of(7, 10), List.of(7, 9), List.of(8, 10)));
        assertCasas(MODOS_M6.get(2), List.of(
                List.of(10, 12), List.of(10, 13), List.of(9, 11),
                List.of(10, 12), List.of(9, 12), List.of(10, 12)));
        assertCasas(MODOS_M6.get(3), List.of(
                List.of(12, 14), List.of(13, 15), List.of(11, 14),
                List.of(12, 14), List.of(12, 15), List.of(12, 14)));
        assertCasas(MODOS_M6.get(4), List.of(
                List.of(14, 17), List.of(15, 17), List.of(14, 17),
                List.of(14, 16), List.of(15, 17), List.of(14, 17)));
        assertCasas(MODOS_M6.get(5), List.of(
                List.of(5, 8), List.of(5, 7, 8), List.of(5, 7, 8),
                List.of(4, 5, 7), List.of(5, 6, 7), List.of(5, 8)));
    }

    @Test
    void transposicaoMantemAsNotasNaEscalaDeCadaTonica() {
        for (String modo : MODOS_M7) {
            auditarEscala(modo, Escala.PENTATONICA_MAIOR_M7, null);
        }
        for (String modo : MODOS_M6) {
            auditarEscala(modo, Escala.PENTATONICA_M6,
                    GeradorDeTablatura.MODO_PENTATONICA_M6_A_EXTRA.equals(modo)
                            ? Escala.PENTATONICA_BLUES : null);
        }
    }

    @Test
    void alphaTexFechaMatematicamenteTodosOsCompassos() {
        for (String modo : MODOS_M7) {
            auditarRitmo(modo, "3 4", 12);
        }
        for (String modo : MODOS_M6.subList(0, 5)) {
            auditarRitmo(modo, "3 4", 12);
        }
        auditarRitmo(MODOS_M6.get(5), "4 4", 16);
    }

    @Test
    void quantidadeDeNotasRespeitaODesenhoDosExtras() {
        assertEquals(24, GeradorDeTablatura.gerarNotas("A", MODOS_M7.get(5)).size());
        assertEquals(32, GeradorDeTablatura.gerarNotas("A", MODOS_M6.get(5)).size());
    }

    @Test
    void webServiceEntregaTitulosIntervalosEArmaduraDosPdfs() {
        EscalatorWebService service = new EscalatorWebService();

        for (PentatonicaM7 shape : PentatonicaM7.values()) {
            ResultadoEstudo resultado = service.gerar(
                    PentatonicaM7.MODO_OPCAO, "Am/C", shape.padraoOpcao());
            assertEquals("Pentatonica M7 de A", resultado.titulo());
            assertEquals(shape.rotulo(), resultado.nomePadrao());
            assertEquals(PentatonicaM7.INTERVALOS, resultado.intervalos());
            assertEquals("[A, C#, D, E, G]", resultado.notasFormatadas());
            assertEquals("D", armaduraDe(resultado), shape.padraoOpcao());
            assertFalse(resultado.tablaturaFormatada().isBlank(), shape.padraoOpcao());
        }

        for (PentatonicaM6 shape : PentatonicaM6.values()) {
            ResultadoEstudo resultado = service.gerar(
                    PentatonicaM6.MODO_OPCAO, "Am/C", shape.padraoOpcao());
            assertEquals("Pentatonica m6 de A Menor", resultado.titulo());
            assertEquals(shape.rotulo(), resultado.nomePadrao());
            assertEquals(PentatonicaM6.INTERVALOS, resultado.intervalos());
            assertEquals("[A, C, D, E, F#]", resultado.notasFormatadas());
            // A m6 pertence ao campo de G maior, nao ao de D maior (que tem C#).
            assertEquals("G", armaduraDe(resultado), shape.padraoOpcao());
            assertFalse(resultado.tablaturaFormatada().isBlank(), shape.padraoOpcao());
        }

        // Todas as tonicas do dropdown precisam de uma armadura renderizavel:
        // os parentes D#/G# sao escritos como Eb/Ab para nao estourar 7 acidentes.
        for (String tonica : TONICAS) {
            for (PentatonicaM7 shape : PentatonicaM7.values()) {
                String armadura = armaduraDe(service.gerar(
                        PentatonicaM7.MODO_OPCAO, tonica, shape.padraoOpcao()));
                assertTrue(ARMADURAS_ALPHATEX.contains(armadura),
                        tonica + "/M7/" + shape.padraoOpcao() + " -> " + armadura);
            }
            for (PentatonicaM6 shape : PentatonicaM6.values()) {
                String armadura = armaduraDe(service.gerar(
                        PentatonicaM6.MODO_OPCAO, tonica, shape.padraoOpcao()));
                assertTrue(ARMADURAS_ALPHATEX.contains(armadura),
                        tonica + "/m6/" + shape.padraoOpcao() + " -> " + armadura);
            }
        }
    }

    @Test
    void shapesDasNovasPentatonicasNaoSaoAceitosEmOutrosModos() {
        EscalatorWebService service = new EscalatorWebService();
        assertThrows(IllegalArgumentException.class, () -> service.gerar("3", "Am/C", "23"));
        assertThrows(IllegalArgumentException.class,
                () -> service.gerar(PentatonicaM7.MODO_OPCAO, "Am/C", "29"));
        assertThrows(IllegalArgumentException.class,
                () -> service.gerar(PentatonicaM6.MODO_OPCAO, "Am/C", "23"));
        assertThrows(IllegalArgumentException.class, () -> service.gerar("1", "Am/C", "34"));
    }

    private static String armaduraDe(ResultadoEstudo resultado) {
        return resultado.tablaturaAlphaTex().lines()
                .filter(linha -> linha.startsWith("\\ks "))
                .findFirst()
                .orElseThrow()
                .substring("\\ks ".length())
                .trim();
    }

    private static void assertCasas(String modo, List<List<Integer>> esperado) {
        List<GeradorDeTablatura.NotaTablatura> notas =
                GeradorDeTablatura.gerarNotas("A", modo);
        List<List<Integer>> atual = new ArrayList<>();
        for (int corda = 1; corda <= 6; corda++) {
            atual.add(new ArrayList<>());
        }
        for (GeradorDeTablatura.NotaTablatura nota : notas.subList(0, notas.size() / 2)) {
            atual.get(nota.corda() - 1).add(nota.traste());
        }
        assertEquals(esperado, atual, modo);
    }

    private static void auditarEscala(String modo, Escala escala, Escala escalaExtra) {
        Set<Integer> permitidas = new HashSet<>();
        for (Nota tonica : Nota.values()) {
            permitidas.clear();
            permitidas.addAll(escala.calcularNotas(tonica).stream()
                    .map(Nota::getValor).collect(Collectors.toSet()));
            if (escalaExtra != null) {
                permitidas.addAll(escalaExtra.calcularNotas(tonica).stream()
                        .map(Nota::getValor).collect(Collectors.toSet()));
            }
            for (GeradorDeTablatura.NotaTablatura nota
                    : GeradorDeTablatura.gerarNotas(tonica.toString(), modo)) {
                int altura = (CORDAS_SOLTAS[nota.corda() - 1] + nota.traste()) % 12;
                assertTrue(permitidas.contains(altura), modo + "/" + tonica
                        + ": nota " + altura + " fora da escala (casa " + nota.traste() + ")");
            }
        }
    }

    private static void auditarRitmo(String modo, String compasso, int unidadesPorCompasso) {
        String tex = GeradorDeAlphaTex.gerar("Teste", "A", modo);
        assertTrue(tex.contains("\\ts " + compasso + "\n"), modo + ": compasso errado");
        assertFalse(tex.contains(" r."), modo + ": nao deve haver pausa");

        int compassos = 0;
        for (String trecho : tex.split("\\|")) {
            Matcher matcher = NOTA_ALPHATEX.matcher(trecho);
            long notas = matcher.results().count();
            if (notas == 0) {
                continue;
            }
            assertEquals(unidadesPorCompasso, notas,
                    modo + ": compasso nao fecha (" + trecho.trim() + ")");
            compassos++;
        }
        assertEquals(2, compassos, modo + ": ida e volta em dois compassos");
    }
}
