/*
 * LoadingForm.java
 *
 * Created on 25. červenec 2009, 18:05
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gui;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;

/**
 *
 * @author Administrator
 */
public class LoadingForm extends Form {
    private Display display;
    private Displayable next;    
    private boolean isSetFinish;
    
    /** Creates a new instance of LoadingForm */
    public LoadingForm(Display display, String title, String message, Displayable next) {
        super(title);
        this.display = display;
        this.next = next;
        
        isSetFinish = false;
        
        append(new Gauge(message, false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING));
    }
    
    public void show() {
        System.out.println(this.display.getCurrent().toString());
        System.out.println(next.toString());
        if (isSetFinish) {
            setFinish();
            return;
        }
        if (this.display.getCurrent() instanceof Alert) {
            this.display.setCurrent((Alert) this.display.getCurrent(), this);
        } else {
            this.display.setCurrent(this);
        }
    }
           
    
    public void setFinish() {
        System.out.println("finish");
        isSetFinish = true;
        if (this.display.getCurrent() instanceof Alert) {
            this.display.setCurrent((Alert) this.display.getCurrent(), next);
        } else {
            this.display.setCurrent(next);
        }
    }
    
}
