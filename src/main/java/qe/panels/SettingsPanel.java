package qe.panels;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qe.entity.settings.Settings;
import qe.utils.Utils;

public class SettingsPanel extends JPanel{

    private static final String TEIID_TEST_ARTIFACTS = "teiid-test-artifacts";
    private static final String TEIID_TEST_ARTIFACTS_V6 = "teiid-test-artifacts-v6";

    private static final long serialVersionUID = -1288130454294139419L;

	private static final Logger LOGGER = LoggerFactory.getLogger(SettingsPanel.class);
	
	private final SaveToSettingsFileAction savePathToResultsAction = new SavePathToResults();
	private final SaveToSettingsFileAction savePathToResitoryAction = new SavePathToRepository();
	private final SaveToSettingsFileAction saveUserNameAction = new SaveUser();
	private final SaveToSettingsFileAction savePasswordAction = new SavePassword();
	private final SaveToSettingsFileAction savePortAction = new SavePort();
	private final SaveToSettingsFileAction saveHostAction = new SaveHost();
	private final SaveToSettingsFileAction saveScenarioPathAction = new SaveScenariosPath();
	private final SaveToSettingsFileAction saveOutputDirAction = new SaveOutputDir();
	private final SaveToSettingsFileAction saveConfigAction = new SaveConfig();
	private final SaveToSettingsFileAction saveArtifactsDirAction = new SaveArtifactsDir();
	private final SaveToSettingsFileAction saveIncludeAction = new SaveInclude();
	private final SaveToSettingsFileAction saveExcludeAction = new SaveExclude();
	private final SaveToSettingsFileAction savePre1SupportAction = new SavePre1Support();
	private final SaveToSettingsFileAction saveTeiidV6ArtifactsSelectedAction = new SaveTeiidTestArtifactsSelection(TEIID_TEST_ARTIFACTS_V6);
	private final SaveToSettingsFileAction saveTeiidArtifactsSelectedAction = new SaveTeiidTestArtifactsSelection(TEIID_TEST_ARTIFACTS);
	private final SaveToSettingsFileAction saveUseStandardArtifactsPathAction = new SaveUseStandardArtifactsPath();
	
	private JTextField summaryTotalsDir;
	private JLabel summaryTotalsDirLabel;
	private JButton summaryTotalsDirBrowseButton;
	private JTextField repositorySettings;
	private JLabel repositorySettingsLabel;
	private JButton repositorySettingsBrowseButton;
	
	private JRadioButton teiidV6Artifacts;
	private JRadioButton teiidArtifacts;
	
	private JTextField userName;
	private JLabel userNameLabel;
	private JTextField password;
	private JLabel passwordLabel;
	private JTextField host;
	private JLabel hostLabel;
	private JTextField port;
	private JLabel portLabel;
	
	private JTextField scenarios;
	private JLabel scenariosLabel;
	private JButton scenariosBrowseButton;
	private JTextField outputDir;
	private JLabel outputDirLabel;
	private JButton outputDirBrowseButton;
	private JTextField config;
	private JLabel configLabel;
	private JButton configBrowseButton;

	private JCheckBox useStandardArtifactsPath;
	private JTextField artifactsDir;
	private JLabel artifactsDirLabel;
	private JButton artifactsDirBrowseButton;
	
	private JTextField include;
	private JLabel includeLabel;
	private JTextField exclude;
	private JLabel excludeLabel;

	private JCheckBox pre1Supported;
	
	public SettingsPanel(){
		super();
		init();
	}
	
