package qe.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import qe.entity.result.QueryFailure;
import qe.log.appender.GUIAppender;

public class LoggingFrame {

	private JTextArea textArea;

	public LoggingFrame( ) {
		frame = new JFrame();
		initialize();
	}

	JFrame frame;

	public LoggingFrame initialize() {
		frame.setBounds(90, 90, 682, 449);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setTitle("Log");
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane);
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		GUIAppender.setArea(textArea);
		return this;
	}
	
	public LoggingFrame setErrors(QueryFailure failure){
		StringBuilder builder = new StringBuilder();
		for (String error : failure.getCompareErrors()) {
			builder.append(error).append('\n').append('\n');
		}

		textArea.setText(builder.toString());
		return this;
	}
	
	public LoggingFrame show(){
		frame.setVisible(true);
		return this;
	}

}
