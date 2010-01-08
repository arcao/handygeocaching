package gui;

import gps.Gps;
import java.util.Vector;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import track.Track;

/**
 * Tato trida reprezentuje navigacni obrazovku, zobrazuje sipku a dalsi udaje
 */
public class Map extends Canvas implements Runnable
{
    //reference na ostatni tridy
    Gui gui;
    Gps gps;
    IconLoader iconLoader;
    Track track;
    
    //promenne ktere meni uzivatel
    int x = 0; //x-ova pozice kurzoru
    int y = 0; //y-ova pozice kurzoru
    int zoom = 50; //defaultni priblizeni
    //promenne ktere meni gpska
    public double latitude = 49.91216; //pozice uzivatele
    public double longitude = 14.22126;  //pozice uzivatele
    public int heading = 0; //kam nam uzivatel miri?
    public String fixMessage = "Není GPS signál";
    //ostatni promenne
    Vector mapItems;
    public int screenWidth;
    public int screenHeight;
    //konstanty
    static final int STEP = 1; //urcuje rychlost posouvani mapy
    static final int ZOOM_STEP = 2; //urcuje rychlost zoomovani
    static final int KEY_DELAY = 10; //pauza mezi dvema stisknutimi klaves
    //ostatni promenne
    private Thread thread;
    private int keyCode;
    private boolean firstPaint;
    
    public Map(Gui ref, Gps ref2, IconLoader ref3, Track ref4)
    {
        try
        {
            gui = ref;
            gps = ref2;
            iconLoader = ref3;
            track = ref4;
            mapItems = new Vector();
            firstPaint = true;
        }
        catch (Exception e)
        {
            gui.showError("map konstruktor",e.toString(),"");
        }
    }
    
    /**
     * Smaze vsechny body v mape
     */
    public void reset()
    {
        mapItems.removeAllElements();
    }
    
    /**
     * Prida bod zajmu do mapy
     */
    public void addMapItem(double a, double b, String c, String d)
    {
        try
        {
            mapItems.addElement(new MapItem(a,b,c,d));
        }
        catch (Exception e)
        {
            gui.showError("addMapItem",e.toString(),"");
        }
    }
    
    /**
     * Spocita kolik kilometru je na jeden stupen zemepisne delky pri dane sirce
     */
    public double getKmPerLonAtLat(double dLatitude)
    {
        double km = dLatitude * 0.01745329252;
        return 111.321 * Math.cos(km);
    }
    
    /**
     * Vrati pocet kilometru na jeden stupen zemepisne sirky
     */
    public double getKmPerLat()
    {
        return 111.000;
    }
    
    /**
     * Tato metoda vykresluje mapu
     */
    public void paint(Graphics g)
    {
        try
        {
            setFullScreenMode(true);
            //velikost platna
            screenWidth = getWidth();
            screenHeight = getHeight();
            if (firstPaint)
            {
                //loading
                g.setColor(0xffffff);
                g.fillRect(0, 0, screenWidth, screenHeight);
                g.setColor(0);
                g.setFont(gui.get_fntNormal());
                g.drawString("Načítám mapu",screenWidth/2,screenHeight/2,Graphics.TOP|Graphics.HCENTER);
                firstPaint = false;
            }
            else if (fixMessage.equals(""))
            {
                int pixelsPerKm = (int)Math.ceil((double)zoom/2 * Math.sqrt((double)zoom/10));
                //kresleni mapy
                g.setColor(0xffffff);
                g.fillRect(0, 0, screenWidth, screenHeight);
                //vykresleni tracku
                track.reset();
                double line[];
                while ((line = track.nextLine())!=null)
                {
                    int x1 = (int)(getKmPerLonAtLat(latitude)*(line[0]-longitude)*pixelsPerKm);
                    int y1 = (int)(getKmPerLat()*(latitude-line[1])*pixelsPerKm);
                    int x2 = (int)(getKmPerLonAtLat(latitude)*(line[2]-longitude)*pixelsPerKm);
                    int y2 = (int)(getKmPerLat()*(latitude-line[3])*pixelsPerKm);
                    g.setColor(0,0,255);
                    g.drawLine(screenWidth/2+x1+x,screenHeight/2+y1+y,screenWidth/2+x2+x,screenHeight/2+y2+y);
                }
                //vykresleni navigacni cary
                if (gps.isNavigating())
                {
                    int xx = (int)(getKmPerLonAtLat(latitude)*(gps.getNavigationLongitude()-longitude)*pixelsPerKm);
                    int yy = (int)(getKmPerLat()*(latitude-gps.getNavigationLatitude())*pixelsPerKm);
                    g.setColor(255,0,0);
                    g.setStrokeStyle(Graphics.DOTTED);
                    g.drawLine(screenWidth/2+x,screenHeight/2+y,screenWidth/2+xx+x,screenHeight/2+yy+y);
                    g.setStrokeStyle(Graphics.SOLID);
                }
                //kresleni jednotlivych bodu
                for (int i=0;i<mapItems.size();i++)
                {
                    MapItem mapItem = (MapItem)mapItems.elementAt(i);
                    int item_x = (int)(getKmPerLonAtLat(latitude)*(mapItem.longitude-longitude)*pixelsPerKm);
                    int item_y = (int)(getKmPerLat()*(latitude-mapItem.latitude)*pixelsPerKm);
                    int halfIcon = iconLoader.getIconSize()/2;
                    g.drawImage(Image.createImage(iconLoader.loadIcon(mapItem.icon)),screenWidth/2+item_x-halfIcon+x,screenHeight/2+item_y-halfIcon+y,Graphics.TOP|Graphics.LEFT);
                    g.setFont(gui.get_fntSmall());
                    g.setColor(0);
                    g.drawString(mapItem.name,screenWidth/2+item_x+halfIcon+2+x,screenHeight/2+item_y-halfIcon+y,Graphics.TOP|Graphics.LEFT);
                }
                //vykresleni pozice uzivatele
                g.setColor(0);
                g.drawArc(screenWidth/2-15+x, screenHeight/2-15+y, 30, 30, 0, 360);
                g.drawLine(screenWidth/2-15+x,screenHeight/2+y,screenWidth/2+15+x,screenHeight/2+y);
                g.drawLine(screenWidth/2+x,screenHeight/2-15+y,screenWidth/2+x,screenHeight/2+15+y);
                //vykresleni headingu
                double radHeading = Math.toRadians(heading);
                int xheading = (int)(screenWidth/2 + x + 15*Math.cos(radHeading-Math.PI/2));
                int yheading = (int)(screenHeight/2 + y + 15*Math.sin(radHeading-Math.PI/2));
                g.setColor(255,0,0);
                g.fillArc(xheading-4,yheading-4,8,8,0,360);
            }
            else
            {
                //neni signal
                g.setColor(0xffffff);
                g.fillRect(0, 0, screenWidth, screenHeight);
                g.setColor(0);
                g.setFont(gui.get_fntNormal());
                g.drawString(fixMessage,screenWidth/2,screenHeight/2,Graphics.TOP|Graphics.HCENTER);
            }
            //tlacitko zpet
            g.setFont(gui.get_fntBold());
            g.setColor(0);
            g.drawString("Zpět",3,screenHeight, Graphics.BOTTOM|Graphics.LEFT);
            //tlacitko navigace
            if (gps.isNavigating())
                g.drawString("Navigace", screenWidth-3,screenHeight,Graphics.BOTTOM|Graphics.RIGHT);
        }
        catch (Exception e)
        {
            gui.showError("map paint",e.toString(),"");
        }
    }
    
