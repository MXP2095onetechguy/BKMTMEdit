package MXPSQL.BKMTMEdit;

import java.awt.*;
// import javax.swing.*;

import javax.swing.JFrame;

public class StaticWidget {
	// popup
	public static SystemTray tray;
	public static TrayIcon trayicon;
	public static PopupMenu traypop = new PopupMenu();
	
	// window
	public static JFrame window;
	public static final int[] winsize = new int[] {860, 680};
	
	private StaticWidget() {}
}
