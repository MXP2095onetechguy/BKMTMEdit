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

import java.net.*;
import java.awt.*;
import java.io.IOException;

import javax.swing.*;

public class HyperLink extends JButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String URL = "";
	
	/**
	 * internal method to style and set the ur;
	 * @param url the url
	 */
	protected void addListenerAndStyle(String url) {
		URL = url;
		
		setBorder(null);
		setForeground(Color.BLUE);
		setBackground(Color.BLACK);
		setOpaque(false);
		setContentAreaFilled(false);
		setBorderPainted(false);
		
		addActionListener((e) -> {
			if(Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))
				try {
					Desktop.getDesktop().browse(new URI(URL));
				} catch (IOException | URISyntaxException e1) {
				}
		});
	}
	
	/**
	 * Constructor
	 * @param url url
	 */
	public HyperLink(String url) {
		super(url);
		
		addListenerAndStyle(url);
	}
	
	/**
	 * Constructor, but with a specific text
	 * @param url url
	 * @param text the text you want
	 */
	public HyperLink(String url, String text) {
		super(text);
		
		addListenerAndStyle(url);
	}
	
	/**
	 * Constructor, but with an image
	 * @param url url
	 * @param icon image you want
	 */
	public HyperLink(String url, Icon icon) {
		super(icon);
		
		addListenerAndStyle(url);
	}
	
	/**
	 * Constructor, but with the text and image you want
	 * @param url url
	 * @param text text you want
	 * @param ico the image you want yeh
	 */
	public HyperLink(String url, String text, Icon ico) {
		super(text, ico);
		
		addListenerAndStyle(url);
	}
	
	@Override
	public String toString() {
		return "Hyperlink to " + URL;
		
	}
}
