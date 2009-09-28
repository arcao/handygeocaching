/**
 * Gps.java
 *
 * Created on 27. duben 2007, 11:08
 *
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
import javax.microedition.xml.rpc.Element;
import track.Track;
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
    private static final long BREAK_NAVI = 500; //refresh
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
        thread = null;
        if (action==NAVIGATION || action==MAP )
        {
            track.stop();
        }
    }
    
    /**
     * Zmeni GPS akci, pouziva se pri prechodu mezi mapou a navigaci
     */
    public void changeAction(int act)
    {
        action = act;
    }
    
    /**
     * Nastavi navigační souřadnice
     */
    public void setNavigationTarget(String lattitude, String longitude, String name)
    {
        targetname = name;
        targetlat = convertLattitude(lattitude);
        targetlon = convertLongitude(longitude);
    }
    
    /**
     * Zjisti, jestli jsou zadany navigacni souřadnice
     */
    public boolean isNavigating()
    {
        return (targetlat!=0 && targetlon!=0);
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
                        double soucet1=0;
                        double soucet2=0;
                        int j;
                        for (j=0;j<deleno;j++)
                        {
                            soucet1 += buffer1[j];
                            soucet2 += buffer2[j];
                        }
                        //deleni
                        double podil = soucet1/deleno;
                        double degrees = Math.floor(Math.abs(podil));
                        double minutes = Math.abs(podil) - degrees;
                        minutes = minutes * 60;
                        String avLattitude = ((podil>0)?"N":"S")+" "+Utils.addZeros(String.valueOf((int)degrees),2)+"° "+String.valueOf(minutes).substring(0,6);
                        podil = soucet2/deleno;
                        degrees = Math.floor(Math.abs(podil));
                        minutes = Math.abs(podil) - degrees;
                        minutes = minutes * 60;
                        String avLongitude = ((podil>0)?"E":"W")+" "+Utils.addZeros(String.valueOf((int)degrees),3)+"° "+String.valueOf(minutes).substring(0,6);
                        //vypis
                        gui.get_frmAveraging().setTitle("Průměrování");
                        gui.get_siCurrentCoordinates().setText(gpsParser.getFriendlyLattitude() + "\n"+ gpsParser.getFriendlyLongitude());
                        gui.get_siAverageLattitude().setText(avLattitude +"\n");
                        gui.get_siAverageLongitude().setText(avLongitude +"\n");
                        gui.get_siMeasures().setText(deleno+"/100");
                        gui.get_siAdditional().setText(gpsParser.getSatelliteCount()+" sat./"+String.valueOf(gpsParser.getSpeed())+" km/h/"+String.valueOf(gpsParser.getAccuracy()));
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
                        
                    }
                    //Zephy 21.11.07 gpsstatus+\
                    else if (action == GPS_SIGNAL)
                    {  
                        gui.get_cvsSignal().latitude = gpsParser.getFriendlyLattitude();
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

                    }
                    //Zephy 21.11.07 gpsstatus+/

                    else if (action == MAP)
                    {
                        gui.get_cvsMap().fixMessage = "";
                        gui.get_cvsMap().latitude = gpsParser.getLatitude();
                        gui.get_cvsMap().longitude = gpsParser.getLongitude();
                        gui.get_cvsMap().heading = (int)gpsParser.getHeading();
                        gui.get_cvsMap().repaint();
                    }
                    else if (action == CURRENT_POSITION)
                    {
                        gui.get_frmCoordinates().setTitle("Souřadnice získány");
                        gui.get_tfLattitude().setString(gpsParser.getFriendlyLattitude());
                        gui.get_tfLongitude().setString(gpsParser.getFriendlyLongitude());
                        lattitude = String.valueOf(gpsParser.getLatitude());
                        longitude = String.valueOf(gpsParser.getLongitude());
                        stop();
                        http.start(Http.NEAREST_CACHES, false);
                    }
                    else //ziskani souradnic u oblibenych
                    {
                        gui.get_frmAddGiven().setTitle("Souřadnice získány");
                        gui.get_tfGivenLattitude().setString(gpsParser.getFriendlyLattitude());
                        gui.get_tfGivenLongitude().setString(gpsParser.getFriendlyLongitude());
                        stop();
                    }
                }
            }
            catch (Exception e)
            {
                gui.get_siDebug().setText("Chyba v Gps: "+e.toString()+"\nNMEA: "+gpsParser.getNmea()+"\nException: "+gpsParser.exception);
                gui.getDisplay().setCurrent(gui.get_frmDebug());
                this.stop();
            }
            try
            {
                if (thread != null)
                    thread.sleep((action == NAVIGATION && gpsParser.source != GpsParser.INTERNAL)? BREAK_NAVI : BREAK);
            }
            catch (InterruptedException e)
            {
            }
            try
            {
                //rozsviceni displeje
                if (settings.flashbackPeriod > 0)
                {
                    if (flashbackLightPause < ((action == NAVIGATION && gpsParser.source != GpsParser.INTERNAL)? 2 * (settings.flashbackPeriod*2-1): settings.flashbackPeriod*2-1))
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
                if (gpsParser.getNmeaCount() == lastNmeaCount && (gpsParser.source == GpsParser.BLUETOOTH || gpsParser.source == GpsParser.GPS_HGE_100))
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
                lastNmeaCount = gpsParser.getNmeaCount();
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
            return (int)navigate;
        }
        catch (Exception e)
        {
            gui.showError("computeCorrectionDirection",e.toString(),"");
            return 0;
        }
    }
    
    
    //Zephy 19.11.07 +\
    /**
     * Zkonvertuje lattitude format z jakehokoliv typu na "N Deg° mi.mmm" a vraci zpet v tomto formatu
     */
    public static String convertLattitudeFormat (String lat, boolean DegMinSecFormat)
    {
        if (!DegMinSecFormat)
        {
            //vzorec uz je ve vychozim formatu "N Deg° mi.mmm"
            return lat;
        }
        
        //prevedeni na zakladni tvar
         try
        {
            double lattitudeDegrees;
            
            lattitudeDegrees = (
                 Integer.parseInt  (lat.substring(6,8))
                + (Double.parseDouble(lat.substring(9,15))/60));
            String tmpOut = (lat.substring(0,6) + String.valueOf(lattitudeDegrees));
            return tmpOut.substring(0, 12);
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
            return "";
        }
               
    }

    /**
     * Zkonvertuje lattitude format z jakehokoliv typu na "N Deg° mi.mmm" a vraci zpet v tomto formatu
     */
    public static String convertLongitudeFormat (String lon, boolean DegMinSecFormat)
    {
        if (!DegMinSecFormat)
        {
            //vzorec uz je ve vychozim formatu "N Deg° mi.mmm"
            return lon;
        }
        
        //prevedeni na zakladni tvar
         try
        {
            double longitudeDegrees;
            String[] Elements = StringTokenizer.getArray(lon, " ");
            if (Elements.length < 4)
            {
                return "";
            }
            
            longitudeDegrees = (
                Integer.parseInt  (lon.substring(7,9))
                +(Double.parseDouble(lon.substring(10,15))/60));
            String tmpOut =  (lon.substring(0,7) + String.valueOf(longitudeDegrees));
            return tmpOut.substring(0, 13);
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
            return "";
        }
    }
    //Zephy 19.11.07 +/
    /**
     * Zkonvertuje lattitude z typu N AA° AA.AAA do AA.AAAAA
     */
    public static double convertLattitude(String lat)
    {
        try
        {
            int direction;
            if (lat.substring(0,1).equals("N"))
            {
                direction = 1;
            }
            else if (lat.substring(0,1).equals("S"))
            {
                direction = -1;
            }
            else
            {
                throw new Exception("lattitude neni N nebo S");
            }
            
            if (!lat.substring(4,6).equals("° ") && !lat.substring(4,6).equals("  ")) {
                throw new Exception("spatny format stupnu u lattitude ");
            }
            
            double lattitudeDegrees = direction * (Integer.parseInt(lat.substring(2,4))+Double.parseDouble(lat.substring(6,12))/60);
            
            return lattitudeDegrees;
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
            return Double.NaN;
        }
    }
    
    /**
     * Zkonvertuje longitude z typu N AAA° AA.AAA do AAA.AAAAA
     */
    public static double convertLongitude(String lon)
    {
        try
        {
            int direction;
            if (lon.substring(0,1).equals("E"))
            {
                direction = 1;
            }
            else if (lon.substring(0,1).equals("W"))
            {
                direction = -1;
            }
            else
            {
                throw new Exception("longitude neni W nebo E");
            }
            
            if (!lon.substring(4,6).equals("° ") && !lon.substring(4,6).equals("  ") && !lon.substring(5,7).equals("° ") && !lon.substring(5,7).equals("  ")) {
                throw new Exception("spatny format stupnu u longitude");
            }
            
            double longitudeDegrees;
            if (lon.substring(4,6).equals("° ") || lon.substring(4,6).equals("  "))
            {
                longitudeDegrees = direction * (Integer.parseInt(lon.substring(2,4))+Double.parseDouble(lon.substring(6,12))/60);
            }
            else
            {
                longitudeDegrees = direction * (Integer.parseInt(lon.substring(2,5))+Double.parseDouble(lon.substring(7,13))/60);
            }
            
            return longitudeDegrees;
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
            return Double.NaN;
        }
    }
    
    
}