/*
 * Navigation.java
 *
 * Created on 7. záøí 2007, 15:05
 *
 */

package gui;

import database.Favourites;
import gps.Gps;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

/**
 * Tato trida reprezentuje navigacni obrazovku, zobrazuje sipku a dalsi udaje
 * @author David Vavra
 */
public class Navigation extends Canvas
{
    public String distance = "";
    public String speed = "";
    public String satellites = "";
    public String altitude = "";
    public String cacheName = "";
    public String accuracy = "";
    public static int angle = 0;
    public static int compass = 0;
    public String azimut = "";
    public String dateTime = "";
    public Image[] arrows, numbers;
    private int image;
    private int transformation;
    private Image rotatedImage;
    private int compassMovement;
    private String[] compassDirections;
    
    private Gui gui;
    private Gps gps;
    private Favourites favourites;
    
    private final double RHO = 180/Math.PI;
    
    private int cX;
    private int cY;
    
    private int radius;

    private boolean viewModeSmall = false;
    private boolean smallRadius;
    
    private int TOP_MARGIN;
    private int BOTTOM_MARGIN;

    
    public Navigation(Gui ref, Gps ref2, Favourites ref3)
    {
        try
        {
            gui = ref;
            gps = ref2;
            favourites = ref3;
            arrows = new Image[4];
            compassDirections = new String[4];
            /*arrows[0] = Image.createImage("/sipka0.png");
            arrows[1] = Image.createImage("/sipka225.png");
            arrows[2] = Image.createImage("/sipka45.png");
            arrows[3] = Image.createImage("/sipka675.png");*/
            
            numbers = new Image[12];
            numbers[0] = Image.createImage("/images/compass/numberN.png");
            numbers[1] = Image.createImage("/images/compass/number030.png");
            numbers[2] = Image.createImage("/images/compass/number060.png");
            numbers[3] = Image.createImage("/images/compass/numberE.png");
            numbers[4] = Image.createImage("/images/compass/number120.png");
            numbers[5] = Image.createImage("/images/compass/number150.png");
            numbers[6] = Image.createImage("/images/compass/numberS.png");
            numbers[7] = Image.createImage("/images/compass/number210.png");
            numbers[8] = Image.createImage("/images/compass/number240.png");
            numbers[9] = Image.createImage("/images/compass/numberW.png");
            numbers[10] = Image.createImage("/images/compass/number300.png");
            numbers[11] = Image.createImage("/images/compass/number330.png");
            
            calculateSizes();

        }
        catch (Exception e)
        {
            gui.showError("tvoreni sipky", e.toString(), "");
        }
       /* distance = "220 m";
        satellites = "5/11 sat.";
        speed = "330 km/h";
        altitude = "300 m.n.m";
        cacheName = "Název keše";
        angle = 270;
        compass = 275;
        PDOP = "PDOP:2.0";
        azimut = "30°";
        dateTime = "1.12 13:46";*/
        
    }
    
    /** 
     * Tato metoda vykresluje navigacni obrazovku
     */
    public void paint(Graphics g)
    {
        try
        {
            calculateSizes();
            int width = getWidth();
            int height = getHeight();
            
            //vymazeme obrazovku
            g.setColor(0xffffff);
            g.fillRect(0, 0, width, height);
            
            //nastavime kompas a smer
            setCompas(g);
            setArrow(g);
            
            int startY = cY + radius + 5;
                    
            //kresleni textu
            if (width<140) //male displeje
            {
                //nadpis
                g.setColor(0,0,0); //black
                g.setFont(gui.get_fntSmallBold());
                g.drawString(cacheName,width/2,1, Graphics.TOP|Graphics.HCENTER);
                
                if (viewModeSmall) {
                    g.setColor(0);
                    g.setFont(gui.get_fntSmallBold());
                    g.drawString(distance,width/2,startY,Graphics.TOP|Graphics.HCENTER);
                } else {
                    //ostatni napisy
                    g.setColor(0);
                    g.setFont(gui.get_fntSmallBold());
                    g.drawString(distance,width/2,startY,Graphics.TOP|Graphics.HCENTER);
                    g.setFont(gui.get_fntSmall());
                    g.drawString(" Az.:"+azimut+" "+accuracy,width/2,startY + 11,Graphics.TOP|Graphics.HCENTER);
                    g.drawString(speed+" "+satellites,width/2,startY + 22,Graphics.TOP|Graphics.HCENTER);
                    g.drawString(dateTime+" "+altitude,width/2,startY + 33,Graphics.TOP|Graphics.HCENTER);
                }

                //tlacitko zpet
                g.drawString("Zpìt",3,height-BOTTOM_MARGIN,Graphics.TOP|Graphics.LEFT);
                //tlacitko mapa
                g.drawString("Mapa", width-35,height-BOTTOM_MARGIN,Graphics.TOP|Graphics.LEFT);
            }
            else //velke displeje
            {
                //nadpis
                g.setColor(0,0,0); //black
                g.setFont(gui.get_fntBold());
                g.drawString(cacheName,width/2,1, Graphics.TOP|Graphics.HCENTER);
                
                if (viewModeSmall) {
                    g.setColor(0);
                    g.setFont(gui.get_fntBold());
                    g.drawString(distance,width/2,startY,Graphics.TOP|Graphics.HCENTER);
                } else {
                    //ostatni napisy
                    g.setColor(0);
                    g.setFont(gui.get_fntBold());
                    g.drawString(distance,width/2,startY,Graphics.TOP|Graphics.HCENTER);
                    g.setFont(gui.get_fntNormal());
                    g.drawString(" Azimut: "+azimut+" | "+accuracy,width/2,startY+18,Graphics.TOP|Graphics.HCENTER);
                    g.drawString(speed+" | "+satellites,width/2,startY+36,Graphics.TOP|Graphics.HCENTER);
                    g.drawString(dateTime+" | "+altitude,width/2,startY+54,Graphics.TOP|Graphics.HCENTER);
                    g.setFont(gui.get_fntBold());
                }
                //tlacitko zpet
                g.drawString("Zpìt",3,height-BOTTOM_MARGIN,Graphics.TOP|Graphics.LEFT);
                //tlacitko mapa
                g.drawString("Mapa", width-44,height-BOTTOM_MARGIN,Graphics.TOP|Graphics.LEFT);
            }
        }
        catch (Exception e)
        {
            gui.showError("navigation paint", e.toString(), "");
        }
    }
    
