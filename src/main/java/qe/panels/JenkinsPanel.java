package qe.panels;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qe.entity.result.RefreshResults;
import qe.entity.settings.Settings;
import qe.gui.DownloadManager;
import qe.jenkins.JenkinsActiveConfiguration;
import qe.jenkins.JenkinsActiveConfiguration.JenkinsStatus;
import qe.jenkins.JenkinsBuild;
import qe.jenkins.JenkinsJob;
import qe.jenkins.JenkinsManager;
import qe.jenkins.JenkinsManager.DownloadPublisher;
import qe.panels.SettingsPanel.SaveToSettingsFileAction;
import qe.utils.FileLoader;
import qe.utils.Utils;

/**
 * This class represents a {@link JPanel} for jenkins.
 * 
 * @author Juraj Duráni
 */
public class JenkinsPanel extends JPanel {

    private static final long serialVersionUID = -8886425441947147863L;
    
    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(JenkinsPanel.class);
    
    private ExecutorService executorService = null;
    
    private StatusPanel status;
    
    private JTextField viewName;
    private JLabel viewNameLabel;
    private JButton selectViewButton;
    
    private JTextField downloadDir;
    private JLabel downloadDirLabel;
    private JButton selectDownloadDirButton;
    
    private JTextField jobName;
    private JLabel jobNameLabel;
    private JButton selectJobButton;
    private JButton showJobButton;
    
    private JButton showResults;
    
    private DownloadManager manager;
    private JButton showDownloadManagerButton;
    
    private JenkinsBuildPanel jenkinsBuildPanel;
    private JScrollPane jenkinsBuildPane;
    
    private JButton downloadAllArtifactsOfNode;
    private JButton downloadAllArtifactsOfBuild;
    private JButton downloadCustomArtifactsOfNode;
    
    private JButton downloadConsoleLogOfNode;
    private JButton downloadConsoleLogsOfBuild;
    private JButton showLogOfNode;
    
    private final SaveToSettingsFileAction saveViewName = new SaveViewName();
    private final SaveToSettingsFileAction saveJobName = new SaveJobName();
    private final SaveToSettingsFileAction saveDownloadDir = new SaveDownloadDir();
    
    private final StatusDownloadPublisher downloadPublisher = new StatusDownloadPublisher();
    
    private JenkinsJob jenkinsJob;
    
    private SelectJobViewWorker selectJobViewWorker;
    private ShowJobWorker showJobWorker;
    private ShowLogWorker showLogWorker;
    
    /**
     * {@inheritDoc}
     */
    public JenkinsPanel(){
        super();
        init();
        LOG.info("Jenkis panel has been initialized.");
    }
    
    /**
     * Initializes this panel;
     */
    private void init(){
        initStatus();
        initView();
        initJob();
        initDownloadDir();
        initJobInfoPanel();
        initMenu();
        initDownloadManager();
        initDefaultValues();
        
        GroupLayout gl = new GroupLayout(this);
        gl.setAutoCreateContainerGaps(true);
        gl.setAutoCreateGaps(true);
        gl.setHorizontalGroup(gl.createSequentialGroup()
            .addGroup(gl.createParallelGroup()
                .addComponent(showJobButton)
                .addComponent(showResults)
                .addComponent(showDownloadManagerButton)
                .addComponent(downloadAllArtifactsOfNode)
                .addComponent(downloadAllArtifactsOfBuild)
                .addComponent(downloadCustomArtifactsOfNode)
                .addComponent(downloadConsoleLogOfNode)
                .addComponent(downloadConsoleLogsOfBuild)
                .addComponent(showLogOfNode))
            .addGroup(gl.createParallelGroup()
                .addComponent(status)
                .addGroup(gl.createParallelGroup()
                    .addComponent(viewNameLabel)
                    .addGroup(gl.createSequentialGroup()
                        .addComponent(viewName)
                        .addComponent(selectViewButton)))
                .addGroup(gl.createParallelGroup()
                    .addComponent(jobNameLabel)
                    .addGroup(gl.createSequentialGroup()
                        .addComponent(jobName)
                        .addComponent(selectJobButton)))
                .addGroup(gl.createParallelGroup()
                    .addComponent(downloadDirLabel)
                    .addGroup(gl.createSequentialGroup()
                        .addComponent(downloadDir)
                        .addComponent(selectDownloadDirButton)))
                .addComponent(jenkinsBuildPane)));
        gl.setVerticalGroup(gl.createParallelGroup()
            .addGroup(gl.createSequentialGroup()
                .addComponent(showJobButton)
                .addComponent(showResults)
                .addGap(25)
                .addComponent(showDownloadManagerButton)
                .addGap(25)
                .addComponent(downloadAllArtifactsOfNode)
                .addComponent(downloadAllArtifactsOfBuild)
                .addComponent(downloadCustomArtifactsOfNode)
                .addGap(25)
                .addComponent(downloadConsoleLogOfNode)
                .addComponent(downloadConsoleLogsOfBuild)
                .addComponent(showLogOfNode))
            .addGroup(gl.createSequentialGroup()
                .addComponent(status)
                .addGroup(gl.createSequentialGroup()
                    .addComponent(viewNameLabel)
                    .addGroup(gl.createParallelGroup()
                        .addComponent(viewName)
                        .addComponent(selectViewButton)))
                .addGroup(gl.createSequentialGroup()
                    .addComponent(jobNameLabel)
                    .addGroup(gl.createParallelGroup()
                        .addComponent(jobName)
                        .addComponent(selectJobButton)))
                .addGroup(gl.createSequentialGroup()
                    .addComponent(downloadDirLabel)
                    .addGroup(gl.createParallelGroup()
                        .addComponent(downloadDir)
                        .addComponent(selectDownloadDirButton)))
                .addComponent(jenkinsBuildPane)));
        
        gl.linkSize(SwingConstants.VERTICAL, jobName, viewName, downloadDir);
        gl.linkSize(selectJobButton, selectViewButton, selectDownloadDirButton);
        gl.linkSize(showJobButton, downloadAllArtifactsOfBuild, downloadAllArtifactsOfNode,
                downloadCustomArtifactsOfNode, showResults, downloadConsoleLogOfNode,
                downloadConsoleLogsOfBuild, showLogOfNode, showDownloadManagerButton);
        
        setLayout(gl);
    }
    
