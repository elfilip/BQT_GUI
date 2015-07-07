package qe.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

/**
 * The cell in the table;
 * 
 * @author jdurani
 *
 */
public class Cell extends JTextField {

    private static final long serialVersionUID = -409413476746932759L;
    
    /**
     * Basic background color.
     */
    private static final Color BASIC_BACKGROUND = Color.WHITE;
    /**
     * Highlighted background color.
     */
    private static final Color HIGHLIGHTED_BACKGROUND = Color.CYAN;
    
    /**
     * Text this cell contains.
     */
    private String text;
    
    /**
     * If this cell is highlighted or not (its background color is {@link #HIGHLIGHTED_BACKGROUND}
     */
    private boolean isHighlighted;
    
    /**
     * Binded cell.
     */
    private Cell bindedCell;
    
    /**
     * Creates new cell.
     * 
     * @param text text for this cell
     */
    public Cell(String text){
        super();
        this.text = text;
        init();
    }
    
    /**
     * Initializes this cell;
     */
    private void init(){
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        setText(text);
        setEditable(false);
        setHorizontalAlignment(CENTER);
        setFont(getFont().deriveFont(Font.BOLD, 15));
        setBackground(BASIC_BACKGROUND);
        isHighlighted = false;
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                highlight();
            }
        });
    }
    
    /**
     * Sets binded cell for this cell.
     * 
     * @param c binded cell
     */
    public void setBindedCell(Cell c){
        bindedCell = c;
    }
    
    /**
     * Highlights this cell and binded cell (if not {@code null}).
     */
    public void highlight(){
        innerHighlight(true);
    }
    
    private void innerHighlight(boolean highlightBindedCell){
        if(isHighlighted){
            setBackground(BASIC_BACKGROUND);
            setText(text);
            isHighlighted = false;
        } else {
            setBackground(HIGHLIGHTED_BACKGROUND);
            setText("'" + text + "'");
            isHighlighted = true;
        }
        if(highlightBindedCell && bindedCell != null){
            bindedCell.innerHighlight(false);
        }
    }
}









