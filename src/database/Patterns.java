/*
 * Patterns.java
 * This file is part of HandyGeocaching.
 *
 * HandyGeocaching is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * (read more at: http://www.gnu.org/licenses/gpl.html)
 */
package database;

import gui.Gui;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import javax.microedition.lcdui.AlertType;
import javax.microedition.rms.RecordEnumeration;

/**
 * Tato trida se stara o spravu a ukladani vzorcu do MultiSolveru
 * @author David Vavra
 */
public class Patterns extends Database
{
    
    public Patterns(Gui ref)
    {
        super(ref, "patterns");
    }
    
    /**
     * Zobrazi vzorec
     */
    public void view()
    {
        try
        {
            RecordEnumeration rc = recordStore.enumerateRecords(this, this, true);
            rc.rebuild();
            int id = 0;
            for (int i = 0; i < gui.get_lstPatterns().getSelectedIndex()+1; i++)
            {
                id = rc.nextRecordId();
            }
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(recordStore.getRecord(id)));
            gui.get_tfPatternName().setString(dis.readUTF());
            gui.get_tfEditPatternLattitude().setString(dis.readUTF());
            gui.get_tfEditPatternLongitude().setString(dis.readUTF());
        }
        catch (Exception e)
        {
            gui.showError("viewPattern",e.toString(),"");
        }
    }    
    
    /**
     * Prida nebo edituje vzorec
     */
    public void addEdit(boolean edit)
    {
        try
        {
            //prevod dat na stream bytu
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(buffer);
            dos.writeUTF(gui.get_tfPatternName().getString());
            dos.writeUTF(gui.get_tfEditPatternLattitude().getString().toUpperCase());
            dos.writeUTF(gui.get_tfEditPatternLongitude().getString().toUpperCase());
            byte[] bytes = buffer.toByteArray();
            //nejdriv zjistit zda uz dane pismeno v databazi je
            if (edit)
            {
                RecordEnumeration rc = recordStore.enumerateRecords(this, this, true);
                rc.rebuild();
                int id = 0;
                for (int i = 0; i < gui.get_lstPatterns().getSelectedIndex()+1; i++)
                {
                    id = rc.nextRecordId();
                }
                recordStore.setRecord(id, bytes, 0, bytes.length);
            }
            else
            {
                recordStore.addRecord(bytes, 0, bytes.length);
            }
            viewAll();
        }
        catch (Exception ex)
        {
            gui.showError("addEditPattern",ex.toString(),"");
        }
    }    
    
    /**
     * Smaze jeden vzorecek
     */
    public void delete()
    {
        try
        {
            RecordEnumeration rc = recordStore.enumerateRecords(this, this, true);
            rc.rebuild();
            int id = 0;
            for (int i = 0; i < gui.get_lstPatterns().getSelectedIndex()+1; i++)
            {
                id = rc.nextRecordId();
            }
            recordStore.deleteRecord(id);
        }
        catch (Exception e)
        {
            gui.showError("deletePattern",e.toString(),"");
        }
    }       
    
    /**
     * Zobrazi vsechny vzorce
     */
    public void viewAll()
    {
        try
        {
            gui.get_lstPatterns().deleteAll();
            RecordEnumeration rc = recordStore.enumerateRecords(this, this, true);
            rc.rebuild();
            for (int i = 0; i < rc.numRecords(); i++)
            {
                DataInputStream dis = new DataInputStream(new ByteArrayInputStream(recordStore.getRecord(rc.nextRecordId())));
                gui.get_lstPatterns().append(dis.readUTF(),null);
            }
        }
        catch (Exception ex)
        {
            gui.showError("viewPatterns",ex.toString(),"");
        }
    }
    
    /**
     * Prida vzorce stazene z netu
     */
    public void addDownloaded(String[][] data)
    {
        try
        {
            for (int i=0;i<data.length;i++)
            {
                //prevod dat na stream bytu
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(buffer);
                dos.writeUTF(data[i][0]);
                dos.writeUTF(data[i][1]);
                dos.writeUTF(data[i][2]);
                byte[] bytes = buffer.toByteArray();
                recordStore.addRecord(bytes, 0, bytes.length);
            }
            gui.showAlert("Vzorečky byly úspěšně uloženy do MultiSolveru",AlertType.INFO,gui.get_frmOverview());
        }
        catch (Exception ex)
        {
            gui.showError("addPatterns",ex.toString(),"");
        }
    }
    
    /**
     * Nastavi vybrany vzorec jako aktivni
     */
    public void setActive()
    {
        try
        {
            RecordEnumeration rc = recordStore.enumerateRecords(this, this, true);
            rc.rebuild();
            int id = 0;
            for (int i = 0; i < gui.get_lstPatterns().getSelectedIndex()+1; i++)
            {
                id = rc.nextRecordId();
            }
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(recordStore.getRecord(id)));
            gui.get_siLattitudePattern().setLabel(dis.readUTF());
            gui.get_siLattitudePattern().setText(dis.readUTF());
            gui.get_siLongitudePattern().setText(dis.readUTF());
        }
        catch (Exception e)
        {
            gui.showError("setActivePattern",e.toString(),"");
        }
    }   
    
}
