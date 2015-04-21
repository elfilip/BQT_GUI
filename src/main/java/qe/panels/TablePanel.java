package qe.panels;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.Group;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
    
    private DocumentBuilderFactory DOC_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
    
    /**
     * XML elements in error file.
     * 
     * @author jdurani
     *
     */
    private static interface Elements {
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
        this.removeAll();
        try{
            DocumentBuilder dBuilder = DOC_BUILDER_FACTORY.newDocumentBuilder();
            Document doc = dBuilder.parse(IOUtils.toInputStream(xml, "UTF-8"));
            NodeList node = doc.getElementsByTagName(Elements.ACTUAL_QUERY_RESULTS);
            boolean isSet = false;
            if(node.getLength() != 0){
                isSet = true;
                type = TYPE_TABLE;
            }
            if(!isSet){
                node = doc.getElementsByTagName(Elements.EXPECTED_QUERY_RESULTS);
                if(node.getLength() != 0){
                    isSet = true;
                    type = TYPE_TABLE;
                }
            }
            if(!isSet){
                node = doc.getElementsByTagName(Elements.ACTUAL_EXCEPTION);
                if(node.getLength() != 0){
                    isSet = true;
                    type = TYPE_EXCEPTION;
                }
            }
            if(!isSet){
                node = doc.getElementsByTagName(Elements.EXPECTED_EXCEPTION);
                if(node.getLength() != 0){
                    isSet = true;
                    type = TYPE_EXCEPTION;
                }
            }
            if(!isSet){
                LOGGER.warn("The document should cotain one of {}, {}, {}, {} ", 
                        Elements.ACTUAL_EXCEPTION, Elements.ACTUAL_QUERY_RESULTS,
                        Elements.EXPECTED_EXCEPTION, Elements.EXPECTED_QUERY_RESULTS);
                //TODO set default content
                return;
            }
            if(type == TYPE_TABLE){
                buildTable(doc);
            } else {
                buildException(doc);
            }
            initTable();
            repaint();
        } catch (Exception ex){
            LOGGER.error("ERROR", ex);
            type = -1;
            //TODO - set default content
        }
    }
    
    /**
     * Prepares this panel (creates layout from actual {@link #table}).
     */
    private void initTable(){
        GroupLayout gl = new GroupLayout(this);
        gl.setAutoCreateContainerGaps(false);
        gl.setAutoCreateGaps(false);
        // horizontal group
        Group hor = gl.createParallelGroup(Alignment.LEADING, false);
        for(int r = 0; r < rows; r++){
            Group horSeq = gl.createSequentialGroup();
            for(int c = 0; c < columns; c++){
                horSeq.addComponent(table[r][c]);
            }
            hor.addGroup(horSeq);
        }
        gl.setHorizontalGroup(hor);
        // vertical group
        Group ver = gl.createParallelGroup(Alignment.LEADING, false);
        for(int c = 0; c < columns; c++){
            Group verSeq = gl.createSequentialGroup();
            for(int r = 0; r < rows; r++){
                verSeq.addComponent(table[r][c]);
            }
            ver.addGroup(verSeq);
        }
        gl.setVerticalGroup(ver);
        
        gl.linkSize(SwingConstants.VERTICAL, getAllTableAsArray());
        for(int c = 0; c < columns; c++){
            gl.linkSize(SwingConstants.HORIZONTAL, getTableColumnAsArray(c));
        }
        setLayout(gl);
    }
    
    /**
     * @return all {@link #table} as one-dimensional array
     */
    private Cell[] getAllTableAsArray(){
        Cell[] cells = new Cell[rows * columns];
        for(int r = 0; r < rows; r++){
            System.arraycopy(table[r], 0, cells, r * columns, columns);
        }
        return cells;
    }
    
    /**
     * @param colIdx column index
     * @return column in actual {@link #table} as one-dimensional array
     */
    private Cell[] getTableColumnAsArray(int colIdx){
        Cell[] cells = new Cell[rows];
        for(int r = 0; r < rows; r++){
            cells[r] = table[r][colIdx];
        }
        return cells;
    }
    
    /**
     * Fills {@link #table}. Expects that document contains a table-result.
     * @param doc an XML document
     * @throws SAXException if the document {@code doc} does not have expected form
     */
    private void buildTable(Document doc) throws SAXException{
        NodeList tableNodeList = doc.getElementsByTagName(Elements.TABLE);
        if(tableNodeList.getLength() == 0){
            throw new SAXException("Expected element not found: " + Elements.TABLE);
        }
        Node tableElement = tableNodeList.item(0);
        // create table
        NamedNodeMap attrs = tableElement.getAttributes();
        String colsC = attrs.getNamedItem(Elements.COLUMN_COUNT).getNodeValue();
        String rowsC = attrs.getNamedItem(Elements.ROW_COUNT).getNodeValue();
        columns = Integer.parseInt(colsC) + 1;
        rows = Integer.parseInt(rowsC) + 1;
        table = new Cell[rows][columns];
        table[0][0] = new Cell("Row", false);
        // fill header
        NodeList selectNodeList = doc.getElementsByTagName(Elements.SELECT);
        if(selectNodeList.getLength() == 0){
            throw new SAXException("Expected element not found: " + Elements.SELECT);
        }
        NodeList dataElementList= doc.getElementsByTagName(Elements.DATA_ELEMENT);
        if(dataElementList.getLength() == 0){
            throw new SAXException("No element " + Elements.DATA_ELEMENT + ".");
        }
        int idx = 0;
        while(idx < dataElementList.getLength()){
            Node dataElement = dataElementList.item(idx);
            StringBuilder b = new StringBuilder(dataElement.getTextContent())
            .append(" [");
            attrs = dataElement.getAttributes();
            b.append(attrs.getNamedItem(Elements.TYPE).getNodeValue())
                .append("]");
            table[0][++idx] = new Cell(b.toString(), false);
            dataElement = dataElement.getNextSibling();
        }
        // fill rows
        NodeList tableRowElementList = doc.getElementsByTagName(Elements.TABLE_ROW);
        NodeList tableCellElementList = doc.getElementsByTagName(Elements.TABLE_CELL);
        int rowIdx = 0;
        int cellIdxOverall = 0;
        while(rowIdx < tableRowElementList.getLength()){
            table[++rowIdx][0] = new Cell(Integer.toString(rowIdx), false);
            int cellIdx = 1;
            while(cellIdx < columns){
                Node tableCellElement = tableCellElementList.item(cellIdxOverall++);
                table[rowIdx][cellIdx++] = new Cell(tableCellElement.getTextContent(), true);
            }
        }
    }
    
    /**
     * Fills {@link #table}. Expects that document contains an exception-result.
     * @param doc an XML document
     * @throws SAXException if the document {@code doc} does not have expected form
     */
    private void buildException(Document doc) throws SAXException{
        NodeList exceptionTypeList = doc.getElementsByTagName(Elements.EXCEPTION_TYPE);
        NodeList exceptionMessageList = doc.getElementsByTagName(Elements.MESSAGE);
        if(exceptionTypeList.getLength() == 0){
            throw new SAXException("No element " + Elements.EXCEPTION_TYPE);
        }
        if(exceptionMessageList.getLength() == 0){
            throw new SAXException("No element " + Elements.MESSAGE);
        }
        Node exceptionType = exceptionTypeList.item(0);
        Node exceptionMessage= exceptionMessageList.item(0);
        rows = 2;
        columns = 2;
        table = new Cell[rows][columns];
        table[0][0] = new Cell("Exception type", false);
        table[0][1] = new Cell(exceptionType.getTextContent(), true);
        table[1][0] = new Cell("Exception message", false);
        table[1][1] = new Cell(exceptionMessage.getTextContent(), true);
    }
    
    /**
     * Compares {@code table} with this table. All different cells will be highlighted in
     * in both tables.
     * 
     * @param table second table
     */
    public void markDiff(TablePanel table){
        if(table == null || table.type != this.type){
            return;
        }
        // header
        for(int c = 0; c < this.columns; c++){
            compareCells(this.table[0][c], getCell(table, 0, c), true);
        }
        // rows
        for(int r = 1; r < this.rows; r++){
            for(int c = 0; c < this.columns; c++){
                compareCells(this.table[r][c], getCell(table, r, c), false);
            }
        }
    }
    
    /**
     * Compares two cells and highlights them if they are different.  
     * 
     * @param c1 cell one
     * @param c2 cell two
     * @param ignoreCase if true, strings will be compared using {@link String#equalsIgnoreCase(String)} 
     *      instead of {@link String#equals(String)}
     */
    private void compareCells(Cell c1, Cell c2, boolean ignoreCase){
        if(c1 == c2){
            return;
        }
        boolean equals = c1 != null ? c1.equals(c2, ignoreCase) : c2.equals(c1, ignoreCase);
        if(!equals){
            if(c1 != null){ c1.highlight(); }
            if(c2 != null){ c2.highlight(); }
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











