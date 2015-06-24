package qe.gui.highlight;

import javax.swing.JTextPane;

/**
 * 
 * @author felias
 *
 *Text pane with XML syntax highlighting
 */
public class XMLTextPane extends JTextPane {
	private static final long serialVersionUID = 4394890898671637335L;

	public XMLTextPane() {
		setEditorKitForContentType("text/xml", new XMLEditorKit());
		setContentType("text/xml");
	}
}
