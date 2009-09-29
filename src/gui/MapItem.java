/*
 * MapItem.java
 * This file is part of HandyGeocaching.
 *
 * HandyGeocaching is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * (read more at: http://www.gnu.org/licenses/gpl.html)
 */
package gui;

/**
 * Tato trida reprezentuje datovou strukturu jednoho bodu na mape
 * @author David Vavra
 */
public class MapItem
{
    public double latitude;
    public double longitude;
    public String icon;
    public String name;
    public MapItem(double a, double b, String c, String d)
    {
        latitude = a;
        longitude = b;
        icon = c;
        name = d;
    }
}
