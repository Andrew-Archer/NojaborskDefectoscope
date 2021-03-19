/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.plcinterfaces.s7_1200;

/**
 * Класс описания тега.<br>
 * Используется для построения таблиц тегов в образе контроллера.
 * @author MalginAS
 */
public class TagDef {
    /**
     * Имя тега.
     */
    public String name;
    /**
     * Адрес тега в пространстве контроллера.
     * <br>Для входов и выходов
     * это номер байта * 10 + номер бита. Например,
     * Адрес входа {@code I6.3} будет имет значение {@code 63}.<br>
     * Для регистров - это смещение байта относи тельно начала блока Holding
     * register.
     */
    public short address;

    public TagDef(String name, int address) {
        this.name = name;
        this.address = (short) address;
    }

    /**
     * Возвращает адрес для протокола Modbus для входов и выходов.
     * @return 
     */
    public short getBitModbusAddr(){
        int bit = address%10;
        int b = address/10;
        return (short) (b*8 + bit);
    }

    /**
     * Возвращает адрес для протокола Modbus для регистров.
     * @return 
     */
    public short getWordModbusAddr(){
        return (short) (address/2);
    }
    
}
