/*
 * TextArea.java
 * This file is part of HandyGeocaching.
 *
 * HandyGeocaching is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * (read more at: http://www.gnu.org/licenses/gpl.html)
 */
package gui;

import java.util.Vector;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

/**
 * Slouzi jako nahrada za vykreslovani textu primo do formulare, ktery na nekterych telefonech nefunguje zcela korektne.
 * @author Arcao
 */
public class TextArea extends Canvas implements Runnable {
    private static final int PADDING = 3; // odsazeni 3px;
    private static final int SCROLL_WIDTH = 3;
    private static final int REPEAT_TIME = 20;
    
    private Display display;
    
    private String text;
    private String[] lineBuffer;
    
    private int position;
    private int linesPerScreen;
    
    private int lastWidth;
    private int lastHeight;
    
    private int lineHeight;
    private int lineHeightBold;
    
    private Font font;
    private Font fontBold;
    
    private String leftButtonText;
    private String rightButtonText;
    
    private Displayable leftButtonScreen = null;
    private Displayable rightButtonScreen = null;
    
    private Runnable leftButtonAction = null;
    private Runnable rightButtonAction = null;
    
    private int scroll_height;
    private int scroll_top;
    
    private int keyCode = 0;
    private Thread repeatThread = null;
    
    private Thread rebuildThread = null;
    
    private boolean switchLeftRight = false;
    
    public TextArea(Display display) {
        this(display, false);
    }
    
    /** Creates a new instance of TextArea */
    public TextArea(Display display, boolean switchLeftRight) {
        text = "";
        lineBuffer = new String[0];
        
        this.switchLeftRight = switchLeftRight;
        
        lastWidth = -1;
        lastHeight = -1;
        
        position = 0;
        font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
        fontBold = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
        
        lineHeight = font.getHeight();
        lineHeightBold = fontBold.getHeight();
        
        scroll_height = 0;
        scroll_top = 0;
        
        leftButtonText = "";
        rightButtonText = "";
        
        this.display = display;
    }
    
    public Displayable show() {
        setFullScreenMode(true);
        Displayable previous = display.getCurrent();
        display.setCurrent(this);
        return previous;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text.trim() + "\n ";
        lastWidth = -1;
        lastHeight = -1;
    }

    public Runnable getLeftButtonAction() {
        return (switchLeftRight)? rightButtonAction : leftButtonAction;
    }

    public void setLeftButtonAction(Runnable leftButtonAction) {
        if (switchLeftRight)
            this.rightButtonAction = leftButtonAction;
        else
            this.leftButtonAction = leftButtonAction;
    }

    public Displayable getLeftButtonScreen() {
        return (switchLeftRight)? rightButtonScreen : leftButtonScreen;
    }

    public void setLeftButtonScreen(Displayable leftButtonScreen) {
        if (switchLeftRight)
            this.rightButtonScreen = leftButtonScreen;
        else
            this.leftButtonScreen = leftButtonScreen;
    }

    public String getLeftButtonText() {
        return (switchLeftRight)? rightButtonText : leftButtonText;
    }

    public void setLeftButtonText(String leftButtonText) {
        if (switchLeftRight)
            this.rightButtonText = leftButtonText;
        else
            this.leftButtonText = leftButtonText;
        repaint();
    }

    public Runnable getRightButtonAction() {
        return (switchLeftRight)? leftButtonAction : rightButtonAction;
    }

    public void setRightButtonAction(Runnable rightButtonAction) {
        if (switchLeftRight)
            this.leftButtonAction = rightButtonAction;
        else
            this.rightButtonAction = rightButtonAction;
    }

    public Displayable getRightButtonScreen() {
        return (switchLeftRight)? leftButtonScreen : rightButtonScreen;
    }

    public void setRightButtonScreen(Displayable rightButtonScreen) {
        if (switchLeftRight)
            this.leftButtonScreen = rightButtonScreen;
        else
            this.rightButtonScreen = rightButtonScreen;
    }

    public String getRightButtonText() {
        return (switchLeftRight)? leftButtonText : rightButtonText;
    }

    public void setRightButtonText(String rightButtonText) {
        if (switchLeftRight)
            this.leftButtonText = rightButtonText;
        else
            this.rightButtonText = rightButtonText;
        repaint();
    }
    
    int positiveMin(int a, int b) {
        int ret = Math.min(a, b);
        if (ret < 0) return Math.max(a, b);
        return ret;
    }
    
    private int findWhiteChar(String text, int pos) {
        int ret = text.indexOf(' ', pos);
        ret = positiveMin(ret, text.indexOf('\n', pos));
        ret = positiveMin(ret, text.indexOf('\t', pos));
        return ret;
    }
    
