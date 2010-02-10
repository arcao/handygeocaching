/*
 * Http.java
 * This file is part of HandyGeocaching.
 *
 * HandyGeocaching is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * (read more at: http://www.gnu.org/licenses/gpl.html)
 */
package http;

import database.Favourites;
import database.FieldNotes;
import database.OfflineCache;
import database.Patterns;
import database.Settings;
import gps.Gps;
import gui.Gui;
import gui.IconLoader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.StringItem;
import utils.StringTokenizer;
import utils.Utils;

/***
 * Tato trida komunikuje se skriptem pres HTTP protokol, parsuje stazena data
 * a zobrazuje je.
 * @author David Vavra
 */
public class Http implements Runnable
{
    
    
    //adresa skriptu
    private static final String url = "http://handygeocaching.sluzba.cz/handy31.php";
    //private static final String arcao_url = "http://testweb/gc/api.php";
    private static final String arcao_url = "http://hgservice.arcao.com/api.php";
    
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
    public static final int FIELD_NOTES = 12;
    public static final int DOWNLOAD_ALL_CACHES = 13;
    
    //reference na ostatni moduly
    private Gui gui;
    private Settings settings;
    private Favourites favourites;
    private Gps gps;
    private Cache cache;
    private IconLoader iconLoader;
    private Patterns patterns;
    private OfflineCache hintCache;
    private OfflineCache listingCache;
    
    private Thread t;
    
