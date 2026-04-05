package br.com.escalator.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class EscalatorController {

    private static final List<String> TONICAS = List.of("C", "G", "D", "A", "E", "B", "F#", "C#", "F", "Bb", "Eb", "Ab", "Db", "Gb", "Cb");

    private final EscalatorWebService escalatorWebService;

    public EscalatorController(EscalatorWebService escalatorWebService) {
        this.escalatorWebService = escalatorWebService;
    }

    @GetMapping("/")
    public String index(
            @RequestParam(defaultValue = "1") String modo,
            @RequestParam(defaultValue = "C") String tonica,
            @RequestParam(defaultValue = "1") String padrao,
            @RequestParam(defaultValue = "false") boolean gerar,
            Model model
    ) {
        model.addAttribute("tonicas", TONICAS);
        model.addAttribute("modoSelecionado", modo);
        model.addAttribute("tonicaSelecionada", tonica);
        model.addAttribute("padraoSelecionado", padrao);

        if (gerar) {
            try {
                ResultadoEstudo resultado = escalatorWebService.gerar(modo, tonica, padrao);
                model.addAttribute("resultado", resultado);
            } catch (IllegalArgumentException ex) {
                model.addAttribute("erro", "Nao foi possivel gerar. Verifique as opcoes selecionadas.");
            }
        }

        return "index";
    }
}
