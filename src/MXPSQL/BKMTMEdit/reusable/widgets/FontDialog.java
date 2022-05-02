package MXPSQL.BKMTMEdit.reusable.widgets;

import java.awt.*;
import javax.swing.*;
import java.util.Arrays;
import java.util.Optional;
import javax.swing.text.JTextComponent;

public class FontDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static int maxFontSize=160;
	
    String[] fontNames=GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    String[] fontStyles={
         "  Plain  ",
         "  Bold  ",
         "  Italic  ",
         "  Plain+Bold  ",
         "  Plain+Italic  ",
         "  Bold+Italic  ",
         "  Plain+Bold+Italic  "
                                 };
    Font selectedFont = null;
    
    DefaultListModel<Integer> fontModel;
    List lst=new List();
    JList<String> fontsList;
    JList<String> fontStyleList;
    JList<Integer> fontSizeList;
    
    
    JPanel jp1,jp2;
    JLabel displayLabel;
    JButton ok,cancel;
    // JTextPane textPane;
    JTextComponent textPane;
    
    private FontDialog dis = this;
    
    public FontDialog(Frame fm, JTextComponent textPane) {
    	super(fm, "Choose A Font!");
    	this.textPane = textPane;
    	setLayout(new BorderLayout());
    	
    	jp1 = new JPanel();
    	jp2 = new JPanel();
    	
    	{
    		jp1.setLayout(new BorderLayout());
    		
    		fontsList = new JList<String>(fontNames);
    		jp1.add(new JScrollPane(fontsList), BorderLayout.WEST);
    		
    		// System.out.println(textPane.getFont().getName());
    		
    		if(Arrays.asList(fontNames).indexOf(textPane.getFont().getName()) != -1) {
    			fontsList.setSelectedIndex(Arrays.asList(fontNames).indexOf(textPane.getFont().getName()));
    		}
    		
    		fontStyleList = new JList<String>(fontStyles);
    		jp1.add(fontStyleList, BorderLayout.CENTER);
    		
    		{
    			fontModel=new DefaultListModel<Integer>();
    			fontSizeList = new JList<Integer>(fontModel);
    			add(new JScrollPane(fontSizeList), BorderLayout.EAST);
    			
    			for(int i = 1; i<FontDialog.maxFontSize+1; i++) {
    				fontModel.addElement(i);
    			}
    		}
    		
    		if(Arrays.asList(fontModel.toArray()).indexOf(textPane.getFont().getSize()) != -1) {
    			fontSizeList.setSelectedIndex(Arrays.asList(fontModel.toArray()).indexOf(textPane.getFont().getSize()));
    		}
    	}
    	
    	{
    		ok = new JButton("Ok");
    		cancel = new JButton("Cancel");
    		
    		ok.addActionListener((e) -> {
    			String fontname = fontsList.getSelectedValue();
    			Integer fontSizeRaw = fontSizeList.getSelectedValue();
    			int fontSize = textPane.getFont().getSize();
    			
    			if(fontname == null) {
    				fontname = textPane.getFont().toString();
    			}
    			else {
    				fontname = fontname.toString();
    			}
    			
    			if(fontSizeRaw != null) {
    				fontSize = fontSizeRaw;
    			}
    			
    			if(fontStyleList.getSelectedValue() == null) selectedFont = new Font(fontname, Font.PLAIN, fontSize);
    			else {
    				
    				// copy pasted from https://github.com/MXP2095onetechguy/JEXTEdit/blob/d5002364a4d109d698b7fb4af926db276ba97eba/src/app/MXPSQL/JEXTEdit/FontAction.java#L282 and copypasted
    				String fontstyle = fontStyleList.getSelectedValue().toString().trim();
    	            switch (fontstyle)
    	            {
    	                case "Plain":
    	                	selectedFont = (new Font(fontname, Font.PLAIN, fontSize));
    	                    break;

    	                case "Bold":
    	                	selectedFont = (new Font(fontname, Font.BOLD, fontSize));
    	                    break;

    	                case "Italic":
    	                	selectedFont = (new Font(fontname, Font.ITALIC, fontSize));
    	                    break;

    	                case "Plain+Bold":
    	                	selectedFont = (new Font(fontname, Font.PLAIN + Font.BOLD, fontSize));
    	                    break;

    	                case "Plain+Italic":
    	                	selectedFont = (new Font(fontname, Font.PLAIN + Font.ITALIC, fontSize));
    	                    break;

    	                case "Bold+Italic":
    	                    selectedFont = (new Font(fontname, Font.BOLD + Font.ITALIC, fontSize));
    	                    break;

    	                case "Plain+Bold+Italic":
    	                    selectedFont = (new Font(fontname, Font.PLAIN + Font.BOLD + Font.ITALIC, fontSize));
    	                    break;
    	            }
    			}
    			
    			dis.dispose();
    		});
    		
    		cancel.addActionListener((e) -> {
    			selectedFont = null;
    			dis.dispose();
    		});
    		
    		jp2.add(ok);
    		jp2.add(cancel);
    	}
    	
    	add(jp1, BorderLayout.CENTER);
    	add(jp2, BorderLayout.SOUTH);
    	
    	dis.pack();
    }
    
    public Optional<Font> get(){
    	if(selectedFont != null) return Optional.of(selectedFont);
    	return Optional.empty();
    }
}
