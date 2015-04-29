package qe.panels;

import java.awt.Font;
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
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EtchedBorder;

import org.apache.logging.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qe.entity.settings.Settings;
import qe.jenkins.JenkinsActiveConfiguration;
import qe.jenkins.JenkinsBuild;
import qe.jenkins.JenkinsJob;
import qe.jenkins.JenkinsManager;
import qe.jenkins.JenkinsManager.DownloadPublisher;
import qe.panels.SettingsPanel.SaveToSettingsFileAction;
import qe.utils.Utils;

/**
 * This class represents a {@link JPanel} for jenkins.
 * 
 * @author jdurani
 *
 */
public class JenkinsPanel extends JPanel {

    private static final long serialVersionUID = -8886425441947147863L;

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JenkinsPanel.class);
    
    private JTextField status;
    private JLabel statusLabel;
    
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
                .addComponent(downloadAllArtifactsOfNode)
                .addComponent(downloadAllArtifactsOfBuild)
                .addComponent(downloadCustomArtifactsOfNode))
            .addGroup(gl.createParallelGroup()
                .addGroup(gl.createParallelGroup()
                    .addComponent(statusLabel)
                    .addComponent(status))
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
                .addComponent(downloadAllArtifactsOfNode)
                .addComponent(downloadAllArtifactsOfBuild)
                .addComponent(downloadCustomArtifactsOfNode))
            .addGroup(gl.createSequentialGroup()
                .addGroup(gl.createSequentialGroup()
                    .addComponent(statusLabel)
                    .addComponent(status, 35, 35, 35))
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
        gl.linkSize(showJobButton, downloadAllArtifactsOfBuild, downloadAllArtifactsOfNode, downloadCustomArtifactsOfNode);
        
        setLayout(gl);
    }
    
    /**
     * Initializes the status. 
     */
    private void initStatus(){
        status = new JTextField();
        status.setEditable(false);
        status.setFont(status.getFont().deriveFont(Font.BOLD, 20));
        status.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        statusLabel = new JLabel("Status");
    }

    /**
     * Sets status to status panel.
     * 
     * @param status
     */
    private void setStatus(String status){
        this.status.setText(status);
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
        if(selectJobViewWorker != null){
            JOptionPane.showMessageDialog(getWindowAncestor(),
                    "Select action already in progress.",
                    "Select Job/View",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        selectJobViewWorker = new SelectJobViewWorker(type);
        selectJobViewWorker.execute();
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
            setStatus("Getting " + (type == JOB ? "jobs..." : "views..."));
            if(type == VIEW){
                return JenkinsManager.getJenkinsViews();
            } else {
                return JenkinsManager.getJenkinsJobs(viewName.getText());
            }
        }
        
        @Override
        protected void done() {
            try{
                setStatus("Getting done.");
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
                JOptionPane.showMessageDialog(getWindowAncestor(), "Nothing to select.", "Select", JOptionPane.INFORMATION_MESSAGE);
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
                        JOptionPane.showMessageDialog(getWindowAncestor(), "No item selected.", "Select", JOptionPane.INFORMATION_MESSAGE);
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
            if(showJobWorker != null){
                JOptionPane.showMessageDialog(getWindowAncestor(),
                        "Show job action already in progress.",
                        "Show Job",
                        JOptionPane.WARNING_MESSAGE);
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
            setStatus("Getting job...");
            return JenkinsManager.getJenkinsJob(viewName.getText(), jobName.getText());
        }
        
        @Override
        protected void done() {
            try{
                setStatus("Getting job done.");
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
        if(downloadArtifactsWorker != null){
            JOptionPane.showMessageDialog(getWindowAncestor(),
                    "Download action already in progress.",
                    "Download",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if(jenkinsJob == null){
            JOptionPane.showMessageDialog(getWindowAncestor(),
                    "No jenkis job. Use \"Show selected job\" button.",
                    "Jenkins job", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        if(jenkinsBuildPanel.getSelectedBuildNumber() == null){
            JOptionPane.showMessageDialog(getWindowAncestor(),
                    "No build number selected.",
                    "Build number", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        File downloadDirFile = new File(downloadDir.getText());
        if(downloadDirFile.exists()){
            if(!downloadDirFile.isDirectory()){
                JOptionPane.showMessageDialog(getWindowAncestor(),
                        "Download directory is not a directory.",
                        "Download directory", JOptionPane.INFORMATION_MESSAGE);
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
            if(jenkinsBuildPanel.getUrlOfSelectedNode() == null){
                JOptionPane.showMessageDialog(getWindowAncestor(),
                        "No node selected.",
                        "Node", JOptionPane.INFORMATION_MESSAGE);
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
                setStatus("Downloading done.");
                get();
                setStatus("Unziping...");
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
                            setStatus("File unzip: " + newFile.getAbsolutePath());
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
                setStatus("Unzipping done.");
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
            setStatus(new StringBuilder(downloadedSizeInBytes)
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























