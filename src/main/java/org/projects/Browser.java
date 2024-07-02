package org.projects;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;

public class Browser {
    private final WebDriver driver;
    private String url;
    private String parentDirectory;

    public Browser(String url, String parentDirectory) {
        this.url = url;
        this.parentDirectory = parentDirectory;
        driver = new ChromeDriver();
        checkIfValidUrl();
    }

    public void downloadPagesToDayDirectory() {
        String year = "2024";
        createYearDirectory(year);
    }

    private void createYearDirectory(String year) {
        String yearDirectoryPath = parentDirectory + File.separator + year;
        File yearDirectory = new File(yearDirectoryPath);

        if (!yearDirectory.exists()) {
            boolean created = yearDirectory.mkdir();
            if (created) {
                System.out.println("Directory created: " + yearDirectory.getAbsolutePath());
            } else {
                throw new RuntimeException("Failed to create directory: " + yearDirectory.getAbsolutePath());
            }
        } else {
            System.out.println("Directory for year " + year + " already exists..  " + yearDirectory.getAbsolutePath());
        }
    }

    private void checkIfValidUrl() {
        driver.get(url);
        try {
            WebElement fullCollectionName = driver.findElement(By.cssSelector("div.metadata-row:nth-child(4) > span:nth-child(2)"));
            if(!fullCollectionName.getText().equals("The Daily Californian")) {
                throw new NoSuchElementException("Not a The Daily Californian article");
            }
        } catch (NoSuchElementException e) {
            driver.close();
            throw new IllegalArgumentException("URL is not valid");
        }
    }

    public String getParentDirectory() {
        return parentDirectory;
    }

    public void setParentDirectory(String parentDirectory) {
        this.parentDirectory = parentDirectory;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
