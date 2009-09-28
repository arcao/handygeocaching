package gui;

import gps.Gps;
import java.util.Vector;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
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
    public int screenWidthHalf;
    public int screenHeightHalf;
    //konstanty
    static final int STEP = 2; //urcuje rychlost posouvani mapy
    static final int ZOOM_STEP = 1; //urcuje rychlost zoomovani
    static final int KEY_DELAY = 20; //pauza mezi dvema stisknutimi klaves
    //ostatni promenne
    private Thread thread;
    private int keyCode;
    private boolean firstPaint;
    
    private int lastDragX = -1;
    private int lastDragY = -1;

    private int fntSmall;
    private int fntSmallBold;
    private int fntBold;
    private int fntNormal;
    private int fntLargeBold;

    private int TOP_MARGIN;

    private int BOTTOM_MARGIN;
    
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
    
    private void drawLine(Graphics g, int x1, int y1, int x2, int y2) {
        if (((x1 >= 0 && x1 <= screenWidth) || (x2 >= 0 && x2 <= screenWidth) ||
             (y1 >= 0 && y1 <= screenHeight) || (y2 >= 0 && y2 <= screenHeight)) &&
             (Math.abs(x2-x1) > 0 || Math.abs(y2-y1) > 0))
        g.drawLine(x1, y1, x2, y2);
    }
    
    /**
     * Tato metoda vykresluje mapu
     */
    public void paint(Graphics g)
    {
        calculateSizes();
        try
        {
            setFullScreenMode(true);
            //velikost platna
            screenWidth = getWidth();
            screenHeight = getHeight();
            
            screenWidthHalf = screenWidth / 2;
            screenHeightHalf = screenHeight / 2;
            
            g.setColor((gui.nightMode) ? 0x0 : 0xffffff); //pozadi
            g.fillRect(0, 0, screenWidth, screenHeight);
            g.setColor((gui.nightMode) ? 0xffffff : 0x0); //text
            
            if (firstPaint)
            {
                //loading
                g.setFont(gui.get_fntNormal());
                g.drawString("Načítám mapu",screenWidthHalf,screenHeightHalf,Graphics.TOP|Graphics.HCENTER);
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
                    drawLine(g,screenWidthHalf+x1+x,screenHeightHalf+y1+y,screenWidthHalf+x2+x,screenHeightHalf+y2+y);
                }
                
                //vykresleni navigacni cary
                if (gps.isNavigating())
                {
                    int xx = (int)(getKmPerLonAtLatLatitude*(gps.getNavigationLongitude()-longitude)*pixelsPerKm);
                    int yy = (int)(getKmPerLat()*(latitude-gps.getNavigationLatitude())*pixelsPerKm);
                    g.setColor((gui.nightMode) ? 0xffffff : 0x0); //navigacni cara
                    g.setStrokeStyle(Graphics.DOTTED);
                    drawLine(g,screenWidthHalf+x,screenHeightHalf+y,screenWidthHalf+xx+x,screenHeightHalf+yy+y);
                    g.setStrokeStyle(Graphics.SOLID);
                }
                //kresleni jednotlivych bodu
                int mapItemsSize = mapItems.size();
                g.setColor((gui.nightMode) ? 0xffffff : 0x0); //font
                g.setFont(gui.get_fntSmall());
                
                int iconSize = iconLoader.getIconSize();
                int iconSizeHalf = iconSize / 2;
                Font fnt = gui.get_fntSmall();
                for (int i=0;i<mapItemsSize;i++)
                {
                    MapItem mapItem = (MapItem)mapItems.elementAt(i);
                    int item_x = (int)(getKmPerLonAtLatLatitude*(mapItem.longitude-longitude)*pixelsPerKm);
                    int item_y = (int)(getKmPerLat()*(latitude-mapItem.latitude)*pixelsPerKm);
                                        
                    int img_x = screenWidthHalf+item_x-iconSizeHalf+x;
                    int img_y = screenHeightHalf+item_y-iconSizeHalf+y;
                    
                    int text_x_end = screenWidthHalf+item_x+iconSizeHalf+2+x+fnt.stringWidth(mapItem.name);
                    
                    if (img_x + iconSize >= 0 && img_x <= screenWidth && img_y + iconSize >= 0 && img_y <= screenHeight) {
                        g.drawImage(iconLoader.loadIcon(mapItem.icon),img_x,img_y,Graphics.TOP|Graphics.LEFT);
                        g.drawString(mapItem.name,screenWidthHalf+item_x+iconSizeHalf+2+x,screenHeightHalf+item_y-iconSizeHalf+y,Graphics.TOP|Graphics.LEFT);
                    } else if (text_x_end >= 0 && img_x <= screenWidth) {
                        g.drawString(mapItem.name,screenWidthHalf+item_x+iconSizeHalf+2+x,screenHeightHalf+item_y-iconSizeHalf+y,Graphics.TOP|Graphics.LEFT);
                    }
                }
                
                //vykresleni pozice uzivatele
                g.drawArc(screenWidth/2-15+x, screenHeight/2-15+y, 30, 30, 0, 360);
                drawLine(g, screenWidth/2-15+x,screenHeight/2+y,screenWidth/2+15+x,screenHeight/2+y);
                drawLine(g, screenWidth/2+x,screenHeight/2-15+y,screenWidth/2+x,screenHeight/2+15+y);
                
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
            if (screenWidth<140) {
                g.setFont(gui.get_fntSmallBold());
            } else {
                g.setFont(gui.get_fntBold());
            }
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
        if (keyCode == KEY_NUM5) 
        {
            x = 0;
            y = 0;
            zoom = 50;
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
        
        int width = getWidth();
        int widthHalf = width / 2;
        Font fnt = (getWidth()<140) ? gui.get_fntSmallBold() : gui.get_fntBold();
        int widthNocni = fnt.stringWidth("Noční") + 2*BORDER;
        int widthZpet = fnt.stringWidth("Zpět") + 2*BORDER;
        int widthNavigace = fnt.stringWidth("Navigace") + 2*BORDER;
                
        int HEIGHT = getHeight();
        int BAR_HEIGHT = BOTTOM_MARGIN + BORDER;
        
        int widthNocniHalf = widthNocni / 2;
        
        //nocni rezim
        if (y > HEIGHT - BAR_HEIGHT &&
            x > widthHalf - widthNocniHalf && x < widthHalf + widthNocniHalf) {
            gui.nightMode = !gui.nightMode;
            repaint();
        
        }
        //zoom in
        else if (x < ZOOM_BUTTON && y < ZOOM_BUTTON) {
            keyCode = Canvas.KEY_STAR;
            thread = new Thread(this);
            thread.start();
        }
        //zoom out
        else if (x > width - ZOOM_BUTTON && y < ZOOM_BUTTON) {
            keyCode = Canvas.KEY_POUND;
            thread = new Thread(this);
            thread.start();
        }
        //Zpet
        else if (y > HEIGHT - BAR_HEIGHT && x < widthZpet) {
            gps.stop();
            gui.getDisplay().setCurrent(gps.getPreviousScreen());
        }
        //Navigace
        else if ( gps.isNavigating() &&
            y > HEIGHT - BAR_HEIGHT && x > width - widthNavigace) {
            
            gui.getDisplay().setCurrent(gui.get_cvsNavigation());
            gps.changeAction(Gps.NAVIGATION);
            thread = null;
        }
    }

    protected void pointerReleased(int x, int y) {
        thread = null;
    }
    
    protected void pointerDragged(int x, int y) {
        int BORDER = 10;
        int ZOOM_BUTTON = 30 + BORDER;
        
        int HEIGHT = getHeight();
        int BAR_HEIGHT = BOTTOM_MARGIN + BORDER;
        
        int width = getWidth();
        int widthHalf = width / 2;
        Font fnt = (getWidth()<140) ? gui.get_fntSmallBold() : gui.get_fntBold();
        int widthNocni = fnt.stringWidth("Noční") + 2*BORDER;                      
        int widthNocniHalf = widthNocni / 2;
        
        int changeX = x - lastDragX;
        int changeY = y - lastDragY;
        
        lastDragX = x;
        lastDragY = y;
        
        if ((y > HEIGHT - BAR_HEIGHT) ||
            (x < ZOOM_BUTTON && y < ZOOM_BUTTON) ||
            (x > width - ZOOM_BUTTON && y < ZOOM_BUTTON)) {
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

    private void calculateSizes() {       
        fntSmallBold = gui.get_fntSmallBold().getHeight();
        fntBold = gui.get_fntBold().getHeight();       
        
        TOP_MARGIN = (getWidth() < 140) ? fntSmallBold : fntBold;
        BOTTOM_MARGIN = TOP_MARGIN;
    }
    
}
