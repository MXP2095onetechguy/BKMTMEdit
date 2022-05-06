package MXPSQL.BKMTMEdit.reusable.widgets;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.util.Enumeration;
import org.jdesktop.swingx.*;

public class SearchTreeDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected JTree tree;
	protected FindBar fbar = new FindBar();
	protected JDialog dis = this;
	
	public static TreeNode find(DefaultMutableTreeNode root, String s) {
	    Enumeration<TreeNode> e = root.depthFirstEnumeration();
	    while (e.hasMoreElements()) {
	        DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
	        if (node.toString().equalsIgnoreCase(s)) {
	            return node;
	        }
	    }
	    return null;
	}
	
	public static TreePath getPath(TreeNode treeNode) {
		java.util.List<Object> nodes = new ArrayList<Object>();
		if (treeNode != null) {
		      nodes.add(treeNode);
		      treeNode = treeNode.getParent();
		      while (treeNode != null) {
		        nodes.add(0, treeNode);
		        treeNode = treeNode.getParent();
		      }
		}
		
		return nodes.isEmpty() ? null : new TreePath(nodes.toArray());
	}
	
	private void initUI() {
		JPanel panel = new JPanel(new BorderLayout());
		
		{
			JXHeader header = new JXHeader();
			
			header.setTitle("Search a tree");
			header.setDescription("Find the node that you want.");
			
			panel.add(header, BorderLayout.NORTH);
		}
		
		panel.add(fbar, BorderLayout.CENTER);
		
		{
			fbar.next.addActionListener((e) -> {
				String query = fbar.field.getText();
				
				TreePath[] tpath = tree.getSelectionPaths();
				
				if(tpath != null) {
					if(tpath.length > 0) {
						TreePath stpath = tpath[0];
						TreeNode node = (TreeNode) stpath.getLastPathComponent();
						if(node instanceof DefaultMutableTreeNode) {
							DefaultMutableTreeNode dmnode = (DefaultMutableTreeNode) node;
							
							TreeNode rnode = find(dmnode, query);
							
							if(rnode == null) {
								JOptionPane.showMessageDialog(dis, query + " not found.", "Not Found", JOptionPane.INFORMATION_MESSAGE);
							}
							else {
								TreePath p = getPath(rnode);
								if(p != null) {
									tree.setSelectionPath(p);
									tree.scrollPathToVisible(p);
								}
							}
						}
					}
				}
				else {
					if(tree.getModel().getRoot() == null) return;
					
					TreeNode rnode = find((DefaultMutableTreeNode) tree.getModel().getRoot(), query);
					
					if(rnode == null) {
						JOptionPane.showMessageDialog(dis, query + " not found.", "Not Found", JOptionPane.INFORMATION_MESSAGE);
					}
					else {
						TreePath p = getPath(rnode);
						if(p != null) {
							tree.setSelectionPath(p);
							tree.scrollPathToVisible(p);
						}
					}
				}
			});
		}
		
		{
			JPanel bp = new JPanel(new BorderLayout());
			
			JButton close = new JButton("Close");
			close.addActionListener((e) -> dis.dispose());
			bp.add(close, BorderLayout.EAST);
			
			JButton backToRoot = new JButton("Reset selection to root");
			backToRoot.addActionListener((e) -> {
				TreeNode root = (TreeNode) tree.getModel().getRoot();
				if(root == null) {
					JOptionPane.showMessageDialog(dis, "No root of tree", "No root", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				TreePath path = getPath(root);
				if(path == null) {
					JOptionPane.showMessageDialog(dis, "Root is empty", "Empty root", JOptionPane.INFORMATION_MESSAGE);
				}
				
				tree.setSelectionPath(path);
				tree.scrollPathToVisible(path);
			});
			bp.add(backToRoot);
			
			panel.add(bp, BorderLayout.SOUTH);
		}
		
		add(panel, BorderLayout.CENTER);
		
		setMinimumSize(new Dimension(300, 200));
	}
	
	public SearchTreeDialog(JTree tree) {
		this.tree = tree;
		
		initUI();
	}

	public SearchTreeDialog(Window win, JTree tree) {
		super(win);
		
		this.tree = tree;
		
		initUI();
	}
	
	class FindBar extends JPanel{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public JButton next = new JButton("Find next");
		public JButton prev = new JButton("Find previous");
		public JTextField field = new JTextField();
		
		public FindBar() {
			setLayout(new BorderLayout());
			
			add(new JScrollPane(field), BorderLayout.CENTER);
			
			{
				JPanel p = new JPanel(new BorderLayout());
				
				p.add(next, BorderLayout.CENTER);
				p.add(prev, BorderLayout.WEST);
				
				add(p, BorderLayout.EAST);
			}
		}
	};
}
