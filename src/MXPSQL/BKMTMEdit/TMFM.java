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
// import javax.script.*;
import javafx.scene.*;
import java.awt.dnd.*;
import java.awt.Dialog;
import java.nio.file.*;
import java.awt.event.*;
import org.jruby.embed.*;
import javax.swing.tree.*;
import java.util.Optional;
import javax.swing.event.*;
import javax.swing.border.*;
import javafx.embed.swing.*;
import com.formdev.flatlaf.*;
import javafx.scene.control.*;
import java.awt.datatransfer.*;
import groovy.lang.GroovyShell;
import org.mozilla.javascript.*;
import javafx.application.Platform;
import MXPSQL.BKMTMEdit.reusable.*;
import MXPSQL.BKMTMEdit.reusable.utils.*;
import groovy.lang.GroovyRuntimeException;
import MXPSQL.BKMTMEdit.reusable.widgets.*;
import org.apache.commons.io.FilenameUtils;
import java.util.concurrent.CountDownLatch;
import javax.swing.UIManager.LookAndFeelInfo;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.pushingpixels.radiance.theming.api.skin.*;
// import MXPSQL.BKMTMEdit.pluginapi.BKMTMEditTabPlugin;
import org.apache.commons.configuration2.ex.ConversionException;
import MXPSQL.BKMTMEdit.reusable.widgets.tabs.JClosableTabbedPane;
import org.pushingpixels.radiance.theming.api.RadianceThemingCortex;


