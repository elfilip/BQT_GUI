package qe.jenkins;

import java.util.HashSet;
import java.util.Set;

import qe.exception.JenkinsException;

/**
 * Jenkins job. Contains zero or more builds.
 *  
 * @author jdurani
 *
 */
public class JenkinsJob {

    /**
     * The URL of this job.
     */
    private String url;
    /**
     * The name of this job.
     */
    private String name;
    /**
     * Builds.
     */
    private Set<JenkinsBuild> builds = new HashSet<>();
    
    /**
     * Returns URL of this job.
     * 
     * @return
     */
    public String getUrl() {
        return url;
    }
    
    /**
     * Sets URL for this job.
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
     * Returns builds of this job.
     * 
     * @return
     */
    public Set<JenkinsBuild> getBuilds() {
        return builds;
    }
    
    /**
     * Returns name of this job.
     * 
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name for this job.
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Adds build for this job. Checks if the build is part of this job.
     * 
     * @param jb
     * @throws JenkinsException
     */
    public void addBuild(JenkinsBuild jb) throws JenkinsException{
        if(jb == null){
            return;
        }
        if(url == null){
            throw new JenkinsException("Unable to check validity of build for this job.");
        }
        String buildUrl = jb.getUrl();
        if(!buildUrl.startsWith(url)){
            throw new JenkinsException("Build is not part of this job.");
        }
        builds.add(jb);
    }
}

















