/*
 * Settings.java
 * This file is part of HandyGeocaching.
 *
 * HandyGeocaching is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * (read more at: http://www.gnu.org/licenses/gpl.html)
 */
package database;

import gps.Gps;
import gui.Gui;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import javax.microedition.lcdui.Choice;
import javax.microedition.rms.RecordStore;

/**
 * Tato třída si pamatuje nastavení aplikace a umož�?uje jeho správu
 * @author David Vavra
 */
public class Settings
{
    //Internal GPS enum
    public static final int INTERNAL_GPS_GENERAL = 0;
    public static final int INTERNAL_GPS_GENERAL_1S = 1;
    public static final int INTERNAL_GPS_SAMSUNG_SGH_I5X0 = 2;
    public static final int INTERNAL_GPS_BLACKBERRY = 3;
        
    //reference
    private Gui gui;
    
    //promenne nastaveni
    public String name;
    public String password;
    public String filter;
    public String lastLattitude;
    public String lastLongitude;
    public int numberCaches;
    public int flashbackPeriod;
    public String lastDevice;
    public boolean vip;
    public boolean incrementalFieldNotes;
    public boolean iconsInFieldNotes;
    public boolean nameInFieldNotesFirst;
    public boolean wrappedFieldNotesList;
    public int internalGPSType;
    public boolean acceptingDialogs;
    
