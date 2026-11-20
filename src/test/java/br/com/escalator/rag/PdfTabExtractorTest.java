package br.com.escalator.rag;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfTabExtractorTest {

    /**
     * Diagnostico inicial da base de conhecimento: confirma que os PDFs tem
     * texto selecionavel e grava o resultado em target/pdf-extract para
     * inspecao. Se um PDF for imagem, o teste falha com mensagem explicita.
     */
    @Test
    void extraiTextoDosPdfsDeReferencia() throws Exception {
        Path destino = Path.of("target", "pdf-extract");
        Files.createDirectories(destino);

        for (String nome : List.of("Shapes_Modos.pdf", "Shapes_Pentablues_UPDATED.pdf")) {
            try (InputStream entrada = getClass().getResourceAsStream("/tabs-kb/pdf/" + nome)) {
                assertNotNull(entrada, "PDF de referencia nao encontrado: " + nome);
                String texto = PdfTabExtractor.extrair(entrada);
                Files.writeString(destino.resolve(nome + ".txt"), texto, StandardCharsets.UTF_8);
                assertTrue(!texto.isBlank(), "PDF sem texto selecionavel (sera preciso OCR): " + nome);
            }
        }
    }
}
