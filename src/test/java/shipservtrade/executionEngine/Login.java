/**
 * @author cherylreyes
 *
 * The TestNG class file to help provide easy annotations, grouping, sequencing & parameterizing of Test Cases
 *
 * Change Log:
 * --- Follow this format <Name> <Date> <Change Description>
 *
 */


package shipservtrade.executionEngine;

import org.testng.ITest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import shipservtrade.config.Constants;
import shipservtrade.utility.ExcelUtils;

public class Login extends BaseTest implements ITest {

	private final int param;
	public ExcelUtils class_excelUtils = new ExcelUtils();
	private String testName = "";

	String docType = "Login";
	String appName = "Trade";

	public Login(int param) {
		this.param = param+1;
	}

	@Override
	public String getTestName() {
		return this.testName = docType + "_" + param;
	}

	@BeforeClass public void setup() throws Exception{
		class_excelUtils.getTestCasesCSV(Constants.inputTestCasesLoginCSV, docType, appName);
	}

	@AfterClass public void tearDown() {
		class_excelUtils.exit();
	}

	@Test
	public void Trade_Login() throws Exception {
		class_excelUtils.webdriver_runTestCase(docType, appName, getTestName(), Constants.inputCSVLoginMapping);
	}

}
