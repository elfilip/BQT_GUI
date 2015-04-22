package qe.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
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
	public static final void setToolTipText(JComponent component, String text){
        component.setToolTipText(text);
        ToolTipManager.sharedInstance().registerComponent(component);
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
}
