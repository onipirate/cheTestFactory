/**
 * @author cherylreyes
 *
 * This class file will contain methods for extracting data from CSV file, browser configuration
 * and other functions necessary for running the test cases
 *
 * Change Log:
 * --- Follow this format <Name> <Date mm-dd-yr> <Change Description>
 */


package shipservtrade.utility;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import java.text.DateFormat;
import java.util.Properties;

import com.csvreader.CsvReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.testng.Reporter;

import org.apache.commons.csv.*;

import shipservtrade.config.ActionKeywords;
import shipservtrade.config.Constants;

public class ExcelUtils {

	public String strBrowser = null;
	public JavascriptExecutor js = null;

	public WebDriver driver;
	public int intCount = 0; // log counter
	public long start;
	public boolean booleanStartSE = true;
	private static final String ESCAPE_PROPERTY = "org.uncommons.reportng.escape-output";
	public String strCount;
	public String strTestCaseName = null;

	public int intTypeLimit = 1000;	// allowed number of test cases
	public boolean blnGo = false; // flag to check if the test case will be executed or not
	public By by = null;
	public String strScreenID = null;
	public String strActionDesc = null;
	public String strObjDesc = null;
	public String strActionType = null;
	public String strStatObjValue = null;
	public String strTestCaseRun = null;

	// Application Types
	public String[] strTCSSTS;
	public String[] strTCSSALL;

	public ActionKeywords class_actionKeywords = new ActionKeywords();
	public JenkinsProperties jenkins = new JenkinsProperties();
	private static Logger log = LogManager.getLogger(ExcelUtils.class.getName());

	/**
	 * Handles the Browser configuration and then Opens the test URL
	 * @param driver
	 * @throws Exception
	 */
	public void webdriver_runConfig(WebDriver driver) throws Exception{
		if(!booleanStartSE){
			driver.quit();
			booleanStartSE = false;
		}

		String strURL = jenkins.getJenkinsWebAppURL();
		if(strURL != null && !strURL.isEmpty()){
			log.debug("using Web URL from Jenkins: {}", strURL);
		}else {
			strURL = getCellValueCSV(Constants.inputCSVConfiguration, "Test URL");
			log.debug("using Web URL from file: {}", strURL);
		}
		class_actionKeywords.webdriver_open(driver, strURL);
	}

	/**
	 * Tears down/closes browser
	 */
	public void exit(){
		if(driver != null) {
			driver.quit();
		}
	}

	/**
	 * Reads the test data from the specified column in CSV
	 * @param strInputCSV CSV filename
	 * @param strColValue CSV column to read
	 * @return String text from specified column
	 * @throws Exception file missing
	 */
	public String getCellValueCSV(String strInputCSV, String strColValue) throws Exception{

		Reader reader = Files.newBufferedReader(Paths.get(strInputCSV));
		CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
				.withFirstRecordAsHeader()
				.withIgnoreHeaderCase()
				.withTrim());

		String strValue = "";

