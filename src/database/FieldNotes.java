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
import javax.microedition.rms.RecordComparator;
import javax.microedition.rms.RecordFilter;

/**
 *
 * @author Administrator
 */
public class FieldNotes implements RecordFilter, RecordComparator {
    
    /** Creates a new instance of FieldNotes */
    public FieldNotes() {
    }

    public boolean matches(byte[] b) {
        return true;
    }

    public int compare(byte[] rec1, byte[] rec2) {
        try {
            FieldNotesItem fni1 = new FieldNotesItem(0, rec1);
            FieldNotesItem fni2 = new FieldNotesItem(0, rec2);
            
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
