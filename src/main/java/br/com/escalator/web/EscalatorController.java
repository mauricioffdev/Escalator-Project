package br.com.escalator.web;

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
            @RequestParam(defaultValue = "false") boolean gerar,
            Model model
    ) {
        boolean maiorPrimeiro = "1".equals(modo);
        // Menor harmonica nao tem relativo maior: o dropdown mostra so a tonica menor.
        boolean somenteMenor = "5".equals(modo);
        // Tonica enviada sem o relativo (dropdown da menor harmonica ou troca de
        // modo): recupera o par correspondente para os demais modos.
        String parTonica = tonica.contains("/") ? tonica : TONICAS.stream()
                .filter(par -> par.split("/")[0].equals(tonica))
                .findFirst()
                .orElse(tonica);
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
        model.addAttribute("padraoSelecionado", padrao);

        if (gerar) {
            try {
                ResultadoEstudo resultado = escalatorWebService.gerar(modo, parTonica, padrao);
                model.addAttribute("resultado", resultado);
            } catch (IllegalArgumentException ex) {
                model.addAttribute("erro", "Nao foi possivel gerar. Verifique as opcoes selecionadas.");
            }
        }

        return "index";
    }
}
