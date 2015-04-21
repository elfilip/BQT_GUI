package qe.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Map.Entry;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.MutableComboBoxModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.apache.logging.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import qe.entity.result.QueryFailure;
import qe.entity.result.ResultGetter;
import qe.entity.result.TestResult;
import qe.exception.GUIException;
import qe.exception.ResultParsingException;
import qe.panels.TablePanel;
import qe.parsing.dom.DomParserExpectedRes;
import qe.parsing.dom.DomParserFailure;
import qe.utils.FileLoader;
import qe.utils.Utils;

/**
 * This panel contains details for specific test
 * 
 * @author felias
 *
 */
public class PanelDetails extends TabbedPanel {
	private static final Logger logger = LoggerFactory.getLogger(PanelDetails.class);
	private ScrollableTable tableErrorList;
	private JTextArea textAreaExpectedRest;
	private JTextArea textAreaActualRes;
	private TablePanel tableExpectedResult;
    private TablePanel tableActualResult;
	private JScrollPane paneErrorsList;
	private JTextField txtQueryName;
	private JTextField txtErrorErrorError;
	private boolean isErrorListExtended;
	private JButton errListExtendButton;
	private JComboBox<String> comboBoxName;
	private ResultGetter results;

	/**
	 * 
	 * @param results
	 *            ResultGetter object
	 */
	public PanelDetails(ResultGetter results) {
		super();
		this.results = results;
		textAreaExpectedRest = new JTextArea();
		textAreaActualRes = new JTextArea();
		txtErrorErrorError = new JTextField();
		txtQueryName = new JTextField();
	}

