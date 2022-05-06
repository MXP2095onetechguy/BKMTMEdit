package MXPSQL.BKMTMEdit.pluginapi;

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

import javax.swing.*;

public abstract class BKMTMEditTabPlugin {
	
	public BKMTMEditTabPlugin() {
	}
	
	public String sayHello() {
		return "Hello from " + getName();
	}
	
	/**
	 * Name for tab and menu
	 * @return the string (name)
	 */
	public abstract String getTabAndMenuName();
	
	/**
	 * Create your UI here and create the logic here too.
	 * @return your JPanel with your UI
	 */
	public abstract JPanel initUIandLogic();
	
	public String getName() {
		return getClass().getName();
	}
}
