package MXPSQL.BKMTMEdit;

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
import java.awt.*;
import java.util.*;
import java.net.URI;
import javax.swing.*;
import java.net.http.*;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RESTClient extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String URL = "";
	
	private HttpClient httpcleint;
	private HttpResponse<String> resp;
	
	private String errmsg = "";
	private boolean requestProceeded = false;
	private boolean requestFailed = false;
	
	
	private JDialog thisdialog = this;
	
	private void doRequest(String url, HttpRequest req) {
		try {
			requestProceeded = true;
			resp = httpcleint.send(req, HttpResponse.BodyHandlers.ofString());
			thisdialog.setVisible(false);
			
			URL = url;
		} catch (IOException | InterruptedException e1) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos);
			e1.printStackTrace(ps);
			
			errmsg = baos.toString();
			requestFailed = true;
		}
	}
	
	
	private void initUI() {
		JTextField text = new JTextField();
		
		
		add(text);
		
		{
			JPanel buttonBox = new JPanel();
			buttonBox.setLayout(new BoxLayout(buttonBox, BoxLayout.LINE_AXIS));
		
			JButton cancelBtn = new JButton("Cancel");
			cancelBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					thisdialog.setVisible(false);
				}
			
			});
		
			JButton getBtn = new JButton("GET");
			getBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					String URl = text.getText();
					HttpRequest req = HttpRequest.newBuilder(URI.create(URl)).GET().build();
					doRequest(URl, req);
					
				}
			
			});
			
			JButton postBtn = new JButton("POST");
			postBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					// String URl = text.getText();
					// HttpRequest req = HttpRequest.newBuilder(URI.create(URl)).POST().builder();
					// doRequest(URl, req);
					JOptionPane.showMessageDialog(thisdialog, "NotImplemented");
					
				}
			
			});
		
		
			buttonBox.add(getBtn);
			buttonBox.add(postBtn);
			buttonBox.add(cancelBtn);
			add(buttonBox, BorderLayout.SOUTH);
		
		}
		
		
	}
	
	public Optional<String> get(){
		StringBuilder sb = new StringBuilder();
		
		if(requestProceeded) {
			if(!requestFailed) {
				sb.append("Status: " + resp.statusCode()+ System.getProperty("line.separator"));
				sb.append("URI: " + resp.uri().toString()+ System.getProperty("line.separator"));
				sb.append("Headers: " + resp.headers().toString()+ System.getProperty("line.separator"));
				sb.append("Request: " + resp.request().toString()+ System.getProperty("line.separator"));
				sb.append("========================"+ System.getProperty("line.separator"));
				sb.append(resp.body());
			}
			else {
				sb.append("Request Failed"+ System.getProperty("line.separator"));
				sb.append("========================"+ System.getProperty("line.separator"));
				sb.append(errmsg);
				
				URL = "Failed REST";
			}
			
		}
		else {
			URL = "No REST";
			sb.append("Cancled REST");
		}
		
		Optional<String> res = Optional.of(sb.toString());
		
		return res;
	}
	
	public RESTClient(Frame f, HttpClient cli) {
		super(f, "REST Client");
		setSize(400, 400);
		setResizable(false);
		httpcleint = cli;
		initUI();
	}
}
