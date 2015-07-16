package qe.jenkins;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.io.FileUtils;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qe.exception.JenkinsException;

/**
 * Utilities for jenkis API invoking.
 * 
 * @author jdurani
 *
 */
public class JenkinsManager {
    
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JenkinsManager.class);
    
    /**
     * Basic jenkins URL
     */
    private static final String JENKINS_URL = "http://jenkins.mw.lab.eng.bos.redhat.com/hudson/";
    /**
     * Type of API we support.
     */
    private static final String API_TYPE = "/api/xml/";
    /**
     * The extension for the URL to download the artifacts. 
     */
    private static final String ZIP = "/*zip*/tmp.zip";
    /**
     * The tree argument.
     */
    private static final String TREE= "?tree=";
    
    /**
     * Private constructor - all methods are static.
     */
    private JenkinsManager(){};
    
    /**
     * Returns the list of available views on jenkins.
     * 
     * @return
     * @throws JenkinsException if an error occurs during fetching an XML document
     */
    public static List<String> getJenkinsViews() throws JenkinsException{
        String url = addTree(new StringBuilder(JENKINS_URL), "views[name]").toString();
        Document doc = getDocument(url);
        return JenkinsXMLAPIPUtils.getViews(doc);
    }
    
    /**
     * Returns the list of available jobs of view.
     * 
     * @param view name of view
     * @return
     * @throws JenkinsException if an error occurs during fetching an XML document
     */
    public static List<String> getJenkinsJobs(String view) throws JenkinsException{
        check(view, "View cannot be empty.");
        String url = addTree(
                new StringBuilder(JENKINS_URL)
                    .append("view/")
                    .append(view), "jobs[name]")
                .toString();
        Document doc = getDocument(url);
        return JenkinsXMLAPIPUtils.getJobs(doc);
    }
    
    /**
     * Returns an instance of {@link JenkinsJob} for selected job.
     * 
     * @param view name of view
     * @param job name of job
     * @return
     * @throws JenkinsException if an error occurs during fetching an XML document
     */
    public static JenkinsJob getJenkinsJob(String view, String job) throws JenkinsException{
        check(view, "View cannot be empty.");
        check(job, "Job cannot be empty.");
        String url = addTree(
                new StringBuilder(JENKINS_URL)
                .append("view/")
                .append(view)
                .append("/job/")
                .append(job), "url,name,builds[number,url,building,result,runs[building,result,url]]")
            .toString();
        Document doc = getDocument(url);
        return JenkinsXMLAPIPUtils.getJob(doc);
    }
    
    /**
     * Downloads selected artifacts of active configuration. 
     *  
     * @param url the URL of active configuration 
     * @param artifactsPath path to selected artifacts (on jenkins)
     * @param destFile destination file name (on local file system)
     * @param publisher download publisher - to publish the download status
     * @param failIfNotFound if true, an exception will be thrown if required 
     *      artifacts are not found at specified URL
     * @throws JenkinsException if specified artifacts are not found
     * @throws IOException download or storing to local file system
     */
    public static void getArtifactsOfNode(String url, String artifactsPath,
            String destFile, DownloadPublisher publisher, boolean failIfNotFound) 
            throws JenkinsException, IOException{
        check(url, "URL cannot be empty.");
        check(destFile, "Destination file cannot be empty.");
        StringBuilder urlBuilder = new StringBuilder(url)
                .append("/artifact/");
        if(artifactsPath != null && !artifactsPath.isEmpty()){
            urlBuilder.append(artifactsPath);
        }
        addZip(urlBuilder);
        downloadFile(urlBuilder.toString(), destFile, publisher, failIfNotFound);        
    }
    
    /**
     * 
     * @param url the URL of active configuration
     * @param destFile destination file name (on local file system)
     * @param publisher download publisher - to publish the download status
     * @param failIfNotFound if true, an exception will be thrown if required 
     *      artifacts are not found at specified URL
     * @throws JenkinsException if specified artifacts are not found
     * @throws IOException download or storing to local file system
     */
    public static void getConsoleLogOfNode(String url, String destFile, final DownloadPublisher publisher, boolean failIfNotFound)
            throws JenkinsException, IOException{
        check(url, "URL cannot be empty");
        check(destFile, "Destination file cannot be empty.");
        StringBuilder urlBuilder = new StringBuilder(url).append("/consoleText/");
        downloadFile(urlBuilder.toString(), destFile, publisher, failIfNotFound);
    }
    
    /**
     * Downloads file from URL and stores it to local file system in file {@code destFile}. 
     * 
     * @param url the URL of file
     * @param destFile local file
     * @param publisher download publisher
     * @param failIfNotFound if true, an exception will be thrown if required 
     *      artifacts are not found at specified URL
     * @throws JenkinsException if specified file is not found
     * @throws IOException download or storing to local file system
     */
    private static void downloadFile(String url, String destFile, final DownloadPublisher publisher, boolean failIfNotFound) throws JenkinsException, IOException{
        LOGGER.info("Downloading file: {} to {}.", url, destFile);
        URL u = new URL(url);
        URLConnection con = u.openConnection();
        final long contentLength = con.getContentLengthLong();
        File destFileFile = new File(destFile);
        if(!destFileFile.exists()){
            File parent = destFileFile.getParentFile();
            if(parent != null){
                FileUtils.forceMkdir(parent);
            }
            destFileFile.createNewFile();
        }
        try(InputStream is = con.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                FileOutputStream fos = new FileOutputStream(destFile, false);
                BufferedOutputStream bos = new BufferedOutputStream(fos)){
            final DownloadTimerTask task = new DownloadTimerTask(contentLength, publisher);
            Timer timer = new Timer();
            timer.schedule(task, 0, publisher.publishInterval());
            for(int i; (i = bis.read()) != -1;){
                bos.write(i);
                task.size++;
            }
            timer.cancel();
            timer.purge();
        } catch (FileNotFoundException ex){
            if(failIfNotFound){
                throw new JenkinsException("No artifacts found.", ex);
            } else {
                LOGGER.warn("Artifact not found: " + ex.getMessage());
            }
        }
    }
    
    /**
     * Timer task - publishes download status.
     * 
     * @author jdurani
     *
     */
    private static class DownloadTimerTask extends TimerTask {
        private long size = 0;
        private final long contentLength;
        private final DownloadPublisher publisher;
        
        private DownloadTimerTask(long contentLength, DownloadPublisher publisher) {
            super();
            this.contentLength = contentLength;
            this.publisher = publisher;
        }

        @Override
        public void run() {
            publisher.publish(size, contentLength);
        }
    }
    
    /**
     * The download publisher - publishes download info.
     * 
     * @author jdurani
     *
     */
    public static interface DownloadPublisher{
        /**
         * Publishes download info.
         * 
         * @param downloaded downloaded size
         * @param objectSize object's size
         */
        void publish(long downloaded, long objectSize);
        
        /**
         * Returns publish interval.
         * 
         * @return
         */
        long publishInterval();
        
        /**
         * Clears this publisher.
         */
        void clear();
    }
    
    /**
     * Returns document from URL using HTTP GET method.
     * 
     * @param url
     * @return
     * @throws JenkinsException
     */
    private static Document getDocument(String url) throws JenkinsException{
        return fetchDocument(url, Connection.Method.GET);
    }
    
    /**
     * Fetches response from the URL and parses it to {@link Document}
     * 
     * @param url
     * @param method
     * @return
     * @throws JenkinsException
     */
    private static Document fetchDocument(String url, Method method) throws JenkinsException{
        try{
            Response res = fetchResponse(url, method);
            LOGGER.debug("Parsing response.");
            return res.parse();
        } catch (IOException t){
            String message = "Exception while parsing document: " + t.getMessage();
            LOGGER.error(message);
            throw new JenkinsException(message, t);
        }
    }
    
    /**
     * Fetches response from the URL.
     * 
     * @param url
     * @param method
     * @return
     * @throws JenkinsException
     */
    private static Response fetchResponse(String url, Method method) throws JenkinsException{
        try{
            LOGGER.info("Fetching response from {} using HTTP method {}", url, method);
            Response res = null;
            IOException lastEx = null;
            for(int i = 1; i <= 3; i++){
                try{
                    res = Jsoup.connect(url)
                            .parser(Parser.xmlParser())
                            .method(method)
                            .maxBodySize(0)
                            .timeout(20_000)
                            .execute();
                } catch (IOException ex){
                    lastEx = ex;
                    LOGGER.warn("Exception while fetching document [try: " + i + "]: " + ex.getMessage());
                }
            }   
            if(res == null){
                throw lastEx;
            }
            LOGGER.debug("Response content type: {}", res.contentType());
            return res;
        } catch (Throwable t){
            String message = "Throwable while fetching document: " + t.getMessage();
            LOGGER.error(message);
            throw new JenkinsException(message, t);
        }
    }
    
    /**
     * Adds ZIP extension to builder (expects that builder contains the URL).
     * @param b
     * @return
     */
    private static StringBuilder addZip(StringBuilder b){
        return b.append(ZIP);
    }
    
    /**
     * Adds tree URL argument
     * 
     * @param b the StringBuilder
     * @param treePath path for tree argument 
     * @return {@code <builder>/api/xml/?tree=<treePath>}
     */
    private static StringBuilder addTree(StringBuilder b, String treePath){
        if(treePath == null){
            return b;
        }
        return b.append(API_TYPE).append(TREE).append(treePath);
    }
    
    /**
     * Checks if {@code param} is non-null and non-empty.
     * 
     * @param param
     * @param errMessage
     * @throws JenkinsException if {@code param} is null or empty
     */
    private static void check(String param, String errMessage) throws JenkinsException{
        if(param == null || param.isEmpty()){
            throw new JenkinsException(errMessage);
        }
    }
}