    // angle in degres
    private void setCompas(Graphics g) {
        g.setColor(255,255,255); //white
        g.fillArc(cX - radius, cY - radius, 2 * radius, 2 * radius, 0, 360);

        g.setColor(0,0,0); //black
        g.drawArc(cX - radius, cY - radius, 2 * radius, 2 * radius, 0, 360);

        double a;
        int x1, x2, y1, y2;
        int lineLength = radius - 10;
        int stringPosition = radius - 23;
        if (smallRadius) {
            lineLength = radius - 5;
            stringPosition = radius - 18;
        }

        for (int i = 0; i < 36; i++) {
            if (smallRadius && i % 3 != 0) {
                continue;
            }

            a = (i * 10 - compass) / RHO;

            double aSin = Math.sin(a);
            double aCos = Math.cos(a);
            x1 = (int) (aSin * lineLength);
            y1 = (int) (aCos * lineLength);
            x2 = (int) (aSin * radius);
            y2 = (int) (aCos * radius);
            g.drawLine(cX + x1, cY - y1, cX + x2, cY - y2);

            int separator = 3;
            if (smallRadius) {
                separator = 9;
            }

            if (i % separator == 0) {
                x1 = (int) (aSin * stringPosition);
                y1 = (int) (aCos * stringPosition);


                g.drawImage(numbers[i / 3], cX + x1, cY - y1, Graphics.VCENTER | Graphics.HCENTER);
            }
        }
    }

    private void setArrow(Graphics g) {
        g.setColor(0, 0, 0); //white

        double a;
        int x1, x2, x3, x4, y1, y2, y3, y4;

        a = angle / RHO;
        x1 = (int) (Math.sin(a) * radius * 0.65);
        y1 = (int) (Math.cos(a) * radius * 0.65);

        a = (angle + 180) / RHO;
        x2 = (int) (Math.sin(a) * (radius * 0.2));
        y2 = (int) (Math.cos(a) * (radius * 0.2));

        a = (angle + 140) / RHO;
        x3 = (int) (Math.sin(a) * (radius * 0.5));
        y3 = (int) (Math.cos(a) * (radius * 0.5));

        a = (angle + 220) / RHO;
        x4 = (int) (Math.sin(a) * (radius * 0.5));
        y4 = (int) (Math.cos(a) * (radius * 0.5));

        g.setColor(255,0,0); //red

        g.drawLine(cX + x1, cY - y1, cX + x3, cY - y3);
        g.drawLine(cX + x3, cY - y3, cX + x2, cY - y2);
        g.drawLine(cX + x2, cY - y2, cX + x4, cY - y4);
        g.drawLine(cX + x4, cY - y4, cX + x1, cY - y1);

        g.fillTriangle(cX + x1, cY - y1, cX + x2, cY - y2, cX + x3, cY - y3);
        g.fillTriangle(cX + x1, cY - y1, cX + x2, cY - y2, cX + x4, cY - y4);
    }
    
    private void calculateSizes() {
        int width = getWidth();
        int height = getHeight();
        cX = width / 2;
        
        TOP_MARGIN = (width < 140) ? 17 : 20;
        BOTTOM_MARGIN = TOP_MARGIN;

        if (viewModeSmall) {
            cY = (height - ((width < 140) ? 11 : 18+10) - TOP_MARGIN - BOTTOM_MARGIN) / 2;
            radius = Math.min(cX, cY) - 5;
            cY = cY + TOP_MARGIN;
        } else {
            cY = (height - ((width < 140) ? 44 : 72+10) - TOP_MARGIN - BOTTOM_MARGIN) / 2;
            radius = Math.min(cX, cY) - 5;
            cY = cY + TOP_MARGIN;
        }

        if (radius < 50) {
            smallRadius = true;
        } else {
            smallRadius = false;
        }
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
            gps.stop();
            gui.getDisplay().setCurrent(gps.getPreviousScreen());
        }
        //prave tlacitko
        if (keyCode == -7 || keyCode == 112 || keyCode == 111)
        {
            favourites.loadFavouritesToMap();
            gui.getDisplay().setCurrent(gui.get_cvsMap());
            gps.changeAction(Gps.MAP);
        }
        
        if (keyCode == KEY_NUM5) {
            viewModeSmall = !viewModeSmall;
            calculateSizes();
            repaint();
        }
    }
}
