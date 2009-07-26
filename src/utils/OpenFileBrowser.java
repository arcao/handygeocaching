package utils;

import java.util.*;  
import java.io.*;  
import javax.microedition.io.*;  
import javax.microedition.io.file.*;  
import javax.microedition.midlet.*;  
import javax.microedition.lcdui.*;  
   
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
    
    private Object tag;

    public static boolean isApiAvailable() {
        return System.getProperty("microedition.io.file.FileConnection.version") != null;
    }

    
    public OpenFileBrowser(Display display, CommandListener listener) {
        super("", List.IMPLICIT);
        
        this.listener = listener;
        this.display = display;
        currDirName = MEGA_ROOT;

        setCommandListener(this);
        setSelectCommand(OPEN);  
        addCommand(BACK);
        addCommand(CANCEL);
    }
    
    public boolean open(Displayable nextScreen) {
        if (!isApiAvailable())
            return false;
        
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
                if (!openDirectory(currFile))
                    display.setCurrent(backScreen);
                list();
            } else {  
                fileName = "file:///" + currDirName + currFile;
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
        new Thread(new Runnable() {
            public void run() {
                Enumeration e;
                try {
                    deleteAll();
                    if (MEGA_ROOT.equals(currDirName)) {
                        e = FileSystemRegistry.listRoots();
                    } else {
                        System.out.println("connector");
                        System.out.println("path: " + "file:///" + currDirName);
                        if (currDir == null) {
                            currDir = (FileConnection)Connector.open("file:///" + currDirName, Connector.READ);
                        } else {
                            currDir.setFileConnection("file:///" + currDirName);
                        }
                        e = currDir.list();
                        append(UP_DIRECTORY,null);
                    }  
                    while (e.hasMoreElements()) {
                        System.out.println("list");
                        String fileName = (String)e.nextElement();
                        System.out.println("fileName:"+fileName+" char_at:"+fileName.charAt(fileName.length()-1));

                        if (fileName.charAt(fileName.length()-1) == SEP) {
                            append(fileName,null);  
                        } else {  
                            System.out.println("h4");  
                            append(fileName,null);  
                        }  
                    }
                    if (currDir != null) {
                        currDir.close();
                        currDir = null;
                    }
                } catch (IOException ioe) {  
                    System.out.println(ioe);  
                }
            }  
        }).start();
    }
    
    private boolean openDirectory(String fileName) {  
        System.out.println("fileName:"+fileName+"cur_dir:"+currDirName+"mega_root:"+MEGA_ROOT);  
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
