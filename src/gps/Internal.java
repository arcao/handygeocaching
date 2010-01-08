/*
 * Internal.java
 *
 * Created on 14. září 2007, 14:21
 *
 */

package gps;

import database.Settings;
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
 * Tato třída získává GPS data z interní GPS použitím Internal API
 * @author David Vavra
 */
public class Internal implements LocationListener
{
    //reference
    private Gui gui;
    private GpsParser gpsParser;
    private Settings settings;
    
    //ostatni promenne
    LocationProvider provider;
    
    public Internal(Gui ref, GpsParser ref2, Settings ref3)
    {
        gui = ref;
        gpsParser = ref2;
        settings = ref3;
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
            gui.get_frmConnecting().append("Připojuji...\n");
            Criteria criteria = new Criteria();
            criteria.setPreferredResponseTime(1000);
            criteria.setAltitudeRequired(true);
            criteria.setSpeedAndCourseRequired(true);
            //criteria.setCostAllowed(true);
            provider = LocationProvider.getInstance(criteria);
            
            if (provider == null)
            {
                //+PB, 17.5.2008: fix pro Samsung SGH-i550, SGH-i560
                // tyto telefony nemaji provider pro rychlost a smer 
                // a pokud je pozadujete, provider neprijde!
                gui.get_frmConnecting().append("Telefon je asi Samsung, že?\n");
                criteria.setSpeedAndCourseRequired(false);
                provider = LocationProvider.getInstance(criteria);
            }
            
            if( provider!=null )
            {
                //?PB: tohle se mi nelibi - u Siemens SXG75 vede na to, ze da souradnice tak
                // jednou za minutu. chce to explicitne pozadat o sekundove tempo
                // provider.setLocationListener(this,-1,-1,-1);
                gui.get_frmConnecting().append("Našli jsme GPS přijímač\n");
                provider.setLocationListener(this,3,1,1);
                gui.get_frmConnecting().append("Přijímač zapnut\n");
            }
            else
            {
                gui.showAlert("Nepodařilo se připojit k interní GPS (1)",AlertType.ERROR,gui.get_lstMode());
                return false;
            }
            //-PB konec fixu   
            
            //?PB: tohle je ale blbost! Po spusteni bude mit provider stav
               // TEMPORARILY_UNAVAILABLE 
               // a to po nekolik desitek sekund, nez se chyti druzice...
               // ! Navic v predesle variante kodu muze v tomto miste byt provider NULL!
            // if( provider.getState()==LocationProvider.AVAILABLE)
            if ( provider!=null && provider.getState()!=LocationProvider.OUT_OF_SERVICE)
            {
                gui.get_frmConnecting().append("Připojeno\n");
                return true;
            }
            else
            {
                gui.showAlert("Nepodařilo se připojit k interní GPS (2)",AlertType.ERROR,gui.get_lstMode());
                return false;
            }
        }
        catch (LocationException ex)
        {
            gui.showAlert("Nepodařilo se připojit k interní GPS (3)",AlertType.ERROR,gui.get_lstMode());
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
        if (location == null) return;
        
        try
        {
            if (location.isValid())
            {
                String nmea = location.getExtraInfo("application/X-jsr179-location-nmea");
                if (nmea != null && nmea.length() > 0 && nmea.trim().startsWith("$GPGSV"))
                    gpsParser.receiveNmea(nmea.trim());

                
                QualifiedCoordinates coordinates = location.getQualifiedCoordinates();
                if (coordinates.getHorizontalAccuracy() > 100) return; //zahazujeme velke nepresnosti
                
                gpsParser.nmeaCount++;
                gpsParser.fix = true;
                gpsParser.latitude = coordinates.getLatitude();
                String friendly = Coordinates.convert(Math.abs(coordinates.getLatitude()),Coordinates.DD_MM);
                gpsParser.friendlyLattitude = ((gpsParser.latitude>0)?"N ":"S ")+Utils.addZeros(friendly.substring(0,friendly.indexOf(':')),2)+Utils.replaceString(Utils.addZerosAfter(friendly.substring(friendly.indexOf(':')), 7),":","° ");
                gpsParser.longitude = coordinates.getLongitude();
                friendly = Coordinates.convert(Math.abs(coordinates.getLongitude()),Coordinates.DD_MM);
                gpsParser.friendlyLongitude = ((gpsParser.longitude>0)?"E ":"W ")+Utils.addZeros(friendly.substring(0,friendly.indexOf(':')),3)+Utils.replaceString(Utils.addZerosAfter(friendly.substring(friendly.indexOf(':')), 7),":","° ");
                gpsParser.accuracy = coordinates.getHorizontalAccuracy();
                
                double altitude = Math.floor(coordinates.getAltitude());
                if (!Double.isNaN(altitude))
                    gpsParser.altitude = altitude;
                
                double heading = location.getCourse();
                if (!Double.isNaN(heading))
                    gpsParser.heading = heading;
                
                double speed = location.getSpeed();
                if (!Double.isNaN(speed))
                    gpsParser.speed = speed; // km/h nebo m/s? buh vi. Dle dokumentace m/s, dle implementace v telefonech km/h. :)
            }
            else
            {
                gpsParser.nmeaCount++;
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
