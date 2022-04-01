package MXPSQL.BKMTMEdit.reusable.widgets;

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

import javafx.event.*;
import javafx.scene.web.*;
import javafx.beans.value.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.concurrent.Worker;
import javafx.scene.input.KeyCode;
import javafx.concurrent.Worker.State;

public class WebkitWebBrowser extends VBox {
	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	public WebView webb;
	WebEngine webe;
	TextField field;
	String homeURL;
	
	
	private void commonInit(String url) {
		homeURL = url;
		
		webb = new WebView();
		webe = webb.getEngine();
		webe.load(homeURL);
		
		
		{
			HBox box = new HBox();
			
			Button rel = new Button("reload");
			Button home = new Button("Home");
			Button back = new Button("<");
			Button forward = new Button(">");
			field = new TextField();
			Button go = new Button("=>");
			
			
			box.getChildren().addAll(rel, home, back, forward, field, go);
			HBox.setHgrow(field, Priority.ALWAYS);
			
			field.setOnKeyPressed((e) -> {
				if(e.getCode() == KeyCode.ENTER) {
					String urll = field.getText();
					webe.load(urll);
				}
			});
			
			{
				// action event
				EventHandler<ActionEvent> goevent = new EventHandler<ActionEvent>() {
					/**
					 *
					 */
					public void handle(ActionEvent e)
					{
						String url = field.getText();
						webe.load(url);
					}
				};
	        
				go.setOnAction(goevent);
	        
			}
			
			{
				// action event
				EventHandler<ActionEvent> relevent = new EventHandler<ActionEvent>() {
					/**
					 *
					 */
					public void handle(ActionEvent e)
					{
						webe.reload();
					}
				};
	        
				rel.setOnAction(relevent);
	        
			}
			
			{
				// action event
				EventHandler<ActionEvent> backevent = new EventHandler<ActionEvent>() {
					/**
					 *
					 */
					public void handle(ActionEvent e)
					{
						webe.executeScript("history.back()");
					}
				};
	        
				back.setOnAction(backevent);
	        
			}
			
			{
				// action event
				EventHandler<ActionEvent> forwardevent = new EventHandler<ActionEvent>() {
					/**
					 *
					 */
					public void handle(ActionEvent e)
					{
						webe.executeScript("history.forward()");
					}
				};
	        
				forward.setOnAction(forwardevent);
	        
			}
			
			{
				EventHandler<ActionEvent> homeevent = new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						// TODO Auto-generated method stub
						webe.load(homeURL);
					}
					
				};
				
				home.setOnAction(homeevent);
			}
			
			getChildren().add(box);
		}
		
		webe.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {

			@Override
			public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
				// TODO Auto-generated method stub
				if(newValue == Worker.State.SUCCEEDED) {
					field.setText(webe.getLocation());
				}
				else if(newValue == Worker.State.FAILED) {
					Dialog<Void> alert = new Dialog<>();
			        alert.getDialogPane().setContentText("There seems to be a problem with this web browser.");
			        alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
			        alert.showAndWait();
				}
				
			}
			
		});
		
		// webe.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.82 Safari/537.36");
		// webe.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.82 Safari/537.36 Edge/99.0.1150.36");
		webe.setUserAgent("MXPSQL.BKMTMEdit.Reusable-and-Builtin-WebBrowser/1.0.0 (Windows NT; Win64; x64) yeee JavaFX/19 Safari/613.1 on Java/17 . Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko)");
		getChildren().add(webb);
	}
	
	public WebEngine getEngine() {
		return webe;
	}
	
	public WebkitWebBrowser() {
		commonInit("https://google.com");
	}
	
	public WebkitWebBrowser(String url) {
		commonInit(url);
	}
}
