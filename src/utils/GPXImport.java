/*
 * Import.java
 *
 * Created on 4. červenec 2009, 10:28
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package utils;

import database.Favourites;
import http.Http;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.StringItem;
import kxml2.KXmlParser;
import kxml2.xmlpull.XmlPullParser;
import kxml2.xmlpull.XmlPullParserException;

/**
 *
 * @author Administrator
 */
public class GPXImport extends Form implements CommandListener {
    private Favourites favourites;
    private static final String GPX_NS = "http://www.topografix.com/GPX/1/1";
    private static final String GROUNDSPEAK_NS = "http://www.groundspeak.com/cache/1/0";
    public static final Command SUCCESS = new Command("SUCCESS", Command.OK, 0);
    public static final Command CANCEL = new Command("Storno", Command.BACK, 0);
    private StringItem siImportCacheCount;
    
    private Display display;
    private CommandListener listener = null;

    private boolean trucking;
    
    private Http http;
    
    /** Creates a new instance of Import */
    public GPXImport(Favourites favourites, Display display, Http http) {
        super("Import z GPX");
        
        this.favourites = favourites;
        this.display = display;
        this.http = http;
        
        append("Importuji z GPX...");
        append(new Gauge("", false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING));
        siImportCacheCount = new StringItem("Objektů naimportováno:","0");
        append(siImportCacheCount);
        
        addCommand(CANCEL);
        setCommandListener(this);
    }

    public CommandListener getListener() {
        return listener;
    }

    public void setListener(CommandListener listener) {
        this.listener = listener;
    }
       
    public void parse(final String fileName) {
        display.setCurrent(this);
        
        Thread t = new Thread() {
            public void run() {
                try {
                    System.out.println(fileName);
                    FileConnection file = (FileConnection) Connector.open(fileName, Connector.READ);
                    parse(file.openInputStream());
                } catch (XmlPullParserException ex) {
                    ex.printStackTrace();
                    favourites.revalidate();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    favourites.revalidate();
                }
                
            }
        };
        t.start();
    }
       
    private void parse(InputStream in) throws IOException, XmlPullParserException {
        KXmlParser parser = new KXmlParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        parser.setInput(in, "UTF-8");
        //ParseEvent pe = null;
        
        //System.out.println("Search gpx tag..");
        parser.next();
        parser.require(XmlPullParser.START_TAG, null, "gpx");
        //System.out.println("gpx tag found");
        
        String parts[][] = new String[1][15];
        
        trucking = true;
        int count = 0;
        while (trucking) {
            parser.next();
            if (parser.getEventType() == XmlPullParser.START_TAG) {
                String tagName = parser.getName();
                
                if (tagName.equals("wpt")) {
                    //System.out.println("wpt tag found");
                    String difficulty = "";
                    String terrain = "";
                    String comment = "";
                    String WayPointName = "";
                    String hint = "";
                    String listing = "";
                    for (int i=0; i < parts[0].length; i++)
                        parts[0][i] = "";
                    
                    parts[0][10] = "waypoint";
                    parts[0][14] = "0";
                    
                    //for (int i=0; i<parser.getAttributeCount(); i++) {
                    //    System.out.println("{"+parser.getAttributeNamespace(i)+"}"+parser.getAttributeName(i)+"="+parser.getAttributeValue(i));
                    //}
                    
                    parts[0][4] = getFriendlyLatLon(parser.getAttributeValue("", "lat"), true); //latitude
                    parts[0][5] = getFriendlyLatLon(parser.getAttributeValue("", "lon"), false); //longitude
                    
                    while ((parser.getEventType() != XmlPullParser.END_TAG) || (parser.getName().equals(tagName) == false)) {
                        parser.next();
                        
                        if (parser.getEventType() == XmlPullParser.START_TAG) {
                            if (!parser.getNamespace().equals(GROUNDSPEAK_NS)) {
                                if (parser.getName().equals("name")) {
                                    parser.next();
                                    parts[0][7] = parser.getText(); //gcCode
                                    WayPointName = parser.getText();
                                } else if (parser.getName().equals("type")) {
                                    parser.next();
                                    parts[0][10] = convertGPXTypeToTypeID(parser.getText()); //typeIconID gc_xxx
                                } else if (parser.getName().equals("desc")) {
                                    parser.next();
                                    comment = (comment.length() > 0) ? parser.getText() + "\r\n" + comment : parser.getText(); //comment
                                } else if (parser.getName().equals("cmt")) {
                                    parser.next();
                                    comment+= (comment.length() > 0) ? "\r\n" + parser.getText() : parser.getText(); //comment
                                }
                            } else {
                                if (parser.getName().equals("cache")) {
                                    parts[0][9] = ""; // disabled/archived
                                    String available = parser.getAttributeValue(GROUNDSPEAK_NS, "available");
                                    String archived = parser.getAttributeValue(GROUNDSPEAK_NS, "archived");
                                    if (available != null && available.toLowerCase().equals("false")) {
                                        parts[0][9] = "disabled";
                                    }
                                    if (archived != null && archived.toLowerCase().equals("false")) {
                                        parts[0][9] = "archived";
                                    }
                                } else if (parser.getName().equals("name")) {
                                    parser.next();
                                    parts[0][0] = parser.getText(); //cache name
                                } else if (parser.getName().equals("placed_by")) {
                                    parser.next();
                                    parts[0][1] = parser.getText();
                                } else if (parser.getName().equals("type")) {
                                    parser.next();
                                    parts[0][2] = parser.getText(); //type name
                                } else if (parser.getName().equals("container")) {
                                    parser.next();
                                    parts[0][3] = parser.getText(); //container size
                                } else if (parser.getName().equals("difficulty")) {
                                    parser.next();
                                    difficulty = parser.getText(); //dificulty
                                } else if (parser.getName().equals("terrain")) {
                                    parser.next();
                                    terrain = parser.getText(); //terrain
                                } else if (parser.getName().equals("long_description")) {
                                    parser.next();
                                    listing = parser.getText();
                                    parts[0][14] = Integer.toString(listing.length() / 1024);
                                    parts[0][13] = (listing.indexOf("<!--Handy") != -1) ? "1":"0";
                                } else if (parser.getName().equals("encoded_hints")) {
                                    parser.next();
                                    hint = parser.getText();
                                    
                                    parts[0][12] = (hint.length() > 0) ? "1":"0";
                                    //comment+= ((comment.length() > 0) ? "\r\n" : "") + parser.getText();
                                } else if (parser.getName().equals("logs")) {
                                    while (parser.getEventType() != XmlPullParser.END_TAG || !parser.getName().equals("logs") || !parser.getNamespace().equals(GROUNDSPEAK_NS))
                                        parser.next();
                                }
                            }
                        }
                    }
                    parts[0][8] = ""; // inventory
                    parts[0][11] = "1"; // has waipoints
                    parts[0][6] = difficulty + "/" + terrain;
                    
                    //System.out.println("adding cache");
                    //System.out.println(parts[0][10].equals("waypoint"));
                    
                    favourites.editId = -1;
                    
                    if (parts[0][10].equals("waypoint")) {
                        favourites.addEdit(WayPointName, "", parts[0][4], parts[0][5], parts[0][10], null, false, "", comment, false, false, false);
                    } else {
                        favourites.addEdit(parts[0][0], Favourites.cachePartsToDesc(parts), parts[0][4], parts[0][5], parts[0][10], null, false, "", comment, false, false, false);
                        http.getHintCache().add(parts[0][7], hint);
                        http.getListingCache().add(parts[0][7], listing);
                    }
                    count++;
                    
                    if (count % 10 == 0)
                        siImportCacheCount.setText(Integer.toString(count));
                } else {
                    while ((parser.getEventType() != XmlPullParser.END_TAG) || (parser.getName().equals(tagName) == false))
                        parser.next();
                }
            }
            if ((parser.getEventType() == XmlPullParser.END_TAG && parser.getName().equals("gpx")) || parser.getEventType() == XmlPullParser.END_DOCUMENT) {
                trucking = false;
                favourites.revalidate();
                
                if (listener != null)
                    listener.commandAction(SUCCESS, this);               
                
                return;
            }
        }
        trucking = false;
    }
    