    private String cookie = ""; //cookie po zalogovani
    private String guideline = ""; //pouziva se pri zjistovani dalsich logu
    private int action; //druh vykonavane akce
    private int previousAction; //akce ukladana do historie kvuli logovani
    private boolean previousRefresh; //minuly refresh ukladany kvuli logovani
    private boolean offline; //offline mod - data se nacitaji z pameti
    public String[] waypoints; //waypointy v nalezenych kesich
    private String[][] foundCaches; //nalezene kese - [0]=nazev kese, [1]=typ kese, [3]=GC_ID
    public String waypoint; //zvoleny waypoint
    public String waypointCacheName; //nayev kese zvoleneho waypointu
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
        hintCache = new OfflineCache("hints");
        listingCache = new OfflineCache("listings");
    }

    public OfflineCache getHintCache() {
        return hintCache;
    }   

    public OfflineCache getListingCache() {
        return listingCache;
    }
       
    /***
     * Dodatecne pridani reference
     **/
    public void setReference(Gps ref)
    {
        gps = ref;
    }
    
    /**
     * Zastavi prenos, ukonci vlakno
     */
    public void stop() {
       t.interrupt();
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
        
        boolean inOfflineCache = false;
        if (action == HINT && hintCache.has(waypoint))
            inOfflineCache = true;
        
        if (action == DETAIL && listingCache.has(waypoint))
            inOfflineCache = true;
        
        if (!inOfflineCache && !gui.logged ) //nezalogovan => zalogovat
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
                    response = downloadData("part=login&sessid="+Utils.sessionId(settings.name, settings.password)+"&version="+gui.getAppProperty("MIDlet-Version")+"&light=0", false, false);
                    if (checkData(response))
                    {
                        String[][] login = parseData(response);
                        cookie = login[0][0];
                        gui.logged = true;
                        //kontrola verze
                        if (!login[0][1].equals("OK") && compareVersion(gui.getAppProperty("MIDlet-Version"), login[0][1]) < 0)
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
                catch (InterruptedException e) {
                    return;
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
                    if (gps.convertLattitude(gui.get_tfLattitude().getString())==Double.NaN || gps.convertLongitude(gui.get_tfLongitude().getString())==Double.NaN)
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
                        response = downloadData("part=nearest&"+coordinates+"&filter="+settings.filter+"&numberCaches="+settings.numberCaches, false, true, "Stahuji seznam nejbližších keší...");
                        if (checkData(response))
                        {
                            foundCaches = parseData(response);
                            gui.get_lstNearestCaches().setTitle("Nejbližší keše");
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
                }
                catch (InterruptedException e) {
                    return;
                }
                catch (Exception e)
                {
                    gui.showError("nejblizsi kese",e.toString(),response);
                }
                break;
            case KEYWORD:
                try
                {
                    response = downloadData("part=keyword&keyword="+Utils.urlUTF8Encode(gui.get_tfKeyword().getString())+"&numberCaches="+settings.numberCaches, false, true, "Stahuji seznam keší...");
                    if (checkData(response))
                    {
                        foundCaches = parseData(response);
                        gui.get_lstNearestCaches().setTitle("Nalezené keše");
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
                catch (InterruptedException e) {
                    return;
                }
                catch (Exception e)
                {
                    gui.showError("keyword",e.toString(),response);
                }
                break;
            case OVERVIEW:
                try
                {
                    gui.fromPreview = true;
                    
                    if (!offline)
                        response = downloadData("part=overview&waypoint="+waypoint, false, true, "Stahuji informace o keši " + waypointCacheName + "...");
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
                            gui.get_frmOverview().setTitle("Detaily keše");
                        }
                        else
                        {
                            //disabled nebo archieved cache
                            gui.get_frmOverview().setTitle(listing[0][9]);
                        }
                        typeNumber = listing[0][10];
                                               
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
                            gui.get_siInventory().setText(gui.get_siInventory().getText()+"\n"+"Tato keš umožňuje stažení vzorečků do MultiSolveru!");
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
                            favourites.addEdit(listing[0][0],response,listing[0][4],listing[0][5],typeNumber,null, false, (offline) ? favourites.found : "", (offline) ? favourites.poznamka : "", false, true, true);
                            //Zephy 19.11.07 +/
                        if (!offline) {
                            favourites.editId = -1;
                            //Zephy 19.11.07 +\ -pridan posledni parametr
                            favourites.addEdit("_Poslední keš",response,listing[0][4],listing[0][5],typeNumber,null, false, "NE", "",false, true, true);                        
                            //Zephy 19.11.07 +/
                        }
                        gui.getDisplay().setCurrent(gui.get_frmOverview());
                    }
                }
                catch (InterruptedException e) {
                    return;
                }
                catch (Exception e)
                {
                    gui.showError("overview",e.toString(),response);
                }
                break;
            case DOWNLOAD_ALL_CACHES:
                try {
                    for (int i = 0; i < waypoints.length; i++) {
                        response = downloadData("part=overview&waypoint="+waypoints[i],false, true, "Stahuji keš " + foundCaches[i][0] + "...");
                        if (checkData(response))
                        {
                            String[][] listing = parseData(response);
                            favourites.editId = -1;
                            favourites.addEdit(listing[0][0],response,listing[0][4],listing[0][5],listing[0][10],null, false, "", "", false, false, false);
                        }
                        else 
                        {
                            break;
                        }
                    }
                    favourites.revalidate();
                    gui.showAlert("Keše byly přidány do oblíbených.", AlertType.INFO, gui.get_lstNearestCaches());
                }
                catch (InterruptedException e) {
                    favourites.revalidate();
                    return;
                }
                catch (Exception e)
                {
                    gui.showError("overview",e.toString(),response);
                }
                break;
            case DETAIL:
                try
                {
                    response = downloadData("part=info&waypoint="+waypoint, false, true, "Stahuji listing keše " + waypointCacheName + "...");
                    if (checkData(response))
                    {
                        gui.get_frmInfo().deleteAll();
                        gui.get_frmInfo().append(gui.get_siBegin());
                        gui.get_frmInfo().append(response);
                        gui.get_frmInfo().append(gui.get_siEnd());
                        //limitace Javy na telefonech SE? nasledujici radka zobrazi jen par vet.
                        //gui.get_siContent().setText(response);
                        gui.getDisplay().setCurrent(gui.get_frmInfo());
                    }
                }
                catch (InterruptedException e) {
                    return;
                }
                catch (Exception e)
                {
                    gui.showError("listing",e.toString(),response);
                }
                break;
            case HINT:
                try
                {
                    response = downloadData("part=hint&waypoint="+waypoint, false, true, "Stahuji nápovědu keše " + waypointCacheName + "...");
                    if (checkData(response))
                    {
                        gui.get_frmHint().deleteAll();
                        gui.get_frmHint().append(response);
                        gui.getDisplay().setCurrent(gui.get_frmHint());
                    }
                }
                catch (InterruptedException e) {
                    return;
                }
                catch (Exception e)
                {
                    gui.showError("hint",e.toString(),response);
                }
                break;
            case WAYPOINTS:
                try
                {
                    response = downloadData("part=waypoints&waypoint="+waypoint, false, true, "Stahuji waypointy keše " + waypointCacheName + "...");
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
                catch (InterruptedException e) {
                    return;
                }
                catch (Exception e)
                {
                    gui.showError("waypoints",e.toString(),response);
                }
                break;
            case LOGS:
                try
                {
                    response = downloadData("part=logs&waypoint="+waypoint, false, true, "Stahuji logy keše " + waypointCacheName + "...");
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
                catch (InterruptedException e) {
                    return;
                }
                catch (Exception e)
                {
                    gui.showError("logs",e.toString(),response);
                }
                break;
            case ALL_LOGS:
                try
                {
                    response = downloadData("part=alllogs&guideline="+guideline, false, true, "Stahuji všechny logy keše " + waypointCacheName + "...");
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
                catch (InterruptedException e) {
                    return;
                }
                catch (Exception e)
                {
                    gui.showError("alllogs",e.toString(),response);
                }
                break;
            case TRACKABLE: 
                try
                {
                    response = downloadData("part=trackable&trnumber="+gui.get_tfTrackingNumber().getString(), false, true, "Hledám informace o předmětu...");
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
                catch (InterruptedException e) {
                    return;
                }
                catch (Exception e)
                {
                    gui.showError("trackable",e.toString(),response);
                }
                break;
            case PATTERNS:
                try
                {
                    response = downloadData("part=patterns&waypoint="+waypoint, false, true, "Stahuji vzorečky keše " + waypointCacheName + "...");
                    if (checkData(response))
                    {
                        String[][] patternsArray = parseData(response);
                        patterns.addDownloaded(patternsArray);
                    }
                }
                catch (InterruptedException e) {
                    return;
                }
                catch (Exception e)
                {
                    gui.showError("patterns",e.toString(),response);
                }
                break;
            case FIELD_NOTES:
                try
                {
                    response = downloadData("action=fieldnotes&fieldnotes="+Utils.urlUTF8Encode(FieldNotes.getInstance().getFieldNotes())+"&incremental="+((settings.incrementalFieldNotes)?"1":"0"), true, true, "Nahrávám Field notes na GC.com...");
                    if (checkData(response))
                    {
                        gui.showAlert("Nahráno " + response + " nových Field notes na GC.com.",AlertType.INFO,gui.get_lstFieldNotes());
                    }
                }
                catch (InterruptedException e) {
                    return;
                }
                catch (Exception e)
                {
                    gui.showError("field_notes",e.toString(),response);
                }
                break;
                
        }
    }
    
    /***
     * Vlastni pripojeni k HTTP a prevod streamu na string
     */
    public String connect(String url) throws InterruptedException
    {
        InputStreamReader reader = null;
        InputStream is = null;
        HttpConnection c = null;
        try
        {
            // Vytvoreni http spojeni
            c = (HttpConnection) Connector.open(url);
            
            // Nastaveni pristupove metody
            c.setRequestMethod(HttpConnection.GET);

            // Otevreni vstupniho proudu
            gui.get_gaLoading().setValue(Gauge.CONTINUOUS_RUNNING);
            if (c.getResponseCode() != 200)
                throw new Exception("TIMEOUT");
            is = c.openInputStream();
            reader = new InputStreamReader(is, "UTF-8");
            gui.get_gaLoading().setValue(Gauge.INCREMENTAL_UPDATING);
            
            //int ch;
            //int x = 0;
            StringBuffer sb = new StringBuffer();
            
            char[] charBuffer = new char[1024];
            int len;
            
            while ((len = reader.read(charBuffer)) != -1)
            {
                gui.get_gaLoading().setValue(Gauge.INCREMENTAL_UPDATING);
                sb.append(charBuffer, 0, len);
                //if (x%50==0)
                //    gui.get_gaLoading().setValue(Gauge.INCREMENTAL_UPDATING);
                //x++;
            }

            String s = sb.toString();
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
        catch (InterruptedException e) {
            throw e;
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
                if (reader != null)
                {
                    reader.close();
                }
                if (is != null)
                {
                    is.close();
                }
                if (c != null)
                {
                    c.close();
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
    public String downloadData(String data) throws InterruptedException {
        return downloadData(data, false, true, null);
    }
    
    public String downloadData(String data, boolean useArcaoUrl, boolean addCookie) throws InterruptedException {
        return downloadData(data, useArcaoUrl, addCookie, null);
    }
        
    public String downloadData(String data, boolean useArcaoUrl, boolean addCookie, String message) throws InterruptedException
    {
        boolean cachedAction = false;
        String cachedResponse = null;
        if ((action == OVERVIEW || action == DOWNLOAD_ALL_CACHES || action == WAYPOINTS || action == LOGS || action == ALL_LOGS) && !refresh)
        {
            cachedAction = true;
            cachedResponse = cache.loadCachedResponse(data);
        } else if (action == HINT) {
            cachedAction = true;
            cachedResponse = hintCache.get(waypoint);
        } else if (action == DETAIL) {
            cachedAction = true;
            cachedResponse = listingCache.get(waypoint);
        }
        
        if (cachedResponse == null) //odpoved neni v kesi, stahujeme z netu
        {
            gui.getDisplay().setCurrent(gui.get_frmLoading());
            if (message != null)                
                gui.get_siMessage().setText(message);
            else if (action == LOGIN)
                gui.get_siMessage().setText("Přihlašování k serveru geocaching.com...");
            else
                gui.get_siMessage().setText("Připojování k serveru a stahování dat...");
            String adress = ((useArcaoUrl) ? arcao_url : url) + "?" + data;
            if (addCookie) adress+= "&cookie="+cookie;
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
                                   
            if (cachedAction && action == HINT)
                hintCache.add(waypoint, returns);
            else if (cachedAction && action == DETAIL)
                listingCache.add(waypoint, returns);
            else if (cachedAction)
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
            if (data.equals("err:TIMEOUT"))
            {
                gui.showAlert("Vypršel časový limit spojení. Server geocaching.com neodpovídá. Zkuste to za chvilku znovu.",AlertType.ERROR,gui.get_lstMenu());
                return false;
            }
            else if (data.length() >= 3 && data.substring(0,3).equals("err"))
            {
                gui.showAlert("Špatně nastavené nebo nedostupné GPRS spojení: " + data,AlertType.ERROR,gui.get_lstMenu());
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
                gui.showAlert("Špatné uživatelské jméno nebo heslo.",AlertType.ERROR,gui.get_frmSettings());
                return false;
            }
            else if (data.equals("ERR_AUTH_FAILED"))
            {
                gui.showAlert("Přihlášení selhalo.",AlertType.ERROR,gui.get_lstMenu());
                return false;
            }            
            else if (data.equals("ERR_YOU_ARE_NOT_LOGGED"))
            {
                gui.logged = false;
                
                //refresh musi byt true, jinak nacita stale z cache informaci, ze uzivatel neni prihlasen
                start(action, true);
                return false;
                //gui.showAlert("Nejste přihlášen k GC.com, pravdědodobně vypršela vaše session.",AlertType.WARNING,gui.get_lstMenu());
                //return false;
            }
            else if (data.equals("ERR_BAD_WAYPOINT"))
            {
                gui.showAlert("Takový waypoint neexistuje!",AlertType.WARNING,gui.get_frmWaypoint());
                return false;
            }
            else if (data.equals("NO_WAYPOINTS"))
            {
                gui.showAlert("Tato keš nemá žádné přídavné waypointy.",AlertType.INFO,gui.get_frmOverview());
                return false;
            }
            else if (data.equals("NO_HINT"))
            {
                gui.showAlert("Tato keš nemá nápovědu.",AlertType.INFO,gui.get_frmOverview());
                return false;
            }
            else if (data.equals("PARSING_ERROR"))
            {
                gui.showAlert("Server geocaching.com pravděpodobně změnil HTML kód, kontaktujte prosím autora.",AlertType.ERROR,gui.get_lstMenu());
                return false;
            }
            else if (data.equals("WRONG_KEYWORD"))
            {
                gui.showAlert("Toto klíčové slovo neodpovídá žádné keši.",AlertType.WARNING,gui.get_frmKeyword());
                return false;
            }
            else if (data.equals("WRONG_TRACKING_NUMBER"))
            {
                gui.showAlert("Předmět s tímto tracking number neexistuje.",AlertType.WARNING,gui.get_frmTrackingNumber());
                return false;
            }
            else if (data.equals("ERR_PM_ONLY"))
            {
                gui.showAlert("Tato keš je přístupná jenom Premium Memberům.",AlertType.WARNING,gui.get_lstSearch());
                return false;
            }
            else if (data.equals("ERR_FIELD_NOTES_FAILED")) 
            {
                gui.showAlert("Nepovedlo se odeslat Field notes.",AlertType.ERROR,gui.get_lstFieldNotes());
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

    private static int compareVersion(String ver1, String ver2) {
        int[] intVer1 = parseVersion(ver1);
        int[] intVer2 = parseVersion(ver2);
        
        int cmp;
        
        if (intVer1.length <= intVer2.length) {
            for(int i = 0; i < intVer1.length; i++) {
                int num = (i < intVer1.length) ? intVer1[i] : 0;
                
                cmp = compareNumber(num, intVer2[i]);
                if (cmp != 0)
                    return cmp;
            }            
            return 0;
        } else {
            for(int i = 0; i < intVer2.length; i++) {
                int num = (i < intVer2.length) ? intVer2[i] : 0;
                
                cmp = compareNumber(intVer1[i], num);
                if (cmp != 0)
                    return cmp;
            }
            return 0;
        }
    }
    
    private static int compareNumber(int number1, int number2) {
       if (number1 > number2)
           return 1;
       if (number1 < number2)
           return -1;
       return 0;
    }
    
    private static int[] parseVersion(String version) {
        String[] stringArray = StringTokenizer.getArray(version, ".");
        int[] intArray = new int[stringArray.length];
        
        for(int i = 0; i < intArray.length; i++) {
            try {
                intArray[i] = Integer.parseInt(stringArray[i]);
            } catch (NumberFormatException e) {
                intArray[i] = 0;
            }
        }
        
        return intArray;
    }
    
    
}
