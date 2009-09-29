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
package track;

/**
 * Tato trida reprezentuje datovou strukturu jednoho bodu usle trasy
 * @author David Vavra
 */
public class TrackItem
{
    double latitude;
    double  longitude;
    public TrackItem(double a, double b)
    {
        latitude = a;
        longitude = b;
    }
}
