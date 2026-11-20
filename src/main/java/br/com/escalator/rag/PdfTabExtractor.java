package br.com.escalator.rag;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * Extracao de texto dos PDFs de referencia de tablatura.
 *
 * <p>Esta classe nao interpreta o conteudo musical: ela apenas transforma o
 * PDF em texto para que o indice da base de conhecimento seja construido a
 * partir da fonte original (sem JSON montado manualmente). A ordenacao por
 * posicao e importante porque tablaturas dependem da disposicao visual dos
 * numeros de casa/dedo.</p>
 */
public final class PdfTabExtractor {

    private PdfTabExtractor() {
    }

    public static String extrair(Path caminho) throws IOException {
        try (PDDocument documento = Loader.loadPDF(caminho.toFile())) {
            return novoStripper().getText(documento);
        }
    }

    public static String extrair(InputStream entrada) throws IOException {
        try (InputStream in = entrada) {
            return extrair(in.readAllBytes());
        }
    }

    private static String extrair(byte[] bytes) throws IOException {
        try (PDDocument documento = Loader.loadPDF(bytes)) {
            return novoStripper().getText(documento);
        }
    }

    private static PDFTextStripper novoStripper() {
        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setSortByPosition(true);
        return stripper;
    }
}
