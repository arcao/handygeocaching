package utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/***
 * V teto tride jsou uzitecne metody, ktere pouzivaji ostatni tridy
 */
public class Utils
{
    
    final static String[] hex = {
        "%00", "%01", "%02", "%03", "%04", "%05", "%06", "%07",
        "%08", "%09", "%0a", "%0b", "%0c", "%0d", "%0e", "%0f",
        "%10", "%11", "%12", "%13", "%14", "%15", "%16", "%17",
        "%18", "%19", "%1a", "%1b", "%1c", "%1d", "%1e", "%1f",
        "%20", "%21", "%22", "%23", "%24", "%25", "%26", "%27",
        "%28", "%29", "%2a", "%2b", "%2c", "%2d", "%2e", "%2f",
        "%30", "%31", "%32", "%33", "%34", "%35", "%36", "%37",
        "%38", "%39", "%3a", "%3b", "%3c", "%3d", "%3e", "%3f",
        "%40", "%41", "%42", "%43", "%44", "%45", "%46", "%47",
        "%48", "%49", "%4a", "%4b", "%4c", "%4d", "%4e", "%4f",
        "%50", "%51", "%52", "%53", "%54", "%55", "%56", "%57",
        "%58", "%59", "%5a", "%5b", "%5c", "%5d", "%5e", "%5f",
        "%60", "%61", "%62", "%63", "%64", "%65", "%66", "%67",
        "%68", "%69", "%6a", "%6b", "%6c", "%6d", "%6e", "%6f",
        "%70", "%71", "%72", "%73", "%74", "%75", "%76", "%77",
        "%78", "%79", "%7a", "%7b", "%7c", "%7d", "%7e", "%7f",
        "%80", "%81", "%82", "%83", "%84", "%85", "%86", "%87",
        "%88", "%89", "%8a", "%8b", "%8c", "%8d", "%8e", "%8f",
        "%90", "%91", "%92", "%93", "%94", "%95", "%96", "%97",
        "%98", "%99", "%9a", "%9b", "%9c", "%9d", "%9e", "%9f",
        "%a0", "%a1", "%a2", "%a3", "%a4", "%a5", "%a6", "%a7",
        "%a8", "%a9", "%aa", "%ab", "%ac", "%ad", "%ae", "%af",
        "%b0", "%b1", "%b2", "%b3", "%b4", "%b5", "%b6", "%b7",
        "%b8", "%b9", "%ba", "%bb", "%bc", "%bd", "%be", "%bf",
        "%c0", "%c1", "%c2", "%c3", "%c4", "%c5", "%c6", "%c7",
        "%c8", "%c9", "%ca", "%cb", "%cc", "%cd", "%ce", "%cf",
        "%d0", "%d1", "%d2", "%d3", "%d4", "%d5", "%d6", "%d7",
        "%d8", "%d9", "%da", "%db", "%dc", "%dd", "%de", "%df",
        "%e0", "%e1", "%e2", "%e3", "%e4", "%e5", "%e6", "%e7",
        "%e8", "%e9", "%ea", "%eb", "%ec", "%ed", "%ee", "%ef",
        "%f0", "%f1", "%f2", "%f3", "%f4", "%f5", "%f6", "%f7",
        "%f8", "%f9", "%fa", "%fb", "%fc", "%fd", "%fe", "%ff"
    };
    
    /***
     * Encode a string to the "x-www-form-urlencoded" form, enhanced
     * with the UTF-8-in-URL proposal. This is what happens:
     *
     * <ul>
     * <li><p>The ASCII characters 'a' through 'z', 'A' through 'Z',
     *        and '0' through '9' remain the same.
     *
     * <li><p>The unreserved characters - _ . ! ~ * ' ( ) remain the same.
     *
     * <li><p>The space character ' ' is converted into a plus sign '+'.
     *
     * <li><p>All other ASCII characters are converted into the
     *        3-character string "%xy", where xy is
     *        the two-digit hexadecimal representation of the character
     *        code
     *
     * <li><p>All non-ASCII characters are encoded in two steps: first
     *        to a sequence of 2 or 3 bytes, using the UTF-8 algorithm;
     *        secondly each of these bytes is encoded as "%xx".
     * </ul>
     *
     * @param s The string to be encoded
     * @return The encoded string
     */
    public static String urlUTF8Encode(String s)
    {
        StringBuffer sbuf = new StringBuffer();
        int len = s.length();
        for (int i = 0; i < len; i++)
        {
            int ch = s.charAt(i);
            if ('A' <= ch && ch <= 'Z')
            {		// 'A'..'Z'
                sbuf.append((char)ch);
            }
            else if ('a' <= ch && ch <= 'z')
            {	// 'a'..'z'
                sbuf.append((char)ch);
            }
            else if ('0' <= ch && ch <= '9')
            {	// '0'..'9'
                sbuf.append((char)ch);
            }
            else if (ch == ' ')
            {			// space
                sbuf.append('+');
            }
            else if (ch == '-' || ch == '_'		// unreserved
                    || ch == '.' || ch == '!'
                    || ch == '~' || ch == '*'
                    || ch == '\'' || ch == '('
                    || ch == ')')
            {
                sbuf.append((char)ch);
            }
            else if (ch <= 0x007f)
            {		// other ASCII
                sbuf.append(hex[ch]);
            }
            else if (ch <= 0x07FF)
            {		// non-ASCII <= 0x7FF
                sbuf.append(hex[0xc0 | (ch >> 6)]);
                sbuf.append(hex[0x80 | (ch & 0x3F)]);
            }
            else
            {					// 0x7FF < ch <= 0xFFFF
                sbuf.append(hex[0xe0 | (ch >> 12)]);
                sbuf.append(hex[0x80 | ((ch >> 6) & 0x3F)]);
                sbuf.append(hex[0x80 | (ch & 0x3F)]);
            }
        }
        return sbuf.toString();
    }
    
