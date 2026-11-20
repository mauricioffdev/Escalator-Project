package br.com.escalator.rag;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Indice derivado dos PDFs de referencia. Nao ha JSON montado manualmente: a
 * fonte da verdade sao os arquivos em {@code tabs-kb/pdf}, lidos na
 * construcao do indice e mantidos em memoria.
 */
public final class PdfTabIndex {

    public static final String DOCUMENTO_MODOS = "Shapes_Modos.pdf";
    public static final String DOCUMENTO_BLUES = "Shapes_Pentablues_UPDATED.pdf";

    private static final Pattern LINHA_CORDA =
            Pattern.compile("^([EBGDAE])\\s+(\\d+(?:\\s+\\d+)*)\\s*$");

    private static final List<String> IGNORAR = List.of(
            "Campo Harmônico", "Blue Note", "www.",
            "Escala Maior -", "Escala Pentatônica Blues -"
    );

    private final List<TabReference> referencias;

    public PdfTabIndex() {
        this.referencias = new ArrayList<>();
        try {
            indexar(DOCUMENTO_MODOS);
            indexar(DOCUMENTO_BLUES);
        } catch (IOException ex) {
            throw new IllegalStateException("Falha ao indexar a base de conhecimento em PDF", ex);
        }
    }

    public List<TabReference> todas() {
        return List.copyOf(referencias);
    }

    public List<TabReference> porDocumento(String documento) {
        return referencias.stream()
                .filter(ref -> ref.documento().equals(documento))
                .toList();
    }

    public Optional<TabReference> buscar(String titulo) {
        return referencias.stream()
                .filter(ref -> ref.titulo().equalsIgnoreCase(titulo))
                .findFirst();
    }

    private void indexar(String documento) throws IOException {
        String texto;
        try (InputStream entrada = getClass().getResourceAsStream("/tabs-kb/pdf/" + documento)) {
            if (entrada == null) {
                throw new IOException("PDF de referencia nao encontrado: " + documento);
            }
            texto = PdfTabExtractor.extrair(entrada);
        }

        String titulo = null;
        List<TabReference.CordaReferencia> cordas = new ArrayList<>();
        int shapesSemTitulo = 0;

        for (String linha : texto.split("\\R")) {
            String atual = linha.trim();
            if (atual.isEmpty()) {
                continue;
            }

            Matcher corda = LINHA_CORDA.matcher(atual);
            if (corda.matches()) {
                // Ha blocos sem titulo impresso na folha: uma nova corda E
                // depois das 6 cordas indica o inicio do proximo shape.
                if (cordas.size() == 6 && "E".equals(corda.group(1))) {
                    adicionar(documento, titulo != null ? titulo : "Shape " + (++shapesSemTitulo), cordas);
                    cordas = new ArrayList<>();
                    titulo = null;
                }
                List<Integer> dedos = Arrays.stream(corda.group(2).split("\\s+"))
                        .map(Integer::parseInt)
                        .toList();
                cordas.add(new TabReference.CordaReferencia(corda.group(1), dedos));
                continue;
            }

            if (deveIgnorar(atual)) {
                continue;
            }

            if (!cordas.isEmpty()) {
                adicionar(documento, titulo != null ? titulo : "Shape " + (++shapesSemTitulo), cordas);
                cordas = new ArrayList<>();
                titulo = null;
            }
            titulo = extrairTitulo(atual);
        }

        if (!cordas.isEmpty()) {
            adicionar(documento, titulo != null ? titulo : "Shape " + (++shapesSemTitulo), cordas);
        }
    }

    private void adicionar(String documento, String titulo,
                           List<TabReference.CordaReferencia> cordas) {
        referencias.add(new TabReference(documento, titulo, List.copyOf(cordas)));
    }

    /** Remove o sufixo de intervalos que acompanha os titulos dos modos. */
    private static String extrairTitulo(String linha) {
        int indice = linha.indexOf("Intervalos:");
        if (indice == 0) {
            return null;
        }
        return indice > 0 ? linha.substring(0, indice).trim() : linha;
    }

    private static boolean deveIgnorar(String linha) {
        return linha.matches("\\d+") || IGNORAR.stream().anyMatch(linha::contains);
    }
}
