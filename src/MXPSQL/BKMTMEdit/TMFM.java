package MXPSQL.BKMTMEdit;

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

import bsh.*;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import javafx.scene.*;
import java.awt.dnd.*;
import java.awt.Dialog;
import java.nio.file.*;
import java.awt.event.*;
import javax.swing.tree.*;
import java.util.Optional;
import javax.swing.event.*;
import javafx.embed.swing.*;
import javafx.scene.control.*;
import java.awt.datatransfer.*;
import MXPSQL.BKMTMEdit.utils.*;
import org.mozilla.javascript.*;
import MXPSQL.BKMTMEdit.widgets.*;
import javafx.application.Platform;
import MXPSQL.BKMTMEdit.reusable.*;
import org.apache.commons.io.FilenameUtils;
import java.util.concurrent.CountDownLatch;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.apache.commons.lang3.tuple.ImmutablePair;
// import MXPSQL.BKMTMEdit.pluginapi.BKMTMEditTabPlugin;


public class TMFM extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public JTabbedPane rb = new JTabbedPane();
	
	public Runnable run;
	
	
	public JSplitPane jsplit;
	public JTabbedPane tabbedEditor = new JTabbedPane();
	
	public JLabel filenameLabel;
	
	
	public ProgressBar pbar;
	
	int webdoccount = 1;
	int textdoccount = 1;
	int termsessioncount = 1;
	
	JFrame dis = this;
	
	TxEditor latestMJFX;
	
	// Reused from https://github.com/MXP2095onetechguy/JEXTEdit/blob/master/src/app/MXPSQL/JEXTEdit/EditorWindow.java#L136 and modified
    DefaultListModel<FirstElementPair<String, Integer>> filesHoldListModel=new DefaultListModel<FirstElementPair<String, Integer>>();
    JList<FirstElementPair<String, Integer>> filesHoldList=new JList<FirstElementPair<String, Integer>>(filesHoldListModel);
    
    JTree ftree;
    DefaultTreeModel fmodel = new DefaultTreeModel(FileTree.scan(new File(StaticStorageProperties.cwd)));
    FTreeWorker ftreeworker;
    
    SwingWorker<Void, Object> filechangedworker;
    
    SwingWorker<Void, Void> bshmacroworker;
    
    protected JMenu jarpluginm;
    protected JMenu bshpluginm;
	
	
	private void initUI() {
		JPanel contentPane = new JPanel(new BorderLayout());
		{
			contentPane.add(rb, BorderLayout.NORTH);
			new DropTarget(rb, new FileDND());
			
			new DropTarget(filesHoldList, new FileDND());
			
			{ 
				JToolBar filetb = new JToolBar();
				
				filetb.setFloatable(false);
				
				JButton newFileButton = new JButton(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/New.png")));
				JButton newWebButton = new JButton(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/NewWeb.png")));
				
				filetb.add(newFileButton);
				filetb.add(newWebButton);
				
				newWebButton.setToolTipText("New web browser");
				
				newFileButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						addDefaultEditor();
					}
					
				});	
				
				newWebButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						addWebBrowser();
					}
					
				});
				
				
				filetb.addSeparator();
				
				JButton openFileButton = new JButton(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/Open.png")));
				JButton viewFileButton = new JButton(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/viewer.png")));
				JButton openRestButton = new JButton(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/OpenWebRq.png")));
				
				openRestButton.setToolTipText("Open by downloading a document from the internet");
				viewFileButton.setToolTipText("View file");
				
				openFileButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						open_file();
					}
					
				});
				
				viewFileButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						view_file();
					}
					
				});
				
				openRestButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						openEditorFromInernet();
						// JOptionPane.showMessageDialog(null, "Seriusly broken");
					}
					
				});
				
				filetb.add(openFileButton);
				filetb.add(viewFileButton);
				filetb.add(openRestButton);
				
				filetb.addSeparator();
				
				JButton infofile = new JButton(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/info.png")));
				
				infofile.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						Component comp = tabbedEditor.getSelectedComponent();
						
						if(!(comp instanceof TxEditor))
							return;
						
						TxEditor editor = (TxEditor) comp;
						
						try {
							if(tabbedEditor.getSelectedIndex() > -1) {
								if(!(editor.filePath == null || editor.getText().isBlank() || editor.getText().isEmpty())) {
									getInfoDialog(new File(editor.filePath));
								}
								else {
									JOptionPane.showMessageDialog(dis, "Um no, it's not a file.");
								}
							}
							else {
								JOptionPane.showMessageDialog(dis, "Um no, there is nothing here.");
							}
						}
						catch(IOException ioe) {
							JOptionPane.showMessageDialog(dis, "Um, No", "nO", JOptionPane.ERROR_MESSAGE);
						}
					}
					
				});
				
				filetb.add(infofile);
				
				filetb.addSeparator();
				
				JButton closeTab = new JButton(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/CloseTab.png")));
				JButton closeAllTab = new JButton(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/CloseAll.png")));
				
				closeTab.setToolTipText("Close the currently selected tab");
				closeAllTab.setToolTipText("Close all tab");
				
				closeTab.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						try {
							closeCurrentTab();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
						}
					}
					
				});
				
				closeAllTab.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						closeAllTab();
					}
					
				});
				
				JButton saveButton = new JButton(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/Save.png")));
				JButton saveasButton = new JButton(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/SaveAs.png")));
				
				saveButton.setToolTipText("Save file");
				saveasButton.setToolTipText("Save file as");
				
				saveButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						save_file(null);
					}
					
				});
				
				saveasButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						saveAs_file(null);
					}
					
				});
				
				filetb.add(saveButton);
				filetb.add(saveasButton);
				
				filetb.addSeparator();
				
				filetb.add(closeTab);
				filetb.add(closeAllTab);
				
				rb.add(filetb, "File");
			}
			
			{
				JToolBar edittb = new JToolBar();
				
				edittb.setFloatable(false);
				
				{
					JButton findButton = new JButton(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/search.png")));
					JButton replaceButton = new JButton(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/replace.png")));
					
					findButton.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							// TODO Auto-generated method stub
							Component comp = tabbedEditor.getSelectedComponent();
							if(comp instanceof TxEditor) {
								TxEditor mjfx = (TxEditor) comp;
								if(mjfx.findDialog()) {
									markChanged();
								}
							}
						}
						
					});
					
					replaceButton.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							// TODO Auto-generated method stub
							Component comp = tabbedEditor.getSelectedComponent();
							if(comp instanceof TxEditor) {
								TxEditor mjfx = (TxEditor) comp;
								if(mjfx.replaceDialog()) {
									markChanged();
								}
							}
						}
						
					});
					
					edittb.add(findButton);
					edittb.add(replaceButton);
					edittb.addSeparator();
				}
				
				{
					JButton addTime = new JButton(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/time.png")));
					JButton addUser = new JButton(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/user.png")));
					JButton addLorem = new JButton(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/TrollGFRight.gif")).getImage().getScaledInstance(48, 48, Image.SCALE_DEFAULT)));
					JButton bork = new JButton(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/bork.PNG")).getImage().getScaledInstance(48, 48, Image.SCALE_DEFAULT)));
					
					addUser.setToolTipText("Insert username into text");
					addTime.setToolTipText("Insert current time into text");
					addLorem.setToolTipText("Insert lorem ipsum into the text");
					bork.setToolTipText("bork bork bork");
					
					addTime.addActionListener((e) -> insertTime());
					addUser.addActionListener((e) -> insertUser());
					addLorem.addActionListener((e) -> insertLorem());
					bork.addActionListener((e) -> borkText());
					
					edittb.add(addTime);
					edittb.add(addUser);
					edittb.add(addLorem);
					edittb.add(bork);
					edittb.addSeparator();
				}
				
				{
					JButton undobtn = new JButton(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/undo.png")));
					JButton redobtn = new JButton(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/redo.png")));
					
					undobtn.addActionListener((e) -> {
						if(tabbedEditor.getSelectedIndex() != -1) {
							Component comp = tabbedEditor.getSelectedComponent();
							
							if(comp instanceof TxEditor) {
								TxEditor mjfx = (TxEditor) comp;
								
								mjfx.area.undoLastAction();
							}
						}
					});
					
					redobtn.addActionListener((e) -> {
						if(tabbedEditor.getSelectedIndex() != -1) {
							Component comp = tabbedEditor.getSelectedComponent();
							
							if(comp instanceof TxEditor) {
								TxEditor mjfx = (TxEditor) comp;
								
								
								
								mjfx.area.redoLastAction();
							}
						}
					});
					
					edittb.add(undobtn);
					edittb.add(redobtn);
					edittb.addSeparator();
				}
				
				{
					JButton copybtn = new JButton(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/copy.png")));
					JButton pastebtn = new JButton(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/paste.png")));
					JButton cutbtn = new JButton(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/cut.png")));
					
					copybtn.addActionListener((e) -> copyText());
					
					pastebtn.addActionListener((e) -> pasteText());
					
					cutbtn.addActionListener((e) -> cutText());
					
					edittb.add(copybtn);
					edittb.add(pastebtn);
					edittb.add(cutbtn);
					edittb.addSeparator();
				}
				
				{
					JButton selall = new JButton(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/selall.png")));
					JButton delsel = new JButton(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/delsel.png")));
					
					selall.setToolTipText("Select all");
					delsel.setToolTipText("Delete selected");
					
					selall.addActionListener((e) -> selectAllText());
					
					delsel.addActionListener((e) -> deleteAllText());
					
					edittb.add(selall);
					edittb.add(delsel);
				}
				
				rb.add(edittb, "Edit");
			}
			
			{
				JMenuBar mb = new JMenuBar();
				
				{
					JMenu filem = new JMenu("File");
					
					{
						JMenuItem newfmi = new JMenuItem("New File");
						newfmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
						newfmi.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/New.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
						newfmi.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								// TODO Auto-generated method stub
								addDefaultEditor();
							}
							
						});
						
						JMenuItem newwmi = new JMenuItem("New Web Browser");
						newwmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK+ActionEvent.SHIFT_MASK));
						newwmi.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/NewWeb.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
						newwmi.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								// TODO Auto-generated method stub
								addWebBrowser();
							}
							
						});
						
						filem.add(newwmi);
						filem.add(newfmi);
					}
					
					filem.addSeparator();
					
					{
						JMenuItem openfmi = new JMenuItem("Open file");
						openfmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
						openfmi.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/Open.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
						
						
						JMenuItem openwmi = new JMenuItem("Open from internet");
						openwmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK+ActionEvent.SHIFT_MASK));
						openwmi.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/OpenWebRq.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
						
						JMenuItem viewfmi = new JMenuItem("View file");
						viewfmi.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/viewer.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
						
						openfmi.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								// TODO Auto-generated method stub
								open_file();
							}
							
						});
						
						viewfmi.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								// TODO Auto-generated method stub
								view_file();
							}
							
						});
						
						openwmi.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								// TODO Auto-generated method stub
								openEditorFromInernet();
							}
							
						});
						
						filem.add(openfmi);
						filem.add(viewfmi);
						filem.add(openwmi);
					}
					
					filem.addSeparator();
					{
						JMenuItem infomi = new JMenuItem("Get Info");
						
						infomi.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/info.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
						
						infomi.addActionListener((e) -> {
							Component comp = tabbedEditor.getSelectedComponent();
							
							if(!(comp instanceof TxEditor))
								return;
							
							TxEditor editor = (TxEditor) comp;
							
							try {
								getInfoDialog(new File(editor.filePath));
							}
							catch(IOException ioe) {
								JOptionPane.showMessageDialog(dis, "Um, No", "nO", JOptionPane.ERROR_MESSAGE);
							}
						});
						
						filem.add(infomi);
					}
					
					filem.addSeparator();
					{
						JMenuItem savemi = new JMenuItem("Save");
						JMenuItem saveasmi = new JMenuItem("Save As");
						
						savemi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
						saveasmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK+ActionEvent.SHIFT_MASK));
						savemi.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/Save.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
						saveasmi.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/SaveAs.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
						
						savemi.addActionListener((e) -> {
							save_file(null);
						});
						
						saveasmi.addActionListener((e) -> {
							saveAs_file(null);
						});
						
						filem.add(savemi);
						filem.add(saveasmi);
					}
					
					filem.addSeparator();
					{
						JMenuItem closetabmi = new JMenuItem("Close tab");
						JMenuItem closealltabmi = new JMenuItem("Close all tabs");
					
						closetabmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK));
						closealltabmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK+ActionEvent.SHIFT_MASK));
						closetabmi.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/CloseTab.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
						closealltabmi.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/CloseAll.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
					
						closetabmi.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								// TODO Auto-generated method stub
								try {
									closeCurrentTab();
								} catch (InterruptedException e1) {
									// TODO Auto-generated catch block
								}
							}
						
						});
						
						closealltabmi.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								// TODO Auto-generated method stub
								closeAllTab();
							}
							
						});
					
						filem.add(closetabmi);
						filem.add(closealltabmi);
					}
					
					filem.addSeparator();
					
					{
						JMenuItem exitmi = new JMenuItem("Exit");
						exitmi.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/Exit.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
					
						exitmi.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								// TODO Auto-generated method stub
								dispatchEvent(new WindowEvent(dis, WindowEvent.WINDOW_CLOSING));
							}
						
						});
					
						filem.add(exitmi);
					}
					
					mb.add(filem);
				}

                {
                    JMenu editm = new JMenu("Edit");
                    mb.add(editm);
                    
                    {
                    	JMenu searchm = new JMenu("Search");
                    	
                        JMenuItem findmi = new JMenuItem("Find");
                        JMenuItem replacemi = new JMenuItem("Replace");
                        
                        findmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
                        replacemi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK+ActionEvent.SHIFT_MASK));
                        findmi.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/search.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
                        replacemi.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/replace.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
                        
                        findmi.addActionListener(new ActionListener() {

    						@Override
    						public void actionPerformed(ActionEvent e) {
    							// TODO Auto-generated method stub
    							Component comp = tabbedEditor.getSelectedComponent();
    							if(comp instanceof TxEditor) {
    								TxEditor mjfx = (TxEditor) comp;
    								if(mjfx.findDialog()) {
    									markChanged();
    								}
    							}
    						}
                        	
                        });
                        
                        replacemi.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								// TODO Auto-generated method stub
								Component comp = tabbedEditor.getSelectedComponent();
    							if(comp instanceof TxEditor) {
    								TxEditor mjfx = (TxEditor) comp;
    								if(mjfx.replaceDialog()) {
    									markChanged();
    								}
    							}
							}
                        	
                        });
                    	
                    	searchm.add(findmi);
                    	searchm.add(replacemi);
                    	
                    	editm.add(searchm);
                    }
                    
                    {
                    	JMenu insertm = new JMenu("Insert");
                    	
                    	JMenuItem insertUser = new JMenuItem("Username");
                    	JMenuItem insertTime = new JMenuItem("Time");
                    	JMenuItem insertLorem = new JMenuItem("Lorem");
                    	JMenuItem borkit = new JMenuItem("Bork");
                    	
                    	
                    	insertUser.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK));
                    	insertTime.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK+ActionEvent.SHIFT_MASK));
                    	insertLorem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK+ActionEvent.SHIFT_MASK+InputEvent.ALT_DOWN_MASK));
                    	borkit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK+InputEvent.ALT_DOWN_MASK));
                    	insertUser.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/user.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
                    	insertTime.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/time.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
                    	insertLorem.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/TrollGFRight.gif")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
                    	borkit.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/bork.PNG")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
                    	
                    	insertTime.addActionListener((e) -> insertTime());
                    	insertUser.addActionListener((e) -> insertUser());
                    	insertLorem.addActionListener((e) -> insertLorem());
                    	borkit.addActionListener((e) -> borkText());
                    	
                    	insertm.add(insertUser);
                    	insertm.add(insertTime);
                    	insertm.add(insertLorem);
                    	insertm.add(borkit);
                    	
                    	editm.add(insertm);
                    }
                    
                    {
                    	JMenu clipbm = new JMenu("Clipboard");
                    	
                    	JMenuItem copymi = new JMenuItem("Copy");
                    	JMenuItem pastemi = new JMenuItem("Paste");
                    	JMenuItem cutmi = new JMenuItem("Cut");
                    	
                    	copymi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
                    	pastemi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
                    	cutmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
                    	
                    	copymi.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/copy.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
                    	pastemi.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/paste.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
                    	cutmi.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/cut.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
                    	
                    	cutmi.addActionListener((e) -> cutText());
                    	
                    	copymi.addActionListener((e) -> copyText());
                    	
                    	pastemi.addActionListener((e) -> pasteText());
                    	
                    	clipbm.add(copymi);
                    	clipbm.add(pastemi);
                    	clipbm.add(cutmi);
                    	editm.add(clipbm);
                    }
                    
                    {
                    	JMenu selutilm = new JMenu("Selection Utilities");
                    	
                    	JMenuItem selallmi = new JMenuItem("Select all");
                    	JMenuItem delselmi = new JMenuItem("Delete Selected");
                    	
                    	selallmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
                    	
                    	selallmi.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/selall.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
                    	delselmi.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/delsel.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
                    	
                    	selallmi.addActionListener((e) -> selectAllText());
                    	
                    	delselmi.addActionListener((e) -> deleteAllText());
                    	
                    	selutilm.add(selallmi);
                    	selutilm.add(delselmi);
                    	
                    	editm.add(selutilm);
                    }
                    
                    {
                    }
                }
                
                {
                	JMenu pluginm = new JMenu("Plugins");
                	bshpluginm = new JMenu("Beanshell Plugins");
                	bshpluginm.setEnabled(false);
                	
                	for(Map.Entry<String, String> plugin : StaticStorageProperties.bshPlugins.entrySet()) {
                		JMenuItem pluginmi = null;
                		bshpluginm.setEnabled(true);
                		
                		try {
                			pluginmi = new JMenuItem(FilenameUtils.getBaseName(new File(plugin.getKey()).getCanonicalFile().getName()));
						} catch (IOException e) {
							pluginmi= new JMenuItem("Plugin");
						}
                		
                		pluginmi.addActionListener((e) -> {
                			bsh.Interpreter i = new bsh.Interpreter();
                			
                			try {
                				// System.out.println(plugin.getValue());
                				i.set("tabbedEditor", tabbedEditor);
								i.eval(plugin.getValue());
							} catch (EvalError e1) {
								// TODO Auto-generated catch block
								ByteArrayOutputStream baos = new ByteArrayOutputStream();
								PrintStream bs = new PrintStream(baos);
								e1.printStackTrace(bs);
								StaticStorageProperties.logger.error("Evaluation error");
								System.err.println(baos.toString());
								JOptionPane.showMessageDialog(dis, baos.toString(), "No, smth is wrong with your plguin code", JOptionPane.ERROR_MESSAGE);
							}
                		});
                		
                		bshpluginm.add(pluginmi);
                	}
                	
                	jarpluginm = new JMenu("JAR Plugins");
                	jarpluginm.setEnabled(false);
                	
                	pluginm.add(bshpluginm);
                	pluginm.add(jarpluginm);
                	mb.add(pluginm);
                }
                
                {
                	JMenu runm = new JMenu("Run");
                	
                	JMenuItem runtermmi = new JMenuItem("add terminal");
                	JMenuItem browsermi = new JMenuItem("Open url in web browser");
                	JMenuItem bshmacrosmi = new JMenuItem("Run beanshell macros");
                	JMenuItem rjsmacrosmi = new JMenuItem("Run rhino javascript macros");
                	JMenuItem opendefaulteditormi = new JMenuItem("Open in default editor");
                	JMenuItem openindefaultmi = new JMenuItem("Open file in default program");
                	
                	
                	runtermmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK));
                	bshmacrosmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
                	rjsmacrosmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK+ActionEvent.SHIFT_MASK));
                	runtermmi.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/Term.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
                	bshmacrosmi.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/macro.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
                	rjsmacrosmi.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/macro.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
                	
                	runtermmi.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							// TODO Auto-generated method stub
							addTerminal();
						}
                		
                	});
                	
                	browsermi.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							// TODO Auto-generated method stub
							if(Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))
								try {
									String url = JOptionPane.showInputDialog("URL pls");
									if(url != null)
										Desktop.getDesktop().browse(new URI(url));
								} catch (IOException | URISyntaxException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
						}
                		
                	});
                	
                	bshmacrosmi.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							// TODO Auto-generated method stub
							runUserBshMacro();
						}
                		
                	});
                	
                	rjsmacrosmi.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							// TODO Auto-generated method stub
							runUserRhinoJSMacro();
						}
                		
                	});
                	
                	opendefaulteditormi.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							// TODO Auto-generated method stub
							if(Desktop.getDesktop().isSupported(Desktop.Action.EDIT)) {
								try {
									Component comp = tabbedEditor.getSelectedComponent();
									
									if(comp instanceof TxEditor) {
										TxEditor jfxcomp = (TxEditor) comp;
										
										if(!jfxcomp.filePath.isEmpty() && !jfxcomp.filePath.isBlank())
											Desktop.getDesktop().edit(new File(jfxcomp.filePath));
										else
											JOptionPane.showMessageDialog(dis, "Not available in filesystem");
									}
									else {
										JOptionPane.showMessageDialog(dis, "Not an editor");
									}
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
									JOptionPane.showMessageDialog(dis, "Error with opening your file", "Open error", JOptionPane.ERROR_MESSAGE);
								}
							}
						}
                		
                	}); 
                	
                	openindefaultmi.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							// TODO Auto-generated method stub
							JFileChooser jfc = new JFileChooser();
							
							jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
							
							if(jfc.showOpenDialog(dis) == JFileChooser.APPROVE_OPTION) {
								if(Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
									try {
										Desktop.getDesktop().open(jfc.getSelectedFile());
									} catch (IOException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
										JOptionPane.showMessageDialog(dis, "Error with opening your file", "Open error", JOptionPane.ERROR_MESSAGE);
									}
								}
							}
						}
                		
                	});
                	
                	runm.add(runtermmi);
                	runm.add(browsermi);
                	runm.addSeparator();
                	runm.add(bshmacrosmi);
                	runm.add(rjsmacrosmi);
                	runm.addSeparator();
                	runm.add(opendefaulteditormi);
                	runm.add(openindefaultmi);
                	
                	mb.add(runm);
                }
                
                {
                	JMenu helpm = new JMenu("Help");
                	
                	JMenuItem aboutmi = new JMenuItem("About");
                	aboutmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK+InputEvent.ALT_DOWN_MASK+ActionEvent.SHIFT_MASK));
                	
                	aboutmi.addActionListener((e) -> {
                		showAbout();
                	});
                	
                	helpm.add(aboutmi);
                	
                	mb.add(helpm);
                }
				
                new DropTarget(mb, new FileDND());
				setJMenuBar(mb);
			}
		}
		
		{	
			JPanel statusPanel = new JPanel(new BorderLayout());
			
			JFXPanel javafxPanel = new JFXPanel();
			pbar = new ProgressBar(0);
			Scene scene = new Scene(pbar);
			javafxPanel.setScene(scene);
			javafxPanel.setToolTipText("U totally not idot.");			
			statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.PAGE_AXIS));
			statusPanel.add(javafxPanel, BorderLayout.NORTH);
			new DropTarget(javafxPanel, new FileDND());
			
			JToolBar statusBar = new JToolBar();
			statusBar.setFloatable(false);
			
	        JLabel readylabel=new JLabel(StaticStorageProperties.baseTitle);
	        readylabel.setFont(new Font("Calibri",Font.PLAIN,15));
	        new DropTarget(readylabel, new FileDND());
	        filenameLabel = new JLabel("");
	        filenameLabel.setFont(new Font("Calibri",Font.PLAIN,15));
	        new DropTarget(filenameLabel, new FileDND());
	        statusBar.add(readylabel);
	        statusBar.add(new JLabel("                          "));
	        statusBar.add(filenameLabel);
	        new DropTarget(statusBar, new FileDND());

			statusPanel.add(statusBar, BorderLayout.CENTER);
			new DropTarget(statusPanel, new FileDND());
			
			contentPane.add(statusPanel, BorderLayout.SOUTH);
		}
		
		
		{
			JTabbedPane docPane = new JTabbedPane();
			new DropTarget(docPane, new FileDND());
			{
				JPanel fhlpanel = new JPanel(new BorderLayout());
				JButton refresh = new JButton("Refresh");
				fhlpanel.add(refresh, BorderLayout.NORTH);
				refresh.addActionListener((e) -> {
					refreshList();
				});
				
				filesHoldList.addListSelectionListener(new List2TabSelectionListener());
				fhlpanel.add(new JScrollPane(filesHoldList));
				docPane.add(fhlpanel, "Document List");
			}
			
			jsplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, docPane, tabbedEditor);
			new DropTarget(jsplit, new FileDND());
	        jsplit.setContinuousLayout(true);
	        jsplit.setOneTouchExpandable(true);
	        jsplit.setDividerLocation(210);
			contentPane.add(jsplit);
			
			tabbedEditor.addChangeListener(new TabChanged());
			new DropTarget(tabbedEditor, new FileDND());
			
			filenameLabel.setText("No lol, I am still starting/");
			
			ftree = new JTree(fmodel);
			new DropTarget(ftree, new FileDND());
			
			MouseListener ml = new MouseAdapter() {
			    public void mousePressed(MouseEvent e) {
			        int selRow = ftree.getRowForLocation(e.getX(), e.getY());
			        if(selRow != -1) {
			        	if(SwingUtilities.isRightMouseButton(e)) {
			        		JPopupMenu popup = new JPopupMenu();
			        		
			        		{
			        			JMenuItem del = new JMenuItem("Delete");
			        			JMenuItem newf = new JMenuItem("New");
			        			JMenuItem openf = new JMenuItem("Open");
			        			JMenuItem viewf = new JMenuItem("View");
			        			JMenuItem rename = new JMenuItem("Rename");
			        			JMenuItem getinfo = new JMenuItem("Get Info");
			        			
			        			newf.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/New.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
			        			openf.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/Open.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
			        			viewf.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/viewer.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
			        			del.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/delsel.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
			        			rename.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/redo.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
			        			getinfo.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/info.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
			        			
			        			rename.addActionListener((e2) -> {
			        				TreePath path = ftree.getPathForLocation(e.getX(), e.getY());
			        				
			        				if(path == null)
			        					return;
			        				
			        				DefaultMutableTreeNode rightClickedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
			        				
			        			    TreePath[] selectionPaths = ftree.getSelectionPaths();

			        			    boolean isSelected = false;
			        			    if (selectionPaths != null) {
			        			      for (TreePath selectionPath : selectionPaths) {
			        			        if (selectionPath.equals(path)) {
			        			          isSelected = true;
			        			        }
			        			      }
			        			    }
			        			    if (!isSelected) {
			        			      ftree.setSelectionPath(path);
			        			    }
			        			    
			        			    Object objec = rightClickedNode.getUserObject();
			        			    
			        			    if(!(objec instanceof File)) {
			        			    	return;
			        			    }
			        			    
			        			    String filename = JOptionPane.showInputDialog(dis, "What do you want for your renamed thing? \nIt is from " + ((File) objec) + " to ...", ((File) objec).getName());
			        			    
			        			    if(filename != null) {
		        			    		File f = (File) objec;
		        			    		File f2 = Paths.get(StaticStorageProperties.cwdPath.toFile().toURI()).resolve(filename).toFile();
		        			    		
										if(!f.renameTo(f2)) {
											JOptionPane.showMessageDialog(dis, "There seems to be a problem renaming the thing.", "No", JOptionPane.ERROR_MESSAGE);
										}
										
										try {
											int maxindex = tabbedEditor.getTabCount();
											
											for(int i = 0; i < maxindex; i++) {
												Component comp = tabbedEditor.getComponentAt(i);
												
												if(comp instanceof TxEditor) {
													TxEditor mjfx = (TxEditor) comp;
													
													if(mjfx.filePath == f.toString()) {
														mjfx.filePath = f2.toString();
														tabbedEditor.setTitleAt(i, f2.toPath().getFileName().toString());
													}
												}
											}
										}
										catch(Exception ex) {
											
										}
			        			    }
			        			});
			        			
			        			openf.addActionListener((e2) -> {
			        				TreePath path = ftree.getPathForLocation(e.getX(), e.getY());
			        				
			        				if(path == null)
			        					return;
			        				
			        				DefaultMutableTreeNode rightClickedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
			        				
			        			    TreePath[] selectionPaths = ftree.getSelectionPaths();

			        			    boolean isSelected = false;
			        			    if (selectionPaths != null) {
			        			      for (TreePath selectionPath : selectionPaths) {
			        			        if (selectionPath.equals(path)) {
			        			          isSelected = true;
			        			        }
			        			      }
			        			    }
			        			    if (!isSelected) {
			        			      ftree.setSelectionPath(path);
			        			    }
			        			    
			        			    Object objec = rightClickedNode.getUserObject();
			        			    
			        			    if(objec instanceof File) {
			        			    	File f = (File) objec;
			        			    	
			        			    	if(f.isFile()) {
			        			    		try {
												open_file_from_File(f);
											} catch (InterruptedException e1) {
												// TODO Auto-generated catch block
												e1.printStackTrace();
											}
			        			    	}
			        			    }
			        			});
			        			
			        			viewf.addActionListener((e2) -> {
			        				TreePath path = ftree.getPathForLocation(e.getX(), e.getY());
			        				
			        				if(path == null)
			        					return;
			        				
			        				DefaultMutableTreeNode rightClickedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
			        				
			        			    TreePath[] selectionPaths = ftree.getSelectionPaths();

			        			    boolean isSelected = false;
			        			    if (selectionPaths != null) {
			        			      for (TreePath selectionPath : selectionPaths) {
			        			        if (selectionPath.equals(path)) {
			        			          isSelected = true;
			        			        }
			        			      }
			        			    }
			        			    if (!isSelected) {
			        			      ftree.setSelectionPath(path);
			        			    }
			        			    
			        			    Object objec = rightClickedNode.getUserObject();
			        			    
			        			    if(objec instanceof File) {
			        			    	File f = (File) objec;
			        			    	
			        			    	if(f.isFile()) {
			        			    		view_file_from_File(f);
			        			    	}
			        			    }
			        			});
			        			
			        			newf.addActionListener((e2) -> {
			        				TreePath path = ftree.getPathForLocation(e.getX(), e.getY());
			        				
			        				if(path == null)
			        					return;
			        				
			        				DefaultMutableTreeNode rightClickedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
			        				
			        			    TreePath[] selectionPaths = ftree.getSelectionPaths();

			        			    boolean isSelected = false;
			        			    if (selectionPaths != null) {
			        			      for (TreePath selectionPath : selectionPaths) {
			        			        if (selectionPath.equals(path)) {
			        			          isSelected = true;
			        			        }
			        			      }
			        			    }
			        			    if (!isSelected) {
			        			      ftree.setSelectionPath(path);
			        			    }
			        			    
			        			    String name = JOptionPane.showInputDialog(dis, "File name...");
			        			    
			        			    if(name != null) {
			        			    	File parent = new File(StaticStorageProperties.cwd);
			        			    	Object objec;
			        			    	
			        			    	{
			        			    		objec = rightClickedNode.getUserObject();
			        			    		if(objec instanceof File) {
			        			    			File f = (File) objec;
			        			    			
			        			    			if(f.isDirectory()) {
			        			    				parent = f;
			        			    			}
			        			    			else {
			        			    				if(f.getParentFile().isDirectory())
			        			    					parent = f.getParentFile();
			        			    			}
			        			    		}
			        			    	}
			        			    	try {
			        			    		Paths.get(name);
			        			    		new File(name).getCanonicalFile();
			        			    	}
			        			    	catch(FileSystemException fsex) {
			        			    		JOptionPane.showMessageDialog(dis, "The name you put in is not really valid.", "No", JOptionPane.ERROR_MESSAGE);
			        			    		return;
			        			    	}
			        			    	catch(IOException ioex) {
			        			    		JOptionPane.showMessageDialog(dis, "The name you put in is causing problem.", "No", JOptionPane.ERROR_MESSAGE);
			        			    		return;
			        			    	}
			        			    	catch(InvalidPathException ipex) {
			        			    		JOptionPane.showMessageDialog(dis, "The name you put in is empty lol, we are not making a file with that.", "No", JOptionPane.ERROR_MESSAGE);
			        			    		return;
			        			    	}
			        			    	
			        			    	
			        			    	File tobecreated = Paths.get(parent.toURI().resolve(name)).toFile();
			        			    	
			        			    	if(!tobecreated.exists()) {
			        			    		int yesno = JOptionPane.showConfirmDialog(dis, "Do you want it to be a file? You may cancel it if you don't want to do it.");
			        			    		try {
			        			    			if(yesno == JOptionPane.YES_OPTION) {
													tobecreated.createNewFile();
			        			    			}
			        			    			else if(yesno == JOptionPane.NO_OPTION) {
			        			    				tobecreated.mkdir();
			        			    			}
											} catch (IOException e1) {
												// TODO Auto-generated catch block
												e1.printStackTrace();
											}
			        			    	}
			        			    	else {
			        			    		JOptionPane.showMessageDialog(dis, "It exist. \n" + tobecreated + " exist.");			        			    	}
			        			    }
			        			});
			        			
			        			del.addActionListener((e2) -> {
			        				TreePath path = ftree.getPathForLocation(e.getX(), e.getY());
			        				
			        				if(path == null)
			        					return;
			        				
			        				DefaultMutableTreeNode rightClickedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
			        				
			        			    TreePath[] selectionPaths = ftree.getSelectionPaths();

			        			    boolean isSelected = false;
			        			    if (selectionPaths != null) {
			        			      for (TreePath selectionPath : selectionPaths) {
			        			        if (selectionPath.equals(path)) {
			        			          isSelected = true;
			        			        }
			        			      }
			        			    }
			        			    if (!isSelected) {
			        			      ftree.setSelectionPath(path);
			        			    }
			        			    
			        			    Object objec = rightClickedNode.getUserObject();
			        			    
			        			    if(!(objec instanceof File)) {
			        			    	return;
			        			    }
			        			    
			        			    int yes = JOptionPane.showConfirmDialog(dis, "Do you want to delete this thing? \n It's name is " + ((File) objec) + "\n Is it a file: " + ((File) objec).isFile(), "yES or no", JOptionPane.YES_NO_OPTION, JOptionPane.DEFAULT_OPTION);
			        			    
			        			    if(yes == JOptionPane.YES_OPTION) {
		        			    		File f = (File) objec;
		        			    		
		        			    		if(f.delete()) {
		        			    			JOptionPane.showMessageDialog(dis, "Successfully deleted thing");
		        			    			// fmodel.setRoot(FileTree.scan(StaticStorageProperties.cwdPath.toFile()));
		        			    			// fmodel.reload();
		        			    		}
		        			    		else {
		        			    			JOptionPane.showMessageDialog(dis, "Filed to delete file", "Delete Failed", JOptionPane.ERROR_MESSAGE);
		        			    		}
			        			    }
			        			});
			        			
			        			getinfo.addActionListener((e2) -> {
			        				TreePath path = ftree.getPathForLocation(e.getX(), e.getY());
			        				
			        				if(path == null)
			        					return;
			        				
			        				DefaultMutableTreeNode rightClickedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
			        				
			        			    TreePath[] selectionPaths = ftree.getSelectionPaths();

			        			    boolean isSelected = false;
			        			    if (selectionPaths != null) {
			        			      for (TreePath selectionPath : selectionPaths) {
			        			        if (selectionPath.equals(path)) {
			        			          isSelected = true;
			        			        }
			        			      }
			        			    }
			        			    if (!isSelected) {
			        			      ftree.setSelectionPath(path);
			        			    }
			        			    
			        			    Object objec = rightClickedNode.getUserObject();
			        			    
			        			    if(!(objec instanceof File)) {
			        			    	return;
			        			    }
			        			    
			        			    File f = (File) objec;
			        			    
									try {
										getInfoDialog(f);
									} catch (IOException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
										JOptionPane.showMessageDialog(dis, "Um, No", "nO", JOptionPane.ERROR_MESSAGE);
									}
			        			    
			        			});
			        			
			        			popup.add(newf);
			        			popup.add(rename);
			        			popup.addSeparator();
			        			popup.add(openf);
			        			popup.add(viewf);
			        			popup.addSeparator();
			        			popup.add(del);
			        			popup.addSeparator();
			        			popup.add(getinfo);
			        		}
			        		
			        		if(e.getClickCount() == 1) {
			        			popup.show(e.getComponent(), e.getX(), e.getY());
			        		}
			        	}
			        	else {
				        	if(e.getClickCount() == 2) {
				        		DefaultMutableTreeNode node = (DefaultMutableTreeNode) ftree.getLastSelectedPathComponent();
				        		if(node.getUserObject() instanceof File) {
				        			File path = (File) node.getUserObject();
				        			
				        			try {
				        				if(path.isFile())
				        					open_file_from_File(path);
				        			}
				        			catch(InterruptedException e2) {
				        				JOptionPane.showMessageDialog(dis, "Error opening file", "File error lol", JOptionPane.ERROR_MESSAGE);
				        			}
				        		}
				            }
			        	}
			        }
			    }
			};
			
			ftree.addMouseListener(ml);
			
			{
				JPanel ftreepanel = new JPanel(new BorderLayout());
				JButton refreshftree = new JButton("Refresh");
				
				refreshftree.addActionListener((e) -> {
					fmodel.setRoot(FileTree.scan(StaticStorageProperties.cwdPath.toFile()));
				});
				
				ftreepanel.add(refreshftree, BorderLayout.NORTH);
				ftreepanel.add(new JScrollPane(ftree));
				
				docPane.add(ftreepanel, "Filesystem Manager");
			}
			
			try {
				ftreeworker = new FTreeWorker(fmodel, StaticStorageProperties.cwdPath);
				ftreeworker.execute();
			}
			catch(IOException ioe) {
				JOptionPane.showMessageDialog(dis, "Failed to create filesystem tree worker\n You need to refresh the filesystem tree manually. \nBtw, ftreeworker is the name of the variable used to handle ftreeworker.", "FTreeworker Error", JOptionPane.ERROR_MESSAGE);
			}
			
			filechangedworker = new SwingWorker<Void, Object>(){

				@Override
				protected Void doInBackground() throws Exception {
					// TODO Auto-generated method stub
					
					try {
						while(!isCancelled()) {
							try {
								while(true) {
									int tabCount = tabbedEditor.getTabCount();
									
									for(int i = 0; i < tabCount; i++) {
										Component comp = tabbedEditor.getComponentAt(i);
										
										if(comp instanceof TxEditor) {
											TxEditor mjfx = (TxEditor) comp;
											
											if(mjfx.filePath != null && !mjfx.filePath.isEmpty() && !mjfx.filePath.isBlank() && mjfx.docName != null && !mjfx.docName.isBlank() && !mjfx.docName.isEmpty()) {
												File fpath = new File(mjfx.filePath);
											
												String text = FSUtils.getTextFromFile(fpath);
												System.out.println(text.equals(mjfx.getText()));
												if(!text.equals(mjfx.getText())) {
													markChanged();
												}
											}
										}
									}
								}
							}
							catch(Exception e) {
								;
							}
						}
					}
					catch(Exception e) {
						e.printStackTrace();
						return null;
					}
					
					return null;
				}
				
			};
			
			
			setContentPane(contentPane);
			
			if(StaticStorageProperties.startingFile == null) {
				addDefaultEditor();
			}
			else {
				try {
					open_file_from_File(StaticStorageProperties.startingFile);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	public TMFM(String title, Runnable run) {
		super();
		setTitle(title);
		initUI();
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// openEditorFromInernet();
		
		this.run = run;
		
		filechangedworker.execute();
		
		new DropTarget(dis, new FileDND());
		
        addWindowListener(new WindowListener() {

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				if(!closeAllTab()) {
					return;
				}
				
				try {
					StaticStorageProperties.server.stop();
					// ftreeworker.cancel(false);
					// filechangedworker.cancel(false);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				ftreeworker.cancel(false);
				
				if(!StaticStorageProperties.logoff)
					StaticStorageProperties.logger.info("Halt and catch on fire lol");
				
				dis.dispose();
				run.run();
			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
        	
        });
	}
	
	class TextListener implements DocumentListener{
		
		void updateDocumentStatus() {
			markChanged();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			// TODO Auto-generated method stub
			updateDocumentStatus();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			// TODO Auto-generated method stub
			updateDocumentStatus();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			// TODO Auto-generated method stub
			updateDocumentStatus();
		}
		
	};
	
	class List2TabSelectionListener implements ListSelectionListener{

		@Override
		public void valueChanged(ListSelectionEvent e) {
			// TODO Auto-generated method stub
			if(filesHoldList.getSelectedValue() != null) {
				FirstElementPair<String, Integer> p = filesHoldList.getSelectedValue();
				
				if(tabbedEditor.getTabCount() > 0) {
					tabbedEditor.setSelectedIndex(p.getRight());
				}
			}
		}
		
	};
	
	class FileDND implements DropTargetListener{

		@Override
		public void dragEnter(DropTargetDragEvent dtde) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void dragOver(DropTargetDragEvent dtde) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void dropActionChanged(DropTargetDragEvent dtde) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void dragExit(DropTargetEvent dte) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void drop(DropTargetDropEvent dtde) {
			// TODO Auto-generated method stub
			dtde.acceptDrop(DnDConstants.ACTION_COPY);
			
			try {
				@SuppressWarnings("unchecked")
				java.util.List<File> droppedFiles = (java.util.List<File>)
	                    dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
				
				for(File filefs : droppedFiles) {
					
					if(filefs.isFile())
						open_file_from_File(filefs);
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
	};
	
	public void closeCurrentTab() throws InterruptedException {
		int selindex = tabbedEditor.getSelectedIndex();
		if(selindex != -1) {
			if(tabbedEditor.getSelectedComponent() instanceof TxEditor) {
				TxEditor mjfx = (TxEditor) tabbedEditor.getSelectedComponent();
				String title = tabbedEditor.getTitleAt(selindex).trim();
				CountDownLatch latch = new CountDownLatch(1);
				String content = mjfx.area.getText();
				String fileContent = "";
				try {
					if(!(mjfx.filePath.isEmpty() || mjfx.filePath.isBlank() || mjfx.filePath == null))
						fileContent = FSUtils.getTextFromFile(new File(mjfx.filePath));
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return;
				}
				if(title.contains("/") || !fileContent.equals(content)) {
					int opt = JOptionPane.showConfirmDialog(dis, "Do you want to save this file?");
					
					if(opt == JOptionPane.YES_OPTION) {
						save_file(() -> latch.countDown());
						try {
							latch.await();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else if(opt == JOptionPane.CANCEL_OPTION) {
						throw new InterruptedException("no");
					}
				}
				
				tabbedEditor.remove(selindex);
			}
			else {
				tabbedEditor.remove(selindex);
			}
		}
		
		refreshList();
	}
	
	boolean closeAllTab() {
		while(tabbedEditor.getTabCount() > 0) {
			try {
				closeCurrentTab();
			}
			catch(InterruptedException ie) {
				return false;
			}
		}
		
		return true;
	}
	
	public void refreshList() {
		filesHoldListModel.removeAllElements();
		
		for(int i = 0; i < tabbedEditor.getTabCount(); i++) {
			filesHoldListModel.addElement(new FirstElementPair<String, Integer>(tabbedEditor.getTitleAt(i), i));
		}
	}
	
	
	public void addWebBrowser() {
		Platform.runLater(() -> pbar.setProgress(ProgressBar.INDETERMINATE_PROGRESS));
		Platform.runLater(() -> {
			WebkitWebBrowser webkit = new WebkitWebBrowser(StaticStorageProperties.home_page);
			new DropTarget(webkit.webb, new FileDND());
			tabbedEditor.add(webkit, "webdocument " + webdoccount);
			webdoccount += 1;
			refreshList();
			pbar.setProgress(0);
		});
	}
	
	public void addTerminal() {
		Platform.runLater(() -> pbar.setProgress(ProgressBar.INDETERMINATE_PROGRESS));
		Platform.runLater(() -> {
			TerminalWidget termw = new TerminalWidget();
			
			new DropTarget(termw, new FileDND());
			tabbedEditor.add(termw, "term " + termsessioncount);
			termsessioncount += 1;
			
			refreshList();
			pbar.setProgress(0);
		});
	}
	
	public void addDefaultEditor() {
		addEditor("Text document " + textdoccount, () -> {
			latestMJFX.docName = "Text document " + textdoccount + "/";
			filenameLabel.setText(latestMJFX.docName);
			latestMJFX.area.getDocument().addDocumentListener(new TextListener());
			
			textdoccount++;
		});
	}
	
	public void addEditor(String title,Runnable run) {
		Platform.runLater(() -> pbar.setProgress(ProgressBar.INDETERMINATE_PROGRESS));
		Platform.runLater(() -> {
			TxEditor mjfx;
			mjfx = new TxEditor(dis);
			latestMJFX = mjfx;
			tabbedEditor.add(mjfx, title);
			new DropTarget(mjfx, new FileDND());
			new DropTarget(mjfx.area, new FileDND());
			pbar.setProgress(0);
			int index=tabbedEditor.getTabCount()-1;
			tabbedEditor.setSelectedIndex(index);
			
			// filenameLabel.setText(title);
			
			refreshList();
			
			run.run();
		});
	}
	
	
	public void openEditorFromInernet() {
		// TODO Auto-generated method stub
		Platform.runLater(() -> pbar.setProgress(ProgressBar.INDETERMINATE_PROGRESS));
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				// TODO Auto-generated method stub
				
				if(!StaticStorageProperties.logoff)
					StaticStorageProperties.logger.debug("WebRq");
				RESTClient cli = new RESTClient(dis, StaticStorageProperties.hclient);
				cli.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
				cli.setVisible(true);
				Optional<String> out = cli.get();
				
				CountDownLatch cdl = new CountDownLatch(1);
				
				filenameLabel.setText("Web Browser");
				
				
				addEditor(cli.URL, () -> {
					cdl.countDown();
				});
				
				try {
					cdl.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
				
				latestMJFX.setText(out.get());
				latestMJFX.area.getDocument().addDocumentListener(new TextListener());
				assert latestMJFX.getText() == out.get();
				
				cli.dispose();
				
				
				Platform.runLater(() -> pbar.setProgress(0));
				return null;
			}
			
		};
		
		worker.execute();
		
	}
	
	
	
	public void open_file() {
		Platform.runLater(() -> pbar.setProgress(ProgressBar.INDETERMINATE_PROGRESS));
		
		SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>(){

			@Override
			protected Object doInBackground() throws Exception {
				// TODO Auto-generated method stub
				
				try {
					JFileChooser jfc = new JFileChooser();
					jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
					
					for(int i = 0; i < StaticStorageProperties.filt.length; i++) {
						jfc.addChoosableFileFilter(StaticStorageProperties.filt[i]);
					}
					
					int r = jfc.showOpenDialog(dis);
					
					if(r == JFileChooser.APPROVE_OPTION) {
						File f = jfc.getSelectedFile();
						
						open_file_from_File(f);
					}
					
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				
				Platform.runLater(() -> pbar.setProgress(0));
				return null;
			}
			
		};
		
		worker.execute();
			
	}
	
	public void open_file_from_File(File f) throws InterruptedException {
		CountDownLatch cdl = new CountDownLatch(1);
		addEditor(f.toPath().getFileName().toString(), () -> {
			cdl.countDown();
		});
		
		cdl.await();
		
		Platform.runLater(() -> {
			
			try {
				latestMJFX.setText(FSUtils.getTextFromFile(f));
				latestMJFX.area.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
				
				for(Map.Entry<String, ImmutablePair<String[], String>> entry : StaticStorageProperties.syntaxset.entrySet()) {
					ImmutablePair<String[], String> syntset = entry.getValue();
					String[] ext = syntset.getLeft();
					
					
					for(String i : ext) {
						String fe = FilenameUtils.getExtension(f.toPath().toString());
						if(fe.equals(i)) {
							// System.out.println(fe + " => " + i);
							latestMJFX.area.setSyntaxEditingStyle(syntset.getRight());
							break;
						}
					}
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
			latestMJFX.filePath = f.getPath().toString();
			filenameLabel.setText(new File(latestMJFX.filePath).getName());
			latestMJFX.area.getDocument().addDocumentListener(new TextListener());
		});
	}
	
	public void view_file() {
		Platform.runLater(() -> pbar.setProgress(ProgressBar.INDETERMINATE_PROGRESS));
		
		SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>(){

			@Override
			protected Object doInBackground() throws Exception {
				// TODO Auto-generated method stub
				
				try {
					JFileChooser jfc = new JFileChooser();
					jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
					
					for(int i = 0; i < StaticStorageProperties.filt.length; i++) {
						jfc.addChoosableFileFilter(StaticStorageProperties.filt[i]);
					}
					
					int r = jfc.showOpenDialog(dis);
					
					if(r == JFileChooser.APPROVE_OPTION) {
						File f = jfc.getSelectedFile();
						
						view_file_from_File(f);
					}
					
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				
				Platform.runLater(() -> pbar.setProgress(0));
				
				
				return null;
			}
			
		};
		
		worker.execute();
			
	}
	
	public void view_file_from_File(File f) {
		TxViewer viewer = new TxViewer();
		
		Platform.runLater(() -> {
			try {
				viewer.setText(FSUtils.getTextFromFile(f));
				viewer.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
				
				for(Map.Entry<String, ImmutablePair<String[], String>> entry : StaticStorageProperties.syntaxset.entrySet()) {
					ImmutablePair<String[], String> syntset = entry.getValue();
					String[] ext = syntset.getLeft();
					
					
					for(String i : ext) {
						String fe = FilenameUtils.getExtension(f.toPath().toString());
						if(fe.equals(i)) {
							// System.out.println(fe + " => " + i);
							viewer.setSyntaxEditingStyle(syntset.getRight());
							break;
						}
					}
				}
				
				tabbedEditor.add(viewer, f.toPath().getFileName().toString());
				refreshList();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		});
	}
	
	public void saveAs_file(Runnable run) {
		Platform.runLater(() -> pbar.setProgress(ProgressBar.INDETERMINATE_PROGRESS));
		SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				// TODO Auto-generated method stub
				int selindex = tabbedEditor.getSelectedIndex();
				Component comp = tabbedEditor.getComponentAt(selindex);
				
				if(comp instanceof TxEditor) {
					TxEditor jfxcomp = (TxEditor)comp;
					
					JFileChooser jfc = new JFileChooser();
					jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
					
					for(int i = 0; i < StaticStorageProperties.filt.length; i++) {
						jfc.addChoosableFileFilter(StaticStorageProperties.filt[i]);
					}
					
					int r = jfc.showSaveDialog(dis);
					
					if(r == JFileChooser.APPROVE_OPTION) {
						
						try {
							File f = jfc.getSelectedFile();
							FSUtils.saveTextToFile(jfxcomp.getText(), f);
							jfxcomp.filePath = f.getPath().toString();
							
							jfxcomp.docName = "";
							
							tabbedEditor.setTitleAt(selindex, f.toPath().getFileName().toString());
							String title = tabbedEditor.getTitleAt(selindex).trim();
							if(title.contains("/"))
								title.replace("/", "");
								tabbedEditor.setTitleAt(selindex, title);
								
							filenameLabel.setText(new File(latestMJFX.filePath).getName());
						}
						catch(IOException ioe) {
							ioe.printStackTrace();
						}
					}
					
					if(run != null)
						run.run();
					
					unMarkChanged();
					refreshList();
				}
				else {
					JOptionPane.showMessageDialog(dis, "Hey, this is not a text editor!", "No", JOptionPane.INFORMATION_MESSAGE);
				}
				
				Platform.runLater(() -> pbar.setProgress(0));
				
				return null;
			}
			
		};
		
		worker.execute();
	}
	
	public void save_file(Runnable run) {
		Platform.runLater(() -> pbar.setProgress(ProgressBar.INDETERMINATE_PROGRESS));
		SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				// TODO Auto-generated method stub
				int selindex = tabbedEditor.getSelectedIndex();
				Component comp = tabbedEditor.getComponentAt(selindex);
				
				if(comp instanceof TxEditor) {
					TxEditor jfxcomp = (TxEditor)comp;
					
					if((!jfxcomp.filePath.isBlank() && !jfxcomp.filePath.isEmpty()) && (jfxcomp.docName.isBlank() && jfxcomp.docName.isEmpty())) {
						try {
							File f = new File(jfxcomp.filePath);
							FSUtils.saveTextToFile(jfxcomp.getText(), f);
							jfxcomp.filePath = f.getPath().toString();
							
							String title = tabbedEditor.getTitleAt(selindex).trim();
							if(title.contains("/")) {
								title.replace("/", "");
								tabbedEditor.setTitleAt(selindex, title);
							}
								
							filenameLabel.setText(new File(latestMJFX.filePath).getName());
						}
						catch(IOException ioe) {
							ioe.printStackTrace();
						}
					}
					else {
						saveAs_file(run);
					}
					
					if(run != null)
						run.run();
					
					unMarkChanged();
					refreshList();
				}
				else {
					JOptionPane.showMessageDialog(dis, "Hey, this is not a text editor!", "No", JOptionPane.INFORMATION_MESSAGE);
				}
				
				Platform.runLater(() -> pbar.setProgress(0));
				
				return null;
			}
			
		};
		
		worker.execute();
	}
	
	public void runUserBshMacro() {
		AskInputDialog bshd = new AskInputDialog(dis, "Beanshell Macros", StaticStorageProperties.bshMacros);
		bshd.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		
		String input = bshd.getInput();
		
		if(input == null)
			return;
		
		int r = JOptionPane.YES_OPTION;
		
		if(StaticStorageProperties.remimdMeAboutMacroSafety)
			r = JOptionPane.showConfirmDialog(dis, "Are you sure about running that? It may be dangerous. \nIt can wipe your drive, \nsend your files somewhere or \nsend your computer's soul to someone's dining hall. \nWho knows, your computer may have you as it's dinner if you run the macro. \nThe point is not to trust any macros from the internet unless you checked it yourself \n(the can delete your drive and ruin your life).", "Watch Out!", JOptionPane.YES_NO_OPTION);
		
		if(r == JOptionPane.YES_OPTION)
			runBshMacro(input);
	}
	
	public void runBshMacro(String macro) {
		bsh.Interpreter i = new bsh.Interpreter();
		StaticStorageProperties.logger.info("Run a beanshell macro.");
		
		if(bshmacroworker != null) {
			bshmacroworker.cancel(true);
		}
		
		bshmacroworker = null;
		Platform.runLater(() -> pbar.setProgress(0));
		
		if(tabbedEditor.getSelectedComponent() instanceof TxEditor) {
			TxEditor mjfx = ((TxEditor) tabbedEditor.getSelectedComponent());
			String oldtext = mjfx.getText();
			
			Platform.runLater(() -> pbar.setProgress(ProgressBar.INDETERMINATE_PROGRESS));
			StaticStorageProperties.logger.info("Running bsh macros");
			
			bshmacroworker = new SwingWorker<Void, Void>() {
				
				@Override
				protected Void doInBackground() throws Exception {
					// TODO Auto-generated method stub
					
					try {
						i.set("bshi", i);
						i.set("Macro", macro);
						i.set("Out", System.out);
						i.set("Err", System.err);
						
						i.set("Editor", mjfx);
						i.set("CaretPosition", mjfx.area.getCaretPosition());
						i.set("CurrentText", mjfx.getText());
						i.eval(macro);
						
						if(!mjfx.getText().equals(oldtext))
							markChanged();
					} catch (EvalError e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					Platform.runLater(() -> pbar.setProgress(0));
					
					StaticStorageProperties.logger.info("Done running bsh macros");
					return null;
				}
				
			};
			
			bshmacroworker.execute();
		}
	}
	
	public void runUserRhinoJSMacro() {
		AskInputDialog rjsd = new AskInputDialog(dis, "Rhino JS Macros", StaticStorageProperties.rhinoJSMacros);
		rjsd.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		
		String input = rjsd.getInput();
		
		if(input == null)
			return;
		
		int r = JOptionPane.YES_OPTION;
		
		if(StaticStorageProperties.remimdMeAboutMacroSafety)
			r = JOptionPane.showConfirmDialog(dis, "Are you sure about running that? It may be dangerous. \nIt can wipe your drive, \nsend your files somewhere or \nsend your computer's soul to someone's dining hall. \nWho knows, your computer may have you as it's dinner if you run the macro. \nThe point is not to trust any macros from the internet unless you checked it yourself \n(the can delete your drive and ruin your life).", "Watch Out!", JOptionPane.YES_NO_OPTION);
		
		if(r == JOptionPane.YES_OPTION)
			runRhinoJSMacro(input);
	}
	
	public void runRhinoJSMacro(String code) {
		Context ctx = new ContextFactory().enterContext();
		StaticStorageProperties.logger.info("Run a rhino js macros");
		
		if(bshmacroworker != null) {
			bshmacroworker.cancel(true);
		}
		
		bshmacroworker = null;
		Platform.runLater(() -> pbar.setProgress(0));
		
		if(tabbedEditor.getSelectedComponent() instanceof TxEditor) {
			TxEditor mjfx = ((TxEditor) tabbedEditor.getSelectedComponent());
			String oldtext = mjfx.getText();
			
			Platform.runLater(() -> pbar.setProgress(ProgressBar.INDETERMINATE_PROGRESS));
			StaticStorageProperties.logger.info("Running rhino js macros");
			
			Scriptable scope = ctx.initStandardObjects();
			
			ScriptableObject.putProperty(scope, "Rhino", Context.javaToJS(ctx, scope));
			ScriptableObject.putProperty(scope, "Out", Context.javaToJS(System.out, scope));
			ScriptableObject.putProperty(scope, "Err", Context.javaToJS(System.err, scope));
			
			ScriptableObject.putProperty(scope, "CurrentText", Context.javaToJS(oldtext, scope));
			ScriptableObject.putProperty(scope, "CaretPosition", Context.javaToJS(mjfx.area.getCaretPosition(), scope));
			ScriptableObject.putProperty(scope, "Editor", Context.javaToJS(mjfx, scope));
			
			ScriptableObject.putProperty(scope, "Macro", Context.javaToJS(code, scope)); 
			
			ctx.evaluateString(scope, code, "script.jsn", 5, null);
			StaticStorageProperties.logger.info("Done running rhino js macros");
			
			Platform.runLater(() -> pbar.setProgress(0));
			
		}
		
		Context.exit();
	}
	
	
	
	
	
	
	
    class TabChanged implements ChangeListener
    {
        @Override
        public void stateChanged(ChangeEvent evt)
        {
            if(tabbedEditor.getTabCount()>0)
            {
            	int selindex = tabbedEditor.getSelectedIndex();
            	
            	Component comp = tabbedEditor.getSelectedComponent();
            	if(comp instanceof TxEditor) {
            		TxEditor jfxcomp = (TxEditor) comp;
            		if(!jfxcomp.filePath.isBlank() && !jfxcomp.filePath.isEmpty())
            			filenameLabel.setText(new File(jfxcomp.filePath).getName());
            		else
            			filenameLabel.setText(jfxcomp.docName);
            	}
            	else if(comp instanceof WebkitWebBrowser){
            		filenameLabel.setText("A web browser lol/");
            	}
            	else if(comp instanceof JFXPanel) {
            		filenameLabel.setText("A Terminal lol/");
            	}
            	else if(comp instanceof TxViewer) {
            		filenameLabel.setText("A viewer viewing " + tabbedEditor.getTitleAt(selindex));
            	}
            	else {
            		filenameLabel.setText("IDK/");
            	}
            	
            	filesHoldList.setSelectedIndex(selindex);
            }
            else {
            	Random rand = new Random();
            	int choose = rand.nextInt(TexyHolder.nololmsgs.length);
            	filenameLabel.setText(TexyHolder.nololmsgs[choose] + "/");
            }
            
            refreshList();
        }
    }
    
    void insertUser() {
    	Component comp = tabbedEditor.getSelectedComponent();
    	
    	if(comp instanceof TxEditor) {
    		TxEditor mjfx = (TxEditor) comp;
    		
    		mjfx.area.insert(System.getProperty("user.name"), mjfx.area.getCaretPosition());
    		markChanged();
    	}
    }
    
    void insertTime() {
    	Component comp = tabbedEditor.getSelectedComponent();
    	
    	if(comp instanceof TxEditor) {
    		TxEditor mjfx = (TxEditor) comp;
    		
    		mjfx.area.insert(new Date().toString(), mjfx.area.getCaretPosition());
    		markChanged();
    	}
    }
    
    void insertLorem() {
    	Component comp = tabbedEditor.getSelectedComponent();
    	
    	if(comp instanceof TxEditor) {
    		TxEditor mjfx = (TxEditor) comp;
    		
    		mjfx.area.insert(TexyHolder.lorem, mjfx.area.getCaretPosition());
    		markChanged();
    	}
    }
    
    void borkText() {
    	Component comp = tabbedEditor.getSelectedComponent();
    	
    	if(comp instanceof TxEditor) {
    		TxEditor mjfx = (TxEditor) comp;
    		
    		mjfx.area.insert("bork bork bork", mjfx.area.getCaretPosition());
    		markChanged();
    	}
    }

    void cutText(){
    	if(tabbedEditor.getSelectedIndex() != -1) {
			Component comp = tabbedEditor.getSelectedComponent();
	
			if(comp instanceof TxEditor) {
				TxEditor mjfx = (TxEditor) comp;
		
				mjfx.area.cut();
				markChanged();
			}
		}
    }
    
    void copyText() {
		if(tabbedEditor.getSelectedIndex() != -1) {
			Component comp = tabbedEditor.getSelectedComponent();
			
			if(comp instanceof TxEditor) {
				TxEditor mjfx = (TxEditor) comp;
				
				mjfx.area.copy();
			}
		}
    }
    
    void pasteText() {
		if(tabbedEditor.getSelectedIndex() != -1) {
			Component comp = tabbedEditor.getSelectedComponent();
			
			if(comp instanceof TxEditor) {
				TxEditor mjfx = (TxEditor) comp;
				
				mjfx.area.paste();
				markChanged();
			}
		}
    }
    
    void selectAllText() {
		if(tabbedEditor.getSelectedIndex() != -1) {
			Component comp = tabbedEditor.getSelectedComponent();
			
			if(comp instanceof TxEditor) {
				TxEditor mjfx = (TxEditor) comp;
				
				mjfx.area.selectAll();
				markChanged();
			}
		}
    }
    
    void deleteAllText() {
		if(tabbedEditor.getSelectedIndex() != -1) {
			Component comp = tabbedEditor.getSelectedComponent();
			
			if(comp instanceof TxEditor) {
				TxEditor mjfx = (TxEditor) comp;
				
				mjfx.area.replaceSelection("");
				markChanged();
			}
		}
    }
    
    void markChanged() {
    	if(tabbedEditor.getSelectedComponent() instanceof TxEditor)
    		if(!tabbedEditor.getTitleAt(tabbedEditor.getSelectedIndex()).contains("/"))
    			tabbedEditor.setTitleAt(tabbedEditor.getSelectedIndex(), tabbedEditor.getTitleAt(tabbedEditor.getSelectedIndex()) + "/");
    }
    
    void unMarkChanged() {
    	if(tabbedEditor.getSelectedComponent() instanceof TxEditor) {
    		if(tabbedEditor.getTitleAt(tabbedEditor.getSelectedIndex()).trim().contains("/")) {
    			String title = tabbedEditor.getTitleAt(tabbedEditor.getSelectedIndex()).trim();
    			title = title.replace("/", "");
    			tabbedEditor.setTitleAt(tabbedEditor.getSelectedIndex(), title);
    		}
    	}
    }
    
    void getInfoDialog(File f) throws IOException {
    	FileInfoDialog d;
    	
		d = new FileInfoDialog(dis, f);
		d.setTitle("Info");
		d.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		d.setLocationRelativeTo(null);
		d.setVisible(true);
    }
    
    void showAbout() {
    	About abtwin = new About(dis);
    	abtwin.setLocationRelativeTo(null);
    	abtwin.setVisible(true);
    }
}
