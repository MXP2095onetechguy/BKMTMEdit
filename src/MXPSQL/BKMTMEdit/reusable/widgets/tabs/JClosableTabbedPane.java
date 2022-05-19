package MXPSQL.BKMTMEdit.reusable.widgets.tabs;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;


public class JClosableTabbedPane extends JTabbedPane{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TabCloseUI closeUI = new TabCloseUI(this);
	public TabOnCloseListener tocl = null;
	public int[] offsetDim = null; // the system native look has problems with the drawing, this is an offset thing
	
	public void paint(Graphics g){
		super.paint(g);
		closeUI.paint(g);
	}
	
	public void addTab(String title, Component component) {
		super.addTab(title, component);
	}
	
	public void addTab(Component component, String title) {
		super.addTab(title, component);
	}
	
	
	public String getTabTitleAt(int index) {
		return super.getTitleAt(index).trim();
	}
	
	private class TabCloseUI implements MouseListener, MouseMotionListener {
		private JClosableTabbedPane  tabbedPane;
		private int closeX = 0 ,closeY = 0, meX = 0, meY = 0;
		private int selectedTab;
		private final int  width = 8, height = 8;
		private Rectangle rectangle = new Rectangle(0,0,width, height);
		public TabCloseUI(JClosableTabbedPane pane) {
			
			tabbedPane = pane;
			tabbedPane.addMouseMotionListener(this);
			tabbedPane.addMouseListener(this);
		}
		public void mouseEntered(MouseEvent me) {}
		public void mouseExited(MouseEvent me) {tabbedPane.setCursor(Cursor.getDefaultCursor());}
		public void mousePressed(MouseEvent me) {}
		public void mouseClicked(MouseEvent me) {}
		public void mouseDragged(MouseEvent me) {}
		
		

		public void mouseReleased(MouseEvent me) {
			if(closeUnderMouse(me.getX(), me.getY())){
				boolean isToCloseTab = tabAboutToClose(selectedTab);
				if (isToCloseTab && selectedTab > -1){			
					tabbedPane.removeTabAt(selectedTab);
				}
				selectedTab = tabbedPane.getSelectedIndex();
			}
			
			tabbedPane.setCursor(Cursor.getDefaultCursor());
		}

		public void mouseMoved(MouseEvent me) {	
			meX = me.getX();
			meY = me.getY();			
			if(mouseOverTab(meX, meY)){
				controlCursor();
				tabbedPane.repaint();
			}
			else {
				tabbedPane.setCursor(Cursor.getDefaultCursor());
			}
		}

		private void controlCursor() {
			if(tabbedPane.getTabCount()>0)
				if(closeUnderMouse(meX, meY)){
					tabbedPane.setCursor(new Cursor(Cursor.HAND_CURSOR));	
					if(selectedTab > -1)
						tabbedPane.setToolTipTextAt(selectedTab, "Close " +tabbedPane.getTitleAt(selectedTab));
				}
				else{
					tabbedPane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					if(selectedTab > -1)
						tabbedPane.setToolTipTextAt(selectedTab,"");
				}	
		}

		private boolean closeUnderMouse(int x, int y) {		
			rectangle.x = closeX;
			rectangle.y = closeY;
			return rectangle.contains(x,y);
		}

		public void paint(Graphics g) {
			
			int tabCount = tabbedPane.getTabCount();
			for(int j = 0; j < tabCount; j++)
				if(tabbedPane.getComponent(j).isShowing()){			
					int x = tabbedPane.getBoundsAt(j).x + tabbedPane.getBoundsAt(j).width -width-5;
					int y = tabbedPane.getBoundsAt(j).y +5;	
					if(offsetDim != null)
						drawClose(g,x+offsetDim[0],y+offsetDim[1]);
					else
						drawClose(g, x, y);
					break;
				}
			if(mouseOverTab(meX, meY)){
				if(offsetDim != null)
					drawClose(g,closeX+offsetDim[0],closeY+offsetDim[1]);
				else
					drawClose(g, closeX, closeY);
			}
		}

		private void drawClose(Graphics g, int x, int y) {
			if(tabbedPane != null && tabbedPane.getTabCount() > 0){
				Graphics2D g2 = (Graphics2D)g;				
				drawColored(g2, isUnderMouse(x,y)? Color.RED : Color.WHITE, x, y);
			}
		}

		private void drawColored(Graphics2D g2, Color color, int x, int y) {
			g2.setStroke(new BasicStroke(5,BasicStroke.JOIN_ROUND,BasicStroke.CAP_ROUND));
			g2.setColor(Color.BLACK);
			g2.drawLine(x, y, x + width, y + height);
			g2.drawLine(x + width, y, x, y + height);
			g2.setColor(color);
			g2.setStroke(new BasicStroke(3, BasicStroke.JOIN_ROUND, BasicStroke.CAP_ROUND));
			g2.drawLine(x, y, x + width, y + height);
			g2.drawLine(x + width, y, x, y + height);

		}

		private boolean isUnderMouse(int x, int y) {
			if(Math.abs(x-meX)<width && Math.abs(y-meY)<height )
				return  true;		
			return  false;
		}

		private boolean mouseOverTab(int x, int y) {
			int tabCount = tabbedPane.getTabCount();
			for(int j = 0; j < tabCount; j++)
				if(tabbedPane.getBoundsAt(j).contains(meX, meY)){
					selectedTab = j;
					closeX = tabbedPane.getBoundsAt(j).x + tabbedPane.getBoundsAt(j).width -width-5;
					closeY = tabbedPane.getBoundsAt(j).y +5;					
					return true;
				}
			return false;
		}

	}

	public boolean tabAboutToClose(int tabIndex) {
		if(tocl != null)
			return tocl.onClose(tabIndex);
		else
			return true;
	}

	
}
