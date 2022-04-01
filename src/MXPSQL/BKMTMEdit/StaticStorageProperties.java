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

import java.util.*;
import org.slf4j.*;
import java.io.File;
import javax.swing.*;
import java.nio.file.*;
import java.util.HashMap;
import java.net.http.HttpClient;
import org.eclipse.jetty.server.Server;
import javax.swing.filechooser.FileFilter;
import org.apache.commons.configuration2.*;
import com.github.zafarkhaja.semver.Version;
import MXPSQL.BKMTMEdit.reusable.filefilters.*;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class StaticStorageProperties {
	// runtime
	public static final Version version = Version.valueOf("1.1.1");
	public static final int abortExit = 1;
	public static final int goodExit = 0;
	public static final int parserExit = -1;
	public static final int badExit = -2;
	public static final String source = "https://github.com/MXP2095onetechguy/BKMTMEdit";
	
	// the,e
	public static final String defaultTheme = "metal";
	public static String theme = defaultTheme;
	public static File synthloc = null;
	public static File intellijson = null;
	
	// editors
	public static Map<String, String> bshMacros = new HashMap<String, String>();
	public static Map<String, String> rhinoJSMacros = new HashMap<String, String>();
	public static boolean remimdMeAboutMacroSafety = true;
	public static List<File> startingFiles = null;
	public static FileFilter[] filt = new FileFilter[] {
			new TxtSaveFilters(),
			new CSVSaveFilters(),
			new HTMLSaveFilters()
	};
	public static String home_page = "https://google.com";
	public static HashMap<String, ImmutablePair<String[], String>> syntaxset = new HashMap<String, ImmutablePair<String[], String>>();
	
	// Plugins
	public static Object pluginManager;
	public static Map<String, String> bshPlugins = new HashMap<String, String>();
	
	// Path
	public static Path cwdPath = Paths.get(System.getProperty("user.dir"));
	public static String cwd = cwdPath.toAbsolutePath().toString();
	public static String JarPath;
	public static String superJarPath;
	
	// netjetty9
	public static HttpClient hclient = HttpClient.newBuilder().build();
	public static Server server;
	public static int vport = 0;
	public static String editorUrl = "https://microsoft.github.io/monaco-editor/playground.html";
	
	
	// args
	public static Namespace ns;
	
	// logging
	public static final Logger logger = LoggerFactory.getLogger(TMMain.class);
	public static boolean logoff = false;
	
	// config
	public static Configuration config;
	public static final String DeafultConfigFileLocation = "BKMTMEdit.properties";
	
	
	
	// swingui
	public static JFrame window;
	public static final String baseTitle = "BKMTMEdit (Better Kate-Middleton The MXPSQL Editor)";
	public static final int[] winsize = new int[] {860, 680};
	
	private StaticStorageProperties() {
		;
	}
}