	private void init(){
		initConnectionProperties();
		initBqtConfig();
		initIncludeExclude();
		initPre1Support();
		initSummaryTotalsDir();
		initRepositorySettings();
		initArtifactsVersion();
		initStandardArtifactsPath();
		initDefaultValues();
		
		GroupLayout gl = new GroupLayout(this);
		gl.setAutoCreateContainerGaps(true);
		gl.setAutoCreateGaps(true);
		gl.setHorizontalGroup(gl.createParallelGroup()
			.addGroup(gl.createParallelGroup()
				.addGroup(gl.createSequentialGroup()
					.addGroup(gl.createParallelGroup()
						.addComponent(userNameLabel)
						.addComponent(userName, 200, 200, 800))
					.addGroup(gl.createParallelGroup()
						.addComponent(passwordLabel)
						.addComponent(password)))
				.addGroup(gl.createSequentialGroup()
					.addGroup(gl.createParallelGroup()
						.addComponent(hostLabel)
						.addComponent(host))
					.addGroup(gl.createParallelGroup()
						.addComponent(portLabel)
						.addComponent(port))))
			.addGroup(gl.createParallelGroup()
				.addComponent(repositorySettingsLabel)
				.addGroup(gl.createSequentialGroup()
					.addComponent(repositorySettings)
					.addComponent(repositorySettingsBrowseButton)))
			.addGroup(gl.createParallelGroup()
		        .addComponent(teiidV6Artifacts)
		        .addComponent(teiidArtifacts))
			.addGroup(gl.createParallelGroup()
				.addComponent(summaryTotalsDirLabel)
				.addGroup(gl.createSequentialGroup()
					.addComponent(summaryTotalsDir)
					.addComponent(summaryTotalsDirBrowseButton)))
			.addGroup(gl.createParallelGroup()
				.addGroup(gl.createParallelGroup()
					.addComponent(scenariosLabel)
					.addGroup(gl.createSequentialGroup()
						.addComponent(scenarios)
						.addComponent(scenariosBrowseButton)))
				.addGroup(gl.createParallelGroup()
					.addComponent(outputDirLabel)
					.addGroup(gl.createSequentialGroup()
						.addComponent(outputDir)
						.addComponent(outputDirBrowseButton)))
				.addGroup(gl.createParallelGroup()
					.addComponent(configLabel)
					.addGroup(gl.createSequentialGroup()
						.addComponent(config)
						.addComponent(configBrowseButton))))
			.addComponent(useStandardArtifactsPath)
			.addGroup(gl.createParallelGroup()
				.addComponent(artifactsDirLabel)
				.addGroup(gl.createSequentialGroup()
					.addComponent(artifactsDir)
					.addComponent(artifactsDirBrowseButton)))
			.addGroup(gl.createParallelGroup()
				.addComponent(includeLabel)
				.addComponent(include)
				.addComponent(excludeLabel)
				.addComponent(exclude))
			.addComponent(pre1Supported));
		
		int fieldHeight = 25;
		int groupsGap = 25;
		gl.setVerticalGroup(gl.createSequentialGroup()
			.addGroup(gl.createParallelGroup()
				.addGroup(gl.createSequentialGroup()
					.addComponent(userNameLabel)
					.addComponent(userName, fieldHeight, fieldHeight, fieldHeight)
					.addComponent(hostLabel)
					.addComponent(host))
				.addGroup(gl.createSequentialGroup()
					.addComponent(passwordLabel)
					.addComponent(password)
					.addComponent(portLabel)
					.addComponent(port)))
			.addGroup(gl.createSequentialGroup()
				.addGap(groupsGap)
				.addComponent(repositorySettingsLabel)
				.addGroup(gl.createParallelGroup()
					.addComponent(repositorySettings)
					.addComponent(repositorySettingsBrowseButton))
				.addComponent(teiidV6Artifacts)
				.addComponent(teiidArtifacts)
                .addGap(groupsGap)
				.addComponent(summaryTotalsDirLabel)
				.addGroup(gl.createParallelGroup()
					.addComponent(summaryTotalsDir)
					.addComponent(summaryTotalsDirBrowseButton))
				.addGap(groupsGap)				
				.addComponent(scenariosLabel)
				.addGroup(gl.createParallelGroup()
					.addComponent(scenarios)
					.addComponent(scenariosBrowseButton))
				.addComponent(outputDirLabel)
				.addGroup(gl.createParallelGroup()
					.addComponent(outputDir)
					.addComponent(outputDirBrowseButton))
				.addComponent(configLabel)
				.addGroup(gl.createParallelGroup()
					.addComponent(config)
					.addComponent(configBrowseButton)))
            .addGap(groupsGap)
            .addComponent(useStandardArtifactsPath)
			.addComponent(artifactsDirLabel)
			.addGroup(gl.createParallelGroup()
				.addComponent(artifactsDir)
				.addComponent(artifactsDirBrowseButton))
			.addGap(groupsGap)
			.addGroup(gl.createSequentialGroup()
				.addComponent(includeLabel)
				.addComponent(include)
				.addComponent(excludeLabel)
				.addComponent(exclude))
			.addGap(groupsGap)
			.addComponent(pre1Supported));
		
		gl.linkSize(host, port, userName, password);
		gl.linkSize(SwingConstants.VERTICAL, scenarios, outputDir, config, artifactsDir, include, exclude, summaryTotalsDir, repositorySettings);
		setLayout(gl);
	}
	
