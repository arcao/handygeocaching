package gui;

import gps.Gps;
import java.util.Vector;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import track.Track;
import utils.ImageCache;

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
    static final int ZOOM_STEP = 1; //urcuje rychlost zoomovani
    static final int KEY_DELAY = 20; //pauza mezi dvema stisknutimi klaves
    //ostatni promenne
    private Thread thread;
    private int keyCode;
    private boolean firstPaint;
    
    private int lastDragX = -1;
    private int lastDragY = -1;
    
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
            
            g.setColor((gui.nightMode) ? 0x0 : 0xffffff); //pozadi
            g.fillRect(0, 0, screenWidth, screenHeight);
            g.setColor((gui.nightMode) ? 0xffffff : 0x0); //text
            
            if (firstPaint)
            {
                //loading
                g.setFont(gui.get_fntNormal());
                g.drawString("Načítám mapu",screenWidth/2,screenHeight/2,Graphics.TOP|Graphics.HCENTER);
                firstPaint = false;
            }
            else if (fixMessage.equals(""))
            {
                int pixelsPerKm = (int)Math.ceil((double)zoom/2 * Math.sqrt((double)zoom/10));
                double getKmPerLonAtLatLatitude = getKmPerLonAtLat(latitude);

                //vykresleni tracku
                track.reset();
                
                g.setColor((gui.nightMode) ? 0xffff00 : 0x0000ff); //usla cara
                
                double line[];
                while ((line = track.nextLine())!=null)
                {
                    int x1 = (int)(getKmPerLonAtLatLatitude*(line[0]-longitude)*pixelsPerKm);
                    int y1 = (int)(getKmPerLat()*(latitude-line[1])*pixelsPerKm);
                    int x2 = (int)(getKmPerLonAtLatLatitude*(line[2]-longitude)*pixelsPerKm);
                    int y2 = (int)(getKmPerLat()*(latitude-line[3])*pixelsPerKm);
                    g.drawLine(screenWidth/2+x1+x,screenHeight/2+y1+y,screenWidth/2+x2+x,screenHeight/2+y2+y);
                }
                
                //vykresleni navigacni cary
                if (gps.isNavigating())
                {
                    int xx = (int)(getKmPerLonAtLatLatitude*(gps.getNavigationLongitude()-longitude)*pixelsPerKm);
                    int yy = (int)(getKmPerLat()*(latitude-gps.getNavigationLatitude())*pixelsPerKm);
                    g.setColor((gui.nightMode) ? 0xffffff : 0x0); //navigacni cara
                    g.setStrokeStyle(Graphics.DOTTED);
                    g.drawLine(screenWidth/2+x,screenHeight/2+y,screenWidth/2+xx+x,screenHeight/2+yy+y);
                    g.setStrokeStyle(Graphics.SOLID);
                }
                //kresleni jednotlivych bodu
                int mapItemsSize = mapItems.size();
                g.setColor((gui.nightMode) ? 0xffffff : 0x0); //font
                g.setFont(gui.get_fntSmall());
                
                for (int i=0;i<mapItemsSize;i++)
                {
                    MapItem mapItem = (MapItem)mapItems.elementAt(i);
                    int item_x = (int)(getKmPerLonAtLatLatitude*(mapItem.longitude-longitude)*pixelsPerKm);
                    int item_y = (int)(getKmPerLat()*(latitude-mapItem.latitude)*pixelsPerKm);
                    int halfIcon = iconLoader.getIconSize()/2;
                    
                    g.drawImage(iconLoader.loadIcon(mapItem.icon),screenWidth/2+item_x-halfIcon+x,screenHeight/2+item_y-halfIcon+y,Graphics.TOP|Graphics.LEFT);
                    g.drawString(mapItem.name,screenWidth/2+item_x+halfIcon+2+x,screenHeight/2+item_y-halfIcon+y,Graphics.TOP|Graphics.LEFT);
                }
                
                //vykresleni pozice uzivatele
                g.drawArc(screenWidth/2-15+x, screenHeight/2-15+y, 30, 30, 0, 360);
                g.drawLine(screenWidth/2-15+x,screenHeight/2+y,screenWidth/2+15+x,screenHeight/2+y);
                g.drawLine(screenWidth/2+x,screenHeight/2-15+y,screenWidth/2+x,screenHeight/2+15+y);
                
                //vykresleni headingu
                double radHeading = Math.toRadians(heading);
                int xheading = (int)(screenWidth/2 + x + 15*Math.cos(radHeading-Math.PI/2));
                int yheading = (int)(screenHeight/2 + y + 15*Math.sin(radHeading-Math.PI/2));
                g.setColor((gui.nightMode) ? 0x00ffff : 0xff0000); //uhel
                g.fillArc(xheading-4,yheading-4,8,8,0,360);
                
                g.setColor((gui.nightMode) ? 0xffffff : 0x0); //text
            }
            else
            {
                //neni signal
                g.setFont(gui.get_fntNormal());
                g.drawString(fixMessage,screenWidth/2,screenHeight/2,Graphics.TOP|Graphics.HCENTER);
            }
            
            // vykresleni zomovacich tlacitek
            
            if (hasPointerEvents()) {
                g.setColor((gui.nightMode) ? 0x333333 : 0xcccccc); //pozadi tlacitek
                g.fillRect(0, 0, 30, 30);
                g.fillRect(screenWidth - 30, 0, screenWidth, 30);

                g.setColor((gui.nightMode) ? 0xffffff : 0x0); //text
                g.drawLine(0, 30, 30, 30);
                g.drawLine(30, 30, 30, 0);
                
                g.drawLine(screenWidth - 30, 30, screenWidth, 30);
                g.drawLine(screenWidth - 30, 30, screenWidth - 30, 0);
                
                //plus
                g.drawLine(5, 30/2, 30 - 5, 30/2);
                g.drawLine(30/2, 5, 30/2, 30-5);
                
                //minus
                g.drawLine(screenWidth - 30 + 5, 30/2, screenWidth - 5, 30/2);
            }
            

            //tlacitko zpet
            g.setFont(gui.get_fntBold());
            g.drawString("Zpět",3,screenHeight, Graphics.BOTTOM|Graphics.LEFT);
            
            if (hasPointerEvents())
                g.drawString("Noční",screenWidth/2,screenHeight, Graphics.BOTTOM|Graphics.HCENTER);
            
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
        if (keyCode == KEY_NUM0) 
        {
            gui.nightMode = !gui.nightMode;
            repaint();
        }
        //leve kontextove tlacitko
        else if (keyCode == -6 || keyCode == -21 || keyCode == -20 || keyCode == 105 || keyCode == 21 || keyCode == -202 || keyCode == 113)
        {
            gps.stop();
            gui.getDisplay().setCurrent(gps.getPreviousScreen());
            thread = null;
        }
        //prave kontextove tlacitko
        else if (gps.isNavigating() && (keyCode == -7 || keyCode == 112 || keyCode == 111))
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

    protected void pointerPressed(int x, int y) {
        int BORDER = 10;
        int ZOOM_BUTTON = 30 + BORDER;
        
        lastDragX = x;
        lastDragY = y;
        
        int width = gui.get_fntBold().stringWidth("Noční") + 2*BORDER;
        int height = gui.get_fntBold().getHeight() + BORDER;
        
        int widthHalf = (getWidth() - width) / 2;
        
        if (y > getHeight() - height && y < getHeight() &&
            x > widthHalf && x < widthHalf + width) {
            gui.nightMode = !gui.nightMode;
            repaint();
        } else if (x < ZOOM_BUTTON && y < ZOOM_BUTTON) {
            keyCode = Canvas.KEY_STAR;
            thread = new Thread(this);
            thread.start();
        } else if (x > getWidth() - ZOOM_BUTTON && y < ZOOM_BUTTON) {
            keyCode = Canvas.KEY_POUND;
            thread = new Thread(this);
            thread.start();
        }   
    }

    protected void pointerReleased(int x, int y) {
        thread = null;
    }
    
    protected void pointerDragged(int x, int y) {
        int BORDER = 10;
        int ZOOM_BUTTON = 30 + BORDER;
        
        int width = gui.get_fntBold().stringWidth("Noční") + 2*BORDER;
        int height = gui.get_fntBold().getHeight() + BORDER;
        
        int widthHalf = (getWidth() - width) / 2;
        
        int changeX = x - lastDragX;
        int changeY = y - lastDragY;
        
        lastDragX = x;
        lastDragY = y;
        
        if ((y > getHeight() - height && y < getHeight() && x > widthHalf && x < widthHalf + width) ||
            (x < ZOOM_BUTTON && y < ZOOM_BUTTON) ||
            (x > getWidth() - ZOOM_BUTTON && y < ZOOM_BUTTON)) {
            return;
        }   
        
        this.x += changeX;
        this.y += changeY;
        
        repaint();
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
