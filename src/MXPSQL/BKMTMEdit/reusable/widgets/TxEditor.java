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
import javax.swing.*;
import java.util.List;
import org.fife.rsta.ui.*;
import java.util.ArrayList;
import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;

public class TxEditor extends JInternalFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public TextEditorPane area;
	public JComboBox<String> languagesbox = new JComboBox<String>();
	public String defaultLang = SyntaxConstants.SYNTAX_STYLE_NONE;
	public String filePath = "";
	public String docName = "";
	public Frame winparent;
	
	int currentSearchFindNumber = 0;
	int currentSearchReplaceNumber = 0;
	boolean searchStatus = true;
	
	protected RTextScrollPane rsp;
	
	public TxEditor(Frame winparent, List<String> languages) {
		area = new TextEditorPane();
		area.setCodeFoldingEnabled(true);
		area.setMarkOccurrences(true);
		
		CollapsibleSectionPanel csp = new CollapsibleSectionPanel();
		rsp = new RTextScrollPane(area);
		csp.add(rsp);
		
		{
			String[] larray = new String[languages.size()];
			for (int i=0;i<larray.length;i++) larray[i]= languages.get(i);
			languagesbox.setModel(new DefaultComboBoxModel<String>(larray));
		}
		
		
		add(languagesbox, BorderLayout.NORTH);
		add(csp, BorderLayout.CENTER);
		
		languagesbox.addActionListener((e) -> {
			if(languagesbox.getSelectedItem() instanceof String) {
				String lang = (String) languagesbox.getSelectedItem();
				
				if(lang == "default*") {
					area.setSyntaxEditingStyle(defaultLang);
				}
				else {
					area.setSyntaxEditingStyle(lang);
				}
			}
		});
		
		this.winparent = winparent;
	}
	
	public String getText() {
		return area.getText();
	}
	
	public void setText(String s) {
		area.setText(s);
	}
	
	public boolean findDialog() {
		JDialog dialog = new JDialog(winparent);
		dialog.setTitle("Find...");
		JPanel buttonpanel = new JPanel();
		JPanel mainpanel = new JPanel();
		JTextField field = new JTextField(10);
		JCheckBox wraparound = new JCheckBox("Wrap");
		boolean status = false;
		
		{
			JButton findbutton = new JButton("Find");
			JButton cancelbutton = new JButton("Cancel");
			
			cancelbutton.addActionListener((e) -> {
				dialog.dispose();
			});
			
			findbutton.addActionListener((e) -> {
				if(field.getText() == null || field.getText().isBlank() || field.getText().isEmpty()) {
					JOptionPane.showMessageDialog(winparent, "This text field is blank!");
					return;
				}
					
				List<Integer> matchpos = new ArrayList<Integer>();
				
				int index = getText().indexOf(field.getText(), area.getCaretPosition());
				
				if(index != -1) {
					while(index >= 0) {
						matchpos.add(index);
						index = getText().indexOf(field.getText(), index+1);
					}
					
					if(currentSearchFindNumber >= matchpos.size())
						currentSearchFindNumber = 0;
					
					area.requestFocus();
					area.setCaretPosition(matchpos.get(currentSearchFindNumber));
					area.select(matchpos.get(currentSearchFindNumber), matchpos.get(currentSearchFindNumber) + field.getText().length());
					currentSearchFindNumber += 1;
				}
				else {
					if(matchpos.size() > 0) {
						area.setCaretPosition(0);
						if(currentSearchFindNumber >= matchpos.size())
							currentSearchFindNumber = 0;
						area.setCaretPosition(matchpos.get(currentSearchFindNumber));
						area.select(matchpos.get(currentSearchFindNumber), matchpos.get(currentSearchFindNumber) + field.getText().length());
					}
					else {
						if(wraparound.isSelected()) {
							area.setCaretPosition(0);
							if(currentSearchFindNumber >= matchpos.size())
								currentSearchFindNumber = 0;
							
							index = getText().indexOf(field.getText(), 0);
							if(index != -1) {
								while(index >= 0) {
									matchpos.add(index);
									index = getText().indexOf(field.getText(), index+1);
								}
								
								area.setCaretPosition(matchpos.get(currentSearchFindNumber));
								area.select(matchpos.get(currentSearchFindNumber), matchpos.get(currentSearchFindNumber) + field.getText().length());
							}
							else {
								JOptionPane.showMessageDialog(winparent, "Text '" + field.getText() + "' not found");
							}
						}
						else {
							JOptionPane.showMessageDialog(winparent, "Text '" + field.getText() + "' not found");
						}
					}
				}
			});
			
			buttonpanel.add(cancelbutton);
			buttonpanel.add(findbutton);
		}
		
		
		{
			mainpanel.add(new JScrollPane(field), BorderLayout.NORTH);
			mainpanel.add(wraparound, BorderLayout.CENTER);
		}
		
		{
			dialog.add(mainpanel, BorderLayout.CENTER);
			dialog.add(buttonpanel, BorderLayout.SOUTH);
		}
		
		dialog.pack();
		
		currentSearchFindNumber = 0;
		
		dialog.setVisible(true);
		
		return status;
	}
	
	public boolean replaceDialog() {
		JDialog dialog = new JDialog(winparent);
		dialog.setTitle("Replace...");
		JPanel buttonpanel = new JPanel();
		JPanel mainpanel = new JPanel();
		JTextField field = new JTextField(10);
		JTextField replacement = new JTextField(10);
		JCheckBox wraparound = new JCheckBox("Wrap");
		
		{
			JButton findbutton = new JButton("Replace");
			JButton cancelbutton = new JButton("Cancel");
			
			cancelbutton.addActionListener((e) -> {
				dialog.dispose();
			});
			
			findbutton.addActionListener((e) -> {
				if(field.getText() == null || field.getText().isBlank() || field.getText().isEmpty() || replacement.getText() == null || replacement.getText().isBlank() || replacement.getText().isEmpty()){
					JOptionPane.showMessageDialog(winparent, "These text fields are blank!");
					return;
				}
				
				String text = getText();
				List<Integer> matchpos = new ArrayList<Integer>();
				
				int index = text.indexOf(field.getText());
				
				if(index != -1) {
					while(index >= 0) {
						matchpos.add(index);
						index = text.indexOf(field.getText(), index+1);
					}
					
					if(currentSearchReplaceNumber >= matchpos.size())
						currentSearchReplaceNumber = 0;
					
					searchStatus = true;
					area.requestFocus();
					currentSearchReplaceNumber += 1;
					
					area.setCaretPosition(matchpos.get(currentSearchReplaceNumber));
					area.select(matchpos.get(currentSearchReplaceNumber), matchpos.get(currentSearchReplaceNumber) + field.getText().length());
					area.replaceSelection(replacement.getText());
				}
				else {
					if(wraparound.isSelected()) {
						area.setCaretPosition(0);
						if(currentSearchReplaceNumber >= matchpos.size())
							currentSearchReplaceNumber = 0;
						
						index = getText().indexOf(field.getText(), 0);
						
						if(index != -1) {
							while(index >= 0) {
								matchpos.add(index);
								index = getText().indexOf(field.getText(), index + 1);
							}
							
							searchStatus = true;
							
							area.setCaretPosition(matchpos.get(currentSearchReplaceNumber));
							area.select(matchpos.get(currentSearchReplaceNumber), matchpos.get(currentSearchReplaceNumber) + field.getText().length());
							area.replaceSelection(replacement.getText());
						}
						else {
							JOptionPane.showMessageDialog(winparent, "Text '" + field.getText() + "' not found");
						}
					}
					else {
						JOptionPane.showMessageDialog(winparent, "Text '" + field.getText() + "' not found");
					}
				
				}
			});
			
			buttonpanel.add(cancelbutton);
			buttonpanel.add(findbutton);
		}
		
		
		{
			JPanel textpanel = new JPanel(new BorderLayout());
			{
				JPanel fieldpanel = new JPanel();
				fieldpanel.add(new JLabel("Find"));
				fieldpanel.add(new JScrollPane(field), BorderLayout.NORTH);
				
				JPanel replacepanel = new JPanel();
				replacepanel.add(new JLabel("Replace"));
				replacepanel.add(replacement, BorderLayout.CENTER);
				
				textpanel.add(fieldpanel, BorderLayout.NORTH);
				textpanel.add(replacepanel);
			}
			
			mainpanel.add(textpanel);
			mainpanel.add(wraparound, BorderLayout.CENTER);
		}
		
		{
			dialog.add(mainpanel, BorderLayout.CENTER);
			dialog.add(buttonpanel, BorderLayout.SOUTH);
		}
		
		dialog.pack();
		
		currentSearchReplaceNumber = 0;
		searchStatus = false;
		
		dialog.setVisible(true);
		
		return searchStatus;
	}
}
