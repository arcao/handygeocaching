/*
 * CacheItem.java
 *
 * Created on 17. øíjen 2007, 12:18
 *
 */

package http;

/**
 * Objekt reprezentujici jednu zakesovanou HTTP odpoved
 * @author David Vavra
 */
class CacheItem
{
    String url;
    String response;
    public CacheItem(String u, String r)
    {
        url = u;
        response = r;
    }
}
