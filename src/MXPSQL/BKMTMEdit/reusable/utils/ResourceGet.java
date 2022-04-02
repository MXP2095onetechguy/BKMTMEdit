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

import java.io.*;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;

public class ResourceGet {
	// simple
	public static URL getURL(Class<?> clas_y, String resource) {
		return clas_y.getResource(resource);
	}
	
	public static URI getURI(Class<?> clas_y, String resource) throws URISyntaxException{
		URL url = getURL(clas_y, resource);
		
		if(url == null) {
			return null;
		}
		else {
			return url.toURI();
		}
	}
	
	public static File getFile(Class<?> clas_y, String resource) {
		URI uri;
		try {
			uri = getURI(clas_y, resource);
			
			if(uri == null) {
				return null;
			}
			else {
				return new File(uri);
			}
		} catch (URISyntaxException e) {
			return null;
		}
	}
	
	public static String getString(Class<?> clas_y, String resource) {
		File f = getFile(clas_y, resource);
		
		if(f == null) {
			return null;
		}
		else {
			try {
				return f.getCanonicalPath();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
	}
	
	public static InputStream getStream(Class<?> clas_y, String resource) {
		return clas_y.getResourceAsStream(resource);
	}
	
	// streamed file
	public static String getResourceInAString(Class<?> clas_y, String resource) throws FileNotFoundException {
		String str = FSUtils.getTextFromFile(getFile(clas_y, resource));
		
		if(str == null) {
			return null;
		}
		else {
			return str;
		}
	}
}
