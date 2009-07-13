package gps;

import database.Favourites;
import database.Settings;
import gui.Gui;
import http.Http;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Hashtable;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.lcdui.AlertType;
import utils.References;
import utils.StringTokenizer;

/**
 * Tato trida se stara o zpracovani NMEA zprav zasilanych od GPS
 */
public class GpsParser implements Runnable
{
    //reference
    private Gui gui;
    private Bluetooth bluetooth;
    private Settings settings;
    private Favourites favourites;
    private Http http;
    private static Internal internal;
    
    //gps udaje
    protected double latitude;
    protected double longitude;
    protected String friendlyLattitude;
    protected String friendlyLongitude;
    protected double heading = 0, speed = 0, altitude = 0;
    protected double accuracy = 50;
    protected int allSatellites, fixSatellites;
    protected boolean fix;
    protected int hour, minute, second;
    protected int day, month, year;
    protected int nmeaCount;
    //Zephy 21.11.07 gpsstatus+\
    protected Hashtable signaldata = new Hashtable(); //Obsahuje cisla satelitu a jejich signal;
    //Zephy oprava 21.12.07 +\
    protected int activsat[] = new int[30];           //seznam aktivnich satelitu
    //Zephy oprava 21.12.07 +/
    protected String pdop, hdop, vdop;
    //Zephy 21.11.07 gpsstatus+/
    
    
    private String communicationURL;
    protected String nmea;
    public String exception = "";
    
    //stavy
    private boolean firstRun = true;
    //zdroje dat
    public static final int BLUETOOTH = 0;
    public static final int GPS_GATE = 1;
    public static final int INTERNAL = 2;
    public int source;
    
    private Thread thread;
    
    /**
     * Pripojeni k neznamemu zarizeni
     */
    public GpsParser(Gui ref, Http ref2, Settings ref3, Favourites ref4, Bluetooth ref5, String address, int gpsSource)
    {
        nmeaCount = 0;
        gui = ref;
        http = ref2;
        settings = ref3;
        favourites = ref4;
        bluetooth = ref5;
        communicationURL = address;
        source = gpsSource;
        if (source == INTERNAL)
        {
            internal = References.getInternal(gui, this, settings);
        }
    }
    
    /**
     * getry a setry jednotlivych private promennych
     */
    public boolean isOpen()
    {
        return thread != null;
    }
    
    public int getNmeaCount()
    {
        return nmeaCount;
    }
    
    public double getHeading()
    {
        return heading;
    }
    
    /**
     * Vraci rychlost v km/h
     */
    public long getSpeed()
    {
        if (source==INTERNAL)
            return (long)(speed*3.6);
        return (long)(speed*1.852);
    }
    
    public double getAltitude()
    {
        return altitude;
    }
    
    public String getSatelliteCount()
    {
        if (source==INTERNAL)
            return "N/A";
        else
            return fixSatellites+"/"+allSatellites;
    }
    
    public String getAccuracy()
    {
        if (source==INTERNAL)
            return String.valueOf((int)accuracy)+" m";
        else
            return accuracy+"(PDOP)";
    }
    //Zephy 21.11.07 gpsstatus+\
    public String getPDOP()
    {
        return pdop;
    }
    
    public String getHDOP()
    {
        return hdop;
    }
    
    public String getVDOP()
    {
        return vdop;
    }
    //Zephy 21.11.07 gpsstatus+/
    public boolean hasFix()
    {
        return fix;
    }
    
    public double getLatitude()
    {
        return latitude;
    }
    
    public double getLongitude()
    {
        return longitude;
    }
    
    public String getFriendlyLattitude()
    {
        return friendlyLattitude;
    }
    
    public String getFriendlyLongitude()
    {
        return friendlyLongitude;
    }
    
    public String getDateTime()
    {
        return day+"."+month+". "+hour+":"+minute;
    }
    
    public String getNmea()
    {
        return nmea;
    }
    
    public boolean stillConnecting()
    {
        return firstRun;
    }
    
    //Zephy 21.11.07 gpsstatus+\
    public Hashtable getSignalData()
    {
        return signaldata;
    }
    
    public void clearSignalData()
    {
        signaldata.clear();
    }
    
    public int[] getActivSat()
    {
        return activsat;
    }
    //Zephy 21.11.07 gpsstatus+/
    
    /**
     * Otevreni spojeni z GPSkou
     */
    public void open()
    {
        close();
        thread = new Thread(this);
        thread.start();
        
    }
    
    /**
     * Zavreni spojeni z GPSkou
     */
    public void close()
    {
        nmeaCount = 0;
        thread = null;
    }
    
