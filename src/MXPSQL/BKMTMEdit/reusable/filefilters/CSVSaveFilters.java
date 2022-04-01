package MXPSQL.BKMTMEdit.reusable.filefilters;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class CSVSaveFilters extends FileFilter {

	@Override
	public boolean accept(File f) {
		// TODO Auto-generated method stub
		if (f.isDirectory()) {
	        return false;
	    }
		
		String s = f.getName().toLowerCase();
		
		return s.endsWith(".csv");
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "CSV files *.csv";
	}

}
