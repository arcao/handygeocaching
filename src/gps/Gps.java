/*
 * Gps.java
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
import http.Http;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Displayable;
import track.Track;
import utils.MathUtil;
import utils.StringTokenizer;
import utils.Utils;
import java.util.Hashtable;

/***
 * Tato trida se stara o zobrazovani GPS dat a nezbytne vypocty
 * @author David Vavra
 */
public class Gps implements Runnable
{
    //kostanty
    private static final long BREAK = 1000; //refresh
    private static final long BREAK_NAVI = 250; //refresh
    private static final int MAXIMUM_SKIPS = 20; //maximalni pocet refreshu bez spojeni z modulem
    
    //mozne akce
    public static final int AVERAGING = 0;
    public static final int NAVIGATION = 1;
    public static final int AVERAGING_RESUME = 2;
    public static final int CURRENT_POSITION = 3;
    public static final int COORDINATES_FAVOURITES = 4;
    public static final int MAP = 5;
    //Zephy 21.11.07 gpsstatus+\
    public static final int GPS_SIGNAL = 6;
    //Zephy 21.11.07 gpsstatus+/
    public static final int CURRENT_POSITION_PROJECTION = 7;
    
    //reference na ostatni moduly
    private Gui gui;
    private GpsParser gpsParser;
    private Http http;
    private Settings settings;
    private Track track;
    
    private int action;
    private Thread thread;
    private double[] buffer1; //buffery pro prumerovani
    private double[] buffer2;
    private int bufferPosition = 0;
    private boolean plnybuffer = false;
    public String lattitude, longitude = null;
    private String targetname = ""; //nazev navigacniho bodu
    private double targetlat = 0, targetlon = 0; //navigacni souradnice
    private int lastNmeaCount = 0; //minuly pocet nmea zprav pro odpojovani
    private int flashbackLightPause = 0; //pocitadlo pro bliknuti
    private int skips = 0; //pocet kol bez spojeni s gpskou
    private Displayable previousScreen; //posledni obrazovka pri navratu z navigace/mapy
    
    public GpsParser getGpsParser() {
        return gpsParser;
    }
    
    public Gps(Gui ref, Http ref2, GpsParser ref3, Settings ref4)
    {
        gui = ref;
        http = ref2;
        gpsParser = ref3;
        settings = ref4;
        track = new Track(gui, gpsParser);
        gui.setReference(track);
    }
    
    /**
     * Zahaji zobrazovani GPS informaci na displeji
     */
    public void start(int act)
    {
        try
        {
            action = act;
            if (action == AVERAGING)
            {
                buffer1 = new double[100];
                buffer2 = new double[100];
                bufferPosition = 0;
                plnybuffer = false;
            }
            thread = new Thread(this);
            thread.start();
            //trackovani
            if (action==NAVIGATION || action==MAP )
            {
                track.start();
            }
        }
        catch (Exception e)
        {
            gui.showError("gps start", e.toString(),"");
        }
    }
    
    /**
     * Zastavi zobrazovani GPS informaci
     */
    public void stop()
    {
        if (action==NAVIGATION || action==MAP )
        {
            track.stop();
        }
        //thread.interrupt();
        thread = null;
    }
    
    /**
     * Zmeni GPS akci, pouziva se pri prechodu mezi mapou a navigaci
     */
    public void changeAction(int act)
    {
        action = act;
    }
    
    /**
     * Nastavi navigacni souradnice
     */
    public void setNavigationTarget(String lattitude, String longitude, String name)
    {
        if (name == null) name = "";
        
        targetname = name;
        targetlat = convertDegToDouble(lattitude);
        targetlon = convertDegToDouble(longitude);
    }
    
    /**
     * Zjisti, jestli jsou zadany navigacni souradnice
     */
    public boolean isNavigating()
    {
        return (!Double.isNaN(targetlat) && !Double.isNaN(targetlon));
    }
    
    /**
     * Vrati navigacni souradnice
     */
    public double getNavigationLatitude()
    {
        return targetlat;
    }
    public double getNavigationLongitude()
    {
        return targetlon;
    }
    
