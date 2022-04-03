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
import java.util.ArrayList;
import net.lingala.zip4j.*;
import java.nio.file.Paths;
import com.formdev.flatlaf.*;
import java.net.ServerSocket;
import javax.swing.UIManager.*;
import org.apache.commons.io.*;
import org.apache.commons.lang3.*;
import org.eclipse.jetty.server.*;
import javafx.embed.swing.JFXPanel;
import MXPSQL.BKMTMEdit.reusable.utils.*;
import java.util.concurrent.CountDownLatch;
import com.github.zafarkhaja.semver.Version;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.synth.SynthLookAndFeel;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.ArgumentParsers;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.apache.commons.configuration2.builder.fluent.*;
import org.pushingpixels.radiance.theming.api.skin.SaharaSkin;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import org.apache.commons.configuration2.ex.ConversionException;
import org.pushingpixels.radiance.theming.api.skin.BusinessSkin;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.pushingpixels.radiance.theming.api.RadianceThemingCortex;
import org.pushingpixels.radiance.theming.api.skin.BusinessBlueSteelSkin;
import org.pushingpixels.radiance.theming.extras.api.skinpack.FindingNemoSkin;
import org.pushingpixels.radiance.theming.extras.api.skinpack.OfficeBlue2007Skin;
import org.pushingpixels.radiance.theming.extras.api.skinpack.OfficeBlack2007Skin;
import org.pushingpixels.radiance.theming.extras.api.skinpack.OfficeSilver2007Skin;


public class TMMain {
	static boolean mainRunOOM = false;
	static boolean causeOOM = false;
	
	static Throwable th = null;
	
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
        ps.println("# I recommend do not mess with this: bkmtmedit.version");
		ps.println("bkmtmedit.version=" + String.valueOf(StaticStorageProperties.version));
		ps.println("# editor related");
		ps.println("# Here are the themes");
		ps.println("# native swing themes option: platform, metal, steel, nimbus");
		ps.println("# flatlaf themes: flatlightlaf, flatdarklaf, flatintellijlaf, flatdarculalaf");
		ps.println("# radiance themes: business, businessblue, sahara, officesilver2007, officeblue2007, officeblack2007, findingnemo");
		ps.println("# special theme option: synth, intellijsynth");
		ps.println("theme.type=flatintellijlaf");
		ps.println("theme.synthxmlfile=");
		ps.println("theme.synthintellijjsonfile=");
		ps.println();
		ps.println("# Web browser related");
		ps.println("# Home page!");
		ps.println("homepage=https://google.com");
		ps.println();
		ps.println("# Extensions");
		ps.println("# Extend the editor with plugins and macros");
		ps.println("# For JAR plugins, no need here, pf4j will handle it");
		ps.println("# Btw, the filename is the id. BKTMEdit will not warn you if there are same ids, but that is impossible unless you use jar plugins, in that case , pf4j (bktmedit's plugin system) will warn you i think but it does not work");
		ps.println("# There cannot be any '#' in the filename because that is used as a separator");
		ps.println("# Beanshell plugins");
		ps.println("# Example: extension.loadbshplugins=p1.bsh#p2.bsh");
		ps.println("extension.loadbshplugins=");
		ps.println("# Beanshell macros");
		ps.println("# Example: extension.preloadbshmacros=s1.bsh#s2.bsh");
		ps.println("# The macro interpreter has the Editor variable, use that variable to set and get text.");
		ps.println("extension.preloadbshmacros=");
		ps.println("# Rhino Javascript Macros");
		ps.println("# Example: extension.preloadrjsmacros=s1.js#s2.js#s3.js");
		ps.println("# The macro interpreter has the Editor variable, the same with the beanshell macro api");
		ps.println("extension.preloadrjsmacros=");
		ps.println("# Groovy Macros");
		ps.println("# Example: extension.preloadgroovymacros=si.gsh#siu.gsh#se.gsh#groovy.gsh");
		ps.println("extension.preloadgroovymacros=");
		ps.println("# Macro Security");
		ps.println("extension.remindmeaboutmacrosafety=true");
		
