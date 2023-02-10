/**
 * @author cherylreyes
 *
 * The TestNG class used for logging in start and end time after Suite Run
 *
 * Change Log:
 * --- Follow this format <Name> <Date> <Change Description>
 *
 */

package shipservtrade.executionEngine;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import shipservtrade.utility.ExcelUtils;

public class BaseTest {

	public ExcelUtils class_excelUtils = new ExcelUtils();

	@BeforeSuite(alwaysRun = true)
	public void getStartTime() {
		class_excelUtils.logStartTime("ShipServ TradeSupplier - WebDriver Start Run");
	}

	@AfterSuite(alwaysRun = true)
	public void getEndTime() {
		class_excelUtils.logEndTime("ShipServ TradeSupplier - WebDriver End Run");
	}

}
