package qe.gui.highlight;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;
import javax.swing.text.PlainView;
import javax.swing.text.Segment;
import javax.swing.text.Utilities;
/**
 * 
 * @author felias
 *
 *View which highlights XML syntax
 */
public class XMLView extends PlainView {
	private static HashMap<Pattern, Color> patternColors;
	private static final String TAG_PATTERN = "(</?[a-zA-Z]+[a-zA-Z0-9]*)\\s?>?";
	private static final String TAG_END_PATTERN = "(/>)";
	private static final String TAG_ATTRIBUTE_PATTERN = "\\s(\\w*)\\=";
	private static final String TAG_ATTRIBUTE_VALUE = "[a-z-]*\\=(\"[^\"]*\")";
	private static final String TAG_COMMENT = "(<!--.*-->)";
	private static final String TAG_CDATA_START = "(\\<!\\[CDATA\\[).*";
	private static final String TAG_CDATA_END = ".*(]]>)";
	static {
		setColors();
	}

	public XMLView(Element elem) {
		super(elem);
		getDocument().putProperty(PlainDocument.tabSizeAttribute, 4);
	}

	public static void setColors() {
		patternColors = new HashMap<Pattern, Color>();
		patternColors.put(Pattern.compile(TAG_CDATA_START), new Color(128, 128, 128));
		patternColors.put(Pattern.compile(TAG_CDATA_END), new Color(128, 128, 128));
		patternColors.put(Pattern.compile(TAG_PATTERN), new Color(7, 138, 70));
		patternColors.put(Pattern.compile(TAG_ATTRIBUTE_PATTERN), new Color(127, 0, 127));
		patternColors.put(Pattern.compile(TAG_END_PATTERN), new Color(7, 138, 70));
		patternColors.put(Pattern.compile(TAG_ATTRIBUTE_VALUE), new Color(42, 0, 255));
		patternColors.put(Pattern.compile(TAG_COMMENT), new Color(63, 95, 191));
	}
	
	 @Override
	    protected int drawUnselectedText(Graphics graphics, int x, int y, int p0,
	            int p1) throws BadLocationException {
	 
	        Document doc = getDocument();
	        String text = doc.getText(p0, p1 - p0);
	 
	        Segment segment = getLineBuffer();
	 
	        SortedMap<Integer, Integer> startMap = new TreeMap<Integer, Integer>();
	        SortedMap<Integer, Color> colorMap = new TreeMap<Integer, Color>();
	 
	        // Match all regexes on this snippet, store positions
	        for (Map.Entry<Pattern, Color> entry : patternColors.entrySet()) {
	 
	            Matcher matcher = entry.getKey().matcher(text);
	 
	            while (matcher.find()) {
	                startMap.put(matcher.start(1), matcher.end());
	                colorMap.put(matcher.start(1), entry.getValue());
	            }
	        }
	 
	        // TODO: check the map for overlapping parts
	         
	        int i = 0;
	 
	        // Colour the parts
	        for (Map.Entry<Integer, Integer> entry : startMap.entrySet()) {
	            int start = entry.getKey();
	            int end = entry.getValue();
	 
	            if (i < start) {
	                graphics.setColor(Color.black);
	                doc.getText(p0 + i, start - i, segment);
	                x = Utilities.drawTabbedText(segment, x, y, graphics, this, i);
	            }
	 
	            graphics.setColor(colorMap.get(start));
	            i = end;
	            doc.getText(p0 + start, i - start, segment);
	            x = Utilities.drawTabbedText(segment, x, y, graphics, this, start);
	        }
	 
	        // Paint possible remaining text black
	        if (i < text.length()) {
	            graphics.setColor(Color.black);
	            doc.getText(p0 + i, text.length() - i, segment);
	            x = Utilities.drawTabbedText(segment, x, y, graphics, this, i);
	        }
	 
	        return x;
	    }
}
