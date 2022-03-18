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
import MXPSQL.BKMTMEdit.widgets.HyperLink;


/* completley made with AWT LOL unliek the rest, which is made in swing */
public class About extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected About dis = this;
	
	void initUI() {
		{
			JPanel pane = new JPanel(new BorderLayout());
			{
				JLabel title = new JLabel(StaticStorageProperties.baseTitle);
				pane.add(title, BorderLayout.NORTH);
			}
			
			{
				JLabel image = new JLabel(new ImageIcon(new ImageIcon(this.getClass().getResource("bkmtmedit.png")).getImage().getScaledInstance(256, 256, Image.SCALE_DEFAULT)));
				pane.add(image);
			}
			
			{
				JLabel desc = new JLabel("");
				desc.setText("<html>hEY, this is a text editor. Written in Java using Swing, AWT and JavaFX. <br>You know, this is actualy open source, under MIT License, you can visit the source with the button below.</html>");
				pane.add(desc, BorderLayout.SOUTH);
			}
			
			add(pane, BorderLayout.NORTH);
		}
		
		{
			JButton takemeto = new HyperLink(StaticStorageProperties.source, "Take me to the IDotic source PLSSSSS :(((((( NO WHYYYYYYYY I WANT TO SEE IT PLSSSSSSSSSS!");
			add(takemeto);
		}
		
		{
			JButton close = new JButton("Close");
			close.addActionListener((e) -> {
				dis.dispose();
			});
			add(close, BorderLayout.SOUTH);
		}
	}
	
	public About(Frame parent) {
		super(parent, "About " + StaticStorageProperties.baseTitle, true);
		
		initUI();
		pack();
		setResizable(false);
	}
}
