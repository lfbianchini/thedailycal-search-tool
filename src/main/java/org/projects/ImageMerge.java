package org.projects;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageMerge {

    private final String parentDirectory;

    public ImageMerge(String parentDirectory) {
        this.parentDirectory = parentDirectory;
    }

    public void mergeImagesToPdf(String outputPdfFileName) {
        File directory = new File(parentDirectory);

        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println("Invalid directory: " + parentDirectory);
            return;
        }

        File[] jpgFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg"));

        if (jpgFiles == null || jpgFiles.length == 0) {
            System.out.println("No JPG files found in directory: " + parentDirectory);
            return;
        }

        PDFMergerUtility merger = new PDFMergerUtility();
        merger.setDestinationFileName(outputPdfFileName);

        List<File> tempFiles = new ArrayList<>();

        try {
            for (File jpgFile : jpgFiles) {
                try {
                    BufferedImage image = ImageIO.read(jpgFile);

                    if (image != null) {
                        PDDocument document = new PDDocument();
                        PDPage page = new PDPage(new PDRectangle(image.getWidth(), image.getHeight()));
                        document.addPage(page);

                        PDImageXObject pdImage = LosslessFactory.createFromImage(document, image);

                        PDPageContentStream contentStream = new PDPageContentStream(document, page);
                        contentStream.drawImage(pdImage, 0, 0, image.getWidth(), image.getHeight());
                        contentStream.close();

                        File tempPdfFile = File.createTempFile("temp_", ".pdf");
                        document.save(tempPdfFile);
                        document.close();

                        merger.addSource(tempPdfFile);
                        tempFiles.add(tempPdfFile);
                        System.out.println("Processed: " + jpgFile.getName() + " -> " + tempPdfFile.getAbsolutePath());
                    }
                } catch (IOException e) {
                    System.err.println("Error processing file: " + jpgFile.getAbsolutePath());
                    e.printStackTrace();
                }
            }

            System.out.println("Merging PDFs...");
            merger.mergeDocuments(null);
            System.out.println("PDF successfully created: " + outputPdfFileName);

        } catch (IOException e) {
            System.err.println("Error creating PDF: " + e.getMessage());
            e.printStackTrace();
        } finally {
            for (File tempFile : tempFiles) {
                if (tempFile.exists()) {
                    if (tempFile.delete()) {
                        System.out.println("Deleted temporary file: " + tempFile.getAbsolutePath());
                    } else {
                        System.err.println("Failed to delete temporary file: " + tempFile.getAbsolutePath());
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        String parentDirectory = "C:\\Users\\Donna\\Desktop\\stanford_proect\\stanford\\1943\\Feb. 15";
        String outputPdfFileName = "C:\\Users\\Donna\\Desktop\\stanford_proect\\stanford\\1943\\Feb. 15\\output.pdf";

        ImageMerge imageMerge = new ImageMerge(parentDirectory);
        imageMerge.mergeImagesToPdf(outputPdfFileName);
    }
}
