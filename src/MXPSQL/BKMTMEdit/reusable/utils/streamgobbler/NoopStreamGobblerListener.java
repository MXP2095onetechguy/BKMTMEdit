package MXPSQL.BKMTMEdit.reusable.utils.streamgobbler;

public class NoopStreamGobblerListener implements StreamGobblerListener {

	@Override
	public void onMessage(String msg) {
		; // do nothing
	}

}