    public void connectionSuccessfull()
    {
        Gps gps = new Gps(gui, http, this, settings);
        http.setReference(gps);
        gui.setReference(gps);
        favourites.setReference(gps);
        gui.getDisplay().setCurrent( gui.get_lstMenu());
        if (source==BLUETOOTH)
            settings.saveLastDevice(communicationURL);
    }
    
    /**
     * Vlakno NMEA komunikace s GPS, na nic neceka, porad parsuje data
     */
    public void run()
    {
        
        StreamConnection streamConnection = null;
        InputStream inputStream = null;
        try
        {
            try
            {
                streamConnection = (StreamConnection)Connector.open(communicationURL);
                inputStream = streamConnection.openInputStream();
            }
            catch (Exception e)
            {
                exception = e.toString();
                if (source == GPS_GATE)
                {
                    gui.showAlert("Program GPS Gate není spuštěn nebo správně nastaven."+e.toString(),AlertType.ERROR,gui.get_lstMode());
                }
                else if (source == BLUETOOTH)
                {
                    //nezdarilo se pripojit k poslednimu zarizeni - hledame jina zarizeni v dosahu
                    gui.get_frmConnecting().append("\nPřipojení se nezdařilo");
                    gui.searchBluetooth();
                }
                close();
            }
            
            //uspesne pripojeni
            if (thread != null)
            {
                connectionSuccessfull();
            }
            
            //cteni dat
            ByteArrayOutputStream byteArrayOutputStream = null;
            while (thread != null)
            {
                try
                {
                    String s;
                    byteArrayOutputStream = new ByteArrayOutputStream();
                    int ch = 0;
                    //cteni dat
                    if (inputStream != null)
                        while ( (ch = inputStream.read()) != '\n')
                        {
                        byteArrayOutputStream.write(ch);
                        }
                    byteArrayOutputStream.flush();
                    byte[] b = byteArrayOutputStream.toByteArray();
                    s = new String(b);
                    nmea = s;
                    receiveNmea(s);
                    byteArrayOutputStream.close();
                }
                catch (Exception ex)
                {
                    exception = ex.toString();
                }
            }
            
            if (inputStream != null)
                inputStream.close();
            if (streamConnection != null)
                streamConnection.close();
        }
        catch (Exception ex)
        {
            exception = ex.toString();
        }
        
    }
    
