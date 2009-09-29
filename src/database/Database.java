/*
 * Database.java
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
import java.io.DataInputStream;
import javax.microedition.rms.RecordComparator;
import javax.microedition.rms.RecordFilter;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreNotOpenException;

/**
 * Tato třída je pouze abstraktní - dědí od ní jednotlivé databáze
 * @author David Vavra
 */
abstract class Database implements RecordFilter, RecordComparator
{
    //reference
    protected Gui gui;
    
    //ostatni promenne
    protected RecordStore recordStore;
    private String databaseName;
    
    public Database(Gui ref, String dbName)
    {
        try
        {
            gui = ref;
            databaseName = dbName;
            recordStore = RecordStore.openRecordStore(dbName, true);
        }
        catch (Exception e)
        {
            gui.showError(databaseName+" konstruktor",e.toString(),"");
        }
    }
    
    /**
     * Zavre spojeni s databazi
     */
    public void close()
    {
        try
        {
            recordStore.closeRecordStore();
        }
        catch(Exception e)
        {}
    }
    
    /**
     * Doporucena struktura trid: view, addEdit, edit, delete, viewAll
     */

    /**
     * Zobrazí všechny položky databázi - konkrétně implementují potomci
     */
    public void viewAll()
    {
        
    }
            
    /**
     * Smaze celou databazi
     */
    public void deleteAll()
    {
        try
        {
            close();
            RecordStore.deleteRecordStore(databaseName);
            recordStore = RecordStore.openRecordStore(databaseName, true);
            viewAll();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            gui.showError("deleteAll "+databaseName,ex.toString(),"");
        }
    }
    
    /**
     * Filtrovani zaznamu - neni
     */
    public boolean matches(byte[] candidate)
    { return true;    }
    
    /**
     * Porovnavani zaznamu - oblibene radime podle abecedy
     */
    public int compare(byte[] rec1, byte[] rec2)
    {
        try
        {
            DataInputStream dis1 = new DataInputStream(new ByteArrayInputStream(rec1));
            DataInputStream dis2 = new DataInputStream(new ByteArrayInputStream(rec2));
            String s1 = dis1.readUTF();
            String s2 = dis2.readUTF();
            // porovnání jmen z prvního a druhého záznamu
            int i = s1.toLowerCase().compareTo(s2.toLowerCase());
            if (i == 0)
            {
                // jména jsou stejná
                return RecordComparator.EQUIVALENT;
            }
            else if (i < 0)
            {
                // první jméno je dríve než druhé
                return RecordComparator.PRECEDES;
            }
            else
            {
                // první jméno je později než druhé
                return RecordComparator.FOLLOWS;
            }
        }
        catch (Exception e)
        {
            gui.showError("compare favouriteso",e.toString(),"");
            return RecordComparator.EQUIVALENT;
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
