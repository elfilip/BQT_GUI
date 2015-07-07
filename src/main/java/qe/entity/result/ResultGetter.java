package qe.entity.result;

import java.awt.Frame;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qe.entity.settings.Settings;
import qe.exception.GUIException;
import qe.exception.ResultParsingException;
import qe.parsing.dom.DomParserFailure;
import qe.parsing.sax.MySAXTerminatorException;
import qe.parsing.sax.SaxFailureHandler;
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
	 * Loads failure for particular query. Failure is loaded from errors_for_COMPARE folder
	 * 
	 * 
	 * @param queryValue
	 *            text of the query
	 * @return query failures
	 * @throws ResultParsingException
	 * @throws GUIException
	 */

	public QueryFailure loadFailureDetails(String queryValue) throws ResultParsingException, GUIException {
		logger.debug("Loading details for query: " + queryValue);
		TestResult result = results.get(this.currentTest);
		StringBuilder errors = new StringBuilder();
		if (result == null) {
			throw new ResultParsingException("Internal Error: Unknown test name " + this.currentTest);
		}
		if (result.isFailuresLoaded() == false) {
			throw new ResultParsingException("Internal Error: Failures should be already loaded for this query " + queryValue);
		}
		QueryFailure fail = result.getFailures().get(queryValue);
		if (fail == null) {
			throw new ResultParsingException("Internal Error: This query is unknown " + queryValue);
		}
		if (fail.getQueryName() != null) {
			return fail;
		} else {
			File compareErrorsFolder = new File(Settings.getInstance().getPathToTestResults() + "/" + this.currentTest + "/" + COMPARE_ERRORS + "/");
			if (compareErrorsFolder.exists() == false) {
				throw new ResultParsingException("Can't find compare errors folder " + compareErrorsFolder.getAbsolutePath());
			}
			File file = new File(compareErrorsFolder, fail.getFileName());
			try {
				DomParserFailure parser = new DomParserFailure(file);
				parser.parseCompareErrorFile(fail);
			} catch (Exception e) {
				errors.append(e.getMessage()).append('\n');
				logger.warn(e.getMessage());
			}
		}
		if (errors.length() > 0) {
			throw new ResultParsingException("Error parsing result: \n" + errors.toString());
		}
		result.setFailuresLoaded(true);
		return fail;
	}

	/**
	 * Loads text of failed queries for current test. QueryFailure objects are partially filled with file name and query value.
	 * 
	 * @return Partially filled result for each failed query.
	 * @throws ResultParsingException
	 */
	public TestResult loadFailedQueries() throws ResultParsingException {
		logger.debug("Loading all failures for test:" + this.currentTest);
		TestResult result = results.get(this.getCurrentTest());

		File compareErrorsFolder = new File(Settings.getInstance().getPathToTestResults() + "/" + this.getCurrentTest() + "/" + COMPARE_ERRORS + "/");
		if (!compareErrorsFolder.exists()) {
			return result;
		}
		if (result == null) {
			throw new ResultParsingException("Internal Error: Unknown test name " + this.currentTest);
		}
		SaxFailureHandler handler = new SaxFailureHandler();
		for (File file : FileLoader.getAllFilesInFolder(compareErrorsFolder, ".err")) {
			try {
				InputStream xmlInput = new FileInputStream(file);
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser saxParser = factory.newSAXParser();
				saxParser.parse(xmlInput, handler);
			} catch (MySAXTerminatorException e) {
				QueryFailure f = new QueryFailure();
				f.setQuery(handler.getQueryValue());
				f.setFileName(file.getName());
				result.addFailure(handler.getQueryValue(), f);
			} catch (Exception e) {
				logger.error("Can't parse file:" + file.getAbsolutePath(), e);
			}
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
