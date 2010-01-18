/**
 * Gui.java
 *
 * Created on 13. únor 2007, 8:48
 *
 */

package handy;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;


/***
 * Tato trida se stara o interakci s uzivatelem, zobrazovani GUI a aplikacni
 * logiku
 * @author David Vavra
 */
public class Gui extends MIDlet implements CommandListener
{
    //mody aplikace
    public boolean nearest = false;
    public boolean logged = false;
    public boolean nearestFromWaypoint = false;
    public boolean nearestFromFavourite = false;
    public boolean keyword = false;
    public boolean favourites = false;
    public boolean trackables = false;
    public boolean multiSolver = false;
    
    //reference na jednotlive moduly
    private Database database;
    private Http http;
    
    /***
     * Creates a new instance of Gui
     */
    public Gui()
    {
        database = new Database(this);
        http = new Http(this, database);
        database.setHttpReference(http);
        if (database.loadSettings())
            initialize();
    }
    
    public Http getHttpReference()
    {
        return http;
    }
    
    private List lstMenu;//GEN-BEGIN:MVDFields
    private Command cmdExit;
    private Command cmdBack;
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
    private Image imgListing;
    private Image imgGPS;
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
    private ChoiceGroup cgOtherSettings;
    private StringItem stringItem6;
    private Command cmdNext;
    private Form frmAllLogs;
    private Image imgNavigate;
    private Command cmdLogIt;
    private Form frmLogIt;
    private ChoiceGroup cgLogType;
    private TextField tfLogText;
    private Image imgKeyword;
    private Image imgPosition;
    private Image imgPaper;
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
    private Image imgTbGc;
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
    private Form frmLogTrackable;
    private ChoiceGroup cgTrLogType;
    private TextField tfTrLogText;
    private Form frmMultiSolver;
    private StringItem siLattitudePattern;
    private StringItem siLongitudePattern;
    private StringItem siLetters;
    private Command cmdAddPattern;
    private Image imgMulti;
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
    private TextField tfDefaultLog;
    private Image imgGPSGate;
    private Image imgBluetooth;
    private List lstPatterns;
    private Command cmdPatterns;
    private Command cmdEditPattern;
    private TextField tfPatternName;
    private StringItem siAfterReplacement;
    private Command cmdDownloadPatterns;
    private StringItem stringItem2;
    private TextBox tbError;
    private StringItem siContent;
    private StringItem siEnd;
    private StringItem siBegin;
    private Command cmdFavourites;
    private Command cmdMultiSolver;
    private Command cmdRefresh;
    private Image imgDecypher;
    private TextBox tbDecypher;
    private Command cmdDecypher;//GEN-END:MVDFields
    private SplashScreen cvsSplashScreen;
//GEN-LINE:MVDMethods
    
    //Konstatnty pro field note
    public static final int TYPE_FOUND_IT = 0;
    public static final int TYPE_DIDN_T_FIND_IT = 1;
    public static final int TYPE_WRITE_NOTE = 2;
    public static final int TYPE_NEEDS_ARCHIVED = 3;
    public static final int TYPE_NEEDS_MAINTENANCE = 4;
    
