/*
 * Database.java
 * This file is part of HandyGeocaching.
 *
 * HandyGeocaching is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * (read more at: http://www.gnu.org/licenses/gpl.html)
 */
package gui;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.StringItem;

/**
 * Slouzi k zobrazeni okna informujici o nacitani nejakych dat.
 * @author Arcao
 */
public class LoadingForm extends Form {
    private final Display display;
    private final Displayable next;    
    private final Alert nextAlert;
    private boolean isSetFinish;
    
    /** Creates a new instance of LoadingForm */
    public LoadingForm(Display display, String title, String message, Displayable next, Alert nextAlert) {
        super(title);
        this.nextAlert = nextAlert;
        this.display = display;
        this.next = next;
        
        isSetFinish = false;
        
        //Bug v Gauge u N5800 - nepouzivat Gauge
        //append(new Gauge(message, false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING));
        append(new StringItem(message, null));
    }
    
    public void show() {
        System.out.println(this.display.getCurrent().toString());
        System.out.println(next.toString());
        if (isSetFinish) {
            setFinish();
            return;
        }
        
        if (nextAlert != null) {
            this.display.setCurrent(nextAlert, this);
        } else {
            this.display.setCurrent(this);
        }
    }
           
    
    public void setFinish() {
        new Thread(new Runnable() {
            public void run() {
                System.out.println("finish");
                isSetFinish = true;

                while(!isShown()) Thread.yield();

                if (nextAlert != null && nextAlert.isShown()) {
                    display.setCurrent(nextAlert, next);
                } else {
                    display.setCurrent(next);
                }
            }
        }).start();
    }
    
}
