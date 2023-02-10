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
import org.testng.annotations.*;
import shipservtrade.config.Constants;
import shipservtrade.utility.ExcelUtils;

public class Quote extends BaseTest implements ITest {

    private final int param;
    private String testName = "";
    public ExcelUtils class_excelUtils = new ExcelUtils();

    String docType = "Quote";
    String appName = "Trade";

    public Quote(int param) {
        this.param = param+1;
    }

    @BeforeClass public void setup() throws Exception{
        class_excelUtils.getTestCasesCSV(Constants.inputTestCaseQuoteCSV, docType, appName);
    }

    @AfterClass public void tearDown() {
        class_excelUtils.exit();
    }

    @Test
    public void Trade_Quote() throws Exception{
        class_excelUtils.webdriver_runTestCase(docType, appName, this.testName, Constants.inputCSVQuoteMapping);
    }

    @Override
    public String getTestName() {
        return this.testName = docType + "_" + param;
    }
}
