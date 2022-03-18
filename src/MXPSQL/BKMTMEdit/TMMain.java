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
import javax.swing.*;
import java.nio.charset.*;
import net.lingala.zip4j.*;
import java.nio.file.Paths;
import java.net.ServerSocket;
import javax.swing.UIManager.*;
import org.apache.commons.io.*;
import org.apache.commons.lang3.*;
import org.eclipse.jetty.server.*;
import javafx.embed.swing.JFXPanel;
import MXPSQL.BKMTMEdit.utils.Argv0;
import MXPSQL.BKMTMEdit.utils.NetUtil;
import MXPSQL.BKMTMEdit.utils.ResourceGet;
import java.util.concurrent.CountDownLatch;
import javax.swing.plaf.synth.SynthLookAndFeel;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.ArgumentParsers;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.apache.commons.configuration2.builder.fluent.*;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import org.apache.commons.configuration2.ex.ConversionException;
import org.apache.commons.configuration2.ex.ConfigurationException;



public class TMMain {
	static boolean mainRunOOM = false;
	static boolean causeOOM = false;
	
	// reused from https://github.com/MXP2095onetechguy/JEXTEdit/blob/master/src/app/MXPSQL/JEXTEdit/JEXTMain.java unmodified
	// check path type
	public static boolean isDirectory(String path) {
	    return path !=null && new File(path).isDirectory();
	}
	
