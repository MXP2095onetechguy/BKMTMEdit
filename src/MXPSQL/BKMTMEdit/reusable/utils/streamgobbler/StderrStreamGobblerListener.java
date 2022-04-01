package MXPSQL.BKMTMEdit.reusable.utils.streamgobbler;

public class StderrStreamGobblerListener implements StreamGobblerListener {

	@Override
	public void onMessage(String msg) {
		System.err.println(msg);
	}

}