    /**
     * Osetreni stisknuti tlacitek - do noveho vlakna kvuli tomu, ze clovek zmackne tlacitko a akce se provadi dokud ho nepusti
     */
    public void keyPressed(int key)
    {
        keyCode = key;
        //leve kontextove tlacitko
        if (keyCode == -6 || keyCode == -21 || keyCode == -20 || keyCode == 105 || keyCode == 21 || keyCode == -202 || keyCode == 113)
        {
            gps.stop();
            gui.getDisplay().setCurrent(gps.getPreviousScreen());
            thread = null;
        }
        //prave kontextove tlacitko
        if (gps.isNavigating() && (keyCode == -7 || keyCode == 112 || keyCode == 111))
        {
            gui.getDisplay().setCurrent(gui.get_cvsNavigation());
            gps.changeAction(Gps.NAVIGATION);
            thread = null;
        }
        //ostatni tlacitka ktera se muzou drzet
        else
        {
            thread = new Thread(this);
            thread.start();
        }
    }
    
    /**
     * Uzivatel pusti tlacitko - vlakno skonci
     */
    public void keyReleased(int keyCode)
    {
        thread = null;
    }
    
    
    /**
     * Vlakno osetreni stisku tlacitek
     */
    public void run()
    {
        try
        {
            while (thread != null)
            {
                repaint();
                //hvezdicka - zoom in
                if (keyCode == Canvas.KEY_STAR)
                {
                    zoom-=ZOOM_STEP;
                    if (zoom<1) //minimalni priblizeni
                        zoom = 1;
                    repaint();
                }
                //krizek - zoom out
                else if (keyCode == Canvas.KEY_POUND)
                {
                    zoom+=ZOOM_STEP;
                    if (zoom>500) //maximalni priblizeni
                        zoom = 500;
                    repaint();
                }
                //doleva
                else if (keyCode == Canvas.KEY_NUM4 || keyCode == Canvas.LEFT || keyCode == -3)
                {
                    x+=STEP;
                    repaint();
                }
                //doprava
                else if (keyCode == Canvas.KEY_NUM6 || keyCode == Canvas.RIGHT || keyCode == -4)
                {
                    x-=STEP;
                    repaint();
                }
                //dolu
                else if (keyCode == Canvas.KEY_NUM8 || keyCode == Canvas.DOWN || keyCode == -2)
                {
                    y-=STEP;
                    repaint();
                }
                //nahoru
                else if (keyCode == Canvas.KEY_NUM2 || keyCode == Canvas.UP || keyCode == -1)
                {
                    y+=STEP;
                    repaint();
                }
                if (thread!=null)
                    thread.sleep(KEY_DELAY);
                
            }
        }
        catch (Exception e)
        {
            gui.showError("map keypressed vlakno",e.toString(),"");
        }
    }
    
}