    /** Called by the system to indicate that a command has been invoked on a particular displayable.//GEN-BEGIN:MVDCABegin
     * @param command the Command that ws invoked
     * @param displayable the Displayable on which the command was invoked
     */
    public void commandAction(Command command, Displayable displayable) {//GEN-END:MVDCABegin
         // Insert global pre-action code here
        if (displayable == lstMenu) {//GEN-BEGIN:MVDCABody
            if (command == lstMenu.SELECT_COMMAND) {
                switch (get_lstMenu().getSelectedIndex()) {
                    case 0://GEN-END:MVDCABody
                         favourites = false;
                         trackables = false;
                         getDisplay().setCurrent(get_lstSearch());//GEN-LINE:MVDCAAction37
                        
                         break;//GEN-BEGIN:MVDCACase37
                    case 5://GEN-END:MVDCACase37
                         // Insert pre-action code here
                        getDisplay().setCurrent(get_frmAbout());//GEN-LINE:MVDCAAction41
                         // Insert post-action code here
                         siVerze.setText(getAppProperty("MIDlet-Version")+"\n");
                         if (database.vip)
                             siDonate.setText("Děkuji moc za Váš příspěvek na vývoj aplikace! Destil");
                         break;//GEN-BEGIN:MVDCACase41
                    case 6://GEN-END:MVDCACase41
                         // Insert pre-action code here
                        exitMIDlet();//GEN-LINE:MVDCAAction115
                         // Insert post-action code here
                        break;//GEN-BEGIN:MVDCACase115
                    case 4://GEN-END:MVDCACase115
                         // Insert pre-action code here
                        getDisplay().setCurrent(get_frmSettings());//GEN-LINE:MVDCAAction139
                         // Insert post-action code here
                         database.setSettings();
                         break;//GEN-BEGIN:MVDCACase139
                    case 1://GEN-END:MVDCACase139
                         // Insert pre-action code here
                         favourites = true;
                         nearest = false;
                         nearestFromWaypoint = false;
                         keyword = false;
                         trackables = false;
                         database.viewFavourites();
                         getDisplay().setCurrent(get_lstFavourites());//GEN-LINE:MVDCAAction214
                        // Insert post-action code here
                         break;//GEN-BEGIN:MVDCACase214
                    case 2://GEN-END:MVDCACase214
                         // Insert pre-action code here
                        getDisplay().setCurrent(get_lstGPS());//GEN-LINE:MVDCAAction236
                         // Insert post-action code here
                        break;//GEN-BEGIN:MVDCACase236
                    case 3://GEN-END:MVDCACase236
                         // Insert pre-action code here
                        // Do nothing//GEN-LINE:MVDCAAction287
                         // Insert post-action code here
                         trackables = true;
                         getDisplay().setCurrent(get_frmTrackingNumber());
                         break;//GEN-BEGIN:MVDCACase287
                }
            }
        } else if (displayable == frmCoordinates) {
            if (command == cmdBack) {//GEN-END:MVDCACase287
                 
                getDisplay().setCurrent(get_lstSearch());//GEN-LINE:MVDCAAction69
                 
            } else if (command == cmdSend) {//GEN-LINE:MVDCACase69
                 // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction68
                 // Insert post-action code here
                 http.start(Http.NEAREST_CACHES);
            }//GEN-BEGIN:MVDCACase68
        } else if (displayable == lstNearestCaches) {
            if (command == cmdBack) {//GEN-END:MVDCACase68
                 // Insert pre-action code here
                 if (nearestFromWaypoint && !nearest)
                 {
                     getDisplay().setCurrent(get_frmOverview());
                 }
                 else if (nearestFromFavourite)
                 {
                     getDisplay().setCurrent(get_frmFavourite());
                 }
                 else
                 {
                     getDisplay().setCurrent(get_frmCoordinates());//GEN-LINE:MVDCAAction73
                    
                 }
                 // Insert post-action code here
             }
             else if (command == lstNearestCaches.SELECT_COMMAND)
             {
                 http.waypoint = http.waypoints[lstNearestCaches.getSelectedIndex()];
                 http.start(Http.OVERVIEW);
             } else if (command == lstNearestCaches.SELECT_COMMAND) {//GEN-BEGIN:MVDCACase73
                switch (get_lstNearestCaches().getSelectedIndex()) {
                    case 0://GEN-END:MVDCACase73
                         // Insert pre-action code here
                        // Do nothing//GEN-LINE:MVDCAAction314
                         // Insert post-action code here
                        break;//GEN-BEGIN:MVDCACase314
                }
             }
        } else if (displayable == frmWaypoint) {
            if (command == cmdBack) {//GEN-END:MVDCACase314
                 // Insert pre-action code here
                getDisplay().setCurrent(get_lstSearch());//GEN-LINE:MVDCAAction87
                 // Insert post-action code here
            } else if (command == cmdSend) {//GEN-LINE:MVDCACase87
                 // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction88
                 // Insert post-action code here
                 http.waypoint = tfWaypoint.getString();
                 http.start(Http.OVERVIEW);
            }//GEN-BEGIN:MVDCACase88
        } else if (displayable == frmOverview) {
            if (command == cmdBack) {//GEN-END:MVDCACase88
                 // Insert pre-action code here
                 if (nearest)
                 {
                     getDisplay().setCurrent(lstNearestCaches);
                 }
                 else if (favourites)
                 {
                     getDisplay().setCurrent(lstFavourites);
                 }
                 else if (keyword)
                 {
                     getDisplay().setCurrent(lstKeyword);
                 }
                 else
                 {
                     getDisplay().setCurrent(get_frmWaypoint());//GEN-LINE:MVDCAAction91
                 }// Insert post-action code here
            } else if (command == cmdWaypoints) {//GEN-LINE:MVDCACase91
                 // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction109
                 // Insert post-action code here
                 http.start(Http.WAYPOINTS);
            } else if (command == cmdInfo) {//GEN-LINE:MVDCACase109
                 // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction105
                 // Insert post-action code here
                 http.start(Http.LISTING);
            } else if (command == cmdHint) {//GEN-LINE:MVDCACase105
                 // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction103
                 // Insert post-action code here
                 http.start(Http.HINT);
            } else if (command == cmdLogs) {//GEN-LINE:MVDCACase103
                 // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction107
                 // Insert post-action code here
                 http.start(Http.LOGS);
            } else if (command == cmdNext) {//GEN-LINE:MVDCACase107
                 // Insert pre-action code here
                 nearestFromWaypoint = true;
                 nearestFromFavourite = false;
                 http.start(Http.NEXT_NEAREST);
                 // Do nothing//GEN-LINE:MVDCAAction173
                 // Insert post-action code here
            } else if (command == cmdLogIt) {//GEN-LINE:MVDCACase173
                 // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction209
                 http.start(Http.LOG_IT);
            } else if (command == cmdFavourite) {//GEN-LINE:MVDCACase209
                 // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction255
                 database.editId = -1;
                 database.addFavourite(siName.getText(),http.favouriteResponse,siOverviewLattitude.getText(),siOverviewLongitude.getText(),http.typeNumber);
            } else if (command == cmdDownloadPatterns) {//GEN-LINE:MVDCACase255
                 // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction388
                 // Insert post-action code here
                 http.start(Http.PATTERNS);
            } else if (command == cmdRefresh) {//GEN-LINE:MVDCACase388
                 // Insert pre-action code here
                 http.waypoint = get_siWaypoint().getText();
                 http.start(Http.OVERVIEW);
                 // Do nothing//GEN-LINE:MVDCAAction410
                 // Insert post-action code here
            }//GEN-BEGIN:MVDCACase410
        } else if (displayable == frmInfo) {
            if (command == cmdBack) {//GEN-END:MVDCACase410
                 // Insert pre-action code here
                getDisplay().setCurrent(get_frmOverview());//GEN-LINE:MVDCAAction111
                 // Insert post-action code here
            }//GEN-BEGIN:MVDCACase111
        } else if (displayable == frmHint) {
            if (command == cmdBack) {//GEN-END:MVDCACase111
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
                 database.addWaypoints(http.response);
            }//GEN-BEGIN:MVDCACase260
        } else if (displayable == frmLogs) {
            if (command == cmdBack) {//GEN-END:MVDCACase260
                 // Insert pre-action code here
                getDisplay().setCurrent(get_frmOverview());//GEN-LINE:MVDCAAction119
                 // Insert post-action code here
             }
             else
             {
                 http.start(Http.ALL_LOGS);
             }//GEN-BEGIN:MVDCACase119
        } else if (displayable == frmAbout) {
            if (command == cmdBack) {//GEN-END:MVDCACase119
                 // Insert pre-action code here
                getDisplay().setCurrent(get_lstMenu());//GEN-LINE:MVDCAAction129
                 // Insert post-action code here
            }//GEN-BEGIN:MVDCACase129
        } else if (displayable == frmLoading) {
            if (command == cmdStop) {//GEN-END:MVDCACase129
                 // Insert pre-action code here
                getDisplay().setCurrent(get_lstMenu());//GEN-LINE:MVDCAAction136
                 // Insert post-action code here
            }//GEN-BEGIN:MVDCACase136
        } else if (displayable == frmSettings) {
            if (command == cmdSave) {//GEN-END:MVDCACase136
                 // Insert pre-action code here
                getDisplay().setCurrent(get_lstMenu());//GEN-LINE:MVDCAAction144
                 // Insert post-action code here
                 database.saveSettings();
            } else if (command == cmdBack) {//GEN-LINE:MVDCACase144
                 // Insert pre-action code here
                getDisplay().setCurrent(get_lstMenu());//GEN-LINE:MVDCAAction142
                 // Insert post-action code here
            }//GEN-BEGIN:MVDCACase142
        } else if (displayable == frmAllLogs) {
            if (command == cmdBack) {//GEN-END:MVDCACase142
                 // Insert pre-action code here
                getDisplay().setCurrent(get_frmLogs());//GEN-LINE:MVDCAAction178
                 // Insert post-action code here
            }//GEN-BEGIN:MVDCACase178
        } else if (displayable == frmLogIt) {
            if (command == cmdBack) {//GEN-END:MVDCACase178
                 // Insert pre-action code here
                getDisplay().setCurrent(get_frmOverview());//GEN-LINE:MVDCAAction208
                 // Insert post-action code here
            } else if (command == cmdSend) {//GEN-LINE:MVDCACase208
                 // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction207
                 // Insert post-action code here
                 http.start(Http.SEND_LOG);
            }//GEN-BEGIN:MVDCACase207
        } else if (displayable == lstKeyword) {
            if (command == cmdBack) {//GEN-END:MVDCACase207
                 // Insert pre-action code here
                getDisplay().setCurrent(get_frmKeyword());//GEN-LINE:MVDCAAction225
                 // Insert post-action code here
             }
             else if (command == lstKeyword.SELECT_COMMAND)
             {
                 http.waypoint = http.waypoints[lstKeyword.getSelectedIndex()];
                 http.start(Http.OVERVIEW);
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
                 http.start(Http.KEYWORD);
            }//GEN-BEGIN:MVDCACase222
        } else if (displayable == lstSearch) {
            if (command == lstSearch.SELECT_COMMAND) {
                switch (get_lstSearch().getSelectedIndex()) {
                    case 0://GEN-END:MVDCACase222
                         // nejblizsi kese
                         nearest = true;
                         nearestFromWaypoint = false;
                         nearestFromFavourite = false;
                         keyword = false;
                         get_frmCoordinates().setTitle("Zadejte souřadnice:");
                         get_tfLattitude().setString(database.lastLattitude);
                         get_tfLongitude().setString(database.lastLongitude);
                         getDisplay().setCurrent(get_frmCoordinates());
                         
                         // Do nothing//GEN-LINE:MVDCAAction229
                         // Insert post-action code here
                         break;//GEN-BEGIN:MVDCACase229
                    case 1://GEN-END:MVDCACase229
                         // hledani podle waypointu
                         nearest = false;
                         keyword = false;
                         getDisplay().setCurrent(get_frmWaypoint());
                         
                         // Do nothing//GEN-LINE:MVDCAAction231
                         // Insert post-action code here
                         break;//GEN-BEGIN:MVDCACase231
                    case 2://GEN-END:MVDCACase231
                         // hledani podle klicoveho slova
                         nearest = false;
                         keyword = true;
                         getDisplay().setCurrent(get_frmKeyword());
                         // Do nothing//GEN-LINE:MVDCAAction233
                         // Insert post-action code here
                         break;//GEN-BEGIN:MVDCACase233
                }
            } else if (command == cmdBack) {//GEN-END:MVDCACase233
                 // Insert pre-action code here
                getDisplay().setCurrent(get_lstMenu());//GEN-LINE:MVDCAAction243
                 // Insert post-action code here
            }//GEN-BEGIN:MVDCACase243
        } else if (displayable == lstGPS) {
            if (command == lstGPS.SELECT_COMMAND) {
                switch (get_lstGPS().getSelectedIndex()) {
                    case 0://GEN-END:MVDCACase243
                         // Insert pre-action code here
                         multiSolver = true;
                         database.viewMultiSolver();
                         getDisplay().setCurrent(get_frmMultiSolver());//GEN-LINE:MVDCAAction331
                        // Insert post-action code here
                         break;//GEN-BEGIN:MVDCACase331
                    case 1://GEN-END:MVDCACase331
                         // Insert pre-action code here
                        getDisplay().setCurrent(get_tbDecypher());//GEN-LINE:MVDCAAction412
                         // Insert post-action code here
                        break;//GEN-BEGIN:MVDCACase412
                }
            } else if (command == cmdBack) {//GEN-END:MVDCACase412
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
                 database.deleteAllFavourites();
            } else if (command == cmdSelect) {//GEN-LINE:MVDCACase257
                 // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction259
                 // Insert post-action code here
                 int selected = firstCheckedFavourite();
                 if (selected != -1)
                     database.viewFavourite(selected, true);
            } else if (command == cmdDelete) {//GEN-LINE:MVDCACase259
                 // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction272
                 // Insert post-action code here
                 database.deleteFavourites();
            } else if (command == cmdAddGiven) {//GEN-LINE:MVDCACase272
                 // Insert pre-action code here
                 multiSolver = false;
                 database.editId = -1;
                 get_tfGivenLattitude().setString(database.lastLattitude);
                 get_tfGivenLongitude().setString(database.lastLongitude);
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
                     database.editFavourite(selected);
            } else if (command == cmdMultiSolver) {//GEN-LINE:MVDCACase285
                 // Insert pre-action code here
                 multiSolver = true;
                 database.viewMultiSolver();
                 getDisplay().setCurrent(get_frmMultiSolver());
                 // Do nothing//GEN-LINE:MVDCAAction408
                 // Insert post-action code here
            }//GEN-BEGIN:MVDCACase408
        } else if (displayable == frmFavourite) {
            if (command == cmdBack) {//GEN-END:MVDCACase408
                 // Insert pre-action code here
                getDisplay().setCurrent(get_lstFavourites());//GEN-LINE:MVDCAAction262
                 // Insert post-action code here
            } else if (command == cmdNext) {//GEN-LINE:MVDCACase262
                 // Insert pre-action code here
                 nearestFromWaypoint = false;
                 nearestFromFavourite = true;
                 http.start(Http.NEXT_NEAREST);
                 // Do nothing//GEN-LINE:MVDCAAction360
                 // Insert post-action code here
            }//GEN-BEGIN:MVDCACase360
        } else if (displayable == frmAddGiven) {
            if (command == cmdBack) {//GEN-END:MVDCACase360
                 // Insert pre-action code here
                getDisplay().setCurrent(get_lstFavourites());//GEN-LINE:MVDCAAction280
                 // Insert post-action code here
            } else if (command == cmdSave) {//GEN-LINE:MVDCACase280
                 // Insert pre-action code here
                // Do nothing//GEN-LINE:MVDCAAction279
                 // Insert post-action code here
                 database.addFavourite(tfGivenName.getString(),tfGivenDescription.getString(),tfGivenLattitude.getString(),tfGivenLongitude.getString(),"0");
                 
            }//GEN-BEGIN:MVDCACase279
        } else if (displayable == frmTrackingNumber) {
            if (command == cmdBack) {//GEN-END:MVDCACase279
                 // Insert pre-action code here
                getDisplay().setCurrent(get_lstMenu());//GEN-LINE:MVDCAAction292
                 // Insert post-action code here
            } else if (command == cmdSend) {//GEN-LINE:MVDCACase292
                 // Insert pre-action code here
                 http.start(Http.TRACKABLE);
                 // Do nothing//GEN-LINE:MVDCAAction294
                 // Insert post-action code here
            }//GEN-BEGIN:MVDCACase294
        } else if (displayable == frmTrackable) {
            if (command == cmdBack) {//GEN-END:MVDCACase294
                 // Insert pre-action code here
                getDisplay().setCurrent(get_frmTrackingNumber());//GEN-LINE:MVDCAAction296
                 // Insert post-action code here
            } else if (command == cmdLogIt) {//GEN-LINE:MVDCACase296
                 // Insert pre-action code here
                 http.start(Http.TRACKABLE_LOG);
                 // Do nothing//GEN-LINE:MVDCAAction297
                 // Insert post-action code here
            }//GEN-BEGIN:MVDCACase297
        } else if (displayable == frmLogTrackable) {
            if (command == cmdBack) {//GEN-END:MVDCACase297
                 // Insert pre-action code here
                getDisplay().setCurrent(get_frmTrackable());//GEN-LINE:MVDCAAction307
                 // Insert post-action code here
            } else if (command == cmdLogIt) {//GEN-LINE:MVDCACase307
                 // Insert pre-action code here
                 http.start(Http.TRACKABLE_SEND_LOG);
                 // Do nothing//GEN-LINE:MVDCAAction308
                 // Insert post-action code here
            }//GEN-BEGIN:MVDCACase308
        } else if (displayable == frmMultiSolver) {
            if (command == cmdDeleteAll) {//GEN-END:MVDCACase308
                 // Insert pre-action code here
                 database.deleteMultiSolver();
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
                 database.computeCoordinates();
                 // Do nothing//GEN-LINE:MVDCAAction349
                 // Insert post-action code here
            } else if (command == cmdPatterns) {//GEN-LINE:MVDCACase349
                 // Insert pre-action code here
                 database.viewPatterns();
                 getDisplay().setCurrent(get_lstPatterns());//GEN-LINE:MVDCAAction380
                // Insert post-action code here
            } else if (command == cmdFavourites) {//GEN-LINE:MVDCACase380
                 // Insert pre-action code here
                 favourites = true;
                 nearest = false;
                 nearestFromWaypoint = false;
                 keyword = false;
                 trackables = false;
                 database.viewFavourites();
                 getDisplay().setCurrent(get_lstFavourites());
                 // Do nothing//GEN-LINE:MVDCAAction406
                 // Insert post-action code here
            }//GEN-BEGIN:MVDCACase406
        } else if (displayable == frmEditPattern) {
            if (command == cmdSave) {//GEN-END:MVDCACase406
                 // Insert pre-action code here
                 if (get_frmEditPattern().getTitle().equals("Upravit vzorec"))
                     database.addEditPattern(true);
                 else
                     database.addEditPattern(false);
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
                 database.addEditLetter();
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
                 database.addFavourite(tfResultName.getString(),tfResultDescription.getString(),tfResultLattitude.getString(),tfResultLongitude.getString(),"20");
                 // Do nothing//GEN-LINE:MVDCAAction352
                 // Insert post-action code here
            }//GEN-BEGIN:MVDCACase352
        } else if (displayable == lstPatterns) {
            if (command == cmdEditPattern) {//GEN-END:MVDCACase352
                 // Insert pre-action code here
                 get_frmEditPattern().setTitle("Upravit vzorec");
                 database.viewPattern();
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
             }
             else if (command == lstPatterns.SELECT_COMMAND)
             {
                 database.setActivePattern();
                 getDisplay().setCurrent(get_frmMultiSolver());
                 
             } else if (command == lstPatterns.SELECT_COMMAND) {//GEN-BEGIN:MVDCACase378
                switch (get_lstPatterns().getSelectedIndex()) {
                    case 0://GEN-END:MVDCACase378
                         // Insert pre-action code here
                        // Do nothing//GEN-LINE:MVDCAAction384
                         // Insert post-action code here
                         
                        break;//GEN-BEGIN:MVDCACase384
                }
             }
        } else if (displayable == tbError) {
            if (command == cmdMenu) {//GEN-END:MVDCACase384
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
        }//GEN-END:MVDCACase417
// Insert global post-action code here
         
}//GEN-LINE:MVDCAEnd
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
    /** This method initializes UI of the application.//GEN-BEGIN:MVDInitBegin
     */
    private void initialize() {//GEN-END:MVDInitBegin
         // Insert pre-init code here
         if (database.vip)
             getDisplay().setCurrent(get_lstMenu());
         else
             getDisplay().setCurrent(get_cvsSplashScreen());
//GEN-LINE:MVDInitInit
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
            lstMenu = new List("Handy Geocaching light", Choice.IMPLICIT, new String[] {//GEN-BEGIN:MVDGetInit5
                "Vyhled\u00E1v\u00E1n\u00ED",
                "Obl\u00EDben\u00E9",
                "Dal\u0161\u00ED funkce",
                "TB/GC",
                "Nastaven\u00ED",
                "O aplikaci",
                "Konec"
            }, new Image[] {
                get_imgSearch(),
                get_imgPosition(),
                get_imgGPS(),
                get_imgTbGc(),
                get_imgSettings(),
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
            });//GEN-END:MVDGetInit5
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd5
        return lstMenu;
    }//GEN-END:MVDGetEnd5
    
    
    
    
    
    
    
    
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
    
    
    
    
    
    
    
    
    
    
    
    
    /** This method returns instance for frmLoading component and should be called instead of accessing frmLoading field directly.//GEN-BEGIN:MVDGetBegin53
     * @return Instance for frmLoading component
     */
    public Form get_frmLoading() {
        if (frmLoading == null) {//GEN-END:MVDGetBegin53
            // Insert pre-init code here
            frmLoading = new Form("Pros\u00EDm \u010Dekejte", new Item[] {get_siMessage()});//GEN-BEGIN:MVDGetInit53
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
            lstNearestCaches = new List("Nejbli\u017E\u0161\u00ED cache", Choice.IMPLICIT, new String[] {"\u017D\u00E1dn\u00E1 data"}, new Image[] {null});//GEN-BEGIN:MVDGetInit71
            lstNearestCaches.addCommand(get_cmdBack());
            lstNearestCaches.setCommandListener(this);
            lstNearestCaches.setSelectedFlags(new boolean[] {false});//GEN-END:MVDGetInit71
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
            frmOverview = new Form("Detaily cache", new Item[] {//GEN-BEGIN:MVDGetInit90
                get_siName(),
                get_siAuthor(),
                get_siType(),
                get_siSize(),
                get_siOverviewLattitude(),
                get_siOverviewLongitude(),
                get_siDifficulty(),
                get_siWaypoint(),
                get_siInventory()
            });
            frmOverview.addCommand(get_cmdBack());
            frmOverview.addCommand(get_cmdHint());
            frmOverview.addCommand(get_cmdInfo());
            frmOverview.addCommand(get_cmdLogs());
            frmOverview.addCommand(get_cmdWaypoints());
            frmOverview.addCommand(get_cmdNext());
            frmOverview.addCommand(get_cmdLogIt());
            frmOverview.addCommand(get_cmdFavourite());
            frmOverview.addCommand(get_cmdDownloadPatterns());
            frmOverview.addCommand(get_cmdRefresh());
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
            siName = new StringItem("N\u00E1zev", "");//GEN-LINE:MVDGetInit94
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
            siAuthor = new StringItem("Autor", "");//GEN-LINE:MVDGetInit95
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
            siWaypoint = new StringItem("Waypoint", "");//GEN-LINE:MVDGetInit96
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
            siType = new StringItem("Typ", "");//GEN-LINE:MVDGetInit97
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
            siSize = new StringItem("Velikost", "");//GEN-LINE:MVDGetInit98
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
            siDifficulty = new StringItem("Obt\u00ED\u017Enost/Ter\u00E9n", "");//GEN-LINE:MVDGetInit100
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
            siInventory = new StringItem("Invent\u00E1\u0159", "");//GEN-LINE:MVDGetInit101
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
            frmInfo = new Form("Podrobnosti", new Item[] {//GEN-BEGIN:MVDGetInit110
                get_siBegin(),
                get_siContent(),
                get_siEnd()
            });
            frmInfo.addCommand(get_cmdBack());
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
                imgSearch = Image.createImage("/nearest.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit120
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd120
        return imgSearch;
    }//GEN-END:MVDGetEnd120
    
    /** This method returns instance for imgListing component and should be called instead of accessing imgListing field directly.//GEN-BEGIN:MVDGetBegin121
     * @return Instance for imgListing component
     */
    public Image get_imgListing() {
        if (imgListing == null) {//GEN-END:MVDGetBegin121
            // Insert pre-init code here
            try {//GEN-BEGIN:MVDGetInit121
                imgListing = Image.createImage("/waypoint.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit121
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd121
        return imgListing;
    }//GEN-END:MVDGetEnd121
    
    /** This method returns instance for imgGPS component and should be called instead of accessing imgGPS field directly.//GEN-BEGIN:MVDGetBegin122
     * @return Instance for imgGPS component
     */
    public Image get_imgGPS() {
        if (imgGPS == null) {//GEN-END:MVDGetBegin122
            // Insert pre-init code here
            try {//GEN-BEGIN:MVDGetInit122
                imgGPS = Image.createImage("/gps.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit122
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd122
        return imgGPS;
    }//GEN-END:MVDGetEnd122
    
    /** This method returns instance for imgAveraging component and should be called instead of accessing imgAveraging field directly.//GEN-BEGIN:MVDGetBegin123
     * @return Instance for imgAveraging component
     */
    public Image get_imgAveraging() {
        if (imgAveraging == null) {//GEN-END:MVDGetBegin123
            // Insert pre-init code here
            try {//GEN-BEGIN:MVDGetInit123
                imgAveraging = Image.createImage("/prumerovani.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit123
            // Insert post-init code here
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
                imgAbout = Image.createImage("/o_aplikaci.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit124
            // Insert post-init code here
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
                imgExit = Image.createImage("/konec.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit125
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd125
        return imgExit;
    }//GEN-END:MVDGetEnd125
    
    /** This method returns instance for frmAbout component and should be called instead of accessing frmAbout field directly.//GEN-BEGIN:MVDGetBegin126
     * @return Instance for frmAbout component
     */
    public Form get_frmAbout() {
        if (frmAbout == null) {//GEN-END:MVDGetBegin126
            // Insert pre-init code here
            frmAbout = new Form("Handy Geocaching light", new Item[] {//GEN-BEGIN:MVDGetInit126
                get_siVerze(),
                get_stringItem1(),
                get_siDonate()
            });
            frmAbout.addCommand(get_cmdBack());
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
            stringItem1 = new StringItem("O aplikaci:", "Tuto aplikaci sponzoruje Axima spol. s.r.o., Palack\u00E9ho t\u0159\u00EDda 16, 61200 Brno.\n\nAplikaci vytvo\u0159il David V\u00E1vra (Destil). Kontakt: me@destil.cz\n\nV p\u0159\u00EDpad\u011B probl\u00E9m\u016F a pro v\u00EDce informac\u00ED nav\u0161tivte str\u00E1nky http://www.destil.cz");//GEN-LINE:MVDGetInit128
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
                imgSettings = Image.createImage("/kladivko.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit140
            // Insert post-init code here
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
                get_cgOtherSettings(),
                get_tfNumberCaches(),
                get_tfDefaultLog(),
                get_stringItem2()
            });
            frmSettings.addCommand(get_cmdBack());
            frmSettings.addCommand(get_cmdSave());
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
    
    /** This method returns instance for cgOtherSettings component and should be called instead of accessing cgOtherSettings field directly.//GEN-BEGIN:MVDGetBegin148
     * @return Instance for cgOtherSettings component
     */
    public ChoiceGroup get_cgOtherSettings() {
        if (cgOtherSettings == null) {//GEN-END:MVDGetBegin148
            // Insert pre-init code here
            cgOtherSettings = new ChoiceGroup("Nejbli\u017E\u0161\u00ED cache:", Choice.MULTIPLE, new String[] {//GEN-BEGIN:MVDGetInit148
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
            cgOtherSettings.setSelectedFlags(new boolean[] {
                false,
                false,
                false,
                false,
                false,
                false
            });//GEN-END:MVDGetInit148
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd148
        return cgOtherSettings;
    }//GEN-END:MVDGetEnd148
    
    
    
    
    
    
    
    
    
    
    
    
    
    /** This method returns instance for stringItem6 component and should be called instead of accessing stringItem6 field directly.//GEN-BEGIN:MVDGetBegin169
     * @return Instance for stringItem6 component
     */
    public StringItem get_stringItem6() {
        if (stringItem6 == null) {//GEN-END:MVDGetBegin169
            // Insert pre-init code here
            stringItem6 = new StringItem("Form\u00E1t:", "N ss\u00B0 mm.mmm E sss\u00B0 mm.mmm");//GEN-LINE:MVDGetInit169
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
                imgNavigate = Image.createImage("/sipka.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit179
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd179
        return imgNavigate;
    }//GEN-END:MVDGetEnd179
    
    
    
    
    
    
    
    
    
    
    /** This method returns instance for cmdLogIt component and should be called instead of accessing cmdLogIt field directly.//GEN-BEGIN:MVDGetBegin202
     * @return Instance for cmdLogIt component
     */
    public Command get_cmdLogIt() {
        if (cmdLogIt == null) {//GEN-END:MVDGetBegin202
            // Insert pre-init code here
            cmdLogIt = new Command("Zalogovat", Command.SCREEN, 8);//GEN-LINE:MVDGetInit202
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd202
        return cmdLogIt;
    }//GEN-END:MVDGetEnd202
    
    /** This method returns instance for frmLogIt component and should be called instead of accessing frmLogIt field directly.//GEN-BEGIN:MVDGetBegin203
     * @return Instance for frmLogIt component
     */
    public Form get_frmLogIt() {
        if (frmLogIt == null) {//GEN-END:MVDGetBegin203
            // Insert pre-init code here
            frmLogIt = new Form("Zalogovat cache", new Item[] {//GEN-BEGIN:MVDGetInit203
                get_cgLogType(),
                get_tfLogText()
            });
            frmLogIt.addCommand(get_cmdSend());
            frmLogIt.addCommand(get_cmdBack());
            frmLogIt.setCommandListener(this);//GEN-END:MVDGetInit203
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd203
        return frmLogIt;
    }//GEN-END:MVDGetEnd203
    
    /** This method returns instance for cgLogType component and should be called instead of accessing cgLogType field directly.//GEN-BEGIN:MVDGetBegin204
     * @return Instance for cgLogType component
     */
    public ChoiceGroup get_cgLogType() {
        if (cgLogType == null) {//GEN-END:MVDGetBegin204
            // Insert pre-init code here
            cgLogType = new ChoiceGroup("Typ logu:", Choice.EXCLUSIVE, new String[0], new Image[0]);//GEN-BEGIN:MVDGetInit204
            cgLogType.setSelectedFlags(new boolean[0]);//GEN-END:MVDGetInit204
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd204
        return cgLogType;
    }//GEN-END:MVDGetEnd204
    
    /** This method returns instance for tfLogText component and should be called instead of accessing tfLogText field directly.//GEN-BEGIN:MVDGetBegin205
     * @return Instance for tfLogText component
     */
    public TextField get_tfLogText() {
        if (tfLogText == null) {//GEN-END:MVDGetBegin205
            // Insert pre-init code here
            tfLogText = new TextField("Text logu:", null, 1000, TextField.ANY);//GEN-LINE:MVDGetInit205
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd205
        return tfLogText;
    }//GEN-END:MVDGetEnd205
    
    /** This method returns instance for imgKeyword component and should be called instead of accessing imgKeyword field directly.//GEN-BEGIN:MVDGetBegin212
     * @return Instance for imgKeyword component
     */
    public Image get_imgKeyword() {
        if (imgKeyword == null) {//GEN-END:MVDGetBegin212
            // Insert pre-init code here
            try {//GEN-BEGIN:MVDGetInit212
                imgKeyword = Image.createImage("/keyword.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit212
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd212
        return imgKeyword;
    }//GEN-END:MVDGetEnd212
    
    /** This method returns instance for imgPosition component and should be called instead of accessing imgPosition field directly.//GEN-BEGIN:MVDGetBegin215
     * @return Instance for imgPosition component
     */
    public Image get_imgPosition() {
        if (imgPosition == null) {//GEN-END:MVDGetBegin215
            // Insert pre-init code here
            try {//GEN-BEGIN:MVDGetInit215
                imgPosition = Image.createImage("/position.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit215
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd215
        return imgPosition;
    }//GEN-END:MVDGetEnd215
    
    /** This method returns instance for imgPaper component and should be called instead of accessing imgPaper field directly.//GEN-BEGIN:MVDGetBegin216
     * @return Instance for imgPaper component
     */
    public Image get_imgPaper() {
        if (imgPaper == null) {//GEN-END:MVDGetBegin216
            // Insert pre-init code here
            try {//GEN-BEGIN:MVDGetInit216
                imgPaper = Image.createImage("/listing.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit216
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd216
        return imgPaper;
    }//GEN-END:MVDGetEnd216
    
    /** This method returns instance for tfNumberCaches component and should be called instead of accessing tfNumberCaches field directly.//GEN-BEGIN:MVDGetBegin218
     * @return Instance for tfNumberCaches component
     */
    public TextField get_tfNumberCaches() {
        if (tfNumberCaches == null) {//GEN-END:MVDGetBegin218
            // Insert pre-init code here
            tfNumberCaches = new TextField("Po\u010Det ke\u0161\u00ED:", "10", 120, TextField.NUMERIC);//GEN-LINE:MVDGetInit218
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
            lstKeyword = new List("Nalezen\u00E9 cache:", Choice.IMPLICIT, new String[] {"\u017D\u00E1dn\u00E1 data"}, new Image[] {null});//GEN-BEGIN:MVDGetInit223
            lstKeyword.addCommand(get_cmdBack());
            lstKeyword.setCommandListener(this);
            lstKeyword.setSelectedFlags(new boolean[] {false});//GEN-END:MVDGetInit223
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
                "Nejbli\u017E\u0161\u00ED",
                "Waypoint",
                "Kl\u00ED\u010Dov\u00E9 slovo"
            }, new Image[] {
                get_imgNearest(),
                get_imgListing(),
                get_imgKeyword()
            });
            lstSearch.addCommand(get_cmdBack());
            lstSearch.setCommandListener(this);
            lstSearch.setSelectedFlags(new boolean[] {
                false,
                false,
                false
            });//GEN-END:MVDGetInit226
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
                imgNearest = Image.createImage("/navigate.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit234
            // Insert post-init code here
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
                "De\u0161ifr\u00E1tor"
            }, new Image[] {
                get_imgMulti(),
                get_imgDecypher()
            });
            lstGPS.addCommand(get_cmdBack());
            lstGPS.setCommandListener(this);
            lstGPS.setSelectedFlags(new boolean[] {
                false,
                false
            });//GEN-END:MVDGetInit237
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
            siOverviewLattitude = new StringItem("Sou\u0159adnice", "");//GEN-LINE:MVDGetInit249
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
            lstFavourites.addCommand(get_cmdAddGiven());
            lstFavourites.addCommand(get_cmdDelete());
            lstFavourites.addCommand(get_cmdEdit());
            lstFavourites.addCommand(get_cmdMultiSolver());
            lstFavourites.setCommandListener(this);
            lstFavourites.setSelectedFlags(new boolean[0]);//GEN-END:MVDGetInit251
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
            cmdSelect = new Command("Zobrazit", Command.SCREEN, 1);//GEN-LINE:MVDGetInit258
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
                get_siDescription()
            });
            frmFavourite.addCommand(get_cmdBack());
            frmFavourite.addCommand(get_cmdNext());
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
    
    
    /** This method returns instance for cmdAddGiven component and should be called instead of accessing cmdAddGiven field directly.//GEN-BEGIN:MVDGetBegin268
     * @return Instance for cmdAddGiven component
     */
    public Command get_cmdAddGiven() {
        if (cmdAddGiven == null) {//GEN-END:MVDGetBegin268
            // Insert pre-init code here
            cmdAddGiven = new Command("+Zadan\u00FD bod", Command.SCREEN, 3);//GEN-LINE:MVDGetInit268
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
            cmdDelete = new Command("Smazat", Command.SCREEN, 4);//GEN-LINE:MVDGetInit271
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
                get_stringItem4(),
                get_tfGivenName(),
                get_tfGivenDescription()
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
            stringItem4 = new StringItem("Form\u00E1t:", "N ss\u00B0 mm.mmm E sss\u00B0 mm.mmm");//GEN-LINE:MVDGetInit276
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
            cmdEdit = new Command("Upravit", Command.SCREEN, 5);//GEN-LINE:MVDGetInit284
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd284
        return cmdEdit;
    }//GEN-END:MVDGetEnd284
    
    /** This method returns instance for imgTbGc component and should be called instead of accessing imgTbGc field directly.//GEN-BEGIN:MVDGetBegin288
     * @return Instance for imgTbGc component
     */
    public Image get_imgTbGc() {
        if (imgTbGc == null) {//GEN-END:MVDGetBegin288
            // Insert pre-init code here
            try {//GEN-BEGIN:MVDGetInit288
                imgTbGc = Image.createImage("/tb.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit288
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd288
        return imgTbGc;
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
            frmTrackable.addCommand(get_cmdLogIt());
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
    
    /** This method returns instance for frmLogTrackable component and should be called instead of accessing frmLogTrackable field directly.//GEN-BEGIN:MVDGetBegin304
     * @return Instance for frmLogTrackable component
     */
    public Form get_frmLogTrackable() {
        if (frmLogTrackable == null) {//GEN-END:MVDGetBegin304
            // Insert pre-init code here
            frmLogTrackable = new Form("Zalogovat TB", new Item[] {//GEN-BEGIN:MVDGetInit304
                get_cgTrLogType(),
                get_tfTrLogText()
            });
            frmLogTrackable.addCommand(get_cmdBack());
            frmLogTrackable.addCommand(get_cmdLogIt());
            frmLogTrackable.setCommandListener(this);//GEN-END:MVDGetInit304
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd304
        return frmLogTrackable;
    }//GEN-END:MVDGetEnd304
    
    /** This method returns instance for cgTrLogType component and should be called instead of accessing cgTrLogType field directly.//GEN-BEGIN:MVDGetBegin305
     * @return Instance for cgTrLogType component
     */
    public ChoiceGroup get_cgTrLogType() {
        if (cgTrLogType == null) {//GEN-END:MVDGetBegin305
            // Insert pre-init code here
            cgTrLogType = new ChoiceGroup("Typ logu", Choice.EXCLUSIVE, new String[0], new Image[0]);//GEN-BEGIN:MVDGetInit305
            cgTrLogType.setSelectedFlags(new boolean[0]);//GEN-END:MVDGetInit305
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd305
        return cgTrLogType;
    }//GEN-END:MVDGetEnd305
    
    /** This method returns instance for tfTrLogText component and should be called instead of accessing tfTrLogText field directly.//GEN-BEGIN:MVDGetBegin306
     * @return Instance for tfTrLogText component
     */
    public TextField get_tfTrLogText() {
        if (tfTrLogText == null) {//GEN-END:MVDGetBegin306
            // Insert pre-init code here
            tfTrLogText = new TextField("Text logu", null, 120, TextField.ANY);//GEN-LINE:MVDGetInit306
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd306
        return tfTrLogText;
    }//GEN-END:MVDGetEnd306
    
    
    
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
    /** This method returns instance for imgMulti component and should be called instead of accessing imgMulti field directly.//GEN-BEGIN:MVDGetBegin332
     * @return Instance for imgMulti component
     */
    public Image get_imgMulti() {
        if (imgMulti == null) {//GEN-END:MVDGetBegin332
            // Insert pre-init code here
            try {//GEN-BEGIN:MVDGetInit332
                imgMulti = Image.createImage("/3.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit332
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd332
        return imgMulti;
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
            tfLetter = new TextField("P\u00EDsmeno", null, 1, TextField.ANY);//GEN-LINE:MVDGetInit343
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
    
    
    
    
    
    
    
    
    /** This method returns instance for tfDefaultLog component and should be called instead of accessing tfDefaultLog field directly.//GEN-BEGIN:MVDGetBegin370
     * @return Instance for tfDefaultLog component
     */
    public TextField get_tfDefaultLog() {
        if (tfDefaultLog == null) {//GEN-END:MVDGetBegin370
            // Insert pre-init code here
            tfDefaultLog = new TextField("V\u00FDchoz\u00ED log:", "Zalogov\u00E1no pomoc\u00ED aplikace Handy Geocaching (http://www.destil.cz)", 120, TextField.ANY);//GEN-LINE:MVDGetInit370
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd370
        return tfDefaultLog;
    }//GEN-END:MVDGetEnd370
    
    /** This method returns instance for imgGPSGate component and should be called instead of accessing imgGPSGate field directly.//GEN-BEGIN:MVDGetBegin373
     * @return Instance for imgGPSGate component
     */
    public Image get_imgGPSGate() {
        if (imgGPSGate == null) {//GEN-END:MVDGetBegin373
            // Insert pre-init code here
            try {//GEN-BEGIN:MVDGetInit373
                imgGPSGate = Image.createImage("/gpsgate.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit373
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd373
        return imgGPSGate;
    }//GEN-END:MVDGetEnd373
    
    /** This method returns instance for imgBluetooth component and should be called instead of accessing imgBluetooth field directly.//GEN-BEGIN:MVDGetBegin374
     * @return Instance for imgBluetooth component
     */
    public Image get_imgBluetooth() {
        if (imgBluetooth == null) {//GEN-END:MVDGetBegin374
            // Insert pre-init code here
            try {//GEN-BEGIN:MVDGetInit374
                imgBluetooth = Image.createImage("/blutooth.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit374
            // Insert post-init code here
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
            lstPatterns.setCommandListener(this);
            lstPatterns.setSelectedFlags(new boolean[] {false});//GEN-END:MVDGetInit375
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
    
    /** This method returns instance for stringItem2 component and should be called instead of accessing stringItem2 field directly.//GEN-BEGIN:MVDGetBegin389
     * @return Instance for stringItem2 component
     */
    public StringItem get_stringItem2() {
        if (stringItem2 == null) {//GEN-END:MVDGetBegin389
            // Insert pre-init code here
            stringItem2 = new StringItem("", "Pozn.: Vlo\u017Een\u00EDm \'%t\' se do logu vlo\u017E\u00ED aktu\u00E1ln\u00ED \u010Das. ");//GEN-LINE:MVDGetInit389
            // Insert post-init code here
        }//GEN-BEGIN:MVDGetEnd389
        return stringItem2;
    }//GEN-END:MVDGetEnd389
    
    
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
                imgDecypher = Image.createImage("/decypher.png");
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }//GEN-END:MVDGetInit413
            // Insert post-init code here
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
    
    public SplashScreen get_cvsSplashScreen()
    {
        if (cvsSplashScreen == null)
        {
            cvsSplashScreen = new SplashScreen();
            cvsSplashScreen.addCommand(get_cmdSend());
            cvsSplashScreen.setCommandListener(this);
        }
        return cvsSplashScreen;
    }
    
    public void startApp()
    {
    }
    
    public void pauseApp()
    {
    }
    
    public void destroyApp(boolean unconditional)
    {
    }
    
    /**
     * Tato metoda se pouziva k zobrazovani vyjimek, ktere mohou v aplikaci nastat
     */
    public void showError(String section, String errorMessage, String data)
    {
        //vymazani chyb
        if (data.equals("Chyba"))
        {
            get_tbError().setString("Problém s komunikačním skriptem,opakujte akci.Pokud se tato chyba ukazuje pořád,máte nefunkční GPRS.");
        }
        else
        {
            get_tbError().setString("Popis chyby:\n\nSekce: " + section + "\nDruh: " + errorMessage + "\nData: '" +
                    data +"'");
        }
        getDisplay().setCurrent(get_tbError());
    }
    
    /**
     * Tato metoda se pouziva k zobrazovani alertu v aplikaci
     */
    public void showAlert(String text, AlertType type, Displayable next)
    {
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
        getDisplay().setCurrent(alert, next);
    }
    
    /**
     * Tato metoda smaze dane elementy v danem Formu nebo Listu
     */
    public void clearListForm(List list, Form form)
    {
        int j;
        int size = 0;
        if (list != null)
        {
            size = list.size();
        }
        if (form != null)
        {
            size = form.size();
        }
        if (size > 0)
        {
            for (j = 0; j < size; j++)
            {
                if (list != null)
                {
                    list.delete(0);
                }
                if (form != null)
                {
                    form.delete(0);
                }
            }
        }
    }
    
    /**
     * Tato funkce vrati cislo prvniho zaskrtnuteho policka, pokud neni zaskrtnuty nic tak -1
     */
    public int firstCheckedFavourite()
    {
        int selected = lstFavourites.getSelectedIndex();
        if (selected==-1) //vybere prvni zaskrtnuty policko
        {
            for (int i=0;i<lstFavourites.size();i++)
            {
                if (lstFavourites.isSelected(i))
                {
                    selected = i;
                    break;
                }
            }
        }
        return selected;
    }
    
    public class SplashScreen extends Canvas implements Runnable
    {
        public Image image;
        private Thread runner;
        
        public SplashScreen()
        {
            try
            {
                image = Image.createImage("/reklama.png");
            }
            catch (Exception e)
            {
                showError("tvoreni sipky", e.toString(), "");
            }
            runner = new Thread(this);
            runner.start();
        }
        
        public void paint(Graphics g)
        {
            int width = getWidth();
            int height = getHeight();
            g.setColor(0xffffff);
            g.fillRect(0, 0, width, height);
            g.setColor(0);
            g.drawImage(image,width/2,height/2-23,Graphics.TOP|Graphics.HCENTER);
        }
        
        public void run()
        {
            try
            {
                runner.sleep(2000);
            }
            catch (InterruptedException ex)
            {
                ex.printStackTrace();
            }
            getDisplay().setCurrent(get_lstMenu());
        }
    }
    
    /**
     * Tato metoda smaze dane elementy v danem ChoiceGroupu
     */
    public void clearChoiceGroup(ChoiceGroup cg)
    {
        int j;
        int size = 0;
        if (cg != null)
        {
            size = cg.size();
        }
        if (size != 0)
        {
            for (j = 0; j < size; j++)
            {
                cg.delete(0);
            }
        }
    }
    
    /////////////--------------- Field Note
    
    public static String getFieldNote(String gcId, int type, String text) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        c.setTime(new Date());
                
        StringBuffer sb = new StringBuffer();
        
        sb.append(gcId);
        sb.append(',');
        
        sb.append(c.get(Calendar.YEAR)).append('-');
        sb.append(nulaNula(c.get(Calendar.MONTH) + 1)).append('-');
        sb.append(nulaNula(c.get(Calendar.DAY_OF_MONTH))).append('T');
        sb.append(nulaNula(c.get(Calendar.HOUR_OF_DAY))).append(':');
        sb.append(nulaNula(c.get(Calendar.MINUTE))).append('Z');
        
        sb.append(',');
        sb.append(getFieldNoteTypeString(type));
        sb.append(',').append('"');
        sb.append(text.replace('"','\''));
        sb.append('"');
        
        return sb.toString();
    }
    
    public static String getFieldNoteTypeString(int type) {
        switch(type) {
            case TYPE_DIDN_T_FIND_IT:
                return "Didn't find it";
            case TYPE_WRITE_NOTE:
                return "Write note";
            case TYPE_NEEDS_ARCHIVED:
                return "Needs archived";
            case TYPE_NEEDS_MAINTENANCE:
                return "Needs maintenance";
            default:
                return "Found it";
        }
    }
    
    private static String nulaNula(int v) {
        if (v < 10)
            return "0"+Integer.toString(v, 10);
        return Integer.toString(v);
    }
    
    public static String getDateString() {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        c.setTime(new Date());
                
        StringBuffer sb = new StringBuffer();
        sb.append(nulaNula(c.get(Calendar.HOUR_OF_DAY))).append(':');
        sb.append(nulaNula(c.get(Calendar.MINUTE)));
        return sb.toString();
    }
    
}
