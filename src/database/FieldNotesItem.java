/*
 * FieldNotesItem.java
 * This file is part of HandyGeocaching.
 *
 * HandyGeocaching is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * (read more at: http://www.gnu.org/licenses/gpl.html)
 */
package database;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import javax.microedition.rms.RecordStore;
import utils.Utils;

/**
 *
 * @author Arcao
 */
public class FieldNotesItem {
    private int id;
    private String gcCode;
    private String name;
    private Date date;
    private int type;
    private String text;
    private RecordStore recordStore;
        
    /** Creates a new instance of FieldNotesItem */
    public FieldNotesItem(int id, RecordStore recordStore, byte[] data) {
        this.recordStore = recordStore;
        this.id = id;
        gcCode = "GC";
        name = "";
        date = new Date();
        type = 0;
        if (id == -1) {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
        
            text = new StringBuffer()
                   .append(nulaNula(c.get(Calendar.HOUR_OF_DAY)))
                   .append(':')
                   .append(nulaNula(c.get(Calendar.MINUTE)))
                   .toString();;
        }
        if (data != null && data.length > 0) {
            try {
                DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));

                gcCode = dis.readUTF();
                name = dis.readUTF();
                date.setTime(dis.readLong());
                type = dis.readInt();
                text = dis.readUTF();

                dis.close();
                dis = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void save() {
        try {
            byte[] data = toData();
            if (id == -1) {
                id = recordStore.addRecord(data, 0, data.length);
            } else {
                recordStore.setRecord(id, data, 0, data.length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private byte[] toData() {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(buffer);

            dos.writeUTF(gcCode);
            dos.writeUTF(name);
            dos.writeLong(date.getTime());
            dos.writeInt(type);
            dos.writeUTF(text);

            return buffer.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public String toString() {
        return toString(false, false);
    }  
   
    public String toString(boolean withType, boolean nameFirst) {
        StringBuffer sb =new StringBuffer();
        
        if (withType) {
            sb.append('(');
            sb.append(FieldNotes.getTypeAbbr(type));
            sb.append(") ");
        }
        
        sb.append(Utils.formatDate(getDate()));
        sb.append(' ');
        if (nameFirst && name.length() > 0) {
            sb.append(name);
            sb.append(' ').append('[').append(gcCode).append(']');
        } else {
            sb.append('[').append(gcCode).append(']');
            sb.append(' ').append(name);
        }
        return sb.toString();
    }
       
    public int getId() {
        return id;
    }
    
    public Date getDate() {
        return date;
    }
    
    public String getDateZuluString() {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        c.setTime(getDate());
                
        StringBuffer sb = new StringBuffer();
        sb.append(c.get(Calendar.YEAR)).append('-');
        sb.append(nulaNula(c.get(Calendar.MONTH) + 1)).append('-');
        sb.append(nulaNula(c.get(Calendar.DAY_OF_MONTH))).append('T');
        sb.append(nulaNula(c.get(Calendar.HOUR_OF_DAY))).append(':');
        sb.append(nulaNula(c.get(Calendar.MINUTE))).append('Z');
        
        return sb.toString();
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
    
    public void setDate(Calendar calendar) {
        date = calendar.getTime();
    }
    
    public String getGcCode() {
        return gcCode;
    }
    
    public void setGcCode(String gcCode) {
        this.gcCode = gcCode.toUpperCase();
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getType() {
        return type;
    }
    
    public void setType(int type) {
        this.type = type;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    private String nulaNula(int v) {
        if (v < 10)
            return "0"+Integer.toString(v, 10);
        return Integer.toString(v);
    }
}
