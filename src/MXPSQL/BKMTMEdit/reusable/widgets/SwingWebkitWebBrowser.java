package MXPSQL.BKMTMEdit.reusable.widgets;

import javax.swing.JPanel;
import javafx.scene.Scene;
import javafx.embed.swing.*;
import java.awt.BorderLayout;
import javafx.scene.web.WebEngine;

public class SwingWebkitWebBrowser extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public WebkitWebBrowser browser;
	public Scene scenery;
	public WebEngine webe;
	protected void commonInit(String url) {
		setLayout(new BorderLayout());
		browser = new WebkitWebBrowser(url);
		webe = browser.getEngine();
		
		JFXPanel panel = new JFXPanel();
		
		scenery = new Scene(browser);
		panel.setScene(scenery);
		
		add(panel);
	}
	
	public SwingWebkitWebBrowser() {
		commonInit("https://google.com");
	}
	
	public SwingWebkitWebBrowser(String url) {
		commonInit(url);
	}
}
