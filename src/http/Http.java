package http;

import database.Favourites;
import database.Patterns;
import database.Settings;
import gps.Gps;
import gui.Gui;
import gui.IconLoader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.StringItem;
import utils.Utils;

/***
 * Tato trida komunikuje se skriptem pres HTTP protokol, parsuje stazena data
 * a zobrazuje je.
 */
public class Http implements Runnable
{
    
    //adresa skriptu
    private static final String url = "http://handygeocaching.sluzba.cz/handy31.php";
    
    //mozne akce
    public static final int LOGIN = 0;
    public static final int NEAREST_CACHES = 1;
    public static final int OVERVIEW = 2;
    public static final int DETAIL = 3;
    public static final int HINT = 4;
    public static final int WAYPOINTS = 5;
    public static final int LOGS = 6;
    public static final int NEXT_NEAREST = 7;
    public static final int ALL_LOGS = 8;
    public static final int KEYWORD = 9;
    public static final int TRACKABLE = 19;
    public static final int PATTERNS = 11;
    
    //reference na ostatni moduly
    private Gui gui;
    private Settings settings;
    private Favourites favourites;
    private Gps gps;
    private Cache cache;
    private IconLoader iconLoader;
    private Patterns patterns;
    
    private Thread t;
    
    private String cookie = ""; //cookie po zalogovani
    private String guideline = ""; //pouziva se pri zjistovani dalsich logu
    private int action; //druh vykonavane akce
    private int previousAction; //akce ukladana do historie kvuli logovani
    private boolean previousRefresh; //minuly refresh ukladany kvuli logovani
    private boolean offline; //offline mod - data se nacitaji z pameti
    public String[] waypoints; //waypointy v nalezenych kesich
    public String waypoint; //zvoleny waypoint
    public String typeNumber; //cislo obrazku typu - pouziva se pri ukladani do oblibenych
    public String response; //odpoved HTTP, vyuziva se hlavne pri ukladani do cache
    public String favouriteResponse; //odpoved HTTP, vyuziva se pri ukladani do oblibenych
    private boolean refresh; //refresh mod - znovunacteni ulozene kese a aktualizace db
    
    public Http(Gui ref, Settings ref2, Favourites ref3, IconLoader ref4, Patterns ref5)
    {
        gui = ref;
        settings = ref2;
        favourites = ref3;
        iconLoader = ref4;
        patterns = ref5;
        cache = new Cache();
    }
    
    /***
     * Dodatecne pridani reference
     **/
    public void setReference(Gps ref)
    {
        gps = ref;
    }
    
    /***
     * Zacatek HTTP komunikace
     */
    public void start(int act, boolean refr)
    {
        offline = false;
        refresh = refr;
        gui.get_gaLoading().setValue(Gauge.CONTINUOUS_IDLE);
        action = act;
        if (action == NEXT_NEAREST)
        {
            if (gui.nearestFromWaypoint)
            {
                gui.get_tfLattitude().setString(gui.get_siOverviewLattitude().getText());
                gui.get_tfLongitude().setString(gui.get_siOverviewLongitude().getText());
            }
            else //nejblizsi z oblibene
            {
                gui.get_tfLattitude().setString(gui.get_siFavouriteLattitude().getText());
                gui.get_tfLongitude().setString(gui.get_siFavouriteLongitude().getText());
            }
            action = NEAREST_CACHES;
        }
        
        if (!gui.logged) //nezalogovan => zalogovat
        {
            if (settings.name.equals("") && settings.password.equals(""))
            {
                gui.showAlert("Nemáte nastaveny přihlašovací údaje na server geocaching.com, budete přesměrováni do nastavení.",AlertType.WARNING,gui.get_frmSettings());
                settings.set();
            }
            else
            {
                previousAction = action;
                previousRefresh = refresh;
                action = LOGIN;
                t = new Thread(this);
                t.start();
            }
        }
        else
        {
            t = new Thread(this);
            t.start();
        }
    }
    
    /**
     * Zacatek offline komunikace - Http trida pouze formatuje, nic nestahuje
     */
    public void startOffline(int act, String resp)
    {
        action = act;
        response = resp;
        offline = true;
        refresh = false;
        t = new Thread(this);
        t.start();
    }
    
