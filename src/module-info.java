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

// BKMTMEdit module-info lmao
// for JavaFX
// bork bork bork
module MXPSQL.BKMTMEdit {
	// java
	requires java.base;
	requires transitive java.sql;
	requires transitive java.net.http;
	requires transitive java.scripting;
	
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
	// requires transitive org.slf4j.simple;
	
	// jetty
	requires transitive org.eclipse.jetty.http;
	requires transitive org.eclipse.jetty.util;
	requires transitive org.eclipse.jetty.server;
	
	// scripting languages yeh!
	// 
	// beanshell
	requires transitive bsh;
	// groovy yay!
	requires transitive org.codehaus.groovy;
	// rhino
	requires transitive org.mozilla.rhino;
	requires transitive org.mozilla.rhino.engine;
	// jruby?
	requires transitive org.jruby.complete;
	
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
	requires transitive java.semver;
	
	// flatlaf
	requires transitive com.formdev.flatlaf;
	
	// radiance
	// requires transitive org.pushingpixels.radiance.common;
	requires transitive org.pushingpixels.radiance.theming;
	requires transitive org.pushingpixels.radiance.theming.extras;
	
	// term
	// requires transitive com.googlecode.lanterna;	
	
	// export
	exports MXPSQL.BKMTMEdit.reusable;
	exports MXPSQL.BKMTMEdit.pluginapi;
	exports MXPSQL.BKMTMEdit.reusable.utils;
	exports MXPSQL.BKMTMEdit.reusable.widgets;
	exports MXPSQL.BKMTMEdit.reusable.filefilters;
	
	// uses
	uses MXPSQL.BKMTMEdit.pluginapi.BKMTMEditTabPlugin;
}
