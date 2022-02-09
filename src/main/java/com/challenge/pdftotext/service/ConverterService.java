package com.challenge.pdftotext.service;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ConverterService {
    public static File convertPDFToText(File uploadedFile) {
        String originalFileName = uploadedFile.getName();

        // remove ".pdf" in the file name and replace it by ".txt"
        String writeName = originalFileName.split("\\.")[0] + ".txt";

        try (PDDocument document = Loader.loadPDF(uploadedFile)) {

            PDFTextStripperByArea stripper = new PDFTextStripperByArea();
            stripper.setSortByPosition(true);

            PDFTextStripper tStripper = new PDFTextStripper();

            String pdfFileInText = tStripper.getText(document);

            // write to text file
            FileWriter myWriter = new FileWriter(writeName, true);

            // split by whitespace
            String[] lines = pdfFileInText.split("\\r?\\n");

            // extract text from image if the pdf text is not searchable
            if (isEmptyStringArray(lines)) {
                String strImg = extractTextFromScannedDocument(document);
                myWriter.write(strImg + "\n");
            } else {
                for (String line : lines) {
                    myWriter.write(line + "\n");
                }
            }

            myWriter.close();

        } catch (TesseractException | IOException e) {
            e.printStackTrace();
        }

        return new File(writeName);
    }

    private static String extractTextFromScannedDocument(PDDocument document) throws IOException, TesseractException {
        // Extract images from file
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        StringBuilder out = new StringBuilder();

        ITesseract tesseract = new Tesseract();

        tesseract.setLanguage("eng"); // Extract ENG

        for (int page = 0; page < document.getNumberOfPages(); page++) {
            BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);

            // Create a temp image file
            File temp = File.createTempFile("tempfile_" + page, ".png");
            ImageIO.write(bim, "png", temp);

            String result = tesseract.doOCR(temp);
            out.append(result);

            // Delete temp file
            temp.delete();

        }

        return out.toString();

    }

    public static boolean isEmptyStringArray(String[] lines) {
        for (String line : lines) {
            if (line != null) {
                return false;
            }
        }
        return true;
    }
}

