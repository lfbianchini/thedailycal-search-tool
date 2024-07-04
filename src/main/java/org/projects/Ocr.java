package org.projects;

import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClient;
import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClientBuilder;
import com.azure.ai.formrecognizer.documentanalysis.models.AnalyzeResult;
import com.azure.ai.formrecognizer.documentanalysis.models.OperationResult;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.polling.SyncPoller;
import com.azure.core.util.BinaryData;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class Ocr {
    private final DocumentAnalysisClient documentClient;

    private static final String endpoint = "";
    private static final String apiKey = "";

    public Ocr() {
        this.documentClient = new DocumentAnalysisClientBuilder()
                .credential(new AzureKeyCredential(apiKey))
                .endpoint(endpoint)
                .buildClient();
    }

    public String findTextFromImages(String folderPath, ArrayList<String> queries) {
        StringBuilder output = new StringBuilder();
        String modelId = "prebuilt-layout";

        File folder = new File(folderPath);
        File[] jpgFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg"));

        if (jpgFiles == null || jpgFiles.length == 0) {
            return "No JPG files found in the specified folder.";
        }

        Arrays.sort(jpgFiles, Comparator.comparing(File::getName));

        for (int i = 0; i < jpgFiles.length; i++) {
            File image = jpgFiles[i];
            final int pageNumber = i + 1;
            try {
                BufferedImage resizedImage = resizeImage(ImageIO.read(image));

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(resizedImage, "jpg", baos);
                byte[] fileContent = baos.toByteArray();

                BinaryData binaryData = BinaryData.fromBytes(fileContent);

                System.out.println("Analyzing image: " + image.getName());
                SyncPoller<OperationResult, AnalyzeResult> analyzeLayoutPoller =
                        documentClient.beginAnalyzeDocument(modelId, binaryData);

                AnalyzeResult analyzeLayoutResult = analyzeLayoutPoller.getFinalResult();

                analyzeLayoutResult.getPages().forEach(documentPage -> documentPage.getLines().forEach(documentLine -> {
                    documentLine.getWords().forEach(documentWord -> {
                        String word = documentWord.getContent().toLowerCase();
                        if (queries.contains(word)) {
                            output.append("Found word '").append(word)
                                    .append("' on page ").append(pageNumber)
                                    .append(" (file: ").append(image.getName()).append(")\n");
                        }
                    });
                }));

            } catch (IOException e) {
                System.err.println("Error reading file " + image.getName() + ": " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Error analyzing file " + image.getName() + ": " + e.getMessage());
            }
        }

        return output.toString();
    }

    private BufferedImage resizeImage(BufferedImage originalImage) {
        Image resultingImage = originalImage.getScaledInstance(1300, 1023, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(1300, 1023, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        return outputImage;
    }
}