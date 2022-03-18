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

import java.net.URL;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URISyntaxException;

public class NetUtil {
	private NetUtil() {
		;
	}
	
	public static boolean isValidUrl(String url) {
		try {
			new URL(url).toURI();
			return true;
		}
		catch(URISyntaxException | MalformedURLException e) {
			return false;
		}
	}
	
	public static boolean isPortValid(int port) {
		boolean yes = true;
		
		if(port > 65535) {
			yes = false;
		}
		
		if(port < 0) {
			yes = false;
		}
		
		return yes;
	}
	
	public static int getAvailablePort(int minPort, int maxPort) throws IOException {
		int cport = minPort;
		
		if(!isPortValid(cport)) {
			throw new IOException("Invalid port!");
		}
		
		ServerSocket sock = null;
		
		try { 
		    sock = new ServerSocket(minPort); 
		} catch( IOException ioe ){
		   for( int i = minPort; i < maxPort; i++ ) try {
		        sock = new ServerSocket( i );
		    } catch( IOException ioe2 ){}
		}
		
		if(sock == null) {
			cport = -1;
		}
		else {
			if(!sock.isClosed())
				sock.close();
			
			sock = null;
		}
		
		return cport;
	}
}
