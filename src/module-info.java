// BKMTMEdit module-info lmao
// for JavaFX
// bork bork bork
module MXPSQL.BKMTMEdit {
	// java
	requires java.base;
	requires transitive java.sql;
	requires transitive java.net.http;
	
	// swing and awt
	requires transitive java.desktop;
	
	// javafx
	requires transitive javafx.web;
	requires transitive javafx.base;
	requires transitive javafx.swing;
	requires transitive javafx.media;
	requires transitive javafx.graphics;
	requires transitive javafx.controls;
	
	// argparse4j
	requires transitive net.sourceforge.argparse4j;
	
	// apache commons
	requires transitive org.apache.commons.io;
	requires transitive org.apache.commons.lang3;
	requires transitive org.apache.commons.configuration2;
	
	// slf4j
	requires transitive org.slf4j;
	requires transitive org.slf4j.simple;
	
	// jetty
	requires transitive org.eclipse.jetty.http;
	requires transitive org.eclipse.jetty.util;
	requires transitive org.eclipse.jetty.server;
	
	// beanshell
	requires transitive bsh;
	
	// zip4j
	requires transitive zip4j;
	
	// pf4j
	// broken rn
	// your best bet is poorly written beanshell plugins
	// the jar plugin system is broken
	// requires transitive org.pf4j;
	
	// rsyntaxtextarea
	requires transitive rsyntaxtextarea;
	requires transitive rstaui;
	
	// swt
	requires transitive org.eclipse.swt;
	
	// terminalfx
	// requires transitive terminalfx;
	
	// semver
	requires java.semver;
	
	// export
	exports MXPSQL.BKMTMEdit.utils;
	exports MXPSQL.BKMTMEdit.widgets;
	exports MXPSQL.BKMTMEdit.pluginapi;
	
	// uses
	uses MXPSQL.BKMTMEdit.pluginapi.BKMTMEditTabPlugin;
}
