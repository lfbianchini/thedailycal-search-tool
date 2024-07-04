package org.projects;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageMerge {

    private final String parentDirectory;
    private PDDocument mergedDocument;
    private String outputPdfFileName;

    public ImageMerge(String parentDirectory, String outputPdfFileName) {
        this.parentDirectory = parentDirectory;
        this.outputPdfFileName = outputPdfFileName;
        this.mergedDocument = new PDDocument();
    }

    public void mergeImagesToPdf(int waitTime) {
        File directory = new File(parentDirectory);

        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println("Invalid directory: " + parentDirectory);
            return;
        }

        // Wait for 5 seconds
        try {
            System.out.println("Waiting " + waitTime + " seconds for downloads to complete...");
            Thread.sleep(waitTime * 1000L);
        } catch (InterruptedException e) {
            System.err.println("Wait interrupted: " + e.getMessage());
        }

        File[] jpgFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg"));

        if (jpgFiles == null || jpgFiles.length == 0) {
            System.out.println("No JPG files found in directory: " + parentDirectory);
            return;
        }

        try {
            for (File jpgFile : jpgFiles) {
                mergeFile(jpgFile);
            }

            saveMergedDocument();
        } catch (IOException e) {
            System.err.println("Error creating PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void mergeFile(File file) throws IOException {
        if (!file.getName().toLowerCase().endsWith(".jpg")) {
            System.out.println("Skipping non-JPG file: " + file.getName());
            return;
        }

        try {
            BufferedImage image = ImageIO.read(file);

            if (image != null) {
                // Reduce image resolution for smaller file size (optional)
                // BufferedImage scaledImage = scaleImage(image, 0.5); // Example: scaling image to 50%

                PDPage page = new PDPage(new PDRectangle(image.getWidth(), image.getHeight()));
                mergedDocument.addPage(page);

                // Create JPEG compressed image
                PDImageXObject pdImage = JPEGFactory.createFromImage(mergedDocument, image, 0.05f); // Adjust quality as needed

                PDPageContentStream contentStream = new PDPageContentStream(mergedDocument, page);
                contentStream.drawImage(pdImage, 0, 0, image.getWidth(), image.getHeight());
                contentStream.close();

                System.out.println("Processed: " + file.getName());
            }
        } catch (IOException e) {
            System.err.println("Error processing file: " + file.getAbsolutePath());
            e.printStackTrace();
            throw e;
        }
    }

    public void saveMergedDocument() throws IOException {
        mergedDocument.save(outputPdfFileName);
        mergedDocument.close();
        System.out.println("PDF successfully created: " + outputPdfFileName);
    }
}