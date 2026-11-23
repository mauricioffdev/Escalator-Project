package br.com.escalator.web;

import br.com.escalator.service.GeradorDeLicks;
import br.com.escalator.service.ModoMaior;
import br.com.escalator.service.ModoMenorHarmonica;

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
        // Nos 7 modos da escala maior a tonica maior vem primeiro (C/Am);
        // nas pentatonicas permanece a menor primeiro (Am/C).
        boolean maiorPrimeiro = MODOS_MAIOR_PRIMEIRO.contains(modo);
        // Menor harmonica nao tem relativo maior: o dropdown mostra so a tonica menor.
        boolean somenteMenor = "5".equals(modo);
        // Tonica enviada sem o relativo (dropdown da menor harmonica ou troca de
        // modo): recupera o par correspondente para os demais modos.
        String parTonica = tonica.contains("/") ? tonica : TONICAS.stream()
                .filter(par -> par.split("/")[0].equals(tonica))
                .findFirst()
                .orElse(tonica);
        String padraoEfetivo = padraoParaModo(modo, padrao);
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
        model.addAttribute("modoSelecionado", modo);
        model.addAttribute("tonicaSelecionada", somenteMenor ? parTonica.split("/")[0] : parTonica);
        model.addAttribute("padraoSelecionado", padraoEfetivo);
        model.addAttribute("modosGregos", ModoMaior.values());
        model.addAttribute("modosMenorHarmonica", ModoMenorHarmonica.values());
        model.addAttribute("licksDisponiveis", GeradorDeLicks.listar());
        model.addAttribute("lickSelecionado", lick);

        if (gerar) {
            try {
                ResultadoEstudo resultado = lick != null && !lick.isBlank()
                        ? escalatorWebService.gerarLick(lick)
                        : escalatorWebService.gerar(modo, parTonica, padraoEfetivo);
                model.addAttribute("resultado", resultado);
            } catch (IllegalArgumentException ex) {
                if (lick != null && !lick.isBlank()) {
                    model.addAttribute("erro", "Lick invalido. Selecione uma opcao disponivel.");
                } else {
                    boolean shapeMenorForaDoModo = ModoMenorHarmonica.porPadrao(padraoEfetivo).isPresent()
                            && !"5".equals(modo);
                    boolean shapeForaDePentatonica = List.of("4", "5", "6", "7", "8").contains(padraoEfetivo)
                            && !("3".equals(modo) || "4".equals(modo));
                    String erro = shapeMenorForaDoModo
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
     * Eolio 7+ (valor 16), que e o mesmo desenho validado.
     */
    private static String padraoParaModo(String modo, String padrao) {
        if (padrao == null || padrao.isBlank()) {
            return "1";
        }
        if ("5".equals(modo)
                && !"1".equals(padrao)
                && !"2".equals(padrao)
                && ModoMenorHarmonica.porPadrao(padrao).isEmpty()) {
            return ModoMenorHarmonica.EOLIO_7.padraoOpcao();
        }
        return padrao;
    }
}
