package org.projects;

import java.io.File;

public class DirectoryTool {
    private final String parentDirectory;
    private String currentDirectory;

    public DirectoryTool(String parentDirectory) {
        this.parentDirectory = parentDirectory;
    }

    public String getParentDirectory() {
        return parentDirectory;
    }

    public String getCurrentDirectory() {
        return currentDirectory;
    }

    public void createDirectory(String year, String date) {
        createYearDirectory(year);
        createDateDirectory(date);
    }

    public void renameFilesInCurrentDirectory() {

    }

    public void createDateDirectory(String date) {
        String dateDirectoryPath = currentDirectory + File.separator + date;
        File dateDirectory = new File(dateDirectoryPath);

        if(!dateDirectory.exists()) {
            boolean created = dateDirectory.mkdir();
            if(created) {
                System.out.println("Directory created: " + dateDirectoryPath);
                currentDirectory = dateDirectoryPath;
            } else {
                throw new RuntimeException("Failed to create directory: " + dateDirectory.getAbsolutePath());
            }
        } else {
            System.out.println("Directory for date " + date + " already exists, using this directory  ");
            currentDirectory = dateDirectoryPath;
        }
    }
    public void createYearDirectory(String year) {
        String yearDirectoryPath = parentDirectory + File.separator + year;
        File yearDirectory = new File(yearDirectoryPath);

        if(!yearDirectory.exists()) {
            boolean created = yearDirectory.mkdir();
            if(created) {
                System.out.println("Directory created: " + yearDirectory.getAbsolutePath());
                currentDirectory = yearDirectoryPath;
            } else {
                throw new RuntimeException("Failed to create directory: " + yearDirectory.getAbsolutePath());
            }
        } else {
            System.out.println("Directory for year " + year + " already exists..  " + yearDirectory.getAbsolutePath());
            currentDirectory = yearDirectoryPath;
        }
    }
}
