package qe.panels;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Group;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EtchedBorder;

import qe.jenkins.JenkinsActiveConfiguration;
import qe.jenkins.JenkinsBuild;
import qe.jenkins.JenkinsJob;
import qe.utils.Utils;

/**
 * Panel for jenkins build. Contains build numbers and matrix for each build.
 * 
 * @author jdurani
 *
 */
public class JenkinsBuildPanel extends JPanel {

    private static final long serialVersionUID = 3414694028944335467L;

    /**
     * Name of job
     */
    private String jobName;

    /**
     * Button group for radio buttons for builds.
     */
    private ButtonGroup buildNumberGroup = new ButtonGroup();

    /**
     * Actual matrix of active configurations.
     */
    private MatrixHolder matrix = new MatrixHolder();

    /**
     * {@inheritDoc}
     */
    public JenkinsBuildPanel(){
        super();
    }

    /**
     * Returns name of actual job.
     * @return
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * Inits this build panel according to jenkins job. 
     * @param job
     */
    public void init(JenkinsJob job){
        this.jobName = job.getName();
        removeAll();
        for(Enumeration<AbstractButton> e = buildNumberGroup.getElements(); e.hasMoreElements(); ){
            buildNumberGroup.remove(e.nextElement());
        }
        for(Enumeration<AbstractButton> e = matrix.buttonGroup.getElements(); e.hasMoreElements(); ){
            matrix.buttonGroup.remove(e.nextElement());
        }
        JRadioButton[] buildButton = new JRadioButton[job.getBuilds().size()];
        int i = 0;
        List<JenkinsBuild> builds = new ArrayList<>(job.getBuilds());
        Collections.sort(builds);
        for(final JenkinsBuild jb : builds){
            JRadioButton b = new JRadioButton(jb.getBuildNumber());
            final MatrixHolder matrixHolder = getMatrix(jb);
            b.addItemListener(new ItemListener() {
                
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if(e.getStateChange() == ItemEvent.SELECTED){
                        fillMatrix(matrixHolder);
                    }
                }
            });
            buildNumberGroup.add(b);
            buildButton[i++] = b;
        }
        JLabel buildNumLab = new JLabel("Build number");
        GroupLayout gl = new GroupLayout(this);
        Group horBuilds = gl.createParallelGroup()
            .addComponent(buildNumLab);
        Group verBuilds = gl.createSequentialGroup()
            .addComponent(buildNumLab)
            .addGap(25);
        for(JRadioButton b : buildButton){
            horBuilds.addComponent(b);
            verBuilds.addComponent(b);
        }
        gl.setHorizontalGroup(gl.createSequentialGroup()
            .addGroup(horBuilds)
            .addGap(30)
            .addComponent(matrix.matrix));
        gl.setVerticalGroup(gl.createParallelGroup()
            .addGroup(verBuilds)
            .addComponent(matrix.matrix));
        gl.setAutoCreateContainerGaps(true);
        gl.setAutoCreateGaps(true);
        setLayout(gl);
    }

    /**
     * Returns matrix of active configurations of build {@code jb}.
     * 
     * @param jb
     * @return
     */
    private MatrixHolder getMatrix(JenkinsBuild jb){
        MatrixHolder holder = new MatrixHolder();
        
        String xLab = jb.getxLabel();
        String yLab = jb.getyLabel();
        Set<JenkinsActiveConfiguration> jacSet = jb.getActiveConfigurations();
        List<String> xVals = new ArrayList<>();
        List<String> yVals = new ArrayList<>();
        for(JenkinsActiveConfiguration jac : jacSet){
            String xv = jac.getxValue();
            String yv = jac.getyValue();
            if(!xVals.contains(xv)){ xVals.add(xv); }
            if(!yVals.contains(yv)){ yVals.add(yv); }
        }
        Collections.sort(xVals);
        Collections.sort(yVals);
        JComponent[][] components = new JComponent[yVals.size() + 1][xVals.size() + 1];
        components[0][0] = new JLabel(yLab + "\\" + xLab);
        for(int i = 0; i < xVals.size(); i++){
            components[0][i+1] = new JLabel(xVals.get(i));
        }
        for(int i = 0; i < yVals.size(); i++){
            components[i+1][0] = new JLabel(yVals.get(i));
        }
        for(JenkinsActiveConfiguration jac : jacSet){
            String xv = jac.getxValue();
            String yv = jac.getyValue();
            int xPos = xVals.indexOf(xv);
            int yPos = yVals.indexOf(yv);
            RadioButtonWithProps rbwp = new RadioButtonWithProps(jac.getUrl(), xv, yv);
            JPanel p = new JPanel();
            p.add(rbwp);
            holder.buttonGroup.add(rbwp);
            components[yPos + 1][xPos + 1] = p;
        }
        for(JComponent[] cs : components){
            for(int i = 0; i < cs.length; i++){
                if(cs[i] == null){
                    cs[i] = new JPanel();
                }
                cs[i].setBorder(new EtchedBorder(EtchedBorder.LOWERED));
              }
        }
        Utils.buildTable(holder.matrix, components);
        return holder;
    }

    /**
     * Replaces actual matrix of active configurations {@link #matrix} 
     * with new matrix {@code newMatrix}.
     * 
     * @param newMatrix
     */
    private void fillMatrix(MatrixHolder newMatrix){
        GroupLayout gl = (GroupLayout)getLayout();
        gl.replace(matrix.matrix, newMatrix.matrix);
        matrix = newMatrix;
    }

    /**
     * Returns selected build number or null if none is selected.
     * 
     * @return
     */
    public String getSelectedBuildNumber(){
        JRadioButton b = (JRadioButton)getSelectedButton(buildNumberGroup);
        return b == null ? null : b.getText();
    }

    /**
     * Returns value on X-axis of selected active configurations or null if none is selected.
     * 
     * @return
     */
    public String getSelectedXValue(){
        RadioButtonWithProps b = (RadioButtonWithProps)getSelectedButton(matrix.buttonGroup);
        return b == null ? null : b.xValue;
    }

    /**
     * Returns value on Y-axis of selected active configurations or null if none is selected.
     * 
     * @return
     */
    public String getSelectedYValue(){
        RadioButtonWithProps b = (RadioButtonWithProps)getSelectedButton(matrix.buttonGroup);
        return b == null ? null : b.yValue;
    }

    /**
     * Returns URL of selected active configurations or null if none is selected.
     * 
     * @return
     */
    public String getUrlOfSelectedNode(){
        RadioButtonWithProps b = (RadioButtonWithProps)getSelectedButton(matrix.buttonGroup);
        return b == null ? null : b.url;
    }
    
    /**
     * Returns selected button of group {@code bg} or null if none is selected.
     * 
     * @param bg
     * @return
     */
    private AbstractButton getSelectedButton(ButtonGroup bg){
        for(Enumeration<AbstractButton> e = bg.getElements(); e.hasMoreElements(); ){
            AbstractButton ab = e.nextElement();
            if(ab.isSelected()){
                return ab;
            }
        }
        return null;
    }

    /**
     * JRadioButton with several properties.
     * 
     * @author jdurani
     *
     */
    @SuppressWarnings("serial")
    private class RadioButtonWithProps extends JRadioButton{
        private final String url;
        private final String xValue;
        private final String yValue;
        
        public RadioButtonWithProps(String url, String xValue, String yValue) {
            super();
            this.url = url;
            this.xValue = xValue;
            this.yValue = yValue;
        }
    }
    
    private class MatrixHolder{
        private JPanel matrix = new JPanel();
        private ButtonGroup buttonGroup = new ButtonGroup();
    }
}


















