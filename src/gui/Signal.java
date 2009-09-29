/*
 * Signal.java
 * This file is part of HandyGeocaching.
 *
 * HandyGeocaching is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * (read more at: http://www.gnu.org/licenses/gpl.html)
 */
package gui;


import gps.Gps;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;



/**
 * Trida zobrazuje stavy signalu GPS
 * @author Zephy
 */
public class Signal extends Canvas
{
    public String speed = "";
    public String satellitescount = "";
    public String altitude = "";
    
    public String latitude = "";
    public String longitude = "";
    public String satelliteslist = "";
    public String signallist="";
    public String activlist="";
    public String ServiceMenuCode = "";
    
    public Hashtable signaldata;
    public int[]  activeSat;
            
    public String pdop = "";
    public String hdop = "";
    public String vdop = "";
    public int[] TopTenSignal = new int[10];
    private boolean ZobrazitDetailSatelitu = false;
    
    private Gui gui;
    private Gps gps;
    private int nVyskaRadku = 16; //Vyska jednoho radku textu
    private int nHorniOkraj = 7;
    
    public Signal(Gui ref, Gps ref2)
    {
        gui = ref;
        gps = ref2;
        //pokusne hodnoty
        /*
        satellitescount = "5/11 sat.";
        speed = "330 km/h";
        altitude = "300 m.n.m";

        latitude = "N 49° 58.2386";
        longitude = "E 016° 58.0063";
        pdop = "2.5";
        vdop = "4.0";
        hdop = "6.0";
        satelliteslist = "0|0|0|0|0|0|";
        activlist = "0|0|0|0|0|0|";
        signallist = "0|0|0|0|0|0|";
        */
    }
    
    /** 
     * Tato metoda vykresluje navigacni obrazovku
     */
    public void paint(Graphics g)
    {
        try
        {
            int width = getWidth();
            int height = getHeight();
         
            //pozadi
            g.setColor(128,255,255);
            g.fillRect(0, 0, width, height);
            g.setColor(0xffffffff);
            g.fillRect(5, 5, width - 10, height - 30);
            //nakresleni oddelovacu 
            //- prvni oddelovac ze shora
            g.setColor(128,255,255);
            g.fillRect(0, 7 + (2 * nVyskaRadku), width, 2);
            //svislej
            g.fillRect(width/2, 7 + (2 * nVyskaRadku), 2, nVyskaRadku+4);
            //cara pod vyskou a rychlosti
            g.fillRect(0, 9 + (3 * nVyskaRadku), width, 2);
            //cara nad silou signalu
            g.fillRect(0, height - 92, width, 2);
            //cara nad DOP
            g.fillRect(0, height - 94 - nVyskaRadku , width, 2);
            //svisla cara pdop/hdop
            g.fillRect((width * 1/3)+2, height - 94 - nVyskaRadku, 2, nVyskaRadku+4);
            //svisla cara hdop/vdop
            g.fillRect((width * 2/3)+2, height - 94 - nVyskaRadku, 2, nVyskaRadku+4);
            

            //zobrazovane textove hodnoty
            g.setColor(0);            
            g.setFont(gui.get_fntSmallBold());
            
            g.drawString( "Lat:"  , 7, 7, Graphics.TOP|Graphics.LEFT);
            g.drawString( "Lon:"  , 7, 7 + nVyskaRadku, Graphics.TOP|Graphics.LEFT);
            
            g.drawString( latitude, 40, 7, Graphics.TOP|Graphics.LEFT);
            g.drawString( longitude, 40, 7 + nVyskaRadku, Graphics.TOP|Graphics.LEFT);
            
            g.setFont(gui.get_fntSmall());
            g.drawString("Rychl: "+speed, 7, 9 + (2 * nVyskaRadku), Graphics.TOP|Graphics.LEFT);
            g.drawString("Výška: "+altitude, (width * 1/2)+5, 9 + (2 * nVyskaRadku), Graphics.TOP|Graphics.LEFT);

            g.drawString("PDOP: "+pdop,7 , height - 107, Graphics.TOP|Graphics.LEFT);            
            g.drawString("HDOP: "+hdop,(width * 1/3)+7, height - 107, Graphics.TOP|Graphics.LEFT);            
            g.drawString("VDOP: "+vdop,(width * 2/3)+7, height - 107, Graphics.TOP|Graphics.LEFT);            

            g.drawString("Nápověda", 7, height - nVyskaRadku - 4,Graphics.TOP|Graphics.LEFT);
            g.drawString("Sat: " + satellitescount, width/2, height - nVyskaRadku - 4, Graphics.TOP|Graphics.HCENTER);
            g.drawString("Zpět", width-35, height - nVyskaRadku - 4, Graphics.TOP|Graphics.LEFT);
            //tajne menu
            if (ZobrazitDetailSatelitu)
            {
                //seznam nazvu satelitu
                g.setColor(0,0,255);
                satelliteslist = getSatellitesList();
                g.drawString( satelliteslist, 7, (height - 94 - nVyskaRadku*2), Graphics.LEFT|Graphics.TOP);
                g.setColor(0);
                //zobrazeni sily signalu (textem)
                g.drawString( signallist, 7, (height - 94 - (nVyskaRadku*3)), Graphics.LEFT|Graphics.TOP);
                //aktivni satelity            
                g.setColor(0,255,0);
                activlist = getActivSatList();
                g.drawString( activlist, 7, (height - 94 - (nVyskaRadku*4)), Graphics.LEFT|Graphics.TOP);               
                //seznam nejsilnejsich signalu
                g.setColor(255,0,0);
                g.drawString( getTopTenList(), 7, (height - 94 - (nVyskaRadku*5)), Graphics.LEFT|Graphics.TOP);
                g.setColor(0);
            }
            
            //zobrazeni signalu
            g.setColor(0,255,0);
            signallist = DrawSignalRect(g, width, height);
            //zobrazeni prumeru
            DrawAverageSignal(g, width, height);
            
            
        }
        catch (Exception e)
        {
            gui.showError("signal paint", e.toString(), "");
        }
    }
    