		ps.close();
	}
	
	public static void main(String[] args) {
		ArgumentParser parser = ArgumentParsers.newFor(StaticStorageProperties.baseTitle)
		.addHelp(true).terminalWidthDetection(true).singleMetavar(true)
		.build().version(StaticStorageProperties.version.toString())
		.defaultHelp(true)
		.description("Good Java Text Editor, with syntax highlighting and open source, extensible too.")
		.epilog("Ook, noice. That's it. That's all folks! The end.");
		
		{
			parser.addArgument("-v", "--version").action(Arguments.version()).help("It's obvious what does it mean on the flag. If you still don't get it, it is used to get the version");
			parser.addArgument("dndf").dest("dndf").help("Drag and drop into this thing if it is possible").type(String.class).nargs("*");
			parser.addArgument("-l", "--logoff").dest("turnofflog").help("turn off those pesky but useful log messages").required(false).action(Arguments.storeTrue());
			parser.addArgument("-c", "--config").dest("configPath").help("Path to config").type(String.class).required(false);
			parser.addArgument("-p", "--homepage").dest("homepage").help("Your homepage!").type(String.class).required(false);
			parser.addArgument("--warnotpeace").dest("wnp").help("War not peace because peacenotwar has shown peace to be the enemy. #Warnotpeace #Nopeacenotwar and don't #peacenotwar").required(false).action(Arguments.storeTrue());
			parser.addArgument("-d", "--pwd").dest("cwd").help("Current directory").type(String.class).required(false);
		}
		
		if(GraphicsEnvironment.isHeadless() || !Desktop.isDesktopSupported()) {
			if(!StaticStorageProperties.logoff)
				StaticStorageProperties.logger.error("Unsupported Environment! There is no desktop.");
			System.exit(StaticStorageProperties.badExit);
		}
		
		if(SystemUtils.IS_OS_AIX || SystemUtils.IS_OS_SOLARIS) {
			StaticStorageProperties.logger.error("AIX and SOLARIS ain't supported.");
			JOptionPane.showMessageDialog(null, "Pls, no. I do not support AIX and Solaris");
			Runtime.getRuntime().halt(StaticStorageProperties.abortExit);
		}
		
		try {
			StaticStorageProperties.ns = parser.parseArgs(args);
		}
		catch(ArgumentParserException apex) {
			parser.handleError(apex);
			System.exit(StaticStorageProperties.parserExit);
		}
		
		if(!StaticStorageProperties.ns.getList("dndf").isEmpty()) {
			ArrayList<Object> fsl = (ArrayList<Object>) StaticStorageProperties.ns.getList("dndf");
			StaticStorageProperties.startingFiles = new ArrayList<File>();
			
			for(int i = 0; i < fsl.size(); i++) {
				if(!(fsl.get(i) instanceof String)) {
					continue;
				}
				
				File f = new File((String) fsl.get(i));
				if(!f.isFile()) {
					System.err.println(f + " is not a valid path to a file!");
					System.exit(StaticStorageProperties.badExit);
				}
				
				try {
					StaticStorageProperties.startingFiles.add(f.getCanonicalFile());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				}
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
		
		// JFrame.setDefaultLookAndFeelDecorated(true);
		
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
			
			String rawSemConfVer = "";
			try {
				rawSemConfVer = StaticStorageProperties.config.getString("bkmtmedit.version");
			}
			catch(ConversionException cex){
				StaticStorageProperties.logger.error("No, smth is wrong with your config version, it's not valid!");
				System.exit(StaticStorageProperties.badExit);
			}
			
			StaticStorageProperties.remimdMeAboutMacroSafety = (StaticStorageProperties.config.getString("extension.remindmeaboutmacrosafety", "true").equals("true"));
			
			if(rawSemConfVer != null) {
				if(rawSemConfVer.isBlank() && rawSemConfVer.isEmpty()) {
					StaticStorageProperties.logger.error("The semver is empty.");
					System.exit(StaticStorageProperties.badExit);
				}
			}
			else {
				StaticStorageProperties.logger.error("I tink you are missing the version in the config.");
				System.exit(StaticStorageProperties.badExit);
			}
			
			try{
				if(StaticStorageProperties.version.greaterThan(Version.valueOf(rawSemConfVer))) {
					StaticStorageProperties.logger.error("Outdated idot. press alt+tab to get the dialog, \nbtw your editor version is " + StaticStorageProperties.version.toString() + " and your editor version is " + Version.valueOf(rawSemConfVer) + ", see it's outdated");
					JOptionPane.showMessageDialog(null, "Too old of a config, I cannot run with this, update your config.");
					System.exit(StaticStorageProperties.badExit);
				}
			}
			catch(Exception e){
				StaticStorageProperties.logger.error("I don't think " + rawSemConfVer + " is a valid semantic versioning, I use it ok. Fix it ok. I cannot run with that version. \nbtw your editor version is " + StaticStorageProperties.version.toString() + " and your editor version is " + Version.valueOf(rawSemConfVer) + ", see it's outdated");
				System.exit(StaticStorageProperties.badExit);
			}
			
			{
				String bshMacroNameRaw = StaticStorageProperties.config.getString("extension.preloadbshmacros");
				if(bshMacroNameRaw != null && !bshMacroNameRaw.isEmpty() && !bshMacroNameRaw.isBlank()) {
					String[] bshMacroName = bshMacroNameRaw.split("#");
					if(bshMacroName.length > 0) {				
						for(int i = 0; i < bshMacroName.length; i++) {
							File ff = new File(bshMacroName[i]);
							if(ff.isFile()) {
								String code = "";
							
								code = FileUtils.readFileToString(ff, StandardCharsets.UTF_8);
							
								StaticStorageProperties.bshMacros.put(ff.getCanonicalPath(), code);
							}
							else {
								StaticStorageProperties.logger.error("A beanshell macro check failed! Press alt+tab to see a dialog box");
								JOptionPane.showMessageDialog(null, "Failed to read beanshell macro " + bshMacroName[i] + ".", "Macro Error", JOptionPane.ERROR_MESSAGE);
								System.exit(StaticStorageProperties.badExit);
							}
						}
					}
				}
			}
			
			{
				String rjsMacroNameRaw = StaticStorageProperties.config.getString("extension.preloadrjsmacros");
				if(rjsMacroNameRaw != null && !rjsMacroNameRaw.isEmpty() && !rjsMacroNameRaw.isBlank()) {
					String[] rjsMacroName = rjsMacroNameRaw.split("#");
					if(rjsMacroName.length > 0) {				
						for(int i = 0; i < rjsMacroName.length; i++) {
							File ff = new File(rjsMacroName[i]);
							if(ff.isFile()) {
								String code = "";
							
								code = FileUtils.readFileToString(ff, StandardCharsets.UTF_8);
							
								StaticStorageProperties.rhinoJSMacros.put(ff.getCanonicalPath(), code);
							}
							else {
								StaticStorageProperties.logger.error("A javascript macro check failed! Press alt+tab to see a dialog box");
								JOptionPane.showMessageDialog(null, "Failed to read javascript macro " + rjsMacroName[i] + ".", "Macro Error", JOptionPane.ERROR_MESSAGE);
								System.exit(StaticStorageProperties.badExit);
							}
						}
					}
				}
			}
			
			{
				String groovyMacroNameRaw = StaticStorageProperties.config.getString("extension.preloadgroovymacros");
				if(groovyMacroNameRaw != null && !groovyMacroNameRaw.isEmpty() && !groovyMacroNameRaw.isBlank()) {
					String[] groovyMacroName = groovyMacroNameRaw.split("#");
					if(groovyMacroName.length > 0) {				
						for(int i = 0; i < groovyMacroName.length; i++) {
							File ff = new File(groovyMacroName[i]);
							if(ff.isFile()) {
								String code = "";
							
								code = FileUtils.readFileToString(ff, StandardCharsets.UTF_8);
							
								StaticStorageProperties.groovyMacros.put(ff.getCanonicalPath(), code);
							}
							else {
								StaticStorageProperties.logger.error("A groovy macro check failed! Press alt+tab to see a dialog box");
								JOptionPane.showMessageDialog(null, "Failed to read Groovy macro " + groovyMacroName[i] + ".", "Macro Error", JOptionPane.ERROR_MESSAGE);
								System.exit(StaticStorageProperties.badExit);
							}
						}
					}
				}
			}
			
			{
				// Set the plugin dir
				System.setProperty("pf4j.pluginsDir", "BKMTMEdit-plugins");
				
				// load the plugins
				// tbd when pf4j works (it's broken with the JPMS I think)
			}
			
			{
				String bshPluginNameRaw = StaticStorageProperties.config.getString("extension.loadbshplugins");
				if(bshPluginNameRaw != null && !bshPluginNameRaw.isBlank() && !bshPluginNameRaw.isEmpty()) {
					String[] bshPluginName = bshPluginNameRaw.split("#");
					
					if(bshPluginName.length > 0) {
						for(int i = 0; i < bshPluginName.length; i++) {
							File ff = new File(bshPluginName[i]);
							
							if(ff.isFile()) {
								String code = "";
								
								code = FileUtils.readFileToString(ff, StandardCharsets.UTF_8);
								
								StaticStorageProperties.bshPlugins.put(ff.getCanonicalPath(), code);
							}
							else {
								StaticStorageProperties.logger.error("A plugin check failed! Press alt+tab to see a dialog box");
								JOptionPane.showMessageDialog(null, "Failed to read plugin " + bshPluginName[i] + ".", "Plugin Error", JOptionPane.ERROR_MESSAGE);
								System.exit(StaticStorageProperties.badExit);
							}
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
			StaticStorageProperties.syntaxset.put("asm6502", new ImmutablePair<String[], String>(null, SyntaxConstants.SYNTAX_STYLE_ASSEMBLER_6502));
			StaticStorageProperties.syntaxset.put("asm86", new ImmutablePair<String[], String>(new String[]{"s", "asm"}, SyntaxConstants.SYNTAX_STYLE_ASSEMBLER_X86));
			
			StaticStorageProperties.syntaxset.put("c", new ImmutablePair<String[], String>(new String[] {"c", "h", "i"}, SyntaxConstants.SYNTAX_STYLE_C) );
			StaticStorageProperties.syntaxset.put("c++", new ImmutablePair<String[], String>(new String[] {"C", "cc", "cxx", "cpp", "c++", "h", "hh", "hpp", "h++", "hxx", "in"}, SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS));
			StaticStorageProperties.syntaxset.put("cs", new ImmutablePair<String[], String>(new String[] {"cs", "csx"}, SyntaxConstants.SYNTAX_STYLE_CSHARP));
			StaticStorageProperties.syntaxset.put("java", new ImmutablePair<String[], String>(new String[] {"java"}, SyntaxConstants.SYNTAX_STYLE_JAVA) );
			
			StaticStorageProperties.syntaxset.put("css", new ImmutablePair<String[], String>(new String[] {"css"}, SyntaxConstants.SYNTAX_STYLE_CSS));
			StaticStorageProperties.syntaxset.put("html", new ImmutablePair<String[], String>(new String[] {"html", "htm"}, SyntaxConstants.SYNTAX_STYLE_HTML));
			StaticStorageProperties.syntaxset.put("js", new ImmutablePair<String[], String>(new String[] {"js", "cjs", "mjs"}, SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT));
			
			StaticStorageProperties.syntaxset.put("batch", new ImmutablePair<String[], String>(new String[] {"bat", "cmd", "btm"}, SyntaxConstants.SYNTAX_STYLE_WINDOWS_BATCH));
			StaticStorageProperties.syntaxset.put("unix-shell", new ImmutablePair<String[], String>(new String[] {"sh", "bash", "zsh"}, SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL));
			
			StaticStorageProperties.syntaxset.put("xml", new ImmutablePair<String[], String>(new String[] {"xml"}, SyntaxConstants.SYNTAX_STYLE_XML));
			StaticStorageProperties.syntaxset.put("json", new ImmutablePair<String[], String>(new String[] {"json"}, SyntaxConstants.SYNTAX_STYLE_JSON));
			StaticStorageProperties.syntaxset.put("json5", new ImmutablePair<String[], String>(null, SyntaxConstants.SYNTAX_STYLE_JSON_WITH_COMMENTS));
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
						StaticStorageProperties.theme = StaticStorageProperties.config.getString("theme.type");

						try {
			    	        // Set cross-platform Java L&F (also called "Metal") by default
							String synthl = StaticStorageProperties.config.getString("theme.synthxmlfile");
							String iltellijl = StaticStorageProperties.config.getString("theme.synthintellijjsonfile");
							if(!(synthl == null || synthl.isBlank() || synthl.isEmpty()))
								StaticStorageProperties.synthloc = new File(synthl);
							
							if(!(iltellijl == null || iltellijl.isBlank() || iltellijl.isEmpty()))
								StaticStorageProperties.intellijson = new File(iltellijl);

							switch(StaticStorageProperties.theme){
								case "platform":
									System.out.println(StaticStorageProperties.theme);
									UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
									// Will not change due to an error (FIXED now! YEH :D)
									// JOptionPane.showMessageDialog(StaticStorageProperties.window, "There seems to be a problem with this theme that prevents it from working, please change to another theme next time", "Platform", JOptionPane.ERROR_MESSAGE);
									break;
								case "metal":
									UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
									break;
								case "steel":
									UIManager.setLookAndFeel(new MetalLookAndFeel());
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
									if(StaticStorageProperties.synthloc != null && StaticStorageProperties.synthloc.exists() && StaticStorageProperties.synthloc.isFile()) {
										try(InputStream stream = new FileInputStream(StaticStorageProperties.synthloc.getCanonicalFile())){
											lookAndFeel.load(stream, StaticStorageProperties.window.getClass());
											UIManager.setLookAndFeel(lookAndFeel);
										}
									}
									else {
										JOptionPane.showMessageDialog(StaticStorageProperties.window, "Your synth .xml file does not exist, reverting to default theme.", "No synth.xml", JOptionPane.ERROR_MESSAGE);
									}

									break;
								}
								
								case "flatlightlaf": {
									UIManager.setLookAndFeel( new FlatLightLaf() );
									break;
								}
								case "flatdarklaf": {
									UIManager.setLookAndFeel( new FlatDarkLaf() );
									break;
								}
								case "flatintellijlaf": {
									UIManager.setLookAndFeel( new FlatIntelliJLaf() );
									break;
								}
								case "flatdarculalaf": {
									UIManager.setLookAndFeel( new FlatDarculaLaf() );
									break;
								}
								case "intellijsynth": {
									System.out.println(StaticStorageProperties.intellijson);
									if(StaticStorageProperties.intellijson != null) {
										if(StaticStorageProperties.intellijson.exists() && StaticStorageProperties.intellijson.isFile()) {
											try(InputStream stream = new FileInputStream(StaticStorageProperties.intellijson.getAbsoluteFile())){
												IntelliJTheme.setup(stream);
											}
										}
										else {
											JOptionPane.showMessageDialog(StaticStorageProperties.window, "Your intellij theme.json does not exist, reverting to default theme.", "No intellij .theme.json", JOptionPane.ERROR_MESSAGE);
										}
									}
									else {
										JOptionPane.showMessageDialog(StaticStorageProperties.window, "Your intellij .theme.json does not exist, reverting to default theme.", "No intellij .theme.json", JOptionPane.ERROR_MESSAGE);
									}
									break;
								}
								
								case "business": {
									RadianceThemingCortex.GlobalScope.setSkin(new BusinessSkin());
									break;
								}
								case "businessblue": {
									RadianceThemingCortex.GlobalScope.setSkin(new BusinessBlueSteelSkin());
									break;
								}
								case "sahara": {
									RadianceThemingCortex.GlobalScope.setSkin(new SaharaSkin());
									break;
								}
								case "officesilver2007": {
									RadianceThemingCortex.GlobalScope.setSkin(new OfficeSilver2007Skin());
									break;
								}
								case "officeblue2007": {
									RadianceThemingCortex.GlobalScope.setSkin(new OfficeBlue2007Skin());
									break;
								}
								case "officeblack2007": {
									RadianceThemingCortex.GlobalScope.setSkin(new OfficeBlack2007Skin());
									break;
								}
								case "findingnemo": {
									RadianceThemingCortex.GlobalScope.setSkin(new FindingNemoSkin());
									break;
								}
								
								default:
									UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
									break;
							}
						}
						catch (Exception e){
							e.printStackTrace();
						}
						/* This single and little line fixes the platform UI mode */ SwingUtilities.updateComponentTreeUI(StaticStorageProperties.window);
						StaticStorageProperties.window.pack();
						
						RadianceThemingCortex.GlobalScope.registerWidget(
							     "org.pushingpixels.radiance.theming.extras.api.tabbed.TabHoverPreviewWidget",
							     JTabbedPane.class, false);
						
						RadianceThemingCortex.GlobalScope.registerWidget(
							     "org.pushingpixels.radiance.theming.extras.api.tabbed.TabOverviewDialogWidget",
							     JTabbedPane.class, false);
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
					
					if(SystemTray.isSupported()) {
						try {
							StaticWidget.tray = SystemTray.getSystemTray();
							StaticWidget.trayicon = new TrayIcon(new ImageIcon(ResourceGet.getURL(TMMain.class, "bkmtmedit.png")).getImage(), "BKMTMEdit");
							StaticWidget.trayicon.setPopupMenu(StaticWidget.traypop);
							
							StaticWidget.tray.add(StaticWidget.trayicon);
						}
						catch(Exception e) {
							e.printStackTrace();
						}
					}
					
					if(mainRunOOM) {
						try {
							try{
								oomCauser();
							}
							catch(OutOfMemoryError oome) {
								// System.err.println("OOM");
								// Runtime.getRuntime().halt(StaticStorageProperties.abortExit);
								throw oome;
							}
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
					th = oome;
				}
				catch(Error err) {
					th = err;
				}
				catch(Exception e) {
					th = e;
				}
				catch(Throwable thr) {
					th = thr;
				}
			});
			
			if(th != null) {
				throw th;
			}
			
			cdl.await();	
		/* Basic stuff */	
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
			StaticWidget.trayicon.displayMessage("Assertion Failure", StaticStorageProperties.baseTitle, TrayIcon.MessageType.ERROR);
			StaticStorageProperties.logger.error("Program has detected a problem durring self-assertion, halt and catch on fire");
			JOptionPane.showMessageDialog(null, msg, "Assertion Failure during program runtime!", JOptionPane.ERROR_MESSAGE);
			// Runtime.getRuntime().halt(StaticStorageProperties.abortExit);
			System.exit(StaticStorageProperties.badExit);
		}
		/* No */
		catch(OutOfMemoryError oom) {
			// StaticWidget.trayicon.displayMessage("Out of Memory! I will ragequit.", StaticStorageProperties.baseTitle, TrayIcon.MessageType.ERROR);
			StaticStorageProperties.logger.error("Program has ran out of memory, halt and catch on fire.");
			JOptionPane.showMessageDialog(null, "It seems that " + StaticStorageProperties.baseTitle + " has ran out of memory. \n" + StaticStorageProperties.baseTitle + " will now terminate for your safety.", "Out of Memory (OOM)", JOptionPane.ERROR_MESSAGE);
			Runtime.getRuntime().halt(StaticStorageProperties.abortExit);
		} catch(StackOverflowError soe){
			StaticWidget.trayicon.displayMessage("A stack overflow has occured, ragequitting", StaticStorageProperties.baseTitle, TrayIcon.MessageType.ERROR);
			StaticStorageProperties.logger.error("Program has stAkCk 0Ve3rRFfflo0wed, halt and catch on fire.");
			JOptionPane.showMessageDialog(null, "It seems that " + StaticStorageProperties.baseTitle + " has stACk OverFLOWed. \n" + StaticStorageProperties.baseTitle + " will now terminate for your safety.", "Out of Memory (OOM)", JOptionPane.ERROR_MESSAGE);
			Runtime.getRuntime().halt(StaticStorageProperties.abortExit);
		} catch(VirtualMachineError vme) {
			StaticWidget.trayicon.displayMessage("I am going to ragequit as fast as I can because the JVM (Java Virtual Machine) is in a teribbly broken state", StaticStorageProperties.baseTitle, TrayIcon.MessageType.ERROR);
			StaticStorageProperties.logger.error("Program's JVM (Java Virtual Machine) has experienced an error, halt and catch on fire.");
			JOptionPane.showMessageDialog(null, "It seems that " + StaticStorageProperties.baseTitle + "'s JVM (Java Virtual Machine) has problems. \n" + StaticStorageProperties.baseTitle + " will now terminate for your safety.", "Out of Memory (OOM)", JOptionPane.ERROR_MESSAGE);
			Runtime.getRuntime().halt(StaticStorageProperties.abortExit);
		} 
		/* await */
		catch (InvocationTargetException | InterruptedException e) {
			// TODO Auto-generated catch block
			System.exit(StaticStorageProperties.badExit);
		} 
		/* Basic Error and Exception */
		catch(Error err) {
			StaticWidget.trayicon.displayMessage("A very serious error has occured", StaticStorageProperties.baseTitle, TrayIcon.MessageType.ERROR);
			StaticStorageProperties.logger.error("Program has experienced a very serious error, a stack trace will be printed and halt and catch on fire.");
			err.printStackTrace();
			JOptionPane.showMessageDialog(null, "It seems that " + StaticStorageProperties.baseTitle + " has experienced a serious error. \n" + StaticStorageProperties.baseTitle + " will now terminate for your safety.", "Out of Memory (OOM)", JOptionPane.ERROR_MESSAGE);
			Runtime.getRuntime().halt(StaticStorageProperties.abortExit);
		} catch(Exception e) {
			// StaticWidget.trayicon.displayMessage("An exception has occured", StaticStorageProperties.baseTitle, TrayIcon.MessageType.ERROR);
			StaticStorageProperties.logger.error("Program has experienced an exception, a stack trace will be printed and halt and catch on fire.");
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "It seems that " + StaticStorageProperties.baseTitle + " has experienced an exception. \n" + StaticStorageProperties.baseTitle + " will now terminate for your safety.", "Out of Memory (OOM)", JOptionPane.ERROR_MESSAGE);
			Runtime.getRuntime().halt(StaticStorageProperties.abortExit);
		}
		/* IDK because it's not Error and Exception */
		catch(Throwable t) {
			StaticWidget.trayicon.displayMessage("The program has experienced a problem ok.", StaticStorageProperties.baseTitle, TrayIcon.MessageType.ERROR);
			StaticStorageProperties.logger.error("Program has experienced an throwable (error/exception), halt and catch on fire.");
			JOptionPane.showMessageDialog(null, "It seems that " + StaticStorageProperties.baseTitle + " has experienced an unknown throwable (error/exception). \n" + StaticStorageProperties.baseTitle + " will now terminate for your safety.", "Out of Memory (OOM)", JOptionPane.ERROR_MESSAGE);
			Runtime.getRuntime().halt(StaticStorageProperties.abortExit);
		}
		
		System.exit(StaticStorageProperties.goodExit);
	}
}
