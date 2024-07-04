package org.projects;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        DirectoryTool dtool = getValidDirectory();
        Browser browser = getValidBrowser(dtool);
        ArrayList<String> queries = collectQueries();
        int waitTime = getValidWaitTime();

        processArchive(dtool, browser, queries, waitTime);

        scanner.close();
    }

    private static DirectoryTool getValidDirectory() {
        while (true) {
            System.out.print("Please enter the path to your archive directory: ");
            String directoryPath = scanner.nextLine();
            File directory = new File(directoryPath);

            if (directory.exists() && directory.isDirectory()) {
                System.out.println("Directory exists!");
                return new DirectoryTool(directoryPath);
            } else {
                System.out.println("Directory does not exist. Please enter a valid directory path.");
            }
        }
    }

    private static Browser getValidBrowser(DirectoryTool dtool) {
        while (true) {
            System.out.print("Please enter a valid URL to a Daily Cal archive: ");
            String urlPath = scanner.nextLine();
            try {
                return new Browser(urlPath, dtool);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static int getValidWaitTime() {
        while (true) {
            System.out.print("Please enter how long you want the program to wait to merge after downloading files (in seconds, suggest 2-5 depending on internet): ");
            if (scanner.hasNextInt()) {
                int waitTime = scanner.nextInt();
                scanner.nextLine();
                if (waitTime > 0) {
                    return waitTime;
                } else {
                    System.out.println("Please enter a positive number.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    private static ArrayList<String> collectQueries() {
        ArrayList<String> queries = new ArrayList<>();

        while (true) {
            System.out.print("Enter a word query (or type -1 to finish): ");
            String input = scanner.nextLine().trim();

            if (input.equals("-1")) {
                break;
            }

            if (!input.isEmpty()) {
                queries.add(input);
                System.out.println("Added: " + input);
            } else {
                System.out.println("Empty input. Please enter a valid query or -1 to finish.");
            }
        }

        System.out.println("Query collection finished. Total queries: " + queries.size());
        return queries;
    }

    private static void processArchive(DirectoryTool dtool, Browser browser, ArrayList<String> queries, int waitTime) {
        System.out.println("Selected archive directory path: " + dtool.getParentDirectory());
        System.out.println("Selected archive url: " + browser.getUrl());

        browser.downloadPagesToDayDirectory();
        String filePath = dtool.getCurrentDirectory() + File.separator + "article.pdf";

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Future<?> mergeTask = executor.submit(() -> {
            ImageMerge merger = new ImageMerge(dtool.getCurrentDirectory(), filePath);
            merger.mergeImagesToPdf(waitTime);
        });

        Future<String> ocrTask = executor.submit(() -> {
            Ocr ocr = new Ocr();
            return ocr.findTextFromImages(dtool.getCurrentDirectory(), queries);
        });

        try {
            mergeTask.get();
            System.out.println("PDF merging completed.");

            String ocrResult = ocrTask.get();
            System.out.println("OCR analysis completed. Results:");
            System.out.println(ocrResult);
        } catch (Exception e) {
            System.err.println("An error occurred during parallel processing: " + e.getMessage());
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }
}