/*
 * References.java
 *
 * Created on 19. listopad 2007, 22:08
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package utils;

import database.Settings;
import gps.GpsParser;
import gps.Internal;
import gui.Gui;

/**
 *
 * @author David Vavra
 */
public class References
{
    private static Internal internal;
    /** Creates a new instance of References */
    public References()
    {
    }
    
    public static Internal getInternal(Gui ref, GpsParser ref2, Settings ref3)
    {
       if (internal == null)
           internal = new Internal(ref, ref2, ref3);
       return internal;
    }
    
}
