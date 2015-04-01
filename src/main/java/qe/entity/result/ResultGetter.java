package qe.entity.result;

import java.awt.Frame;
import java.io.File;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qe.entity.settings.Settings;
import qe.exception.GUIException;
import qe.exception.ResultParsingException;
import qe.parsing.dom.DomParserFailure;
import qe.utils.FileLoader;

/**
 * Loads test results from files
 * 
 * @author felias
 */
public class ResultGetter {

	private HashMap<String, TestResult> results;
	private final String COMPARE_ERRORS = "errors_for_COMPARE";
	private String currentTest = null;
	private static final Logger logger = LoggerFactory.getLogger(ResultGetter.class);

	public ResultGetter(Frame frame) {

	}

	/**
	 * Loads summary total from test report
	 * 
	 * @return Map of test results: key- testName, value- TestResult
	 * @throws ResultParsingException
	 */
	public HashMap<String, TestResult> loadSummaryTotal() throws ResultParsingException {
		logger.debug("Parsing summary total file");
		String pathToTestResults = Settings.getInstance().getPathToTestResults();
		if (pathToTestResults == null) {
			throw new ResultParsingException("Path to test results is not configured");
		}
		SummaryTotalParser total = new SummaryTotalParser(new File(pathToTestResults + "/Summary_totals.txt"));

		results = new HashMap<String, TestResult>();
		try {
			results = total.processReport();
		} catch (GUIException e1) {
			throw new ResultParsingException("Unable to parse Test result report: " + e1.getMessage());
		} catch (Exception e) {
			throw new ResultParsingException("Unable to parse Test result report: " + e.getMessage());
		}
		return results;
	}

	/**
	 * Returns currently loaded test results
	 * 
	 * @return
	 */
	public HashMap<String, TestResult> getResults() {
		return results;
	}

	/**
	 * Loads failures for particular test. Failures are loaded from
	 * errors_for_COMPARE folder
	 * 
	 * @param testName
	 *            name of the test for which failures would be loaded
	 * @return test results
	 * @throws ResultParsingException
	 * @throws GUIException
	 */
	public TestResult loadFailuresForTest(String testName) throws ResultParsingException, GUIException {
		logger.debug("Loading all failures for test:" + testName);
		TestResult result = results.get(testName);
		StringBuilder errors = new StringBuilder();
		if (result == null) {
			throw new ResultParsingException("Internal Error: Unknown test name " + testName);
		}
		if (result.isFailuresLoaded()) {
			return result;
		} else {
			File compareErrorsFolder = new File(Settings.getInstance().getPathToTestResults() + "/" + testName + "/" + COMPARE_ERRORS + "/");
			if (compareErrorsFolder.exists() == false) {
				throw new GUIException("No compare errors have been found.");
			}
			for (File file : FileLoader.getAllFilesInFolder(compareErrorsFolder, ".err")) {
				try {
					DomParserFailure parser = new DomParserFailure(file);
					result.addFailure(parser.parseCompareErrorFile());
				} catch (Exception e) {
					errors.append(e.getMessage()).append('\n');
					logger.warn(e.getMessage());
				}
			}
		}
		if (errors.length() > 0) {
			throw new ResultParsingException("Error parsing results: \n" + errors.toString());
		}
		result.setFailuresLoaded(true);
		return result;
	}

	/**
	 * Returns current active test
	 * 
	 * @return current active test
	 */
	public String getCurrentTest() {
		return currentTest;
	}

	/**
	 * Sets current active test
	 * 
	 * @param currentTest
	 */
	public void setCurrentTest(String currentTest) {
		this.currentTest = currentTest;
	}

}
