package handy;

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
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.StringItem;

/***
 * Tato trida komunikuje se skriptem pres HTTP protokol, parsuje stazena data
 * a zobrazuje je.
 */
public class Http implements Runnable
{
    
    //adresa skriptu
    private static final String url = "http://handygeocaching.sluzba.cz/handy31.php";
    private static final String url2 = "http://log.destil.cz/log26.php"; //skript pro logovani
    private static final String arcao_url = "http://hgservice.arcao.com/api.php";
    //  private static final String url = "http://localhost/Destilapps/HandyGeocaching/handy28.php";
    //  private static final String url2 = "http://localhost/Destilapps/HandyGeocaching/log26.php"; //skript pro logovani
    
    //mozne akce
    static final int LOGIN = 0;
    static final int NEAREST_CACHES = 1;
    static final int OVERVIEW = 2;
    static final int LISTING = 3;
    static final int HINT = 4;
    static final int WAYPOINTS = 5;
    static final int LOGS = 6;
    static final int NEXT_NEAREST = 7;
    static final int ALL_LOGS = 8;
    static final int LOG_IT = 9;
    static final int SEND_LOG = 10;
    static final int KEYWORD = 11;
    static final int TRACKABLE = 12;
    static final int TRACKABLE_LOG = 13;
    static final int TRACKABLE_SEND_LOG = 14;
    static final int PATTERNS = 15;
    
    //reference na ostatni moduly
    Gui gui;
    Database database;
    Cache cache;
    Thread t;
    
    private String cookie = ""; //cookie po zalogovani
    private String guideline = ""; //pouziva se pri zjistovani dalsich logu
    private String logid = ""; //pouziva se pri odkazu na logovani
    private String[] logtypenumbers; //ukladaji se sem druhy logu
    private String[] travelbugs; //ukladaji se sem travelbugy, ktere je mozno hodit do kese pri logovani
    private String viewstate; //uklada se do nej viewstate pro odeslani formulare logovani
    private int action; //druh vykonavane akce
    private int previousAction; //akce ukladana do historie kvuli logovani
    private boolean offline; //offline mod - data se nacitaji z pameti
    public String[] waypoints; //waypointy v nalezenych kesich
    public String waypoint; //zvoleny waypoint
    public String typeNumber; //cislo obrazku typu - pouziva se pri ukladani do oblibenych
    public String response; //odpoved HTTP, vyuziva se hlavne pri ukladani do cache
    public String favouriteResponse; //odpoved HTTP, vyuziva se pri ukladani do oblibenych
    
    Http(Gui reference, Database db)
    {
        gui = reference;
        database = db;
        cache = new Cache();
    }
    
