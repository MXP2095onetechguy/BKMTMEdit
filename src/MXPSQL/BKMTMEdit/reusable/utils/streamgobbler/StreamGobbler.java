package MXPSQL.BKMTMEdit.reusable.utils.streamgobbler;

import java.io.*;
import java.util.*;

// Gobbles the stream and gives it to the observers
public class StreamGobbler extends Thread {
	protected List<StreamGobblerListener> listeners = new ArrayList<StreamGobblerListener>();
	protected InputStream is;
	protected boolean shouldRun = true;
	
	public StreamGobbler(InputStream is) {
		this.is = is;
	}
	
	
	
	public void changeInputStream(InputStream is) {
		this.is = is;
	}
	
	public InputStream getInputStream() {
		return is;
	}
	
	
	
	public void addListener(StreamGobblerListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(StreamGobblerListener listener) {
		listeners.remove(listener);
	}
	
	public List<StreamGobblerListener> getListeners(){
		return listeners;
	}
	
	
	
	public void setCancel(boolean yes) {
		shouldRun = yes;
	}
	
	public boolean isCanceled() {
		return !shouldRun;
	}
	
	@Override
	public void run() {
		while(shouldRun) {
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader bfr = new BufferedReader(isr);
				String line=null;
				while(((line = bfr.readLine()) != null) && shouldRun)
					for(StreamGobblerListener sgl : listeners)
						sgl.onMessage(line);
			}
			catch(IOException ioe) {
				;
			}
		}
	}
}