    /** 
     * Getry a setry pro predchozi obrazovku
     */
    public Displayable getPreviousScreen()
    {
        return previousScreen;
    }
    public void setPreviousScreen(Displayable screen)
    {
        previousScreen = screen;
    }
    
    
    /**
     * Provadi zpracovani dat ziskanych z GPS pristroje
     */
    public void run()
    {
        while (thread != null)
        {
            try
            {
                //nevalidni pozice
                if (!gpsParser.hasFix())
                {
                    if (action == AVERAGING)
                    {
                        gui.get_frmAveraging().setTitle("Není GPS signál("+gpsParser.getSatelliteCount()+" s.)");
                    }
                    else if (action == NAVIGATION)
                    {
                        gui.get_cvsNavigation().cacheName = "Není GPS signál("+gpsParser.getSatelliteCount()+" s.)";
                        gui.get_cvsNavigation().repaint();
                    }
                    else if (action == MAP)
                    {
                        gui.get_cvsMap().fixMessage = "Není GPS signál("+gpsParser.getSatelliteCount()+" s.)";
                        gui.get_cvsMap().repaint();
                    }
                    //Zephy 21.11.07 gpsstatus+\
                    else if (action == GPS_SIGNAL)
                    {
                        gui.get_cvsSignal().signaldata = gpsParser.getSignalData();
                        gui.get_cvsSignal().activeSat = gpsParser.getActivSat();

                        gui.get_cvsSignal().latitude = "<není signál>";
                        gui.get_cvsSignal().longitude = "<není signál>";
                        gui.get_cvsSignal().speed = "0km/h";
                        gui.get_cvsSignal().altitude = "0m";
                        gui.get_cvsSignal().satellitescount = gpsParser.getSatelliteCount();                        
                        gui.get_cvsSignal().pdop = gpsParser.getPDOP();
                        gui.get_cvsSignal().hdop = gpsParser.getHDOP();
                        gui.get_cvsSignal().vdop = gpsParser.getVDOP();
                        
                        gui.get_cvsSignal().repaint();
                       
                    }
                    //Zephy 21.11.07 gpsstatus+/ 
                    else
                    {
                        gui.get_frmCoordinates().setTitle("Není GPS signál("+gpsParser.getSatelliteCount()+" s.)");
                    }
                }
                else 
                {
                    //prumerne souradnice
                    if (action == AVERAGING || action == AVERAGING_RESUME)
                    {
                        String skips;
                        //zaznamenani
                        buffer1[bufferPosition]=gpsParser.getLatitude();
                        buffer2[bufferPosition]=gpsParser.getLongitude();
                        if (bufferPosition==99)
                        {
                            bufferPosition = 0;
                            plnybuffer=true;
                        }
                        bufferPosition++;
                        //vypocet
                        //cim budu delit
                        int deleno;
                        if (plnybuffer)
                            deleno = 100;
                        else
                            deleno = bufferPosition;
                        //soucet
                        double sumLat=0;
                        double sumLon=0;
                        int j;
                        for (j=0;j<deleno;j++)
                        {
                            sumLat += buffer1[j];
                            sumLon += buffer2[j];
                        }
                        
                        gui.get_frmAveraging().setTitle("Průměrování");
                        gui.get_siCurrentCoordinates().setText(gpsParser.getFriendlyLatitude() + "\n"+ gpsParser.getFriendlyLongitude());
                        gui.get_siAverageLattitude().setText(convertDoubleToDeg(sumLat/deleno, false) +"\n");
                        gui.get_siAverageLongitude().setText(convertDoubleToDeg(sumLon/deleno, true) +"\n");
                        gui.get_siMeasures().setText(deleno+"/100");
                        gui.get_siAdditional().setText(gpsParser.getSatelliteCount()+" sat./"+String.valueOf(gpsParser.getSpeed())+" km/h/"+String.valueOf(gpsParser.getAccuracy()));
                        
                        if (gpsParser.go4cacheClient != null)
                            gpsParser.go4cacheClient.setAction("Průměruji souřadnice...");
                
                    }
                    else if (action == NAVIGATION)
                    {
                        gui.get_cvsNavigation().cacheName = targetname;
                        double rad_distance = computeRadianDistance(gpsParser.getLatitude(),targetlat,gpsParser.getLongitude(),targetlon);
                        int distance = radianToMeters(rad_distance);
                        double bearing = computeBearing(gpsParser.getLatitude(),targetlat,gpsParser.getLongitude(),targetlon, rad_distance);
                        //gui.get_siDebug().setText("fixMessage:"+gpsParser.hasFix()+"\nlat:"+gpsParser.getLattitude()+"\nlon:"+gpsParser.getLongitude()+"heading:"+gpsParser.getHeading()+"nmeaCount:"+gpsParser.getNmeaCount()+"\ndistance:"+distance+"cislo:"+Float.parse("6366832.9383716631",10));
                        double navigate = computeCorrectionDirection(bearing, gpsParser.getHeading());
                        
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new Date());
                        int month = calendar.get(Calendar.MONTH)+1;
                        int date = calendar.get(Calendar.DATE);
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);
                        String strMinute = String.valueOf(minute);
                        if (strMinute.length()==1)
                            strMinute = "0"+strMinute;
                        String dateTime = date+"."+month+". "+hour+":"+strMinute;
                        gui.get_cvsNavigation().distance=String.valueOf(distance)+" m";
                        gui.get_cvsNavigation().speed = String.valueOf(gpsParser.getSpeed()) +" km/h";
                        gui.get_cvsNavigation().altitude = gpsParser.getAltitude()+" m.n.m";
                        gui.get_cvsNavigation().satellites = gpsParser.getSatelliteCount()+" sat.";
                        gui.get_cvsNavigation().angle = navigate;
                        gui.get_cvsNavigation().compass = (int) gpsParser.getHeading();
                        gui.get_cvsNavigation().accuracy = "±"+gpsParser.getAccuracy();
                        gui.get_cvsNavigation().azimut = ((int)bearing)+"°";
                        gui.get_cvsNavigation().dateTime = dateTime;
                        gui.get_cvsNavigation().repaint();
                        
                        if (gpsParser.go4cacheClient != null)
                            gpsParser.go4cacheClient.setAction("Hledám keš " + targetname + "...");
                        
                    }
                    //Zephy 21.11.07 gpsstatus+\
                    else if (action == GPS_SIGNAL)
                    {  
                        gui.get_cvsSignal().latitude = gpsParser.getFriendlyLatitude();
                        gui.get_cvsSignal().longitude = gpsParser.getFriendlyLongitude();
                        gui.get_cvsSignal().speed = String.valueOf(gpsParser.getSpeed()) +"km/h";
                        gui.get_cvsSignal().altitude = gpsParser.getAltitude()+"m";
                        gui.get_cvsSignal().satellitescount = gpsParser.getSatelliteCount();
                        gui.get_cvsSignal().signaldata = gpsParser.getSignalData();
                        gui.get_cvsSignal().activeSat = gpsParser.getActivSat();
                        gui.get_cvsSignal().pdop = gpsParser.getPDOP();
                        gui.get_cvsSignal().hdop = gpsParser.getHDOP();
                        gui.get_cvsSignal().vdop = gpsParser.getVDOP();
                        gui.get_cvsSignal().repaint();
                        
                        if (gpsParser.go4cacheClient != null)
                            gpsParser.go4cacheClient.setAction("");
                    }
                    //Zephy 21.11.07 gpsstatus+/

                    else if (action == MAP)
                    {
                        gui.get_cvsMap().fixMessage = "";
                        gui.get_cvsMap().latitude = gpsParser.getLatitude();
                        gui.get_cvsMap().longitude = gpsParser.getLongitude();
                        gui.get_cvsMap().heading = (int)gpsParser.getHeading();
                        gui.get_cvsMap().repaint();
                        
                        if (gpsParser.go4cacheClient != null)
                            gpsParser.go4cacheClient.setAction("Prohížím si mapu keší v okolí...");
                    }
                    else if (action == CURRENT_POSITION)
                    {
                        gui.get_frmCoordinates().setTitle("Souřadnice získány");
                        gui.get_tfLattitude().setString(gpsParser.getFriendlyLatitude());
                        gui.get_tfLongitude().setString(gpsParser.getFriendlyLongitude());
                        lattitude = String.valueOf(gpsParser.getLatitude());
                        longitude = String.valueOf(gpsParser.getLongitude());
                        stop();
                        http.start(Http.NEAREST_CACHES, false);
                        
                        if (gpsParser.go4cacheClient != null)
                            gpsParser.go4cacheClient.setAction("");
                    }
                    else if (action == CURRENT_POSITION_PROJECTION)
                    {
                        gui.get_tfProjectionLatitude().setString(gpsParser.getFriendlyLatitude());
                        gui.get_tfProjectionLongtitude().setString(gpsParser.getFriendlyLongitude());
                        stop();
                        
                        if (gpsParser.go4cacheClient != null)
                            gpsParser.go4cacheClient.setAction("");
                    }
                    else //ziskani souradnic u oblibenych
                    {
                        gui.get_frmAddGiven().setTitle("Souřadnice získány");
                        gui.get_tfGivenLattitude().setString(gpsParser.getFriendlyLatitude());
                        gui.get_tfGivenLongitude().setString(gpsParser.getFriendlyLongitude());
                        stop();
                        
                        if (gpsParser.go4cacheClient != null)
                            gpsParser.go4cacheClient.setAction("");
                    }
                }
            }
            catch (Exception e)
            {
                gui.showError("GPS thread", e.toString(), "\nNMEA: "+gpsParser.getNmea()+"\nException: "+gpsParser.exception);
                this.stop();
            }
            try
            {
                if (thread != null)
                    thread.sleep((action == NAVIGATION)? BREAK_NAVI : BREAK);
            }
            catch (InterruptedException e)
            {
            }
            try
            {
                //rozsviceni displeje
                if (settings.flashbackPeriod > 0)
                {
                    if (flashbackLightPause < ((action == NAVIGATION)? 4 * (settings.flashbackPeriod*2-1): settings.flashbackPeriod*2-1))
                    {
                        flashbackLightPause++;
                    }
                    else
                    {
                        gui.getDisplay().flashBacklight(1);
                        flashbackLightPause = 0;
                    }
                }
                //detekce preruseneho spojeni
                if ((gpsParser.source == GpsParser.BLUETOOTH || gpsParser.source == GpsParser.GPS_HGE_100) && !gpsParser.isOpen()) {
                    gui.showAlert("Spojení s GPS modulem bylo přerušeno!",AlertType.ERROR,gui.get_lstMode());
                    gpsParser.close();
                    stop();
                }
                
                /*if (gpsParser.getNmeaCount() == lastNmeaCount && (gpsParser.source == GpsParser.BLUETOOTH || gpsParser.source == GpsParser.GPS_HGE_100))
                {
                    skips++;
                    if (skips>MAXIMUM_SKIPS)
                    {
                        gui.showAlert("Spojení s GPS modulem bylo přerušeno!",AlertType.ERROR,gui.get_lstMode());
                        gpsParser.close();
                        stop();
                        skips = 0;
                    }
                }
                else
                {
                    skips = 0;
                }
                lastNmeaCount = gpsParser.getNmeaCount();*/
            }
            catch (Exception e)
            {
                gui.showError("gps vlakno 2", e.toString(),"");
            }
        }
    }
    
    
    /**
     * Spocita radianovou vzdalenost mezi dvema souradnicema v celych metrech
     */
    public double computeRadianDistance(double lat1, double lat2, double lon1, double lon2)
    {
        try
        {
            //prevod na radiany
            lat1 = Math.toRadians(lat1);
            lat2 = Math.toRadians(lat2);
            lon1 = Math.toRadians(lon1);
            lon2 = Math.toRadians(lon2);
            //vlastni vypocet
            double t1 = Math.sin(lat1) * Math.sin(lat2);
            double t2 = Math.cos(lat1) * Math.cos(lat2);
            double t3 = Math.cos(lon1 - lon2);
            double t4 = t2 * t3;
            double t5 = t1 + t4;
            double rad_dist = Utils.atan(-t5/Math.sqrt(-t5 * t5 +1)) + 2 * Utils.atan(1);
            return rad_dist;
        }
        catch (Exception e)
        {
            gui.showError("computeRadianDistance",e.toString(),"");
            return 0;
        }
    }
    
    /**
     * Prevede radiany na metry
     */
    public int radianToMeters(double radians)
    {
        try
        {
            double meters = radians * 1609.3470878864446 * 3437.74677 * 1.1508;
            return (int) meters;
        }
        catch (Exception e)
        {
            gui.showError("radianToMeters",e.toString(),"");
            return 0;
        }
    }
    
    /**
     * Spocita bearing
     */
    public double computeBearing(double lat1, double lat2, double lon1, double lon2, double rad_dist)
    {
        try
        {
            //prevod na radiany
            double a = Math.PI /180;
            lat1 = lat1*a;
            lat2 = lat2*a;
            lon1 = lon1*a;
            lon2 = lon2*a;
            //vlastni vypocet
            double rad_bearing;
            double t1 = Math.sin(lat2) - Math.sin(lat1) * Math.cos(rad_dist);
            double t2 = Math.cos(lat1) * Math.sin(rad_dist);
            double t3 = t1/t2;
            if(Math.sin(lon2 - lon1) < 0)
            {
                rad_bearing = Utils.atan(-t3 /Math.sqrt(-t3 * t3 + 1)) + 2 * Utils.atan(1);
            }
            else
            {
                rad_bearing = 2 * Math.PI - (Utils.atan(-t3 / Math.sqrt(-t3 * t3 + 1)) + 2 * Utils.atan(1));
            }
            //prevod na stupne
            double bearing = Math.toDegrees(rad_bearing);
            bearing = 360 - bearing;
            return bearing;
        }
        catch (Exception e)
        {
            gui.showError("computeBearing",e.toString(),"");
            return 0;
        }
    }
    
    /**
     * Spocita korekcni smer k danym souradnicim ve stupnich
     */
    public double computeCorrectionDirection(double bearing, double heading)
    {
        try
        {
            double navigate = bearing - heading;
            if (navigate<0)
            {
                navigate = 360 + navigate;
            }
            return navigate;
        }
        catch (Exception e)
        {
            gui.showError("computeCorrectionDirection",e.toString(),"");
            return 0;
        }
    }
        
    public static String formatDeg(String source, boolean isLon) {
        double value = convertDegToDouble(source);
        
        if (value == Double.NaN)
            throw new IllegalArgumentException("Špatný formát souřadnic.");
        return convertDoubleToDeg(value, isLon);
    }
    
    public static String convertDoubleToDeg(double source, boolean isLon) {
        return convertDoubleToDeg(source, isLon, 3);
    }
    
    public static String convertDoubleToDeg(double source, boolean isLon, int precision) {
        StringBuffer sb = new StringBuffer();
        if (source < 0) {
            sb.append((!isLon) ? 'S':'W');
            source = -source;
        } else {
            sb.append((!isLon) ? 'N':'E');
        }
        sb.append(' ');

        int deg = (int) source;
        sb.append(deg);
        sb.append("° ");
        
        double min = (source - deg) * 60D;
               
        sb.append(Utils.round(min, precision));
        
        return sb.toString();
    }

    public static double convertDegToDouble(String source) {
        String tmp = source.trim().replace(',','.');

        int index = 0;
        int end = 0;

        char ch = ' ';

        double deg = 0D;
        double min = 0D;
        double sec = 0D;

        double direction = 1;

        try {
            ch = Character.toUpperCase(tmp.charAt(index));
            if (ch == 'S' || ch == 'W' || ch == '-') {
                direction = -1;
                index++;
            }
            if (ch == 'N' || ch == 'E' || ch == '+')
                index++;

            while (!Character.isDigit(tmp.charAt(index))) index++;
            end = getDoubleNumberEnd(tmp, index);
            deg = Float.parseFloat(tmp.substring(index, end));
            index = end;

            while (index < tmp.length() && !Character.isDigit(tmp.charAt(index))) index++;
            if (index < tmp.length()) {
                end = getDoubleNumberEnd(tmp, index);
                min = Double.parseDouble(tmp.substring(index, end));
                index = end;

                while (index < tmp.length() && !Character.isDigit(tmp.charAt(index))) index++;
                if (index < tmp.length()) {
                    end = getDoubleNumberEnd(tmp, index);
                    sec = Double.parseDouble(tmp.substring(index, end));
                    index = end;
                }
            }
            
            return direction * (deg + (min / 60D) + (sec / 3600D));
        } catch (Exception e) {
            return Float.NaN;
        }
    }

    private static int getDoubleNumberEnd(String source, int start) {
        for (int i = start; i < source.length(); i++) {
            if (!Character.isDigit(source.charAt(i)) && source.charAt(i) != '.') {
                return i;
            }
        }
        return source.length();
    }
    
    public static double[] coordinateProjection(double latitude, double longtitude, double azimuth, double distance) {
        double ret[] = new double[2];

        //source: geocaching_tool2.xls
        double ro = 180D / Math.PI;
        double R = 6378000D;
        
        double fi2 = Math.sin(latitude/ro)*Math.cos(distance/R)+Math.cos(latitude/ro)*Math.sin(distance/R)*Math.cos(azimuth/ro);
        System.out.println("fi2="+fi2);
        double lat = ro * MathUtil.asin(fi2);
        System.out.println("lat="+lat);
        double x = (Math.cos(distance/R)-Math.sin(latitude/ro)*Math.sin(lat/ro))/(Math.cos(latitude/ro)*Math.cos(lat/ro));
        System.out.println("x="+x);
        double y = Math.sin(distance/R)*Math.sin(azimuth/ro)/Math.cos(lat/ro);
        System.out.println("y="+y);
        double la2 = atan2(y, x); //MathUtil.atan2(y, x);
        System.out.println("la2="+la2);
        double lon = longtitude + la2*ro;
        System.out.println("lon="+lon);
              
        ret[0] = lat;
        ret[1] = lon;
        return ret;
    }
    
    /**
     * Vraci uhel bodu v radianech proti ose x
     * Implementace dle wikipedie s osetrenim y=0. MathUtil.atan2 
     * vraci divne vysledky pro urcite hodnoty.
     *
     * @param y souradnice bodu na ose y
     * @param x souradnice bodu na ose x
     * @return uhel bodu v radianech proti ose x
     */
    private static double atan2(double y, double x) {
        if (y == 0)
            return (x>=0) ? 0 : Math.PI;
        
        return 2*MathUtil.atan((Math.sqrt(x*x+y*y)-x)/y);
    }
    
}
