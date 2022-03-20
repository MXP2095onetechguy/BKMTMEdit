package MXPSQL.BKMTMEdit.utils;

/**
MIT License

Copyright (c) 2022 MXPSQL

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

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
