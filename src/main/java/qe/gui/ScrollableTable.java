package qe.gui;

import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.TableModel;

/**
 * Workaround to get scrollbars for table
 * @author felias
 *
 */
public class ScrollableTable extends JTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5593935465214512708L;

	public ScrollableTable(TableModel model) {
		super(model);
	}

	public boolean getScrollableTracksViewportWidth() {
		if (autoResizeMode != AUTO_RESIZE_OFF) {
			if (getParent() instanceof JViewport) {
				return (((JViewport) getParent()).getWidth() > getPreferredSize().width);
			}
		}
		return false;
	}
}
