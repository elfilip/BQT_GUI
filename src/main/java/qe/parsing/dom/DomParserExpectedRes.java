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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import qe.exception.ResultParsingException;
import qe.utils.Utils;

/**
 * XML DOM parser for replacing and modifying expected results
 * 
 * @author felias
 *
 */
public class DomParserExpectedRes {
	private static final Logger logger = LoggerFactory.getLogger(DomParserExpectedRes.class);

	File file;
	Document originalResult;
	private final String EXPECTED_EXCEPTION_ERR = "expectedException";
	private final String EXPECTED_QUERY_RESULTS_ERR = "expectedQueryResults";
	private final String EXPECTED_EXCEPTION = "exception";
	private final String EXPECTED_QUERY_RESULTS = "queryResults";
	private final String STACK_TRACE = "stackTrace";

	/**
	 * 
	 * @param file
	 *            Path to expected result XML of a query
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public DomParserExpectedRes(File file) throws ParserConfigurationException, SAXException, IOException {
		this.file = file;
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		originalResult = dBuilder.parse(file);
	}

	/**
	 * Replaces expected result with content of the parameter
	 * 
	 * @param node
	 *            new expected result
	 * 
	 * @throws ResultParsingException
	 */
	public void replaceExpectedResult(Document expResultDoc) throws ResultParsingException {
		String elementName;
		Node newResult=expResultDoc.getFirstChild();
		if (newResult.getNodeName().equals(EXPECTED_EXCEPTION_ERR)) {
			elementName = EXPECTED_EXCEPTION;
			NodeList stackTrace =expResultDoc.getElementsByTagName(STACK_TRACE); //removing stacktrace element, it shouldn't be in the expected result
			if(stackTrace.getLength()>0){
				newResult.removeChild(stackTrace.item(0));
			}
			NodeList list = originalResult.getElementsByTagName(elementName);			
			if (list.getLength() > 0) {
				Node imported = originalResult.importNode(newResult, true);
				list.item(0).getParentNode().replaceChild(imported, list.item(0));
				originalResult.renameNode(imported, imported.getNamespaceURI(), "exception");
			} else {
				list = originalResult.getElementsByTagName(EXPECTED_QUERY_RESULTS);
				if (list.getLength() == 0) {
					throw new ResultParsingException("Expected result doesn't contains element " + elementName + ": " + file.getAbsolutePath());
				}
				Node imported = originalResult.importNode(newResult, true);
				while (list.item(0).hasChildNodes())
					list.item(0).removeChild(list.item(0).getFirstChild());
				list.item(0).appendChild(imported);
				originalResult.renameNode(imported, imported.getNamespaceURI(), "exception");
			}

		} else if (newResult.getNodeName().equals(EXPECTED_QUERY_RESULTS_ERR)) {
			elementName = EXPECTED_QUERY_RESULTS;
			NodeList expectedResult = originalResult.getElementsByTagName(EXPECTED_QUERY_RESULTS);
			if (expectedResult.getLength() != 1) {
				throw new ResultParsingException("Expected result is malformed: no element " + EXPECTED_QUERY_RESULTS + ": " + file.getAbsolutePath());
			}
			while (expectedResult.item(0).hasChildNodes())
				expectedResult.item(0).removeChild(expectedResult.item(0).getFirstChild());
			NodeList newExpectedResult = newResult.getChildNodes();
			for (int i = 0; i < newExpectedResult.getLength(); i++) {
				Node importedchild = originalResult.importNode(newExpectedResult.item(i), true);
				expectedResult.item(0).appendChild(importedchild);
			}
		} else {
			throw new ResultParsingException("Expected result is malformed: no element " + EXPECTED_EXCEPTION_ERR + " or " + EXPECTED_QUERY_RESULTS_ERR);
		}
	}

	/**
	 * Writes modified expected results into a file, overwites the original file
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws TransformerException
	 * @throws XPathExpressionException
	 */
	public void writeXMLdocument() throws FileNotFoundException, IOException, TransformerException, XPathExpressionException {
		logger.debug("Writing new expected result into file:" + file.getAbsolutePath());
		Utils.printDocument(originalResult, new FileOutputStream(file));
	}
}
