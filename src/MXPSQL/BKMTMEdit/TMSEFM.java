package MXPSQL.BKMTMEdit;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.dnd.*;
import java.awt.event.*;
import org.fife.ui.rtextarea.*;
import com.formdev.flatlaf.*;
import org.fife.ui.rsyntaxtextarea.*;
import java.awt.datatransfer.DataFlavor;
import MXPSQL.BKMTMEdit.reusable.utils.*;
import javax.swing.UIManager.LookAndFeelInfo;
import org.fife.rsta.ui.CollapsibleSectionPanel;
import org.pushingpixels.radiance.theming.api.skin.*;
import MXPSQL.BKMTMEdit.reusable.widgets.findr.FindAccessory;
import org.pushingpixels.radiance.theming.api.RadianceThemingCortex;

/**
 * Stdout Editor version of TMFM
 * Way minimal
 * @author MXPSQL
 *
 */
public class TMSEFM extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int exit = StaticStorageProperties.goodExit;
	private JFrame dis = this;
	public RSyntaxTextArea tx = new RSyntaxTextArea();
	public RTextScrollPane txp = new RTextScrollPane(tx);
	
    protected JRadioButtonMenuItem metalthememi;
	protected JRadioButtonMenuItem platformthememi;
	protected JRadioButtonMenuItem nimbusthememi;
	protected JRadioButtonMenuItem flatlightthememi;
	protected JRadioButtonMenuItem flatdarkthememi;
	protected JRadioButtonMenuItem flatintellijthememi;
	protected JRadioButtonMenuItem flatDCMthememi;
	protected JRadioButtonMenuItem business;
	protected JRadioButtonMenuItem businessblue;
	protected JRadioButtonMenuItem sahara;
	
    protected void dispatchExitEvent() {
    	dispatchEvent(new WindowEvent(dis, WindowEvent.WINDOW_CLOSING));
    }
    
    
	public void open_file_from_File(File f) throws InterruptedException {
			
		try {
			tx.setText(FSUtils.getTextFromFile(f));
			tx.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}
    
	public void open_file() {
		
		SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>(){

			@Override
			protected Object doInBackground() throws Exception {
				// TODO Auto-generated method stub
				
				try {
					JFileChooser jfc = new JFileChooser();
					jfc.setAccessory(new FindAccessory(jfc));
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
				
				return null;
			}
			
		};
		
		worker.execute();
			
	}
	
	private void initUI() {
		setLayout(new BorderLayout());
		
		{
			CollapsibleSectionPanel csp = new CollapsibleSectionPanel();
			csp.add(txp);
			dis.add(csp, BorderLayout.CENTER);
			
			new DropTarget(txp, new FileDND());
			new DropTarget(csp, new FileDND());
			new DropTarget(tx, new FileDND());
		}
		
		{
			JPanel boxbtn = new JPanel();
			boxbtn.setLayout(new BorderLayout());
			
			
			JButton ok = new JButton("Ok");
			JButton no = new JButton("Cancel");
			
			ok.addActionListener((e) -> {
				if(JOptionPane.showConfirmDialog(dis, "are you sure you want to send and submit this text?", "Sure man?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) return;
				
				System.out.println(tx.getText());
				exit = StaticStorageProperties.goodExit;
				dispatchExitEvent();
			});
			
			no.addActionListener((e) -> {
				if(JOptionPane.showConfirmDialog(dis, "are you sure you want to cancel?", "Sure man?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) return;
				exit = StaticStorageProperties.abortExit;
				dispatchExitEvent();
			});
			
			boxbtn.add(ok);
			boxbtn.add(no, BorderLayout.EAST);
			
			dis.add(boxbtn, BorderLayout.SOUTH);
		}
		
		{
			JMenuBar mb = new JMenuBar();
			
			{
				JMenu fm = new JMenu("File");
				
				JMenuItem openmi = new JMenuItem("Open");
				JMenuItem exitmi = new JMenuItem("Exit");
				
				openmi.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/Open.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
				exitmi.setIcon(new ImageIcon(new ImageIcon(ResourceGet.getURL(this.getClass(), "Resource/Exit.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT)));
				
				openmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
				exitmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK+ActionEvent.SHIFT_MASK+InputEvent.ALT_DOWN_MASK));
				
				openmi.addActionListener((e) -> open_file());
				exitmi.addActionListener((e) -> {
					exit = StaticStorageProperties.abortExit;
					dispatchExitEvent();
				});
				
				fm.add(openmi);
				fm.add(exitmi);
				
				mb.add(fm);
			}
			
			{
        		JMenu appearancem = new JMenu("View");
        		
        		appearancem.setToolTipText("Only few selected themes are available here");
        		
        		ButtonGroup bgr = new ButtonGroup();
        		// ButtonGroup pointer = new ButtonGroup();
        		
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
								case "Business":
									RadianceThemingCortex.GlobalScope.setSkin(new BusinessSkin());
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
						// Funny things that happen: the UI becomes garbage and an exception is thrown, we don't want that funny thing
						switch(e.getActionCommand()) {
							case "Business":
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
        		business = new JRadioButtonMenuItem("Business");
        		businessblue = new JRadioButtonMenuItem("Business Blue");
        		sahara = new JRadioButtonMenuItem("Sahara");
        		
        		metalthememi.addActionListener(Taal);
        		platformthememi.addActionListener(Taal);
        		nimbusthememi.addActionListener(Taal);
        		flatlightthememi.addActionListener(Taal);
        		flatdarkthememi.addActionListener(Taal);
        		flatintellijthememi.addActionListener(Taal);
        		flatDCMthememi.addActionListener(Taal);
        		business.addActionListener(Taal);
        		businessblue.addActionListener(Taal);
        		sahara.addActionListener(Taal);
        		
        		bgr.add(metalthememi);
        		bgr.add(platformthememi);
        		bgr.add(nimbusthememi);
        		bgr.add(flatlightthememi);
        		bgr.add(flatdarkthememi);
        		bgr.add(flatintellijthememi);
        		bgr.add(flatDCMthememi);
        		bgr.add(business);
        		bgr.add(businessblue);
        		bgr.add(sahara);
        		
        		appearancem.add(metalthememi);
        		appearancem.add(platformthememi);
        		appearancem.add(nimbusthememi);
        		appearancem.addSeparator();
        		appearancem.add(flatlightthememi);
        		appearancem.add(flatdarkthememi);
        		appearancem.add(flatintellijthememi);
        		appearancem.add(flatDCMthememi);
        		appearancem.addSeparator();
        		appearancem.add(business);
        		appearancem.add(businessblue);
        		appearancem.add(sahara);
        		
        		// appearancem.setEnabled(false);
        		
        		StaticStorageProperties.theme = StaticStorageProperties.config.getString("theme.type");
        		bgr.clearSelection();
        		switch(StaticStorageProperties.theme) {
        			case "metal":
        				metalthememi.setSelected(true);
        				break;
        			case "platform":
        				platformthememi.setSelected(true);
        				break;
        			case "nimbus":
        				nimbusthememi.setSelected(true);
        				break;
        			case "flatlightlaf":
        				flatlightthememi.setSelected(true);
        				break;
        			case "flatdarklaf":
        				flatdarkthememi.setSelected(true);
        				break;
        			case "flatintellijlaf":
        				flatintellijthememi.setSelected(true);
        				break;
        			case "flatdarculalaf":
        				flatDCMthememi.setSelected(true);
        				break;
        			case "business":
        				business.setSelected(true);
        				break;
        			case "businessblue":
        				businessblue.setSelected(true);
        				break;
        			case "sahara":
        				sahara.setSelected(true);
        				break;
        			default:
    					bgr.clearSelection();
    					break;
        		}
        		
        		mb.add(appearancem);
			}
			
			setJMenuBar(mb);
		}
	}
	
	public TMSEFM(String title, Runnable run) {
		super(title);
		
		initUI();
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
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
				
				try {
					StaticStorageProperties.server.stop();
					// ftreeworker.cancel(false);
					// filechangedworker.cancel(false);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
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
						if(JOptionPane.showConfirmDialog(dis, "U sure you want to drag and drop this file?", "Sure?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) return;
					
						tx.setText(FSUtils.getTextFromFile(filefs));
						break;
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
	};
	
	public int getExit() {
		return exit;
	}
}