    /**
     * Initializes download manager.
     */
    private void initDownloadManager(){
        manager = new DownloadManager();
    }
    
    /**
     * Initializes the status. 
     */
    private void initStatus(){
        status = new StatusPanel();
    }

    /**
     * Initializes view.
     */
    private void initView(){
        viewName = SettingsPanel.getTextFiled(saveViewName);
        viewNameLabel = new JLabel("View");
        selectViewButton = new JButton("Select view");
        selectViewButton.addActionListener(new SelectViewActionListener());
        
        Utils.setToolTipText(viewName, "View name in jenkins.");
    }

    /**
     * Initializes job.
     */
    private void initJob(){
        jobName = SettingsPanel.getTextFiled(saveJobName);
        jobNameLabel = new JLabel("Job");
        selectJobButton = new JButton("Select job");
        selectJobButton.addActionListener(new SelectJobActionListener());
        
        Utils.setToolTipText(jobName, "Name of job in jenkins.");
    }

    /**
     * Initializes download directory.
     */
    private void initDownloadDir(){
        downloadDir = SettingsPanel.getTextFiled(saveDownloadDir);
        downloadDirLabel = new JLabel("Download directory");
        selectDownloadDirButton = SettingsPanel.getBrowseButton(JFileChooser.DIRECTORIES_ONLY, downloadDir, saveDownloadDir);
        
        Utils.setToolTipText(viewName, "View name in jenkins.");
    }

    /**
     * Initializes panel for job info - builds.
     */
    private void initJobInfoPanel(){
        jenkinsBuildPanel = new JenkinsBuildPanel(this);
        jenkinsBuildPane = Utils.getScrollPane(jenkinsBuildPanel);
    }

    /**
     * Initializes menu.
     */
    private void initMenu(){
        showJobButton = new JButton("Show selected job");
        showJobButton.addActionListener(new ShowJobActionListener());
        
        showResults = new JButton("Show results");
        showResults.addActionListener(new ShowResultsActionListener());
        
        showDownloadManagerButton = new JButton("Show download manager");
        showDownloadManagerButton.addActionListener(new ShowDownloadManagerListener());
        
        downloadAllArtifactsOfNode = new JButton("Download all artifacts of node");
        downloadAllArtifactsOfNode.addActionListener(new DownloadAllArtifactsOfNodeActionListener());

        downloadAllArtifactsOfBuild = new JButton("Download all artifacts of build");
        downloadAllArtifactsOfBuild.addActionListener(new DownloadAllArtifactsOfBuildActionListener());
        
        downloadCustomArtifactsOfNode = new JButton("Download custom artifacts of node");
        downloadCustomArtifactsOfNode.addActionListener(new DownloadCustomArtifactsOfNodeActionListener());
        
        downloadConsoleLogOfNode = new JButton("Download console log of node");
        downloadConsoleLogOfNode.addActionListener(new DownloadConsoleLogOfNodeActionListener());
        
        downloadConsoleLogsOfBuild = new JButton("Download console logs of build");
        downloadConsoleLogsOfBuild.addActionListener(new DownloadConsoleLogsOfBuildActionListener());
        
        showLogOfNode = new JButton("Show log of node");
        showLogOfNode.addActionListener(new ShowLogOfNodeActionListener());
        
        buildSelected(false);
        nodeSelected(false);
    }
    
    /**
     * If artifacts have been downloaded, enables show results button.
     */
    private void enableShowResults(){
        showResults.setEnabled(getSummaryTotalsFile(getPathToArtifactsOfSelectedNode()) != null);
    }
    
    /**
     * If console log have been downloaded, enables show log button.
     */
    private void enableShowLog(){
        File base = getPathToArtifactsOfSelectedNode();
        if(base != null){
            showLogOfNode.setEnabled(new File(base, DownloadLogWorker.CONSOLE_LOG).exists());
        }
    }
    
