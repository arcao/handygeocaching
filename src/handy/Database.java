/**
 * Database.java
 *
 * Created on 29. duben 2007, 15:43
 *
 */

package handy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordComparator;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordFilter;
import javax.microedition.rms.RecordStore;

/***
 * Tato trida pracuje s RMS, uklada persistentni data a nacita je do aplikace
 * @author David Vavra
 */
public class Database implements RecordFilter, RecordComparator
{
    private final String DB_SETTING = "HGSetLit";
    private final String DB_FAVOURITES = "HGFavLit";
    private final String DB_LETTERS = "HGLetLit";
    private final String DB_PATTERNS = "HGPatLit";
    
    //reference
    Gui gui;
    Http http;
    
    //nastaveni
    String name;
    String password;
    String filter;
    String lastLattitude;
    String lastLongitude;
    int numberCaches;
    String defaultLog;
    boolean vip;
    
    int editId = -1;
    private String editType;
        
    /*** Creates a new instance of Database */
    public Database(Gui reference)
    {
        gui = reference;
        /*try
        {
            RecordStore.deleteRecordStore("settings");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }*/
    }
    
    public void setHttpReference(Http ref)
    {
        http = ref;
    }
    
    /**
     * Po startu aplikace tato metoda nacte data do promennych nebo vytvori
     * defaultni hodnoty nastaveni
     * vraci true, pokud se nacteni databaze podari
     */
    public boolean loadSettings()
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
            defaultLog = "Nalezeno %t. Zalogováno pomocí mobilní aplikace Handy Geocaching http://www.destil.cz";
            vip = false;
            
