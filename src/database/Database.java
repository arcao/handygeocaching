/*
 * Database.java
 *
 * Created on 17. øíjen 2007, 9:45
 *
 */

package database;

import gui.Gui;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import javax.microedition.rms.RecordComparator;
import javax.microedition.rms.RecordFilter;
import javax.microedition.rms.RecordStore;

/**
 * Tato tøída je pouze abstraktní - dìdí od ní jednotlivé databáze
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
     * Zobrazí všechny položky databázi - konkrétnì implementují potomci
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
            int i = s1.compareTo(s2);
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
                // první jméno je pozdìji než druhé
                return RecordComparator.FOLLOWS;
            }
        }
        catch (Exception e)
        {
            gui.showError("compare favouriteso",e.toString(),"");
            return RecordComparator.EQUIVALENT;
        }
    }
    
}
