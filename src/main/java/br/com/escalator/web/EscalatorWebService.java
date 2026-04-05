package br.com.escalator.web;

import br.com.escalator.model.Escala;
import br.com.escalator.model.Nota;
import br.com.escalator.service.GeradorDePadroes;
import br.com.escalator.service.GeradorDeTablatura;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EscalatorWebService {

    private static final Map<String, String> BEMOL_TO_SHARP = Map.of(
            "BB", "A_SHARP", "EB", "D_SHARP", "AB", "G_SHARP", "DB", "C_SHARP", "GB", "F_SHARP", "CB", "B"
    );

    private static final Map<String, String> SHARP_TO_BEMOL = Map.of(
            "A#", "Bb", "D#", "Eb", "G#", "Ab", "C#", "Db", "F#", "Gb"
    );

    private static final List<Nota> TONICAS_MAIORES_DE_BEMOL = List.of(
            Nota.F, Nota.A_SHARP, Nota.D_SHARP, Nota.G_SHARP, Nota.C_SHARP, Nota.F_SHARP
    );

    private final GeradorDePadroes geradorDePadroes = new GeradorDePadroes();

    public ResultadoEstudo gerar(String modoOpcao, String tonicaEntrada, String opcaoPadrao) {
        boolean isModoMenor = "2".equals(modoOpcao);
        String modoNome = isModoMenor ? "Menor Natural (Relativa)" : "Maior";

        Nota tonicaBase = parseTonica(tonicaEntrada);
        boolean usarNotacaoBemol = isTonicaDeBemol(tonicaBase);

        Nota tonicaFinal = tonicaBase;
        String nomeExibicaoTonicaFinal = formatarTonicaParaExibicao(tonicaBase, usarNotacaoBemol);

        if (isModoMenor) {
            tonicaFinal = Nota.getNotaPorValor(tonicaBase.getValor() + 9);
            nomeExibicaoTonicaFinal = formatarTonicaParaExibicao(tonicaFinal, usarNotacaoBemol);
        }

        List<Nota> notasDaEscala = Escala.MAIOR.calcularNotas(tonicaBase);
        if (isModoMenor) {
            int indiceTonicaMenor = notasDaEscala.indexOf(tonicaFinal);
            if (indiceTonicaMenor != -1) {
                Collections.rotate(notasDaEscala, -indiceTonicaMenor);
            }
        }

        String titulo = nomeExibicaoTonicaFinal + " " + modoNome;
        String notasFormatadas = formatarNotasDaEscala(notasDaEscala, usarNotacaoBemol);

        return switch (opcaoPadrao) {
            case "2" -> {
                List<String> triades = geradorDePadroes.gerarTriades(notasDaEscala, modoNome).stream()
                        .map(item -> formatarTriadeParaExibicao(item, usarNotacaoBemol))
                        .toList();
                yield new ResultadoEstudo(titulo, notasFormatadas, "Sequencia de Triades", triades, "");
            }
            case "3" -> {
                String[] tab3Npc = GeradorDeTablatura.gerar(tonicaFinal.toString(), modoNome);
                String tablatura = String.join(System.lineSeparator(), tab3Npc);
                yield new ResultadoEstudo(titulo, notasFormatadas, "3 Notas por Corda", List.of(), tablatura);
            }
            default -> {
                List<String> sequencia = geradorDePadroes.gerarSequencia(notasDaEscala, 3).stream()
                        .map(item -> formatarTriadeParaExibicao(item, usarNotacaoBemol))
                        .toList();
                yield new ResultadoEstudo(titulo, notasFormatadas, "Sequencia de 3 Notas", sequencia, "");
            }
        };
    }

    private Nota parseTonica(String tonicaEntrada) {
        if (tonicaEntrada == null || tonicaEntrada.isBlank()) {
            throw new IllegalArgumentException("Tonica invalida");
        }
        String entrada = tonicaEntrada.trim().toUpperCase();
        String enumName = BEMOL_TO_SHARP.getOrDefault(entrada, entrada.replace("#", "_SHARP"));
        return Nota.valueOf(enumName);
    }

    private boolean isTonicaDeBemol(Nota tonicaOriginal) {
        return TONICAS_MAIORES_DE_BEMOL.contains(tonicaOriginal);
    }

    private String formatarTonicaParaExibicao(Nota tonica, boolean usarNotacaoBemol) {
        String nomeNota = tonica.toString().replace("_SHARP", "#");
        if (usarNotacaoBemol) {
            return SHARP_TO_BEMOL.getOrDefault(nomeNota, nomeNota);
        }
        return nomeNota;
    }

    private String formatarNotasDaEscala(List<Nota> notasDaEscala, boolean usarNotacaoBemol) {
        List<String> notasFormatadas = notasDaEscala.stream()
                .map(nota -> formatarTonicaParaExibicao(nota, usarNotacaoBemol))
                .collect(Collectors.toList());
        return "[" + String.join(", ", notasFormatadas) + "]";
    }

    private String formatarTriadeParaExibicao(String triade, boolean usarNotacaoBemol) {
        String triadeFormatada = triade.replace("_SHARP", "#");
        if (!usarNotacaoBemol) {
            return triadeFormatada;
        }

        String resultado = triadeFormatada;
        for (Map.Entry<String, String> entry : SHARP_TO_BEMOL.entrySet()) {
            resultado = resultado.replace(entry.getKey(), entry.getValue());
        }
        return resultado;
    }
}
