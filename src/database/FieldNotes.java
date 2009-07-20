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
 * @author Administrator
 */
public class FieldNotes implements RecordFilter, RecordComparator {
    public static final int TYPE_FOUND_IT = 0;
    public static final int TYPE_DIDN_T_FIND_IT = 1;
    public static final int TYPE_WRITE_NOTE = 2;
    public static final int TYPE_NEEDS_ARCHIVED = 3;
    public static final int TYPE_NEEDS_MAINTENANCE = 4;
    
    private static FieldNotes instance = null;
    
    private RecordStore recordStore = null;
    
    /** Creates a new instance of FieldNotes */
    private FieldNotes() {
        try {
            recordStore = RecordStore.openRecordStore("FieldNotes", true);
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
            RecordEnumeration rc = recordStore.enumerateRecords(this, this, true);
            rc.rebuild();
            int numRecords = rc.numRecords();
            int[] recordIds = new int[numRecords];
            for (int i = 0; i < numRecords; i++)
            {
                recordIds[i] = rc.nextRecordId();
            }
            for (int i = 0; i < numRecords; i++)
            {
                recordStore.deleteRecord(recordIds[i]);
            }
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
    
    public static String getIconName(int type) {
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

    public boolean matches(byte[] b) {
        return true;
    }

    public int compare(byte[] rec1, byte[] rec2) {
        try {
            FieldNotesItem fni1 = new FieldNotesItem(-1, recordStore, rec1);
            FieldNotesItem fni2 = new FieldNotesItem(-1, recordStore, rec2);
            
            long i = fni1.getDate().getTime() - fni2.getDate().getTime();
            if (i != 0)
                return compareResult(i);
            
            i = fni1.getGcCode().compareTo(fni2.getGcCode());
            if (i != 0)
                return compareResult(i);
            
            i = fni1.getType() - fni2.getType();
            if (i != 0)
                return compareResult(i);
            
            i = fni1.getText().compareTo(fni2.getText());
            if (i != 0)
                return compareResult(i);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return RecordComparator.EQUIVALENT;
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
    
}
