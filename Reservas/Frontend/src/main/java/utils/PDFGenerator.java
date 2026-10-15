

package utils;


//
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;

import java.awt.Desktop;
import java.io.File;

public class PDFGenerator   {

    private PDFGenerator() {}

    public static Cell getCell(Paragraph paragraph, Integer alignment, boolean border) {
        Cell cell = new Cell().add(paragraph);
        cell.setPadding(5);
        if (border) {
            cell.setBorder(new SolidBorder(1));
        } else {
            cell.setBorder(Border.NO_BORDER);
        }
        if (alignment != null) {
            cell.setTextAlignment(TextAlignment.values()[alignment]);
        }
        return cell;
    }

    public static Cell getCell(Image image, Integer alignment, boolean border) {
        Cell cell = new Cell().add(image);
        if (alignment != null) {
            image.setHorizontalAlignment(HorizontalAlignment.values()[alignment]);
        }
        cell.setPadding(5);
        if (border) {
            cell.setBorder(new SolidBorder(1));
        } else {
            cell.setBorder(Border.NO_BORDER);
        }
        return cell;
    }

    public static void openPdf(String path) {
        try {
            File pdfFile = new File(path);
            if (pdfFile.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(pdfFile);
                } else {
                    System.out.println("Awt Desktop is not supported!");
                }
            } else {
                System.out.println("File is not exists!");
            }
            System.out.println("Done");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
