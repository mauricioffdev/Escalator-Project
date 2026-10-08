package br.com.escalator.web;

import br.com.escalator.model.Escala;
import br.com.escalator.model.Nota;
import br.com.escalator.service.GeradorDeAlphaTex;
import br.com.escalator.service.GeradorDePadroes;
import br.com.escalator.service.GeradorDeTablatura;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class EscalatorWebService {

    private static final Map<String, String> BEMOL_TO_SHARP = Map.of(
            "BB", "A_SHARP", "EB", "D_SHARP", "AB", "G_SHARP", "DB", "C_SHARP", "GB", "F_SHARP", "CB", "B"
    );

    private static final Map<String, String> SHARP_TO_BEMOL = Map.of(
            "A#", "Bb", "D#", "Eb", "G#", "Ab", "C#", "Db", "F#", "Gb"
    );

    /** Tonicas maiores cuja armadura e escrita com benois: F, Bb, Eb, Ab, Db, Gb. */
    private static final List<Nota> TONICAS_MAIORES_DE_BEMOL = List.of(
            Nota.F, Nota.A_SHARP, Nota.D_SHARP, Nota.G_SHARP, Nota.C_SHARP, Nota.F_SHARP
    );

    /** Par relativo escolhido no dropdown: sempre comeca pela menor. */
    private record ParRelativo(Nota menor, Nota maior) {
    }

    private final GeradorDePadroes geradorDePadroes = new GeradorDePadroes();

    public ResultadoEstudo gerar(String modoOpcao, String tonicaEntrada, String opcaoPadrao) {
        ParRelativo par = parseParRelativo(modoOpcao, tonicaEntrada);

        String modoNome;
        Nota tonicaFinal;
        List<Nota> notasDaEscala;
        boolean isPentatonica;

        if ("2".equals(modoOpcao)) {
            modoNome = "Menor Natural (Relativa)";
            tonicaFinal = par.menor();
            notasDaEscala = Escala.MAIOR.calcularNotas(par.maior());
            int indiceTonicaMenor = notasDaEscala.indexOf(tonicaFinal);
            if (indiceTonicaMenor != -1) {
                Collections.rotate(notasDaEscala, -indiceTonicaMenor);
            }
            isPentatonica = false;
        } else if ("3".equals(modoOpcao)) {
            modoNome = GeradorDeTablatura.MODO_PENTATONICA_M7;
            tonicaFinal = par.menor();
            notasDaEscala = Escala.PENTATONICA_M7.calcularNotas(tonicaFinal);
            isPentatonica = true;
        } else if ("4".equals(modoOpcao)) {
            modoNome = GeradorDeTablatura.MODO_PENTATONICA_BLUES;
            tonicaFinal = par.menor();
            notasDaEscala = Escala.PENTATONICA_BLUES.calcularNotas(tonicaFinal);
            isPentatonica = true;
        } else if ("5".equals(modoOpcao)) {
            // Menor harmonica: mesma tonica do par relativo, porem com a 7maior
            // (T 2 3b 4 5 6b 7+, ex.: A B C D E F G#).
            modoNome = GeradorDeTablatura.MODO_MENOR_HARMONICA;
            tonicaFinal = par.menor();
            notasDaEscala = Escala.MENOR_HARMONICA.calcularNotas(tonicaFinal);
            isPentatonica = false;
        } else {
            modoNome = "Maior";
            tonicaFinal = par.maior();
            notasDaEscala = Escala.MAIOR.calcularNotas(tonicaFinal);
            isPentatonica = false;
        }

        // A convencao de escrita (com benois) acompanha a armadura: para modos
        // menor/pentatonica a armadura e a da relativa maior do tom escolhido.
        Nota tonicaMaiorParaConvencao = "1".equals(modoOpcao)
                ? tonicaFinal
                : Nota.getNotaPorValor(par.menor().getValor() + 3);
        boolean usarNotacaoBemol = isTonicaDeBemol(tonicaMaiorParaConvencao);
        String nomeExibicaoTonicaFinal = formatarTonicaParaExibicao(tonicaFinal, usarNotacaoBemol);

        String titulo;
        if (isPentatonica) {
            if (GeradorDeTablatura.MODO_PENTATONICA_BLUES.equals(modoNome)) {
                titulo = "Pentatonica Blues " + nomeExibicaoTonicaFinal + "m";
            } else {
                titulo = "Pentatonica de " + nomeExibicaoTonicaFinal + "m";
            }
        } else {
            titulo = nomeExibicaoTonicaFinal + " " + modoNome;
        }
        // Na menor harmonica os nomes dos graus ja saem com a 7maior escrita
        // certa (ex.: C# em vez de Db), entao os padroes usam esses nomes.
        boolean menorHarmonica = GeradorDeTablatura.MODO_MENOR_HARMONICA.equals(modoNome);
        List<String> nomesDosGraus = menorHarmonica
                ? nomesDaMenorHarmonica(notasDaEscala, usarNotacaoBemol)
                : nomesDosGraus(notasDaEscala, usarNotacaoBemol);
        String notasFormatadas = "[" + String.join(", ", nomesDosGraus) + "]";

        return switch (opcaoPadrao) {
            case "2" -> {
                List<String> triades = menorHarmonica
                        ? geradorDePadroes.gerarTriadesPorNomes(nomesDosGraus, modoNome)
                        : geradorDePadroes.gerarTriades(notasDaEscala, modoNome).stream()
                        .map(item -> formatarTriadeParaExibicao(item, usarNotacaoBemol))
                        .toList();
                yield new ResultadoEstudo(titulo, notasFormatadas, "Sequencia de Triades", triades, "", "");
            }
            case "3" -> {
                String nomePadrao = "Shapes-Tab";
                String[] tab3Npc = GeradorDeTablatura.gerar(tonicaFinal.toString(), modoNome);
                String tablatura = String.join(System.lineSeparator(), tab3Npc);
                String alphaTex = GeradorDeAlphaTex.gerar(titulo, tonicaFinal.toString(), modoNome);
                yield new ResultadoEstudo(titulo, notasFormatadas, nomePadrao, List.of(), tablatura, alphaTex);
            }
            default -> {
                List<String> sequencia = menorHarmonica
                        ? geradorDePadroes.gerarSequenciaPorNomes(nomesDosGraus, 3)
                        : geradorDePadroes.gerarSequencia(notasDaEscala, 3).stream()
                        .map(item -> formatarTriadeParaExibicao(item, usarNotacaoBemol))
                        .toList();
                yield new ResultadoEstudo(titulo, notasFormatadas, "Sequencia de 3 Notas", sequencia, "", "");
            }
        };
    }

    /**
     * Aceita o par relativo (ex.: Am/C) e, apenas na menor harmonica, tambem a
     * tonica menor sozinha (ex.: Am), ja que essa escala nao tem relativo maior.
     */
    private ParRelativo parseParRelativo(String modoOpcao, String tonicaEntrada) {
        if (tonicaEntrada == null || tonicaEntrada.isBlank()) {
            throw new IllegalArgumentException("Tonica invalida");
        }
        String[] partes = tonicaEntrada.trim().split("/");
        if (partes.length == 1 && "5".equals(modoOpcao)) {
            Nota menor = parseTonica(partes[0]);
            return new ParRelativo(menor, Nota.getNotaPorValor(menor.getValor() + 3));
        }
        if (partes.length != 2) {
            throw new IllegalArgumentException("Tonica deve ser um par relativo (ex.: Am/C)");
        }
        Nota menor = parseTonica(partes[0]);
        Nota maior = parseTonica(partes[1]);
        if (maior.getValor() != (menor.getValor() + 3) % 12) {
            throw new IllegalArgumentException("Par relativo invalido: " + tonicaEntrada);
        }
        return new ParRelativo(menor, maior);
    }

    private Nota parseTonica(String tonicaEntrada) {
        if (tonicaEntrada == null || tonicaEntrada.isBlank()) {
            throw new IllegalArgumentException("Tonica invalida");
        }
        String entrada = tonicaEntrada.trim().toUpperCase().replaceFirst("M$", "");
        String enumName = BEMOL_TO_SHARP.getOrDefault(entrada, entrada.replace("#", "_SHARP"));
        return Nota.valueOf(enumName);
    }

    private boolean isTonicaDeBemol(Nota tonicaMaior) {
        return TONICAS_MAIORES_DE_BEMOL.contains(tonicaMaior);
    }

    private String formatarTonicaParaExibicao(Nota tonica, boolean usarNotacaoBemol) {
        String nomeNota = tonica.toString().replace("_SHARP", "#");
        if (usarNotacaoBemol) {
            return SHARP_TO_BEMOL.getOrDefault(nomeNota, nomeNota);
        }
        return nomeNota;
    }

    private List<String> nomesDosGraus(List<Nota> notasDaEscala, boolean usarNotacaoBemol) {
        return notasDaEscala.stream()
                .map(nota -> formatarTonicaParaExibicao(nota, usarNotacaoBemol))
                .toList();
    }

    /**
     * Na menor harmonica a ultima nota e a 7maior, escrita a partir da 7ma
     * menor: bemol vira natural (Bb -> B), natural vira sustenido (G -> G#) e,
     * quando a 7ma menor ja e sustenida, mantem a forma enarmonica legivel
     * (Fx -> G). Assim a partitura e os padroes mostram o acidente certo.
     */
    private List<String> nomesDaMenorHarmonica(List<Nota> notasDaEscala, boolean usarNotacaoBemol) {
        List<String> nomes = new ArrayList<>(notasDaEscala.size());
        for (int i = 0; i < notasDaEscala.size(); i++) {
            Nota nota = notasDaEscala.get(i);
            nomes.add(i == notasDaEscala.size() - 1
                    ? nomeDaSetimaMaior(nota, usarNotacaoBemol)
                    : formatarTonicaParaExibicao(nota, usarNotacaoBemol));
        }
        return nomes;
    }

    private String nomeDaSetimaMaior(Nota setimaMaior, boolean usarNotacaoBemol) {
        String setimaMenor = formatarTonicaParaExibicao(
                Nota.getNotaPorValor(setimaMaior.getValor() - 1), usarNotacaoBemol);
        if (setimaMenor.endsWith("b")) {
            return setimaMenor.substring(0, setimaMenor.length() - 1);
        }
        if (setimaMenor.endsWith("#")) {
            return formatarTonicaParaExibicao(setimaMaior, usarNotacaoBemol);
        }
        return setimaMenor + "#";
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
