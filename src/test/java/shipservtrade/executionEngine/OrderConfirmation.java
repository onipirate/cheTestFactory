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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import shipservtrade.config.Constants;
import shipservtrade.utility.ExcelUtils;

public class OrderConfirmation extends BaseTest implements ITest {

    private final int param;
    public ExcelUtils class_excelUtils = new ExcelUtils();
    private String testName = "";

    String docType = "POC";
    String appName = "Trade";

    public OrderConfirmation(int param) {
        this.param = param+1;
    }

    @BeforeClass public void setup() throws Exception{
        class_excelUtils.getTestCasesCSV(Constants.inputTestCasePocCSV, docType, appName);
    }

    @AfterClass public void tearDown() {
        class_excelUtils.exit();
    }

    @Test
    public void Trade_Poc() throws Exception {
        class_excelUtils.webdriver_runTestCase(docType, appName, getTestName(), Constants.inputCSVPocMapping);
    }

    @Override
    public String getTestName() {
        return this.testName = docType + "_" + param;
    }

}
