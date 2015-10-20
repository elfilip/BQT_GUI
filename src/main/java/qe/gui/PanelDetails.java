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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.AbstractButton;
import javax.swing.BoundedRangeModel;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultRowSorter;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.MutableComboBoxModel;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.ScrollPaneConstants;
import javax.swing.SortOrder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.logging.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import qe.entity.result.QueryFailure;
import qe.entity.result.ResultGetter;
import qe.entity.result.TestResult;
import qe.exception.GUIException;
import qe.exception.ResultParsingException;
import qe.gui.highlight.XMLTextPane;
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
	private XMLTextPane textAreaExpectedRest;
	private XMLTextPane textAreaActualRes;
	private TablePanel tableExpectedResult;
	private TablePanel tableActualResult;
	private JScrollPane paneErrorsList;
	private JTextField txtQueryName;
	private JTextField txtErrorErrorError;
	private boolean isErrorListExtended;
	private JButton errListExtendButton;
	private JComboBox<String> comboBoxName;
	private ResultGetter results;
	private TableRowSorter<TableModel> sorter;
	private JTextField errorfilter;
	JButton switchDisplay;
	private boolean showingQueries=false;

	/**
	 * 
	 * @param results
	 *            ResultGetter object
	 */
	public PanelDetails(ResultGetter results) {
		super();
		this.results = results;
		textAreaExpectedRest = new XMLTextPane();
		textAreaActualRes = new XMLTextPane();
		txtErrorErrorError = new JTextField();
		txtQueryName = new JTextField();
		tableExpectedResult = new TablePanel();
		tableActualResult = new TablePanel();
	}

	public void initialize() {
		logger.debug("Initializing Panel Details");
		// Panel for test details

		getParent();
		GridBagLayout gbl_panel_details = new GridBagLayout();
		panel.setLayout(gbl_panel_details);

		// Table for list of errors for one test - parses files with compare
		// errors
		DefaultTableModel errModel = new DefaultTableModel();
		errModel.setColumnIdentifiers(new Object[] { "Errors", "Errors" });
		tableErrorList = new ScrollableTable(errModel);
		tableErrorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableErrorList.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		tableErrorList.getSelectionModel().addListSelectionListener(new ListSelectionListener() { // initializes expected and actual result for query

					@Override
					public void valueChanged(ListSelectionEvent event) {
						if (event.getValueIsAdjusting()) {
							return; // wait for end of multiple events
						}
						int row = tableErrorList.getSelectedRow();
						if (row > -1) {

							if (results.getCurrentTest() == null) {
								return;
							}
							logger.debug("Loading details for query" + tableErrorList.getValueAt(row, 0));
							// QueryFailure f = results.getResults().get(results.getCurrentTest()).getFailures().get(tableErrorList.getValueAt(row, 0));
							QueryFailure f = null;
							try {
								f = results.loadFailureDetails((String) tableErrorList.getValueAt(row, 0));
							} catch (ResultParsingException e) {
								Utils.showMessageDialog(rootFrame, Level.ERROR, e.getMessage(), e);
								return;
							} catch (GUIException e) {
								Utils.showMessageDialog(rootFrame, Level.ERROR, e.getMessage(), e);
								return;
							}
							if (f == null) {
								throw new RuntimeException("Internal error: Unable to find failure in the hashtable");
							}
							textAreaActualRes.setText(f.getActualResult());
							textAreaExpectedRest.setText(f.getExpectedResult());
							txtQueryName.setText(f.getQuery());
							txtErrorErrorError.setText(f.getCompareErrors().get(0));
							// tables
							tableActualResult.parseXML(f.getActualResult());
							tableExpectedResult.parseXML(f.getExpectedResult());
							tableActualResult.bindCells(tableExpectedResult);
							logger.debug("Details loaded for query " + tableErrorList.getValueAt(row, 0));
						} else {
							textAreaActualRes.setText(null);
							textAreaExpectedRest.setText(null);
							txtQueryName.setText(null);
							txtErrorErrorError.setText(null);
							// tables
							tableActualResult.clearTable();
							tableExpectedResult.clearTable();
						}
					}
				});
		// tableErrorList.setAutoCreateRowSorter(true);
		sorter = new TableRowSorter<>(tableErrorList.getModel());
		tableErrorList.setRowSorter(sorter);
		// Combobox for selecting test
		@SuppressWarnings("serial")
		class Aaa extends DefaultComboBoxModel<String> implements MutableComboBoxModel<String> {

		}
		final JButton switchDisplay=new JButton("Show queries");
		switchDisplay.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
						if(showingQueries==false){					
							switchDisplay.setText("Show filenames");
							showCompareErrors(results.getCurrentTest(), 1);
						}else{
							switchDisplay.setText("Show queries");
							showCompareErrors(results.getCurrentTest(), 0);
						}
						showingQueries=!showingQueries;
						((DefaultRowSorter) tableErrorList.getRowSorter()).sort();
			}
		});
		GridBagConstraints gbc_switchDisplay = new GridBagConstraints();
		gbc_switchDisplay.gridx = 0;
		gbc_switchDisplay.gridy = 0;
		gbc_switchDisplay.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc_switchDisplay.fill=GridBagConstraints.HORIZONTAL;
		panel.add(switchDisplay, gbc_switchDisplay);

		errorfilter=new JTextField();
		errorfilter.getDocument().addDocumentListener(
	                new DocumentListener() {
	                    public void changedUpdate(DocumentEvent e) {
	                    	updateFilter();
	                    }
	                    public void insertUpdate(DocumentEvent e) {
	                    	updateFilter();
	                    }
	                    public void removeUpdate(DocumentEvent e) {
	                    	updateFilter();
	                    }
	                });
		GridBagConstraints gbc_errorfilter = new GridBagConstraints();
		gbc_errorfilter.gridx = 0;
		gbc_errorfilter.gridy = 1			;
		gbc_errorfilter.anchor = GridBagConstraints.SOUTHWEST;
		gbc_errorfilter.fill=GridBagConstraints.HORIZONTAL;
		panel.add(errorfilter, gbc_errorfilter);
		comboBoxName = new JComboBox<String>(new Aaa());
		comboBoxName.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (arg0.getStateChange() == ItemEvent.SELECTED) {
					logger.debug("Selecting test results of scenario " + arg0.getItem().toString());
					if (showingQueries) {
						showCompareErrors((String) arg0.getItem(), 1);
					} else {
						showCompareErrors((String) arg0.getItem(), 0);
					}

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
		JLabel lblQueryName = new JLabel("Query:");
		GridBagConstraints gbc_lblQueryName = new GridBagConstraints();
		gbc_lblQueryName.insets = new Insets(0, 0, 5, 5);
		gbc_lblQueryName.anchor = GridBagConstraints.EAST;
		gbc_lblQueryName.gridx = 2;
		gbc_lblQueryName.gridy = 0;
		panel.add(lblQueryName, gbc_lblQueryName);

		// Text field for query name
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
				int row = tableErrorList.getSelectedRow();
				if (row == -1) {
					Utils.showMessageDialog(rootFrame, Level.ERROR, "Please select test failure", null);
					return;
				}
				QueryFailure f = results.getResults().get(results.getCurrentTest()).getFailures().get(tableErrorList.getValueAt(row, 0));
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
				int row = tableErrorList.getSelectedRow();
				if (row == -1) {
					Utils.showMessageDialog(rootFrame, Level.ERROR, "Please select query.", null);
					return;
				}
				File expectedResult = null;
				Document root;
				try {
					root = DomParserFailure.parseString(textAreaExpectedRest.getText());
				} catch (Exception e2) {
					Utils.showMessageDialog(rootFrame, Level.ERROR, "Error when parsing content of expected result. Please check your modifications.", e2);
					return;
				}
				try {
					File pathtoExpectedResults = FileLoader.getPathToExpectedResults(results.getCurrentTest());
					expectedResult = FileLoader.findTestInExepectedResults(results.getResults().get(results.getCurrentTest()).getFailures().get(tableErrorList.getValueAt(row, 0)).getFileName(), results.getCurrentTest(), pathtoExpectedResults);
					if (expectedResult == null) {
						throw new ResultParsingException("Expected result file can't be found");
					}
					DomParserExpectedRes parser = new DomParserExpectedRes(expectedResult);
					parser.replaceExpectedResult(root);
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

		// Saves expected result, which is in textAreaActualRes into a
		// proper file in the repository
		JButton invisible = new JButton("Save Actual Result");
		invisible.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = tableErrorList.getSelectedRow();
				if (row == -1) {
					Utils.showMessageDialog(rootFrame, Level.ERROR, "Please select query.", null);
					return;
				}
				File expectedResult = null;
				Document root;
				try {
					StringBuilder sb = new StringBuilder(textAreaActualRes.getText());
					Utils.replaceAll(sb, "actualQueryResults", "expectedQueryResults");
					Utils.replaceAll(sb, "actualException", "expectedException");
					root = DomParserFailure.parseString(sb.toString());
				} catch (Exception e2) {
					Utils.showMessageDialog(rootFrame, Level.ERROR, "Error when parsing content of actual result. Please check your modifications.", e2);
					return;
				}
				try {
					File pathtoExpectedResults = FileLoader.getPathToExpectedResults(results.getCurrentTest());
					expectedResult = FileLoader.findTestInExepectedResults(results.getResults().get(results.getCurrentTest()).getFailures().get(tableErrorList.getValueAt(row, 0)).getFileName(), results.getCurrentTest(), pathtoExpectedResults);
					if (expectedResult == null) {
						throw new ResultParsingException("Expected result file can't be found");
					}
					DomParserExpectedRes parser = new DomParserExpectedRes(expectedResult);
					parser.replaceExpectedResult(root);
					parser.writeXMLdocument();
				} catch (Exception e1) {
					Utils.showMessageDialog(rootFrame, Level.ERROR, "Error when saving actual result:\n" + e1.getMessage(), e1);
					return;
				}
				Utils.showMessageDialog(rootFrame, Level.INFO, "Actual Result has been saved in:\n" + expectedResult.getAbsolutePath(), null);

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

		textAreaActualRes = new XMLTextPane();
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

		textAreaExpectedRest = new XMLTextPane();
		paneExpectedResult.setViewportView(textAreaExpectedRest);
		setVerticalAndHorizontalUnitIncrement(20, 20, paneActualResult, paneExpectedResult);
		bindScrollPanes(paneActualResult, paneExpectedResult);

		final JButton showAsTable = new JButton("Show as table");
		showAsTable.addActionListener(new ActionListener() {
			boolean isXml = true;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (isXml) {
					paneExpectedResult.setViewportView(tableExpectedResult);
					paneActualResult.setViewportView(tableActualResult);
					showAsTable.setText("Show as XML");
					isXml = false;
				} else {
					paneExpectedResult.setViewportView(textAreaExpectedRest);
					paneActualResult.setViewportView(textAreaActualRes);
					showAsTable.setText("Show as table");
					isXml = true;
				}
			}
		});
		GridBagConstraints gbc_showAsTable = new GridBagConstraints();
		gbc_showAsTable.insets = new Insets(0, 0, 5, 5);
		gbc_showAsTable.gridx = 4;
		gbc_showAsTable.gridy = 2;
		gbc_showAsTable.anchor = GridBagConstraints.EAST;
		panel.add(showAsTable, gbc_showAsTable);
	}

	private void setVerticalAndHorizontalUnitIncrement(int vert, int hor, JScrollPane... panes) {
		if (panes == null) {
			return;
		}
		for (JScrollPane pane : panes) {
			pane.getVerticalScrollBar().setUnitIncrement(vert);
			pane.getHorizontalScrollBar().setUnitIncrement(hor);
		}
	}

	private void bindScrollPanes(final JScrollPane... panes) {
		if (panes == null || panes.length == 0) {
			return;
		}
		for (JScrollPane pane : panes) {
			// vertical model
			final BoundedRangeModel verticalModel = pane.getVerticalScrollBar().getModel();
			verticalModel.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					int value = verticalModel.getValue();
					for (JScrollPane p : panes) {
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
					for (JScrollPane p : panes) {
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
	public void showCompareErrors(String testName, int hiddenColumnIndex) {

		results.setCurrentTest(testName);
		((DefaultTableModel) tableErrorList.getModel()).setRowCount(0);
		try {
			results.loadFailedQueries();
		} catch (ResultParsingException e) {
			logger.warn("Errors when parsing files with failures", e);
		}

		DefaultTableModel model = (DefaultTableModel) tableErrorList.getModel();
		if (hiddenColumnIndex == 1) {
			if (tableErrorList.getColumnModel().getColumn(1).getMaxWidth() > 0) {
				tableErrorList.getColumnModel().getColumn(0).setMinWidth(tableErrorList.getColumnModel().getColumn(1).getMinWidth());
				tableErrorList.getColumnModel().getColumn(0).setMaxWidth(tableErrorList.getColumnModel().getColumn(1).getMaxWidth());
			}
			tableErrorList.getColumnModel().getColumn(1).setMinWidth(0);
			tableErrorList.getColumnModel().getColumn(1).setMaxWidth(0);
		} else if (hiddenColumnIndex == 0) {
			if (tableErrorList.getColumnModel().getColumn(0).getMaxWidth() > 0) {
				tableErrorList.getColumnModel().getColumn(1).setMinWidth(tableErrorList.getColumnModel().getColumn(0).getMinWidth());
				tableErrorList.getColumnModel().getColumn(1).setMaxWidth(tableErrorList.getColumnModel().getColumn(0).getMaxWidth());
			}
			tableErrorList.getColumnModel().getColumn(0).setMinWidth(0);
			tableErrorList.getColumnModel().getColumn(0).setMaxWidth(0);
		} else {
			tableErrorList.getColumnModel().getColumn(1).setMinWidth(0);
			tableErrorList.getColumnModel().getColumn(1).setMaxWidth(0);
		}

		TestResult result = results.getResults().get(testName);
		if (result == null) {
			Utils.showMessageDialog(rootFrame, Level.ERROR, "Internal Error: Test name doesn't exist: " + testName, null);
			return;
		}
		for (Entry<String, QueryFailure> failure : result.getFailures().entrySet()) {
			model.addRow(new Object[] { failure.getKey(), failure.getValue().getFileName() });
		}
		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		int columnIndexToSort = 1;
		sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.ASCENDING));
		sorter.setSortKeys(sortKeys);

	}

	/**
	 * Loads list of all tests
	 */
	public void fillCompBox() {
		comboBoxName.removeAllItems();
		int counter = 0;
		if (results.getResults() != null) {
			List<String> scenarios = new ArrayList<String>(results.getResults().keySet());
			Collections.sort(scenarios);
			for (String s : scenarios) {
				comboBoxName.insertItemAt(s, counter);
				counter++;
			}
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

	/**
	 * Selects test in the comboBox
	 * 
	 * @param index
	 */
	public int getIndexOfItem(String name) {

		String item;
		int i = 0;
		while ((item = comboBoxName.getItemAt(i)) != null) {
			if (item.equals(name)) {
				return i;
			}
			i++;
		}
		return -1;
	}
	
    private void updateFilter() {
        RowFilter<TableModel, Object> rf = null;
        //If current expression doesn't parse, don't update.
        try {
        	if(showingQueries==false)
              rf = RowFilter.regexFilter(errorfilter.getText(), 1);
        	else
              rf = RowFilter.regexFilter(errorfilter.getText(), 0);
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
        sorter.setRowFilter(rf);
    }
}
