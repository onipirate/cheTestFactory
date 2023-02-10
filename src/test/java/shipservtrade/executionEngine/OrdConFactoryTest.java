/**
 * @author cherylreyes
 *
 * The TestNG class file uses @Factory annotation to iterate and execute test cases in a Test Suite
 *
 * Change Log:
 * --- Follow this format <Name> <Date> <Change Description>
 *
 */


package shipservtrade.executionEngine;

import org.testng.annotations.Factory;
import shipservtrade.config.Constants;
import shipservtrade.utility.ExcelUtils;

import java.io.IOException;

public class OrdConFactoryTest extends BaseTest {

	public ExcelUtils class_excelUtils = new ExcelUtils();

	@Factory
	public Object[] ordConFactoryMethod() throws IOException {
		Integer tcCount = class_excelUtils.count(this.getClass().getName().substring(30), Constants.inputTestCasePocCSV) - 1;
		Object[] obj = new Object[tcCount];

		for (int iter = 0; iter < tcCount; iter++) {
			obj[iter] = new OrderConfirmation(iter);
		}

		return obj;
	}

}