		for (CSVRecord csvRecord : csvParser){
			strValue = csvRecord.get(strColValue);
		}
		csvParser.close();
		return strValue;
	}

	/**
	 * Initializes Browser
	 * @param browser browser name from Configuration file
	 */
	public void configureBrowser(String browser) {
		FirefoxOptions firefoxOptions = new FirefoxOptions();
		firefoxOptions.setCapability("javascript.enabled", false);
		firefoxOptions.setAcceptInsecureCerts(false);
		firefoxOptions.setHeadless(true);
		firefoxOptions.setCapability("marionette", true);
		System.setProperty("webdriver.gecko.driver", Constants.resource_path_geckodriver);

		switch (browser) {
			case "FIREFOX":
				driver = new FirefoxDriver(firefoxOptions);
				log.info("Launching {} as browser", browser);
				break;

			case "CHROME":
				ChromeOptions options = new ChromeOptions();
//				options.addArguments("start-maximized");
				options.addArguments("window-size=1920,1080"); //window-size=1325,744"
				options.addArguments("enable-automation");
				options.addArguments("--headless");
				options.addArguments("--disable-gpu");
				options.addArguments("test-type");
				System.setProperty("webdriver.chrome.driver", Constants.resource_path_chrome);
				driver = new ChromeDriver(options);
				log.info("Launching {} as browser", browser);
				break;

			case "IE":
				InternetExplorerOptions ie = new InternetExplorerOptions();
				ie.setCapability(InternetExplorerDriver.FORCE_CREATE_PROCESS, true);
				ie.setCapability(InternetExplorerDriver.IE_SWITCHES, "-private");
				ie.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
				ie.ignoreZoomSettings();
				System.setProperty("webdriver.ie.driver", Constants.resource_path_ie);
				driver = new InternetExplorerDriver(ie);
				log.info("Launching {} as browser", browser);
				break;

			case "EDGEx64":
				System.setProperty("webdriver.edge.driver", Constants.resource_path_edge);
				EdgeOptions edgeOptions = new EdgeOptions();
				edgeOptions.setCapability("useAutomationExtension", true);
				edgeOptions.setCapability("excludeSwitches", Collections.singletonList("enable-automation"));
				driver = new EdgeDriver(edgeOptions);
				log.info("Launching {} as browser", browser);
				break;

			default:
				log.warn("Browser {} is invalid. Launching Firefox as default browser", browser);
				driver = new FirefoxDriver(firefoxOptions);
		}
		js=(JavascriptExecutor) driver;
	}

	/**
	 * Merges arrays
	 * @param arrays
	 * @return
	 */
	public static String[] merge(String[]... arrays){
		// Count the number of arrays passed for merging and the total size of resulting array
		int count = 0;
		for (String[] array: arrays)
		{
			count += array.length;
		}

		// Create new array and copy all array contents
		String[] mergedArray = (String[]) java.lang.reflect.Array.newInstance(arrays[0][0].getClass(), count);
		int start = 0;
		for (String[] array: arrays)
		{
			System.arraycopy(array, 0, mergedArray, start, array.length);
			start += array.length;
		}
		return mergedArray;
	}

	/**
	 * Prints out Execution Start Date and Time
	 * @param strAppTest
	 */
	public void logStartTime(String strAppTest) {
		start = System.currentTimeMillis();
    	// Display Start Date and Time
    	DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss a");
    	Date date = new Date();
    	log.info("{} - Start Date and Time  : {}",strAppTest, dateFormat.format(date));
    	class_actionKeywords.logMessage("IGNORE", strAppTest+" - Start Date and Time  : " + dateFormat.format(date));
	}

	/**
	 * Prints out Execution End Date and Time
	 * @param strAppTest
	 */
	public void logEndTime(String strAppTest) {
		// display End Date and Time
    	DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss a");
    	Date date = new Date();

    	log.info("{} - End Date and Time    : ", strAppTest, dateFormat.format(date));
    	class_actionKeywords.logMessage("IGNORE", strAppTest+" - End Date and Time    : " + dateFormat.format(date));
    	// Get elapsed time in milliseconds
    	long elapsedTimeMillis = System.currentTimeMillis()-start;

        long time = elapsedTimeMillis / 1000;
        String seconds = Integer.toString((int)(time % 60));
        String minutes = Integer.toString((int)((time % 3600) / 60));
        String hours = Integer.toString((int)(time / 3600));
        for (int i = 0; i < 2; i++) {
        	if (seconds.length() < 2) {
        		seconds = "0" + seconds;
        	}
        	if (minutes.length() < 2) {
        		minutes = "0" + minutes;
        	}
        	if (hours.length() < 2) {
        		hours = "0" + hours;
        	}
        }
		log.info("Total Execution Time : {}:{}:{} ", hours, minutes, seconds);
		class_actionKeywords.logMessage("IGNORE", "Total Execution Time : " + hours + ":" + minutes + ":" + seconds);
	}

	/**
	 * launches driver based on Web Browser
	 * @throws Exception
	 */
	public void launchBrowser() throws Exception {
		strBrowser = jenkins.getJenkinsBrowser();
		if (strBrowser != null && !strBrowser.isEmpty()) {
			log.debug("Configure browser driver from Jenkins: {} ", strBrowser);
		}
		else {
			strBrowser = getCellValueCSV(Constants.inputCSVConfiguration, "Browser");
			log.debug("Configure browser driver from file: {}", strBrowser);
		}
		configureBrowser(strBrowser);
	}

	/**
	 * Gets all test cases from a CSV source and save to an array
	 * @param strInputCSV
	 * @param strUserType
	 * @param strTestCaseSheetName
	 * @throws Exception
	 */
	public void getTestCasesCSV(String strInputCSV, String strUserType, String strTestCaseSheetName) throws Exception {
		// HINT: By default ReportNG escape the special characters, so in order not to see the href link in the reported log
		// instead of your screenshot you will need to disable this escaping as:
		System.setProperty(ESCAPE_PROPERTY, "false");

		log.debug("Listing Test Cases from {}", strInputCSV);
   		Reader reader = Files.newBufferedReader(Paths.get(strInputCSV));
		CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
				.withFirstRecordAsHeader()
				.withIgnoreHeaderCase()
				.withTrim());

		int intApp = 0;
		int intFill = 0;

		if(strUserType.compareTo("Login")==0 || strUserType.compareTo("Quote")==0 || strUserType.compareTo("POC")==0){
			strTCSSTS = new String[intTypeLimit];
			intFill = 0;

			// fill the arrays with "" values
			while(intFill!=intTypeLimit){
				strTCSSTS[intFill] = "";
				intFill = intFill + 1;
			}

			// save test cases
			for(CSVRecord csvRecord : csvParser){
				strTCSSTS[intApp] = "Trade_" + csvRecord.get("Test Case") + " " + csvRecord.get(strTestCaseSheetName);
				intApp = intApp + 1;
			}

			// sort arrays
			Arrays.sort(strTCSSTS);
			strTCSSALL = merge(strTCSSTS);
			Arrays.sort(strTCSSALL);

		}
	}

	/**
	 * Count the number of rows in a file
	 * @param filename path directory and file name
	 * @return
	 * @throws IOException
	 */
	public int count(String module, String filename) throws IOException {
		InputStream is = new BufferedInputStream(new FileInputStream(filename));
		try {
			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean empty = true;
			while ((readChars = is.read(c)) != -1) {
				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
			}
			log.debug("{} : Test Case count: {}", module, count-1);
			return (count == 0 && !empty) ? 1 : count;

		} finally {
			is.close();
		}
	}

	/**
	 * Load Properties file to get the specific value based on given key
	 * @param objKey
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public String loadPropertiesFile(String objKey, String filename) throws IOException {
		String objVal = null;
		if(objKey!=null && !objKey.isEmpty()) {
			FileInputStream inputStream = new FileInputStream(filename);
			Properties properties = new Properties();
			properties.load(inputStream);
			objVal = properties.getProperty(objKey);
		}
		return objVal;
	}

	/**
	 * Identifies the element locator provided for Object ID
	 * @param objKey page object id
	 */
	public void webdriver_createObjectIdentifier(String objKey) throws IOException {
		String objVal = loadPropertiesFile(objKey, Constants.objectRepoFile);
		if(objVal == null) {
			by = null;
		}else if(objVal.contains("name|")){
			objVal = objVal.replace("name|", "");
			by = By.name(objVal);
		}else if(objVal.contains("id|")){
			objVal = objVal.replace("id|", "");
			by = By.id(objVal);
		}else if(objVal.contains("css=")){
			objVal = objVal.replace("css=", "");
			by = By.cssSelector(objVal);
		}else if(objVal.contains("xpath=")){
			objVal = objVal.replace("xpath=", "");
			by = By.xpath(objVal);
		}else if(objVal.contains("classname|")){
			objVal = objVal.replace("classname|", "");
			by = By.xpath(objVal);
		}
	}

	/**
	 * Checks if the test case needs to be executed. True if is set to RUN otherwise False
	 * @param strUserType
	 * @param strTCType
	 * @param strTestCase
	 * @return
	 * @throws Exception
	 */
	public boolean webdriver_checkToExecute(String strUserType, String strTCType, String strTestCase) {

		if(strUserType.compareTo("Login")==0 || strUserType.compareTo("Quote")==0 || strUserType.compareTo("POC")==0)
		{
			if(Arrays.binarySearch(strTCSSALL, strTCType + "_" + strTestCase + " RUN") >=0 ){
				blnGo = true;}else{blnGo = false;}
		}
		return blnGo;
	}


	/**
	 * Runs the steps indicated in the Test Cases
	 * @param strUserType
	 * @param strTCType
	 * @param strTestCase
	 * @throws Exception
	 */
	public void webdriver_runTestCase(String strUserType, String strTCType, String strTestCase, String strInputFile) throws Exception  {
		intCount = 3; // reset intCount

		CsvReader csvReader = new CsvReader(strInputFile);
		csvReader.readRecord(); // skip the first row of Mapping sheet
		csvReader.readHeaders();

		strTestCaseName = strTCType + "_" + strTestCase;

		blnGo = webdriver_checkToExecute(strUserType, strTCType, strTestCase);

		if(!blnGo){
			class_actionKeywords.logMessage("SKIPPED", strTestCaseName + " - Skipped");
			return;
		}

		log.info("...RUNNING TEST CASE : {} ", strTestCaseName );

		launchBrowser();

		while (csvReader.readRecord()){
			strScreenID = csvReader.get("Scr ID");
			strActionDesc = csvReader.get("Test Steps");
			strObjDesc = csvReader.get("Object Description");
			strActionType = csvReader.get("Action");
			strStatObjValue = csvReader.get("Static Object Value"); //Expected Text Content of an element. For static text only (labels, page title, etc)
			strTestCaseRun = csvReader.get(strTestCase);
			strCount = Integer.toString(intCount);

			webdriver_createObjectIdentifier(strObjDesc);

			if(strTestCaseRun.toUpperCase().compareTo("RUN")==0 || strTestCaseRun.toUpperCase().compareTo("")!=0)
			{
				Reporter.log("<br>" + intCount + "|" + strScreenID + " |" + strActionDesc +  " |" );
				log.info(" {} | {} | {} | {} | {}" , intCount, strScreenID, strActionDesc, strActionType, strStatObjValue);

				switch (strActionType.trim()) {
					case "runConfig":
						webdriver_runConfig(driver);
						break;
					case "assertEqualTextPresent":
						class_actionKeywords.assertEqualTextPresent(driver, by, strStatObjValue, strScreenID);;
						break;
					case "assertLoginErrorMessagePresent":
						if(strTestCaseRun.toUpperCase().compareTo("RUN")!=0){
							log.debug(" Perform Action: {} on Test Case Run value: {} ", strActionType, strTestCaseRun);
							class_actionKeywords.assertEqualTextPresent(driver, by, strTestCaseRun, strScreenID);
						}
						else class_actionKeywords.assertEqualTextPresent(driver, by, strStatObjValue, strScreenID);
						break;
//					case "calendarPicker":
//						class_actionKeywords.calendarPicker(driver, by, strScreenID);
//						break;
					case "click":
						class_actionKeywords.click(driver, by, strScreenID);
						break;
					case "clickCookiesAlert":
						class_actionKeywords.clickCookiesAlert(driver, strScreenID);
						break;
					case "enterReferenceNumber":
						class_actionKeywords.enterReferenceNumber(driver, by, strStatObjValue, strScreenID);
						break;
					case "enter_Password":
						if(strTestCaseRun.toUpperCase().compareTo("RUN")!=0){
							log.debug(" Perform Action: {} on Test Case Run value: {} ", strActionType, strTestCaseRun);
							class_actionKeywords.enterText(driver, by, strTestCaseRun, strScreenID);
						}
						else if(strStatObjValue != null || !strStatObjValue.isEmpty()) {
							String defaultPassword = getCellValueCSV(Constants.inputCSVConfiguration, "Password");
							log.debug(" Perform Action: {} on Test Case Run value: {} ", strActionType, strTestCaseRun);
							class_actionKeywords.enterText(driver, by, defaultPassword, strScreenID);
						}
						else class_actionKeywords.enterText(driver, by, strStatObjValue, strScreenID);
						break;
					case "enter_UserName":
						if(strTestCaseRun.toUpperCase().compareTo("RUN")!=0){
							log.debug(" Perform Action: {} on Test Case Run value: {} ", strActionType, strTestCaseRun);
							class_actionKeywords.enterText(driver, by, strTestCaseRun, strScreenID);
						}
						else if (strStatObjValue != null || !strStatObjValue.isEmpty()) {
							String defaultUserName = getCellValueCSV(Constants.inputCSVConfiguration, "Username");
							class_actionKeywords.enterText(driver, by, defaultUserName, strScreenID);
						}
						else class_actionKeywords.enterText(driver, by, strStatObjValue, strScreenID);
						break;
					case "enterText":
						if(strTestCaseRun.toUpperCase().compareTo("RUN")!=0){
							log.debug(" Perform Action: {} on Test Case Run value: {} ", strActionType, strTestCaseRun);
							class_actionKeywords.enterText(driver, by, strTestCaseRun, strScreenID);
						}
						else class_actionKeywords.enterText(driver, by, strStatObjValue, strScreenID);
						break;
					case "enterTextIfBlank":
						if(strTestCaseRun.toUpperCase().compareTo("RUN")!=0){
							log.debug(" Perform Action: {} on Test Case Run value: {} ", strActionType, strTestCaseRun);
							class_actionKeywords.sendKeysIfBlank(driver, by, strTestCaseRun, strScreenID);
						}
						else class_actionKeywords.sendKeysIfBlank(driver, by, strStatObjValue, strScreenID);
						break;
					case "enterUnitCostForMultipleLineItems":
						if(strTestCaseRun.toUpperCase().compareTo("RUN")!=0){
							log.debug(" Perform Action: {} on Test Case Run value: {} ", strActionType, strTestCaseRun);
							class_actionKeywords.enterUnitCostForMultipleLineItems(driver, by, strTestCaseRun, strScreenID);
						}
						else class_actionKeywords.enterUnitCostForMultipleLineItems(driver, by, strStatObjValue, strScreenID);
						break;
					case "expandOrderMenu":
						class_actionKeywords.expandOrderMenu(driver, by,strScreenID);
						break;
					case "getPageTitle":
						class_actionKeywords.getPageTitle(driver, strStatObjValue, strScreenID);
						break;
					case "hoverAndClick":
						class_actionKeywords.hoverAndClick(driver, by, strScreenID);
						break;
					case "hoverAndDoubleClick":
						class_actionKeywords.hoverAndDoubleClick(driver, by, strScreenID);
						break;
					case "hoverAndEnterText":
						if(strTestCaseRun.toUpperCase().compareTo("RUN")!=0) {
							log.debug(" Perform Action: {} on Test Case Run value: {} ", strActionType, strTestCaseRun);
							class_actionKeywords.hoverAndEnterText(driver, by, strTestCaseRun, strScreenID);
						}
						else class_actionKeywords.hoverAndEnterText(driver, by, strStatObjValue, strScreenID);
						break;
					case "logOutFromTrade":
						class_actionKeywords.logOutFromTrade(driver, strScreenID);
						break;
					case "moveElementIntoViewAndClick":
						class_actionKeywords.moveElementIntoViewAndClick(driver, by, strScreenID);
						break;
					case "moveElementIntoViewAndEnterText":
						if(strTestCaseRun.toUpperCase().compareTo("RUN")!=0){
							log.debug(" Perform Action: {} on Test Case Run value: {} ", strActionType, strTestCaseRun);
							class_actionKeywords.moveElementIntoViewAndEnterText(driver, by, strTestCaseRun, strScreenID);
						}
						else class_actionKeywords.moveElementIntoViewAndEnterText(driver, by, strStatObjValue, strScreenID);
						break;
					case "moveElementIntoViewAndVerifyText":
						class_actionKeywords.moveElementIntoViewAndVerifyText(driver, by, strStatObjValue,strScreenID);
						break;
					case "scrollIntoView":
						class_actionKeywords.scrollIntoView(driver, by, strScreenID);
						break;
					case "selectByVisibleTextFromDropdownList":
						if(strTestCaseRun.toUpperCase().compareTo("RUN")!=0) {
							log.debug(" Perform Action: {} on Test Case Run value: {} ", strActionType, strTestCaseRun);
							class_actionKeywords.selectByVisibleText(driver, by, strTestCaseRun, strScreenID);
						}
						else class_actionKeywords.selectByVisibleText(driver, by, strStatObjValue, strScreenID);
						break;
					case "selectByVisibleTextFromDropdownIfNoValue":
						class_actionKeywords.selectByVisibleTextIfNoValue(driver, by, strStatObjValue, strScreenID);
						break;
					case "submitDocument":
						class_actionKeywords.submitDocument(driver, by, strScreenID);
						break;
					case "verifyElementIsEnabled":
						class_actionKeywords.verifyElementIsEnabled(driver, by, strScreenID);
						break;
					case "verifyTextIsPresent":
						class_actionKeywords.verifyTextIsPresent(driver, by, strStatObjValue, strScreenID);
						break;
					case "waitForPageLoad":
						class_actionKeywords.waitForPageLoad(driver);
						break;
					default:
						intCount = intCount + 1;
						break;
				}
				intCount = intCount + 1;
			}
			else if(strTestCaseRun.toUpperCase().compareTo("")==0)
			{
				intCount = intCount + 1;
			}
		}

		class_actionKeywords.webdriver_takeScreenshot(driver, "PASSED| Test Run Complete", "PASSED");

	}

}
