package qe.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Abstract class for all members of tabbled pane
 * @author felias
 *
 */
public abstract class TabbedPanel {
	protected JPanel panel;
	protected JFrame rootFrame;

	public TabbedPanel() {
		panel = new JPanel();
	}

	public JPanel getPanel() {
		return panel;
	}

	protected JFrame getParent() {
		rootFrame = (JFrame) SwingUtilities.getWindowAncestor(panel);
		if (rootFrame == null) {
			throw new RuntimeException("You must add this panel into frame or tabbed pane before initialization.");
		}
		return rootFrame;
	}
	/**
	 * Loads all components for the panel
	 */
    public abstract void initialize();
}
