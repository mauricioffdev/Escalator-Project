package br.com.escalator;

import br.com.escalator.model.Escala;
import br.com.escalator.model.Nota;
import br.com.escalator.service.GeradorDePadroes;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.Map;

public class App {

    // --- Constantes de Configuração e Mapeamento ---

    // Mapeamento de Bemol (String Exibição) para Sustenido (String Enum Interna)
    private static final Map<String, String> BEMOL_TO_SHARP = Map.of(
            "BB", "A_SHARP",
            "EB", "D_SHARP",
            "AB", "G_SHARP",
            "DB", "C_SHARP",
            "GB", "F_SHARP",
            "CB", "B"
    );

    // Mapeamento de Sustenido (String Enum Interna) para Bemol (String Exibição)
    private static final Map<String, String> SHARP_TO_BEMOL = Map.of(
            "A#", "Bb",
            "D#", "Eb",
            "G#", "Ab",
            "C#", "Db",
            "F#", "Gb"
    );

    // As duas sequências para exibir como guia de input
    private static final String CICLO_SUSTENIDOS_EXIBICAO = "C, G, D, A, E, B, F#, C#";
    private static final String CICLO_BEMOIS_EXIBICAO = "F, Bb, Eb, Ab, Db, Gb, Cb";

    // Tônicas maiores que devem usar notação de bemol em toda a sua família.
    private static final List<Nota> TONICAS_MAIORES_DE_BEMOL = List.of(
            Nota.F, Nota.A_SHARP, Nota.D_SHARP, Nota.G_SHARP, Nota.C_SHARP, Nota.F_SHARP
    );


    // --- Metodo Principal e Loop de Execução ---

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GeradorDePadroes gerador = new GeradorDePadroes();
        boolean rodando = true;

        System.out.println();
        System.out.println("--- Escalator: Gerador de Padrões de Guitarra (Escalas Diatônicas) ---");

