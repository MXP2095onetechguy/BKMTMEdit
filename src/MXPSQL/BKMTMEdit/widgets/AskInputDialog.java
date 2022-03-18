package MXPSQL.BKMTMEdit.widgets;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

public class AskInputDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Map<String, String> list_of_objec;
	protected String input = null;
	
	protected JTextArea area = new RSyntaxTextArea(20, 30);
	protected JComboBox<String> cbox;
	
	public AskInputDialog(Frame parent, String title, Map<String, String> stuffYaWant) {
		super(parent);
		
		setTitle(title);
		
		list_of_objec = stuffYaWant;
		
		ArrayList<String> files = new ArrayList<String>(list_of_objec.keySet());
		
		
		JPanel panel = new JPanel(new BorderLayout());
		
		{
		
			panel.add(new RTextScrollPane(area), BorderLayout.NORTH);
			
			String[] farray = new String[files.size()];

			for (int i=0;i<farray.length;i++) farray[i]= files.get(i);
			
			{
				JPanel panel4premadestuff = new JPanel(new BorderLayout());
				cbox = new JComboBox<String>(farray);
				cbox.addActionListener((e) -> {
					
					area.setText(list_of_objec.get(cbox.getSelectedItem()));
				});
				panel4premadestuff.add(cbox);
				panel.add(panel4premadestuff);
			}
			
			{
				JPanel buttonBox = new JPanel(new BorderLayout());
				
				JButton ok = new JButton("Ok");
				JButton cancel = new JButton("Cancel");
				
				ok.addActionListener((e) -> {
					input = area.getText();
					dispose();
				});
				
				cancel.addActionListener((e) -> {
					input = null;
					dispose();
				});
				
				buttonBox.add(ok);
				buttonBox.add(cancel, BorderLayout.EAST);
				
				panel.add(buttonBox, BorderLayout.SOUTH);
			}
		}
		
		add(panel);
		
		pack();
		
		{
			Rectangle r = getBounds();
			
			setMinimumSize(new Dimension(r.width, r.height));
		}
	}
	
	public String getInput() {
		
		setVisible(true);
		
		return input;
	}
}
