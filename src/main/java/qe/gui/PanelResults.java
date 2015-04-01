package qe.gui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.logging.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qe.entity.result.ResultGetter;
import qe.entity.result.TestResult;
import qe.exception.ResultParsingException;
import qe.utils.Utils;
/**
 * Panel for viewing summary of test run
 * @author felias
 *
 */
public class PanelResults extends TabbedPanel {
	private static final Logger logger = LoggerFactory.getLogger(ResultsGUI.class);
	private JTable table;
	private ResultGetter results;
	private PanelDetails panel_details;

	public PanelResults(ResultGetter results, PanelDetails panel_details) {
		super();
		this.results = results;
		this.panel_details = panel_details;
	}

	public void initialize() {
		logger.debug("Initializing Summary result panel");
		// Panel for summary results
		getParent();
		// Table with results
		String columnNames[] = { "Name", "Success", "Failed", "Total", "Skipped" };
		DefaultTableModel model = new DefaultTableModel();
		model.setColumnIdentifiers(columnNames);
		panel.setLayout(new GridBagLayout());

		// Label Test Results
		GridBagConstraints c1 = new GridBagConstraints();
		c1.fill = GridBagConstraints.NONE;
		c1.gridx = 3;
		c1.weightx = 0;
		c1.anchor = GridBagConstraints.CENTER;
		c1.weighty = 0;
		c1.gridy = 0;
		c1.gridwidth = 2;
		c1.gridheight = 1;
		JLabel lblNewLabel = new JLabel("Test Results");
		lblNewLabel.setFont(new Font("Dialog", Font.BOLD, 23));
		panel.add(lblNewLabel, c1);

		// Table with Summary test results
		GridBagConstraints c2 = new GridBagConstraints();
		c2.fill = GridBagConstraints.BOTH;
		c2.weightx = 1;
		c2.weighty = 1;
		c2.gridx = 0;
		c2.gridy = 1;
		c2.gridwidth = 5;
		c2.gridheight = 9;
		JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane, c2);
		table = new JTable(model);
		table.addMouseListener(new MouseAdapter() { // Listens for clicks on the
													// table. Swithes to details
													// panel
			@Override
			public void mouseClicked(MouseEvent e) {
				JTable target = (JTable) e.getSource();
				int row = target.getSelectedRow();
				getParentPane().setSelectedIndex(1);
				table.getModel().getValueAt(row, 0);
				panel_details.setIndexInCombobox(row);
				logger.debug("Swithing to panel details and selecting " + table.getModel().getValueAt(row, 0));
			}
		});
		addRows(table); // fills table with data
		scrollPane.setViewportView(table);

		// Button for refresing test results
		JButton btnNewButton = new JButton("Refresh");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				logger.info("Test results refresh");
				addRows(table);
				panel_details.fillCompBox();
			}
		});
		GridBagConstraints c3 = new GridBagConstraints();
		c3.fill = GridBagConstraints.NONE;
		c3.weightx = 0;
		c3.weighty = 0;
		c3.gridx = 4;
		c3.anchor = GridBagConstraints.LINE_END;
		c3.gridy = 0;
		c3.gridwidth = 1;
		c3.gridheight = 1;
		panel.add(btnNewButton, c3);
		logger.debug("Panel Summary results initialized");
	}

	private void addRows(JTable table) {
		HashMap<String, TestResult> map = null;
		try {
			map = results.loadSummaryTotal();
		} catch (ResultParsingException e1) {
			Utils.showMessageDialog(rootFrame, Level.ERROR, e1.getMessage(), e1);
		}
		if (map == null) {
			return;
		}
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.setRowCount(0);
		for (Entry<String, TestResult> e : map.entrySet()) {
			Object[] row = new Object[5];
			row[0] = e.getKey();
			row[1] = e.getValue().getNumberOfSuccessfulTests();
			row[2] = e.getValue().getNumberOfErrorTests();
			row[3] = e.getValue().getNumberOfTotalTests();
			row[4] = e.getValue().getNumberOfSkippedTests();
			model.addRow(row);
		}

	}

	private JTabbedPane getParentPane() {
		if (panel.getParent() instanceof JTabbedPane) {
			return (JTabbedPane) panel.getParent();
		} else {
			throw new RuntimeException("This panel must be in tabbed pane");
		}
	}
}
