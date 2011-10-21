/*
 * IOUtils.java
 *
 * Created on 21. říjen 2011, 8:06
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import javax.microedition.io.Connection;

/**
 *
 * @author Arcao
 */
public class IOUtils {
    
    public static void silentClose(Connection c) {
        try {
            if (c != null)
                c.close();
        } catch (IOException e) {}
    }
    
    public static void silentClose(InputStream is) {
        try {
            if (is != null)
                is.close();
        } catch (IOException e) {}
    }
    
     public static void silentClose(OutputStream os) {
        try {
            if (os != null)
                os.close();
        } catch (IOException e) {}
    }
    
    public static void silentClose(Reader r) {
        try {
            if (r != null)
                r.close();
        } catch (IOException e) {}
    }
    
     public static void silentClose(Writer w) {
        try {
            if (w != null)
                w.close();
        } catch (IOException e) {}
    }
}
