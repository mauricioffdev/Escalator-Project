package br.com.escalator.rag;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PdfTabPositionalExtractorTest {

    /** Grava as coordenadas brutas para permitir a calibracao dedo -> casa. */
    @Test
    void extraiCoordenadasDosPdfsDeReferencia() throws Exception {
        Path destino = Path.of("target", "pdf-extract");
        Files.createDirectories(destino);

        for (String nome : List.of("Shapes_Modos.pdf", "Shapes_Pentablues_UPDATED.pdf")) {
            try (InputStream entrada = getClass().getResourceAsStream("/tabs-kb/pdf/" + nome)) {
                assertNotNull(entrada, "PDF de referencia nao encontrado: " + nome);
                List<PdfTabPositionalExtractor.Token> tokens =
                        PdfTabPositionalExtractor.extrair(entrada);
                assertFalse(tokens.isEmpty(), "PDF sem texto posicionado: " + nome);
                Files.writeString(destino.resolve(nome + ".posicoes.txt"),
                        formatar(tokens), StandardCharsets.UTF_8);
            }
        }
    }

    private static String formatar(List<PdfTabPositionalExtractor.Token> tokens) {
        return tokens.stream()
                .sorted(Comparator.comparingInt(PdfTabPositionalExtractor.Token::pagina)
                        .thenComparing(Comparator.comparingDouble(
                                PdfTabPositionalExtractor.Token::y).reversed())
                        .thenComparingDouble(PdfTabPositionalExtractor.Token::x))
                .map(t -> String.format(Locale.ROOT, "p%d y=%.1f x=%.1f [%s]",
                        t.pagina(), t.y(), t.x(), t.texto()))
                .collect(Collectors.joining("\n"));
    }
}
