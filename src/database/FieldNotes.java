/*
 * FieldNotes.java
 *
 * Created on 16. červenec 2009, 10:44
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package database;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Vector;
import javax.microedition.rms.RecordComparator;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordFilter;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

/**
 *
 * @author Arcao
 */
public class FieldNotes implements RecordFilter, RecordComparator {
    public static final int TYPE_FOUND_IT = 0;
    public static final int TYPE_DIDN_T_FIND_IT = 1;
    public static final int TYPE_WRITE_NOTE = 2;
    public static final int TYPE_NEEDS_ARCHIVED = 3;
    public static final int TYPE_NEEDS_MAINTENANCE = 4;
    
    private static String RECORD_STORE_FILENAME = "FieldNotes";
    
    private static FieldNotes instance = null;
    
    protected RecordStore recordStore = null;
    
    /** Creates a new instance of FieldNotes */
    private FieldNotes() {
        try {
            recordStore = RecordStore.openRecordStore(RECORD_STORE_FILENAME, true);
        } catch (RecordStoreException ex) {
            ex.printStackTrace();
        }
    }
    
    public static FieldNotes getInstance() {
        if (instance == null) 
            instance = new FieldNotes();
        return instance;
    }
    
    public int getLength() {
        try {
            return recordStore.getNumRecords();
        } catch (RecordStoreNotOpenException ex) {
            ex.printStackTrace();
            return 0;
        }
    }
    
    public FieldNotesItem create() {
        return new FieldNotesItem(-1, recordStore, null);
    }
    
    public void deleteByIndex(int index) {
        try {
            RecordEnumeration rc = recordStore.enumerateRecords(this, this, true);
            rc.rebuild();

            int id = 0;
            for (int i = 0; i <= index; i++)
                id = rc.nextRecordId();
            
            recordStore.deleteRecord(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   
    public void deleteById(int id) {
        try {          
            recordStore.deleteRecord(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void deleteAll() {
        try {
            recordStore.closeRecordStore();
            recordStore.deleteRecordStore(RECORD_STORE_FILENAME);
            recordStore = RecordStore.openRecordStore(RECORD_STORE_FILENAME, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public FieldNotesItem getById(int id) {
        try {
            return new FieldNotesItem(id, recordStore, recordStore.getRecord(id));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public FieldNotesItem getByIndex(int index) {
        try {
            RecordEnumeration rc = recordStore.enumerateRecords(this, this, true);
            rc.rebuild();

            int id = 0;
            for (int i = 0; i <= index; i++)
                id = rc.nextRecordId();
            
            return new FieldNotesItem(id, recordStore, recordStore.getRecord(id));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public String[] getAllNames() {
        try {
            RecordEnumeration rc = recordStore.enumerateRecords(this, this, true);
            rc.rebuild();

            int id = 0;
            String[] items = new String[rc.numRecords()];
            for (int i = 0; i < rc.numRecords(); i++)
            {
                id = rc.nextRecordId();
                items[i] = new FieldNotesItem(id, recordStore, recordStore.getRecord(id)).toString();
            }
            
            return items;
        } catch (Exception e) {
            e.printStackTrace();
            return new String[0];
        }
    }
    
    public int[] getAllIds() {
        try {
            RecordEnumeration rc = recordStore.enumerateRecords(this, this, true);
            rc.rebuild();

            int[] items = new int[rc.numRecords()];
            for (int i = 0; i < rc.numRecords(); i++)
            {
                items[i] = rc.nextRecordId();
            }
            
            return items;
        } catch (Exception e) {
            e.printStackTrace();
            return new int[0];
        }
    }
    
    public FieldNotesItem[] getAll() {
        try {
            RecordEnumeration rc = recordStore.enumerateRecords(this, this, true);
            rc.rebuild();

            int id = 0;
            FieldNotesItem[] items = new FieldNotesItem[rc.numRecords()];
            for (int i = 0; i < rc.numRecords(); i++)
            {
                id = rc.nextRecordId();
                items[i] = new FieldNotesItem(id, recordStore, recordStore.getRecord(id));
            }
            
            return items;
        } catch (Exception e) {
            e.printStackTrace();
            return new FieldNotesItem[0];
        }
    }
    
    public String getFieldNotes() {
        StringBuffer sb = new StringBuffer();
        FieldNotesItem[] items = getAll();
        
        for (int i = 0; i < items.length; i++) {
            FieldNotesItem item = items[i];
            
            if (i > 0) sb.append("\r\n");
                     
            sb.append(item.getGcCode());
            sb.append(',');
            sb.append(item.getDateZuluString());
            sb.append(',');
            sb.append(getTypeString(item.getType()));
            sb.append(',').append('"');
            sb.append(item.getText().replace('"','\''));
            sb.append('"');
        }
        return sb.toString();
    }
    
    public static String getTypeString(int type) {
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
    
    public static String getTypeIconName(int type) {
        switch(type) {
            case TYPE_DIDN_T_FIND_IT:
                return "didn_t_find";
            case TYPE_WRITE_NOTE:
                return "write_note";
            case TYPE_NEEDS_ARCHIVED:
                return "needs_archived";
            case TYPE_NEEDS_MAINTENANCE:
                return "needs_maintanance";
            default:
                return "found_it";
        }
    }
    
    public static String getTypeAbbr(int type) {
        switch(type) {
            case TYPE_DIDN_T_FIND_IT:
                return "DNF";
            case TYPE_WRITE_NOTE:
                return "W";
            case TYPE_NEEDS_ARCHIVED:
                return "ARCH";
            case TYPE_NEEDS_MAINTENANCE:
                return "MAIN";
            default:
                return "F";
        }
    }

    public boolean matches(byte[] b) {
        return true;
    }

    public int compare(byte[] rec1, byte[] rec2) {
        try {
            DataInputStream dis1 = new DataInputStream(new ByteArrayInputStream(rec1));
            DataInputStream dis2 = new DataInputStream(new ByteArrayInputStream(rec2));

            String gcCode1 = dis1.readUTF();
            dis1.readUTF(); //name
            long date1 = dis1.readLong();
            int type1 = dis1.readInt();
            
            String gcCode2 = dis2.readUTF();
            dis2.readUTF(); //name
            long date2 = dis2.readLong();
            int type2 = dis2.readInt();
            
            long i = date1 - date2;
            if (i != 0)
                return compareResult(i);
            i = gcCode1.compareTo(gcCode2);
            if (i != 0)
                return compareResult(i);
            i = type1 - type2;
            if (i != 0)
                return compareResult(i);
            
            return RecordComparator.EQUIVALENT;   
        } catch (Exception e) {
            e.printStackTrace();
            return RecordComparator.EQUIVALENT;
        }
    }
    
    private int compareResult(long i) {
        if (i == 0) {
           return RecordComparator.EQUIVALENT;
        } else if (i < 0) {
            return RecordComparator.PRECEDES;
        } else {
            return RecordComparator.FOLLOWS;
        }
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
}
