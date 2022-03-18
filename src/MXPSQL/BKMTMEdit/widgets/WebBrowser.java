package MXPSQL.BKMTMEdit.widgets;

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

import java.util.Optional;
import javafx.scene.web.*;
import javafx.scene.Scene;
import javafx.util.Callback;
import javafx.embed.swing.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.layout.StackPane;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;

public class WebBrowser extends JFXPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Scene scene;
	WebView web;
	
	
	private void commonInit() {
		web = new WebView();
        getEngine().setOnAlert(event -> showAlert(event.getData()));
        getEngine().setConfirmHandler(message -> showConfirm(message));
        getEngine().setOnError(event -> {
        	Alert dl = new Alert(AlertType.ERROR);
        	dl.setTitle("Web Error!");
        	
        	dl.setContentText(event.getMessage());
        	// dl.showAndWait();
        });
        
        // reused from https://github.com/MXP2095onetechguy/JEXTEdit/blob/master/src/app/MXPSQL/JEXTEdit/JEmbeddedWeb.java#L132 and modified
		getEngine().setPromptHandler(new Callback<PromptData, String>(){

			@Override
			public String call(PromptData param) {
				// TODO Auto-generated method stub
				String results = "";
				
				TextInputDialog td = new TextInputDialog(param.getDefaultValue());
				td.setHeaderText(param.getMessage());
				
				Optional<String> res = td.showAndWait();
				
				if(res.isPresent()) {
					results = res.get();
				}
				else {
					if(param.getDefaultValue() != null) {
						results = param.getDefaultValue();
					}
					else {
						results = null;
					}
				}
				
				return results;
			}
			
		});
		StackPane v = new StackPane(web);
		scene = new Scene(v);
		setScene(scene);
	}
	
    private void showAlert(String message) {
        Dialog<Void> alert = new Dialog<>();
        alert.getDialogPane().setContentText(message);
        alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
        alert.showAndWait();
    }

    private boolean showConfirm(String message) {
        Dialog<ButtonType> confirm = new Dialog<>();
        confirm.getDialogPane().setContentText(message);
        confirm.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
        boolean result = confirm.showAndWait().filter(ButtonType.YES::equals).isPresent();

        return result ;
    }
	
	
	public WebBrowser() {
		commonInit();
	}
	
	public WebBrowser(String url) {
		commonInit();
		getEngine().load(url);
	}
	
	public WebEngine getEngine() {
		return web.getEngine();
	}
}
