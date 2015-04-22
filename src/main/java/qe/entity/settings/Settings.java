package qe.entity.settings;

import java.io.File;
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
    /**
     * Singleton instance.
     */
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
	private String artifactsDir;
	private String pre1Support;
	private String teiidTestArtifactsDir;
	private String useStandardArtifactsPath;
	
	/**
	 * Path to properties file.
	 */
	private static final String PATH_TO_SETTINGS = System.getProperty("user.home") + File.separator + "bqt" + File.separator + "settings-bqt.properties";

	// property keys
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
	private static final String PROP_ARTIFACTS_DIR = "bqt.artifacts.dir";
	private static final String PROP_PRE_1_SUPPORT = "bqt.pre.1.support";
	private static final String PROP_TEIID_TEST_ARTIFACTS_DIR = "bqt.teiid.test.artifacts.dir";
	private static final String PROP_USE_STANDARD_ARTIFACTS_PATH = "bqt.use.standard.artifacts.path";

	protected Settings(){}
	
	/**
	 * Returns the instance of Settings class or it will create a new instance if no one exists.
	 * @return
	 */
	public static Settings getInstance() {
		if (settings == null) {
			settings = new Settings();
			File dir = new File(PATH_TO_SETTINGS).getParentFile();
			if(!dir.exists()){
				dir.mkdirs();
			}
		}
		return settings;
	}

	/**
	 * Returns path to test results(the folder of a file Summary_totals.txt).
	 * @return
	 */
	public String getPathToTestResults() {
		return pathToTestResults;
	}

	/**
	 * Sets path to test results (the folder of a file Summary_file.txt).
	 * 
	 * @param pathToTestResults
	 */
	public void setPathToTestResults(String pathToTestResults) {
		this.pathToTestResults = pathToTestResults;
		saveSettings();
	}
	
	/**
	 * Returns the path to test repository(the root of the dataservices repo).
	 * @return
	 */
	public String getPathToRepository() {
		return pathToRepository;
	}

	/**
	 * Sets path to test repository (dataservices repo).
	 * @param pathToRepository
	 */
	public void setPathToRepository(String pathToRepository) {
		this.pathToRepository = pathToRepository;
		saveSettings();
	}

	/**
	 * Returns the host address.
	 * 
	 * @return
	 */
	public String getHost() {
		return host;
	}
	
	/**
	 * Sets the host address.
	 * 
	 * @param host
	 */
	public void setHost(String host) {
		this.host = host;
		saveSettings();
	}

    /**
     * Returns the port number.
     * 
     * @return
     */
	public String getPort() {
		return port;
	}

    /**
     * Sets the port number.
     * 
     * @param port
     */
	public void setPort(String port) {
		this.port = port;
		saveSettings();
	}

    /**
     * Returns the username.
     * 
     * @return
     */
	public String getUsername() {
		return username;
	}

    /**
     * Sets the username.
     * 
     * @param username
     */
	public void setUsername(String username) {
		this.username = username;
		saveSettings();
	}

    /**
     * Returns the password.
     * 
     * @return
     */
	public String getPassword() {
		return password;
	}

    /**
     * Sets the password.
     * 
     * @param password
     */
	public void setPassword(String password) {
		this.password = password;
		saveSettings();
	}

    /**
     * Returns the scenario-include property.
     * 
     * @return
     */
	public String getScenarioInclude() {
		return scenarioInclude;
	}

    /**
     * Sets the scenario-include property.
     * 
     * @param scenarioInclude
     */
	public void setScenarioInclude(String scenarioInclude) {
		this.scenarioInclude = scenarioInclude;
		saveSettings();
	}

    /**
     * Returns the scenario-exclude property.
     * 
     * @return
     */
	public String getScenarioExclude() {
		return scenarioExclude;
	}

    /**
     * Sets the scenario-exclude property.
     * 
     * @param scenarioExclude
     */
	public void setScenarioExclude(String scenarioExclude) {
		this.scenarioExclude = scenarioExclude;
		saveSettings();
	}

    /**
     * Returns the output directory.
     * 
     * @return
     */
	public String getOutputDir() {
		return outputDir;
	}

    /**
     * Sets the output directory.
     * 
     * @param outputDir
     */
	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
		saveSettings();
	}

    /**
     * Returns path to the scenario.
     * 
     * @return
     */
	public String getScenarioPath() {
		return scenarioPath;
	}

    /**
     * Sets path to the scenario.
     * 
     * @param scenarioPath
     */
	public void setScenarioPath(String scenarioPath) {
		this.scenarioPath = scenarioPath;
		saveSettings();
	}

    /**
     * Returns path to the config file.
     * 
     * @return
     */
	public String getConfig() {
		return config;
	}

    /**
     * Sets path to the config file.
     * @param config
     */
	public void setConfig(String config) {
		this.config = config;
		saveSettings();
	}
	

    /**
     * Returns path to artifacts directory.
     * 
     * @return
     */
	public String getArtifactsDir() {
		return artifactsDir;
	}

    /**
     * Sets path to artifacts directory.
     * 
     * @param artifactsDir
     */
	public void setArtifactsDir(String artifactsDir) {
		this.artifactsDir = artifactsDir;
		saveSettings();
	}

    /**
     * Returns value of the pre-1-support property.
     * 
     * @return
     */
	public String getPre1Support() {
		return pre1Support;
	}

    /**
     * Sets value of the pre-1-support property.
     * 
     * @param pre1Support
     */
	public void setPre1Support(String pre1Support) {
	    this.pre1Support = pre1Support;
	    saveSettings();
	}

    /**
     * Returns the name of teiid-test-artifacts directory.
     * 
     * @return
     */
	public String getTeiidTestArtifactsDir() {
        return teiidTestArtifactsDir;
    }

    /**
     * Sets the name of teiid-test-artifacts property.
     * 
     * @param teiidTestArtifacts
     */
	public void setTeiidTestArtifactsDir(String teiidTestArtifacts) {
        this.teiidTestArtifactsDir = teiidTestArtifacts;
        saveSettings();
    }

    /**
     * Returns value of use-standard-artifacts-path property.
     * 
     * @return
     */
	public String getUseStandardArtifactsPath() {
        return useStandardArtifactsPath;
    }

    /**
     * Sets value of use-standard-artifacts-path property.
     * 
     * @param useStandardArtifactsPath
     */
	public void setUseStandardArtifactsPath(String useStandardArtifactsPath) {
        this.useStandardArtifactsPath = useStandardArtifactsPath;
        saveSettings();
    }
	
    /**
	 * Saves current settings into a file.
	 * @return false if the settings file doesn't exist
	 */
	public boolean saveSettings() {
		Properties props = new Properties();
		addToProperties(props, PROP_PATH_TO_TEST_RESULTS, pathToTestResults);
		addToProperties(props, PROP_PATH_TO_TEST_REPOSITORY, pathToRepository);
		addToProperties(props, PROP_ARTIFACTS_DIR, artifactsDir);
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
		addToProperties(props, PROP_TEIID_TEST_ARTIFACTS_DIR, teiidTestArtifactsDir);
		addToProperties(props, PROP_USE_STANDARD_ARTIFACTS_PATH, useStandardArtifactsPath);
		
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
	 * Adds property key-value to properties.
	 * 
	 * @param props
	 * @param key
	 * @param value
	 */
	private void addToProperties(Properties props, String key, String value){
		if (value != null && !value.isEmpty()) {
			props.setProperty(key, value);
		} else {
			props.setProperty(key, "");
		}
	}
	/**
	 * Loads settings form file.
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
		artifactsDir = props.getProperty(PROP_ARTIFACTS_DIR);
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
		teiidTestArtifactsDir = props.getProperty(PROP_TEIID_TEST_ARTIFACTS_DIR);
		useStandardArtifactsPath = props.getProperty(PROP_USE_STANDARD_ARTIFACTS_PATH);
	}

	/**
	 * Creates empty configuration file as a example if no configuration file exists.
	 */
	public void createEmptyConfigurationFile() {
		Properties props = new Properties();

		props.setProperty(PROP_PATH_TO_TEST_RESULTS, "");

		props.setProperty(PROP_PATH_TO_TEST_REPOSITORY, "");
		
		props.setProperty(PROP_ARTIFACTS_DIR, "");
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
		props.setProperty(PROP_TEIID_TEST_ARTIFACTS_DIR, "");
		props.setProperty(PROP_USE_STANDARD_ARTIFACTS_PATH, "");
		try {
			props.store(new FileOutputStream(PATH_TO_SETTINGS), null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
