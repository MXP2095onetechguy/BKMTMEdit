package MXPSQL.BKMTMEdit.widgets;

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
