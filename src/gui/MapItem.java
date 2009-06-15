/*
 * MapItem.java
 *
 * Created on 11. zברם 2007, 17:42
 *
 */

package gui;

/**
 * Tato trida reprezentuje datovou strukturu jednoho bodu na mape
 * @author David Vavra
 */
public class MapItem
{
    public double latitude;
    public double longitude;
    public String icon;
    public String name;
    public MapItem(double a, double b, String c, String d)
    {
        latitude = a;
        longitude = b;
        icon = c;
        name = d;
    }
}
