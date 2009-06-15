/*
 * Settings.java
 *
 * Created on 16. øíjen 2007, 21:11
 *
 */

package database;

import gui.Gui;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import javax.microedition.rms.RecordStore;

/**
 * Tato tøída si pamatuje nastavení aplikace a umožòuje jeho správu
 * @author David Vavra
 */
public class Settings
{
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
            flashbackPeriod = 4;
            lastDevice = "";
            vip = false;
            
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
            gui.get_cgOtherSettings().setSelectedFlags(flags);
            gui.get_tfNumberCaches().setString(String.valueOf(numberCaches));
            gui.get_tfBackLight().setString(String.valueOf(flashbackPeriod));
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
            boolean[] selected = new boolean[gui.get_cgOtherSettings().size()];
            gui.get_cgOtherSettings().getSelectedFlags(selected);
            filter = "";
            for (int i=0;i<selected.length;i++)
            {
                filter += (selected[i])?"1":"0";
            }
            numberCaches = Integer.parseInt(gui.get_tfNumberCaches().getString());
            if (numberCaches > 20)
                numberCaches = 20;
            flashbackPeriod = Integer.parseInt(gui.get_tfBackLight().getString());
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
            //vlozeni znaku °
            lastLattitude = lat.substring(0,4)+"°"+lat.substring(5);
            lastLongitude = lon.substring(0,5)+"°"+lon.substring(6);
            
            store(false);
        }
        catch (Exception e)
        {
            gui.showError("saveCoordinates",e.toString(),"");
        }
    }    
    
}