    public boolean isParsing() {
        return trucking;
    }
    
    public void stop() {
        trucking = false;
        favourites.revalidate();
    }
            
    
    private static String convertGPXTypeToType(String type) {
        return type.substring(type.indexOf('|') + 1);
    }
    
    private static String convertGPXTypeToTypeID(String type) {
        String name = type.substring(type.indexOf('|') + 1).toLowerCase();
        type = type.substring(0, type.indexOf('|'));
        
        if (type.equalsIgnoreCase("waypoint")) {
            return "waypoint";
        } else if (name.startsWith("traditional")) {
            return "gc_traditional";
        } else if (name.startsWith("multi")) {
            return "gc_multi";
        } else if (name.startsWith("mystery")) {
            return "gc_unknown";
        } else if (name.startsWith("earth")) {
            return "gc_earthcache";
        } else if (name.startsWith("event")) {
            return "gc_event";
        } else if (name.startsWith("cito")) {
            return "gc_cito";
        } else if (name.startsWith("webcam")) {
            return "gc_webcam";
        } else if (name.startsWith("letter")) {
            return "gc_letter";
        } else if (name.startsWith("virtual")) {
            return "gc_vistual";
        } else if (name.startsWith("locationless")) {
            return "gc_locationless";
        }
        
        return "gc_unknown";
    }
    
    private static String getFriendlyLatLon(String in, boolean isLat) {
        in = in.replace(',','.');
        
        double tmp = Double.parseDouble(in);
        int degree = (int) tmp;
        int minute = (int) (Math.abs(tmp - degree) * 60);
        tmp = (Math.abs(tmp) - degree) * 60 - minute;
        
        int fraction = (int) (tmp * 1000);
        
        
        char direction = ' ';
        if (isLat && degree >= 0) {
            direction = 'N';
        } else if (isLat && degree < 0) {
            direction = 'S';
        } else if (!isLat && degree >= 0) {
            direction = 'E';
        } else if (!isLat && degree < 0) {
            direction = 'W';
        }
        
        degree = Math.abs(degree);
        
        String friendlyFraction = Utils.addZeros(Integer.toString(fraction), 3);
        String friendlyMinute = Utils.addZeros(Integer.toString((int) minute), 2);
        String friendlyDegree = Utils.addZeros(Integer.toString((int) degree), (isLat) ? 2 : 3);
        
        return direction+" "+friendlyDegree+"° "+friendlyMinute+"."+friendlyFraction;
    }

    public void commandAction(Command command, Displayable displayable) {
       if (command == CANCEL) {
           stop();
           if (listener != null)
               listener.commandAction(CANCEL, this);
           //display.setCurrent(backScreen);
       }
    }
    
}