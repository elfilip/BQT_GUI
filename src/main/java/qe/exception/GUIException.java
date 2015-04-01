package qe.exception;

/**
 * Exception for GUI specific situations.s
 * 
 * @author felias
 *
 */
public class GUIException extends Exception {

	private static final long serialVersionUID = 7121260240207611768L;

	public GUIException(String str) {
		super(str);
	}

	public GUIException() {
		super();
	}
}
