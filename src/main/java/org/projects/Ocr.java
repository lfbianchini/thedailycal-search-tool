package org.projects;

import com.azure.ai.documentintelligence.DocumentIntelligenceAsyncClient;
import com.azure.ai.documentintelligence.DocumentIntelligenceClientBuilder;
import com.azure.ai.documentintelligence.models.AnalyzeDocumentRequest;
import com.azure.ai.documentintelligence.models.AnalyzeResult;
import com.azure.ai.documentintelligence.models.AnalyzeResultOperation;
import com.azure.core.credential.KeyCredential;
import com.azure.core.util.polling.PollerFlux;

import java.io.File;
import java.util.ArrayList;

public class Ocr {
    public final DocumentIntelligenceAsyncClient client;
    private static final String endpoint;
    private static final String apiKey;

    static {
        endpoint = "no";
        apiKey = "no";
    }

    public Ocr() {
        this.client = new DocumentIntelligenceClientBuilder()
                .credential(new KeyCredential(apiKey))
                .endpoint(endpoint)
                .buildAsyncClient();
    }

    public String findTextFromImage(File image, ArrayList<String> queries) {
        StringBuilder output = new StringBuilder();
        String modelId = "prebuilt-layout";
        String documentUrl = "https://raw.githubusercontent.com/Azure-Samples/cognitive-services-REST-api-samples/master/curl/form-recognizer/sample-layout.pdf";

        PollerFlux<AnalyzeResultOperation, AnalyzeResult> analyzeLayoutPoller =
                client.beginAnalyzeDocument(modelId,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        new AnalyzeDocumentRequest().setUrlSource(documentUrl));

        AnalyzeResult analyzeLayoutResult = analyzeLayoutPoller.getSyncPoller().getFinalResult();

        // pages
        analyzeLayoutResult.getPages().forEach(documentPage -> {
                    System.out.printf("Page has width: %.2f and height: %.2f, measured with unit: %s%n",
                            documentPage.getWidth(),
                            documentPage.getHeight(),
                            documentPage.getUnit());

                    // lines
                    documentPage.getLines().forEach(documentLine ->
                            System.out.printf("Line '%s' is within a bounding polygon %s.%n",
                                    documentLine.getContent(),
                                    documentLine.getPolygon()));

                    // words
                    documentPage.getWords().forEach(documentWord ->
                            System.out.printf("Word '%s' has a confidence score of %.2f.%n",
                                    documentWord.getContent(),
                                    documentWord.getConfidence()));
                });


//
//        for(DetectedTextLine detectedLine:result.getRead().getBlocks().get(0).getLines()) {
//            System.out.println(detectedLine.getText());
//            List<DetectedTextWord> detectedWordList = detectedLine.getWords();
//            for (DetectedTextWord detectedWord : detectedWordList) {
//                String word = detectedWord.getText().toLowerCase();
//                if(queries.contains(word)) {
//                    output.append("Found specified word ").append(word).append(" on line ").append(detectedLine.getText()).append("\n");
//                }
//            }
//        }

        return "test";
    }

    public static void main(String[] args) {
        Ocr ocr = new Ocr();
        ArrayList<String> queries = new ArrayList<>();
        queries.add("sports");
        queries.add("personal adjustments");
        File img = new File("C:\\Users\\Donna\\Desktop\\stanford_proect\\stanford\\1943\\Feb. 15\\991045148619706532_C120187575_035_R.jpg");
        System.out.println(ocr.findTextFromImage(img, queries));

    }
}
