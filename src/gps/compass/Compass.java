/*
 * Compass.java
 * This file is part of HandyGeocaching.
 *
 * HandyGeocaching is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * (read more at: http://www.gnu.org/licenses/gpl.html)
 */

package gps.compass;

import gps.Gps;

/**
 * Třída pro získání azimutu z kompasu vestavěném v mobilním zařízení.
 * @author Arcao
 */
public abstract class Compass {
    protected float magneticDeclination = 0f;
    
    /**
     * Nastavuje rozdíl ve stupních mezi pravým a magnetickým severem
     * @param declination rozdíl ve stupních 
     */
    public void setMagnetigDeclination(float magneticDeclination) {
        this.magneticDeclination = magneticDeclination;
    }
    
    public float getMagneticDeclination() {
        return magneticDeclination;
    }
    
    public static String formatDelination(float declination) {
        StringBuffer sb = new StringBuffer();
        if (declination < 0) {
            sb.append('-');
            declination = -declination;
        }
        
        int deg = (int) declination;
        sb.append(deg);
        sb.append("°");

        int min = (int) ((declination - deg) * 60);
        if (min > 0) {
            sb.append(' ');
            sb.append(min);
            sb.append('\'');
        }

        return sb.toString();
    }

    public static float parseDecliantion(String declination) {
        return (float) Gps.convertDegToDouble(declination);
    }
    
    /**
     * Vravcí azimut kompasu
     * @return azimut kompasu 
     */
    public abstract float getAzimuth();
    
    protected abstract boolean hasCompass();
    
    /**
     * Vrací true, pokud je kompas v mobilním zařízení podporován, jinak ne.
     * @return true, pokud je kompas podporován
     */
    public static boolean isSupported() {
        try {
            if (System.getProperty("microedition.location.version") == null)
                return false;
            
            Compass c = getCompass(0f);
            if (c == null || !c.hasCompass())
                return false;
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Vrací instanci objektu Compass, pokud se nezadaří (není podpora, nebo zakázal uživatel, vrací null)
     * @return instanci objektu Compass
     */
    public static Compass getCompass(float magneticDeclination) {
        try {
            Class.forName("javax.microedition.location.Orientation");
            
            Compass c = (Compass) Class.forName("gps.compass.CompassImplementation").newInstance();
            c.setMagnetigDeclination(magneticDeclination);
            return c;
        } catch (Exception e) {
            return null;
        }
    }
}