    public static String toUTF8(String input) {
        StringBuffer sbuf = new StringBuffer();
        int len = input.length();
        for (int i = 0; i < len; i++)
        {
            int ch = input.charAt(i);
            if (ch <= 0x007f)
            {		// other ASCII
                sbuf.append((char)ch);
            }
            else if (ch <= 0x07FF)
            {		// non-ASCII <= 0x7FF
                sbuf.append((char)(0xc0 | (ch >> 6)));
                sbuf.append((char)(0x80 | (ch & 0x3F)));
            }
            else
            {					// 0x7FF < ch <= 0xFFFF
                sbuf.append((char)(0xe0 | (ch >> 12)));
                sbuf.append((char)(0x80 | ((ch >> 6) & 0x3F)));
                sbuf.append((char)(0x80 | (ch & 0x3F)));
            }
        }
        return sbuf.toString();
    }
    
    public static String fromUTF8(String input) {
        StringBuffer sbuf = new StringBuffer();
        int len = input.length();
        int i = 0;
        try 
        {
            while(i < len) {
                int ch = input.charAt(i);
                if (ch < 128)
                {   // jeden znak
                    sbuf.append((char)ch);
                }
                else if (ch >= 192 && ch <= 223)
                {   // dva znaky
                    ch = ((ch & 31) << 6) + (input.charAt(i + 1) & 63);
                    i++;
                    sbuf.append((char)ch);

                }
                else if (ch >= 224 && ch <= 239)
                {   // tri znaky
                    ch = ((ch & 15) << 12) + ((input.charAt(i + 1) & 63) << 6) + (input.charAt(i + 2) & 63);
                    i++;
                    i++;
                    sbuf.append((char)ch);
                }
                else
                {   // ctyri znaky
                    ch = ((ch & 7) << 18) + ((input.charAt(i + 1) & 63) << 12) + ((input.charAt(i + 2) & 63) << 6) + (input.charAt(i + 3) & 63);
                    i++;
                    i++;
                    i++;
                    sbuf.append((char)ch);
                }
                i++;
            }
        }
        catch (Exception e) {}
        return sbuf.toString();
    }
    
    public static String repairUTF8(String s) {
        // v urcitych situacich je spatne dekodovano UTF-8, tady se to napravuje
        String t = s;
        t = replaceString(t, "Å¾", "ž");
        t = replaceString(t, "Å¡", "š");
        return t;
    }
    
    public static String sessionId(String name, String password)
    {
        String utf8name = toUTF8(name);
        String utf8password = toUTF8(password);
        
        System.out.println(utf8name);
        System.out.println(utf8password);
        
        String sid = String.valueOf(utf8name.length());
        sid += "-"+String.valueOf(utf8password.length());
        for (int i=0;i<utf8name.length();i++)
        {
            sid += "-"+String.valueOf(utf8name.substring(i,i+1).hashCode());
        }
        for (int i=0;i<utf8password.length();i++)
        {
            sid += "-"+String.valueOf(utf8password.substring(i,i+1).hashCode());
        }
        if (utf8password.length()+utf8name.length() < 20)
        {
            for (int i=0;i<(20-(utf8password.length()+utf8name.length()));i++)
            {
                Random random = new Random();
                sid += "-"+String.valueOf(randomNumber(0,255,random));
            }
        }
        return sid;
    }
    /**
     * Vrati nahodne cislo od from do to
     */
    public static int randomNumber(int from, int to, Random rnd)
    {
        return from+Math.abs(rnd.nextInt() % to);
    }
    
