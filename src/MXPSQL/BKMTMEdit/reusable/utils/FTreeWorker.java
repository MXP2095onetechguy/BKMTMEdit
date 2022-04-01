package MXPSQL.BKMTMEdit.reusable.utils;

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
import java.nio.file.*;
import java.io.IOException;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultTreeModel;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.BasicFileAttributes;
import static java.nio.file.StandardWatchEventKinds.*;

public class FTreeWorker extends SwingWorker<Void, Object> {
	DefaultTreeModel fmodel;
	Map<WatchKey,Path> keys;
	WatchService watcher;
	Path pwdir;
	
    @SuppressWarnings("unchecked")
	static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }
	
	void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                throws IOException
            {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
	
	void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE);
        keys.put(key, dir);
    }
	
	public FTreeWorker(DefaultTreeModel mod, Path pwdir) throws IOException {
		fmodel = mod;
		this.pwdir = pwdir;
		keys = new HashMap<WatchKey, Path>();
		
		try {
			watcher = FileSystems.getDefault().newWatchService();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			watcher = null;
		}
		
		if(watcher != null) {
			registerAll(pwdir);
		}
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		// TODO Auto-generated method stub
		if(watcher == null) {
			return null;
		}
		
		try {
			while(!isCancelled()) {
				WatchKey key;
				try {
				    key = pwdir.register(watcher,
				                           ENTRY_CREATE,
				                           ENTRY_DELETE);
				    
					for(;;) {
						
					    try {
					        key = watcher.take();
					    } catch (InterruptedException x) {
					    	break;
					    }

					    for (WatchEvent<?> event: key.pollEvents()) {
					        WatchEvent.Kind<?> kind = event.kind();

					        // This key is registered only
					        // for ENTRY_CREATE events,
					        // but an OVERFLOW event can
					        // occur regardless if events
					        // are lost or discarded.
					        if (kind == OVERFLOW) {
					            continue;
					        }
					        
					        Path dir = keys.get(key);
					        
					        if(dir == null) {
					        	continue;
					        }
					        
			                WatchEvent<Path> ev = cast(event);
			                Path name = ev.context();
			                Path child = dir.resolve(name);
			                
			                if(kind == ENTRY_CREATE) {
			                	try {
			                		if(Files.isDirectory(child, NOFOLLOW_LINKS)) {
			                			registerAll(child);
			                		}
			                	}
			                	catch(IOException ioe) {
			                		ioe.printStackTrace();
			                	}
			                }


					        // Verify that the new
					        //  file is a text file.
					        if(fmodel != null)
								fmodel.setRoot(FileTree.scan(pwdir.toFile()));
								fmodel.reload();
								
								
							if(isCancelled())
								break;
					    }
					    
					    if(isCancelled())
					    	break;

					    // Reset the key -- this step is critical if you want to
					    // receive further watch events.  If the key is no longer valid,
					    // the directory is inaccessible so exit the loop.
					    boolean valid = key.reset();
					    if (!valid) {
					        break;
					    }
					}
				} catch (IOException ioex) {
				    System.err.println(ioex);
				} catch(ArrayIndexOutOfBoundsException aioobex) {
					
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return null;
	}

}
