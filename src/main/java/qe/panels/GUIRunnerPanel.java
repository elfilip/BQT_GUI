package qe.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Properties;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.apache.logging.log4j.Level;
import org.jboss.bqt.client.TestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qe.entity.settings.Settings;
import qe.log.appender.GUIAppender;
import qe.utils.Utils;

/**
 * Panel for starting BQT via GUI.
 * 
 * @author jdurani
 *
 */
public class GUIRunnerPanel extends JPanel {
	
	private static final long serialVersionUID = -7581598828220798660L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GUIRunnerPanel.class);
	
	/**
	 * BQT tool properties.
	 */
	public static interface BQTProperties { 
		//keys for properties for bqt-distro
		public static final String SCENARIO_FILE_PROP = "scenario.file";
		public static final String RESULT_MODE_PROP = "result.mode";
		public static final String QUERYSET_ARTIFACTS_DIR_PROP = "queryset.artifacts.dir";
		public static final String OUTPUT_DIR_PROP = "output.dir";
		public static final String CONFIG_PROP = "config";
		public static final String HOST_NAME_PROP = "host.name";
		public static final String HOST_PORT_PROP = "host.port";
		public static final String USERNAME_PROP = "username";
		public static final String PASSWORD_PROP = "password";
		public static final String SUPPORT_OLD_PROP_FORMAT_PROP= "support.pre1.0.scenario";
		
		//include exclude options
		public static final String INCLUDE_PROP = "bqt.scenario.include";
		public static final String EXCLUDE_PROP = "bqt.scenario.exclude";
		
		//result modes
		public static final String RESULT_MODE_SQL = "SQL";
		public static final String RESULT_MODE_NONE = "NONE";
		public static final String RESULT_MODE_COMPARE = "COMPARE";
		public static final String RESULT_MODE_GENERATE= "GENERATE";
		
	}
	
	private JComboBox<String> resultModes;
	private JLabel resultModesLabel;
	
	private JButton startButton;
	private JButton cancelButton;
	
	private BQTRunner runningInstance;
	
	private JTextPane bqtLogPane;
	private JScrollPane bqtLogScrollPane;
	
	private JLabel status;
	private JLabel statusLabel;
	
	/**
	 * Creates a new instance.
	 */
	public GUIRunnerPanel() {
		super();
		init();
	}
	
	/**
	 * Initializes this panel.
	 */
	private void init(){
		initResultModes();
		initOptionButtons();
		initBqtLogPane();
		initStatusLabel();
		
		GroupLayout gl = new GroupLayout(this);
		gl.setAutoCreateContainerGaps(true);
		gl.setAutoCreateGaps(true);
		gl.setHorizontalGroup(gl.createParallelGroup()
			.addGroup(gl.createSequentialGroup()
				.addComponent(resultModesLabel)
				.addComponent(resultModes, 120, 120, 120))
			.addGroup(gl.createSequentialGroup()
				.addComponent(startButton)
				.addComponent(cancelButton)
				.addComponent(statusLabel)
				.addComponent(status))
			.addComponent(bqtLogScrollPane));
		
		int groupsGap = 25;
		gl.setVerticalGroup(gl.createSequentialGroup()
			.addGroup(gl.createSequentialGroup()
				.addGap(groupsGap)
				.addGroup(gl.createParallelGroup()
					.addComponent(resultModesLabel)
					.addComponent(resultModes, 25, 25, 25))
				.addGap(groupsGap)				
			.addGap(groupsGap)
			.addGroup(gl.createParallelGroup(Alignment.CENTER)
				.addComponent(startButton)
				.addComponent(cancelButton)
				.addComponent(statusLabel)
				.addComponent(status))
			.addComponent(bqtLogScrollPane, 200, 400, 800)));
		
		setLayout(gl);
	}
	
	private void initStatusLabel(){
		status = new JLabel("NOT RUNNING");
		status.setFont(new Font("Arial", Font.BOLD, 20));
		Utils.setToolTipText(status, "BQT status");
		statusLabel = new JLabel("Status: ");
	}
	
	private void initBqtLogPane(){
		bqtLogPane = GUIAppender.getTextPane("BQT_GUI");
		bqtLogScrollPane = new JScrollPane(bqtLogPane);
	}
	
	/**
	 * Initializes buttons. 
	 */
	private void initOptionButtons(){
		startButton = new JButton("Start");
		startButton.addActionListener(new StartBQTActionListener());
		
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new CancelBQTActionListener());
		
		Utils.setToolTipText(startButton, "Start BQT task.");
		Utils.setToolTipText(cancelButton, "Cancel BQT task.");
	}
	
	/**
	 * Initializes result-mode related part of this panel;
	 */
	private void initResultModes(){
		resultModes = new JComboBox<String>();
		resultModes.addItem(BQTProperties.RESULT_MODE_NONE);
		resultModes.addItem(BQTProperties.RESULT_MODE_COMPARE);
		resultModes.addItem(BQTProperties.RESULT_MODE_SQL);
		resultModes.addItem(BQTProperties.RESULT_MODE_GENERATE);
		
		resultModes.setSelectedItem(BQTProperties.RESULT_MODE_COMPARE);
		
		Utils.setToolTipText(resultModes, "Result mode. Same as \"result.mode\" property.");
		
		resultModesLabel = new JLabel("Result mode");
	}
	
	/**
	 * Determines, if this panel could be disposed. If BQT is not running
	 * method return {@code true}. Otherwise method returns same results as
	 * method {@link #cancelActualJob(boolean)}.
	 * 
	 * @return true, if this panel could be disposed, false otherwise
	 * @see #cancelActualJob(boolean)
	 */
	public boolean couldDispose(){
		if(runningInstance == null){
			return true;
		}
		return cancelActualJob(true);
	}
	
	/**
	 * <p>
	 * This method shows a confirm dialog. If the user confirm that he want to cancel
	 * actual job, then actual BQT-job will be canceled and method will return {@code true}.
	 * Otherwise method will not cancel actual job and {@code false} will be returned.
	 * </p>
	 * <p>
	 * Method supposes that the BQT-job is running.
	 * </p>
	 * 
	 * @param disposeLogFrame If the BQT-job is running and a BQT-log-frame is visible,
	 * 		then the frame will be disposed.
	 * @return true, if actual job has been canceled, false otherwise
	 */
	public boolean cancelActualJob(boolean disposeLogFrame){
		int result = JOptionPane.showConfirmDialog(getWindowAncestor(), "BQT is still running. Do you want to interrupt it?");
		if(result == JOptionPane.YES_OPTION){
			if(runningInstance != null){
				runningInstance.cancel(true);
			}
			return true;
		} else {
			return false;
		}
		
	}
	
	/**
	 * Sets text and foreground color for status label.
	 * @param statusText
	 * @param bg
	 */
	private void setStatus(String statusText, Color bg) {
		status.setText(statusText);
		status.setForeground(bg);
	}

	private JFrame getWindowAncestor(){
		return (JFrame) SwingUtilities.getWindowAncestor(this);
	}
	
	/**
	 * Action for the Start-BQT button
	 * @author jdurani
	 *
	 */
	private class StartBQTActionListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(runningInstance != null){
				JOptionPane.showMessageDialog(getWindowAncestor(), "BQT already running!", "BQT running", JOptionPane.ERROR_MESSAGE);
			} else {
				runningInstance = new BQTRunner();
				runningInstance.execute();
			}
		}
	}
	
	/**
	 * Action for the Cancel-BQT button.
	 * @author jdurani
	 *
	 */
	private class CancelBQTActionListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(runningInstance != null){
				cancelActualJob(false);
			}
		}
	}
	
	/**
	 * Swing worker, which sets all required properties for BQT and runs BQT-job.
	 *  
	 * @author jdurani
	 *
	 */
	private class BQTRunner extends SwingWorker<Void, Void> {
		
		
		@Override
		protected Void doInBackground() throws Exception {
			Properties props = getProperties();
			bqtLogPane.setText("");
			LOGGER.info("Starting BQT with properties: " + props + ".");
			setStatus("IN PROGRESS", Color.ORANGE); 
			new TestClient().runTest(props);
			LOGGER.debug("BQT ended.");
			return null;
		}
		
		@Override
		protected void done() {
			runningInstance = null;
			try{
				LOGGER.debug("Checking result.");
				get();
				setStatus("   DONE   ", Color.GREEN);
				LOGGER.debug("Result OK.");
			} catch (ExecutionException ex){
				ex.printStackTrace();
				Utils.showMessageDialog(getWindowAncestor(), Level.WARN,
						"Task ends with an exception: " + ex.getCause().getMessage() + "."
								+ System.getProperty("line.separator") + "See log for more details.", ex);
				setStatus("  FAILED  ", Color.RED);
			} catch (CancellationException ex){
				ex.printStackTrace();
				Utils.showMessageDialog(getWindowAncestor(), Level.INFO,
						"Task has been cancelled.", ex);
				setStatus("  FAILED  ", Color.RED);
			} catch (InterruptedException ex){
				ex.printStackTrace();
				Utils.showMessageDialog(getWindowAncestor(), Level.WARN,
						"Task has been interrupted. See log for more details.", ex);
				setStatus("  FAILED  ", Color.RED);
			}
		}
		
		/**
		 * Returns all required properties.
		 * @return
		 */
		private Properties getProperties(){
			Properties props = new Properties();
			Settings settings = Settings.getInstance();
			props.setProperty(BQTProperties.HOST_NAME_PROP, settings.getHost());
			props.setProperty(BQTProperties.HOST_PORT_PROP, settings.getPort());
			props.setProperty(BQTProperties.USERNAME_PROP, settings.getUsername());
			props.setProperty(BQTProperties.PASSWORD_PROP, settings.getPassword());
			props.setProperty(BQTProperties.SCENARIO_FILE_PROP, settings.getScenarioPath());
			props.setProperty(BQTProperties.RESULT_MODE_PROP, resultModes.getSelectedItem().toString());
			props.setProperty(BQTProperties.OUTPUT_DIR_PROP, settings.getOutputDir());
			props.setProperty(BQTProperties.CONFIG_PROP, settings.getConfig());
			props.setProperty(BQTProperties.SUPPORT_OLD_PROP_FORMAT_PROP, settings.getPre1Support());
			props.setProperty(BQTProperties.INCLUDE_PROP, settings.getScenarioInclude());
			props.setProperty(BQTProperties.EXCLUDE_PROP, settings.getScenarioExclude());
			
			
			if(Boolean.parseBoolean(settings.getUseStandardArtifactsPath())){
			    StringBuilder b = new StringBuilder(settings.getPathToRepository())
			            .append(File.separator)
			            .append(settings.getTeiidTestArtifactsDir())
			            .append(File.separator)
			            .append("ctc-tests")
			            .append(File.separator)
			            .append("queries");
			    props.setProperty(BQTProperties.QUERYSET_ARTIFACTS_DIR_PROP, b.toString());
			} else {
			    props.setProperty(BQTProperties.QUERYSET_ARTIFACTS_DIR_PROP, settings.getArtifactsDir());
			}
			
			
			return props;
		}
	}
}