	public void initialize() {
		logger.debug("Initializing panel Details");
		// Panel for test details

		getParent();
		GridBagLayout gbl_panel_details = new GridBagLayout();
		panel.setLayout(gbl_panel_details);

		// Table for list of errors for one test - parses files with compare
		// errors
		DefaultTableModel errModel = new DefaultTableModel();
		errModel.setColumnIdentifiers(new Object[] { "Errors" });
		tableErrorList = new ScrollableTable(errModel);
		tableErrorList.setPreferredSize(new Dimension(750, 750));
		tableErrorList.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		tableErrorList.getSelectionModel().addListSelectionListener(new ListSelectionListener() { // initializes
																									// expected
																									// and
																									// actual
																									// result
																									// for
																									// query
					@Override
					public void valueChanged(ListSelectionEvent event) {
						if (tableErrorList.getSelectedRow() > -1) {

							int row = tableErrorList.getSelectedRow();
							if (results.getCurrentTest() == null) {
								return;
							}
							logger.debug("Loading details for query" + tableErrorList.getValueAt(tableErrorList.getSelectedRow(), 0));
							QueryFailure f = results.getResults().get(results.getCurrentTest()).getFailures().get(row);
							textAreaActualRes.setText(f.getActualResult());
							textAreaExpectedRest.setText(f.getExpectedResult());
							txtQueryName.setText(f.getQueryName());
							txtErrorErrorError.setText(f.getCompareErrors().get(0));
						    //tables
						    tableActualResult.parseXML(f.getActualResult());
						    tableExpectedResult.parseXML(f.getExpectedResult());
						    tableActualResult.markDiff(tableExpectedResult);
							logger.debug("Details loaded for query " + tableErrorList.getValueAt(tableErrorList.getSelectedRow(), 0));
						}
					}
				});

		// Combobox for selecting test
		class Aaa<String> extends DefaultComboBoxModel<String> implements MutableComboBoxModel<String> {

		}
		comboBoxName = new JComboBox<String>(new Aaa<String>());
		comboBoxName.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (arg0.getStateChange() == ItemEvent.SELECTED) {
					logger.debug("Selecting test results of scenario " + arg0.getItem().toString());
					showCompareErrors((String) arg0.getItem());
					if (tableErrorList.getModel().getRowCount() > 0) {
						tableErrorList.setRowSelectionInterval(0, 0);
					}
				}
			}
		});
		GridBagConstraints gbc_comboBoxName = new GridBagConstraints();
		gbc_comboBoxName.insets = new Insets(0, 0, 5, 5);
		this.fillCompBox();
		if (comboBoxName.getModel().getSize() > 0)
			comboBoxName.setSelectedIndex(0);
		gbc_comboBoxName.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBoxName.gridx = 0;
		gbc_comboBoxName.gridy = 2;
		gbc_comboBoxName.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc_comboBoxName.weightx = 0.2;
		gbc_comboBoxName.weighty = 0;
		gbc_comboBoxName.gridwidth = 1;
		gbc_comboBoxName.gridheight = 2;
		panel.add(comboBoxName, gbc_comboBoxName);

		// Label Query Name:
		JLabel lblQueryName = new JLabel("Query Name:");
		GridBagConstraints gbc_lblQueryName = new GridBagConstraints();
		gbc_lblQueryName.insets = new Insets(0, 0, 5, 5);
		gbc_lblQueryName.anchor = GridBagConstraints.EAST;
		gbc_lblQueryName.gridx = 2;
		gbc_lblQueryName.gridy = 0;
		panel.add(lblQueryName, gbc_lblQueryName);

		// Test field for query name
		txtQueryName = new JTextField();
		txtQueryName.setText("Name of the query");
		GridBagConstraints gbc_txtQueryName = new GridBagConstraints();
		gbc_txtQueryName.insets = new Insets(0, 0, 5, 0);
		gbc_txtQueryName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtQueryName.gridx = 3;
		gbc_txtQueryName.gridy = 0;
		gbc_txtQueryName.gridwidth = 8;
		panel.add(txtQueryName, gbc_txtQueryName);
		txtQueryName.setColumns(10);

		// Label Errors:
		JLabel lblErrors = new JLabel("Errors:");
		GridBagConstraints gbc_lblErrors = new GridBagConstraints();
		gbc_lblErrors.insets = new Insets(0, 0, 5, 5);
		gbc_lblErrors.anchor = GridBagConstraints.EAST;
		gbc_lblErrors.gridx = 2;
		gbc_lblErrors.gridy = 1;
		panel.add(lblErrors, gbc_lblErrors);

		// Textfield for Errors for particular query
		txtErrorErrorError = new JTextField();
		txtErrorErrorError.setText("Error Error Error");
		txtErrorErrorError.setColumns(10);
		GridBagConstraints gbc_txtErrorErrorError = new GridBagConstraints();
		gbc_txtErrorErrorError.insets = new Insets(0, 0, 5, 0);
		gbc_txtErrorErrorError.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtErrorErrorError.gridx = 3;
		gbc_txtErrorErrorError.gridy = 1;
		gbc_txtErrorErrorError.gridwidth = 8;
		gbc_txtErrorErrorError.anchor = GridBagConstraints.LINE_END;
		panel.add(txtErrorErrorError, gbc_txtErrorErrorError);
		JButton btnShowMore = new JButton("More Errors");
		btnShowMore.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				logger.debug("Spawning a new Frame with errors");
				if (tableErrorList.getSelectedRow() == -1) {
					Utils.showMessageDialog(rootFrame, Level.ERROR, "Please select test failure", null);
					return;
				}
				QueryFailure f = results.getResults().get(results.getCurrentTest()).getFailures().get(tableErrorList.getSelectedRow());
				ErrorsFrame errFrame = new ErrorsFrame("Errors for " + results.getCurrentTest() + ": " + f.getQueryName());
				errFrame.initialize().setErrors(f).show();
			}
		});

		// Button for extend error list viewport
		errListExtendButton = new JButton(">");
		errListExtendButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				GridBagConstraints gbc_paneErrorsList = new GridBagConstraints();
				gbc_paneErrorsList.insets = new Insets(0, 0, 0, 5);
				gbc_paneErrorsList.fill = GridBagConstraints.BOTH;
				gbc_paneErrorsList.gridx = 0;
				gbc_paneErrorsList.gridy = 3;
				gbc_paneErrorsList.weighty = 0;
				gbc_paneErrorsList.gridwidth = 2;
				gbc_paneErrorsList.gridheight = 10;
				if (isErrorListExtended == false) {
					gbc_paneErrorsList.weightx = 1;
					errListExtendButton.setText("<");
				} else {
					gbc_paneErrorsList.weightx = 0;
					errListExtendButton.setText(">");

				}
				isErrorListExtended = !isErrorListExtended;
				panel.add(paneErrorsList, gbc_paneErrorsList);
				panel.getParent().validate();
				panel.getParent().repaint();
			}
		});

		GridBagConstraints gbc_errListExtendButton = new GridBagConstraints();
		gbc_errListExtendButton.insets = new Insets(0, 0, 5, 5);
		gbc_errListExtendButton.gridx = 1;
		gbc_errListExtendButton.gridy = 2;
		errListExtendButton.setPreferredSize(new Dimension(50, 25));
		panel.add(errListExtendButton, gbc_errListExtendButton);

		// Button to show more errors, if query test results contains multiple
		// errors
		GridBagConstraints gbc_btnShowMore = new GridBagConstraints();
		gbc_btnShowMore.insets = new Insets(0, 0, 5, 5);
		gbc_btnShowMore.gridx = 5;
		gbc_btnShowMore.gridy = 2;
		gbc_btnShowMore.anchor = GridBagConstraints.EAST;
		panel.add(btnShowMore, gbc_btnShowMore);

		// Saves expected result, which is in textAreaExpectedRest, into a
		// proper file in the repository
		JButton btnNewButton_1 = new JButton("Save");
		btnNewButton_1.setToolTipText("Saves currents state of expected result");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File expectedResult = null;
				Node root;
				try {
					root = DomParserFailure.parseString(textAreaExpectedRest.getText());
				} catch (Exception e2) {
					Utils.showMessageDialog(rootFrame, Level.ERROR, "Error when parsing content of expected result. Please check your modifications.", e2);
					return;
				}
				try {
					File pathtoExpectedResults = FileLoader.getPathToExpectedResults(results.getCurrentTest());
					expectedResult = FileLoader.findTestInExepectedResults(results.getResults().get(results.getCurrentTest()).getFailures().get(tableErrorList.getSelectedRow()).getFileName(), results.getCurrentTest(), pathtoExpectedResults);
					if (expectedResult == null) {
						throw new ResultParsingException("Expected result file can't be found");
					}
					DomParserExpectedRes parser = new DomParserExpectedRes(expectedResult);
					parser.replaceExpectedResult(root.getFirstChild());
					parser.writeXMLdocument();
				} catch (Exception e1) {
					Utils.showMessageDialog(rootFrame, Level.ERROR, "Error when saving expected result:\n" + e1.getMessage(), e1);
					return;
				}
				Utils.showMessageDialog(rootFrame, Level.INFO, "Expected Result has been saved in:\n" + expectedResult.getAbsolutePath(), null);
			}
		});

		GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
		gbc_btnNewButton_1.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton_1.gridx = 6;
		gbc_btnNewButton_1.gridy = 2;
		panel.add(btnNewButton_1, gbc_btnNewButton_1);

		// Invisible button
		JButton invisible = new JButton("Save Actual Result");
		invisible.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				logger.info("cool");

			}
		});
		GridBagConstraints gbc_invisible = new GridBagConstraints();
		gbc_invisible.insets = new Insets(0, 0, 5, 5);
		gbc_invisible.gridx = 7;
		gbc_invisible.gridy = 2;
		panel.add(invisible, gbc_invisible);

		// Scroll pane to show table with errors
		paneErrorsList = new JScrollPane();
		paneErrorsList.setPreferredSize(new Dimension(1, 1));
		paneErrorsList.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		paneErrorsList.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		GridBagConstraints gbc_paneErrorsList = new GridBagConstraints();
		gbc_paneErrorsList.insets = new Insets(0, 0, 0, 5);
		gbc_paneErrorsList.fill = GridBagConstraints.BOTH;
		gbc_paneErrorsList.gridx = 0;
		gbc_paneErrorsList.gridy = 3;
		gbc_paneErrorsList.weightx = 0.33;
		gbc_paneErrorsList.weighty = 0;
		gbc_paneErrorsList.gridwidth = 2;
		gbc_paneErrorsList.gridheight = 10;
		panel.add(paneErrorsList, gbc_paneErrorsList);
		paneErrorsList.setViewportView(tableErrorList);
		// Text area for actual result
		final JScrollPane paneActualResult = new JScrollPane();
		GridBagConstraints gbc_paneActualResult = new GridBagConstraints();
		gbc_paneActualResult.insets = new Insets(0, 0, 0, 5);
		gbc_paneActualResult.fill = GridBagConstraints.BOTH;
		gbc_paneActualResult.gridx = 2;
		gbc_paneActualResult.gridy = 3;
		gbc_paneActualResult.weightx = 0.633;
		gbc_paneActualResult.weighty = 1;
		gbc_paneActualResult.gridwidth = 4;
		gbc_paneActualResult.gridheight = 10;
		panel.add(paneActualResult, gbc_paneActualResult);

		textAreaActualRes = new JTextArea();
		paneActualResult.setViewportView(textAreaActualRes);

		// Text area for expected results
		final JScrollPane paneExpectedResult = new JScrollPane();
		GridBagConstraints gbc_paneExpectedResult = new GridBagConstraints();
		gbc_paneExpectedResult.insets = new Insets(0, 0, 0, 5);
		gbc_paneExpectedResult.fill = GridBagConstraints.BOTH;
		gbc_paneExpectedResult.gridx = 6;
		gbc_paneExpectedResult.gridy = 3;
		gbc_paneExpectedResult.weightx = 0.633;
		gbc_paneExpectedResult.weighty = 1;
		gbc_paneExpectedResult.gridwidth = 4;
		gbc_paneExpectedResult.gridheight = 10;
		panel.add(paneExpectedResult, gbc_paneExpectedResult);

		textAreaExpectedRest = new JTextArea();
		paneExpectedResult.setViewportView(textAreaExpectedRest);
		setVerticalAndHorizontalUnitIncrement(20, 20, paneActualResult, paneExpectedResult);
		bindScrollPanes(paneActualResult, paneExpectedResult);
		
        tableExpectedResult = new TablePanel();
        tableActualResult = new TablePanel();
        
        final JButton shoAsTable = new JButton("Show as table");
        shoAsTable.addActionListener(new ActionListener() {
            boolean isXml = true;
            @Override
            public void actionPerformed(ActionEvent e) {
                if(isXml){
        	        paneExpectedResult.setViewportView(tableExpectedResult);
        	        paneActualResult.setViewportView(tableActualResult);
        	        shoAsTable.setText("Show as XML");
        	        isXml = false;
                } else {
                    paneExpectedResult.setViewportView(textAreaExpectedRest);
                    paneActualResult.setViewportView(textAreaActualRes);
                    shoAsTable.setText("Show as table");
                    isXml = true;
                }
            }
        });
		GridBagConstraints gbc_showAsTable = new GridBagConstraints();
        gbc_showAsTable.insets = new Insets(0, 0, 5, 5);
        gbc_showAsTable.gridx = 4;
        gbc_showAsTable.gridy = 2;
        gbc_showAsTable.anchor = GridBagConstraints.EAST;
        panel.add(shoAsTable, gbc_showAsTable);
	}
	
	private void setVerticalAndHorizontalUnitIncrement(int vert, int hor, JScrollPane... panes){
	    if(panes == null){
	        return;
	    }
	    for(JScrollPane pane : panes){
	        pane.getVerticalScrollBar().setUnitIncrement(vert);
	        pane.getHorizontalScrollBar().setUnitIncrement(hor);
	    }
	}
	
	private void bindScrollPanes(final JScrollPane... panes){
	    if(panes == null || panes.length == 0){
	        return;
	    }
	    for(JScrollPane pane : panes){
	        // vertical model
	        final BoundedRangeModel verticalModel = pane.getVerticalScrollBar().getModel();
	        verticalModel.addChangeListener(new ChangeListener() {
                
                @Override
                public void stateChanged(ChangeEvent e) {
                    int value = verticalModel.getValue();
                    for(JScrollPane p : panes){
                        p.getVerticalScrollBar().setValue(value);
                    }
                }
            });
            // horizontal model
            final BoundedRangeModel horizontalModel = pane.getHorizontalScrollBar().getModel();
            horizontalModel.addChangeListener(new ChangeListener() {
                
                @Override
                public void stateChanged(ChangeEvent e) {
                    int value = horizontalModel.getValue();
                    for(JScrollPane p : panes){
                        p.getHorizontalScrollBar().setValue(value);
                    }
                }
            });

	    }
	}

	/**
	 * Loads compare errors(query failures) for test
	 * 
	 * @param testName
	 *            name of the tests
	 */
	public void showCompareErrors(String testName) {
		((DefaultTableModel) tableErrorList.getModel()).setRowCount(0);
		try {
			results.loadFailuresForTest(testName);
		} catch (ResultParsingException e) {
			logger.warn("Errors when parsing files with failures", e);
		} catch (GUIException e) {
			Utils.showMessageDialog(rootFrame, Level.INFO, e.getMessage(), e);
		}
		results.setCurrentTest(testName);
		DefaultTableModel model = (DefaultTableModel) tableErrorList.getModel();
		TestResult result = results.getResults().get(testName);
		if (result == null) {
			Utils.showMessageDialog(rootFrame, Level.ERROR, "Internal Error: Test name doesn't exist: " + testName, null);
			return;
		}
		for (QueryFailure failure : result.getFailures()) {
			model.addRow(new Object[] { failure.getQuery() });
		}
	}

	/**
	 * Loads list of all tests
	 */
	public void fillCompBox() {
		comboBoxName.removeAllItems();
		int counter = 0;
		if (results.getResults() != null)
			for (Entry<String, TestResult> e : results.getResults().entrySet()) {
				comboBoxName.insertItemAt(e.getKey(), counter);
				counter++;
			}
	}

	/**
	 * Selects test in the comboBox
	 * 
	 * @param index
	 */
	public void setIndexInCombobox(int index) {
		if (index < 0 || index > comboBoxName.getItemCount()) {
			logger.error("Unable to select index in combobox: " + index);
			return;
		}
		results.setCurrentTest(comboBoxName.getItemAt(index));
		comboBoxName.setSelectedIndex(index);
	}
}
