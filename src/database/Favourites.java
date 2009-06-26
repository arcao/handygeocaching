/*
 * Favourites.java
 *
 * Created on 16. øíjen 2007, 21:18
 *
 */

package database;

import gps.Gps;
import gui.Gui;
import gui.IconLoader;
import http.Http;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import javax.microedition.apdu.APDUConnection;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Displayable;
import javax.microedition.rms.RecordEnumeration;
import utils.Utils;

/**
 * Spravuje oblibene polozky
 * @author David Vavra
 */
public class Favourites extends Database
{
    //reference
    private Gps gps;
    private Http http;
    private Settings settings;
    private IconLoader iconLoader;
    
    //ostatni promenne
    public int editId = -1;
    private String editType;
    public String found = "";
    
    public Favourites(Gui ref, Settings ref2, IconLoader ref3)
    {
        super(ref, "favourites");
        settings = ref2;
        iconLoader = ref3;
    }
    
    /**
     * Dodatecne nastavovani referenci
     */
    public void setReference(Http ref)
    {
        http = ref;
    }
    
    public void setReference(Gps ref)
    {
        gps = ref;
    }
    
    /**
     * Zobrazi jednu konkretni oblibenou
     */
    public void view(int number, boolean view)
    {
        try
        {
            RecordEnumeration rc = recordStore.enumerateRecords(this, this, true);
            rc.rebuild();
            int id = 0;
            for (int i = 0; i <= number; i++)
            {
                id = rc.nextRecordId();
            }
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(recordStore.getRecord(id)));
            String name = dis.readUTF();
            String type = dis.readUTF();
            String description = dis.readUTF();
            String lattitude = dis.readUTF();
            String longitude = dis.readUTF();
            try {
                found = dis.readUTF();
            } catch (Exception e) {
                found = "NE";
            }
            //nastaveni editId pro pripad refreshe v overview a nasledneho ulozeni do oblibenych
            if (isCache(type)) //cache
            {
                editId = id;
                editType = type;
            }
            //prima navigace
            if (!view)
            {
                gps.setNavigationTarget(lattitude, longitude, name);
            }
            //zobrazeni
            else
            {
                gui.get_frmFavourite().setTitle(name);
                gui.get_siFavouriteLattitude().setText(lattitude);
                gui.get_siFavouriteLongitude().setText(longitude);
                gui.get_siDescription().setText(description);
                gui.get_siNalezeno1().setText(found);
                if (isCache(type)) //cache
                {
                    http.startOffline(Http.OVERVIEW, description);
                }
                else
                {
                    gui.getDisplay().setCurrent(gui.get_frmFavourite());
                }
            }
            
        }
        catch (Exception e)
        {
            gui.showError("viewFavourite",e.toString(),"");
        }
    }
    
    /**
     * Tato metoda otevre externi browser s mapy.cz
     */
    public void mapyCz(int number)
    {
        try
        {
            RecordEnumeration rc = recordStore.enumerateRecords(this, this, true);
            rc.rebuild();
            int id = 0;
            for (int i = 0; i <= number; i++)
            {
                id = rc.nextRecordId();
            }
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(recordStore.getRecord(id)));
            dis.readUTF();
            dis.readUTF();
            dis.readUTF();
            String lattitude = dis.readUTF();
            String longitude = dis.readUTF();
            try {
                dis.readUTF();
            } catch (Exception e) {}
            lattitude = Utils.replaceString(Utils.replaceString(lattitude, "° ","d"),"N ","");
            longitude = Utils.replaceString(Utils.replaceString(longitude, "° ","d"),"E ","");
            gui.platformRequest("http://wap.mapy.cz/search?from=&query="+lattitude+"+"+longitude+"&mapType=ophoto&zoom=16");
        }
        catch (Exception e)
        {
            gui.showError("mapyCz",e.toString(),"");
        }
    }
    
    /**
     * Prida oblibenou polozku do databaze (editId=-1), nebo edituje zadany zaznam (editId>=0)
     */
    public void addEdit(String name, String description, String lattitude, String longitude, String type, Displayable nextScreen, boolean DegMinSecFormat, String found)
    {
        try
        {
            //Zephy 19.11.07 +\
            lattitude = gps.convertLattitudeFormat(lattitude, DegMinSecFormat);
            longitude = gps.convertLongitudeFormat(longitude, DegMinSecFormat);
            //Zephy 19.11.07 +/
            
            //Zephy 19.11.07 + pridan OR v podmince
            if (lattitude=="" | Gps.convertLattitude(lattitude)==0)
            {
                if (gui.fromMultiSolver)
                    gui.showAlert("Špatný formát první souøadnice",AlertType.WARNING,gui.get_frmResult());
                else
                {
                    gui.showAlert("Špatný formát první souøadnice",AlertType.WARNING,gui.get_frmAddGiven());
                }
            }
            //Zephy 19.11.07 + pridan OR v podmince
            else if(longitude == "" | Gps.convertLongitude(longitude)==0)
            {
                
                if (gui.fromMultiSolver)
                    gui.showAlert("Špatný formát druhé souøadnice",AlertType.WARNING,gui.get_frmResult());
                else
                    gui.showAlert("Špatný formát druhé souøadnice",AlertType.WARNING,gui.get_frmAddGiven());
            }
            else
            {
                
                if (name.equals(""))
                    name = "Beze jména";
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(buffer);
                dos.writeUTF(name);
                if (editId>=0) //pri editaci se bere typ odjinud
                    type = editType;
                dos.writeUTF(type);
                dos.writeUTF(description);
                dos.writeUTF(lattitude);
                dos.writeUTF(longitude);
                dos.writeUTF(found);
                byte[] bytes = buffer.toByteArray();
                //posledni cache
                if (name.equals("_Poslední cache"))
                {                    
                    editId = -1;
                    RecordEnumeration rc = recordStore.enumerateRecords(this, this, true);
                    rc.rebuild();
                    int id = 0;
                    for (int i = 0; i < rc.numRecords(); i++)
                    {
                        id = rc.nextRecordId();
                        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(recordStore.getRecord(id)));
                        if (dis.readUTF().equals("_Poslední cache"))
                        {
                            editId = id;
                            break;
                        }
                    }
                }
                                
                
                if (editId==-1) //pridani noveho zaznamu
                    recordStore.addRecord(bytes, 0, bytes.length);
                else //editace stavajiciho zaznamu
                    recordStore.setRecord(editId, bytes, 0, bytes.length);
                settings.saveCoordinates(lattitude, longitude);
                viewAll();
                
                
                if (!name.equals("_Poslední cache") && editId==-1)
                {
                    gui.showAlert("Uloženo do oblíbených",AlertType.INFO,nextScreen);
                }
                //Zephy 19.11.07 +\ -tohle doplneno aby se po skonceni editace preslo na seznam bodu
                else
                {
                    gui.showAlert("Zmìny uloženy",AlertType.INFO,nextScreen);
                }
                //Zephy 19.11.07 +/
            }
        }
        catch (Exception ex)
        {
            gui.showError("addFavourite",ex.toString(),"");
        }
    }
    
    public String getCacheName(int number) {
        try
        {
            RecordEnumeration rc = recordStore.enumerateRecords(this, this, true);
            rc.rebuild();
            int id = 0;
            for (int i = 0; i <= number; i++)
            {
                id = rc.nextRecordId();
            }
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(recordStore.getRecord(id)));
            return dis.readUTF();
        }
        catch (Exception e)
        {
            gui.showError("getCacheName",e.toString(),"");
            return "";
        }
    }
    
    
    public void setFound(int number, Date found, Displayable nextScreen) {
        try
        {
            RecordEnumeration rc = recordStore.enumerateRecords(this, this, true);
            rc.rebuild();
            int id = 0;
            for (int i = 0; i <= number; i++)
            {
                id = rc.nextRecordId();
            }
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(recordStore.getRecord(id)));
            String name = dis.readUTF();
            String type = dis.readUTF();
            String description = dis.readUTF();
            String lattitude = dis.readUTF();
            String longitude = dis.readUTF();
            dis.readUTF();
            editId = id;
            editType = type;
            
            addEdit(name, description, lattitude, longitude, type, nextScreen, false, Utils.formatDate(found));
        }
        catch (Exception e)
        {
            gui.showError("setFound",e.toString(),"");
        }
    }
    
    /**
     * Zobrazi jednu konkretni oblibenou pro editaci
     */
    public void edit(int number)
    {
        try
        {
            RecordEnumeration rc = recordStore.enumerateRecords(this, this, true);
            rc.rebuild();
            int id = 0;
            for (int i = 0; i <= number; i++)
            {
                id = rc.nextRecordId();
            }
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(recordStore.getRecord(id)));
            String name = dis.readUTF();
            String type = dis.readUTF();
            String description = dis.readUTF();
            String lattitude = dis.readUTF();
            String longitude = dis.readUTF();
            try {
                found = dis.readUTF();
            } catch (Exception e) {
                found = "NE";
            }
            editId = id;
            editType = type;
            
            //zobrazeni
            if (isCache(type))
            {
                gui.showAlert("Cache není možné upravovat",AlertType.WARNING,gui.get_lstFavourites());
            }
            else
            {
                gui.get_frmAddGiven().setTitle("Upravit bod");
                gui.get_tfGivenName().setString(name);
                gui.get_tfGivenLattitude().setString(lattitude);
                gui.get_tfGivenLongitude().setString(longitude);
                gui.get_tfGivenDescription().setString(description);
                gui.get_siNalezeno().setText(found);
                gui.getDisplay().setCurrent(gui.get_frmAddGiven());
            }
        }
        catch (Exception e)
        {
            gui.showError("editFavourite",e.toString(),"");
        }
    }
    
    /**
     * Smaze ty oblibene, ktere jsou zaskrtnute checkboxem
     */
    public void delete()
    {
        try
        {
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
                if (gui.get_lstFavourites().isSelected(i))
                {
                    recordStore.deleteRecord(recordIds[i]);
                }
            }
            viewAll();
        }
        catch (Exception e)
        {
            gui.showError("deleteFavourites",e.toString(),"");
        }
    }
    
    /**
     * Vypise seznam oblibenych bodu
     */
    public void viewAll()
    {
        try
        {
            gui.get_lstFavourites().deleteAll();
            RecordEnumeration rc = recordStore.enumerateRecords(this, this, true);
            rc.rebuild();
            for (int i = 0; i < rc.numRecords(); i++)
            {
                DataInputStream dis = new DataInputStream(new ByteArrayInputStream(recordStore.getRecord(rc.nextRecordId())));
                String name = dis.readUTF();
                String type = dis.readUTF();
                gui.get_lstFavourites().append(name,iconLoader.loadIcon(type));
            }
        }
        catch (Exception ex)
        {
            gui.showError("viewFavourites",ex.toString(),"");
        }
    }
    
    /**
     * Nahraje vsechny oblibene do mapy
     */
    public void loadFavouritesToMap()
    {
        try
        {
            gui.get_cvsMap().reset();
            RecordEnumeration rc = recordStore.enumerateRecords(this, this, true);
            rc.rebuild();
            for (int i = 0; i < rc.numRecords(); i++)
            {
                DataInputStream dis = new DataInputStream(new ByteArrayInputStream(recordStore.getRecord(rc.nextRecordId())));
                String name = dis.readUTF();
                String type = dis.readUTF();
                dis.readUTF();
                double lattitude = Gps.convertLattitude(dis.readUTF());
                double longitude = Gps.convertLongitude(dis.readUTF());
                try {
                    dis.readUTF();
                } catch (Exception e) {}
                
                if (!name.equals("_Poslední cache"))
                    gui.get_cvsMap().addMapItem(lattitude,longitude,type,name);
            }
        }
        catch (Exception ex)
        {
            gui.showError("loadFavouritesToMap",ex.toString(),"");
        }
    }
    
    /**
     * Tato metoda prida do oblibenych vsechny pridavne waypointy
     */
    public void addWaypoints(String data)
    {
        try
        {
            String[][] waypoints = http.parseData(data);
            for (int i=0;i<waypoints.length;i++)
            {
                //ulozeni do db
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(buffer);
                dos.writeUTF(waypoints[i][1]+"-"+waypoints[i][0]);
                dos.writeUTF("additional_waypoint");
                dos.writeUTF(waypoints[i][4]);
                dos.writeUTF(waypoints[i][2]);
                dos.writeUTF(waypoints[i][3]);
                byte[] bytes = buffer.toByteArray();
                recordStore.addRecord(bytes, 0, bytes.length);
            }
            viewAll();
        }
        catch (Exception e)
        {
            gui.showError("addWaypoints",e.toString(),"");
        }
    }
    
    /**
     * Vrati true, pokud typ je nejaky druh kese
     */
    private boolean isCache(String type)
    {
        //Zephy 20.11.07 +\ - kvuli zmene typu
        //return (!type.equals("0") && !type.equals("1") && !type.equals("20") && !type.equals("averaging"));
        return (type.substring(0,3).equals("gc_"));
        //Zephy 20.11.07 +/
    }
    
    
    
}
