/*
 * MapItem.java
 *
 * Created on 11. zברם 2007, 17:42
 * 
 */

package track;

/**
 * Tato trida reprezentuje datovou strukturu jednoho bodu usle trasy
 * @author David Vavra
 */
public class TrackItem
{
    double latitude;
    double  longitude;
    public TrackItem(double a, double b)
    {
        latitude = a;
        longitude = b;
    }
}