        public String getSatellitesList()
    {
                                       
        String SeznamSatelitu = "";
        if (signaldata != null)
        {
            Enumeration tmpEnum = signaldata.keys();
            while (tmpEnum.hasMoreElements())
            {
                 String key = (String)tmpEnum.nextElement();
                 SeznamSatelitu = SeznamSatelitu + key + "|";

            }
        }
        return SeznamSatelitu;
    }

        public String getActivSatList()
    {

        String SeznamAktivnichSatelitu = "";
        if (activeSat != null)
        {
            for(int i = 0; i < 11;i++)
            {
                if (activeSat[i] > 0)
                {
                    SeznamAktivnichSatelitu = SeznamAktivnichSatelitu + activeSat[i] + "|";
                }    
            }
        }

        return SeznamAktivnichSatelitu;
    }        
        

     /**
     * Nakresli obdelniky signalu.
     */
    public String DrawSignalRect(Graphics g, int SirkaDisplaye, int VyskaDisplaye)
    {
        String siglist = "";
        
        int Index = 0;
        int nSpodek = VyskaDisplaye - 40;
        int nVyska = 50;
        int nSirkaSloupce = (SirkaDisplaye - 10) / 12;  //podle sirky displaye vypocitam jakou muzu mit sirku indikatoru pro 12 satelitu
        int tmpNull[] = new int[10];
        
        System.arraycopy(tmpNull, 0, TopTenSignal, 0, 10);
        
        if (signaldata != null)
        {
            Enumeration tmpEnum = signaldata.keys();
            while (tmpEnum.hasMoreElements())
            {
                 String key = (String)tmpEnum.nextElement();
                 g.setColor(255,0,0);
                 if (IsActivated(Integer.valueOf(key).intValue()))
                 {
                     g.setColor(0,255,0);
                 }
                 String tmp = (String)signaldata.get(key);
                 if (tmp.equals(""))
                 {
                     tmp = "00";   
                 }
                 int Signal = 0;
                 try
                 {
                    Signal =  Integer.valueOf(tmp).intValue();
                 }
                 catch (Exception e)
                 {
                    gui.showError("prevod sily signalu na cislo", e.toString(), "");
                    
                 }                 
                 
                 g.fillRect(7+ (nSirkaSloupce * Index), nSpodek - Signal, (nSirkaSloupce-1), Signal);
                 siglist = siglist + tmp + "|";
                 setTopTen(Signal);

                 Index++;
            }
        }
        g.setColor(0);
        //cara vymezujici aktivni/neaktivni signal
        g.drawLine(5, nSpodek - 25, SirkaDisplaye - 6, nSpodek - 25);
        //ramecek kolem sily signalu
        g.drawRect(5, nSpodek - nVyska, SirkaDisplaye - 11, nVyska);
        //ramecek kolem seznamu satelitu
       // g.drawRect(0, nSpodek + 12, getWidth()-1, 12);
        return siglist;
        
    }

