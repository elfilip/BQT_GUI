package qe.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

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
        

        Action escapeAction = new AbstractAction() {
            // close the frame when the user presses escape
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        };
        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), "ESCAPE");
        frame.getRootPane().getActionMap().put("ESCAPE", escapeAction);


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
