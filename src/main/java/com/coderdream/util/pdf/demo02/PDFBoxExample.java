package com.coderdream.util.pdf.demo02;

import com.coderdream.util.CommonUtil;
import com.coderdream.util.file.PdfFileFinder;
import java.io.File;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;

@Slf4j
public class PDFBoxExample {

    public static void main(String[] args) {
        try {
            String folderName = "170202";
            String pdfFileName = PdfFileFinder.findPdfFileName(folderName);
            String fileNameWithPath =
              CommonUtil.getFullPath(folderName) + pdfFileName;
            PDDocument document = PDDocument.load(new File(fileNameWithPath));
            CustomPDFTextStripper stripper = new CustomPDFTextStripper();
            String text = stripper.getText(document);
            System.out.println(text);
            document.close();
        } catch (IOException e) {
            log.error("Error, {}", e.getMessage(), e);
        }
    }
}
