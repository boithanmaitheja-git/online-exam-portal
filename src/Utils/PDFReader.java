package Utils;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.File;
import java.io.IOException;

public class PDFReader {

    public static String extractText(String pdfPath) {
        StringBuilder content = new StringBuilder();
        try (PDDocument document = Loader.loadPDF(new File(pdfPath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            content.append(stripper.getText(document));
        } catch (IOException e) {
            System.out.println("⚠️ Error reading PDF: " + e.getMessage());
        }
        return content.toString();
    }

    public static void main(String[] args) {
        // Test run
        String text = extractText("C:/Users/karth/OneDrive/Desktop/OnlineExamPortal/sample.pdf");
        System.out.println(text);
    }
}
