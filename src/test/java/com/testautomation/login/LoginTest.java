package com.testautomation.login;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
//import java.time.Duration;
import java.util.Date;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class LoginTest {
	WebDriver driver;
	XSSFWorkbook workbook;
	XSSFSheet sheet;
	XSSFCell cell;
	ExtentReports extent;
	ExtentTest test;

	@BeforeMethod
	public void setUp() {
		// Set up the ExtentReport
		ExtentSparkReporter htmlReporter = new ExtentSparkReporter("LoginTest_Extent-report.html");
		extent = new ExtentReports();
		extent.attachReporter(htmlReporter);

	}

	@BeforeSuite
	public void setupExtentReport() {
		extent = new ExtentReports();
	}

	@BeforeTest
	public void TestSetup() {
		driver = new ChromeDriver();

		// Enter url.
		driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");
		driver.manage().window().maximize();

//		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
	}

	@Test
	public void ReadData() throws IOException, InterruptedException {

		test = extent.createTest("Login Logout Test");
		// Import excel sheet.
		File src = new File("./files/DataforLoginTest.xlsx");

		// Load the file.
		FileInputStream finput = new FileInputStream(src);

		// Load he workbook.
		workbook = new XSSFWorkbook(finput);

		// Load the sheet in which data is stored.
		sheet = workbook.getSheetAt(0);

		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			// Import data for Email.
			cell = sheet.getRow(i).getCell(1);

			String un = cell.getStringCellValue();
//			System.out.println("username value is " + un);
			System.out.println("User" + i + " value is: " + un);

			Thread.sleep(1000);
			// Assuming the first cell contains the username and the second cell contains
			// the password

			// cell.setCellType(Cell.CELL_TYPE_STRING);
			driver.findElement(By.name("username")).clear();
			driver.findElement(By.name("username")).sendKeys(cell.getStringCellValue());
			// Import data for password.
			cell = sheet.getRow(i).getCell(2);
			String pwd = cell.getStringCellValue();
//			System.out.println("Password value is " + pwd);
			System.out.println("Password" + i + " value is: " + pwd);

			// cell.setCellType(Cell.CELL_TYPE_STRING);
			driver.findElement(By.name("password")).clear();

			driver.findElement(By.name("password")).sendKeys(cell.getStringCellValue());
			Thread.sleep(5000);

			driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div/div[1]/div/div[2]/div[2]/form/div[3]/button"))
					.click();

			if (un.equals("Admin") && pwd.equals("admin123")) {
				// Valid credentials
				Thread.sleep(2000);
				Assert.assertTrue(driver.getCurrentUrl().contains("dashboard"));
				Thread.sleep(2000);
				captureScreenshot();

				System.out.println("Login Successful. Test case passed.");
				test.log(Status.PASS, "Login with valid credentials successful");

				Thread.sleep(2000);
				driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[1]/header/div[1]/div[2]/ul/li/span/i"))
						.click();
				Thread.sleep(1000);
				driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[1]/header/div[1]/div[2]/ul/li/ul/li[4]/a"))
						.click();
				Thread.sleep(1000);

			} else {
				// Invalid credentials
				Assert.assertTrue(driver.getCurrentUrl().contains("auth/login"));
				Thread.sleep(2000);
				captureScreenshot();

				System.out.println("Login Failed. Test case failed.");
				test.log(Status.FAIL, "Login with invalid credentials failed");
				Thread.sleep(1000);
			}
//			Thread.sleep(5000);
//			captureScreenshot();
			Thread.sleep(2000);
		}

	}

	@AfterClass
	public void closeBrowser() {
		driver.quit();

	}

	@AfterSuite
	public void flushExtentReport() {
		extent.flush();
	}

	private void captureScreenshot() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		Date now = new Date();
		String timestamp = sdf.format(now);

		File screenshotFile = ((org.openqa.selenium.TakesScreenshot) driver)
				.getScreenshotAs(org.openqa.selenium.OutputType.FILE);
		String screenshotPath = "C:\\Users\\User1\\eclipse-workspace\\StarAgile_QA_Assignment\\ScreenShots\\LoginTest_Screenshot_"
				+ timestamp + ".png";

		try {
			org.apache.commons.io.FileUtils.copyFile(screenshotFile, new File(screenshotPath));
			test.addScreenCaptureFromPath(screenshotPath);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
