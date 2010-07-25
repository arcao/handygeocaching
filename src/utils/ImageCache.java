/*
 * ImageCache.java
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
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;

/**
 * Kesuje obrazek v pameti tak, aby se nemusel znovu nacitat. 
 * @author Arcao
 */
public class ImageCache {
    private static Vector cache = null;
        
    public static Image get(final String fileName, final String defaultFileName) {
        if (cache == null)
            cache = new Vector();
        
        for (int i=0; i < cache.size(); i++) {
            ImageCacheItem item = (ImageCacheItem) cache.elementAt(i);
            if (item.getFileName().equals(fileName)) {
                return item.getImage(); 
            }
        }
        
        //nenalezeno
        try {
            Image image = null;
            try {
                image = Image.createImage(fileName);
            } catch (Exception e) {
                //pokud se nepovedlo nacist, pokusime se nacist defaultFilename
                if (defaultFileName != null) {
                    image = Image.createImage(defaultFileName);
                } else {
                    throw e;
                }
            }
                            
            ImageCacheItem newItem = new ImageCacheItem(fileName, image);
            cache.addElement(newItem);

            return newItem.getImage();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static Image createImage(String fileName) {
        return get(fileName, null);
    }
}
