package qe.jenkins;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qe.exception.JenkinsException;
import qe.jenkins.JenkinsActiveConfiguration.JenkinsStatus;

/**
 * The jenkins build. Contains active configurations - configurations that was run during the build.
 * 
 * <p>
 * Implements comparable - uses build number.
 * </p>
 *  
 * @author jdurani
 *
 */
public class JenkinsBuild implements Comparable<JenkinsBuild>{

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(JenkinsBuild.class);
    
    /**
     * Number of this build.
     */
    private String buildNumber;
    /**
     * Label of X-axis.
     */
    private String xLabel;
    /**
     * Label of Y-axis.
     */
    private String yLabel;
    /**
     * The URl of this build.
     */
    private String url;
    /**
     * The status of build.
     */
    private JenkinsStatus status = JenkinsStatus.NONE;
    /**
     * The active configurations.
     */
    private Set<JenkinsActiveConfiguration> activeConfigurations = new HashSet<>();
    /**
     * The pending active configurations.
     */
    private Set<JenkinsActiveConfiguration> pendingActiveConfigurations = new HashSet<>();
    
    /**
     * Returns build number.
     * 
     * @return
     */
    public String getBuildNumber() {
        return buildNumber;
    }
    
    /**
     * Sets build number.
     * 
     * @param buildNumber
     */
    public void setBuildNumber(String buildNumber) {
        this.buildNumber = buildNumber;
    }
    
    /**
     * Returns label for X-axis.
     * 
     * @return
     */
    public String getxLabel() {
        return xLabel;
    }
    
    /**
     * Sets label for X-axis.
     * 
     * @param xLabel
     */
    public void setxLabel(String xLabel) {
        this.xLabel = xLabel;
    }
    
    /**
     * Returns label for Y-axis.
     * 
     * @return
     */
    public String getyLabel() {
        return yLabel;
    }
    
    /**
     * Sets label for Y-axis.
     * 
     * @param yLabel
     */
    public void setyLabel(String yLabel) {
        this.yLabel = yLabel;
    }
    
    /**
     * Returns status of this build.
     * 
     * @return
     */
    public JenkinsStatus getStatus() {
        return status;
    }

    /**
     * Sets status of this build.
     * 
     * @param status
     */
    public void setStatus(JenkinsStatus status) {
        this.status = status;
    }

    /**
     * Returns active configurations of this build.
     * 
     * @return
     */
    public Set<JenkinsActiveConfiguration> getActiveConfigurations() {
        return activeConfigurations;
    }
    
    /**
     * Returns pending active configurations of this build.
     * 
     * @return
     */
    public Set<JenkinsActiveConfiguration> getPendingActiveConfigurations() {
        return pendingActiveConfigurations;
    }

    /**
     * Returns URL of this build.
     * 
     * @return
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets URL for this build.
     * 
     * @param url
     */
    public void setUrl(String url) {
        if(url != null && !url.endsWith("/")){
            url += "/";
        }
        this.url = url;
    }
    
    /**
     * Adds an active configuration to this build. Checks if the active configuration is part of this  build.
     * @param jac
     * @throws JenkinsException
     */
    public void addActiveConfiguration(JenkinsActiveConfiguration jac) throws JenkinsException{
        if(jac == null){
            return;
        }
        String jacUrl = jac.getUrl();
        if(xLabel == null
                || yLabel == null
                || buildNumber == null
                || url == null
                || jacUrl == null){
            throw new JenkinsException("Unable to check validity of active configuration for this build.");
        }
        String jobUrl = this.url.substring(0, this.url.length() - buildNumber.length() - 1);
        if(!jacUrl.startsWith(jobUrl)){
            throw new JenkinsException("Active configuration is not part of build's job.");
        }
        if(!jacUrl.endsWith("/" + buildNumber + "/")){
            pendingActiveConfigurations.add(jac);
            return;
        }
        if(!jacUrl.contains(("/" + xLabel + "="))){
            throw new JenkinsException("Active configuration and build have differenet X-label.");
        }
        if(!jacUrl.contains(("," + yLabel + "="))){
            throw new JenkinsException("Active configuration and build have differenet Y-label.");
        }
        
        activeConfigurations.add(jac);
    }
    
    public void setStatusOfPendingConfigurations(){
        if(status == JenkinsStatus.BUILDING){
            setStatusOfPendingConfigurations(JenkinsStatus.PENDING);
        } else if(status == JenkinsStatus.ABORTED){
            setStatusOfPendingConfigurations(JenkinsStatus.ABORTED);
        } else {
            if(!pendingActiveConfigurations.isEmpty()){
                // TODO only warning?
                // TODO we need a better check
                LOG.error("Build finished normally but contains pending active configurations.");
//                throw new IllegalStateException("Build finished normally but contains pending active configurations.");
            }
            setStatusOfPendingConfigurations(JenkinsStatus.NONE);
        }
    }
    
    private void setStatusOfPendingConfigurations(JenkinsStatus js){
        for(JenkinsActiveConfiguration jac : pendingActiveConfigurations){
            jac.setStatus(js);
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == this){
            return true;
        }
        if(this.url == null){ // we need it for equals
            return false;
        }
        if(!(obj instanceof JenkinsBuild)){
            return false;
        }
        return this.url.equals(((JenkinsBuild) obj).url);
    }
    
    @Override
    public int hashCode() {
        return this.url == null ? 1 : this.url.hashCode();
    }
    
    @Override
    public int compareTo(JenkinsBuild o) {
        if(this.buildNumber == null){
            return -1;
        }
        if(o.buildNumber == null){
            return 1;
        }
        int thisBN = Integer.parseInt(this.buildNumber);
        int oBN = Integer.parseInt(o.buildNumber);
        return Integer.compare(thisBN, oBN);
    }
}


