        do {
            System.out.println("\n-------------------------------------------");
            System.out.println("Escolha o Modo de Estudo:");
            System.out.println("1 - Escala Maior");
            System.out.println("2 - Escala Menor Natural (Relativa)");
            System.out.print("Sua escolha: ");
            String modoOpcao = scanner.nextLine();

            String modoNome;
            Escala escalaBase;
            int semitonsRelativos = 0;

            if (modoOpcao.equals("1")) {
                modoNome = "Maior";
                escalaBase = Escala.MAIOR;
            } else if (modoOpcao.equals("2")) {
                modoNome = "Menor Natural (Relativa)";
                escalaBase = Escala.MENOR_NATURAL;
                semitonsRelativos = 9;
            } else {
                System.out.println("Opção de modo inválida.");
                continue;
            }

            Nota tonicaBase = coletarTonica(scanner);
            if (tonicaBase == null) {
                rodando = false;
                break;
            }

            // Flag de notação baseada na tônica original
            boolean usarNotacaoBemol = isTonicaDeBemol(tonicaBase);

            Nota tonicaFinal;
            if (semitonsRelativos > 0) {
                int valorTonicaBase = tonicaBase.getValor();
                tonicaFinal = Nota.getNotaPorValor(valorTonicaBase + semitonsRelativos);
                System.out.println("\nCalculando a Escala Relativa Menor...");
                System.out.println(formatarTonicaParaExibicao(tonicaBase, usarNotacaoBemol) + " Maior -> Relativa Menor: " + formatarTonicaParaExibicao(tonicaFinal, usarNotacaoBemol));
            } else {
                tonicaFinal = tonicaBase;
            }

            List<Nota> notasDaEscala = escalaBase.calcularNotas(tonicaFinal);

            System.out.println("\n-------------------------------------------");
            System.out.println("Opções de Padrão para " + formatarTonicaParaExibicao(tonicaFinal, usarNotacaoBemol) + " " + modoNome + ":");
            System.out.println("1 - Padrão Sequência de 3 Notas");
            System.out.println("2 - Sequência de Tríades (Campo Harmônico)");
            System.out.print("Escolha uma opção: ");
            String opcao = scanner.nextLine();

            System.out.println("\n----------------- Saída para " + formatarTonicaParaExibicao(tonicaFinal, usarNotacaoBemol) + " " + modoNome + " -----------------");
            String notasFormatadas = formatarNotasDaEscala(notasDaEscala, usarNotacaoBemol);
            System.out.println("Notas: " + notasFormatadas + "\n");

            switch (opcao) {
                case "1":
                    int notasPorSequencia = 3;
                    List<String> padrao = gerador.gerarSequencia(notasDaEscala, notasPorSequencia);
                    System.out.println("PADRÃO ESCOLHIDO: Sequência de " + notasPorSequencia + " notas:");
                    System.out.println(formatarPadraoParaExibicao(padrao, usarNotacaoBemol));
                    break;
                case "2":
                    List<String> triades = gerador.gerarTriades(notasDaEscala, modoNome);
                    System.out.println("PADRÃO ESCOLHIDO: Tríades Diatônicas (Campo Harmônico):");
                    triades.forEach(triade -> System.out.println(formatarTriadeParaExibicao(triade, usarNotacaoBemol)));
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

    // --- Metodos Auxiliares para Interatividade e Formatação ---

    private static Nota coletarTonica(Scanner scanner) {
        Nota tonica = null;
        while (tonica == null) {
            System.out.println("\n-------------------------------------------");
            System.out.println("Notas disponíveis (Ciclos):");
            System.out.println("Sustenidos: [" + CICLO_SUSTENIDOS_EXIBICAO + "]");
            System.out.println("Bemóis:     [" + CICLO_BEMOIS_EXIBICAO + "]");
            System.out.print("Digite a Tônica (ex: C, Bb, G#) ou 'Sair': ");

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

    /*
     * Verifica se a tônica inicial (maior) pertence ao ciclo dos bemóis.
     * Toda a família da escala (maior e relativa menor) será formatada com bemóis se essa condição for verdadeira.
     */
    private static boolean isTonicaDeBemol(Nota tonicaOriginal) {
        return TONICAS_MAIORES_DE_BEMOL.contains(tonicaOriginal);
    }

    /*
     * Formata o nome de uma Tônica.
     */
    private static String formatarTonicaParaExibicao(Nota tonica, boolean usarNotacaoBemol) {
        if (usarNotacaoBemol) {
            return SHARP_TO_BEMOL.getOrDefault(tonica.toString(), tonica.toString());
        } else {
            return tonica.toString();
        }
    }

    /*
     * Converte a lista de notas para a notação musical usando # ou b.
     */
    private static String formatarNotasDaEscala(List<Nota> notasDaEscala, boolean usarNotacaoBemol) {
        List<String> notasFormatadas = notasDaEscala.stream()
                .map(nota -> {
                    if (usarNotacaoBemol) {
                        return SHARP_TO_BEMOL.getOrDefault(nota.toString(), nota.toString());
                    } else {
                        return nota.toString();
                    }
                })
                .collect(Collectors.toList());

        return "[" + String.join(", ", notasFormatadas) + "]";
    }

    /*
     * Formata os padrões de sequência, trocando # por 'b' onde for necessário.
     */
    private static String formatarPadraoParaExibicao(List<String> padrao, boolean usarNotacaoBemol) {
        List<String> padraoFormatado = padrao.stream()
                .map(sequencia -> {
                    String seq = sequencia;
                    if (usarNotacaoBemol) {
                        for (Map.Entry<String, String> entry : SHARP_TO_BEMOL.entrySet()) {
                            seq = seq.replace(entry.getKey(), entry.getValue());
                        }
                    }
                    return seq;
                })
                .collect(Collectors.toList());

        return String.join(", ", padraoFormatado);
    }

    /*
     * Formata a saída das tríades, trocando # por 'b' onde for necessário.
     */
    private static String formatarTriadeParaExibicao(String triade, boolean usarNotacaoBemol) {
        if (!usarNotacaoBemol) {
            return triade;
        }

        String triadeFormatada = triade;
        for (Map.Entry<String, String> entry : SHARP_TO_BEMOL.entrySet()) {
            triadeFormatada = triadeFormatada.replace(entry.getKey(), entry.getValue());
        }
        return triadeFormatada;
    }
}