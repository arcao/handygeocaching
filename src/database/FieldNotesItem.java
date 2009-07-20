/*
 * FieldNotesItem.java
 *
 * Created on 16. červenec 2009, 11:09
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
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
 * @author Administrator
 */
public class FieldNotesItem {
    private int id;
    private String gcCode;
    private String name;
    private long date;
    private int type;
    private String text;
    private RecordStore recordStore;
        
    /** Creates a new instance of FieldNotesItem */
    public FieldNotesItem(int id, RecordStore recordStore, byte[] data) {
        this.recordStore = recordStore;
        this.id = id;
        gcCode = "";
        name = "";
        date = 0;
        type = 0;
        text = "";

        if (data != null && data.length > 0) {
            try {
                DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));

                gcCode = dis.readUTF();
                name = dis.readUTF();
                date = dis.readLong();
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
            dos.writeLong(date);
            dos.writeInt(type);
            dos.writeUTF(text);

            return buffer.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
    
    public String toString() {
        return new StringBuffer()
        .append(Utils.formatDate(getDate()))
        .append(' ')
        .append('[').append(gcCode).append(']')
        .append(' ')
        .append(name)
        .toString();
    }
    
    public int getId() {
        return id;
    }
    
    public Date getDate() {
        if (date == 0)
            return new Date();
        return new Date(date);
    }
    
    public String getDateZuluString() {
        Calendar c = Calendar.getInstance();
        c.setTime(getDate());
        c.setTimeZone(TimeZone.getTimeZone("GMT"));
        
        StringBuffer sb = new StringBuffer();
        sb.append(c.get(Calendar.YEAR)).append('-');
        sb.append(nulaNula(c.get(Calendar.MONTH))).append('-');
        sb.append(nulaNula(c.get(Calendar.DAY_OF_MONTH))).append('-').append('T');
        sb.append(nulaNula(c.get(Calendar.HOUR_OF_DAY))).append(':');
        sb.append(nulaNula(c.get(Calendar.MINUTE))).append('Z');
        
        return sb.toString();
    }
    
    public void setDate(Date date) {
        this.date = date.getTime();
    }
    
    public void setDate(Calendar calendar) {
        date = calendar.getTime().getTime();
    }
    
    public String getGcCode() {
        return gcCode;
    }
    
    public void setGcCode(String gcCode) {
        this.gcCode = gcCode;
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