    /**
     * J2ME nema arcus tangens, zde je jeji implementace
     */
    static public double atan(double x)
    {
        final double SQRT3 = 1.732050807568877294;
        boolean signChange=false;
        boolean Invert=false;
        int sp=0;
        double x2, a;
        // check up the sign change
        if(x<0.)
        {
            x=-x;
            signChange=true;
        }
        // check up the invertation
        if(x>1.)
        {
            x=1/x;
            Invert=true;
        }
        // process shrinking the domain until x<PI/12
        while(x>Math.PI/12)
        {
            sp++;
            a=x+SQRT3;
            a=1/a;
            x=x*SQRT3;
            x=x-1;
            x=x*a;
        }
        // calculation core
        x2=x*x;
        a=x2+1.4087812;
        a=0.55913709/a;
        a=a+0.60310579;
        a=a-(x2*0.05160454);
        a=a*x;
        // process until sp=0
        while(sp>0)
        {
            a=a+Math.PI/6;
            sp--;
        }
        // invertation took place
        if(Invert) a=Math.PI/2-a;
        // sign change took place
        if(signChange) a=-a;
        //
        return a;
    }
    
    /**
     * J2ME nema str_replace, zde je jeji implementace
     */
    public static String replaceString(String _text, String _searchStr, String _replacementStr)
    {
        // String buffer to store str
        StringBuffer sb = new StringBuffer();
        
        // Search for search
        int searchStringPos = _text.indexOf(_searchStr);
        int startPos = 0;
        int searchStringLength = _searchStr.length();
        
        // Iterate to add string
        while (searchStringPos != -1)
        {
            sb.append(_text.substring(startPos, searchStringPos)).append(_replacementStr);
            startPos = searchStringPos + searchStringLength;
            searchStringPos = _text.indexOf(_searchStr, startPos);
        }
        
        // Create string
        sb.append(_text.substring(startPos,_text.length()));
        
        return sb.toString();
    }
    
    /**
     * Kontrola, jestli je dany znak pismeno
     */
    public static boolean isLetter(char ch)
    {
        ch = Character.toLowerCase(ch);
        if (ch=='a' || ch=='b' ||ch=='c' ||ch=='d' ||ch=='e' ||ch=='f' ||ch=='g' ||ch=='h' ||ch=='i' ||ch=='j' ||ch=='k' ||ch=='l' ||ch=='m' ||ch=='n' ||ch=='o' ||ch=='p' ||ch=='q' || ch=='r' ||ch=='s' ||ch=='t' ||ch=='u' ||ch=='v' ||ch=='w' ||ch=='x' ||ch=='y' ||ch=='z')
            return true;
        else
            return false;
    }
    
    /**
     * Doplneni cisla nulami na zacatku na danou delku
     */
    public static String addZeros(String s, int length)
    {
        String prefix = "";
        for (int i=0;i<(length-s.length());i++)
        {
           prefix = prefix.concat("0");
        }
        return prefix+s;
    }
    
    /**
     * Doplneni cisla nulami na konec na danou delku
     */
    public static String addZerosAfter(String s, int length)
    {
        String surfix = "";
        for (int i=0;i<(length-s.length());i++)
        {
           surfix = surfix.concat("0");
        }
        return s+surfix;
    }
    
    /**
     * Desifrovani/sifrovani podle sifry ROT13
     */
    public static String decypherText(String s)
    {
        s = s.toLowerCase();
        s = s.replace('a','N');
        s = s.replace('b','O');
        s = s.replace('c','P');
        s = s.replace('d','Q');
        s = s.replace('e','R');
        s = s.replace('f','S');
        s = s.replace('g','T');
        s = s.replace('h','U');
        s = s.replace('i','V');
        s = s.replace('j','W');
        s = s.replace('k','X');
        s = s.replace('l','Y');
        s = s.replace('m','Z');
        s = s.replace('n','A');
        s = s.replace('o','B');
        s = s.replace('p','C');
        s = s.replace('q','D');
        s = s.replace('r','E');
        s = s.replace('s','F');
        s = s.replace('t','G');
        s = s.replace('u','H');
        s = s.replace('v','I');
        s = s.replace('w','J');
        s = s.replace('x','K');
        s = s.replace('y','L');
        s = s.replace('z','M');
        return s;
    }
    
    public static String formatDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        StringBuffer sb = new StringBuffer();
        sb.append(c.get(Calendar.DATE)).append(".").append(c.get(Calendar.MONTH) + 1).append(". ");
        sb.append(c.get(Calendar.HOUR_OF_DAY)).append(":").append(c.get(Calendar.MINUTE));

        return sb.toString();
    }
}
