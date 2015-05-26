package qe.jenkins;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qe.exception.JenkinsException;
import qe.jenkins.JenkinsActiveConfiguration.JenkinsStatus;

/**
 * Utilities for jenkins' XML API.
 * 
 * @author jdurani
 *
 */
class JenkinsXMLAPIPUtils {
   
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JenkinsXMLAPIPUtils.class);
    
    /**
     * Private constructor - utils class.
     */
    private JenkinsXMLAPIPUtils() {}
    
    /**
     * Returns all view names from document {@code doc}.
     * 
     * @param doc
     * @return
     */
    static List<String> getViews(Document doc){
        LOGGER.debug("Getting jenkins view names from document {}.", doc.toString());
        return getTextOfElements(doc, "view > name");
    }
    
    /**
     * Returns all job names from document {@code doc}.
     * 
     * @param doc
     * @return
     */
    static List<String> getJobs(Document doc){
        LOGGER.debug("Getting jenkins job names from document {}.", doc.toString());
        return getTextOfElements(doc, "job > name");
    }
    
    /**
     * Builds and returns and instance of {@link JenkinsJob} from document {@code doc}.
     * 
     * @param doc
     * @return
     * @throws JenkinsException
     */
    static JenkinsJob getJob(Document doc) throws JenkinsException{
        LOGGER.debug("Getting jenkins job from document {}.", doc.toString());
        //we support only matrix projects
        JenkinsJob job = new JenkinsJob();
        String jobUrl = getTextOfElements(doc, "matrixProject > url").get(0);
        String jobName = getTextOfElements(doc, "matrixProject > name").get(0);
        job.setUrl(jobUrl);
        job.setName(jobName);
        Elements buildElements = doc.select("matrixProject > build");
        for(Element buildElem : buildElements){
            String number = buildElem.select("number").get(0).text();
            String buildUrl = buildElem.select("url").get(0).text();
            
            Elements runs = buildElem.select("run");
            JenkinsBuild build = new JenkinsBuild();
            build.setBuildNumber(number);
            build.setUrl(buildUrl);
            boolean first = true;
            for(Element run : runs){
                String runURL = run.select("url").get(0).ownText();
                String nodeId = runURL.substring(jobUrl.length(), runURL.length() - number.length() - 2);
                int idx1 = nodeId.indexOf('=');
                int idx2 = nodeId.lastIndexOf('=');
                if(idx1 == -1 || idx2 == idx1){
                    throw new JenkinsException("Job label has unexpected name (less than 2 occurrences of \"=\"): " + nodeId);
                }
                int idx3 = nodeId.indexOf(",");
                if(idx3 == -1){
                    throw new JenkinsException("Job label has unexpected name (no \",\"): " + nodeId);
                }
                String xLabel = nodeId.substring(0, idx1);
                String yLabel = nodeId.substring(idx3 + 1, idx2);
                String xValue = nodeId.substring(idx1 + 1, idx3);
                String yValue = nodeId.substring(idx2 + 1, nodeId.length());
                if(first){
                    first = false;
                    build.setxLabel(xLabel);
                    build.setyLabel(yLabel);
                }
                JenkinsActiveConfiguration activeConfiguration = new JenkinsActiveConfiguration();
                activeConfiguration.setUrl(runURL);
                activeConfiguration.setxValue(xValue);
                activeConfiguration.setyValue(yValue);
                
                if(Boolean.valueOf(run.select("building").get(0).ownText())){
                    activeConfiguration.setStatus(JenkinsStatus.BUILDING);
                } else {
                    activeConfiguration.setStatus(JenkinsStatus.valueOf(run.select("result").get(0).ownText()));
                }
                try{
                    build.addActiveConfiguration(activeConfiguration);
                } catch (JenkinsException ex){
                    LOGGER.warn("Exception while adding active configuration: " + ex.getMessage());
                }
            }
            job.addBuild(build);
        }
        return job;
    }
    
    /**
     * Returns list of texts of tags in document {@code doc} that fits {@code cssQuery}.
     * 
     * @param doc
     * @param cssQuery
     * @return
     */
    private static List<String> getTextOfElements(Element doc, String cssQuery){
        List<String> texts = new ArrayList<>();
        for(Element e : doc.select(cssQuery)){
            texts.add(e.ownText());
        }
        return texts;
    }
}

















