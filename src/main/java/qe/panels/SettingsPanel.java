package qe.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qe.entity.settings.Settings;

public class SettingsPanel extends JPanel{

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
	
	private JTextField summaryTotalsDir;
	private JLabel summaryTotalsDirLabel;
	private JButton summaryTotalsDirBrowseButton;
	private JTextField repositorySettings;
	private JLabel repositorySettingsLabel;
	private JButton repositorySettingsBrowseButton;
	
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
						.addComponent(configBrowseButton)))
				.addGroup(gl.createParallelGroup()
					.addComponent(artifactsDirLabel)
					.addGroup(gl.createSequentialGroup()
						.addComponent(artifactsDir)
						.addComponent(artifactsDirBrowseButton))))
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
					.addComponent(configBrowseButton))
				.addComponent(artifactsDirLabel)
				.addGroup(gl.createParallelGroup()
					.addComponent(artifactsDir)
					.addComponent(artifactsDirBrowseButton)))
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
		setText(summaryTotalsDir, settings.getPathToTestResults());
		setText(repositorySettings, settings.getPathToRepository());
		setText(host, settings.getHost());
		setText(port, settings.getPort());
		setText(userName, settings.getUsername());
		setText(password, settings.getPassword());
		setText(include, settings.getScenarioInclude());
		setText(exclude, settings.getScenarioExclude());
		setText(scenarios, settings.getScenarioPath());
		setText(outputDir, settings.getOutputDir());
		setText(config, settings.getConfig());
		setText(artifactsDir, settings.getArtefactsDir());
		if(settings.getPre1Support() != null){
			pre1Supported.setSelected(Boolean.parseBoolean(settings.getPre1Support()));
		}
	}
	
	private void setText(JTextField field, String text){
		if (text != null) {
			field.setText(text);
		}
	}
	
	private void initSummaryTotalsDir(){
		// Label Path to test results:
		summaryTotalsDirLabel = new JLabel("Path to test results:");
		summaryTotalsDirLabel.setHorizontalAlignment(SwingConstants.LEFT);
		summaryTotalsDir = getTextFiled(savePathToResultsAction);
		setToolTipText(summaryTotalsDir, "The folder with Summary_totals.txt");
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
		
		setToolTipText(include, "Include scenario pattern. Same as \"bqt.scenario.include\" property.");
		setToolTipText(exclude, "Exclude scenario pattern. Same as \"bqt.scenario.exclude\" property.");
		
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
		setToolTipText(pre1Supported, "If old names of BQT properties are supported. Same as \"support.pre1.0.scenario\" property.");
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
		
		artifactsDir = getTextFiled(saveArtifactsDirAction);
		artifactsDirLabel = new JLabel("Artifacts directory");
		artifactsDirBrowseButton = getBrowseButton(JFileChooser.DIRECTORIES_ONLY, artifactsDir, saveArtifactsDirAction);
		
		setToolTipText(scenarios, "Path to scenario file. It could be a single file or a directory. Same as \"scenario.file\" property.");
		setToolTipText(outputDir, "Path to output directory. Same as \"output.dir\" property.");
		setToolTipText(config, "Path to default config file. Usually <bqt-distro-path>/config/test.properties. Same as \"config\" property.");
		setToolTipText(artifactsDir, "Path to queries and expected results. "
				+ "Usually <dataservices-path>/<test-artifacts-dir>/ctc-tests/queries. Same as \"queryset.artifacts.dir\" property.");
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
	private JButton getBrowseButton(int selectionMode, JTextField textField, SaveToSettingsFileAction saveAction){
		JButton button = new JButton("Browse");
		button.addActionListener(new BrowseActionListener(textField, selectionMode, saveAction));
		return button;
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
		
		setToolTipText(userName, "User name for JDV server. Same as \"username\" property.");
		setToolTipText(password, "Password for JDV server. Same as \"password\" property.");
		setToolTipText(host, "Host name of JDV server. Same as \"host.name\" property.");
		setToolTipText(port, "Port of JDV server. Same as \"host.port\" property.");
	}
	
	private static void setToolTipText(JComponent component, String text){
		component.setToolTipText(text);
		ToolTipManager.sharedInstance().registerComponent(component);
	}
	
	private static JTextField getTextFiled(final SaveToSettingsFileAction focusLostSaveAction){
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
			if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
				textField.setText(chooser.getSelectedFile().getAbsolutePath());
				saveAction.save();
			}
		}
	}
	
	private static interface SaveToSettingsFileAction{
		void save();
	}
	
	private class SaveArtifactsDir implements SaveToSettingsFileAction{
		@Override
		public void save() {
			Settings.getInstance().setArtefactsDir(artifactsDir.getText());
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
			LOGGER.info("Exclude scenario is set to " + pre1Supported.isSelected());
		}
	}
}

















