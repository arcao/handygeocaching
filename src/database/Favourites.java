/*
 * Favourites.java
 *
 * Created on 16. říjen 2007, 21:18
 *
 */

package database;

import gps.Gps;
import gui.Gui;
import gui.IconLoader;
import gui.LoadingForm;
import http.Http;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import javax.microedition.apdu.APDUConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Displayable;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
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
    public int id = -1;
    public int editId = -1;
    private String editType;
    public String found = "";
    public String poznamka = "";
    
    private RecordEnumeration recordEnumeration = null;

    private boolean needUpdateViewAll = true;
    
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

    public void deleteAll() {
        revalidate();
        super.deleteAll();
        http.getHintCache().deleteAll();
        http.getListingCache().deleteAll();
    }


    
    /**
     * Zobrazi jednu konkretni oblibenou
     */
    public void view(int number, boolean view)
    {
        try
        {
            this.id = number;
            RecordEnumeration rc = getRecordEnumeration();
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
            try {
                poznamka = dis.readUTF();
            } catch (Exception e) {
                poznamka = "";
            }
            //nastaveni editId pro pripad refreshe v overview a nasledneho ulozeni do oblibenych
            //if (isCache(type)) //cache
            //{
            editId = id;
            editType = type;
            //}
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
                gui.get_siPoznamka().setText(poznamka);
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
        System.out.println("id:" + String.valueOf(id));
        System.out.println("editId:" + String.valueOf(editId));
    }
    
    /**
     * Tato metoda otevre externi browser s mapy.cz
     */
    public void mapyCz(int number)
    {
        try
        {
            this.id = number;
            RecordEnumeration rc = getRecordEnumeration();
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
            
            //lattitude = Utils.replaceString(Utils.replaceString(lattitude, "° ","d"),"N ","");
            //longitude = Utils.replaceString(Utils.replaceString(longitude, "° ","d"),"E ","");
            lattitude = Double.toString(Gps.convertLattitude(lattitude));
            longitude = Double.toString(Gps.convertLongitude(longitude));
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
    public void addEdit(String name, String description, String lattitude, String longitude, String type, Displayable nextScreen, boolean DegMinSecFormat, String found, String poznamka) {
        addEdit(name, description, lattitude, longitude, type, nextScreen, DegMinSecFormat, found, poznamka, true, true, true);
    }
    
    public void addEdit(String name, String description, String lattitude, String longitude, String type, Displayable nextScreen, boolean DegMinSecFormat, String found, String poznamka, boolean refreshView, boolean needRevalidate, boolean saveLastUse)
    {
        Alert alert = null;
        try
        {
            //debug info
            //System.out.println("addEdit");
            //System.out.println("name: " + name);
            //System.out.println("desc: " + description);
            //System.out.println("lat: " + lattitude);
            //System.out.println("lon: " + longitude);
            //System.out.println("type: " + type);
            //System.out.println("degFormat: " + ((DegMinSecFormat) ? "true" : "false"));
            //System.out.println("found: " + found);
            //System.out.println("poznamka: " + poznamka);
            
            //Zephy 19.11.07 +\
            lattitude = gps.convertLattitudeFormat(lattitude, DegMinSecFormat);
            longitude = gps.convertLongitudeFormat(longitude, DegMinSecFormat);
            //Zephy 19.11.07 +/
            
            //Zephy 19.11.07 + pridan OR v podmince
            if (lattitude=="" | Gps.convertLattitude(lattitude)==Double.NaN)
            {
                if (gui.fromMultiSolver)
                    alert = gui.showAlert("Špatný formát první souřadnice",AlertType.WARNING,gui.get_frmResult());
                else
                {
                    alert = gui.showAlert("Špatný formát první souřadnice",AlertType.WARNING,gui.get_frmAddGiven());
                }
            }
            //Zephy 19.11.07 + pridan OR v podmince
            else if(longitude == "" | Gps.convertLongitude(longitude)==Double.NaN)
            {
                
                if (gui.fromMultiSolver)
                    alert = gui.showAlert("Špatný formát druhé souřadnice",AlertType.WARNING,gui.get_frmResult());
                else
                    alert = gui.showAlert("Špatný formát druhé souřadnice",AlertType.WARNING,gui.get_frmAddGiven());
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
                dos.writeUTF(poznamka);
                byte[] bytes = buffer.toByteArray();
                //posledni cache
                if (name.equals("_Poslední cache"))
                {                    
                    editId = -1;
                    RecordEnumeration rc = getRecordEnumeration();
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
                                
                
                if (editId==-1) { //pridani noveho zaznamu
                    this.id = recordStore.addRecord(bytes, 0, bytes.length) - 1;
                }
                else
                {
                    //editace stavajiciho zaznamu
                    this.id = editId - 1;
                    recordStore.setRecord(editId, bytes, 0, bytes.length);
                }
                if (needRevalidate) 
                    revalidate();
                if (saveLastUse)
                    settings.saveCoordinates(lattitude, longitude);
                
                if (!name.equals("_Poslední cache") && editId==-1)
                {
                    if (nextScreen != null) alert = gui.showAlert("Uloženo do oblíbených",AlertType.INFO,nextScreen);
                }
                //Zephy 19.11.07 +\ -tohle doplneno aby se po skonceni editace preslo na seznam bodu
                else
                {
                    if (nextScreen != null) alert = gui.showAlert("Změny uloženy",AlertType.INFO,nextScreen);
                }
                //Zephy 19.11.07 +/
                
                if (refreshView)
                    viewAll(alert);
            }
        }
        catch (Exception ex)
        {
            gui.showError("addFavourite",ex.toString(),"");
            ex.printStackTrace();
        }
        //System.out.println("id:" + String.valueOf(id));
        //System.out.println("editId:" + String.valueOf(editId));
    }
    
    public String getCacheName(int number) {
        try
        {
            RecordEnumeration rc = getRecordEnumeration();
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
            this.id = number;
            RecordEnumeration rc = getRecordEnumeration();
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
            String poznamka = "";
            try{
                dis.readUTF();
                poznamka = dis.readUTF();
            } catch(Exception e) {}
            editId = id;
            editType = type;
            
            addEdit(name, description, lattitude, longitude, type, nextScreen, false, Utils.formatDate(found), poznamka, false, false, true);
        }
        catch (Exception e)
        {
            gui.showError("setFound",e.toString(),"");
        }
    }
    
    public void setPoznamka(int number, String poznamka, Displayable nextScreen) {
        try
        {
            this.id = number;
            RecordEnumeration rc = getRecordEnumeration();
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
            String found = "NE";
            try{
                found = dis.readUTF();
                dis.readUTF();
            } catch(Exception e) {}
            editId = id;
            editType = type;
            
            addEdit(name, description, lattitude, longitude, type, nextScreen, false, found, poznamka, false, false, true);
        }
        catch (Exception e)
        {
            gui.showError("setPoznamka",e.toString(),"");
        }
    }
    
    public String getPoznamka(int number) {
        try
        {
            RecordEnumeration rc = getRecordEnumeration();
            int id = 0;
            for (int i = 0; i <= number; i++)
            {
                id = rc.nextRecordId();
            }
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(recordStore.getRecord(id)));
            dis.readUTF(); //name
            dis.readUTF(); //type
            dis.readUTF(); //description
            dis.readUTF(); //lattitude
            dis.readUTF(); //longitude
            dis.readUTF(); //found
            return dis.readUTF(); //poznamka
        }
        catch (Exception e)
        {
            gui.showError("getPoznamka",e.toString(),"");
            return "";
        }
    }
    
    
    /**
     * Zobrazi jednu konkretni oblibenou pro editaci
     */
    public void edit(int number)
    {
        try
        {
            this.id = number;
            
            RecordEnumeration rc = getRecordEnumeration();
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
            try {
                String poznamka = dis.readUTF();
            } catch (Exception e) {
                String poznamka = "";
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
        System.out.println("id:" + String.valueOf(id));
        System.out.println("editId:" + String.valueOf(editId));
    }
    
    /**
     * Smaze ty oblibene, ktere jsou zaskrtnute checkboxem
     */
    public void delete()
    {
        try
        {
            RecordEnumeration rc = getRecordEnumeration();
            int numRecords = rc.numRecords();
            int[] recordIds = new int[numRecords];
            for (int i = 0; i < numRecords; i++)
            {
                recordIds[i] = rc.nextRecordId();
            }
            boolean needRevalidate = false;
            for (int i = 0; i < numRecords; i++)
            {
                if (gui.get_lstFavourites().isSelected(i))
                {
                    String[] parts = getCachePartsID(recordIds[i]);
                    if (parts.length > 0) {
                        http.getHintCache().delete(parts[7]); //delete hint
                        http.getListingCache().delete(parts[7]); //delete listing
                    }
                    recordStore.deleteRecord(recordIds[i]);
                    needRevalidate = true;
                }
            }
            
            if (needRevalidate) {
                revalidate();
                viewAll();
            }
        }
        catch (Exception e)
        {
            gui.showError("deleteFavourites",e.toString(),"");
        }
    }
    
    /**
     * Vypise seznam oblibenych bodu
     */
    public void viewAll() {
        viewAll(null);
    }
    
    public void viewAll(Alert alert)
    {
        if (!needUpdateViewAll) {
            gui.getDisplay().setCurrent(gui.get_lstFavourites());
            return;
        }
        needUpdateViewAll = false;
        final LoadingForm lForm = new LoadingForm(gui.getDisplay(), "Načítám..", "Načítám seznam keší...", gui.get_lstFavourites(), alert);
        lForm.show();
        
        try
        {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        //if (recordStore.getNumRecords() != 0)
                        //    lForm.show();
                        
                        gui.get_lstFavourites().deleteAll();
                        RecordEnumeration rc = getRecordEnumeration();
                        int count = rc.numRecords();
                        
                        for (int i = 0; i < count; i++)
                        {
                            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(recordStore.getRecord(rc.nextRecordId())));
                            String name = dis.readUTF();
                            String type = dis.readUTF();
                            gui.get_lstFavourites().append(name,iconLoader.loadIcon(type));
                        }
                    } catch (Exception e) {}
                    lForm.setFinish();

                }
            });
            t.start();
        }
        catch (Exception ex)
        {
            gui.showError("viewFavourites",ex.toString(),"");
            lForm.setFinish();
        }
    }
    
    /**
     * Vraci pole informaci o kesi.
     * Pole: name, author, type, size, latitude, longitude, difficulty(x/x), GC_num, inventory, disabled/archived, typeCode(gc_xxx), hasWaypoints(0/1), hasHints(0/1), isMultiSolverSupported(0/1), listingSize(KB)
     */
    public String[] getCacheParts(int index) {
        try {
            RecordEnumeration rc = getRecordEnumeration();
            int id = 0;
            for (int i = 0; i <= index; i++)
            {
                id = rc.nextRecordId();
            }
            return getCachePartsID(id);
        } catch (Exception e) {
            e.printStackTrace();
            return new String[0];
        }
    }
    public String[] getCachePartsID(int id) {
        try {
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(recordStore.getRecord(id)));
            dis.readUTF(); //name
            String type = dis.readUTF(); //type
            String description = dis.readUTF();
            
            if (!isCache(type))
                return new String[0];
            
            String[][] data =gui.http.parseData(description);
            if (data.length == 0 || data[0].length == 0)
                return new String[0];
            
            return data[0];
        } catch (Exception e) {
            e.printStackTrace();
            return new String[0];
        }
    }
    
    public static String cachePartsToDesc(String[][] parts) {
        StringBuffer sb = new StringBuffer();
        for (int x=0; x < parts.length; x++) {
            for(int y=0; y < parts[x].length; y++) {
                if (y != 0)
                    sb.append('}'); //line item sep
                sb.append(parts[x][y]);
            }
            sb.append('{'); //line sep
        }
        return sb.toString();
    }
    
    /**
     * Nahraje vsechny oblibene do mapy
     */
    public void loadFavouritesToMap()
    {
        try
        {
            gui.get_cvsMap().reset();
            RecordEnumeration rc = getRecordEnumeration();
            for (int i = 0; i < rc.numRecords(); i++)
            {
                DataInputStream dis = new DataInputStream(new ByteArrayInputStream(recordStore.getRecord(rc.nextRecordId())));
                String name = dis.readUTF();
                String type = dis.readUTF();
                dis.readUTF();
                double lattitude = Gps.convertLattitude(dis.readUTF());
                double longitude = Gps.convertLongitude(dis.readUTF());
                                
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
            revalidate();
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
    
    private RecordEnumeration getRecordEnumeration() {
        try {
            if (recordEnumeration == null) {
                recordEnumeration = recordStore.enumerateRecords(this, this, true);
                recordEnumeration.rebuild();
            }
            recordEnumeration.reset();
        } catch (Exception e) {}
        
        return recordEnumeration;
    }
    
    public void revalidate() {
        needUpdateViewAll = true;
        if (recordEnumeration != null)
            recordEnumeration.destroy();
        recordEnumeration = null;
    }
}
