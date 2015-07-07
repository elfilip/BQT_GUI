package qe.gui.highlight;

import javax.swing.text.StyledEditorKit;
import javax.swing.text.ViewFactory;

public class XMLEditorKit extends StyledEditorKit {

	private static final long serialVersionUID = 8762210474047092504L;

	private final String CONTENT_TYPE = "text/xml";

	private ViewFactory factory = new XMLFactory();

	public ViewFactory getViewFactory() {
		return factory;
	}

	public String getContentType() {
		return CONTENT_TYPE;
	}
}
