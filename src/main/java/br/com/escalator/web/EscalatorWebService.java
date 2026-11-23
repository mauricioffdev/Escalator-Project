package br.com.escalator.web;

import br.com.escalator.model.Armadura;
import br.com.escalator.model.Escala;
import br.com.escalator.model.Nota;
import br.com.escalator.service.GeradorDeAlphaTex;
import br.com.escalator.service.GeradorDeLicks;
import br.com.escalator.service.GeradorDePadroes;
import br.com.escalator.service.GeradorDeTablatura;
import br.com.escalator.service.ModoMaior;
import br.com.escalator.service.ModoMenorHarmonica;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
public class EscalatorWebService {

    /** Entrada do usuario em bemol (minusculas) para o nome do enum. */
    private static final Map<String, String> BEMOL_TO_SHARP = Map.of(
            "BB", "A_SHARP", "EB", "D_SHARP", "AB", "G_SHARP",
            "DB", "C_SHARP", "GB", "F_SHARP", "CB", "B"
    );

    /**
     * Par relativo escolhido no dropdown. {@code usarBemol} vem do nome digitado
     * (ciclo das quartas) e nao da altitude: F# e C# sao sustenidos, Gb e Cb
     * sao bemois, mesmo quando soam igual.
     */
    private record ParRelativo(Nota menor, Nota maior, boolean usarBemol) {
    }

    private final GeradorDePadroes geradorDePadroes = new GeradorDePadroes();

    public ResultadoEstudo gerar(String modoOpcao, String tonicaEntrada, String opcaoPadrao) {
        // Os 7 shapes da menor harmonica "viraram padroes" do mesmo jeito dos
        // gregos. Um shape so faz sentido dentro do modo 5: sem esta guarda a
        // combinacao cruzada cairia no case default e devolveria uma sequencia
        // de 3 notas generica, sem tablatura, em vez do shape escolhido.
        Optional<ModoMenorHarmonica> modoMenorHarmonica = ModoMenorHarmonica.porPadrao(opcaoPadrao);
        if (modoMenorHarmonica.isPresent() && !"5".equals(modoOpcao)) {
            throw new IllegalArgumentException(
                    "Este shape esta disponivel apenas para a menor harmonica.");
        }

        ParRelativo par = parseParRelativo(modoOpcao, tonicaEntrada);

        String modoNome;
        Nota tonicaFinal;
        List<Nota> notasDaEscala;
        boolean isPentatonica;
        // A escala maior agora e um unico modo de estudo: o grego e escolhido
        // no seletor de padrao. As opcoes legadas seguem funcionando por URL.
        Optional<ModoMaior> modoGrego = "1".equals(modoOpcao)
                ? ModoMaior.porPadrao(opcaoPadrao)
                : Optional.empty();
        var modoMaior = modoGrego.isPresent() ? modoGrego : ModoMaior.porOpcao(modoOpcao);

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
            // Menor harmonica: mesma escala de 7 notas para os 7 shapes. Sem um
            // shape escolhido, mantem o desenho original (Eolio 7+) e a tonica
            // digitada, preservando URLs e titulos ja validados.
            ModoMenorHarmonica modo = modoMenorHarmonica.orElse(ModoMenorHarmonica.EOLIO_7);
            modoNome = modo.modoTablatura();
            tonicaFinal = Nota.getNotaPorValor(par.menor().getValor() + modo.grau());
            notasDaEscala = escalaMenorHarmonicaRotacionada(par.menor(), tonicaFinal);
            isPentatonica = false;
        } else if (modoMaior.isPresent()) {
            ModoMaior modo = modoMaior.get();
            modoNome = modo.modoTablatura();
            tonicaFinal = Nota.getNotaPorValor(par.maior().getValor() + modo.grau());
            notasDaEscala = escalaMaiorRotacionada(par.maior(), tonicaFinal);
            isPentatonica = false;
        } else {
            modoNome = "Maior";
            tonicaFinal = par.maior();
            notasDaEscala = Escala.MAIOR.calcularNotas(tonicaFinal);
            isPentatonica = false;
        }

