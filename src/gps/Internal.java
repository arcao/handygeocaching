/*
 * Internal.java
 * This file is part of HandyGeocaching.
 *
 * HandyGeocaching is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * (read more at: http://www.gnu.org/licenses/gpl.html)
 */
package gps;

import database.Settings;
import gui.Gui;
import java.util.Vector;
import javax.microedition.lcdui.AlertType;
import javax.microedition.location.Coordinates;
import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;
import utils.StringTokenizer;
import utils.Utils;

/**
 * Tato třída získává GPS data z interní GPS použitím Internal API
 * @author David Vavra
 */
public class Internal implements LocationListener {
    //reference
    private Gui gui;
    private GpsParser gpsParser;
    private Settings settings;
    
    //ostatni promenne
    LocationProvider provider;
    
    public Internal(Gui ref, GpsParser ref2, Settings ref3) {
        gui = ref;
        gpsParser = ref2;
        settings = ref3;
        if (start()) {
            gpsParser.connectionSuccessfull();
        }
    }
    
    /**
     * Vytvari objekt provideru. Na zaklade nastaveni bere v uvahu nastaveni criteria u internich GPS.
     */
    public LocationProvider createProvider() throws LocationException {
        LocationProvider provider = null;
        Criteria criteria = new Criteria();
        criteria.setAltitudeRequired(true);
        criteria.setSpeedAndCourseRequired(true);
        criteria.setCostAllowed(true);
        criteria.setPreferredPowerConsumption(Criteria.POWER_USAGE_HIGH);
        
        
        switch(settings.internalGPSType) {
            case Settings.INTERNAL_GPS_BLACKBERRY:
                provider = LocationProvider.getInstance(null);
                break;
            case Settings.INTERNAL_GPS_SAMSUNG_SGH_I5X0:
                //+PB, 17.5.2008: fix pro Samsung SGH-i550, SGH-i560
                // tyto telefony nemaji provider pro rychlost a smer
                // a pokud je pozadujete, provider neprijde!
                criteria.setSpeedAndCourseRequired(false);
                provider = LocationProvider.getInstance(criteria);
                break;
            default:
                provider = LocationProvider.getInstance(criteria);
                break;
        }
        
        return provider;
    }
    
    /**
     * Registruje listener na zadany provider. U Siemens SXG75 a BBY se zada o sekundove tempo
     * aktualizace.
     *
     * @param provider objekt provideru
     */
    public void registerLocationListener(LocationProvider provider) {
        switch(settings.internalGPSType) {
            case Settings.INTERNAL_GPS_BLACKBERRY:
            case Settings.INTERNAL_GPS_SAMSUNG_SGH_I5X0:
            case Settings.INTERNAL_GPS_GENERAL_1S:
                provider.setLocationListener(this,1,1,1);
                break;
            default:
                provider.setLocationListener(this,-1,-1,-1);
                break;
        }
    }
    
    /**
     * Zacatek gps komunikace, vraci false pokud se nepodarilo pripojit
     */
    public boolean start() {
        try {
            gui.getDisplay().setCurrent(gui.get_frmConnecting());
            gui.get_frmConnecting().append("Připojuji...\n");
            
            provider = createProvider();
            
            if( provider!=null ) {
                gui.get_frmConnecting().append("Našli jsme GPS přijímač\n");
                registerLocationListener(provider);
                gui.get_frmConnecting().append("Přijímač zapnut\n");
            } else {
                gui.showAlert("Nepodařilo se připojit k interní GPS. Nepovedlo se vytvořit provider.",AlertType.ERROR,gui.get_lstMode());
                return false;
            }
            
            if (provider.getState()!=LocationProvider.OUT_OF_SERVICE) {
                gui.get_frmConnecting().append("Připojeno\n");
                return true;
            } else {
                gui.showAlert("Nepodařilo se připojit k interní GPS. Je GPS zapnuto?",AlertType.ERROR,gui.get_lstMode());
                return false;
            }
        } catch (LocationException ex) {
            gui.showAlert("Nepodařilo se připojit k interní GPS: "+ex.toString(),AlertType.ERROR,gui.get_lstMode());
            return false;
        } catch (Exception e) {
            gui.showError("internal start",e.toString(),"");
            return false;
        }
    }
    
    /**
     * Ziskani potrebnych GPS dat
     */
    public void locationUpdated(LocationProvider provider, final Location location) {
        if (location == null) return;
        
        new Thread(new Runnable() {
            public void run() {
                try {
                    //Pokud podporuje application/X-jsr179-location-nmea, ziskame informace o satelitech
                    String nmea = location.getExtraInfo("application/X-jsr179-location-nmea");
                    if (nmea != null && nmea.length() > 0) {
                        String[] lines = StringTokenizer.getArray(nmea, "$");
                        for(int i = 0; i < lines.length; i++) {
                            if (lines[i].length() > 0 && (lines[i].startsWith("GPGSV") || lines[i].startsWith("GPGSA")))
                                gpsParser.receiveNmea("$" + lines[i]);
                        }
                    }
                    
                    gpsParser.nmeaCount++;
                    
                    if (location.isValid()) {
                        gpsParser.fix = true;
                        gpsParser.fixType = 1;
                        
                        QualifiedCoordinates coordinates = location.getQualifiedCoordinates();
                        //if (coordinates.getHorizontalAccuracy() > 100) return; //zahazujeme velke nepresnosti
                        
                        gpsParser.latitude = coordinates.getLatitude();
                        gpsParser.friendlyLatitude = Gps.convertDoubleToDeg(coordinates.getLatitude(), false, 4);
                        
                        gpsParser.longitude = coordinates.getLongitude();
                        gpsParser.friendlyLongitude = Gps.convertDoubleToDeg(coordinates.getLongitude(), true, 4);
                        gpsParser.accuracyInMeters = coordinates.getHorizontalAccuracy();
                        
                        double altitude = Math.floor(coordinates.getAltitude());
                        if (!Double.isNaN(altitude))
                            gpsParser.altitude = altitude;
                        
                        double heading = location.getCourse();
                        if (!Double.isNaN(heading))
                            gpsParser.heading = heading;
                        
                        double speed = location.getSpeed();
                        if (!Double.isNaN(speed))
                            gpsParser.speed = speed;
                        
                        if (gpsParser.go4cacheClient != null)
                            gpsParser.go4cacheClient.setCoordinates(gpsParser.latitude, gpsParser.longitude);
                    } else {
                        if (gpsParser.go4cacheClient != null)
                            gpsParser.go4cacheClient.setFix(false);
                        gpsParser.fix = false;
                        gpsParser.fixType = 0;
                        gpsParser.fixSatellites = 0;
                    }
                } catch (Exception e) {
                    gui.showError("location updated",e.toString(),"");
                }
            }
        }).start();
    }
    
    /**
     * Zmena stavu providera
     */
    public void providerStateChanged(LocationProvider provider, int state) {
        if (provider.getState()!=LocationProvider.AVAILABLE)
            gpsParser.fix = false;
    }
    
}
