/*
 * FileChooser.java
 *
 * Created on 4. èervenec 2009, 10:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package utils;

import javax.microedition.lcdui.*;
import java.io.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import javax.microedition.io.*;
import javax.microedition.io.file.*;
import javax.microedition.io.file.FileConnection;
/**
 *
 * @author Arcao
 */
public class FileChooser extends List implements CommandListener, FileSystemListener {
      //FileSelection fileSelection;
      private Display display;
      // define the file separator
      private final static String FILE_SEPARATOR =(System.getProperty("file.separator") != null) ? System.getProperty("file.separator") : "/";
      private Command open = new Command("Open", Command.OK, 1);
      private String errorMsg = null;
      private Alert alert;
      private Vector rootsList = new Vector();
      private final static String upper_dir = "..";
      private FileConnection currentRoot = null;
      private FileConnection fconn = null;
      private static final int CHUNK_SIZE = 1024;

      FileChooser(/*FileSelection fileSelection*/) {
            super("File Browser", List.IMPLICIT);
            //this.fileSelection = fileSelection;
            deleteAll();
            addCommand(open);
            setSelectCommand(open);
            setCommandListener(this);
            //FileSystemRegistry.addFileSystemListener(FileSelector.this);
            execute();
      }

      public void execute() {
            String initDir = System.getProperty("fileconn.dir");
            loadRoots();
            if (initDir != null) {
                  try {
                        currentRoot = (FileConnection) Connector.open(initDir, Connector.READ);
                        displayCurrentRoot();
                  } catch (Exception e) {
                        displayAllRoots();
                  }
            } else {
                  displayAllRoots();
            }
      }

      private void loadRoots() {
            if (!rootsList.isEmpty()) {
                  rootsList.removeAllElements();
            }
            try {
                  Enumeration roots = FileSystemRegistry.listRoots();
                  while (roots.hasMoreElements()) {
                        rootsList.addElement(FILE_SEPARATOR + (String) roots.nextElement());
                  }
            } catch (Throwable e) {
            }
      }

      private void displayCurrentRoot() {
            try {
                  setTitle(currentRoot.getURL());
                  deleteAll();
                  append(upper_dir, null);
                  Enumeration listOfDirs = currentRoot.list("*", false);
                  while (listOfDirs.hasMoreElements()) {
                        String currentDir = (String) listOfDirs.nextElement();
                        if (currentDir.endsWith(FILE_SEPARATOR)) {
                              append(currentDir, null);
                        } else {
                              append(currentDir, null);
                        }
                  }

                  Enumeration listOfFiles = currentRoot.list("*.gpx",false);
                  while(listOfFiles.hasMoreElements()) {
                        String currentFile=(String) listOfFiles.nextElement();
                        if(currentFile.endsWith(FILE_SEPARATOR)) {
                              append(currentFile,null);
                        }
                        else {
                              append(currentFile,null);
                        }
                  }
            } catch (IOException e) {
            } catch (SecurityException e) {
            }
      }

      private void displayAllRoots() {
            setTitle("[Roots]");
            deleteAll();
            Enumeration roots = rootsList.elements();
            while (roots.hasMoreElements()) {
                  String root = (String) roots.nextElement();
            }
            currentRoot = null;
      }

      private void openSelected() {
            int selectedIndex = getSelectedIndex();
            if (selectedIndex >= 0) {
                  String selectedFile = getString(selectedIndex);
                  if (selectedFile.endsWith(FILE_SEPARATOR)) {
                        try {
                              if (currentRoot == null) {
                                    currentRoot = (FileConnection) Connector.open("file:///" + selectedFile, Connector.READ);
                              } else {
                                    currentRoot.setFileConnection(selectedFile);
                              }
                              displayCurrentRoot();
                        } catch (IOException e) {
                              System.out.println(e.getMessage());
                        } catch (SecurityException e) {
                              System.out.println(e.getMessage());
                        }
                  } else if (selectedFile.equals(upper_dir)) {
                        if (rootsList.contains(currentRoot.getPath() + currentRoot.getName())) {
                              displayAllRoots();
                        } else {
                              try {
                                    currentRoot = (FileConnection) Connector.open("file://" + currentRoot.getPath(), Connector.READ);
                                    displayCurrentRoot();
                              } catch (IOException e) {
                                    System.out.println(e.getMessage());
                              }
                        }
                  } else {
                        String url = currentRoot.getURL() + selectedFile;
                        //vybran soubor
                        //byteConvert(url, selectedFile);
                  }
            }
      }

      public void stop() {
            if (currentRoot != null) {
                  try {
                        currentRoot.close();
                  } catch (IOException e) {
                  }
            }
      }

      public void commandAction(Command c, Displayable d) {
            if (c == open) {
                  openSelected();
            }
      }

      public void rootChanged(int state, String rootNmae) {
      }
} 