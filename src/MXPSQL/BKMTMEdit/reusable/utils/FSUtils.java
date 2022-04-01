package MXPSQL.BKMTMEdit.reusable.utils;

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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.IOException;
import java.io.PrintStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;

public class FSUtils {
	public static void printstreamTextToFile(String text, PrintStream ps) throws IOException {
		String[] lines = text.split(System.lineSeparator());

		for(int i = 0; i < lines.length; i++){
			ps.println(lines[i]);
		}
	}

	public static void saveTextToFile(String text, File paf) throws IOException {
		DataOutputStream d = new DataOutputStream(new FileOutputStream(paf));
		
		d.writeBytes(text);
		d.close();
	}
	
	public static String getTextFromFile(File f) throws FileNotFoundException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		
		Scanner fscan = new Scanner(f);
		
		while(fscan.hasNextLine()) {
			ps.println(fscan.nextLine());
			// System.out.println(baos.toString());
		}
		
		fscan.close();
		
		return baos.toString();
	}
	
	private FSUtils() {
		
	}
}
