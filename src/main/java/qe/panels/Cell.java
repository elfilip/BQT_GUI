package qe.panels;

import java.awt.Color;
import java.math.BigDecimal;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

/**
 * The cell in the table;
 * 
 * @author jdurani
 *
 */
public class Cell extends JPanel {

    private static final long serialVersionUID = -409413476746932759L;
    
    /**
     * Text this cell contains.
     */
    private String text;
    /**
     * If this cell is editable or not.
     */
    private boolean editable;
    
    /**
     * Creates new cell.
     * 
     * @param text text for this cell
     * @param editable if this cell should be editable or not
     */
    public Cell(String text, boolean editable){
        super();
        this.text = text;
        this.editable = editable;
        init();
    }
    
    /**
     * Initializes this cell;
     */
    private void init(){
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        add(new JLabel(text));
    }
    
    /**
     * Determines if this cell contains same text as another cell. Number are compared as number.
     * Method uses {@link String#trim()} method befor comparing numbers (for now, there is no
     * explicit declaration of type of texts - text, number, ...).
     * 
     * @param c another cell
     * @param ignoreCase true if strings should be compared by {@link String#equalsIgnoreCase(String)}
     * @return true if this cell contains same text as cell {@code c}
     */
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
    
    /**
     * Highlights this cell.
     */
    public void highlight(){
        setBackground(Color.GRAY);
    }
}









