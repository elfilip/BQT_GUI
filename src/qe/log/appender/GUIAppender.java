package qe.log.appender;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JTextArea;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Logging appender for text area
 * @author felias
 *
 */
public class GUIAppender extends AppenderSkeleton {

	static private JTextArea area = null;
	public synchronized void append(LoggingEvent event) {
		if (area != null) {
			area.append(this.getLayout().format(event));
			if(event.getThrowableInformation()!=null){
				StringWriter errors = new StringWriter();
				event.getThrowableInformation().getThrowable().printStackTrace(new PrintWriter(errors));
				area.append(errors.toString().substring(0,800)+"...\n");
			}
		}
	}

	public void close() {
	}

	public boolean requiresLayout() {
		return true;
	}

	public static void setArea(JTextArea areaField) {
		area = areaField;
	}
}
