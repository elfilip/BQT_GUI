package qe.entity.result;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents result of a test
 * 
 * @author felias
 *
 */
public class TestResult {

	private int numberOfErrorTests = -1;
	private int numberOfTotalTests = -1;
	private int numberOfSuccessfulTests = -1;
	private int numberOfSkippedTests = -1;

	private HashMap<String, QueryFailure> failures = new HashMap<String,QueryFailure>();
	private boolean failuresLoaded = false;

	/**
	 * Returns number of failed queries
	 * 
	 * @return number of failed queries
	 */
	public int getNumberOfErrorTests() {
		return numberOfErrorTests;
	}

	public void setNumberOfErrorTests(int numberOfErrorTests) {
		this.numberOfErrorTests = numberOfErrorTests;
	}

	/**
	 * Returns number of all executed queries
	 * @return number of all executed queries
	 */
	public int getNumberOfTotalTests() {
		return numberOfTotalTests;
	}

	public void setNumberOfTotalTests(int numberOfTotalTests) {
		this.numberOfTotalTests = numberOfTotalTests;
	}

	/**
	 * Returns number of successful queries
	 * @return number of successful queries
	 */
	public int getNumberOfSuccessfulTests() {
		return numberOfSuccessfulTests;
	}

	public void setNumberOfSuccessfulTests(int numberOfSuccessfulTests) {
		this.numberOfSuccessfulTests = numberOfSuccessfulTests;
	}

	/**
	 * Adds details about one query failure
	 * @param fail failured query details
	 */
	public void addFailure(String key, QueryFailure fail) {
		failures.put(key, fail);
	}
	/**
	 * Checks whether the query failures have been already loaded for this test
	 * @return true if the query failures are loaded
	 */
	public boolean isFailuresLoaded() {
		return failuresLoaded;
	}

	public void setFailuresLoaded(boolean failuresLoaded) {
		this.failuresLoaded = failuresLoaded;
	}
	/**
	 * Returns all query failures as list
	 * @return list of query failures
	 */
	public HashMap<String,QueryFailure> getFailures() {
		return failures;
	}

	/**
	 * Returns number of skipped queries
	 * @return number of skipped queries
	 */
	public int getNumberOfSkippedTests() {
		return numberOfSkippedTests;
	}

	public void setNumberOfSkippedTests(int numberOfSkippedTests) {
		this.numberOfSkippedTests = numberOfSkippedTests;
	}

}
