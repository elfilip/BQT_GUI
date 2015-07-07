package qe.panels;

import java.util.List;

import javax.swing.JPanel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qe.exception.ResultParsingException;
import qe.utils.Utils;

/**
 * Panel for basic table view.
 * 
 * @author jdurani
 *
 */
public class TablePanel extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = -6932438524346906661L;

    private static final Logger LOGGER = LoggerFactory.getLogger(TablePanel.class);
    
    /**
     * XML elements in error file.
     * 
     * @author jdurani
     *
     */
    private static interface TagNames {
        static final String ACTUAL_EXCEPTION = "actualException";
        static final String ACTUAL_QUERY_RESULTS = "actualQueryResults";
        static final String EXPECTED_EXCEPTION = "expectedException";
        static final String EXPECTED_QUERY_RESULTS = "expectedQueryResults";
        static final String TABLE = "table";
        static final String COLUMN_COUNT = "columnCount";
        static final String ROW_COUNT = "rowCount";
        static final String TABLE_ROW = "tableRow";
        static final String TABLE_CELL = "tableCell";
        static final String SELECT = "select";
        static final String DATA_ELEMENT = "dataElement";
        static final String TYPE = "type";
        static final String EXCEPTION_TYPE = "exceptionType";
        static final String MESSAGE = "message";
    }
    
    /**
     * Table panel shows a table. 
     */
    private static final int TYPE_TABLE = 0;
    /**
     * Table panel shows an exception.
     */
    private static final int TYPE_EXCEPTION = 1; 
    
    /**
     * Type of this table. Can be one of {@link #TYPE_TABLE}, {@link #TYPE_EXCEPTION}
     */
    private int type = -1;
    
    /**
     * Table.
     */
    private Cell[][] table;
    private int rows;
    private int columns;
    
    /**
     * This method parses an XML document {@code xml} and creates table.
     * 
     * @param xml XML document as string
     */
    public void parseXML(String xml){
        if(LOGGER.isTraceEnabled()){
            LOGGER.trace("Parsing xml: {}", xml);
        }
        if(xml == null || xml.isEmpty()){
            clearTable();
            return;
        }
        this.removeAll();
        try{
            Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
            if(LOGGER.isTraceEnabled()){
                LOGGER.trace("Parsed document: {}", doc);
            }
            Elements node = doc.getElementsByTag(TagNames.ACTUAL_QUERY_RESULTS);
            boolean isSet = false;
            if(!node.isEmpty()){
                isSet = true;
                type = TYPE_TABLE;
            }
            if(!isSet){
                node = doc.getElementsByTag(TagNames.EXPECTED_QUERY_RESULTS);
                if(!node.isEmpty()){
                    isSet = true;
                    type = TYPE_TABLE;
                }
            }
            if(!isSet){
                node = doc.getElementsByTag(TagNames.ACTUAL_EXCEPTION);
                if(!node.isEmpty()){
                    isSet = true;
                    type = TYPE_EXCEPTION;
                }
            }
            if(!isSet){
                node = doc.getElementsByTag(TagNames.EXPECTED_EXCEPTION);
                if(!node.isEmpty()){
                    isSet = true;
                    type = TYPE_EXCEPTION;
                }
            }
            if(!isSet){
                LOGGER.warn("The document should contain one of {}, {}, {}, {}.", 
                        TagNames.ACTUAL_EXCEPTION, TagNames.ACTUAL_QUERY_RESULTS,
                        TagNames.EXPECTED_EXCEPTION, TagNames.EXPECTED_QUERY_RESULTS);
                clearTable();
                return;
            }
            if(type == TYPE_TABLE){
                buildTable(node.get(0));
            } else {
                buildException(node.get(0));
            }
            Utils.buildTable(this, table);
            repaint();
        } catch (Exception ex){
            LOGGER.error("ERROR", ex);
            type = -1;
            clearTable();
        }
    }
    
    /**
     * Fills {@link #table}. Expects that element contains a table-result.
     * @param doc an XML document
     * @throws ResultParsingException if the node {@code rootElement} does not have expected form
     */
    private void buildTable(Element rootElement) throws ResultParsingException{
        Elements tableElements = rootElement.getElementsByTag(TagNames.TABLE);
        if(tableElements.isEmpty()){
            throw new ResultParsingException("Expected element not found: " + TagNames.TABLE);
        }
        Element tableElement = tableElements.get(0);
        // create table
        String colsC = tableElement.attr(TagNames.COLUMN_COUNT);
        String rowsC = tableElement.attr(TagNames.ROW_COUNT);
        columns = Integer.parseInt(colsC) + 1;
        rows = Integer.parseInt(rowsC) + 1;
        table = new Cell[rows][columns];
        table[0][0] = new Cell("Row");
        Elements dataElements = rootElement.select(TagNames.SELECT + " " + TagNames.DATA_ELEMENT);
        if(dataElements.isEmpty()){
            throw new ResultParsingException("No element " + TagNames.DATA_ELEMENT + ".");
        }
        int idx = 0;
        for(Element dataElement : dataElements){
            StringBuilder b = new StringBuilder(getWholeText(dataElement))
                    .append(" [")
                    .append(dataElement.attr(TagNames.TYPE))
                    .append("]");
            table[0][++idx] = new Cell(b.toString());
        }
        // fill rows
        Elements tableRowElements= rootElement.getElementsByTag(TagNames.TABLE_ROW);
        int rowIdx = 0;
        for(Element tableRow : tableRowElements){
            table[++rowIdx][0] = new Cell(Integer.toString(rowIdx));
            int cellIdx = 1;
            for(Element tableCell : tableRow.getElementsByTag(TagNames.TABLE_CELL)){
                table[rowIdx][cellIdx++] = new Cell(getWholeText(tableCell));
            }
        }
    }
    
    /**
     * Fills {@link #table}. Expects that element contains an exception-result.
     * @param doc an XML document
     * @throws ResultParsingException if the node {@code rootElement} does not have expected form
     */
    private void buildException(Element rootElement) throws ResultParsingException{
        LOGGER.trace("Root element: " + rootElement);
        Elements exceptionType = rootElement.getElementsByTag(TagNames.EXCEPTION_TYPE);
        Elements exceptionMessage = rootElement.getElementsByTag(TagNames.MESSAGE);
        if(exceptionType.isEmpty()){
            throw new ResultParsingException("No element " + TagNames.EXCEPTION_TYPE);
        }
        if(exceptionMessage.isEmpty()){
            throw new ResultParsingException("No element " + TagNames.MESSAGE);
        }
        rows = 2;
        columns = 2;
        table = new Cell[rows][columns];
        table[0][0] = new Cell("Exception type");
        table[0][1] = new Cell(getWholeText(exceptionType.get(0)));
        table[1][0] = new Cell("Exception message");
        table[1][1] = new Cell(getWholeText(exceptionMessage.get(0)));
    }
    
    private String getWholeText(Element e){
        Elements children = e.children();
        if(children.isEmpty()){
            return getWholeTextOfTextNodes(e);
        } else {
            StringBuilder b = new StringBuilder();
            for(Element ch : children){
                b.append(getWholeTextOfTextNodes(ch));
            }
            return b.toString();
        }
    }
    
    private String getWholeTextOfTextNodes(Element e){
        List<TextNode> texts =  e.textNodes();
        if(texts.isEmpty()){
            return e.text();
        }
        StringBuilder b = new StringBuilder();
        for(TextNode tn : texts){
            b.append(tn.getWholeText());
        }
        return b.toString();
    }
    
    /**
     * Binds cells in {@code table} with cells in this table. If the cell in one
     * table is highlighted, then corresponding cell in the other table is 
     * highlighted too.
     * 
     * @param table second table
     * @see #unbindCells()
     */
    public void bindCells(TablePanel table){
        if(this.table == null){
            return; // nothing to do
        }
        unbindCells();
        if(table == null || table.type != this.type){
            return;
        }
        for(int r = 0; r < this.rows; r++){
            for(int c = 0; c < this.columns; c++){
                Cell cell1 = this.table[r][c];
                Cell cell2 = getCell(table, r, c);
                if(cell1 != null){ cell1.setBindedCell(cell2); }
                if(cell2 != null){ cell2.setBindedCell(cell1); }
            }
        }
    }
    
    /**
     * Clears this table.
     */
    public void clearTable(){
        unbindCells();
        removeAll();
        repaint();
    }
    
    /**
     * Unbinds all cells.
     * 
     * @see #bindCells()
     */
    public void unbindCells(){
        if(this.table == null){
            return; // nothing to do
        }
        for(Cell[] r : table){
            for(Cell c : r){
                c.setBindedCell(null);
            }
        }
    }
        
    /**
     * @param table table
     * @param row row index
     * @param col column index
     * @return cell instance at position {@code [row][col]} in table {@code table}
     *      or null if {@code row} or {@code col} is out of range 
     */
    private Cell getCell(TablePanel table, int row, int col){
        if(row < 0 || row >= table.rows || col < 0 || col >= table.columns){
            return null;
        }
        return table.table[row][col];
    }
}











