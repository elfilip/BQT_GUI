package qe.gui.highlight;

import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
/**
 * 
 * @author felias
 *
 *Factory for XML syntax highlighting
 */
public class XMLFactory implements ViewFactory{

	@Override
	public View create(Element elem) {
		// TODO Auto-generated method stub
		return new XMLView(elem);
	}

}
