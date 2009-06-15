/*
 * IconLoader.java
 *
 * Created on 15. øíjen 2007, 18:19
 */

package gui;

import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * Tato tøída primárnì zjišuje rozlišení obrazovky. To je potøeba pøi variabilním naèítání ikonek pro kadé rozlišení jinou.
 * @author David Vavra
 */
public class IconLoader extends Canvas
{
    private int screenResolution;
    private Gui gui;
    private String iconSize = "12";
    
    /**
     * Rozhodne, jakemu rozliseni odpovida jaka ikona
     */
    public IconLoader(Gui ref)
    {
        gui = ref;
        screenResolution = this.getWidth()*this.getHeight();
        if (screenResolution <= (140*140))
            iconSize = "12";
        else
            iconSize = "21";
        //iconSize = "12";
    }
    
    public int getIconSize()
    {
        return Integer.parseInt(iconSize);
    }
    
    public void paint(Graphics g)
    {
    }
    
    /**
     * Tato metoda zajisti dynamicke loadovani ikon podle rozliseni telefonu
     */
    public Image loadIcon(String name)
    {
        Image img = null;
        try
        {
            img = Image.createImage("/icons"+iconSize+"/"+name+".png");
        }
        catch (Exception e)
        {
            gui.showError("loadIcon",e.toString(),this.getWidth()+"x"+this.getWidth());
        }
        return img;
    }
}
