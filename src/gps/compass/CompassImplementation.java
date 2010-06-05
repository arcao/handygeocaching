/*
 * CompassImplementation.java
 * This file is part of HandyGeocaching.
 *
 * HandyGeocaching is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * (read more at: http://www.gnu.org/licenses/gpl.html)
 */

package gps.compass;

/**
 * Implementace abstraktní třídy kompasu
 * @author Arcao
 */
class CompassImplementation extends Compass {
    public float getAzimuth() {
        try {
            javax.microedition.location.Orientation o = javax.microedition.location.Orientation.getOrientation();
            if (o.isOrientationMagnetic()) {
                return (o.getCompassAzimuth() + magneticDeclination) % 360;
            } else {
                return o.getCompassAzimuth();
            }
        } catch (Exception e) {
            return Float.NaN;
        }
    }
}