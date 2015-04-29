package qe.exception;

/**
 * Jenkins exception.
 * 
 * @author jdurani
 *
 */
@SuppressWarnings("serial")
public class JenkinsException extends Exception {

    /**
     * {@inheritDoc}
     */
    public JenkinsException() {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     * @param message {@inheritDoc}
     * @param cause {@inheritDoc}
     */
    public JenkinsException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     * 
     * @param message {@inheritDoc}
     */
    public JenkinsException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     * 
     * @param cause {@inheritDoc}
     */
    public JenkinsException(Throwable cause) {
        super(cause);
    }
}
