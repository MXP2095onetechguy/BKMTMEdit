package MXPSQL.BKMTMEdit.widgets;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.nio.file.*;
import java.nio.file.attribute.*;

public class FileInfoDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public FileInfoDialog(Frame parent, File f) throws IOException {
		super(parent);
		
		JPanel fm = new JPanel(new BorderLayout());
		
		BasicFileAttributes attr = Files.readAttributes(f.getCanonicalFile().toPath(), BasicFileAttributes.class);
		
		{
			JPanel panel1 = new JPanel(new BorderLayout());
			
			panel1.add(new JLabel("Path: " + f.getCanonicalPath()), BorderLayout.NORTH);
			panel1.add(new JLabel("Is file: " + (f.isFile() ? "yes" : "no")), BorderLayout.SOUTH);
			
			fm.add(panel1, BorderLayout.NORTH);
		}
		
		{
			JPanel panel2 = new JPanel(new BorderLayout());
			panel2.add(new JLabel("Creation date and time: " + attr.creationTime()), BorderLayout.NORTH);
			panel2.add(new JLabel("Last modified date and time: " + attr.lastModifiedTime()));
			
			fm.add(panel2);
		}
		
		{
			JButton quit = new JButton("Quit this window");
			
			quit.addActionListener((e) -> {
				dispose();
			});
			
			fm.add(quit, BorderLayout.SOUTH);
		}
		
		add(fm);	
		
		pack();
	}

}
