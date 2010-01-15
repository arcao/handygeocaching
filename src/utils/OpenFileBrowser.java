/*
 * OpenFileBrowser.java
 * This file is part of HandyGeocaching.
 *
 * HandyGeocaching is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * (read more at: http://www.gnu.org/licenses/gpl.html)
 */
package utils;

import gui.Gui;
import gui.LoadingForm;
import java.io.IOException;
import java.util.Enumeration;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;
import javax.microedition.io.file.IllegalModeException;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
   
/**
 * Slouzi k vybrani souboru v pameti mobilu.
 * @author Arcao
 */
public class OpenFileBrowser extends List implements CommandListener   
{  
    private String currDirName;
    private String fileName = "";
   
    private final static Command OPEN = new Command("Otevřít", Command.ITEM, 1);  
    private final static Command BACK = new Command("Zpět", Command.BACK, 2);
    
    public final static Command OK = OPEN;
    public final static Command CANCEL = new Command("Storno", Command.EXIT, 3);
   
    private final static String UP_DIRECTORY = "..";  
    private final static String MEGA_ROOT = "/";  
    private final static String SEP_STR = "/";  
    private final static char   SEP = '/'; 
    
    private Displayable backScreen = null;
    private Displayable nextScreen = null;
    private CommandListener listener;
    private Display display;
    private FileConnection currDir = null;
    
    private boolean buggyAPI = false; 
    
    private Object tag;

    public static boolean isApiAvailable() {
        return System.getProperty("microedition.io.file.FileConnection.version") != null;
    }

    
    public OpenFileBrowser(Display display, CommandListener listener) {
        super("Otevřít GPX..", List.IMPLICIT);
        
        this.listener = listener;
        this.display = display;
        currDirName = MEGA_ROOT;

        setCommandListener(this);
        setSelectCommand(OPEN);  
        addCommand(BACK);
        addCommand(CANCEL);
    }
    
    public boolean open(Displayable nextScreen) {
        if (!isApiAvailable()) {
            Gui.getInstance().showAlert("Telefon neumožňuje načítat soubory.", AlertType.ERROR, display.getCurrent());
            return false;
        }
        
        this.nextScreen = nextScreen;
        backScreen = display.getCurrent();
        display.setCurrent(this);
        list();
        return true;
    }
    
     public void commandAction(Command c, Displayable d) {  
        System.out.println("updir:"+UP_DIRECTORY);  
        if (c == OPEN) {  
            final String currFile = getString(getSelectedIndex());  
            System.out.println("currFile:"+currFile);  
            
            if (currFile.endsWith(SEP_STR) || currFile.equals(UP_DIRECTORY)) {  
                System.out.println("dirUP");
                if (!openDirectory(currFile)) {
                    if (backScreen != null)
                        display.setCurrent(backScreen);
                    if (listener != null)
                        listener.commandAction(CANCEL, this);
                    return;
                }
                list();
            } else {
                if (!buggyAPI) {
                    fileName = "file:///" + currDirName + currFile;
                } else {
                    fileName = "file://" + currDirName + currFile;
                }
                if (nextScreen != null)
                    display.setCurrent(nextScreen);
                if (listener != null)
                    listener.commandAction(OK, this);
            }                
        } else if (c == BACK) {
            if (!openDirectory(UP_DIRECTORY)) {
                if (backScreen != null)
                    display.setCurrent(backScreen);
                if (listener != null)
                    listener.commandAction(CANCEL, this);
                return;
            }
            list();
        } else if (c == CANCEL) {  
            if (backScreen != null)
                display.setCurrent(backScreen);
            if (listener != null)
                listener.commandAction(CANCEL, this);
        }  
    }
     
    private void list() {
        try {
        final LoadingForm lForm = new LoadingForm(display, "Načítám...", "Načítám seznam souborů...", this, null);
        lForm.show();
        
        final OpenFileBrowser that = this;
                
        new Thread(new Runnable() {
            public void run() {
                Enumeration e;
                try {
                    deleteAll();
                    if (MEGA_ROOT.equals(currDirName)) {
                        e = FileSystemRegistry.listRoots();
                        while (e.hasMoreElements()) {
                            append((String)e.nextElement(),null);
                        }
                    } else {
                        currDir = null;
                        
                        if (!buggyAPI)
                            currDir = (FileConnection)Connector.open("file:///" + currDirName, Connector.READ);
                        
                        if (currDir == null || !currDir.exists()) {
                            currDir = (FileConnection)Connector.open("file://" + currDirName, Connector.READ);
                            buggyAPI = true;
                        }
                        
                        append(UP_DIRECTORY,null);
                        
                        e = currDir.list();
                        while (e.hasMoreElements()) {
                            fileName = (String) e.nextElement();
                            if (fileName.length() > 1 && fileName.charAt(fileName.length()-1) == SEP)
                                append(fileName,null);  
                        }
                        
                        e = currDir.list();
                        while (e.hasMoreElements()) {
                            fileName = (String) e.nextElement();
                            if (fileName.length() >= 4 && fileName.substring(fileName.length() - 4).equalsIgnoreCase(".gpx"))
                                append(fileName,null);
                        }
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    Gui.getInstance().showError("OFBrowser_list_in", "IOException", ioe.toString());
                    return;
                } catch (SecurityException se) {
                    se.printStackTrace();
                    
                    if (backScreen != null)
                        display.setCurrent(backScreen);
                    if (listener != null)
                        listener.commandAction(CANCEL, that);
                    return;
                } catch (IllegalModeException ime) {
                    ime.printStackTrace();
                    
                    if (backScreen != null)
                        display.setCurrent(backScreen);
                    if (listener != null)
                        listener.commandAction(CANCEL, that);
                    return;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Gui.getInstance().showError("OFBrowser_list", "Exception", ex.toString());
                    return;
                } finally {
                    if (currDir != null) {
                        try { currDir.close(); } catch (Exception ex) { }
                        currDir = null;
                    }
                }    
                lForm.setFinish();
            }  
        }).start();
        } catch (Exception e) {
            Gui.getInstance().showError("OFBrowser_list_out", "Exception", e.toString());
        }
    }
    
    private boolean openDirectory(String fileName) {  
        System.out.println("fileName:"+fileName+" cur_dir:"+currDirName+" mega_root:"+MEGA_ROOT);  
        if (currDirName.equals(MEGA_ROOT)) {  
            if (fileName.equals(UP_DIRECTORY)) {  
                // can not go up from MEGA_ROOT  
                return false;
            }  
            currDirName = fileName;
        }   
        else if (fileName.equals(UP_DIRECTORY))   
        {  
            System.out.println("up");  
            int i = currDirName.lastIndexOf(SEP, currDirName.length()-2);  
            if (i != -1) {  
                currDirName = currDirName.substring(0, i+1);
            } else {  
                currDirName = MEGA_ROOT;  
            }
        } else {  
            currDirName = currDirName + fileName; 
        }  
        return true;
    }
    
    public Object getTag() {
        return tag;
    }
    
    public void setTag(Object tag) {
        this.tag = tag;
    }
    
    public Displayable getBackScreen() {
        return backScreen;
    }
    
    public Displayable getNextScreen() {
        return nextScreen;
    }
    
    public String getFileName() {
        return fileName;
    }
}   
