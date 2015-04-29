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
 * <p>
 * How to add new property - create property key in Keys; create get/set methods;
 * </p>
 * 
 * @author felias
 *
 */
public class Settings {
    /**
     * Singleton instance.
     */
	private static Settings settings = null;
	
	/**
	 * Path to properties file.
	 */
	private static final String PATH_TO_SETTINGS = System.getProperty("user.home")
        	        + File.separator + "bqt"
        	        + File.separator + "settings-bqt.properties";

	/**
	 * Settings.
	 */
	private final Properties properties = new Properties();
	
	/**
	 * Keys for properties.
	 * @author jdurani
	 *
	 */
    private enum Keys {
        PATH_TO_TEST_RESULTS("path_to_test_results"),
        PATH_TO_TEST_REPOSITORY("path_to_test_repository"),
        HOST("dv.host.name"),
        PORT("dv.host.port"),
        USERNAME("dv.host.username"),
        PASSWORD("dv.host.password"),
        INCLUDE_SCENARIO("bqt.scenario.include"),
        EXCLUDE_SCENARIO("bqt.scenario.exclude"),
        SCENARIO_DIR("bqt.scenario.dir"),
        OUTPUT("bqt.output.dir"),
        CONFIG("bqt.config"),
        ARTIFACTS_DIR("bqt.artifacts.dir"),
        PRE_1_SUPPORT("bqt.pre.1.support"),
        TEIID_TEST_ARTIFACTS_DIR("dataservices.teiid.test.artifacts.dir"),
        USE_STANDARD_ARTIFACTS_PATH("bqt.use.standard.artifacts.path"),
        JENKINS_USERNAME("jenkins.username"),
        JENKINS_VIEW("jenkins.view"),
        JENKINS_JOB("jenkins.job"),
        JENKINS_DOWNLOAD_DIR("jenkins.download.dir");
        
        private final String key;
        
        private Keys(String key) {
            this.key = key;
        }
        
        @Override
        public String toString() {
            return key;
        }
    }
	
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
	 * Returns path to test results (the folder of a file Summary_totals.txt).
	 * @return
	 */
	public String getPathToTestResults() {
		return getFromProperties(Keys.PATH_TO_TEST_RESULTS);
	}

	/**
	 * Sets path to test results (the folder of a file Summary_file.txt).
	 * 
	 * @param pathToTestResults
	 */
	public void setPathToTestResults(String pathToTestResults) {
	    addToProperties(Keys.PATH_TO_TEST_RESULTS, pathToTestResults);
	}
	
	/**
	 * Returns the path to test repository(the root of the dataservices repo).
	 * @return
	 */
	public String getPathToRepository() {
		return getFromProperties(Keys.PATH_TO_TEST_REPOSITORY);
	}

	/**
	 * Sets path to test repository (dataservices repo).
	 * @param pathToRepository
	 */
	public void setPathToRepository(String pathToRepository) {
	    addToProperties(Keys.PATH_TO_TEST_REPOSITORY, pathToRepository);
	}

	/**
	 * Returns the host address.
	 * 
	 * @return
	 */
	public String getHost() {
		return getFromProperties(Keys.HOST);
	}
	
	/**
	 * Sets the host address.
	 * 
	 * @param host
	 */
	public void setHost(String host) {
	    addToProperties(Keys.HOST, host);
	}

    /**
     * Returns the port number.
     * 
     * @return
     */
	public String getPort() {
	    return getFromProperties(Keys.PORT);
	}

    /**
     * Sets the port number.
     * 
     * @param port
     */
	public void setPort(String port) {
	    addToProperties(Keys.PORT, port);
	}

    /**
     * Returns the username.
     * 
     * @return
     */
	public String getUsername() {
	    return getFromProperties(Keys.USERNAME);
	}

    /**
     * Sets the username.
     * 
     * @param username
     */
	public void setUsername(String username) {
	    addToProperties(Keys.USERNAME, username);
	}

    /**
     * Returns the password.
     * 
     * @return
     */
	public String getPassword() {
	    return getFromProperties(Keys.PASSWORD);
	}

    /**
     * Sets the password.
     * 
     * @param password
     */
	public void setPassword(String password) {
	    addToProperties(Keys.PASSWORD, password);
	}

    /**
     * Returns the scenario-include property.
     * 
     * @return
     */
	public String getScenarioInclude() {
	    return getFromProperties(Keys.INCLUDE_SCENARIO);
	}

    /**
     * Sets the scenario-include property.
     * 
     * @param scenarioInclude
     */
	public void setScenarioInclude(String scenarioInclude) {
	    addToProperties(Keys.INCLUDE_SCENARIO, scenarioInclude);
	}

    /**
     * Returns the scenario-exclude property.
     * 
     * @return
     */
	public String getScenarioExclude() {
	    return getFromProperties(Keys.EXCLUDE_SCENARIO);
	}

    /**
     * Sets the scenario-exclude property.
     * 
     * @param scenarioExclude
     */
	public void setScenarioExclude(String scenarioExclude) {
	    addToProperties(Keys.EXCLUDE_SCENARIO, scenarioExclude);
	}

    /**
     * Returns the output directory.
     * 
     * @return
     */
	public String getOutputDir() {
	    return getFromProperties(Keys.OUTPUT);
	}

    /**
     * Sets the output directory.
     * 
     * @param outputDir
     */
	public void setOutputDir(String outputDir) {
	    addToProperties(Keys.OUTPUT, outputDir);
	}

    /**
     * Returns path to the scenario.
     * 
     * @return
     */
	public String getScenarioPath() {
	    return getFromProperties(Keys.SCENARIO_DIR);
	}