    public void DrawAverageSignal(Graphics g, int SirkaDisplaye, int VyskaDisplaye)
    {
        int nSpodek = VyskaDisplaye - 40;
        
        int nAverage3 = ((TopTenSignal[0]+TopTenSignal[1]+TopTenSignal[2]) / 3);
        int nAverage4 = ((TopTenSignal[0]+TopTenSignal[1]+TopTenSignal[2]+TopTenSignal[3]) / 4);
        int nSirkaSloupce = (SirkaDisplaye *(nAverage3)/45);
        if (nSirkaSloupce > (SirkaDisplaye - 11))
        {
            //omezeni aby to nelezlo za roh
            nSirkaSloupce = (SirkaDisplaye - 11);
        }
         
        //sila prumeru tri satelitu
        if (nAverage3 < 25)
        {
            g.setColor(255,0,0);
        }
        else if (nAverage3 >= 20 && nAverage3 <=34)
        {
            g.setColor(255,128,0);
        }
        else if (nAverage3 >= 35)
        {
            g.setColor(0,255,0);
        }
        g.fillRect(6, nSpodek+1, nSirkaSloupce, nVyskaRadku);
        //ramecek kolem
        g.setColor(0);
        g.drawRect(5, nSpodek, (SirkaDisplaye - 11), nVyskaRadku); 
        //textova hodnota
        g.setFont(gui.get_fntSmallBold());
        g.setColor(0);
        g.drawString("3: "+String.valueOf(nAverage3)+", 4: "+String.valueOf(nAverage4), 7,nSpodek+1,Graphics.TOP|Graphics.LEFT);
        
    }
    
    public String getTopTenList()
    {
        String tmpList = "";
        for(int i=0; i < 10;i++)
        {
            tmpList = tmpList + String.valueOf( TopTenSignal[i] ) + "|";
        }
        return tmpList;
    }
    
    public void setTopTen(int Signal)
    {
        boolean bPokracovat = true;
        int index = 0;
        while( (index < 10) && bPokracovat )
        {
            if (TopTenSignal[index] < Signal)
            {
                
                if (index < 9)
                {
                    System.arraycopy(TopTenSignal, index, TopTenSignal, index+1, (9 - index));
                }
                
                TopTenSignal[index] = Signal;
                bPokracovat = false;
            }
            index++;
           
        }
    }
    
    
    public boolean  IsActivated(int SatId)
    {
        int index = 0;
        while (activeSat[index] > 0)
        {
            if (activeSat[index] == SatId)
            {
                return true;
            }
            index++;
        }
        return false;
    }
    
    /**
     * Osetreni stisknuti leveho a praveho kontextoveho tlacitka
     */
    public void keyPressed(int keyCode)
    {
        repaint();
        //leve tlacitko
        if (keyCode == -6 || keyCode == -21 || keyCode == -20 || keyCode == 105 || keyCode == 21 || keyCode == -202 || keyCode == 113)
        {
            gui.getDisplay().setCurrent(gui.get_frmGpsSignalHelp());
        }
        //prave tlacitko
        if (keyCode == -7 || keyCode == 112 || keyCode == 111)
        {
            gps.stop();
            gui.getDisplay().setCurrent(gps.getPreviousScreen());            
        }
        //Odemknuti Service menu
        if (      (keyCode == 57 && ServiceMenuCode.length() == 0)
                ||(keyCode == 51 && ServiceMenuCode.length() == 1)
                ||(keyCode == 55 && ServiceMenuCode.length() == 2)
                ||(keyCode == 52 && ServiceMenuCode.length() == 3)
                ||(keyCode == 57 && ServiceMenuCode.length() == 4) 
                )
        {
            ServiceMenuCode = ServiceMenuCode + "x";
            if (ServiceMenuCode.length() == 5)
            {
                ZobrazitDetailSatelitu = (!ZobrazitDetailSatelitu);
            }
        }
        else
        {
            ServiceMenuCode = "";
        }
        
         
    }
}
