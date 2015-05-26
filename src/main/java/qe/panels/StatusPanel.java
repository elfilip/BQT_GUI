package qe.panels;

import java.awt.Color;
import java.awt.Font;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

@SuppressWarnings("serial")
public class StatusPanel extends JPanel {

    private JTextField status;
    private JLabel statusLabel;
    
    public StatusPanel() {
        super();
        init();
    }
    
    private void init(){
        status = new JTextField();
        status.setEditable(false);
        status.setFont(status.getFont().deriveFont(Font.BOLD, 20));
        status.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        statusLabel = new JLabel("Status");
        
        GroupLayout gl = new GroupLayout(this);
        gl.setAutoCreateContainerGaps(false);
        gl.setAutoCreateGaps(true);
        gl.setVerticalGroup(gl.createSequentialGroup()
                .addComponent(statusLabel)
                .addComponent(status, 35, 35, 35));
        gl.setHorizontalGroup(gl.createParallelGroup()
            .addComponent(statusLabel)
            .addComponent(status));
        setLayout(gl);
    }
    
    /**
     * Sets text and foreground color for status label.
     * 
     * @param statusText
     * @param bg
     */
    public void setStatus(String statusText, Color bg) {
        status.setText(statusText);
        status.setForeground(bg);
    }
    
    /**
     * Sets status to status panel.
     * 
     * @param status
     */
    public void setStatus(String status){
        setStatus(status, Color.BLACK);
    }
}












