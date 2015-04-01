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
	private String host;
	private String port;
	private String username;
	private String password;
	private String scenarioInclude;
	private String scenarioExclude;
	private String outputDir;
	private String scenarioPath;
	private String config;
	private String artefactsDir;
	private String pre1Support;
	
	private static final String PATH_TO_SETTINGS = System.getProperty("user.home") + "/settings-bqt.properties";

	private static final String PROP_PATH_TO_TEST_RESULTS = "path_to_test_results";
	private static final String PROP_PATH_TO_TEST_REPOSITORY = "path_to_test_repository";
	private static final String PROP_HOST = "dv.host.name";
	private static final String PROP_PORT = "dv.host.port";
	private static final String PROP_USERNAME = "dv.host.username";
	private static final String PROP_PASSWORD = "dv.host.password";
	private static final String PROP_INCLUDE_SCENARIO = "bqt.scenario.include";
	private static final String PROP_EXCLUDE_SCENARIO = "bqt.scenario.exclude";
	private static final String PROP_SCENARIO_DIR = "bqt.scenario.dir";
	private static final String PROP_OUTPUT = "bqt.output.dir";
	private static final String PROP_CONFIG = "bqt.config";
	private static final String PROP_ARTEFACTS_DIR = "bqt.artefacts.dir";
	private static final String PROP_PRE_1_SUPPORT = "bqt.pre.1.support";

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

	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
		saveSettings();
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
		saveSettings();
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
		saveSettings();
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
		saveSettings();
	}
	public String getScenarioInclude() {
		return scenarioInclude;
	}
	public void setScenarioInclude(String scenarioInclude) {
		this.scenarioInclude = scenarioInclude;
		saveSettings();
	}
	public String getScenarioExclude() {
		return scenarioExclude;
	}
	public void setScenarioExclude(String scenarioExclude) {
		this.scenarioExclude = scenarioExclude;
		saveSettings();
	}
	public String getOutputDir() {
		return outputDir;
	}
	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
		saveSettings();
	}
	public String getScenarioPath() {
		return scenarioPath;
	}
	public void setScenarioPath(String scenarioPath) {
		this.scenarioPath = scenarioPath;
		saveSettings();
	}
	public String getConfig() {
		return config;
	}
	public void setConfig(String config) {
		this.config = config;
		saveSettings();
	}
	public String getArtefactsDir() {
		return artefactsDir;
	}
	public void setArtefactsDir(String artefactsDir) {
		this.artefactsDir = artefactsDir;
		saveSettings();
	}
	
	public String getPre1Support() {
		return pre1Support;
	}
	public void setPre1Support(String pre1Support) {
		this.pre1Support = pre1Support;
		saveSettings();
	}
	/**
	 * Saves current settings into a file
	 * @return false if the settings file doesn't exist
	 */
	public boolean saveSettings() {
		Properties props = new Properties();
		addToProperties(props, PROP_PATH_TO_TEST_RESULTS, pathToTestResults);
		addToProperties(props, PROP_PATH_TO_TEST_REPOSITORY, pathToRepository);
		addToProperties(props, PROP_ARTEFACTS_DIR, artefactsDir);
		addToProperties(props, PROP_CONFIG, config);
		addToProperties(props, PROP_EXCLUDE_SCENARIO, scenarioExclude);
		addToProperties(props, PROP_INCLUDE_SCENARIO, scenarioInclude);
		addToProperties(props, PROP_HOST, host);
		addToProperties(props, PROP_PORT, port);
		addToProperties(props, PROP_USERNAME, username);
		addToProperties(props, PROP_PASSWORD, password);
		addToProperties(props, PROP_SCENARIO_DIR, scenarioPath);
		addToProperties(props, PROP_OUTPUT, outputDir);
		addToProperties(props, PROP_PRE_1_SUPPORT, pre1Support);
		
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
	
	private void addToProperties(Properties props, String key, String value){
		if (value != null && !value.isEmpty()) {
			props.setProperty(key, value);
		} else {
			props.setProperty(key, "");
		}
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
		pathToTestResults = props.getProperty(PROP_PATH_TO_TEST_RESULTS);
		pathToRepository = props.getProperty(PROP_PATH_TO_TEST_REPOSITORY);
		artefactsDir = props.getProperty(PROP_ARTEFACTS_DIR);
		config = props.getProperty(PROP_CONFIG);
		scenarioExclude = props.getProperty(PROP_EXCLUDE_SCENARIO);
		scenarioInclude = props.getProperty(PROP_INCLUDE_SCENARIO);
		host = props.getProperty(PROP_HOST);
		port = props.getProperty(PROP_PORT);
		username = props.getProperty(PROP_USERNAME);
		password = props.getProperty(PROP_PASSWORD);
		scenarioPath = props.getProperty(PROP_SCENARIO_DIR);
		outputDir = props.getProperty(PROP_OUTPUT);
		pre1Support = props.getProperty(PROP_PRE_1_SUPPORT);
	}

	/**
	 * Creates empty configuration file as a example if no configuration file exists
	 */
	public void createEmptyConfigurationFile() {
		Properties props = new Properties();

		props.setProperty(PROP_PATH_TO_TEST_RESULTS, "");

		props.setProperty(PROP_PATH_TO_TEST_REPOSITORY, "");
		
		props.setProperty(PROP_ARTEFACTS_DIR, "");
		props.setProperty(PROP_CONFIG, "");
		props.setProperty(PROP_EXCLUDE_SCENARIO, "");
		props.setProperty(PROP_INCLUDE_SCENARIO, "");
		props.setProperty(PROP_HOST, "");
		props.setProperty(PROP_PORT, "");
		props.setProperty(PROP_USERNAME, "");
		props.setProperty(PROP_PASSWORD, "");
		props.setProperty(PROP_SCENARIO_DIR, "");
		props.setProperty(PROP_OUTPUT, "");
		props.setProperty(PROP_PRE_1_SUPPORT, "");
		try {
			props.store(new FileOutputStream(PATH_TO_SETTINGS), null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
