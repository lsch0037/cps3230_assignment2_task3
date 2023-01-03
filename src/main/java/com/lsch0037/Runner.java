package com.lsch0037;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.lsch0037.Pages.*;
import com.lsch0037.Json.*;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.google.gson.Gson;

public class Runner {
	private String userId;
	private WebDriver driver;
	private MarketAlertLogin marketAlertLogin;
	private MarketAlertList marketAlertList;

	//login status as updated by the EventsLog api
	private boolean systemIsLoggedIn;
	//number of alerts as updated by the EventsLog api
	private int systemNumberOfAlerts;

	private Gson gson;
	
	public Runner(String userId, String chromeDrvierPath){
		//set path to ChromeDriver executable
        System.setProperty("webdriver.chrome.driver", chromeDrvierPath);
		
		this.userId = userId;
		this.driver = new ChromeDriver();
		this.marketAlertLogin = new MarketAlertLogin(driver);
		this.marketAlertList = new MarketAlertList(driver);


		this.systemIsLoggedIn = false;
		this.systemNumberOfAlerts = 0;
	
		this.gson = new Gson();
	}

	public void run(){	
		boolean loggedIn = false;
		
		deleteAlerts();
		
		while(true){
			
			switch(Util.randomInt(0,5)){
			case 0:
				//Valid login
				if(!loggedIn){
					//UNCOMMENT THE LINE BELOW ONLY FOR TESTING PURPOSES ONLY
					UserValidLogin();
					
					goToLoginPage();
					goodLogin();
					loggedIn = true;
				}
				break;
			
			case 1:
				//Logout
				if(loggedIn){
					logout();
					loggedIn = false;
				}
				break;
            case 2:
            case 3:
                //Post valid alert
                postValidAlert();
                break;
            case 4:
                //Delete Alerts
				deleteAlerts();
				break;
            }

			checkEventsLog();
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void goToLoginPage(){
		driver.get("https://www.marketalertum.com/Alerts/login");
	}

	// Logs in with a correct user Id
	public void goodLogin(){
		System.out.println("User Logging in ");
		login(userId);
	}

	// Logs in with a bad user Id
	public void badLogin(){
		System.out.println("Bad login at: " + System.currentTimeMillis());
		login("Bad Id");
	}
	
	// Attempts to log into the system with the given userId
	private void login(String userId){
		marketAlertLogin.inputUserId(userId);
		marketAlertLogin.submit();
	}

	// Logs out from the system
	public void logout(){
		System.out.println("User logging out");
		marketAlertList.logOut();
	}
	
	private Alert createValidAlert(){
		int alertType = Util.randomInt(1, 6);
		String heading = Util.getAlphaNumericString(10);
		String description = Util.getAlphaNumericString(10);
		String url = Util.getAlphaNumericString(10);
		String imageUrl = Util.getAlphaNumericString(10);
		int priceInCents = Util.randomInt(0, 1000000);

		return new Alert(alertType, heading, description, url, imageUrl, userId ,priceInCents);
	}
	
	public String postValidAlert(){
		System.out.println("Posting Valid Alert");
		Alert alert = createValidAlert();
    	
    	try{
    		return postAlert(alert);
    	}catch(Exception e){
    		System.out.println("Failed to Post Alert");
    		e.printStackTrace();
    		return "";
    	}
	}
	
	public String postInvalidAlert(){
		System.out.println("Posting Invalid Alert");
		Alert alert = createValidAlert();
    	alert.postedBy = "Bad Id";
    	
    	try{
    		return postAlert(alert);
    	}catch(Exception e){
    		System.out.println("Failed to read Response as expected");
    		return "";
    	}
	}
	
	public String postAlert(Alert alert) throws Exception{
		String alertJson = gson.toJson(alert);
		System.out.println("Posting Alert:" + alertJson);
		
		return marketAlertApiCall("https://api.marketalertum.com/Alert", "POST", alertJson);
	}
	
	public String deleteAlerts(){
		System.out.println("Deleting Alerts");
		try {
			return marketAlertApiCall("https://api.marketalertum.com/Alert?userid=" + userId, "DELETE", "");
		} catch (Exception e) {
			System.out.println("Failed to delete Alerts");
			e.printStackTrace();
			return null;
		}
	}
	
	public EventsLog[] getEventsLog(){
		try {
			String response = marketAlertApiCall("https://api.marketalertum.com/EventsLog/" + userId, "GET", "");
			return gson.fromJson(response, EventsLog[].class);
		} catch (Exception e) {
			System.out.println("Failed to get Events Log");
			e.printStackTrace();
			return null;
		}	
	}
	
	private String marketAlertApiCall(String urlString, String requestMethod, String alertJson) throws Exception{
		URL url = new URL (urlString);
    	HttpURLConnection con = (HttpURLConnection)url.openConnection();
    	con.setReadTimeout(10000);
    	con.setConnectTimeout(15000);
    	con.setRequestMethod(requestMethod);
    	con.setDoOutput(true);
    	con.setDoInput(true);
    	
    	
    	if(requestMethod == "POST"){
    		
    		con.setRequestProperty("Content-Type", "application/json");
    		con.setRequestProperty("Accept", "application/json");
    	
    		try(OutputStream os = con.getOutputStream()) {
    			byte[] inBytes = alertJson.getBytes("utf-8");
    			os.write(inBytes, 0, inBytes.length);
    			os.close();
    		}catch(IOException e){
    			System.out.println("Failed to Write to Stream");
    			e.printStackTrace();
    			con.disconnect();
    			return "";
    		}
		}
    	
    	//System.out.println("ResponseCode: " + con.getResponseCode());
    	
    	StringBuilder response = new StringBuilder();
    	
    	try(BufferedReader br = new BufferedReader(
    			  new InputStreamReader(con.getInputStream(), "utf-8"))) {
    			    String responseLine = null;
    			    while ((responseLine = br.readLine()) != null) {
    			        response.append(responseLine.trim());
    			    }
    			    br.close();
    			}
    	
    	con.disconnect();
 
    	//System.out.println("Response:" + response.toString());
    	
    	return response.toString();
	}
	
	public int getNumOfAlerts(){
		EventsLog events[]= getEventsLog();
		
		if(events.length == 0)
			return 0;
		
		EventsLog lastEvent = events[events.length - 1];

		return lastEvent.systemState.alerts.length;
	}
	
	public void checkEventsLog(){
		EventsLog events[]= getEventsLog();
		
		if(events.length == 0)
			return;
		
		EventsLog lastEvent = events[events.length - 1];
		int type = lastEvent.eventLogType;
		
		switch(type){
		case 0:
			AlertCreated();
			break;
		case 1:
			AlertsDeleted();
			break;
		case 5:
			UserValidLogin();
			break;
		case 6:
			UserLoggedOut();
			break;
		case 7:
			UserViewedAlerts();
			break;
		default:
			System.out.println("Unknown Event Type: " + type);
			break;
		}
	}
	
	public void AlertCreated(){
		System.out.println("Observed Alert Created");
		//update number of alerts
		systemNumberOfAlerts++;
	}
	
	public void AlertsDeleted(){
		System.out.println("Observed Alert Deleted");
		//update number of alerts
		systemNumberOfAlerts = 0;
	}
	
	public void UserValidLogin(){
		System.out.println("Observed User Valid Login");
		//update login status
		systemIsLoggedIn = true;
	}
	
	public void UserLoggedOut(){
		System.out.println("Observed User Logout");
		//update login status
		systemIsLoggedIn = false;
	}
	
	public void UserViewedAlerts(){
		System.out.println("Observed user Viewed Alerts");
	}

	//returns the latest number of alerts as updated by the EventsLog API
	public int getAlerts(){
		checkEventsLog();
		return systemNumberOfAlerts;
	}

	//returns the latest login status as updated by the EventsLog API
	public boolean getLoginStatus(){
		checkEventsLog();
		return systemIsLoggedIn;
	}
}