    /**
     * Enables/Disables all buttons which are relevant to node.
     * 
     * @param selected if node is selected or deselected
     */
    void nodeSelected(boolean selected){
        enableButtons(selected, downloadConsoleLogOfNode,
                downloadAllArtifactsOfNode, downloadCustomArtifactsOfNode);
        if(selected){
            enableShowResults();
            enableShowLog();
        } else {
            enableButtons(false, showLogOfNode, showResults);
        }
    }
    
    /**
     * Enables/Disables all buttons which are relevant to build
     * 
     * @param selected if build is selected or deselected 
     */
    void buildSelected(boolean selected){
        enableButtons(selected, downloadConsoleLogsOfBuild, downloadAllArtifactsOfBuild);
    }

    /**
     * Sets "enabled" flag to every button in {@code buttons}.
     * 
     * @param enabled
     * @param buttons
     */
    private void enableButtons(boolean enabled, JButton... buttons){
        if(buttons != null){
            for(JButton b : buttons){
                b.setEnabled(enabled);
            }
        }
    }
    
    /**
     * Initializes default values.
     */
    private void initDefaultValues(){
        Settings set = Settings.getInstance();
        viewName.setText(set.getJenkinsView());
        jobName.setText(set.getJenkinsJob());
        downloadDir.setText(set.getJenkinsDownloadDir());
    }

    /**
     * Updates job info panel.
     */
    private void updateJobInfoPanel() {
        if(jenkinsJob == null){
            return;
        }
        jenkinsBuildPanel.init(jenkinsJob);
        jenkinsBuildPane.setViewportView(jenkinsBuildPanel);
    }

    /**
     * Clears job info.
     */
    private void removeJobInfoPanel(){
        jenkinsJob = null;
        jenkinsBuildPane.setViewportView(null);
    }

    /**
     * Returns window ancestor of this panel;
     * @return
     */
    private Window getWindowAncestor(){
        return SwingUtilities.getWindowAncestor(this);
    }

    private class SaveViewName implements SaveToSettingsFileAction {
        @Override
        public void save() {
            Settings.getInstance().setJenkinsView(viewName.getText());
            LOG.info("Setting jenkins view name to {}.", viewName.getText());
        }
    }
    
    private class SaveJobName implements SaveToSettingsFileAction {
        @Override
        public void save() {
            Settings.getInstance().setJenkinsJob(jobName.getText());
            LOG.info("Setting jenkins job name to {}.", jobName.getText());
        }
    }
    
    private class SaveDownloadDir implements SaveToSettingsFileAction {
        @Override
        public void save() {
            Settings.getInstance().setJenkinsDownloadDir(downloadDir.getText());
            LOG.info("Setting jenkins download directory to {}.", downloadDir.getText());
        }
    }
    