	private void initDefaultValues(){
		Settings settings = Settings.getInstance();
		summaryTotalsDir.setText(settings.getPathToTestResults());
		repositorySettings.setText(settings.getPathToRepository());
		host.setText(settings.getHost());
		port.setText(settings.getPort());
		userName.setText(settings.getUsername());
		password.setText(settings.getPassword());
		include.setText(settings.getScenarioInclude());
		exclude.setText(settings.getScenarioExclude());
		scenarios.setText(settings.getScenarioPath());
		outputDir.setText(settings.getOutputDir());
		config.setText(settings.getConfig());
		artifactsDir.setText(settings.getArtifactsDir());
		pre1Supported.setSelected(Boolean.parseBoolean(settings.getPre1Support()));
		String tta = settings.getTeiidTestArtifactsDir();
		if(TEIID_TEST_ARTIFACTS_V6.equals(tta)){
		    teiidV6Artifacts.setSelected(true);
		} else if(TEIID_TEST_ARTIFACTS.equals(tta)){
		    teiidArtifacts.setSelected(true);
		}
		if(settings.getUseStandardArtifactsPath() != null){
		    useStandardArtifactsPath.setSelected(Boolean.parseBoolean(settings.getUseStandardArtifactsPath()));
		}
		boolean enabled = !useStandardArtifactsPath.isSelected(); 
        artifactsDir.setEnabled(enabled);
        artifactsDirBrowseButton.setEnabled(enabled);
        artifactsDirLabel.setEnabled(enabled);
	}
	
	private void initArtifactsVersion(){
	    teiidV6Artifacts = getRadioButton(TEIID_TEST_ARTIFACTS_V6, false, saveTeiidV6ArtifactsSelectedAction);
	    teiidArtifacts = getRadioButton(TEIID_TEST_ARTIFACTS, false,  saveTeiidArtifactsSelectedAction);
	    
	    ButtonGroup bg = new ButtonGroup();
	    bg.add(teiidArtifacts);
	    bg.add(teiidV6Artifacts);
	}
	
	private void initSummaryTotalsDir(){
		// Label Path to test results:
		summaryTotalsDirLabel = new JLabel("Path to test results:");
		summaryTotalsDirLabel.setHorizontalAlignment(SwingConstants.LEFT);
		summaryTotalsDir = getTextFiled(savePathToResultsAction);
		Utils.setToolTipText(summaryTotalsDir, "The folder with Summary_totals.txt");
		summaryTotalsDir.setColumns(10);
		
		summaryTotalsDirBrowseButton = getBrowseButton(JFileChooser.DIRECTORIES_ONLY, summaryTotalsDir, savePathToResultsAction);
	}
	
	private void initRepositorySettings(){
		repositorySettingsLabel = new JLabel("Path to test repository:");
		repositorySettingsLabel.setHorizontalAlignment(SwingConstants.LEFT);
		
		repositorySettings = getTextFiled(savePathToResitoryAction);
		repositorySettings.setColumns(10);

		repositorySettingsBrowseButton = getBrowseButton(JFileChooser.DIRECTORIES_ONLY, repositorySettings, savePathToResitoryAction);
	}
	
