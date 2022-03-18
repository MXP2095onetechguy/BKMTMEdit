package MXPSQL.BKMTMEdit.pluginapi;

import javax.swing.*;

public abstract class BKMTMEditTabPlugin {
	
	public JMenu pluginMenu;
	public JMenuItem mi;
	public JTabbedPane etab;
	
	public BKMTMEditTabPlugin() {
	}
	
	public String sayHello() {
		return "Hello from " + getName();
	}
	
	public void preInit() {
		mi = new JMenuItem(getName());
		
		pluginMenu.add(mi);
	}
	
	public abstract void initUIandLogic();
	
	public String getName() {
		return getClass().getName();
	}
}
