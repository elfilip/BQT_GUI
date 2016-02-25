package qe.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import qe.entity.result.QueryFailure;

/**
 * Frame for all errors of specific query
 * 
 * @author felias
 *
 */
public class ErrorsFrame {
	private String title;
	private JTextArea textArea;

	public ErrorsFrame(String title) {
		frame = new JFrame();

		this.title = title;
		initialize();
	}

	JFrame frame;

	/**
	 * Initializes error frame
	 * 
	 * @return
	 */
	public ErrorsFrame initialize() {
	    frame.setLocationByPlatform(true);
	    frame.setSize(900, 449);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setTitle(title);
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane);

		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		return this;
	}

	/**
	 * Sets query errors
	 * 
	 * @param failure
	 * @return
	 */
	public ErrorsFrame setErrors(QueryFailure failure) {
		StringBuilder builder = new StringBuilder();
		for (String error : failure.getCompareErrors()) {
			builder.append(error).append('\n').append('\n');
		}

		textArea.setText(builder.toString());
		return this;
	}

	public ErrorsFrame show() {
		frame.setVisible(true);
		return this;
	}

}
