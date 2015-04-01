package qe.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qe.entity.settings.Settings;

/**
 * Panel for configuring the application
 * 
 * @author felias
 *
 */
public class PanelSettings extends TabbedPanel {
	private static final Logger logger = LoggerFactory.getLogger(PanelSettings.class);

	private JTextField textField;
	private JTextField repositorySettingsTextfield;

	public PanelSettings() {
		super();
	}

	public void initialize() {
		logger.debug("Initializing panel Settings");
		// Panel for settings
		getParent();
		panel.setLayout(new GridBagLayout());

		// Label Path to test results:
		GridBagConstraints c4 = new GridBagConstraints();
		c4.fill = GridBagConstraints.NONE;
		c4.weightx = 0;
		c4.weighty = 0;
		c4.gridx = 0;
		c4.anchor = GridBagConstraints.LINE_START;
		c4.gridy = 0;
		c4.gridwidth = 2;
		c4.gridheight = 1;
		JLabel lblNewLabel_1 = new JLabel("Path to test results:");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(lblNewLabel_1, c4);

		// Text field for Path to test results
		GridBagConstraints c5 = new GridBagConstraints();
		c5.fill = GridBagConstraints.HORIZONTAL;
		c5.weightx = 1;
		c5.weighty = 1;
		c5.gridx = 3;
		c5.anchor = GridBagConstraints.CENTER;
		c5.gridy = 0;
		c5.gridwidth = 3;
		c5.gridheight = 1;
		textField = new JTextField();
		textField.setToolTipText("The folder with Summary_totals.txt");
		textField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				Settings.getInstance().setPathToTestResults(textField.getText());
				logger.info("Path to test results is set to" + textField.getText());
			}
		});
		panel.add(textField, c5);
		if (Settings.getInstance().getPathToTestResults() != null) {
			textField.setText(Settings.getInstance().getPathToTestResults());
		}
		textField.setColumns(10);

		// Choose button with file chooser
		GridBagConstraints c6 = new GridBagConstraints();
		c6.fill = GridBagConstraints.NONE;
		c6.weightx = 0;
		c6.weighty = 0;
		c6.gridx = 4;
		c6.anchor = GridBagConstraints.LINE_END;
		c6.gridy = 0;
		c6.gridwidth = 1;
		c6.gridheight = 1;
		JButton btnChoose = new JButton("Choose...");
		btnChoose.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setAcceptAllFileFilterUsed(false);
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.setAcceptAllFileFilterUsed(false);
				if ((fc.showDialog(rootFrame, "Open")) == JFileChooser.APPROVE_OPTION) {
					textField.setText(fc.getSelectedFile().getAbsolutePath());
					Settings.getInstance().setPathToTestResults(textField.getText());
					logger.info("Path to test results is set to" + textField.getText());
				}
			}
		});
		panel.add(btnChoose);

		// Label Path to test repository:
		GridBagConstraints c11 = new GridBagConstraints();
		c11.fill = GridBagConstraints.NONE;
		c11.weightx = 0;
		c11.weighty = 0;
		c11.gridx = 0;
		c11.anchor = GridBagConstraints.CENTER;
		c11.gridy = 1;
		c11.gridwidth = 2;
		c11.gridheight = 1;
		JLabel repositoryLabel = new JLabel("Path to test repository:");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(repositoryLabel, c11);

		// Text field for Path to test repository
		GridBagConstraints c12 = new GridBagConstraints();
		c12.fill = GridBagConstraints.HORIZONTAL;
		c12.weightx = 1;
		c12.weighty = 1;
		c12.gridx = 3;
		c12.anchor = GridBagConstraints.CENTER;
		c12.gridy = 1;
		c12.gridwidth = 3;
		c12.gridheight = 1;
		repositorySettingsTextfield = new JTextField();
		repositorySettingsTextfield.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				Settings.getInstance().setPathToRepository(repositorySettingsTextfield.getText());
				logger.info("Path to test repository is set to" + repositorySettingsTextfield.getText());
			}
		});
		panel.add(repositorySettingsTextfield, c12);
		if (Settings.getInstance().getPathToRepository() != null) {
			repositorySettingsTextfield.setText(Settings.getInstance().getPathToRepository());
		}
		repositorySettingsTextfield.setColumns(10);

		// Choose button with File chooser
		GridBagConstraints c13 = new GridBagConstraints();
		c13.fill = GridBagConstraints.NONE;
		c13.weightx = 0;
		c13.weighty = 0;
		c13.gridx = 6;
		c13.anchor = GridBagConstraints.CENTER;
		c13.gridy = 1;
		c13.gridwidth = 1;
		c13.gridheight = 1;
		JButton btnSettingsRepository = new JButton("Choose...");
		btnSettingsRepository.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.setAcceptAllFileFilterUsed(false);
				if ((fc.showDialog(rootFrame, "Open")) == JFileChooser.APPROVE_OPTION) {
					repositorySettingsTextfield.setText(fc.getSelectedFile().getAbsolutePath());
					Settings.getInstance().setPathToRepository(repositorySettingsTextfield.getText());
					logger.info("Path to test repository is set to" + repositorySettingsTextfield.getText());
				}
			}
		});
		panel.add(btnSettingsRepository, c13);
		logger.debug("Panel setting initialized");

	}

}
