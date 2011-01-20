/*
 * GPXImport.java
 * This file is part of HandyGeocaching.
 *
 * HandyGeocaching is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * (read more at: http://www.gnu.org/licenses/gpl.html)
 */
package utils;

import database.Favourites;
import gps.Gps;
import gui.Gui;
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
 * Trida, ktera provadi import kesi a bodu z GPX formatu.
 * @author Arcao
 */
public class GPXImport extends Form implements CommandListener {
    private Favourites favourites;
    private static final String GPX_NS1 = "http://www.topografix.com/GPX/1/0";
    private static final String GPX_NS2 = "http://www.topografix.com/GPX/1/1";
    private static final String GROUNDSPEAK_NS = "http://www.groundspeak.com/cache/1/";
    private static final String GROUNDSPEAK_NS1 = "http://www.groundspeak.com/cache/1/0";
    private static final String GROUNDSPEAK_NS2 = "http://www.groundspeak.com/cache/1/0/1";
    private static final String GROUNDSPEAK_NS3 = "http://www.groundspeak.com/cache/1/1";
        
    public static final Command SUCCESS = new Command("SUCCESS", Command.OK, 0);
    public static final Command CANCEL = new Command("Storno", Command.BACK, 0);
    private StringItem siImportCacheCount;
    
    private Display display;
    private CommandListener listener = null;

    private boolean trucking;
    
    private Http http;
    
