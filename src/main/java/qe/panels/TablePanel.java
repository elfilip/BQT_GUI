package qe.panels;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.codec.binary.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qe.entity.settings.Settings;
import qe.exception.ResultParsingException;

/**
 * Panel for basic table view.
 * 
 * @author jdurani
 *
 */
public class TablePanel extends JTable {
    /**
     * 
     */
    private static final long serialVersionUID = -6932438524346906661L;

    private static final Logger LOG = LoggerFactory.getLogger(TablePanel.class);
    
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
        static final String MESSAGE_CONTAINS = "message-contains";
        static final String MESSAGE_STARTS_WITH = "message-startswith";
        static final String MESSAGE_REGEX = "messageRegex";
        static final String ATTRIBUTE_IS_BASE64 = "base64";
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
    
    private boolean[][] selected;
    private TablePanel binded;
    private InnerTableModel model;
    private int rows;
    private int columns;
    
    private boolean decodeBase64;
    
    public TablePanel() {
        super();
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setRowHeight(10 + new Cell("").getFont().getSize());
        model = new InnerTableModel();
        setModel(model);
        setDefaultRenderer(Cell.class, new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Cell c = (Cell) value;
                if(selected != null){
                    if((selected[column][row] && !c.isHighlighted())
                            || (!selected[column][row] && c.isHighlighted())){
                        c.highlight();
                    }
                }
                return c;
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(selected != null){
                    int r = getSelectedRow();
                    int c = getSelectedColumn();
                    selected[c][r] = !selected[c][r];
                    model.fireTableCellUpdated(r, c);
                    if(binded != null){
                        binded.model.fireTableCellUpdated(r, c);
                    }
                }
            }
        });
    }
    
    /**
     * This method parses an XML document {@code xml} and creates table.
     * 
     * @param xml XML document as string
     */
    public void parseXML(String xml){
        
        decodeBase64 = Boolean.valueOf(Settings.getInstance().getDecodeBase64());
        
        if(LOG.isTraceEnabled()){
            LOG.trace("Parsing xml: {}", xml);
        }
        model.clear();
        try{
            Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
            if(LOG.isTraceEnabled()){
                LOG.trace("Parsed document: {}", doc);
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
                LOG.warn("The document should contain one of {}, {}, {}, {}.", 
                        TagNames.ACTUAL_EXCEPTION, TagNames.ACTUAL_QUERY_RESULTS,
                        TagNames.EXPECTED_EXCEPTION, TagNames.EXPECTED_QUERY_RESULTS);
                clearTable();
                return;
            }
            if(type == TYPE_TABLE){
                LOG.debug("Creating table.");
                buildTable(node.get(0));
            } else {
                LOG.debug("Creating table from exception.");
                buildException(node.get(0));
            }
            initSelected();
            LOG.debug("Table created.");
            model.fireTableStructureChanged();
            if(type == TYPE_TABLE){
                for(int i = 0; i < columns; i++){
                    getColumnModel().getColumn(i).setMinWidth(180);
                }
            } else {
                getColumnModel().getColumn(0).setMinWidth(200);
                getColumnModel().getColumn(1).setMinWidth(800);
            }
        } catch (Exception ex){
            LOG.error("ERROR", ex);
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
        String[] header = new String[columns];
        header[0] = "Row";
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
            header[++idx] = b.toString();
        }
        model.setHeader(header);
        // fill rows
        Elements tableRowElements= rootElement.getElementsByTag(TagNames.TABLE_ROW);
        int rowIdx = 0;
        for(Element tableRow : tableRowElements){
            Cell[] datas = new Cell[columns];
            datas[0] = new Cell(Integer.toString(++rowIdx));
            int cellIdx = 1;
            for(Element tableCell : tableRow.getElementsByTag(TagNames.TABLE_CELL)){
                datas[cellIdx++] = new Cell(getWholeText(tableCell));
            }
            model.addData(datas);
        }
    }
    
    /**
     * Fills {@link #table}. Expects that element contains an exception-result.
     * @param doc an XML document
     * @throws ResultParsingException if the node {@code rootElement} does not have expected form
     */
    private void buildException(Element rootElement) throws ResultParsingException{
        LOG.trace("Root element: " + rootElement);
        Elements exceptionType = rootElement.getElementsByTag(TagNames.EXCEPTION_TYPE);
        Elements exceptionMessage = rootElement.getElementsByTag(TagNames.MESSAGE);
        Elements exceptionMessageStartsWith = rootElement.getElementsByTag(TagNames.MESSAGE_STARTS_WITH);
        Elements exceptionMessageRegex = rootElement.getElementsByTag(TagNames.MESSAGE_REGEX);
        Elements exceptionMessageContains = rootElement.getElementsByTag(TagNames.MESSAGE_CONTAINS);
        String messageType;
        String message;
        if(exceptionType.isEmpty()){
            throw new ResultParsingException("No element " + TagNames.EXCEPTION_TYPE);
        }
        if(!exceptionMessage.isEmpty()){
            message = getWholeText(exceptionMessage.get(0));
            messageType = "Exception message [plain]";
        } else if(!exceptionMessageStartsWith.isEmpty()){
            message = getWholeText(exceptionMessageStartsWith.get(0));
            messageType = "Exception message [starts-with]";
        } else if(!exceptionMessageRegex.isEmpty()){
            message = getWholeText(exceptionMessageRegex.get(0));
            messageType = "Exception message [regex]";
        } else if(!exceptionMessageContains.isEmpty()){
            message = getWholeText(exceptionMessageContains.get(0));
            messageType = "Exception message [contains]";
        } else {
            throw new ResultParsingException("Need at least one of elements " + TagNames.MESSAGE + ", " + TagNames.MESSAGE_STARTS_WITH
                    + ", " + TagNames.MESSAGE_REGEX + ", " + TagNames.MESSAGE_CONTAINS);
        }
        rows = 2;
        columns = 2;
        model.setHeader(new String[]{"Exception type", messageType});
        model.addData(new Cell[]{new Cell(getWholeText(exceptionType.get(0))), new Cell(message)});
    }
    
    private void initSelected(){
        selected = new boolean[columns][rows];
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
        String result = b.toString();
        if(decodeBase64 && "true".equals(e.attributes().get(TagNames.ATTRIBUTE_IS_BASE64))) {
            result = result + " [" + new String(Base64.decodeBase64(result)) + ']';
        }
        return result;
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
        if(table == null || table.type != this.type){
            return; // nothing to do
        }
        table.binded = this;
        this.binded = table;
        selected = new boolean[Math.max(this.columns, table.columns)][Math.max(this.rows, table.rows)];
        table.selected = this.selected;
    }
    
    /**
     * Clears this table.
     */
    public void clearTable(){
        rows = 0;
        columns = 0;
        model.clear();
    }
    
    /**
     * Unbinds all cells.
     * 
     * @see #bindCells()
     */
    public void unbindCells(){
        if(this.binded != null){
            binded.binded = null;
            binded.initSelected();
        }
        this.binded = null;
        initSelected();
    }
    
    @SuppressWarnings("serial")
    private static class InnerTableModel extends AbstractTableModel{

        private final List<Cell[]> data = new LinkedList<Cell[]>();
        private String[] headers;
        
        private void setHeader(String[] headers) {
            this.headers = headers;
        }
        
        private void addData(Cell[] row){
            data.add(row);
        }
        
        private void clear(){
            data.clear();
        }
        
        @Override
        public int getRowCount() {
            return data.size();
        }
        
        @Override
        public String getColumnName(int column) {
            return headers == null ? null : headers[column];
        }
        
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return Cell.class;
        }
        
        @Override
        public int getColumnCount() {
            return headers == null ? 0 : headers.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return data.get(rowIndex)[columnIndex];
        }
        
    }
}











