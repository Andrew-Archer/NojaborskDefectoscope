/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.commonObjects;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * создает поток в который есть возможность писать типы short, int, double
 * для программ типа С++
 * @author SmorkalovAV
 */
public class RevDataOutputStream extends OutputStream {
    byte []buff;
    int pos = 0;

    /**
     * Создаем с массивом 1024 элемента
     */
    public RevDataOutputStream() {
        this(1024);
    }
    /**
     * Создаем со значением размера создаваемого массива
     * @param siz размен массива
     */
    public RevDataOutputStream(int siz) {
        buff = new byte[siz];
    }
    /**
     * На базе созданного массива
     * @param buf созданный массив
     */
    public RevDataOutputStream(byte [] buf) {
        buff = buf;
    }
    /**
     * Получает буфер полностью, вместе с незанятой частью
     * @return буфер
     */
    public byte[] getBuff() {
        return buff;
    }
    
    public void writeRevShort(int sn) throws IOException{
        if(buff == null){
            throw new IOException("Массив не инициализирован");
        }
        if(pos+1>=buff.length){
            throw new IOException("Вышли за пределы массива " + (pos + 1));
        }
        buff[pos+1] = (byte) ((sn >> 8)&0xff);
        buff[pos] = (byte) (sn & 0xff);
        pos += 2;
    }

    public void writeRevInt(long sn) throws IOException {
        if(buff == null){
            throw new IOException("Массив не инициализирован");
        }
        if(pos+3>=buff.length){
            throw new IOException("Вышли за пределы массива " + (pos + 1));
        }
        buff[pos+3] = (byte) (sn >>> 24);
        buff[pos+2] = (byte) ((sn >>> 16)&0xff);
        buff[pos+1] = (byte) ((sn >>> 8)&0xff);
        buff[pos] = (byte) (sn & 0xff);
        pos += 4;
    }

    public void writeRevDouble(double s) throws IOException {
        if(buff == null){
            throw new IOException("Массив не инициализирован");
        }
        if(pos+7>=buff.length){
            throw new IOException("Вышли за пределы массива " + (pos + 1));
        }
        long sn = (long) Double.doubleToLongBits(s);
        buff[pos+7] = (byte) (sn >>> 56);
        buff[pos+6] = (byte) (sn >>> 48);
        buff[pos+5] = (byte) (sn >>> 40);
        buff[pos+4] = (byte) (sn >>> 32);
        buff[pos+3] = (byte) (sn >>> 24);
        buff[pos+2] = (byte) (sn >>> 16);
        buff[pos+1] = (byte) (sn >>> 8);
        buff[pos] = (byte) (sn & 0xff);
        pos += 8;
    }
    /**
     * Возвращает тосльк занятую данными часть буфера
     * @return массив данных
     */
    public byte[] toByteArray() {
        return Arrays.copyOf(buff, pos);
    }

    @Override
    public void write(int b) throws IOException {
        if(buff == null){
            throw new IOException("Массив не инициализирован");
        }
        if(pos+7>=buff.length){
            throw new IOException("Вышли за пределы массива " + (pos + 1));
        }
        buff[pos++] = (byte) (b&0xff);
    }

    public void writeInt(int sn) throws IOException {
        if(buff == null){
            throw new IOException("Массив не инициализирован");
        }
        if(pos+3>=buff.length){
            throw new IOException("Вышли за пределы массива " + (pos + 1));
        }
        buff[pos] = (byte) (sn >>> 24);
        buff[pos+1] = (byte) ((sn >>> 16)&0xff);
        buff[pos+2] = (byte) ((sn >>> 8)&0xff);
        buff[pos+3] = (byte) (sn & 0xff);
        pos += 4;
    }

    public void writeShort(short sn) throws IOException {
        if(buff == null){
            throw new IOException("Массив не инициализирован");
        }
        if(pos+1>=buff.length){
            throw new IOException("Вышли за пределы массива " + (pos + 1));
        }
        buff[pos] = (byte) ((sn >>> 8)&0xff);
        buff[pos+1] = (byte) (sn & 0xff);
        pos += 2;
    }
}