    //ostatni promenne
    private RecordStore recordStore;   
    public Settings(Gui ref)
    {
        try
        {
            gui = ref;
            recordStore = RecordStore.openRecordStore("settings", true);
        }
        catch (Exception e)
        {
            gui.showError("settings konstruktor",e.toString(),"");
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
     * Po startu aplikace tato metoda nacte data do promennych nebo vytvori
     * defaultni hodnoty nastaveni
     * vraci true, pokud se nacteni databaze podari
     */
    public boolean load()
    {
        try
        {
            //defaultni nastaveni
            name = "";
            password = "";
            filter = "111100";
            lastLattitude = "N 50° 00.000";
            lastLongitude = "E 014° 00.000";
            numberCaches = 10;
            flashbackPeriod = 0;
            lastDevice = "";
            vip = false;
            incrementalFieldNotes = true;
            iconsInFieldNotes = true;
            nameInFieldNotesFirst = false;
            wrappedFieldNotesList = true;
            internalGPSType = INTERNAL_GPS_GENERAL;
            acceptingDialogs = false;
            
            if (recordStore.getNumRecords() == 0)
            {  //prvni start aplikace
                store(true);
            }
            else
            {
                //ostatni starty aplikace, nacitam data
                byte[] datas = recordStore.getRecord(1);
                DataInputStream DI = new DataInputStream(new ByteArrayInputStream(datas));
                
                name = DI.readUTF();
                password = DI.readUTF();
                filter = DI.readUTF();
                lastLattitude = DI.readUTF();
                lastLongitude = DI.readUTF();
                numberCaches = DI.readInt();
                flashbackPeriod = DI.readInt();
                lastDevice = DI.readUTF();
                vip = DI.readBoolean();
                incrementalFieldNotes = DI.readBoolean();
                iconsInFieldNotes = DI.readBoolean();
                nameInFieldNotesFirst = DI.readBoolean();
                wrappedFieldNotesList = DI.readBoolean();
                internalGPSType = DI.readInt();
                acceptingDialogs = DI.readBoolean();
            }
            return true;
        }
        catch (EOFException e)
        {
            return true;
        }
        catch (Exception e)
        {
            gui.showError("loadSettings",e.toString(),"");
            return false;
        }
    }
    
    /**
     * Nastavi formular podle ulozeneho nastaveni
     */
    public void set()
    {
        try
        {
            gui.get_tfName().setString(name);
            gui.get_tfPassword().setString(password);
            boolean[] flags = new boolean[6];
            for (int i=0;i<6;i++)
            {
                flags[i] = (filter.substring(i,i+1).equals("1"))?true:false;
            }
            gui.get_cgCacheFilter().setSelectedFlags(flags);
            gui.get_tfNumberCaches().setString(String.valueOf(numberCaches));
            gui.get_tfBackLight().setString(String.valueOf(flashbackPeriod));
            
            flags = new boolean[4];
            flags[0] = incrementalFieldNotes;
            flags[1] = iconsInFieldNotes;
            flags[2] = nameInFieldNotesFirst;
            flags[3] = wrappedFieldNotesList;
            gui.get_cgFieldNotes().setSelectedFlags(flags);
            
            gui.get_cgInternalGPSType().setSelectedIndex(internalGPSType, true);
            
            gui.get_cgAcceptingDialogs().setSelectedIndex((acceptingDialogs) ? 0 : 1, true);
        }
        catch (Exception e)
        {
            gui.showError("setSettings",e.toString(),"");
        }
    }
    
    /**
     * Ulozi vybrane nastaveni do promennych
     */
    public void save()
    {
        try
        {
            name = gui.get_tfName().getString();
            password = gui.get_tfPassword().getString();
            boolean[] selected = new boolean[gui.get_cgCacheFilter().size()];
            gui.get_cgCacheFilter().getSelectedFlags(selected);
            filter = "";
            for (int i=0;i<selected.length;i++)
            {
                filter += (selected[i])?"1":"0";
            }
            numberCaches = Integer.parseInt(gui.get_tfNumberCaches().getString());
            if (numberCaches > 20)
                numberCaches = 20;
            flashbackPeriod = Integer.parseInt(gui.get_tfBackLight().getString());
            
            selected = new boolean[4];
            gui.get_cgFieldNotes().getSelectedFlags(selected);
            incrementalFieldNotes = selected[0];
            iconsInFieldNotes = selected[1];
            nameInFieldNotesFirst = selected[2];
            wrappedFieldNotesList = selected[3];
            
            internalGPSType = gui.get_cgInternalGPSType().getSelectedIndex();
            
            acceptingDialogs = (gui.get_cgAcceptingDialogs().getSelectedIndex() == 0);
            
            gui.get_lstFieldNotes().setFitPolicy((wrappedFieldNotesList)? Choice.TEXT_WRAP_ON : Choice.TEXT_WRAP_OFF);
            
            store(false);
        }
        catch (Exception e)
        {
            gui.showError("saveSettings",e.toString(),"");
        }
    }
    
    /**
     * Ulozi nastaveni podle promennych do databaze
     */
    public void store(boolean createNewRecord)
    {
        try
        {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(buffer);
            dos.writeUTF(name);
            dos.writeUTF(password);
            dos.writeUTF(filter);
            dos.writeUTF(lastLattitude);
            dos.writeUTF(lastLongitude);
            dos.writeInt(numberCaches);
            dos.writeInt(flashbackPeriod);
            dos.writeUTF(lastDevice);
            dos.writeBoolean(vip);
            dos.writeBoolean(incrementalFieldNotes);
            dos.writeBoolean(iconsInFieldNotes);
            dos.writeBoolean(nameInFieldNotesFirst);
            dos.writeBoolean(wrappedFieldNotesList);
            dos.writeInt(internalGPSType);
            dos.writeBoolean(acceptingDialogs);
            
            byte[] bytes = buffer.toByteArray();
            if (createNewRecord)
                recordStore.addRecord(bytes, 0, bytes.length);
            else
                recordStore.setRecord(1, bytes, 0, bytes.length);
        }
        catch (Exception e)
        {
            gui.showError("storeSettings",e.toString(),"");
        }
    }    
    
    /**
     * Po uspesnem pripojeni ulozi adresu zarizeni
     */
    public void saveLastDevice(String bluetoothAdress)
    {
        try
        {
            lastDevice = bluetoothAdress;
            store(false);
        }
        catch (Exception e)
        {
            gui.showError("saveLastDevice",e.toString(),"");
        }
    }
    
    /**
     * Po kontrole vip pri prihlasovani nastavi VIP mod
     */
    public void setVIP(boolean mode)
    {
        try
        {
            vip = mode;
            store(false);
        }
        catch (Exception e)
        {
            gui.showError("setVIP",e.toString(),"");
        }
    }
    
    /**
     * Ulozi zadane souradnice, vola se po projiti kontroly zadani souradnice
     */
    public void saveCoordinates(String lat, String lon)
    {
        try
        {
            lastLattitude = Gps.formatDeg(lat, false);
            lastLongitude = Gps.formatDeg(lon, true);
            
            store(false);
        }
        catch (Exception e)
        {
            gui.showError("saveCoordinates",e.toString(),"");
        }
    }    
 
 }