    /***
     * Zacatek HTTP komunikace
     */
    public void start(int act)
    {
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
        offline = false;
        if (!gui.logged) //nezalogovan => zalogovat
        {
            if (database.name.equals("") && database.password.equals(""))
            {
                gui.showAlert("Nemáte nastaveny přihlašovací údaje na server geocaching.com, budete přesměrováni do nastavení.",AlertType.WARNING,gui.get_frmSettings());
                database.setSettings();
            }
            else
            {
                previousAction = action;
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
                    response = downloadData("part=login&sessid="+Utils.sessionId(database.name, database.password)+"&version="+gui.getAppProperty("MIDlet-Version")+"&light=0");
                    if (checkData(response))
                    {
                        String[][] login = parseData(response);
                        cookie = login[0][0];
                        gui.logged = true;
                        //kontrola verze
                        if (false && !login[0][1].equals("OK"))
                        {
                            gui.showAlert("Je k dispozici nová verze aplikace: "+login[0][1],AlertType.INFO,gui.get_frmLoading());
                            t.sleep(3000);
                        }
                        //vip mod
                        if (login[0][2].equals("1"))
                            database.setVIP(true);
                        else
                            database.setVIP(false);
                        start(previousAction);
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
                    coordinates = "lattitude="+Utils.convertLattitude(gui.get_tfLattitude().getString())+"&longitude="+Utils.convertLongitude(gui.get_tfLongitude().getString());
                    //spatny format souradnic
                    if (Utils.convertLattitude(gui.get_tfLattitude().getString()).equals("0") || Utils.convertLongitude(gui.get_tfLongitude().getString()).equals("0"))
                    {
                        rightCoordFormat = false;
                        gui.showAlert("Špatný formát souřadnic",AlertType.WARNING,gui.get_frmCoordinates());
                    }
                    else
                    {
                        database.saveCoordinates(gui.get_tfLattitude().getString(), gui.get_tfLongitude().getString());
                    }
                    if (rightCoordFormat)
                    {
                        response = downloadData("part=nearest&cookie="+cookie+"&"+coordinates+"&filter="+database.filter+"&numberCaches="+database.numberCaches);
                        //response = "1mi Kourim - Old Town at 50/15}3}GCK0A3{1.3mi Kaskada}2}GCRHGZ{3.1mi Zahradni zeleznice}2}GC12TEG{3.2mi Vykricnik}2}GCV9Z8{6.7mi Dve Veze / Two Towers}8}GCM39Z{7.1mi Jigsaw cache}3}GCQVDN{7.8mi Nix daal}3}GCTVE5{8.1mi Hradnikova okruzni cache / Hradniks ring chache}2}GCXYJ2{8.3mi Skaly u Kohoutova mlyna}2}GC12RVP{";
                        if (checkData(response))
                        {
                            String[][] nearestCaches = parseData(response);
                            gui.clearListForm(gui.get_lstNearestCaches(),null);
                            waypoints = new String[nearestCaches.length];
                            for (int i=0;i<nearestCaches.length;i++)
                            {
                                gui.get_lstNearestCaches().append(nearestCaches[i][0],Image.createImage("/"+nearestCaches[i][1]+".png"));
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
                    response = downloadData("part=keyword&cookie="+cookie+"&keyword="+Utils.urlUTF8Encode(gui.get_tfKeyword().getString())+"&numberCaches="+database.numberCaches);
                    if (checkData(response))
                    {
                        String[][] foundCaches = parseData(response);
                        gui.clearListForm(gui.get_lstKeyword(),null);
                        waypoints = new String[foundCaches.length];
                        for (int i=0;i<foundCaches.length;i++)
                        {
                            gui.get_lstKeyword().append(foundCaches[i][0],Image.createImage("/"+foundCaches[i][1]+".png"));
                            waypoints[i] = foundCaches[i][2];
                        }
                        gui.getDisplay().setCurrent(gui.get_lstKeyword());
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
                        logid = listing[0][7];
                        typeNumber = listing[0][10];
                        //jsou pridavne waypointy?
                        gui.get_frmOverview().removeCommand(gui.get_cmdWaypoints());
                        if (listing[0][12].equals("1"))
                            gui.get_frmOverview().addCommand(gui.get_cmdWaypoints());
                        //je mozne stahnout vzorce?
                        gui.get_frmOverview().removeCommand(gui.get_cmdDownloadPatterns());
                        if (listing[0][13].equals("1"))
                        {
                            gui.get_frmOverview().addCommand(gui.get_cmdDownloadPatterns());
                            gui.get_siInventory().setText(gui.get_siInventory().getText()+"\n"+"Tato cache umožnuje stažení vzorečků do MultiSolveru!");
                        }
                        //je mozne obnovit listing?
                        gui.get_frmOverview().removeCommand(gui.get_cmdRefresh());
                        if (offline)
                            gui.get_frmOverview().addCommand(gui.get_cmdRefresh());
                        favouriteResponse = response;
                        if (!offline)
                            database.addFavourite("Poslední cache",response,listing[0][4],listing[0][5],typeNumber);
                        gui.getDisplay().setCurrent(gui.get_frmOverview());
                    }
                }
                catch (Exception e)
                {
                    gui.showError("overview",e.toString(),response);
                }
                break;
            case LISTING:
                try
                {
                    response = downloadData("part=info&cookie="+cookie+"&waypoint="+waypoint);
                    if (checkData(response))
                    {
                        gui.get_siContent().setText(response); 
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
                        gui.clearListForm(null, gui.get_frmHint());
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
                        gui.clearListForm(null, gui.get_frmWaypoints());
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
                        gui.clearListForm(null, gui.get_frmLogs());
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
                        gui.clearListForm(null, gui.get_frmAllLogs());
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
            case LOG_IT:
                //typy logu
                gui.clearChoiceGroup(gui.get_cgLogType());
                for (int i=0;i<=Gui.TYPE_NEEDS_MAINTENANCE;i++)
                {
                    gui.get_cgLogType().append(Gui.getFieldNoteTypeString(i),null);
                }
                gui.get_tfLogText().setString(Utils.replaceString(database.defaultLog, "%t", Gui.getDateString()));

                gui.getDisplay().setCurrent(gui.get_frmLogIt());
                break;
            case SEND_LOG:
                try
                {
                    response = downloadData("cookie="+cookie+"&action=fieldnotes&fieldnotes="+Utils.urlUTF8Encode(Gui.getFieldNote(
                            logid,
                            gui.get_cgLogType().getSelectedIndex(),
                            gui.get_tfLogText().getString())
                    )+"&incremental=0", arcao_url);
                    checkData(response);
                }
                catch (Exception e)
                {
                    gui.showError("send_log",e.toString(),response);
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
                        logid = trackable[0][7];
                        gui.getDisplay().setCurrent(gui.get_frmTrackable());
                    }
                }
                catch (Exception e)
                {
                    gui.showError("trackable",e.toString(),response);
                }
                break;
            case TRACKABLE_LOG:
                try
                {
                    response = downloadData("part=trackable_log&cookie="+cookie+"&logid="+logid);
                    if (checkData(response))
                    {
                        String[][] logtypes = parseData(response);
                        gui.clearChoiceGroup(gui.get_cgTrLogType());
                        logtypenumbers = new String[logtypes.length-1];
                        for (int i=0;i<logtypes.length-1;i++)
                        {
                            logtypenumbers[i] = logtypes[i][0];
                            gui.get_cgTrLogType().append(logtypes[i][1],null);
                        }
                        viewstate = logtypes[logtypes.length-1][0];
                        gui.getDisplay().setCurrent(gui.get_frmLogTrackable());
                    }
                }
                catch (Exception e)
                {
                    gui.showError("trackable log it",e.toString(),response);
                }
                break;
            case TRACKABLE_SEND_LOG:
                try
                {
                    String data = "viewstate="+Utils.urlUTF8Encode(viewstate)+"&logtext="+Utils.urlUTF8Encode(gui.get_tfTrLogText().getString());
                    response = sendHttpPost(url2+"?part=trackable&trnumber="+gui.get_tfTrackingNumber().getString()+"&cookie="+cookie+"&logid="+logid+"&logtype="+logtypenumbers[gui.get_cgTrLogType().getSelectedIndex()],data);
                    checkData(response);
                }
                catch (Exception e)
                {
                    gui.showError("trackable send_log",e.toString(),response);
                }
                break;
            case PATTERNS:
                try
                {
                    response = downloadData("part=patterns&cookie="+cookie+"&waypoint="+waypoint);
                    if (checkData(response))
                    {
                        String[][] patterns = parseData(response);
                        database.addPatterns(patterns);
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
        String progress = "";
        try
        {
            progress = "1";
            // Vytvoreni http spojeni
            c = (HttpConnection) Connector.open(url);
            // Nastaveni pristupove metody
            c.setRequestMethod(HttpConnection.GET);
            // Otevreni vstupniho proudu
            progress += "2";
            is = c.openInputStream();
            
            //nacteni dat byte po bytu
            progress += "3";
            baos2 = new ByteArrayOutputStream();
            DataOutputStream os = new DataOutputStream(baos2);
            int onebyte;
            progress += "4";
            while ((onebyte = is.read()) != -1)
            {
                os.write(onebyte);
            }
            progress += "5";
            byte[] polebytu = baos2.toByteArray();
            os.close();
            baos2.close();
            
            //nacteni delky a vstupu do vystupniho proudu - uprava na upravene UTF-8
            progress += "6";
            baos = new ByteArrayOutputStream();
            dos = new DataOutputStream(baos);
            progress += "7";
            dos.writeShort(polebytu.length);
            for(int i=0;i<polebytu.length;i++) dos.write(polebytu[i]);
            
            //ziskani stringu z vystupniho proudu
            progress += "8";
            byte[] prd = baos.toByteArray();
            bais = new ByteArrayInputStream(prd);
            dis = new DataInputStream(bais);
            progress += "9";
            String s = dis.readUTF();
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
            //System.out.println("err");
            return "err:"+e.toString()+" progress="+progress;
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
     * Odeslani dat pomoci POSTu, pouziva se pri logovani
     */
    private String sendHttpPost( String url, String postMessage )
    {
        gui.getDisplay().setCurrent(gui.get_frmLoading());
        gui.get_siMessage().setText("Připojování k serveru a odesílání dat...");
        HttpConnection      hcon = null;
        DataInputStream     dis = null;
        OutputStream    os = null;
        StringBuffer        responseMessage = new StringBuffer();
        // the request body
        String requeststring = postMessage;
        
        try
        {
            // an HttpConnection with both read and write access
            System.out.println(url);
            hcon = ( HttpConnection )Connector.open( url );
            
            // set the request method to POST
            hcon.setRequestMethod( HttpConnection.POST );
            hcon.setRequestProperty("User-Agent","Profile/MIDP-2.0 Configuration/CLDC-1.1");
            hcon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            hcon.setRequestProperty("Content-Length", String.valueOf(requeststring.length()));
            // obtain DataOutputStream for sending the request string
            os = hcon.openOutputStream();
            byte[] request_body = requeststring.getBytes();
            
            // send request string to server
            os.write(request_body);
            //end for( int i = 0; i < request_body.length; i++ )
            
            // obtain DataInputStream for receiving server response
            dis = new DataInputStream( hcon.openInputStream() );
            
            // retrieve the response from server
            int ch;
            while( ( ch = dis.read() ) != -1 )
            {
                responseMessage.append( (char)ch );
            }//end while( ( ch = dis.read() ) != -1 ) {
        }
        catch( Exception e )
        {
            responseMessage.append( "ERROR" );
        }
        finally
        {
            // free up i/o streams and http connection
            try
            {
                if( hcon != null ) hcon.close();
                if( dis != null ) dis.close();
                if( os != null ) os.close();
            }
            catch ( IOException ioe )
            {
                ioe.printStackTrace();
            }//end try/catch
        }//end try/catch/finally
        System.out.println(responseMessage);
        return responseMessage.toString();
    }
    
    public String downloadData(String data) {
        return downloadData(data, url);
    }
    
    /**
     * Zjednodusovaci metoda a rozhodovani, zda se data budou nacitat z kese nebo ne
     */
    public String downloadData(String data, String url)
    {
        boolean cachedAction = false;
        String cachedResponse = null;
        if (action == OVERVIEW || action == LISTING || action == HINT || action == WAYPOINTS || action == LOGS || action == ALL_LOGS || action == LOG_IT)
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
            if (data.length() > 2 && data.substring(0,3).equals("err"))
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
                database.setSettings();
                gui.showAlert("Špatné uživatelské jméno nebo heslo",AlertType.ERROR,gui.get_frmSettings());
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
            else if (action == SEND_LOG && data.equals("1"))
            {
                if (gui.trackables)
                    gui.showAlert("Log byl úspěšně uložen",AlertType.CONFIRMATION,gui.get_frmTrackable());
                else
                    gui.showAlert("Log byl úspěšně uložen",AlertType.CONFIRMATION,gui.get_frmOverview());
                return false;
            }
            else if (action == SEND_LOG && data.equals("0"))
            {
                if (gui.trackables)
                    gui.showAlert("Log nebyl uložen",AlertType.WARNING,gui.get_frmTrackable());
                else
                    gui.showAlert("Log nebyl uložen",AlertType.WARNING,gui.get_frmOverview());
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
