package MXPSQL.BKMTMEdit.reusable.widgets;

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
import java.util.*;
import javax.swing.*;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

public class AskInputDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Map<String, String> list_of_objec;
	protected String input = null;
	
	protected JTextArea area = new RSyntaxTextArea(20, 30);
	protected JComboBox<String> cbox;
	
	public AskInputDialog(Frame parent, String title, Map<String, String> stuffYaWant) {
		super(parent);
		
		setTitle(title);
		
		list_of_objec = stuffYaWant;
		
		ArrayList<String> files = new ArrayList<String>(list_of_objec.keySet());
		
		
		JPanel panel = new JPanel(new BorderLayout());
		
		{
		
			panel.add(new RTextScrollPane(area), BorderLayout.NORTH);
			
			String[] farray = new String[files.size()];

			for (int i=0;i<farray.length;i++) farray[i]= files.get(i);
			
			{
				JPanel panel4premadestuff = new JPanel(new BorderLayout());
				cbox = new JComboBox<String>(farray);
				cbox.addActionListener((e) -> {
					
					area.setText(list_of_objec.get(cbox.getSelectedItem()));
				});
				panel4premadestuff.add(cbox);
				panel.add(panel4premadestuff);
			}
			
			{
				JPanel buttonBox = new JPanel(new BorderLayout());
				
				JButton ok = new JButton("Ok");
				JButton cancel = new JButton("Cancel");
				
				ok.addActionListener((e) -> {
					input = area.getText();
					dispose();
				});
				
				cancel.addActionListener((e) -> {
					input = null;
					dispose();
				});
				
				buttonBox.add(ok);
				buttonBox.add(cancel, BorderLayout.EAST);
				
				panel.add(buttonBox, BorderLayout.SOUTH);
			}
		}
		
		add(panel);
		
		pack();
		
		{
			Rectangle r = getBounds();
			
			setMinimumSize(new Dimension(r.width, r.height));
		}
	}
	
	public String getInput() {
		
		setVisible(true);
		
		return input;
	}
}
