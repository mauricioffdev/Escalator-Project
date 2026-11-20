package br.com.escalator.rag;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Extracao dos numeros de dedo com suas coordenadas no PDF.
 *
 * <p>As tablaturas de referencia imprimem dedos (1 a 4), nao casas. A casa so
 * pode ser recuperada pela posicao horizontal do numero em relacao aos
 * trastes do diagrama; por isso o texto simples de {@link PdfTabExtractor}
 * nao e suficiente para derivar shapes.</p>
 */
public final class PdfTabPositionalExtractor {

    public record Token(String texto, float x, float y, int pagina) {
    }

    private PdfTabPositionalExtractor() {
    }

    public static List<Token> extrair(InputStream entrada) throws IOException {
        List<Token> tokens = new ArrayList<>();
        try (InputStream in = entrada;
             PDDocument documento = Loader.loadPDF(in.readAllBytes())) {
            PDFTextStripper stripper = new PDFTextStripper() {
                @Override
                protected void writeString(String text, List<TextPosition> posicoes) throws IOException {
                    for (TextPosition posicao : posicoes) {
                        String valor = posicao.getUnicode();
                        if (valor != null && !valor.isBlank()) {
                            tokens.add(new Token(valor.trim(), posicao.getX(),
                                    posicao.getY(), getCurrentPageNo()));
                        }
                    }
                    super.writeString(text, posicoes);
                }
            };
            stripper.setSortByPosition(true);
            stripper.getText(documento);
        }
        return List.copyOf(tokens);
    }
}
