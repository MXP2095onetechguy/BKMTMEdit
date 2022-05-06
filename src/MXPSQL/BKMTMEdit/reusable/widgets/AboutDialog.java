package MXPSQL.BKMTMEdit.reusable.widgets;

import java.awt.*;
import javax.swing.*;
import org.jdesktop.swingx.JXHeader;
import javax.swing.table.AbstractTableModel;

public class AboutDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected JPanel top;
	protected JTabbedPane infoTab;
	protected JPanel programInfo;
	protected JPanel sysInfo;
	
	protected JXHeader header;
	
	protected JTextArea attrs;
	
	protected AboutDialog dis = this;
	
	protected void initUI() {
		JPanel contentPanel = new JPanel(new BorderLayout());
		infoTab = new JTabbedPane();
		
		programInfo = new JPanel(new BorderLayout());
		sysInfo = new JPanel(new BorderLayout());
		infoTab.add(programInfo, "About and Info");
		
		header = new JXHeader();
		add(header, BorderLayout.NORTH);
		
		{
			
			JTable tarea = new JTable(10, 5);
			/* JTextArea tarea = new JTextArea();
			
			{
				StringBuilder sb = new StringBuilder();
				
				sb.append("OS: ").append(System.getProperty("os.name", "unknown")).append("\n")
				.append("OS Version: ").append(System.getProperty("os.version", "unknown")).append("\n")
				.append("Architecture (Arch): ").append(System.getProperty("os.arch", "unknown")).append("\n")
				.append("\n")
				.append("Java runtime version: ").append(System.getProperty("java.version", "unknown")).append("\n")
				.append("Java runtime vendor: ").append(System.getProperty("java.vendor", "unknown")).append("\n")
				.append("Java class version: ").append(System.getProperty("java.class.version", "unknown")).append("\n")
				.append("\n")
				.append("Java home: ").append(System.getProperty("java.home", "/////unknown")).append("\n")
				.append("Java classpath: ").append(System.getProperty("java.class.path", "/////unknown")).append("\n")
				.append("Java library path: ").append(System.getProperty("java.library.path", "/////unknown")).append("\n")
				.append("Java temp dir: ").append(System.getProperty("java.io.temp", "/////unknown")).append("\n")
				.append("\n")
				.append("Java virtual machine name: ").append(System.getProperty("java.vm.name", "unknown")).append("\n")
				.append("Java virtual machine version: ").append(System.getProperty("java.vm.version", "unknown")).append("\n")
				.append("Java virtual machine vendor: ").append(System.getProperty("java.vm.vendor", "unknown")).append("\n")
				.append("\n")
				.append("Java specification name: ").append(System.getProperty("java.specification.name", "unknown")).append("\n")
				.append("Java specification version: ").append(System.getProperty("java.specification.version", "unknown")).append("\n")
				.append("Java specification vendor: ").append(System.getProperty("java.specification.vendor", "unknown")).append("\n");
				
				tarea.setText(sb.toString());
			}
			
			tarea.setEditable(false); */
			
			tarea.setModel(new SysInfoTableModel());
			tarea.setFillsViewportHeight(true);
			
			JScrollPane spane = new JScrollPane(tarea);
			
			sysInfo.add(spane, BorderLayout.CENTER);
			infoTab.add(sysInfo, "System information");
		}
		
		attrs = new JTextArea();
		attrs.setText("Attributions can go here");
		attrs.setEditable(false);
		infoTab.add(new JScrollPane(attrs), "Attributions");
		
		contentPanel.add(infoTab, BorderLayout.CENTER);
		add(contentPanel);
		
		{
			JButton close = new JButton("Close");
			close.addActionListener((e) -> {
				dis.dispose();
			});
			{
				JPanel se = new JPanel(new BorderLayout());
				se.add(close, BorderLayout.EAST);
				add(se, BorderLayout.SOUTH);
			}
		}
		
	}
	
	public void setInfoPanel(Component comp) {
		for(Component c : programInfo.getComponents()) {
			programInfo.remove(c);
		}
		
		infoTab.revalidate();
		infoTab.repaint();
		
		programInfo.add(comp);
	}
	
	public void setAttrsText(String text) {
		attrs.setEditable(true);
		attrs.setText(text);
		attrs.setEditable(false);
	}
	
	public String getAttrsText() {
		return attrs.getText();
	}
	
	public AboutDialog() {
		super();
		
		initUI();
		pack();
	}
	
	public AboutDialog(Frame parent) {
		super(parent);
		
		initUI();
		pack();
	}
	
	public AboutDialog(Frame parent, String title) {
		super(parent, title);
		
		initUI();
		pack();
	}
	
	public AboutDialog(Frame parent, String title, boolean modal) {
		super(parent, title, modal);
		
		initUI();
		pack();
	}
	
	
	
	
	
	public class SysInfoTableModel extends AbstractTableModel{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private String[] columnNames = {
				"Name",
				"Description",
				"Value"
		};
		
		private Object[][] data = new Object[][] {
			{"os.name", "OS", System.getProperty("os.name", "unknown")},
			{"os.version", "OS Version", System.getProperty("os.version", "unknown")},
			{"os.arch", "Architecture (Arch)", System.getProperty("os.arch", "unknown")},
			
			{"java.version", "Java Runtime Version", System.getProperty("java.version", "unknown")},
			{"java.vendor", "Java Runtime Vendor", System.getProperty("java.vendor", "unknown")},
			{"java.vendor.url", "Java Runtime Vendor URL", System.getProperty("java.vendor.url", "unknown")},
			{"java.class.version", "Java Class Format Version", System.getProperty("java.class.version", "unknown")},
			
			{"java.home", "Java Home", System.getProperty("java.home", "/////unknown")},
			{"java.class.path", "Java ClassPath", System.getProperty("java.class.path", "/////unknown")},
			{"java.library.path", "Java Library Loading Path", System.getProperty("java.library.path", "/////unknown")},
			{"java.io.temp", "Java Temporary Path", System.getProperty("java.io.temp", "/////unknown")},
			
			{"java.vm.name", "Java Virtual Machine (JVM)", System.getProperty("java.vm.name", "unknown")},
			{"java.vm.version", "Java Virtual Machine Version", System.getProperty("java.vm.version", "unknown")},
			{"java.vm.vendor", "Java Virtual Machine Vendor", System.getProperty("java.vm.vendor", "unknown")},
			
			{"java.specification.name", "Java Specification", System.getProperty("java.specification.name", "unknown")},
			{"java.specification.version", "Java Specification Version", System.getProperty("java.specification.version", "unknown")},
			{"java.specification.vendor", "Java Specification Vendor", System.getProperty("java.specification.vendor", "unknown")}
		};
		
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
		
		@Override
		public int getRowCount() {
			// TODO Auto-generated method stub
			return data.length;
		}

		@Override
		public int getColumnCount() {
			// TODO Auto-generated method stub
			return columnNames.length;
		}
		
		@Override
		public String getColumnName(int col) {
			return columnNames[col];
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			// TODO Auto-generated method stub
			return data[rowIndex][columnIndex];
		}
		
	}
}
