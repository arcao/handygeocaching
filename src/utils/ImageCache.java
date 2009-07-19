/*
 * ImageCache.java
 *
 * Created on 19. červenec 2009, 15:43
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package utils;

import java.util.Vector;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;

/**
 *
 * @author Administrator
 */
public class ImageCache {
    private static Vector cache = null;
    
    public static Image get(final String fileName) {
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
            ImageCacheItem newItem = new ImageCacheItem(fileName, Image.createImage(fileName));
            cache.addElement(newItem);

            return newItem.getImage();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
