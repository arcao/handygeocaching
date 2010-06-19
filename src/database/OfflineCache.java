/*
 * OfflineCache.java
 * This file is part of HandyGeocaching.
 *
 * HandyGeocaching is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * (read more at: http://www.gnu.org/licenses/gpl.html)
 */
package database;

import gnu.classpath.util.zip.DataFormatException;
import gnu.classpath.util.zip.Deflater;
import gnu.classpath.util.zip.Inflater;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordFilter;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

/**
 *
 * @author Arcao
 */
public class OfflineCache {
    private String dbName;
    private RecordStore recordStore = null;
    
    /** Creates a new instance of OfflineCache */
    public OfflineCache(String dbName) {
        this.dbName = dbName;
        try {
            recordStore = RecordStore.openRecordStore(dbName, true);
        } catch (RecordStoreException ex) {
            ex.printStackTrace();
        }
    }
    
    public void add(String waypoint, String data) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(buffer);
            
            dos.writeUTF(waypoint); //waypoint
            boolean compression = (data.length() > 200) ? true : false;
            dos.writeBoolean(compression); //is GZipped
            if (compression) {
                byte cData[] = compress(data);
                dos.writeInt(cData.length);
                dos.write(cData);
            } else {
                dos.writeUTF(data);
            }
            
            byte rsData[] = buffer.toByteArray();
            
            dos.close();
            buffer.close();
            
            int id = searchID(waypoint);
            if (id == -1)
                recordStore.addRecord(rsData, 0, rsData.length);
            else
                recordStore.setRecord(id, rsData, 0, rsData.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String get(String waypoint) {
        int id = searchID(waypoint);
        if (id == -1)
            return null;
        
        DataInputStream dis = null;
        try {
            dis = new DataInputStream(new ByteArrayInputStream(recordStore.getRecord(id)));
            dis.readUTF(); //waypoint name
            boolean compression = dis.readBoolean(); //future use, is GZipped
            if (compression) {
                int len = dis.readInt();
                byte cData[] = new byte[len];
                dis.read(cData);
                return decompress(cData);
            } else {
                return dis.readUTF(); //data
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (dis != null)
                try {
                    dis.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
        }
    }
    
    public void delete(String waypoint) {
        int id = searchID(waypoint);
        if (id != -1)
            
            try {
                recordStore.deleteRecord(id);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
    }
    
    public void deleteAll() {
        try {
            recordStore.closeRecordStore();
            recordStore.deleteRecordStore(dbName);
            recordStore = RecordStore.openRecordStore(dbName, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private int searchID(final String waypoint) {
        try {
            RecordEnumeration en = recordStore.enumerateRecords(new RecordFilter() {
                                       public boolean matches(byte[] b) {
                                           DataInputStream dis = new DataInputStream(new ByteArrayInputStream(b));
                                           try {
                                               return dis.readUTF().equals(waypoint);
                                           } catch (IOException ex) {
                                               ex.printStackTrace();
                                               return false;
                                           }
                                       }
                                   }, null, true);
            if (en.numRecords() > 0)
                return en.nextRecordId();
            else
                return -1;
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }        
    }

    public boolean has(String waypoint) {
        return searchID(waypoint) != -1;
    }
    
    public int usedSize() {
        try {
            return recordStore.getSize();
        } catch (RecordStoreNotOpenException ex) {
            ex.printStackTrace();
            return 0;
        }
    }
    
    public int totalSize() {
        try {
            return recordStore.getSize() + recordStore.getSizeAvailable();
        } catch (RecordStoreNotOpenException ex) {
            ex.printStackTrace();
            return 0;
        }
    }
    
    public int count() {
        try {
            return recordStore.getNumRecords();
        } catch (RecordStoreNotOpenException ex) {
            ex.printStackTrace();
            return 0;
        }
    }
    
    public byte[] compress(String text) {
        Deflater c = new Deflater();
        try {
            c.setInput(text.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        c.finish();
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte buffer[] = new byte[4096];
        int len = 0;
        
        while ((len = c.deflate(buffer)) > 0) {
            bos.write(buffer, 0, len);
        }
        
        return bos.toByteArray();
    }
    
    public String decompress(byte[] data) {
        Inflater d = new Inflater();
        d.setInput(data, 0, data.length);
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte buffer[] = new byte[4096];
        int len = 0;
        try {
            while ((len = d.inflate(buffer)) > 0) {
                bos.write(buffer, 0, len);
            }
        } catch (DataFormatException ex) {
            ex.printStackTrace();
        }
        d.end();
        
        byte[] out = bos.toByteArray();
        try {
            return new String(out, 0, out.length, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
