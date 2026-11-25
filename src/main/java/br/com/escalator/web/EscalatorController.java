package br.com.escalator.web;

import br.com.escalator.service.GeradorDeLicks;
import br.com.escalator.service.ModoMaior;
import br.com.escalator.service.ModoMenorHarmonica;
import br.com.escalator.service.ModoMenorMelodica;
import br.com.escalator.service.PentatonicaM6;
import br.com.escalator.service.PentatonicaM7;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class EscalatorController {

    private static final List<String> TONICAS = List.of(
            "Am/C", "Em/G", "Bm/D", "F#m/A", "C#m/E", "G#m/B", "D#m/F#", "A#m/C#",
            "Dm/F", "Gm/Bb", "Cm/Eb", "Fm/Ab", "Bbm/Db", "Ebm/Gb", "Abm/Cb"
    );

    /** Modos em que a tonica maior aparece primeiro no rotulo (C/Am). */
    private static final List<String> MODOS_MAIOR_PRIMEIRO =
            List.of("1", "6", "7", "8", "9", "2", "10");

    private final EscalatorWebService escalatorWebService;

    public EscalatorController(EscalatorWebService escalatorWebService) {
        this.escalatorWebService = escalatorWebService;
    }

    /** Opção do dropdown de tônica: valor canônico (menor/maior) e rótulo na ordem do modo. */
    public record TonicaOpcao(String valor, String rotulo) {
    }

    @GetMapping("/")
    public String index(
            @RequestParam(defaultValue = "1") String modo,
            @RequestParam(defaultValue = "Am/C") String tonica,
            @RequestParam(defaultValue = "1") String padrao,
            @RequestParam(defaultValue = "") String lick,
            @RequestParam(defaultValue = "false") boolean gerar,
            Model model
    ) {
        // Tonica enviada sem o relativo (dropdown da menor harmonica ou troca de
        // modo): recupera o par correspondente para os demais modos.
        String parTonica = tonica.contains("/") ? tonica : TONICAS.stream()
                .filter(par -> par.split("/")[0].equals(tonica))
                .findFirst()
                .orElse(tonica);
        String padraoEfetivo = padraoParaModo(modo, padrao);
        // Os licks agora moram dentro do proprio seletor de Padrão. O parametro
        // antigo `lick` continua aceito por URLs legadas, mas o valor de `padrao`
        // tem prioridade para manter um unico controle no formulario.
        boolean lickLegadoInvalido = false;
        GeradorDeLicks.Lick lickEscolhido = GeradorDeLicks.porPadrao(padraoEfetivo);
        if (lickEscolhido == null && lick != null && !lick.isBlank()) {
            lickEscolhido = GeradorDeLicks.buscar(lick);
            if (lickEscolhido != null) {
                padraoEfetivo = lickEscolhido.padraoOpcao();
            } else {
                lickLegadoInvalido = true;
            }
        }
        // Com um lick ativo o modo da URL e o da escala de origem do lick:
        // assim o dropdown mostra somente o grupo correspondente.
        String modoEfetivo = lickEscolhido == null ? modo : lickEscolhido.modo();
        // Nos 7 modos da escala maior a tonica maior vem primeiro (C/Am);
        // nas pentatonicas permanece a menor primeiro (Am/C).
        boolean maiorPrimeiro = MODOS_MAIOR_PRIMEIRO.contains(modoEfetivo);
        // Nas menores harmonica e melodica o dropdown mostra so a tonica menor;
        // a leitura diatonica continua usando uma parente maior propria.
        boolean somenteMenor = GeradorDeLicks.MODO_MENOR_HARMONICA.equals(modoEfetivo)
                || ModoMenorMelodica.MODO_OPCAO.equals(modoEfetivo);
        List<TonicaOpcao> tonicas = TONICAS.stream()
                .map(par -> {
                    String[] partes = par.split("/");
                    if (somenteMenor) {
                        return new TonicaOpcao(partes[0], partes[0]);
                    }
                    String rotulo = maiorPrimeiro ? partes[1] + "/" + partes[0] : par;
                    return new TonicaOpcao(par, rotulo);
                })
                .toList();

        model.addAttribute("tonicas", tonicas);
        model.addAttribute("rotuloTonica", somenteMenor
                ? "Tônica (menor)"
                : (maiorPrimeiro ? "Tônica (maior/menor)" : "Tônica (menor/maior)"));
        model.addAttribute("modoSelecionado", modoEfetivo);
        model.addAttribute("tonicaSelecionada", somenteMenor ? parTonica.split("/")[0] : parTonica);
        model.addAttribute("padraoSelecionado", padraoEfetivo);
        model.addAttribute("modosGregos", ModoMaior.values());
        model.addAttribute("modosMenorHarmonica", ModoMenorHarmonica.values());
        model.addAttribute("modosMenorMelodica", ModoMenorMelodica.values());
        model.addAttribute("modosPentatonicaM7", PentatonicaM7.values());
        model.addAttribute("modosPentatonicaM6", PentatonicaM6.values());
        // Cada escala recebe somente o seu proprio grupo de licks.
        model.addAttribute("licksPentaM7",
                GeradorDeLicks.listarPorModo(GeradorDeLicks.MODO_PENTA_M7));
        model.addAttribute("licksMenorHarmonica",
                GeradorDeLicks.listarPorModo(GeradorDeLicks.MODO_MENOR_HARMONICA));
        model.addAttribute("lickSelecionado", lickEscolhido == null ? "" : lickEscolhido.id());

        if (gerar && lickLegadoInvalido) {
            model.addAttribute("erro", "Lick invalido. Selecione uma opcao disponivel.");
        } else if (gerar) {
            try {
                ResultadoEstudo resultado = lickEscolhido != null
                        ? escalatorWebService.gerarLick(lickEscolhido)
                        : escalatorWebService.gerar(modoEfetivo, parTonica, padraoEfetivo);
                model.addAttribute("resultado", resultado);
            } catch (IllegalArgumentException ex) {
                if (lickEscolhido != null) {
                    model.addAttribute("erro", "Lick invalido. Selecione uma opcao disponivel.");
                } else {
                    boolean shapeMenorForaDoModo = ModoMenorHarmonica.porPadrao(padraoEfetivo).isPresent()
                            && !"5".equals(modo);
                    boolean shapeMelodicaForaDoModo = ModoMenorMelodica.porPadrao(padraoEfetivo).isPresent()
                            && !ModoMenorMelodica.MODO_OPCAO.equals(modo);
                    boolean shapeM7ForaDoModo = PentatonicaM7.porPadrao(padraoEfetivo).isPresent()
                            && !PentatonicaM7.MODO_OPCAO.equals(modo);
                    boolean shapeM6ForaDoModo = PentatonicaM6.porPadrao(padraoEfetivo).isPresent()
                            && !PentatonicaM6.MODO_OPCAO.equals(modo);
                    boolean shapeForaDePentatonica = List.of("4", "5", "6", "7", "8").contains(padraoEfetivo)
                            && !("3".equals(modo) || "4".equals(modo));
                    String erro = shapeMenorForaDoModo || shapeMelodicaForaDoModo
                            || shapeM7ForaDoModo || shapeM6ForaDoModo
                            ? ex.getMessage()
                            : shapeForaDePentatonica
                            ? "Os Shapes 2 a 5 e Extra estao disponiveis apenas para as pentatonicas (m7 ou blues)."
                            : "Nao foi possivel gerar. Verifique as opcoes selecionadas.";
                    model.addAttribute("erro", erro);
                }
            }
        }

        return "index";
    }

    /**
     * Na menor harmonica o antigo Shape 1 (valor 3) nao existe mais. Qualquer
     * padrao de outra familia recebido por URL legada e redirecionado para o
     * Eolio 7+ (valor 16), que e o mesmo desenho validado. Na menor melodica as
     * sequencias 1 e 2 continuam disponiveis e qualquer outro padrao fora da
     * familia volta ao primeiro shape do PDF (valor 35). Nas pentatonicas M7 e
     * m6 qualquer padrao fora da familia volta ao Shape 1 do proprio modo.
     */
    private static String padraoParaModo(String modo, String padrao) {
        if (padrao == null || padrao.isBlank()) {
            return "1";
        }
        // Licks pertencem a escala do seu grupo: o valor e preservado e o
        // controller ajusta o modo efetivo antes de gerar ou renderizar.
        if (GeradorDeLicks.porPadrao(padrao) != null) {
            return padrao;
        }
        if ("5".equals(modo)
                && !"1".equals(padrao)
                && !"2".equals(padrao)
                && ModoMenorHarmonica.porPadrao(padrao).isEmpty()) {
            return ModoMenorHarmonica.EOLIO_7.padraoOpcao();
        }
        if (ModoMenorMelodica.MODO_OPCAO.equals(modo)
                && !"1".equals(padrao)
                && !"2".equals(padrao)
                && ModoMenorMelodica.porPadrao(padrao).isEmpty()) {
            return ModoMenorMelodica.DORICO_7PLUS.padraoOpcao();
        }
        if (PentatonicaM7.MODO_OPCAO.equals(modo)
                && PentatonicaM7.porPadrao(padrao).isEmpty()) {
            return PentatonicaM7.SHAPE_1.padraoOpcao();
        }
        if (PentatonicaM6.MODO_OPCAO.equals(modo)
                && PentatonicaM6.porPadrao(padrao).isEmpty()) {
            return PentatonicaM6.SHAPE_1.padraoOpcao();
        }
        return padrao;
    }
}
