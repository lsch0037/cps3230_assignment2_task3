package com.lsch0037.Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

//Page object class for the marketAlertUm login page
public class MarketAlertLogin extends PageObject {

    public MarketAlertLogin(WebDriver driver){
        super(driver);
    }

    //Enters the given userId in the input field
    public void inputUserId(String userId){
    	WebElement textInput = driver.findElement(By.xpath("//input[@name='UserId']"));
        textInput.sendKeys(userId);
    }

    //Presses the submit button near the input field
    public void submit(){
    	WebElement submitButton = driver.findElement(By.xpath("//input[@type='submit']"));
        submitButton.click();
    }

    /*
     * Verifies that the user is in fact on the Login page by comparing the url to that expected on the Login page
     * Returns true if the url matches, false otherwise
     */
    //TODO: CHECK IF NECESSARY
    public boolean isOnLogInPage(){
        if(driver.getCurrentUrl().equals("https://www.marketalertum.com/Alerts/Login"))
            return true;

        return false;
    }
    
}