    private void rebuild() {
        int width = getWidth();
        int height = getHeight();
        
        linesPerScreen = (int) Math.floor(height / lineHeight);
        
        Vector v = new Vector();
        int found;
        int maxWidth = width - 2 * PADDING - SCROLL_WIDTH; 
        int left = 0;
        int pos = 0;
        int len = text.length();
        
        //pridani dvou radku na zacatek kvuli symbolu spojeni u Nokii
        v.addElement("");
        v.addElement("");               
       
        StringBuffer sbLine = new StringBuffer();
        
        while (pos < len) {
            while(pos < len && text.charAt(pos) == ' ')
                pos++;
            
            if (pos >= len)
                break;
            
            found = findWhiteChar(text, pos);
            if (found == -1)
                found = len - 2;
            
            //bereme i white char
            found++;
            
            String item = text.substring(pos, found);
            int itemWidth = font.stringWidth(item);
            
            if (left + itemWidth > maxWidth) {
                //slovo se na radek jiz nevejde
                v.addElement(sbLine.toString().trim());
                sbLine.setLength(0);
                left = 0;
                if (itemWidth < maxWidth) {
                    //ale vejde se na dalsi radek
                    sbLine.append(item.trim());
                    sbLine.append(' ');
                    left+= itemWidth;
                    pos = found;
                } else {
                    //nevejde se ani na dalsi radek
                    //rozpulit slovo, protoze se nevejde na radek
                    while (pos < found) {
                        int charWidth = font.charWidth(text.charAt(pos));
                        System.out.println(text.substring(pos, found));
                        while (pos < found && left + charWidth < maxWidth) {
                            sbLine.append(text.charAt(pos));
                            left+= charWidth;
                            pos++;

                            charWidth = font.charWidth(text.charAt(pos));
                        }
                        if (left + charWidth >= maxWidth) {
                            v.addElement(sbLine.toString().trim());
                            sbLine.setLength(0);
                            left = 0;
                        }
                    }
                }
                //kontrola noveho radku
                if (item.endsWith("\n")) {
                    v.addElement(sbLine.toString().trim());
                    sbLine.setLength(0);
                    left = 0;
                }
            } else {
                //na radek se jeste vejde, pridame do sbLine
                sbLine.append(item.trim());
                sbLine.append(' ');
                pos = found;
                left+= itemWidth;
                
                //kontrola noveho radku
                if (item.endsWith("\n")) {
                    v.addElement(sbLine.toString().trim());
                    //v.addElement("");
                    sbLine.setLength(0);
                    left = 0;
                }
            }
        }
        
        if (sbLine.length() > 0)
            v.addElement(sbLine.toString().trim());

        v.addElement("");
        
        //osetrime pozici
        position = (int) (position * ((double) v.size() / (double) lineBuffer.length));
        
        lineBuffer = new String[v.size()];
        v.copyInto(lineBuffer);
        
        if (lineBuffer.length > linesPerScreen - 1) {
            scroll_height = ((height - lineHeightBold) * (linesPerScreen - 1)) / lineBuffer.length;
        } else {
            scroll_height = 0;
        }
                
        lastWidth = width;
        lastHeight = height;
    }

    protected void paint(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        
        g.setFont(font);
                
        //pokud se zmenilo rozliseni prekopeme radky
        if (width != lastWidth || height != lastHeight) {
            lastWidth = width;
            lastHeight = height;
            //rebuild();
            //smazani pozadi
            g.setColor((Gui.getInstance().nightMode) ? 0x0 : 0xffffff); //pozadi
            g.fillRect(0, 0, width, height);
            g.setColor((Gui.getInstance().nightMode) ? 0xffffff : 0x0); //text
            
            g.drawString("Načítám...", width/2, height/2, Graphics.HCENTER | Graphics.BASELINE);
            if (rebuildThread == null) {
                rebuildThread = new Thread() {
                    public void run() {
                        rebuild();
                        repaint();
                        rebuildThread = null;
                    }
                };
                rebuildThread.start();
            }
        } else {
            //smazani pozadi
            g.setColor((Gui.getInstance().nightMode) ? 0x0 : 0xffffff); //pozadi
            g.fillRect(0, 0, width, height);
            g.setColor((Gui.getInstance().nightMode) ? 0xffffff : 0x0); //text

            for(int i = 0; i < linesPerScreen; i++) {
                if (position + i >= lineBuffer.length - 1)
                    break;
                g.drawString(lineBuffer[position + i], PADDING, i * lineHeight, Graphics.TOP | Graphics.LEFT);
            }

            //vykresleni SCROLLBARU
            if (position > 0) {
                scroll_top = ((height - lineHeightBold) * position) / lineBuffer.length;
            } else {
                scroll_top = 0;
            }
            g.fillRect(width - SCROLL_WIDTH, scroll_top, SCROLL_WIDTH, scroll_height);
        }
        //vykresleni spodniho radku
        g.setColor((Gui.getInstance().nightMode) ? 0x0 : 0xffffff); //pozadi
        g.fillRect(0, height - lineHeightBold, width, lineHeightBold);
        
        //vykresleni spodnich tlacitek
        g.setColor((Gui.getInstance().nightMode) ? 0xffffff : 0x0); //text
        g.setFont(fontBold);
        g.drawString(leftButtonText, PADDING, height, Graphics.BOTTOM | Graphics.LEFT);
        g.drawString(rightButtonText, width - PADDING, height, Graphics.BOTTOM | Graphics.RIGHT);
        
        if (hasPointerEvents())
            g.drawString("Noční", width / 2, height, Graphics.BOTTOM|Graphics.HCENTER);
    }