            RecordStore rsSettings = RecordStore.openRecordStore(DB_SETTING, true);
            if (rsSettings.getNumRecords() == 0)
            {  //prvni start aplikace
                rsSettings.closeRecordStore();
                storeSettings(true);
            }
            else
            {
                //ostatni starty aplikace, nacitam data
                byte[] datas = rsSettings.getRecord(1);
                DataInputStream DI = new DataInputStream(new ByteArrayInputStream(datas));
                
                name = DI.readUTF();
                password = DI.readUTF();
                filter = DI.readUTF();
                lastLattitude = DI.readUTF();
                lastLongitude = DI.readUTF();
                numberCaches = DI.readInt();
                defaultLog = DI.readUTF();
                vip = DI.readBoolean();
                rsSettings.closeRecordStore();
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
     * Po kontrole vip pri prihlasovani nastavi VIP mod
     */
    public void setVIP(boolean mode)
    {
        try
        {
            vip = mode;
            storeSettings(false);
        }
        catch (Exception e)
        {
            gui.showError("setVIP",e.toString(),"");
        }
    }
    
    /**
     * Nastavi formular podle ulozeneho nastaveni
     */
    public void setSettings()
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
            gui.get_tfDefaultLog().setString(defaultLog);
        }
        catch (Exception e)
        {
            gui.showError("setSettings",e.toString(),"");
        }
    }
    
    /**
     * Ulozi vybrane nastaveni do promennych
     */
    public void saveSettings()
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
            defaultLog = gui.get_tfDefaultLog().getString();
            storeSettings(false);
        }
        catch (Exception e)
        {
            gui.showError("saveSettings",e.toString(),"");
        }
    }
    
    /**
     * Ulozi nastaveni podle promennych do databaze
     */
    public void storeSettings(boolean createNewRecord)
    {
        try
        {
            RecordStore rsSettings = RecordStore.openRecordStore(DB_SETTING, true);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(buffer);
            dos.writeUTF(name);
            dos.writeUTF(password);
            dos.writeUTF(filter);
            dos.writeUTF(lastLattitude);
            dos.writeUTF(lastLongitude);
            dos.writeInt(numberCaches);
            dos.writeUTF(defaultLog);
            dos.writeBoolean(vip);
            byte[] bytes = buffer.toByteArray();
            if (createNewRecord)
                rsSettings.addRecord(bytes, 0, bytes.length);
            else
                rsSettings.setRecord(1, bytes, 0, bytes.length);
            rsSettings.closeRecordStore();
        }
        catch (Exception e)
        {
            gui.showError("storeSettings",e.toString(),"");
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
            
            storeSettings(false);
        }
        catch (Exception e)
        {
            gui.showError("saveCoordinates",e.toString(),"");
        }
    }
    
    /**
     * Ulozi oblibeny bod do databaze
     */
    public void addFavourite(String name, String description, String lattitude, String longitude, String type)
    {
        try
        {
            if (Utils.convertLattitude(lattitude).equals("0"))
            {
                if (gui.multiSolver)
                    gui.showAlert("Špatný formát první souřadnice",AlertType.WARNING,gui.get_frmResult());
                else
                    gui.showAlert("Špatný formát první souřadnice",AlertType.WARNING,gui.get_frmAddGiven());
            }
            else if(Utils.convertLongitude(longitude).equals("0"))
            {
                if (gui.multiSolver)
                    gui.showAlert("Špatný formát druhé souřadnice",AlertType.WARNING,gui.get_frmResult());
                else
                    gui.showAlert("Špatný formát druhé souřadnice",AlertType.WARNING,gui.get_frmAddGiven());
            }
            else
            {
                if (name.equals(""))
                    name = "Beze jména";
                RecordStore rsFavourites = RecordStore.openRecordStore(DB_FAVOURITES, true);
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(buffer);
                dos.writeUTF(name);
                if (editId>=0) //pri editaci se bere typ odjinud
                    type = editType;
                dos.writeUTF(type);
                dos.writeUTF(description);
                dos.writeUTF(lattitude);
                dos.writeUTF(longitude);
                byte[] bytes = buffer.toByteArray();
                //posledni cache
                if (name.equals("Poslední cache"))
                {
                    editId = -1;
                    RecordEnumeration rc = rsFavourites.enumerateRecords(this, this, true);
                    rc.rebuild();
                    int id = 0;
                    for (int i = 0; i < rc.numRecords(); i++)
                    {
                        id = rc.nextRecordId();
                        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(rsFavourites.getRecord(id)));
                        if (dis.readUTF().equals("Poslední cache"))
                        {
                            editId = id;
                            break;
                        }
                    }
                }
                if (editId==-1) //pridani noveho zaznamu
                    rsFavourites.addRecord(bytes, 0, bytes.length);
                else //editace stavajiciho zaznamu
                    rsFavourites.setRecord(editId, bytes, 0, bytes.length);
                rsFavourites.closeRecordStore();
                saveCoordinates(lattitude, longitude);
                viewFavourites();
                if (gui.favourites)
                    gui.getDisplay().setCurrent(gui.get_lstFavourites());
                if (gui.multiSolver)
                    gui.getDisplay().setCurrent(gui.get_lstFavourites());
            }
        }
        catch (Exception ex)
        {
            gui.showError("addFavourite",ex.toString(),"");
        }
    }
    
    /**
     * Vypise seznam oblibenych bodu
     */
    public void viewFavourites()
    {
        try
        {
            gui.clearListForm(gui.get_lstFavourites(),null);
            RecordStore rsFavourites = RecordStore.openRecordStore(DB_FAVOURITES, true);
            RecordEnumeration rc = rsFavourites.enumerateRecords(this, this, true);
            rc.rebuild();
            for (int i = 0; i < rc.numRecords(); i++)
            {
                DataInputStream dis = new DataInputStream(new ByteArrayInputStream(rsFavourites.getRecord(rc.nextRecordId())));
                String name = dis.readUTF();
                String type = dis.readUTF();
                gui.get_lstFavourites().append(name,Image.createImage("/"+type+".png"));
            }
            rsFavourites.closeRecordStore();
        }
        catch (Exception ex)
        {
            gui.showError("viewFavourites",ex.toString(),"");
        }
    }
    
    /**
     * Smaze vsechny oblibene
     */
    public void deleteAllFavourites()
    {
        try
        {
            gui.clearListForm(gui.get_lstFavourites(),null);
            RecordStore rsFavourites = RecordStore.openRecordStore(DB_FAVOURITES, true);
            rsFavourites.closeRecordStore();
            RecordStore.deleteRecordStore("favourites");
        }
        catch (Exception ex)
        {
            gui.showError("deleteAllFavourites",ex.toString(),"");
        }
    }
    
    /**
     * Zobrazi jednu konkretni oblibenou
     */
    public void viewFavourite(int number, boolean view)
    {
        try
        {
            RecordStore rsFavourites = RecordStore.openRecordStore(DB_FAVOURITES, true);
            RecordEnumeration rc = rsFavourites.enumerateRecords(this, this, true);
            rc.rebuild();
            int id = 0;
            for (int i = 0; i <= number; i++)
            {
                id = rc.nextRecordId();
            }
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(rsFavourites.getRecord(id)));
            String name = dis.readUTF();
            String type = dis.readUTF();
            String description = dis.readUTF();
            String lattitude = dis.readUTF();
            String longitude = dis.readUTF();
            rsFavourites.closeRecordStore();
            if (view)
            {
                gui.get_frmFavourite().setTitle(name);
                gui.get_siFavouriteLattitude().setText(lattitude);
                gui.get_siFavouriteLongitude().setText(longitude);
                gui.get_siDescription().setText(description);
                if (!type.equals("0") && !type.equals("1") && !type.equals("20")  && !type.equals("prumerovani")) //cache
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
     * Zobrazi jednu konkretni oblibenou
     */
    public void editFavourite(int number)
    {
        try
        {
            RecordStore rsFavourites = RecordStore.openRecordStore(DB_FAVOURITES, true);
            RecordEnumeration rc = rsFavourites.enumerateRecords(this, this, true);
            rc.rebuild();
            int id = 0;
            for (int i = 0; i <= number; i++)
            {
                id = rc.nextRecordId();
            }
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(rsFavourites.getRecord(id)));
            String name = dis.readUTF();
            String type = dis.readUTF();
            String description = dis.readUTF();
            String lattitude = dis.readUTF();
            String longitude = dis.readUTF();
            rsFavourites.closeRecordStore();
            editId = id;
            editType = type;
            
            //zobrazeni
            if (!type.equals("0") && !type.equals("1") && !type.equals("20")) //cache
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
                gui.getDisplay().setCurrent(gui.get_frmAddGiven());
            }
        }
        catch (Exception e)
        {
            gui.showError("editFavourite",e.toString(),"");
        }
    }
    
    /**
     * Tato metoda prida do oblibenych vsechny pridavne waypointy
     */
    public void addWaypoints(String data)
    {
        try
        {
            RecordStore rsFavourites = RecordStore.openRecordStore(DB_FAVOURITES, true);
            String[][] waypoints = http.parseData(data);
            for (int i=0;i<waypoints.length;i++)
            {
                //ulozeni do db
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(buffer);
                dos.writeUTF(waypoints[i][1]+"-"+waypoints[i][0]);
                dos.writeUTF("1");
                dos.writeUTF(waypoints[i][4]);
                dos.writeUTF(waypoints[i][2]);
                dos.writeUTF(waypoints[i][3]);
                byte[] bytes = buffer.toByteArray();
                rsFavourites.addRecord(bytes, 0, bytes.length);
            }
            rsFavourites.closeRecordStore();
            viewFavourites();
        }
        catch (Exception e)
        {
            gui.showError("addWaypoints",e.toString(),"");
        }
    }
    
    /**
     * Smaze ty oblibene, ktere jsou zaskrtnute checkboxem
     */
    public void deleteFavourites()
    {
        try
        {
            RecordStore rsFavourites = RecordStore.openRecordStore(DB_FAVOURITES, true);
            RecordEnumeration rc = rsFavourites.enumerateRecords(this, this, true);
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
                    rsFavourites.deleteRecord(recordIds[i]);
                }
            }
            rsFavourites.closeRecordStore();
            viewFavourites();
        }
        catch (Exception e)
        {
            gui.showError("deleteFavourites",e.toString(),"");
        }
    }
    
    /**
     * Zobrazi informace o multine podle informaci v databazi
     */
    public void viewMultiSolver()
    {
        try
        {
            RecordStore rsLetters = RecordStore.openRecordStore(DB_LETTERS, true);
            RecordEnumeration rc = rsLetters.enumerateRecords(this, this, true);
            rc.rebuild();
            String letters = "";
            for (int i = 0; i < rc.numRecords(); i++)
            {
                DataInputStream dis = new DataInputStream(new ByteArrayInputStream(rsLetters.getRecord(rc.nextRecordId())));
                String letter = dis.readUTF();
                int value = dis.readInt();
                letters += letter+"="+value+", ";
            }
            gui.get_siLetters().setText(letters);
            rsLetters.closeRecordStore();
        }
        catch (Exception ex)
        {
            gui.showError("viewMultiSolver",ex.toString(),"");
        }
    }
    
    /**
     * Zobrazi vsechny vzorce
     */
    public void viewPatterns()
    {
        try
        {
            gui.clearListForm(gui.get_lstPatterns(), null);
            RecordStore rsPatterns = RecordStore.openRecordStore(DB_PATTERNS, true);
            RecordEnumeration rc = rsPatterns.enumerateRecords(this, this, true);
            rc.rebuild();
            for (int i = 0; i < rc.numRecords(); i++)
            {
                DataInputStream dis = new DataInputStream(new ByteArrayInputStream(rsPatterns.getRecord(rc.nextRecordId())));
                gui.get_lstPatterns().append(dis.readUTF(),null);
            }
            rsPatterns.closeRecordStore();
        }
        catch (Exception ex)
        {
            gui.showError("viewPatterns",ex.toString(),"");
        }
    }
    
    /**
     * Prida nebo edituje pismeno multiSolveru
     */
    public void addEditLetter()
    {
        try
        {
            //prevod dat na stream bytu
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(buffer);
            dos.writeUTF(gui.get_tfLetter().getString().toUpperCase());
            dos.writeInt(Integer.parseInt(gui.get_tfValue().getString()));
            byte[] bytes = buffer.toByteArray();
            //nejdriv zjistit zda uz dane pismeno v databazi je
            RecordStore rsLetters = RecordStore.openRecordStore(DB_LETTERS, true);
            RecordEnumeration rc = rsLetters.enumerateRecords(this, this, true);
            rc.rebuild();
            int id;
            boolean edited = false;
            for (int i = 0; i < rc.numRecords(); i++)
            {
                id = rc.nextRecordId();
                DataInputStream dis = new DataInputStream(new ByteArrayInputStream(rsLetters.getRecord(id)));
                String letter = dis.readUTF();
                if (letter.equals(gui.get_tfLetter().getString()))
                {
                    rsLetters.setRecord(id, bytes, 0, bytes.length);
                    edited = true;
                    break;
                }
            }
            if (!edited)
                rsLetters.addRecord(bytes, 0, bytes.length);
            rsLetters.closeRecordStore();
            viewMultiSolver();
        }
        catch (Exception ex)
        {
            gui.showError("addEditLetter",ex.toString(),"");
        }
    }
    
    /**
     * Prida nebo edituje vzorec
     */
    public void addEditPattern(boolean edit)
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
            RecordStore rsPatterns = RecordStore.openRecordStore(DB_PATTERNS, true);
            if (edit)
            {
                RecordEnumeration rc = rsPatterns.enumerateRecords(this, this, true);
                rc.rebuild();
                int id = 0;
                for (int i = 0; i < gui.get_lstPatterns().getSelectedIndex()+1; i++)
                {
                    id = rc.nextRecordId();
                }
                rsPatterns.setRecord(id, bytes, 0, bytes.length);
            }
            else
            {
                rsPatterns.addRecord(bytes, 0, bytes.length);
            }
            rsPatterns.closeRecordStore();
            viewPatterns();
        }
        catch (Exception ex)
        {
            gui.showError("addEditPattern",ex.toString(),"");
        }
    }
    
    /**
     * Prida vzorce stazene z netu
     */
    public void addPatterns(String[][] data)
    {
        try
        {
            RecordStore rsPatterns = RecordStore.openRecordStore(DB_PATTERNS, true);
            for (int i=0;i<data.length;i++)
            {
                //prevod dat na stream bytu
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(buffer);
                dos.writeUTF(data[i][0]);
                dos.writeUTF(data[i][1]);
                dos.writeUTF(data[i][2]);
                byte[] bytes = buffer.toByteArray();
                rsPatterns.addRecord(bytes, 0, bytes.length);
            }
            rsPatterns.closeRecordStore();
            gui.showAlert("Vzorečky byly úspěšně uloženy do MultiSolveru",AlertType.INFO,gui.get_frmOverview());
        }
        catch (Exception ex)
        {
            gui.showError("addPatterns",ex.toString(),"");
        }
    }
    
    /**
     * Zobrazi vzorec
     */
    public void viewPattern()
    {
        try
        {
            RecordStore rsPatterns = RecordStore.openRecordStore(DB_PATTERNS, true);
            RecordEnumeration rc = rsPatterns.enumerateRecords(this, this, true);
            rc.rebuild();
            int id = 0;
            for (int i = 0; i < gui.get_lstPatterns().getSelectedIndex()+1; i++)
            {
                id = rc.nextRecordId();
            }
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(rsPatterns.getRecord(id)));
            gui.get_tfPatternName().setString(dis.readUTF());
            gui.get_tfEditPatternLattitude().setString(dis.readUTF());
            gui.get_tfEditPatternLongitude().setString(dis.readUTF());
            rsPatterns.closeRecordStore();
        }
        catch (Exception e)
        {
            gui.showError("viewPattern",e.toString(),"");
        }
    }
    
    /**
     * Nastavi vybrany vzorec jako aktivni
     */
    public void setActivePattern()
    {
        try
        {
            RecordStore rsPatterns = RecordStore.openRecordStore(DB_PATTERNS, true);
            RecordEnumeration rc = rsPatterns.enumerateRecords(this, this, true);
            rc.rebuild();
            int id = 0;
            for (int i = 0; i < gui.get_lstPatterns().getSelectedIndex()+1; i++)
            {
                id = rc.nextRecordId();
            }
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(rsPatterns.getRecord(id)));
            gui.get_siLattitudePattern().setLabel(dis.readUTF());
            gui.get_siLattitudePattern().setText(dis.readUTF());
            gui.get_siLongitudePattern().setText(dis.readUTF());
            rsPatterns.closeRecordStore();
        }
        catch (Exception e)
        {
            gui.showError("setActivePattern",e.toString(),"");
        }
    }
    
    /**
     * Nastavi multi solver na defaultni hodnoty a smaze vsechny pismena
     */
    public void deleteMultiSolver()
    {
        try
        {
            gui.get_siLattitudePattern().setLabel("Aktuální vzorec");
            gui.get_siLattitudePattern().setText("Není zvolen aktuální vzorec");
            gui.get_siLongitudePattern().setText("");
            
            RecordStore rsLetters = RecordStore.openRecordStore(DB_LETTERS, true);
            rsLetters.closeRecordStore();
            RecordStore.deleteRecordStore("letters");
            
            RecordStore rsPatterns = RecordStore.openRecordStore(DB_PATTERNS, true);
            rsPatterns.closeRecordStore();
            RecordStore.deleteRecordStore("patterns");
            
            viewMultiSolver();
        }
        catch (Exception e)
        {
            gui.showError("deleteMultiSolver",e.toString(),"");
        }
    }
    
    /**
     * Spocita souradnice podle vzorce a promennych v MultiSolveru
     */
    public void computeCoordinates()
    {
        try
        {
            if (gui.get_siLattitudePattern().getText().equals("Není zvolen aktuální vzorec"))
            {
                gui.showAlert("Musíte nejdřív zvolit jeden vzorec ze vzorečků.",AlertType.WARNING,gui.get_frmMultiSolver());
            }
            else
            {
                //nejdriv nahrada promennych
                char firstChar1 = gui.get_siLattitudePattern().getText().charAt(0);
                char firstChar2 = gui.get_siLongitudePattern().getText().charAt(0);
                String lattitude = gui.get_siLattitudePattern().getText().substring(1);
                String longitude = gui.get_siLongitudePattern().getText().substring(1);
                RecordStore rsLetters = RecordStore.openRecordStore(DB_LETTERS, true);
                RecordEnumeration rc = rsLetters.enumerateRecords(this, this, true);
                rc.rebuild();
                boolean edited = false;
                for (int i = 0; i < rc.numRecords(); i++)
                {
                    DataInputStream dis = new DataInputStream(new ByteArrayInputStream(rsLetters.getRecord(rc.nextRecordId())));
                    String letter = dis.readUTF();
                    int value = dis.readInt();
                    lattitude = Utils.replaceString(lattitude, letter, String.valueOf(value));
                    longitude = Utils.replaceString(longitude, letter, String.valueOf(value));
                }
                rsLetters.closeRecordStore();
                //kontrola, jestli bylo vsechno nahrazeno
                boolean allReplaced = true;
                int i;
                for (i=0;i<(lattitude+longitude).length();i++)
                {
                    if (Utils.isLetter((lattitude+longitude).charAt(i)))
                    {
                        allReplaced = false;
                        break;
                    }
                }
                if (!allReplaced)
                {
                    gui.showAlert("Písmeno '"+(lattitude+longitude).charAt(i)+"' nemá nastavenou hodnotu. Před výpočtem musí mít všechna písmena svojí hodnotu.",AlertType.WARNING,gui.get_frmMultiSolver());
                }
                else
                {
                    //nastaveni nahrazenych souradnic
                    gui.get_siAfterReplacement().setText(lattitude+"\n"+longitude);
                    //vypocet lattitude
                    boolean loadingExpression = false;
                    String expression = "";
                    boolean computingError = false;
                    for (i=0;i<lattitude.length();i++)
                    {
                        if (lattitude.charAt(i)=='[')
                        {
                            loadingExpression = true;
                        }
                        else if (lattitude.charAt(i)==']')
                        {
                            //nalezena expression => vypocet
                            String result = "";
                            try
                            {
                                result = String.valueOf(Expression.evaluate(expression));
                            }
                            catch (Exception e)
                            {
                                gui.showAlert("Není možno vypočítat tento výraz první souřadnice: "+expression+". Zkontrolujte, zda má výraz správný formát.",AlertType.ERROR,gui.get_frmEditPattern());
                                computingError = true;
                                break;
                            }
                            lattitude = Utils.replaceString(lattitude, "["+expression+"]",result);
                            i = 0;
                            loadingExpression = false;
                            expression = "";
                        }
                        else if (loadingExpression)
                        {
                            expression += lattitude.charAt(i);
                        }
                    }
                    //vypocet longitude
                    loadingExpression = false;
                    expression = "";
                    for (i=0;i<longitude.length();i++)
                    {
                        if (longitude.charAt(i)=='[')
                        {
                            loadingExpression = true;
                        }
                        else if (longitude.charAt(i)==']')
                        {
                            //nalezena expression => vypocet
                            String result = "";
                            try
                            {
                                result = String.valueOf(Expression.evaluate(expression));
                            }
                            catch (Exception e)
                            {
                                gui.showAlert("Není možno vypočítat tento výraz druhé souřadnice: "+expression+". Zkontrolujte, zda má výraz správný formát.",AlertType.ERROR,gui.get_frmEditPattern());
                                computingError = true;
                                break;
                            }
                            longitude = Utils.replaceString(longitude, "["+expression+"]",result);
                            i = 0;
                            loadingExpression = false;
                            expression = "";
                        }
                        else if (loadingExpression)
                        {
                            expression += longitude.charAt(i);
                        }
                    }
                    //vypis vysledku
                    if (!computingError)
                    {
                        gui.get_tfResultLattitude().setString(firstChar1+lattitude);
                        gui.get_tfResultLongitude().setString(firstChar2+longitude);
                        gui.getDisplay().setCurrent(gui.get_frmResult());
                    }
                }
            }
        }
        catch (Exception e)
        {
            gui.showError("computeCoordinates",e.toString(),"");
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
                // první jméno je později než druhé
                return RecordComparator.FOLLOWS;
            }
        }
        catch (Exception e)
        {
            gui.showError("compare",e.toString(),"");
            return RecordComparator.EQUIVALENT;
        }
    }
}
