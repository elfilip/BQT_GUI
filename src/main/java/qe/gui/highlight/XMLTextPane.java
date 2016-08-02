package qe.gui.highlight;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.undo.UndoManager;

/**
 * 
 * @author felias
 *
 *Text pane with XML syntax highlighting
 */
public class XMLTextPane extends JTextPane {
	private static final long serialVersionUID = 4394890898671637335L;

	public XMLTextPane() {
		setEditorKitForContentType("text/xml", new XMLEditorKit());
		setContentType("text/xml");
        final UndoManager man = new UndoManager();
        man.setLimit(500);
        getDocument().addUndoableEditListener(man);
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "undo");
        getActionMap().put("undo", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e){
                if(man.canUndo()){
                    man.undo();
                }
            }
        });
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK), "redo");
        getActionMap().put("redo", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e){
                if(man.canRedo()){
                    man.redo();
                }
            }
        });
	}
}