	// reused from https://github.com/MXP2095onetechguy/JEXTEdit/blob/master/src/app/MXPSQL/JEXTEdit/JEXTMain.java#L347 modified
	public static boolean unzipJar() {
		try {
			try {
				StaticStorageProperties.superJarPath = new File(TMMain.class
				          .getProtectionDomain()
				          .getCodeSource()
				          .getLocation()
				          .toURI()
				          .getPath()).toString();
				
			}
			catch(NullPointerException noe) {
				StaticStorageProperties.superJarPath = StaticStorageProperties.JarPath;
			}
			
			
			/* if(!new File("META-INF").exists()) {
				new File("META-INF").mkdir();
			}
			
			if(!new File("org").exists()) {
				new File("org").mkdir();
			} */
			
			File sawtdir = new File("MXPSQL");
			if(!sawtdir.exists()) {
				sawtdir.mkdir();
			}
			
			try {
				ZipFile jarred = new ZipFile(StaticStorageProperties.superJarPath);
				// unzipy.unzip(superJarPath, cwd);
				jarred.extractFile("MXPSQL/", Paths.get(StaticStorageProperties.cwd).toString());
				jarred.close();
			}
			catch(java.nio.file.InvalidPathException jniof) {
				
			}
			
			return true;
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static void oomCauser() throws InterruptedException {
		if(!causeOOM)
			return;
		
		int arrSize = 15;  
		long memoryConsumed = 0;  
        long[] memoryAllocated = null;    
        for (int loop = 0; loop < Integer.MAX_VALUE; loop++) {  
            memoryAllocated = new long[arrSize];  
            memoryAllocated[0] = 0;  
            memoryConsumed += arrSize * Long.SIZE;  
            System.out.println("Memory Consumed: " + memoryConsumed);  
            arrSize *= arrSize * 2;  
            Thread.sleep(500);  
        }
	}
	
	public static void makeConfig(File f) throws FileNotFoundException {
		PrintStream ps = new PrintStream(f);
		
		if(ps.checkError()) {
			System.out.println("No, config failed, use alt+tab to see the dialog");
			JOptionPane.showMessageDialog(null, "It seems that we have a problem here. I cannot write the file, so no.", "Failure", JOptionPane.ERROR_MESSAGE);
			System.exit(StaticStorageProperties.badExit);
		}
		
		ps.println("# " + StaticStorageProperties.baseTitle + ", this is the config file.");
        ps.println("# LOLOLOLOL");
		ps.println("version=" + String.valueOf(StaticStorageProperties.version));
		ps.println("# editor related");
		ps.println("# editor themes option: platform, metal, nimbus and synth");
		ps.println("# Platform is broken lol");
		ps.println("theme=metal");
		ps.println("synthfile=");
		ps.println();
		ps.println("# Web browser related");
		ps.println("# Home page!");
		ps.println("homepage=https://google.com");
		ps.println();
		ps.println("# Beanshell macros");
		ps.println("# There cannot be any '#' in the filename because that is used as a separator");
		ps.println("# Example: preloadbshmacros=s1.bsh#s2.bsh");
		ps.println("# The macro interpreter has the Editor variable, use that variable to set and get text.");
		ps.println("preloadbshmacros=");
		ps.println("# Macro Security");
		ps.println("remindmeaboutmacrosafety=true");
		
		ps.close();
	}
	
	public static void main(String[] args) {
		ArgumentParser parser = ArgumentParsers.newFor(StaticStorageProperties.baseTitle).addHelp(true).build().version(String.valueOf(StaticStorageProperties.version)).description("Good Java Text Editor, with syntax highlighting.").epilog("Ook, noice. That's it. That's all folks! The end.");
		
		{
			parser.addArgument("-v", "--version").action(Arguments.version()).help("It's obvious what does it mean on the flag. If you still don't get it, it is used to get the version");
			parser.addArgument("dndf").dest("dndf").help("Drag and drop into this thing if it is possible").type(String.class).nargs("?");
			parser.addArgument("-l", "--logoff").dest("turnofflog").help("turn off those pesky but useful log messages").required(false).action(Arguments.storeTrue());
			parser.addArgument("-c", "--config").dest("configPath").help("Path to config").type(String.class).required(false);
			parser.addArgument("-p", "--homepage").dest("homepage").help("Your homepage!").type(String.class).required(false);
			parser.addArgument("-d", "--pwd").dest("cwd").help("Current directory").type(String.class).required(false);
		}
		
		try {
			StaticStorageProperties.ns = parser.parseArgs(args);
		}
		catch(ArgumentParserException apex) {
			parser.handleError(apex);
			System.exit(StaticStorageProperties.parserExit);
		}
		
		if(SystemUtils.IS_OS_AIX || SystemUtils.IS_OS_SOLARIS) {
			JOptionPane.showMessageDialog(null, "Pls, no. I do not support AIX and Solaris");
			Runtime.getRuntime().halt(StaticStorageProperties.abortExit);
		}
		
		if(StaticStorageProperties.ns.getString("dndf") != null) {
			StaticStorageProperties.startingFile = new File(StaticStorageProperties.ns.getString("dndf"));
			
			if(!StaticStorageProperties.startingFile.isFile()) {
				System.err.println(StaticStorageProperties.startingFile + " is not a valid path to a file!");
				System.exit(StaticStorageProperties.badExit);
			}
			
			try {
				StaticStorageProperties.startingFile = StaticStorageProperties.startingFile.getCanonicalFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				StaticStorageProperties.startingFile = null;
			}
		}
		
		if(StaticStorageProperties.ns.getString("cwd") != null){
			File pcdir = new File(StaticStorageProperties.ns.getString("cwd"));
			
			if(!pcdir.isDirectory()) {
				System.err.println(pcdir + " is not a valid path to a directory!");
				System.exit(StaticStorageProperties.badExit);
			}
			
			String beforecanonical = System.getProperty("user.dir");
			
			try {
				System.setProperty("user.dir", pcdir.getCanonicalPath());
				StaticStorageProperties.cwd = pcdir.getCanonicalPath();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.setProperty("user.dir", beforecanonical);
				StaticStorageProperties.cwd = beforecanonical;
			}
			
			StaticStorageProperties.cwdPath = Paths.get(StaticStorageProperties.cwd);
		}
		
		StaticStorageProperties.logoff = StaticStorageProperties.ns.get("turnofflog");
		
		// reused from https://github.com/MXP2095onetechguy/JEXTEdit/blob/master/src/app/MXPSQL/JEXTEdit/JEXTMain.java#L499 unmodified
        // sun.awt.xembedserver workaround, run if Linux
        if(SystemUtils.IS_OS_LINUX) {
        	System.setProperty("sun.awt.xembedserver", "true");
        	assert System.getProperty("sun.awt.xembedserver") == "true";
        }
		
		if(GraphicsEnvironment.isHeadless() || !Desktop.isDesktopSupported()) {
			if(!StaticStorageProperties.logoff)
				StaticStorageProperties.logger.error("Unsupported Environment! There is no desktop.");
			System.exit(StaticStorageProperties.badExit);
		}
		
		try {
			String floc = StaticStorageProperties.DeafultConfigFileLocation;
			
			if(StaticStorageProperties.ns.getString("configPath") != null){
				floc = StaticStorageProperties.ns.getString("configPath");
			}
			
			File f = new File(floc);
			
			if(!f.exists()) {
				System.out.println("I got you a dialog, check it out using alt+tab");
				JOptionPane.showMessageDialog(null, "You don't have a config. let me make one.");
				f.createNewFile();
				
				makeConfig(f);
				
				System.out.println("Done, check the new dialog using alt+tab");
				JOptionPane.showMessageDialog(null, "OK, jobs done.");
				
				System.exit(StaticStorageProperties.goodExit);
			}
			
			Configurations configs = new Configurations();
			StaticStorageProperties.config = configs.properties(f);
			
			double confVer = 0;
			try {
				confVer = StaticStorageProperties.config.getDouble("version");
			}
			catch(ConversionException cex){
				System.out.println("No, smth is wrong with your config version, it's not a number!");
				System.exit(StaticStorageProperties.badExit);
			}
			
			StaticStorageProperties.remimdMeAboutMacroSafety = (StaticStorageProperties.config.getString("remindmeaboutmacrosafety", "true").equals("true"));
			
			if(StaticStorageProperties.version > confVer) {
				System.out.println("Outdated idot. press alt+tab to get the dialog");
				JOptionPane.showMessageDialog(null, "Too old of a config, I cannot run with this, update your config.");
				System.exit(StaticStorageProperties.badExit);
			}
			
			{
				String[] macroName = StaticStorageProperties.config.getString("preloadbshmacros").split("#");
				if(macroName.length > 0) {				
					for(int i = 0; i < macroName.length; i++) {
						File ff = new File(macroName[i]);
						if(ff.isFile()) {
							String code = "";
						
							code = FileUtils.readFileToString(ff, StandardCharsets.UTF_8);
						
							StaticStorageProperties.bshMacros.put(ff.getCanonicalPath(), code);
						}
						else {
							System.out.println("A macro check failed! Press alt+tab to see a dialog box");
							JOptionPane.showMessageDialog(null, "Failed to read macro " + macroName[i] + ".", "Macro Error", JOptionPane.ERROR_MESSAGE);
							System.exit(StaticStorageProperties.badExit);
						}
					}
				}
			}
		}
		catch(ConfigurationException | IOException mex) {
			JOptionPane.showMessageDialog(null, mex, "Configuration Is Bad", JOptionPane.ERROR_MESSAGE);
			System.exit(StaticStorageProperties.badExit);
		}
        
        try {
			StaticStorageProperties.JarPath = Argv0.Get(TMMain.class).getPath().toString();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			// e1.printStackTrace();
			StaticStorageProperties.JarPath = StaticStorageProperties.cwdPath.resolve(System.getProperty("java.class.path")).toString();
		}
        
        /* if(!isDirectory(StaticStorageProperties.JarPath)) {
        	if(!new File( Paths.get(StaticStorageProperties.cwd).resolve("MXPSQL").resolve("BKMTMEdit").resolve("Resource").resolve("unused").resolve("toestrap.jpg").toString() ).exists()) {
        		JOptionPane.showMessageDialog(null, "Please wait while we extract our resources. It will begin once you close this dialog. Once we are done extracting, we will show the window.");
            	if(!unzipJar()) {
            		System.err.println("Error occured while unzipping assets");
            		System.exit(0);
            	}
        	}
        } */
		
		{
			if(StaticStorageProperties.ns.getString("homepage") != null) {
				if(!NetUtil.isValidUrl(StaticStorageProperties.ns.getString("homepage"))) {
					System.err.println("Invalid URL from args!");
					System.exit(StaticStorageProperties.badExit);
				}
				else {
					StaticStorageProperties.home_page = StaticStorageProperties.ns.getString("homepage");
				}
			}
			else {
				if(StaticStorageProperties.config.getString("homepage") != null && StaticStorageProperties.config.getString("homepage") != "") {
					if(!NetUtil.isValidUrl(StaticStorageProperties.config.getString("homepage"))) {
						System.err.println("Invalid URL from config!");
						System.exit(StaticStorageProperties.badExit);
					}
					else {
						StaticStorageProperties.home_page = StaticStorageProperties.config.getString("homepage");
					}
				}
			}
		}
		
		CountDownLatch cdl = new CountDownLatch(1);
		
		/* {
			File f = StaticStorageProperties.cwdPath.resolve("src").resolve("MXPSQL").resolve("BKMTMEdit").resolve("Resource").resolve("bkmtmedit.png").toFile();
			
			if(f.exists()) {
				
				StaticStorageProperties.window.setIconImage( new ImageIcon( StaticStorageProperties.cwdPath.resolve("src").resolve("MXPSQL").resolve("BKMTMEdit").resolve("Resource").resolve("bkmtmedit.png").toFile().toString() ).getImage() );
			}
			else {
				f = StaticStorageProperties.cwdPath.resolve("MXPSQL").resolve("BKMTMEdit").resolve("Resource").resolve("bkmtmedit.png").toFile();
				if(f.exists())
					StaticStorageProperties.window.setIconImage( new ImageIcon( f.toString() ).getImage() );
			}
		} */
		
		{
			StaticStorageProperties.syntaxset.put("asm6502", new ImmutablePair<String[], String>(new String[]{"s6", "asm6"}, SyntaxConstants.SYNTAX_STYLE_ASSEMBLER_6502));
			StaticStorageProperties.syntaxset.put("asm86", new ImmutablePair<String[], String>(new String[]{"s", "asm"}, SyntaxConstants.SYNTAX_STYLE_ASSEMBLER_X86));
			
			StaticStorageProperties.syntaxset.put("c", new ImmutablePair<String[], String>(new String[] {"c", "h"}, SyntaxConstants.SYNTAX_STYLE_C) );
			StaticStorageProperties.syntaxset.put("c++", new ImmutablePair<String[], String>(new String[] {"C", "cc", "cxx", "cpp", "c++", "h", "hh", "hpp", "h++", "hxx"}, SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS));
			StaticStorageProperties.syntaxset.put("cs", new ImmutablePair<String[], String>(new String[] {"cs", "csx"}, SyntaxConstants.SYNTAX_STYLE_CSHARP));
			StaticStorageProperties.syntaxset.put("java", new ImmutablePair<String[], String>(new String[] {"java"}, SyntaxConstants.SYNTAX_STYLE_JAVA) );
			
			StaticStorageProperties.syntaxset.put("css", new ImmutablePair<String[], String>(new String[] {"css"}, SyntaxConstants.SYNTAX_STYLE_CSS));
			StaticStorageProperties.syntaxset.put("html", new ImmutablePair<String[], String>(new String[] {"html", "htm"}, SyntaxConstants.SYNTAX_STYLE_HTML));
			StaticStorageProperties.syntaxset.put("js", new ImmutablePair<String[], String>(new String[] {"js", "cjs", "mjs"}, SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT));
			
			StaticStorageProperties.syntaxset.put("batch", new ImmutablePair<String[], String>(new String[] {"bat", "cmd", "btm"}, SyntaxConstants.SYNTAX_STYLE_WINDOWS_BATCH));
			StaticStorageProperties.syntaxset.put("unix-shell", new ImmutablePair<String[], String>(new String[] {"sh", "bash", "zsh"}, SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL));
			
			StaticStorageProperties.syntaxset.put("xml", new ImmutablePair<String[], String>(new String[] {"xml"}, SyntaxConstants.SYNTAX_STYLE_XML));
			StaticStorageProperties.syntaxset.put("json", new ImmutablePair<String[], String>(new String[] {"json"}, SyntaxConstants.SYNTAX_STYLE_JSON));
			StaticStorageProperties.syntaxset.put("json5", new ImmutablePair<String[], String>(new String[] {"json5"}, SyntaxConstants.SYNTAX_STYLE_JSON_WITH_COMMENTS));
			StaticStorageProperties.syntaxset.put("ini", new ImmutablePair<String[], String>(new String[] {"ini"}, SyntaxConstants.SYNTAX_STYLE_INI));
			StaticStorageProperties.syntaxset.put("properties", new ImmutablePair<String[], String>(new String[] {"properties"}, SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE));
			
			StaticStorageProperties.syntaxset.put("lua", new ImmutablePair<String[], String>(new String[] {"lua"}, SyntaxConstants.SYNTAX_STYLE_LUA));
			StaticStorageProperties.syntaxset.put("python", new ImmutablePair<String[], String>(new String[] {"ipy", "py", "pyc", "pyw"}, SyntaxConstants.SYNTAX_STYLE_PYTHON));
			StaticStorageProperties.syntaxset.put("ruby", new ImmutablePair<String[], String>(new String[] {"rb"}, SyntaxConstants.SYNTAX_STYLE_RUBY));
			StaticStorageProperties.syntaxset.put("tcl", new ImmutablePair<String[], String>(new String[] {"tcl", "tbc"}, SyntaxConstants.SYNTAX_STYLE_TCL));
			StaticStorageProperties.syntaxset.put("perl", new ImmutablePair<String[], String>(new String[] {"plx", "pl", "pm", "xs", "t", "pod", "cgi"}, SyntaxConstants.SYNTAX_STYLE_PERL));
		}
		
		if(!StaticStorageProperties.logoff)
			StaticStorageProperties.logger.debug("Starting Up");
		
		StaticStorageProperties.tabproviders.stream().forEach((e) -> {
			StaticStorageProperties.loadedTabPlugins.add(e);
		});
		
		try {
			
			SwingUtilities.invokeAndWait(() -> {
				try{
					{
						StaticStorageProperties.window = new TMFM(StaticStorageProperties.baseTitle, () -> {
							cdl.countDown();
						});

						// StaticStorageProperties.window.setResizable(false);
						StaticStorageProperties.window.setMinimumSize(new Dimension(StaticStorageProperties.winsize[0], StaticStorageProperties.winsize[1]));
						StaticStorageProperties.window.setSize(StaticStorageProperties.winsize[0], StaticStorageProperties.winsize[1]);
						StaticStorageProperties.window.setLocationRelativeTo(null);

						StaticStorageProperties.window.setIconImage(new ImageIcon(ResourceGet.getURL(TMMain.class, "bkmtmedit.png")).getImage());
					}

					{
						StaticStorageProperties.theme = StaticStorageProperties.config.getString("theme");

						try {
			    	        // Set cross-platform Java L&F (also called "Metal")
							String synthl = StaticStorageProperties.config.getString("synthfile");
							if(!(synthl == null || synthl.isBlank() || synthl.isEmpty()))
								StaticStorageProperties.synthloc = new File(synthl);

							switch(StaticStorageProperties.theme){
								case "platform":
									// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
									// Will not change due to an error
									JOptionPane.showMessageDialog(StaticStorageProperties.window, "There seems to be a problem with this theme that prevents it from working, please change to another theme next time", "Platform", JOptionPane.ERROR_MESSAGE);
									break;
								case "metal":
									UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
									break;
								case "nimbus":
								    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
								        if ("Nimbus".equals(info.getName())) {
								        	UIManager.setLookAndFeel(info.getClassName());
								            break;
								        }
								    }
									break;
								case "synth": {
										SynthLookAndFeel lookAndFeel = new SynthLookAndFeel();
										if(StaticStorageProperties.synthloc.exists() && StaticStorageProperties.synthloc.isFile()) {
											try(InputStream stream = new FileInputStream(StaticStorageProperties.synthloc)){
												lookAndFeel.load(stream, StaticStorageProperties.window.getClass());
												UIManager.setLookAndFeel(lookAndFeel);
											}
										}
										else {
											JOptionPane.showMessageDialog(StaticStorageProperties.window, "File " + StaticStorageProperties.synthloc.getPath().toString() + " does not exist, reverting to default theme.", "No", JOptionPane.ERROR_MESSAGE);
										}

										break;
									}
								default:
									break;
							}
						}
						catch (Exception e){
							e.printStackTrace();
						}
					}

					{
						try {

							ServerSocket sock = new ServerSocket(0);
							assert sock != null;
							assert sock.getLocalPort() > 0;

							StaticStorageProperties.vport = sock.getLocalPort();
							if(!sock.isClosed())
								sock.close();

							if(!StaticStorageProperties.logoff)
								StaticStorageProperties.logger.info("Found port at " + StaticStorageProperties.vport);

							if(!StaticStorageProperties.logoff)
								StaticStorageProperties.logger.info("Grrol the browser at http://localhost:" + StaticStorageProperties.vport);
						}
						catch(IOException ioe) {
							if(!StaticStorageProperties.logoff)
								StaticStorageProperties.logger.error("Failure at finding available port");
								ioe.printStackTrace();
							System.exit(StaticStorageProperties.badExit);
						}

						StaticStorageProperties.server = new Server(StaticStorageProperties.vport);
						ResourceHandler resourceHandler = new ResourceHandler();
						resourceHandler.setDirAllowed(true);
						resourceHandler.setResourceBase(".");
						StaticStorageProperties.server.setHandler(resourceHandler);
						try {
							StaticStorageProperties.server.start();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							if(!StaticStorageProperties.logoff)
								StaticStorageProperties.logger.error("Failure at running webserver");
								e.printStackTrace();
							System.exit(StaticStorageProperties.badExit);
						}
					}
					
					if(mainRunOOM) {
						try {
							oomCauser();
						}
						catch(InterruptedException ie) {
							;
						}
					}
					
					{
						PrintStream eps = System.err;
						System.setErr(new PrintStream(new ByteArrayOutputStream()));
						new JFXPanel();
						System.setErr(eps);
					}


					StaticStorageProperties.editorUrl = "http://localhost:" + StaticStorageProperties.vport + "/src/MXPSQL/BKMTMEdit/WebEditor/index.html";
					if(!StaticStorageProperties.logoff)
						StaticStorageProperties.logger.debug("Running BKMTMEdit");

					StaticStorageProperties.window.setVisible(true);
				}
				catch(OutOfMemoryError oome){
					throw oome;
				}
				catch(Exception e) {
					throw e;
				}
			});
			cdl.await();
		} catch(AssertionError no) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos);
			no.printStackTrace(ps);
			String msg = baos.toString();
			try {
				baos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ps.close();
			System.err.println(msg);
			JOptionPane.showMessageDialog(null, msg, "Assertion Failure during program runtime!", JOptionPane.ERROR_MESSAGE);
			StaticStorageProperties.logger.info("Program has detected a problem, halt and catch on fire");
			// Runtime.getRuntime().halt(StaticStorageProperties.abortExit);
			System.exit(StaticStorageProperties.badExit);
		}catch(OutOfMemoryError oom) {
			JOptionPane.showMessageDialog(null, "It seems that " + StaticStorageProperties.baseTitle + " has ran out of memory. \n" + StaticStorageProperties.baseTitle + " will now terminate for your safety.", "Out of Memory (OOM)", JOptionPane.ERROR_MESSAGE);
			StaticStorageProperties.logger.info("Program has ran out of memory, halt and catch on fire.");
			Runtime.getRuntime().halt(StaticStorageProperties.abortExit);
		} catch (InvocationTargetException | InterruptedException e) {
			// TODO Auto-generated catch block
			System.exit(StaticStorageProperties.badExit);
		}
		
		System.exit(StaticStorageProperties.goodExit);
	}
}