        // Toda a escrita (titulo, graus, triades, sequencias e armadura) vem da
        // MESMA soletracao diatonica do campo harmonico em Armadura. O ciclo e o
        // do nome digitado: quintas -> sustenidos, quartas -> bemois.
        boolean usarBemol = par.usarBemol();
        boolean menorHarmonica = "5".equals(modoOpcao);
        Nota parente = par.maior();
        // Na menor harmonica o root de alguns modos e a 7ma elevada do parente
        // (ex.: G# em Am). O titulo deve usar a MESMA soletracao da escala,
        // senao aparece "Db Diminuto" com primeira nota "C#" na mesma saida.
        String nomeExibicaoTonicaFinal = menorHarmonica
                && tonicaFinal.getValor() == (parente.getValor() + 8) % 12
                ? nomeDaSetimaMaior(tonicaFinal, parente, usarBemol)
                : Armadura.nomeDaNota(tonicaFinal, parente, usarBemol);

        String titulo;
        if (isPentatonica) {
            // O h2 do resultado usa text-transform: uppercase, entao um "m"
            // minusculo viraria "M" e confundiria com modo maior. Escrever
            // "A Menor" mantem a informacao legivel em qualquer fonte.
            if (GeradorDeTablatura.MODO_PENTATONICA_BLUES.equals(modoNome)) {
                titulo = "Pentatonica Blues " + nomeExibicaoTonicaFinal + " Menor";
            } else {
                titulo = "Pentatonica de " + nomeExibicaoTonicaFinal + " Menor";
            }
        } else {
            titulo = nomeExibicaoTonicaFinal + " " + modoNome;
        }

        List<String> nomesDosGraus = menorHarmonica
                ? nomesDaMenorHarmonica(notasDaEscala, parente, usarBemol)
                : nomesDosGraus(notasDaEscala, parente, usarBemol);
        String notasFormatadas = "[" + String.join(", ", nomesDosGraus) + "]";

        // As pentatonicas nao tem sequencia de intervalos nos PDFs. Na escala
        // maior sem grego explicito, Jonio e a referencia; na menor harmonica,
        // o desenho original (Eolio 7+) e a referencia quando nenhum shape e
        // escolhido.
        String intervalos = "";
        if ("5".equals(modoOpcao)) {
            intervalos = modoMenorHarmonica.orElse(ModoMenorHarmonica.EOLIO_7).intervalos();
        } else if ("1".equals(modoOpcao) || modoMaior.isPresent()) {
            intervalos = modoMaior.orElse(ModoMaior.JONIO).intervalos();
        }

        // Escolher um grego (ou um modo da menor harmonica) no padrao e o
        // equivalente a pedir o shape 3NPC dele. O antigo "Shape 1" da escala
        // maior tambem e Jonio: apenas troca o rotulo para nao mostrar o mesmo
        // modo duas vezes no resultado.
        String padraoEfetivo;
        String nomePadrao;
        if (modoGrego.isPresent()) {
            padraoEfetivo = "3";
            nomePadrao = modoGrego.get().rotulo();
        } else if (modoMenorHarmonica.isPresent()) {
            padraoEfetivo = "3";
            nomePadrao = modoMenorHarmonica.get().rotulo();
        } else {
            padraoEfetivo = opcaoPadrao;
            nomePadrao = "1".equals(modoOpcao)
                    ? modoMaior.orElse(ModoMaior.JONIO).rotulo()
                    : "Shape 1";
        }

