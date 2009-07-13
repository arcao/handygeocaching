/*
 * Cache.java
 *
 * Created on 4. červenec 2007, 12:16
 * 
 */

package http;

import java.util.Vector;

/**
 * Tato trida se stara o cachovani kesi(:)) tak, aby se uz jednou stazene veci nemusely tahat znova.
 * @author David Vavra
 */
public class Cache
{
    private Vector caches;
    
    public Cache()
    {
        caches = new Vector();
    }
    
    /**
     * Nacte zakesovanou odpoved HTTP, pokud neni zakesovana, vrati null
     */
    public String loadCachedResponse(String url)
    {
        int position = positionInCache(url);
        if (position == -1)
        {
           return null; 
        }
        else
        {
            return ((CacheItem)caches.elementAt(position)).response;
        }
        
    }
    
    /**
     * Prida HTTP odpoved do kese
     */
    public void addCachedResponse(String url, String response)
    {
       caches.addElement(new CacheItem(url, response));
    }
    
    /**
     * Zjisti pozici zakesovane odpovedi ve spojaku, pokud neni, vrati -1
     */
    public int positionInCache(String url)
    {
        for (int i=0;i<caches.size();i++)
        {
            if (((CacheItem)caches.elementAt(i)).url.equals(url))
                return i;
        }
        return -1;
    }
    
}
