package de.bht.jvr.qtm;

/**
 * This file is part of jVR.
 *
 * Copyright 2013 Marc Roßbach
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class QTMUtility {
    /*-----------------------Big-Endian----------------------------*/

    /**
     * Big endian byte array to double.
     * 
     * @param b
     *            the b
     * @return the double
     */
    public static double BigEndianByteArrayToDouble(byte[] b) {
        return BigEndianByteArrayToDouble(b, 0);
    }

    /**
     * Big endian byte array to double.
     * 
     * @param b
     *            the b
     * @param offset
     *            the offset
     * @return the double
     */
    public static double BigEndianByteArrayToDouble(byte[] b, int offset) {
        return Double.longBitsToDouble(BigEndianByteArrayToLong(b, offset)); // TODO
    }

    /**
     * Big endian byte array to int.
     * 
     * @param b
     *            the b
     * @return the int
     */
    public static int BigEndianByteArrayToInt(byte[] b) {
        return BigEndianByteArrayToInt(b, 0);
    }

    /**
     * Big endian byte array to int.
     * 
     * @param b
     *            the b
     * @param offset
     *            the offset
     * @return the int
     */
    public static int BigEndianByteArrayToInt(byte[] b, int offset) {
        if (b.length - offset < 4)
            return 0;

        int x = 0;
        for (int i = 0; i < 4; i++) {
            x <<= 8;
            x ^= b[i + offset] & 0xFF;
        }

        return x;
    }

    /**
     * Big endian byte array to long.
     * 
     * @param b
     *            the b
     * @return the long
     */
    public static long BigEndianByteArrayToLong(byte[] b) {
        return BigEndianByteArrayToLong(b, 0);
    }

    /**
     * Big endian byte array to long.
     * 
     * @param b
     *            the b
     * @param offset
     *            the offset
     * @return the long
     */
    public static long BigEndianByteArrayToLong(byte[] b, int offset) {
        if (b.length - offset < 8)
            return 0;

        long x = 0;
        for (int i = 0; i < 8; i++) {
            x <<= 8;
            x ^= (long) b[i + offset] & 0xFF;
        }

        return x;
    }

    /**
     * Copy bytes.
     * 
     * @param source
     *            the source
     * @param sourceOffset
     *            the source offset
     * @param target
     *            the target
     * @param targetOffset
     *            the target offset
     */
    public static void copyBytes(byte[] source, int sourceOffset, byte[] target, int targetOffset) {
        System.arraycopy(source, sourceOffset, target, targetOffset, Math.min(target.length - targetOffset, source.length - sourceOffset));
    }

    /**
     * Double to big endian byte array.
     * 
     * @param x
     *            the x
     * @return the byte[]
     */
    public static byte[] doubleToBigEndianByteArray(double x) {
        long l = Double.doubleToLongBits(x);
        return longToBigEndianByteArray(l);
    }

    /**
     * Double to little endian byte array.
     * 
     * @param x
     *            the x
     * @return the byte[]
     */
    public static byte[] doubleToLittleEndianByteArray(double x) {
        long l = Double.doubleToLongBits(x);
        return longToLittleEndianByteArray(l);
    }

    /*-----------------------Little-Endian----------------------------*/

    /**
     * Int to big endian byte array.
     * 
     * @param x
     *            the x
     * @return the byte[]
     */
    public static byte[] intToBigEndianByteArray(int x) {
        byte[] b = new byte[] { 0, 0, 0, 0 };
        for (int i = 3; i >= 0; i--) {
            b[i] = (byte) (x & 0xFF);
            x >>= 8;
        }

        return b;
    }

    /**
     * Int to little endian byte array.
     * 
     * @param x
     *            the x
     * @return the byte[]
     */
    public static byte[] intToLittleEndianByteArray(int x) {
        byte[] b = new byte[] { 0, 0, 0, 0 };
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (x & 0xFF);
            x >>= 8;
        }

        return b;
    }

    /**
     * Little endian byte array to double.
     * 
     * @param b
     *            the b
     * @return the double
     */
    public static double LittleEndianByteArrayToDouble(byte[] b) {
        return LittleEndianByteArrayToDouble(b, 0);
    }

    /**
     * Little endian byte array to double.
     * 
     * @param b
     *            the b
     * @param offset
     *            the offset
     * @return the double
     */
    public static double LittleEndianByteArrayToDouble(byte[] b, int offset) {
        return Double.longBitsToDouble(LittleEndianByteArrayToLong(b, offset));
    }

    /**
     * Little endian byte array to int.
     * 
     * @param b
     *            the b
     * @return the int
     */
    public static int LittleEndianByteArrayToInt(byte[] b) {
        return LittleEndianByteArrayToInt(b, 0);
    }

    /**
     * Little endian byte array to int.
     * 
     * @param b
     *            the b
     * @param offset
     *            the offset
     * @return the int
     */
    public static int LittleEndianByteArrayToInt(byte[] b, int offset) {
        if (b.length - offset < 4)
            return 0;

        int x = 0;
        for (int i = 3; i >= 0; i--) {
            x <<= 8;
            x ^= b[i + offset] & 0xFF;
        }

        return x;
    }

    /**
     * Little endian byte array to long.
     * 
     * @param b
     *            the b
     * @return the long
     */
    public static long LittleEndianByteArrayToLong(byte[] b) {
        return LittleEndianByteArrayToLong(b, 0);
    }

    /**
     * Little endian byte array to long.
     * 
     * @param b
     *            the b
     * @param offset
     *            the offset
     * @return the long
     */
    public static long LittleEndianByteArrayToLong(byte[] b, int offset) {
        if (b.length - offset < 8)
            return 0;

        long x = 0;
        for (int i = 7; i >= 0; i--) {
            x <<= 8;
            x ^= (long) b[i + offset] & 0xFF;
        }

        return x;
    }

    /**
     * Long to big endian byte array.
     * 
     * @param x
     *            the x
     * @return the byte[]
     */
    public static byte[] longToBigEndianByteArray(long x) {
        byte[] b = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 };
        for (int i = 7; i >= 0; i--) {
            b[i] = (byte) (x & 0xFF);
            x >>= 8;
        }

        return b;
    }

    /*----------------------------------------------------------------*/

    /**
     * Long to little endian byte array.
     * 
     * @param x
     *            the x
     * @return the byte[]
     */
    public static byte[] longToLittleEndianByteArray(long x) {
        byte[] b = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 };
        for (int i = 0; i < 8; i++) {
            b[i] = (byte) (x & 0xFF);
            x >>= 8;
        }

        return b;
    }
}
