/**
 * @author cherylreyes
 *
 * Constant variables
 *
 * Change Log:
 * --- Follow this format <Name> <Date> <Change Description>
 *
 */

package shipservtrade.config;

public class Constants {

	//Path to the Mapping CSV files
	public static final String strDataEnginePath = "src\\test\\java\\shipservtrade\\dataEngine\\";
	public static final String inputCSVLoginMapping = strDataEnginePath + "LoginMapping.csv";
	public static final String inputCSVQuoteMapping = strDataEnginePath + "QuoteMapping.csv";
	public static final String inputCSVPocMapping = strDataEnginePath + "PocMapping.csv";

	//Path to Configuration.csv file
	public static final String inputCSVConfiguration = strDataEnginePath + "Configuration.csv";

	//Path to Test Cases csv file
	public static final String inputTestCasesLoginCSV = strDataEnginePath + "Login.csv";
	public static final String inputTestCaseQuoteCSV = strDataEnginePath + "Quote.csv";
	public static final String inputTestCasePocCSV = strDataEnginePath + "POC.csv";

	//Path to Object Repositories
	public static final String objectRepoFile = strDataEnginePath + "object.properties";

	//Directory on where screenshots are saved
	public static final String strScreenshotPath = "target\\surefire-reports\\html\\TestRunImage\\";
	public static final String strPassedScreenshotPath = "target\\surefire-reports\\html\\TestPassedRunImage\\";

	//Browser Driver
	public static final String resource_path_geckodriver = "resources\\geckodriver\\geckodriver.exe";
	public static final String resource_path_chrome = "resources\\chromedriver\\chromedriver.exe";
	public static final String resource_path_ie = "resources\\iedriverserver\\IEDriverServer.exe";
	public static final String resource_path_edge = "resources\\edgedriver_win64\\msedgedriver.exe";

	public static final String COOKIES_ALERT_XPATH = "//div[@class='adroll_consent_button']";
	public static final String COOKIES_LINK_XPATH = "//div[contains(text(),'Allow')]";
	public static final String SUBMIT_CONFIRM_XPATH ="//button[contains(text(),'Confirm')]";
	public static final String SUBMIT_YES_XPATH ="//button[contains(text(),'Yes')]";

	public static final long  WAIT_TIMEOUT_SEC = 40;

	//Calendar-picker
	public static final String CALENDAR_PICKER_MONTH = "//div[@class='CalendarMonthGrid_month__horizontal CalendarMonthGrid_month__horizontal_1']//div[@class='CalendarMonth_caption CalendarMonth_caption_1']";
	public static final String CALENDAR_PICKER_DAY_ROW = "//div[@class='CalendarMonthGrid_month__horizontal CalendarMonthGrid_month__horizontal_1']//tr";
	public static final String CALENDAR_PICKER_DAY_COL = "//div[@class='CalendarMonthGrid_month__horizontal CalendarMonthGrid_month__horizontal_1']//tr//td";
	public static final String CALENDAR_NAVIGATION = "//div[@class='DayPickerNavigation_button DayPickerNavigation_button_1 DayPickerNavigation_button__default DayPickerNavigation_button__default_2 DayPickerNavigation_button__horizontal DayPickerNavigation_button__horizontal_3 DayPickerNavigation_button__horizontalDefault DayPickerNavigation_button__horizontalDefault_4 DayPickerNavigation_rightButton__horizontalDefault DayPickerNavigation_rightButton__horizontalDefault_5']//*[@class='DayPickerNavigation_svg__horizontal DayPickerNavigation_svg__horizontal_1']";

	public static final String ORDERS_UNACTIONED_XPATH = "//*[@href='/orders/unactioned']";

	//Element Strings for Log out
	public static final String LOGOUT_PROFILE_LINK = "//div[@class='sc-dOkuiw jlfjSn']";
	public static final String LOGOUT_LINK = "//div[contains(text(),'Log out')]";

}
