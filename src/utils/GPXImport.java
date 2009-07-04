/*
 * Import.java
 *
 * Created on 4. èervenec 2009, 10:28
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package utils;

import database.Favourites;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import kxml2.KXmlParser;
import kxml2.xmlpull.XmlPullParser;
import kxml2.xmlpull.XmlPullParserException;

/**
 *
 * @author Administrator
 */
public class GPXImport {
    private Favourites favourites;
    
    /** Creates a new instance of Import */
    private GPXImport(Favourites favourites) {
        this.favourites = favourites;
    }
    
    public static void fromFile(Favourites favourites, String fileName) {
        GPXImport i = new GPXImport(favourites);
        i.parse(fileName);
    }
    
    private void parse(final String fileName) {
        Thread t = new Thread() {
            public void run() {
                try {
                    FileConnection file = (FileConnection) Connector.open(fileName, Connector.READ);
                    parse(file.openInputStream());
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (XmlPullParserException ex) {
                    ex.printStackTrace();
                }
                
            }
        };
        t.start();
        try {
            t.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    public void parse(InputStream in) throws IOException, XmlPullParserException {
        Reader reader = new InputStreamReader(in);
        KXmlParser parser = new KXmlParser();
        parser.setInput(reader);
        //ParseEvent pe = null;
        
        parser.next();
        parser.require(XmlPullParser.START_TAG, null, "rss");
        parser.next();
        parser.require(XmlPullParser.START_TAG, null, "channel");
        
        boolean trucking = true;
        boolean first = true;
        while (trucking) {
            parser.next();
            if (parser.getEventType() == XmlPullParser.START_TAG) {
                String name = parser.getName();
                if (name.equals("item")) {
                    String title, link, description;
                    title = link = description = null;
                    while ((parser.getEventType() != XmlPullParser.END_TAG) ||
                            (parser.getName().equals(name) == false)) {
                        parser.next();
                        if (parser.getEventType() == XmlPullParser.START_TAG &&
                                parser.getName().equals("title")) {
                            parser.next();
                            title = parser.getText();
                        } else if (parser.getEventType() == XmlPullParser.START_TAG &&
                                parser.getName().equals("link")) {
                            parser.next();
                            link = parser.getText();
                        } else if (parser.getEventType() == XmlPullParser.START_TAG &&
                                parser.getName().equals("description")) {
                            parser.next();
                            description = parser.getText();
                        }
                    }
                } else {
                    while ((parser.getEventType() != XmlPullParser.END_TAG) ||
                            (parser.getName().equals(name) == false))
                        parser.next();
                }
            }
            if (parser.getEventType() == XmlPullParser.END_TAG && parser.getName().equals("rss"))
                trucking = false;
        }
    }
    
    
}
