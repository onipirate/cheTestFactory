/**
 * @author cherylreyes
 *
 * This class file will contain methods for all actions that will be used to drive the test
 *
 * Change Log:
 * --- Follow this format <Name> <Date> <Change Description>
 *
 */

package shipservtrade.config;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.Reporter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class ActionKeywords {

	public String strReportImage = "";
	public String strReportImageID = "";
	public String strScreenshotSource = "";

	public String strLog = null;
	public String strScreenID = null;

	private static Logger log = LogManager.getLogger(ActionKeywords.class.getName());

	/**
	 * Opens URL
	 * @param driver
	 * @param strURL
	 */
	public void webdriver_open(WebDriver driver, String strURL) throws Exception {
		log.info("Loading URL: {}", strURL);
		driver.get(strURL);
		driver.manage().window().maximize();
		waitForPageLoad(driver);

		clickCookiesAlert(driver, strScreenID);
	}

	/**
	 * Returns the current date and time with the given format "MMddyyyyhhmmss"
	 * @return
	 */
	public String getCurrentDateTime() {
		String DATE_FORMAT = "MMddyyyyhhmmss";
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		Calendar c1 = Calendar.getInstance();
		//c1.add(Calendar.DATE,1);
		return sdf.format(c1.getTime());
	}

	/**
	 * Takes the screenshot for failed steps/test cases
	 * @param driver
	 * @param strLog
	 * @throws Exception
	 */
	public void webdriver_takeScreenshot(WebDriver driver, String strLog, String strScreenID) throws Exception {

		strReportImage = strScreenID + "_" + getCurrentDateTime() + ".png";
		strReportImageID = strScreenID + "_" + getCurrentDateTime();
		strScreenshotSource = Constants.strScreenshotPath + strReportImage;

		if (driver instanceof TakesScreenshot) {
			log.info("Capture screenshot {} of error to path {} " , strReportImage, strScreenshotSource);
			File tempFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(tempFile, new File(strScreenshotSource));
		}

		if(strLog.contains("PASSED")){
			logMessage("PASSED", strLog);
		}
		else logMessage("FAILED", strLog);
	}

	/**
	 * Logs messages and Formats report details
	 * @param strPassFail execution status
	 * @param strMessage execution details
	 */
	public void logMessage(String strPassFail, String strMessage) {
		File outputImageDir = new File(Constants.strScreenshotPath);
		File screenshotName = new File(strScreenshotSource);

		if (strPassFail.compareTo("PASSED")==0){
			Reporter.getCurrentTestResult().setStatus(ITestResult.SUCCESS);
			attachImageOnReport(outputImageDir, screenshotName, strMessage);
		}else if(strPassFail.compareTo("IGNORE")==0){
			Reporter.getCurrentTestResult().setStatus(ITestResult.SKIP);
			Reporter.log("<br>" + strMessage);
		}else if(strPassFail.compareTo("SKIPPED")==0){
			Reporter.getCurrentTestResult().setStatus(ITestResult.SKIP);
			Reporter.log("<br>" + strMessage);
		}
		else{
			Reporter.getCurrentTestResult().setStatus(ITestResult.FAILURE);
			attachImageOnReport(outputImageDir, screenshotName, strMessage);
			fail(strMessage);
		}
	}

	/**
	 * Attach screenshots on the report
	 * @param outputDir screenshot file location
	 * @param screenShotName screenshot filename
	 * @param msg screenshot description
	 */
	public void attachImageOnReport(File outputDir, File screenShotName, String msg) {

		String ESCAPE_PROPERTY = "org.uncommons.reportng.escape-output";
		System.setProperty(ESCAPE_PROPERTY, "false");

		StringBuffer htmlOutput = new StringBuffer();
		htmlOutput.append("<p><a href=\"").append(outputDir.getName());
		htmlOutput.append(File.separator).append(screenShotName.getName());
		htmlOutput.append("\" target=\"_blank\">");
		htmlOutput.append("<img src=\"");
		htmlOutput.append(outputDir.getName()).append(File.separator).append(screenShotName.getName());
		htmlOutput.append("\" width=\"256px\"/>");
		htmlOutput.append("</a>");
		htmlOutput.append(msg);

		Reporter.log(htmlOutput.toString());
	}

	/**
	 * Wait for visibility of element
	 * Element will proceed with action without waiting timeout to complete but will throw an error if exceeded
	 * @param driver
	 * @param by
	 * @param strScreenID
	 * @throws Exception
	 */
	public void waitForImageVisibility(WebDriver driver, By by, String strScreenID) throws Exception {
		try {
			log.debug("Wait for locator: {} to display in {} seconds", by, Constants.WAIT_TIMEOUT_SEC);
			WebDriverWait wait = new WebDriverWait(driver, Constants.WAIT_TIMEOUT_SEC);
			wait.until(ExpectedConditions.visibilityOfElementLocated(by));
			log.debug("Object Id {} is displayed", by);
		}
		catch (Exception e) {
			strLog = "|Failed|TimeoutException on ElementText (" + by + ") is encountered!";
			webdriver_takeScreenshot(driver, strLog, strScreenID);
		}
	}

	/**
	 * Wait for element to become clickable
	 * Element will proceed with action without waiting timeout to complete but will throw an error if exceeded
	 * @param driver
	 * @param by
	 * @param strScreenID
	 * @throws Exception
	 */
	public void waitForElementToBeClickable(WebDriver driver, By by, String strScreenID) throws Exception {
		try {
			log.debug("Wait for locator: {} to display in {} seconds", by, Constants.WAIT_TIMEOUT_SEC);
			WebDriverWait wait = new WebDriverWait(driver, Constants.WAIT_TIMEOUT_SEC);
			wait.until(ExpectedConditions.elementToBeClickable(by));
			log.debug("Object Id : {} is displayed", by);
		}
		catch (Exception e) {
			strLog = "|Failed|TimeoutException on ElementText (" + by + ") is encountered!";
			webdriver_takeScreenshot(driver, strLog, strScreenID);
		}
	}

	//************************************** OBJECT ACTIONS **********************************************************

	/**
	 * Assert correctness of text present in the page based on expected value
	 * @param driver
	 * @param by
	 * @param strObjValue
	 * @param strScreenID
	 * @throws Exception
	 */
	public void assertEqualTextPresent(WebDriver driver, By by, String strObjValue, String strScreenID) throws Exception {
		try{
			waitForImageVisibility(driver, by, strScreenID);
			assertEquals(driver.findElement(by).getText(), strObjValue);
			log.debug("Assertion Passed! {} verified present", driver.findElement(by).getText());
		} catch (AssertionError e){
			strLog = "|Failed|Assertion Error on Element (" + by + ") not found!";
			webdriver_takeScreenshot(driver, strLog, strScreenID);
		}catch (Exception e){
			strLog = "|Failed|Element (" + by + ") not found!";
			webdriver_takeScreenshot(driver, strLog, strScreenID);
		}
	}

	/**
	 * Clicks an object element (i.e. button etc.)
	 * @param driver
	 * @param by
	 * @throws Exception
	 */
	public void click(WebDriver driver, By by, String strScreenID) throws Exception {
		try {
			waitForElementToBeClickable(driver, by, strScreenID);
			driver.findElement(by).click();

		} catch (Exception e) {
			strLog = "|Failed|Element (" + by + ") not found!";
			webdriver_takeScreenshot(driver, strLog, strScreenID);
		}
	}

	/**
	 * Click accept when cookies alert is displayed
	 * @param driver
	 * @param strScreenID
	 * @throws Exception
	 */
	public void clickCookiesAlert(WebDriver driver, String strScreenID) throws Exception {
		try {
			By by = By.xpath(Constants.COOKIES_ALERT_XPATH);
			if (driver.findElements(by).size() > 0) {
				waitForImageVisibility(driver, by, strScreenID);
				WebElement element = driver.findElement(By.xpath(Constants.COOKIES_LINK_XPATH));
				element.click();
				log.debug("Accept/Click on Cookies Alert: ", element);
			}
			else log.debug("No Cookies Alert displayed");
		}catch (Exception e){
			strLog = "|IGNORE|Element (" + Constants.COOKIES_ALERT_XPATH + ") not found!";
			logMessage("IGNORE", strLog);
			log.warn("No alert on screen is displayed");
		}
	}

	/**
	 * Generate Reference Number and enter in the corresponding field
	 * @param driver
	 * @param by
	 * @param strStatObjValue
	 * @param strScreenID
	 * @throws Exception
	 */
	public void enterReferenceNumber(WebDriver driver, By by, String strStatObjValue, String strScreenID) throws Exception {

		log.debug("Generating reference number for {}", strStatObjValue);
		try{
			if(strStatObjValue.contains("Quote")){
				String strRefNo = "QOT-AUTO-"+getCurrentDateTime();
				waitForImageVisibility(driver, by, strScreenID);
				Reporter.log("<br> New Quote Reference is  " +strRefNo);

				driver.findElement(by).sendKeys(strRefNo);
			}
			else if(strStatObjValue.contains("POC")){
				String strRefNo = "POC-AUTO-"+getCurrentDateTime();
				waitForImageVisibility(driver, by, strScreenID);
				Reporter.log("<br> New POC Reference is " +strRefNo);

				driver.findElement(by).sendKeys(strRefNo);
			}
		}catch (Exception e){
			strLog = "|Failed|Element (" + by + ") not found!";
			webdriver_takeScreenshot(driver, strLog, strScreenID);
		}
	}

	/**
	 * Expand Order Menu list
	 * @param driver
	 * @param by
	 * @param strScreenID
	 * @throws Exception
	 */
	public void expandOrderMenu(WebDriver driver, By by, String strScreenID) throws Exception {
		try {
			if (driver.findElements(By.xpath(Constants.ORDERS_UNACTIONED_XPATH)).size() < 1) {
				driver.findElement(by).click();
			}
		} catch(Exception e){
			strLog = "|Failed|Element (" + by + ") not found!";
			webdriver_takeScreenshot(driver, strLog, strScreenID);
		}
	}

	/**
	 * Fill up unit cost in all line items
	 * @param driver
	 * @param by
	 * @param text
	 * @param strScreenID
	 * @throws Exception
	 */
	public void enterUnitCostForMultipleLineItems(WebDriver driver, By by, String text, String strScreenID) throws Exception {

		Integer itemCount = driver.findElements(by).size();

		if(itemCount > 0){
			for(int i=0; i<itemCount; i++){
				String strElem = "name='equipments[0].lineItems[" + i + "].unitCost'";
				By byElem = By.xpath(strElem);
				enterText(driver, byElem, text, strScreenID);
			}
		}
	}

	/**
	 * To verify expected screen is displayed through Page Title
	 * @param driver
	 * @param strStatObjValue
	 * @param strScreenID
	 * @throws Exception
	 */
	public void getPageTitle(WebDriver driver, String strStatObjValue, String strScreenID) throws Exception {
		String strPageTitle = driver.getTitle();
		try{
			assertEquals(strPageTitle, strStatObjValue);
		} catch (AssertionError e){
			strLog = "|Failed|Page Title (" + strPageTitle + ") not equal to (" + strStatObjValue + ")";
			webdriver_takeScreenshot(driver, strLog, strScreenID);
		}catch (Exception e){
			strLog = "|Failed|Page Title (" + strPageTitle + ") not equal to (" + strStatObjValue + ")!";
			webdriver_takeScreenshot(driver, strLog, strScreenID);
		}
	}

	/**
	 * Mouse over, then click on an object element (i.e. button etc.)
	 * @param driver
	 * @param by
	 * @throws Exception
	 */
	public void hoverAndClick(WebDriver driver, By by, String strScreenID) throws Exception{
		try{
			waitForImageVisibility(driver, by, strScreenID);

			Actions builder = new Actions(driver);
			Action action = builder
			 	.moveToElement(driver.findElement(by))
				.click()
				.build();
			action.perform();
			log.debug("...hover and click on Element: {}", by);

//			WebDriverWait wait = new WebDriverWait(driver, Constants.WAIT_TIMEOUT_SEC);
//			WebElement oldHtml = driver.findElement(By.tagName("html"));
//			wait.until(ExpectedConditions.stalenessOf(oldHtml));
		} catch(Exception e) {
			strLog = "|Failed|Element (" + by + ") not found!";
			webdriver_takeScreenshot(driver, strLog, strScreenID);
		}
	}

	/**
	 * Click twice on the web element
	 * @param driver
	 * @param by
	 * @param strScreenID
	 * @throws Exception
	 */
	public void hoverAndDoubleClick(WebDriver driver, By by, String strScreenID) throws Exception{
		try{
			Actions builder = new Actions(driver);
			Action action = builder
					.moveToElement(driver.findElement(by))
					.click(driver.findElement(by))
					.click(driver.findElement(by))
					.build();
			action.perform();
			log.debug("...hover and click twice on Element: {}", by);

		} catch(Exception e) {
			strLog = "|Failed|Element (" + by + ") not found!";
			webdriver_takeScreenshot(driver, strLog, strScreenID);
		}
	}

	/**
	 * Use of Action Class to mouse over and enter text into an element field
	 * @param driver
	 * @param by
	 * @param strObjValue
	 * @param strScreenID
	 * @throws Exception
	 */
	public void hoverAndEnterText(WebDriver driver, By by, String strObjValue, String strScreenID) throws Exception {
		try{
			Actions builder = new Actions(driver);
			Action seriesOfActions = builder
					.moveToElement(driver.findElement(by))
					.click()
					.sendKeys(driver.findElement(by), strObjValue)
					.release()
					.build();
			seriesOfActions.perform();
			log.debug("...hover and click before entering text on Element: {}", by);

		} catch(Exception e) {
			strLog = "|FAILED|Element (" + by + ") not found!";
			webdriver_takeScreenshot(driver, strLog, strScreenID);
		}
	}

	/**
	 * Log out from trade by going to user profile link and clicking on log out
	 * @param driver
	 * @param strScreenID
	 * @throws Exception
	 */
	public void logOutFromTrade(WebDriver driver, String strScreenID) throws Exception {
		log.debug("...Logging out from Trade Application");
		By byProfileLink = By.xpath(Constants.LOGOUT_PROFILE_LINK);
		By byLogOutLink = By.xpath(Constants.LOGOUT_LINK);
		click(driver, byProfileLink, strScreenID);
		hoverAndClick(driver, byLogOutLink, strScreenID);
		log.debug("...Successfully Logged out from Trade Application");
	}

	/**
	 * Scroll element into view before then click on link/button
	 * @param driver implements methods in WebDriver interface
	 * @param by element locator
	 * @param strScreenID identifies the screen id
	 * @throws Exception
	 */
	public void moveElementIntoViewAndClick(WebDriver driver, By by, String strScreenID) throws Exception {
		scrollIntoView(driver, by, strScreenID);
		click(driver, by, strScreenID);
	}

	/**
	 * Scroll element into view before entering text
	 * @param driver use to implement methods in WebDriver
	 * @param by element locator
	 * @param text
	 * @param strScreenID
	 * @throws Exception
	 */
	public void moveElementIntoViewAndEnterText(WebDriver driver, By by, String text, String strScreenID) throws Exception {
		scrollIntoView(driver, by, strScreenID);
		enterText(driver, by, text, strScreenID);
	}

	/**
	 * Scroll element into view before performing the verification
	 * @param driver use to implement methods in WebDriver
	 * @param by element locator
	 * @param text accepts Strings
	 * @param strScreenID String to hold the screen id
	 * @throws Exception
	 */
	public void moveElementIntoViewAndVerifyText(WebDriver driver, By by, String text, String strScreenID) throws Exception {
		scrollIntoView(driver, by, strScreenID);
		verifyTextIsPresent(driver, by, text, strScreenID);
	}

	/**
	 * Verifies if text is present
	 * @param driver
	 * @param strObjID
	 * @return
	 * @throws Exception
	 */
	public boolean isTextPresent(WebDriver driver, String strObjID) throws Exception{
		boolean blnFlag = false;
		if(driver.getPageSource().contains(strObjID)){
			blnFlag = true;
		}else{
			blnFlag = false;
		}
		return blnFlag;
	}

	/**
	 * scroll down page to put the field into view
	 * @param driver
	 * @param by
	 */
	public void scrollIntoView(WebDriver driver, By by, String strScreenID) throws Exception {
		try{
			waitForImageVisibility(driver, by, strScreenID);
			JavascriptExecutor jse2 = (JavascriptExecutor)driver;
			jse2.executeScript("arguments[0].scrollIntoView()", driver.findElement(by));
			log.debug("Scrolled into view element: {}", by);
		} catch (Exception e) {
			strLog = "|Failed|Element (" + by + ") not found!";
			webdriver_takeScreenshot(driver, strLog, strScreenID);
		}

	}


	/**
	 * Selects an option from a dropdown
	 * @param driver
	 * @param by
	 * @param strStatObjValue
	 * @throws Exception
	 */
	public void selectByVisibleText(WebDriver driver, By by, String strStatObjValue, String strScreenID) throws Exception{

		try{
			WebElement element = driver.findElement(by);
			new Select(element).selectByVisibleText(strStatObjValue);
			log.debug("Select value {} from dropdown field {}", strStatObjValue, element.getText());
		} catch(TimeoutException e) {
			strLog = "|Failed|Element (" + by + ") not found!";
			webdriver_takeScreenshot(driver, strLog, strScreenID);
		} catch(NoSuchElementException e) {
			strLog = "|Failed|Element (" + by + ") not found!";
			webdriver_takeScreenshot(driver, strLog, strScreenID);
		}
	}

	/**
	 * Select value from dropdown list if field has no value or blank else retain original value
	 * @param driver
	 * @param by
	 * @param text
	 * @param strScreenID
	 */
	public void selectByVisibleTextIfNoValue(WebDriver driver, By by, String text, String strScreenID){
		WebElement element = driver.findElement(by);
		Select select = new Select(element);
		WebElement option = select.getFirstSelectedOption();
		String defaultItem = option.getText();

		if(defaultItem.equals("Select")) {
			select.selectByVisibleText(text);
			log.debug("Selected value {} from dropdown", text);
		}
	}

	/**
	 * Enter text if field is blank else retain original value
	 * @param driver
	 * @param by
	 * @param text
	 * @param strScreenID
	 * @throws Exception
	 */
	public void sendKeysIfBlank(WebDriver driver, By by, String text, String strScreenID) throws Exception {

		try{
			WebElement element = driver.findElement(by);
			String strElem = element.getAttribute("value");
			if(strElem.isEmpty()) {
				element.sendKeys(text);
			}
		} catch(Exception e){
			strLog = "|Failed|Element (" + by + ") not found!";
			webdriver_takeScreenshot(driver, strLog, strScreenID);
		}
	}

	/**
	 * Clicks on Submit button and confirms the submission
	 * @param driver
	 * @param by
	 * @param strScreenID
	 * @throws Exception
	 */
	public void submitDocument(WebDriver driver, By by, String strScreenID) throws Exception {
		try{
			driver.findElement(by).click();
			if (driver.findElements(By.xpath(Constants.SUBMIT_CONFIRM_XPATH)).size() > 0) {
				driver.findElement(By.xpath(Constants.SUBMIT_CONFIRM_XPATH)).click();
				log.debug("Submit document by clicking on Confirm button");
			} else {
				driver.findElement(By.xpath(Constants.SUBMIT_YES_XPATH)).click();
				log.debug("Submit document by clicking on YES option");
			}
		} catch (Exception e){
			strLog = "|Failed|Element (" + by + ") not found!";
			webdriver_takeScreenshot(driver, strLog, strScreenID);
		}
	}

	/**
	 * Input texts on a Textbox
	 * @param driver
	 * @param by
	 * @param strObjValue
	 * @throws Exception
	 */
	public void enterText(WebDriver driver, By by, String strObjValue, String strScreenID) throws Exception{
		try {
			waitForImageVisibility(driver, by, strScreenID);
			driver.findElement(by).clear();
			driver.findElement(by).sendKeys(strObjValue);
			log.debug("Successfully input string: {} into text field", driver.findElement(by).getAttribute("value"));
		} catch(Exception e) {
			strLog = "|FAILED|Element (" + by + ") not found!";
			webdriver_takeScreenshot(driver, strLog, strScreenID);
		}
	}


	/**
	 * Verify link contains text or Strings but this will not cause failure of test script
	 * @param driver
	 * @param by
	 * @param strStatObjValue
	 * @param strScreenID
	 * @throws Exception
	 */
	public void verifyTextIsPresent(WebDriver driver, By by, String strStatObjValue, String strScreenID) throws Exception {
		try{
			log.debug("Verifying text on element {} ", by);
			waitForImageVisibility(driver, by, strScreenID);
			String elemText = driver.findElement(by).getText();

			if(!elemText.contains(strStatObjValue)){
				waitForImageVisibility(driver, by, strScreenID);
				strLog = "|Verification Failed|Element Text (" + elemText + ") not found!";
				Reporter.log("<br>" +strLog+ " Element locator used: " +by);
				log.error("{}. Element locator used: {}", strLog, by);
			}
			log.debug("Element String value: {} is found", elemText);
		}catch (Exception e){
			strLog = "|Failed|Element (" + by + ") not found!";
			webdriver_takeScreenshot(driver, strLog, strScreenID);
		}
	}

	/**
	 * Check if element is active before proceeding with other actions
	 * @param driver
	 * @param by
	 * @param strScreenID
	 * @throws Exception
	 */
	public void verifyElementIsEnabled(WebDriver driver, By by, String strScreenID) throws Exception {
		try{
			waitForImageVisibility(driver, by, strScreenID);

			if(!driver.findElement(by).isEnabled()){
				strLog = "|Failed|Element (" + by + ") not active!";
				log.debug(" {} ", strLog);
				webdriver_takeScreenshot(driver, strLog, strScreenID);
			}
			log.debug(" {} is enabled", by);
		}catch (Exception e){
			strLog = "|Failed|Element (" + by + ") not found!";
			webdriver_takeScreenshot(driver, strLog, strScreenID);
		}
	}

	/**
	 * Wait for DOM page to load
	 * @param driver
	 */
	public void waitForPageLoad(WebDriver driver) {

		Wait<WebDriver> wait = new WebDriverWait(driver, 30);
		wait.until(driver1 -> {
			log.debug("Current Window State : {} ",
					 String.valueOf(((JavascriptExecutor) driver1).executeScript("return document.readyState")));
			return String
					.valueOf(((JavascriptExecutor) driver1).executeScript("return document.readyState"))
					.equals("complete");
		});
	}


}
