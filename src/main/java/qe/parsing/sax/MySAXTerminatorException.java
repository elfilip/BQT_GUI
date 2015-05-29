package qe.parsing.sax;

import org.xml.sax.SAXException;
/**
 * Exception which terminates Sax parsing if thrown
 * 
 * @author felias
 *
 */
public class MySAXTerminatorException extends SAXException {
	MySAXTerminatorException() {
		super("This is not an error");
	}
}