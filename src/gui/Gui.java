/*
 * Gui.java
 * This file is part of HandyGeocaching.
 *
 * HandyGeocaching is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * (read more at: http://www.gnu.org/licenses/gpl.html)
 */
package gui;

import database.FieldNotes;
import database.FieldNotesItem;
import java.util.Calendar;
import javax.microedition.lcdui.ItemStateListener;
import utils.ConfirmDialog;
import utils.GPXImport;
import utils.OpenFileBrowser;
import database.Favourites;
import database.MultiSolver;
import database.Patterns;
import database.Settings;
import gps.Bluetooth;
import gps.Gps;
import gps.GpsParser;
import http.Http;
import java.util.Date;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.DateField;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;
import track.Track;
import utils.References;
import utils.Utils;


/***
 * Tato trida se stara o interakci s uzivatelem, zobrazovani GUI a aplikacni
 * logiku
 * @author David Vavra
 */
public class Gui extends MIDlet implements CommandListener, ItemStateListener {
    //mody aplikace
    public boolean modeGPS = false;
    public boolean nearest = false;
    public boolean logged = false;
    public boolean nearestFromWaypoint = false;
    public boolean nearestFromFavourite = false;
    public boolean navigateToPoint = false;
    public boolean navigateToFavourite = false;
    public boolean keyword = false;
    public boolean fromFavourites = false;
    public boolean fromTrackables = false;
    public boolean fromMultiSolver = false;
    public boolean fromFieldNotes = false;
    public boolean fromPreview = false;
    public boolean gpsGate = false;
    public boolean nightMode = false;
    
    //reference na jednotlive moduly
    private Gps gps = null;
    private Bluetooth bluetooth;
    private Settings settings;
    private Favourites favourites;
    private MultiSolver multiSolver;
    private Patterns patterns;
    public Http http;
    private GpsParser gpsParser;
    private IconLoader iconLoader;
    private Track track;
    private References references;
    
    private FieldNotesItem fieldNoteItem = null;
    
    /***
     * Creates a new instance of Gui
     */
    public Gui() {
        settings = new Settings(this);
        iconLoader = new IconLoader(this);
        favourites = new Favourites(this, settings, iconLoader);
        patterns = new Patterns(this);
        multiSolver = new MultiSolver(this, patterns);
        http = new Http(this, settings, favourites, iconLoader, patterns);
        favourites.setReference(http);
        settings.load();
        initialize();
        references = new References();
    }
    
    /**
     * Dodatecne nastavovani referenci
     */
    public void setReference(Gps ref) {
        gps = ref;
    }
    
    public void setReference(GpsParser ref) {
        gpsParser = ref;
    }
    
    public void setReference(Bluetooth ref) {
        bluetooth = ref;
    }
    
    public void setReference(Track ref) {
        track = ref;
    }
    
    private List lstDevices;//GEN-BEGIN:MVDFields
    private List lstMenu;
    private Form frmAveraging;
    private StringItem siCurrentCoordinates;
    private StringItem siAverageLattitude;
    private StringItem siMeasures;
    private StringItem siAdditional;
    private Command cmdExit;
    private Command cmdBack;
    private Command cmdPause;
    private Command cmdResume;
    private List lstMode;
    private Form frmLoading;
    private StringItem siMessage;
    private Command cmdSend;
    private Command cmdMenu;
    private Form frmCoordinates;
    private TextField tfLattitude;
    private TextField tfLongitude;
    private List lstNearestCaches;
    private Form frmWaypoint;
    private TextField tfWaypoint;
    private Form frmOverview;
    private StringItem siName;
    private StringItem siAuthor;
    private StringItem siWaypoint;
    private StringItem siType;
    private StringItem siSize;
    private StringItem siDifficulty;
    private StringItem siInventory;
    private Command cmdHint;
    private Command cmdInfo;
    private Command cmdLogs;
    private Command cmdWaypoints;
    private Form frmInfo;
    private Form frmHint;
    private Form frmWaypoints;
    private Form frmLogs;
    private Image imgSearch;
    private Image imgNoGps;
    private Image imgGps;
    private Image imgAveraging;
    private Image imgAbout;
    private Image imgExit;
    private Form frmAbout;
    private StringItem siVerze;
    private StringItem stringItem1;
    private Command cmdStop;
    private Image imgSettings;
    private Form frmSettings;
    private Command cmdSave;
    private StringItem stringItem3;
    private TextField tfName;
    private TextField tfPassword;
    private ChoiceGroup cgCacheFilter;
    private Form frmConnecting;
    private Command cmdNavigate;
    private StringItem stringItem6;
    private Command cmdNext;
    private Form frmAllLogs;
    private Image imgNavigate;
    private Font fntBold;
    private Font fntNormal;
    private org.netbeans.microedition.lcdui.SplashScreen ssAdvertisement;
    private Image imgAdvertisement;
    private Form frmDebug;
    private StringItem siDebug;
    private StringItem siDebug2;
    private Image imgKeyword;
    private Image imgFavourites;
    private TextField tfNumberCaches;
    private Form frmKeyword;
    private TextField tfKeyword;
    private List lstKeyword;
    private List lstSearch;
    private Image imgNearest;
    private List lstGPS;
    private StringItem siOverviewLattitude;
    private StringItem siOverviewLongitude;
    private List lstFavourites;
    private Command cmdFavourite;
    private Command cmdDeleteAll;
    private Command cmdSelect;
    private Form frmFavourite;
    private StringItem siFavouriteLattitude;
    private StringItem siFavouriteLongitude;
    private StringItem siDescription;
    private Command cmdAddActual;
    private Command cmdAddGiven;
    private Command cmdDelete;
    private Form frmAddGiven;
    private TextField tfGivenLattitude;
    private TextField tfGivenLongitude;
    private StringItem stringItem4;
    private TextField tfGivenName;
    private TextField tfGivenDescription;
    private StringItem siDonate;
    private Command cmdEdit;
    private Image imgTravelbug;
    private Form frmTrackingNumber;
    private TextField tfTrackingNumber;
    private StringItem stringItem7;
    private Form frmTrackable;
    private StringItem siOrigin;
    private StringItem siLastLocation;
    private StringItem siReleased;
    private StringItem siOwner;
    private StringItem siGoal;
    private StringItem siAbout;
    private Font fntSmall;
    private Form frmMultiSolver;
    private StringItem siLattitudePattern;
    private StringItem siLongitudePattern;
    private StringItem siLetters;
    private Command cmdAddPattern;
    private Image imgMultiSolver;
    private Form frmEditPattern;
    private TextField tfEditPatternLattitude;
    private TextField tfEditPatternLongitude;
    private StringItem stringItem8;
    private Form frmAddLetter;
    private Command cmdAddLetter;
    private TextField tfLetter;
    private TextField tfValue;
    private StringItem stringItem9;
    private Command cmdCompute;
    private Form frmResult;
    private TextField tfResultLattitude;
    private TextField tfResultLongitude;
    private TextField tfResultName;
    private TextField tfResultDescription;
    private Gauge gaLoading;
    private TextField tfBackLight;
    private StringItem stringItem10;
    private Command cmdYes;
    private Command cmdNo;
    private Image imgBluetooth;
    private List lstPatterns;
    private Command cmdPatterns;
    private Command cmdEditPattern;
    private TextField tfPatternName;
    private StringItem siAfterReplacement;
    private Command cmdDownloadPatterns;
    private StringItem siAverageLongitude;
    private TextBox tbError;
    private Font fntSmallBold;
    private Command cmdBegin;
    private Command cmdEnd;
    private StringItem siContent;
    private StringItem siEnd;
    private StringItem siBegin;
    private Command cmdFavourites;
    private Command cmdMultiSolver;
    private Command cmdRefresh;
    private Image imgDecypher;
    private TextBox tbDecypher;
    private Command cmdDecypher;
    private Command cmdMap;
    private Form frmConnectionHelp;
    private StringItem stringItem12;
    private Image imgWaypoint;
    private Form frmDebug1;
    private StringItem siPart;
    private StringItem siDebug1;
    private Command cmdMapyCz;
    private Image imgPdaGps;
    private Image imgOther;
    private ChoiceGroup cgGivenFormat;
    private Form frmGpsSignalHelp;
    private StringItem siSouradnice;
    private StringItem siRychlost;
    private StringItem siVyska;
    private StringItem siPresnost;
    private StringItem siPravdepodobnost;
    private StringItem siSat;
    private StringItem siNalezeno;
    private StringItem siNalezenoOver;
    private Form frmNalezeno;
    private StringItem siNazevKese;
    private DateField dfNalezeno;
    private Command cmdNastavit;
    private Command cmdAddFieldNotes;
    private StringItem siNalezeno1;
    private StringItem siSestaveni;
    private TextBox tbPoznamka;
    private Command cmdPoznamka;
    private StringItem siPoznamka;
    private Command cmdAdd;
    private StringItem siPoznamkaOver;
    private Font fntLargeBold;
    private List lstFieldNotes;
    private Image imgFieldNotes;
    private Form frmFieldNote;
    private StringItem siFNGcCode;
    private TextField tfFNGcCode;
    private ChoiceGroup cgFNType;
    private DateField dtFNDate;
    private TextField tfFNText;
    private Command cmdPostFieldNotes;
    private ChoiceGroup cgFieldNotes;
    private Command cmdSetFound;
    private Command cmdImportGPX;
    private Gauge gaLoadingIndicator;
    private Command cmdMemoryInfo;
    private Form frmMemoryInfo;
    private StringItem siHeapSize;
    private StringItem siRMSFavourities;
    private StringItem siRMSHint;
    private StringItem siRMSFieldNotes;
    private StringItem siRMSListing;
    private Command cmdDownloadAll;
    private ChoiceGroup cgInternalGPSType;//GEN-END:MVDFields
    private Navigation cvsNavigation;
    private Map cvsMap;
    //Zephy 21.11.07 gpsstatus+\
    private Signal cvsSignal;
    //Zephy 21.11.07 gpsstatus+/
    private OpenFileBrowser openFileBrowser = null;
    private GPXImport gpxImportForm = null;
//GEN-LINE:MVDMethods
    
    
    
