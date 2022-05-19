package MXPSQL.BKMTMEdit.reusable.widgets;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;
import org.jdesktop.swingx.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class SearchTreeDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected JTree tree;
	protected FindBar fbar = new FindBar(false);
	protected JDialog dis = this;
	
	protected String cachedName = "";
	protected java.util.List<TreeNode> nodesHit = null;
	
	protected int index = 0;
	
	public static java.util.List<TreeNode> findNodes(DefaultMutableTreeNode root, String query) {
		java.util.List<TreeNode> nodes = new LinkedList<TreeNode>();
	    Enumeration<TreeNode> e = root.depthFirstEnumeration();
	    while (e.hasMoreElements()) {
	        DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
	        if (node.toString().equalsIgnoreCase(query)) {
	        	nodes.add(node);
	        }
	    }
	    return nodes;
	}
	
	public static TreeNode find(DefaultMutableTreeNode root, String s) {
		java.util.List<TreeNode> nodes = findNodes(root, s);
		if(nodes.size() > 0) {
			return nodes.get(0);
		}
		else {
			return null;
		}
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
	
	protected void setSelectedNode(int nindex) {
		DefaultMutableTreeNode nodei = (DefaultMutableTreeNode) nodesHit.get(nindex);
		TreePath tpath = new TreePath(nodei.getPath());
		tree.setSelectionPath(tpath);
		if(!nodei.isLeaf())tree.expandPath(tpath);
		tree.scrollPathToVisible(tpath);
	}
	
	protected void loadCache() {
		String query = fbar.field.getText();
		cachedName = query;
		
		nodesHit = findNodes((DefaultMutableTreeNode)tree.getModel().getRoot(), query);
		index = 0;
	}
	
	protected void browseNode(boolean backwards) {
		if(nodesHit == null) {
			JOptionPane.showMessageDialog(tree, "Cache not loaded yet. \nTo create/update the cache, use the load cache button", "Cache not loaded yet", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		if(nodesHit.size() < 1) {
			JOptionPane.showMessageDialog(tree, "Node '" + cachedName + "' not found. \nTo update the cache, use the load cache button.", "No nodes found.", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		if(!backwards) {
			if(index >= nodesHit.size()) {
				index = 0;
			}
		}
		else {
			if(index <= 0) {
				index = nodesHit.size() - 1;
			}
		}
		
		
		setSelectedNode(index);
		if(!backwards) {
			index += 1;
		}
		else {
			index -= 1;
		}
		
		System.out.println(index);
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
			
			fbar.load.addActionListener((e) -> loadCache());
			fbar.field.addActionListener((e) -> loadCache());
			fbar.field.getDocument().addDocumentListener(new DocumentListener() {

				@Override
				public void insertUpdate(DocumentEvent e) {
					// TODO Auto-generated method stub
					loadCache();
				}

				@Override
				public void removeUpdate(DocumentEvent e) {
					// TODO Auto-generated method stub
					loadCache();
				}

				@Override
				public void changedUpdate(DocumentEvent e) {
					// TODO Auto-generated method stub
					loadCache();
				}
				
			});
			
			fbar.next.addActionListener((e) -> browseNode(false));
			fbar.prev.addActionListener((e) -> browseNode(true));
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
		
		public JButton load = new JButton("Load Tree Cache");
		public JButton next = new JButton("Find next");
		public JButton prev = new JButton("Find previous");
		public JTextField field = new JTextField();
		
		public FindBar(boolean addload) {
			setLayout(new BorderLayout());
			
			add(new JScrollPane(field), BorderLayout.CENTER);
			
			{
				JPanel p = new JPanel(new BorderLayout());
				if(addload) p.add(load, BorderLayout.EAST);
				p.add(next, BorderLayout.CENTER);
				p.add(prev, BorderLayout.WEST);
				
				add(p, BorderLayout.SOUTH);
			}
		}
	};
}
