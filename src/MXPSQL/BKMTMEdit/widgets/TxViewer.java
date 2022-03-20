package MXPSQL.BKMTMEdit.widgets;

/**
MIT License

Copyright (c) 2022 MXPSQL

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

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
