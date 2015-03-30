package qe.parsing.dom;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import qe.entity.result.QueryFailure;
import qe.exception.ResultParsingException;

/**
 * XML DOM parser to load data from compare errors file
 * @author felias
 *
 */
public class DomParserFailure {

	private final String ACTUAL_EXCEPTION = "actualException";
	private final String ACTUAL_QUERY_RESULTS = "actualQueryResults";
	private final String EXPECTED_EXCEPTION = "expectedException";
	private final String EXPECTED_QUERY_RESULTS = "expectedQueryResults";
	private final String QUERY_RESULTS = "queryResults";
	private final String FAILURE_MESSAGE = "failureMessage";

	Document doc;
	QueryFailure failure;
	File file;

	/**
	 * 
	 * @param file path to compare errors file
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public DomParserFailure(File file) throws ParserConfigurationException, SAXException, IOException {
		this.file = file;
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		doc = dBuilder.parse(file);
		failure = new QueryFailure();
	}

	/**
	 * Load query, query name, compare errors, actual result, expected result from the compare errors xml file
	 * @return
	 * @throws ResultParsingException
	 * @throws TransformerException
	 */
	public QueryFailure parseCompareErrorFile() throws ResultParsingException, TransformerException {
		failure.setQuery(getQuery());
		failure.setQueryName(getQueryName());
		failure.setCompareErrors(getFailureMessages());
		failure.setActualResult(getActualResult());
		failure.setExpectedResult(getExpectedResult());
		failure.setFileName(file.getName());
		return failure;
	}
	private String getQueryName() throws ResultParsingException{
		NodeList list = doc.getElementsByTagName(QUERY_RESULTS);
		if (list.getLength() != 1) {
			throw new ResultParsingException("Exactly one " + QUERY_RESULTS + " element must be in the " + file.getAbsolutePath());
		}
		Node query = list.item(0);
		Node attribute = query.getAttributes().getNamedItem("name");
		if (attribute == null) {
			throw new ResultParsingException("Attribute 'name' is missing in the " + QUERY_RESULTS + " element in " + file.getAbsolutePath());
		}
		return attribute.getNodeValue();
	}

	private String getQuery() throws ResultParsingException {
		NodeList list = doc.getElementsByTagName(QUERY_RESULTS);
		if (list.getLength() != 1) {
			throw new ResultParsingException("Exactly one " + QUERY_RESULTS + " element must be in the " + file.getAbsolutePath());
		}
		Node query = list.item(0);
		Node attribute = query.getAttributes().getNamedItem("value");
		if (attribute == null) {
			throw new ResultParsingException("Attribute 'value' is missing in the " + QUERY_RESULTS + " element in " + file.getAbsolutePath());
		}
		return attribute.getNodeValue();
	}

	private LinkedList<String> getFailureMessages() throws ResultParsingException {
		NodeList list = doc.getElementsByTagName(FAILURE_MESSAGE);
		LinkedList<String> errors = new LinkedList<String>();
		if (list.getLength() == 0) {
			throw new ResultParsingException("No failure message found in " + file.getAbsolutePath());
		}
		for (int i = 0; i < list.getLength(); i++) {
			errors.add(list.item(i).getTextContent());
		}
		return errors;
	}

	private String getActualResult() throws ResultParsingException, TransformerException{
		NodeList list=doc.getElementsByTagName(ACTUAL_QUERY_RESULTS);
		if(list.getLength()==0){
			list=doc.getElementsByTagName(ACTUAL_EXCEPTION);
			if(list.getLength()==0){
				throw new ResultParsingException("No actual result in "+file.getAbsolutePath());
			}
		}
        return convertElementToString(list.item(0));
	}

	private String convertElementToString(Node node) throws TransformerException {
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer = transFactory.newTransformer();
		StringWriter buffer = new StringWriter();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.transform(new DOMSource(node), new StreamResult(buffer));
		return buffer.toString();
	}
	
	private String getExpectedResult() throws ResultParsingException, TransformerException{
		NodeList list=doc.getElementsByTagName(EXPECTED_QUERY_RESULTS);
		if(list.getLength()==0){
			list=doc.getElementsByTagName(EXPECTED_EXCEPTION);
			if(list.getLength()==0){
				throw new ResultParsingException("No expected result in "+file.getAbsolutePath());
			}
		}
        return convertElementToString(list.item(0));
	}
	
	public static Node parseString(String xml) throws ParserConfigurationException, SAXException, IOException{
		InputSource is = new InputSource();
		 is.setCharacterStream(new StringReader(xml));
		 DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		 Document doc = db.parse(is);
		 return doc;
	}
	
}