    /**
     * Funkce pro zjisteni souradnic s ruznych NMEA zprav
     */
    private void extractData(String [] param, int a, int b, int c, int d, int e)
    {
        try
        {
            int degree, minute, fraction;
            String friendlyFraction;
            double f;
            if (param[a].length() > 8 && param[b].length() == 1)
            {
                degree = Integer.parseInt(param[a].substring(0, 2));
                minute = Integer.parseInt(param[a].substring(2, 4));
                fraction = Integer.parseInt(param[a].substring(5, 9).concat("0000").substring(0, 4));
                friendlyFraction = param[a].substring(5, 9).concat("0000").substring(0, 4);
                latitude = degree + ((double)minute+(double)fraction/10000)/60;
                if (param[b].charAt(0) == 'S')
                    latitude =  -latitude;
                friendlyLattitude = param[b].charAt(0)+" "+degree+"° "+minute+"."+friendlyFraction;
            }
            if (param[c].length() > 9 && param[d].length() == 1)
            {
                degree = Integer.parseInt(param[c].substring(0, 3));
                String degree2 = param[c].substring(0, 3);
                minute = Integer.parseInt(param[c].substring(3, 5));
                fraction = Integer.parseInt(param[c].substring(6, 10).concat("0000").substring(0, 4));
                friendlyFraction = param[c].substring(6, 10).concat("0000").substring(0, 4);
                longitude = degree + ((double)minute+(double)fraction/10000)/60;
                if (param[d].charAt(0) == 'W')
                    longitude =  -longitude;
                friendlyLongitude = param[d].charAt(0)+" "+degree2+"° "+minute+"."+friendlyFraction;
            }
            if (param[e].length() > 5)
            {
                hour = Integer.parseInt(param[e].substring(0, 2));
                minute = Integer.parseInt(param[e].substring(2, 4));
                second = Integer.parseInt(param[e].substring(4, 6));
            }
            
        }
        catch (Exception ex)
        {
            gui.showError("extractData",ex.toString(),"");
        }
    }
    //Zephy 21.11.07 gpsstatus+\
    protected void saveActivSat(String[] sat)
    {
        int Index = 0;
        int PocetPrvkuPole = sat.length;
        for(int i = 3; i <=14; i++)
        {
            //Zephy oprava 20.12.07 +\
            if (i <= PocetPrvkuPole)    //ochrana aby promenna "i" nelezla mimo rozsah pole
            {
                if (sat[i].equals(""))
                {
                    activsat[Index] = 0;
                }
                else
                {
                    activsat[Index] = Integer.parseInt(sat[i]);
                }
                Index++;
            }
            //Zephy oprava 20.12.07 +/
        }
        
    }
    //Zephy 21.11.07 gpsstatus+/
    /**
     * Parsovani NMEA zprav a ziskavani potrebnych udaju
     */
    protected void receiveNmea(String nmea)
    {
        try
        {
            int starIndex = nmea.indexOf('*');
            if (starIndex == -1)
                return;
            String [] param = StringTokenizer.getArray(nmea.substring(0, starIndex), ",");
            //Zephy oprava 20.12.07 +\
            int PocetPrvkuParam = param.length;
            //Zephy oprava 20.12.07 +/
            if (param[0].equals("$GPGSV"))
            {
                int i, j;
                nmeaCount++;
                allSatellites = Integer.parseInt(param[3]);
                //Zephy 21.11.07 gpsstatus+\
                if (param[2].equals("1"))
                {
                    signaldata.clear();
                }

                //Zephy oprava 20.12.07 +\
                //1.cast
                if ((PocetPrvkuParam > 7) )    //kontrola zda nelezu mimo rozsah
                {
                    String key = NullToNula(param[4]);
                    String val = NullToNula(param[7]);
                    signaldata.put(key, val);                    
                }
                //2.cast
                if ((PocetPrvkuParam >11) )    //kontrola zda nelezu mimo rozsah
                {
                   String key = NullToNula(param[8]);
                   String val = NullToNula(param[11]);
                   signaldata.put(key, val);                    
                }

                //3.cast
                if ((PocetPrvkuParam >15) )    //kontrola zda nelezu mimo rozsah
                {
                    String key = NullToNula(param[12]);
                    String val = NullToNula(param[15]);
                    signaldata.put(key, val);
                }

                //4.cast
                if ((PocetPrvkuParam >19) )    //kontrola zda nelezu mimo rozsah
                {
                    String key = NullToNula(param[16]);
                    String val = NullToNula(param[19]);
                    signaldata.put(key, val);                    
                }
                //Zephy oprava 20.12.07 +/
                
                //Zephy 21.11.07 gpsstatus+/
                
            }
            else if (param[0].equals("$GPGLL"))
            {
                nmeaCount++;
                extractData(param, 1, 2, 3, 4, 5);
                fix = (param[6].charAt(0) == 'A');
            }
            else if (param[0].equals("$GPRMC"))
            {
                nmeaCount++;
                extractData(param, 3, 4, 5, 6, 1);
                fix = (param[2].charAt(0) == 'A');
                if (fix)
                {
                    day = Integer.parseInt(param[9].substring(0, 2));
                    month = Integer.parseInt(param[9].substring(2, 4));
                    year = 2000 + Integer.parseInt(param[9].substring(4, 6));
                    if (param[7].length() > 0)
                        speed = Double.parseDouble(param[7]);
                    else
                        speed = 0;
                    if (param[8].length() > 0)
                        heading = Double.parseDouble(param[8]);
                }
            }
            else if (param[0].equals("$GPGGA"))
            {
                nmeaCount++;
                if (param[6].equals("0"))
                {
                    fix = false;
                    //Zephy 21.11.07 gpsstatus+\
                    fixSatellites = 0;
                    //Zephy 21.11.07 gpsstatus+/
                }
                else
                {
                    extractData(param, 2, 3, 4, 5, 1);
                    fixSatellites = Integer.parseInt(param[7]);
                    if (param[9].length() > 0)
                        altitude = Double.parseDouble(param[9]);
                }
            }
            else if (param[0].equals("$GPGSA"))
            {
                //Zephy 21.11.07 gpsstatus+\
                //Zephy oprava 20.12.07 +\
                if (param.length >= 17)     //ochrana jestli veta ma vubec tolik prvku. I kdyz je divny ze by nemela.
                {
                    if (param[15].length() > 0)
                    {
                        accuracy = Double.parseDouble(param[15]);
                        pdop = param[15];
                    }
                    if (param[16].length() > 0)
                    {
                        hdop = param[16];
                    }
                    if (param[17].length() > 0)
                    {
                        vdop = param[17];
                    }

                    saveActivSat(param);
                    //Zephy 21.11.07 gpsstatus+/
                }
                //Zephy oprava 20.12.07 +/
                else
                {
                        accuracy = 0;
                        pdop = "0";
                        hdop = "0";
                        vdop = "0";
                }
                nmeaCount++;
            }
            else if (!param[0].equals(null))
            {
                nmeaCount++;
            }
        }
        catch (Exception e)
        {
            gui.showError("receiveNmea",e.toString(),nmea);
        }
    }
   
    private String NullToNula(String Hodnota)
    {
        if (Hodnota.length() == 0)
        {
            return "00";
        }
        else
        {
            return Hodnota;
        }
    }
    
}


