package MXPSQL.BKMTMEdit.widgets;

import java.awt.*;
import javax.swing.*;
import org.fife.rsta.ui.*;
import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;

public class TxViewer extends JInternalFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public RSyntaxTextArea area = new RSyntaxTextArea(20, 60);
	
	public TxViewer() {
		setLayout(new BorderLayout());
		
		add(area);
		
		area.setEditable(false);
		
		area.setCodeFoldingEnabled(true);
		area.setMarkOccurrences(true);
		
		CollapsibleSectionPanel csp = new CollapsibleSectionPanel();
		csp.add(new RTextScrollPane(area));
		add(new JLabel("Text viewer"), BorderLayout.NORTH);
		add(csp, BorderLayout.CENTER);
	}
	
	public void setText(String text) {
		area.setText(text);
	}
	
	public String getText() {
		return area.getText();
	}
	
	public void setSyntaxEditingStyle(String style) {
		area.setSyntaxEditingStyle(style);
	}
}
