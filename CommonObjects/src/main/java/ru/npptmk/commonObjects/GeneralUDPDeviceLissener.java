/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.commonObjects;

/**
 * Уведомление о завершении обработки данных по очередной точки сканирования.
 * Вызывается в потоке обработки сразу по оканчании обработки.
 * Используется в базовом для работающих по УДП протоколу драйвере {@code GeneralUDPDevice}.
 * @author SmorkalovAV
 */
public interface GeneralUDPDeviceLissener {
    public void onEvent();
}
