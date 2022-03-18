package MXPSQL.BKMTMEdit.widgets;

import org.apache.commons.lang3.tuple.Pair;

public class FirstElementPair<L, R> extends Pair<L, R> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	L key;
	R value;
	
	public FirstElementPair(L key, R value){
		this.key = key;
		this.value = value;
	}
	
	@Override
	public R setValue(R value) {
		// TODO Auto-generated method stub
		this.value = value;
		return null;
	}

	@Override
	public L getLeft() {
		// TODO Auto-generated method stub
		return key;
	}

	@Override
	public R getRight() {
		// TODO Auto-generated method stub
		return value;
	}
	
	@Override
	public String toString() {
		return key.toString();
	}

}
