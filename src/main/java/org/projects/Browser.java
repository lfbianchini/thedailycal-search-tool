package org.projects;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.util.Arrays;

public class Browser {
    private final WebDriver driver;
    private String url;
    private DirectoryTool dTool;

    public Browser(String url, String parentDirectory) {
        this.url = url;
        dTool = new DirectoryTool(parentDirectory);
        driver = new ChromeDriver();
        checkIfValidUrl();
    }

    public void downloadPagesToDayDirectory() {
        //e.g. "The Daily Californian, Fri Jan. 3, 1975"
        String title = driver.findElement(By.cssSelector("div.metadata-row:nth-child(1) > span:nth-child(2)")).getText();
        String[] parts = title.split(",\\s");
        String date = parts[1].substring(4);
        String year = parts[2];

        createDirectory(year, date);


    }

    private void createDirectory(String year, String date) {
        dTool.createYearDirectory(year);
        dTool.createDateDirectory(date);
    }



    private void checkIfValidUrl() {
        System.out.println("Checking if URL is valid");
        driver.get(url);
        try {
            String fullCollectionName = driver.findElement(By.cssSelector("div.metadata-row:nth-child(4) > span:nth-child(2)")).getText();
            if(!fullCollectionName.equals("The Daily Californian")) {
                throw new NoSuchElementException("Not a The Daily Californian article");
            }
        } catch (NoSuchElementException e) {
            driver.close();
            throw new IllegalArgumentException("URL is not valid");
        }
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
