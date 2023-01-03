package com.lsch0037.Pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

//Page object class for the marketAlertUm list page once the user has logged in
public class MarketAlertList extends PageObject{

    public MarketAlertList(WebDriver driver){
        super(driver);
    }

    /*
     * Verifies that the user is in fact on the Alerts page by comparing the url to that expected on the alerts page
     * Returns true if the url matches, false otherwise
     */
    public boolean isOnAlertsPage(){
        if(driver.getCurrentUrl().equals("https://www.marketalertum.com/Alerts/List"))
            return true;

        return false;
    }

    // Returns a list of alert elements shown on the page
    public List<WebElement> getAlerts(){
        //refresh to ensure the information is up to date
        driver.navigate().refresh();

        //return alert element
        return driver.findElements(By.tagName("tbody"));
    }

    // Logs out of the system
    public void logOut(){
    	driver.get("https://www.marketalertum.com/Home/Logout");
    }
}