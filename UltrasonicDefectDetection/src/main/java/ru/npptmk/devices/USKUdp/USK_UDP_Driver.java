/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.devices.USKUdp;

/**
 *
 * @author RazumnovAA
 */
public interface USK_UDP_Driver {

    /**
     * Копирует в текущий набор параметры из другого типоразмера.
     *
     * @param prms копируемые параметры.
     */
    public void copyParams(DeviceUSKUdpParams prms);

    /**
     * Отдает набор минимальных значений А скана для отрисовки на панеле
     * калибровки УЗК.
     *
     * @return массив минимальных значений А скан.
     */
    public int[] getAScanMin();

    /**
     * Отдает набор мксимальных значений А скана для отрисовки на панеле
     * калибровки УЗК.
     *
     * @return массив максимальных значений А скан.
     */
    public int[] getAScanMax();

    /**
     * Возвращает номер канала в котором содержится А скан
     *
     * @return номер канала по которому пришел А скан
     */
    public short getA_scan_channel();

    /**
     * Возвращает канал, который настраиваем и по которому пересылается аСкан
     *
     * @return идентификатор активного канала
     */
    public int getActiveChan();

    /**
     * Возвращает параметры активного канала
     *
     * @return парамерты
     */
    public DeviceUSKUdpParam getActiveChanParam();

    /**
     * Возвращает тощину канала толщинометрии в зависимости от режима
     * калибровки. Если калибровка разрешена, то возвращается значение в
     * миллиметрах, иначе в условных единицах времени.
     *
     * @param nc номер канала.
     * @return значение толщины или времени.
     */
    public float getThick(int nc);

    /**
     * Возвращает длину графика для дефектоскопии.
     *
     * @param nc
     * @return
     */
    public int getGraficLength(int nc);
}
