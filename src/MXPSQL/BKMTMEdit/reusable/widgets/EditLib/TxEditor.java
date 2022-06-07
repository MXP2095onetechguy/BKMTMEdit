package MXPSQL.BKMTMEdit.reusable.widgets.EditLib;

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
import java.awt.event.*;
import java.util.Optional;
import org.fife.rsta.ui.*;
import java.util.ArrayList;
import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;
import MXPSQL.BKMTMEdit.reusable.utils.*;
import MXPSQL.BKMTMEdit.reusable.widgets.FontDialog;

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
	
	private TxEditor dis = this;
	
	public TxEditor(Frame winparent, List<String> languages) {
		area = new TextEditorPane();
		area.setCodeFoldingEnabled(true);
		area.setMarkOccurrences(true);
		
		{
			JMenuBar mb = new JMenuBar();
			
			{
				JMenu view = new JMenu("View");
				
				JCheckBoxMenuItem linewrap = new JCheckBoxMenuItem("Line Wrap / Word Wrap");
				JMenuItem font = new JMenuItem("Font");
				JMenuItem bcolor = new JMenuItem("Background Color");
				
				linewrap.addActionListener((e) -> {
					if(linewrap.getModel().isSelected()) area.setLineWrap(true);
					else area.setLineWrap(false);
				});
				
				font.addActionListener((e) -> {
					FontDialog d = new FontDialog(winparent, area);
					d.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
					d.setVisible(true);
					Optional<Font> f = d.get();
					if(f.isPresent()) area.setFont(f.get());
				});;
				
				bcolor.addActionListener((e) -> {
					Color c = JColorChooser.showDialog(dis, "Choose a background color", area.getBackground());
					if(c != null) area.setBackground(c);
				});
				
				linewrap.setSelected(false);
				
				view.add(linewrap);
				view.add(font);
				view.add(bcolor);
				
				mb.add(view);
			}
			
			setJMenuBar(mb);
		}
		
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
		JCheckBox reverse = new JCheckBox("Reverse");
		
		boolean status = false;
		
		ActionListener rep = (e) -> {
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
				
				int oldCaret = field.getCaretPosition();
				area.requestFocus();
				try{
					if(getText().length() < matchpos.get(currentSearchReplaceNumber)) { area.setCaretPosition(matchpos.get(currentSearchReplaceNumber));
						area.select(matchpos.get(currentSearchFindNumber), matchpos.get(currentSearchFindNumber) + field.getText().length());
					}
				}
				catch(IndexOutOfBoundsException ioobe) {
					field.requestFocus();
					field.setCaretPosition(oldCaret);
					return;
				}
				if(reverse.isSelected()) currentSearchFindNumber -= 1;
				else currentSearchFindNumber += 1;
				
				
				field.requestFocus();
				field.setCaretPosition(oldCaret);
			}
			else {
				if(matchpos.size() > 0) {
					area.setCaretPosition(0);
					if(currentSearchFindNumber >= matchpos.size())
						currentSearchFindNumber = 0;
					
					int oldCaret = field.getCaretPosition();
					
					area.requestFocus();
					try{
						if(getText().length() < matchpos.get(currentSearchFindNumber)) {
							area.setCaretPosition(matchpos.get(currentSearchFindNumber));
							area.select(matchpos.get(currentSearchFindNumber), matchpos.get(currentSearchFindNumber) + field.getText().length());
						}
					}
					catch(IndexOutOfBoundsException ioobe) {
						field.requestFocus();
						field.setCaretPosition(oldCaret);
						return;
					}
					
					field.requestFocus();
					field.setCaretPosition(oldCaret);
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
							
							
							int oldCaret = field.getCaretPosition();
							
							area.requestFocus();
							try{
								if(getText().length() < matchpos.get(currentSearchFindNumber)) {
									area.setCaretPosition(matchpos.get(currentSearchFindNumber));
									area.select(matchpos.get(currentSearchFindNumber), matchpos.get(currentSearchFindNumber) + field.getText().length());
								}
							}
							catch(IndexOutOfBoundsException ioobe) {
								field.requestFocus();
								field.setCaretPosition(oldCaret);
								return;
							}
							
							field.requestFocus();
							field.setCaretPosition(oldCaret);
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
		};
		
		field.addActionListener(rep);
		
		{
			JButton findbutton = new JButton("Find");
			JButton cancelbutton = new JButton("Cancel");
			
			cancelbutton.addActionListener((e) -> {
				dialog.dispose();
			});
			
			findbutton.addActionListener(rep);
			
			buttonpanel.add(cancelbutton);
			buttonpanel.add(findbutton);
		}
		
		
		{
			mainpanel.add(new JScrollPane(field), BorderLayout.NORTH);
			{
				JPanel checkk = new JPanel(new BorderLayout());
				checkk.add(wraparound, BorderLayout.CENTER);
				checkk.add(reverse, BorderLayout.SOUTH);
				
				mainpanel.add(checkk, BorderLayout.CENTER);
			}
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
		JCheckBox reverse = new JCheckBox("Reverse");
		
		ActionListener rep = (e) -> {
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
				
				int oldCaret = field.getCaretPosition();
				
				searchStatus = true;
				area.requestFocus();
				if(reverse.isSelected()) currentSearchReplaceNumber -= 1;
				else currentSearchReplaceNumber += 1;
				
				
				try{
					if(getText().length() < matchpos.get(currentSearchReplaceNumber)) {
						area.setCaretPosition(matchpos.get(currentSearchReplaceNumber));
						area.select(matchpos.get(currentSearchReplaceNumber), matchpos.get(currentSearchReplaceNumber) + field.getText().length());
					}
				}
				catch(IndexOutOfBoundsException ioobe) {
					field.requestFocus();
					field.setCaretPosition(oldCaret);
					return;
				}
				area.replaceSelection(replacement.getText());
				
				field.requestFocus();
				field.setCaretPosition(oldCaret);
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
						
						int oldIndex = field.getCaretPosition();
						
						area.requestFocus();
						try{
							if(getText().length() < matchpos.get(currentSearchReplaceNumber)) {
								area.setCaretPosition(matchpos.get(currentSearchReplaceNumber));
								area.select(matchpos.get(currentSearchReplaceNumber), matchpos.get(currentSearchReplaceNumber) + field.getText().length());
							}
						}
						catch(IndexOutOfBoundsException ioobe) {
							field.requestFocus();
							field.setCaretPosition(oldIndex);
							return;
						}
						area.replaceSelection(replacement.getText());
						
						field.requestFocus();
						field.setCaretPosition(oldIndex);
					}
					else {
						JOptionPane.showMessageDialog(winparent, "Text '" + field.getText() + "' not found");
					}
				}
				else {
					JOptionPane.showMessageDialog(winparent, "Text '" + field.getText() + "' not found");
				}
			
			}
		};
		
		field.addActionListener(rep);
		replacement.addActionListener(rep);
		
		{
			JButton findbutton = new JButton("Replace");
			JButton cancelbutton = new JButton("Cancel");
			
			cancelbutton.addActionListener((e) -> {
				dialog.dispose();
			});
			
			findbutton.addActionListener(rep);
			
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
			{
				JPanel checkk = new JPanel(new BorderLayout());
				checkk.add(wraparound, BorderLayout.CENTER);
				checkk.add(reverse, BorderLayout.SOUTH);
				
				mainpanel.add(checkk, BorderLayout.CENTER);
			}
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
	
	public int getCurrentLine() {
		return TextUtils.getLineAtCaret(area);
	}
	
	public int getCurrentColumn() {
		return TextUtils.getColumnAtCaret(area);
	}
}
