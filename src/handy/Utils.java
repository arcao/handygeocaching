package handy;

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
    
    static String sessionId(String name, String password)
    {
        name = toUTF8(name);
        password = toUTF8(password);
        
        String sid = String.valueOf(name.length());
        sid += "-"+String.valueOf(password.length());
        for (int i=0;i<name.length();i++)
        {
            sid += "-"+String.valueOf(name.substring(i,i+1).hashCode());
        }
        for (int i=0;i<password.length();i++)
        {
            sid += "-"+String.valueOf(password.substring(i,i+1).hashCode());
        }
        if (password.length()+name.length() < 20)
        {
            
            for (int i=0;i<(20-(password.length()+name.length()));i++)
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
    static int randomNumber(int from, int to, Random rnd)
    {
        return from+Math.abs(rnd.nextInt() % to);
    }
    
    /**
     * Zkonvertuje lattitude z typu N AA° AA.AAA do AA.AAAAA
     */
    public static String convertLattitude(String lat)
    {
        try
        {
            int direction;
            if (lat.substring(0,1).equals("N"))
            {
                direction = 1;
            }
            else if (lat.substring(0,1).equals("S"))
            {
                direction = -1;
            }
            else
            {
                throw new Exception("lattitude neni N nebo S");
            }
            
            String lattitudeDegrees = String.valueOf(direction * (Integer.parseInt(lat.substring(2,4))))+"."+divide(lat.substring(6,12), 60, 6);
            
            return lattitudeDegrees;
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
            return "0";
        }
    }
    
    /**
     * Zkonvertuje longitude z typu N AAA° AA.AAA do AAA.AAAAA
     */
    public static String convertLongitude(String lon)
    {
        try
        {
            int direction;
            if (lon.substring(0,1).equals("E"))
            {
                direction = 1;
            }
            else if (lon.substring(0,1).equals("W"))
            {
                direction = -1;
            }
            else
            {
                throw new Exception("longitude neni W nebo E");
            }
            String longitudeDegrees = String.valueOf(direction * (Integer.parseInt(lon.substring(2,5))))+"."+divide(lon.substring(7,13), 60, 6);
            
            return longitudeDegrees;
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
            return "0";
        }
    }
    
    /**
     * V CLDC 1.0 neumime pracovat s plovouci desetinnou carkou - zde je reseni
     */
    public static String divide(String number, int divideBy, int accuracy)
    {
        boolean carkaNalezena = false;
        String predCarkou = "";
        String zaCarkou = "";
        for (int i=0;i<number.length();i++)
        {
            if (number.substring(i,i+1).equals("."))
            {
                carkaNalezena = true;
            }
            else if (carkaNalezena)
            {
                zaCarkou += number.substring(i,i+1);
            }
            else
            {
                predCarkou += number.substring(i,i+1);
            }
        }
        int realAccuracy = accuracy-zaCarkou.length();
        int cimNasobit = 1;
        for (int j=0;j<realAccuracy;j++)
        {
            cimNasobit *= 10;
        }
        String vysledek = String.valueOf((Integer.parseInt(predCarkou+zaCarkou))*(cimNasobit)/divideBy);
        while (vysledek.length()!=accuracy)
        {
            vysledek = "0"+vysledek;
        }
        return vysledek;
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
    
    
}
