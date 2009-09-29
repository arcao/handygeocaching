/*
 * ImageCacheItem.java
 * This file is part of HandyGeocaching.
 *
 * HandyGeocaching is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * (read more at: http://www.gnu.org/licenses/gpl.html)
 */
package utils;

import javax.microedition.lcdui.Image;

/**
 * Trida reprezentujici polozku obrazku v kesi.
 * @author Arcao
 */
public class ImageCacheItem {
        private String fileName;
        private Image image;
        
        public ImageCacheItem(String fileName, Image image) {
            this.fileName = fileName;
            this.image = image;
        }
        
        public String getFileName() {
            return fileName;
        }
        
        public Image getImage() {
            return image;
        }
    }