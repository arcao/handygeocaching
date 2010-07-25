/*
 * IconLoader.java
 * This file is part of HandyGeocaching.
 *
 * HandyGeocaching is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * (read more at: http://www.gnu.org/licenses/gpl.html)
 */
package gui;

import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import utils.ImageCache;

/**
 * Tato trida primarne zjistuje rozliseni obrazovky. To je potreba pri variabilnim nacitani ikonek pro kazde rozliseni jinou.
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
                img = ImageCache.get("/icons"+iconSize+"/"+name+".png", null);
            } else {
                img = ImageCache.get("/images/"+name+".png", null);
            }
        }
        catch (Exception e)
        {
            gui.showError("loadIcon",e.toString(),this.getWidth()+"x"+this.getWidth());
        }
        return img;
    }
    
    public Image loadCacheIcon(String name)
    {
        Image img = null;
        try
        {
            img = ImageCache.get("/icons"+iconSize+"/"+name+".png", "/icons"+iconSize+"/gc_unknown.png");
        }
        catch (Exception e)
        {
            gui.showError("loadIcon",e.toString(),this.getWidth()+"x"+this.getWidth());
        }
        return img;
    }
    
}
