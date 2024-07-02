package org.projects;

import net.sourceforge.tess4j.*;

import java.io.File;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean validDirectory = false;
        String directoryPath = "";
        DirectoryTool dtool = null;
        Browser browser = null;

        while(!validDirectory) {
            System.out.print("Please enter the path to your archive directory: ");
            directoryPath = scanner.nextLine();

            File directory = new File(directoryPath);

            if (directory.exists() && directory.isDirectory()) {
                validDirectory = true;
                dtool = new DirectoryTool(directoryPath);
                System.out.println("Directory exists!");
            } else {
                System.out.println("Directory does not exist. Please enter a valid directory path.");
            }
        }

        boolean validURL = false;
        String urlPath = "";

        while(!validURL) {
            System.out.print("Please enter a valid URL to a Daily Cal archive: ");
            urlPath = scanner.nextLine();
            try {
                browser = new Browser(urlPath, dtool.getParentDirectory());
                validURL = true;
            } catch(IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        System.out.println("Selected directory path: " + dtool.getParentDirectory());
        System.out.println("Selected directory path: " + browser.getUrl());
        browser.downloadPagesToDayDirectory();

        scanner.close();
    }
}