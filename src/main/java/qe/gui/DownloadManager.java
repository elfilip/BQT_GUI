package qe.gui;

import java.awt.Dimension;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Group;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import qe.panels.StatusPanel;
import qe.utils.Utils;

public class DownloadManager extends JFrame{

    private static final long serialVersionUID = 814990201706953568L;

    private List<Holder> statuses = new ArrayList<>();
    private JPanel mainPanel = new JPanel();
    private JScrollPane main = Utils.getScrollPane(mainPanel);
    
    public DownloadManager() {
        super();
        mainPanel.setLayout(init(mainPanel));
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().add(main);
        main.setPreferredSize(new Dimension(1000, 500));
        setTitle("Download manager.");
    }
    
    private LayoutManager init(JPanel panel){
        GroupLayout gl = new GroupLayout(panel);
        Group ver = gl.createSequentialGroup();
        for(Holder h : statuses){
            ver.addGroup(gl.createParallelGroup()
                    .addComponent(h.status)
                    .addComponent(h.cancel));
        }
        Group hor = gl.createParallelGroup();
        for(Holder h : statuses){
            hor.addGroup(gl.createSequentialGroup()
                .addComponent(h.status, 500, 500, 2500)
                .addGap(20)
                .addComponent(h.cancel, 100, 100, 100));
            gl.linkSize(SwingConstants.VERTICAL, h.status, h.cancel);
        }
        gl.setAutoCreateContainerGaps(true);
        gl.setVerticalGroup(ver);
        gl.setHorizontalGroup(hor);
        return gl;
    }
    
    public synchronized void addDownloadItem(StatusPanel sp, JButton cancelButton){
        statuses.add(0, new Holder(sp, cancelButton));
        mainPanel.setLayout(init(mainPanel));
        mainPanel.repaint();
    }
    
    private class Holder{
        private StatusPanel status;
        private JButton cancel;
        
        public Holder(StatusPanel sp, JButton b) {
            super();
            this.status = sp;
            this.cancel = b;
        }
        
        @Override
        public String toString() {
            return status + "///" + cancel;
        }
    }
}
