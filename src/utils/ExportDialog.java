/*
 * ExportDialog.java
 *
 * Created on 24. leden 2011, 14:58
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package utils;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;

/**
 *
 * @author msloup
 */
public class ExportDialog extends Form implements CommandListener, ItemCommandListener {
    public static final Command OK = new Command("Ok", Command.OK, 0);
    public static final Command CANCEL = new Command("Storno", Command.CANCEL, 1);
    protected static final Command BROWSE = new Command("Procházet...", Command.ITEM, 1);
    
    private Display display;
    private CommandListener listener = null;
    
    protected TextField pathField;
    protected TextField fileField;
    protected StringItem browseButton;
    
    protected FileBrowser fileBrowser;
    
    protected String fileName = null;
    
    /** Creates a new instance of ExportDialog */
    public ExportDialog(Display display) {
        super("");

        this.display = display;        
        super.setCommandListener(this);
        
        pathField = new TextField("Cesta:", null, 256, TextField.UNEDITABLE);
        fileField = new TextField("Soubor:", null, 256, TextField.ANY);
        
        browseButton = new StringItem(null, BROWSE.getLabel(), Item.BUTTON);
        browseButton.setLayout(Item.LAYOUT_RIGHT);
        browseButton.setDefaultCommand(BROWSE);
        
        append(pathField);
        append(fileField);
        append(browseButton);
        
        addCommand(OK);
        addCommand(CANCEL);
    }

    public void setCommandListener(CommandListener l) {
        listener = l;
    }
    
    public void show() {
        display.setCurrent(this);
    }
    

    public void commandAction(Command command, Displayable displayable) {
        if (displayable == fileBrowser && command == FileBrowser.OK) {
            String fileName = fileBrowser.getFileName();
            if (fileBrowser.isDirectorySelected()) {
                pathField.setString(fileName);
            } else {
                pathField.setString(fileName.substring(0, fileName.lastIndexOf('/') - 1));
                fileField.setString(fileName.substring(fileName.lastIndexOf('/') + 1));
            }
        } else if (command == BROWSE) {
            fileBrowser = new FileBrowser(display, this);
            fileBrowser.setDirectorySelectionAllowed(true);
            fileBrowser.setTitle(getTitle());
        } else if (command == OK) {
            
        }
    }

    public void commandAction(Command command, Item item) {
        commandAction(command, this);
    }
    
}
