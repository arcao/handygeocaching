/*
 * Go4CacheClient.java
 * This file is part of HandyGeocaching.
 *
 * HandyGeocaching is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * (read more at: http://www.gnu.org/licenses/gpl.html)
 */

package http;

import java.io.IOException;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import utils.Utils;

/**
 * Client implementation for Go4Cache service
 * @author martin.sloup
 */
public class Go4CacheClient implements Runnable {
    private static final int TIME_BETWEEN_REQUESTS = 300000;
    private static final String API_URL = "http://api.go4cache.com";
    
    private boolean terminated = false;
    private Thread thread;
    
    private String userName;
    private double latitude;
    private double longtitude;
    private String action;
    private boolean fix;
    
    /**
     * Creates a new instance of Go4CacheClient
     * @param userName Geocaching user name
     */
    public Go4CacheClient(String userName) {
        thread = new Thread(this);
        
        this.userName = userName;
        latitude = 0;
        longtitude = 0;
        action = "";
        fix = false;
    }
    
    /**
     * Set the actual coordinates and set a fix to true
     * @param latitude latitude
     * @param longtitude longtitude
     */
    public void setCoordinates(double latitude, double longtitude) {
        this.latitude = latitude;
        this.longtitude = longtitude;
        fix = true;
    }
    
    /**
     * Set a current user action
     * @param action user action
     */
    public void setAction(String action) {
        this.action = action;
    }
    
    /**
     * Set a new Geocaching user name
     * @param userName Geocaching user name
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    /**
     * Set a gps fix
     * @param fix has a gps fix
     */
    public void setFix(boolean fix) {
        this.fix = fix;
    }
    
    
    /**
     * Start a main thread for updating status to server
     */
    public void start() {
        thread.start();
    }
    
    /**
     * Stop a main thread
     */
    public void stop() {
        terminated = true;
        notifyAll();
    }
    
    public void run() {
        HttpConnection connection = null;
        OutputStream output = null;
        
        try {
            while(!terminated) {
                if (fix) {
                    try {
                        connection = (HttpConnection) Connector.open(API_URL);
                        connection.setRequestMethod(HttpConnection.POST);
                        connection.setRequestProperty("User-Agent", Http.getUserAgent());
                        
                        output = connection.openOutputStream();
                        StringBuffer sb = new StringBuffer();
                        sb.append("u=");
                        sb.append(Utils.urlUTF8Encode(userName));
                        sb.append("&lt=");
                        sb.append((float)latitude);
                        sb.append("&ln=");
                        sb.append((float)longtitude);
                        sb.append("&a=");
                        sb.append(Utils.urlUTF8Encode(action));
                        sb.append("&s=");
                        output.write(sb.toString().getBytes("UTF-8"));
                        output.close();
                        output = null;
                        connection.getResponseCode();
                        connection.close();
                        connection = null;
                    } catch (IOException e) {
                        // do nothing
                    } finally {
                        if (output != null)
                            try { output.close(); } catch (IOException e) {}
                        if (connection != null)
                            try { connection.close(); } catch (IOException e) {}
                    }
                }
                wait(TIME_BETWEEN_REQUESTS);
            }
        } catch(InterruptedException e) {
            // do nothing
        }
    }
}