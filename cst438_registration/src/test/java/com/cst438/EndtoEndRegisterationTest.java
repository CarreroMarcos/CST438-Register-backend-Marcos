package com.cst438;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EndtoEndRegisterationTest {
	public static final String CHROME_DRIVER_FILE_LOCATION =
            "C:\\Users\\Marcos\\eclipse-workspace\\Lab3\\chromedriver-win64\\chromedriver.exe";

	public static final String URL = "http://localhost:3000/admin";

	public static final String TEST_STUDENT_EMAIL = "studentAddTest@csumb.edu";

	public static final String TEST_STUDENT_NAME = "Bob Lo"; 

	public static final String TEST_STATUS = "Good";
	
	public static final String TEST_STATUS_CODE = "0";

	public static final int SLEEP_DURATION = 1000; // 1 second.
	
	WebDriver driver;
	
	@BeforeEach
	public void testSetup() throws Exception {

		System.setProperty(
                 "webdriver.chrome.driver", 
                 CHROME_DRIVER_FILE_LOCATION);
		ChromeOptions ops = new ChromeOptions();
		ops.addArguments("--remote-allow-origins=*");
		driver = new ChromeDriver(ops);


		driver.get(URL);
		Thread.sleep(SLEEP_DURATION);
	}
//	(registration service) add, update and delete a Student.  
//	These should be 3 separate tests. 
//	One java class with 3 @Test methods.
	
	@Test
	public void addStudentTest() throws Exception{
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
		try {
			
			driver.findElement(By.id("addStudent")).click();
			Thread.sleep(SLEEP_DURATION);
			
			driver.findElement(By.id("studentName")).sendKeys(TEST_STUDENT_NAME);
			driver.findElement(By.id("studentEmail")).sendKeys(TEST_STUDENT_EMAIL);
			driver.findElement(By.id("statusCode")).sendKeys(TEST_STATUS_CODE);
			driver.findElement(By.id("status")).sendKeys(TEST_STATUS);
			Thread.sleep(SLEEP_DURATION);
			
			driver.findElement(By.id("add")).click();
			Thread.sleep(SLEEP_DURATION);
			
			WebElement we = driver.findElement(By.xpath("//tr[td='"+TEST_STUDENT_EMAIL+"']"));
			assertNotNull(we, "Test student not found in registeration after successfully adding the Student.");
			
			
		} catch (Exception ex) {
			throw ex;
		} finally {
			driver.quit();
		}
	}
	
	@Test
	public void editStudentTest() throws Exception{
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		Thread.sleep(SLEEP_DURATION);
		try {
			
			driver.findElement(By.id("editStudent4")).click();
			Thread.sleep(SLEEP_DURATION);
			
			WebElement nameInput = driver.findElement(By.id("editName"));
			WebElement emailInput = driver.findElement(By.id("editEmail"));

			// Clear the input fields using JavaScript
			((JavascriptExecutor) driver).executeScript("arguments[0].value = '';", nameInput);
			((JavascriptExecutor) driver).executeScript("arguments[0].value = '';", emailInput);
			Thread.sleep(SLEEP_DURATION);

			nameInput.sendKeys("changeName TEST");

			Thread.sleep(SLEEP_DURATION);
			emailInput.sendKeys(Keys.CONTROL + "a"); 
			emailInput.sendKeys(Keys.BACK_SPACE); 
			emailInput.sendKeys("newEmail@csumb.org");
			
			Thread.sleep(SLEEP_DURATION);
			Thread.sleep(SLEEP_DURATION);
			Thread.sleep(SLEEP_DURATION);
			
			driver.findElement(By.id("saveEdit")).click();
			Thread.sleep(SLEEP_DURATION);
			
			WebElement we = driver.findElement(By.xpath("//tr[td='newEmail@csumb.org']"));
			assertNotNull(we, "Edited Test student not found in registeration after successfully editing the Student.");
			
			
		} catch (Exception ex) {
			throw ex;
		} finally {
			driver.quit();
		}
	}
	
	@Test
	public void dropStudentTest() throws Exception{
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		Thread.sleep(SLEEP_DURATION);
		try {
			
			driver.findElement(By.id("drop4")).click();
			Thread.sleep(SLEEP_DURATION);
			WebDriverWait wait = new WebDriverWait(driver, 1);
            wait.until(ExpectedConditions.alertIsPresent());
            
            Alert simpleAlert = driver.switchTo().alert();
            simpleAlert.accept();			

            Thread.sleep(SLEEP_DURATION);
			assertThrows(NoSuchElementException.class, () -> {
				driver.findElement(By.xpath("//tr[td='newEmail@csumb.org']"));
			});	
			
		} catch (Exception ex) {
			throw ex;
		} finally {
			driver.quit();
		}
	}
	

}
