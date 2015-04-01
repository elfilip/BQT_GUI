package qe.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qe.entity.settings.Settings;
import qe.exception.ResultParsingException;

/**
 * Util class for loading data from file
 * @author felias
 *
 */
public class FileLoader {
	private static final Logger logger = LoggerFactory.getLogger(FileLoader.class);
	
	/**
	 * Loads file into string
	 * @param file path to file
	 * @return string representation of the file
	 * @throws FileNotFoundException
	 */
	public static String readFile(File file) throws FileNotFoundException {
		BufferedReader reader = null;

		reader = new BufferedReader(new FileReader(file));

		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");

		try {
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}
		} catch (IOException e) {
			logger.warn("Failure when reading file:"+file.getAbsolutePath());
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				logger.warn("Failure when closing file:"+file.getAbsolutePath());
			}
		}

		return stringBuilder.toString();
	}

	/**
	 * Returns all files in the specified folder which have particular suffix
	 * @param folder path to folder
	 * @param suffix expected suffix of the files
	 * @return list of found files
	 */
	public static LinkedList<File> getAllFilesInFolder(File folder, String suffix) {
		LinkedList<File> list = new LinkedList<File>();
		for (File fileEntry : folder.listFiles()) {
			if (!fileEntry.isDirectory() && (suffix == null || fileEntry.getName().endsWith(suffix))) {
				list.add(fileEntry);
			}
		}
		return list;
	}

	/**
	 * Searches and load scenario file for defined test
	 * @param testName name of the test
	 * @return list of properties
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ResultParsingException
	 */
	public static ArrayList<Properties> loadScenarioFile(String testName) throws FileNotFoundException, IOException, ResultParsingException {
		logger.debug("Searching scenario file at "+Settings.getInstance().getPathToRepository()+ "/teiid-test-artifacts-v6/scenario-deploy-artifacts");
		File f = new File(Settings.getInstance().getPathToRepository() + "/teiid-test-artifacts-v6/scenario-deploy-artifacts");
		if(f.exists()==false){
			throw new ResultParsingException("Path to test repository is invalid: "+f.getAbsolutePath());
		}
		ArrayList<Properties> scenarios = new ArrayList<Properties>();
		for (File dir : f.listFiles()) {
			if (dir.isDirectory()) {
				File scenario = new File(dir.getAbsolutePath(), "/scenarios/" + testName + ".properties");
				if (scenario.exists()) {
					Properties props = new Properties();
					props.load(new FileInputStream(scenario));
					scenarios.add(props);
				}
			}
		}
		return scenarios;
	}
/**
 * Gets path to foler of expected result. It finds the patch using the scenario file
 * @param testName name of the test 
 * @return patch to expected results
 * @throws FileNotFoundException
 * @throws IOException
 * @throws ResultParsingException
 */
	public static File getPathToExpectedResults(String testName) throws FileNotFoundException, IOException, ResultParsingException {
		// TODO Action if more than one scenario is found
		ArrayList<Properties> scenarios = loadScenarioFile(testName);
		if (scenarios.isEmpty()) {
			throw new ResultParsingException("Can't find scenarios file to find expected results");
		}
		String dirName = scenarios.get(0).getProperty("queryset.dir");
		String resultsDirName = scenarios.get(0).getProperty("expected.results.dir");
		if (null == dirName) {
			throw new ResultParsingException("Property queryset.dirname is not in scenarilo file " + testName);
		}
		if (resultsDirName == null) {
			throw new ResultParsingException("Property expected.results.dirname is not in scenarilo file" + testName);
		}
		File result = new File(Settings.getInstance().getPathToRepository() + "/teiid-test-artifacts-v6/ctc-tests/queries/" + dirName + "/" + resultsDirName);
		if (result.exists() == false) {
			throw new ResultParsingException("Path to expected results of test - " + testName + " doesn't exist:" + result.getAbsolutePath());
		}
		return result;
	}
/**
 * Finds expected result file. At first, it guesses the name and path of the file. Using fulltext search if unsuccessful.
 * @param fileName name of the expected result file
 * @param testName name of the test
 * @param pathToExpectedResults path to folder with expected results for this test
 * @return returns the expected result file
 * @throws FileNotFoundException
 * @throws IOException
 * @throws ResultParsingException
 */
	public static File findTestInExepectedResults(String fileName, String testName, File pathToExpectedResults) throws FileNotFoundException, IOException, ResultParsingException {

		int index = fileName.lastIndexOf("_") - 1;
		if (index > 0) {
			String normalizedFileName = fileName.substring(0, fileName.lastIndexOf("_") - 1);
			normalizedFileName=normalizedFileName+ fileName.substring(0, fileName.length() - 3) + "xml";
			logger.debug("Guessing test result filename: "+normalizedFileName);
			File file = new File(pathToExpectedResults, normalizedFileName );
			if (file.exists()) {
				return file;
			}
		}
		logger.debug("Guessing failed - Initiating full text search at: "+pathToExpectedResults.getAbsolutePath());
		return fullTextSearch(pathToExpectedResults,fileName.substring(0, fileName.length()-3)+"xml");
		
	}

	/**
	 * Gets all subfolders of a folder
	 * @param folder patch to folder
	 * @return list of all subfolder and files
	 */
	public static ArrayList<File> getAllSubfolders(File folder) {
		ArrayList<File> dirs = new ArrayList<File>();
		for (File dir : folder.listFiles()) {
			if (dir.isDirectory()) {
				dirs.add(dir);
			}
		}
		return dirs;
	}
	
	/**
	 * Searches a file using full text search
	 * @param path path to folder where the searching starts recursively
	 * @param fileName name of the wanted file
	 * @return The first found file
	 */
	public static File fullTextSearch(File path, String fileName) {
		File fi = null;
		if (fileName.equals(path.getName()) && path.isFile()) {
			return path;
		}
		if (path.isDirectory()) {
			for (File f : path.listFiles()) {
				fi = fullTextSearch(f, fileName);
				if (fi != null) {
					return fi;
				}
			}
		}
		return null;
	}
}
