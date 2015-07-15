package qe.jenkins;

import java.util.HashSet;
import java.util.Set;

/**
 * An active configuration of jenkins build.
 * 
 * @author jdurani
 *
 */
public class JenkinsActiveConfiguration {

    /**
     * Status of jenkins configuration. 
     * @author jdurani
     *
     */
    public static enum JenkinsStatus {SUCCESS, FAILURE, UNSTABLE, ABORTED, BUILDING, PENDING, NONE};
    
    /**
     * Value on X-axis in multi-configuration matrix. 
     */
    private String xValue;
    /**
     * Value on Y-axis in multi-configuration matrix. 
     */
    private String yValue;
    /**
     * The URL of this configuration. 
     */
    private String url;
    /**
     * Status of this configuration.
     */
    private JenkinsStatus status = JenkinsStatus.NONE;
    /**
     * Build artifacts.
     */
    private Set<String> artifacts = new HashSet<>();
    
    /**
     * Returns value on X-axis. 
     * @return
     */
    public String getxValue() {
        return xValue;
    }

    /**
     * Sets value on X-axis.
     * @param xValue
     */
    public void setxValue(String xValue) {
        this.xValue = xValue;
    }

    /**
     * Returns value on Y-axis.
     * @return
     */
    public String getyValue() {
        return yValue;
    }
    
    /**
     * Sets value on Y-axis.
     * @param yValue
     */
    public void setyValue(String yValue) {
        this.yValue = yValue;
    }
    
    /**
     * Returns URL of this configuration.
     * @return
     */
    public String getUrl() {
        return url;
    }
    
    /**
     * Sets URL of this configuration.
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
     * Adds a build artifact.
     * 
     * @param artifact
     */
    public void addArtifact(String artifact){
        if(artifact != null){
            artifacts.add(artifact);
        }
    }

    /**
     * Returns all build artifacts.
     * 
     * @return
     */
    public Set<String> getArtifacts() {
        return artifacts;
    }
    
    /**
     * Returns status of this configuration.
     * @return
     */
    public JenkinsStatus getStatus() {
        return status;
    }

    /**
     * Sets status of this configuration.
     * 
     * @param status
     */
    public void setStatus(JenkinsStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this){
            return true;
        }
        if(this.url == null){ // we need it for equals
            return false;
        }
        if(!(obj instanceof JenkinsActiveConfiguration)){
            return false;
        }
        return this.url.equals(((JenkinsActiveConfiguration) obj).url);
    }
    
    @Override
    public int hashCode() {
        return this.url == null ? 1 : this.url.hashCode();
    }
}






