    /***
     * V tomto vlakne probiha veskera HTTP komunikace, zde probiha zpracovani HTTP odpovedi
     */
    public void run()
    {
        switch (action)
        {
            case LOGIN:
                try
                {
                    response = downloadData("part=login&sessid="+Utils.sessionId(settings.name, settings.password)+"&version="+gui.getAppProperty("MIDlet-Version")+"&light=0&build="+gui.getAppProperty("Build-Vendor")+"-"+gui.getAppProperty("Build-Version"));
                    if (checkData(response))
                    {
                        String[][] login = parseData(response);
                        cookie = login[0][0];
                        gui.logged = true;
                        //kontrola verze
                        if (!login[0][1].equals("OK"))
                        {
                            gui.showAlert("Je k dispozici nová verze aplikace: "+login[0][1],AlertType.INFO,gui.get_frmLoading());
                            t.sleep(3000);
                        }
                        //vip mod
                        if (login[0][2].equals("1"))
                            settings.setVIP(true);
                        else
                            settings.setVIP(false);
                        start(previousAction, previousRefresh);
                    }
                }
                catch (Exception e)
                {
                    gui.showError("logovani",e.toString(),response);
                }
                break;
            case NEAREST_CACHES:
                try
                {
                    String coordinates;
                    boolean rightCoordFormat = true;
                    //zadane souradnice
                    coordinates = "lattitude="+gps.convertLattitude(gui.get_tfLattitude().getString())+"&longitude="+gps.convertLongitude(gui.get_tfLongitude().getString());
                    //spatny format souradnic
                    if (gps.convertLattitude(gui.get_tfLattitude().getString())==0 || gps.convertLongitude(gui.get_tfLongitude().getString())==0)
                    {
                        rightCoordFormat = false;
                        gui.showAlert("Špatný formát souřadnic",AlertType.WARNING,gui.get_frmCoordinates());
                    }
                    else
                    {
                        settings.saveCoordinates(gui.get_tfLattitude().getString(), gui.get_tfLongitude().getString());
                    }
                    if (rightCoordFormat)
                    {
                        response = downloadData("part=nearest&cookie="+cookie+"&"+coordinates+"&filter="+settings.filter+"&numberCaches="+settings.numberCaches);
                        if (checkData(response))
                        {
                            String[][] nearestCaches = parseData(response);
                            gui.get_lstNearestCaches().setTitle("Nejbližší cache");
                            gui.get_lstNearestCaches().deleteAll();
                            waypoints = new String[nearestCaches.length];
                            for (int i=0;i<nearestCaches.length;i++)
                            {
                                gui.get_lstNearestCaches().append(nearestCaches[i][0],iconLoader.loadIcon(nearestCaches[i][1]));
                                waypoints[i] = nearestCaches[i][2];
                            }
                            gui.getDisplay().setCurrent(gui.get_lstNearestCaches());
                        }
                    }
                }
                catch (Exception e)
                {
                    gui.showError("nejblizsi kese",e.toString(),response);
                }
                break;
            case KEYWORD:
                try
                {
                    response = downloadData("part=keyword&cookie="+cookie+"&keyword="+Utils.urlUTF8Encode(gui.get_tfKeyword().getString())+"&numberCaches="+settings.numberCaches);
                    if (checkData(response))
                    {
                        String[][] foundCaches = parseData(response);
                        gui.get_lstNearestCaches().setTitle("Nalezené cache");
                        gui.get_lstNearestCaches().deleteAll();
                        waypoints = new String[foundCaches.length];
                        for (int i=0;i<foundCaches.length;i++)
                        {
                            gui.get_lstNearestCaches().append(foundCaches[i][0],iconLoader.loadIcon(foundCaches[i][1]));
                            waypoints[i] = foundCaches[i][2];
                        }
                        gui.getDisplay().setCurrent(gui.get_lstNearestCaches());
                    }
                    
                }
                catch (Exception e)
                {
                    gui.showError("keyword",e.toString(),response);
                }
                break;
            case OVERVIEW:
                try
                {
                    if (!offline)
                        response = downloadData("part=overview&cookie="+cookie+"&waypoint="+waypoint);
                    if (checkData(response))
                    {
                        String[][] listing = parseData(response);
                        gui.get_siName().setText(listing[0][0]);
                        gui.get_siAuthor().setText(listing[0][1]);
                        gui.get_siType().setText(listing[0][2]);
                        gui.get_siSize().setText(listing[0][3]);
                        gui.get_siOverviewLattitude().setText(listing[0][4]);
                        gui.get_siOverviewLongitude().setText(listing[0][5]);
                        gui.get_siDifficulty().setText(listing[0][6]);
                        gui.get_siWaypoint().setText(listing[0][7]);
                        waypoint = listing[0][7];
                        gui.get_siInventory().setText(listing[0][8]);
                        if (listing[0][9].equals(""))
                        {
                            gui.get_frmOverview().setTitle("Přehled cache");
                        }
                        else
                        {
                            //disabled nebo archieved cache
                            gui.get_frmOverview().setTitle(listing[0][9]);
                        }
                        typeNumber = listing[0][10];
                        
                        gui.get_frmOverview().removeCommand(gui.get_cmdNastavitNalez());
                        if (offline)
                            gui.get_frmOverview().addCommand(gui.get_cmdNastavitNalez());
                        
                        gui.get_frmOverview().removeCommand(gui.get_cmdPoznamka());
                        if (offline)
                            gui.get_frmOverview().addCommand(gui.get_cmdPoznamka());
                        
                        //jsou pridavne waypointy?
                        gui.get_frmOverview().removeCommand(gui.get_cmdWaypoints());
                        if (listing[0][11].equals("1"))
                            gui.get_frmOverview().addCommand(gui.get_cmdWaypoints());
                        //je mozne stahnout napovedu?
                        gui.get_frmOverview().removeCommand(gui.get_cmdHint());
                        if (listing[0][12].equals("1"))
                        {
                            gui.get_frmOverview().addCommand(gui.get_cmdHint());
                        }
                        //je mozne stahnout vzorce?
                        gui.get_frmOverview().removeCommand(gui.get_cmdDownloadPatterns());
                        if (listing[0][13].equals("1"))
                        {
                            gui.get_frmOverview().addCommand(gui.get_cmdDownloadPatterns());
                            gui.get_siInventory().setText(gui.get_siInventory().getText()+"\n"+"Tato cache umožnuje stažení vzorečků do MultiSolveru!");
                        }    
                        //nastaveni kB podrobnosti
                        gui.get_frmOverview().removeCommand(gui.get_cmdInfo());
                        gui.changeCmdInfoLabel(listing[0][14]);
                        gui.get_frmOverview().addCommand(gui.get_cmdInfo());
                        //je mozne obnovit listing?
                        gui.get_frmOverview().removeCommand(gui.get_cmdRefresh());
                        if (offline)
                            gui.get_frmOverview().addCommand(gui.get_cmdRefresh());
                        favouriteResponse = response;
                        if (offline) {
                            gui.get_siNalezenoOver().setText(favourites.found);
                            gui.get_siPoznamkaOver().setText(favourites.poznamka);
                        } else {
                            gui.get_siNalezenoOver().setText("");
                            gui.get_siPoznamkaOver().setText("");
                        }
                        if (refresh)
                            //Zephy 19.11.07 +\ -pridan posledni parametr
                            favourites.addEdit(listing[0][0],response,listing[0][4],listing[0][5],typeNumber,null, false, (offline) ? favourites.found : "", (offline) ? favourites.poznamka : "");
                            //Zephy 19.11.07 +/
                        if (!offline) {
                            favourites.editId = -1;
                            //Zephy 19.11.07 +\ -pridan posledni parametr
                            favourites.addEdit("_Poslední cache",response,listing[0][4],listing[0][5],typeNumber,null, false, "NE", "");                        
                            //Zephy 19.11.07 +/
                        }
                        gui.getDisplay().setCurrent(gui.get_frmOverview());
                    }
                }
                catch (Exception e)
                {
                    gui.showError("overview",e.toString(),response);
                }
                break;
            case DETAIL:
                try
                {
                    response = downloadData("part=info&cookie="+cookie+"&waypoint="+waypoint);
                    if (checkData(response))
                    {
                        gui.get_frmInfo().deleteAll();
                        gui.get_frmInfo().append(gui.get_siBegin());
                        gui.get_frmInfo().append(response);
                        gui.get_frmInfo().append(gui.get_siEnd());
                        //limitace Javy na telefonech SE? nasledujici radka ulozi jen par vet.
                        //gui.get_siContent().setText(response);
                        gui.getDisplay().setCurrent(gui.get_frmInfo());
                    }
                }
                catch (Exception e)
                {
                    gui.showError("listing",e.toString(),response);
                }
                break;
            case HINT:
                try
                {
                    response = downloadData("part=hint&cookie="+cookie+"&waypoint="+waypoint);
                    if (checkData(response))
                    {
                        gui.get_frmHint().deleteAll();
                        gui.get_frmHint().append(response);
                        gui.getDisplay().setCurrent(gui.get_frmHint());
                    }
                }
                catch (Exception e)
                {
                    gui.showError("hint",e.toString(),response);
                }
                break;
            case WAYPOINTS:
                try
                {
                    response = downloadData("part=waypoints&cookie="+cookie+"&waypoint="+waypoint);
                    if (checkData(response))
                    {
                        String[][] waypoints = parseData(response);
                        gui.get_frmWaypoints().deleteAll();
                        for (int i=0;i<waypoints.length;i++)
                        {
                            gui.get_frmWaypoints().append(new StringItem(waypoints[i][1]+"-"+waypoints[i][0],waypoints[i][2]+" "+waypoints[i][3]));
                            gui.get_frmWaypoints().append(new StringItem(null,waypoints[i][4]+"\n"));
                        }
                        gui.getDisplay().setCurrent(gui.get_frmWaypoints());
                    }
                }
                catch (Exception e)
                {
                    gui.showError("waypoints",e.toString(),response);
                }
                break;
            case LOGS:
                try
                {
                    response = downloadData("part=logs&cookie="+cookie+"&waypoint="+waypoint);
                    if (checkData(response))
                    {
                        String[][] logs = parseData(response);
                        gui.get_frmLogs().deleteAll();
                        for (int i=0;i<logs.length;i++)
                        {
                            if (i==logs.length-1)
                            {
                                Command cmdMoreLogs = new Command(logs[i][0]+" dalších", Command.SCREEN, 1);;
                                gui.get_frmLogs().removeCommand(cmdMoreLogs);
                                if (!logs[i][0].equals("0"))
                                {
                                    gui.get_frmLogs().addCommand(cmdMoreLogs);
                                }
                                guideline = logs[i][1];
                            }
                            else
                            {
                                gui.get_frmLogs().append(new StringItem(logs[i][0]+":"+logs[i][1]+"("+logs[i][2]+" nalezeno)",logs[i][3]+": "+logs[i][4]));
                            }
                        }
                        gui.getDisplay().setCurrent(gui.get_frmLogs());
                    }
                }
                catch (Exception e)
                {
                    gui.showError("logs",e.toString(),response);
                }
                break;
            case ALL_LOGS:
                try
                {
                    response = downloadData("part=alllogs&cookie="+cookie+"&guideline="+guideline);
                    if (checkData(response))
                    {
                        String[][] logs = parseData(response);
                        gui.get_frmAllLogs().deleteAll();
                        for (int i=0;i<logs.length;i++)
                        {
                            gui.get_frmAllLogs().append(new StringItem(logs[i][0]+":"+logs[i][1]+"("+logs[i][2]+" nalezeno)",logs[i][3]+": "+logs[i][4]));
                        }
                        gui.getDisplay().setCurrent(gui.get_frmAllLogs());
                    }
                }
                catch (Exception e)
                {
                    gui.showError("alllogs",e.toString(),response);
                }
                break;
            case TRACKABLE: 
                try
                {
                    response = downloadData("part=trackable&cookie="+cookie+"&trnumber="+gui.get_tfTrackingNumber().getString());
                    if (checkData(response))
                    {
                        String[][] trackable = parseData(response);
                        gui.get_frmTrackable().setTitle(trackable[0][0]);
                        gui.get_siOrigin().setText(trackable[0][1]);
                        gui.get_siLastLocation().setText(trackable[0][2]);
                        gui.get_siReleased().setText(trackable[0][3]);
                        gui.get_siOwner().setText(trackable[0][4]);
                        gui.get_siGoal().setText(trackable[0][5]);
                        gui.get_siAbout().setText(trackable[0][6]);
                        gui.getDisplay().setCurrent(gui.get_frmTrackable());
                    }
                }
                catch (Exception e)
                {
                    gui.showError("trackable",e.toString(),response);
                }
                break;
            case PATTERNS:
                try
                {
                    response = downloadData("part=patterns&cookie="+cookie+"&waypoint="+waypoint);
                    if (checkData(response))
                    {
                        String[][] patternsArray = parseData(response);
                        patterns.addDownloaded(patternsArray);
                    }
                }
                catch (Exception e)
                {
                    gui.showError("patterns",e.toString(),response);
                }
                break;
                
        }
    }
    
