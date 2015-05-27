package qe.panels;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.apache.logging.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qe.entity.result.RefreshResults;
import qe.entity.settings.Settings;
import qe.jenkins.JenkinsActiveConfiguration;
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
 * @author jdurani
 */
public class JenkinsPanel extends JPanel {

    private static final long serialVersionUID = -8886425441947147863L;

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JenkinsPanel.class);
    
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
    
    private JenkinsBuildPanel jenkinsBuildPanel;
    private JScrollPane jenkinsBuildPane;
    
    private JButton downloadAllArtifactsOfNode;
    private JButton downloadAllArtifactsOfBuild;
    private JButton downloadCustomArtifactsOfNode;
    
    private final SaveToSettingsFileAction saveViewName = new SaveViewName();
    private final SaveToSettingsFileAction saveJobName = new SaveJobName();
    private final SaveToSettingsFileAction saveDownloadDir = new SaveDownloadDir();
    
    private final DownloadPublisher downloadPublisher = new StatusDownloadPublisher();
    
    private JenkinsJob jenkinsJob;
    
    private SelectJobViewWorker selectJobViewWorker;
    private ShowJobWorker showJobWorker;
    private DownloadArtifactsWorker downloadArtifactsWorker;
    
    /**
     * {@inheritDoc}
     */
    public JenkinsPanel(){
        super();
        init();
        LOGGER.info("Jenkis panel has been initialized.");
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
        initDefaultValues();
        
        GroupLayout gl = new GroupLayout(this);
        gl.setAutoCreateContainerGaps(true);
        gl.setAutoCreateGaps(true);
        gl.setHorizontalGroup(gl.createSequentialGroup()
            .addGroup(gl.createParallelGroup()
                .addComponent(showJobButton)
                .addComponent(showResults)
                .addComponent(downloadAllArtifactsOfNode)
                .addComponent(downloadAllArtifactsOfBuild)
                .addComponent(downloadCustomArtifactsOfNode))
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
                .addComponent(downloadAllArtifactsOfNode)
                .addComponent(downloadAllArtifactsOfBuild)
                .addComponent(downloadCustomArtifactsOfNode))
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
                downloadCustomArtifactsOfNode, showResults);
        
        setLayout(gl);
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
        jenkinsBuildPanel = new JenkinsBuildPanel();
        jenkinsBuildPane = Utils.getScrollPane(jenkinsBuildPanel);
    }

    /**
     * Initializes menu.
     */
    private void initMenu(){
        showJobButton = new JButton("Show selected job");
        showJobButton.addActionListener(new ShowJobActionListener());
        
        downloadAllArtifactsOfBuild = new JButton("Download all artifacts of build");
        downloadAllArtifactsOfBuild.addActionListener(new DownloadAllArtifactsOfBuildActionListener());
        
        downloadAllArtifactsOfNode = new JButton("Download all artifacts of node");
        downloadAllArtifactsOfNode.addActionListener(new DownloadAllArtifactsOfNodeActionListener());
        
        downloadCustomArtifactsOfNode = new JButton("Download custom artifacts of node");
        downloadCustomArtifactsOfNode.addActionListener(new DownloadCustomArtifactsOfNodeActionListener());
        
        showResults = new JButton("Show results");
        showResults.addActionListener(new ShowResultsActionListener());
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
            LOGGER.info("Setting jenkins view name to {}.", viewName.getText());
        }
    }
    
    private class SaveJobName implements SaveToSettingsFileAction {
        @Override
        public void save() {
            Settings.getInstance().setJenkinsJob(jobName.getText());
            LOGGER.info("Setting jenkins job name to {}.", jobName.getText());
        }
    }
    
    private class SaveDownloadDir implements SaveToSettingsFileAction {
        @Override
        public void save() {
            Settings.getInstance().setJenkinsDownloadDir(downloadDir.getText());
            LOGGER.info("Setting jenkins download directory to {}.", downloadDir.getText());
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
        if(!notRunning(selectJobViewWorker)){
            return;
        }
        selectJobViewWorker = new SelectJobViewWorker(type);
        selectJobViewWorker.execute();
    }
    
    private <T,K> boolean notRunning(SwingWorker<T, K> w){
        if(w != null){
            Utils.showMessageDialog((JFrame)getWindowAncestor(), Level.WARN, 
                    "Action already in progress.", null);
            return false;
        }
        return true;
    }
    

    private boolean needJenkinJob(){
        if(jenkinsJob == null){
            Utils.showMessageDialog((JFrame)getWindowAncestor(), Level.DEBUG,
                    "No jenkis job. Use \"Show selected job\" button.",
                    null);
            return false;
        }
        return true;
    }
    
    private boolean needBuildNumber(){
        if(jenkinsBuildPanel.getSelectedBuildNumber() == null){
            Utils.showMessageDialog((JFrame)getWindowAncestor(), Level.DEBUG,
                    "No build number selected.",
                    null);
            return false;
        }
        return true;
    }
    
    private boolean needNode(){
        if(jenkinsBuildPanel.getUrlOfSelectedNode() == null){
            Utils.showMessageDialog((JFrame)getWindowAncestor(), Level.DEBUG,
                    "No node selected.",
                    null);
            return false;
        }
        return true;
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
            LOGGER.debug("Options to select: {}", options);
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
            if(!notRunning(showJobWorker)){
                return;
            }
            showJobWorker = new ShowJobWorker();
            showJobWorker.execute();
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
     * Checks if all required information have been set.
     *  
     * @return
     */
    private boolean checkRequiredJenkinsSettingsBeforeDownload(){
        if(!notRunning(downloadArtifactsWorker)){
            return false;
        }
        if(!needJenkinJob()){
            return false;
        }
        if(!needBuildNumber()){
            return false;
        }
        File downloadDirFile = new File(downloadDir.getText());
        if(downloadDirFile.exists()){
            if(!downloadDirFile.isDirectory()){
                Utils.showMessageDialog((JFrame)getWindowAncestor(), Level.WARN,
                        "Download directory is not a directory.",
                        null);
                return false;
            }
            File[] dirContent = downloadDirFile.listFiles();
            if(dirContent != null && dirContent.length != 0){
                int option = JOptionPane.showConfirmDialog(getWindowAncestor(),
                                "Download direcotry is not empty. Some files may be overridden."
                                + System.lineSeparator() + System.lineSeparator()
                                + "Would you like to continue?",
                        "Download directory",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if(option != JOptionPane.YES_OPTION){
                    return false;
                }
            }
        }
        return true;
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
            if(!checkRequiredJenkinsSettingsBeforeDownload()){
                return;
            }
            if(!needNode()){
                return;
            }
            String destFile = "1.zip";
            File downloadDirFile = new File(downloadDir.getText(),
                    jenkinsBuildPanel.getJobName() + File.separator
                    + jenkinsBuildPanel.getSelectedBuildNumber() + File.separator
                    + jenkinsBuildPanel.getSelectedXValue() + File.separator
                    + jenkinsBuildPanel.getSelectedYValue());
            Properties prop = new Properties();
            prop.put(DownloadArtifactsWorker.URL, jenkinsBuildPanel.getUrlOfSelectedNode());
            prop.put(DownloadArtifactsWorker.FILE, destFile);
            prop.put(DownloadArtifactsWorker.UNZIP_FILE, downloadDirFile.getAbsolutePath());
            prop.put(DownloadArtifactsWorker.FAIL, true);
            
            List<Properties> props = new ArrayList<>();
            props.add(prop);
            downloadArtifactsWorker = new DownloadArtifactsWorker(props);
            downloadArtifactsWorker.execute();
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
            if(!checkRequiredJenkinsSettingsBeforeDownload()){
                return;
            }
            Set<JenkinsBuild> builds = jenkinsJob.getBuilds();
            JenkinsBuild actualBuild = null;
            String sbn = jenkinsBuildPanel.getSelectedBuildNumber();
            for(JenkinsBuild b : builds){
                if(b.getBuildNumber().equals(sbn)){
                    actualBuild = b;
                    break;
                }
            }
            List<Properties> props = new ArrayList<>();
            String basePath = downloadDir.getText() + File.separator
                    + jenkinsBuildPanel.getJobName() + File.separator
                    + actualBuild.getBuildNumber();
            int i = 0;
            for(JenkinsActiveConfiguration jac : actualBuild.getActiveConfigurations()){
                File downloadDirFile = new File(basePath,
                        jac.getxValue() + File.separator + jac.getyValue());
                Properties prop = new Properties();
                prop.put(DownloadArtifactsWorker.URL, jac.getUrl());
                prop.put(DownloadArtifactsWorker.FILE, (i++) + ".zip");
                prop.put(DownloadArtifactsWorker.UNZIP_FILE, downloadDirFile.getAbsolutePath());
                prop.put(DownloadArtifactsWorker.FAIL, false);
                props.add(prop);
            }
            downloadArtifactsWorker = new DownloadArtifactsWorker(props);
            downloadArtifactsWorker.execute();
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
            if(!checkRequiredJenkinsSettingsBeforeDownload()){
                return;
            }
            // TODO fill properties
            
            List<Properties> props = new ArrayList<>();
            
            downloadArtifactsWorker = new DownloadArtifactsWorker(props);
            downloadArtifactsWorker.execute();
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
        
        private final List<Properties> toDownload;
        
        private DownloadArtifactsWorker(List<Properties> toDownload) {
            super();
            this.toDownload = toDownload;
        } 
        
        @Override
        protected Void doInBackground() throws Exception {
            // TODO - parallel downloads
            // TODO - cancel button
            for(Properties p : toDownload){
                JenkinsManager.getArtifactsOfNode(
                        p.getProperty(URL),
                        p.getProperty(ARTIFACTS_PATH),
                        p.getProperty(FILE),
                        downloadPublisher,
                        Boolean.valueOf(p.getProperty(FAIL)));
                downloadPublisher.clear();
            }
            return null;
        }
        
        @Override
        protected void done() {
            try{
                status.setStatus("Downloading done.");
                get();
                status.setStatus("Unziping...");
                for(Properties p : toDownload){
                    boolean failIfNotFound = Boolean.valueOf(p.getProperty(FAIL));
                    File unzipDir = new File(p.getProperty(UNZIP_FILE));
                    File zipFile = new File(p.getProperty(FILE));
                    try(FileInputStream fis = new FileInputStream(zipFile);
                            ZipInputStream zis = new ZipInputStream(fis);
                            BufferedInputStream bis = new BufferedInputStream(zis)){
                        ZipEntry ze = zis.getNextEntry();
                        while(ze!=null){
                            File newFile = new File(unzipDir, ze.getName());
                            status.setStatus("File unzip: " + newFile.getAbsolutePath());
                            new File(newFile.getParent()).mkdirs();
                            try(FileOutputStream fos = new FileOutputStream(newFile);
                                    BufferedOutputStream bos = new BufferedOutputStream(fos)){       
                                for(int i; (i = bis.read()) != -1; ) {
                                    bos.write(i);
                                }   
                            }
                            ze = zis.getNextEntry();
                         }
                    } catch (IOException ex){
                        if(failIfNotFound){
                            Utils.showMessageDialog((JFrame)getWindowAncestor(), Level.ERROR,
                                    "Error while unzipping file: " + ex.getMessage(), ex);
                        } else {
                            LOGGER.warn("Error while unzipping file: " + ex.getMessage());
                        }
                    }
                    zipFile.delete();
                }
                status.setStatus("Unzipping done.");
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
                downloadArtifactsWorker = null;
            }
        }
    }

    
    private class ShowResultsActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("asdfasfasf");
        	LOGGER.info("Showing test results from Jenkins");
        	if(!needJenkinJob()){
    	        return;
	        }
        	if(!needBuildNumber()){
        	    return;
            }
        	LOGGER.info("Vybrany run jex "+ jenkinsBuildPanel.getSelectedXValue());
        	LOGGER.info("Vybrany run jey "+ jenkinsBuildPanel.getSelectedYValue());
        	LOGGER.info("Vybrany run jeurl "+ jenkinsBuildPanel.getUrlOfSelectedNode());
        	Set<JenkinsBuild> builds = jenkinsJob.getBuilds();
            JenkinsBuild actualBuild = null;
            String sbn = jenkinsBuildPanel.getSelectedBuildNumber();
            for(JenkinsBuild b : builds){
                if(b.getBuildNumber().equals(sbn)){
                    actualBuild = b;
                    break;
                }
            }
            if(jenkinsBuildPanel.getSelectedXValue() == null || jenkinsBuildPanel.getSelectedYValue()==null){
                Utils.showMessageDialog((JFrame)getWindowAncestor(), Level.ERROR,"No build run is selected",null);
                return;
            }
        	String basePath = downloadDir.getText() + File.separator
        	        + jenkinsBuildPanel.getJobName() + File.separator
                    + actualBuild.getBuildNumber();
        	File jenkinsResults = new File(basePath,jenkinsBuildPanel.getSelectedXValue() + File.separator + jenkinsBuildPanel.getSelectedYValue());
        	LOGGER.debug("Searching jenkins results at "+jenkinsResults.getAbsolutePath());
        	File summaryResults=FileLoader.fullTextSearch(jenkinsResults, "Summary_totals.txt");
        	if(summaryResults ==null){
        	    Utils.showMessageDialog((JFrame)getWindowAncestor(), Level.ERROR,"No test results have been found at the location:\n"+jenkinsResults.getAbsolutePath()+"\nYou must download the results before showing them.",null);
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
    
    /**
     * Download publisher that shows download status in status panel.
     * 
     * @author jdurani
     *
     */
    private class StatusDownloadPublisher implements DownloadPublisher {
        private volatile long lastSize = 0;
        
        private static final long DEFAULT_PUBLISH_INTERVAL = 500;
        private final String[] SUFIX_iB = {"B", "kiB", "MiB", "GiB", "TiB", "PiB", "EiB", "ZiB", "YiB"};
        private final String[] SUFIX_B = {"B", "kB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};
        
        @Override
        public long publishInterval() {
            return DEFAULT_PUBLISH_INTERVAL;
        }
        
        @Override
        public void publish(long downloaded, long objectSize) {
            boolean useIB = true;
            long downloadedSize = downloaded - lastSize;
            lastSize = downloaded;
            String rate = getSizeInBytes(downloadedSize * (1000.0 / DEFAULT_PUBLISH_INTERVAL), useIB);
            String downloadedSizeInBytes = getSizeInBytes(downloaded, useIB);
            status.setStatus(new StringBuilder(downloadedSizeInBytes)
                    .append(" / ")
                    .append((objectSize < 0 ? "???" : getSizeInBytes((double)objectSize, useIB)))
                    .append("   [at ")
                    .append(rate)
                    .append("/s]").toString());
        }
        
        @Override
        public void clear(){
            lastSize = 0;
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























