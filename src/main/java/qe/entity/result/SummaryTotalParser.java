package qe.entity.result;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import qe.exception.GUIException;
import qe.utils.FileLoader;

/**
 *Parses Summary_totals.txt
 *
 *@author felias
 */
public class SummaryTotalParser {
	private File report;
	private Matcher m;
	public SummaryTotalParser(File file) {
    report=file;
	}
	
	/**
	 * Process Summary_totals.txt using regular expression to extract test summary
	 * @return Map: key - test name, value - test results
	 * @throws GUIException
	 * @throws FileNotFoundException
	 */
	public HashMap<String,TestResult> processReport() throws GUIException,FileNotFoundException{
		Pattern p = Pattern.compile("([^\\s]+)\\s*([0-9]+)\\s*([0-9]+)\\s*([0-9]+)\\s*([0-9]+).*");
		m = p.matcher(FileLoader.readFile(report));

		boolean resultFound = false;
        HashMap<String,TestResult> testResults=new HashMap<String, TestResult>();
		while (m.find()) {		
			TestResult t=new TestResult();
			t.setNumberOfSuccessfulTests(Integer.parseInt(m.group(2)));
			t.setNumberOfErrorTests(Integer.parseInt(m.group(3)));
			t.setNumberOfTotalTests(Integer.parseInt(m.group(4)));
			t.setNumberOfSkippedTests(Integer.parseInt(m.group(5)));
			testResults.put(m.group(1), t);
			resultFound = true;
		}
		if (resultFound == false) {
			throw new GUIException("No regular expression match for getting results. The report is malformed.");
		}
		return testResults;
	}
}
