package MXPSQL.BKMTMEdit.reusable.utils.streamgobbler;

// The observers will listen to whatever the streamgobblers have
// It's up to you to do with what the streamgobbler gave you
@FunctionalInterface
public interface StreamGobblerListener {
	public void onMessage(String msg);
}
