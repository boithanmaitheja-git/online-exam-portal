package Utils;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.File;

public class TestPDFBox {
    public static void main(String[] args) {
        String pdfPath = "C:/Users/karth/OneDrive/Desktop/OnlineExamPortal/sample.pdf";
        try (PDDocument document = Loader.loadPDF(new File(pdfPath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            System.out.println(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
