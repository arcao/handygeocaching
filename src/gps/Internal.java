/*
 * Internal.java
 *
 * Created on 14. záøí 2007, 14:21
 *
 */

package gps;

import gui.Gui;
import javax.microedition.lcdui.AlertType;
import javax.microedition.location.Coordinates;
import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;
import utils.Utils;

/**
 * Tato tøída získává GPS data z interní GPS použitím Internal API
 * @author David Vavra
 */
public class Internal implements LocationListener
{
    //reference
    private Gui gui;
    private GpsParser gpsParser;
    
    //ostatni promenne
    LocationProvider provider;
    
    public Internal(Gui ref, GpsParser ref2)
    {
        gui = ref;
        gpsParser = ref2;
        if (start())
        {
            gpsParser.connectionSuccessfull();
        }
    }
    
    /**
     * Zacatek gps komunikace, vraci false pokud se nepodarilo pripojit
     */
    public boolean start()
    {
        try
        {
            gui.getDisplay().setCurrent(gui.get_frmConnecting());
            gui.get_frmConnecting().append("Pøipojuji...\n");
            Criteria criteria = new Criteria();
            criteria.setAltitudeRequired(true);
            criteria.setSpeedAndCourseRequired(true);
            criteria.setCostAllowed(true);
            provider = LocationProvider.getInstance(criteria);
            provider.setLocationListener(this,-1,-1,-1);
            if (provider == null)
            {
                gui.showAlert("Nepodaøilo se pøipojit k interní GPS (1)",AlertType.ERROR,gui.get_lstMode());
            }
            else if (provider.getState()==LocationProvider.AVAILABLE)
            {
                gui.get_frmConnecting().append("Pøipojeno\n");
                return true;
            }
            else
            {
                gui.showAlert("Nepodaøilo se pøipojit k interní GPS (2)",AlertType.ERROR,gui.get_lstMode());
            }
            return false;
        }
        catch (LocationException ex)
        {
            gui.showAlert("Nepodaøilo se pøipojit k interní GPS (3)",AlertType.ERROR,gui.get_lstMode());
            return false;
        }
        catch (Exception e)
        {
            gui.showError("internal start",e.toString(),"");
            return false;
        }
    }
    
    /**
     * Ziskani potrebnych GPS dat
     */
    public void locationUpdated(LocationProvider provider, Location location)
    {
        try
        {
            gpsParser.nmeaCount++;
            if (location.isValid())
            {
                gpsParser.fix = true;
                QualifiedCoordinates coordinates = location.getQualifiedCoordinates();
                gpsParser.latitude = coordinates.getLatitude();
                gpsParser.friendlyLattitude = ((gpsParser.latitude>0)?"N ":"S ")+Utils.replaceString(Coordinates.convert(coordinates.getLatitude(),Coordinates.DD_MM),":","° ");
                gpsParser.longitude = coordinates.getLongitude();
                String friendly = Coordinates.convert(coordinates.getLongitude(),Coordinates.DD_MM);
                gpsParser.friendlyLongitude = ((gpsParser.longitude>0)?"E ":"W ")+Utils.addZeros(friendly.substring(0,2),3)+Utils.replaceString(friendly.substring(2),":","° ");
                gpsParser.altitude = coordinates.getAltitude();
                gpsParser.accuracy = coordinates.getHorizontalAccuracy();
                gpsParser.heading = location.getCourse();
                gpsParser.speed = location.getSpeed();
            }
            else
            {
                gpsParser.fix = false;
            }
        }
        catch (Exception e)
        {
            gui.showError("location updated",e.toString(),"");
        }
    }
    
    /**
     * Zmena stavu providera
     */
    public void providerStateChanged(LocationProvider provider, int state)
    {
        if (provider.getState()!=LocationProvider.AVAILABLE)
            gpsParser.fix = false;
    }
    
}