	/**
	 * Initializes include/exclude related part of this panel; 
	 */
	private void initIncludeExclude(){
		include = getTextFiled(saveIncludeAction);
		exclude = getTextFiled(saveExcludeAction);
		
		Utils.setToolTipText(include, "Include scenario pattern. Same as \"bqt.scenario.include\" property.");
		Utils.setToolTipText(exclude, "Exclude scenario pattern. Same as \"bqt.scenario.exclude\" property.");
		
		includeLabel = new JLabel("Include scenarios");
		excludeLabel = new JLabel("Exclude scenarios");
	}
	
	
	/**
	 * Initializes pre-1-support check box. 
	 */
	private void initPre1Support(){
		pre1Supported = new JCheckBox("Support old names");
		pre1Supported.setSelected(true);
		pre1Supported.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				savePre1SupportAction.save();
			}
		});
		Utils.setToolTipText(pre1Supported, "If old names of BQT properties are supported. Same as \"support.pre1.0.scenario\" property.");
	}
	
	private void initStandardArtifactsPath(){
	    artifactsDir = getTextFiled(saveArtifactsDirAction);
	    artifactsDirLabel = new JLabel("Artifacts directory");
	    artifactsDirBrowseButton = getBrowseButton(JFileChooser.DIRECTORIES_ONLY, artifactsDir, saveArtifactsDirAction);
	    Utils.setToolTipText(artifactsDir, "Path to queries and expected results. "
	            + "Usually <dataservices-path>/<test-artifacts-dir>/ctc-tests/queries. Same as \"queryset.artifacts.dir\" property.");
        
	    useStandardArtifactsPath = new JCheckBox("Use standard path to artifacts.", true);
	    Utils.setToolTipText(useStandardArtifactsPath, "If true, artifacts in dataservices repo will be used ("
	            + "<dataservices-path>/<test-artifacts-dir>/ctc-tests/queries).");
	    useStandardArtifactsPath.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                saveUseStandardArtifactsPathAction.save();
                boolean enabled = !useStandardArtifactsPath.isSelected(); 
                artifactsDir.setEnabled(enabled);
                artifactsDirBrowseButton.setEnabled(enabled);
                artifactsDirLabel.setEnabled(enabled);
            }
        });
	}
	
	/**
	 * Initializes bqt-config related part of this panel. 
	 */
	private void initBqtConfig(){
		scenarios = getTextFiled(saveScenarioPathAction);
		scenariosLabel = new JLabel("Scenario (directory or file)");
		scenariosBrowseButton = getBrowseButton(JFileChooser.FILES_AND_DIRECTORIES, scenarios, saveScenarioPathAction);
		
		outputDir = getTextFiled(saveOutputDirAction);
		outputDirLabel = new JLabel("Output directory");
		outputDirBrowseButton = getBrowseButton(JFileChooser.DIRECTORIES_ONLY, outputDir, saveOutputDirAction);
		
		config = getTextFiled(saveConfigAction);
		configLabel = new JLabel("Configuration file");
		configBrowseButton = getBrowseButton(JFileChooser.FILES_ONLY, config, saveConfigAction);
		
		
		Utils.setToolTipText(scenarios, "Path to scenario file. It could be a single file or a directory. Same as \"scenario.file\" property.");
		Utils.setToolTipText(outputDir, "Path to output directory. Same as \"output.dir\" property.");
		Utils.setToolTipText(config, "Path to default config file. Usually <bqt-distro-path>/config/test.properties. Same as \"config\" property.");
	}
	
	/**
	 * Returns a new button with text {@code Browse} which will show {@link JFileChooser}.
	 * Path of selected file will be set as text to {@code textFiel}.
	 * 
	 * @param selectionMode selection mode for {@link JFileChooser}
	 * @param textField text field, where the path of selected file will be shown
	 * @return browse button
	 * 
	 * @see {@link JFileChooser#setFileSelectionMode(int)}
	 * 
	 */
	public static JButton getBrowseButton(int selectionMode, JTextField textField, SaveToSettingsFileAction saveAction){
		JButton button = new JButton("Browse");
		button.addActionListener(new BrowseActionListener(textField, selectionMode, saveAction));
		return button;
	}
	
	private JRadioButton getRadioButton(String text, boolean isSelected, final SaveToSettingsFileAction sa){
	    JRadioButton b = new JRadioButton(text, isSelected);
	    b.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                sa.save();
            }
        });
	    return b;
	}
	
	/**
	 * Initializes connection-properties related part of this panel.
	 */
	private void initConnectionProperties(){
		userName = getTextFiled(saveUserNameAction);
		password = getTextFiled(savePasswordAction);
		host = getTextFiled(saveHostAction);
		port = getTextFiled(savePortAction);
		
		userNameLabel = new JLabel("Username");
		passwordLabel = new JLabel("Password");
		hostLabel = new JLabel("Host");
		portLabel = new JLabel("Port");
		
		Utils.setToolTipText(userName, "User name for JDV server. Same as \"username\" property.");
		Utils.setToolTipText(password, "Password for JDV server. Same as \"password\" property.");
		Utils.setToolTipText(host, "Host name of JDV server. Same as \"host.name\" property.");
		Utils.setToolTipText(port, "Port of JDV server. Same as \"host.port\" property.");
	}
	
	public static JTextField getTextFiled(final SaveToSettingsFileAction focusLostSaveAction){
		JTextField field = new JTextField();
		field.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				focusLostSaveAction.save();
			}
		});
		return field;
	}
	
	/**
	 * Action for the Browse button.
	 * @author jdurani
	 *
	 */
	private static class BrowseActionListener implements ActionListener {
		
		private final JTextField textField;
		private final int selectionMode;
		private final SaveToSettingsFileAction saveAction;
		private JFileChooser chooser;
		
		private BrowseActionListener(JTextField textField, int selectionMode, SaveToSettingsFileAction saveAction) {
			this.textField = textField;
			this.selectionMode = selectionMode;
			this.saveAction = saveAction;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(chooser == null){
				chooser = new JFileChooser(textField.getText());
				chooser.setMultiSelectionEnabled(false);
				chooser.setFileSelectionMode(selectionMode);
				chooser.setAcceptAllFileFilterUsed(false);
			} else {
				chooser.setCurrentDirectory(new File(textField.getText()));
			}
			if(chooser.showOpenDialog(SwingUtilities.getWindowAncestor((Component)e.getSource())) == JFileChooser.APPROVE_OPTION){
				textField.setText(chooser.getSelectedFile().getAbsolutePath());
				saveAction.save();
			}
		}
	}
	
	public static interface SaveToSettingsFileAction{
		void save();
	}
	
	private class SaveArtifactsDir implements SaveToSettingsFileAction{
		@Override
		public void save() {
			Settings.getInstance().setArtifactsDir(artifactsDir.getText());
			LOGGER.info("Artifacts dir is set to " + artifactsDir.getText());
		}
	}
	
	private class SaveConfig implements SaveToSettingsFileAction{
		@Override
		public void save() {
			Settings.getInstance().setConfig(config.getText());
			LOGGER.info("Config path is set to " + config.getText());
		}
	}
	
	private class SaveScenariosPath implements SaveToSettingsFileAction{
		@Override
		public void save() {
			Settings.getInstance().setScenarioPath(scenarios.getText());
			LOGGER.info("Path to scenarios is set to " + scenarios.getText());
		}
	}
	
	private class SaveOutputDir implements SaveToSettingsFileAction{
		@Override
		public void save() {
			Settings.getInstance().setOutputDir(outputDir.getText());
			LOGGER.info("Output dir is set to " + outputDir.getText());
		}
	}
	
	private class SavePathToResults implements SaveToSettingsFileAction{
		@Override
		public void save() {
			Settings.getInstance().setPathToTestResults(summaryTotalsDir.getText());
			LOGGER.info("Path to test results is set to " + summaryTotalsDir.getText());
		}
	}
	
	private class SavePathToRepository implements SaveToSettingsFileAction{
		@Override
		public void save() {
			Settings.getInstance().setPathToRepository(repositorySettings.getText());
			LOGGER.info("Path to test repository is set to " + repositorySettings.getText());
		}
	}
	
	private class SaveUser implements SaveToSettingsFileAction{
		@Override
		public void save() {
			Settings.getInstance().setUsername(userName.getText());
			LOGGER.info("Username is set to " + userName.getText());
		}
	}
	
	private class SavePassword implements SaveToSettingsFileAction{
		@Override
		public void save() {
			Settings.getInstance().setPassword(password.getText());
			LOGGER.info("Password is set to " + password.getText());
		}
	}
	
	private class SaveHost implements SaveToSettingsFileAction{
		@Override
		public void save() {
			Settings.getInstance().setHost(host.getText());
			LOGGER.info("Host address is set to " + host.getText());
		}
	}
	
	private class SavePort implements SaveToSettingsFileAction{
		@Override
		public void save() {
			Settings.getInstance().setPort(port.getText());
			LOGGER.info("Port is set to " + port.getText());
		}
	}
	
	private class SaveInclude implements SaveToSettingsFileAction{
		@Override
		public void save() {
			Settings.getInstance().setScenarioInclude(include.getText());
			LOGGER.info("Include scenario is set to " + include.getText());
		}
	}
	
	private class SaveExclude implements SaveToSettingsFileAction{
		@Override
		public void save() {
			Settings.getInstance().setScenarioExclude(exclude.getText());
			LOGGER.info("Exclude scenario is set to " + exclude.getText());
		}
	}
	
	private class SavePre1Support implements SaveToSettingsFileAction{
	    @Override
	    public void save() {
	        Settings.getInstance().setPre1Support(Boolean.toString(pre1Supported.isSelected()));
	        LOGGER.info("Old names support is set to " + pre1Supported.isSelected());
	    }
	}
	
	private class SaveTeiidTestArtifactsSelection implements SaveToSettingsFileAction{
	    
	    private final String dirname;
	    
	    public SaveTeiidTestArtifactsSelection(String dirname) {
            this.dirname = dirname;
        }
	    
	    @Override
	    public void save() {
	        Settings.getInstance().setTeiidTestArtifactsDir(dirname);
	        LOGGER.info("Teiid-test-artifacts is set to " + dirname);
	    }
	}
	
	private class SaveUseStandardArtifactsPath implements SaveToSettingsFileAction{
		@Override
		public void save() {
			Settings.getInstance().setUseStandardArtifactsPath(Boolean.toString(useStandardArtifactsPath.isSelected()));
			LOGGER.info("Use standard path is set to " + useStandardArtifactsPath.isSelected());
		}
	}
}

