    /**
     * Sets path to the scenario.
     * 
     * @param scenarioPath
     */
	public void setScenarioPath(String scenarioPath) {
	    addToProperties(Keys.SCENARIO_DIR, scenarioPath);
	}

    /**
     * Returns path to the config file.
     * 
     * @return
     */
	public String getConfig() {
	    return getFromProperties(Keys.CONFIG);
	}

    /**
     * Sets path to the config file.
     * @param config
     */
	public void setConfig(String config) {
	    addToProperties(Keys.CONFIG, config);
	}
	

    /**
     * Returns path to artifacts directory.
     * 
     * @return
     */
	public String getArtifactsDir() {
	    return getFromProperties(Keys.ARTIFACTS_DIR);
	}

    /**
     * Sets path to artifacts directory.
     * 
     * @param artifactsDir
     */
	public void setArtifactsDir(String artifactsDir) {
	    addToProperties(Keys.ARTIFACTS_DIR, artifactsDir);
	}

    /**
     * Returns value of the pre-1-support property.
     * 
     * @return
     */
	public String getPre1Support() {
	    return getFromProperties(Keys.PRE_1_SUPPORT);
	}

    /**
     * Sets value of the pre-1-support property.
     * 
     * @param pre1Support
     */
	public void setPre1Support(String pre1Support) {
	    addToProperties(Keys.PRE_1_SUPPORT, pre1Support);
	}

    /**
     * Returns the name of teiid-test-artifacts directory.
     * 
     * @return
     */
	public String getTeiidTestArtifactsDir() {
	    return getFromProperties(Keys.TEIID_TEST_ARTIFACTS_DIR);
    }

    /**
     * Sets the name of teiid-test-artifacts property.
     * 
     * @param teiidTestArtifacts
     */
	public void setTeiidTestArtifactsDir(String teiidTestArtifacts) {
	    addToProperties(Keys.TEIID_TEST_ARTIFACTS_DIR, teiidTestArtifacts);
    }

    /**
     * Returns value of use-standard-artifacts-path property.
     * 
     * @return
     */
	public String getUseStandardArtifactsPath() {
	    return getFromProperties(Keys.USE_STANDARD_ARTIFACTS_PATH);
    }

    /**
     * Sets value of use-standard-artifacts-path property.
     * 
     * @param useStandardArtifactsPath
     */
	public void setUseStandardArtifactsPath(String useStandardArtifactsPath) {
	    addToProperties(Keys.USE_STANDARD_ARTIFACTS_PATH, useStandardArtifactsPath);
    }
	
	/**
	 * Returns user name for jenkins.
	 * 
	 * @return
	 */
    public String getJenkinsUsername() {
        return getFromProperties(Keys.JENKINS_USERNAME);
    }

    /**
     * Sets user name for jenkins.
     * @param jenkinsUsername
     */
    public void setJenkinsUsername(String jenkinsUsername) {
        addToProperties(Keys.JENKINS_USERNAME, jenkinsUsername);
    }

    /**
     * Returns jenkins view.
     * 
     * @return
     */
    public String getJenkinsView() {
        return getFromProperties(Keys.JENKINS_VIEW);
    }

    /**
     * Sets jenkins view.
     * 
     * @param jenkinsView
     */
    public void setJenkinsView(String jenkinsView) {
        addToProperties(Keys.JENKINS_VIEW, jenkinsView);
    }
    
    /**
     * Returns jekins job.
     * 
     * @return
     */
    public String getJenkinsJob() {
        return getFromProperties(Keys.JENKINS_JOB);
    }
    
    /**
     * Sets jenkins job.
     * 
     * @param jenkinsJob
     */
    public void setJenkinsJob(String jenkinsJob) {
        addToProperties(Keys.JENKINS_JOB, jenkinsJob);
    }

    /**
     * Returns jekins download directory.
     * 
     * @return
     */
    public String getJenkinsDownloadDir() {
        return getFromProperties(Keys.JENKINS_DOWNLOAD_DIR);
    }

    /**
     * Sets jenkins download directory.
     * 
     * @param jenkinsDownloadDir
     */
    public void setJenkinsDownloadDir(String jenkinsDownloadDir) {
        addToProperties(Keys.JENKINS_DOWNLOAD_DIR, jenkinsDownloadDir);
    }

    /**
	 * Saves current settings into a file.
	 * 
	 * @return false if the settings file doesn't exist
	 */
	public boolean saveSettings() {
		try {
			properties.store(new FileOutputStream(PATH_TO_SETTINGS), null);
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
	 * @param key
	 * @param value
	 */
	private void addToProperties(Keys key, String value){
	    properties.setProperty(key.key, value == null ? "" : value);
	    saveSettings();
	}
	
	/**
	 * Returns property by key.
	 * 
	 * @param key
	 * @return
	 */
	private String getFromProperties(Keys key){
	    return properties.getProperty(key.key, "");
	}
	
	/**
	 * Loads settings form file.
	 */
	public void loadSettings() {
	    properties.clear();
		try {
			properties.load(new FileInputStream(PATH_TO_SETTINGS));
		} catch (FileNotFoundException e) {
			createEmptyConfigurationFile();
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates empty configuration file as a example if no configuration file exists.
	 */
	public void createEmptyConfigurationFile() {
	    properties.clear();
	    for(Keys key : Keys.values()){
	        properties.setProperty(key.key, "");
	    }
		try {
			properties.store(new FileOutputStream(PATH_TO_SETTINGS), null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
