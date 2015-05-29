package qe.parsing.sax;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import qe.entity.result.QueryFailure;

import java.util.*;

/**
 * Sax parser to get query value from file
 * @author felias
 *
 */
public class SaxFailureHandler extends DefaultHandler {
private String queryValue;
	
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase("queryResults")) {

			queryValue=attributes.getValue("value");					
			throw new MySAXTerminatorException();
		}

	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {

	}

	public void characters(char ch[], int start, int length)
			throws SAXException {

	}

	public String getQueryValue() {
		return queryValue;
	}




}