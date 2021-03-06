/*
 * FieldNotesExport.java
 *
 * Created on 19. leden 2011, 12:46
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package utils;

import database.FieldNotes;
import gui.Gui;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

/**
 *
 * @author msloup
 */
public class FieldNotesExport extends Form implements CommandListener {
    protected Display display;
    
    public static final Command SUCCESS = new Command("SUCCESS", Command.OK, 0);
    public static final Command CANCEL = new Command("Storno", Command.BACK, 0);

    private CommandListener listener = null;
    private ExportDialog dialog;
    
    public FieldNotesExport(Display display) {
        super("Export Field notes");
        
        this.display = display;
        dialog = new ExportDialog(display);
        dialog.setCommandListener(this);
        
        append("Exportuji Field Notes...");
        
        addCommand(CANCEL);
        setCommandListener(this);
    }
        
    public void save(final String fileName) {
        System.out.println("Displaying FieldNotesExport");
        display.setCurrent(this);
        
        final Displayable that = this;
        Thread t = new Thread() {
            public void run() {
                FileConnection fc = null;
                OutputStream os = null;
                try {
                    System.out.println(fileName);
                    fc = (FileConnection) Connector.open(fileName);
                    if (fc.exists()) {
                        fc.delete();
                    }
                    fc.create();
                    os = fc.openOutputStream();
                    save(os);
                    if (listener != null)
                        listener.commandAction(SUCCESS, that);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Gui.getInstance().showError("Field Notes Export", ex.toString(), "Nastala chyba při exportu Field notes. Prosím zašlete chybové hlášení na arcao@arcao.com");
                } finally {
                    IOUtils.silentClose(os);
                    IOUtils.silentClose(fc);
                }
                
            }
        };
        t.start();
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command == CANCEL) {
           if (listener != null)
               listener.commandAction(CANCEL, this);
       }
    }

    private void save(OutputStream outputStream) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
        writer.write(FieldNotes.getInstance().getFieldNotes());
        writer.close();  
    }
    
    public CommandListener getListener() {
        return listener;
    }

    public void setListener(CommandListener listener) {
        this.listener = listener;
    }

}
