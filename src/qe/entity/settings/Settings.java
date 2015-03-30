package qe.entity.settings;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * This singleton contains configuration properties of the application
 * 
 * @author felias
 *
 */
public class Settings {
	private static Settings settings = null;
	private String pathToTestResults;
	private String pathToRepository;

	private final String PATH_TO_TEST_RESULTS = "path_to_test_results";
	private final String PATH_TO_SETTINGS = "./settings.properties";
	private final String PATH_TO_TEST_REPOSITORY = "path_to_test_repository";

	protected Settings() {

	}
	/**
	 * Returns the instance of Settings class or it will create a new instance if no one exists 
	 * @return
	 */
	public static Settings getInstance() {
		if (settings == null) {
			settings = new Settings();
		}
		return settings;
	}

	/**
	 * Returns path to test results(the folder of a file Summary_totals.txt)
	 * @return
	 */
	public String getPathToTestResults() {
		return pathToTestResults;
	}

	public void setPathToTestResults(String pathToTestResults) {
		this.pathToTestResults = pathToTestResults;
		saveSettings();
	}
/**
 * Returns the path to test repository(the root of the dataservices repo)
 * @return
 */
	public String getPathToRepository() {
		return pathToRepository;
	}

	public void setPathToRepository(String pathToRepository) {
		this.pathToRepository = pathToRepository;
		saveSettings();
	}

	/**
	 * Saves current settings into a file
	 * @return false if the settings file doesn't exist
	 */
	public boolean saveSettings() {
		Properties props = new Properties();
		if (pathToTestResults != null && pathToTestResults.length() != 0) {
			props.setProperty(PATH_TO_TEST_RESULTS, pathToTestResults);
		} else {
			props.setProperty(PATH_TO_TEST_RESULTS, "");
		}
		if (pathToRepository != null && pathToRepository.length() != 0) {
			props.setProperty(PATH_TO_TEST_REPOSITORY, pathToRepository);
		} else {
			props.setProperty(PATH_TO_TEST_REPOSITORY, "");
		}
		try {
			props.store(new FileOutputStream(PATH_TO_SETTINGS), null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
/**
 * Loads settings form file
 */
	public void loadSettings() {
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(PATH_TO_SETTINGS));
		} catch (FileNotFoundException e) {
			createEmptyConfigurationFile();
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		pathToTestResults = props.getProperty(PATH_TO_TEST_RESULTS);
		pathToRepository = props.getProperty(PATH_TO_TEST_REPOSITORY);
	}

	/**
	 * Creates empty configuration file as a example if no configuration file exists
	 */
	public void createEmptyConfigurationFile() {
		Properties props = new Properties();

		props.setProperty(PATH_TO_TEST_RESULTS, "");

		props.setProperty(PATH_TO_TEST_REPOSITORY, "");
		try {
			props.store(new FileOutputStream(PATH_TO_SETTINGS), null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
