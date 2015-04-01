package qe.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qe.entity.result.ResultGetter;
import qe.entity.settings.Settings;
import qe.log.appender.GUIAppender;
import qe.panels.GUIRunnerPanel;
import qe.panels.SettingsPanel;

/**
 * 
 * @author felias
 *
 *Main gate of the application. Contains the main frame and tabbed panel
 */
public class ResultsGUI {

	private static final Logger logger = LoggerFactory.getLogger(ResultsGUI.class);

	private JFrame frmBqtTestParser;
	private JTabbedPane tabbedPane;
	private ResultGetter results = new ResultGetter(frmBqtTestParser);
	private GUIRunnerPanel guiPanel;
	private JTextPane log;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ResultsGUI window = new ResultsGUI();
					window.frmBqtTestParser.setVisible(true);
				} catch (Exception e) {
					logger.error("Error when starting application", e);
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ResultsGUI() {
		initialize();
	}

	/**
	 * Initializes the contents of the frame and loads components
	 */
	@SuppressWarnings("serial")
	private void initialize() {
		// Load configuration file if exists, otherwise crates empty
		// configuration file
		logger.debug("Loading settings");
		Settings.getInstance().loadSettings();

		// main window of the GUI
		frmBqtTestParser = new JFrame(){
			@Override
			public void dispose() {
				if(guiPanel.couldDispose()){
					super.dispose();
				}
			}
		};
		frmBqtTestParser.setTitle("BQT Test Parser");
//		frmBqtTestParser.setBounds(100, 100, 682, 449);
		frmBqtTestParser.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Container for panels with test results and settings
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

		log = GUIAppender.getTextPane("ALL_GUI");

		JScrollPane logpane = new JScrollPane(log);
		
		JSplitPane splitpane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane, logpane);
		splitpane.setOneTouchExpandable(true);
		splitpane.setDividerSize(15);

		splitpane.setResizeWeight(0.9);

		frmBqtTestParser.getContentPane().add(splitpane);

		PanelDetails details = new PanelDetails(results);
		PanelResults totalResults = new PanelResults(results, details);
//		PanelSettings settings = new PanelSettings();
		guiPanel = new GUIRunnerPanel();
		JScrollPane bqtPane = new JScrollPane(guiPanel);
		JScrollPane settingsPane = new JScrollPane(new SettingsPanel());
		
		tabbedPane.addTab("Total Results", null, totalResults.getPanel(), null);
		tabbedPane.addTab("Results Details", null, details.getPanel(), null);
		tabbedPane.addTab("BQT Runner", null, bqtPane, null);
		tabbedPane.addTab("Settings", null, settingsPane, null);
//		tabbedPane.addTab("Settings", null, settings.getPanel(), null);
//		settings.initialize();
		totalResults.initialize();
		details.initialize();
		frmBqtTestParser.pack();
		frmBqtTestParser.setLocationRelativeTo(null);
		logger.info("Application has been initialized.");

	}
}
