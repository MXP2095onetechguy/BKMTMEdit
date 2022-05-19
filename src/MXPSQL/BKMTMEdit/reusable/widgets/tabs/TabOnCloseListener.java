package MXPSQL.BKMTMEdit.reusable.widgets.tabs;

@FunctionalInterface
public interface TabOnCloseListener {
	/**
	 * Should I close the tab?
	 * @param tabIndex what tab is it?
	 * @return false to say No. true to close the tab.
	 */
	public boolean onClose(int tabIndex);
}
