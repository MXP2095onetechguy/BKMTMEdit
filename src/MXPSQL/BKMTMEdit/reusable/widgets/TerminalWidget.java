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

import java.io.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.text.*;

public class TerminalWidget extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ConsolePane cp = new ConsolePane();
	public TerminalWidget() {
		setLayout(new BorderLayout());
		add(new JScrollPane(cp), BorderLayout.CENTER);
    }
	
    public interface CommandListener {

        public void commandOutput(String text);
        
        public void commandStderr(String text);

        public void commandCompleted(String cmd, int result);

        public void commandFailed(Exception exp);
    }

    public class ConsolePane extends JPanel implements CommandListener, Terminal {

        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JTextArea textArea;
        private int userInputStart = 0;
        private Command cmd;

        public ConsolePane() {

            cmd = new Command(this);

            setLayout(new BorderLayout());
            textArea = new JTextArea(20, 30);
            ((AbstractDocument) textArea.getDocument()).setDocumentFilter(new ProtectedDocumentFilter(this));
            add(new JScrollPane(textArea));
            
            ActionMap am = textArea.getActionMap();
            Action oldAction = am.get("insert-break");
            am.put("insert-break", new AbstractAction() {
                /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
                public void actionPerformed(ActionEvent e) {
                    int range = textArea.getCaretPosition() - userInputStart;
                    try {
                        String text = textArea.getText(userInputStart, range).trim();
                        userInputStart += range;
                        if (!cmd.isRunning()) {
                            cmd.execute(text);
                        } else {
                            try {
                                cmd.send(text + "\n");
                            } catch (IOException ex) {
                                appendText("!! Failed to send command to process: " + ex.getMessage() + "\n");
                            }
                        }
                    } catch (BadLocationException ex) {
                    }
                    oldAction.actionPerformed(e);
                }
            });

        }

        @Override
        public void commandOutput(String text) {
            SwingUtilities.invokeLater(new AppendTask(this, text));
        }

        @Override
        public void commandFailed(Exception exp) {
            SwingUtilities.invokeLater(new AppendTask(this, "Command failed - " + exp.getMessage()));
        }
        
        @Override
        public void commandStderr(String text) {
        	SwingUtilities.invokeLater(new AppendTask(this, text));
        }

        @Override
        public void commandCompleted(String cmd, int result) {
            appendText("\n> " + cmd + " exited with " + result + "\n");
            appendText("\n");
        }

        protected void updateUserInputPos() {
            int pos = textArea.getCaretPosition();
            textArea.setCaretPosition(textArea.getText().length());
            userInputStart = pos;

        }

        @Override
        public int getUserInputStart() {
            return userInputStart;
        }

        @Override
        public void appendText(String text) {
            textArea.append(text);
            updateUserInputPos();
        }
    }

    public interface UserInput {

        public int getUserInputStart();
    }

    public interface Terminal extends UserInput {
        public void appendText(String text);
    }

    public class AppendTask implements Runnable {

        private Terminal terminal;
        private String text;

        public AppendTask(Terminal textArea, String text) {
            this.terminal = textArea;
            this.text = text;
        }

        @Override
        public void run() {
            terminal.appendText(text);
        }
    }

    public class Command {

        private CommandListener listener;
        private ProcessRunner runner;

        public Command(CommandListener listener) {
            this.listener = listener;
        }

        public boolean isRunning() {

            return runner != null && runner.isAlive();

        }

        public void execute(String cmd) {

            if (!cmd.trim().isEmpty()) {

                java.util.List<String> values = new ArrayList<>(25);
                if (cmd.contains("\"")) {

                    while (cmd.contains("\"")) {

                        String start = cmd.substring(0, cmd.indexOf("\""));
                        cmd = cmd.substring(start.length());
                        String quote = cmd.substring(cmd.indexOf("\"") + 1);
                        cmd = cmd.substring(cmd.indexOf("\"") + 1);
                        quote = quote.substring(0, cmd.indexOf("\""));
                        cmd = cmd.substring(cmd.indexOf("\"") + 1);

                        if (!start.trim().isEmpty()) {
                            String parts[] = start.trim().split(" ");
                            values.addAll(Arrays.asList(parts));
                        }
                        values.add(quote.trim());

                    }

                    if (!cmd.trim().isEmpty()) {
                        String parts[] = cmd.trim().split(" ");
                        values.addAll(Arrays.asList(parts));
                    }

                } else {

                    if (!cmd.trim().isEmpty()) {
                        String parts[] = cmd.trim().split(" ");
                        values.addAll(Arrays.asList(parts));
                    }

                }

                runner = new ProcessRunner(listener, values);

            }

        }

        public void send(String cmd) throws IOException {
            runner.write(cmd);
        }
    }

    public class ProcessRunner extends Thread {

        private java.util.List<String> cmds;
        private CommandListener listener;

        private Process process;

        public ProcessRunner(CommandListener listener, java.util.List<String> cmds) {
            this.cmds = cmds;
            this.listener = listener;
            start();
        }

        @Override
        public void run() {
            try {
                ProcessBuilder pb = new ProcessBuilder(cmds);
                pb.redirectErrorStream();
                process = pb.start();
                StderrStreamGobbler stdg = new StderrStreamGobbler(process.getErrorStream(), listener);
                StreamReader reader = new StreamReader(listener, process.getInputStream());
                // Need a stream writer...

                int result = process.waitFor();

                // Terminate the stream writer
                reader.join();

                StringJoiner sj = new StringJoiner(" ");
                cmds.stream().forEach((cmd) -> {
                    sj.add(cmd);
                });

                listener.commandCompleted(sj.toString(), result);
            } catch (Exception exp) {
                exp.printStackTrace();
                listener.commandFailed(exp);
            }
        }

        public void write(String text) throws IOException {
            if (process != null && process.isAlive()) {
                process.getOutputStream().write(text.getBytes());
                process.getOutputStream().flush();
            }
        }
    }

    public class StreamReader extends Thread {

        private InputStream is;
        private CommandListener listener;

        public StreamReader(CommandListener listener, InputStream is) {
            this.is = is;
            this.listener = listener;
            start();
        }

        @Override
        public void run() {
            try {
                int value = -1;
                while ((value = is.read()) != -1) {
                    listener.commandOutput(Character.toString((char) value));
                }
            } catch (IOException exp) {
                exp.printStackTrace();
            }
        }
    }

    public class ProtectedDocumentFilter extends DocumentFilter {

        private UserInput userInput;

        public ProtectedDocumentFilter(UserInput userInput) {
            this.userInput = userInput;
        }

        public UserInput getUserInput() {
            return userInput;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (offset >= getUserInput().getUserInputStart()) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            if (offset >= getUserInput().getUserInputStart()) {
                super.remove(fb, offset, length); //To change body of generated methods, choose Tools | Templates.
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (offset >= getUserInput().getUserInputStart()) {
                super.replace(fb, offset, length, text, attrs); //To change body of generated methods, choose Tools | Templates.
            }
        }
    }
    
    public class StderrStreamGobbler extends Thread {
        InputStream is;
        CommandListener lis;

        private StderrStreamGobbler(InputStream is, CommandListener lis) {
            this.is = is;
            this.lis = lis;
            start();
        }

        @Override
        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                while ((line = br.readLine()) != null)
                	lis.commandStderr(line);
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
