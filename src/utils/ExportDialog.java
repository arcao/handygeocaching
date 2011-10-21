/*
 * ExportDialog.java
 *
 * Created on 24. leden 2011, 14:58
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package utils;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
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
        
        browseButton = new StringItem(null, BROWSE.getLabel(), StringItem.BUTTON);
        browseButton.setLayout(Item.LAYOUT_RIGHT);
        browseButton.setDefaultCommand(BROWSE);
        browseButton.setItemCommandListener(this);
        
        append(pathField);
        append(fileField);
        append(browseButton);
        
        addCommand(OK);
        addCommand(CANCEL);
        addCommand(BROWSE);
    }

    public void setCommandListener(CommandListener l) {
        listener = l;
    }
    
    public void show() {
        System.out.println("Displaying Export Dialog");
        display.setCurrent(this);
    }
    

    public void commandAction(Command command, Displayable displayable) {
        if (displayable == fileBrowser && command == FileBrowser.OK) {
            String fileName = fileBrowser.getFileName();
            if (fileBrowser.isDirectorySelected()) {
                pathField.setString(fileName);
            } else {
                pathField.setString(fileName.substring(0, fileName.lastIndexOf('/') + 1));
                fileField.setString(fileName.substring(fileName.lastIndexOf('/') + 1));
            }
            display.setCurrent(this);
        } else if (command == BROWSE) {
            fileBrowser = new FileBrowser(display, this);
            fileBrowser.setDirectorySelectionAllowed(true);
            fileBrowser.setTitle(getTitle());
            fileBrowser.show();
        } else if (command == OK || command == CANCEL) {
            if (command == OK && (fileField.getString().length() == 0 || pathField.getString().length() == 0)) {
                Alert a = new Alert("Chyba", "Jméno souboru nebo cesta musí být vyplněna!", null, AlertType.ERROR);
                display.setCurrent(a, this);
                return;
            }
            
            if (listener != null)
                listener.commandAction(command, displayable);
        }
    }
    
    public String getFileName() {
        return pathField.getString() + fileField.getString();
    }

    public void commandAction(Command command, Item item) {
        if (item == browseButton) {
            commandAction(BROWSE, this);
        }
    }
    
}