    /** Creates a new instance of GPXImport */
    public GPXImport(Favourites favourites, Display display, Http http) {
        super("Import z GPX");
        
        this.favourites = favourites;
        this.display = display;
        this.http = http;
        
        append("Importuji z GPX...");
        //Bug v Gauge u N5800 - nepouzivat Gauge
        //append(new Gauge("", false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING));
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
                } catch (Exception ex) {
                    ex.printStackTrace();
                    favourites.revalidate();
                    Gui.getInstance().showError("GPX Import", ex.toString(), "Nastala chyba při importu GPX. Prosím zašlete chybové hlášení včetně GPX souboru na arcao@arcao.com");
                }
                
            }
        };
        t.start();
    }
       
    private void parse(InputStream in) throws IOException, XmlPullParserException, Exception {
        KXmlParser parser = new KXmlParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        parser.setInput(in, "UTF-8");
        //ParseEvent pe = null;
        
        //System.out.println("Search gpx tag..");
        parser.next();
        if (!parser.getName().equals("gpx") && !parser.getName().equals("loc"))
            throw new Exception("Nejedná se o GPX nebo LOC soubor.");
        //parser.require(XmlPullParser.START_TAG, null, "gpx");
        //System.out.println("gpx tag found");
        
        String parts[][] = new String[1][15];
        
        String lastGcCode = "";
        String lastCacheName = "";
        
        trucking = true;
        int count = 0;
        while (trucking) {
            parser.next();
            if (parser.getEventType() == XmlPullParser.START_TAG) {
                String tagName = parser.getName();
                
                if (tagName.equals("waypoint")) {
                    for (int i=0; i < parts[0].length; i++)
                        parts[0][i] = "";
                    
                    parts[0][1] = "?";
                    parts[0][2] = "?";
                    parts[0][3] = "?";
                    parts[0][10] = "waypoint";
                    parts[0][12] = "1";
                    parts[0][13] = "0";
                    parts[0][14] = "0";
                    
                    while ((parser.getEventType() != XmlPullParser.END_TAG) || (parser.getName().equals(tagName) == false)) {
                        parser.next();
                        if (parser.getEventType() == XmlPullParser.START_TAG) {
                            if (parser.getName().equals("name")) {
                                parts[0][7] = parser.getAttributeValue(null, "id"); //GCCode
                                if (parts[0][7] == null)
                                    parts[0][7] = "";
                                if (parts[0][7].length() > 2 && parts[0][7].substring(0, 2).equalsIgnoreCase("gc"))
                                    parts[0][10] = "gc_traditional";
                                    
                                parser.next();
                                
                                parts[0][0] = parser.getText(); //cacheName
                            } else if (parser.getName().equals("coord")) {
                                parts[0][4] = getFriendlyLatLon(parser.getAttributeValue(null, "lat"), true); //latitude
                                parts[0][5] = getFriendlyLatLon(parser.getAttributeValue(null, "lon"), false); //longitude
                                parser.next();
                            }
                            
                        }
                    }
                    parts[0][11] = "1"; // has waypoints
                    parts[0][6] = "?/?";
                                        
                    favourites.editId = -1;
                    
                    if (parts[0][10].equals("waypoint")) {
                        favourites.addEdit(parts[0][0], "", parts[0][4], parts[0][5], parts[0][10], null, false, "", "", false, false, false);
                    } else {
                        favourites.addEdit(parts[0][0], Favourites.cachePartsToDesc(parts), parts[0][4], parts[0][5], parts[0][10], null, false, "", "", false, false, false);
                    }
                    count++;
                    
                    //if (count % 10 == 0)
                    siImportCacheCount.setText(Integer.toString(count));
                } else if (tagName.equals("wpt")) {
                    //System.out.println("wpt tag found");
                    String difficulty = "";
                    String terrain = "";
                    String comment = "";
                    String cmt = "";
                    String waypointName = "";
                    String realWaypointName = "";
                    String hint = "";
                    String listing = "";
                    for (int i=0; i < parts[0].length; i++)
                        parts[0][i] = "";
                    
                    parts[0][10] = "waypoint";
                    parts[0][12] = "0";
                    parts[0][13] = "0";
                    parts[0][14] = "0";
                                        
                    //for (int i=0; i<parser.getAttributeCount(); i++) {
                    //    System.out.println("{"+parser.getAttributeNamespace(i)+"}"+parser.getAttributeName(i)+"="+parser.getAttributeValue(i));
                    //}
                    
                    parts[0][4] = Gps.formatDeg(parser.getAttributeValue(null, "lat"), false); //latitude
                    parts[0][5] = Gps.formatDeg(parser.getAttributeValue(null, "lon"), true); //longitude
                    
                    while ((parser.getEventType() != XmlPullParser.END_TAG) || (parser.getName().equals(tagName) == false)) {
                        parser.next();
                        
                        if (parser.getEventType() == XmlPullParser.START_TAG) {
                            if (!parser.getNamespace().startsWith(GROUNDSPEAK_NS)) {
                                if (parser.getName().equals("name")) {
                                    parser.next();
                                    waypointName = parser.getText(); //gcCode
                                    parts[0][7] = waypointName;
                                } else if (parser.getName().equals("type")) {
                                    parser.next();
                                    parts[0][10] = convertGPXTypeToTypeID(parser.getText()); //typeIconID gc_xxx
                                } else if (parser.getName().equals("desc")) {
                                    parser.next();
                                    realWaypointName = parser.getText();
                                    if (realWaypointName == null)
                                        realWaypointName = "";
                                    if (realWaypointName.length() > 0)
                                        comment = (comment.length() > 0) ? realWaypointName + "\r\n" + comment : realWaypointName; //comment
                                } else if (parser.getName().equals("cmt")) {
                                    parser.next();
                                    String text = parser.getText();
                                    if (text == null) 
                                        text = "";
                                    cmt = text;
                                    if (text.length() > 0)
                                        comment+= (comment.length() > 0) ? "\r\n" + text : text; //comment
                                }
                            } else {
                                if (parser.getName().equals("cache")) {
                                    parts[0][9] = ""; // disabled/archived
                                    String available = parser.getAttributeValue(null, "available");
                                    String archived = parser.getAttributeValue(null, "archived");
                                    if (available != null && available.equalsIgnoreCase("false")) {
                                        parts[0][9] = "disabled";
                                    }
                                    if (archived != null && archived.equalsIgnoreCase("true")) {
                                        parts[0][9] = "archived";
                                    }
                                } else if (parser.getName().equals("name")) {
                                    parser.next();
                                    parts[0][0] = parser.getText(); //cache name
                                    lastCacheName = parts[0][0];
                                } else if (parser.getName().equals("placed_by")) {
                                    parser.next();
                                    parts[0][1] = parser.getText();
                                } else if (parser.getName().equals("type")) {
                                    parser.next();
                                    parts[0][2] = parser.getText(); //type name
                                    parts[0][10] = convertGPXTypeToTypeID(parts[0][2]);
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
                                    String isHTML = parser.getAttributeValue("", "html");
                                    parser.next();
                                    listing = parser.getText();
                                    if (listing == null)
                                        listing = "";
                                                                                                  
                                    if (isHTML != null && isHTML.equalsIgnoreCase("true"))
                                        listing = stripTags(listing);
                                    
                                    parts[0][14] = Integer.toString(listing.length() / 1024);
                                    parts[0][13] = (listing.indexOf("<!--Handy") != -1) ? "1":"0";
                                } else if (parser.getName().equals("encoded_hints")) {
                                    parser.next();
                                    hint = parser.getText();
                                    if (hint == null)
                                        hint = "";
                                    parts[0][12] = (hint.length() > 0) ? "1":"0";
                                    //comment+= ((comment.length() > 0) ? "\r\n" : "") + parser.getText();
                                } else if (parser.getName().equals("logs")) {
                                    while (parser.getEventType() != XmlPullParser.END_TAG || !parser.getName().equals("logs") || !parser.getNamespace().startsWith(GROUNDSPEAK_NS))
                                        parser.next();
                                } else if (parser.getName().equals("attributes")) {
                                    while (parser.getEventType() != XmlPullParser.END_TAG || !parser.getName().equals("attributes") || !parser.getNamespace().startsWith(GROUNDSPEAK_NS))
                                        parser.next();
                                } else if (parser.getName().equals("travelbugs")) {
                                    while (parser.getEventType() != XmlPullParser.END_TAG || !parser.getName().equals("travelbugs") || !parser.getNamespace().startsWith(GROUNDSPEAK_NS)) {
                                        parser.next();
                                        if (parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals("name")) {
                                            parser.next();
                                            parts[0][8]+= parser.getText() + ", ";
                                        }
                                    }
                                }
                                
                            }
                        }
                    }
                    parts[0][11] = "1"; // has waipoints
                    parts[0][6] = difficulty + "/" + terrain;
                    
                    //System.out.println("adding cache");
                    //System.out.println(parts[0][10].equals("waypoint"));
                    
                    parts[0][0] = parts[0][0].replace('{','(').replace('}',')'); 
                    parts[0][1] = parts[0][1].replace('{','(').replace('}',')'); 
                    parts[0][8] = parts[0][8].replace('{','(').replace('}',')'); 
                                        
                    favourites.editId = -1;
                    
                    if (parts[0][10].equals("waypoint")) {
                        if (waypointName.length() > 2 && lastGcCode.length() > 2 && waypointName.substring(2).equalsIgnoreCase(lastGcCode.substring(2)))
                            waypointName = lastCacheName + "-" + realWaypointName;
                        favourites.addEdit(waypointName, cmt, parts[0][4], parts[0][5], parts[0][10], null, false, "", comment, false, false, false);
                    } else {
                        favourites.addEdit(parts[0][0], Favourites.cachePartsToDesc(parts), parts[0][4], parts[0][5], parts[0][10], null, false, "", comment, false, false, false);
                        if (hint.length() > 0)
                            http.getHintCache().add(parts[0][7], hint);
                        if (listing.length() > 0)
                            http.getListingCache().add(parts[0][7], listing);
                        lastGcCode = waypointName;
                    }
                    count++;
                    
                    //if (count % 10 == 0)
                    siImportCacheCount.setText(Integer.toString(count));
                } else {
                    while ((parser.getEventType() != XmlPullParser.END_TAG) || (parser.getName().equals(tagName) == false))
                        parser.next();
                }
            }
            if ((parser.getEventType() == XmlPullParser.END_TAG && (parser.getName().equals("gpx") || parser.getName().equals("loc"))) ||
                 parser.getEventType() == XmlPullParser.END_DOCUMENT) {
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
        System.out.println(type);
        String name = type.toLowerCase();
        
        if (type.indexOf("|") > -1) {
            name = type.substring(type.indexOf('|') + 1).toLowerCase();
            type = type.substring(0, type.indexOf('|'));
        }
        
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
        } else if (name.startsWith("event") || name.startsWith("mega-event")) {
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
        } else if (name.startsWith("wherigo")) {
            return "gc_wherigo";
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
    
    private String stripTags(String html) {
        StringBuffer sb = new StringBuffer();
        
        html = html.replace('\n', ' ');
        html = Utils.unHTMLEntity(html);
        
        int pos = 0;
        int foundTag = 0;
        int foundComment = 0;
        
        foundTag = html.indexOf('<', pos);
        foundComment = html.indexOf("<!--", pos);
                
        while(foundTag != -1 || foundComment != -1) {
            if (foundTag < foundComment || (foundComment == -1 && foundTag != -1)) {
                sb.append(html.substring(pos, foundTag));
                pos = foundTag + 1;
                //pridani prazdnych radku. Ale dost naprd, slo by to tisickrat lip
                if (pos + 2 < html.length()) {
                    String tag2CH = html.substring(pos, pos + 2).toLowerCase();
                    if (tag2CH.equals("p>") || tag2CH.equals("p "))
                        sb.append("\n\n");
                }
                if (pos + 3 < html.length()) {
                    String tag3CH = html.substring(pos, pos + 3).toLowerCase();
                    if (tag3CH.equals("br>") || tag3CH.equals("br ") || tag3CH.equals("br/"))
                        sb.append("\n");
                }
                if (pos + 4 < html.length()) {
                    String tag4CH = html.substring(pos, pos + 4).toLowerCase();
                    if (tag4CH.startsWith("/h") && (tag4CH.charAt(2) >= '1' && tag4CH.charAt(2) <= '7') && tag4CH.charAt(3) == '>')
                        sb.append("\n");
                    if (tag4CH.equals("div>") || tag4CH.equals("div "))
                        sb.append("\n\n");
                }                
                
                if ((foundTag = html.indexOf('>', pos)) != -1)
                    pos = foundTag + 1;
            } else {
                sb.append(html.substring(pos, foundComment));
                pos = foundComment + 4;
                if ((foundComment = html.indexOf("-->", pos)) != -1)
                    pos = foundComment + 3;
            }
            
            foundTag = html.indexOf('<', pos);
            foundComment = html.indexOf("<!--", pos);
        }
        
        sb.append(html.substring(pos));
        
        return sb.toString();
    }
}
