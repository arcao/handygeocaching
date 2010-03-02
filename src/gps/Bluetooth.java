/*
 * Bluetooth.java
 * This file is part of HandyGeocaching.
 *
 * HandyGeocaching is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * (read more at: http://www.gnu.org/licenses/gpl.html)
 */
package gps;

import database.Favourites;
import database.Settings;
import gui.Gui;
import http.Http;
import java.io.IOException;
import java.util.Vector;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.lcdui.AlertType;

/***
 * Tato trida se stara o nalezeni BT zarizeni, navazani komunikace s BT zarizenim a overeni, ze se jedna o GPS modul
 * @author David Vavra
 */
public class Bluetooth implements DiscoveryListener
{
    //reference
    Gui gui;
    GpsParser gpsParser;
    Http http;
    Settings settings;
    Favourites favourites;
    
    
    protected DiscoveryAgent discoveryAgent;
    private LocalDevice localDevice;
    public static String bluetoothAdress = "";
    public Vector devices;
    private boolean quickLaunch;
    
    /***
     * Hned pri vytvoreni se zacinaji hledat nova Bluetooth zarizeni
     */
    public Bluetooth(Gui reference, Http ref2, Settings ref3, Favourites ref4, boolean quick)
    {
        gui = reference;
        http = ref2;
        settings = ref3;
        favourites = ref4;
        quickLaunch = quick;
    }
    
    public void setReference(GpsParser ref)
    {
        gpsParser = ref;
    }
    
    /**
     * Zjisti, zda je Bluetooth zapnuty
     */
    public boolean isOn()
    {
        //mozna buggy, tak vraci vzdy true
        return true;
        /*try
        {
            localDevice = LocalDevice.getLocalDevice();
            localDevice.setDiscoverable(DiscoveryAgent.GIAC);
            return true;
        }
        catch (BluetoothStateException ex)
        {
            gui.showAlert("Máte vypnuté Bluetooth!",AlertType.WARNING,gui.get_lstMode());
            return false;
        }*/
    }
    
    /**
     * Zacne hledat BT zarizeni
     */
    public void searchDevices()
    {
        try
        {
            gui.get_frmConnecting().append("\nHledám dostupná Bluetooth zařízení...");
            devices = new Vector();
            discoveryAgent = localDevice.getDiscoveryAgent();
            discoveryAgent.startInquiry(DiscoveryAgent.GIAC, this);
        }
        catch (Exception e)
        {
            gui.showError("searchDevices",e.toString(),"");
        }
    }
    
    /**
     * Start hledani sluzeb daneho BT zarizeni. Nutno v novem vlakne
     */
    public void searchForServices()
    {
        try
        {
            gui.get_frmConnecting().append("\nPřipojuji k zařízení...");
            discoveryAgent.cancelInquiry(this);
            SearchServices search = new SearchServices(gui, this);
            search.start();
        }
        catch (Exception e)
        {
            gui.get_frmConnecting().append("\nNastala vyjímka: searchForServices, "+e.toString());
        }
    }
    
    /**
     * Zobrazeni nalezeneho zarizeni
     */
    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod)
    {
        String progress="";
        try
        {
            devices.addElement(btDevice);
            if (devices.size()==1)
            {
                gui.getDisplay().setCurrent(gui.get_lstDevices());
                gui.get_lstDevices().deleteAll();
            }
            String name;
            try
            {
                name = btDevice.getFriendlyName(false);
            }
            catch (IOException ex)
            {
                name = "Neznámé BT zařízení";
            }
            gui.get_lstDevices().append(name,null);
        }
        catch (Exception e)
        {
            gui.showAlert("Nastala vyjímka: deviceDiscovered, "+e,AlertType.ERROR,gui.get_lstDevices());
        }
    }
    
    /**
     * Po dokonceni hledani se kontroluje, jestli bylo vubec neco nalezeno
     */
    public void inquiryCompleted(int discType)
    {
        try
        {
            if (devices.size()==0)
            {
                gui.get_frmConnecting().append("\nŽádná Bluetooth zařízení v dosahu nebo problém s Bluetooth");
            }
        }
        catch (Exception e)
        {
            gui.showAlert("Nastala vyjímka: inquiryCompleted, "+e,AlertType.ERROR,gui.get_lstDevices());
        }
    }
    
    /**
     * Zpracovani nalezene sluzby, zjisteni discoveryAgent adresy, start GPS komunikace
     */
    public void servicesDiscovered(int transID, ServiceRecord[] servRecord)
    {
        try
        {
            //btspp://000A3A2424BE:1;authenticate=false;encrypt:false;master=false
            gui.get_frmConnecting().append("\nPřipojeno!");
            //zjisteni discoveryAgent adresy
            bluetoothAdress = servRecord[0].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
            discoveryAgent.cancelServiceSearch(transID);
            //zahajeni gps komunikace
            gpsParser = new GpsParser(gui, http, settings, favourites, this, bluetoothAdress, GpsParser.BLUETOOTH);
            gpsParser.open();
        }
        catch (Exception e)
        {
            gui.get_frmConnecting().append("\nNastala vyjímka: serviceDiscovered, "+e);
        }
    }
    
    /**
     * Pokud sluzba nebyla nalezena, zobraz chybu
     */
    public void serviceSearchCompleted(int transID, int responseCode)
    {
        if (bluetoothAdress.equals(""))
        {
            gui.showAlert("Váš telefon je připojen k jinému BT zařízení nebo GPS modul již komunikuje s jiným mobilem.",AlertType.ERROR,gui.get_lstDevices());
        }
    }
}

/**
 * Toto vlakno se stara o spusteni hledani Bluetooth sluzeb
 */
class SearchServices extends Thread
{
    Gui main;
    Bluetooth bl;
    SearchServices(Gui reference, Bluetooth blreference)
    {
        main = reference;
        bl = blreference;
    }
    public void run()
    {
        boolean searching = true;
        int timeout=0;
        while (searching)
        {
            try
            {
                timeout++;
                if (timeout>20)
                {
                    main.get_frmConnecting().append("\nNepodařilo se zahájit hledání Bluetooth služby");
                    searching = false;
                }
                bl.discoveryAgent.searchServices(null, new UUID[]{new UUID(0x1101)},(RemoteDevice)bl.devices.elementAt(main.get_lstDevices().getSelectedIndex()),bl);
                searching = false;
            }
            catch (BluetoothStateException ex)
            {
                try
                {
                    sleep(150);
                }
                catch (InterruptedException exc)
                {
                    main.get_frmConnecting().append("\nNastala vyjímka: SearchServices, "+exc);
                }
            }
        }
    }
}

