/* 
 * StringTokenizer.java
 * This file is part of HandyGeocaching.
 *
 * HandyGeocaching is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * (read more at: http://www.gnu.org/licenses/gpl.html)
 */
package utils;

import java.util.Vector;
/**
 * Tato trida rozdeli jeden string podle zadaneho separatoru, vrati pole nebo vector stringu
 */
public class StringTokenizer {

    public static Vector getVector(String s, String sep) {
        Vector v = new Vector();
        int pos = 0;
        int found = 0;
        int sepLen = sep.length();
        
        while ((found = s.indexOf(sep, pos)) > -1) {
            v.addElement(s.substring(pos, found));
            pos = found+sepLen;
        }
        v.addElement(s.substring(pos));
        
        return v;
    }

    public static String[] getArray(String s, String sep) {
        Vector tokens = getVector(s,sep);
        String[] st = new String[tokens.size()];
        tokens.copyInto(st);
        
        return st;
    }
}
