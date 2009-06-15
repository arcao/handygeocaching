/*
 * MultiSolver.java
 *
 * Created on 17. øíjen 2007, 10:12
 *
 */

package database;

import gui.Gui;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import javax.microedition.lcdui.AlertType;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import utils.Expression;
import utils.Utils;

/**
 * Tato trida se stara o spravu a ukladani pismen (promennych) do MultiSolveru a take funkce tykajici se MultiSolveru jako celku
 * @author David Vavra
 */
public class MultiSolver extends Database
{
    //reference
    Patterns patterns;
    
    public MultiSolver(Gui ref, Patterns ref2)
    {
        super(ref, "letters");
        patterns = ref2;
    }
    
   /**
     * Prida nebo edituje pismeno multiSolveru
     */
    public void addEdit()
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
            RecordEnumeration rc = recordStore.enumerateRecords(this, this, true);
            rc.rebuild();
            int id;
            boolean edited = false;
            for (int i = 0; i < rc.numRecords(); i++)
            {
                id = rc.nextRecordId();
                DataInputStream dis = new DataInputStream(new ByteArrayInputStream(recordStore.getRecord(id)));
                String letter = dis.readUTF();
                if (letter.equals(gui.get_tfLetter().getString()))
                {
                    recordStore.setRecord(id, bytes, 0, bytes.length);
                    edited = true;
                    break;
                }
            }
            if (!edited)
                recordStore.addRecord(bytes, 0, bytes.length);
            viewAll();
        }
        catch (Exception ex)
        {
            gui.showError("addEditLetter",ex.toString(),"");
        }
    }    
    
   /**
     * Zobrazi informace o multine podle informaci v databazi
     */
    public void viewAll()
    {
        try
        {
            RecordEnumeration rc = recordStore.enumerateRecords(this, this, true);
            rc.rebuild();
            String letters = "";
            for (int i = 0; i < rc.numRecords(); i++)
            {
                DataInputStream dis = new DataInputStream(new ByteArrayInputStream(recordStore.getRecord(rc.nextRecordId())));
                String letter = dis.readUTF();
                int value = dis.readInt();
                letters += letter+"="+value+", ";
            }
            gui.get_siLetters().setText(letters);
        }
        catch (Exception ex)
        {
            gui.showError("viewAll letters",ex.toString(),"");
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
            
            deleteAll();
            patterns.deleteAll();
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
                gui.showAlert("Musíte nejdøív zvolit jeden vzorec ze vzoreèkù.",AlertType.WARNING,gui.get_frmMultiSolver());
            }
            else
            {
                //nejdriv nahrada promennych
                char firstChar1 = gui.get_siLattitudePattern().getText().charAt(0);
                char firstChar2 = gui.get_siLongitudePattern().getText().charAt(0);
                String lattitude = gui.get_siLattitudePattern().getText().substring(1);
                String longitude = gui.get_siLongitudePattern().getText().substring(1);
                RecordEnumeration rc = recordStore.enumerateRecords(this, this, true);
                rc.rebuild();
                boolean edited = false;
                for (int i = 0; i < rc.numRecords(); i++)
                {
                    DataInputStream dis = new DataInputStream(new ByteArrayInputStream(recordStore.getRecord(rc.nextRecordId())));
                    String letter = dis.readUTF();
                    int value = dis.readInt();
                    lattitude = Utils.replaceString(lattitude, letter, String.valueOf(value));
                    longitude = Utils.replaceString(longitude, letter, String.valueOf(value));
                }
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
                    gui.showAlert("Písmeno '"+(lattitude+longitude).charAt(i)+"' nemá nastavenou hodnotu. Pøed výpoètem musí mít všechna písmena svojí hodnotu.",AlertType.WARNING,gui.get_frmMultiSolver());
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
                                gui.showAlert("Není možno vypoèítat tento výraz první souøadnice: "+expression+". Zkontrolujte, zda má výraz správný formát.",AlertType.ERROR,gui.get_frmEditPattern());
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
                                gui.showAlert("Není možno vypoèítat tento výraz druhé souøadnice: "+expression+". Zkontrolujte, zda má výraz správný formát.",AlertType.ERROR,gui.get_frmEditPattern());
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
}