    /***
     * Vlastni pripojeni k HTTP a prevod streamu na string
     */
    public String connect(String url)
    {
        HttpConnection c = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        ByteArrayOutputStream baos2 = null;
        DataOutputStream dos = null;
        ByteArrayInputStream bais = null;
        DataInputStream dis = null;
        try
        {
            // Vytvoreni http spojeni
            c = (HttpConnection) Connector.open(url);
            // Nastaveni pristupove metody
            c.setRequestMethod(HttpConnection.GET);
            // Otevreni vstupniho proudu
            gui.get_gaLoading().setValue(Gauge.CONTINUOUS_RUNNING);
            is = c.openInputStream();
            gui.get_gaLoading().setValue(Gauge.INCREMENTAL_UPDATING);
            
            //nacteni dat byte po bytu
            baos2 = new ByteArrayOutputStream();
            DataOutputStream os = new DataOutputStream(baos2);
            int onebyte;
            int x = 0;
            while ((onebyte = is.read()) != -1)
            {
                os.write(onebyte);
                if (x%50==0)
                    gui.get_gaLoading().setValue(Gauge.INCREMENTAL_UPDATING);
                x++;
            }
            byte[] polebytu = baos2.toByteArray();
            os.close();
            baos2.close();
            
            //nacteni delky a vstupu do vystupniho proudu - uprava na upravene UTF-8
            baos = new ByteArrayOutputStream();
            dos = new DataOutputStream(baos);
            dos.writeShort(polebytu.length);
            for(int i=0;i<polebytu.length;i++) dos.write(polebytu[i]);
            
            //ziskani stringu z vystupniho proudu
            byte[] prd = baos.toByteArray();
            bais = new ByteArrayInputStream(prd);
            dis = new DataInputStream(bais);
            String s = dis.readUTF();
            s = Utils.repairUTF8(s);
            gui.get_gaLoading().setValue(Gauge.INCREMENTAL_IDLE);
            //vraceni vystupnich dat
            return s;
            
            // v nacitani nastala chyba - tohle odchyceni vyjimky spolupracuje s
            //funkci stahniData()
        }
        catch (SecurityException e)
        {
            return "MUST_ALLOW";
        }
        catch (Exception e)
        {
            return "err:"+e.toString();
        }
        finally
        {
            try
            {
                //zavreni spojeni i v pripade vyjimky
                if (is != null)
                {
                    is.close();
                }
                if (c != null)
                {
                    c.close();
                }
                if (baos != null)
                {
                    baos.close();
                }
                if (baos2 != null)
                {
                    baos2.close();
                }
                if (dos != null)
                {
                    dos.close();
                }
                if (bais != null)
                {
                    bais.close();
                }
                if (dis != null)
                {
                    dis.close();
                }
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
    }
    
    
    /**
     * Zjednodusovaci metoda a rozhodovani, zda se data budou nacitat z kese nebo ne
     */
    public String downloadData(String data)
    {
        boolean cachedAction = false;
        String cachedResponse = null;
        if ((action == OVERVIEW || action == DETAIL || action == HINT || action == WAYPOINTS || action == LOGS || action == ALL_LOGS) && !refresh)
        {
            cachedAction = true;
            cachedResponse = cache.loadCachedResponse(data);
        }
        
        if (cachedResponse == null) //odpoved neni v kesi, stahujeme z netu
        {
            gui.getDisplay().setCurrent(gui.get_frmLoading());
            if (action == LOGIN)
                gui.get_siMessage().setText("Přihlašování k serveru geocaching.com...");
            else
                gui.get_siMessage().setText("Připojování k serveru a stahování dat...");
            String adress = url + "?" + data;
            String returns = "";
            System.out.println(adress);
            returns = connect(adress);
            //pokud se to zjebne zkusim to jeste jednou
            if (returns.startsWith("err"))
            { 
                try
                {
                    t.sleep(1000);
                }
                catch (InterruptedException ex)
                {
                }
                returns = connect(adress);
            }
            System.out.println(returns);
            if (cachedAction)
                cache.addCachedResponse(data, returns);
            return returns;
        }
        else //nacitame z kese
        {
            return cachedResponse;
        }
    }
    
    /**
     * Kontrola stazenych dat a zobrazeni chybovych hlasek
     */
    public boolean checkData(String data)
    {
        try
        {
            if (data.length() == 0)
            {
                gui.showAlert("Server geocaching.com vrátil neočekávanou odpověď",AlertType.ERROR,gui.get_lstMenu());
                return false;
            }
            else if (data.substring(0,3).equals("err"))
            {
                gui.showAlert("Špatně nastavené nebo nedostupné GPRS spojení",AlertType.ERROR,gui.get_lstMenu());
                return false;
            }
            else if (data.equals("MUST_ALLOW"))
            {
                gui.showAlert("Pro správnou funkčnost musíte povolit připojení. Restartujte aplikaci.",AlertType.WARNING,gui.get_lstMenu());
                return false;
            }
            else if (data.equals("ERR_BAD_PASSWORD"))
            {
                settings.set();
                gui.showAlert("Špatné uživatelské jméno nebo heslo",AlertType.ERROR,gui.get_frmSettings());
                return false;
            }
            else if (data.equals("ERR_AUTH_FAILED"))
            {
                gui.showAlert("Přihlášení selhalo",AlertType.ERROR,gui.get_lstMenu());
                return false;
            }            
            else if (data.equals("ERR_YOU_ARE_NOT_LOGGED"))
            {
                gui.showAlert("Nejste přihlášen k GC.com, pravdědodobně vypršela vaše session.",AlertType.WARNING,gui.get_lstMenu());
                gui.logged = false;
                return false;
            }
            else if (data.equals("ERR_BAD_WAYPOINT"))
            {
                gui.showAlert("Takový waypoint neexistuje!",AlertType.WARNING,gui.get_frmWaypoint());
                return false;
            }
            else if (data.equals("NO_WAYPOINTS"))
            {
                gui.showAlert("Tato cache nemá žádné přídavné waypointy",AlertType.INFO,gui.get_frmOverview());
                return false;
            }
            else if (data.equals("NO_HINT"))
            {
                gui.showAlert("Tato cache nemá nápovědu",AlertType.INFO,gui.get_frmOverview());
                return false;
            }
            else if (data.equals("PARSING_ERROR"))
            {
                gui.showAlert("Server geocaching.com pravděpodobně změnil HTML kód, kontaktujte prosím autora.",AlertType.ERROR,gui.get_lstMenu());
                return false;
            }
            else if (data.equals("WRONG_KEYWORD"))
            {
                gui.showAlert("Toto klíčové slovo neodpovídá žádné keši",AlertType.WARNING,gui.get_frmKeyword());
                return false;
            }
            else if (data.equals("WRONG_TRACKING_NUMBER"))
            {
                gui.showAlert("Předmět s tímto tracking number neexistuje",AlertType.WARNING,gui.get_frmTrackingNumber());
                return false;
            }
            else if (data.equals("ERR_PM_ONLY"))
            {
                gui.showAlert("Tato cache je přístupná jenom Premium Memberům",AlertType.WARNING,gui.get_lstSearch());
                return false;
            }
            else
            {
                return true;
            }
        }
        catch (Exception e)
        {
            gui.showError("checkData",e.toString(),data);
            return false;
        }
    }
    
    /***
     * Rozparsovani prijatych dat do dvojrozmerneho pole (data jsou oddeleny } a radek ukonceny {)
     */
    public String[][] parseData(String data)
    {
        try
        {
            //zjisteni velikosti vysledneho pole
            int i;
            int j = 0;
            int numberColumns = 0;
            int numberRows = 0;
            for (i=0;i<data.length();i++)
            {
                String znak = data.substring(i,i+1);
                if (znak.equals("}"))
                {
                    j++;
                }
                else if (znak.equals("{"))
                {
                    numberColumns = j+1;
                    j = 0;
                    numberRows++;
                }
            }
            //naplneni pole
            String[][] array = new String[numberRows][numberColumns];
            String word = "";
            int currentColumn = 0;
            int currentRow = 0;
            for (i=0;i<data.length();i++)
            {
                String znak = data.substring(i,i+1);
                if (znak.equals("{") || znak.equals("}"))
                {
                    array[currentRow][currentColumn] = word;
                    word = "";
                    currentColumn++;
                    if (currentColumn>numberColumns-1)
                    {
                        currentColumn = 0;
                        currentRow++;
                    }
                }
                else
                {
                    word += znak;
                }
            }
            return array;
        }
        catch (Exception e)
        {
            gui.showError("parseData",e.toString(),data);
            return null;
        }
    }
    
    
}