    private class SelectViewActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            startSelectWorker(SelectJobViewWorker.VIEW);
        }
    }
    
    private class SelectJobActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            startSelectWorker(SelectJobViewWorker.JOB);
        }
    }
    
    private void startSelectWorker(int type){
        if(!notRunning(selectJobViewWorker, true)){
            return;
        }
        selectJobViewWorker = new SelectJobViewWorker(type);
        executeWorker(selectJobViewWorker);
    }
    
    private <T,K> boolean notRunning(SwingWorker<T, K> w, boolean showMessageDialog){
        if(w != null){
            if(showMessageDialog){
                Utils.showMessageDialog((JFrame)getWindowAncestor(), Level.WARN, 
                    "Action already in progress.", null);
            }
            return false;
        }
        return true;
    }
    

    private boolean isJenkinJobSelected(boolean showMessageDialog){
        if(jenkinsJob == null){
            if(showMessageDialog){
                Utils.showMessageDialog((JFrame)getWindowAncestor(), Level.DEBUG,
                        "No jenkis job. Use \"Show selected job\" button.",
                        null);
            }
            return false;
        }
        return true;
    }
    
    private boolean isBuildSelected(boolean showMessageDialog){
        if(jenkinsBuildPanel.getSelectedBuildNumber() == null){
            if(showMessageDialog){
                Utils.showMessageDialog((JFrame)getWindowAncestor(), Level.DEBUG,
                    "No build number selected.",
                    null);
            }
            return false;
        }
        return true;
    }
    
    private boolean isNodeSelected(boolean showMessageDialog){
        if(jenkinsBuildPanel.getUrlOfSelectedNode() == null){
            if(showMessageDialog){
                Utils.showMessageDialog((JFrame)getWindowAncestor(), Level.DEBUG,
                    "No node selected.",
                    null);
            }
            return false;
        }
        return true;
    }
    
    private void showDownloadManager(){
        LOG.debug("Showing the download manager.");
        if(!manager.isVisible()){
            manager.setLocationRelativeTo(getWindowAncestor());
            manager.pack();
            manager.setVisible(true);
        } else {
            manager.toFront();
        }
    }
    
    private class ShowDownloadManagerListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            showDownloadManager();
        }
    }
    
    private <T, K> void executeWorker(SwingWorker<T, K> worker){
        if(executorService == null){
            executorService = Executors.newCachedThreadPool();
        }
        LOG.debug("Submiting worker {}.", worker);
        executorService.submit(worker);
    }

    private JenkinsBuild getSelectedBuild() {
        Set<JenkinsBuild> builds = jenkinsJob.getBuilds();
        JenkinsBuild actualBuild = null;
        String sbn = jenkinsBuildPanel.getSelectedBuildNumber();
        for(JenkinsBuild b : builds){
            if(b.getBuildNumber().equals(sbn)){
                actualBuild = b;
                break;
            }
        }
        return actualBuild;
    }

    private static int ID = Integer.MIN_VALUE;
    
    private static synchronized int getNextDownloadID(){
        return ID++;
    }
    
    private File getBasicPathToArtifacts(){
        if(isJenkinJobSelected(false)){
            return FileUtils.getFile(downloadDir.getText(),
                jenkinsBuildPanel.getJobName());
        }
        return null;
    }
    
    private File getPathToArtifactsOfSelectedBuild(){
        if(isBuildSelected(false)){
            File basic = getBasicPathToArtifacts();
            return basic == null ? null : FileUtils.getFile(basic,
                        jenkinsBuildPanel.getSelectedBuildNumber());
        }
        return null;
    }
    
    private File getPathToArtifactsOfSelectedNode(){
        if(isNodeSelected(false)){
            File build = getPathToArtifactsOfSelectedBuild();
            return build == null ? null : FileUtils.getFile(build,
                        jenkinsBuildPanel.getSelectedXValue(),
                        jenkinsBuildPanel.getSelectedYValue());
        }
        return null;
    }
    
    private File getSummaryTotalsFile(File baseDirectory){
        return baseDirectory == null ? null : FileLoader.fullTextSearch(baseDirectory, "Summary_totals.txt");
    }
    
    /**
     * Gets and shows list of views or jobs from jenkins.
     * 
     * @author jdurani
     *
     */
    private class SelectJobViewWorker extends SwingWorker<List<String>, Void>{
        private static final int JOB = 0;
        private static final int VIEW = 1;
        
        private final int type;

        public SelectJobViewWorker(int type) {
            super();
            this.type = type;
        }
        
        @Override
        protected List<String> doInBackground() throws Exception {
            status.setStatus("Getting " + (type == JOB ? "jobs..." : "views..."));
            if(type == VIEW){
                return JenkinsManager.getJenkinsViews();
            } else {
                return JenkinsManager.getJenkinsJobs(viewName.getText());
            }
        }
        
        @Override
        protected void done() {
            try{
                status.setStatus("Getting done.");
                List<String> names = get();
                showDialog(names);
            } catch(InterruptedException | ExecutionException | CancellationException ex){
                Throwable t;
                if(ex instanceof ExecutionException){
                    t = ex.getCause();
                } else {
                    t = ex;
                }
                Utils.showMessageDialog((JFrame)getWindowAncestor(), Level.ERROR,
                        t.getMessage(), t);
            } finally {
                selectJobViewWorker = null;
            }
        }

        /**
         * Shows input dialog with options.
         * @param options
         */
        private void showDialog(List<String> options){
            LOG.debug("Options to select: {}", options);
            if(options == null || options.isEmpty()){
                Utils.showMessageDialog((JFrame)getWindowAncestor(), Level.DEBUG,
                        "Nothing to select.", null);
                return;
            }
            
            final JDialog dialog = new JDialog(getWindowAncestor());
            
            DefaultListModel<String> model = new DefaultListModel<>();
            
            Collections.sort(options);
            for(String s : options){
                model.addElement(s);
            }
            final JList<String> list = new JList<>(model);
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane listPane = Utils.getScrollPane(list);
            
            JButton okButton = new JButton("OK");
            okButton.addActionListener(new ActionListener() {
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(list.getSelectedIndex() == -1){
                        Utils.showMessageDialog((JFrame)getWindowAncestor(), Level.DEBUG,
                                "No item selected.", null);
                    } else {
                        String selectedObject = list.getSelectedValue();
                        if(type == JOB){
                            jobName.setText(selectedObject);
                            saveJobName.save();
                        } else {
                            viewName.setText(selectedObject);
                            saveViewName.save();
                        }
                        dialog.dispose();
                    }
                }
            });
            
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(new ActionListener() {
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    dialog.dispose();
                }
            });
            
            JPanel dialogPanel = new JPanel();
            
            GroupLayout gl = new GroupLayout(dialogPanel);
            gl.setAutoCreateContainerGaps(true);
            gl.setAutoCreateGaps(true);
            gl.setHorizontalGroup(gl.createParallelGroup()
                .addComponent(listPane)
                .addGroup(gl.createSequentialGroup()
                    .addComponent(okButton)
                    .addComponent(cancelButton)));
            gl.setVerticalGroup(gl.createSequentialGroup()
                .addComponent(listPane)
                .addGroup(gl.createParallelGroup()
                    .addComponent(okButton)
                    .addComponent(cancelButton)));
            gl.linkSize(okButton, cancelButton);
            dialogPanel.setLayout(gl);
            
            dialog.add(dialogPanel);
            dialog.setLocationRelativeTo(getWindowAncestor());
            dialog.setTitle("Select");
            dialog.pack();
            dialog.setVisible(true);
        }
    }
    
    private class ShowJobActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(!notRunning(showJobWorker, true)){
                return;
            }
            showJobWorker = new ShowJobWorker();
            executeWorker(showJobWorker);
        }
    }

    /**
     * Gets and sets {@link #jenkinsJob} from jenkins.
     * 
     * @author jdurani
     *
     */
    private class ShowJobWorker extends SwingWorker<JenkinsJob, Void>{
        @Override
        protected JenkinsJob doInBackground() throws Exception {
            removeJobInfoPanel();
            status.setStatus("Getting job...");
            return JenkinsManager.getJenkinsJob(viewName.getText(), jobName.getText());
        }
        
        @Override
        protected void done() {
            try{
                status.setStatus("Getting job done.");
                jenkinsJob = get();
                updateJobInfoPanel();
            } catch(InterruptedException | ExecutionException | CancellationException ex){
                jenkinsJob = null;
                Throwable t;
                if(ex instanceof ExecutionException){
                    t = ex.getCause();
                } else {
                    t = ex;
                }
                Utils.showMessageDialog((JFrame)getWindowAncestor(), Level.ERROR,
                        t.getMessage(), t);
            } finally {
                showJobWorker = null;
            }
        }
    }
    
    /**
     * Downloads all artifacts of node (active configuration).
     * 
     * @author jdurani
     *
     */
    private class DownloadAllArtifactsOfNodeActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(!isNodeSelected(true)){
                return;
            }
            int id = getNextDownloadID();
            String destFile = id + ".zip";
            Properties prop = new Properties();
            prop.put(DownloadArtifactsWorker.URL, jenkinsBuildPanel.getUrlOfSelectedNode());
            prop.put(DownloadArtifactsWorker.FILE, destFile);
            prop.put(DownloadArtifactsWorker.UNZIP_FILE, getPathToArtifactsOfSelectedNode().getAbsolutePath());
            prop.put(DownloadArtifactsWorker.FAIL, true);
            
            DownloadArtifactsWorker downloadArtifactsWorker = new DownloadArtifactsWorker(prop, id);
            executeWorker(downloadArtifactsWorker);
        }
    }

    /**
     * Downloads all artifacts of build.
     * 
     * @author jdurani
     *
     */
    private class DownloadAllArtifactsOfBuildActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            JenkinsBuild actualBuild = getSelectedBuild();
            
            File basePath = getPathToArtifactsOfSelectedBuild();
            for(JenkinsActiveConfiguration jac : actualBuild.getActiveConfigurations()){
                if(jac.getStatus() == JenkinsStatus.FAILURE
                        || jac.getStatus() == JenkinsStatus.SUCCESS
                        || jac.getStatus() == JenkinsStatus.UNSTABLE){
                    int id = getNextDownloadID();
                    File downloadDirFile = FileUtils.getFile(basePath, jac.getxValue(), jac.getyValue());
                    Properties prop = new Properties();
                    prop.put(DownloadArtifactsWorker.URL, jac.getUrl());
                    prop.put(DownloadArtifactsWorker.FILE, id + ".zip");
                    prop.put(DownloadArtifactsWorker.UNZIP_FILE, downloadDirFile.getAbsolutePath());
                    prop.put(DownloadArtifactsWorker.FAIL, false);
                    DownloadArtifactsWorker downloadArtifactsWorker = new DownloadArtifactsWorker(prop, id);
                    executeWorker(downloadArtifactsWorker);
                }
            }
        }
    }
    
    /**
     * Downloads selected artifacts of node (active configuration).
     * 
     * @author jdurani
     *
     */
    private class DownloadCustomArtifactsOfNodeActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO fill properties
            
            Properties props = new Properties();
            
            DownloadArtifactsWorker downloadArtifactsWorker = new DownloadArtifactsWorker(props, getNextDownloadID());
            executeWorker(downloadArtifactsWorker);
        }
    }

    /**
     * Downloads all files.
     * 
     * @author jdurani
     *
     */
    private class DownloadArtifactsWorker extends SwingWorker<Void, Void>{
        
        private static final String URL = "url";
        private static final String FILE = "file";
        private static final String FAIL = "fail.if.not.found";
        private static final String UNZIP_FILE = "unzip.file";
        private static final String ARTIFACTS_PATH = "path";
        
        private final Properties toDownload;
        private final int downloadID;
        
        private DownloadArtifactsWorker(Properties toDownload, int downloadID) {
            super();
            this.toDownload = toDownload;
            this.downloadID = downloadID;
            LOG.debug("Prepared to download {}.", this);
        } 
        
        @Override
        public String toString() {
            return this.downloadID + " - " + this.toDownload.get(URL);
        }
        
        @Override
        protected Void doInBackground() throws Exception {
            LOG.debug("Starting download of the artifacts.");
            downloadPublisher.add(downloadID, "Artifacts: " + toDownload.getProperty(URL));
            JenkinsManager.getArtifactsOfNode(
                    toDownload.getProperty(URL),
                    toDownload.getProperty(ARTIFACTS_PATH),
                    toDownload.getProperty(FILE),
                    downloadPublisher,
                    Boolean.valueOf(toDownload.getProperty(FAIL)),
                    downloadID);
            downloadPublisher.clear(downloadID);
            return null;
        }
        
        @Override
        protected void done() {
            try{
                get();
                downloadPublisher.setStatus(downloadID, "Unziping...");
                boolean failIfNotFound = Boolean.valueOf(toDownload.getProperty(FAIL));
                File unzipDir = new File(toDownload.getProperty(UNZIP_FILE));
                File zipFile = new File(toDownload.getProperty(FILE));
                int bufferSize = 8192;
                try(FileInputStream fis = new FileInputStream(zipFile);
                        ZipInputStream zis = new ZipInputStream(fis);
                        BufferedInputStream bis = new BufferedInputStream(zis, bufferSize)){
                    ZipEntry ze = null;
                    while((ze = zis.getNextEntry()) != null){
                        File newFile = new File(unzipDir, ze.getName());
                        LOG.trace("Zip entry {}", ze);
                        LOG.trace("New file: {}.", newFile);
                        if(ze.isDirectory()){
                            LOG.trace("Creating directory {} - {}.", newFile, newFile.mkdirs());
                        } else {
                            LOG.trace("Creating directory {} - {}.", newFile.getParentFile(), newFile.getParentFile().mkdirs());
                            try(FileOutputStream fos = new FileOutputStream(newFile);
                                    BufferedOutputStream bos = new BufferedOutputStream(fos, bufferSize)){
                                byte[] buffer = new byte[bufferSize];
                                for(int i; (i = bis.read(buffer)) != -1; ) {
                                    bos.write(buffer, 0, i);
                                }   
                            }
                        }
                     }
                } catch (IOException ex){
                    if(failIfNotFound){
                        Utils.showMessageDialog((JFrame)getWindowAncestor(), Level.ERROR,
                                "Error while unzipping file: " + ex.getMessage(), ex);
                    } else {
                        LOG.warn("Error while unzipping file: " + ex.getMessage(), ex);
                    }
                }
                zipFile.delete();
                downloadPublisher.setStatus(downloadID, "Unzipping done.");
            } catch(InterruptedException | ExecutionException | CancellationException ex){
                Throwable t;
                if(ex instanceof ExecutionException){
                    t = ex.getCause();
                } else {
                    t = ex;
                }
                if(t instanceof CancellationException){
                    LOG.info(t.getMessage());
                    downloadPublisher.setStatus(downloadID, t.getMessage());
                } else {
                    Utils.showMessageDialog((JFrame)getWindowAncestor(), Level.ERROR,
                        t.getMessage(), t);
                }
            }
            enableShowResults();
        }
    }

    private class ShowResultsActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            LOG.info("Showing test results from Jenkins");
        	if(!isJenkinJobSelected(true)
    	            || !isBuildSelected(true)
	                || !isNodeSelected(true)){
        	    return;
        	}
        	LOG.info("Vybrany run jex "+ jenkinsBuildPanel.getSelectedXValue());
        	LOG.info("Vybrany run jey "+ jenkinsBuildPanel.getSelectedYValue());
        	LOG.info("Vybrany run jeurl "+ jenkinsBuildPanel.getUrlOfSelectedNode());

            File jenkinsResults = getPathToArtifactsOfSelectedNode();
        	LOG.debug("Searching jenkins results at {}.", jenkinsResults.getAbsolutePath());
        	File summaryResults = getSummaryTotalsFile(jenkinsResults);
        	if(summaryResults == null){
        	    Utils.showMessageDialog((JFrame)getWindowAncestor(), Level.ERROR,
        	            "No test results have been found at the location:\n"+jenkinsResults.getAbsolutePath()+"\nYou must download the results before showing them.",
        	            null);
        	    return;
        	}
        	Settings.getInstance().setPathToTestResults(summaryResults.getParent());
        	JTabbedPane parent;
        	if (getParent() instanceof JTabbedPane) {
        	    parent= (JTabbedPane) getParent();
    		} else {
    			throw new RuntimeException("This panel must be in tabbed pane");
    		}
        	parent.setSelectedIndex(0);
        	RefreshResults.getRefreshButton().doClick();
        }
    }
    
    private class DownloadConsoleLogOfNodeActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(!isJenkinJobSelected(true)
                    || !isBuildSelected(true)
                    || !isNodeSelected(true)){
                return;
            }
            
            Properties props = new Properties();
            props.put(DownloadArtifactsWorker.URL, jenkinsBuildPanel.getUrlOfSelectedNode());
            props.put(DownloadArtifactsWorker.FILE, getPathToArtifactsOfSelectedNode().getAbsolutePath());
            props.put(DownloadArtifactsWorker.FAIL, true);
            
            DownloadLogWorker downloadLogWorker = new DownloadLogWorker(props, getNextDownloadID());
            executeWorker(downloadLogWorker);
        }
    }
    
    private class DownloadConsoleLogsOfBuildActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            JenkinsBuild actualBuild = getSelectedBuild();
            File basePath = getPathToArtifactsOfSelectedBuild();
            for(JenkinsActiveConfiguration jac : actualBuild.getActiveConfigurations()){
                File downloadDirFile = FileUtils.getFile(basePath,
                        jac.getxValue(),
                        jac.getyValue());
                Properties prop = new Properties();
                prop.put(DownloadArtifactsWorker.URL, jac.getUrl());
                prop.put(DownloadArtifactsWorker.FILE, downloadDirFile.getAbsolutePath());
                prop.put(DownloadArtifactsWorker.FAIL, false);
                DownloadLogWorker downloadLogWorker = new DownloadLogWorker(prop, getNextDownloadID());
                executeWorker(downloadLogWorker);
            }
        }
    }
    
    private class DownloadLogWorker extends SwingWorker<Void, Void>{
        
        private final Properties toDownload;
        private final int downloadID;
        private static final String CONSOLE_LOG = "consoleLog";
        
        private DownloadLogWorker(Properties toDownload, int downloadID) {
            super();
            this.toDownload = toDownload;
            this.downloadID = downloadID;
        }

        @Override
        protected Void doInBackground() throws Exception {
            LOG.debug("Starting download of the console log.");
            downloadPublisher.add(downloadID, "Console log: " + toDownload.getProperty(DownloadArtifactsWorker.URL));
            JenkinsManager.getConsoleLogOfNode(toDownload.getProperty(DownloadArtifactsWorker.URL),
                    toDownload.getProperty(DownloadArtifactsWorker.FILE) + File.separator + CONSOLE_LOG,
                    downloadPublisher,
                    Boolean.valueOf(toDownload.getProperty(DownloadArtifactsWorker.FAIL)),
                    downloadID);
            downloadPublisher.clear(downloadID);
            return null;
        }
        
        @Override
        protected void done() {
            try{
                get();
                downloadPublisher.setStatus(downloadID, "Downloading done.");
            } catch(InterruptedException | ExecutionException | CancellationException ex){
                Throwable t;
                if(ex instanceof ExecutionException){
                    t = ex.getCause();
                } else {
                    t = ex;
                }
                if(t instanceof CancellationException){
                    LOG.info(t.getMessage());
                    downloadPublisher.setStatus(downloadID, t.getMessage());
                } else {
                    Utils.showMessageDialog((JFrame)getWindowAncestor(), Level.ERROR,
                        t.getMessage(), t);
                }
            }
            enableShowLog();
        }
    }
    
    private class ShowLogOfNodeActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(!notRunning(showLogWorker, true)
                    || !isJenkinJobSelected(true)
                    || !isBuildSelected(true)
                    || !isNodeSelected(true)){
                return;
            }
            File logFile = FileUtils.getFile(getPathToArtifactsOfSelectedNode(),
                    DownloadLogWorker.CONSOLE_LOG);
            if(!logFile.exists()){
                Utils.showMessageDialog((JFrame)getWindowAncestor(), Level.WARN, "Log file not found. Use one of \"Download log...\" buttons.", null);
                return;
            }
            showLogWorker = new ShowLogWorker(logFile.getAbsolutePath());
            executeWorker(showLogWorker);
        }
    }
    
    private class ShowLogWorker extends SwingWorker<JTextArea, Void> {
        
        private final String logFile;
        private final String findActionKey = "strtSearch";
        
        private ShowLogWorker(String logFile) {
            super();
            this.logFile = logFile;
        }

        @Override
        protected JTextArea doInBackground() throws Exception{
            status.setStatus("Reading console log ...");
            try(FileReader reader = new FileReader(logFile);
                    BufferedReader br = new BufferedReader(reader)){
                final JTextArea area = new JTextArea();
                area.read(br, logFile);
                area.setEditable(false);
                area.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK), findActionKey);
                area.getActionMap().put(findActionKey, new FindInAreaAction(area));
                return area;
            }
        }
        
        @Override
        protected void done(){
            try{
                status.setStatus("Showing log...");
                JTextArea area = get();
                JPanel panel = new JPanel();
                JScrollPane pane = new JScrollPane(area);
                
                GroupLayout gl = new GroupLayout(panel);
                gl.setVerticalGroup(gl.createSequentialGroup()
                        .addComponent(pane, 500, 500, 1500));
                gl.setHorizontalGroup(gl.createSequentialGroup()
                        .addComponent(pane, 600, 600, 2500));
                gl.setAutoCreateContainerGaps(true);
                panel.setLayout(gl);
                JFrame consoleFrame = new JFrame();
                consoleFrame.add(panel);
                consoleFrame.setResizable(true);
                consoleFrame.setTitle("Console log - " + logFile);
                consoleFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                consoleFrame.pack();
                consoleFrame.setLocationRelativeTo(getWindowAncestor());
                consoleFrame.setVisible(true);
            } catch(InterruptedException | ExecutionException | CancellationException ex){
                Throwable t;
                if(ex instanceof ExecutionException){
                    t = ex.getCause();
                } else {
                    t = ex;
                }
                Utils.showMessageDialog((JFrame)getWindowAncestor(), Level.ERROR,
                        t.getMessage(), t);
            } finally {
                status.setStatus("Showing log done.");
                showLogWorker = null;
            }
        }
    }
    
    /**
     * Find action.
     * @author Juraj Duráni
     *
     */
    @SuppressWarnings("serial")
    private class FindInAreaAction extends AbstractAction{

        private final JTextArea area;
        private final String nextResultActionString = "findNext";
        private String pattern;
        
        private FindInAreaAction(JTextArea area) {
            this.area = area;
            area.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), nextResultActionString);
            area.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), nextResultActionString);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String newPattern = JOptionPane.showInputDialog(SwingUtilities.getWindowAncestor(area),
                    "What should I look for?\nEnter a java regular expression.\nPress ENTER for more results.", pattern);
            if(newPattern == null){
                LOG.debug("Nothing to search for.");
                return;
            }
            pattern = newPattern;
            LOG.debug("Searching for pattern: {}", pattern);
            area.getActionMap().put(nextResultActionString, null);
            FindNextMatchAction action = new FindNextMatchAction(area,
                    Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(area.getText()));
            area.getActionMap().put(nextResultActionString, action);
            action.actionPerformed(null);
            
        }
    }
    
    @SuppressWarnings("serial")
    private class FindNextMatchAction extends AbstractAction{
        
        private final JTextArea area;
        private final Matcher matcher;
        private boolean firstPass = true;
        
        
        private FindNextMatchAction(JTextArea area, Matcher matcher) {
            this.area = area;
            this.matcher = matcher;
        }

        @Override
        public void actionPerformed(ActionEvent e){
            boolean found = matcher.find();
            if(!found && !firstPass){
                LOG.debug("Reseting matcher.");
                matcher.reset();
                found = matcher.find();
            }
            firstPass = false;
            if(found){
                area.select(matcher.start(), matcher.end());
            } else {
                LOG.debug("Patter not found.");
            }
        }   
    }
    
    /**
     * Download publisher that shows download status in status panel.
     * 
     * @author jdurani
     *
     */
    private class StatusDownloadPublisher implements DownloadPublisher {
        
        private static final long DEFAULT_PUBLISH_INTERVAL = 500;
        private final String[] SUFIX_iB = {"B", "kiB", "MiB", "GiB", "TiB", "PiB", "EiB", "ZiB", "YiB"};
        private final String[] SUFIX_B = {"B", "kB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};
        
        private final Map<Integer, StatusPanel> statuses = new HashMap<>();
        private final Map<Integer, Long> lastSizes = new HashMap<>();
        
        @Override
        public long publishInterval() {
            return DEFAULT_PUBLISH_INTERVAL;
        }
        
        @Override
        public void publish(long downloaded, long objectSize, int downloadID) {
            boolean useIB = true;
            long downloadedSize = downloaded - lastSizes.get(downloadID).longValue();
            lastSizes.put(downloadID, downloaded);
            String rate = getSizeInBytes(downloadedSize * (1000.0 / DEFAULT_PUBLISH_INTERVAL), useIB);
            String downloadedSizeInBytes = getSizeInBytes(downloaded, useIB);
            setStatus(downloadID, new StringBuilder(downloadedSizeInBytes)
                    .append(" / ")
                    .append((objectSize < 0 ? "???" : getSizeInBytes((double)objectSize, useIB)))
                    .append("   [")
                    .append(rate)
                    .append("/s]").toString());
        }
        
        @Override
        public void clear(int downloadID){
            lastSizes.put(downloadID, 0L);
        }
        
        private void add(final int downloadID, String statusTitle){
            LOG.debug("Adding a new status to the download manager.");
            StatusPanel sp = new StatusPanel(statusTitle);
            statuses.put(downloadID, sp);
            lastSizes.put(downloadID, 0L);
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(new ActionListener() {
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    JenkinsManager.cancelActiveDownload(downloadID);
                }
            });
            manager.addDownloadItem(sp, cancelButton);
            showDownloadManager();
        }
        
        private void setStatus(int downloadID, String status){
            statuses.get(downloadID).setStatus(status);
        }
        
        private String getSizeInBytes(double d, boolean useIB){
            double divisor = useIB ? 1024.0 : 1000.0;
            int dividedNum = 0;
            while(Math.abs(d) > divisor){
                d /= divisor;
                dividedNum++;
            }
            d = Math.round(d * 100) / 100.0;
            return Double.toString(d) + (useIB ? SUFIX_iB[dividedNum] : SUFIX_B[dividedNum]);
        }
    }
}























