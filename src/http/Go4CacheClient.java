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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import utils.Utils;

/**
 * Client implementation for Go4Cache service
 * @author martin.sloup
 */
public class Go4CacheClient implements Runnable {
    private static final String hex = "0123456789abcdef";
    private static final int TIME_BETWEEN_REQUESTS = 300000; //5min
    private static final String API_URL = "http://api.go4cache.com";
    
    private boolean terminated;
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
        terminated = true;
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
    
    public void setActionNothing() {
        this.action = "";
    }
    
    public void setActionAveragingCoordinates() {
        this.action = "pending";
    }
        
    public void setActionHeading(String geoId) {
        this.action = geoId;
    }
    
    public void setActionViewingCacheMap() {
        this.action = "discovering";
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
    public synchronized void start() {
        if (terminated) {
            terminated = false;
            thread.start();
        }
    }
    
    /**
     * Stop a main thread
     */
    public synchronized void stop() {
        terminated = true;
        notifyAll();
    }
    
    public void run() {
        HttpConnection connection = null;
        OutputStream output = null;
        
        String u, lt, ln, a;
        
        System.out.println("Go4Cache client thread started...");
        
        try {
            while(!terminated) {
                if (fix && userName != null && userName.length() > 0) {
                    try {
                        System.out.println("Sending coordinates to Go4Cache...");
                        System.out.println(API_URL);
                        connection = (HttpConnection) Connector.open(API_URL, Connector.READ_WRITE);
                        connection.setRequestMethod(HttpConnection.POST);
                        connection.setRequestProperty("User-Agent", Http.getUserAgent());
                        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        
                        u = userName;
                        lt = Utils.round(latitude,6);
                        ln = Utils.round(longtitude, 6);
                        a = action;
                        
                        output = connection.openOutputStream();
                        StringBuffer sb = new StringBuffer();
                        sb.append("u=");
                        sb.append(Utils.urlUTF8Encode(u));
                        sb.append("&lt=");
                        sb.append(lt);
                        sb.append("&ln=");
                        sb.append(ln);
                        sb.append("&a=");
                        sb.append(Utils.urlUTF8Encode(a));
                        sb.append("&s=");
                        sb.append(generateHash(u,lt,ln,a));
                        System.out.println(sb.toString());
                        output.write(sb.toString().getBytes("UTF-8"));
                        output.flush();
                        output.close();
                        output = null;
                        System.out.println(connection.getResponseCode());
                        //InputStream is = connection.openInputStream();
                        //
                        //StringBuffer sa = new StringBuffer();
                        //int ch;
                        //while((ch = is.read()) != -1) {
                        //    sa.append((char)ch);
                        //}
                        //is.close();
                        //System.out.println(sa.toString());
                        
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
                synchronized (this) {
                    wait(TIME_BETWEEN_REQUESTS);
                }
            }
        } catch(InterruptedException e) {
            // do nothing
        }
        terminated = true;
    }
    
    private static String generateHash(String u, String lt, String ln, String a) {
        StringBuffer sb=new StringBuffer();StringBuffer sc=new StringBuffer();
        String[] s={"@","G",u,"3","H","h","n","|",lt,"D","c",ln,"g","0",a,"Y",
        "1"};int[] i={0,3,10,8,9,5,2,7,6,4,1};return sa(sb.append(s[i[(2<<2)+1]>>
        1]).append(s[(i[(1<<1)+1]>>1)+3]).append(s[i[(2<<3)-7]<<1]).append(s[i[1
        <<3]+1]).append(s[(i[(16>>1)+2]<<4)-5]).append(s[(i[(1<<1)+1]>>1)+3])
        .append(s[(i[(3<<1)-5]<<2)+2]).append(s[(i[(1<<1)-1]<<1)+1]).append(sa
        (sc.append(s[(1<<2)+1]).append(s[(3>>2)]).append(s[(2<<2)-2]).append(s
        [(6<<1)-3]).append(s[(7<<1)+1]).append(s[25>>1]).append(s[7>>1]).
        append(s[(3<<2)+1]).append(s[(5<<1)]).append(s[(6>>4)]).append(s[(6<<1)-
        2]).append(s[9>>1]).append(s[(9<<1)-2]).append(s[(1<<2)+2]).append(s[11
        >>3]).toString())).toString());
    }
    private static int r(int n,int c){return (n<<c)|(n>>>(32-c));}
    private static String sa(String str) {
        byte[] x;try {x=str.getBytes("UTF-8");}catch(Exception e){return new 
        String();}int[] blks=new int[(((x.length+8)>>6)+1)*16];int i;
        for(i=0;i<x.length;i++) blks[i>>2]|=(x[i]&0xFF)<<(24-(i%4)*8);      
        blks[i>>2]|=0x80<<(24-(i%4)*8);blks[blks.length-1]=x.length*8;
        int[] w = new int[80];int a=1732584193,b=-271733879,c=-1732584194,d=
        271733878,e=-1009589776;for(i = 0; i < blks.length; i += 16) {
        int olda=a,oldb=b,oldc=c,oldd=d,olde=e;for(int j = 0; j < 80; j++) {
        w[j]=(j<16)?blks[i+j]:r(w[j-3]^w[j-8]^w[j-14]^w[j-16],1);
        int t=r(a,5)+e+w[j]+((j<20)?1518500249+((b&c)|((~b)&d))
        :(j<40)?1859775393+(b^c^d):(j<60)?-1894007588+((b&c)|(b&d)|(c&d))
        :-899497514+(b^c^d));e=d;d=c;c=r(b,30);b=a;a=t;
        }a+=olda;b+=oldb;c+=oldc;d+=oldd;e+=olde;}int[] words={a,b,c,d,e};
        StringBuffer sb=new StringBuffer();for(i=0;i<words.length;i++) for(int 
        j=7;j>=0;j--) sb.append(hex.charAt((words[i]>>(j*4))&0xF));return sb.
        toString();
    }
}