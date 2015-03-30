package qe.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import qe.entity.result.ResultGetter;
import qe.entity.settings.Settings;
import qe.log.appender.GUIAppender;

/**
 * 
 * @author felias
 *
 *Main gate of the application. Contains the main frame and tabbed panel
 */
public class ResultsGUI {

	private static final Logger logger = Logger.getLogger(ResultsGUI.class);

	private JFrame frmBqtTestParser;
	private JTabbedPane tabbedPane;
	private ResultGetter results = new ResultGetter(frmBqtTestParser);
	private JTextArea log;

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
	private void initialize() {
		// Load configuration file if exists, otherwise crates empty
		// configuration file
		logger.debug("Loading settings");
		Settings.getInstance().loadSettings();

		// main window of the GUI
		frmBqtTestParser = new JFrame();
		frmBqtTestParser.setTitle("BQT Test Parser");
		frmBqtTestParser.setBounds(100, 100, 682, 449);
		frmBqtTestParser.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Container for panels with test results and settings
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

		log = new JTextArea();
		log.setEditable(false);

		GUIAppender.setArea(log);
		JScrollPane logpane = new JScrollPane();

		logpane.getViewport().add(log);

		JSplitPane splitpane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane, logpane);
		splitpane.setOneTouchExpandable(true);
		splitpane.setDividerSize(15);

		splitpane.setResizeWeight(0.9);

		frmBqtTestParser.getContentPane().add(splitpane);

		PanelDetails details = new PanelDetails(results);
		PanelResults totalResults = new PanelResults(results, details);
		PanelSettings settings = new PanelSettings();
		tabbedPane.addTab("Total Results", null, totalResults.getPanel(), null);
		tabbedPane.addTab("Results Details", null, details.getPanel(), null);
		tabbedPane.addTab("Settings", null, settings.getPanel(), null);
		settings.initialize();
		totalResults.initialize();
		details.initialize();
		logger.info("Application has been initialized.");

	}
}
