package qe.panels;

import java.awt.Color;
import java.math.BigDecimal;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

public class Cell extends JPanel {

    private String text;
    private boolean editable;
    
    public Cell(String text, boolean editable){
        super();
        this.text = text;
        this.editable = editable;
        init();
    }
    
    private void init(){
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        add(new JLabel(text));
    }
    
    public boolean equals(Cell c, boolean ignoreCase){
        if(c == null){
            return false;
        }
        if(this.text == null){
            return c.text == null;
        }
        try{
            BigDecimal bdThis = new BigDecimal(this.text.trim());
            BigDecimal bdC = new BigDecimal(c.text.trim());
            return bdThis.compareTo(bdC) == 0;
        } catch (NumberFormatException ex){
            // ignore; next code will handle it
        } catch (NullPointerException ex){
            // in case c.text == null
            return false;
        }
        return ignoreCase ? this.text.equalsIgnoreCase(c.text) : this.text.equals(c.text);
    }
    
    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
    }
}
