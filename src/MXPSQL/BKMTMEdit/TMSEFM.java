package MXPSQL.BKMTMEdit;

import java.awt.*;
import java.io.File;
import javax.swing.*;
import java.awt.dnd.*;
import java.awt.event.*;
import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;
import java.awt.datatransfer.DataFlavor;
import MXPSQL.BKMTMEdit.reusable.utils.FSUtils;
import org.fife.rsta.ui.CollapsibleSectionPanel;

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
				dispatchEvent(new WindowEvent(dis, WindowEvent.WINDOW_CLOSING));
			});
			
			no.addActionListener((e) -> {
				if(JOptionPane.showConfirmDialog(dis, "are you sure you want to cancel?", "Sure man?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) return;
				exit = StaticStorageProperties.abortExit;
				dispatchEvent(new WindowEvent(dis, WindowEvent.WINDOW_CLOSING));
			});
			
			boxbtn.add(ok);
			boxbtn.add(no, BorderLayout.EAST);
			
			dis.add(boxbtn, BorderLayout.SOUTH);
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
