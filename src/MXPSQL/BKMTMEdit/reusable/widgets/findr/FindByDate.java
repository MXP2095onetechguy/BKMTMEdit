package MXPSQL.BKMTMEdit.reusable.widgets.findr;

import java.awt.*;
import java.text.*;
import java.io.File;
import javax.swing.*;
import java.util.Date;

/**
Implements a user interface and generates FindFilter for selecting
files by date.
*/
public class FindByDate extends JPanel implements FindFilterFactory
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static String	THE_BIG_BANG = "The Big Bang";
	public static String	THE_BIG_CRUNCH = "The Big Crunch";
	public static String	YESTERDAY = "Yesterday";
	public static String	TODAY = "Today";
	public static String	NOW = "Now";

	public static String	MODIFIED_LABEL = "Modified";
	public static String	FORMAT_LABEL = "mm/dd/yyyy";
	public static String	FROM_DATE_LABEL = "between start of";
	public static String	TO_DATE_LABEL = "and end of";

	protected JComboBox<String>		fromDateField = null;
	protected JComboBox<String>		toDateField = null;

	protected String[]		fromDateItems = {THE_BIG_BANG,YESTERDAY,TODAY};
	protected String[]		toDateItems = {THE_BIG_CRUNCH,TODAY,NOW,YESTERDAY};


	FindByDate ()
	{
		super();
		setLayout(new BorderLayout());

		Font font = new Font("Helvetica",Font.PLAIN,10);

		// Grid Layout
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(0,2,2,2));

		// Date selection criteria
		JLabel modified = new JLabel(MODIFIED_LABEL,SwingConstants.LEFT);
		modified.setFont(font);
		modified.setForeground(Color.black);
		p.add(modified);

		// format note
		JLabel format = new JLabel(FORMAT_LABEL,SwingConstants.LEFT);
		format.setFont(font);
		format.setForeground(Color.black);
		p.add(format);

		// between
		JLabel betweenLabel = new JLabel(FROM_DATE_LABEL,SwingConstants.RIGHT);
		betweenLabel.setFont(font);
		betweenLabel.setForeground(Color.black);
		p.add(betweenLabel);

		// from date
		//fromDateField = new JTextField(8);
		fromDateField = new JComboBox<String>(fromDateItems);
		fromDateField.setFont(font);
		fromDateField.setEditable(true);
		p.add(fromDateField);

		// and
		JLabel andLabel = new JLabel(TO_DATE_LABEL,SwingConstants.RIGHT);
		andLabel.setFont(font);
		andLabel.setForeground(Color.black);
		p.add(andLabel);

		//toDateField = new JTextField(8);
		toDateField = new JComboBox<String>(toDateItems);
		toDateField.setFont(font);
		toDateField.setEditable(true);
		p.add(toDateField);

		add(p,BorderLayout.NORTH);
	}

	/**
		Generate a search filter object based on the setting of this UI
		component.

		@return a FindFilter object that implements the selection criteria
	*/
	public FindFilter createFindFilter ()
	{
		long from = -1;
		long to = -1;

		from = startDateToTime((String)fromDateField.getSelectedItem());
		to = endDateToTime((String)toDateField.getSelectedItem());

		return new DateFilter(from,to);
	}

	/**
		Convenience method for converting the start date text to milliseconds
		since January 1, 1970.

		@return milliseconds since January 1, 1970
	*/
	protected long startDateToTime (String s)
	{
		if (s == null) return -1;
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		Date d = formatter.parse(s,new ParsePosition(0));
		if (d == null)
		{
			if (s.equalsIgnoreCase(TODAY))
			{
				String today = formatter.format(new Date());
				d = formatter.parse(today,new ParsePosition(0));
			}
			else if (s.equalsIgnoreCase(YESTERDAY))
			{
				String yesterday = formatter.format(
						new Date(new Date().getTime() - 24*60*60*1000));
				d = formatter.parse(yesterday,new ParsePosition(0));
			}
			else if (s.equalsIgnoreCase(THE_BIG_BANG))
			{
				return 0;	// Not exactly the beginning of time, but
							//close enough for computer work
			}
		}
		if (d != null) return d.getTime();
		return -1;
	}

	/**
		Convenience method for converting the end date text to milliseconds
		since January 1, 1970. The end time is the end of the specified day.

		@return milliseconds since January 1, 1970
	*/
	protected long endDateToTime (String s)
	{
		if (s == null) return -1;
		SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");

		long time = -1;
		Date d = dateFormatter.parse(s,new ParsePosition(0));
		if (d == null)
		{
			if (s.equalsIgnoreCase(TODAY))
			{
				String today = dateFormatter.format(new Date());
				d = dateFormatter.parse(today,new ParsePosition(0));
				if (d != null) time = d.getTime() + (24L*3600L*1000L);
			}
			else if (s.equalsIgnoreCase(YESTERDAY))
			{
				String yesterday = dateFormatter.format(
					new Date(new Date().getTime() - 24*60*60*1000));
				d = dateFormatter.parse(yesterday,new ParsePosition(0));
				if (d != null) time = d.getTime() + (24L*3600L*1000L);
			}
			else if (s.equalsIgnoreCase(NOW))
			{
				d = new Date();
				if (d != null) time = d.getTime();
			}
			else if (s.equalsIgnoreCase(THE_BIG_CRUNCH))
			{
				time = Long.MAX_VALUE;
			}
		}
		else
		{
			// Valid date. Now add 24 hours to make sure that the
			// date is inclusive
			time = d.getTime() + (24L*3600L*1000L);
		}

		return time;
	}

	/**
		Filter object for selecting files by the date range specified by the UI.
	*/
	class DateFilter implements FindFilter
	{
		protected long	startTime = -1;
		protected long	endTime = -1;


		DateFilter (long from, long to)
		{
			startTime = from;
			endTime = to;
		}

		public boolean accept (File f, FindProgressCallback callback)
		{
			if (f == null) return false;

			long t = f.lastModified();

			if (startTime >= 0)
			{
				if (t < startTime) return false;
			}
			if (endTime >= 0)
			{
				if (t > endTime) return false;
			}

			return true;
		}
	}

}