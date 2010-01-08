/*
 * IconLoader.java
 *
 * Created on 15. říjen 2007, 18:19
 */

package gui;

import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import utils.ImageCache;

/**
 * Tato třída primárně zjišťuje rozlišení obrazovky. To je potřeba při variabilním načítání ikonek pro každé rozlišení jinou.
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
    public Image loadIcon(String name) {
        return loadIcon(name, true);
    }
    
    public Image loadIcon(String name, boolean sizable)
    {
        Image img = null;
        try
        {
            if (sizable) {
                img = ImageCache.get("/icons"+iconSize+"/"+name+".png");
            } else {
                img = ImageCache.get("/images/"+name+".png");
            }
        }
        catch (Exception e)
        {
            gui.showError("loadIcon",e.toString(),this.getWidth()+"x"+this.getWidth());
        }
        return img;
    }
}