    protected void keyPressed(int keyCode) {
        this.keyCode = keyCode;
        
        //leve tlacitko
        if (keyCode == -6 || keyCode == -21 || keyCode == -20 || keyCode == 105 || keyCode == 21 || keyCode == -202 || keyCode == 113)
        {
            if (leftButtonAction != null)
                leftButtonAction.run();
            if (leftButtonScreen != null)
                display.setCurrent(leftButtonScreen);
        } else if (keyCode == -7 || keyCode == 112 || keyCode == 111) { //prave tlacitko
            if (rightButtonAction != null)
                rightButtonAction.run();
            if (rightButtonScreen != null)
                display.setCurrent(rightButtonScreen);
        } else if (keyCode == KEY_NUM0) {
            Gui.getInstance().nightMode = !Gui.getInstance().nightMode;
            repaint();
        }else {
            keyRepeated(keyCode);
        }
    }
    
    protected void keyRepeated(int keyCode) {
        if (keyCode == Canvas.KEY_NUM4 || keyCode == Canvas.LEFT || keyCode == -3) { //doleva
            position-=(linesPerScreen - 1);
            if (position < 0)
                position = 0;
            repaint();
        } else if (keyCode == Canvas.KEY_NUM6 || keyCode == Canvas.RIGHT || keyCode == -4) { //doprava
            position+=(linesPerScreen - 1);
            if (position > lineBuffer.length - (linesPerScreen - 1))
                position = Math.max(0, lineBuffer.length - (linesPerScreen - 1));
            repaint();
        } else if (keyCode == Canvas.KEY_NUM8 || keyCode == Canvas.DOWN || keyCode == -2) { //dolu
            position++;
            if (position > lineBuffer.length - (linesPerScreen - 1))
                position = Math.max(0, lineBuffer.length - (linesPerScreen - 1));
            repaint();
        } else if (keyCode == Canvas.KEY_NUM2 || keyCode == Canvas.UP || keyCode == -1) { //nahoru
            position--;
            if (position < 0)
                position = 0;
            repaint();
        } else if (keyCode == Canvas.KEY_NUM1 || keyCode == Canvas.KEY_NUM3) { //home
            position = 0;
            repaint();
        } else if (keyCode == Canvas.KEY_NUM7 || keyCode == Canvas.KEY_NUM9) { //end
            position = Math.max(0, lineBuffer.length  - (linesPerScreen - 1));
            repaint();
        }
    }
    
    protected void pointerPressed(int x, int y) {
        int width = getWidth();
        int height = getHeight();
        
        int widthHalf = width / 2;
        int nocniWidthHalf = fontBold.stringWidth("Nocni") / 2;
        int leftButtonWidth = fontBold.stringWidth(leftButtonText);
        int rightButtonWidth = fontBold.stringWidth(rightButtonText);
        
        //nocni rezim
        if (y > height - lineHeightBold) {
            if (x > widthHalf - nocniWidthHalf - PADDING && x < widthHalf + nocniWidthHalf + PADDING) {
                keyPressed(Canvas.KEY_NUM0);
            } else if (x < leftButtonWidth + 2*PADDING) {
                keyPressed(-6); //leve tlacitko
            } else if (x > width - (rightButtonWidth + 2*PADDING)) {
                keyPressed(-7); //prave tlacitko
            }
        } else {
            if (x < 2./3 * width) {
                if (y < (height - lineHeightBold) / 2) {
                    keyPressed(Canvas.UP);
                    repeatThread = new Thread(this);
                    repeatThread.start();
                } else {
                    keyPressed(Canvas.DOWN);
                    repeatThread = new Thread(this);
                    repeatThread.start();
                }
            } else {
                position = computePosition(y);
                repaint();
            }
        }
    }

    protected void pointerDragged(int x, int y) {
        int width = getWidth();
        int height = getHeight();
        
        if (y < height - lineHeightBold && x > 2./3 * width) {
            position = computePosition(y);
            repaint();
        }
    }

    protected void pointerReleased(int i, int i0) {
        repeatThread = null;
    }
       
    private int computePosition(int y) {
        int height = getHeight();
        
        if (y < 10)
            return 0;
        if (y > height - lineHeightBold - 10)
            return Math.max(0, lineBuffer.length  - (linesPerScreen - 1));
        
        return Math.max(0, ((y - 10) * (lineBuffer.length - (linesPerScreen - 1))) / (height - lineHeightBold - 20)); 
    }

    public void run() {
        while (repeatThread != null) {
            try { Thread.sleep(REPEAT_TIME); } catch (Exception e) { break; }
            keyRepeated(keyCode);
        }
    }
}