    /** Called by the system to indicate that a command has been invoked on a particular displayable.//GEN-BEGIN:MVDCABegin
     * @param command the Command that ws invoked
     * @param displayable the Displayable on which the command was invoked
     */
    public void commandAction(Command command, Displayable displayable) {//GEN-END:MVDCABegin
        // Insert global pre-action code here
        if (displayable == lstDevices) {//GEN-BEGIN:MVDCABody
            if (command == cmdExit) {//GEN-END:MVDCABody
                // Insert pre-action code here
                exitMIDlet();//GEN-LINE:MVDCAAction17
                // Insert post-action code here
            } else if (command == lstDevices.SELECT_COMMAND) {
                getDisplay().setCurrent(get_frmConnecting());
                bluetooth.searchForServices();
            } else if (command == cmdBack) {//GEN-LINE:MVDCACase17
                // Insert pre-action code here
                getDisplay().setCurrent(get_lstMode());//GEN-LINE:MVDCAAction43
                // Insert post-action code here
                bluetooth.devices.removeAllElements();
                lstDevices.deleteAll();
                
            } else if (command == lstDevices.SELECT_COMMAND) {//GEN-BEGIN:MVDCACase43
                switch (get_lstDevices().getSelectedIndex()) {
                    case 0://GEN-END:MVDCACase43
                        // Insert pre-action code here
                        // Do nothing//GEN-LINE:MVDCAAction198
                        // Insert post-action code here
                        break;//GEN-BEGIN:MVDCACase198
                }
            }
        } else if (displayable == lstMenu) {
            if (command == lstMenu.SELECT_COMMAND) {
                switch (get_lstMenu().getSelectedIndex()) {
                    case 0://GEN-END:MVDCACase198
                        fromFavourites = false;
                        fromTrackables = false;
                        fromPreview = false;
                        fromFieldNotes = false;
                        getDisplay().setCurrent(get_lstSearch());//GEN-LINE:MVDCAAction37
                        
                        break;//GEN-BEGIN:MVDCACase37
                    case 5://GEN-END:MVDCACase37
                        // Insert pre-action code here
                        getDisplay().setCurrent(get_frmAbout());//GEN-LINE:MVDCAAction41
                        // Insert post-action code here
                        siVerze.setText(getAppProperty("MIDlet-Version")+"\n");
                        get_siSestaveni().setText(getAppProperty("Build-Vendor")+"-"+getAppProperty("Build-Version")+"\n");
                        if (settings.vip)
                            siDonate.setText("Děkuji moc za Váš příspěvek na vývoj aplikace! (č.účtu autora je 51-5385890237/0100)");
                        break;//GEN-BEGIN:MVDCACase41
                    case 6://GEN-END:MVDCACase41
                        // Insert pre-action code here
                        exitMIDlet();//GEN-LINE:MVDCAAction115
                        // Insert post-action code here
                        break;//GEN-BEGIN:MVDCACase115
                    case 3://GEN-END:MVDCACase115
                        // Insert pre-action code here
                        getDisplay().setCurrent(get_frmSettings());//GEN-LINE:MVDCAAction139
                        // Insert post-action code here
                        settings.set();
                        break;//GEN-BEGIN:MVDCACase139
                    case 1://GEN-END:MVDCACase139
                        // Insert pre-action code here
                        fromFavourites = true;
                        fromPreview = false;
                        fromFieldNotes = false;
                        nearest = false;
                        nearestFromWaypoint = false;
                        navigateToPoint = false;
                        keyword = false;
                        fromTrackables = false;
                        /*
getDisplay ().setCurrent (get_lstFavourites());//GEN-LINE:MVDCAAction214
                        // Insert post-action code here
                        */
                        favourites.viewAll();
                        break;//GEN-BEGIN:MVDCACase214
                    case 2://GEN-END:MVDCACase214
                        // Insert pre-action code here
                        getDisplay().setCurrent(get_lstGPS());//GEN-LINE:MVDCAAction236
                        // Insert post-action code here
                        break;//GEN-BEGIN:MVDCACase236
                    case 4://GEN-END:MVDCACase236
                        // Insert pre-action code here
                        fromFavourites = false;
                        fromFieldNotes = true;
                        fromPreview = false;
                        
                        get_lstFieldNotes().deleteAll();
                        FieldNotesItem[] items = FieldNotes.getInstance().getAll();
                        for(int i=0; i < items.length; i++)
                            get_lstFieldNotes().append(items[i].toString(!settings.iconsInFieldNotes, settings.nameInFieldNotesFirst), (settings.iconsInFieldNotes)? iconLoader.loadIcon(FieldNotes.getTypeIconName(items[i].getType()), false) : null);
                        //for(int i=0; i < get_lstFieldNotes().size(); i++)
                        //    get_lstFieldNotes().setFont(i, get_fntSmall());
                        getDisplay().setCurrent(get_lstFieldNotes());//GEN-LINE:MVDCAAction548
                        // Insert post-action code here
                        break;//GEN-BEGIN:MVDCACase548
                }
            }
        } else if (displayable == frmAveraging) {
            if (command == cmdBack) {//GEN-END:MVDCACase548
                // Insert pre-action code here
                getDisplay().setCurrent(get_lstGPS());//GEN-LINE:MVDCAAction22
                // Insert post-action code here
                gps.stop();
            } else if (command == cmdResume) {//GEN-LINE:MVDCACase22
                // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction26
                // Insert post-action code here
                gps.start(gps.AVERAGING_RESUME);
                get_frmAveraging().removeCommand(get_cmdResume());
                get_frmAveraging().addCommand(get_cmdPause());
            } else if (command == cmdPause) {//GEN-LINE:MVDCACase26
                // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction24
                // Insert post-action code here
                gps.stop();
                get_frmAveraging().removeCommand(get_cmdPause());
                get_frmAveraging().addCommand(get_cmdResume());
            } else if (command == cmdFavourite) {//GEN-LINE:MVDCACase24
                // Insert pre-action code here
                gps.stop();
                favourites.editId = -1;
                favourites.addEdit("Výsledek průměrování","",siAverageLattitude.getText(),siAverageLongitude.getText(),"average",get_lstFavourites(), false, "NE", "", true, true, true);
                // Do nothing//GEN-LINE:MVDCAAction391
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase391
        } else if (displayable == lstMode) {
            if (command == cmdExit) {//GEN-END:MVDCACase391
                // Insert pre-action code here
                exitMIDlet();//GEN-LINE:MVDCAAction33
                // Insert post-action code here
            } else if (command == lstMode.SELECT_COMMAND) {//GEN-BEGIN:MVDCACase33
                switch (get_lstMode().getSelectedIndex()) {
                    case 0://GEN-END:MVDCACase33
                        // Insert pre-action code here
                        modeGPS = true;
                        gpsGate = false;
                        get_frmConnecting().deleteAll();
                        getDisplay().setCurrent(get_frmConnecting());
                        if (settings.lastDevice.equals("")) //jeste jsme se nikdy nepripojili
                        {
                            // Do nothing//GEN-LINE:MVDCAAction30
                            searchBluetooth();
                        } else {
                            bluetooth = new Bluetooth(this, http, settings, favourites, true);
                            if (bluetooth.isOn()) {
                                get_frmConnecting().append("Připojuji k poslednímu zařízení...");
                                getDisplay().setCurrent(get_frmConnecting());
                                gpsParser = new GpsParser(this, http, settings, favourites, bluetooth, settings.lastDevice, GpsParser.BLUETOOTH);
                                bluetooth.setReference(gpsParser);
                                gpsParser.open();
                            }
                        }
                        break;//GEN-BEGIN:MVDCACase30
                    case 4://GEN-END:MVDCACase30
                        // Insert pre-action code here
                        
                        getDisplay().setCurrent(get_lstMenu());//GEN-LINE:MVDCAAction32
                        
                        modeGPS = false;
                        break;//GEN-BEGIN:MVDCACase32
                    case 2://GEN-END:MVDCACase32
                        // Insert pre-action code here
                        modeGPS = true;
                        gpsGate = true;
                        gpsParser = new GpsParser(this, http, settings, favourites, bluetooth, "http://127.0.0.1:20175", GpsParser.GPS_GATE);
                        gpsParser.open();
                        // Do nothing//GEN-LINE:MVDCAAction372
                        // Insert post-action code here
                        break;//GEN-BEGIN:MVDCACase372
                    case 1://GEN-END:MVDCACase372
                        // Insert pre-action code here
                        modeGPS = true;
                        gpsGate = false;
                        gpsParser = new GpsParser(this, http, settings, favourites, bluetooth, "", GpsParser.INTERNAL);
                        // Do nothing//GEN-LINE:MVDCAAction422
                        // Insert post-action code here
                        break;//GEN-BEGIN:MVDCACase422
                    case 3://GEN-END:MVDCACase422
                        // Insert pre-action code here
                        // Do nothing//GEN-LINE:MVDCAAction541
                        // Insert post-action code here
                        modeGPS = true;
                        gpsGate = false;
                        get_frmConnecting().append("Připojuji se k Sony Ericssson HGE-100...");
                        getDisplay().setCurrent(get_frmConnecting());
                        gpsParser = new GpsParser(this, http, settings, favourites, bluetooth, "comm:AT5;baudrate=9600", GpsParser.GPS_HGE_100);
                        gpsParser.open();
                        break;//GEN-BEGIN:MVDCACase541
                }
            } else if (command == cmdHint) {//GEN-END:MVDCACase541
                // Insert pre-action code here
                getDisplay().setCurrent(get_frmConnectionHelp());//GEN-LINE:MVDCAAction423
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase423
        } else if (displayable == frmCoordinates) {
            if (command == cmdBack) {//GEN-END:MVDCACase423
                // Insert pre-action code here
                if (navigateToPoint) {
                    getDisplay().setCurrent(get_lstGPS());
                } else {
                    getDisplay().setCurrent(get_lstSearch());//GEN-LINE:MVDCAAction69
                }
            } else if (command == cmdSend) {//GEN-LINE:MVDCACase69
                // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction68
                // Insert post-action code here
                if (navigateToPoint) {
                    if (gps.convertLattitude(get_tfLattitude().getString())==0 || gps.convertLongitude(get_tfLongitude().getString())==0) {
                        showAlert("Špatný formát souřadnic",AlertType.WARNING,get_frmCoordinates());
                    } else {
                        settings.saveCoordinates(get_tfLattitude().getString(), get_tfLongitude().getString());
                        getDisplay().setCurrent(get_cvsNavigation());
                        gps.setNavigationTarget(get_tfLattitude().getString(), get_tfLongitude().getString(),"Zadaný bod");
                        gps.start(Gps.NAVIGATION);
                        gps.setPreviousScreen(frmCoordinates);
                    }
                    
                } else {
                    http.start(Http.NEAREST_CACHES, false);
                }
            }//GEN-BEGIN:MVDCACase68
        } else if (displayable == lstNearestCaches) {
            if (command == cmdBack) {//GEN-END:MVDCACase68
                // Insert pre-action code here
                if (nearestFromWaypoint && !nearest && !keyword) {
                    getDisplay().setCurrent(get_frmOverview());
                } else if (!nearestFromFavourite && !nearest && keyword) {
                    getDisplay().setCurrent(get_frmKeyword());
                } else if (nearestFromFavourite) {
                    getDisplay().setCurrent(get_frmFavourite());
                } else {
                    if (modeGPS) {
                        getDisplay().setCurrent(get_lstSearch());
                    } else {
                        getDisplay().setCurrent(get_frmCoordinates());//GEN-LINE:MVDCAAction73
                    }
                }
                // Insert post-action code here
            } else if (command == lstNearestCaches.SELECT_COMMAND) {
                http.waypoint = http.waypoints[lstNearestCaches.getSelectedIndex()];
                http.waypointCacheName = lstNearestCaches.getString(lstNearestCaches.getSelectedIndex());
                http.start(Http.OVERVIEW, false);
            } else if (command == lstNearestCaches.SELECT_COMMAND) {//GEN-BEGIN:MVDCACase73
                switch (get_lstNearestCaches().getSelectedIndex()) {
                    case 0://GEN-END:MVDCACase73
                        // Insert pre-action code here
                        // Do nothing//GEN-LINE:MVDCAAction314
                        // Insert post-action code here
                        break;//GEN-BEGIN:MVDCACase314
                }
            } else if (command == cmdDownloadAll) {//GEN-END:MVDCACase314
                // Insert pre-action code here
                http.start(Http.DOWNLOAD_ALL_CACHES, false);
                // Do nothing//GEN-LINE:MVDCAAction612
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase612
        } else if (displayable == frmWaypoint) {
            if (command == cmdBack) {//GEN-END:MVDCACase612
                // Insert pre-action code here
                getDisplay().setCurrent(get_lstSearch());//GEN-LINE:MVDCAAction87
                // Insert post-action code here
            } else if (command == cmdSend) {//GEN-LINE:MVDCACase87
                // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction88
                // Insert post-action code here
                http.waypoint = tfWaypoint.getString();
                http.waypointCacheName = http.waypoint;
                http.start(Http.OVERVIEW, false);
            }//GEN-BEGIN:MVDCACase88
        } else if (displayable == frmOverview) {
            if (command == cmdBack) {//GEN-END:MVDCACase88
                // Insert pre-action code here
                fromPreview = false;
                if (nearest || keyword) {
                    getDisplay().setCurrent(get_lstNearestCaches());
                } else if (fromFavourites) {
                    favourites.viewAll();
                    //getDisplay().setCurrent(get_lstFavourites());
                } else {
                    getDisplay().setCurrent(get_frmWaypoint());//GEN-LINE:MVDCAAction91
                }// Insert post-action code here
            } else if (command == cmdWaypoints) {//GEN-LINE:MVDCACase91
                // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction109
                // Insert post-action code here
                http.waypointCacheName = get_siName().getText();
                http.start(Http.WAYPOINTS, false);
            } else if (command == cmdInfo) {//GEN-LINE:MVDCACase109
                // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction105
                // Insert post-action code here
                http.waypointCacheName = get_siName().getText();
                http.start(Http.DETAIL, false);
            } else if (command == cmdHint) {//GEN-LINE:MVDCACase105
                // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction103
                // Insert post-action code here
                http.waypointCacheName = get_siName().getText();
                http.start(Http.HINT, false);
            } else if (command == cmdLogs) {//GEN-LINE:MVDCACase103
                // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction107
                // Insert post-action code here
                http.waypointCacheName = get_siName().getText();
                http.start(Http.LOGS, false);
            } else if (command == cmdNavigate) {//GEN-LINE:MVDCACase107
                // Insert pre-action code here
                if (modeGPS) {
                    // Do nothing//GEN-LINE:MVDCAAction168
                    getDisplay().setCurrent(get_cvsNavigation());
                    gps.setNavigationTarget(get_siOverviewLattitude().getText(), get_siOverviewLongitude().getText(), get_siName().getText());
                    gps.start(Gps.NAVIGATION);
                    gps.setPreviousScreen(frmOverview);
                } else {
                    showAlert("Tato funkce je přístupná jenom v režimu GPS",AlertType.WARNING,get_lstMode());
                }
            } else if (command == cmdNext) {//GEN-LINE:MVDCACase168
                // Insert pre-action code here
                nearestFromWaypoint = true;
                nearestFromFavourite = false;
                http.start(Http.NEXT_NEAREST, false);
                // Do nothing//GEN-LINE:MVDCAAction173
                // Insert post-action code here
            } else if (command == cmdFavourite) {//GEN-LINE:MVDCACase173
                // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction255
                favourites.editId = -1;
                //Zephy oprava 22.12.07 - posledni parametr na false +\
                favourites.addEdit(siName.getText(),
                        http.favouriteResponse,
                        siOverviewLattitude.getText(), siOverviewLongitude.getText(),
                        http.typeNumber,get_frmOverview(), false, "NE", "", false, true, true);
                //Zephy oprava 22.12.07 +/
            } else if (command == cmdDownloadPatterns) {//GEN-LINE:MVDCACase255
                // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction388
                // Insert post-action code here
                http.waypointCacheName = get_siName().getText();
                http.start(Http.PATTERNS, false);
            } else if (command == cmdRefresh) {//GEN-LINE:MVDCACase388
                // Insert pre-action code here
                http.waypoint = get_siWaypoint().getText();
                http.waypointCacheName = get_siName().getText();
                http.start(Http.OVERVIEW, true);
                // Do nothing//GEN-LINE:MVDCAAction410
                // Insert post-action code here
            } else if (command == cmdAddFieldNotes) {//GEN-LINE:MVDCACase410
                // Insert pre-action code here
                fieldNoteItem = FieldNotes.getInstance().create();
                fieldNoteItem.setGcCode(get_siWaypoint().getText());
                fieldNoteItem.setName(get_siName().getText());

                get_siFNGcCode().setText(fieldNoteItem.getGcCode());
                get_tfFNGcCode().setString(fieldNoteItem.getGcCode());
                get_cgFNType().setSelectedIndex(fieldNoteItem.getType(), true);
                get_dtFNDate().setDate(fieldNoteItem.getDate());
                get_tfFNText().setString(fieldNoteItem.getText());

                get_frmFieldNote().deleteAll();
                get_frmFieldNote().append(get_siFNGcCode());
                get_frmFieldNote().append(get_cgFNType());
                get_frmFieldNote().append(get_dtFNDate());
                get_frmFieldNote().append(get_tfFNText());
                
                get_frmFieldNote().setTitle("FN: " + fieldNoteItem.getName());

                getDisplay().setCurrent(get_frmFieldNote());//GEN-LINE:MVDCAAction507
                // Insert post-action code here
            } else if (command == cmdPoznamka) {//GEN-LINE:MVDCACase507
                // Insert pre-action code here
                get_tbPoznamka().setTitle("Poznámka pro "+get_siName().getText());
                get_tbPoznamka().setString(favourites.getPoznamka(favourites.id));
                getDisplay().setCurrent(get_tbPoznamka());//GEN-LINE:MVDCAAction589
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase589
        } else if (displayable == frmInfo) {
            if (command == cmdBack) {//GEN-END:MVDCACase589
                // Insert pre-action code here
                getDisplay().setCurrent(get_frmOverview());//GEN-LINE:MVDCAAction111
                // Insert post-action code here
            } else if (command == cmdEnd) {//GEN-LINE:MVDCACase111
                // Insert pre-action code here
                getDisplay().setCurrentItem(get_siEnd());
                // Do nothing//GEN-LINE:MVDCAAction399
                // Insert post-action code here
            } else if (command == cmdBegin) {//GEN-LINE:MVDCACase399
                // Insert pre-action code here
                getDisplay().setCurrentItem(get_siBegin());
                // Do nothing//GEN-LINE:MVDCAAction398
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase398
        } else if (displayable == frmHint) {
            if (command == cmdBack) {//GEN-END:MVDCACase398
                // Insert pre-action code here
                getDisplay().setCurrent(get_frmOverview());//GEN-LINE:MVDCAAction113
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase113
        } else if (displayable == frmWaypoints) {
            if (command == cmdBack) {//GEN-END:MVDCACase113
                // Insert pre-action code here
                getDisplay().setCurrent(get_frmOverview());//GEN-LINE:MVDCAAction117
                // Insert post-action code hereC
            } else if (command == cmdFavourite) {//GEN-LINE:MVDCACase117
                // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction260
                favourites.addWaypoints(http.response);
            }//GEN-BEGIN:MVDCACase260
        } else if (displayable == frmLogs) {
            if (command == cmdBack) {//GEN-END:MVDCACase260
                // Insert pre-action code here
                getDisplay().setCurrent(get_frmOverview());//GEN-LINE:MVDCAAction119
                // Insert post-action code here
            } else {
                http.start(Http.ALL_LOGS, false);
            }//GEN-BEGIN:MVDCACase119
        } else if (displayable == frmAbout) {
            if (command == cmdBack) {//GEN-END:MVDCACase119
                // Insert pre-action code here
                getDisplay().setCurrent(get_lstMenu());//GEN-LINE:MVDCAAction129
                // Insert post-action code here
            } else if (command == cmdMemoryInfo) {//GEN-LINE:MVDCACase129
                // Insert pre-action code here
                fillMemoryInfoForm();
                getDisplay().setCurrent(get_frmMemoryInfo());//GEN-LINE:MVDCAAction602
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase602
        } else if (displayable == frmLoading) {
            if (command == cmdStop) {//GEN-END:MVDCACase602
                // Insert pre-action code here
                http.stop();
                getDisplay().setCurrent(get_lstMenu());//GEN-LINE:MVDCAAction136
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase136
        } else if (displayable == frmSettings) {
            if (command == cmdSave) {//GEN-END:MVDCACase136
                // Insert pre-action code here
                getDisplay().setCurrent(get_lstMenu());//GEN-LINE:MVDCAAction144
                // Insert post-action code here
                settings.save();
            } else if (command == cmdBack) {//GEN-LINE:MVDCACase144
                // Insert pre-action code here
                getDisplay().setCurrent(get_lstMenu());//GEN-LINE:MVDCAAction478
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase478
        } else if (displayable == frmConnecting) {
            if (command == cmdBack) {//GEN-END:MVDCACase478
                // Insert pre-action code here
                getDisplay().setCurrent(get_lstMode());//GEN-LINE:MVDCAAction153
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase153
        } else if (displayable == frmAllLogs) {
            if (command == cmdBack) {//GEN-END:MVDCACase153
                // Insert pre-action code here
                getDisplay().setCurrent(get_frmLogs());//GEN-LINE:MVDCAAction178
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase178
        } else if (displayable == ssAdvertisement) {
            if (command == ssAdvertisement.DISMISS_COMMAND) {//GEN-END:MVDCACase178
                // Insert pre-action code here
                getDisplay().setCurrent(get_lstMode());//GEN-LINE:MVDCAAction185
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase185
        } else if (displayable == frmDebug) {
            if (command == cmdMenu) {//GEN-END:MVDCACase185
                // Insert pre-action code here
                getDisplay().setCurrent(get_lstMenu());//GEN-LINE:MVDCAAction191
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase191
        } else if (displayable == lstKeyword) {
            if (command == cmdBack) {//GEN-END:MVDCACase191
                // Insert pre-action code here
                getDisplay().setCurrent(get_frmKeyword());//GEN-LINE:MVDCAAction225
                // Insert post-action code here
            } else if (command == lstKeyword.SELECT_COMMAND) {
                http.waypoint = http.waypoints[lstKeyword.getSelectedIndex()];
                http.waypointCacheName = lstKeyword.getString(lstKeyword.getSelectedIndex());
                http.start(Http.OVERVIEW, false);
            } else if (command == lstKeyword.SELECT_COMMAND) {//GEN-BEGIN:MVDCACase225
                switch (get_lstKeyword().getSelectedIndex()) {
                    case 0://GEN-END:MVDCACase225
                        // Insert pre-action code here
                        // Do nothing//GEN-LINE:MVDCAAction316
                        // Insert post-action code here
                        break;//GEN-BEGIN:MVDCACase316
                }
            }
        } else if (displayable == frmKeyword) {
            if (command == cmdBack) {//GEN-END:MVDCACase316
                // Insert pre-action code here
                getDisplay().setCurrent(get_lstSearch());//GEN-LINE:MVDCAAction221
                // Insert post-action code here
            } else if (command == cmdSend) {//GEN-LINE:MVDCACase221
                // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction222
                // Insert post-action code here
                http.start(Http.KEYWORD, false);
            }//GEN-BEGIN:MVDCACase222
        } else if (displayable == lstSearch) {
            if (command == lstSearch.SELECT_COMMAND) {
                switch (get_lstSearch().getSelectedIndex()) {
                    case 0://GEN-END:MVDCACase222
                        // nejblizsi kese
                        nearest = true;
                        nearestFromWaypoint = false;
                        nearestFromFavourite = false;
                        navigateToPoint = false;
                        keyword = false;
                        if (!modeGPS) {
                            get_frmCoordinates().setTitle("Zadejte souřadnice:");
                            get_tfLattitude().setString(settings.lastLattitude);
                            get_tfLongitude().setString(settings.lastLongitude);
                        } else {
                            get_frmCoordinates().setTitle("Zadejte souřadnice:");
                            get_tfLattitude().setString("");
                            get_tfLongitude().setString("");
                            gps.start(Gps.CURRENT_POSITION);
                        }
                        getDisplay().setCurrent(get_frmCoordinates());
                        
                        // Do nothing//GEN-LINE:MVDCAAction229
                        // Insert post-action code here
                        break;//GEN-BEGIN:MVDCACase229
                    case 1://GEN-END:MVDCACase229
                        // hledani podle waypointu
                        nearest = false;
                        navigateToPoint = false;
                        keyword = false;
                        getDisplay().setCurrent(get_frmWaypoint());
                        
                        // Do nothing//GEN-LINE:MVDCAAction231
                        // Insert post-action code here
                        break;//GEN-BEGIN:MVDCACase231
                    case 2://GEN-END:MVDCACase231
                        // hledani podle klicoveho slova
                        nearest = false;
                        navigateToPoint = false;
                        keyword = true;
                        getDisplay().setCurrent(get_frmKeyword());
                        // Do nothing//GEN-LINE:MVDCAAction233
                        // Insert post-action code here
                        break;//GEN-BEGIN:MVDCACase233
                    case 3://GEN-END:MVDCACase233
                        // Insert pre-action code here
                        // nejblizsi kese
                        nearest = true;
                        nearestFromWaypoint = false;
                        nearestFromFavourite = false;
                        navigateToPoint = false;
                        keyword = false;
                        get_frmCoordinates().setTitle("Zadejte souřadnice:");
                        get_tfLattitude().setString(settings.lastLattitude);
                        get_tfLongitude().setString(settings.lastLongitude);

                        getDisplay().setCurrent(get_frmCoordinates());
                        // Do nothing//GEN-LINE:MVDCAAction539
                        // Insert post-action code here
                        break;//GEN-BEGIN:MVDCACase539
                }
            } else if (command == cmdBack) {//GEN-END:MVDCACase539
                // Insert pre-action code here
                getDisplay().setCurrent(get_lstMenu());//GEN-LINE:MVDCAAction243
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase243
        } else if (displayable == lstGPS) {
            if (command == lstGPS.SELECT_COMMAND) {
                switch (get_lstGPS().getSelectedIndex()) {
                    case 4://GEN-END:MVDCACase243
                        // naviguj
                        if (modeGPS) {
                            // Do nothing
                            // Insert post-action code here
                            navigateToPoint = true;
                            navigateToFavourite = false;
                            getDisplay().setCurrent(get_frmCoordinates());
                            get_tfLattitude().setString(settings.lastLattitude);
                            get_tfLongitude().setString(settings.lastLongitude);
                        } else {
                            showAlert("Tato funkce je přístupná jenom v režimu GPS",AlertType.WARNING,get_lstMode());
                        }
                        // Do nothing//GEN-LINE:MVDCAAction240
                        // Insert post-action code here
                        break;//GEN-BEGIN:MVDCACase240
                    case 3://GEN-END:MVDCACase240
                        // prumerovani
                        if (modeGPS) {
                            getDisplay().setCurrent(get_frmAveraging());
                            // Insert post-action code here
                            gps.start(Gps.AVERAGING);
                        } else {
                            showAlert("Tato funkce je přístupná jenom v režimu GPS",AlertType.WARNING,get_lstMode());
                        }
                        // Do nothing//GEN-LINE:MVDCAAction242
                        // Insert post-action code here
                        break;//GEN-BEGIN:MVDCACase242
                    case 0://GEN-END:MVDCACase242
                        // Insert pre-action code here
                        fromMultiSolver = true;
                        multiSolver.viewAll();
                        getDisplay().setCurrent(get_frmMultiSolver());//GEN-LINE:MVDCAAction331
                        // Insert post-action code here
                        break;//GEN-BEGIN:MVDCACase331
                    case 2://GEN-END:MVDCACase331
                        // Insert pre-action code here
                        getDisplay().setCurrent(get_tbDecypher());//GEN-LINE:MVDCAAction412
                        // Insert post-action code here
                        break;//GEN-BEGIN:MVDCACase412
                    case 1://GEN-END:MVDCACase412
                        // Insert pre-action code here
                        fromTrackables = true;
                        getDisplay().setCurrent(get_frmTrackingNumber());//GEN-LINE:MVDCAAction433
                        // Insert post-action code here
                        break;//GEN-BEGIN:MVDCACase433
                    case 5://GEN-END:MVDCACase433
                        // Insert pre-action code here
                        //Zephy 21.11.07 gpsstatus+\
                        // gps signal
                        if (modeGPS) {
                            get_tfLattitude().setString("");
                            get_tfLongitude().setString("");
                            getDisplay().setCurrent(get_cvsSignal());
                            gps.start(Gps.GPS_SIGNAL);
                            gps.setPreviousScreen(get_lstGPS());
                        } else {
                            //TMP+\
                            //getDisplay().setCurrent(get_cvsSignal());
                            //gps.setPreviousScreen(lstGPS);
                            //TMP-/
                            
                            showAlert("Tato funkce je přístupná jenom v režimu GPS",AlertType.WARNING,get_lstMode());
                        }
                        //Zephy 21.11.07 gpsstatus+/
                        // Do nothing//GEN-LINE:MVDCAAction469
                        // Insert post-action code here
                        break;//GEN-BEGIN:MVDCACase469
                }
            } else if (command == cmdBack) {//GEN-END:MVDCACase469
                // Insert pre-action code here
                getDisplay().setCurrent(get_lstMenu());//GEN-LINE:MVDCAAction244
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase244
        } else if (displayable == lstFavourites) {
            if (command == cmdBack) {//GEN-END:MVDCACase244
                // Insert pre-action code here
                getDisplay().setCurrent(get_lstMenu());//GEN-LINE:MVDCAAction253
                // Insert post-action code here
            } else if (command == cmdDeleteAll) {//GEN-LINE:MVDCACase253
                // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction257
                ConfirmDialog dialog = new ConfirmDialog(getDisplay(), "Smazat všechny položky?", "Opravdu chcete smazat všechny položky?");
                dialog.setActionNoDisplayable(lstFavourites);
                dialog.setActionYes(new Runnable() {
                    public void run() {
                        favourites.deleteAll();
                    }
                });
                dialog.show();
            } else if (command == cmdSelect) {//GEN-LINE:MVDCACase257
                // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction259
                // Insert post-action code here
                int selected = firstCheckedFavourite();
                if (selected != -1)
                    favourites.view(selected, true);
            } else if (command == cmdAddActual) {//GEN-LINE:MVDCACase259
                // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction269
                // Insert post-action code here
                fromMultiSolver = false;
                if (modeGPS) {
                    favourites.editId = -1;
                    get_frmAddGiven().setTitle("Získávám souřadnice...");
                    get_tfGivenName().setString("");
                    get_tfGivenDescription().setString("");
                    getDisplay().setCurrent(get_frmAddGiven());
                    gps.start(Gps.COORDINATES_FAVOURITES);
                } else {
                    showAlert("Tato funkce je přístupná jenom v režimu GPS",AlertType.WARNING,get_lstMode());
                }
            } else if (command == cmdDelete) {//GEN-LINE:MVDCACase269
                // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction272
                // Insert post-action code here
                ConfirmDialog dialog = new ConfirmDialog(getDisplay(), "Smazat položky?", "Opravdu chcete smazat zvolené položky?");
                dialog.setActionNoDisplayable(lstFavourites);
                dialog.setActionYes(new Runnable() {
                    public void run() {
                        favourites.delete();
                    }
                });
                dialog.show();
            } else if (command == cmdAddGiven) {//GEN-LINE:MVDCACase272
                // Insert pre-action code here
                fromMultiSolver = false;
                favourites.editId = -1;
                get_tfGivenLattitude().setString(settings.lastLattitude);
                get_tfGivenLongitude().setString(settings.lastLongitude);
                get_tfGivenName().setString("");
                get_tfGivenDescription().setString("");
                get_frmAddGiven().setTitle("Přidat bod");
                
                
                getDisplay().setCurrent(get_frmAddGiven());//GEN-LINE:MVDCAAction270
                // Insert post-action code here
            } else if (command == cmdEdit) {//GEN-LINE:MVDCACase270
                // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction285
                int selected = firstCheckedFavourite();
                if (selected != -1)
                    favourites.edit(selected);
            } else if (command == cmdNavigate) {//GEN-LINE:MVDCACase285
                // Insert pre-action code here
                if (modeGPS) {
                    int selected = firstCheckedFavourite();
                    if (selected != -1) {
                        navigateToFavourite = true;
                        navigateToPoint = false;
                        getDisplay().setCurrent(get_cvsNavigation());
                        favourites.view(selected, false);
                        gps.start(Gps.NAVIGATION);
                        gps.setPreviousScreen(get_lstFavourites());
                    }
                } else {
                    showAlert("Tato funkce je přístupná jenom v režimu GPS",AlertType.WARNING,get_lstMode());
                }
                // Do nothing//GEN-LINE:MVDCAAction390
                // Insert post-action code here
            } else if (command == cmdMultiSolver) {//GEN-LINE:MVDCACase390
                fromMultiSolver = true;
                multiSolver.viewAll();
                getDisplay().setCurrent(get_frmMultiSolver());
                // Do nothing//GEN-LINE:MVDCAAction408
                // Insert post-action code here
            } else if (command == cmdMap) {//GEN-LINE:MVDCACase408
                // Insert pre-action code here
                if (modeGPS) {
                    favourites.loadFavouritesToMap();
                    getDisplay().setCurrent(get_cvsMap());
                    gps.start(Gps.MAP);
                    gps.setPreviousScreen(get_lstFavourites());
                } else {
                    showAlert("Tato funkce je přístupná jenom v režimu GPS",AlertType.WARNING,get_lstMode());
                }
                // Do nothing//GEN-LINE:MVDCAAction420
                // Insert post-action code here
            } else if (command == cmdMapyCz) {//GEN-LINE:MVDCACase420
                // Insert pre-action code here
                int selected = firstCheckedFavourite();
                if (selected != -1) {
                    favourites.mapyCz(selected);
                }
                // Do nothing//GEN-LINE:MVDCAAction453
                // Insert post-action code here
            } else if (command == cmdAddFieldNotes) {//GEN-LINE:MVDCACase453
                // Insert pre-action code here
                int selected = firstCheckedFavourite();
                if (selected != -1) {
                    String[] parts = favourites.getCacheParts(selected);
                    
                    if (parts.length != 0) {
                        fieldNoteItem = FieldNotes.getInstance().create();
                        fieldNoteItem.setGcCode(parts[7]);
                        fieldNoteItem.setName(parts[0]);

                        get_siFNGcCode().setText(fieldNoteItem.getGcCode());
                        get_tfFNGcCode().setString(fieldNoteItem.getGcCode());
                        get_cgFNType().setSelectedIndex(fieldNoteItem.getType(), true);
                        get_dtFNDate().setDate(fieldNoteItem.getDate());
                        get_tfFNText().setString(fieldNoteItem.getText());

                        get_frmFieldNote().deleteAll();
                        get_frmFieldNote().append(get_siFNGcCode());
                        get_frmFieldNote().append(get_cgFNType());
                        get_frmFieldNote().append(get_dtFNDate());
                        get_frmFieldNote().append(get_tfFNText());
                        
                        get_frmFieldNote().setTitle("FN: " + fieldNoteItem.getName());

                        getDisplay().setCurrent(get_frmFieldNote());//GEN-LINE:MVDCAAction509
                    // Insert post-action code here
                    } else {
                        showAlert("Field note lze vytvořit pouze u keše. Pokud znáte GC kód, vytvořte field note v části Hlavní menu -> Field Notes -> Přidat.", AlertType.ERROR, null);
                    }
                }
            } else if (command == cmdPoznamka) {//GEN-LINE:MVDCACase509
                // Insert pre-action code here
                int selected = firstCheckedFavourite();
                if (selected != -1) {
                    get_tbPoznamka().setTitle("Poznámka pro "+favourites.getCacheName(selected));
                    favourites.id = selected;
                    get_tbPoznamka().setString(favourites.getPoznamka(favourites.id));
                    getDisplay().setCurrent(get_tbPoznamka());//GEN-LINE:MVDCAAction521
                // Insert post-action code here
                }
            } else if (command == cmdImportGPX) {//GEN-LINE:MVDCACase521
                // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction587
                // Insert post-action code here
                openFileBrowser = new OpenFileBrowser(getDisplay(), this);
                openFileBrowser.open(null);
            }//GEN-BEGIN:MVDCACase587
        } else if (displayable == frmFavourite) {
            if (command == cmdBack) {//GEN-END:MVDCACase587
                // Insert pre-action code here
                getDisplay().setCurrent(get_lstFavourites());//GEN-LINE:MVDCAAction262
                // Insert post-action code here
            } else if (command == cmdNavigate) {//GEN-LINE:MVDCACase262
                // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction263
                if (modeGPS) {
                    navigateToFavourite = true;
                    navigateToPoint = false;
                    getDisplay().setCurrent(get_cvsNavigation());
                    gps.setNavigationTarget(get_siFavouriteLattitude().getText(), get_siFavouriteLongitude().getText(), get_frmFavourite().getTitle());
                    gps.start(Gps.NAVIGATION);
                    gps.setPreviousScreen(frmFavourite);
                } else {
                    showAlert("Tato funkce je přístupná jenom v režimu GPS",AlertType.WARNING,get_lstMode());
                }
            } else if (command == cmdNext) {//GEN-LINE:MVDCACase263
                // Insert pre-action code here
                nearestFromWaypoint = false;
                nearestFromFavourite = true;
                http.start(Http.NEXT_NEAREST, false);
                // Do nothing//GEN-LINE:MVDCAAction360
                // Insert post-action code here
            } else if (command == cmdPoznamka) {//GEN-LINE:MVDCACase360
                // Insert pre-action code here
                get_tbPoznamka().setTitle("Poznámka pro "+favourites.getCacheName(favourites.id));
                get_tbPoznamka().setString(favourites.getPoznamka(favourites.id));
                getDisplay().setCurrent(get_tbPoznamka());//GEN-LINE:MVDCAAction524
                // Insert post-action code here
            } else if (command == cmdSetFound) {//GEN-LINE:MVDCACase524
                // Insert pre-action code here
                get_siNazevKese().setText(get_frmFavourite().getTitle());
                get_dfNalezeno().setDate(new Date());
                getDisplay().setCurrent(get_frmNalezeno());//GEN-LINE:MVDCAAction585
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase585
        } else if (displayable == frmAddGiven) {
            if (command == cmdBack) {//GEN-END:MVDCACase585
                // Insert pre-action code here
                favourites.viewAll();
                /*
getDisplay ().setCurrent (get_lstFavourites());//GEN-LINE:MVDCAAction280
                // Insert post-action code here
                 */
            } else if (command == cmdSave) {//GEN-LINE:MVDCACase280
                // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction279
                // Insert post-action code here
                favourites.addEdit(tfGivenName.getString(),tfGivenDescription.getString(),tfGivenLattitude.getString(),tfGivenLongitude.getString(),"waypoint",get_lstFavourites(), (cgGivenFormat.getSelectedIndex()==1), "NE", "", false, true, true);
                /*
                 *Zephy 19.11.07 REM - pri editaci bodu a zadani chybnych souradnic byla prebita hlaska o chybne zadanych souradnicich a vlezlo se zpet do seznamu. Toto bylo presunuto do Favourites.java
                 */
                favourites.viewAll();
            }//GEN-BEGIN:MVDCACase279
        } else if (displayable == frmTrackingNumber) {
            if (command == cmdBack) {//GEN-END:MVDCACase279
                // Insert pre-action code here
                getDisplay().setCurrent(get_lstGPS());//GEN-LINE:MVDCAAction292
                // Insert post-action code here
            } else if (command == cmdSend) {//GEN-LINE:MVDCACase292
                // Insert pre-action code here
                http.start(Http.TRACKABLE, false);
                // Do nothing//GEN-LINE:MVDCAAction294
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase294
        } else if (displayable == frmTrackable) {
            if (command == cmdBack) {//GEN-END:MVDCACase294
                // Insert pre-action code here
                getDisplay().setCurrent(get_frmTrackingNumber());//GEN-LINE:MVDCAAction296
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase296
        } else if (displayable == frmMultiSolver) {
            if (command == cmdDeleteAll) {//GEN-END:MVDCACase296
                // Insert pre-action code here
                multiSolver.deleteMultiSolver();
                // Do nothing//GEN-LINE:MVDCAAction327
                // Insert post-action code here
            } else if (command == cmdBack) {//GEN-LINE:MVDCACase327
                // Insert pre-action code here
                getDisplay().setCurrent(get_lstGPS());//GEN-LINE:MVDCAAction322
                // Insert post-action code here
            } else if (command == cmdAddLetter) {//GEN-LINE:MVDCACase322
                // Insert pre-action code here
                getDisplay().setCurrent(get_frmAddLetter());//GEN-LINE:MVDCAAction342
                // Insert post-action code here
            } else if (command == cmdCompute) {//GEN-LINE:MVDCACase342
                // Insert pre-action code here
                multiSolver.computeCoordinates();
                // Do nothing//GEN-LINE:MVDCAAction349
                // Insert post-action code here
            } else if (command == cmdPatterns) {//GEN-LINE:MVDCACase349
                // Insert pre-action code here
                patterns.viewAll();
                getDisplay().setCurrent(get_lstPatterns());//GEN-LINE:MVDCAAction380
                // Insert post-action code here
            } else if (command == cmdFavourites) {//GEN-LINE:MVDCACase380
                // Insert pre-action code here
                fromFavourites = true;
                nearest = false;
                nearestFromWaypoint = false;
                navigateToPoint = false;
                keyword = false;
                fromTrackables = false;
                favourites.viewAll();
                //getDisplay().setCurrent(get_lstFavourites());
                // Do nothing//GEN-LINE:MVDCAAction406
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase406
        } else if (displayable == frmEditPattern) {
            if (command == cmdSave) {//GEN-END:MVDCACase406
                // Insert pre-action code here
                if (get_frmEditPattern().getTitle().equals("Upravit vzorec"))
                    patterns.addEdit(true);
                else
                    patterns.addEdit(false);
                getDisplay().setCurrent(get_lstPatterns());//GEN-LINE:MVDCAAction338
                // Insert post-action code here
            } else if (command == cmdBack) {//GEN-LINE:MVDCACase338
                // Insert pre-action code here
                getDisplay().setCurrent(get_lstPatterns());//GEN-LINE:MVDCAAction337
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase337
        } else if (displayable == frmAddLetter) {
            if (command == cmdSave) {//GEN-END:MVDCACase337
                // Insert pre-action code here
                multiSolver.addEdit();
                getDisplay().setCurrent(get_frmMultiSolver());//GEN-LINE:MVDCAAction347
                // Insert post-action code here
            } else if (command == cmdBack) {//GEN-LINE:MVDCACase347
                // Insert pre-action code here
                getDisplay().setCurrent(get_frmMultiSolver());//GEN-LINE:MVDCAAction346
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase346
        } else if (displayable == frmResult) {
            if (command == cmdBack) {//GEN-END:MVDCACase346
                // Insert pre-action code here
                getDisplay().setCurrent(get_frmMultiSolver());//GEN-LINE:MVDCAAction351
                // Insert post-action code here
            } else if (command == cmdFavourite) {//GEN-LINE:MVDCACase351
                // Insert pre-action code here
                favourites.editId = -1;
                //Zephy oprava 22.12.07 - posledni parametr na false +\
                favourites.addEdit(tfResultName.getString(),tfResultDescription.getString(),tfResultLattitude.getString(),tfResultLongitude.getString(),"multisolver_waypoint",get_lstFavourites(), false, "NE", "", true, true, true);
                //Zephy oprava 22.12.07 - posledni parametr na false +/
                // Do nothing//GEN-LINE:MVDCAAction352
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase352
        } else if (displayable == lstPatterns) {
            if (command == cmdEditPattern) {//GEN-END:MVDCACase352
                // Insert pre-action code here
                get_frmEditPattern().setTitle("Upravit vzorec");
                patterns.view();
                getDisplay().setCurrent(get_frmEditPattern());//GEN-LINE:MVDCAAction382
                // Insert post-action code here
            } else if (command == cmdBack) {//GEN-LINE:MVDCACase382
                // Insert pre-action code here
                getDisplay().setCurrent(get_frmMultiSolver());//GEN-LINE:MVDCAAction377
                // Insert post-action code here
            } else if (command == cmdAddPattern) {//GEN-LINE:MVDCACase377
                // Insert pre-action code here
                get_frmEditPattern().setTitle("Přidat vzorec");
                getDisplay().setCurrent(get_frmEditPattern());//GEN-LINE:MVDCAAction378
                // Insert post-action code here
            } else if (command == lstPatterns.SELECT_COMMAND) {
                patterns.setActive();
                getDisplay().setCurrent(get_frmMultiSolver());
                
            } else if (command == lstPatterns.SELECT_COMMAND) {//GEN-BEGIN:MVDCACase378
                switch (get_lstPatterns().getSelectedIndex()) {
                    case 0://GEN-END:MVDCACase378
                        // Insert pre-action code here
                        // Do nothing//GEN-LINE:MVDCAAction384
                        // Insert post-action code here
                        
                        break;//GEN-BEGIN:MVDCACase384
                }
            } else if (command == cmdDelete) {//GEN-END:MVDCACase384
                // Insert pre-action code here
                ConfirmDialog dialog = new ConfirmDialog(getDisplay(), "Smazat položku?", "Opravdu chcete smazat tuto položku?");
                dialog.setActionNoDisplayable(lstPatterns);
                dialog.setActionYes(new Runnable() {
                    public void run() {
                        patterns.delete();
                        patterns.viewAll();
                    }
                });
                dialog.setActionYesDisplayable(lstPatterns);
                dialog.show();
                // Do nothing//GEN-LINE:MVDCAAction427
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase427
        } else if (displayable == tbError) {
            if (command == cmdMenu) {//GEN-END:MVDCACase427
                // Insert pre-action code here
                getDisplay().setCurrent(get_lstMenu());//GEN-LINE:MVDCAAction394
// Insert post-action code here
            }//GEN-BEGIN:MVDCACase394
        } else if (displayable == tbDecypher) {
            if (command == cmdBack) {//GEN-END:MVDCACase394
                // Insert pre-action code here
                getDisplay().setCurrent(get_lstGPS());//GEN-LINE:MVDCAAction418
// Insert post-action code here
            } else if (command == cmdDecypher) {//GEN-LINE:MVDCACase418
                // Insert pre-action code here
                get_tbDecypher().setString(Utils.decypherText(get_tbDecypher().getString()));
                // Do nothing//GEN-LINE:MVDCAAction417
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase417
        } else if (displayable == frmConnectionHelp) {
            if (command == cmdBack) {//GEN-END:MVDCACase417
                // Insert pre-action code here
                getDisplay().setCurrent(get_lstMode());//GEN-LINE:MVDCAAction426
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase426
        } else if (displayable == frmDebug1) {
            if (command == cmdBack) {//GEN-END:MVDCACase426
                // Insert pre-action code here
                getDisplay().setCurrent(get_lstMenu());//GEN-LINE:MVDCAAction444
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase444
        } else if (displayable == frmGpsSignalHelp) {
            if (command == cmdBack) {//GEN-END:MVDCACase444
                // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction489
                // Insert post-action code here
                //Zephy 10.12.07 +\ +plus pridan navic cely frmGpsSignalHelp
                getDisplay().setCurrent(get_cvsSignal());
                //Zephy 10.12.07 +/
            }//GEN-BEGIN:MVDCACase489
        } else if (displayable == frmNalezeno) {
            if (command == cmdBack) {//GEN-END:MVDCACase489
                // Insert pre-action code here
                getDisplay().setCurrent(get_lstFavourites());//GEN-LINE:MVDCAAction503
                // Insert post-action code here
            } else if (command == cmdNastavit) {//GEN-LINE:MVDCACase503
                // Insert pre-action code here
                favourites.setFound(favourites.id, get_dfNalezeno().getDate(), get_lstFavourites());
                // Do nothing//GEN-LINE:MVDCAAction505
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase505
        } else if (displayable == tbPoznamka) {
            if (command == cmdBack) {//GEN-END:MVDCACase505
                // Insert pre-action code here
                getDisplay().setCurrent(get_lstFavourites());//GEN-LINE:MVDCAAction519
                // Insert post-action code here
            } else if (command == cmdSave) {//GEN-LINE:MVDCACase519
                // Insert pre-action code here
                favourites.setPoznamka(favourites.id, get_tbPoznamka().getString(), get_lstFavourites());
                /*
getDisplay ().setCurrent (get_lstFavourites());//GEN-LINE:MVDCAAction517
                // Insert post-action code here
                 */
            }//GEN-BEGIN:MVDCACase517
        } else if (displayable == lstFieldNotes) {
            if (command == cmdBack) {//GEN-END:MVDCACase517
                // Insert pre-action code here
                getDisplay().setCurrent(get_lstMenu());//GEN-LINE:MVDCAAction546
                // Insert post-action code here
            } else if (command == cmdDeleteAll) {//GEN-LINE:MVDCACase546
                // Insert pre-action code here
                ConfirmDialog dialog = new ConfirmDialog(getDisplay(), "Smazat všechny položky?", "Opravdu chcete smazat všechny položky?");
                dialog.setActionNoDisplayable(lstFieldNotes);
                dialog.setActionYes(new Runnable() {
                    public void run() {
                        FieldNotes.getInstance().deleteAll();
                        get_lstFieldNotes().deleteAll();
                    }
                });
                dialog.setActionYesDisplayable(lstFieldNotes);
                dialog.show();
                // Do nothing//GEN-LINE:MVDCAAction573
                // Insert post-action code here
            } else if (command == cmdDelete) {//GEN-LINE:MVDCACase573
                // Insert pre-action code here
                ConfirmDialog dialog = new ConfirmDialog(getDisplay(), "Smazat položky?", "Opravdu chcete smazat zvolené položky?");
                dialog.setActionNoDisplayable(lstFieldNotes);
                dialog.setActionYes(new Runnable() {
                    public void run() {

                    int ids[] = FieldNotes.getInstance().getAllIds();
                    for (int i = 0; i < ids.length; i++) {
                        if (get_lstFieldNotes().isSelected(i)) {
                            FieldNotes.getInstance().deleteById(ids[i]);
                        }
                    }

                    get_lstFieldNotes().deleteAll();
                    FieldNotesItem[] items = FieldNotes.getInstance().getAll();
                    for(int i=0; i < items.length; i++)
                        get_lstFieldNotes().append(items[i].toString(!settings.iconsInFieldNotes, settings.nameInFieldNotesFirst), (settings.iconsInFieldNotes)? iconLoader.loadIcon(FieldNotes.getTypeIconName(items[i].getType()), false) : null);
                    }
                });
                dialog.setActionYesDisplayable(lstFieldNotes);
                dialog.show();
                // Do nothing//GEN-LINE:MVDCAAction571
                // Insert post-action code here
            } else if (command == cmdPostFieldNotes) {//GEN-LINE:MVDCACase571
                // Insert pre-action code here
                http.start(Http.FIELD_NOTES, true);
                // Do nothing//GEN-LINE:MVDCAAction575
                // Insert post-action code here
            } else if (command == cmdEdit) {//GEN-LINE:MVDCACase575
                // Insert pre-action code here
                int selection = firstChecked(get_lstFieldNotes());
                if (selection != -1) {
                    fieldNoteItem = FieldNotes.getInstance().getByIndex(selection);
                    
                    get_siFNGcCode().setText(fieldNoteItem.getGcCode());
                    get_tfFNGcCode().setString(fieldNoteItem.getGcCode());
                    get_cgFNType().setSelectedIndex(fieldNoteItem.getType(), true);
                    get_dtFNDate().setDate(fieldNoteItem.getDate());
                    get_tfFNText().setString(fieldNoteItem.getText());

                    get_frmFieldNote().deleteAll();
                    if (fieldNoteItem.getName().length() > 0) {
                        get_frmFieldNote().append(get_siFNGcCode());
                    } else {
                        get_frmFieldNote().append(get_tfFNGcCode());
                    }
                    get_frmFieldNote().append(get_cgFNType());
                    get_frmFieldNote().append(get_dtFNDate());
                    get_frmFieldNote().append(get_tfFNText());
                    get_frmFieldNote().setTitle("Upravit field note");
                    
                    if (fieldNoteItem.getName().length() > 0)
                        get_frmFieldNote().setTitle("FN: "+fieldNoteItem.getName());
                    
                    getDisplay().setCurrent(get_frmFieldNote());//GEN-LINE:MVDCAAction569
                // Insert post-action code here
                }
            } else if (command == cmdAdd) {//GEN-LINE:MVDCACase569
                // Insert pre-action code here
                fieldNoteItem = FieldNotes.getInstance().create();
                get_siFNGcCode().setText(fieldNoteItem.getGcCode());
                get_tfFNGcCode().setString(fieldNoteItem.getGcCode());
                get_cgFNType().setSelectedIndex(fieldNoteItem.getType(), true);
                get_dtFNDate().setDate(fieldNoteItem.getDate());
                get_tfFNText().setString(fieldNoteItem.getText());
                
                get_frmFieldNote().deleteAll();
                get_frmFieldNote().append(get_tfFNGcCode());
                get_frmFieldNote().append(get_cgFNType());
                get_frmFieldNote().append(get_dtFNDate());
                get_frmFieldNote().append(get_tfFNText());
                get_frmFieldNote().setTitle("Přidat field note");
                
                getDisplay().setCurrent(get_frmFieldNote());//GEN-LINE:MVDCAAction567
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase567
        } else if (displayable == frmFieldNote) {
            if (command == cmdBack) {//GEN-END:MVDCACase567
                // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction552
                // Insert post-action code here
                if (fromPreview) {
                    getDisplay().setCurrent(get_frmOverview()); 
                } else if (fromFieldNotes) {
                    getDisplay().setCurrent(get_lstFieldNotes()); 
                } else if (fromFavourites) {
                    getDisplay().setCurrent(get_lstFavourites()); 
                } else {
                    getDisplay().setCurrent(get_lstMenu()); 
                }
            } else if (command == cmdSave) {//GEN-LINE:MVDCACase552
                // Insert pre-action code here
                if (fromFieldNotes) {
                    fieldNoteItem.setGcCode(get_tfFNGcCode().getString());
                } else {
                    fieldNoteItem.setGcCode(get_siFNGcCode().getText());
                }
                fieldNoteItem.setType(get_cgFNType().getSelectedIndex());
                fieldNoteItem.setDate(get_dtFNDate().getDate());
                fieldNoteItem.setText(get_tfFNText().getString());
                
                fieldNoteItem.save();
                
                if (fromFavourites && fieldNoteItem.getType() == FieldNotes.TYPE_FOUND_IT) {
                    favourites.setFound(favourites.id, fieldNoteItem.getDate(), null);
                }
                // Do nothing//GEN-LINE:MVDCAAction554
                // Insert post-action code here
                Displayable fRet = null;
                if (fromPreview) {
                    fRet = get_frmOverview(); 
                } else if (fromFieldNotes) {
                    fRet = get_lstFieldNotes();
                    
                    get_lstFieldNotes().deleteAll();
                    FieldNotesItem[] items = FieldNotes.getInstance().getAll();
                    for(int i=0; i < items.length; i++)
                       get_lstFieldNotes().append(items[i].toString(!settings.iconsInFieldNotes, settings.nameInFieldNotesFirst), (settings.iconsInFieldNotes)? iconLoader.loadIcon(FieldNotes.getTypeIconName(items[i].getType()), false) : null);
                } else if (fromFavourites) {
                    fRet = get_lstFavourites(); 
                } else {
                    fRet = get_lstMenu(); 
                }
                
                showAlert("Field note uloženo.", AlertType.INFO, fRet);
            }//GEN-BEGIN:MVDCACase554
        } else if (displayable == frmMemoryInfo) {
            if (command == cmdBack) {//GEN-END:MVDCACase554
                // Insert pre-action code here
                getDisplay().setCurrent(get_frmAbout());//GEN-LINE:MVDCAAction605
                // Insert post-action code here
            }//GEN-BEGIN:MVDCACase605
        }//GEN-END:MVDCACase605
// Insert global post-action code here
        
        if (displayable == openFileBrowser && openFileBrowser != null) {
            if (command == OpenFileBrowser.OK) {
                gpxImportForm = new GPXImport(favourites, getDisplay(), http);
                gpxImportForm.setListener(this);
                gpxImportForm.parse(openFileBrowser.getFileName());
                openFileBrowser = null;
            }
        } else if (displayable == gpxImportForm && gpxImportForm != null) {
            if (command == GPXImport.CANCEL || command == GPXImport.SUCCESS) {
                favourites.viewAll();
            }
        }
        
}//GEN-LINE:MVDCAEnd
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /** This method initializes UI of the application.//GEN-BEGIN:MVDInitBegin
     */
    private void initialize() {//GEN-END:MVDInitBegin
        // Insert pre-init code here
        if (settings.vip)
            getDisplay().setCurrent(get_lstMode());
        else
            getDisplay().setCurrent(get_ssAdvertisement());//GEN-LINE:MVDInitInit
        // Insert post-init code here
    }//GEN-LINE:MVDInitEnd
    
    /***
     * This method should return an instance of the display.
     */
    public Display getDisplay()//GEN-FIRST:MVDGetDisplay
{
        return Display.getDisplay(this);
    }//GEN-LAST:MVDGetDisplay
    
    /***
     * This method should exit the midlet.
     */
    public void exitMIDlet()//GEN-FIRST:MVDExitMidlet
{
        settings.close();
        favourites.close();
        multiSolver.close();
        patterns.close();
        if (gps != null)
            gps.stop();
        if (gpsParser != null)
            gpsParser.close();
        getDisplay().setCurrent(null);
        destroyApp(true);
        notifyDestroyed();
    }//GEN-LAST:MVDExitMidlet
    
    
    
    /** This method returns instance for lstMenu component and should be called instead of accessing lstMenu field directly.//GEN-BEGIN:MVDGetBegin5
     * @return Instance for lstMenu component
     */
    public List get_lstMenu() {
        if (lstMenu == null) {//GEN-END:MVDGetBegin5
            // Insert pre-init code here
            lstMenu = new List("Handy Geocaching", Choice.IMPLICIT, new String[] {//GEN-BEGIN:MVDGetInit5
                "Vyhled\u00E1v\u00E1n\u00ED",
                "Obl\u00EDben\u00E9",
                "Dal\u0161\u00ED funkce",
                "Nastaven\u00ED",
                "Field notes",
                "O aplikaci",
                "Konec"
            }, new Image[] {
                get_imgSearch(),
                get_imgFavourites(),
                get_imgOther(),
                get_imgSettings(),
                get_imgFieldNotes(),
                get_imgAbout(),
                get_imgExit()
            });
            lstMenu.setCommandListener(this);
            lstMenu.setSelectedFlags(new boolean[] {
                false,
                false,
                false,
                false,
                false,
                false,
                false
            });
            lstMenu.setFitPolicy(Choice.TEXT_WRAP_OFF);//GEN-END:MVDGetInit5
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd5
        return lstMenu;
    }//GEN-END:MVDGetEnd5
    
    /** This method returns instance for frmAveraging component and should be called instead of accessing frmAveraging field directly.//GEN-BEGIN:MVDGetBegin7
     * @return Instance for frmAveraging component
     */
    public Form get_frmAveraging() {
        if (frmAveraging == null) {//GEN-END:MVDGetBegin7
            // Insert pre-init code here
            frmAveraging = new Form("Pr\u016Fm\u011Brov\u00E1n\u00ED", new Item[] {//GEN-BEGIN:MVDGetInit7
                get_siCurrentCoordinates(),
                get_siAverageLattitude(),
                get_siAverageLongitude(),
                get_siMeasures(),
                get_siAdditional()
            });
            frmAveraging.addCommand(get_cmdBack());
            frmAveraging.addCommand(get_cmdPause());
            frmAveraging.addCommand(get_cmdResume());
            frmAveraging.addCommand(get_cmdFavourite());
            frmAveraging.setCommandListener(this);//GEN-END:MVDGetInit7
            // Insert post-init code here
            get_frmAveraging().removeCommand(get_cmdResume());
        }//GEN-BEGIN:MVDGetEnd7
        return frmAveraging;
    }//GEN-END:MVDGetEnd7
    
    
    /** This method returns instance for siCurrentCoordinates component and should be called instead of accessing siCurrentCoordinates field directly.//GEN-BEGIN:MVDGetBegin10
     * @return Instance for siCurrentCoordinates component
     */
    public StringItem get_siCurrentCoordinates() {
        if (siCurrentCoordinates == null) {//GEN-END:MVDGetBegin10
            // Insert pre-init code here
            siCurrentCoordinates = new StringItem("Aktu\u00E1ln\u00ED sou\u0159adnice:", "");//GEN-LINE:MVDGetInit10
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd10
        return siCurrentCoordinates;
    }//GEN-END:MVDGetEnd10
    
    /** This method returns instance for siAverageLattitude component and should be called instead of accessing siAverageLattitude field directly.//GEN-BEGIN:MVDGetBegin11
     * @return Instance for siAverageLattitude component
     */
    public StringItem get_siAverageLattitude() {
        if (siAverageLattitude == null) {//GEN-END:MVDGetBegin11
            // Insert pre-init code here
            siAverageLattitude = new StringItem("Pr\u016Fm\u011Brn\u00E9 sou\u0159adnice:", "");//GEN-LINE:MVDGetInit11
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd11
        return siAverageLattitude;
    }//GEN-END:MVDGetEnd11
    
    /** This method returns instance for siMeasures component and should be called instead of accessing siMeasures field directly.//GEN-BEGIN:MVDGetBegin12
     * @return Instance for siMeasures component
     */
    public StringItem get_siMeasures() {
        if (siMeasures == null) {//GEN-END:MVDGetBegin12
            // Insert pre-init code here
            siMeasures = new StringItem("Po\u010Det m\u011B\u0159en\u00ED:", "");//GEN-LINE:MVDGetInit12
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd12
        return siMeasures;
    }//GEN-END:MVDGetEnd12
    
    /** This method returns instance for siAdditional component and should be called instead of accessing siAdditional field directly.//GEN-BEGIN:MVDGetBegin13
     * @return Instance for siAdditional component
     */
    public StringItem get_siAdditional() {
        if (siAdditional == null) {//GEN-END:MVDGetBegin13
            // Insert pre-init code here
            siAdditional = new StringItem("Satelity/Rychlost/P\u0159esnost", "");//GEN-LINE:MVDGetInit13
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd13
        return siAdditional;
    }//GEN-END:MVDGetEnd13
    
    
    /** This method returns instance for cmdExit component and should be called instead of accessing cmdExit field directly.//GEN-BEGIN:MVDGetBegin15
     * @return Instance for cmdExit component
     */
    public Command get_cmdExit() {
        if (cmdExit == null) {//GEN-END:MVDGetBegin15
            // Insert pre-init code here
            cmdExit = new Command("Konec", Command.EXIT, 1);//GEN-LINE:MVDGetInit15
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd15
        return cmdExit;
    }//GEN-END:MVDGetEnd15
    
    /** This method returns instance for cmdBack component and should be called instead of accessing cmdBack field directly.//GEN-BEGIN:MVDGetBegin21
     * @return Instance for cmdBack component
     */
    public Command get_cmdBack() {
        if (cmdBack == null) {//GEN-END:MVDGetBegin21
            // Insert pre-init code here
            cmdBack = new Command("Zp\u011Bt", Command.BACK, 1);//GEN-LINE:MVDGetInit21
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd21
        return cmdBack;
    }//GEN-END:MVDGetEnd21
    
    /** This method returns instance for cmdPause component and should be called instead of accessing cmdPause field directly.//GEN-BEGIN:MVDGetBegin23
     * @return Instance for cmdPause component
     */
    public Command get_cmdPause() {
        if (cmdPause == null) {//GEN-END:MVDGetBegin23
            // Insert pre-init code here
            cmdPause = new Command("Pozastavit", Command.SCREEN, 1);//GEN-LINE:MVDGetInit23
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd23
        return cmdPause;
    }//GEN-END:MVDGetEnd23
    
    /** This method returns instance for cmdResume component and should be called instead of accessing cmdResume field directly.//GEN-BEGIN:MVDGetBegin25
     * @return Instance for cmdResume component
     */
    public Command get_cmdResume() {
        if (cmdResume == null) {//GEN-END:MVDGetBegin25
            // Insert pre-init code here
            cmdResume = new Command("Pokra\u010Dovat", Command.SCREEN, 1);//GEN-LINE:MVDGetInit25
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd25
        return cmdResume;
    }//GEN-END:MVDGetEnd25
    
    /** This method returns instance for lstMode component and should be called instead of accessing lstMode field directly.//GEN-BEGIN:MVDGetBegin27
     * @return Instance for lstMode component
     */
    public List get_lstMode() {
        if (lstMode == null) {//GEN-END:MVDGetBegin27
            // Insert pre-init code here
            lstMode = new List("Zvolte m\u00F3d:", Choice.IMPLICIT, new String[] {//GEN-BEGIN:MVDGetInit27
                "Bluetooth GPS",
                "Intern\u00ED GPS",
                "PDA GPS",
                "SonyEricsson HGE-100",
                "Bez GPS"
            }, new Image[] {
                get_imgBluetooth(),
                get_imgGps(),
                get_imgPdaGps(),
                get_imgGps(),
                get_imgNoGps()
            });
            lstMode.addCommand(get_cmdExit());
            lstMode.addCommand(get_cmdHint());
            lstMode.setCommandListener(this);
            lstMode.setSelectedFlags(new boolean[] {
                false,
                false,
                false,
                false,
                false
            });
            lstMode.setFitPolicy(Choice.TEXT_WRAP_OFF);//GEN-END:MVDGetInit27
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd27
        return lstMode;
    }//GEN-END:MVDGetEnd27
    
    
    
    
    
    
    
    
    
    /** This method returns instance for frmLoading component and should be called instead of accessing frmLoading field directly.//GEN-BEGIN:MVDGetBegin53
     * @return Instance for frmLoading component
     */
    public Form get_frmLoading() {
        if (frmLoading == null) {//GEN-END:MVDGetBegin53
            // Insert pre-init code here
            frmLoading = new Form("Pros\u00EDm \u010Dekejte", new Item[] {//GEN-BEGIN:MVDGetInit53
                get_siMessage(),
                get_gaLoading()
            });
            frmLoading.addCommand(get_cmdStop());
            frmLoading.setCommandListener(this);//GEN-END:MVDGetInit53
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd53
        return frmLoading;
    }//GEN-END:MVDGetEnd53
    
    /** This method returns instance for siMessage component and should be called instead of accessing siMessage field directly.//GEN-BEGIN:MVDGetBegin54
     * @return Instance for siMessage component
     */
    public StringItem get_siMessage() {
        if (siMessage == null) {//GEN-END:MVDGetBegin54
            // Insert pre-init code here
            siMessage = new StringItem("", "P\u0159ipojuji se k serveru a stahuji data...");//GEN-LINE:MVDGetInit54
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd54
        return siMessage;
    }//GEN-END:MVDGetEnd54
    
    
    
    
    /** This method returns instance for cmdSend component and should be called instead of accessing cmdSend field directly.//GEN-BEGIN:MVDGetBegin58
     * @return Instance for cmdSend component
     */
    public Command get_cmdSend() {
        if (cmdSend == null) {//GEN-END:MVDGetBegin58
            // Insert pre-init code here
            cmdSend = new Command("Ok", Command.SCREEN, 1);//GEN-LINE:MVDGetInit58
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd58
        return cmdSend;
    }//GEN-END:MVDGetEnd58
    
    
    /** This method returns instance for cmdMenu component and should be called instead of accessing cmdMenu field directly.//GEN-BEGIN:MVDGetBegin62
     * @return Instance for cmdMenu component
     */
    public Command get_cmdMenu() {
        if (cmdMenu == null) {//GEN-END:MVDGetBegin62
            // Insert pre-init code here
            cmdMenu = new Command("Menu", Command.SCREEN, 1);//GEN-LINE:MVDGetInit62
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd62
        return cmdMenu;
    }//GEN-END:MVDGetEnd62
    
    
    /** This method returns instance for frmCoordinates component and should be called instead of accessing frmCoordinates field directly.//GEN-BEGIN:MVDGetBegin65
     * @return Instance for frmCoordinates component
     */
    public Form get_frmCoordinates() {
        if (frmCoordinates == null) {//GEN-END:MVDGetBegin65
            // Insert pre-init code here
            frmCoordinates = new Form("Zadejte sou\u0159adnice:", new Item[] {//GEN-BEGIN:MVDGetInit65
                get_tfLattitude(),
                get_tfLongitude(),
                get_stringItem6()
            });
            frmCoordinates.addCommand(get_cmdSend());
            frmCoordinates.addCommand(get_cmdBack());
            frmCoordinates.setCommandListener(this);//GEN-END:MVDGetInit65
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd65
        return frmCoordinates;
    }//GEN-END:MVDGetEnd65
    
    /** This method returns instance for tfLattitude component and should be called instead of accessing tfLattitude field directly.//GEN-BEGIN:MVDGetBegin66
     * @return Instance for tfLattitude component
     */
    public TextField get_tfLattitude() {
        if (tfLattitude == null) {//GEN-END:MVDGetBegin66
            // Insert pre-init code here
            tfLattitude = new TextField("", "", 120, TextField.ANY);//GEN-LINE:MVDGetInit66
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd66
        return tfLattitude;
    }//GEN-END:MVDGetEnd66
    
    /** This method returns instance for tfLongitude component and should be called instead of accessing tfLongitude field directly.//GEN-BEGIN:MVDGetBegin67
     * @return Instance for tfLongitude component
     */
    public TextField get_tfLongitude() {
        if (tfLongitude == null) {//GEN-END:MVDGetBegin67
            // Insert pre-init code here
            tfLongitude = new TextField("", "", 120, TextField.ANY);//GEN-LINE:MVDGetInit67
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd67
        return tfLongitude;
    }//GEN-END:MVDGetEnd67
    
    
    /** This method returns instance for lstNearestCaches component and should be called instead of accessing lstNearestCaches field directly.//GEN-BEGIN:MVDGetBegin71
     * @return Instance for lstNearestCaches component
     */
    public List get_lstNearestCaches() {
        if (lstNearestCaches == null) {//GEN-END:MVDGetBegin71
            // Insert pre-init code here
            lstNearestCaches = new List("Nejbli\u017E\u0161\u00ED ke\u0161e", Choice.IMPLICIT, new String[] {"\u017D\u00E1dn\u00E1 data"}, new Image[] {null});//GEN-BEGIN:MVDGetInit71
            lstNearestCaches.addCommand(get_cmdBack());
            lstNearestCaches.addCommand(get_cmdDownloadAll());
            lstNearestCaches.setCommandListener(this);
            lstNearestCaches.setSelectedFlags(new boolean[] {false});
            lstNearestCaches.setFitPolicy(Choice.TEXT_WRAP_ON);//GEN-END:MVDGetInit71
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd71
        return lstNearestCaches;
    }//GEN-END:MVDGetEnd71
    
    
    
    
    /** This method returns instance for frmWaypoint component and should be called instead of accessing frmWaypoint field directly.//GEN-BEGIN:MVDGetBegin85
     * @return Instance for frmWaypoint component
     */
    public Form get_frmWaypoint() {
        if (frmWaypoint == null) {//GEN-END:MVDGetBegin85
            // Insert pre-init code here
            frmWaypoint = new Form("Zadejte waypoint:", new Item[] {get_tfWaypoint()});//GEN-BEGIN:MVDGetInit85
            frmWaypoint.addCommand(get_cmdBack());
            frmWaypoint.addCommand(get_cmdSend());
            frmWaypoint.setCommandListener(this);//GEN-END:MVDGetInit85
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd85
        return frmWaypoint;
    }//GEN-END:MVDGetEnd85
    
    /** This method returns instance for tfWaypoint component and should be called instead of accessing tfWaypoint field directly.//GEN-BEGIN:MVDGetBegin86
     * @return Instance for tfWaypoint component
     */
    public TextField get_tfWaypoint() {
        if (tfWaypoint == null) {//GEN-END:MVDGetBegin86
            // Insert pre-init code here
            tfWaypoint = new TextField("", "GC", 120, TextField.ANY);//GEN-LINE:MVDGetInit86
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd86
        return tfWaypoint;
    }//GEN-END:MVDGetEnd86
    
    
    /** This method returns instance for frmOverview component and should be called instead of accessing frmOverview field directly.//GEN-BEGIN:MVDGetBegin90
     * @return Instance for frmOverview component
     */
    public Form get_frmOverview() {
        if (frmOverview == null) {//GEN-END:MVDGetBegin90
            // Insert pre-init code here
            frmOverview = new Form("Detaily ke\u0161e", new Item[] {//GEN-BEGIN:MVDGetInit90
                get_siName(),
                get_siAuthor(),
                get_siType(),
                get_siSize(),
                get_siOverviewLattitude(),
                get_siOverviewLongitude(),
                get_siNalezenoOver(),
                get_siDifficulty(),
                get_siWaypoint(),
                get_siInventory(),
                get_siPoznamkaOver()
            });
            frmOverview.addCommand(get_cmdBack());
            frmOverview.addCommand(get_cmdHint());
            frmOverview.addCommand(get_cmdInfo());
            frmOverview.addCommand(get_cmdLogs());
            frmOverview.addCommand(get_cmdWaypoints());
            frmOverview.addCommand(get_cmdNavigate());
            frmOverview.addCommand(get_cmdNext());
            frmOverview.addCommand(get_cmdFavourite());
            frmOverview.addCommand(get_cmdDownloadPatterns());
            frmOverview.addCommand(get_cmdRefresh());
            frmOverview.addCommand(get_cmdAddFieldNotes());
            frmOverview.addCommand(get_cmdPoznamka());
            frmOverview.setCommandListener(this);//GEN-END:MVDGetInit90
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd90
        return frmOverview;
    }//GEN-END:MVDGetEnd90
    
    /** This method returns instance for siName component and should be called instead of accessing siName field directly.//GEN-BEGIN:MVDGetBegin94
     * @return Instance for siName component
     */
    public StringItem get_siName() {
        if (siName == null) {//GEN-END:MVDGetBegin94
            // Insert pre-init code here
            siName = new StringItem("N\u00E1zev:", "");//GEN-LINE:MVDGetInit94
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd94
        return siName;
    }//GEN-END:MVDGetEnd94
    
    /** This method returns instance for siAuthor component and should be called instead of accessing siAuthor field directly.//GEN-BEGIN:MVDGetBegin95
     * @return Instance for siAuthor component
     */
    public StringItem get_siAuthor() {
        if (siAuthor == null) {//GEN-END:MVDGetBegin95
            // Insert pre-init code here
            siAuthor = new StringItem("Autor:", "");//GEN-LINE:MVDGetInit95
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd95
        return siAuthor;
    }//GEN-END:MVDGetEnd95
    
    /** This method returns instance for siWaypoint component and should be called instead of accessing siWaypoint field directly.//GEN-BEGIN:MVDGetBegin96
     * @return Instance for siWaypoint component
     */
    public StringItem get_siWaypoint() {
        if (siWaypoint == null) {//GEN-END:MVDGetBegin96
            // Insert pre-init code here
            siWaypoint = new StringItem("Waypoint:", "");//GEN-LINE:MVDGetInit96
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd96
        return siWaypoint;
    }//GEN-END:MVDGetEnd96
    
    /** This method returns instance for siType component and should be called instead of accessing siType field directly.//GEN-BEGIN:MVDGetBegin97
     * @return Instance for siType component
     */
    public StringItem get_siType() {
        if (siType == null) {//GEN-END:MVDGetBegin97
            // Insert pre-init code here
            siType = new StringItem("Typ:", "");//GEN-LINE:MVDGetInit97
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd97
        return siType;
    }//GEN-END:MVDGetEnd97
    
    /** This method returns instance for siSize component and should be called instead of accessing siSize field directly.//GEN-BEGIN:MVDGetBegin98
     * @return Instance for siSize component
     */
    public StringItem get_siSize() {
        if (siSize == null) {//GEN-END:MVDGetBegin98
            // Insert pre-init code here
            siSize = new StringItem("Velikost:", "");//GEN-LINE:MVDGetInit98
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd98
        return siSize;
    }//GEN-END:MVDGetEnd98
    
    
    /** This method returns instance for siDifficulty component and should be called instead of accessing siDifficulty field directly.//GEN-BEGIN:MVDGetBegin100
     * @return Instance for siDifficulty component
     */
    public StringItem get_siDifficulty() {
        if (siDifficulty == null) {//GEN-END:MVDGetBegin100
            // Insert pre-init code here
            siDifficulty = new StringItem("Obt\u00ED\u017Enost/Ter\u00E9n:", "");//GEN-LINE:MVDGetInit100
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd100
        return siDifficulty;
    }//GEN-END:MVDGetEnd100
    
    /** This method returns instance for siInventory component and should be called instead of accessing siInventory field directly.//GEN-BEGIN:MVDGetBegin101
     * @return Instance for siInventory component
     */
    public StringItem get_siInventory() {
        if (siInventory == null) {//GEN-END:MVDGetBegin101
            // Insert pre-init code here
            siInventory = new StringItem("Invent\u00E1\u0159:", "");//GEN-LINE:MVDGetInit101
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd101
        return siInventory;
    }//GEN-END:MVDGetEnd101
    
    /** This method returns instance for cmdHint component and should be called instead of accessing cmdHint field directly.//GEN-BEGIN:MVDGetBegin102
     * @return Instance for cmdHint component
     */
    public Command get_cmdHint() {
        if (cmdHint == null) {//GEN-END:MVDGetBegin102
            // Insert pre-init code here
            cmdHint = new Command("N\u00E1pov\u011Bda", Command.SCREEN, 2);//GEN-LINE:MVDGetInit102
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd102
        return cmdHint;
    }//GEN-END:MVDGetEnd102
    
    /** This method returns instance for cmdInfo component and should be called instead of accessing cmdInfo field directly.//GEN-BEGIN:MVDGetBegin104
     * @return Instance for cmdInfo component
     */
    public Command get_cmdInfo() {
        if (cmdInfo == null) {//GEN-END:MVDGetBegin104
            // Insert pre-init code here
            cmdInfo = new Command("Podrobnosti", Command.SCREEN, 3);//GEN-LINE:MVDGetInit104
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd104
        return cmdInfo;
    }//GEN-END:MVDGetEnd104
    
    /** This method returns instance for cmdLogs component and should be called instead of accessing cmdLogs field directly.//GEN-BEGIN:MVDGetBegin106
     * @return Instance for cmdLogs component
     */
    public Command get_cmdLogs() {
        if (cmdLogs == null) {//GEN-END:MVDGetBegin106
            // Insert pre-init code here
            cmdLogs = new Command("Logy", Command.SCREEN, 4);//GEN-LINE:MVDGetInit106
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd106
        return cmdLogs;
    }//GEN-END:MVDGetEnd106
    
    /** This method returns instance for cmdWaypoints component and should be called instead of accessing cmdWaypoints field directly.//GEN-BEGIN:MVDGetBegin108
     * @return Instance for cmdWaypoints component
     */
    public Command get_cmdWaypoints() {
        if (cmdWaypoints == null) {//GEN-END:MVDGetBegin108
            // Insert pre-init code here
            cmdWaypoints = new Command("Waypointy", Command.SCREEN, 5);//GEN-LINE:MVDGetInit108
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd108
        return cmdWaypoints;
    }//GEN-END:MVDGetEnd108
    
    /** This method returns instance for frmInfo component and should be called instead of accessing frmInfo field directly.//GEN-BEGIN:MVDGetBegin110
     * @return Instance for frmInfo component
     */
    public Form get_frmInfo() {
        if (frmInfo == null) {//GEN-END:MVDGetBegin110
            // Insert pre-init code here
            frmInfo = new Form("Listing ke\u0161e", new Item[] {//GEN-BEGIN:MVDGetInit110
                get_siBegin(),
                get_siContent(),
                get_siEnd()
            });
            frmInfo.addCommand(get_cmdBack());
            frmInfo.addCommand(get_cmdBegin());
            frmInfo.addCommand(get_cmdEnd());
            frmInfo.setCommandListener(this);//GEN-END:MVDGetInit110
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd110
        return frmInfo;
    }//GEN-END:MVDGetEnd110
    
    /** This method returns instance for frmHint component and should be called instead of accessing frmHint field directly.//GEN-BEGIN:MVDGetBegin112
     * @return Instance for frmHint component
     */
    public Form get_frmHint() {
        if (frmHint == null) {//GEN-END:MVDGetBegin112
            // Insert pre-init code here
            frmHint = new Form("N\u00E1pov\u011Bda", new Item[0]);//GEN-BEGIN:MVDGetInit112
            frmHint.addCommand(get_cmdBack());
            frmHint.setCommandListener(this);//GEN-END:MVDGetInit112
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd112
        return frmHint;
    }//GEN-END:MVDGetEnd112
    
    /** This method returns instance for frmWaypoints component and should be called instead of accessing frmWaypoints field directly.//GEN-BEGIN:MVDGetBegin116
     * @return Instance for frmWaypoints component
     */
    public Form get_frmWaypoints() {
        if (frmWaypoints == null) {//GEN-END:MVDGetBegin116
            // Insert pre-init code here
            frmWaypoints = new Form("Waypointy", new Item[0]);//GEN-BEGIN:MVDGetInit116
            frmWaypoints.addCommand(get_cmdBack());
            frmWaypoints.addCommand(get_cmdFavourite());
            frmWaypoints.setCommandListener(this);//GEN-END:MVDGetInit116
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd116
        return frmWaypoints;
    }//GEN-END:MVDGetEnd116
    
    /** This method returns instance for frmLogs component and should be called instead of accessing frmLogs field directly.//GEN-BEGIN:MVDGetBegin118
     * @return Instance for frmLogs component
     */
    public Form get_frmLogs() {
        if (frmLogs == null) {//GEN-END:MVDGetBegin118
            // Insert pre-init code here
            frmLogs = new Form("Posledn\u00ED logy", new Item[0]);//GEN-BEGIN:MVDGetInit118
            frmLogs.addCommand(get_cmdBack());
            frmLogs.setCommandListener(this);//GEN-END:MVDGetInit118
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd118
        return frmLogs;
    }//GEN-END:MVDGetEnd118
    
    /** This method returns instance for imgSearch component and should be called instead of accessing imgSearch field directly.//GEN-BEGIN:MVDGetBegin120
     * @return Instance for imgSearch component
     */
    public Image get_imgSearch() {
        if (imgSearch == null) {//GEN-END:MVDGetBegin120
            // Insert pre-init code here
            try {//GEN-BEGIN:MVDGetInit120
                imgSearch = Image.createImage("/dummyIcon.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit120
            imgSearch = iconLoader.loadIcon("search");
        }//GEN-BEGIN:MVDGetEnd120
        return imgSearch;
    }//GEN-END:MVDGetEnd120
    
    /** This method returns instance for imgNoGps component and should be called instead of accessing imgNoGps field directly.//GEN-BEGIN:MVDGetBegin121
     * @return Instance for imgNoGps component
     */
    public Image get_imgNoGps() {
        if (imgNoGps == null) {//GEN-END:MVDGetBegin121
            // Insert pre-init code here
            try {//GEN-BEGIN:MVDGetInit121
                imgNoGps = Image.createImage("/dummyIcon.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit121
            imgNoGps = iconLoader.loadIcon("nogps");
        }//GEN-BEGIN:MVDGetEnd121
        return imgNoGps;
    }//GEN-END:MVDGetEnd121
    
    /** This method returns instance for imgGps component and should be called instead of accessing imgGps field directly.//GEN-BEGIN:MVDGetBegin122
     * @return Instance for imgGps component
     */
    public Image get_imgGps() {
        if (imgGps == null) {//GEN-END:MVDGetBegin122
            // Insert pre-init code here
            try {//GEN-BEGIN:MVDGetInit122
                imgGps = Image.createImage("/dummyIcon.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit122
            imgGps = iconLoader.loadIcon("satelite");
        }//GEN-BEGIN:MVDGetEnd122
        return imgGps;
    }//GEN-END:MVDGetEnd122
    
    /** This method returns instance for imgAveraging component and should be called instead of accessing imgAveraging field directly.//GEN-BEGIN:MVDGetBegin123
     * @return Instance for imgAveraging component
     */
    public Image get_imgAveraging() {
        if (imgAveraging == null) {//GEN-END:MVDGetBegin123
            // Insert pre-init code here
            try {//GEN-BEGIN:MVDGetInit123
                imgAveraging = Image.createImage("/dummyIcon.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit123
            imgAveraging = iconLoader.loadIcon("average");
        }//GEN-BEGIN:MVDGetEnd123
        return imgAveraging;
    }//GEN-END:MVDGetEnd123
    
    /** This method returns instance for imgAbout component and should be called instead of accessing imgAbout field directly.//GEN-BEGIN:MVDGetBegin124
     * @return Instance for imgAbout component
     */
    public Image get_imgAbout() {
        if (imgAbout == null) {//GEN-END:MVDGetBegin124
            // Insert pre-init code here
            try {//GEN-BEGIN:MVDGetInit124
                imgAbout = Image.createImage("/dummyIcon.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit124
            imgAbout = iconLoader.loadIcon("about");
        }//GEN-BEGIN:MVDGetEnd124
        return imgAbout;
    }//GEN-END:MVDGetEnd124
    
    /** This method returns instance for imgExit component and should be called instead of accessing imgExit field directly.//GEN-BEGIN:MVDGetBegin125
     * @return Instance for imgExit component
     */
    public Image get_imgExit() {
        if (imgExit == null) {//GEN-END:MVDGetBegin125
            // Insert pre-init code here
            try {//GEN-BEGIN:MVDGetInit125
                imgExit = Image.createImage("/dummyIcon.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit125
            imgExit = iconLoader.loadIcon("exit");
        }//GEN-BEGIN:MVDGetEnd125
        return imgExit;
    }//GEN-END:MVDGetEnd125
    
    /** This method returns instance for frmAbout component and should be called instead of accessing frmAbout field directly.//GEN-BEGIN:MVDGetBegin126
     * @return Instance for frmAbout component
     */
    public Form get_frmAbout() {
        if (frmAbout == null) {//GEN-END:MVDGetBegin126
            // Insert pre-init code here
            frmAbout = new Form("Handy Geocaching", new Item[] {//GEN-BEGIN:MVDGetInit126
                get_siVerze(),
                get_siSestaveni(),
                get_stringItem1(),
                get_siDonate()
            });
            frmAbout.addCommand(get_cmdBack());
            frmAbout.addCommand(get_cmdMemoryInfo());
            frmAbout.setCommandListener(this);//GEN-END:MVDGetInit126
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd126
        return frmAbout;
    }//GEN-END:MVDGetEnd126
    
    /** This method returns instance for siVerze component and should be called instead of accessing siVerze field directly.//GEN-BEGIN:MVDGetBegin127
     * @return Instance for siVerze component
     */
    public StringItem get_siVerze() {
        if (siVerze == null) {//GEN-END:MVDGetBegin127
            // Insert pre-init code here
            siVerze = new StringItem("Verze:", "");//GEN-LINE:MVDGetInit127
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd127
        return siVerze;
    }//GEN-END:MVDGetEnd127
    
    /** This method returns instance for stringItem1 component and should be called instead of accessing stringItem1 field directly.//GEN-BEGIN:MVDGetBegin128
     * @return Instance for stringItem1 component
     */
    public StringItem get_stringItem1() {
        if (stringItem1 == null) {//GEN-END:MVDGetBegin128
            // Insert pre-init code here
            stringItem1 = new StringItem("O aplikaci:", "Tuto aplikaci sponzoruje Axima spol. s.r.o., Palack\u00E9ho t\u0159\u00EDda 16, 61200 Brno.\n\nAplikaci vytvo\u0159il David V\u00E1vra (Destil). Kontakt: me@destil.cz\n\nV p\u0159\u00EDpad\u011B probl\u00E9m\u016F a pro v\u00EDce informac\u00ED nav\u0161tivte str\u00E1nky http://hg.destil.cz");//GEN-LINE:MVDGetInit128
            stringItem1.setText(stringItem1.getText()+"\n");
// Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd128
        return stringItem1;
    }//GEN-END:MVDGetEnd128
    
    
    
    
    
    /** This method returns instance for cmdStop component and should be called instead of accessing cmdStop field directly.//GEN-BEGIN:MVDGetBegin135
     * @return Instance for cmdStop component
     */
    public Command get_cmdStop() {
        if (cmdStop == null) {//GEN-END:MVDGetBegin135
            // Insert pre-init code here
            cmdStop = new Command("Stop", Command.STOP, 1);//GEN-LINE:MVDGetInit135
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd135
        return cmdStop;
    }//GEN-END:MVDGetEnd135
    
    /** This method returns instance for imgSettings component and should be called instead of accessing imgSettings field directly.//GEN-BEGIN:MVDGetBegin140
     * @return Instance for imgSettings component
     */
    public Image get_imgSettings() {
        if (imgSettings == null) {//GEN-END:MVDGetBegin140
            // Insert pre-init code here
            try {//GEN-BEGIN:MVDGetInit140
                imgSettings = Image.createImage("/dummyIcon.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit140
            imgSettings = iconLoader.loadIcon("settings");
        }//GEN-BEGIN:MVDGetEnd140
        return imgSettings;
    }//GEN-END:MVDGetEnd140
    
    /** This method returns instance for frmSettings component and should be called instead of accessing frmSettings field directly.//GEN-BEGIN:MVDGetBegin141
     * @return Instance for frmSettings component
     */
    public Form get_frmSettings() {
        if (frmSettings == null) {//GEN-END:MVDGetBegin141
            // Insert pre-init code here
            frmSettings = new Form("Nastaven\u00ED", new Item[] {//GEN-BEGIN:MVDGetInit141
                get_stringItem3(),
                get_tfName(),
                get_tfPassword(),
                get_cgCacheFilter(),
                get_tfNumberCaches(),
                get_cgInternalGPSType(),
                get_cgFieldNotes(),
                get_tfBackLight(),
                get_stringItem10()
            });
            frmSettings.addCommand(get_cmdSave());
            frmSettings.addCommand(get_cmdBack());
            frmSettings.setCommandListener(this);//GEN-END:MVDGetInit141
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd141
        return frmSettings;
    }//GEN-END:MVDGetEnd141
    
    /** This method returns instance for cmdSave component and should be called instead of accessing cmdSave field directly.//GEN-BEGIN:MVDGetBegin143
     * @return Instance for cmdSave component
     */
    public Command get_cmdSave() {
        if (cmdSave == null) {//GEN-END:MVDGetBegin143
            // Insert pre-init code here
            cmdSave = new Command("Ulo\u017Eit", Command.OK, 1);//GEN-LINE:MVDGetInit143
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd143
        return cmdSave;
    }//GEN-END:MVDGetEnd143
    
    /** This method returns instance for stringItem3 component and should be called instead of accessing stringItem3 field directly.//GEN-BEGIN:MVDGetBegin145
     * @return Instance for stringItem3 component
     */
    public StringItem get_stringItem3() {
        if (stringItem3 == null) {//GEN-END:MVDGetBegin145
            // Insert pre-init code here
            stringItem3 = new StringItem("P\u0159ihla\u0161ovac\u00ED \u00FAdaje na geocaching.com:", "");//GEN-LINE:MVDGetInit145
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd145
        return stringItem3;
    }//GEN-END:MVDGetEnd145
    
    /** This method returns instance for tfName component and should be called instead of accessing tfName field directly.//GEN-BEGIN:MVDGetBegin146
     * @return Instance for tfName component
     */
    public TextField get_tfName() {
        if (tfName == null) {//GEN-END:MVDGetBegin146
            // Insert pre-init code here
            tfName = new TextField("Jm\u00E9no:", null, 120, TextField.ANY);//GEN-LINE:MVDGetInit146
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd146
        return tfName;
    }//GEN-END:MVDGetEnd146
    
    /** This method returns instance for tfPassword component and should be called instead of accessing tfPassword field directly.//GEN-BEGIN:MVDGetBegin147
     * @return Instance for tfPassword component
     */
    public TextField get_tfPassword() {
        if (tfPassword == null) {//GEN-END:MVDGetBegin147
            // Insert pre-init code here
            tfPassword = new TextField("Heslo:", null, 120, TextField.ANY | TextField.PASSWORD);//GEN-LINE:MVDGetInit147
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd147
        return tfPassword;
    }//GEN-END:MVDGetEnd147
    
    /** This method returns instance for cgCacheFilter component and should be called instead of accessing cgCacheFilter field directly.//GEN-BEGIN:MVDGetBegin148
     * @return Instance for cgCacheFilter component
     */
    public ChoiceGroup get_cgCacheFilter() {
        if (cgCacheFilter == null) {//GEN-END:MVDGetBegin148
            // Insert pre-init code here
            cgCacheFilter = new ChoiceGroup("Nejbli\u017E\u0161\u00ED ke\u0161e:", Choice.MULTIPLE, new String[] {//GEN-BEGIN:MVDGetInit148
                "Traditional",
                "Multi",
                "Mystery",
                "Ostatn\u00ED typy",
                "Nalezen\u00E9 a vlastn\u00ED",
                "Disabled"
            }, new Image[] {
                null,
                null,
                null,
                null,
                null,
                null
            });
            cgCacheFilter.setSelectedFlags(new boolean[] {
                false,
                false,
                false,
                false,
                false,
                false
            });//GEN-END:MVDGetInit148
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd148
        return cgCacheFilter;
    }//GEN-END:MVDGetEnd148
    
    
    /** This method returns instance for frmConnecting component and should be called instead of accessing frmConnecting field directly.//GEN-BEGIN:MVDGetBegin152
     * @return Instance for frmConnecting component
     */
    public Form get_frmConnecting() {
        if (frmConnecting == null) {//GEN-END:MVDGetBegin152
            // Insert pre-init code here
            frmConnecting = new Form("Pr\u016Fb\u011Bh p\u0159ipojen\u00ED", new Item[0]);//GEN-BEGIN:MVDGetInit152
            frmConnecting.addCommand(get_cmdBack());
            frmConnecting.setCommandListener(this);//GEN-END:MVDGetInit152
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd152
        return frmConnecting;
    }//GEN-END:MVDGetEnd152
    
    
    
    
    
    
    
    
    
    
    /** This method returns instance for cmdNavigate component and should be called instead of accessing cmdNavigate field directly.//GEN-BEGIN:MVDGetBegin167
     * @return Instance for cmdNavigate component
     */
    public Command get_cmdNavigate() {
        if (cmdNavigate == null) {//GEN-END:MVDGetBegin167
            // Insert pre-init code here
            cmdNavigate = new Command("Navigovat", Command.SCREEN, 1);//GEN-LINE:MVDGetInit167
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd167
        return cmdNavigate;
    }//GEN-END:MVDGetEnd167
    
    /** This method returns instance for stringItem6 component and should be called instead of accessing stringItem6 field directly.//GEN-BEGIN:MVDGetBegin169
     * @return Instance for stringItem6 component
     */
    public StringItem get_stringItem6() {
        if (stringItem6 == null) {//GEN-END:MVDGetBegin169
            // Insert pre-init code here
            stringItem6 = new StringItem("Form\u00E1t:", "N ss\u00B0 mm.mmm\nE sss\u00B0 mm.mmm");//GEN-LINE:MVDGetInit169
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd169
        return stringItem6;
    }//GEN-END:MVDGetEnd169
    
    
    
    /** This method returns instance for cmdNext component and should be called instead of accessing cmdNext field directly.//GEN-BEGIN:MVDGetBegin172
     * @return Instance for cmdNext component
     */
    public Command get_cmdNext() {
        if (cmdNext == null) {//GEN-END:MVDGetBegin172
            // Insert pre-init code here
            cmdNext = new Command("Naj\u00EDt nejbli\u017E\u0161\u00ED", Command.SCREEN, 10);//GEN-LINE:MVDGetInit172
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd172
        return cmdNext;
    }//GEN-END:MVDGetEnd172
    
    
    /** This method returns instance for frmAllLogs component and should be called instead of accessing frmAllLogs field directly.//GEN-BEGIN:MVDGetBegin177
     * @return Instance for frmAllLogs component
     */
    public Form get_frmAllLogs() {
        if (frmAllLogs == null) {//GEN-END:MVDGetBegin177
            // Insert pre-init code here
            frmAllLogs = new Form("V\u0161echny dal\u0161\u00ED logy", new Item[0]);//GEN-BEGIN:MVDGetInit177
            frmAllLogs.addCommand(get_cmdBack());
            frmAllLogs.setCommandListener(this);//GEN-END:MVDGetInit177
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd177
        return frmAllLogs;
    }//GEN-END:MVDGetEnd177
    
    /** This method returns instance for imgNavigate component and should be called instead of accessing imgNavigate field directly.//GEN-BEGIN:MVDGetBegin179
     * @return Instance for imgNavigate component
     */
    public Image get_imgNavigate() {
        if (imgNavigate == null) {//GEN-END:MVDGetBegin179
            // Insert pre-init code here
            try {//GEN-BEGIN:MVDGetInit179
                imgNavigate = Image.createImage("/dummyIcon.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit179
            imgNavigate = iconLoader.loadIcon("arrow");
        }//GEN-BEGIN:MVDGetEnd179
        return imgNavigate;
    }//GEN-END:MVDGetEnd179
    
    /** This method returns instance for fntBold component and should be called instead of accessing fntBold field directly.//GEN-BEGIN:MVDGetBegin182
     * @return Instance for fntBold component
     */
    public Font get_fntBold() {
        if (fntBold == null) {//GEN-END:MVDGetBegin182
            // Insert pre-init code here
            fntBold = Font.getFont(Font.FACE_SYSTEM, 0x1, Font.SIZE_MEDIUM);//GEN-LINE:MVDGetInit182
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd182
        return fntBold;
    }//GEN-END:MVDGetEnd182
    
    /** This method returns instance for fntNormal component and should be called instead of accessing fntNormal field directly.//GEN-BEGIN:MVDGetBegin183
     * @return Instance for fntNormal component
     */
    public Font get_fntNormal() {
        if (fntNormal == null) {//GEN-END:MVDGetBegin183
            // Insert pre-init code here
            fntNormal = Font.getFont(Font.FACE_SYSTEM, 0x0, Font.SIZE_MEDIUM);//GEN-LINE:MVDGetInit183
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd183
        return fntNormal;
    }//GEN-END:MVDGetEnd183
    
    /** This method returns instance for ssAdvertisement component and should be called instead of accessing ssAdvertisement field directly.//GEN-BEGIN:MVDGetBegin184
     * @return Instance for ssAdvertisement component
     */
    public org.netbeans.microedition.lcdui.SplashScreen get_ssAdvertisement() {
        if (ssAdvertisement == null) {//GEN-END:MVDGetBegin184
            // Insert pre-init code here
            ssAdvertisement = new org.netbeans.microedition.lcdui.SplashScreen(getDisplay());//GEN-BEGIN:MVDGetInit184
            ssAdvertisement.setCommandListener(this);
            ssAdvertisement.setTitle("Handy Geocaching");
            ssAdvertisement.setFullScreenMode(true);
            ssAdvertisement.setText("");
            ssAdvertisement.setImage(get_imgAdvertisement());
            ssAdvertisement.setTimeout(2000);//GEN-END:MVDGetInit184
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd184
        return ssAdvertisement;
    }//GEN-END:MVDGetEnd184
    
    /** This method returns instance for imgAdvertisement component and should be called instead of accessing imgAdvertisement field directly.//GEN-BEGIN:MVDGetBegin189
     * @return Instance for imgAdvertisement component
     */
    public Image get_imgAdvertisement() {
        if (imgAdvertisement == null) {//GEN-END:MVDGetBegin189
            // Insert pre-init code here
            try {//GEN-BEGIN:MVDGetInit189
                imgAdvertisement = Image.createImage("/reklama.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit189
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd189
        return imgAdvertisement;
    }//GEN-END:MVDGetEnd189
    
    /** This method returns instance for frmDebug component and should be called instead of accessing frmDebug field directly.//GEN-BEGIN:MVDGetBegin190
     * @return Instance for frmDebug component
     */
    public Form get_frmDebug() {
        if (frmDebug == null) {//GEN-END:MVDGetBegin190
            // Insert pre-init code here
            frmDebug = new Form("Debug", new Item[] {//GEN-BEGIN:MVDGetInit190
                get_siDebug(),
                get_siDebug2()
            });
            frmDebug.addCommand(get_cmdMenu());
            frmDebug.setCommandListener(this);//GEN-END:MVDGetInit190
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd190
        return frmDebug;
    }//GEN-END:MVDGetEnd190
    
    /** This method returns instance for siDebug component and should be called instead of accessing siDebug field directly.//GEN-BEGIN:MVDGetBegin192
     * @return Instance for siDebug component
     */
    public StringItem get_siDebug() {
        if (siDebug == null) {//GEN-END:MVDGetBegin192
            // Insert pre-init code here
            siDebug = new StringItem("", "");//GEN-LINE:MVDGetInit192
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd192
        return siDebug;
    }//GEN-END:MVDGetEnd192
    
    /** This method returns instance for siDebug2 component and should be called instead of accessing siDebug2 field directly.//GEN-BEGIN:MVDGetBegin193
     * @return Instance for siDebug2 component
     */
    public StringItem get_siDebug2() {
        if (siDebug2 == null) {//GEN-END:MVDGetBegin193
            // Insert pre-init code here
            siDebug2 = new StringItem("", "");//GEN-LINE:MVDGetInit193
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd193
        return siDebug2;
    }//GEN-END:MVDGetEnd193
    
    
    /** This method returns instance for lstDevices component and should be called instead of accessing lstDevices field directly.//GEN-BEGIN:MVDGetBegin3
     * @return Instance for lstDevices component
     */
    public List get_lstDevices() {
        if (lstDevices == null) {//GEN-END:MVDGetBegin3
            // Insert pre-init code here
            lstDevices = new List("Vyberte GPS modul:", Choice.IMPLICIT, new String[] {"Pros\u00EDm \u010Dekejte..."}, new Image[] {null});//GEN-BEGIN:MVDGetInit3
            lstDevices.addCommand(get_cmdExit());
            lstDevices.addCommand(get_cmdBack());
            lstDevices.setCommandListener(this);
            lstDevices.setSelectedFlags(new boolean[] {false});
            lstDevices.setFitPolicy(Choice.TEXT_WRAP_OFF);//GEN-END:MVDGetInit3
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd3
        return lstDevices;
    }//GEN-END:MVDGetEnd3
    
    
    
    
    
    /** This method returns instance for imgKeyword component and should be called instead of accessing imgKeyword field directly.//GEN-BEGIN:MVDGetBegin212
     * @return Instance for imgKeyword component
     */
    public Image get_imgKeyword() {
        if (imgKeyword == null) {//GEN-END:MVDGetBegin212
            // Insert pre-init code here
            try {//GEN-BEGIN:MVDGetInit212
                imgKeyword = Image.createImage("/dummyIcon.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit212
            imgKeyword = iconLoader.loadIcon("srch_keyword");
        }//GEN-BEGIN:MVDGetEnd212
        return imgKeyword;
    }//GEN-END:MVDGetEnd212
    
    /** This method returns instance for imgFavourites component and should be called instead of accessing imgFavourites field directly.//GEN-BEGIN:MVDGetBegin215
     * @return Instance for imgFavourites component
     */
    public Image get_imgFavourites() {
        if (imgFavourites == null) {//GEN-END:MVDGetBegin215
            // Insert pre-init code here
            try {//GEN-BEGIN:MVDGetInit215
                imgFavourites = Image.createImage("/dummyIcon.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit215
            imgFavourites = iconLoader.loadIcon("favorite");
        }//GEN-BEGIN:MVDGetEnd215
        return imgFavourites;
    }//GEN-END:MVDGetEnd215
    
    
    /** This method returns instance for tfNumberCaches component and should be called instead of accessing tfNumberCaches field directly.//GEN-BEGIN:MVDGetBegin218
     * @return Instance for tfNumberCaches component
     */
    public TextField get_tfNumberCaches() {
        if (tfNumberCaches == null) {//GEN-END:MVDGetBegin218
            // Insert pre-init code here
            tfNumberCaches = new TextField("Po\u010Det ke\u0161\u00ED p\u0159i hled\u00E1n\u00ED:", "10", 120, TextField.NUMERIC);//GEN-LINE:MVDGetInit218
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd218
        return tfNumberCaches;
    }//GEN-END:MVDGetEnd218
    
    /** This method returns instance for frmKeyword component and should be called instead of accessing frmKeyword field directly.//GEN-BEGIN:MVDGetBegin219
     * @return Instance for frmKeyword component
     */
    public Form get_frmKeyword() {
        if (frmKeyword == null) {//GEN-END:MVDGetBegin219
            // Insert pre-init code here
            frmKeyword = new Form("Zadejte kl\u00ED\u010Dov\u00E9 slovo:", new Item[] {get_tfKeyword()});//GEN-BEGIN:MVDGetInit219
            frmKeyword.addCommand(get_cmdBack());
            frmKeyword.addCommand(get_cmdSend());
            frmKeyword.setCommandListener(this);//GEN-END:MVDGetInit219
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd219
        return frmKeyword;
    }//GEN-END:MVDGetEnd219
    
    /** This method returns instance for tfKeyword component and should be called instead of accessing tfKeyword field directly.//GEN-BEGIN:MVDGetBegin220
     * @return Instance for tfKeyword component
     */
    public TextField get_tfKeyword() {
        if (tfKeyword == null) {//GEN-END:MVDGetBegin220
            // Insert pre-init code here
            tfKeyword = new TextField("", null, 120, TextField.ANY);//GEN-LINE:MVDGetInit220
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd220
        return tfKeyword;
    }//GEN-END:MVDGetEnd220
    
    /** This method returns instance for lstKeyword component and should be called instead of accessing lstKeyword field directly.//GEN-BEGIN:MVDGetBegin223
     * @return Instance for lstKeyword component
     */
    public List get_lstKeyword() {
        if (lstKeyword == null) {//GEN-END:MVDGetBegin223
            // Insert pre-init code here
            lstKeyword = new List("Nalezen\u00E9 ke\u0161e:", Choice.IMPLICIT, new String[] {"\u017D\u00E1dn\u00E1 data"}, new Image[] {null});//GEN-BEGIN:MVDGetInit223
            lstKeyword.addCommand(get_cmdBack());
            lstKeyword.setCommandListener(this);
            lstKeyword.setSelectedFlags(new boolean[] {false});
            lstKeyword.setFitPolicy(Choice.TEXT_WRAP_ON);//GEN-END:MVDGetInit223
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd223
        return lstKeyword;
    }//GEN-END:MVDGetEnd223
    
    /** This method returns instance for lstSearch component and should be called instead of accessing lstSearch field directly.//GEN-BEGIN:MVDGetBegin226
     * @return Instance for lstSearch component
     */
    public List get_lstSearch() {
        if (lstSearch == null) {//GEN-END:MVDGetBegin226
            // Insert pre-init code here
            lstSearch = new List("Vyhled\u00E1v\u00E1n\u00ED", Choice.IMPLICIT, new String[] {//GEN-BEGIN:MVDGetInit226
                "Nejbli\u017E\u0161\u00ED dle GPS",
                "GC k\u00F3d",
                "Kl\u00ED\u010Dov\u00E9 slovo",
                "Sou\u0159adnice"
            }, new Image[] {
                get_imgGps(),
                get_imgWaypoint(),
                get_imgKeyword(),
                get_imgNearest()
            });
            lstSearch.addCommand(get_cmdBack());
            lstSearch.setCommandListener(this);
            lstSearch.setSelectedFlags(new boolean[] {
                false,
                false,
                false,
                false
            });
            lstSearch.setFitPolicy(Choice.TEXT_WRAP_OFF);//GEN-END:MVDGetInit226
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd226
        return lstSearch;
    }//GEN-END:MVDGetEnd226
    
    /** This method returns instance for imgNearest component and should be called instead of accessing imgNearest field directly.//GEN-BEGIN:MVDGetBegin234
     * @return Instance for imgNearest component
     */
    public Image get_imgNearest() {
        if (imgNearest == null) {//GEN-END:MVDGetBegin234
            // Insert pre-init code here
            try {//GEN-BEGIN:MVDGetInit234
                imgNearest = Image.createImage("/dummyIcon.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit234
            imgNearest = iconLoader.loadIcon("srch_nearest");
        }//GEN-BEGIN:MVDGetEnd234
        return imgNearest;
    }//GEN-END:MVDGetEnd234
    
    /** This method returns instance for lstGPS component and should be called instead of accessing lstGPS field directly.//GEN-BEGIN:MVDGetBegin237
     * @return Instance for lstGPS component
     */
    public List get_lstGPS() {
        if (lstGPS == null) {//GEN-END:MVDGetBegin237
            // Insert pre-init code here
            lstGPS = new List("Dal\u0161\u00ED funkce", Choice.IMPLICIT, new String[] {//GEN-BEGIN:MVDGetInit237
                "MultiSolver",
                "TB/GC",
                "De\u0161ifr\u00E1tor",
                "Pr\u016Fm\u011Brov\u00E1n\u00ED",
                "Navigace",
                "GPS Sign\u00E1l"
            }, new Image[] {
                get_imgMultiSolver(),
                get_imgTravelbug(),
                get_imgDecypher(),
                get_imgAveraging(),
                get_imgNavigate(),
                get_imgGps()
            });
            lstGPS.addCommand(get_cmdBack());
            lstGPS.setCommandListener(this);
            lstGPS.setSelectedFlags(new boolean[] {
                false,
                false,
                false,
                false,
                false,
                false
            });
            lstGPS.setFitPolicy(Choice.TEXT_WRAP_OFF);//GEN-END:MVDGetInit237
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd237
        return lstGPS;
    }//GEN-END:MVDGetEnd237
    
    /** This method returns instance for siOverviewLattitude component and should be called instead of accessing siOverviewLattitude field directly.//GEN-BEGIN:MVDGetBegin249
     * @return Instance for siOverviewLattitude component
     */
    public StringItem get_siOverviewLattitude() {
        if (siOverviewLattitude == null) {//GEN-END:MVDGetBegin249
            // Insert pre-init code here
            siOverviewLattitude = new StringItem("Sou\u0159adnice:", "");//GEN-LINE:MVDGetInit249
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd249
        return siOverviewLattitude;
    }//GEN-END:MVDGetEnd249
    
    /** This method returns instance for siOverviewLongitude component and should be called instead of accessing siOverviewLongitude field directly.//GEN-BEGIN:MVDGetBegin250
     * @return Instance for siOverviewLongitude component
     */
    public StringItem get_siOverviewLongitude() {
        if (siOverviewLongitude == null) {//GEN-END:MVDGetBegin250
            // Insert pre-init code here
            siOverviewLongitude = new StringItem("", "");//GEN-LINE:MVDGetInit250
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd250
        return siOverviewLongitude;
    }//GEN-END:MVDGetEnd250
    
    /** This method returns instance for lstFavourites component and should be called instead of accessing lstFavourites field directly.//GEN-BEGIN:MVDGetBegin251
     * @return Instance for lstFavourites component
     */
    public List get_lstFavourites() {
        if (lstFavourites == null) {//GEN-END:MVDGetBegin251
            // Insert pre-init code here
            lstFavourites = new List("Obl\u00EDben\u00E9", Choice.MULTIPLE, new String[0], new Image[0]);//GEN-BEGIN:MVDGetInit251
            lstFavourites.addCommand(get_cmdBack());
            lstFavourites.addCommand(get_cmdDeleteAll());
            lstFavourites.addCommand(get_cmdSelect());
            lstFavourites.addCommand(get_cmdAddActual());
            lstFavourites.addCommand(get_cmdAddGiven());
            lstFavourites.addCommand(get_cmdDelete());
            lstFavourites.addCommand(get_cmdEdit());
            lstFavourites.addCommand(get_cmdNavigate());
            lstFavourites.addCommand(get_cmdMultiSolver());
            lstFavourites.addCommand(get_cmdMap());
            lstFavourites.addCommand(get_cmdMapyCz());
            lstFavourites.addCommand(get_cmdAddFieldNotes());
            lstFavourites.addCommand(get_cmdPoznamka());
            lstFavourites.addCommand(get_cmdImportGPX());
            lstFavourites.setCommandListener(this);
            lstFavourites.setSelectedFlags(new boolean[0]);
            lstFavourites.setFitPolicy(Choice.TEXT_WRAP_OFF);//GEN-END:MVDGetInit251
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd251
        return lstFavourites;
    }//GEN-END:MVDGetEnd251
    
    /** This method returns instance for cmdFavourite component and should be called instead of accessing cmdFavourite field directly.//GEN-BEGIN:MVDGetBegin254
     * @return Instance for cmdFavourite component
     */
    public Command get_cmdFavourite() {
        if (cmdFavourite == null) {//GEN-END:MVDGetBegin254
            // Insert pre-init code here
            cmdFavourite = new Command("Do obl\u00EDben\u00FDch", Command.SCREEN, 6);//GEN-LINE:MVDGetInit254
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd254
        return cmdFavourite;
    }//GEN-END:MVDGetEnd254
    
    /** This method returns instance for cmdDeleteAll component and should be called instead of accessing cmdDeleteAll field directly.//GEN-BEGIN:MVDGetBegin256
     * @return Instance for cmdDeleteAll component
     */
    public Command get_cmdDeleteAll() {
        if (cmdDeleteAll == null) {//GEN-END:MVDGetBegin256
            // Insert pre-init code here
            cmdDeleteAll = new Command("Smazat v\u0161e", Command.SCREEN, 8);//GEN-LINE:MVDGetInit256
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd256
        return cmdDeleteAll;
    }//GEN-END:MVDGetEnd256
    
    /** This method returns instance for cmdSelect component and should be called instead of accessing cmdSelect field directly.//GEN-BEGIN:MVDGetBegin258
     * @return Instance for cmdSelect component
     */
    public Command get_cmdSelect() {
        if (cmdSelect == null) {//GEN-END:MVDGetBegin258
            // Insert pre-init code here
            cmdSelect = new Command("Zobrazit", Command.SCREEN, 2);//GEN-LINE:MVDGetInit258
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd258
        return cmdSelect;
    }//GEN-END:MVDGetEnd258
    
    /** This method returns instance for frmFavourite component and should be called instead of accessing frmFavourite field directly.//GEN-BEGIN:MVDGetBegin261
     * @return Instance for frmFavourite component
     */
    public Form get_frmFavourite() {
        if (frmFavourite == null) {//GEN-END:MVDGetBegin261
            // Insert pre-init code here
            frmFavourite = new Form(null, new Item[] {//GEN-BEGIN:MVDGetInit261
                get_siFavouriteLattitude(),
                get_siFavouriteLongitude(),
                get_siDescription(),
                get_siNalezeno1(),
                get_siPoznamka()
            });
            frmFavourite.addCommand(get_cmdBack());
            frmFavourite.addCommand(get_cmdNavigate());
            frmFavourite.addCommand(get_cmdNext());
            frmFavourite.addCommand(get_cmdPoznamka());
            frmFavourite.addCommand(get_cmdSetFound());
            frmFavourite.setCommandListener(this);//GEN-END:MVDGetInit261
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd261
        return frmFavourite;
    }//GEN-END:MVDGetEnd261
    
    /** This method returns instance for siFavouriteLattitude component and should be called instead of accessing siFavouriteLattitude field directly.//GEN-BEGIN:MVDGetBegin264
     * @return Instance for siFavouriteLattitude component
     */
    public StringItem get_siFavouriteLattitude() {
        if (siFavouriteLattitude == null) {//GEN-END:MVDGetBegin264
            // Insert pre-init code here
            siFavouriteLattitude = new StringItem("Sou\u0159adnice:", "");//GEN-LINE:MVDGetInit264
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd264
        return siFavouriteLattitude;
    }//GEN-END:MVDGetEnd264
    
    /** This method returns instance for siFavouriteLongitude component and should be called instead of accessing siFavouriteLongitude field directly.//GEN-BEGIN:MVDGetBegin265
     * @return Instance for siFavouriteLongitude component
     */
    public StringItem get_siFavouriteLongitude() {
        if (siFavouriteLongitude == null) {//GEN-END:MVDGetBegin265
            // Insert pre-init code here
            siFavouriteLongitude = new StringItem("", "");//GEN-LINE:MVDGetInit265
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd265
        return siFavouriteLongitude;
    }//GEN-END:MVDGetEnd265
    
    /** This method returns instance for siDescription component and should be called instead of accessing siDescription field directly.//GEN-BEGIN:MVDGetBegin266
     * @return Instance for siDescription component
     */
    public StringItem get_siDescription() {
        if (siDescription == null) {//GEN-END:MVDGetBegin266
            // Insert pre-init code here
            siDescription = new StringItem("Popis:", "");//GEN-LINE:MVDGetInit266
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd266
        return siDescription;
    }//GEN-END:MVDGetEnd266
    
    /** This method returns instance for cmdAddActual component and should be called instead of accessing cmdAddActual field directly.//GEN-BEGIN:MVDGetBegin267
     * @return Instance for cmdAddActual component
     */
    public Command get_cmdAddActual() {
        if (cmdAddActual == null) {//GEN-END:MVDGetBegin267
            // Insert pre-init code here
            cmdAddActual = new Command("+Aktu\u00E1ln\u00ED bod", Command.SCREEN, 4);//GEN-LINE:MVDGetInit267
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd267
        return cmdAddActual;
    }//GEN-END:MVDGetEnd267
    
    /** This method returns instance for cmdAddGiven component and should be called instead of accessing cmdAddGiven field directly.//GEN-BEGIN:MVDGetBegin268
     * @return Instance for cmdAddGiven component
     */
    public Command get_cmdAddGiven() {
        if (cmdAddGiven == null) {//GEN-END:MVDGetBegin268
            // Insert pre-init code here
            cmdAddGiven = new Command("+Zadan\u00FD bod", Command.SCREEN, 5);//GEN-LINE:MVDGetInit268
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd268
        return cmdAddGiven;
    }//GEN-END:MVDGetEnd268
    
    /** This method returns instance for cmdDelete component and should be called instead of accessing cmdDelete field directly.//GEN-BEGIN:MVDGetBegin271
     * @return Instance for cmdDelete component
     */
    public Command get_cmdDelete() {
        if (cmdDelete == null) {//GEN-END:MVDGetBegin271
            // Insert pre-init code here
            cmdDelete = new Command("Smazat", Command.SCREEN, 6);//GEN-LINE:MVDGetInit271
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd271
        return cmdDelete;
    }//GEN-END:MVDGetEnd271
    
    /** This method returns instance for frmAddGiven component and should be called instead of accessing frmAddGiven field directly.//GEN-BEGIN:MVDGetBegin273
     * @return Instance for frmAddGiven component
     */
    public Form get_frmAddGiven() {
        if (frmAddGiven == null) {//GEN-END:MVDGetBegin273
            // Insert pre-init code here
            frmAddGiven = new Form("P\u0159idat bod", new Item[] {//GEN-BEGIN:MVDGetInit273
                get_tfGivenLattitude(),
                get_tfGivenLongitude(),
                get_cgGivenFormat(),
                get_stringItem4(),
                get_tfGivenName(),
                get_tfGivenDescription(),
                get_siNalezeno()
            });
            frmAddGiven.addCommand(get_cmdSave());
            frmAddGiven.addCommand(get_cmdBack());
            frmAddGiven.setCommandListener(this);//GEN-END:MVDGetInit273
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd273
        return frmAddGiven;
    }//GEN-END:MVDGetEnd273
    
    /** This method returns instance for tfGivenLattitude component and should be called instead of accessing tfGivenLattitude field directly.//GEN-BEGIN:MVDGetBegin274
     * @return Instance for tfGivenLattitude component
     */
    public TextField get_tfGivenLattitude() {
        if (tfGivenLattitude == null) {//GEN-END:MVDGetBegin274
            // Insert pre-init code here
            tfGivenLattitude = new TextField("Sou\u0159adnice:", null, 120, TextField.ANY);//GEN-LINE:MVDGetInit274
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd274
        return tfGivenLattitude;
    }//GEN-END:MVDGetEnd274
    
    /** This method returns instance for tfGivenLongitude component and should be called instead of accessing tfGivenLongitude field directly.//GEN-BEGIN:MVDGetBegin275
     * @return Instance for tfGivenLongitude component
     */
    public TextField get_tfGivenLongitude() {
        if (tfGivenLongitude == null) {//GEN-END:MVDGetBegin275
            // Insert pre-init code here
            tfGivenLongitude = new TextField("", null, 120, TextField.ANY);//GEN-LINE:MVDGetInit275
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd275
        return tfGivenLongitude;
    }//GEN-END:MVDGetEnd275
    
    /** This method returns instance for stringItem4 component and should be called instead of accessing stringItem4 field directly.//GEN-BEGIN:MVDGetBegin276
     * @return Instance for stringItem4 component
     */
    public StringItem get_stringItem4() {
        if (stringItem4 == null) {//GEN-END:MVDGetBegin276
            // Insert pre-init code here
            stringItem4 = new StringItem("Vzor:", "\n1) N st\u00B0 mi.mmm \n    E sst\u00B0 mi.mmm\n2) N st\u00B0 mi ss.sss \n    E sst\u00B0 mi ss.sss");//GEN-LINE:MVDGetInit276
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd276
        return stringItem4;
    }//GEN-END:MVDGetEnd276
    
    /** This method returns instance for tfGivenName component and should be called instead of accessing tfGivenName field directly.//GEN-BEGIN:MVDGetBegin277
     * @return Instance for tfGivenName component
     */
    public TextField get_tfGivenName() {
        if (tfGivenName == null) {//GEN-END:MVDGetBegin277
            // Insert pre-init code here
            tfGivenName = new TextField(" N\u00E1zev:", null, 120, TextField.ANY);//GEN-LINE:MVDGetInit277
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd277
        return tfGivenName;
    }//GEN-END:MVDGetEnd277
    
    /** This method returns instance for tfGivenDescription component and should be called instead of accessing tfGivenDescription field directly.//GEN-BEGIN:MVDGetBegin278
     * @return Instance for tfGivenDescription component
     */
    public TextField get_tfGivenDescription() {
        if (tfGivenDescription == null) {//GEN-END:MVDGetBegin278
            // Insert pre-init code here
            tfGivenDescription = new TextField("Popis:", null, 1200, TextField.ANY);//GEN-LINE:MVDGetInit278
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd278
        return tfGivenDescription;
    }//GEN-END:MVDGetEnd278
    
    /** This method returns instance for siDonate component and should be called instead of accessing siDonate field directly.//GEN-BEGIN:MVDGetBegin281
     * @return Instance for siDonate component
     */
    public StringItem get_siDonate() {
        if (siDonate == null) {//GEN-END:MVDGetBegin281
            // Insert pre-init code here
            siDonate = new StringItem("Donate:", "Pokud se V\u00E1m aplikace l\u00EDb\u00ED, podpo\u0159te finan\u010Dn\u011B jej\u00ED v\u00FDvoj! Za\u0161lete pros\u00EDm libovolnou \u010D\u00E1stku na \u00FA\u010Det autora:\n\n51-5385890237/0100\n\nVe zpr\u00E1v\u011B pro p\u0159\u00EDjemce uve\u010F\u0165e sv\u016Fj nick na GC.com, p\u0159estane se V\u00E1m pak ukazovat \u00FAvodn\u00ED reklama.");//GEN-LINE:MVDGetInit281
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd281
        return siDonate;
    }//GEN-END:MVDGetEnd281
    
    /** This method returns instance for cmdEdit component and should be called instead of accessing cmdEdit field directly.//GEN-BEGIN:MVDGetBegin284
     * @return Instance for cmdEdit component
     */
    public Command get_cmdEdit() {
        if (cmdEdit == null) {//GEN-END:MVDGetBegin284
            // Insert pre-init code here
            cmdEdit = new Command("Upravit", Command.SCREEN, 7);//GEN-LINE:MVDGetInit284
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd284
        return cmdEdit;
    }//GEN-END:MVDGetEnd284
    
    /** This method returns instance for imgTravelbug component and should be called instead of accessing imgTravelbug field directly.//GEN-BEGIN:MVDGetBegin288
     * @return Instance for imgTravelbug component
     */
    public Image get_imgTravelbug() {
        if (imgTravelbug == null) {//GEN-END:MVDGetBegin288
            // Insert pre-init code here
            try {//GEN-BEGIN:MVDGetInit288
                imgTravelbug = Image.createImage("/dummyIcon.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit288
            imgTravelbug = iconLoader.loadIcon("travelbug");
        }//GEN-BEGIN:MVDGetEnd288
        return imgTravelbug;
    }//GEN-END:MVDGetEnd288
    
    /** This method returns instance for frmTrackingNumber component and should be called instead of accessing frmTrackingNumber field directly.//GEN-BEGIN:MVDGetBegin289
     * @return Instance for frmTrackingNumber component
     */
    public Form get_frmTrackingNumber() {
        if (frmTrackingNumber == null) {//GEN-END:MVDGetBegin289
            // Insert pre-init code here
            frmTrackingNumber = new Form("TB/GC", new Item[] {//GEN-BEGIN:MVDGetInit289
                get_tfTrackingNumber(),
                get_stringItem7()
            });
            frmTrackingNumber.addCommand(get_cmdBack());
            frmTrackingNumber.addCommand(get_cmdSend());
            frmTrackingNumber.setCommandListener(this);//GEN-END:MVDGetInit289
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd289
        return frmTrackingNumber;
    }//GEN-END:MVDGetEnd289
    
    /** This method returns instance for tfTrackingNumber component and should be called instead of accessing tfTrackingNumber field directly.//GEN-BEGIN:MVDGetBegin290
     * @return Instance for tfTrackingNumber component
     */
    public TextField get_tfTrackingNumber() {
        if (tfTrackingNumber == null) {//GEN-END:MVDGetBegin290
            // Insert pre-init code here
            tfTrackingNumber = new TextField("Tracking number:", "", 120, TextField.ANY);//GEN-LINE:MVDGetInit290
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd290
        return tfTrackingNumber;
    }//GEN-END:MVDGetEnd290
    
    /** This method returns instance for stringItem7 component and should be called instead of accessing stringItem7 field directly.//GEN-BEGIN:MVDGetBegin291
     * @return Instance for stringItem7 component
     */
    public StringItem get_stringItem7() {
        if (stringItem7 == null) {//GEN-END:MVDGetBegin291
            // Insert pre-init code here
            stringItem7 = new StringItem("", "Tracking number naleznete na dan\u00E9m trackovateln\u00E9m p\u0159edm\u011Btu.");//GEN-LINE:MVDGetInit291
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd291
        return stringItem7;
    }//GEN-END:MVDGetEnd291
    
    /** This method returns instance for frmTrackable component and should be called instead of accessing frmTrackable field directly.//GEN-BEGIN:MVDGetBegin295
     * @return Instance for frmTrackable component
     */
    public Form get_frmTrackable() {
        if (frmTrackable == null) {//GEN-END:MVDGetBegin295
            // Insert pre-init code here
            frmTrackable = new Form("TB/GC", new Item[] {//GEN-BEGIN:MVDGetInit295
                get_siOrigin(),
                get_siLastLocation(),
                get_siReleased(),
                get_siOwner(),
                get_siGoal(),
                get_siAbout()
            });
            frmTrackable.addCommand(get_cmdBack());
            frmTrackable.setCommandListener(this);//GEN-END:MVDGetInit295
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd295
        return frmTrackable;
    }//GEN-END:MVDGetEnd295
    
    /** This method returns instance for siOrigin component and should be called instead of accessing siOrigin field directly.//GEN-BEGIN:MVDGetBegin298
     * @return Instance for siOrigin component
     */
    public StringItem get_siOrigin() {
        if (siOrigin == null) {//GEN-END:MVDGetBegin298
            // Insert pre-init code here
            siOrigin = new StringItem("P\u016Fvod", "");//GEN-LINE:MVDGetInit298
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd298
        return siOrigin;
    }//GEN-END:MVDGetEnd298
    
    /** This method returns instance for siLastLocation component and should be called instead of accessing siLastLocation field directly.//GEN-BEGIN:MVDGetBegin299
     * @return Instance for siLastLocation component
     */
    public StringItem get_siLastLocation() {
        if (siLastLocation == null) {//GEN-END:MVDGetBegin299
            // Insert pre-init code here
            siLastLocation = new StringItem("Posledn\u00ED v\u00FDskyt", "");//GEN-LINE:MVDGetInit299
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd299
        return siLastLocation;
    }//GEN-END:MVDGetEnd299
    
    /** This method returns instance for siReleased component and should be called instead of accessing siReleased field directly.//GEN-BEGIN:MVDGetBegin300
     * @return Instance for siReleased component
     */
    public StringItem get_siReleased() {
        if (siReleased == null) {//GEN-END:MVDGetBegin300
            // Insert pre-init code here
            siReleased = new StringItem("Vysl\u00E1no", "");//GEN-LINE:MVDGetInit300
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd300
        return siReleased;
    }//GEN-END:MVDGetEnd300
    
    /** This method returns instance for siOwner component and should be called instead of accessing siOwner field directly.//GEN-BEGIN:MVDGetBegin301
     * @return Instance for siOwner component
     */
    public StringItem get_siOwner() {
        if (siOwner == null) {//GEN-END:MVDGetBegin301
            // Insert pre-init code here
            siOwner = new StringItem("Vlastn\u00EDk", "");//GEN-LINE:MVDGetInit301
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd301
        return siOwner;
    }//GEN-END:MVDGetEnd301
    
    /** This method returns instance for siGoal component and should be called instead of accessing siGoal field directly.//GEN-BEGIN:MVDGetBegin302
     * @return Instance for siGoal component
     */
    public StringItem get_siGoal() {
        if (siGoal == null) {//GEN-END:MVDGetBegin302
            // Insert pre-init code here
            siGoal = new StringItem("C\u00EDl p\u0159edm\u011Btu", "");//GEN-LINE:MVDGetInit302
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd302
        return siGoal;
    }//GEN-END:MVDGetEnd302
    
    /** This method returns instance for siAbout component and should be called instead of accessing siAbout field directly.//GEN-BEGIN:MVDGetBegin303
     * @return Instance for siAbout component
     */
    public StringItem get_siAbout() {
        if (siAbout == null) {//GEN-END:MVDGetBegin303
            // Insert pre-init code here
            siAbout = new StringItem("Popis p\u0159edm\u011Btu", "");//GEN-LINE:MVDGetInit303
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd303
        return siAbout;
    }//GEN-END:MVDGetEnd303
    
    
    
    
    
    /** This method returns instance for fntSmall component and should be called instead of accessing fntSmall field directly.//GEN-BEGIN:MVDGetBegin311
     * @return Instance for fntSmall component
     */
    public Font get_fntSmall() {
        if (fntSmall == null) {//GEN-END:MVDGetBegin311
            // Insert pre-init code here
            fntSmall = Font.getFont(Font.FACE_SYSTEM, 0x0, Font.SIZE_SMALL);//GEN-LINE:MVDGetInit311
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd311
        return fntSmall;
    }//GEN-END:MVDGetEnd311
    
    /** This method returns instance for frmMultiSolver component and should be called instead of accessing frmMultiSolver field directly.//GEN-BEGIN:MVDGetBegin317
     * @return Instance for frmMultiSolver component
     */
    public Form get_frmMultiSolver() {
        if (frmMultiSolver == null) {//GEN-END:MVDGetBegin317
            // Insert pre-init code here
            frmMultiSolver = new Form("MultiSolver", new Item[] {//GEN-BEGIN:MVDGetInit317
                get_siLattitudePattern(),
                get_siLongitudePattern(),
                get_siLetters()
            });
            frmMultiSolver.addCommand(get_cmdBack());
            frmMultiSolver.addCommand(get_cmdDeleteAll());
            frmMultiSolver.addCommand(get_cmdAddLetter());
            frmMultiSolver.addCommand(get_cmdCompute());
            frmMultiSolver.addCommand(get_cmdPatterns());
            frmMultiSolver.addCommand(get_cmdFavourites());
            frmMultiSolver.setCommandListener(this);//GEN-END:MVDGetInit317
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd317
        return frmMultiSolver;
    }//GEN-END:MVDGetEnd317
    
    /** This method returns instance for siLattitudePattern component and should be called instead of accessing siLattitudePattern field directly.//GEN-BEGIN:MVDGetBegin319
     * @return Instance for siLattitudePattern component
     */
    public StringItem get_siLattitudePattern() {
        if (siLattitudePattern == null) {//GEN-END:MVDGetBegin319
            // Insert pre-init code here
            siLattitudePattern = new StringItem("Aktu\u00E1ln\u00ED vzorec:", "Nen\u00ED zvolen aktu\u00E1ln\u00ED vzorec");//GEN-LINE:MVDGetInit319
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd319
        return siLattitudePattern;
    }//GEN-END:MVDGetEnd319
    
    /** This method returns instance for siLongitudePattern component and should be called instead of accessing siLongitudePattern field directly.//GEN-BEGIN:MVDGetBegin320
     * @return Instance for siLongitudePattern component
     */
    public StringItem get_siLongitudePattern() {
        if (siLongitudePattern == null) {//GEN-END:MVDGetBegin320
            // Insert pre-init code here
            siLongitudePattern = new StringItem("", "");//GEN-LINE:MVDGetInit320
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd320
        return siLongitudePattern;
    }//GEN-END:MVDGetEnd320
    
    /** This method returns instance for siLetters component and should be called instead of accessing siLetters field directly.//GEN-BEGIN:MVDGetBegin321
     * @return Instance for siLetters component
     */
    public StringItem get_siLetters() {
        if (siLetters == null) {//GEN-END:MVDGetBegin321
            // Insert pre-init code here
            siLetters = new StringItem("P\u00EDsmena:", "");//GEN-LINE:MVDGetInit321
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd321
        return siLetters;
    }//GEN-END:MVDGetEnd321
    
    /** This method returns instance for cmdAddPattern component and should be called instead of accessing cmdAddPattern field directly.//GEN-BEGIN:MVDGetBegin324
     * @return Instance for cmdAddPattern component
     */
    public Command get_cmdAddPattern() {
        if (cmdAddPattern == null) {//GEN-END:MVDGetBegin324
            // Insert pre-init code here
            cmdAddPattern = new Command("+Vzorec", Command.SCREEN, 1);//GEN-LINE:MVDGetInit324
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd324
        return cmdAddPattern;
    }//GEN-END:MVDGetEnd324
    /** This method returns instance for imgMultiSolver component and should be called instead of accessing imgMultiSolver field directly.//GEN-BEGIN:MVDGetBegin332
     * @return Instance for imgMultiSolver component
     */
    public Image get_imgMultiSolver() {
        if (imgMultiSolver == null) {//GEN-END:MVDGetBegin332
            // Insert pre-init code here
            try {//GEN-BEGIN:MVDGetInit332
                imgMultiSolver = Image.createImage("/dummyIcon.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit332
            imgMultiSolver = iconLoader.loadIcon("multisolver");
        }//GEN-BEGIN:MVDGetEnd332
        return imgMultiSolver;
    }//GEN-END:MVDGetEnd332
    
    /** This method returns instance for frmEditPattern component and should be called instead of accessing frmEditPattern field directly.//GEN-BEGIN:MVDGetBegin333
     * @return Instance for frmEditPattern component
     */
    public Form get_frmEditPattern() {
        if (frmEditPattern == null) {//GEN-END:MVDGetBegin333
            // Insert pre-init code here
            frmEditPattern = new Form("Upravit/P\u0159idat vzorec", new Item[] {//GEN-BEGIN:MVDGetInit333
                get_tfPatternName(),
                get_tfEditPatternLattitude(),
                get_tfEditPatternLongitude(),
                get_stringItem8()
            });
            frmEditPattern.addCommand(get_cmdBack());
            frmEditPattern.addCommand(get_cmdSave());
            frmEditPattern.setCommandListener(this);//GEN-END:MVDGetInit333
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd333
        return frmEditPattern;
    }//GEN-END:MVDGetEnd333
    
    /** This method returns instance for tfEditPatternLattitude component and should be called instead of accessing tfEditPatternLattitude field directly.//GEN-BEGIN:MVDGetBegin334
     * @return Instance for tfEditPatternLattitude component
     */
    public TextField get_tfEditPatternLattitude() {
        if (tfEditPatternLattitude == null) {//GEN-END:MVDGetBegin334
            // Insert pre-init code here
            tfEditPatternLattitude = new TextField("Vzorec sou\u0159adnic:", "N ab\u00B0 cd.efg", 120, TextField.ANY);//GEN-LINE:MVDGetInit334
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd334
        return tfEditPatternLattitude;
    }//GEN-END:MVDGetEnd334
    
    /** This method returns instance for tfEditPatternLongitude component and should be called instead of accessing tfEditPatternLongitude field directly.//GEN-BEGIN:MVDGetBegin335
     * @return Instance for tfEditPatternLongitude component
     */
    public TextField get_tfEditPatternLongitude() {
        if (tfEditPatternLongitude == null) {//GEN-END:MVDGetBegin335
            // Insert pre-init code here
            tfEditPatternLongitude = new TextField("", "E hij\u00B0 kl.mno", 120, TextField.ANY);//GEN-LINE:MVDGetInit335
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd335
        return tfEditPatternLongitude;
    }//GEN-END:MVDGetEnd335
    
    /** This method returns instance for stringItem8 component and should be called instead of accessing stringItem8 field directly.//GEN-BEGIN:MVDGetBegin336
     * @return Instance for stringItem8 component
     */
    public StringItem get_stringItem8() {
        if (stringItem8 == null) {//GEN-END:MVDGetBegin336
            // Insert pre-init code here
            stringItem8 = new StringItem("Help:", "Do vzorce m\u016F\u017Eete ps\u00E1t \u010D\u00EDsla nebo jednop\u00EDsmenn\u00E9 prom\u011Bnn\u00E9(velk\u00E1 a mal\u00E1 p\u00EDsmena anglick\u00E9 abecedy). M\u016F\u017Eete pou\u017E\u00EDvat tyto matematick\u00E9 znaky: (,),+,-,*(n\u00E1soben\u00ED),/(celo\u010D\u00EDseln\u00E9 d\u011Blen\u00ED),^(mocnina),%(modulo). \u010C\u00E1sti, kde se m\u00E1 prov\u00E1d\u011Bt n\u011Bjak\u00FD v\u00FDpo\u010Det uzav\u00EDrejte do hranat\u00FDch z\u00E1vorek.\n\nP\u0159\u00EDklad:\nN 49\u00B0 14.[A*(B+C)][D^2][A-3]\nE 014\u00B0 AB.[A*B*C/(1+E)]");//GEN-LINE:MVDGetInit336
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd336
        return stringItem8;
    }//GEN-END:MVDGetEnd336
    
    /** This method returns instance for frmAddLetter component and should be called instead of accessing frmAddLetter field directly.//GEN-BEGIN:MVDGetBegin339
     * @return Instance for frmAddLetter component
     */
    public Form get_frmAddLetter() {
        if (frmAddLetter == null) {//GEN-END:MVDGetBegin339
            // Insert pre-init code here
            frmAddLetter = new Form("P\u0159idat p\u00EDsmeno", new Item[] {//GEN-BEGIN:MVDGetInit339
                get_tfLetter(),
                get_tfValue(),
                get_stringItem9()
            });
            frmAddLetter.addCommand(get_cmdBack());
            frmAddLetter.addCommand(get_cmdSave());
            frmAddLetter.setCommandListener(this);//GEN-END:MVDGetInit339
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd339
        return frmAddLetter;
    }//GEN-END:MVDGetEnd339
    
    /** This method returns instance for cmdAddLetter component and should be called instead of accessing cmdAddLetter field directly.//GEN-BEGIN:MVDGetBegin341
     * @return Instance for cmdAddLetter component
     */
    public Command get_cmdAddLetter() {
        if (cmdAddLetter == null) {//GEN-END:MVDGetBegin341
            // Insert pre-init code here
            cmdAddLetter = new Command("+P\u00EDsmeno", Command.SCREEN, 1);//GEN-LINE:MVDGetInit341
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd341
        return cmdAddLetter;
    }//GEN-END:MVDGetEnd341
    
    /** This method returns instance for tfLetter component and should be called instead of accessing tfLetter field directly.//GEN-BEGIN:MVDGetBegin343
     * @return Instance for tfLetter component
     */
    public TextField get_tfLetter() {
        if (tfLetter == null) {//GEN-END:MVDGetBegin343
            // Insert pre-init code here
            tfLetter = new TextField("P\u00EDsmeno", null, 1, TextField.ANY | TextField.NON_PREDICTIVE);//GEN-LINE:MVDGetInit343
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd343
        return tfLetter;
    }//GEN-END:MVDGetEnd343
    
    /** This method returns instance for tfValue component and should be called instead of accessing tfValue field directly.//GEN-BEGIN:MVDGetBegin344
     * @return Instance for tfValue component
     */
    public TextField get_tfValue() {
        if (tfValue == null) {//GEN-END:MVDGetBegin344
            // Insert pre-init code here
            tfValue = new TextField("Hodnota", null, 120, TextField.NUMERIC);//GEN-LINE:MVDGetInit344
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd344
        return tfValue;
    }//GEN-END:MVDGetEnd344
    
    /** This method returns instance for stringItem9 component and should be called instead of accessing stringItem9 field directly.//GEN-BEGIN:MVDGetBegin345
     * @return Instance for stringItem9 component
     */
    public StringItem get_stringItem9() {
        if (stringItem9 == null) {//GEN-END:MVDGetBegin345
            // Insert pre-init code here
            stringItem9 = new StringItem("Pozn\u00E1mka:", "Zad\u00E1n\u00EDm ji\u017E ulo\u017Een\u00E9ho p\u00EDsmena m\u016F\u017Eete toto p\u00EDsmeno editovat.");//GEN-LINE:MVDGetInit345
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd345
        return stringItem9;
    }//GEN-END:MVDGetEnd345
    
    /** This method returns instance for cmdCompute component and should be called instead of accessing cmdCompute field directly.//GEN-BEGIN:MVDGetBegin348
     * @return Instance for cmdCompute component
     */
    public Command get_cmdCompute() {
        if (cmdCompute == null) {//GEN-END:MVDGetBegin348
            // Insert pre-init code here
            cmdCompute = new Command("Vypo\u010D\u00EDtat", Command.SCREEN, 3);//GEN-LINE:MVDGetInit348
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd348
        return cmdCompute;
    }//GEN-END:MVDGetEnd348
    
    /** This method returns instance for frmResult component and should be called instead of accessing frmResult field directly.//GEN-BEGIN:MVDGetBegin350
     * @return Instance for frmResult component
     */
    public Form get_frmResult() {
        if (frmResult == null) {//GEN-END:MVDGetBegin350
            // Insert pre-init code here
            frmResult = new Form("V\u00FDsledek", new Item[] {//GEN-BEGIN:MVDGetInit350
                get_siAfterReplacement(),
                get_tfResultLattitude(),
                get_tfResultLongitude(),
                get_tfResultName(),
                get_tfResultDescription()
            });
            frmResult.addCommand(get_cmdBack());
            frmResult.addCommand(get_cmdFavourite());
            frmResult.setCommandListener(this);//GEN-END:MVDGetInit350
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd350
        return frmResult;
    }//GEN-END:MVDGetEnd350
    
    /** This method returns instance for tfResultLattitude component and should be called instead of accessing tfResultLattitude field directly.//GEN-BEGIN:MVDGetBegin355
     * @return Instance for tfResultLattitude component
     */
    public TextField get_tfResultLattitude() {
        if (tfResultLattitude == null) {//GEN-END:MVDGetBegin355
            // Insert pre-init code here
            tfResultLattitude = new TextField("Vypo\u010Dten\u00E9 sou\u0159adnice:", null, 120, TextField.ANY);//GEN-LINE:MVDGetInit355
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd355
        return tfResultLattitude;
    }//GEN-END:MVDGetEnd355
    
    /** This method returns instance for tfResultLongitude component and should be called instead of accessing tfResultLongitude field directly.//GEN-BEGIN:MVDGetBegin356
     * @return Instance for tfResultLongitude component
     */
    public TextField get_tfResultLongitude() {
        if (tfResultLongitude == null) {//GEN-END:MVDGetBegin356
            // Insert pre-init code here
            tfResultLongitude = new TextField("", null, 120, TextField.ANY);//GEN-LINE:MVDGetInit356
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd356
        return tfResultLongitude;
    }//GEN-END:MVDGetEnd356
    
    /** This method returns instance for tfResultName component and should be called instead of accessing tfResultName field directly.//GEN-BEGIN:MVDGetBegin357
     * @return Instance for tfResultName component
     */
    public TextField get_tfResultName() {
        if (tfResultName == null) {//GEN-END:MVDGetBegin357
            // Insert pre-init code here
            tfResultName = new TextField("N\u00E1zev:", "Vypo\u010Dten\u00FD bod", 120, TextField.ANY);//GEN-LINE:MVDGetInit357
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd357
        return tfResultName;
    }//GEN-END:MVDGetEnd357
    
    /** This method returns instance for tfResultDescription component and should be called instead of accessing tfResultDescription field directly.//GEN-BEGIN:MVDGetBegin358
     * @return Instance for tfResultDescription component
     */
    public TextField get_tfResultDescription() {
        if (tfResultDescription == null) {//GEN-END:MVDGetBegin358
            // Insert pre-init code here
            tfResultDescription = new TextField("Popis:", null, 120, TextField.ANY);//GEN-LINE:MVDGetInit358
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd358
        return tfResultDescription;
    }//GEN-END:MVDGetEnd358
    
    /** This method returns instance for gaLoading component and should be called instead of accessing gaLoading field directly.//GEN-BEGIN:MVDGetBegin359
     * @return Instance for gaLoading component
     */
    public Gauge get_gaLoading() {
        if (gaLoading == null) {//GEN-END:MVDGetBegin359
            // Insert pre-init code here
            gaLoading = new Gauge("", false, 60, 0);//GEN-LINE:MVDGetInit359
            // Insert post-init code here
            gaLoading.setMaxValue(Gauge.INDEFINITE);
        }//GEN-BEGIN:MVDGetEnd359
        return gaLoading;
    }//GEN-END:MVDGetEnd359
    
    /** This method returns instance for tfBackLight component and should be called instead of accessing tfBackLight field directly.//GEN-BEGIN:MVDGetBegin361
     * @return Instance for tfBackLight component
     */
    public TextField get_tfBackLight() {
        if (tfBackLight == null) {//GEN-END:MVDGetBegin361
            // Insert pre-init code here
            tfBackLight = new TextField("Frekvence blik\u00E1n\u00ED:", null, 120, TextField.NUMERIC);//GEN-LINE:MVDGetInit361
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd361
        return tfBackLight;
    }//GEN-END:MVDGetEnd361
    
    /** This method returns instance for stringItem10 component and should be called instead of accessing stringItem10 field directly.//GEN-BEGIN:MVDGetBegin362
     * @return Instance for stringItem10 component
     */
    public StringItem get_stringItem10() {
        if (stringItem10 == null) {//GEN-END:MVDGetBegin362
            // Insert pre-init code here
            stringItem10 = new StringItem("", "Frekvence rozsvicov\u00E1n\u00ED displeje p\u0159i navigaci v sekund\u00E1ch.0=vypne blik\u00E1n\u00ED.\n ");//GEN-LINE:MVDGetInit362
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd362
        return stringItem10;
    }//GEN-END:MVDGetEnd362
    
    
    
    /** This method returns instance for cmdYes component and should be called instead of accessing cmdYes field directly.//GEN-BEGIN:MVDGetBegin366
     * @return Instance for cmdYes component
     */
    public Command get_cmdYes() {
        if (cmdYes == null) {//GEN-END:MVDGetBegin366
            // Insert pre-init code here
            cmdYes = new Command("Ano", Command.CANCEL, 1);//GEN-LINE:MVDGetInit366
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd366
        return cmdYes;
    }//GEN-END:MVDGetEnd366
    
    /** This method returns instance for cmdNo component and should be called instead of accessing cmdNo field directly.//GEN-BEGIN:MVDGetBegin367
     * @return Instance for cmdNo component
     */
    public Command get_cmdNo() {
        if (cmdNo == null) {//GEN-END:MVDGetBegin367
            // Insert pre-init code here
            cmdNo = new Command("Ne", Command.OK, 2);//GEN-LINE:MVDGetInit367
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd367
        return cmdNo;
    }//GEN-END:MVDGetEnd367
    
    
    
    /** This method returns instance for imgBluetooth component and should be called instead of accessing imgBluetooth field directly.//GEN-BEGIN:MVDGetBegin374
     * @return Instance for imgBluetooth component
     */
    public Image get_imgBluetooth() {
        if (imgBluetooth == null) {//GEN-END:MVDGetBegin374
            // Insert pre-init code here
            try {//GEN-BEGIN:MVDGetInit374
                imgBluetooth = Image.createImage("/dummyIcon.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit374
            imgBluetooth = iconLoader.loadIcon("bluetooth");
        }//GEN-BEGIN:MVDGetEnd374
        return imgBluetooth;
    }//GEN-END:MVDGetEnd374
    
    /** This method returns instance for lstPatterns component and should be called instead of accessing lstPatterns field directly.//GEN-BEGIN:MVDGetBegin375
     * @return Instance for lstPatterns component
     */
    public List get_lstPatterns() {
        if (lstPatterns == null) {//GEN-END:MVDGetBegin375
            // Insert pre-init code here
            lstPatterns = new List("Vzore\u010Dky", Choice.IMPLICIT, new String[] {"\u017D\u00E1dn\u00FD vzorec"}, new Image[] {null});//GEN-BEGIN:MVDGetInit375
            lstPatterns.addCommand(get_cmdBack());
            lstPatterns.addCommand(get_cmdAddPattern());
            lstPatterns.addCommand(get_cmdEditPattern());
            lstPatterns.addCommand(get_cmdDelete());
            lstPatterns.setCommandListener(this);
            lstPatterns.setSelectedFlags(new boolean[] {false});
            lstPatterns.setFitPolicy(Choice.TEXT_WRAP_OFF);//GEN-END:MVDGetInit375
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd375
        return lstPatterns;
    }//GEN-END:MVDGetEnd375
    
    /** This method returns instance for cmdPatterns component and should be called instead of accessing cmdPatterns field directly.//GEN-BEGIN:MVDGetBegin379
     * @return Instance for cmdPatterns component
     */
    public Command get_cmdPatterns() {
        if (cmdPatterns == null) {//GEN-END:MVDGetBegin379
            // Insert pre-init code here
            cmdPatterns = new Command("Vzore\u010Dky", Command.SCREEN, 2);//GEN-LINE:MVDGetInit379
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd379
        return cmdPatterns;
    }//GEN-END:MVDGetEnd379
    
    /** This method returns instance for cmdEditPattern component and should be called instead of accessing cmdEditPattern field directly.//GEN-BEGIN:MVDGetBegin381
     * @return Instance for cmdEditPattern component
     */
    public Command get_cmdEditPattern() {
        if (cmdEditPattern == null) {//GEN-END:MVDGetBegin381
            // Insert pre-init code here
            cmdEditPattern = new Command("Upr.vzorec", Command.SCREEN, 2);//GEN-LINE:MVDGetInit381
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd381
        return cmdEditPattern;
    }//GEN-END:MVDGetEnd381
    
    /** This method returns instance for tfPatternName component and should be called instead of accessing tfPatternName field directly.//GEN-BEGIN:MVDGetBegin385
     * @return Instance for tfPatternName component
     */
    public TextField get_tfPatternName() {
        if (tfPatternName == null) {//GEN-END:MVDGetBegin385
            // Insert pre-init code here
            tfPatternName = new TextField("N\u00E1zev:", "Beze jm\u00E9na", 120, TextField.ANY);//GEN-LINE:MVDGetInit385
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd385
        return tfPatternName;
    }//GEN-END:MVDGetEnd385
    
    /** This method returns instance for siAfterReplacement component and should be called instead of accessing siAfterReplacement field directly.//GEN-BEGIN:MVDGetBegin386
     * @return Instance for siAfterReplacement component
     */
    public StringItem get_siAfterReplacement() {
        if (siAfterReplacement == null) {//GEN-END:MVDGetBegin386
            // Insert pre-init code here
            siAfterReplacement = new StringItem("Po nahrazen\u00ED p\u00EDsmen:", "");//GEN-LINE:MVDGetInit386
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd386
        return siAfterReplacement;
    }//GEN-END:MVDGetEnd386
    
    /** This method returns instance for cmdDownloadPatterns component and should be called instead of accessing cmdDownloadPatterns field directly.//GEN-BEGIN:MVDGetBegin387
     * @return Instance for cmdDownloadPatterns component
     */
    public Command get_cmdDownloadPatterns() {
        if (cmdDownloadPatterns == null) {//GEN-END:MVDGetBegin387
            // Insert pre-init code here
            cmdDownloadPatterns = new Command("Vzore\u010Dky", Command.SCREEN, 9);//GEN-LINE:MVDGetInit387
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd387
        return cmdDownloadPatterns;
    }//GEN-END:MVDGetEnd387
    
    
    /** This method returns instance for siAverageLongitude component and should be called instead of accessing siAverageLongitude field directly.//GEN-BEGIN:MVDGetBegin392
     * @return Instance for siAverageLongitude component
     */
    public StringItem get_siAverageLongitude() {
        if (siAverageLongitude == null) {//GEN-END:MVDGetBegin392
            // Insert pre-init code here
            siAverageLongitude = new StringItem("", "");//GEN-LINE:MVDGetInit392
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd392
        return siAverageLongitude;
    }//GEN-END:MVDGetEnd392
    
    /** This method returns instance for tbError component and should be called instead of accessing tbError field directly.//GEN-BEGIN:MVDGetBegin393
     * @return Instance for tbError component
     */
    public TextBox get_tbError() {
        if (tbError == null) {//GEN-END:MVDGetBegin393
            // Insert pre-init code here
            tbError = new TextBox("Nastala chyba", null, 120, TextField.ANY);//GEN-BEGIN:MVDGetInit393
            tbError.addCommand(get_cmdMenu());
            tbError.setCommandListener(this);//GEN-END:MVDGetInit393
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd393
        return tbError;
    }//GEN-END:MVDGetEnd393
    
    /** This method returns instance for fntSmallBold component and should be called instead of accessing fntSmallBold field directly.//GEN-BEGIN:MVDGetBegin395
     * @return Instance for fntSmallBold component
     */
    public Font get_fntSmallBold() {
        if (fntSmallBold == null) {//GEN-END:MVDGetBegin395
            // Insert pre-init code here
            fntSmallBold = Font.getFont(Font.FACE_SYSTEM, 0x1, Font.SIZE_SMALL);//GEN-LINE:MVDGetInit395
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd395
        return fntSmallBold;
    }//GEN-END:MVDGetEnd395
    
    /** This method returns instance for cmdBegin component and should be called instead of accessing cmdBegin field directly.//GEN-BEGIN:MVDGetBegin396
     * @return Instance for cmdBegin component
     */
    public Command get_cmdBegin() {
        if (cmdBegin == null) {//GEN-END:MVDGetBegin396
            // Insert pre-init code here
            cmdBegin = new Command("Na za\u010D\u00E1tek", Command.SCREEN, 1);//GEN-LINE:MVDGetInit396
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd396
        return cmdBegin;
    }//GEN-END:MVDGetEnd396
    
    /** This method returns instance for cmdEnd component and should be called instead of accessing cmdEnd field directly.//GEN-BEGIN:MVDGetBegin397
     * @return Instance for cmdEnd component
     */
    public Command get_cmdEnd() {
        if (cmdEnd == null) {//GEN-END:MVDGetBegin397
            // Insert pre-init code here
            cmdEnd = new Command("Na konec", Command.SCREEN, 2);//GEN-LINE:MVDGetInit397
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd397
        return cmdEnd;
    }//GEN-END:MVDGetEnd397
    
    /** This method returns instance for siContent component and should be called instead of accessing siContent field directly.//GEN-BEGIN:MVDGetBegin401
     * @return Instance for siContent component
     */
    public StringItem get_siContent() {
        if (siContent == null) {//GEN-END:MVDGetBegin401
            // Insert pre-init code here
            siContent = new StringItem("", "");//GEN-LINE:MVDGetInit401
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd401
        return siContent;
    }//GEN-END:MVDGetEnd401
    
    /** This method returns instance for siEnd component and should be called instead of accessing siEnd field directly.//GEN-BEGIN:MVDGetBegin402
     * @return Instance for siEnd component
     */
    public StringItem get_siEnd() {
        if (siEnd == null) {//GEN-END:MVDGetBegin402
            // Insert pre-init code here
            siEnd = new StringItem("", "--KONEC--");//GEN-LINE:MVDGetInit402
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd402
        return siEnd;
    }//GEN-END:MVDGetEnd402
    
    /** This method returns instance for siBegin component and should be called instead of accessing siBegin field directly.//GEN-BEGIN:MVDGetBegin403
     * @return Instance for siBegin component
     */
    public StringItem get_siBegin() {
        if (siBegin == null) {//GEN-END:MVDGetBegin403
            // Insert pre-init code here
            siBegin = new StringItem("", " ");//GEN-LINE:MVDGetInit403
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd403
        return siBegin;
    }//GEN-END:MVDGetEnd403
    
    /** This method returns instance for cmdFavourites component and should be called instead of accessing cmdFavourites field directly.//GEN-BEGIN:MVDGetBegin405
     * @return Instance for cmdFavourites component
     */
    public Command get_cmdFavourites() {
        if (cmdFavourites == null) {//GEN-END:MVDGetBegin405
            // Insert pre-init code here
            cmdFavourites = new Command("Obl\u00EDben\u00E9", Command.SCREEN, 6);//GEN-LINE:MVDGetInit405
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd405
        return cmdFavourites;
    }//GEN-END:MVDGetEnd405
    
    /** This method returns instance for cmdMultiSolver component and should be called instead of accessing cmdMultiSolver field directly.//GEN-BEGIN:MVDGetBegin407
     * @return Instance for cmdMultiSolver component
     */
    public Command get_cmdMultiSolver() {
        if (cmdMultiSolver == null) {//GEN-END:MVDGetBegin407
            // Insert pre-init code here
            cmdMultiSolver = new Command("MultiSolver", Command.SCREEN, 9);//GEN-LINE:MVDGetInit407
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd407
        return cmdMultiSolver;
    }//GEN-END:MVDGetEnd407
    
    /** This method returns instance for cmdRefresh component and should be called instead of accessing cmdRefresh field directly.//GEN-BEGIN:MVDGetBegin409
     * @return Instance for cmdRefresh component
     */
    public Command get_cmdRefresh() {
        if (cmdRefresh == null) {//GEN-END:MVDGetBegin409
            // Insert pre-init code here
            cmdRefresh = new Command("Obnovit", Command.SCREEN, 12);//GEN-LINE:MVDGetInit409
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd409
        return cmdRefresh;
    }//GEN-END:MVDGetEnd409
    
    /** This method returns instance for imgDecypher component and should be called instead of accessing imgDecypher field directly.//GEN-BEGIN:MVDGetBegin413
     * @return Instance for imgDecypher component
     */
    public Image get_imgDecypher() {
        if (imgDecypher == null) {//GEN-END:MVDGetBegin413
            // Insert pre-init code here
            try {//GEN-BEGIN:MVDGetInit413
                imgDecypher = Image.createImage("/dummyIcon.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit413
            imgDecypher = iconLoader.loadIcon("decypher");
        }//GEN-BEGIN:MVDGetEnd413
        return imgDecypher;
    }//GEN-END:MVDGetEnd413
    
    /** This method returns instance for tbDecypher component and should be called instead of accessing tbDecypher field directly.//GEN-BEGIN:MVDGetBegin415
     * @return Instance for tbDecypher component
     */
    public TextBox get_tbDecypher() {
        if (tbDecypher == null) {//GEN-END:MVDGetBegin415
            // Insert pre-init code here
            tbDecypher = new TextBox("De\u0161ifr\u00E1tor", null, 1200, TextField.ANY);//GEN-BEGIN:MVDGetInit415
            tbDecypher.addCommand(get_cmdDecypher());
            tbDecypher.addCommand(get_cmdBack());
            tbDecypher.setCommandListener(this);//GEN-END:MVDGetInit415
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd415
        return tbDecypher;
    }//GEN-END:MVDGetEnd415
    
    /** This method returns instance for cmdDecypher component and should be called instead of accessing cmdDecypher field directly.//GEN-BEGIN:MVDGetBegin416
     * @return Instance for cmdDecypher component
     */
    public Command get_cmdDecypher() {
        if (cmdDecypher == null) {//GEN-END:MVDGetBegin416
            // Insert pre-init code here
            cmdDecypher = new Command("De\u0161ifruj", Command.SCREEN, 1);//GEN-LINE:MVDGetInit416
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd416
        return cmdDecypher;
    }//GEN-END:MVDGetEnd416
    
    /** This method returns instance for cmdMap component and should be called instead of accessing cmdMap field directly.//GEN-BEGIN:MVDGetBegin419
     * @return Instance for cmdMap component
     */
    public Command get_cmdMap() {
        if (cmdMap == null) {//GEN-END:MVDGetBegin419
            // Insert pre-init code here
            cmdMap = new Command("Mapa", Command.SCREEN, 2);//GEN-LINE:MVDGetInit419
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd419
        return cmdMap;
    }//GEN-END:MVDGetEnd419
    
    /** This method returns instance for frmConnectionHelp component and should be called instead of accessing frmConnectionHelp field directly.//GEN-BEGIN:MVDGetBegin424
     * @return Instance for frmConnectionHelp component
     */
    public Form get_frmConnectionHelp() {
        if (frmConnectionHelp == null) {//GEN-END:MVDGetBegin424
            // Insert pre-init code here
            frmConnectionHelp = new Form("N\u00E1pov\u011Bda", new Item[] {get_stringItem12()});//GEN-BEGIN:MVDGetInit424
            frmConnectionHelp.addCommand(get_cmdBack());
            frmConnectionHelp.setCommandListener(this);//GEN-END:MVDGetInit424
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd424
        return frmConnectionHelp;
    }//GEN-END:MVDGetEnd424
    
    /** This method returns instance for stringItem12 component and should be called instead of accessing stringItem12 field directly.//GEN-BEGIN:MVDGetBegin425
     * @return Instance for stringItem12 component
     */
    public StringItem get_stringItem12() {
        if (stringItem12 == null) {//GEN-END:MVDGetBegin425
            // Insert pre-init code here
            stringItem12 = new StringItem("", "Program m\u016F\u017Eete s GPS p\u0159ipojit n\u011Bkolika zp\u016Fsoby. P\u0159i volb\u011B \'Bluetooth GPS\' mus\u00EDte vlastnit Bluetooth GPS modul a v\u00E1\u0161 telefon mus\u00ED podporovat Bluetooth API (JSR-82).\n\nP\u0159i volb\u011B \'Intern\u00ED GPS\' se m\u016F\u017Eete p\u0159ipojit k intern\u00ED GPS, kterou maj\u00ED n\u011Bkter\u00E9 telefony jako nap\u0159.Nokia N95. V\u00E1\u0161 telefon mus\u00ED podporovat Location API (JSR-179).\n\nVolba \'PDA GPS\' V\u00E1m zp\u0159\u00EDstupn\u00ED GPSku na PDA. Mus\u00EDte m\u00EDt ale spu\u0161t\u011Bn speci\u00E1ln\u00ED propojovac\u00ED software, v\u00EDce na str\u00E1nk\u00E1ch aplikace.\n\nVolba \'Bez GPS\' nevy\u017Eaduje \u017E\u00E1dn\u00E9 p\u0159ipojen\u00ED, av\u0161ak mus\u00EDte se obej\u00EDt bez v\u0161ech lokaliza\u010Dn\u00EDch a naviga\u010Dn\u00EDch funkc\u00ED.");//GEN-LINE:MVDGetInit425
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd425
        return stringItem12;
    }//GEN-END:MVDGetEnd425
    
    
    /** This method returns instance for imgWaypoint component and should be called instead of accessing imgWaypoint field directly.//GEN-BEGIN:MVDGetBegin431
     * @return Instance for imgWaypoint component
     */
    public Image get_imgWaypoint() {
        if (imgWaypoint == null) {//GEN-END:MVDGetBegin431
            // Insert pre-init code here
            try {//GEN-BEGIN:MVDGetInit431
                imgWaypoint = Image.createImage("/dummyIcon.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit431
            imgWaypoint = iconLoader.loadIcon("srch_id");
        }//GEN-BEGIN:MVDGetEnd431
        return imgWaypoint;
    }//GEN-END:MVDGetEnd431
    
    
    
    
    /** This method returns instance for frmDebug1 component and should be called instead of accessing frmDebug1 field directly.//GEN-BEGIN:MVDGetBegin443
     * @return Instance for frmDebug1 component
     */
    public Form get_frmDebug1() {
        if (frmDebug1 == null) {//GEN-END:MVDGetBegin443
            // Insert pre-init code here
            frmDebug1 = new Form("Debug", new Item[] {//GEN-BEGIN:MVDGetInit443
                get_siPart(),
                get_siDebug1()
            });
            frmDebug1.addCommand(get_cmdBack());
            frmDebug1.setCommandListener(this);//GEN-END:MVDGetInit443
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd443
        return frmDebug1;
    }//GEN-END:MVDGetEnd443
    
    /** This method returns instance for siPart component and should be called instead of accessing siPart field directly.//GEN-BEGIN:MVDGetBegin450
     * @return Instance for siPart component
     */
    public StringItem get_siPart() {
        if (siPart == null) {//GEN-END:MVDGetBegin450
            // Insert pre-init code here
            siPart = new StringItem("Part", "<Enter Text>");//GEN-LINE:MVDGetInit450
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd450
        return siPart;
    }//GEN-END:MVDGetEnd450
    
    /** This method returns instance for siDebug1 component and should be called instead of accessing siDebug1 field directly.//GEN-BEGIN:MVDGetBegin451
     * @return Instance for siDebug1 component
     */
    public StringItem get_siDebug1() {
        if (siDebug1 == null) {//GEN-END:MVDGetBegin451
            // Insert pre-init code here
            siDebug1 = new StringItem("Debug", "<Enter Text>");//GEN-LINE:MVDGetInit451
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd451
        return siDebug1;
    }//GEN-END:MVDGetEnd451
    
    /** This method returns instance for cmdMapyCz component and should be called instead of accessing cmdMapyCz field directly.//GEN-BEGIN:MVDGetBegin452
     * @return Instance for cmdMapyCz component
     */
    public Command get_cmdMapyCz() {
        if (cmdMapyCz == null) {//GEN-END:MVDGetBegin452
            // Insert pre-init code here
            cmdMapyCz = new Command("Mapy.cz", Command.SCREEN, 3);//GEN-LINE:MVDGetInit452
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd452
        return cmdMapyCz;
    }//GEN-END:MVDGetEnd452
    
    /** This method returns instance for imgPdaGps component and should be called instead of accessing imgPdaGps field directly.//GEN-BEGIN:MVDGetBegin454
     * @return Instance for imgPdaGps component
     */
    public Image get_imgPdaGps() {
        if (imgPdaGps == null) {//GEN-END:MVDGetBegin454
            // Insert pre-init code here
            try {//GEN-BEGIN:MVDGetInit454
                imgPdaGps = Image.createImage("/dummyIcon.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit454
            imgPdaGps = iconLoader.loadIcon("pdagps");
// Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd454
        return imgPdaGps;
    }//GEN-END:MVDGetEnd454
    
    /** This method returns instance for imgOther component and should be called instead of accessing imgOther field directly.//GEN-BEGIN:MVDGetBegin455
     * @return Instance for imgOther component
     */
    public Image get_imgOther() {
        if (imgOther == null) {//GEN-END:MVDGetBegin455
            // Insert pre-init code here
            try {//GEN-BEGIN:MVDGetInit455
                imgOther = Image.createImage("/dummyIcon.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit455
            // Insert post-init code here
            imgOther = iconLoader.loadIcon("other_tools");
        }//GEN-BEGIN:MVDGetEnd455
        return imgOther;
    }//GEN-END:MVDGetEnd455
    
    /** This method returns instance for cgGivenFormat component and should be called instead of accessing cgGivenFormat field directly.//GEN-BEGIN:MVDGetBegin456
     * @return Instance for cgGivenFormat component
     */
    public ChoiceGroup get_cgGivenFormat() {
        if (cgGivenFormat == null) {//GEN-END:MVDGetBegin456
            // Insert pre-init code here
            cgGivenFormat = new ChoiceGroup("Form\u00E1t:", Choice.EXCLUSIVE, new String[] {//GEN-BEGIN:MVDGetInit456
                "Stupn\u011B, Min.",
                "Stupn\u011B, Min., Sec"
            }, new Image[] {
                null,
                null
            });
            cgGivenFormat.setSelectedFlags(new boolean[] {
                false,
                false
            });
            cgGivenFormat.setFitPolicy(Choice.TEXT_WRAP_ON);//GEN-END:MVDGetInit456
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd456
        return cgGivenFormat;
    }//GEN-END:MVDGetEnd456
    
    
    
    /** This method returns instance for frmGpsSignalHelp component and should be called instead of accessing frmGpsSignalHelp field directly.//GEN-BEGIN:MVDGetBegin479
     * @return Instance for frmGpsSignalHelp component
     */
    public Form get_frmGpsSignalHelp() {
        if (frmGpsSignalHelp == null) {//GEN-END:MVDGetBegin479
            // Insert pre-init code here
            frmGpsSignalHelp = new Form(null, new Item[] {//GEN-BEGIN:MVDGetInit479
                get_siSouradnice(),
                get_siRychlost(),
                get_siVyska(),
                get_siPresnost(),
                get_siPravdepodobnost(),
                get_siSat()
            });
            frmGpsSignalHelp.addCommand(get_cmdBack());
            frmGpsSignalHelp.setCommandListener(this);//GEN-END:MVDGetInit479
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd479
        return frmGpsSignalHelp;
    }//GEN-END:MVDGetEnd479
    
    /** This method returns instance for siSouradnice component and should be called instead of accessing siSouradnice field directly.//GEN-BEGIN:MVDGetBegin480
     * @return Instance for siSouradnice component
     */
    public StringItem get_siSouradnice() {
        if (siSouradnice == null) {//GEN-END:MVDGetBegin480
            // Insert pre-init code here
            siSouradnice = new StringItem("Lat. - zem\u011Bpisn\u00E1 \u0161\u00ED\u0159ka\nLon. - zem\u011Bpisn\u00E1 d\u00E9lka", "Sou\u0159adnice aktu\u00E1ln\u00ED pozice. Mus\u00ED b\u00FDt aktivn\u00ED alespo\u0148 t\u0159i satelity");//GEN-LINE:MVDGetInit480
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd480
        return siSouradnice;
    }//GEN-END:MVDGetEnd480
    
    /** This method returns instance for siRychlost component and should be called instead of accessing siRychlost field directly.//GEN-BEGIN:MVDGetBegin482
     * @return Instance for siRychlost component
     */
    public StringItem get_siRychlost() {
        if (siRychlost == null) {//GEN-END:MVDGetBegin482
            // Insert pre-init code here
            siRychlost = new StringItem("\nRychl. - Rychlost", "Aktu\u00E1ln\u00ED rychlost pohybu v kilometrech za hodinu. Mus\u00ED b\u00FDt aktivn\u00ED alespo\u0148 \u010Dty\u0159i satelity.");//GEN-LINE:MVDGetInit482
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd482
        return siRychlost;
    }//GEN-END:MVDGetEnd482
    
    /** This method returns instance for siVyska component and should be called instead of accessing siVyska field directly.//GEN-BEGIN:MVDGetBegin483
     * @return Instance for siVyska component
     */
    public StringItem get_siVyska() {
        if (siVyska == null) {//GEN-END:MVDGetBegin483
            // Insert pre-init code here
            siVyska = new StringItem("\nV\u00FD\u0161ka", "Aktu\u00E1ln\u00ED nadmo\u0159sk\u00E1 v\u00FD\u0161ka v metrech. Mus\u00ED b\u00FDt aktivn\u00ED alespo\u0148 t\u0159i satelity.");//GEN-LINE:MVDGetInit483
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd483
        return siVyska;
    }//GEN-END:MVDGetEnd483
    
    /** This method returns instance for siPresnost component and should be called instead of accessing siPresnost field directly.//GEN-BEGIN:MVDGetBegin484
     * @return Instance for siPresnost component
     */
    public StringItem get_siPresnost() {
        if (siPresnost == null) {//GEN-END:MVDGetBegin484
            // Insert pre-init code here
            siPresnost = new StringItem("\nP/H/V/DOP - rozptyl p\u0159esnosti m\u011B\u0159en\u00ED", "P - pozi\u010Dn\u00ED, H - horizont\u00E1ln\u00ED, V - vertik\u00E1ln\u00ED. Obecn\u011B se d\u00E1 \u0159\u00EDci \u017Ee \u010D\u00EDm bl\u00ED\u017E jsou tyto hodnoty rovny jedni\u010Dce, t\u00EDm je ur\u010Den\u00E1 pozice GPS p\u0159esn\u011Bj\u0161\u00ED.");//GEN-LINE:MVDGetInit484
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd484
        return siPresnost;
    }//GEN-END:MVDGetEnd484
    
    /** This method returns instance for siPravdepodobnost component and should be called instead of accessing siPravdepodobnost field directly.//GEN-BEGIN:MVDGetBegin485
     * @return Instance for siPravdepodobnost component
     */
    public StringItem get_siPravdepodobnost() {
        if (siPravdepodobnost == null) {//GEN-END:MVDGetBegin485
            // Insert pre-init code here
            siPravdepodobnost = new StringItem("\nPravd\u011Bpodobnost p\u0159ipojen\u00ED", "Sloupec pod tabulkou ze s\u00EDlou sign\u00E1lu. Pr\u016Fm\u011Brn\u00E1 s\u00EDla sign\u00E1lu ze t\u0159\u00ED nejsiln\u011Bj\u0161ich satelit\u016F. \u010Cerven\u00E1-slab\u00FD sign\u00E1l, Oran\u017Eov\u00E1-st\u0159edn\u00ED \u0161ance na p\u0159ipojen\u00ED, Zelen\u00E1 - dobr\u00FD sign\u00E1l. \u010C\u00EDm vy\u0161\u0161\u00ED sloupec, t\u00EDm je pravd\u011Bpodobnost p\u0159ipojen\u00ED vy\u0161\u0161\u00ED. \u010C\u00EDseln\u011B je tato hodnota vyj\u00E1d\u0159ena za \"3:\" pro t\u0159i satelity a \"4:\" pro \u010Dty\u0159i satelity.");//GEN-LINE:MVDGetInit485
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd485
        return siPravdepodobnost;
    }//GEN-END:MVDGetEnd485
    
    /** This method returns instance for siSat component and should be called instead of accessing siSat field directly.//GEN-BEGIN:MVDGetBegin486
     * @return Instance for siSat component
     */
    public StringItem get_siSat() {
        if (siSat == null) {//GEN-END:MVDGetBegin486
            // Insert pre-init code here
            siSat = new StringItem("\nSat.", "Po\u010Det aktivn\u00EDch / viditeln\u00FDch satelit\u016F.");//GEN-LINE:MVDGetInit486
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd486
        return siSat;
    }//GEN-END:MVDGetEnd486
    
    /** This method returns instance for siNalezeno component and should be called instead of accessing siNalezeno field directly.//GEN-BEGIN:MVDGetBegin492
     * @return Instance for siNalezeno component
     */
    public StringItem get_siNalezeno() {
        if (siNalezeno == null) {//GEN-END:MVDGetBegin492
            // Insert pre-init code here
            siNalezeno = new StringItem("Nalezeno", "NE");//GEN-LINE:MVDGetInit492
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd492
        return siNalezeno;
    }//GEN-END:MVDGetEnd492
    
    /** This method returns instance for siNalezenoOver component and should be called instead of accessing siNalezenoOver field directly.//GEN-BEGIN:MVDGetBegin494
     * @return Instance for siNalezenoOver component
     */
    public StringItem get_siNalezenoOver() {
        if (siNalezenoOver == null) {//GEN-END:MVDGetBegin494
            // Insert pre-init code here
            siNalezenoOver = new StringItem("Nalezeno:", "");//GEN-LINE:MVDGetInit494
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd494
        return siNalezenoOver;
    }//GEN-END:MVDGetEnd494
    
    /** This method returns instance for frmNalezeno component and should be called instead of accessing frmNalezeno field directly.//GEN-BEGIN:MVDGetBegin495
     * @return Instance for frmNalezeno component
     */
    public Form get_frmNalezeno() {
        if (frmNalezeno == null) {//GEN-END:MVDGetBegin495
            // Insert pre-init code here
            frmNalezeno = new Form("Nalezeno", new Item[] {//GEN-BEGIN:MVDGetInit495
                get_siNazevKese(),
                get_dfNalezeno()
            });
            frmNalezeno.addCommand(get_cmdBack());
            frmNalezeno.addCommand(get_cmdNastavit());
            frmNalezeno.setCommandListener(this);//GEN-END:MVDGetInit495
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd495
        return frmNalezeno;
    }//GEN-END:MVDGetEnd495
    
    /** This method returns instance for siNazevKese component and should be called instead of accessing siNazevKese field directly.//GEN-BEGIN:MVDGetBegin496
     * @return Instance for siNazevKese component
     */
    public StringItem get_siNazevKese() {
        if (siNazevKese == null) {//GEN-END:MVDGetBegin496
            // Insert pre-init code here
            siNazevKese = new StringItem("Nazev", "");//GEN-LINE:MVDGetInit496
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd496
        return siNazevKese;
    }//GEN-END:MVDGetEnd496
    
    /** This method returns instance for dfNalezeno component and should be called instead of accessing dfNalezeno field directly.//GEN-BEGIN:MVDGetBegin497
     * @return Instance for dfNalezeno component
     */
    public DateField get_dfNalezeno() {
        if (dfNalezeno == null) {//GEN-END:MVDGetBegin497
            // Insert pre-init code here
            dfNalezeno = new DateField("Nalezeno", DateField.DATE_TIME);//GEN-LINE:MVDGetInit497
            // Insert post-init code here
            dfNalezeno.setDate(new Date());
        }//GEN-BEGIN:MVDGetEnd497
        return dfNalezeno;
    }//GEN-END:MVDGetEnd497
    
    /** This method returns instance for cmdNastavit component and should be called instead of accessing cmdNastavit field directly.//GEN-BEGIN:MVDGetBegin504
     * @return Instance for cmdNastavit component
     */
    public Command get_cmdNastavit() {
        if (cmdNastavit == null) {//GEN-END:MVDGetBegin504
            // Insert pre-init code here
            cmdNastavit = new Command("Nastavit", Command.OK, 1);//GEN-LINE:MVDGetInit504
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd504
        return cmdNastavit;
    }//GEN-END:MVDGetEnd504
    
    /** This method returns instance for cmdAddFieldNotes component and should be called instead of accessing cmdAddFieldNotes field directly.//GEN-BEGIN:MVDGetBegin506
     * @return Instance for cmdAddFieldNotes component
     */
    public Command get_cmdAddFieldNotes() {
        if (cmdAddFieldNotes == null) {//GEN-END:MVDGetBegin506
            // Insert pre-init code here
            cmdAddFieldNotes = new Command("+Field note", Command.SCREEN, 10);//GEN-LINE:MVDGetInit506
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd506
        return cmdAddFieldNotes;
    }//GEN-END:MVDGetEnd506
    /** This method returns instance for siNalezeno1 component and should be called instead of accessing siNalezeno1 field directly.//GEN-BEGIN:MVDGetBegin510
     * @return Instance for siNalezeno1 component
     */
    public StringItem get_siNalezeno1() {
        if (siNalezeno1 == null) {//GEN-END:MVDGetBegin510
            // Insert pre-init code here
            siNalezeno1 = new StringItem("Nalezeno:", "");//GEN-LINE:MVDGetInit510
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd510
        return siNalezeno1;
    }//GEN-END:MVDGetEnd510
    
    /** This method returns instance for siSestaveni component and should be called instead of accessing siSestaveni field directly.//GEN-BEGIN:MVDGetBegin514
     * @return Instance for siSestaveni component
     */
    public StringItem get_siSestaveni() {
        if (siSestaveni == null) {//GEN-END:MVDGetBegin514
            // Insert pre-init code here
            siSestaveni = new StringItem("Speci\u00E1ln\u00ED sestaven\u00ED:", "");//GEN-LINE:MVDGetInit514
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd514
        return siSestaveni;
    }//GEN-END:MVDGetEnd514
    
    /** This method returns instance for tbPoznamka component and should be called instead of accessing tbPoznamka field directly.//GEN-BEGIN:MVDGetBegin515
     * @return Instance for tbPoznamka component
     */
    public TextBox get_tbPoznamka() {
        if (tbPoznamka == null) {//GEN-END:MVDGetBegin515
            // Insert pre-init code here
            tbPoznamka = new TextBox("Pozn\u00E1mka pro ...", null, 8192, TextField.ANY);//GEN-BEGIN:MVDGetInit515
            tbPoznamka.addCommand(get_cmdSave());
            tbPoznamka.addCommand(get_cmdBack());
            tbPoznamka.setCommandListener(this);//GEN-END:MVDGetInit515
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd515
        return tbPoznamka;
    }//GEN-END:MVDGetEnd515
    
    /** This method returns instance for cmdPoznamka component and should be called instead of accessing cmdPoznamka field directly.//GEN-BEGIN:MVDGetBegin520
     * @return Instance for cmdPoznamka component
     */
    public Command get_cmdPoznamka() {
        if (cmdPoznamka == null) {//GEN-END:MVDGetBegin520
            // Insert pre-init code here
            cmdPoznamka = new Command("Pozn\u00E1mka", Command.SCREEN, 10);//GEN-LINE:MVDGetInit520
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd520
        return cmdPoznamka;
    }//GEN-END:MVDGetEnd520

    /** This method returns instance for siPoznamka component and should be called instead of accessing siPoznamka field directly.//GEN-BEGIN:MVDGetBegin522
     * @return Instance for siPoznamka component
     */
    public StringItem get_siPoznamka() {
        if (siPoznamka == null) {//GEN-END:MVDGetBegin522
            // Insert pre-init code here
            siPoznamka = new StringItem("Pozn\u00E1mka:", "");//GEN-LINE:MVDGetInit522
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd522
        return siPoznamka;
    }//GEN-END:MVDGetEnd522

    /** This method returns instance for cmdAdd component and should be called instead of accessing cmdAdd field directly.//GEN-BEGIN:MVDGetBegin523
     * @return Instance for cmdAdd component
     */
    public Command get_cmdAdd() {
        if (cmdAdd == null) {//GEN-END:MVDGetBegin523
            // Insert pre-init code here
            cmdAdd = new Command("P\u0159idat", Command.SCREEN, 1);//GEN-LINE:MVDGetInit523
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd523
        return cmdAdd;
    }//GEN-END:MVDGetEnd523

    /** This method returns instance for siPoznamkaOver component and should be called instead of accessing siPoznamkaOver field directly.//GEN-BEGIN:MVDGetBegin525
     * @return Instance for siPoznamkaOver component
     */
    public StringItem get_siPoznamkaOver() {
        if (siPoznamkaOver == null) {//GEN-END:MVDGetBegin525
            // Insert pre-init code here
            siPoznamkaOver = new StringItem("Pozn\u00E1mka:", "");//GEN-LINE:MVDGetInit525
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd525
        return siPoznamkaOver;
    }//GEN-END:MVDGetEnd525

    /** This method returns instance for fntLargeBold component and should be called instead of accessing fntLargeBold field directly.//GEN-BEGIN:MVDGetBegin527
     * @return Instance for fntLargeBold component
     */
    public Font get_fntLargeBold() {
        if (fntLargeBold == null) {//GEN-END:MVDGetBegin527
            // Insert pre-init code here
            fntLargeBold = Font.getFont(Font.FACE_SYSTEM, 0x1, Font.SIZE_LARGE);//GEN-LINE:MVDGetInit527
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd527
        return fntLargeBold;
    }//GEN-END:MVDGetEnd527

    /** This method returns instance for lstFieldNotes component and should be called instead of accessing lstFieldNotes field directly.//GEN-BEGIN:MVDGetBegin542
     * @return Instance for lstFieldNotes component
     */
    public List get_lstFieldNotes() {
        if (lstFieldNotes == null) {//GEN-END:MVDGetBegin542
            // Insert pre-init code here
            lstFieldNotes = new List("Field notes", Choice.MULTIPLE, new String[0], new Image[0]);//GEN-BEGIN:MVDGetInit542
            lstFieldNotes.addCommand(get_cmdBack());
            lstFieldNotes.addCommand(get_cmdAdd());
            lstFieldNotes.addCommand(get_cmdEdit());
            lstFieldNotes.addCommand(get_cmdDelete());
            lstFieldNotes.addCommand(get_cmdDeleteAll());
            lstFieldNotes.addCommand(get_cmdPostFieldNotes());
            lstFieldNotes.setCommandListener(this);
            lstFieldNotes.setSelectedFlags(new boolean[0]);
            lstFieldNotes.setSelectCommand(null);
            lstFieldNotes.setFitPolicy(Choice.TEXT_WRAP_DEFAULT);//GEN-END:MVDGetInit542
            // Insert post-init code here
            lstFieldNotes.setFitPolicy((settings.wrappedFieldNotesList)? Choice.TEXT_WRAP_ON : Choice.TEXT_WRAP_OFF);
        }//GEN-BEGIN:MVDGetEnd542
        return lstFieldNotes;
    }//GEN-END:MVDGetEnd542
 
    /** This method returns instance for imgFieldNotes component and should be called instead of accessing imgFieldNotes field directly.//GEN-BEGIN:MVDGetBegin549
     * @return Instance for imgFieldNotes component
     */
    public Image get_imgFieldNotes() {
        if (imgFieldNotes == null) {//GEN-END:MVDGetBegin549
            // Insert pre-init code here
            try {//GEN-BEGIN:MVDGetInit549
                imgFieldNotes = Image.createImage("/dummyIcon.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit549
            // Insert post-init code here
            imgFieldNotes = iconLoader.loadIcon("gc_event");
        }//GEN-BEGIN:MVDGetEnd549
        return imgFieldNotes;
    }//GEN-END:MVDGetEnd549

    /** This method returns instance for frmFieldNote component and should be called instead of accessing frmFieldNote field directly.//GEN-BEGIN:MVDGetBegin550
     * @return Instance for frmFieldNote component
     */
    public Form get_frmFieldNote() {
        if (frmFieldNote == null) {//GEN-END:MVDGetBegin550
            // Insert pre-init code here
            frmFieldNote = new Form("Field notes", new Item[] {//GEN-BEGIN:MVDGetInit550
                get_siFNGcCode(),
                get_tfFNGcCode(),
                get_cgFNType(),
                get_dtFNDate(),
                get_tfFNText()
            });
            frmFieldNote.addCommand(get_cmdBack());
            frmFieldNote.addCommand(get_cmdSave());
            frmFieldNote.setCommandListener(this);//GEN-END:MVDGetInit550
            // Insert post-init code here
            frmFieldNote.setItemStateListener(this);
        }//GEN-BEGIN:MVDGetEnd550
        return frmFieldNote;
    }//GEN-END:MVDGetEnd550
 

    /** This method returns instance for siFNGcCode component and should be called instead of accessing siFNGcCode field directly.//GEN-BEGIN:MVDGetBegin556
     * @return Instance for siFNGcCode component
     */
    public StringItem get_siFNGcCode() {
        if (siFNGcCode == null) {//GEN-END:MVDGetBegin556
            // Insert pre-init code here
            siFNGcCode = new StringItem("K\u00F3d ke\u0161e:", "GC12345");//GEN-LINE:MVDGetInit556
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd556
        return siFNGcCode;
    }//GEN-END:MVDGetEnd556

    /** This method returns instance for tfFNGcCode component and should be called instead of accessing tfFNGcCode field directly.//GEN-BEGIN:MVDGetBegin557
     * @return Instance for tfFNGcCode component
     */
    public TextField get_tfFNGcCode() {
        if (tfFNGcCode == null) {//GEN-END:MVDGetBegin557
            // Insert pre-init code here
            tfFNGcCode = new TextField("K\u00F3d ke\u0161e:", "GC12345", 120, TextField.ANY | TextField.NON_PREDICTIVE);//GEN-LINE:MVDGetInit557
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd557
        return tfFNGcCode;
    }//GEN-END:MVDGetEnd557

    /** This method returns instance for cgFNType component and should be called instead of accessing cgFNType field directly.//GEN-BEGIN:MVDGetBegin558
     * @return Instance for cgFNType component
     */
    public ChoiceGroup get_cgFNType() {
        if (cgFNType == null) {//GEN-END:MVDGetBegin558
            // Insert pre-init code here
            cgFNType = new ChoiceGroup("Typ z\u00E1pisu:", Choice.POPUP, new String[] {//GEN-BEGIN:MVDGetInit558
                "Found it",
                "Didn\'t find it",
                "Write note",
                "Needs archived",
                "Needs maintenance"
            }, new Image[] {
                null,
                null,
                null,
                null,
                null
            });
            cgFNType.setSelectedFlags(new boolean[] {
                false,
                false,
                false,
                false,
                false
            });//GEN-END:MVDGetInit558
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd558
        return cgFNType;
    }//GEN-END:MVDGetEnd558

    /** This method returns instance for dtFNDate component and should be called instead of accessing dtFNDate field directly.//GEN-BEGIN:MVDGetBegin564
     * @return Instance for dtFNDate component
     */
    public DateField get_dtFNDate() {
        if (dtFNDate == null) {//GEN-END:MVDGetBegin564
            // Insert pre-init code here
            dtFNDate = new DateField("Datum:", DateField.DATE_TIME);//GEN-LINE:MVDGetInit564
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd564
        return dtFNDate;
    }//GEN-END:MVDGetEnd564

    /** This method returns instance for tfFNText component and should be called instead of accessing tfFNText field directly.//GEN-BEGIN:MVDGetBegin565
     * @return Instance for tfFNText component
     */
    public TextField get_tfFNText() {
        if (tfFNText == null) {//GEN-END:MVDGetBegin565
            // Insert pre-init code here
            tfFNText = new TextField("Text:", null, 4096, TextField.ANY);//GEN-LINE:MVDGetInit565
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd565
        return tfFNText;
    }//GEN-END:MVDGetEnd565

    /** This method returns instance for cmdPostFieldNotes component and should be called instead of accessing cmdPostFieldNotes field directly.//GEN-BEGIN:MVDGetBegin574
     * @return Instance for cmdPostFieldNotes component
     */
    public Command get_cmdPostFieldNotes() {
        if (cmdPostFieldNotes == null) {//GEN-END:MVDGetBegin574
            // Insert pre-init code here
            cmdPostFieldNotes = new Command("Odeslat na GC.com", Command.SCREEN, 20);//GEN-LINE:MVDGetInit574
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd574
        return cmdPostFieldNotes;
    }//GEN-END:MVDGetEnd574

    /** This method returns instance for cgFieldNotes component and should be called instead of accessing cgFieldNotes field directly.//GEN-BEGIN:MVDGetBegin577
     * @return Instance for cgFieldNotes component
     */
    public ChoiceGroup get_cgFieldNotes() {
        if (cgFieldNotes == null) {//GEN-END:MVDGetBegin577
            // Insert pre-init code here
            cgFieldNotes = new ChoiceGroup("Nastaven\u00ED Field notes:", Choice.MULTIPLE, new String[] {//GEN-BEGIN:MVDGetInit577
                "Inkrem. Field notes",
                "Ikonky ve Field notes",
                "Nejprve n\u00E1zev ke\u0161e",
                "Zalamovat text"
            }, new Image[] {
                null,
                null,
                null,
                null
            });
            cgFieldNotes.setSelectedFlags(new boolean[] {
                false,
                false,
                false,
                false
            });//GEN-END:MVDGetInit577
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd577
        return cgFieldNotes;
    }//GEN-END:MVDGetEnd577
    /** This method returns instance for cmdSetFound component and should be called instead of accessing cmdSetFound field directly.//GEN-BEGIN:MVDGetBegin584
     * @return Instance for cmdSetFound component
     */
    public Command get_cmdSetFound() {
        if (cmdSetFound == null) {//GEN-END:MVDGetBegin584
            // Insert pre-init code here
            cmdSetFound = new Command("Nastavit n\u00E1lez", Command.SCREEN, 10);//GEN-LINE:MVDGetInit584
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd584
        return cmdSetFound;
    }//GEN-END:MVDGetEnd584

    /** This method returns instance for cmdImportGPX component and should be called instead of accessing cmdImportGPX field directly.//GEN-BEGIN:MVDGetBegin586
     * @return Instance for cmdImportGPX component
     */
    public Command get_cmdImportGPX() {
        if (cmdImportGPX == null) {//GEN-END:MVDGetBegin586
            // Insert pre-init code here
            cmdImportGPX = new Command("Importovat z GPX...", Command.SCREEN, 11);//GEN-LINE:MVDGetInit586
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd586
        return cmdImportGPX;
    }//GEN-END:MVDGetEnd586
    /** This method returns instance for gaLoadingIndicator component and should be called instead of accessing gaLoadingIndicator field directly.//GEN-BEGIN:MVDGetBegin596
     * @return Instance for gaLoadingIndicator component
     */
    public Gauge get_gaLoadingIndicator() {
        if (gaLoadingIndicator == null) {//GEN-END:MVDGetBegin596
            // Insert pre-init code here
            gaLoadingIndicator = new Gauge(null, false, 100, 50);//GEN-LINE:MVDGetInit596
            // Insert post-init code here
            gaLoadingIndicator.setMaxValue(Gauge.CONTINUOUS_RUNNING);
        }//GEN-BEGIN:MVDGetEnd596
        return gaLoadingIndicator;
    }//GEN-END:MVDGetEnd596

    /** This method returns instance for cmdMemoryInfo component and should be called instead of accessing cmdMemoryInfo field directly.//GEN-BEGIN:MVDGetBegin601
     * @return Instance for cmdMemoryInfo component
     */
    public Command get_cmdMemoryInfo() {
        if (cmdMemoryInfo == null) {//GEN-END:MVDGetBegin601
            // Insert pre-init code here
            cmdMemoryInfo = new Command("Informace o pam\u011Bti", Command.SCREEN, 1);//GEN-LINE:MVDGetInit601
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd601
        return cmdMemoryInfo;
    }//GEN-END:MVDGetEnd601

    /** This method returns instance for frmMemoryInfo component and should be called instead of accessing frmMemoryInfo field directly.//GEN-BEGIN:MVDGetBegin603
     * @return Instance for frmMemoryInfo component
     */
    public Form get_frmMemoryInfo() {
        if (frmMemoryInfo == null) {//GEN-END:MVDGetBegin603
            // Insert pre-init code here
            frmMemoryInfo = new Form("Informace o pam\u011Bti", new Item[] {//GEN-BEGIN:MVDGetInit603
                get_siHeapSize(),
                get_siRMSFavourities(),
                get_siRMSHint(),
                get_siRMSListing(),
                get_siRMSFieldNotes()
            });
            frmMemoryInfo.addCommand(get_cmdBack());
            frmMemoryInfo.setCommandListener(this);//GEN-END:MVDGetInit603
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd603
        return frmMemoryInfo;
    }//GEN-END:MVDGetEnd603

    /** This method returns instance for siHeapSize component and should be called instead of accessing siHeapSize field directly.//GEN-BEGIN:MVDGetBegin606
     * @return Instance for siHeapSize component
     */
    public StringItem get_siHeapSize() {
        if (siHeapSize == null) {//GEN-END:MVDGetBegin606
            // Insert pre-init code here
            siHeapSize = new StringItem("Pam\u011B\u0165 (KB):", "0/0");//GEN-LINE:MVDGetInit606
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd606
        return siHeapSize;
    }//GEN-END:MVDGetEnd606

    /** This method returns instance for siRMSFavourities component and should be called instead of accessing siRMSFavourities field directly.//GEN-BEGIN:MVDGetBegin607
     * @return Instance for siRMSFavourities component
     */
    public StringItem get_siRMSFavourities() {
        if (siRMSFavourities == null) {//GEN-END:MVDGetBegin607
            // Insert pre-init code here
            siRMSFavourities = new StringItem("RMS obl\u00EDben\u00E9 (KB):", "0/0");//GEN-LINE:MVDGetInit607
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd607
        return siRMSFavourities;
    }//GEN-END:MVDGetEnd607

    /** This method returns instance for siRMSHint component and should be called instead of accessing siRMSHint field directly.//GEN-BEGIN:MVDGetBegin608
     * @return Instance for siRMSHint component
     */
    public StringItem get_siRMSHint() {
        if (siRMSHint == null) {//GEN-END:MVDGetBegin608
            // Insert pre-init code here
            siRMSHint = new StringItem("RMS n\u00E1pov\u011Bda - hint (KB):", "0/0");//GEN-LINE:MVDGetInit608
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd608
        return siRMSHint;
    }//GEN-END:MVDGetEnd608

    /** This method returns instance for siRMSFieldNotes component and should be called instead of accessing siRMSFieldNotes field directly.//GEN-BEGIN:MVDGetBegin609
     * @return Instance for siRMSFieldNotes component
     */
    public StringItem get_siRMSFieldNotes() {
        if (siRMSFieldNotes == null) {//GEN-END:MVDGetBegin609
            // Insert pre-init code here
            siRMSFieldNotes = new StringItem("RMS Field notes (KB):", "0/0");//GEN-LINE:MVDGetInit609
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd609
        return siRMSFieldNotes;
    }//GEN-END:MVDGetEnd609

    /** This method returns instance for siRMSListing component and should be called instead of accessing siRMSListing field directly.//GEN-BEGIN:MVDGetBegin610
     * @return Instance for siRMSListing component
     */
    public StringItem get_siRMSListing() {
        if (siRMSListing == null) {//GEN-END:MVDGetBegin610
            // Insert pre-init code here
            siRMSListing = new StringItem("RMS listing (KB):", "0/0");//GEN-LINE:MVDGetInit610
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd610
        return siRMSListing;
    }//GEN-END:MVDGetEnd610

    /** This method returns instance for cmdDownloadAll component and should be called instead of accessing cmdDownloadAll field directly.//GEN-BEGIN:MVDGetBegin611
     * @return Instance for cmdDownloadAll component
     */
    public Command get_cmdDownloadAll() {
        if (cmdDownloadAll == null) {//GEN-END:MVDGetBegin611
            // Insert pre-init code here
            cmdDownloadAll = new Command("St\u00E1hnout v\u0161e", Command.ITEM, 1);//GEN-LINE:MVDGetInit611
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd611
        return cmdDownloadAll;
    }//GEN-END:MVDGetEnd611

    /** This method returns instance for cgInternalGPSType component and should be called instead of accessing cgInternalGPSType field directly.//GEN-BEGIN:MVDGetBegin613
     * @return Instance for cgInternalGPSType component
     */
    public ChoiceGroup get_cgInternalGPSType() {
        if (cgInternalGPSType == null) {//GEN-END:MVDGetBegin613
            // Insert pre-init code here
            cgInternalGPSType = new ChoiceGroup("Typ intern\u00ED GPS:", Choice.POPUP, new String[] {//GEN-BEGIN:MVDGetInit613
                "Obecn\u00FD",
                "Obec. 1s aktual.",
                "Sam. SGH-i5x0",
                "BlackBerry"
            }, new Image[] {
                null,
                null,
                null,
                null
            });
            cgInternalGPSType.setSelectedFlags(new boolean[] {
                true,
                false,
                false,
                false
            });//GEN-END:MVDGetInit613
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd613
        return cgInternalGPSType;
    }//GEN-END:MVDGetEnd613
    
    public Navigation get_cvsNavigation() {
        if (cvsNavigation == null) {
            cvsNavigation = new Navigation(this, gps, favourites);
            cvsNavigation.setFullScreenMode(true);
        }
        return cvsNavigation;
    }
    
    public Map get_cvsMap() {
        if (cvsMap == null) {
            cvsMap = new Map(this, gps, iconLoader, track);
            cvsMap.setFullScreenMode(true);
        }
        return cvsMap;
    }
    
    //Zephy 21.11.07 gpsstatus+\
    public Signal get_cvsSignal() {
        if (cvsSignal == null) {
            cvsSignal = new Signal(this, gps);
            cvsSignal.setFullScreenMode(true);
        }
        return cvsSignal;
    }
    //Zephy 21.11.07 gpsstatus+/
    
    public void startApp() {
    }
    
    public void pauseApp() {
    }
    
    public void destroyApp(boolean unconditional) {
    }
    
    /**
     * Tato metoda se pouziva k zobrazovani vyjimek, ktere mohou v aplikaci nastat
     */
    public void showError(String section, String errorMessage, String data) {
        //vymazani chyb
        if (data.equals("Chyba")) {
            get_tbError().setString("Problém s komunikačním skriptem, opakujte akci. Pokud se tato chyba ukazuje pořád, máte nefunkční GPRS.");
        } else {
            get_tbError().setString("Popis chyby:\n\nSekce: " + section + "\nDruh: " + errorMessage + "\nData: '" +
                    "'");
        }
        //System.out.println(get_tbError().getString());
        getDisplay().setCurrent(get_tbError());
    }
    
    /**
     * Tato metoda se pouziva k zobrazovani alertu v aplikaci
     */
    public Alert showAlert(String text, AlertType type, Displayable next) {
        String caption = "";
        if (type == AlertType.ALARM)
            caption = "Alarm";
        else if (type == AlertType.CONFIRMATION)
            caption = "OK";
        else if (type == AlertType.ERROR)
            caption = "Chyba";
        else if (type == AlertType.INFO)
            caption = "Info";
        else if (type==AlertType.WARNING)
            caption = "Upozornění";
        Alert alert = new Alert(caption,text,null,type);
        alert.setTimeout(Alert.FOREVER);
        if (type == AlertType.INFO)
            alert.setTimeout(800);
        if (next == null) next = getDisplay().getCurrent();
        getDisplay().setCurrent(alert, next);
        return alert;
    }
    
    /**
     * Tato funkce vrati cislo prvniho zaskrtnuteho policka listu typu multiple, pokud neni zaskrtnuty nic tak -1
     */
    public int firstCheckedFavourite() {
        int selected = lstFavourites.getSelectedIndex();
        if (selected==-1) //vybere prvni zaskrtnuty policko
        {
            for (int i=0;i<lstFavourites.size();i++) {
                if (lstFavourites.isSelected(i)) {
                    selected = i;
                    break;
                }
            }
        }
        return selected;
    }
    
    public int firstChecked(List list) {
        int selected = list.getSelectedIndex();
        if (selected==-1) //vybere prvni zaskrtnuty policko
        {
            for (int i=0;i<list.size();i++) {
                if (list.isSelected(i)) {
                    selected = i;
                    break;
                }
            }
        }
        return selected;
    }
    
    public void changeCmdInfoLabel(String label) {
        cmdInfo = new Command("Listing("+label+"kB)", Command.SCREEN, 3);
    }
    
    public void searchBluetooth() {
        bluetooth = new Bluetooth(this, http, settings, favourites, false);
        if (bluetooth.isOn()) {
            bluetooth.searchDevices();
        }
    }

    public void itemStateChanged(Item item) {
        if (item == get_dtFNDate()) {
            Calendar c = Calendar.getInstance();
            c.setTime(get_dtFNDate().getDate());
            String time = new StringBuffer()
                            .append(Utils.nulaNula(c.get(Calendar.HOUR_OF_DAY)))
                            .append(':')
                            .append(Utils.nulaNula(c.get(Calendar.MINUTE)))
                            .toString();
            String text = get_tfFNText().getString();
            if (text.length() == 0) {
                get_tfFNText().setString(time);
            } else if (text.length() >= 5 && text.charAt(2) == ':') {
                text = time + text.substring(5);
                get_tfFNText().setString(text);
            }
        }
    }
    
    private void fillMemoryInfoForm() {
        get_siHeapSize().setText((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 + "/" + Runtime.getRuntime().totalMemory() / 1024);
        get_siRMSFavourities().setText(favourites.usedSize() / 1024 + "/" + favourites.totalSize() / 1024 + " (" + favourites.count() + ")");
        get_siRMSHint().setText(http.getHintCache().usedSize() / 1024 + "/" + http.getHintCache().totalSize() / 1024 + " (" + http.getHintCache().count() + ")");
        get_siRMSListing().setText(http.getListingCache().usedSize() / 1024 + "/" + http.getListingCache().totalSize() / 1024 + " (" + http.getListingCache().count() + ")");
        get_siRMSFieldNotes().setText(FieldNotes.getInstance().usedSize() / 1024 + "/" + FieldNotes.getInstance().totalSize() / 1024 + " (" + FieldNotes.getInstance().count() + ")");
    }
}
