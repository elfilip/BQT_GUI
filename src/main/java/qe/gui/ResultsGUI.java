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
import qe.panels.BQTRunnerPanel;
import qe.panels.JenkinsPanel;
import qe.panels.SettingsPanel;
import qe.utils.Utils;

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
	private BQTRunnerPanel bqtPanel;

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
				if(bqtPanel.couldDispose()){
					super.dispose();
				}
			}
		};
		frmBqtTestParser.setTitle("BQT Test Parser");
		frmBqtTestParser.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Container for panels with test results and settings
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

		JTextPane log = GUIAppender.getTextPane("ALL_GUI");
		JScrollPane logpane = Utils.getScrollPane(log);
		
		JSplitPane splitpane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane, logpane);
		splitpane.setOneTouchExpandable(true);
		splitpane.setDividerSize(15);

		splitpane.setResizeWeight(0.9);

		frmBqtTestParser.getContentPane().add(splitpane);

		PanelDetails details = new PanelDetails(results);
		PanelResults totalResults = new PanelResults(results, details);
		bqtPanel = new BQTRunnerPanel();
		JenkinsPanel jenkinsPanel = new JenkinsPanel();
		JScrollPane settingsPane = Utils.getScrollPane(new SettingsPanel());
		
		tabbedPane.addTab("Total Results", null, totalResults.getPanel(), null);
		tabbedPane.addTab("Results Details", null, details.getPanel(), null);
		tabbedPane.addTab("BQT Runner", null, bqtPanel, null);
		tabbedPane.addTab("Jenkins", null, jenkinsPanel, null);
		tabbedPane.addTab("Settings", null, settingsPane, null);
//		tabbedPane.setSelectedIndex(3);
		totalResults.initialize();
		details.initialize();
		frmBqtTestParser.pack();
		frmBqtTestParser.setLocationRelativeTo(null);
		logger.info("Application has been initialized.");

	}
}
