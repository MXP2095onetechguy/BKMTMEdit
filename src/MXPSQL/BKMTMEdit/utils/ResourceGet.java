package MXPSQL.BKMTMEdit.utils;

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
		URL url = getURL(clas_y, resource);
		
		if(url == null) {
			return null;
		}
		else {
			return new File(url.toString());
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
