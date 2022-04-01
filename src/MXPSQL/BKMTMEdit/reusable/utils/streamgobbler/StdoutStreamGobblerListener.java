package MXPSQL.BKMTMEdit.reusable.utils.streamgobbler;

public class StdoutStreamGobblerListener implements StreamGobblerListener {
	
	@Override
	public void onMessage(String msg) {
		System.out.println(msg);
	}
}
