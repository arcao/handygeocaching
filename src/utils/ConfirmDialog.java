/*
 * ConfirmDialog.java
 *
 * Created on 26. srpen 2009, 10:38
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

/**
 *
 * @author Arcao
 */
public class ConfirmDialog implements CommandListener {
    private Display display;
    private Alert alert;
    private Command commandYes = new Command("Ano", Command.OK, 0);
    private Command commandNo = new Command("Ne", Command.CANCEL, 0);
    private Runnable actionYes = null;
    private Runnable actionNo = null;
    private Displayable actionYesDisplayable = null;
    private Displayable actionNoDisplayable = null;
    
    /** Creates a new instance of ConfirmDialog */
    public ConfirmDialog(Display display, String title, String text) {
        this.display = display;
        alert = new Alert(title,text,null,AlertType.CONFIRMATION);
        alert.addCommand(commandNo);
        alert.addCommand(commandYes);
        
        alert.setCommandListener(this);
    }
    
    public void setActionYes(Runnable action) {
        actionYes = action;
    }
    
    public void setActionNo(Runnable action) {
        actionNo = action;
    }
    
    public void setActionYesDisplayable(Displayable action) {
        actionYesDisplayable = action;
    }
    
    public void setActionNoDisplayable(Displayable action) {
        actionNoDisplayable = action;
    }
    
    public void show() {
        display.setCurrent(alert);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command == commandYes) {
            if (actionYes != null)
                actionYes.run();
            if (actionYesDisplayable != null)
                display.setCurrent(actionYesDisplayable);
        } else if (command == commandNo) {
            if (actionNo != null)
                actionNo.run();
            if (actionNoDisplayable != null)
                display.setCurrent(actionNoDisplayable);
        }
    }
    
}