public class TMFM extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public JTabbedPane rb = new JTabbedPane();
	public final int rb_main_i = 0;
	public final int rb_edit_i = 1;
	
	public Runnable run;
	
	
	public JSplitPane jsplit;
	public JClosableTabbedPane tabbedEditor = new JClosableTabbedPane();
	protected JTabbedPane documentPane;
	
	public JLabel filenameLabel;
	public JLabel caretLabel;
	
	public ProgressBar pbar;
	
	
	JPanel statusPanel;
	
	
    protected JMenu jarpluginm;
    protected JMenu bshpluginm;
    protected JMenu macromediam;
    protected JMenu editm;
    
    protected JCheckBoxMenuItem rbvisiblemi;
    protected JCheckBoxMenuItem docpanevisiblemi;
    protected JCheckBoxMenuItem statusPanelvisiblemi;
    
    protected JRadioButtonMenuItem metalthememi;
	protected JRadioButtonMenuItem platformthememi;
	protected JRadioButtonMenuItem nimbusthememi;
	protected JRadioButtonMenuItem flatlightthememi;
	protected JRadioButtonMenuItem flatdarkthememi;
	protected JRadioButtonMenuItem flatintellijthememi;
	protected JRadioButtonMenuItem flatDCMthememi;
	protected JRadioButtonMenuItem businessblue;
	protected JRadioButtonMenuItem sahara;
	
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
    SwingWorker<Void, Void> jsmacroworker;
    SwingWorker<Void, Void> groovyshellmacroworker;
    SwingWorker<Void, Void> rbmacroworker;
    
    protected void dispatchExitEvent() {
    	dispatchEvent(new WindowEvent(dis, WindowEvent.WINDOW_CLOSING));
    }
	
	
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
							closeCurrentTab(true);
						} catch (InterruptedException | IOException e1) {
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
									closeCurrentTab(true);
								} catch (InterruptedException | IOException e1) {
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
								dispatchExitEvent();
							}
						
						});
					
						filem.add(exitmi);
					}
					
					mb.add(filem);
				}

                {
                    editm = new JMenu("Edit");
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
                    	JMenu historym = new JMenu("History");
                    	
                    	JMenuItem undomi = new JMenuItem("Undo");
                    	JMenuItem redomi = new JMenuItem("Redo");
                    	
                    	undomi.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/undo.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
                    	redomi.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/redo.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
                    	
    					undomi.addActionListener((e) -> {
    						if(tabbedEditor.getSelectedIndex() != -1) {
    							Component comp = tabbedEditor.getSelectedComponent();
    							
    							if(comp instanceof TxEditor) {
    								TxEditor mjfx = (TxEditor) comp;
    								
    								mjfx.area.undoLastAction();
    							}
    						}
    					});
    					
    					redomi.addActionListener((e) -> {
    						if(tabbedEditor.getSelectedIndex() != -1) {
    							Component comp = tabbedEditor.getSelectedComponent();
    							
    							if(comp instanceof TxEditor) {
    								TxEditor mjfx = (TxEditor) comp;
    								
    								
    								
    								mjfx.area.redoLastAction();
    							}
    						}
    					});
    					
    					historym.add(undomi);
    					historym.add(redomi);
                    	
                    	editm.add(historym);
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
                }
                
                {
                	JMenu viewm = new JMenu("View");
                	
                	{
                		JMenu componentviewm = new JMenu("Components");
                		
                    	rbvisiblemi = new JCheckBoxMenuItem("Show Ribbon");
                    	docpanevisiblemi = new JCheckBoxMenuItem("Show Document Pane");
                    	statusPanelvisiblemi = new JCheckBoxMenuItem("Show Status Panel");
                    	
                    	rbvisiblemi.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/eyes.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
                    	docpanevisiblemi.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/eyes.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
                    	statusPanelvisiblemi.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/eyes.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
                    	
                    	rbvisiblemi.setSelected(true);
                    	docpanevisiblemi.setSelected(true);
                    	statusPanelvisiblemi.setSelected(true);
                    	
                    	rbvisiblemi.addActionListener((e) -> {
                    		if(rbvisiblemi.getModel().isSelected()) rb.setVisible(true);
                    		else rb.setVisible(false);
                    	});
                    	
                    	statusPanelvisiblemi.addActionListener((e) -> {
                    		if(statusPanelvisiblemi.getModel().isSelected()) statusPanel.setVisible(true);
                    		else statusPanel.setVisible(false);
                    	});
                    	
                    	docpanevisiblemi.addActionListener((e) -> {
                    		if(docpanevisiblemi.getModel().isSelected()) jsplit.setLeftComponent(documentPane);
                    		else jsplit.remove(documentPane);
                    	});
                    	
                    	docpanevisiblemi.setEnabled(false);
                    	docpanevisiblemi.setToolTipText("It's broken alright. The theme will not change and it's buggy ok.");
                    	
                    	componentviewm.add(rbvisiblemi);
                    	componentviewm.add(docpanevisiblemi);
                    	componentviewm.add(statusPanelvisiblemi);
                    	
                    	viewm.add(componentviewm);
                	}
                	
                	{
                		JMenu appearancem = new JMenu("Appearance");
                		
                		appearancem.setToolTipText("Only few selected themes are available here");
                		
                		ButtonGroup bgr = new ButtonGroup();
                		ButtonGroup pointer = new ButtonGroup();
                		
                		// Taal for Theme-Appearance-Action-Listener
                		ActionListener Taal = new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								// TODO Auto-generated method stub
								try {
									switch(e.getActionCommand()) {								
										case "Metal":
											UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
											SwingUtilities.updateComponentTreeUI(StaticWidget.window);
											break;
										case "System Platform":
											UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
											SwingUtilities.updateComponentTreeUI(StaticWidget.window);
											break;
										case "Nimbus":
										    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
										        if ("Nimbus".equals(info.getName())) {
										        	UIManager.setLookAndFeel(info.getClassName());
										        	SwingUtilities.updateComponentTreeUI(StaticWidget.window);
										            break;
										        }
										    }
											break;
										case "FlatLaf Light":
											StaticStorageProperties.logger.info("FlatSYS");
											UIManager.setLookAndFeel( new FlatLightLaf() );
											SwingUtilities.updateComponentTreeUI(StaticWidget.window);
											break;
										case "FlatLaf Dark":
											UIManager.setLookAndFeel( new FlatDarkLaf() );
											SwingUtilities.updateComponentTreeUI(StaticWidget.window);
											break;
										case "FlatLaf Intellij":
											UIManager.setLookAndFeel( new FlatIntelliJLaf() );
											SwingUtilities.updateComponentTreeUI(StaticWidget.window);
											break;
										case "FlatLaf Darcula":
											UIManager.setLookAndFeel( new FlatDarculaLaf() );
											SwingUtilities.updateComponentTreeUI(StaticWidget.window);
											break;
										case "Business Blue":
											RadianceThemingCortex.GlobalScope.setSkin(new BusinessBlueSteelSkin());
											SwingUtilities.updateComponentTreeUI(StaticWidget.window);
											break;
										case "Sahara":
											RadianceThemingCortex.GlobalScope.setSkin(new SaharaSkin());
											SwingUtilities.updateComponentTreeUI(StaticWidget.window);
											break;
										default:
											StaticStorageProperties.logger.info("No ok 4 theme");
											break;
									}
								} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
										| UnsupportedLookAndFeelException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
									JOptionPane.showMessageDialog(dis, "Failed to set your theme", "Theming failure", JOptionPane.ERROR_MESSAGE);
								}
								
								SwingUtilities.updateComponentTreeUI(StaticWidget.window);
								
								// prevents anything funny from happening if you switch from the radiance theme to the flatlaf themes
								// Funny things that happen: the ui becomes garbage and an exception is thrown, we don't want that funny thing
								switch(e.getActionCommand()) {
									case "Business Blue":
									case "Sahara":
										flatlightthememi.setEnabled(false);
										flatdarkthememi.setEnabled(false);
										flatintellijthememi.setEnabled(false);
										flatDCMthememi.setEnabled(false);
										break;
									default:
										flatlightthememi.setEnabled(true);
										flatdarkthememi.setEnabled(true);
										flatintellijthememi.setEnabled(true);
										flatDCMthememi.setEnabled(true);
										break;
								}
							}
                			
                		};
                		
                		metalthememi = new JRadioButtonMenuItem("Metal");
                		platformthememi = new JRadioButtonMenuItem("System Platform");
                		nimbusthememi = new JRadioButtonMenuItem("Nimbus");
                		flatlightthememi = new JRadioButtonMenuItem("FlatLaf Light");
                		flatdarkthememi = new JRadioButtonMenuItem("FlatLaf Dark");
                		flatintellijthememi= new JRadioButtonMenuItem("FlatLaf Intellij");
                		flatDCMthememi = new JRadioButtonMenuItem("FlatLaf Darcula");
                		businessblue = new JRadioButtonMenuItem("Business Blue");
                		sahara = new JRadioButtonMenuItem("Sahara");
                		
                		metalthememi.addActionListener(Taal);
                		platformthememi.addActionListener(Taal);
                		nimbusthememi.addActionListener(Taal);
                		flatlightthememi.addActionListener(Taal);
                		flatdarkthememi.addActionListener(Taal);
                		flatintellijthememi.addActionListener(Taal);
                		flatDCMthememi.addActionListener(Taal);
                		businessblue.addActionListener(Taal);
                		sahara.addActionListener(Taal);
                		
                		bgr.add(metalthememi);
                		bgr.add(platformthememi);
                		bgr.add(nimbusthememi);
                		bgr.add(flatlightthememi);
                		bgr.add(flatdarkthememi);
                		bgr.add(flatintellijthememi);
                		bgr.add(flatDCMthememi);
                		bgr.add(businessblue);
                		bgr.add(sahara);
                		
                		appearancem.add(metalthememi);
                		appearancem.add(platformthememi);
                		appearancem.add(nimbusthememi);
                		appearancem.add(flatlightthememi);
                		appearancem.add(flatdarkthememi);
                		appearancem.add(flatintellijthememi);
                		appearancem.add(flatDCMthememi);
                		appearancem.add(businessblue);
                		appearancem.add(sahara);
                		
                		// appearancem.setEnabled(false);
                		
                		StaticStorageProperties.theme = StaticStorageProperties.config.getString("theme.type");
                		switch(StaticStorageProperties.theme) {
                			case "metal":
                				metalthememi.setSelected(true);
                				break;
                			case "platform":
                				platformthememi.setSelected(true);
                				break;
            				default:
            					bgr.clearSelection();
            					break;
                		}
                		
                		viewm.add(appearancem);
                	}
                	
                	mb.add(viewm);
                }
                
                {
                	JMenu pluginm = new JMenu("Plugins");
                	pluginm.setEnabled(false);
                	bshpluginm = new JMenu("Beanshell Plugins");
                	bshpluginm.setEnabled(false);
                	
                	for(Map.Entry<String, String> plugin : StaticStorageProperties.bshPlugins.entrySet()) {
                		JMenuItem pluginmi = null;
                		bshpluginm.setEnabled(true);
                		pluginm.setEnabled(true);
                		
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
                	JMenuItem groovymacrosmi = new JMenuItem("Run Groovy macros");
                	JMenuItem rbmi = new JMenuItem("Run Ruby Macros");
                	JMenuItem opendefaulteditormi = new JMenuItem("Open in default editor");
                	JMenuItem openindefaultmi = new JMenuItem("Open file in default program");
                	macromediam = new JMenu("Macros");
                	
                	
                	runtermmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK));
                	bshmacrosmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
                	rjsmacrosmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK+ActionEvent.SHIFT_MASK));
                	groovymacrosmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK+InputEvent.ALT_DOWN_MASK));
                	rbmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK+InputEvent.ALT_DOWN_MASK+ActionEvent.SHIFT_MASK));
                	runtermmi.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/Term.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
                	bshmacrosmi.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/macro.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
                	rjsmacrosmi.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/macro.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
                	groovymacrosmi.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/macro.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
                	rbmi.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/macro.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
                	
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
                	
                	groovymacrosmi.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							// TODO Auto-generated method stub
							runUserGroovyMacro();
						}
					});
                	
                	rbmi.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							// TODO Auto-generated method stub
							runUserRbMacro();
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
                	macromediam.add(bshmacrosmi);
                	macromediam.add(rjsmacrosmi);
                	macromediam.add(groovymacrosmi);
                	macromediam.add(rbmi);
                	runm.add(macromediam);
                	runm.addSeparator();
                	runm.add(opendefaulteditormi);
                	runm.add(openindefaultmi);
                	
                	mb.add(runm);
                }
                
                {
                	JMenu helpm = new JMenu("Help");
                	
                	JMenuItem aboutmi = new JMenuItem("About");
                	JMenuItem licensemi = new JMenuItem("License");
                	
                	aboutmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK+InputEvent.ALT_DOWN_MASK+ActionEvent.SHIFT_MASK));
                	licensemi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK+InputEvent.ALT_DOWN_MASK+ActionEvent.SHIFT_MASK));
                	
                	aboutmi.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/About.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
                	licensemi.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/License.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
                	
                	aboutmi.addActionListener((e) -> {
                		showAbout();
                	});
                	
                	licensemi.addActionListener((e) -> {
                		try {
							JOptionPane.showMessageDialog(dis, FSUtils.getTextFromFile(ResourceGet.getFile(this.getClass(), "LICENSE")), "LICENSE", JOptionPane.INFORMATION_MESSAGE);
						} catch (HeadlessException | FileNotFoundException e1) {
							JOptionPane.showMessageDialog(this, e1, "Error opening LICENSE", JOptionPane.ERROR_MESSAGE);
						}
                	});
                	
                	helpm.add(aboutmi);
                	helpm.addSeparator();
                	helpm.add(licensemi);
                	
                	mb.add(helpm);
                }
				
                new DropTarget(mb, new FileDND());
				setJMenuBar(mb);
			}
		}
		
		{	
			GridBagLayout gbl = new GridBagLayout();
			GridBagConstraints gbc = new GridBagConstraints();
	        gbc.gridx = 0;
	        gbc.gridy = 0;
	        gbc.weightx = gbc.weighty = 1.0;
	        gbc.fill = GridBagConstraints.BOTH;
			
			statusPanel = new JPanel(new BorderLayout());
			
			JFXPanel javafxPanel = new JFXPanel();
			pbar = new ProgressBar(0);
			Scene scene = new Scene(pbar);
			javafxPanel.setScene(scene);
			javafxPanel.setToolTipText("U totally not idot.");			
			statusPanel.setLayout(gbl);
			statusPanel.add(javafxPanel, gbc);
			new DropTarget(javafxPanel, new FileDND());
			
			JToolBar statusBar = new JToolBar();
			statusBar.setBorder((Border) new BevelBorder(BevelBorder.LOWERED));
			statusBar.setFloatable(false);
			
	        JLabel readylabel=new JLabel(StaticStorageProperties.baseTitle);
	        readylabel.setFont(new Font("Calibri",Font.PLAIN,15));
	        new DropTarget(readylabel, new FileDND());
	        filenameLabel = new JLabel("");
	        filenameLabel.setFont(new Font("Calibri",Font.PLAIN,15));
	        caretLabel = new JLabel("");
	        caretLabel.setFont(new Font("Calibri",Font.PLAIN,15));
	        new DropTarget(filenameLabel, new FileDND());
	        statusBar.add(readylabel);
	        // statusBar.add(new JLabel("                          "));
	        statusBar.addSeparator();
	        statusBar.add(filenameLabel);
	        statusBar.addSeparator();
	        statusBar.add(caretLabel);
	        new DropTarget(statusBar, new FileDND());
	        
	        gbc.gridy = 1;
			statusPanel.add(statusBar, gbc);
			new DropTarget(statusPanel, new FileDND());
			
			contentPane.add(statusPanel, BorderLayout.SOUTH);
		}
		
		
		{
			documentPane = new JTabbedPane();
			new DropTarget(documentPane, new FileDND());
			{
				JPanel fhlpanel = new JPanel(new BorderLayout());
				JButton refresh = new JButton("Refresh");
				fhlpanel.add(refresh, BorderLayout.NORTH);
				refresh.addActionListener((e) -> {
					refreshList();
				});
				
				filesHoldList.addListSelectionListener(new List2TabSelectionListener());
				fhlpanel.add(new JScrollPane(filesHoldList));
				documentPane.add(fhlpanel, "Document List");
			}
			
			jsplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, documentPane, tabbedEditor);
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
		        			    		File f2 = Paths.get(StaticStorageProperties.cwdPath.toFile().toURI()).resolve(f.getParent().toString()).resolve(filename).toFile();
		        			    		
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
				
				documentPane.add(ftreepanel, "Filesystem Manager");
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
			
			if(StaticStorageProperties.startingFiles != null) {
				if(StaticStorageProperties.startingFiles.isEmpty()) {
					addDefaultEditor();
				}
				else {
					try {
						for(int i = 0; i < StaticStorageProperties.startingFiles.size(); i++) {
							open_file_from_File(StaticStorageProperties.startingFiles.get(i));
						}
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			else {
				addDefaultEditor();
			}
			
			StaticStorageProperties.theme = StaticStorageProperties.config.getString("theme.type");
			if(StaticStorageProperties.theme.equals("platform"))
				tabbedEditor.offsetDim = new int[] {5, 0};
				tabbedEditor.tocl = (intd) -> {
					try {
						closeCurrentTab(false);
						return true;
					}
					catch(IOException ioe) {
						return false;
					}
					catch(InterruptedException ite) {
						return false;
					}
			};
			
			{
				java.awt.MenuItem aboutmi = new java.awt.MenuItem("About");
				java.awt.MenuItem openmi = new java.awt.MenuItem("Open");
				java.awt.MenuItem exitmi = new java.awt.MenuItem("Exit");
				
				aboutmi.addActionListener((e) -> {
					showAbout();
				});
				
				openmi.addActionListener((e) -> {
					open_file();
				});
				
				exitmi.addActionListener((e) -> dispatchExitEvent());
				
				StaticWidget.traypop.add(openmi);
				StaticWidget.traypop.addSeparator();
				StaticWidget.traypop.add(aboutmi);
				StaticWidget.traypop.addSeparator();
				StaticWidget.traypop.add(exitmi);
			}
		}
		
    	{
    		try {
    			if(!StaticStorageProperties.config.getBoolean("visibility.ribbon", true)) rbvisiblemi.doClick();
    		}
    		catch(ConversionException ce) {
    			StaticStorageProperties.logger.info("Invalid value for visibility.ribbon");
    			// ce.printStackTrace();
    		}
    		
    		/* try {
    			if(!StaticStorageProperties.config.getBoolean("visibility.documentpane", true)) docpanevisiblemi.doClick();
    		}
    		catch(ConversionException ce) {	
    			StaticStorageProperties.logger.info("Invalid value for visibility.documentpane");
    			ce.printStackTrace();
    		} */
    		
    		try {
    			if(!StaticStorageProperties.config.getBoolean("visibility.statusbar", true)) statusPanelvisiblemi.doClick();
    		}
    		catch(ConversionException ce) {
    			StaticStorageProperties.logger.info("Invalid value for visibikity.statusbar");
    			// ce.printStackTrace();
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
		
		// runRbMacro("nonexistent.to_non_existent(nonext2)");
		
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
	
	class TextCaretListener implements CaretListener{

		@Override
		public void caretUpdate(CaretEvent e) {
			// TODO Auto-generated method stub
			
			if(!(tabbedEditor.getSelectedComponent() instanceof TxEditor))
				return;
			
			TxEditor mjfx = (TxEditor) tabbedEditor.getSelectedComponent();
			
            int linenum = -1;
            int columnnum = -1;
            
            try {
                // First we find the position of the caret. This is the number of where the caret is in relation to the start of the JTextArea
                // in the upper left corner. We use this position to find offset values (eg what line we are on for the given position as well as
                // what position that line starts on.
                int caretpos = mjfx.area.getCaretPosition();
                linenum = mjfx.area.getLineOfOffset(caretpos);

                // We subtract the offset of where our line starts from the overall caret position.
                // So lets say that we are on line 5 and that line starts at caret position 100, if our caret position is currently 106
                // we know that we must be on column 6 of line 5.
                columnnum = caretpos - mjfx.area.getLineStartOffset(linenum);

                // We have to add one here because line numbers start at 0 for getLineOfOffset and we want it to start at 1 for display.
                linenum += 1;
                
                columnnum += 1;
            }
            catch(Exception ex) { }
            
            caretLabel.setText("Column: " + columnnum + " Line row: " + linenum);
		}
		
	};
	
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
            		macromediam.setEnabled(true);
            		editm.setEnabled(true);
            		
            		try {
            			rb.setEnabledAt(rb_edit_i, true);
            			rb.setSelectedIndex(rb_main_i);
            		}
            		catch(IndexOutOfBoundsException ioobe) {;}
            	}
            	else {
            		macromediam.setEnabled(false);
            		editm.setEnabled(false);
            		
            		try {
            			rb.setEnabledAt(rb_edit_i, false);
            			rb.setSelectedIndex(rb_main_i);
            		}
            		catch(IndexOutOfBoundsException ioobe) {;}
            	}
            	
            	if(comp instanceof TxEditor) {
            		TxEditor jfxcomp = (TxEditor) comp;
            		if(!jfxcomp.filePath.isBlank() && !jfxcomp.filePath.isEmpty())
            			filenameLabel.setText(new File(jfxcomp.filePath).getName());
            		else
            			filenameLabel.setText(jfxcomp.docName);
            		
            	}
            	else if(comp instanceof SwingWebkitWebBrowser){
            		filenameLabel.setText("A web browser lol/");
            	}
            	else if(comp instanceof TerminalWidget) {
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
        		macromediam.setEnabled(false);
        		editm.setEnabled(false);
        		
        		try {
        			rb.setEnabledAt(rb_edit_i, false);
        			rb.setSelectedIndex(rb_main_i);
        		}
        		catch(IndexOutOfBoundsException ioobe) {;}
            }
            
            refreshList();
        }
    }
	
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
				if(!(dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor) instanceof java.util.List<?>))
					return;
				
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
	
	public void closeCurrentTab(boolean automaticallyDispose) throws InterruptedException, IOException {
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
						throw new IOException("no");
					}
				}
				
				if(automaticallyDispose) tabbedEditor.remove(selindex);
			}
			else {
				if(automaticallyDispose) tabbedEditor.remove(selindex);
			}
		}
		
		refreshList();
	}
	
	boolean closeAllTab() {
		while(tabbedEditor.getTabCount() > 0) {
			try {
				closeCurrentTab(true);
			}
			catch(InterruptedException ie) {
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			SwingWebkitWebBrowser webkit = new SwingWebkitWebBrowser(StaticStorageProperties.home_page);
			new DropTarget(webkit, new FileDND());
			tabbedEditor.addTab(webkit, "webdocument " + webdoccount);
			webdoccount += 1;
			refreshList();
			pbar.setProgress(0);
		});
	}
	
	public void addTerminal() {
		Platform.runLater(() -> pbar.setProgress(ProgressBar.INDETERMINATE_PROGRESS));
		Platform.runLater(() -> {
			// JOptionPane.showMessageDialog(dis, "Just no, it's bad and does not work properly", "NO, it bad", JOptionPane.WARNING_MESSAGE);
			TerminalWidget termw = new TerminalWidget();
			new DropTarget(termw, new FileDND());
			tabbedEditor.addTab(termw, "term " + termsessioncount);
			termsessioncount += 1;
			
			refreshList();
			pbar.setProgress(0);
		});
	}
	
	public void addDefaultEditor() {
		addEditor("Text document " + textdoccount);
		latestMJFX.docName = "Text document " + textdoccount + "/";
		filenameLabel.setText(latestMJFX.docName);
		latestMJFX.area.getDocument().addDocumentListener(new TextListener());
		
		textdoccount++;
	}
	
	public TxEditor addEditor(String title) {
		Platform.runLater(() -> pbar.setProgress(ProgressBar.INDETERMINATE_PROGRESS));
		TxEditor mjfx;
		
		ArrayList<String> langs = new ArrayList<String>();
		langs.add("default*");
		langs.add(SyntaxConstants.SYNTAX_STYLE_NONE);
		for(Map.Entry<String, ImmutablePair<String[], String>> entry : StaticStorageProperties.syntaxset.entrySet()) {
			ImmutablePair<String[], String> syntset = entry.getValue();
			String syntax = syntset.getValue();
			
			langs.add(syntax);
		}
		
		mjfx = new TxEditor(dis, langs);
		mjfx.defaultLang = langs.get(1);
		mjfx.area.addCaretListener(new TextCaretListener());
		latestMJFX = mjfx;
		tabbedEditor.addTab(mjfx, title);
		new DropTarget(mjfx, new FileDND());
		new DropTarget(mjfx.area, new FileDND());
		Platform.runLater(() -> pbar.setProgress(0));
		int index=tabbedEditor.getTabCount()-1;
		tabbedEditor.setSelectedIndex(index);
		
		// filenameLabel.setText(title);
		
		refreshList();
		
		return mjfx;
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
				
				filenameLabel.setText("Web Browser");
				
				
				addEditor(cli.URL);
				
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
		addEditor(f.toPath().getFileName().toString());
		
		Platform.runLater(() -> {
			
			try {
				latestMJFX.setText(FSUtils.getTextFromFile(f));
				latestMJFX.area.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
				
				for(Map.Entry<String, ImmutablePair<String[], String>> entry : StaticStorageProperties.syntaxset.entrySet()) {
					ImmutablePair<String[], String> syntset= entry.getValue();
					String[] ext = syntset.getLeft();
					
					if(ext == null)
						continue;
					
					
					for(String i : ext) {
						String fe = FilenameUtils.getExtension(f.toPath().toString());
						
						if(fe.equals(i)) {
							// System.out.println(fe + " => " + i);
							latestMJFX.area.setSyntaxEditingStyle(syntset.getRight());
							latestMJFX.defaultLang = syntset.getRight();
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
					
					if(ext == null)
						continue;
					
					for(String i : ext) {
						String fe = FilenameUtils.getExtension(f.toPath().toString());
						if(fe.equals(i)) {
							// System.out.println(fe + " => " + i);
							viewer.setSyntaxEditingStyle(syntset.getRight());
							break;
						}
					}
				}
				
				tabbedEditor.addTab(viewer, f.toPath().getFileName().toString());
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
							String extension = jfc.getFileFilter().getDescription();
							File f = jfc.getSelectedFile();
							if(extension.equals("Text files *.txt")) {
								String f2 = f.toString();
								f2 = f2 + ".txt";
								
								f = new File(f2);
							}
							else if(extension.equals("CSV files *.csv")) {
								String f2 = f.toString();
								f2 = f2 + ".csv";
								
								f = new File(f2);
							}
							else if(extension.equals("HTML Document *.html,*.htm")) {
								// always .html
								
								String f2 = f.toString();
								f2 = f2 + ".html";
								
								f = new File(f2);
							}
							
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
					
					i.set("bshi", i);
					i.set("Macro", macro);
					i.set("Out", System.out);
					i.set("Err", System.err);
					
					i.set("Editor", mjfx);
					i.set("CaretPosition", mjfx.area.getCaretPosition());
					i.set("CurrentText", mjfx.getText());
					
					try{
						i.eval(macro);
					}
					catch(OutOfMemoryError oome) {
						StaticStorageProperties.logger.error("Wow, your beanshell script ran out of memory you can of tuna.");
						oome.printStackTrace();
						if(StaticWidget.trayicon != null) StaticWidget.trayicon.displayMessage("Your beanshell script ran out of memory you can of tuna", StaticStorageProperties.baseTitle, TrayIcon.MessageType.ERROR);
						JOptionPane.showMessageDialog(dis, "Your beanshell/bsh script ran out of memory.", "No, you tuna can!", JOptionPane.ERROR_MESSAGE);
					}
					catch(EvalError ee) {
						StaticStorageProperties.logger.error("Bsh has errored out");
						ee.printStackTrace();
						// JOptionPane.showMessageDialog(dis, "Your script is a problematic here.", "No, your script has errored out!", JOptionPane.ERROR_MESSAGE);
						if(StaticWidget.trayicon != null) StaticWidget.trayicon.displayMessage("Calm down on your keymashed Beanshell macros, it has erorrs.", StaticStorageProperties.baseTitle, TrayIcon.MessageType.ERROR);
						JOptionPane.showMessageDialog(dis, "Your beanshell script is a problematic here.", "No, your script has errored out!", JOptionPane.ERROR_MESSAGE);
					}
					catch(Throwable t) {
						StaticStorageProperties.logger.error("Runtime error on beanshell");
						t.printStackTrace();
						if(StaticWidget.trayicon != null) StaticWidget.trayicon.displayMessage("Your BSH script has experienced unhandled runtime error.", StaticStorageProperties.baseTitle, TrayIcon.MessageType.ERROR);
						JOptionPane.showMessageDialog(dis, "Your beanshell script has experienced a runtime error.", "No, your script has unhandled runtime errors!", JOptionPane.ERROR_MESSAGE);
					}
					
					if(!mjfx.getText().equals(oldtext))
						markChanged();
					
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
		StaticStorageProperties.logger.info("Run a rhino js macros");
		
		if(jsmacroworker != null) {
			jsmacroworker.cancel(true);
		}
		
		jsmacroworker = null;
		
		Platform.runLater(() -> pbar.setProgress(0));
		
		jsmacroworker = new SwingWorker<Void, Void>(){

			@Override
			protected Void doInBackground() throws Exception {
				// TODO Auto-generated method stub
				Context ctx = new ContextFactory().enterContext(); 
				if(tabbedEditor.getSelectedComponent() instanceof TxEditor) {
					TxEditor mjfx = ((TxEditor) tabbedEditor.getSelectedComponent());
					String oldtext = mjfx.getText();
					
					Platform.runLater(() -> pbar.setProgress(ProgressBar.INDETERMINATE_PROGRESS));
					StaticStorageProperties.logger.info("Running rhino js macros");
					
					Scriptable scope = ctx.initStandardObjects();
					/*
var a = CurrentText;
for(var i = 0; i < 1501; i++){
	a += CurrentText;
}

Editor.setText(a);
					 */
					
					ScriptableObject.putProperty(scope, "Rhino", Context.javaToJS(ctx, scope));
					ScriptableObject.putProperty(scope, "Out", Context.javaToJS(System.out, scope));
					ScriptableObject.putProperty(scope, "Err", Context.javaToJS(System.err, scope));
					
					ScriptableObject.putProperty(scope, "CurrentText", Context.javaToJS(oldtext, scope));
					ScriptableObject.putProperty(scope, "CaretPosition", Context.javaToJS(mjfx.area.getCaretPosition(), scope));
					ScriptableObject.putProperty(scope, "Editor", Context.javaToJS(mjfx, scope));
					
					ScriptableObject.putProperty(scope, "Macro", Context.javaToJS(code, scope)); 
					
					try{
						ctx.evaluateString(scope, code, "memscript.jsn", 5, null);
					}
					catch(EcmaError ece) {
						StaticStorageProperties.logger.error("Javascript has errored out lol");
						ece.printStackTrace();
						if(StaticWidget.trayicon != null) StaticWidget.trayicon.displayMessage("Your javascript macro has errors in it.", StaticStorageProperties.baseTitle, TrayIcon.MessageType.ERROR);
						JOptionPane.showMessageDialog(dis, "Your rhino javascript script (yes ik) is a problematic here.", "No, your script has errored out!", JOptionPane.ERROR_MESSAGE);
					}
					catch(Throwable t) {
						StaticStorageProperties.logger.error("Runtime error on JavaScript");
						t.printStackTrace();
						if(StaticWidget.trayicon != null) StaticWidget.trayicon.displayMessage("Your JS script has experienced unhandled runtime error.", StaticStorageProperties.baseTitle, TrayIcon.MessageType.ERROR);
						JOptionPane.showMessageDialog(dis, "Your JavaScript script has experienced a runtime error.", "No, your script has unhandled runtime errors!", JOptionPane.ERROR_MESSAGE);
					}
					
					StaticStorageProperties.logger.info("Done running rhino js macros");
					
					Platform.runLater(() -> pbar.setProgress(0));
					
				}
				
				Context.exit();
				
				return null;
			}
			
		};
		
		jsmacroworker.execute();
	}
	
	public void runUserGroovyMacro() {
		AskInputDialog groovyi = new AskInputDialog(dis, "Groovy Macros", StaticStorageProperties.groovyMacros);
		groovyi.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		
		String input = groovyi.getInput();
		
		if(input == null)
			return;
		
		int r = JOptionPane.YES_OPTION;
		
		if(StaticStorageProperties.remimdMeAboutMacroSafety)
			r = JOptionPane.showConfirmDialog(dis, "Are you sure about running that? It may be dangerous. \nIt can wipe your drive, \nsend your files somewhere or \nsend your computer's soul to someone's dining hall. \nWho knows, your computer may have you as it's dinner if you run the macro. \nThe point is not to trust any macros from the internet unless you checked it yourself \n(the can delete your drive and ruin your life).", "Watch Out!", JOptionPane.YES_NO_OPTION);
		
		if(r == JOptionPane.YES_OPTION)
			runGroovyMacro(input);
	}
	
	public void runGroovyMacro(String macro) {
		StaticStorageProperties.logger.info("Starting to run Apache Groovy Macros");
		if(groovyshellmacroworker != null) {
			groovyshellmacroworker.cancel(true);
		}
		
		groovyshellmacroworker = null;
		
		groovyshellmacroworker = new SwingWorker<Void, Void>(){

			@Override
			protected Void doInBackground() throws Exception {
				// TODO Auto-generated method stub
				GroovyShell gsh = new GroovyShell(TMMain.class.getClassLoader());
				
				if(tabbedEditor.getSelectedComponent() instanceof TxEditor) {
					Platform.runLater(() -> pbar.setProgress(0));
					
					Platform.runLater(() -> pbar.setProgress(ProgressBar.INDETERMINATE_PROGRESS));
					StaticStorageProperties.logger.info("Running Apache Groovy Macros");
					
					TxEditor mjfx = ((TxEditor) tabbedEditor.getSelectedComponent());
					String oldtext = mjfx.getText();
					
					gsh.setVariable("Editor", mjfx);
					gsh.setVariable("CaretPosition", mjfx.area.getCaretPosition());
					gsh.setVariable("CurrentText", oldtext);
					
					gsh.setVariable("Out", System.out);
					gsh.setVariable("Err", System.err);
					
					gsh.setVariable("Macro", macro);
					
					gsh.setVariable("GroovySh", gsh);
					
					try{
						gsh.evaluate(macro);
					}
					catch(OutOfMemoryError oome) {
						StaticStorageProperties.logger.error("Wow, your groovy script ran out of memory you can of tuna.");
						oome.printStackTrace();
						if(StaticWidget.trayicon != null) StaticWidget.trayicon.displayMessage("Your groovy ran out of memory you can of tuna", StaticStorageProperties.baseTitle, TrayIcon.MessageType.ERROR);
						JOptionPane.showMessageDialog(dis, "Your groovy script ran out of memory.", "No, you tuna can!", JOptionPane.ERROR_MESSAGE);
					}
					catch(GroovyRuntimeException e) {
						StaticStorageProperties.logger.error("Groovy has errored out");
						e.printStackTrace();
						if(StaticWidget.trayicon != null) StaticWidget.trayicon.displayMessage("Your groovy script has errors in it.", StaticStorageProperties.baseTitle, TrayIcon.MessageType.ERROR);
						JOptionPane.showMessageDialog(dis, "Your groovy script is a problematic here.", "No, your script has errored out!", JOptionPane.ERROR_MESSAGE);
					}
					catch(Throwable t) {
						StaticStorageProperties.logger.error("Runtime error on groovy");
						t.printStackTrace();
						if(StaticWidget.trayicon != null) StaticWidget.trayicon.displayMessage("Your Groovy script has experienced unhandled runtime error.", StaticStorageProperties.baseTitle, TrayIcon.MessageType.ERROR);
						JOptionPane.showMessageDialog(dis, "Your groovy script has experienced a runtime error.", "No, your script has unhandled runtime errors!", JOptionPane.ERROR_MESSAGE);
					}
					
					StaticStorageProperties.logger.info("Finished running Apache Groovy Macros");
					
					Platform.runLater(() -> pbar.setProgress(0));
				}
				return null;
			}
		};
		
		groovyshellmacroworker.execute();;
	}
	
	public void runUserRbMacro() {
		AskInputDialog rbi = new AskInputDialog(dis, "JRuby Macros", StaticStorageProperties.groovyMacros);
		rbi.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		
		String input = rbi.getInput();
		
		if(input == null)
			return;
		
		int r = JOptionPane.YES_OPTION;
		
		if(StaticStorageProperties.remimdMeAboutMacroSafety)
			r = JOptionPane.showConfirmDialog(dis, "Are you sure about running that? It may be dangerous. \nIt can wipe your drive, \nsend your files somewhere or \nsend your computer's soul to someone's dining hall. \nWho knows, your computer may have you as it's dinner if you run the macro. \nThe point is not to trust any macros from the internet unless you checked it yourself \n(the can delete your drive and ruin your life).", "Watch Out!", JOptionPane.YES_NO_OPTION);
		
		if(r == JOptionPane.YES_OPTION)
			runRbMacro(input);
	}
	
	public void runRbMacro(String macro) {
		StaticStorageProperties.logger.info("Starting to run JRuby Macros");
		if(rbmacroworker != null) {
			rbmacroworker.cancel(true);
		}
		
		rbmacroworker=null;
		
		rbmacroworker = new SwingWorker<Void, Void>(){

			@Override
			protected Void doInBackground() throws Exception {
				// TODO Auto-generated method stub
				if(tabbedEditor.getSelectedComponent() instanceof TxEditor) {
					TxEditor tx = (TxEditor) tabbedEditor.getSelectedComponent();
					Platform.runLater(() -> pbar.setProgress(0));
					ScriptingContainer rbse = new ScriptingContainer();
					rbse.put("rbse", rbse);
					
					rbse.put("Editor", tx);
					rbse.put("CaretPosition", tx.area.getCaretPosition());
					rbse.put("CurrentText", tx.getText());
					
					rbse.put("Out", System.out);
					rbse.put("Err", System.err);
					
					rbse.put("Macro", macro);
					
					try {
						rbse.runScriptlet(macro);
					}
					catch(OutOfMemoryError oome) {
						/* StaticStorageProperties.logger.error("Wow, you ran out of memory you can of tuna.");
						oome.printStackTrace();
						if(StaticWidget.trayicon != null) StaticWidget.trayicon.displayMessage("You ran out mf memory you can of tuna", StaticStorageProperties.baseTitle, TrayIcon.MessageType.ERROR);
						JOptionPane.showMessageDialog(dis, "Your groovy script ran out of memory.", "No, you tuna can!", JOptionPane.ERROR_MESSAGE); */
						StaticStorageProperties.logger.error("Wow, your ruby script ran out of memory you can of tuna.");
						oome.printStackTrace();
						if(StaticWidget.trayicon != null) StaticWidget.trayicon.displayMessage("Your ruby script ran out mf memory you can of tuna", StaticStorageProperties.baseTitle, TrayIcon.MessageType.ERROR);
						JOptionPane.showMessageDialog(dis, "Your ruby script ran out of memory.", "No, you tuna can!", JOptionPane.ERROR_MESSAGE);
					}
					catch(EvalFailedException e) {
						StaticStorageProperties.logger.error("Ruby has errored out");
						e.printStackTrace();
						if(StaticWidget.trayicon != null) StaticWidget.trayicon.displayMessage("Your JRuby script has error in it.", StaticStorageProperties.baseTitle, TrayIcon.MessageType.ERROR);
						JOptionPane.showMessageDialog(dis, "Your ruby script is a problematic here.", "No, your script has errored out!", JOptionPane.ERROR_MESSAGE);
					}
					catch(Throwable t) {
						StaticStorageProperties.logger.error("Runtime error on ruby");
						t.printStackTrace();
						if(StaticWidget.trayicon != null) StaticWidget.trayicon.displayMessage("Your JRuby script has experienced unhandled runtime error.", StaticStorageProperties.baseTitle, TrayIcon.MessageType.ERROR);
						JOptionPane.showMessageDialog(dis, "Your ruby script has experienced a runtime error.", "No, your script has unhandled runtime errors!", JOptionPane.ERROR_MESSAGE);
					}
					
					StaticStorageProperties.logger.info("Finished running JRuby Macros");
					
					Platform.runLater(() -> pbar.setProgress(0));
				}
				return null;
			}
			
		};
		
		rbmacroworker.execute();
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
