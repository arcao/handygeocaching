/*
 * Properties.java
 *
 * Created on 25. leden 2011, 9:46
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package utils;

import gui.Gui;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.midlet.MIDlet;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

/**
 *
 * @author msloup
 */
public class Properties {
    public static final byte TYPE_BOOLEAN = 1;
    public static final byte TYPE_BYTE = 2;
    public static final byte TYPE_CHAR = 3;
    public static final byte TYPE_DOUBLE = 4;
    public static final byte TYPE_FLOAT = 5;
    public static final byte TYPE_INT = 6;
    public static final byte TYPE_LONG = 7;
    public static final byte TYPE_SHORT = 8;
    public static final byte TYPE_STRING = 9;
    
    
    private static Properties instance = new Properties();
    
    private Hashtable local = new Hashtable();
    private RecordStore rs = null;
    private Object lock = new Object();
        
    private Properties() {
        try {
            rs = RecordStore.openRecordStore("Properties", true);
            load();
        } catch (Exception e) {}
    }

    public Object get(String key) {
        if (local.containsKey(key))
            return local.get(key);
        
        Object value = System.getProperty(key);
        if (value != null)
            return value;
        
        return Gui.getInstance().getAppProperty(key);
    }

    public Object set(String key, Object value) {
        local.put(key, value);
        store();
        
        return value;
    }
    
    protected void load() throws RecordStoreNotOpenException, RecordStoreException, IOException {
         if (rs.getNumRecords() > 0) {
            byte[] data = rs.getRecord(1);
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
            while(dis.available() > 0) {
                String key = dis.readUTF();
                byte type = dis.readByte();
                switch(type) {
                    case TYPE_BOOLEAN: local.put(key, new Boolean(dis.readBoolean())); break;
                    case TYPE_BYTE: local.put(key, new Byte(dis.readByte())); break;
                    case TYPE_CHAR: local.put(key, new Character(dis.readChar())); break;
                    case TYPE_DOUBLE: local.put(key, new Double(dis.readDouble())); break;
                    case TYPE_FLOAT: local.put(key, new Float(dis.readFloat())); break;
                    case TYPE_INT: local.put(key, new Integer(dis.readInt())); break;
                    case TYPE_LONG: local.put(key, new Long(dis.readLong())); break;
                    case TYPE_SHORT: local.put(key, new Short(dis.readShort())); break;
                    case TYPE_STRING: local.put(key, dis.readUTF()); break;
                }
            }
            dis.close();
        }    
    }
    
    protected void store() {
        if (rs == null)
            return;
        
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);

            Enumeration e = local.keys();
            while(e.hasMoreElements()) {
                String key = (String) e.nextElement();
                Object value = local.get(key);
                byte type = getType(value);

                dos.writeUTF(key);
                dos.writeByte(type);
                switch(type) {
                    case TYPE_BOOLEAN: dos.writeBoolean(((Boolean)value).booleanValue()); break;
                    case TYPE_BYTE: dos.writeByte(((Byte)value).byteValue()); break;
                    case TYPE_CHAR: dos.writeChar(((Character)value).charValue()); break;
                    case TYPE_DOUBLE: dos.writeDouble(((Double)value).doubleValue());
                    case TYPE_FLOAT: dos.writeFloat(((Float)value).floatValue()); break;
                    case TYPE_INT: dos.writeInt(((Integer)value).intValue()); break;
                    case TYPE_LONG: dos.writeLong(((Long)value).longValue()); break;
                    case TYPE_SHORT: dos.writeShort(((Short)value).shortValue()); break;
                    default: dos.writeUTF(value.toString()); break;
                }
            }
            
            dos.close();
            bos.close();
            
            byte[] data = bos.toByteArray();
            
            if (rs.getNumRecords() > 0) {
                rs.setRecord(1, data, 0, data.length);
            } else {
                rs.addRecord(data, 0, data.length);
            }
        } catch (Exception e) {}
        
    }
    
    protected byte getType(Object o) {
        if (o instanceof Boolean)
            return TYPE_BOOLEAN;
        if (o instanceof Byte)
            return TYPE_BYTE;
        if (o instanceof Character)
            return TYPE_CHAR;
        if (o instanceof Double)
            return TYPE_DOUBLE;
        if (o instanceof Float)
            return TYPE_FLOAT;
        if (o instanceof Integer)
            return TYPE_INT;
        if (o instanceof Long)
            return TYPE_LONG;
        if (o instanceof Short)
            return TYPE_SHORT;
        return TYPE_STRING;
    }
    
    // ----------- Static methods ----------- //
    public static String get(String key, String def) {
        Object value = instance.get(key);
        if (value == null)
            return def;
        
        return instance.get(key).toString();
    }
    
    
    
    
}
