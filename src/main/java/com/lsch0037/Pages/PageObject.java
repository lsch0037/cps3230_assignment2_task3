package com.lsch0037.Pages;

import org.openqa.selenium.WebDriver;

//Page object superclass
public class PageObject {
    WebDriver driver;

    public PageObject(WebDriver driver) {
        this.driver = driver;
    }
}
