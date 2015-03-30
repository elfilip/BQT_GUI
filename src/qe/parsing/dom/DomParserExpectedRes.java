package qe.parsing.dom;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import qe.exception.ResultParsingException;
import qe.utils.Utils;

/**
 * XML DOM parser for replacing and modifying expected results
 * @author felias
 *
 */
public class DomParserExpectedRes {
	private static final Logger logger = Logger.getLogger(DomParserExpectedRes.class);
	
	File file;
	Document doc;
	private final String EXPECTED_EXCEPTION_ERR = "expectedException";
	private final String EXPECTED_QUERY_RESULTS_ERR = "expectedQueryResults";
	private final String EXPECTED_EXCEPTION = "exception";
	private final String EXPECTED_QUERY_RESULTS = "queryResults";
	
	/**
	 * 
	 * @param file Path to expected result XML of a query
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public DomParserExpectedRes(File file) throws ParserConfigurationException, SAXException, IOException{
		this.file = file;
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		doc = dBuilder.parse(file);
	}
	
	/**
	 * Replaces expected result with content of the parameter
	 * @param node new expected result
	 * @throws ResultParsingException
	 */
	public void replaceExpectedResult(Node node) throws ResultParsingException{
		String elementName;
		if(node.getNodeName().equals(EXPECTED_EXCEPTION_ERR)){
			elementName=EXPECTED_EXCEPTION;
			NodeList list = doc.getElementsByTagName(elementName);
			if(list.getLength()==0){
				throw new ResultParsingException("Expected result doesn't contains element "+elementName+": "+file.getAbsolutePath());
			}
			Node imported=doc.importNode(node,true);
			list.item(0).getParentNode().replaceChild(imported, list.item(0));
			doc.renameNode(imported, imported.getNamespaceURI(), "exception");
		}else if(node.getNodeName().equals(EXPECTED_QUERY_RESULTS_ERR)){
			
			elementName=EXPECTED_QUERY_RESULTS;
			NodeList expectedResult=doc.getElementsByTagName(EXPECTED_QUERY_RESULTS);
			if(expectedResult.getLength()!=1){
				throw new ResultParsingException("Expected result is malformed: no element "+EXPECTED_QUERY_RESULTS+": "+file.getAbsolutePath());
			}
			NodeList childNodes=expectedResult.item(0).getChildNodes();
			while (expectedResult.item(0).hasChildNodes())
				expectedResult.item(0).removeChild(expectedResult.item(0).getFirstChild());
			/*for(int i=0;i<childNodes.getLength();i++){
			//	if (childNodes.item(i) instanceof Element == false)
					expectedResult.item(0).removeChild(childNodes.item(i));
					//  continue;
			}*/
			NodeList newExpectedResult =node.getChildNodes();
			for(int i=0;i<newExpectedResult.getLength();i++){
				Node importedchild=doc.importNode(newExpectedResult.item(i), true);
				expectedResult.item(0).appendChild(importedchild);
			}
		}else{
			throw new ResultParsingException("Expected result is malformed: no element "+EXPECTED_EXCEPTION_ERR+" or "+EXPECTED_QUERY_RESULTS_ERR);
		}
	}
	/**
	 * Writes modified expected results into a file, overwites the original file
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws TransformerException
	 * @throws XPathExpressionException
	 */
	public void writeXMLdocument() throws FileNotFoundException, IOException, TransformerException, XPathExpressionException{
		logger.debug("Writing new expected result into file:"+file.getAbsolutePath());
		Utils.printDocument(doc, new FileOutputStream(file));
	}
}
