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
    public int angle = 0;
    public int compass = 0;
    public String azimut = "";
    public String dateTime = "";
    public Image[] arrows;
    private int image;
    private int transformation;
    private Image rotatedImage;
    private int compassMovement;
    private String[] compassDirections;
    
    private Gui gui;
    private Gps gps;
    private Favourites favourites;
    
    public Navigation(Gui ref, Gps ref2, Favourites ref3)
    {
        try
        {
            gui = ref;
            gps = ref2;
            favourites = ref3;
            arrows = new Image[4];
            compassDirections = new String[4];
            arrows[0] = Image.createImage("/sipka0.png");
            arrows[1] = Image.createImage("/sipka225.png");
            arrows[2] = Image.createImage("/sipka45.png");
            arrows[3] = Image.createImage("/sipka675.png");
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
            int width = getWidth();
            int height = getHeight();
            
            //tvorba sipky
            if (angle % 90 < 11)
                image = 0;
            else if (angle % 90 < 33)
                image = 1;
            else if (angle % 90 < 55)
                image = 2;
            else if (angle % 90 < 77)
                image = 3;
            else
                image = 0;
            
            if (angle>=0 && angle <77)
                transformation = Sprite.TRANS_NONE;
            else if (angle >=77 && angle<167)
                transformation = Sprite.TRANS_ROT90;
            else if (angle >=167 && angle<257)
                transformation = Sprite.TRANS_ROT180;
            else if (angle >=257 && angle<347)
                transformation = Sprite.TRANS_ROT270;
            else
                transformation = Sprite.TRANS_NONE;
            rotatedImage = Image.createImage(arrows[image],0,0,60,60,transformation);
            
            
            //tvorba kompasu
            if (compass % 90 < 11)
                compassMovement = 0;
            else if (compass % 90 < 33)
                compassMovement = 15;
            else if (compass % 90 < 55)
                compassMovement = 30;
            else if (compass % 90 < 77)
                compassMovement = -15;
            else
                compassMovement = 0;
            
            if (compass>0 && compass <=45)
            {
                compassDirections[0] = "S";
                compassDirections[1] = "V";
                compassDirections[2] = "J";
                compassDirections[3] = "Z";
            }
            else if (compass >45 && compass<=135)
            {
                compassDirections[0] = "V";
                compassDirections[1] = "J";
                compassDirections[2] = "Z";
                compassDirections[3] = "S";
            }
            else if (compass >135 && compass<=225)
            {
                compassDirections[0] = "J";
                compassDirections[1] = "Z";
                compassDirections[2] = "S";
                compassDirections[3] = "V";
            }
            else if (compass >225 && compass<=315)
            {
                compassDirections[0] = "Z";
                compassDirections[1] = "S";
                compassDirections[2] = "V";
                compassDirections[3] = "J";
            }
            else
            {
                compassDirections[0] = "S";
                compassDirections[1] = "V";
                compassDirections[2] = "J";
                compassDirections[3] = "Z";
            }
            
            //kresleni
            if (width<140) //male displeje
            {
                //nadpis
                g.setColor(0xffffff);
                g.fillRect(0, 0, width, height);
                g.setColor(0);
                g.setFont(gui.get_fntSmallBold());
                g.drawString(cacheName,width/2,height/2-65, Graphics.TOP|Graphics.HCENTER);
                //sipka
                g.drawImage(rotatedImage,width/2,height/2-43,Graphics.TOP|Graphics.HCENTER);
                //kompas
                g.setColor(255,0,0);
                g.setFont(gui.get_fntSmall());
                g.drawString(compassDirections[0],width/2+compassMovement,height/2-53,Graphics.TOP|Graphics.HCENTER);
                g.drawString(compassDirections[1],width/2+35,height/2+compassMovement-13,Graphics.TOP|Graphics.HCENTER);
                g.drawString(compassDirections[2],width/2-compassMovement,height/2+17,Graphics.TOP|Graphics.HCENTER);
                g.drawString(compassDirections[3],width/2-35,height/2-compassMovement-13,Graphics.TOP|Graphics.HCENTER);
                //ostatni napisy
                g.setColor(0);
                g.setFont(gui.get_fntSmallBold());
                g.drawString(distance,width/2,height/2-18,Graphics.TOP|Graphics.HCENTER);
                g.setFont(gui.get_fntSmall());
                g.drawString(" Az.:"+azimut+" "+accuracy,width/2,height/2+28,Graphics.TOP|Graphics.HCENTER);
                g.drawString(speed+" "+satellites,width/2,height/2+39,Graphics.TOP|Graphics.HCENTER);
                g.drawString(dateTime+" "+altitude,width/2,height/2+50,Graphics.TOP|Graphics.HCENTER);
                g.drawString("Zpìt",3,height-17,Graphics.TOP|Graphics.LEFT);
                g.drawString("Mapa", width-35,height-17,Graphics.TOP|Graphics.LEFT);
            }
            else //velke displeje
            {
                //nadpis
                g.setColor(0xffffff);
                g.fillRect(0, 0, width, height);
                g.setColor(0);
                g.setFont(gui.get_fntBold());
                g.drawString(cacheName,width/2,height/2-89, Graphics.TOP|Graphics.HCENTER);
                //sipka
                g.drawImage(rotatedImage,width/2,height/2-63,Graphics.TOP|Graphics.HCENTER);
                //kompas
                g.setColor(255,0,0);
                g.setFont(gui.get_fntSmall());
                g.drawString(compassDirections[0],width/2+compassMovement,height/2-73,Graphics.TOP|Graphics.HCENTER);
                g.drawString(compassDirections[1],width/2+35,height/2+compassMovement-33,Graphics.TOP|Graphics.HCENTER);
                g.drawString(compassDirections[2],width/2-compassMovement,height/2-3,Graphics.TOP|Graphics.HCENTER);
                g.drawString(compassDirections[3],width/2-35,height/2-compassMovement-33,Graphics.TOP|Graphics.HCENTER);
                //ostatni napisy
                g.setColor(0);
                g.setFont(gui.get_fntBold());
                g.drawString(distance,width/2,height/2+9,Graphics.TOP|Graphics.HCENTER);
                g.setFont(gui.get_fntNormal());
                g.drawString(" Azimut: "+azimut+" | "+accuracy,width/2,height/2+36,Graphics.TOP|Graphics.HCENTER);
                g.drawString(speed+" | "+satellites,width/2,height/2+54,Graphics.TOP|Graphics.HCENTER);
                g.drawString(dateTime+" | "+altitude,width/2,height/2+72,Graphics.TOP|Graphics.HCENTER);
                g.setFont(gui.get_fntBold());
                g.drawString("Zpìt",3,height-20,Graphics.TOP|Graphics.LEFT);
                //tlacitko mapa
                g.drawString("Mapa", width-44,height-20,Graphics.TOP|Graphics.LEFT);
            }
        }
        catch (Exception e)
        {
            gui.showError("navigation paint", e.toString(), "");
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
    }
}
