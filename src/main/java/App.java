package br.com.escalator;

import br.com.escalator.model.Escala;
import br.com.escalator.model.Nota;
import br.com.escalator.service.GeradorDePadroes;
import br.com.escalator.service.GeradorDeTablatura;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class App {

    private static final Map<String, String> BEMOL_TO_SHARP = Map.of(
            "BB", "A_SHARP", "EB", "D_SHARP", "AB", "G_SHARP", "DB", "C_SHARP", "GB", "F_SHARP", "CB", "B"
    );
    private static final Map<String, String> SHARP_TO_BEMOL = Map.of(
            "A#", "Bb", "D#", "Eb", "G#", "Ab", "C#", "Db", "F#", "Gb"
    );
    private static final String CICLO_SUSTENIDOS_EXIBICAO = "C, G, D, A, E, B, F#, C#";
    private static final String CICLO_BEMOIS_EXIBICAO = "F, Bb, Eb, Ab, Db, Gb, Cb";
    private static final List<Nota> TONICAS_MAIORES_DE_BEMOL = List.of(
            Nota.F, Nota.A_SHARP, Nota.D_SHARP, Nota.G_SHARP, Nota.C_SHARP, Nota.F_SHARP
    );

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GeradorDePadroes gerador = new GeradorDePadroes();
        boolean rodando = true;

        System.out.println("\n--- Escalator: Gerador de Padrões de Guitarra (Escalas Diatônicas) ---");

        do {
            System.out.println("\n-------------------------------------------");
            System.out.println("Escolha o Modo de Estudo:");
            System.out.println("1 - Escala Maior");
            System.out.println("2 - Escala Menor Natural (Relativa)");
            System.out.print("Sua escolha: ");
            String modoOpcaoStr = scanner.nextLine();

            String modoNome;
            boolean isModoMenor = false;

            switch (modoOpcaoStr) {
                case "1":
                    modoNome = "Maior";
                    break;
                case "2":
                    modoNome = "Menor Natural (Relativa)";
                    isModoMenor = true;
                    break;
                default:
                    System.out.println("Opção de modo inválida.");
                    pressioneEnterParaContinuar(scanner);
                    continue;
            }

            if (isModoMenor) {
                System.out.println("\nPara a escala Menor, por favor, digite a TÔNICA DA ESCALA MAIOR relativa.");
                System.out.println("(Ex: para F# Menor, digite A. Para A Menor, digite C)");
            }

            Nota tonicaBase = coletarTonica(scanner);
            if (tonicaBase == null) {
                rodando = false;
                break;
            }

            boolean usarNotacaoBemol = isTonicaDeBemol(tonicaBase);
            Nota tonicaFinal = tonicaBase;
            String nomeExibicaoTonicaFinal = formatarTonicaParaExibicao(tonicaBase, usarNotacaoBemol);

            if (isModoMenor) {
                int valorTonicaBase = tonicaBase.getValor();
                tonicaFinal = Nota.getNotaPorValor(valorTonicaBase + 9);
                nomeExibicaoTonicaFinal = formatarTonicaParaExibicao(tonicaFinal, usarNotacaoBemol);
                System.out.println("\nCalculando a Escala Relativa Menor...");
                System.out.println(formatarTonicaParaExibicao(tonicaBase, usarNotacaoBemol) + " Maior -> Relativa Menor: " + nomeExibicaoTonicaFinal);
            }

            List<Nota> notasDaEscala = Escala.MAIOR.calcularNotas(tonicaBase);

            if (isModoMenor) {
                int indiceTonicaMenor = notasDaEscala.indexOf(tonicaFinal);
                if (indiceTonicaMenor != -1) {
                    Collections.rotate(notasDaEscala, -indiceTonicaMenor);
                }
            }

            System.out.println("\n-------------------------------------------");
            System.out.println("Opções de Padrão para " + nomeExibicaoTonicaFinal + " " + modoNome + ":");
            System.out.println("1 - Padrão Sequência de 3 Notas");
            System.out.println("2 - Sequência de Tríades (Campo Harmônico)");
            System.out.println("3 - 3 Notas por Corda (Tablatura)");
            System.out.print("Escolha uma opção: ");
            String opcao = scanner.nextLine();

            System.out.println("\n----------------- Saída para " + nomeExibicaoTonicaFinal + " " + modoNome + " -----------------");
            String notasFormatadas = formatarNotasDaEscala(notasDaEscala, usarNotacaoBemol);
            System.out.println("Notas: " + notasFormatadas + "\n");

            switch (opcao) {
                case "1":
                    List<String> padrao = gerador.gerarSequencia(notasDaEscala, 3);
                    System.out.println("PADRÃO ESCOLHIDO: Sequência de 3 notas:");
                    System.out.println(formatarPadraoParaExibicao(padrao, usarNotacaoBemol));
                    break;
                case "2":
                    List<String> triades = gerador.gerarTriades(notasDaEscala, modoNome);
                    System.out.println("PADRÃO ESCOLHIDO: Tríades Diatônicas (Campo Harmônico):");
                    triades.forEach(triade -> System.out.println(formatarTriadeParaExibicao(triade, usarNotacaoBemol)));
                    break;
                case "3":
                    String[] tab3Npc = GeradorDeTablatura.gerar(tonicaFinal.toString(), modoNome);
                    imprimirTablatura("3 Notas por Corda", tab3Npc);
                    break;
                default:
                    System.out.println("Opção de padrão inválida. Exibindo apenas a escala.");
            }

            System.out.println("\n-------------------------------------------");
            System.out.println("Pressione ENTER para escolher outra escala ou digite 'Sair'.");
            if (scanner.nextLine().toUpperCase().equals("SAIR")) {
                rodando = false;
            }
        } while (rodando);

        System.out.println("Estudo finalizado! Parabéns!");
        scanner.close();
    }

    private static void pressioneEnterParaContinuar(Scanner scanner) {
        System.out.println("Pressione ENTER para continuar...");
        scanner.nextLine();
    }

    private static void imprimirTablatura(String titulo, String[] tablatura) {
        System.out.println("PADRÃO ESCOLHIDO: " + titulo);
        for (String linha : tablatura) {
            System.out.println(linha);
        }
    }

    private static Nota coletarTonica(Scanner scanner) {
        Nota tonica = null;
        while (tonica == null) {
            System.out.println("\n-------------------------------------------");
            System.out.println("Notas disponíveis (Ciclos):");
            System.out.println("Sustenidos: [" + CICLO_SUSTENIDOS_EXIBICAO + "]");
            System.out.println("Bemóis:     [" + CICLO_BEMOIS_EXIBICAO + "]");
            System.out.print("Digite a Tônica MAIOR (ex: C, Bb, A) ou 'Sair': ");
            String entrada = scanner.nextLine().toUpperCase();
            if (entrada.equals("SAIR")) return null;
            String enumName = BEMOL_TO_SHARP.get(entrada);
            if (enumName == null) {
                enumName = entrada.replace("#", "_SHARP");
            }
            try {
                tonica = Nota.valueOf(enumName);
            } catch (IllegalArgumentException e) {
                System.out.println("Entrada inválida. Por favor, digite uma das notas listadas.");
            }
        }
        return tonica;
    }

    private static boolean isTonicaDeBemol(Nota tonicaOriginal) {
        return TONICAS_MAIORES_DE_BEMOL.contains(tonicaOriginal);
    }

    private static String formatarTonicaParaExibicao(Nota tonica, boolean usarNotacaoBemol) {
        String nomeNota = tonica.toString().replace("_SHARP", "#");
        if (usarNotacaoBemol) {
            return SHARP_TO_BEMOL.getOrDefault(nomeNota, nomeNota);
        }
        return nomeNota;
    }

    private static String formatarNotasDaEscala(List<Nota> notasDaEscala, boolean usarNotacaoBemol) {
        List<String> notasFormatadas = notasDaEscala.stream()
                .map(nota -> formatarTonicaParaExibicao(nota, usarNotacaoBemol))
                .collect(Collectors.toList());
        return "[" + String.join(", ", notasFormatadas) + "]";
    }

    private static String formatarPadraoParaExibicao(List<String> padrao, boolean usarNotacaoBemol) {
        return padrao.stream()
                .map(sequencia -> formatarTriadeParaExibicao(sequencia, usarNotacaoBemol))
                .collect(Collectors.joining(", "));
    }

    private static String formatarTriadeParaExibicao(String triade, boolean usarNotacaoBemol) {
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