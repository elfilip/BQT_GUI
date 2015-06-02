package qe.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.Group;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.logging.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Util class for general static methods
 * @author felias
 *
 */
public class Utils {
	private static final Logger logger = LoggerFactory.getLogger(Utils.class);
	/**
	 * Prints xml document into output stream
	 * @param doc xml document to be printed
	 * @param out output stream to be written to
	 * @throws IOException
	 * @throws TransformerException
	 * @throws XPathExpressionException
	 */
	public static void printDocument(Document doc, OutputStream out) throws IOException, TransformerException, XPathExpressionException {
		doc.normalize();
		XPathFactory xpathFactory = XPathFactory.newInstance();
		// XPath to find empty text nodes.
		XPathExpression xpathExp = xpathFactory.newXPath().compile(
		    	"//text()[normalize-space(.) = '']");  
		NodeList emptyTextNodes = (NodeList) 
		        xpathExp.evaluate(doc, XPathConstants.NODESET);

		// Remove each empty text node from document.
		for (int i = 0; i < emptyTextNodes.getLength(); i++) {
		    Node emptyTextNode = emptyTextNodes.item(i);
		    emptyTextNode.getParentNode().removeChild(emptyTextNode);
		}
		TransformerFactory tf = TransformerFactory.newInstance();
	    Transformer transformer = tf.newTransformer();
	    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
	    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

	    transformer.transform(new DOMSource(doc), 
	         new StreamResult(new OutputStreamWriter(out, "UTF-8")));
	}
	
	/**
	 * Sets tool tip text for the component.
	 * 
	 * @param component the component
	 * @param text text to be set
	 */
	public static void setToolTipText(JComponent component, String text){
        component.setToolTipText(text);
        ToolTipManager.sharedInstance().registerComponent(component);
    }
	
	/**
	 * Creates new {@link JScrollPane} with view {@code panel}.
	 * 
	 * @param panel
	 * @return
	 */
	public static JScrollPane getScrollPane(JComponent panel){
	    JScrollPane pane = new JScrollPane(panel);
	    pane.getVerticalScrollBar().setUnitIncrement(20);
	    pane.getHorizontalScrollBar().setUnitIncrement(20);
	    return pane;
	}
	
	/**
	 * Builds panel {@code tablePanel} as table and fills its cells with
	 * {@code comps}.
	 * 
	 * @param tablePanel table panel
	 * @param comps components to be set as cells
	 */
    public static void buildTable(JPanel tablePanel, JComponent[][] comps){
        GroupLayout gl = new GroupLayout(tablePanel);
        gl.setAutoCreateContainerGaps(false);
        gl.setAutoCreateGaps(false);
        // horizontal group
        int rows = comps.length;
        int columns = comps[0].length;
        Group hor = gl.createParallelGroup(Alignment.LEADING, false);
        for(int r = 0; r < rows; r++){
            Group horSeq = gl.createSequentialGroup();
            for(int c = 0; c < columns; c++){
                horSeq.addComponent(comps[r][c]);
            }
            hor.addGroup(horSeq);
        }
        gl.setHorizontalGroup(hor);
        // vertical group
        Group ver = gl.createParallelGroup(Alignment.LEADING, false);
        for(int c = 0; c < columns; c++){
            Group verSeq = gl.createSequentialGroup();
            for(int r = 0; r < rows; r++){
                verSeq.addComponent(comps[r][c]);
            }
            ver.addGroup(verSeq);
        }
        gl.setVerticalGroup(ver);
        
        linkSizeOfComponents(gl, comps);
        tablePanel.setLayout(gl);
    }
	
	/**
	 * Links size of components in matrix {@code comps}.
	 * 
	 * @param gl
	 * @param comps
	 */
	public static void linkSizeOfComponents(GroupLayout gl, JComponent[][] comps){
	    gl.linkSize(SwingConstants.VERTICAL, getAllTableAsArray(comps));
	    int columns = comps[0].length;
        for(int c = 0; c < columns; c++){
            gl.linkSize(SwingConstants.HORIZONTAL, getTableColumnAsArray(comps, c));
        }   
	}
	
	/**
	 * 
	 * @param comps
	 * @return all {@code comps} as one-dimensional array
	 */
    private static JComponent[] getAllTableAsArray(JComponent[][] comps){
        int rows = comps.length;
        int columns = comps[0].length;
        JComponent[] cells = new JComponent[rows * columns];
        for(int r = 0; r < rows; r++){
            System.arraycopy(comps[r], 0, cells, r * columns, columns);
        }
        return cells;
    }
    
    /**
     * 
     * @param comps
     * @param colIdx column index
     * @return column in {@code comps} as one-dimensional array
     */
    private static JComponent[] getTableColumnAsArray(JComponent[][] comps, int colIdx){
        int rows = comps.length;
        JComponent[] cells = new JComponent[rows];
        for(int r = 0; r < rows; r++){
            cells[r] = comps[r][colIdx];
        }
        return cells;
    }
	
    
    
	/**
	 * Shows message dialog and writes into log
	 * @param rootFrame parent for this dialog
	 * @param level Type of the message
	 * @param message Content of the message
	 * @param e Assigned throwable or null
	 */
	public static void showMessageDialog(JFrame rootFrame,Level level, String message, Throwable e){
		if(level==Level.INFO){
			logger.info(message,e);
			JOptionPane.showMessageDialog(rootFrame,message);
		}else if(level ==Level.DEBUG){
			logger.debug(message,e);
			JOptionPane.showMessageDialog(rootFrame, message,"DEBUG",JOptionPane.INFORMATION_MESSAGE);
		}else if(level==Level.ERROR){
			logger.error(message,e);
			JOptionPane.showMessageDialog(rootFrame, message,"ERROR",JOptionPane.ERROR_MESSAGE);
		}else if(level==Level.WARN){
			logger.warn(message,e);
			JOptionPane.showMessageDialog(rootFrame, message,"WARN",JOptionPane.WARNING_MESSAGE);
		}else{
			logger.debug(message,e);
		}
	}
	
	public static void replaceAll(StringBuilder builder, String from, String to)
	{
	    int index = builder.indexOf(from);
	    while (index != -1)
	    {
	        builder.replace(index, index + from.length(), to);
	        index += to.length(); 
	        index = builder.indexOf(from, index);
	    }
	}
}
