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


import java.awt.*;
import javax.swing.*;
import MXPSQL.BKMTMEdit.reusable.widgets.*;


/* completley made with AWT LOL unliek the rest, which is made in swing */
public class About extends AboutDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected About dis = this;
	
	void initUIAgain() {
		JPanel bugPane = new JPanel(new BorderLayout());
		{
			JTextArea tarea = new JTextArea();
			
			{
				StringBuilder sb = new StringBuilder();
				sb.append("A JVM based text editor. \n\n")
				.append("Written in Swing, SwingX, AWT and JavaFX using Java 11 \nand the Java programming language. \n\n")
				.append("Open source under the MIT License. \n\n")
				.append("Currently at version ").append(StaticStorageProperties.version.toString()).append(". ");
				tarea.setText(sb.toString());
			}
			
			tarea.setEditable(false);
			
			bugPane.add(new JScrollPane(tarea), BorderLayout.CENTER);
		}
		
		{
			JButton takemeto = new HyperLink(StaticStorageProperties.source, "Take me to the IDotic source PLSSSSS :(((((( NO WHYYYYYYYY I WANT TO SEE IT PLSSSSSSSSSS!");
			bugPane.add(takemeto, BorderLayout.SOUTH);
		}
		
		setInfoPanel(bugPane);
		
		header.setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("bkmtmedit.png")).getImage().getScaledInstance(64, 64, Image.SCALE_DEFAULT)));
		header.setTitle(StaticStorageProperties.baseName);
		header.setDescription("A java based text editor, written in AWT, Swing and JavaFX using Java 11.");
		
		{
			StringBuilder sb = new StringBuilder();
			
			sb.append("There is a lot of libraries to talk about here, so because of that, I will not talk everything here, ").append("\n").append("but you can go to the source and read pom.xml to see the libraries used.").append("\n")
			.append("\n")
			.append("These UI Libraries: ").append("\n")
			.append("JavaFX version 11 is used, Licensed under GPL with linking exception").append("\n")
			.append("SwingX (Part of SwingLabs), Licensed under LGPL").append("\n")
			.append("Could have used SWT, but it's broken. Licensed under the EPL").append("\n")
			.append("RSyntaxTextArea is also used, licensed under the 3-Clause BSD License").append("\n")
			.append("FlatLaf is used for the theme, licensed under the Apache-2.0 License").append("\n")
			.append("Radiance for the theme too, licensed under the 3-Clause BSD License").append("\n")
			.append("\n")
			.append("These scripting languages: ").append("\n")
			.append("Beanshell, licensed under the Apache-2.0 License").append("\n")
			.append("Rhino (the javascript engine), licensed under the MPL-2.0 License").append("\n")
			.append("Groovy, licensed under the Apache-2.0 License").append("\n")
			.append("JRuby, licensed under 3 license (EPL, LGPL and GPL), but I choose the EPL");
			
			setAttrsText(sb.toString());
		}
	}
	
	public About(Frame parent) {
		super(parent, "About " + StaticStorageProperties.baseTitle, true);
		
		initUIAgain();
		setSize(600, 500);
		setResizable(false);
	}
}
