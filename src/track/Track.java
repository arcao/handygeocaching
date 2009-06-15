/*
 * Track.java
 *
 * Created on 17. øíjen 2007, 18:03
 */

package track;

import gps.GpsParser;
import gui.Gui;
import java.util.Vector;

/**
 * Tato tøída uchovává prošlou trasu. Trasa se ukládá pøi navigaci a mapì.
 * @author David Vavra
 */
public class Track implements Runnable
{
    //reference
    private Gui gui;
    private GpsParser gpsParser;
    
    //konstanty
    private static final long TRACK_DELAY = 4000; //za jak dlouho se trackne dalsi pozice v ms
    
    //promenne
    private Thread thread;
    private Vector trackData;
    private int trackIndex = 0;
    
    public Track(Gui ref, GpsParser ref2)
    {
        gui = ref;
        gpsParser = ref2;
        trackData = new Vector();
    }
    
    public void start()
    {
        thread = new Thread(this);
        thread.start();
    }
    
    public void stop()
    {
        thread = null;
    }
    
    public void reset()
    {
        trackIndex = 0;
    }
    
    /**
     * Pridavani bodu do tracku provadi samostatne vlakno
     */
    public void run()
    {
        try
        {
            double lat, lon;
            while (thread != null)
            {
                if (gpsParser.hasFix())
                {
                    lat = gpsParser.getLatitude();
                    lon = gpsParser.getLongitude();
                    if (trackData.size()==0)
                    {
                        trackData.addElement(new TrackItem(lat, lon));
                    }
                    else
                    {
                        TrackItem last = (TrackItem)trackData.lastElement();
                        if (last.latitude!=lat || last.longitude!=lon)
                        {
                            trackData.addElement(new TrackItem(lat, lon));
                        }
                    }
                }
                if (thread != null)
                    thread.sleep(TRACK_DELAY);
            }
        }
        catch (Exception e)
        {
            gui.showError("track run",e.toString(),"");
        }
    }
    
    /**
     * Metoda ktera prochazi cely track - pouziva se pri vykreslovani
     * Kazda iterace vraci souradnice cary mezi dvema body jako pole. Pokud jsme na konci, vrati se null
     */
    public double[] nextLine()
    {
        try
        {
            if (trackData.size()<2)
                return null;
            if ((trackIndex+1)>=trackData.size())
                return null;
            double[] line = new double[4];
            TrackItem trackItem = (TrackItem)trackData.elementAt(trackIndex);
            line[0] = trackItem.longitude;
            line[1] = trackItem.latitude;
            trackIndex++;
            trackItem = (TrackItem)trackData.elementAt(trackIndex);
            line[2] = trackItem.longitude;
            line[3] = trackItem.latitude;
            return line;
        }
        catch (Exception e)
        {
            gui.showError("track nextline",e.toString(),"");
            return null;
        }
    }
    
}
