/*
 * CacheItem.java
 * This file is part of HandyGeocaching.
 *
 * HandyGeocaching is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * (read more at: http://www.gnu.org/licenses/gpl.html)
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
