package MXPSQL.BKMTMEdit.reusable.widgets.findr;

import java.awt.*;
import java.io.File;
import javax.swing.*;

/**
Implements user interface and generates FindFilter for selecting
files by name.
*/
public class FindByName extends JPanel implements FindFilterFactory
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String		NAME_CONTAINS = "contains";
	protected String		NAME_IS = "is";
	protected String		NAME_STARTS_WITH = "starts with";
	protected String		NAME_ENDS_WITH = "ends with";
	protected int			NAME_CONTAINS_INDEX = 0;
	protected int			NAME_IS_INDEX = 1;
	protected int			NAME_STARTS_WITH_INDEX = 2;
	protected int			NAME_ENDS_WITH_INDEX = 3;
	protected String[]		criteria = {NAME_CONTAINS,
										NAME_IS,
										NAME_STARTS_WITH,
										NAME_ENDS_WITH};

	protected JTextField	nameField = null;
	protected JComboBox<String>		combo = null;
	protected JCheckBox		ignoreCaseCheck = null;

	FindByName ()
	{
		super();
		setLayout(new BorderLayout());

		// Grid Layout
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(0,2,2,2));

		// Name
		combo = new JComboBox<String>(criteria);
		combo.setFont(new Font("Helvetica",Font.PLAIN,10));
		combo.setPreferredSize(combo.getPreferredSize());
		p.add(combo);

		nameField = new JTextField(12);
		nameField.setFont(new Font("Helvetica",Font.PLAIN,10));
		p.add(nameField);

		// ignore case
		p.add(new JLabel("",SwingConstants.RIGHT));

		ignoreCaseCheck = new JCheckBox("ignore case",true);
		ignoreCaseCheck.setForeground(Color.black);
		ignoreCaseCheck.setFont(new Font("Helvetica",Font.PLAIN,10));
		p.add(ignoreCaseCheck);

		add(p,BorderLayout.NORTH);
	}

	public FindFilter createFindFilter ()
	{
		return new NameFilter(nameField.getText(),combo.getSelectedIndex(),
								ignoreCaseCheck.isSelected());
	}

	/**
		Filter object for selecting files by name.
	*/
	class NameFilter implements FindFilter
	{
		protected String	match = null;
		protected int		howToMatch = -1;
		protected boolean	ignoreCase = true;

		NameFilter (String name, int how, boolean ignore)
		{
			match = name;
			howToMatch = how;
			ignoreCase = ignore;
		}

		public boolean accept (File f, FindProgressCallback callback)
		{
			if (f == null) return false;

			if ((match == null) || (match.length() == 0)) return true;
			if (howToMatch < 0) return true;

			String filename = f.getName();


			if (howToMatch == NAME_CONTAINS_INDEX)
			{
				if (ignoreCase)
				{
					if (filename.toLowerCase().indexOf(match.toLowerCase()) >= 0)
						return true;
					else return false;
				}
				else
				{
					if (filename.indexOf(match) >= 0) return true;
					else return false;
				}
			}
			else if (howToMatch == NAME_IS_INDEX)
			{
				if (ignoreCase)
				{
					if (filename.equalsIgnoreCase(match)) return true;
					else return false;
				}
				else
				{
					if (filename.equals(match)) return true;
					else return false;
				}
			}
			else if (howToMatch == NAME_STARTS_WITH_INDEX)
			{
				if (ignoreCase)
				{
					if (filename.toLowerCase().startsWith(match.toLowerCase()))
						return true;
					else return false;
				}
				else
				{
					if (filename.startsWith(match)) return true;
					else return false;
				}
			}
			else if (howToMatch == NAME_ENDS_WITH_INDEX)
			{
				if (ignoreCase)
				{
					if (filename.toLowerCase().endsWith(match.toLowerCase()))
						return true;
					else return false;
				}
				else
				{
					if (filename.endsWith(match)) return true;
					else return false;
				}
			}
			
			return true;
		}
	}

}
