package org.projects;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Browser {
    private WebDriver driver;
    private String url;
    private final DirectoryTool dTool;

    public Browser(String url, DirectoryTool dTool) {
        this.url = url;
        this.dTool = dTool;
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
        setBrowserDownloadPath(dTool.getCurrentDirectory());
        WebElement downloadNodeParent = driver.findElement(By.cssSelector("#record-files-list > tbody:nth-child(2)"));
        List<WebElement> downloadNodesList = downloadNodeParent.findElements(By.tagName("tindui-app-file-download-link"));
        for(WebElement downloadNode : downloadNodesList) {
            String url = downloadNode.getAttribute("url");
            driver.get(url);
        }
    }

    private void setBrowserDownloadPath(String path) {
        Map<String, Object> chromePrefs  = new HashMap<String, Object>();
        chromePrefs.put("download.default_directory", path);
        chromePrefs.put("download.directory_upgrade", true);
        chromePrefs.put("browser.set_download_behavior", "allow");
        chromePrefs.put("download.prompt_for_download", false);
        ChromeOptions newOptions = new ChromeOptions();
        newOptions.setExperimentalOption("prefs", chromePrefs);
        driver.quit();
        driver = new ChromeDriver(newOptions);
        driver.get(url);
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
