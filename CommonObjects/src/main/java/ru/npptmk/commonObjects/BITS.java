/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.commonObjects;

/**
 * класс представляет из себя набор чисел, 
 * соответстующим определенным битам
 * @author SmorkalovAV
 */
public class BITS {
    public static final int bit0 = 1;
    public static final int bit1 = 2;
    public static final int bit2 = 4;
    public static final int bit3 = 8;
    public static final int bit4 = 16;
    public static final int bit5 = 32;
    public static final int bit6 = 64;
    public static final int bit7 = 128;
    public static final int bit8 = 256;
    public static final int bit9 = 512;
    public static final int bit10 = 1024;
    public static final int bit11 = 2048;
    public static final int bit12 = 4096;
    public static final int bit13 = 8192;
    public static final int bit14 = 16384;
    public static final int bit15 = 32768;
    public static boolean getBit(short val, int bitNo){
        int m = 1;
        for (int i=0; i<bitNo;i++){
            m= m*2;
        }
        if ((val&m) == 0) {
            return false;
        } else {
            return true;
        }
    }
    public static short setBit(short val, int bitNo, boolean bitVal){
        int m = 1;
        for (int i=0; i<bitNo;i++){
            m= m*2;
        }
        if (bitVal) {
            return (short) (val | m);
        } else {
            return (short) (val & (~m));
        }
    }
    public static byte setBit(byte val, int bitNo, boolean bitVal){
        if (bitVal) {
            return (byte) (val | (1<<bitNo));
        } else {
            short m = (short) (1<<bitNo);
            return (byte) (val & (~m));
        }
    }
}
