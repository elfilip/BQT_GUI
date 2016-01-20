package qe.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.apache.logging.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qe.entity.result.RefreshResults;
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
	
    private static final Logger logger = LoggerFactory.getLogger(PanelResults.class);
    private static final int MAJOR = 2;
    private static final int MINOR = 1;
	
	private JTable table;
	private ResultGetter results;
	private PanelDetails panel_details;
    private int[][] highlight;


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
		@SuppressWarnings("serial")
		DefaultTableModel model = new DefaultTableModel(){
			 @Override
	            public Class<?> getColumnClass(int column) {
	                switch (column) {
	                    case 1:
	                        return Integer.class;
	                    case 2:
	                        return Integer.class;
	                    case 3:
	                        return Integer.class;
	                    case 4:
	                        return Integer.class;
	                    case 0:
	                    default:
	                        return String.class;
	                }
	            }
		};
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
		table.setAutoCreateRowSorter(true);
		table.setDefaultRenderer(String.class, new Renderer());
		table.setDefaultRenderer(Integer.class, new Renderer());
		table.addMouseListener(new MouseAdapter() { // Listens for clicks on the
													// table. Switches to details
													// panel
			@Override
			public void mouseClicked(MouseEvent e) {
				JTable target = (JTable) e.getSource();
				int row = target.getSelectedRow();
				getParentPane().setSelectedIndex(1);
				panel_details.setIndexInCombobox(panel_details.getIndexOfItem((String)table.getValueAt(row, 0)));
				logger.debug("Swithing to panel details and selecting " + table.getModel().getValueAt(row, 0));
			}
		});
		addRows(table); // fills table with data
		scrollPane.setViewportView(table);

		// Button for refreshing test results
		JButton btnNewButton = new JButton("Refresh");
		RefreshResults.setRefreshButton(btnNewButton);
		btnNewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
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
		List<String> scenarios = new ArrayList<String>(map.keySet());
		Collections.sort(scenarios);
		
		int columns = 5;
		highlight = new int[scenarios.size()][columns];
		int rowI = 0;
		for(String scen : scenarios){
		    TestResult res = map.get(scen);
		    Object[] row = new Object[columns];
            row[0] = scen;
            row[1] = new Integer(res.getNumberOfSuccessfulTests());
            row[2] = new Integer(res.getNumberOfErrorTests());
            row[3] = new Integer(res.getNumberOfTotalTests());
            row[4] = new Integer(res.getNumberOfSkippedTests());
            boolean hasFailed = res.getNumberOfErrorTests() != 0;
            boolean hasSkipped = res.getNumberOfSkippedTests() != 0;
            if(hasFailed || hasSkipped){
                for(int i = 0; i < columns; i++){
                    highlight[rowI][i] = MINOR;
                }
                if(hasFailed){
                    highlight[rowI][2] = MAJOR;
                }
                if(hasSkipped){
                    highlight[rowI][4] = MAJOR;
                }
            }
            rowI++;
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
	
	private class Renderer implements TableCellRenderer {
        
	    private final Color colorMajorHighlight = new Color(240, 189, 189);
	    private final Color colorMinorHighlight = new Color(206, 255, 168);
	    private final Map<String, JLabel> labels = new HashMap<>();
	    
	    @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel text = getTextLabel(table, row, column);
            Color color = Color.WHITE;
            boolean opaque = false;
            if(highlight[row][column] == MAJOR){
                color = colorMajorHighlight;
                opaque = true;
            } else if(highlight[row][column] == MINOR){
                color = colorMinorHighlight;
                opaque = true;
            }
            text.setText(String.valueOf(value));
            text.setOpaque(opaque);
            text.setBackground(color);
            return text;
        }

        private JLabel getTextLabel(JTable table, int row, int column) {
            JLabel text = labels.get(row + " " + column);
            if(text == null){
                text = new JLabel();
                text.setFont(table.getFont());
                labels.put(row + " " + column, text);
            }
            return text;
        }    
    }
}
