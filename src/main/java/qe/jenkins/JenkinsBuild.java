package qe.jenkins;

import java.util.HashSet;
import java.util.Set;

import qe.exception.JenkinsException;

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
     * The active configurations.
     */
    private Set<JenkinsActiveConfiguration> activeConfigurations = new HashSet<>();
    
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
     * Returns active configurations of this build.
     * 
     * @return
     */
    public Set<JenkinsActiveConfiguration> getActiveConfigurations() {
        return activeConfigurations;
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
            throw new JenkinsException("Active configuration is not part of this build [" + buildNumber + ", " + jacUrl + "].");
        }
        if(!jacUrl.contains(("/" + xLabel + "="))){
            throw new JenkinsException("Active configuration and build have differenet X-label.");
        }
        if(!jacUrl.contains(("," + yLabel + "="))){
            throw new JenkinsException("Active configuration and build have differenet Y-label.");
        }
        
        activeConfigurations.add(jac);
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


