        return switch (padraoEfetivo) {
            case "2" -> {
                // Os nomes ja chegam soletrados no ciclo certo; nao ha segunda
                // conversao por altitude que possa gerar enarmonia divergente.
                List<String> triades = geradorDePadroes.gerarTriadesPorNomes(nomesDosGraus, modoNome);
                yield new ResultadoEstudo(titulo, notasFormatadas, "Sequencia de Triades",
                        triades, "", "", intervalos);
            }
            case "3" -> gerarResultadoTablatura(
                    titulo, notasFormatadas, tonicaFinal, nomePadrao, modoNome, usarBemol, intervalos);
            case "4" -> gerarResultadoShape(titulo, notasFormatadas, tonicaFinal, modoNome,
                    usarBemol, isPentatonica, "Shape 2",
                    GeradorDeTablatura.MODO_PENTATONICA_M7_SHAPE2,
                    GeradorDeTablatura.MODO_PENTATONICA_BLUES_SHAPE2, intervalos);
            case "5" -> gerarResultadoShape(titulo, notasFormatadas, tonicaFinal, modoNome,
                    usarBemol, isPentatonica, "Shape 3",
                    GeradorDeTablatura.MODO_PENTATONICA_M7_SHAPE3,
                    GeradorDeTablatura.MODO_PENTATONICA_BLUES_SHAPE3, intervalos);
            case "6" -> gerarResultadoShape(titulo, notasFormatadas, tonicaFinal, modoNome,
                    usarBemol, isPentatonica, "Shape 4",
                    GeradorDeTablatura.MODO_PENTATONICA_M7_SHAPE4,
                    GeradorDeTablatura.MODO_PENTATONICA_BLUES_SHAPE4, intervalos);
            case "7" -> gerarResultadoShape(titulo, notasFormatadas, tonicaFinal, modoNome,
                    usarBemol, isPentatonica, "Shape 5",
                    GeradorDeTablatura.MODO_PENTATONICA_M7_SHAPE5,
                    GeradorDeTablatura.MODO_PENTATONICA_BLUES_SHAPE5, intervalos);
            case "8" -> gerarResultadoShape(titulo, notasFormatadas, tonicaFinal, modoNome,
                    usarBemol, isPentatonica, "Shape Extra",
                    GeradorDeTablatura.MODO_PENTATONICA_M7_EXTRA,
                    GeradorDeTablatura.MODO_PENTATONICA_BLUES_EXTRA, intervalos);
            default -> {
                List<String> sequencia = geradorDePadroes.gerarSequenciaPorNomes(nomesDosGraus, 3);
                yield new ResultadoEstudo(titulo, notasFormatadas, "Sequencia de 3 Notas",
                        sequencia, "", "", intervalos);
            }
        };
    }

    /** Gera o lick fixo escolhido no dropdown de Licks. */
    public ResultadoEstudo gerarLick(String lickOpcao) {
        GeradorDeLicks.Lick lick = GeradorDeLicks.buscar(lickOpcao);
        if (lick == null) {
            throw new IllegalArgumentException("Lick invalido");
        }
        return new ResultadoEstudo(
                lick.titulo(),
                lick.notasFormatadas(),
                lick.nomePadrao(),
                List.of(),
                lick.tablatura(),
                lick.alphaTex());
    }

    /** Gera a tablatura ASCII e o AlphaTex para o shape/modo informado. */
    private ResultadoEstudo gerarResultadoTablatura(String titulo, String notasFormatadas,
                                                     Nota tonica, String nomePadrao,
                                                     String modoParaTablatura, boolean usarBemol, String intervalos) {
        String[] linhasTablatura = GeradorDeTablatura.gerar(tonica.toString(), modoParaTablatura);
        String tablatura = String.join(System.lineSeparator(), linhasTablatura);
        String alphaTex = GeradorDeAlphaTex.gerar(titulo, tonica.toString(), modoParaTablatura, usarBemol);
        return new ResultadoEstudo(titulo, notasFormatadas, nomePadrao, List.of(), tablatura, alphaTex, intervalos);
    }

    /**
     * Resolve o shape pentatonico escolhido (m7 ou blues) e gera o resultado.
     */
    private ResultadoEstudo gerarResultadoShape(String titulo, String notasFormatadas, Nota tonica,
                                                 String modoNome, boolean usarBemol, boolean isPentatonica,
                                                 String nomePadrao, String modoM7, String modoBlues, String intervalos) {
        if (!isPentatonica) {
            throw new IllegalArgumentException(
                    "Este shape esta disponivel apenas para as pentatonicas (m7 ou blues).");
        }
        String modoShape = GeradorDeTablatura.MODO_PENTATONICA_BLUES.equals(modoNome) ? modoBlues : modoM7;
        return gerarResultadoTablatura(titulo, notasFormatadas, tonica, nomePadrao, modoShape, usarBemol, intervalos);
    }

    /** Graus da escala maior a partir da tonica do modo (ex.: D Dorico começa em D). */
    private List<Nota> escalaMaiorRotacionada(Nota parente, Nota tonicaModo) {
        List<Nota> notas = Escala.MAIOR.calcularNotas(parente);
        int indice = notas.indexOf(tonicaModo);
        if (indice > 0) {
            Collections.rotate(notas, -indice);
        }
        return notas;
    }

    /** Graus da menor harmonica a partir do root do shape (ex.: B Locrio 6). */
    private List<Nota> escalaMenorHarmonicaRotacionada(Nota tonica, Nota tonicaModo) {
        List<Nota> notas = Escala.MENOR_HARMONICA.calcularNotas(tonica);
        int indice = notas.indexOf(tonicaModo);
        if (indice > 0) {
            Collections.rotate(notas, -indice);
        }
        return notas;
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
            return new ParRelativo(menor, Nota.getNotaPorValor(menor.getValor() + 3),
                    Armadura.usarBemol(partes[0]));
        }
        if (partes.length != 2) {
            throw new IllegalArgumentException("Tonica deve ser um par relativo (ex.: Am/C)");
        }
        Nota menor = parseTonica(partes[0]);
        Nota maior = parseTonica(partes[1]);
        if (maior.getValor() != (menor.getValor() + 3) % 12) {
            throw new IllegalArgumentException("Par relativo invalido: " + tonicaEntrada);
        }
        return new ParRelativo(menor, maior, Armadura.usarBemol(partes[1]));
    }

    private Nota parseTonica(String tonicaEntrada) {
        if (tonicaEntrada == null || tonicaEntrada.isBlank()) {
            throw new IllegalArgumentException("Tonica invalida");
        }
        String entrada = tonicaEntrada.trim().toUpperCase().replaceFirst("M$", "");
        String enumName = BEMOL_TO_SHARP.getOrDefault(entrada, entrada.replace("#", "_SHARP"));
        return Nota.valueOf(enumName);
    }

    /** Nomes diatonicos no campo harmonico da relativa maior. */
    private List<String> nomesDosGraus(List<Nota> notasDaEscala, Nota parente, boolean usarBemol) {
        return notasDaEscala.stream()
                .map(nota -> Armadura.nomeDaNota(nota, parente, usarBemol))
                .toList();
    }

    /**
     * Na menor harmonica a ultima nota e a 7maior, escrita a partir da 7ma
     * menor (5o grau da parente maior): bemol vira natural (Bb -> B) e natural
     * vira sustenido (G -> G#). Quando a 7ma menor ja e sustenida, mantem a
     * forma enarmonica legivel do proprio campo harmonico.
     */
    private List<String> nomesDaMenorHarmonica(List<Nota> notasDaEscala, Nota parente, boolean usarBemol) {
        // A 7maior fica um semiton acima da 7ma menor do parente (grau 4),
        // qualquer que seja a rotacao do modo. Identifica-la pela altitude
        // evita aplicar a regra na ultima nota quando a escala comeca em outro
        // grau (ex.: G# encontra no meio da sequencia no Locrio 6 de Am).
        int valorSetimaMaior = (parente.getValor() + 8) % 12;
        List<String> nomes = new ArrayList<>(notasDaEscala.size());
        for (Nota nota : notasDaEscala) {
            nomes.add(nota.getValor() == valorSetimaMaior
                    ? nomeDaSetimaMaior(nota, parente, usarBemol)
                    : Armadura.nomeDaNota(nota, parente, usarBemol));
        }
        return nomes;
    }

    private String nomeDaSetimaMaior(Nota setimaMaior, Nota parente, boolean usarBemol) {
        String setimaMenor = Armadura.nomeDoGrau(parente, 4, usarBemol);
        if (setimaMenor.endsWith("b")) {
            return setimaMenor.substring(0, setimaMenor.length() - 1);
        }
        if (setimaMenor.endsWith("#")) {
            return Armadura.nomeDaNota(setimaMaior, parente, usarBemol);
        }
        return setimaMenor + "#";
    }
}
