/*
 * LongUTFDataInputStream.java
 * This file is part of HandyGeocaching.
 *
 * HandyGeocaching is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * (read more at: http://www.gnu.org/licenses/gpl.html)
 */

package utils;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UTFDataFormatException;

/**
 * An easy extension of DataInputStream class for supporting readLongUTF method
 * which is using an unsigned 32-bit UTF length instead of unsigned 16-bit length 
 * in original readUTF method.
 * @author msloup
 */
public class LongUTFDataInputStream extends DataInputStream {
    
    public LongUTFDataInputStream(InputStream is) {
        super(is);
    }
    
    /**
     * This code borrows heavily from DataInputStreat.readUTF() source.
     * However, it uses a 32-bit UTF length.
     *
     * @return a Unicode string.
     * @exception EOFException if this input stream reaches the end before
     * reading all the bytes.
     * @exception IOException if an I/O error occurs.
     * @exception UTFDataFormatException if the bytes do not represent a valid
     * modified UTF-8 encoding of a string.
     * @see java.io.DataInputStream#readUTF()
     */
    public String readLongUTF() throws IOException {
        int utflen = readInt();
        int c, char2, char3;
        char[] chararr = new char[utflen];
        byte[] bytearr = new byte[utflen];
        int count = 0;
        int chararr_count = 0;

        readFully(bytearr, 0, utflen);

        while (count < utflen) {
            c = (int)bytearr[count] & 0xff;
            switch (c >> 4) {
                case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7:
                    /* 0xxxxxxx*/
                    count++;
                    chararr[chararr_count++] = (char)c;
                    break;
                case 12: case 13:
                    /* 110x xxxx   10xx xxxx*/
                    count += 2;
                    if (count > utflen)
                        throw new UTFDataFormatException("malformed input: partial character at end");
                    char2 = (int)bytearr[count - 1];
                    if ((char2 & 0xC0) != 0x80)
                        throw new UTFDataFormatException("malformed input around byte " + count);
                    chararr[chararr_count++] = (char)(((c & 0x1F) << 6) |
                                                      (char2 & 0x3F));
                    break;
                case 14:
                    /* 1110 xxxx  10xx xxxx  10xx xxxx */
                    count += 3;
                    if (count > utflen)
                        throw new UTFDataFormatException("malformed input: partial character at end");
                    char2 = (int)bytearr[count - 2];
                    char3 = (int)bytearr[count - 1];
                    if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80))
                        throw new UTFDataFormatException("malformed input around byte " + (count-1));
                    chararr[chararr_count++] = (char)(((c & 0x0F) << 12) |
                                                      ((char2 & 0x3F) << 6) |
                                                      ((char3 & 0x3F) << 0));
                    break;
                default:
                    /* 10xx xxxx,  1111 xxxx */
                    throw new UTFDataFormatException("malformed input around byte " + count);
            }
        }
        // The number of chars produced may be less than utflen
        return new String(chararr, 0, chararr_count);
    }
}
