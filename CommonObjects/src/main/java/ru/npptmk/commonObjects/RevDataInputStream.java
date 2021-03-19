/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.commonObjects;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.Arrays;

/**
 * создает поток из которого есть возможность читать типы short, int, double
 * для программ типа С++
 * Создается базе массива или потока, и инициирует внутренний массив
 * при обращении возвращает из массива два, четыре или восемь байт, собранных в
 * воответственном типе запросе short, int и double.
 * @author SmorkalovAV
 */
public class RevDataInputStream extends InputStream {

    private byte [] buff = new byte[50];
    private int pos = 0;
    private int size;
    /**
     * На базе созданоого буфера
     * @param buf буфер
     */
    public RevDataInputStream(byte[] buf) {
        buff = buf;
        size = buf.length;
    }
    public int getPos(){
        return pos;
    }
    /**
     * Возврщает созданный объект DataInputStream для 
     * рабты с другой версией этого объекта.
     * @return 
     */
    public DataInputStream getDataInputStream(){
        return new DataInputStream(this);
    }
    /**
     * На базе входного потока
     * @param in входной поток
     */
    public RevDataInputStream(InputStream in) {
        try {
            size = in.read(buff);
            int readBlock = buff.length;
            while(size == buff.length){
                buff = Arrays.copyOf(buff, buff.length + readBlock);
                size += in.read(buff, size, readBlock);
            }
        } catch (IOException ex) {
        }
    }
    /**
     * Считывает из буфера 2 байта в Си-шном формате представления целого.
     * @return 2 байта пробразованы в int чтобы сохранить знак числа.
     * @throws IOException 
     */
    public int readRevShort() throws IOException {
        if(buff == null){
            throw new IOException("Массив не инициализирован");
        }
        if(pos+1>=size){
            throw new IOException("Вышли за пределы массива " + (pos + 1));
        }
        int tm = ((buff[pos+1]&0xff)<<8)+(buff[pos]&0xff);
        pos += 2;
        return tm;
    }
    /**
     * Считыает и буфера 4 байта в Си-шном формате представления целого.
     * @return 4 байта преобразованы в long.
     * @throws IOException 
     */
    public long readRevInt() throws IOException {
        if(buff == null){
            throw new IOException("Массив не инициализирован");
        }
        if(pos+3>=size){
            throw new IOException("Вышли за пределы массива " + (pos + 1));
        }
        long tm = buff[pos+3]&0xff;
        tm <<= 24;
        long tm1 = buff[pos+2]&0xff;
        tm1 <<= 16;
        tm += tm1;
        tm1 = buff[pos+1]&0xff;
        tm1 <<= 8;
        tm += tm1;
        tm += buff[pos]&0xff;
        pos += 4;
        return tm;
    }
    /**
     * 
     * @return
     * @throws IOException
     * @throws SocketTimeoutException 
     */
    public double readRevDouble() throws IOException, SocketTimeoutException {
        if(buff == null){
            throw new IOException("Массив не инициализирован");
        }
        if(pos+3>=size){
            throw new IOException("Вышли за пределы массива " + (pos + 1));
        }
        long tm = buff[pos+7]&0xff;
        tm <<= 56;
        long tm1 = buff[pos+6]&0xff;
        tm1 <<= 48;
        tm += tm1;
        tm1 = buff[pos+5]&0xff;
        tm1 <<= 40;
        tm += tm1;
        tm1 = buff[pos+4]&0xff;
        tm1 <<= 32;
        tm += tm1;
        tm1 = buff[pos+3]&0xff;
        tm1 <<= 24;
        tm += tm1;
        tm1 = buff[pos+2]&0xff;
        tm1 <<= 16;
        tm += tm1;
        tm1 = buff[pos+1]&0xff;
        tm1 <<= 8;
        tm += tm1;
        tm += buff[pos]&0xff;
        pos += 8;
        return Double.longBitsToDouble(tm);
    }
    /**
     * Преобразует в массив байтов
     * @return массив байтов
     * @throws IOException 
     */
    public byte[] toBytesArray(){
        return buff;
    }

    @Override
    public int read() throws IOException {
        if(pos +1 >= size){
            throw new IOException("Вышли за пределы массива " + (pos + 1));
        }
        return buff[pos++];
    }

    @Override
    public int read(byte[] b) throws IOException {
        if (pos + b.length >= size){
            throw new IOException("Вышли за пределы массива " + (pos + b.length));
        }
        System.arraycopy(buff, pos, b, 0, b.length);
        pos += b.length;
        return b.length;
    }


    
    public int readInt()  throws IOException {
        if(buff == null){
            throw new IOException("Массив не инициализирован");
        }
        if(pos+3>=size){
            throw new IOException("Вышли за пределы массива " + (pos + 1));
        }
        long tm = buff[pos]&0xff;
        tm <<= 24;
        long tm1 = buff[pos+1]&0xff;
        tm1 <<= 16;
        tm += tm1;
        tm1 = buff[pos+2]&0xff;
        tm1 <<= 8;
        tm += tm1;
        tm += buff[pos+3]&0xff;
        pos += 4;
        return (int) tm;
    }

}
