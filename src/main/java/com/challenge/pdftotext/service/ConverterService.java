package com.challenge.pdftotext.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class ConverterService {
    public static File convertPDFToText(File uploadedFile) throws IOException {
        String originalFileName = uploadedFile.getName();

        try (PDDocument document = Loader.loadPDF(uploadedFile)) {

            PDFTextStripperByArea stripper = new PDFTextStripperByArea();
            stripper.setSortByPosition(true);

            PDFTextStripper tStripper = new PDFTextStripper();

            String pdfFileInText = tStripper.getText(document);

            // split by whitespace
            String[] lines = pdfFileInText.split("\\r?\\n");


            // remove ".pdf" in the file name and replace it by ".txt"
            String writeName = originalFileName.split("\\.")[0] + ".txt";

            // write to text file
            FileWriter myWriter = new FileWriter(writeName, true);
            for (String line : lines) {
                myWriter.write(line + "\n");
            }
            myWriter.close();

            return new File(writeName);

        }

    }
}

