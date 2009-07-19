/*
 * ImageCacheItem.java
 *
 * Created on 19. červenec 2009, 16:02
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package utils;

import javax.microedition.lcdui.Image;

/**
 *
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