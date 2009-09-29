/*
 * References.java
 * This file is part of HandyGeocaching.
 *
 * HandyGeocaching is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * (read more at: http://www.gnu.org/licenses/gpl.html)
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
