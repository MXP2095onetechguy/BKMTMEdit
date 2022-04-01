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
